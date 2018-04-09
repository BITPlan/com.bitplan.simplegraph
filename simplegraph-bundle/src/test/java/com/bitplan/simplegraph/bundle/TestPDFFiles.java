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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.gremlin.RegexPredicate;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.filesystem.FileNode;
import com.bitplan.simplegraph.filesystem.FileSystem;
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

  @Test
  public void testPDFFiles() throws Exception {
    FileSystem fs = new FileSystem();
    fs.connect();
    FileNode fileRoot = fs.moveTo("../simplegraph-pdf/src/test/data/rfcs");
    fileRoot.recursiveOut("files", Integer.MAX_VALUE);
    assertEquals(69, fs.g().V().count().next().longValue());
    PdfSystem ps = new PdfSystem();
    ps.connect();
    // potentially limit the number of files to be analyzed
    int limit = 42;
    fs.g().V().hasLabel("file").has("ext", "pdf").range(0, limit)
        .forEachRemaining(file -> {
          File pdfFile = new File(file.property("path").value().toString());
          SimpleNode pdfNode = ps.moveTo(pdfFile);
          pdfNode.out("pages");
          pdfNode.property("name", pdfFile.getName());
        });
    // debug = true;
    if (debug)
      ps.forAll(SimpleNode.printDebug);
    long pageCount = ps.g().V().hasLabel("page").count().next().longValue();
    // there should be at least 71 pages (for some reasons 77 might also show up ... might be page length A4/US letter dependent)
    assertTrue(pageCount>=71);
    // there should be 2 pages referencing George Gregg
    assertEquals(2,
        ps.g().V().hasLabel("page")
            .has("text", RegexPredicate.regex(".*George Gregg.*")).count()
            .next().longValue());

    // there should be one RFC mentioning George Gregg
    if (debug)
      ps.g().V().hasLabel("page")
          .has("text", RegexPredicate.regex(".*George Gregg.*")).in("pages")
          .dedup().forEachRemaining(
              pdf -> System.out.println(pdf.property("name").value()));
    List<Object> rfcs = ps.g().V().hasLabel("page")
        .has("text", RegexPredicate.regex(".*George Gregg.*")).in("pages")
        .dedup().values("name").toList();
    assertEquals(1, rfcs.size());
    assertEquals("rfc21.pdf", rfcs.get(0));
  }

}
