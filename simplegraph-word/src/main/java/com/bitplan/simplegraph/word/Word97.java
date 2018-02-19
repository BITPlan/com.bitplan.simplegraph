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
package com.bitplan.simplegraph.word;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class Word97 {
  public HWPFDocument doc;
  public WordExtractor we;
  public Range range;
  public Throwable error;

  /**
   * construct a new Word document from a file
   * 
   * @param file
   */
  public Word97(File file) {
    try {
      init(new FileInputStream(file));
    } catch (Throwable th) {
      error = th;
    }
  }

  /**
   * construct a new Word document from an URL
   * 
   * @param url
   */
  public Word97(String url) {
    try {
      InputStream is = new URL(url).openStream();
      init(is);
    } catch (Throwable th) {
      error = th;
    }
  }

  /**
   * construct a new Worddocument from an inputStream
   * 
   * @param is
   */
  public Word97(InputStream is) {
    init(is);
  }

  /**
   * initialize the word document from an input stream
   * 
   * @param is
   */
  public void init(InputStream is) {
    try {
      POIFSFileSystem fs = new POIFSFileSystem(is);
      doc = new HWPFDocument(fs);
      we = new WordExtractor(doc);
      range = doc.getRange();
    } catch (Throwable th) {
      error = th;
    }
  }
}
