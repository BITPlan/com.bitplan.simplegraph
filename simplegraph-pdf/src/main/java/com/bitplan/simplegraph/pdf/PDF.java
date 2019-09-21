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
package com.bitplan.simplegraph.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Portable Document File extractor help
 */
public class PDF {
  public Throwable error;
  PDDocument doc;
  PDFTextStripper pdfStripper;
  int pages;

  /**
   * construct this PDF from the given url
   * 
   * @throws IOException
   * @throws MalformedURLException
   */
  public PDF(String url) {
    try {
      init(new URL(url).openStream());
    } catch (Throwable th) {
      error = th;
    }
  }

  /**
   * construct this PDF from the given File
   * 
   * @param file
   */
  public PDF(File file) {
    try {
      init(new FileInputStream(file));
    } catch (Throwable th) {
      error = th;
    }
  }

  /**
   * construct me from the given InputStream
   * 
   * @param is
   */
  public PDF(InputStream is) {
    init(is);
  }

  /**
   * initialize me for the given input stream
   */
  public void init(InputStream is) {
    try {
      // might want to switch off logging here to improve performance
      String[] loggers = { "org.apache.pdfbox.util.PDFStreamEngine",
          "org.apache.pdfbox.util", "org.apache.pdfbox.util.PDFStreamEngine",
          "org.apache.pdfbox.pdfparser.PDFObjectStreamParser",
          "org.apache.pdfbox.cos.COSDocument",
          "org.apache.pdfbox.pdmodel.font.PDSimpleFont",
          "org.apache.pdfbox.pdmodel.font.PDType1Font",
          "org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap",
          "org.apache.pdfbox.pdmodel.graphics.color.PDSeparation",
          "org.apache.pdfbox.pdmodel.graphics.color.PDColorState",
          "org.apache.pdfbox.pdmodel.graphics.color.PDICCBased",
          "org.apache.pdfbox.pdfparser.PDFObjectStreamParser" };
      for (String logger : loggers) {
        org.apache.log4j.Logger logpdfengine = org.apache.log4j.Logger
            .getLogger(logger);
        logpdfengine.setLevel(org.apache.log4j.Level.OFF);
      }

      doc = PDDocument.load(is);
      pages = doc.getNumberOfPages();
      is.close();
      pdfStripper = new PDFTextStripper();
    } catch (Throwable th) {
      error = th;
    }
  }

  /**
   * get the text for this PDF
   * @return the text
   */
  public String getText() {
    String result = "?";
    try {
      result = pdfStripper.getText(doc);
    } catch (Throwable th) {
      error = th;
      result = "Error: " + th.getMessage();
    }
    return result;
  }

  /**
   * get the page text for the given page
   * 
   * @param page
   * @return the page text
   */
  public String getPageText(int page) {
    String result = "?";
    try {
      pdfStripper.setStartPage(page);
      pdfStripper.setEndPage(page);
      result = pdfStripper.getText(doc);
    } catch (Throwable th) {
      error = th;
      result = "Error: " + th.getMessage();
    }
    return result;
  }

  /**
   * allows to modify the tolerance factors
   * 
   * @param sTolFactor
   * @param aTolFactor
   */
  public void setTolerance(float sTolFactor, float aTolFactor) {
    float stol = pdfStripper.getSpacingTolerance();
    float atol = pdfStripper.getAverageCharTolerance();
    stol = stol * sTolFactor;
    atol = atol * aTolFactor;
    pdfStripper.setSpacingTolerance(stol);
    pdfStripper.setAverageCharTolerance(atol);
  }
  
  /**
   * close me by closing my document
   * @throws IOException
   */
  public void close() throws IOException {
    doc.close();
  }

}
