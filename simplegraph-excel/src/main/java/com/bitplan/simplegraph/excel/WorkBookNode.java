package com.bitplan.simplegraph.excel;

import java.util.Map;
import java.util.stream.Stream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
  
  /**
   * create a Work Book Node
   * @param simpleGraph
   * @param kind
   * @param keys
   */
  public WorkBookNode(SimpleGraph simpleGraph, String kind, String[] keys) {
    super(simpleGraph, kind, keys);
    // TODO Auto-generated constructor stub
  }

  @Override
  public Map<String, Object> initMap() {
    // TODO Auto-generated method stub
    return null;
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
