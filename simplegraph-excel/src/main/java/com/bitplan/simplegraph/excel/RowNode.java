package com.bitplan.simplegraph.excel;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.bitplan.simplegraph.core.Keys;
import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

public class RowNode extends SimpleNodeImpl {

  private Object titleRaw;
  private int rowIndex;
  private List<String> row;
  private List<String> titleRow;

  public RowNode(SimpleGraph simpleGraph, String kind, String[] keys) {
    super(simpleGraph, kind, keys);
  }

  public RowNode(WorkBookNode workBookNode, List<String> titleRow,
      List<String> row, int rowIndex) {
    this(workBookNode.getSimpleGraph(),"row",Keys.EMPTY_KEYS);
    this.titleRow=titleRow;
    this.rowIndex=rowIndex;
    this.row=row;
    super.setVertexFromMap();
  }

  @Override
  public Map<String, Object> initMap() {
    map.put("row", this.rowIndex);
    for (int colIndex=0;colIndex<=titleRow.size();colIndex++) {
      if (row.size()>colIndex) {
         String name=titleRow.get(colIndex);
         String value=row.get(colIndex);
         map.put(name, value);
      }
    }
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
