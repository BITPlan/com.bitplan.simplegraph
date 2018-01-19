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
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.junit.Test;

import com.bitplan.powerpoint.PowerPointSystem;
import com.bitplan.powerpoint.Slide;
import com.bitplan.powerpoint.SlideNode;
import com.bitplan.powerpoint.SlideShow;
import com.bitplan.powerpoint.SlideShowNode;

/**
 * test the access to the PowerPoint System
 * 
 * @author wf
 *
 */
public class TestPowerPoint extends BaseTest {

 
  /**
   * 
   * @param sls
   * @throws Exception 
   */
  public Slide slideForNode(SlideShow sls, SimpleNode node, String... props) throws Exception {
    SlideNode slide = (SlideNode) sls.createSlide();
    Map<String, Object> map = node.getMap();
    slide.property("source", map.get("source"));
    int y = 20;
    int x = 20;
    int lwidth = 150;
    int twidth = 500;
    double fontSize = 24.0;
    int height = (int) (fontSize * 1.25);
   
    String name = map.get("name").toString();
    slide.setTitle(name);
    String url = "http://royal-family.bitplan.com/index.php/"
        + name.replaceAll(" ", "_");
    slide.addText(slide.addTextBox(x, y, lwidth, height), "link:", fontSize,
        Color.black);
    slide.addHyperlink(slide.addTextBox(x + lwidth, y, twidth, height),
        map.get("name").toString(), fontSize, Color.blue, url);
    y += height;
    for (String prop : props) {
      Object textObj = map.get(prop);
      if (textObj != null) {
        String text = textObj.toString();
        slide.addText(slide.addTextBox(x, y, lwidth, height), prop + ":",
            fontSize, Color.black);
        slide.addText(slide.addTextBox(x + lwidth, y, twidth, height), text,
            fontSize, Color.blue);
        y += height;
      }
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos, true, "utf-8");
    node.printNameValues(ps);
    ps.close();
    slide.setNotes(new String(baos.toByteArray(), StandardCharsets.UTF_8));
    return slide;
  }
  
  /**
   * get a new powerpoint file for the given path and title
   * @param pptFilePath
   * @param title
   * @return
   * @throws Exception
   */
  public SlideShow getPPT(String pptFilePath,String title) throws Exception {
    PowerPointSystem pps = new PowerPointSystem();
    File pptFile = new File(pptFilePath);
    // remove file if it exists
    if (pptFile.exists())
      pptFile.delete();
    // to force to create a new empty slide show
    SlideShow sls = (SlideShow) pps.connect("").moveTo(pptFilePath);
    sls.setTitle(title);
    return sls;
  }
  
  @Test
  public void testPowerPointCreateWikiData() throws Exception {
    debug=true;
    SimpleNode queenVictoria = TestWikiData.getQueenVictoria();
    String pptFilePath = "QueenVictoria.pptx";
    SlideShow sls =this.getPPT(pptFilePath, "Queen Victoria");
    if (debug)
      queenVictoria.printNameValues(System.out);
    
    String slideprops[] = { 
       "image"
        //"image","sex or gender", "father", "mother", "wikidata_id",
        //"date of birth", "place of birth", "date of death", "wiki_en", "label_en", "source" 
    };
    // add a property "source" to the node
    String source = "WikiData";
    queenVictoria.property("source", source);
    queenVictoria.property("name", queenVictoria.getMap().get("label_en"));
    SlideNode qv = (SlideNode) slideForNode(sls, queenVictoria, slideprops);
    List<SimpleNode> children = queenVictoria.out("child")
        .collect(Collectors.toCollection(ArrayList::new));
    for (SimpleNode child:children) {
      child.property("source", source);
      child.property("name", child.getMap().get("label_en"));
      slideForNode(sls,child,slideprops);
    }
    sls.save();
  }

  @Test
  public void testPowerPointCreateRoyal92() throws Exception {
    SimpleSystem royal92 = TestTripleStore.readSiDIF();
    // start with Queen Victoria (Person Id=I1)
    SimpleNode queenVictoria = royal92.moveTo("id=I1");
    String pptFilePath = "QueenVictoriaRoyal92.pptx";
    SlideShow sls = this.getPPT(pptFilePath, "Queen Victoria");
    
    /**
     * born = 1819-05-24 sex = female nobleTitle = Queen of England childOf =
     * F42 yearBorn = 1819 parentOf = F1 died = 1901-01-22 diedAt = Royal
     * Mausoleum,Frogmore,Berkshire,England birthPlace =
     * Kensington,Palace,London,England yearDied = 1901 isA = Person monthBorn =
     * 5 name = Victoria Hanover id = I1 monthDied = 1
     */
    String slideprops[] = { "name", "nobleTitle", "sex", "yearBorn",
        "birthPlace", "yearDied", "diedAt", "source" };
    // add slides
    String source = "Royal 92 GEDCOM file";
    queenVictoria.property("source", source);
    SlideNode qv = (SlideNode) slideForNode(sls, queenVictoria, slideprops);
    // debug = true;
    for (SimpleNode child : TestTripleStore.children(queenVictoria, 1)) {
      child.property("source", source);
      SlideNode slide = (SlideNode) slideForNode(sls, child, slideprops);
      assertEquals(slide.getTitle(), child.getMap().get("name"));
      String text = slide.getText();
      if (debug)
        System.out.println(text.replaceAll("<br>", "\n"));
      assertTrue(text.contains(child.getMap().get("nobleTitle").toString()));
      String notes = slide.getNotes();
      if (debug)
        System.out.println(notes.replaceAll("<br>", "\n"));
      // check that the newly set property value source is available wher expect
      String csource = slide.getMap().get("source").toString();
      assertEquals(source, csource);
      assertEquals(source,
          slide.getVertex().property("source").value().toString());
      assertTrue(notes.contains(child.getMap().get("childOf").toString()));
      assertTrue(notes.contains(source));
    }
    sls.save();
  }

  @Test
  public void testPowerPointAsGraph() throws Exception {
    PowerPointSystem pps = new PowerPointSystem();
    SlideShowNode sls = (SlideShowNode) pps.connect("")
        .moveTo("QueenVictoria.pptx");
    if (debug)
      sls.printNameValues(System.out);
    assertEquals("Queen Victoria", sls.getTitle());
    List<SimpleNode> slides = sls.out("slides")
        .collect(Collectors.toCollection(ArrayList::new));
    if (debug)
      slides.forEach(slide -> slide.printNameValues(System.out));
    assertEquals(10, slides.size());
    pps.graph().io(IoCore.graphml()).writeGraph("QueenVictoriaPowerPoint.xml");
  }

}
