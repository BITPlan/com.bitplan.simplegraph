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
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Test;

/**
 * get TinkerPop3 API
 * 
 * @author wf
 *
 */
public class TestTinkerPop3 extends BaseTest {

  @Test
  // http://kelvinlawrence.net/book/Gremlin-Graph-Guide.html#beyond
  public void testAirRoutes() throws IOException {
    Graph graph = TinkerGraph.open();
    graph.io(IoCore.graphml()).readGraph("src/test/air-routes.graphml");
    if (debug)
      LOGGER.log(Level.INFO, graph.toString());
    GraphTraversalSource g = graph.traversal();
    assertEquals(3619, g.V().count().next().longValue());
    Map<String, ?> aus = g.V().has("code", "AUS").valueMap().next();
    aus.forEach((k, v) -> {
      if (debug)
        LOGGER.log(Level.INFO, String.format("%s = %s", k, v));
    });
    Long n = g.V().has("code", "DFW").out().count().next();
    if (debug)
      LOGGER.log(Level.INFO, "There are " + n + " routes from Dallas");
    assertEquals(221, n.longValue());

    List<Object> fromAus = (g.V().has("code", "AUS").out().values("code")
        .order().toList());
    if (debug)
      LOGGER.log(Level.INFO, fromAus.toString());

    List<Path> lhrToUsa = g.V().has("code", "LHR").outE().inV()
        .has("country", "US").limit(5).path().by("code").by("dist").toList();

    lhrToUsa.forEach((k) -> {
      if (debug)
        LOGGER.log(Level.INFO, k.toString());
    });

    ArrayList<Path> routes = new ArrayList<>();
    g.V().has("code", "SAT").out().path().by("icao").fill(routes);
    if (debug)
      LOGGER.log(Level.INFO, routes.toString());
  }

  public Graph getFirstGraph() {
    // http://tinkerpop.apache.org/docs/3.1.8/tutorials/getting-started/
    Graph graph = TinkerGraph.open(); // 1
    Vertex marko = graph.addVertex(T.label, "person", T.id, 1, "name", "marko",
        "age", 29); // 2
    Vertex vadas = graph.addVertex(T.label, "person", T.id, 2, "name", "vadas",
        "age", 27);
    Vertex lop = graph.addVertex(T.label, "software", T.id, 3, "name", "lop",
        "lang", "java");
    Vertex josh = graph.addVertex(T.label, "person", T.id, 4, "name", "josh",
        "age", 32);
    Vertex ripple = graph.addVertex(T.label, "software", T.id, 5, "name",
        "ripple", "lang", "java");
    Vertex peter = graph.addVertex(T.label, "person", T.id, 6, "name", "peter",
        "age", 35);
    Edge e = marko.addEdge("knows", vadas, T.id, 7, "weight", 0.5f); // 3
    assertNotNull(e);
    marko.addEdge("knows", josh, T.id, 8, "weight", 1.0f);
    marko.addEdge("created", lop, T.id, 9, "weight", 0.4f);
    josh.addEdge("created", ripple, T.id, 10, "weight", 1.0f);
    josh.addEdge("created", lop, T.id, 11, "weight", 0.4f);
    peter.addEdge("created", lop, T.id, 12, "weight", 0.2f);
    return graph;
  }

  /**
   * dump the given graph
   * 
   * @param graph
   */
  public void dumpGraph(Graph graph) {
    if (debug) {
      graph.traversal().V().forEachRemaining(
          vertex -> System.out.println(String.format("%s=%s %s", vertex.id(),
              vertex.label(), vertex.property("name").value())));
      graph.traversal().E().forEachRemaining(
          edge -> System.out.println(String.format("%s- %s >%s",
              edge.inVertex().id(), edge.label(), edge.outVertex().id())));
    }
  }

  @Test
  public void testTinkerpop() {
    Graph graph = getFirstGraph();
    // 1 is the id of the marko node
    GraphTraversal<Vertex, Path> p = graph.traversal().V(1).out("knows")
        .values("name").path();
    long pcount = p.count().next().longValue();
    assertEquals(2, pcount);
    // debug = true;
    dumpGraph(graph);
  }

  @Test
  public void testTutorial() {
    // see http://tinkerpop.apache.org/docs/3.3.1/tutorials/getting-started/
    Graph graph = TinkerFactory.createModern();
    assertEquals(28.0,
        graph.traversal().V().has("name", P.within("vadas", "marko"))
            .values("age").mean().next().doubleValue(),
        0.001);
    List<Object> creators = graph.traversal().V().has("name", "marko")
        .out("created").in("created").values("name").toStream()
        .collect(Collectors.toList());
    assertEquals(3, creators.size());
    String creatorNames = String.join(",",
        creators.toArray(new String[creators.size()]));
    assertEquals("marko,josh,peter", creatorNames);

    List<Object> creatorsWithOutMarko = graph.traversal().V()
        .has("name", "marko").as("exclude").out("created").in("created")
        .where(P.neq("exclude")).values("name").toStream()
        .collect(Collectors.toList());
    assertEquals(2, creatorsWithOutMarko.size());
    String creatorNamesWithOutMarko = String.join(",",
        creatorsWithOutMarko.toArray(new String[creatorsWithOutMarko.size()]));
    assertEquals("josh,peter", creatorNamesWithOutMarko);

    List<Map<String, Object>> selected = graph.traversal().V().as("a").out()
        .as("b").out().as("c").select("a", "b", "c").toStream()
        .collect(Collectors.toList());
    assertEquals(2, selected.size());
    if (debug)
      selected.forEach(SimpleNode.printMapDebug);
    List<Map<Object, Object>> groups = graph.traversal().V().group().by(T.label)
        .toStream().collect(Collectors.toList());
    if (debug)
      groups.forEach(SimpleNode.printMapDebug);
    assertEquals(1,groups.size());
    debug = true;
    List<Map<Object, Object>> groupsByName = graph.traversal().V().group().by(T.label).by("name").
    toStream().collect(Collectors.toList());
    if (debug)
      groupsByName.forEach(SimpleNode.printMapDebug);
  }

}
