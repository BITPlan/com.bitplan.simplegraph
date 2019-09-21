/**
 * Copyright (c) 2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.simplegraph
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.simplegraph.bundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.gremlin.RegexPredicate;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.filesystem.FileNode;
import com.bitplan.simplegraph.filesystem.FileSystem;
import com.bitplan.simplegraph.pdf.PdfNode;
import com.bitplan.simplegraph.pdf.PdfSystem;

/**
 * test PDF Files
 * 
 * @author wf
 *
 */
public class TestPDFFiles {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.simplegraph.bundle");

  public static final String RFC_DIRECTORY = "../simplegraph-pdf/src/test/data/rfcs";

  /**
   * get the file system for the given path
   * 
   * @param -
   *          the path
   * @return - the graph for the path containing all files and subdirectories
   */
  public FileSystem getFileSystem(String path) {
    FileSystem fs = new FileSystem();
    fs.connect();
    FileNode fileRoot = fs.moveTo(path);
    fileRoot.recursiveOut("files", Integer.MAX_VALUE);
    return fs;
  }

  /**
   * get the PDF files from a FileSystem and retrieve pages with text
   * 
   * @param fs
   *          - the file system in which to look for the PDF files
   * @param limit
   *          - limit the result to the number if files given
   * @return the PDF System
   * @throws Exception
   */
  public PdfSystem getPdfSystemForFileSystem(FileSystem fs, int limit)
      throws Exception {
    PdfSystem ps = new PdfSystem();
    ps.connect();
    fs.g().V().hasLabel("file").has("ext", "pdf").range(0, limit)
        .forEachRemaining(file -> {
          File pdfFile = new File(file.property("path").value().toString());
          PdfNode pdfNode = (PdfNode) ps.moveTo(pdfFile);
          pdfNode.out("pages");
          pdfNode.property("name", pdfFile.getName());
          try {
            pdfNode.getPdf().close();
          } catch (IOException e) {
            pdfNode.getPdf().error=e;
          }
        });
    return ps;
  }

  /**
   * get Index for the given keyWords
   * 
   * @param pdfSystem
   *          - the pdfSystem to search
   * @param keyWords
   * @return - the map of filenames
   */
  public Map<String, List<String>> getIndex(PdfSystem pdfSystem,
      String... keyWords) {
    // create a sorted map of results
    Map<String, List<String>> index = new TreeMap<String, List<String>>();
    for (String keyWord : keyWords) {
      List<Object> founds = pdfSystem.g().V().hasLabel("page")
          .has("text", RegexPredicate.regex(".*" + keyWord + ".*")).in("pages")
          .dedup().values("name").toList();
      // create a list of file names for the keywords found (basically this only
      // convert List of object to list of string
      List<String> foundList = new ArrayList<String>();
      for (Object found : founds) {
        foundList.add((String) found);
      }
      // put the result into the hash map using keyword as the index
      index.put(keyWord, foundList);
    }
    return index;
  }

  @Test
  public void testPDFFiles() throws Exception {
    FileSystem fs = getFileSystem(RFC_DIRECTORY);
    assertEquals(69, fs.g().V().count().next().longValue());

    // potentially limit the number of files to be analyzed
    int limit = 69;
    PdfSystem pdfSystem = getPdfSystemForFileSystem(fs, limit);
    //debug = true;
    if (debug)
      pdfSystem.forAll(SimpleNode.printDebug);
    long pageCount = pdfSystem.g().V().hasLabel("page").count().next()
        .longValue();
    // there should be at least 71 pages (for some reasons 77 might also show up
    // ... might be page length A4/US letter dependent)
    assertTrue(pageCount >= 71);
    // there should be 2 pages referencing George Gregg
    assertEquals(2,
        pdfSystem.g().V().hasLabel("page")
            .has("text", RegexPredicate.regex(".*George Gregg.*")).count()
            .next().longValue());

    // there should be one RFC mentioning George Gregg
    if (debug)
      pdfSystem.g().V().hasLabel("page")
          .has("text", RegexPredicate.regex(".*George Gregg.*")).in("pages")
          .dedup().forEachRemaining(
              pdf -> System.out.println(pdf.property("name").value()));
    List<Object> rfcs = pdfSystem.g().V().hasLabel("page")
        .has("text", RegexPredicate.regex(".*George Gregg.*")).in("pages")
        .dedup().values("name").toList();
    assertEquals(1, rfcs.size());
    assertEquals("rfc21.pdf", rfcs.get(0));
    Map<String, List<String>> index = this.getIndex(pdfSystem, "George Gregg");
    assertNotNull(index);
    assertEquals(index.size(), 1);
    List<String> fileNameList = index.get("George Gregg");
    assertEquals(1, fileNameList.size());
  }

  @Test
  /**
   * test for https://github.com/BITPlan/com.bitplan.simplegraph/issues/12
   */
  public void testPDFIndexing() throws Exception {
    FileSystem fs = getFileSystem(RFC_DIRECTORY);
    int limit = Integer.MAX_VALUE;
    PdfSystem pdfSystem = getPdfSystemForFileSystem(fs, limit);
    Map<String, List<String>> index = this.getIndex(pdfSystem, "ARPA",
        "proposal", "plan");
    // debug=true;
    showIndex(index,debug);
    assertEquals(14,index.get("ARPA").size());
    assertEquals(9,index.get("plan").size());
    assertEquals(8,index.get("proposal").size());
  }

  static void showIndex(Map<String, List<String>> index, boolean debug) {
    if (debug) {
      for (Entry<String, List<String>> indexEntry : index.entrySet()) {
        List<String> fileNameList = indexEntry.getValue();
        System.out.println(String.format("%15s=%3d %s", indexEntry.getKey(),
            fileNameList.size(), fileNameList));
      }
    }
    
  }

}
