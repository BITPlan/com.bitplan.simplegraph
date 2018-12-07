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
package com.bitplan.simplegraph.bundle;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Test;

import com.bitplan.gremlin.RegexPredicate;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.excel.ExcelSystem;
import com.bitplan.simplegraph.html.HtmlSystem;
import com.bitplan.simplegraph.mediawiki.MediaWikiPageNode;
import com.bitplan.simplegraph.mediawiki.MediaWikiSystem;
import com.bitplan.simplegraph.wikidata.WikiDataSystem;

/**
 * test case that combines: HTMLSystem, WikiDataSystem, MediaWikiSystem and
 * ExcelSystem
 * 
 * @author wf
 *
 */
public class TestThermalBathInTuscany {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.simplegraph.bundle");
  final String WIKIPEDIA_DE = "https://de.wikipedia.org";
  final String WIKIPEDIA_DE_BASE = WIKIPEDIA_DE + "/wiki/";
  final String THERMALBATH_LIST = "Liste_der_Thermalbäder_in_der_Toskana";

  /**
   * get the thermal baths in tuscany from wikipedia and add a node for each
   * 
   * @param graph
   * @throws Exception
   */
  public void getThermalBathsInTuscanyHTML(Graph graph) throws Exception {
    // the list of thermal bath

    String url = WIKIPEDIA_DE_BASE + THERMALBATH_LIST;
    // get a graph based html parser to visit the url
    HtmlSystem hs = HtmlSystem.forUrl(url);
    // check for all links on the page
    GraphTraversal<Vertex, Vertex> linkNodes = hs.g().V().hasLabel("a");
    linkNodes.forEachRemaining(linkNode -> {
      if (linkNode.property("href").isPresent()
          && linkNode.property("title").isPresent()) {
        // SimpleNode.printDebug.accept(linkNode);
        String link = linkNode.property("href").value().toString();
        String title = linkNode.property("title").value().toString();
        if (link.startsWith("/wiki/") && !link.contains("Hilfe")
            && !link.contains("Thermalbad") && !link.equals("/wiki/Toskana")
            && !link.contains("Liste") && !link.contains("Kategorie")
            && !link.contains("Spezial:") && !link.contains("Wikipedia")) {
          // create a node for each bath
          Vertex v = graph.addVertex("wikipedia");
          v.property("title", title);
          v.property("link", WIKIPEDIA_DE_BASE + link);
        }
      }
    });
  }

  /**
   * get the thermal bath in Tuscany via Mediawiki API
   * 
   * @param graph
   * @throws Exception
   */
  private void getThermalBathsInTuscany(Graph graph) throws Exception {
    MediaWikiSystem mws = new MediaWikiSystem();
    MediaWikiPageNode pageNode = (MediaWikiPageNode) mws
        .connect(WIKIPEDIA_DE, "/w").moveTo(THERMALBATH_LIST);
    String pageContent = pageNode.getVertex().property("pagecontent").value()
        .toString();
    if (debug)
      System.out.println(pageContent);
    // The list is between {{TOC}} and {{TOC}}
    Pattern p = Pattern.compile("\\{\\{TOC\\}\\}(.*)\\{\\{TOC\\}\\}",
        Pattern.DOTALL);
    Matcher m = p.matcher(pageContent);
    if (m.find()) {
      String linksMarkup = m.group(1);
      // look for * [[link]] or * [[link|title]] markup
      Pattern plink = Pattern.compile("\\*\\s\\[\\[(.*?)(\\|(.*))?\\]\\]");
      Matcher mlink = plink.matcher(linksMarkup);
      while (mlink.find()) {
        String link = mlink.group(1);
        String title = mlink.group(3);
        if (title == null)
          title = link;
        if (debug)
          System.out.println(
              String.format("%2d %s:%s", mlink.groupCount(), link, title));
        Vertex v = graph.addVertex("wikipedia");
        v.property("title", title);
        v.property("link", WIKIPEDIA_DE_BASE + link);
      }
    }

  }

  /**
   * get the wikiData Items for all "wikipedia" nodes in the given graph and add
   * the property "wikidata" with the entity ID
   * 
   * @param g
   */
  public void getWikiDataItems(Graph graph) {
    graph.traversal().V().hasLabel("wikipedia").forEachRemaining(w -> {
      String url = w.property("link").value().toString();
      try {
        HtmlSystem hs = HtmlSystem.forUrl(url);
        hs.g().V().hasLabel("a")
            .has("href", RegexPredicate.regex(
                "^https://www.wikidata.org/wiki/Special:EntityPage/Q[0-9]+$"))
            .forEachRemaining(wd -> {
              String href = wd.property("href").value().toString();
              String[] parts = href.split("/");
              String entityId = parts[parts.length - 1];
              w.property("wikidata", entityId);
            });
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, e.getMessage(), e);
      }
    });
  }

  /**
   * get the wiki data information
   * 
   * @param graph
   * @throws Exception
   */
  public void getWikiDataInfo(Graph graph) throws Exception {
    WikiDataSystem wikiDataSystem = new WikiDataSystem();
    wikiDataSystem.connect();
    graph.traversal().V().has("wikidata").forEachRemaining(qnode -> {
      String q = qnode.property("wikidata").value().toString();
      SimpleNode wdNode = wikiDataSystem.moveTo(q);
      if (debug)
        SimpleNode.printDebug.accept(wdNode.getVertex());
      qnode.property("coordinates",
          wdNode.getVertex().property("P625").value());
      qnode.property("image", wdNode.getVertex().property("P18").value());

    });
  }

  @Test
  public void testWikiList() throws Exception {
    Graph graph = TinkerGraph.open();

    getThermalBathsInTuscany(graph);
    getWikiDataItems(graph);
    getWikiDataInfo(graph);

    GraphTraversalSource g = graph.traversal();
    // debug = true;
    if (debug)
      g.V().forEachRemaining(SimpleNode.printDebug);

    ExcelSystem es = new ExcelSystem();
    Workbook wb = es.createWorkBook(g);
    assertEquals(1, wb.getNumberOfSheets());
    File tmpFile = File.createTempFile("tuscany2019", ".xlsx");
    es.save(wb, tmpFile.getAbsolutePath());
    if (debug)
      LOGGER.log(Level.INFO, "saved excel to " + tmpFile.getAbsolutePath());

  }

}
