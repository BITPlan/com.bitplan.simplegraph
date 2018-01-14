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
package com.bitplan.powerpoint;

import java.util.Map;
import java.util.stream.Stream;

import org.apache.poi.xslf.usermodel.XSLFSlide;

import com.bitplan.simplegraph.SimpleGraph;
import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * a slide node that wraps a slide
 * @author wf
 *
 */
public class SlideNode extends SimpleNodeImpl {

  private XSLFSlide slide;

  /**
   * create a SlideNode from the given PowerPoint slide
   * @param simpleGraph
   * @param slide
   */
  public SlideNode(SimpleGraph simpleGraph, XSLFSlide slide) {
    super(simpleGraph,"slide");
    this.slide = slide;
    super.setVertexFromMap();
  }

  @Override
  public Map<String, Object> initMap() {
    map.put("number", slide.getSlideNumber());
    map.put("title", slide.getTitle());
    map.put("comments", slide.getComments());
    return map;
  }

  @Override
  public Stream<SimpleNode> out(String edgeName) {
    // TODO if we want to show detail e.g shapes
    return null;
  }

  @Override
  public Stream<SimpleNode> in(String edgeName) {
    // TODO if we want to link back e.g. to slideshow
    return null;
  }

}
