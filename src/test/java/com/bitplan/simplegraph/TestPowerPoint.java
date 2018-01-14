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
package com.bitplan.simplegraph;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.POIXMLProperties;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.junit.Test;

import com.bitplan.powerpoint.PowerPointSystem;

/**
 * test the access to the PowerPoint System
 * @author wf
 *
 */
public class TestPowerPoint {

  /**
   * 
   * @param ppt
   */
  public XSLFSlide slideForPerson(XMLSlideShow ppt, SimpleNode person) {
    XSLFSlide slide = ppt.createSlide();
    
    XSLFTextBox shape = slide.createTextBox();
    int x=20;
    int y=20;
    int width=200;
    int height=200;
    shape.setAnchor(new Rectangle(x, y, width, height));
    XSLFTextParagraph p = shape.addNewTextParagraph();
    XSLFTextRun r1 = p.addNewTextRun();
    Map<String, Object> map = person.getMap();
    r1.setText(map.get("name").toString());
    r1.setFontColor(Color.blue);
    r1.setFontSize(24.);
    return slide;
  }
  
  @Test
  public void testPowerPointCreate() throws Exception {
    SimpleSystem royal92 = TestTripleStore.readSiDIF();
    // start with Queen Victoria (Person Id=I1)
    SimpleNode queenVictoria = royal92.moveTo("id=I1");
    
    //create a new empty slide show
    XMLSlideShow ppt = new XMLSlideShow();
    POIXMLProperties props = ppt.getProperties();
    props.getCoreProperties().setTitle("Queen Victoria");
    //add slides
    slideForPerson(ppt,queenVictoria);
    for (SimpleNode child:TestTripleStore.children(queenVictoria)) {
      slideForPerson(ppt,child);
    }
    FileOutputStream out = new FileOutputStream("QueenVictoria.pptx");
    ppt.write(out);
    out.close();
    ppt.close();
  }
  
  @Test
  public void testPowerPointAsGraph() throws Exception {
    PowerPointSystem pps = new PowerPointSystem();
    SimpleNode sls = pps.connect("").moveTo("QueenVictoria.pptx");
    sls.printNameValues(System.out);
    List<SimpleNode> slides = sls.out("slides").collect(Collectors.toCollection(ArrayList::new));
    slides.forEach(slide -> slide.printNameValues(System.out));
    assertEquals(10, slides.size());
  } 

}
