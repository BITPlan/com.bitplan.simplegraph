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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.mediawiki.MediaWikiPageNode;
import com.bitplan.simplegraph.mediawiki.MediaWikiSystem;
import com.bitplan.simplegraph.powerpoint.PowerPointSystem;
import com.bitplan.simplegraph.powerpoint.Slide;
import com.bitplan.simplegraph.powerpoint.SlideNode;
import com.bitplan.simplegraph.powerpoint.SlideShow;
import com.bitplan.simplegraph.powerpoint.SlideShowNode;
import com.bitplan.simplegraph.triplestore.TestTripleStore;
import com.bitplan.simplegraph.wikidata.TestWikiData;


/**
 * test the access to the PowerPoint System
 * 
 * @author wf
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPowerPointSystem  {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.powerpoint");
  
  /**
   * 
   * @param sls
   * @throws Exception
   */
  public Slide slideForNode(SlideShow sls, SimpleNode node, String... props)
      throws Exception {
    SlideNode slide = (SlideNode) sls.createSlide();
    Map<String, Object> map = node.getMap();
    String source = map.get("source").toString();
    slide.property("source", source);
    int y = 20;
    int x = 235;
    int lwidth = 115;
    int twidth = 350;
    double fontSize = 16.0;
    int height = (int) (fontSize * 1.5);
    int imageheight = 800;
    String name = map.get("name").toString();
    slide.setTitle(name);
    String url = null;
    if ("WikiData".equals(source))
      url = "https://tools.wmflabs.org/sqid/#/view?id="
          + node.getProperty("wikidata_id");
    else
      url = "http://royal-family.bitplan.com/index.php/"
          + name.replaceAll(" ", "_");
    slide.addText(slide.addTextBox(x, y, lwidth, height), "link:", fontSize,
        Color.black);
    slide.addHyperlink(slide.addTextBox(x + lwidth, y, twidth, height),
        map.get("name").toString(), fontSize, Color.blue, url);
    y += height;
    for (String prop : props) {
      Object propObj = node.getProperty(prop);
      if (propObj != null) {
        if (propObj instanceof String) {
          String text = propObj.toString();
          slide.addText(slide.addTextBox(x, y, lwidth, height), prop + ":",
              fontSize, Color.black);
          double tFontSize = fontSize;
          if (text.length() > 45)
            tFontSize = fontSize * 0.75;
          slide.addText(slide.addTextBox(x + lwidth, y, twidth, height),
              propObj.toString(), tFontSize, Color.blue);
          y += height;
          // if (y > imageheight) {
          // x = 20;
          // }
        } else if (propObj instanceof BufferedImage) {
          BufferedImage image = (BufferedImage) propObj;
          XSLFPictureShape picture = slide.addPicture(image);
          if (picture != null) {
            imageheight = image.getHeight();
            slide.setPosition(picture, 20, y - height, image.getWidth(),
                imageheight);
          }
        } else {
          LOGGER.log(Level.INFO, String.format("property %s=(%s) %s", prop,
              propObj.getClass().getSimpleName(), propObj.toString()));
        }

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
   * 
   * @param pptFilePath
   * @param title
   * @return
   * @throws Exception
   */
  public SlideShow getPPT(String pptFilePath, String title) throws Exception {
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

  /**
   * get the image for the given person
   * 
   * @param mws
   * @param personNode
   * @return - the buffered image
   * @throws Exception
   */
  public BufferedImage getImage(MediaWikiSystem mws, SimpleNode personNode,
      int size) throws Exception {
    if (personNode==null)
      return null;
    String image = personNode.getProperty("image").toString();
    MediaWikiPageNode pageNode = (MediaWikiPageNode) mws
        .moveTo("File:" + image);
    return pageNode.getImage(size);
  }

  /**
   * add a slide for the given person
   * 
   * @param sls
   * @param mws
   * @param person
   * @param slideprops
   * @param debug
   * @throws Exception
   */
  private void addSlide(SlideShow sls, MediaWikiSystem mws, SimpleNode person,
      String[] slideprops, boolean debug) throws Exception {
    // add a property "source" to the node
    String source = "WikiData";
    if (person.getMap().containsKey("P18")) {
      BufferedImage image = this.getImage(mws, person, 200);
      if (image != null)
        person.property("picture", image);
    }
    person.property("source", source);
    person.property("name", person.getMap().get("label_en"));
    SlideNode personNode = (SlideNode) slideForNode(sls, person, slideprops);
    if (debug)
      personNode.printNameValues(System.out);
  }

  /**
   * add slides for children
   * 
   * @param sls
   * @param mws
   * @param mws
   * @param person
   * @param levels
   * @throws Exception
   */
  private void addSlides(SlideShow sls, MediaWikiSystem mws, SimpleNode person,
      String[] slideprops, int levels, boolean debug) throws Exception {
    this.addSlide(sls, mws, person, slideprops, debug);
    List<SimpleNode> children = person.out("child")
        .collect(Collectors.toCollection(ArrayList::new));
    for (SimpleNode child : children) {
      if (levels >= 1) {
        addSlides(sls, mws, child, slideprops, levels - 1, debug);
      }
    }
  }

  @Test
  public void testPowerPointCreateWikiData() throws Exception {
    debug = true;
    // prepare access to wikimedia for getting pictures
    MediaWikiSystem mws = new MediaWikiSystem();
    mws.connect("https://commons.wikimedia.org", "/w");

    String props[] = { "P21", "P22", "P25", "P109", "P569", "P19", "P570",
        "P20", "P1543" };
    SimpleNode queenVictoria = TestWikiData.getQueenVictoria(props);
    String pptFilePath = "../simplegraph-powerpoint/QueenVictoria.pptx";
    SlideShow sls = this.getPPT(pptFilePath, "Queen Victoria");
    if (debug)
      queenVictoria.printNameValues(System.out);

    String slideprops[] = { "picture", "wikidata_id", "image", "sex or gender",
        "father", "mother", "date of birth", "place of birth", "date of death",
        "place of death", "wiki_en", "label_en", "source" };
    // add slides for children and grand children of queen victoria
    this.addSlides(sls, mws, queenVictoria, slideprops, 1, debug);
    sls.save();
    TestWikiData.wikiDataSystem.close();
  }

  @Test
  public void testPowerPointCreateRoyal92() throws Exception {
    SimpleSystem royal92 = TestTripleStore.readSiDIF();
    // start with Queen Victoria (Person Id=I1)
    SimpleNode queenVictoria = royal92.moveTo("id=I1");
    String pptFilePath = "../simplegraph-powerpoint/QueenVictoriaRoyal92.pptx";
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
    assertNotNull(qv);
    // debug = true;
    for (SimpleNode child : TestTripleStore.children(queenVictoria, 1)) {
      child.property("source", source);
      SlideNode slide = (SlideNode) slideForNode(sls, child, slideprops);
      assertEquals(slide.getTitle(), child.getMap().get("name"));
      String text = slide.getText(SlideNode.SEPARATOR);
      if (debug)
        System.out.println(text);
      assertTrue(text.contains(child.getMap().get("nobleTitle").toString()));
      String notes = slide.getNotes(SlideNode.SEPARATOR);
      if (debug)
        System.out.println(notes);
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
  public void testPowerPointDoafterCreateAsGraph() throws Exception {
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
    pps.graph().io(IoCore.graphml()).writeGraph("../simplegraph-powerpoint/QueenVictoriaPowerPoint.xml");
  }

}
