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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import com.bitplan.rythm.RythmContext;

/**
 * test the rythm template engine
 * 
 * @author wf
 *
 */
public class TestRythm extends BaseTest {

  // for the documentation we have to translate local file path to github path
  // just add the following PREFIX do so
  public static final String GITHUB_URL_PREFIX = "https://github.com/BITPlan/com.bitplan.simplegraph/blob/master/";

  @Test
  public void testRythmFromFile() throws Exception {
    RythmContext rythmContext = RythmContext.getInstance();
    File templateDir = new File("src/main/rythm");
    rythmContext.setTemplateRoot(templateDir.getAbsolutePath());
    // set it again - the engine should not be reconfigured
    rythmContext.setTemplateRoot(templateDir.getAbsolutePath());
    File template = new File(templateDir, "test.rythm");
    Map<String, Object> rootMap = new HashMap<String, Object>();
    rootMap.put("title", "testTitle");
    String result = rythmContext.render(template, rootMap);
    assertEquals(result.trim(), "testTitle");
  }

  @Test
  public void testRythmFromString() throws Exception {
    // debug=true;
    RythmContext rythmContext = RythmContext.getInstance();
    String template = "@// Rythem template\n"
        + "@// you can try me out at http://fiddle.rythmengine.com\n"
        + "@// Created by Wolfgang Fahl, BITPlan GmbH,  2018-01-12\n"
        + "@args() {\n" + "  String title,int times;\n" + "}\n"
        + "@for (int i=0;i<times;i++) {\n" + "  @title @i\n" + "}";
    if (debug) {
      // if there is something wrong with the template try it out in the Rythm
      // fiddle
      LOGGER.log(Level.INFO, template);
    }
    Map<String, Object> rootMap = new HashMap<String, Object>();
    rootMap.put("title", "step");
    rootMap.put("times", 5);
    String result = rythmContext.render(template, rootMap);
    if (debug)
      LOGGER.log(Level.INFO, result);
    assertTrue(result.contains("step 4"));
  }

  /**
   * generate a GraphViz graph for the given parameters
   * @param start
   * @param edge
   * @param property
   * @param idProperty
   * @param urlPrefix
   * @param rankDir
   * @param graphname
   * @return
   * @throws Exception
   */
  public static String generateGraphViz(SimpleNode start, String edge,
      String property, String idProperty, String urlPrefix, String rankDir,String graphname) throws Exception {
    // prepare the Map of information to be supplied for the Rythm template
    Map<String, Object> rootMap = new HashMap<String, Object>();
    // the SimpleNode to start with
    rootMap.put("start", start);
    // the edges to select
    rootMap.put("edge", edge);
    // the property to show (for edges/vertices)
    rootMap.put("property", property);
    // the property to derive the URL from
    rootMap.put("idProperty", idProperty);
    // the prefix to prepend to the idProperty to get the final url
    rootMap.put("urlPrefix", urlPrefix);
    // style of graph e.g. TB, BT, RL, LR (Top-Bottom, Bottom-Top, Right-Left,
    // Left-Right see. graphviz rankdir
    rootMap.put("rankdir", rankDir);
    // the name of the graph
    rootMap.put("graphname",
        graphname);
    // get us a Rythm context to be able to render via a template
    RythmContext rythmContext = RythmContext.getInstance();
    // choose a Rythm template that will work on our graph
    File template = new File("src/main/rythm/graphvizTree.rythm");
    // let Rythm do the rendering according to the template
    String graphViz = rythmContext.render(template, rootMap);
    return graphViz;
  }

  @Test
  public void testGenerateGraphVizViaRythm() throws Exception {
    debug=true;
    SimpleNode start = getFileNode("src", Integer.MAX_VALUE);
    if (debug)
      start.forAll(SimpleNode.printDebug);
    String graphViz = generateGraphViz(start, "parent", "name", "path",
        GITHUB_URL_PREFIX,"RL","FileSystemGraphForSrcDirectoryOfSimpleGraphGitHubOpenSourceProject");
    // debug = true;
    assertTrue(graphViz.contains("TestRythm.java"));
    if (debug)
      System.out.println(graphViz.trim());
  }

  @Test
  public void testGenerateGraphVizManually() throws Exception {
    // debug = true;
    SimpleNode start = getFileNode("src", Integer.MAX_VALUE);
    // get the gremlin starting point
    GraphTraversalSource g = start.g();
    // traverse using Apache Gremlin
    // first print all vertices
    g.V().forEachRemaining(vertex -> {
      // lambda
      Vertex v = vertex; // just to force needed import to make it available for
                         // cut&paste to rythm template
      String label = v.property("name").value().toString();
      String path = v.property("path").value().toString();
      String url = GITHUB_URL_PREFIX + path;
      if (debug) {
        // do not use LOGGER since we might want to copy&paste the result
        System.out.println(String.format("\"%s\" [ label=\"%s\" URL=\"%s\" ]",
            path, label, url));
      }
    });
    // then print all edges
    g.E().hasLabel("parent").forEachRemaining(edge -> {
      // lambda
      String in = (String) edge.inVertex().property("path").value();
      String out = (String) edge.outVertex().property("path").value();
      String label = edge.label();
      if (debug)
        // do not use LOGGER since we might want to copy&paste the result
        System.out.println(
            String.format("\"%s\"->\"%s\" [label=\"%s\"]", out, in, label));
    });
  }

}
