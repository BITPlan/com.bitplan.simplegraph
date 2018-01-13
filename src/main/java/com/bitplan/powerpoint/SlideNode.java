package com.bitplan.powerpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.tinkerpop.gremlin.structure.T;

import com.bitplan.simplegraph.SimpleGraph;
import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

public class SlideNode extends SimpleNodeImpl {

  private XSLFSlide slide;

  public SlideNode(SimpleGraph simpleGraph, XSLFSlide slide) {
    super(simpleGraph);
    this.slide = slide;
    Map<String, Object> map = getMap();
    super.setVertex(simpleGraph.graph().addVertex(T.label, "slide", "number",map.get("number")));
  }

  @Override
  public Map<String, Object> getMap() {
    // FIXME make getMap and setVertex less redundant
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("number", slide.getSlideNumber());
    result.put("title", slide.getTitle());
    result.put("comments", slide.getComments());
    return result;
  }

  @Override
  public Stream<SimpleNode> out(String edgeName) {
    // TODO if we want to show detail e.g shapse
    return null;
  }

  @Override
  public Stream<SimpleNode> in(String edgeName) {
    // TODO if we want to link back e.g. to slideshow
    return null;
  }

}
