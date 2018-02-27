package com.bitplan.simplegraph.excel;

import java.util.Map;
import java.util.stream.Stream;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.bitplan.simplegraph.core.Keys;
import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

public class SheetNode extends SimpleNodeImpl {

  private XSSFSheet sheet;

  public SheetNode(SimpleGraph simpleGraph, String kind, String[] keys) {
    super(simpleGraph, kind, keys);
  }

  public SheetNode(SimpleGraph simpleGraph, XSSFSheet sheet) {
    this(simpleGraph,"sheet",Keys.EMPTY_KEYS);
    this.sheet=sheet;
    super.setVertexFromMap();
  }

  @Override
  public Map<String, Object> initMap() {
    map.put("sheetname", sheet.getSheetName());
    return map;
  }

  @Override
  public Stream<SimpleNode> out(String edgeName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Stream<SimpleNode> in(String edgeName) {
    // TODO Auto-generated method stub
    return null;
  }

}
