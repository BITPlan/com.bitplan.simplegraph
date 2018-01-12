package com.bitplan.simplegraph;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Test;

/**
 * get TinkerPop3 API
 * @author wf
 *
 */
public class TestTinkerPop3 {
  boolean debug=true;
  
  @Test
  // http://kelvinlawrence.net/book/Gremlin-Graph-Guide.html#beyond
  public void testAirRoutes() throws IOException {
    Graph graph = TinkerGraph.open();
    graph.io(IoCore.graphml()).readGraph("src/test/air-routes.graphml");
    if (debug)
      System.out.println(graph.toString());
    GraphTraversalSource g = graph.traversal();
    assertEquals(3619, g.V().count().next().longValue());
    Map<String, ?> aus = g.V().has("code", "AUS").valueMap().next();
    aus.forEach((k, v) -> System.out.println(String.format("%s = %s", k, v)));
    Long n = g.V().has("code","DFW").out().count().next();
    if (debug)
      System.out.println("There are " + n + " routes from Dallas");
    assertEquals(221,n.longValue());

    List<Object> fromAus = (g.V().has("code","AUS").out().values("code").order().toList());
    if (debug)
      System.out.println(fromAus);

    
    List<Path> lhrToUsa = g.V().has("code", "LHR").outE().inV()
        .has("country", "US").limit(5).path().by("code").by("dist").toList();

    lhrToUsa.forEach((k) -> System.out.println(k));
    
    ArrayList <Path> routes = new ArrayList<>();
    g.V().has("code","SAT").out().path().by("icao").fill(routes);
    if (debug)
      System.out.println(routes);

  }
  
  @Test
  public void testTinkerpop() {
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
    marko.addEdge("knows", josh, T.id, 8, "weight", 1.0f);
    marko.addEdge("created", lop, T.id, 9, "weight", 0.4f);
    josh.addEdge("created", ripple, T.id, 10, "weight", 1.0f);
    josh.addEdge("created", lop, T.id, 11, "weight", 0.4f);
    peter.addEdge("created", lop, T.id, 12, "weight", 0.2f);
    GraphTraversal<Vertex, Path> p = graph.traversal().V(marko).out("knows")
        .values("name").path();
    System.out.println(p.count());
  }

}
