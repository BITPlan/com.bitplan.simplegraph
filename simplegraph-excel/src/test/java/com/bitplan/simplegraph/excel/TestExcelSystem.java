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

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.junit.Test;

import com.bitplan.simplegraph.core.TestTinkerPop3;

/**
 * test the excel system
 * @author wf
 *
 */
public class TestExcelSystem {

  @Test
  public void testCreateExcel() throws Exception {
    ExcelSystem es = new ExcelSystem();
    Graph graph = TestTinkerPop3.getAirRoutes();
    GraphTraversalSource g = graph.traversal();
    // es.setDebug(true);
    Workbook wb = es.createWorkBook(g);
    assertEquals(6,wb.getNumberOfSheets());
    es.save(wb, "air-routes.xlsx");
  }

}
