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
package com.bitplan.simplegraph.xml;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;

/**
 * test the XML System
 * 
 * @author wf
 *
 */
public class TestXmlSystem {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.simplegraph.xml");

  @Test
  public void testXml() throws Exception {
    File pomFile = new File("../simplegraph-xml/pom.xml");
    assertTrue(pomFile.exists());
    XmlSystem xs = new XmlSystem();
    xs.connect();
    xs.moveTo(pomFile.toURI().toString());
    // debug = true;
    if (debug)
      xs.forAll(SimpleNode.printDebug);
    xs.getStartNode().g().V().hasLabel("artifactId")
        .forEachRemaining(xmlnode -> {
          assertTrue(xmlnode.property("text").value().toString()
              .startsWith("com.bitplan.simplegraph"));
        });
  }

}
