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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.TestTinkerPop3;

/**
 * test the excel system
 * 
 * @author wf
 *
 */
public class TestExcelSystem {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.simplegraph.excel");

  String testAirRouteFileName = "air-routes.xlsx";
  String testModernFileName="modern.xlsx";

  @Test
  public void testCreateExcelAirRoutes() throws Exception {
    ExcelSystem es = new ExcelSystem();
    Graph graph = TestTinkerPop3.getAirRoutes();
    GraphTraversalSource g = graph.traversal();
    // es.setDebug(true);
    Workbook wb = es.createWorkBook(g);
    assertEquals(6, wb.getNumberOfSheets());
    es.save(wb, testAirRouteFileName);
  }
  
  @Test
  public void testCreateExcelModern() throws Exception {
    ExcelSystem es = new ExcelSystem();
    Graph graph = TinkerFactory.createModern();
    GraphTraversalSource g = graph.traversal();
    // es.setDebug(true);
    Workbook wb = es.createWorkBook(g);
    assertEquals(4, wb.getNumberOfSheets());
    es.save(wb, testModernFileName);
  }

  @Test
  public void testReadExcel() throws Exception {
    ExcelSystem es = new ExcelSystem();
    es.connect();
    File testFile = new File(testModernFileName);
    es.moveTo(testFile.toURI().toString());
    // debug=true;
    if (debug)
      es.forAll(SimpleNode.printDebug);
    long nodeCount = es.g().V().count().next().longValue();
    assertEquals(17,nodeCount);
  }

}
