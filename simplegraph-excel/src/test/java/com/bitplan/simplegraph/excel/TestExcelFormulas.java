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
package com.bitplan.simplegraph.excel;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;

/**
 * test for implementing
 * https://github.com/BITPlan/com.bitplan.simplegraph/issues/29
 * 
 * @author wf
 *
 */
public class TestExcelFormulas {

  String excelFileName = "src/test/data/modernwithformulas.xlsx";
  public static boolean debug = false;

  @Test
  public void testReadFormula() throws Exception {
    File excelFile = new File(excelFileName);
    assertTrue(excelFileName + " does not exist", excelFile.exists());
    ExcelSystem es = new ExcelSystem();
    es.connect();

    es.moveTo(excelFile.toURI().toString());
    // debug = true;
    if (debug)
      es.forAll(SimpleNode.printDebug);
    GraphTraversalSource g = es.g();
    long nodeCount = g.V().count().next().longValue();
    assertEquals(17, nodeCount);
    long formulaCount = g.V().has("sum.formula").count().next().longValue();
    assertEquals(4,formulaCount);
  }
  
}
