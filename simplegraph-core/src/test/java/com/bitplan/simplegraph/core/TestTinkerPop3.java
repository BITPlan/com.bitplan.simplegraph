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
package com.bitplan.simplegraph.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Test;

import com.bitplan.rythm.RythmContext;

/**
 * get TinkerPop3 API
 * 
 * @author wf
 *
 */
public class TestTinkerPop3 {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.simplegraph.core");

  /**
   * get the AirRoutes example graph
   * 
   * @return
   * @throws Exception
   */
  public static Graph getAirRoutes() throws Exception {
    Graph graph = TinkerGraph.open();
    graph.io(IoCore.graphml())
        .readGraph("../simplegraph-core/src/test/air-routes.graphml");
    if (debug)
      LOGGER.log(Level.INFO, graph.toString());
    return graph;
  }

  @Test
  // http://kelvinlawrence.net/book/Gremlin-Graph-Guide.html#beyond
  public void testAirRoutes() throws Exception {
    Graph graph = getAirRoutes();
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
  public void testSchema() {
    Graph graph = TinkerFactory.createModern();
    if (debug) {
      graph.traversal().V().next().properties()
          .forEachRemaining(prop -> System.out.println(String.format("%s=%s",
              prop.label(), prop.value().getClass().getSimpleName())));
      graph.traversal().V().next().edges(Direction.OUT)
          .forEachRemaining(edge -> System.out.println(
              String.format("%s->%s", edge.label(), edge.outVertex().label())));
    }
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
    assertEquals(1, groups.size());
    // debug = true;
    List<Map<Object, Object>> groupsByName = graph.traversal().V().group()
        .by(T.label).by("name").toStream().collect(Collectors.toList());
    if (debug)
      groupsByName.forEach(SimpleNode.printMapDebug);
  }

  @Test
  /**
   * test according to https://github.com/krlawrence/graph
   * http://kelvinlawrence.net/book/Gremlin-Graph-Guide.html
   * http://kelvinlawrence.net/book/Gremlin-Graph-Guide.pdf
   */
  public void testGuide() throws Exception {
    Graph graph = getAirRoutes();
    GraphTraversalSource g = graph.traversal();
    // 3.1.1. A quick look at Gremlin and SQL
    // g.V().hasLabel('airport').groupCount().by('country')
    Map<Object, Long> groupCount = g.V().hasLabel("airport").groupCount()
        .by("country").next();
    if (debug)
      groupCount.entrySet().forEach(entry -> System.out
          .println(String.format("%s=%s", entry.getKey(), entry.getValue())));
    // uncomment if you"d like to see all available ids
    // g.V().forEachRemaining(v->System.out.println(v.id()));
    // Query the properties of vertex 3
    GraphTraversal<Vertex, Object> unfolded1 = g.V("3").valueMap(true).unfold();
    // debug = true;
    if (debug) {
      unfolded1.forEachRemaining(o -> System.out.println(o.toString()));
      /**
       * country=[US] code=[AUS] longest=[12250] city=[Austin] id=3
       * lon=[-97.6698989868164] type=[airport] label=airport elev=[542]
       * icao=[KAUS] region=[US-TX] runways=[2] lat=[30.1944999694824]
       * desc=[Austin Bergstrom International Airport]
       */
    }
    assertEquals(14,
        g.V("3").valueMap(true).unfold().count().next().longValue());
    GraphTraversal<Vertex, Vertex> v3 = g.V(3);
    GraphTraversal<Vertex, Map<Object, Object>> vmap = v3.valueMap(true);
    GraphTraversal<Vertex, Object> unfolded = g.V(3).valueMap(true).unfold();
    if (debug)
      unfolded.forEachRemaining(o -> System.out.println(
          String.format("%s (%s)", o.toString(), o.getClass().getName())));
    if (debug)
      g.V().hasLabel("airport").values("code")
          .forEachRemaining(o -> System.out.println(o.toString()));

    // g.V().has('code','AUS').out().out().out().has('code','AGR').path().by('code')
    GraphTraversal<Vertex, Path> paths = g.V().has("code", "AUS").out().out()
        .out().has("code", "AGR").path().by("code");
    assertEquals(4, paths.count().next().longValue());
    paths = g.V().has("code", "AUS").out().out().out().has("code", "AGR").path()
        .by("code");
    if (debug)
      paths.forEachRemaining(path -> {
        System.out.println(path.toString());
      });
    // g.V().has('code','AUS').repeat(out()).times(3).has('code','AGR').path().by('code')
    
    if (debug)
      g.V().has("code", "AUS").repeat(__.out()).times(3).has("code", "AGR")
          .path().by("code").forEachRemaining(path -> {
            System.out.println(path.toString());
          });
    //fail("tschüss");
    // Find vertices that are airports
    // g.V().hasLabel('airport')
    assertEquals(3374, g.V().hasLabel("airport").count().next().longValue());
    // Find the DFW vertex
    // g.V().has('code','DFW')
    assertEquals("Dallas",
        g.V().has("code", "DFW").next().property("city").value());
    // Combining those two previous queries (two ways that are equivalent)
    // g.V().hasLabel('airport').has('code','DFW')
    assertEquals("8", g.V().hasLabel("airport").has("code", "DFW").next().id());
    // g.V().has('airport','code','DFW')
    assertEquals("8", g.V().has("airport", "code", "DFW").next().id());
    // g.V().has('airport','code','DFW').next().getClass()
    assertEquals("TinkerVertex",
        g.V().has("airport", "code", "DFW").next().getClass().getSimpleName());
    // 3.2.1. Retrieving property values from a vertex
    // What property values are stored in the DFW vertex?
    // g.V().has('airport','code','DFW').values()
    List<Object> values = g.V().has("airport", "code", "DFW").values().toList();
    assertEquals(12, values.size());
    // debug=true;
    if (debug)
      values.forEach(value -> System.out.println(value));
    // Return just the city name property
    // fail("tschüss");    
    // g.V().has('airport','code','DFW').values('city')
    assertEquals("Dallas", g.V().has("code", "DFW").values("city").next());
    // Return the 'runways' and 'icao' property values.
    // g.V().has('airport','code','DFW').values('runways','icao')
    GraphTraversal<Vertex, Object> rit = g.V().has("code", "DFW")
        .values("runways", "icao");
    assertEquals("KDFW", rit.next());
    assertEquals(7, rit.next());
    // Does a specific property exist on a given vertex or edge?

    // Find all edges that have a 'dist' property
    // g.E().has('dist')
    g.E().has("dist").forEachRemaining(e -> {
      if (debug) {
        System.out.println(String.format("%s -> %s %3d miles",
            e.inVertex().values("city").next(),
            e.outVertex().values("city").next(), e.property("dist").value()));
      }
    });

    // Find all vertices that have a 'region' property
    // g.V().has('region')
    assertEquals(3374, g.V().has("region").count().next().longValue());

    // Find all the vertices that do not have a 'region' property
    // g.V().hasNot('region')
    assertEquals(245, g.V().hasNot("region").count().next().longValue());

    // The above is shorthand for
    // g.V().not(has('region'))
    assertEquals(245, g.V().not(__.has("region")).count().next().longValue());
    // 3.2.3. Counting things
    // How many airports are there in the graph?
    // g.V().hasLabel('airport').count()
    assertEquals(3374, g.V().hasLabel("airport").count().next().longValue());
    // How many routes are there?
    // g.V().hasLabel('airport').outE('route').count()
    assertEquals(43400,
        g.V().hasLabel("airport").outE("route").count().next().longValue());
    // How many routes are there?
    // g.V().outE('route').count()
    assertEquals(43400, g.V().outE("route").count().next().longValue());
    // // How many routes are there?
    // g.E().hasLabel('route').count()
    assertEquals(43400, g.E().hasLabel("route").count().next().longValue());
  }

  @Test
  public void testCountingGroups() throws Exception {
    // 3.2.4. Counting groups of things
    Graph graph = getAirRoutes();
    GraphTraversalSource g = graph.traversal();
    // How many of each type of vertex are there?
    // g.V().groupCount().by(label)
    Map<Object, Long> labelTypeCount = g.V().groupCount().by(T.label).next();
    if (debug)
      labelTypeCount.entrySet().forEach(entry -> System.out
          .println(String.format("%s=%4d", entry.getKey(), entry.getValue())));
    Long countries = labelTypeCount.get("country");
    assertEquals(237, countries.longValue());
    // How many of each type of vertex are there?
    // g.V().label().groupCount()
    // [continent:7,country:237,version:1,airport:3374]

    Map<Object, Long> labelTypeCount2 = g.V().label().groupCount().next();
    // check we have the same result
    labelTypeCount2.entrySet().forEach(entry -> assertEquals(entry.getValue(),
        labelTypeCount.get(entry.getKey())));
    // How many of each type of edge are there?
    // g.E().groupCount().by(label)
    g.E().groupCount().by(T.label);
    
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testUML() throws Exception {
    Graph graph = getAirRoutes();
    GraphTraversalSource g = graph.traversal();
    for (String className : g.V().label().dedup().toList()) {
      List<VertexProperty> vprops = new ArrayList<VertexProperty>();
      g.V().hasLabel(className).next().properties()
          .forEachRemaining(prop -> vprops.add(prop));
      for (VertexProperty vprop : vprops) {
        if (debug)
          System.out.println(String.format("%s.%s", className, vprop.label()));
      }
    }
    for (String edgeName : g.E().label().dedup().toList()) {
      Edge edge = g.E().hasLabel(edgeName).next();
      if (debug)
        System.out.println(String.format("%s -> %s: %s",
            edge.outVertex().label(), edge.inVertex().label(), edge.label()));
    }
    ;
    // .properties().forEachRemaining(prop->vprops.add(prop));
    File plantUMLTemplate = new File("../simplegraph-core/src/main/rythm/plantuml.rythm");
    Map<String, Object> rootMap = new HashMap<String, Object>();
    rootMap.put("g", g);
    rootMap.put("title", "AirRoutes");
    String uml = RythmContext.getInstance().render(plantUMLTemplate, rootMap);
    // debug=true;
    if (debug)
      System.out.println(uml);

  }

  @Test
  public void testLabels() throws Exception {
    Graph graph = getAirRoutes();
    GraphTraversalSource g = graph.traversal();
    long vertexLabelCount = g.V().label().dedup().count().next().longValue();
    assertEquals(4, vertexLabelCount);
    long edgeLabelCount = g.E().label().dedup().count().next().longValue();
    assertEquals(2, edgeLabelCount);
  }

}
