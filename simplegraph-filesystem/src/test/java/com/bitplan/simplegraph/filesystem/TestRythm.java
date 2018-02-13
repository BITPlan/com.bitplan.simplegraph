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
package com.bitplan.simplegraph.filesystem;

import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import com.bitplan.rythm.RythmContext;
import com.bitplan.simplegraph.SimpleNode;

/**
 * test the rythm template engine
 * 
 * @author wf
 *
 */
public class TestRythm  {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.simplegraph");
  
  // for the documentation we have to translate local file path to github path
  // just add the following PREFIX do so
  public static final String GITHUB_URL_PREFIX = "https://github.com/BITPlan/com.bitplan.simplegraph/blob/master/simplegraph-filesystem/";

  @Test
  public void testGenerateGraphVizViaRythm() throws Exception {
    // debug=true;
    SimpleNode start = TestFileSystem.getFileNode("../simplegraph-filesystem/src", Integer.MAX_VALUE);
    if (debug)
      start.forAll(SimpleNode.printDebug);
    String graphViz = RythmContext.getInstance().renderGraphViz(start, "parent", "name", "path",
        GITHUB_URL_PREFIX,"RL","FileSystemGraphForSrcDirectoryOfSimpleGraphGitHubOpenSourceProject");
    // debug = true;
    assertTrue(graphViz.contains("TestRythm.java"));
    if (debug)
      System.out.println(graphViz.trim());
  }

  @Test
  public void testGenerateGraphVizManually() throws Exception {
    // debug = true;
    SimpleNode start = TestFileSystem.getFileNode("src", Integer.MAX_VALUE);
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
