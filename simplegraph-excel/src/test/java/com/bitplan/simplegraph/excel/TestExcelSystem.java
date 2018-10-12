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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.TestTinkerPop3;

/**
 * test the excel system
 * 
 * @author wf
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestExcelSystem {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.simplegraph.excel");

  String testAirRouteFileName = "../simplegraph-excel/air-routes.xlsx";
  String testModernFileName = "../simplegraph-excel/modern.xlsx";

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
    Sheet knowsSheet = wb.getSheet("knows");
    // check 
    // https://github.com/BITPlan/com.bitplan.simplegraph/issues/23
    // is fixed
    assertEquals("weight",knowsSheet.getRow(0).getCell(0).getStringCellValue());
    assertEquals("in (person)",knowsSheet.getRow(0).getCell(1).getStringCellValue());
    es.save(wb, testModernFileName);
  }

  @Test
  public void testReadExcel() throws Exception {
    ExcelSystem es = new ExcelSystem();
    es.connect();
    File testFile = new File(testModernFileName);
    es.moveTo(testFile.toURI().toString());
    debug = true;
    if (debug)
      es.forAll(SimpleNode.printDebug);
    long nodeCount = es.g().V().count().next().longValue();
    assertEquals(17, nodeCount);
    List<Map<String, Object>> sheetMapList = es.g().V().has("sheetname")
        .valueMap("sheetname").toList();
    assertEquals(4, sheetMapList.size());
    assertEquals(12, es.g().V().has("row").count().next().longValue());
    assertEquals(4,
        es.g().V().has("row").out("sheet").dedup().count().next().longValue());
    Graph gorg=TinkerFactory.createModern();
    Graph gnow=es.asGraph();
    // make sure that the graphs have the same number of vertices
    assertEquals(gorg.traversal().V().count().next().longValue(),gnow.traversal().V().count().next().longValue());
    // check the properties
    TestTinkerPop3.debug=true;
    TestTinkerPop3.dumpGraph(gorg);
    TestTinkerPop3.dumpGraph(gnow);
  }

}
