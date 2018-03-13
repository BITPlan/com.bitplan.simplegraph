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
package com.bitplan.simplegraph.powerpoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.poi.POIXMLProperties;
import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * a slide show node wraps a power point slideshow
 * 
 * @author wf
 *
 */
public class SlideShowNode extends SimpleNodeImpl implements SlideShow {

  protected XMLSlideShow slideshow;
  protected String pathOrUl;
  protected File pptFile;

  /**
   * get the SlideShow
   */
  public XMLSlideShow getSlideshow() {
    return slideshow;
  }

  /**
   * create a SlideShow
   * 
   * @param simpleGraph
   * @param nodeQuery
   * @throws Exception
   */
  public SlideShowNode(SimpleGraph simpleGraph, String pathOrUrl,
      String... keys) {
    super(simpleGraph, "slideshow", keys);
    InputStream is = null;
    try {
      try {
        URL url = new URL(pathOrUrl);
        is = url.openStream();
      } catch (MalformedURLException e1) {
        this.pathOrUl = pathOrUrl;
        pptFile = new File(pathOrUl);
        if (pptFile.canRead())
          is = new FileInputStream(pathOrUl);
      }
      if (is != null)
        slideshow = new XMLSlideShow(is);
      else
        slideshow = new XMLSlideShow();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    super.setVertexFromMap();
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
  public Map<String, Object> initMap() {
    map.put("path", pathOrUl);
    CoreProperties cp = getCoreProperties();
    map.put("title", cp.getTitle());
    return map;
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
      List<SimpleNode> slideNodes = new ArrayList<SimpleNode>();
      List<XSLFSlide> slides = slideshow.getSlides();
      int pageNo = 1;
      for (XSLFSlide slide : slides) {
        SlideNode slideNode = new SlideNode(this, slide);
        slideNodes.add(slideNode);
        slideNode.property("pageNo", pageNo++);
        slideNode.property("pages", slides.size());
      }
      links = slideNodes.stream();
      break;
    }
    return links;
  }

  @Override
  public String getTitle() {
    Object titleObj = map.get("title");
    if (titleObj != null)
      return titleObj.toString();
    else
      return "";
  }

  @Override
  public void setTitle(String title) {
    this.getCoreProperties().setTitle(title);
    map.put("title", title);
  }

  @Override
  public void save() throws Exception {
    FileOutputStream out = new FileOutputStream(pathOrUl);
    slideshow.write(out);
    out.close();
    slideshow.close();
  }

  @Override
  public Slide createSlide() {
    XSLFSlide xslide = slideshow.createSlide();
    return new SlideNode(this, xslide);
  }

}
