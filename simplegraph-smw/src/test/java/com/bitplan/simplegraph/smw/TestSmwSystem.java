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
package com.bitplan.simplegraph.smw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import com.bitplan.rythm.RythmContext;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.json.JsonPrettyPrinter;
import com.bitplan.simplegraph.smw.SmwSystem.Geo;
import com.bitplan.simplegraph.smw.SmwSystem.WikiPage;

/**
 * test Semantic Mediawiki access
 * 
 * @author wf
 *
 */
public class TestSmwSystem {
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
    smw.connect("https://www.semantic-mediawiki.org", "/w");
    return smw;
  }

  @Test
  public void testPage() throws Exception {
    // debug=true;
    SmwSystem smwSystem = getSMWSystem();
    SimpleNode pageNode = smwSystem.moveTo("page=Sol");
    if (debug)
      pageNode.forAll(SimpleNode.printDebug);
    String pageContent = pageNode.getProperty("pagecontent").toString();
    assertTrue(pageContent.contains("star at the center"));
  }

  @Test
  public void testAsk() throws Exception {
    // debug = true;
    SmwSystem smwSystem = getSMWSystem();
    smwSystem.setDebug(debug);
    // see https://www.semantic-mediawiki.org/wiki/Help:Concepts
    String query = "{{#ask:[[Concept:Semantic MediaWiki Cons 2012]]\n"
        + "|?Has_Wikidata_item_ID=WikiDataId\n"
        + "|?Has planned finish=finish\n" + "|?Has planned start=start\n"
        + "|?Has_location=location\n" + "|format=table\n" + "}}";
    SimpleNode askResult = smwSystem.moveTo(query);
    if (debug)
      askResult.forAll(SimpleNode.printDebug);
    long printOutCount = askResult.g().V().hasLabel("printouts").count().next()
        .longValue();
    assertEquals(2, printOutCount);
    long nodeCount = askResult.g().V().count().next().longValue();
    assertEquals(19, nodeCount);
    Object metaCount = askResult.g().V().hasLabel("meta").next()
        .property("count").value();
    assertNotNull(metaCount);
    assertEquals(2, Integer.parseInt(metaCount.toString()));
    // debug = true;
    if (debug)
      askResult.g().V().has("isA", "Semantic MediaWiki Cons 2012")
          .forEachRemaining(SimpleNode.printDebug);
  }

  @Test
  public void testFixAsk() {
    // debug=true;
    String askQuery = "{{#ask:[[Concept:Semantic Web Events 2012]]\n"
        + "|?Has_location\n" + "|format=table\n" + "}}";
    String fixedAsk = SmwSystem.fixAsk(askQuery);
    if (debug)
      System.out.println(fixedAsk);
    assertTrue("[[Concept:Semantic_Web_Events_2012]]|?Has_location|format=table"
        .equals(fixedAsk));
    String concept = SmwSystem.getConcept(askQuery);
    assertEquals("Semantic Web Events 2012", concept);
    askQuery = "[[Concept:Participant]] [[-Has subobject::SMWEvent2018-02]]";
    concept = SmwSystem.getConcept(askQuery);
    assertEquals("Participant", concept);
  }

  @Test
  public void testBrowseToMap() throws Exception {
    // debug=true;
    SmwSystem smwSystem = getSMWSystem();
    String subject = "Help:List_of_datatypes";
    SimpleNode browseNode = smwSystem.moveTo("browsebysubject=" + subject);
    if (debug)
      browseNode.forAll(SimpleNode.printDebug);
  }

  @Test
  public void testBrowseBySubject() throws Exception {
    // debug = true;
    SmwSystem smwSystem = getSMWSystem();
    String subject = "SMWCon_Fall_2012/Filtered_result_format";
    SimpleNode browseNode = smwSystem.moveTo("browsebysubject=" + subject);
    List<String> properties = new ArrayList<String>();
    browseNode.g().V().has("property").forEachRemaining(prop -> {
      String propName = prop.property("property").value().toString();
      if (!propName.startsWith("_")) {
        properties.add(propName);
        if (debug)
          System.out.println(String.format("* [[Property:%s]]", propName));
      }
    });
    assertEquals(12, properties.size());
    assertTrue(properties.contains("Has_speaker"));
  }

  @Test
  public void testDataTypes() throws Exception {
    // https://www.semantic-mediawiki.org/wiki/Help:JSON_format
    String query = "{{#ask:\n" + " [[Category:Datatypes]]\n"
        + " [[Document status::effective]]\n" + " [[Document language::en]]\n"
        + " |?Has datatype ID=typeid\n" + "|?Has datatype name=Datatype\n"
        + " |?Has description=Description\n" + " |?=Help page\n"
        + " |?Has component=Provided by\n" + " |format=table\n"
        + " |mainlabel=-\n" + " |headers=plain\n" + "}}";
    // debug=true;
    SmwSystem smwSystem = getSMWSystem();
    SimpleNode dtNode = smwSystem.moveTo("ask=" + query);
    FileUtils.writeStringToFile(new File("src/test/datatypes.json"),
        JsonPrettyPrinter.prettyPrint(smwSystem.getJson()), "UTF-8");
    long resultsCount = dtNode.g().V().hasLabel("results").count().next()
        .longValue();
    assertEquals(1, resultsCount);
    long outEdges = dtNode.g().V().hasLabel("results").out().count().next()
        .longValue();
    assertEquals(17, outEdges);
    if (debug)
      dtNode.g().V().hasLabel("results").outE().forEachRemaining(
          edge -> SimpleNode.printDebug.accept(edge.outVertex()));
    smwSystem.conceptAlizePrintRequests("datatype", dtNode);
    assertNotNull(dtNode);
    // this.generateSwitchCases(dtNode);
  }

  /**
   * generate switch Cases
   * 
   * @param dtNode
   */
  /*
   * public void generateSwitchCases(SimpleNode dtNode) { final StringBuffer
   * code = new StringBuffer();
   * dtNode.g().V().hasLabel("datatype").order().by("Datatype")
   * .forEachRemaining(dt -> { Object dataType =
   * dt.property("Datatype").value(); WikiPage wikiPage = (WikiPage)
   * dt.property("wikipage").value(); assertNotNull(wikiPage); String help =
   * "https://www.semantic-mediawiki.org/wiki/Help:Type_" + dataType; help =
   * help.replaceAll(" ", "_"); assertEquals(help, wikiPage.getFullurl());
   * code.append(String.format("// %s\n// %s\n// %s: \n", dataType, help,
   * dt.property("Description").value()));
   * code.append(String.format("case \"%s\": // %s\n",
   * dt.property("typeid").value(), dataType)); code.append("break;\n"); });
   * debug=true; if (debug) System.out.print(code); }
   */

  @Test
  public void testDataTypeAsConcept() throws Exception {
    // see
    // https://www.semantic-mediawiki.org/w/index.php?title=User:WolfgangFahl/Workdocumentation_2018-01-02&action=edit
    String askQuery = "{{#ask: [[Has_annotation_uri::+]]\n"
        + "|?Has_annotation_uri=anu\n" + "|?Has_boolean=boo\n"
        + "|?Has_code=cod\n" + "|?Has_date=dat\n" + "|?Has email address=ema\n"
        + "|?Has Wikidata item ID=eid\n" + "|?Has coordinates=geo\n"
        + "|?Has number=num\n" + "|?Has mlt=mlt\n" + "|?Has example=wpg\n"
        + "|?Telephone number=tel\n" + "|?Has temperatureExample=tem\n"
        + "|?Area=qty\n" + "|?SomeProperty=txt\n" + "|?Soccer result=rec\n"
        + "|?Has_URL=uri\n" + "|format=ol\n" + "}}";
    SmwSystem smwSystem = getSMWSystem();
    // debug = true;
    smwSystem.setDebug(debug);
    SimpleNode dtNode = smwSystem.moveTo("ask=" + askQuery);
    smwSystem.conceptAlizePrintRequests("datatype", dtNode);
    if (debug)
      smwSystem.forAll(SimpleNode.printDebug);
    assertEquals(2,
        smwSystem.g().V().hasLabel("datatype").count().next().longValue());
    if (debug)
      smwSystem.g().V().hasLabel("datatype")
          .forEachRemaining(SimpleNode.printDebug);
    Vertex dtV = smwSystem.g().V().hasLabel("datatype").next();
    assertEquals("datatype", dtV.property("isA").value().toString());
    assertEquals("https://www.semantic-mediawiki.org",
        dtV.property("anu").value().toString());
    assertTrue((Boolean) dtV.property("boo").value());
    assertTrue(dtV.property("cod").value().toString().startsWith("Code"));
    assertTrue(dtV.property("dat").value().toString()
        .startsWith("Fri May 22 17:32:00"));
    if (dtV.property("eid").isPresent())
      assertEquals("Q9682", dtV.property("eid").value().toString());
    assertEquals("mailto:president@whitehouse.gov",
        dtV.property("ema").value().toString());
    assertTrue(dtV.property("geo").value() instanceof Geo);
    assertEquals("  32° 42’ 54.00” N  117°  9’ 45.00” W",
        dtV.property("geo").value().toString());
    assertTrue(dtV.property("wpg").value() instanceof WikiPage);
    assertEquals("Semantic MediaWiki", dtV.property("wpg").value().toString());
    assertEquals("Did you create the page for Tokyo 東京 ? Yes ✓",
        dtV.property("txt").value().toString());
  }

  @Test
  public void testTimeFormat() throws ParseException {
    long timestamps[] = { 1432315920, 1519171200 };
    String timeraws[] = { "1/2015/5/22/17/32/0/0", "1/2018/2/21" };
    String expected[] = { "2015-05-22 17:32:00", "2018-02-21 00:00:00" };
    for (int i = 0; i < timeraws.length; i++) {
      long timestamp = timestamps[i] * 1000;
      Date timeRawDate = SmwSystem.getTime(timeraws[i]);
      Date timeStampDate = new Date(timestamp);
      SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      assertEquals(expected[i], isoDate.format(timeRawDate));
      // assertEquals(timeRawDate,timeStampDate);
      if (debug) {
        System.out.println((timestamp - timeRawDate.getTime()));
        System.out.println(isoDate.format(timeStampDate));
        System.out.println(isoDate.format(timeRawDate));
      }
    }
  }

  @Test
  public void testSimpleGraphModules() throws Exception {
    String askQuery = "{{#ask: [[Concept:SimpleGraphModule]]\n"
        + "|mainlabel=SimpleGraphModule\n"
        + "| ?SimpleGraphModule name = name\n"
        + "| ?SimpleGraphModule logo = logo\n"
        + "| ?SimpleGraphModule modulename = modulename\n"
        + "| ?SimpleGraphModule systemname = systemname\n"
        + "| ?SimpleGraphModule url = url\n"
        + "| ?SimpleGraphModule apiname = apiname\n"
        + "| ?SimpleGraphModule apiurl = apiurl\n"
        + "| ?SimpleGraphModule documentation = documentation\n" + "}}";
    SmwSystem smw = new SmwSystem();
    // debug = true;
    smw.setDebug(debug);
    smw.connect("http://wiki.bitplan.com", "/");
    smw.moveTo("ask=" + askQuery);
    if (debug)
      smw.forAll(SimpleNode.printDebug);
    List<Vertex> moduleVs = smw.getStartNode().g().V()
        .hasLabel("SimpleGraphModule").toList();
    RythmContext rythmContext = RythmContext.getInstance();
    Map<String, Object> rootMap = new HashMap<String, Object>();
    rootMap.put("moduleVs", moduleVs);
    String template = "@import org.apache.tinkerpop.gremlin.structure.Vertex\n"
        + "@import com.bitplan.simplegraph.smw.SmwSystem.WikiPage\n"
        + "@args {\n" + "  List<Vertex> moduleVs\n" + "}\n"
        + "@def WikiPage wikipage(Vertex v, String propname){\n"
        + " return (WikiPage)v.property(propname).value();\n" + "}\n"
        + "@def link(Vertex v, String propname) {\n@{ WikiPage wp=wikipage(v,propname)} URL=\"[[@(wp.fulltext)]]\"}\n"
        + "@def image(Vertex v, String propname){\n@{ WikiPage wp=wikipage(v,propname)} image=\"@(wp.fulltext.replace(\"File:\",\"\"))\"}\n"
        + "@def prop(Vertex v, String propname){\n"
        + " @(v.property(propname).value().toString())\n" + "}\n"
        + "<graphviz>\n" + "digraph hubandspoke @(\"{\")\n"
        + "  edge [dir=\"both\"]\n" + "  layout=\"circo\";\n"
        + "node [shape=circle,\n"
        + "      fixedsize=true, # don't allow nodes to change sizes dynamically\n"
        + "      width=1]\n" + "@for (Vertex mA:moduleVs) {\n"
        + " @(prop(mA,\"name\")) [ @link(mA,\"wikipage\") @image(mA,\"logo\") ]\n"
        + "}\n" + "@for (Vertex mA:moduleVs) {\n"
        + "@for (Vertex mB:moduleVs) {\n" + " @if (mA!=mB) {\n"
        + "@(prop(mA,\"name\")) ->@(prop(mB,\"name\"))\n" + "}\n" + "}\n"
        + "}\n" + "@(\"}\")\n" + "</graphviz>\n";
    String graphVizCode = rythmContext.render(template, rootMap);
    // debug = true;
    if (debug) {
      System.out.println(graphVizCode);

      for (Vertex moduleV : moduleVs) {
        System.out.println(String.format("# [[%s]]",
            ((WikiPage) (moduleV.property("logo").value())).fulltext));
      }
    }
  }

  @Test
  public void testGeoConversion() {
    Geo[] geos = { new Geo(0.0, 0.0), new Geo(30.0, 30.0),
        new Geo(-45.0, -45.0) };
    String[] expected = { "   0°  0’  0.00” N    0°  0’  0.00” E",
        "  30°  0’  0.00” N   30°  0’  0.00” E",
        "  45°  0’  0.00” S   45°  0’  0.00” W" };
    int i = 0;
    for (Geo geo : geos) {
      if (debug)
        System.out.println(geo.toString());
      assertEquals(expected[i++], geo.toString());
    }
  }

}
