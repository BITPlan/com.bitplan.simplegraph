package com.bitplan.powerpoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.poi.POIXMLProperties;
import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.tinkerpop.gremlin.structure.T;

import com.bitplan.filesystem.FileNode;
import com.bitplan.simplegraph.SimpleGraph;
import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

public class SlideShowNode extends SimpleNodeImpl{

  protected XMLSlideShow slideshow;
  protected String path;
  /**
   * create a SlideShow
   * @param simpleGraph
   * @param nodeQuery
   * @throws Exception
   */
  public SlideShowNode(SimpleGraph simpleGraph, String path) throws Exception {
    super(simpleGraph);
    this.path=path;
    slideshow=new XMLSlideShow(new FileInputStream(path));
    // my properties e.g. title
    Map<String, Object> map = getMap();
    super.setVertex(simpleGraph.graph().addVertex(T.label, "slideshow", "title", map.get("title")));
  }

  
  /**
   * get the core properties
   * 
   * @return
   */
  public CoreProperties getCoreProperties() {
    POIXMLProperties props = slideshow.getProperties();
    CoreProperties cp = props.getCoreProperties();
    return cp;
  }

  @Override
  public Map<String, Object> getMap() {
    Map<String, Object> result = new HashMap<String, Object>();
    CoreProperties cp=getCoreProperties();
    result.put("title", cp.getTitle());
    return result;
  }

  @Override
  public Stream<SimpleNode> out(String edgeName) {
    return inOrOut(edgeName);
  }
  
  @Override
  public Stream<SimpleNode> in(String edgeName) {
    return inOrOut(edgeName);
  }

  private Stream<SimpleNode> inOrOut(String edgeName) {
    Stream<SimpleNode> links = Stream.of();
    switch (edgeName) {
    case "slides":
        List<SimpleNode> slides = new ArrayList<SimpleNode>();
        for (XSLFSlide slide : slideshow.getSlides() ){
          SlideNode slideNode=new SlideNode(this,slide);
          slides.add(slideNode);
        }
        links = slides.stream();
      break;
    }
    return links;
  }


 
}
