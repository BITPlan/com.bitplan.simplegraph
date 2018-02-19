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

import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class Word {
  public XWPFDocument doc;
  public Throwable error;
  
  /**
   * construct a new Word document from a file
   * @param file
   */
  public Word(File file) {
    try {
      init(new FileInputStream(file));
    } catch (Throwable th) {
      error = th;
    }
  }

  /**
   * construct a new Word document from an URL
   * @param url
   */
  public Word(String url) {
    try {
      InputStream is = new URL(url).openStream();
      init(is);
    } catch (Throwable th) {
      error = th;
    }
  }
  
  /**
   * construct a new Worddocument from an inputStream
   * @param is
   */
  public Word(InputStream is) {
    init(is);
  }

  /**
   * initialize the word document from an input stream
   * @param is
   */
  public void init(InputStream is) {
    try {
      doc = new XWPFDocument(is);
    } catch (Throwable th) {
      error = th;
    }
  }
}
