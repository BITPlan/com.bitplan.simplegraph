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
