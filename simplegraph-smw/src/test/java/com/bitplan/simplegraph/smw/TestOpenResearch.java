package com.bitplan.simplegraph.smw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Ignore;
import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;

/**
 * test semantic mediawiki API access for the openresearch site
 * 
 * @author wf
 *
 */
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

  public String getMatchGroup(String s, String pattern, int group) {
    Pattern rp = Pattern.compile(pattern, Pattern.DOTALL);
    String result = null;
    if (s != null) {
      Matcher m = rp.matcher(s);
      if (m.find()) {
        result = m.group(group);
      }
    }
    return result;
  }

  /**
   * get properties for the given topic
   * 
   * @param pageContent
   * @param topic
   * @return Properties
   */
  public List<String> getProps(String pageContent, String topic) {
    String pattern = "\\{\\{" + topic + "\\s*?(.*?)\\s*?\\}\\}";
    List<String> props = new ArrayList<String>();
    String nameValueStr = this.getMatchGroup(pageContent, pattern, 1);
    if (nameValueStr != null) {
      String[] nameValues = nameValueStr.split("\\|");
      for (String nameValue : nameValues) {
        if (!nameValue.isEmpty()) {
          String[] pair = nameValue.split("=");
          String name = pair[0].trim();
          props.add(name);
        }
      }
    }
    return props;
  }

  @Test
  public void testGetProps() {
    String pageContent = "{{Journal\n"
        + " | title = Transactions on Design Automation of Electronic Systems\n"
        + " | logo = \n" + " | Field = Computer science\n"
        + " | Homepage = acm.org/pubs/todaes\n" + " | has impact factor = \n"
        + " | has editor = \n" + " | has publisher = ACM\n"
        + " | has frequency = \n" + " | Start date = \n" + " | End date = \n"
        + "}}";
    List<String> props = this.getProps(pageContent, "Journal");
    assertEquals(11, props.size());
  }

  public class Property {
    String name;
    String type;
    String page;

    public Property(String name) {
      this.name = name;
      this.page = name.replace(" ", "_");
      this.type = "Page";
    }
  }

  public Property getProperty(SmwSystem smwSystem, String propName)
      throws Exception {
    String pageContent = smwSystem.getWiki()
        .getPageContent("Property:" + propName);
    if (pageContent == null)
      return null;
    Property prop = new Property(propName);
    // System.out.println(pageContent);
    String pattern = "\\[\\[has type::(.*?)(\\|.*)?\\]\\]";
    String type = this.getMatchGroup(pageContent, pattern, 1);
    if (type != null)
      prop.type = type;
    return prop;
  }

  @Test
  public void testGetProperty() throws Exception {
    SmwSystem smwSystem = getSMWSystem();
    Property prop = getProperty(smwSystem, "Acronym");
    assertEquals(prop.name, "Acronym");
    assertEquals(prop.type, "Text");
  }

  public Map<String, Property> getPropertyMapForPageList(String topic,
      List<String> pages, int sampleCount) throws Exception {
    sampleCount = Math.min(sampleCount, pages.size());
    SmwSystem smwSystem = getSMWSystem();
    Map<String, Property> propMap = new HashMap<String, Property>();
    for (int i = 0; i < sampleCount; i++) {
      String page = pages.get(i);
      // System.out.println(page);
      String pageContent = smwSystem.getWiki().getPageContent(page);
      if (pageContent != null) {
        List<String> props = this.getProps(pageContent, topic);
        for (String propName : props) {
          if (!propName.isEmpty() && !propMap.containsKey(propName)) {
            Property prop = this.getProperty(smwSystem, propName);
            if (prop != null)
              propMap.put(propName, prop);
          }
        }
      }
    }
    return propMap;
  }

  @Test
  public void testPropsForCategory() throws Exception {
    String category = "Journal";
    List<String> pages = this.getPages("Category:" + category);
    Map<String, Property> propMap = this.getPropertyMapForPageList(category,
        pages, 7);
    for (Property prop : propMap.values()) {
      System.out.println(String.format("%s %s", prop.type, prop.name));
    }
  }

  @Test
  public void testCategories() throws Exception {
    String categories[] = { "Event", "Event series", "Journal", "Organization",
        "Paper", "Person", "Project", "Tool" };
    for (String category : categories) {
      String categoryPage = category.replace(" ", "_");
      List<String> pages = this.getPages("Category:" + categoryPage);
      String template = "";
      template += "note top of %s\n";
      template += "[[https://www.openresearch.org/wiki/Category:%s %s]]\n";
      template += "2020-02: %d instances\n";
      template += "end note\n";
      template += "class %s [[https://www.openresearch.org/wiki/Category:%s %s]] {";

      // System.out.println(String.format(
      // "%s [ label=\"%4d:%s\"
      // URL=\"[https://www.openresearch.org/wiki/Category:%s]\"]",
      // category, pages.size(), category, category));
      System.out.println(String.format(template, categoryPage, categoryPage,
          category, pages.size(), categoryPage, categoryPage, category));
      Map<String, Property> propMap = this.getPropertyMapForPageList(category,
          pages, 7);
      for (Property prop : propMap.values())
        System.out.println(String.format(
            "  %s [[https://www.openresearch.org/wiki/Property:%s %s]]",
            prop.type, prop.page, prop.name));
      System.out.println("}");
    }
  }

  @Test
  public void testEvents() throws Exception {
    SmwSystem smwSystem = getSMWSystem();
    List<String> pages = this.getPages("Category:Event");
    String msg=String.format("expected >=6000 pages for Category:Event but found %d", pages.size());
    assertTrue(msg,6000 <= pages.size());
    if (debug)
      for (int i = 0; i <= 10; i++) {
        SimpleNode pageNode = smwSystem.moveTo("page=" + pages.get(i));
        if (true)
          pageNode.forAll(SimpleNode.printDebug);
      }
  }

  @Ignore
  // long running test - please activate only when needed
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
