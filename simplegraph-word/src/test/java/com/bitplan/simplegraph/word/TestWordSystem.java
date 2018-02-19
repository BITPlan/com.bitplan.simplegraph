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

import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;

/**
 * test the Word System
 * @author wf
 *
 */
public class TestWordSystem {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.simplegraph.word");

  @Test
  public void testWordSystem() throws Exception {
    WordSystem ws=new WordSystem();
    ws.connect();
    ws.moveTo("https://www.benefits.va.gov/compensation/docs/shiplist.docx");
    if (debug)
      ws.forAll(SimpleNode.printDebug);
    String text=ws.g().V().has("text").next().property("text").value().toString();
    assertTrue(text.contains("USS Dewey (DLG-14)"));
  }

}
