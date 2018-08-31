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

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.bitplan.simplegraph.core.Keys;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.Holder;

/**
 * Microsoft Excel helper utilities
 * 
 * @author wf
 *
 */
public class Excel {
  public static String DATE_FORMAT = "yyyy-mm-dd hh:mm";
  public static boolean debug = false;
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.simplegraph.excel");
  private static CellStyle boldStyle;

  public XSSFWorkbook workbook = null;
  public Throwable error;
  // private FormulaEvaluator evaluator;

  /**
   * get the contents of a sheet
   * 
   * @param sheet
   * @return
   */
  public List<List<Object>> getSheetContent(XSSFSheet sheet) {
    List<List<Object>> result = new ArrayList<List<Object>>();
    Iterator<Row> rows = sheet.rowIterator();
    while (rows.hasNext()) {
      XSSFRow row = (XSSFRow) rows.next();
      Iterator<Cell> cells = row.cellIterator();
      List<Object> rowList = new ArrayList<Object>();
      while (cells.hasNext()) {
        XSSFCell cell = (XSSFCell) cells.next();
        Object cellValue = getCellValue(cell);
        rowList.add(cellValue);
      }
      if (rowList.size() > 0)
        result.add(rowList);
    }
    return result;
  }

  private Object getCellValue(XSSFCell cell) {
    Object cellValue = null;
    CellType cellType = cell.getCellTypeEnum();
    if (CellType.FORMULA == cellType)
      cellType = cell.getCachedFormulaResultTypeEnum();
    switch (cellType) {
    case BOOLEAN:
      cellValue = cell.getBooleanCellValue();
      break;
    case NUMERIC:
      cellValue = cell.getNumericCellValue();
      break;
    case STRING:
      cellValue = cell.getStringCellValue();
      break;
    case BLANK:
      break;
    case ERROR:
      cellValue = cell.getErrorCellValue();
      break;

    // CELL_TYPE_FORMULA will never occur
    case FORMULA:
      break;
    case _NONE:
      cellValue = cell.toString();
      break;
    default:
      break;
    }
    return cellValue;
  }

  /**
   * create an Excel sheet from the given url
   * 
   * @param url
   */
  public Excel(String url) {
    // http://stackoverflow.com/questions/5836965/how-to-open-xlsx-files-with-poi-ss
    try {
      InputStream is = new URL(url).openStream();
      workbook = new XSSFWorkbook(is);
      // evaluator = workbook.getCreationHelper().createFormulaEvaluator();
    } catch (Throwable th) {
      error = th;
    }
  }

