package com.bitplan.simplegraph;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.POIXMLProperties;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.junit.Test;

import com.bitplan.powerpoint.PowerPointSystem;

public class TestPowerPoint {

  @Test
  public void testPowerPointCreate() throws Exception {
    //create a new empty slide show
    XMLSlideShow ppt = new XMLSlideShow();
    POIXMLProperties props = ppt.getProperties();
    props.getCoreProperties().setTitle("Queen Victoria");
    //add slides
    for (int i=1;i<=9;i++) {
      XSLFSlide slide = ppt.createSlide();
     
      XSLFTextBox shape = slide.createTextBox();
      int x=20;
      int y=20;
      int width=200;
      int height=200;
      shape.setAnchor(new Rectangle(x, y, width, height));
      XSLFTextParagraph p = shape.addNewTextParagraph();
      XSLFTextRun r1 = p.addNewTextRun();
      r1.setText("Slide "+i);
      r1.setFontColor(Color.blue);
      r1.setFontSize(24.);

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
    assertEquals(9, slides.size());
  } 

}
