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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.xslf.usermodel.XSLFHyperlink;
import org.apache.poi.xslf.usermodel.XSLFNotes;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide;

import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleStepNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * a slide node that wraps a powerpoint slide
 * 
 * @author wf
 *
 */
public class SlideNode extends SimpleNodeImpl implements Slide, SimpleStepNode {

  public static String SEPARATOR = "\n";
  boolean failSafe = true;
  private XSLFSlide slide;
  transient protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.powerpoint");

  /**
   * get the underlying powerpoint slide
   * 
   * @return
   */
  public XSLFSlide getSlide() {
    return slide;
  }

  /**
   * reinit constructor
   * 
   * @param simpleGraph
   * @param keys
   */
  public SlideNode(PowerPointSystem ps, String... keys) {
    this(ps, null, keys);
  }

  /**
   * create a SlideNode from the given PowerPoint slide
   * 
   * @param simpleGraph
   * @param slide
   */
  public SlideNode(SimpleGraph simpleGraph, XSLFSlide slide, String... keys) {
    super(simpleGraph, "slide", keys);
    this.slide = slide;
    if (slide != null)
      super.setVertexFromMap();
  }

  public SlideShow getSlideShow() {
    return (SlideShow) super.getSimpleGraph();
  }

  @Override
  public Map<String, Object> initMap() {
    map.put("number", slide.getSlideNumber());
    map.put("title", slide.getTitle());
    map.put("comments", slide.getComments());
    map.put("name", getName());
    map.put("text", getText(SEPARATOR));
    map.put("notes", getNotes(SEPARATOR));
    return map;
  }

  /**
   * set the position of a simple shape
   */
  public XSLFSimpleShape setPosition(XSLFSimpleShape shape, int x, int y,
      int width, int height) {
    Rectangle rect = new Rectangle(x, y, width, height);
    shape.setAnchor(rect);
    return shape;
  }

  /**
   * add a text box
   * 
   * @param sheet
   * @param x
   * @param y
   * @param width
   * @param height
   * @return
   */
  public XSLFTextBox addTextBox(int x, int y, int width, int height) {
    XSLFTextBox textbox = slide.createTextBox();
    setPosition(textbox, x, y, width, height);
    return textbox;
  }

  /**
   * add Text to the given shape
   * 
   * @param shape
   * @param text
   * @param fontSize
   * @param color
   * @return the text Run
   */
  public XSLFTextRun addText(XSLFTextShape shape, String text, double fontSize,
      Color color) {
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
  public XSLFHyperlink addHyperlink(XSLFTextShape shape, String text,
      double fontSize, Color color, String link) {
    XSLFTextRun r = this.addText(shape, text, fontSize, color);
    XSLFHyperlink hyperlink = r.createHyperlink();
    hyperlink.setAddress(link);
    return hyperlink;
  }

  /**
   * add a Picture
   * 
   * @param image
   * @return
   * @throws Exception
   */
  public XSLFPictureShape addPicture(BufferedImage image) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      ImageIO.write(image, "jpg", baos);
    } catch (IOException e) {
      if (failSafe) {
        LOGGER.log(Level.WARNING, "addPicture failed", e);
        return null;
      } else
        throw new RuntimeException(e);
    }
    byte[] pictureData = baos.toByteArray();
    XSLFPictureData pd = getSlideShow().getSlideshow().addPicture(pictureData,
        PictureData.PictureType.JPEG);
    XSLFPictureShape pic = slide.createPicture(pd);
    return pic;
  }

  /**
   * get the text from the given list of shapes
   * 
   * @param shapes
   * @return
   */
  public String getShapesText(List<XSLFShape> shapes, String sep) {
    String result = "";
    String delim = "";
    if (shapes != null) {
      for (XSLFShape shape : shapes) {
        result += delim + SlideNode.getShapeText(shape, delim, sep);
        delim = sep;
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
  public static String getShapeText(XSLFShape shape, String delim, String sep) {
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
  
  /**
   * set the notes to a given text
   */
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
   * get the text of the notes see e.g.
   * https://stackoverflow.com/q/24873725/1497139
   */
  @Override
  public String getNotes(String separator) {
    String result = "";
    String delim = "";
    XSLFNotes notes = slide.getNotes();
    if (notes != null) {
      for (XSLFShape shape : notes) {
        result += delim + getShapeText(shape, delim, separator);
        delim = separator;
      }
    }
    return result;
  }

  @Override
  public String getText(String separator) {
    String result = this.getShapesText(slide.getShapes(), separator);
    return result;
  }

  /**
   * get the slide as an image
   * 
   * @param out
   * @param width
   * @param height
   * @param zoom
   * @param withBackground
   * @throws Exception
   */
  public void outputSlideAsImage(OutputStream out, double zoom,
      boolean withBackground) throws Exception {
    Dimension pageSize = this.getSlideShow().getSlideshow().getPageSize();
    SlideImage slideImage = new SlideImage(getSlide(), pageSize.width,
        pageSize.height, zoom);
    slideImage.drawImage(withBackground);
    slideImage.save(out);
  }

  /**
   * output the given slide as an image
   * 
   * @param slideFile
   * @param zoom
   * @param withBackground
   * @throws Exception
   */
  public void outputSlideAsImage(File slideFile, double zoom,
      boolean withBackground) throws Exception {
    FileOutputStream out = new FileOutputStream(slideFile);
    this.outputSlideAsImage(out, zoom, withBackground);
    long fileLen=slideFile.length();
    if (fileLen==0) {
      throw new Exception("outputSlideAsImage failed for "+slideFile.getName()+" size of file is 0");
    }
  }

  public String getName() {
    CTSlide ctSlide = slide.getXmlObject();
    String name = ctSlide.getCSld().getName();
    return name;
  }

  @Override
  public String getTitle() {
    return map.get("title").toString();
  }

  @Override
  public void setTitle(String title) {
    map.put("title", title);
  }

  @Override
  public int getPageNo() {
    return (Integer) map.get("pageNo");
  }

  @Override
  public int getPages() {
    return (Integer) map.get("pages");
  }

  @Override
  public Stream<SimpleStepNode> out(String edgeName) {
    return null;
  }

  @Override
  public Stream<SimpleStepNode> in(String edgeName) {
    return null;
  }

}
