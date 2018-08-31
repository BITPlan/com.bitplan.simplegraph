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

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bitplan.simplegraph.core.Keys;
import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * 
 * @author wf
 *
 */
public class WorkBookNode extends SimpleNodeImpl {
  XSSFWorkbook workbook =null;
  private Excel excel;
  private String query;
  
  /**
   * create a Work Book Node
   * @param simpleGraph
   * @param kind
   * @param keys
   */
  public WorkBookNode(SimpleGraph simpleGraph, String kind, String[] keys) {
    super(simpleGraph, kind, keys);
  }

  /**
   * create a workbook
   * @param excelSystem
   * @param nodeQuery
   */
  public WorkBookNode(ExcelSystem excelSystem, String nodeQuery) {
    this(excelSystem,"workbook",Keys.EMPTY_KEYS);
    this.query=nodeQuery;
    excel=new Excel(nodeQuery);
    super.setVertexFromMap();
    // get the sheets as a tree
    init();
  }

  /**
   * initialize my nodes
   */
  private void init() {
    List<XSSFSheet> sheets = excel.getSheets();
    for (XSSFSheet sheet:sheets) {
      SimpleNode sheetNode=new SheetNode(this.getSimpleGraph(),sheet);
      this.getVertex().addEdge(sheetNode.property("sheetname").toString(), sheetNode.getVertex());
      List<List<Object>> sheetContent = excel.getSheetContent(sheet);
      if (sheetContent.size()>0) {
        List<Object> titleRow = sheetContent.get(0);
        if (sheetContent.size()>1) {
          for (int rowIndex=1;rowIndex<sheetContent.size();rowIndex++) {
            List<Object> row = sheetContent.get(rowIndex);
            // create a node for the row
            SimpleNode rowNode=new RowNode(this,titleRow,row,rowIndex);
            sheetNode.getVertex().addEdge("rows", rowNode.getVertex());
            rowNode.getVertex().addEdge("sheet",sheetNode.getVertex());
          }
        }
      }
    }
  }

  @Override
  public Map<String, Object> initMap() {
    map.put("query", query);
    return map;
  }

}