  /**
   * set the cellComment for the given cell to the given text see
   * https://stackoverflow.com/q/16099912/1497139
   * 
   * @param cell
   * @param text
   */
  @SuppressWarnings("rawtypes")
  public static void setComment(Cell cell, String text) {
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
  public static Workbook createWorkBook(GraphTraversalSource g) {
    // from POI 4.0.0 on IndexedColorMap colorMap = new DefaultIndexedColorMap();
    // https://poi.apache.org/spreadsheet/quick-guide.html#NewSheet
    Workbook wb = new XSSFWorkbook();
    // add a sheet of vertices per vertex Label
    g.V().label().dedup()
        .forEachRemaining(vertexLabel -> addSheet(wb, vertexLabel, g.V(),new XSSFColor(Color.BLUE)));
    // add a sheet of edges per edge Label
    g.E().label().dedup()
        .forEachRemaining(edgeLabel -> addSheet(wb, edgeLabel, g.E(),new XSSFColor(Color.GREEN)));
    return wb;
  }

  /**
   * add a sheet for the given label and GraphTraversal (Vertex/Edge)
   * 
   * @param wb
   * @param itemLabel
   * @param items
   * @param color - the color to use
   */
  private static <T> void addSheet(Workbook wb, String itemLabel,
      GraphTraversal<T, T> items, XSSFColor color) {
    boldStyle = wb.createCellStyle();
    Font font = wb.createFont();
    font.setBold(true);
    boldStyle.setFont(font);

    // create one sheet per Label
    Sheet sheet = wb.createSheet(fixSheetName(itemLabel));
    // set the color
    if (sheet instanceof XSSFSheet) {
     XSSFSheet xsheet = (XSSFSheet)sheet;
     xsheet.setTabColor(color);
    }
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
          addCell(wb, headerRow, "id", colIndex, boldStyle);
          Vertex vertex = (Vertex) item;
          addCells(wb, headerRow, vertex, colIndex, true);
        } else if (item instanceof Edge) {
          Edge edge = (Edge) item;
          edge.properties().forEachRemaining(prop -> {
            addCell(wb, headerRow, prop.key(), colIndex, boldStyle);
          });
          addCell(wb, headerRow, "in", colIndex, boldStyle);
          addCell(wb, headerRow, "out", colIndex, boldStyle);
        }
        // create a new Row
        rowHolder.setValue(sheet.createRow(++rowNumber));
        rowIndex.setValue(rowNumber);
      }
      // normal row
      Row row = rowHolder.getFirstValue();
      Holder<Integer> colIndex = new Holder<Integer>(0);
      if (item instanceof Vertex) {
        Vertex vertex = (Vertex) item;
        addCell(wb, row, vertex.id(), colIndex, null);
        addCells(wb, row, vertex, colIndex, false);
      } else if (item instanceof Edge) {
        Edge edge = (Edge) item;
        edge.properties().forEachRemaining(prop -> {
          addCell(wb, row, prop.value(), colIndex, null);
        });
        addCell(wb, row, edge.inVertex().id(), colIndex, null);
        addCell(wb, row, edge.outVertex().id(), colIndex, null);
      }
      rowIndex.setValue(rowIndex.getFirstValue() + 1);
    });
  }

  /**
   * add the cells for the given vertex to the given row
   * 
   * @param wb
   * 
   * @param row
   * @param vertex
   * @param colIndex
   * @param header
   */
  private static void addCells(Workbook wb, Row row, Vertex vertex,
      Holder<Integer> colIndex, boolean header) {
    SimpleNode simpleNode = SimpleNode.of(vertex);
    boolean done = false;
    // do we have SimpleNode with a predefined order of keys?
    if (simpleNode != null) {
      Keys keys = simpleNode.getKeys();
      if (!keys.isEmpty()) {
        for (String key : keys.getKeysList().get()) {
          Object value = null;
          if (vertex.property(key).isPresent())
            value = vertex.property(key).value();
          addCell(wb, row, header ? key : value, colIndex,
              header ? boldStyle : null);
        }
        // ordered mode done
        done = true;
      }
    }
    // there is no order - try our luck with the set of properties
    if (!done) {
      vertex.properties().forEachRemaining(prop -> {
        if (prop.label() != SimpleNode.SELF_LABEL)
          addCell(wb, row, header ? prop.label() : prop.value(), colIndex,
              header ? boldStyle : null);
      });
    }
  }

  /**
   * https://stackoverflow.com/questions/451452/valid-characters-for-excel-sheet-names
   * 
   * @param sheetName
   * @return a valid sheetName
   */
  private static String fixSheetName(String sheetName) {
    String invalid = "[]*/\\?";
    for (int i = 0; i < invalid.length(); i++) {
      char invalidChar = invalid.charAt(i);
      sheetName = sheetName.replace(invalidChar, '_');
    }
    return sheetName;
  }

  /**
   * get sheets
   * 
   * @return
   */
  public List<XSSFSheet> getSheets() {
    List<XSSFSheet> sheets = new ArrayList<XSSFSheet>();
    for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
      sheets.add(workbook.getSheetAt(index));
    }
    return sheets;
  }

  /**
   * add a Cell with the given value at the given column Index to the given row
   * if a cellStyle is given the the cell's style to it
   * 
   * @param wb
   * 
   * @param row
   * @param value
   * @param colIndex
   * @param cellStyle
   */
  private static void addCell(Workbook wb, Row row, Object value,
      Holder<Integer> colIndex, CellStyle cellStyle) {
    Cell cell = row.createCell(colIndex.getFirstValue());
    CreationHelper createHelper = wb.getCreationHelper();
    ;
    colIndex.setValue(colIndex.getFirstValue() + 1);
    if (value instanceof Integer) {
      Integer intValue = (Integer) value;
      cell.setCellValue(intValue.doubleValue());
    } else if (value instanceof Long) {
      Long longValue = (Long) value;
      cell.setCellValue(longValue.doubleValue());
    } else if (value instanceof Double) {
      cell.setCellValue((Double) value);
    } else if (value instanceof Date) {
      if (cellStyle == null)
        cellStyle = wb.createCellStyle();
      cellStyle.setDataFormat(
          createHelper.createDataFormat().getFormat(DATE_FORMAT));
      Date date = (Date) value;
      cell.setCellValue(date);
    } else if (value instanceof Boolean) {
      Boolean bool = (Boolean) value;
      cell.setCellValue(bool);
    } else {
      if (value != null)
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
  public void save(String path) throws Exception {
    save(workbook, path);
  }

  /**
   * save the given work book to the given path
   * 
   * @param wb
   * @param path
   * @throws Exception
   */
  public static void save(Workbook wb, String path) throws Exception {
    FileOutputStream fileOut = new FileOutputStream(path);
    wb.write(fileOut);
    wb.close();
    fileOut.close();
  }
} // Excel
