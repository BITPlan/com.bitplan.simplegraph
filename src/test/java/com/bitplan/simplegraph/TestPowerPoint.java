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
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.junit.Test;

import com.bitplan.powerpoint.PowerPointSystem;

/**
 * test the access to the PowerPoint System
 * 
 * @author wf
 *
 */
public class TestPowerPoint {

  /**
   * 
   * @param ppt
   */
  public XSLFSlide slideForNode(XMLSlideShow ppt, SimpleNode node,
      String ... props) {
    XSLFSlide slide = ppt.createSlide();
    int y = 20;
    Map<String, Object> map = node.getMap();
    for (String prop : props) {
      XSLFTextBox shape = slide.createTextBox();
      int x = 20;
      int width = 600;
      int height = 25;
      shape.setAnchor(new Rectangle(x, y, width, height));
      XSLFTextParagraph p = shape.addNewTextParagraph();
      XSLFTextRun r1 = p.addNewTextRun();
      r1.setText(prop + ":" + map.get(prop).toString());
      r1.setFontColor(Color.blue);
      r1.setFontSize(24.);
      y += height;
    }
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
    /**
     * born = 1819-05-24
sex = female
nobleTitle = Queen of England
childOf = F42
yearBorn = 1819
parentOf = F1
died = 1901-01-22
diedAt = Royal Mausoleum,Frogmore,Berkshire,England
birthPlace = Kensington,Palace,London,England
yearDied = 1901
isA = Person
monthBorn = 5
name = Victoria Hanover
id = I1
monthDied = 1
     */
    String slideprops[]= {"name","nobleTitle","sex","yearBorn","birthPlace","yearDied","diedAt"};
    //add slides
    slideForNode(ppt,queenVictoria,slideprops);
    for (SimpleNode child:TestTripleStore.children(queenVictoria,1)) {
      slideForNode(ppt,child,slideprops);
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
    List<SimpleNode> slides = sls.out("slides")
        .collect(Collectors.toCollection(ArrayList::new));
    slides.forEach(slide -> slide.printNameValues(System.out));
    assertEquals(10, slides.size());
    pps.graph().io(IoCore.graphml()).writeGraph("QueenVictoriaPowerPoint.xml");
  }

}
