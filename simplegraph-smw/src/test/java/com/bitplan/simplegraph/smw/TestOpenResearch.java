package com.bitplan.simplegraph.smw;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;

public class TestOpenResearch {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.smw");

  /**
   * get the Semantic Mediawiki System under test
   * 
   * @throws Exception
   */
  public SmwSystem getSMWSystem() throws Exception {
    SmwSystem smw = new SmwSystem();
    // debug=true;
    smw.setDebug(debug);
    smw.connect("https://www.openresearch.org", "/mediawiki");
    return smw;
  }

  @Test
  public void testPage() throws Exception {
    // debug=true;
    SmwSystem smwSystem = getSMWSystem();
    SimpleNode pageNode = smwSystem.moveTo("page=RTAS2020");
    if (debug)
      pageNode.forAll(SimpleNode.printDebug);
    String pageContent = pageNode.getProperty("pagecontent").toString();
    assertTrue(pageContent.contains(
        "Has coordinator=IEEE, USENIX-The Advanced Computing Systems Association"));
  }

  List<String> getPages(String ask) throws Exception {
    List<String> result = new ArrayList<String>();
    SmwSystem smw = getSMWSystem();
    String askQuery = "{{#ask: [[" + ask + "]]\n" + "|mainlabel=page\n"
        + "|headers=plain\n" + "|limit=10000\n" + "}}";
    smw.moveTo("ask=" + askQuery);
    List<Vertex> categoryVs = smw.getStartNode().g().V().toList();
    for (Vertex categoryV : categoryVs) {
      result.add(categoryV.label());
    }
    return result;
  }

  @Test
  public void testCategories() throws Exception {
    String categories[] = { "Event", "Event series", "Journal", "Organization",
        "Paper", "Person", "Project", "Tool" };
    for (String category : categories) {
      List<String> pages = this.getPages("Category:" + category);
      System.out.println(String.format(
          "%s [ label=\"%4d:%s\" URL=\"[https://www.openresearch.org/wiki/Category:%s]\"]",
          category, pages.size(), category, category));
    }
  }

  @Test
  public void testEvents() throws Exception {
    SmwSystem smwSystem = getSMWSystem();
    List<String> pages = this.getPages("Category:Event");
    for (int i = 0; i <= 10; i++) {
      SimpleNode pageNode = smwSystem.moveTo("page=" + pages.get(i));
      if (true)
        pageNode.forAll(SimpleNode.printDebug);
    }
  }

  @Test
  public void testAllCategories() throws Exception {
    List<String> rawCategories = this.getPages(":Category:+");
    List<String> categories = new ArrayList<String>();
    for (String category : rawCategories) {
      if (!category.contains("#"))
        categories.add(category);
    }
    Collections.sort(categories);
    System.out.println(categories.size());
    for (String category : categories) {
      System.out.println(category);
    }
    int catcount = 0;
    for (String category : categories) {
      List<String> pages = this.getPages(category);
      if (pages.size() > 8) {
        System.out.println(String.format("%4d:%s", pages.size(), category));
        catcount++;
      }
    }
    System.out.println(catcount);
  }
}
