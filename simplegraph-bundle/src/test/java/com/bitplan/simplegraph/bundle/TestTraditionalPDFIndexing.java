package com.bitplan.simplegraph.bundle;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.junit.Test;

import com.bitplan.simplegraph.pdf.PDF;

public class TestTraditionalPDFIndexing {

  /**
   *  helper class to extend Structure - this is what
   *  the issue is - here a new structural element is added statically with all the follow up issues
   *  now the structural information that we have PDFFile instead of PDF and that there is a File file
   *  needs to be know to all parts of the navigation part of the algorithm. In fact this information is only
   *  needed locally when processing a single PDF (as a node in the graph)
   */
  public class PDFFile extends PDF {
    File file;
    
    /**
     * construct me for the given File
     * @param pFile
     */
    public PDFFile(File pFile) {
      super(pFile);
      this.file=pFile;
    }
  }
  
  /**
   * https://stackoverflow.com/a/14051951/1497139
   * 
   * @param dirPath
   * @param extension - list of extensions to look for
   * @return the files
   */
  public List<File> explorePath(String dirPath, String... extensions) {

    File topDir = new File(dirPath);

    List<File> directories = new ArrayList<>();
    directories.add(topDir);

    List<File> textFiles = new ArrayList<>();

    List<String> filterWildcards = new ArrayList<>();
    for (String extension : extensions) {
      filterWildcards.add("*." + extension);
    }

    FileFilter typeFilter = new WildcardFileFilter(filterWildcards);

    while (directories.isEmpty() == false) {
      List<File> subDirectories = new ArrayList<File>();

      for (File f : directories) {
        subDirectories.addAll(Arrays
            .asList(f.listFiles((FileFilter) DirectoryFileFilter.INSTANCE)));
        textFiles.addAll(Arrays.asList(f.listFiles(typeFilter)));
      }

      directories.clear();
      directories.addAll(subDirectories);
    }

    return textFiles;
  }
  
  /**
   * get the PDF files for the given list of files
   * @param pdfFiles
   * @return the list of PDFs
   */
  public List<PDFFile> getPdfsFromFileList(List<File> pdfFiles) {
    List<PDFFile> pdfs=new ArrayList<PDFFile>();
    for (File pdfFile:pdfFiles) {
      pdfs.add(new PDFFile(pdfFile));
    }
    return pdfs;
  }
  
  /**
   * get Index for the given keyWords
   * 
   * @param pdfSystem
   *          - the pdfSystem to search
   * @param keyWords
   * @return - the map of filenames
   */
  public Map<String, List<String>> getIndex(List<PDFFile> pdfs,
      String... keyWords) {
    // create a sorted map of results
    Map<String, List<String>> index = new TreeMap<String, List<String>>();
    for (String keyWord : keyWords) {
      // create a list of file names for the keywords found 
      List<String> foundList = new ArrayList<String>();
      for (PDFFile pdf:pdfs) {
        // https://stackoverflow.com/a/9560307/1497139
        if (org.apache.commons.lang3.StringUtils.containsIgnoreCase(pdf.getText(), keyWord)) {
          foundList.add(pdf.file.getName()); // here we access by structure (early binding)
          // - in the graph solution by name (late binding)
        }
      }
      // put the result into the hash map using keyword as the index
      index.put(keyWord, foundList);
    }
    return index;
  }

  @Test
  public void testPdfIndexing() {
    List<File> pdfFiles = this.explorePath(TestPDFFiles.RFC_DIRECTORY, "pdf");
    assertEquals(67,pdfFiles.size());
    List<PDFFile> pdfs = this.getPdfsFromFileList(pdfFiles);
    assertEquals(67,pdfs.size());
    Map<String, List<String>> index = this.getIndex(pdfs, "ARPA",
        "proposal", "plan");
    boolean debug=true;
    TestPDFFiles.showIndex(index,debug);
    assertEquals(14,index.get("ARPA").size());
    assertEquals(9,index.get("plan").size());
    assertEquals(8,index.get("proposal").size());
  }

}
