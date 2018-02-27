package com.bitplan.simplegraph.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

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
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.bitplan.simplegraph.impl.Holder;

public class Excel {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.simplegraph.excel");
  
  public XSSFWorkbook workbook = null;
  public Throwable error;

  /**
   * get the contents of a sheet
   * @param sheet
   * @return
   */
  public List<List<String>> getSheetContent(XSSFSheet sheet) {
    List<List<String>> result = new ArrayList<List<String>>();
    Iterator<Row> rows = sheet.rowIterator();
    while (rows.hasNext()) {
      XSSFRow row = (XSSFRow) rows.next();
      Iterator<Cell> cells = row.cellIterator();
      List<String> rowList = new ArrayList<String>();
      while (cells.hasNext()) {
        XSSFCell cell = (XSSFCell) cells.next();
        String cellValue = cell.toString();
        if (!"".equals(cellValue))
          rowList.add(cellValue);
      }
      if (rowList.size() > 0)
        result.add(rowList);
    }
    return result;
  }

  public Excel(String url) {
    // http://stackoverflow.com/questions/5836965/how-to-open-xlsx-files-with-poi-ss
    try {
      InputStream is = new URL(url).openStream();
      workbook = new XSSFWorkbook(is);
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
  private static <T> void addSheet(Workbook wb, String itemLabel,
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
   * get sheets
   * @return
   */
  public List<XSSFSheet> getSheets() {
    List<XSSFSheet> sheets=new ArrayList<XSSFSheet>();
    for (int index=0;index<workbook.getNumberOfSheets();index++) {
     sheets.add(workbook.getSheetAt(index));
    }
    return sheets;
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
  private static void addCell(Row row, Object value, int colIndex,
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
  public void save(String path) throws Exception {
    save(workbook,path);
  }

  public static void save(Workbook wb, String path) throws Exception {
    FileOutputStream fileOut = new FileOutputStream(path);
    wb.write(fileOut);
    wb.close();
    fileOut.close();
  }
} // Excel
