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

import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.Holder;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

/**
 * allows Access to Microsoft Excel Tables
 * 
 * @author wf
 *
 */
public class ExcelSystem extends SimpleSystemImpl {
  /**
   * initialize me
   */
  public ExcelSystem() {
    super.setName("ExcelSystem");
    super.setVersion("0.0.1");
  }

  @Override
  public SimpleSystem connect(String... connectionParams) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Class<? extends SimpleNode> getNodeClass() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * set the cellComment for the given cell to the given text see
   * https://stackoverflow.com/q/16099912/1497139
   * 
   * @param cell
   * @param text
   */
  @SuppressWarnings("rawtypes")
  public void setComment(Cell cell, String text) {
    Drawing drawing = cell.getSheet().createDrawingPatriarch();
    CreationHelper factory = cell.getSheet().getWorkbook().getCreationHelper();
    ClientAnchor anchor = factory.createClientAnchor();
    anchor.setCol1(cell.getColumnIndex());
    anchor.setCol2(cell.getColumnIndex() + 1);
    anchor.setRow1(cell.getRowIndex());
    anchor.setRow2(cell.getRowIndex() + 3);

    Comment comment = drawing.createCellComment(anchor);
    RichTextString str = factory.createRichTextString(text);
    comment.setVisible(Boolean.TRUE);
    comment.setString(str);

    cell.setCellComment(comment);
  }

  /**
   * create a Workbook for the given graph
   * 
   * @param g
   * @return
   */
  public Workbook createWorkBook(GraphTraversalSource g) {
    // https://poi.apache.org/spreadsheet/quick-guide.html#NewSheet
    Workbook wb = new XSSFWorkbook();
    // add a sheet of vertices per vertex Label
    g.V().label().dedup().forEachRemaining(vertexLabel->addSheet(wb,vertexLabel, g.V()));
    // add a sheet of edges per edge Label
    g.E().label().dedup().forEachRemaining(edgeLabel->addSheet(wb, edgeLabel, g.E()));
    return wb;
  }

  /**
   * add a sheet for the given label and GraphTraversal (Vertex/Edge)
   * 
   * @param wb
   * @param itemLabel
   * @param items
   */
  private <T> void addSheet(Workbook wb, String itemLabel,
      GraphTraversal<T, T> items) {
    CellStyle boldStyle = wb.createCellStyle();
    Font font = wb.createFont();
    font.setBold(true);
    boldStyle.setFont(font);

    // create one sheet per Label
    Sheet sheet = wb.createSheet(itemLabel);
    // rowIndex to be used in Lambda starting from 0
    Holder<Integer> rowIndex = new Holder<Integer>(0);
    // look for nodes/vertices with the given vertexLabel
    items.hasLabel(itemLabel).forEachRemaining(item -> {
      // get the current rowNumber
      Integer rowNumber = rowIndex.getFirstValue();
      // create a new row for this rowNumber
      Holder<Row> rowHolder = new Holder<Row>(sheet.createRow(rowNumber));
      // first row is header line
      if (rowNumber == 0) {
        // define style
        Row headerRow = rowHolder.getFirstValue();
        // add cells for the given row
        Holder<Integer> colIndex = new Holder<Integer>(0);
        if (item instanceof Vertex) {
          addCell(headerRow,"id",colIndex.getFirstValue(),boldStyle);
          colIndex.setValue(colIndex.getFirstValue() + 1);
          Vertex vertex = (Vertex) item;
          vertex.properties().forEachRemaining(prop -> {
            addCell(headerRow, prop.label(), colIndex.getFirstValue(),
                boldStyle);
            colIndex.setValue(colIndex.getFirstValue() + 1);
          });
        } else if (item instanceof Edge) {
          Edge edge = (Edge) item;
          edge.properties().forEachRemaining(prop -> {
            addCell(headerRow, edge.label(), colIndex.getFirstValue(),
                boldStyle);
            colIndex.setValue(colIndex.getFirstValue() + 1);
          });
          int col=colIndex.getFirstValue();
          addCell(headerRow,"in",col,boldStyle);
          addCell(headerRow,"out",col+1,boldStyle);
        }
        // create a new Row
        rowHolder.setValue(sheet.createRow(++rowNumber));
        rowIndex.setValue(rowNumber);
      }
      Row row = rowHolder.getFirstValue();
      Holder<Integer> colIndex = new Holder<Integer>(0);
      if (item instanceof Vertex) {
        Vertex vertex = (Vertex) item;
        addCell(row,vertex.id(),colIndex.getFirstValue(),null);
        colIndex.setValue(colIndex.getFirstValue() + 1);
        vertex.properties().forEachRemaining(prop -> {
          addCell(row, prop.value(), colIndex.getFirstValue(), null);
          colIndex.setValue(colIndex.getFirstValue() + 1);
        });
      } else if (item instanceof Edge) {
        Edge edge = (Edge) item;
        edge.properties().forEachRemaining(prop -> {
          addCell(row, prop.value(), colIndex.getFirstValue(), null);
          colIndex.setValue(colIndex.getFirstValue() + 1);
        });
        int col=colIndex.getFirstValue();
        addCell(row,edge.inVertex().id(),col,null);
        addCell(row,edge.outVertex().id(),col+1,null);    
      }
      rowIndex.setValue(rowIndex.getFirstValue() + 1);
    });
  }

  /**
   * add a Cell with the given value at the given column Index to the given row
   * if a cellStyle is given the the cell's style to it
   * 
   * @param row
   * @param value
   * @param colIndex
   * @param cellStyle
   */
  private void addCell(Row row, Object value, int colIndex,
      CellStyle cellStyle) {
    Cell cell = row.createCell(colIndex);
    if (value instanceof Integer) {
      Integer intValue = (Integer) value;
      cell.setCellValue(intValue.doubleValue());
    } else if (value instanceof Double) {
      cell.setCellValue((Double) value);
    } else {
      cell.setCellValue(value.toString()); // type handling!
    }
    if (cellStyle != null)
      cell.setCellStyle(cellStyle);
    if (debug) {
      // add comments with type information
      setComment(cell, value.getClass().getName());
    }
  }

  /**
   * save the given workbook to the given path
   * 
   * @param wb
   * @param path
   * @throws Exception
   */
  public void save(Workbook wb, String path) throws Exception {
    FileOutputStream fileOut = new FileOutputStream(path);
    wb.write(fileOut);
    wb.close();
    fileOut.close();
  }

}
