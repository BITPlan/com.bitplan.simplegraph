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

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.xslf.usermodel.XSLFHyperlink;
import org.apache.poi.xslf.usermodel.XSLFNotes;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide;

import com.bitplan.simplegraph.SimpleGraph;
import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * a slide node that wraps a powerpoint slide
 * @author wf
 *
 */
public class SlideNode extends SimpleNodeImpl implements Slide {

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
  
  public SlideShow getSlideShow() {
    return (SlideShow)super.getSimpleGraph();
  }

  @Override
  public Map<String, Object> initMap() {
    map.put("number", slide.getSlideNumber());
    map.put("title", slide.getTitle());
    map.put("comments", slide.getComments());
    map.put("name", getName());
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
  
  /**
   * add a text box
   * @param sheet 
   * @param x
   * @param y
   * @param width
   * @param height
   * @return
   */
  public XSLFTextBox addTextBox(int x,int y,int width,int height) {
    XSLFTextBox textbox = slide.createTextBox();
    Rectangle rect=new Rectangle(x,y,width,height);
    textbox.setAnchor(rect);
    return textbox;
  }
  
  /**
   * add Text to the given shape
   * @param shape
   * @param text
   * @param fontSize
   * @param color
   * @return the text Run
   */
  public XSLFTextRun addText(XSLFTextShape shape,String text, double fontSize,Color color) {
    XSLFTextRun r = shape.addNewTextParagraph().addNewTextRun();
    r.setText(text);
    r.setFontSize(fontSize);
    r.setFontColor(color);
    return r;
  }
  
  /**
   * add a Hyperlink to the given text
   * 
   * @param shape
   * @param text
   * @param link
   * @return - the hyperlink just created
   */
  public XSLFHyperlink addHyperlink(XSLFTextShape shape,String text,double fontSize, Color color, String link) {
    XSLFTextRun r = this.addText(shape, text, fontSize, color);
    XSLFHyperlink hyperlink = r.createHyperlink();
    hyperlink.setAddress(link);
    return hyperlink;
  }

  /**
   * get the text from the given list of shapes
   * 
   * @param shapes
   * @return
   */
  public String getShapesText(List<XSLFShape> shapes) {
    String result = "";
    String delim = "";
    if (shapes != null) {
      for (XSLFShape shape : shapes) {
        result+=this.getShapeText(shape, delim, "<br>");
      }
    }
    return result;
  }

  /**
   * get the text of the given Shape
   * 
   * @param shape
   * @param delim
   * @param sep
   * @return
   */
  public String getShapeText(XSLFShape shape, String delim, String sep) {
    String result = "";
    if (shape instanceof XSLFTextShape) {
      XSLFTextShape txShape = (XSLFTextShape) shape;
      for (XSLFTextParagraph xslfParagraph : txShape.getTextParagraphs()) {
        result += delim + xslfParagraph.getText();
        delim = sep;
      }
    }
    return result;
  }
  
  public void setNotes(String text) {
    // get or create notes
    XSLFNotes note = this.getSlideShow().getSlideshow().getNotesSlide(slide);
    // insert text into the first possible placeholder
    for (XSLFTextShape shape : note.getPlaceholders()) {
        if (shape.getTextType() == Placeholder.BODY) {
            shape.setText(text);
            break;
        }
    }
  }

  /**
   * get the text of the notes
   * see e.g. https://stackoverflow.com/q/24873725/1497139
   */
  public String getNotes() {
    String result = "";
    String delim = "";
    XSLFNotes notes = slide.getNotes();
    if (notes != null) {
      for (XSLFShape shape : notes) {
        result += getShapeText(shape, delim, "<br>");
      }
    }
    return result;
  }

  @Override
  public String getText() {
    String result = this.getShapesText(slide.getShapes());
    return result;
  }
  
  public String getName() {
    CTSlide ctSlide = slide.getXmlObject();
    String name=ctSlide.getCSld().getName();
    return name;
  }

  @Override
  public String getTitle() {
    return map.get("title").toString();
  }

  @Override
  public void setTitle(String title) {
    map.put("title",title);
  }


  @Override
  public int getPageNo() {
    return (Integer)map.get("pageNo");
  }

  @Override
  public int getPages() {
    return (Integer)map.get("pages");
  }

}
