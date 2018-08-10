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

import static org.apache.tinkerpop.gremlin.process.traversal.P.*;
import static org.apache.tinkerpop.gremlin.structure.T.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.logging.Logger;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Testcases derived from
 * http://tinkerpop.apache.org/docs/3.3.0/tutorials/getting-started/
 * 
 * @author wf
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestGremlinTutorial {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.simplegraph.core");

  /**
   * check the given expected object to be equal to the objectToCheck
   * 
   * @param objectToCheck
   * @param expected
   */
  private void check(Object objectToCheck, Object expected) {
    if (debug) {
      System.out.println(objectToCheck);
    }
    assertEquals(expected, objectToCheck);
  }

  /**
   * The First Five Minutes
   * 
   * translated from Gremlin console to Java code
   */
  @Test
  public void test1_TheFirstFiveMinutes() {

    /*-
     
     gremlin> graph = TinkerFactory.createModern()
     ==>tinkergraph[vertices:6 edges:6]
    */

    Graph graph = TinkerFactory.createModern();
    check(graph.toString(), "tinkergraph[vertices:6 edges:6]");

    /*
     * gremlin> g = graph.traversal()
     * ==>graphtraversalsource[tinkergraph[vertices:6 edges:6], standard]
     * 
     */

    GraphTraversalSource g = graph.traversal();
    String gstring = g.toString();
    check(gstring,
        "graphtraversalsource[tinkergraph[vertices:6 edges:6], standard]");

    /*-
     
    gremlin> g.V() //(1)
    ==>v[1]
    ==>v[2]
    ==>v[3]
    ==>v[4]
    ==>v[5]
    ==>v[6]
    
     */

    check(g.V().toList().toString(), "[v[1], v[2], v[3], v[4], v[5], v[6]]");

    /*-
     
     gremlin> g.V(1) //(2)
    ==>v[1]
    
     */

    check(g.V("1").toList().toString(), "[v[1]]");

    /*-
    gremlin> g.V(1).values('name') //3
    ==>marko
    */

    check(g.V("1").values("name").toList().toString(), "[marko]");

    /*-
    
     gremlin> g.V(1).outE('knows') //4
    ==>e[7][1-knows->2]
    ==>e[8][1-knows->4]
    
     */

    check(g.V("1").outE("knows").toList().toString(),
        "[e[7][1-knows->2], e[8][1-knows->4]]");

    /*-
    
    gremlin> g.V(1).outE('knows').inV().values('name') //5\
    ==>vadas
    ==>josh
    
     */

    check(g.V("1").outE("knows").inV().values("name").toList().toString(),
        "[vadas, josh]");

    /*-
     
     gremlin> g.V(1).out('knows').values('name') //6\
    ==>vadas
    ==>josh
     
     */

    check(g.V("1").out("knows").values("name").toList().toString(),
        "[vadas, josh]");

    /*-
     
     gremlin> g.V(1).out('knows').has('age', gt(30)).values('name') //7
    ==>josh
      
     */

    check(g.V("1").out("knows").has("age", gt(30)).values("name").toList()
        .toString(), "[josh]");
  }

  /**
   * The Next Fifteen Minutes
   * 
   * translated from Gremlin console to Java code
   */
  @Test
  public void test2_CreatingAGraph() {

    /*-
     gremlin> graph = TinkerGraph.open()
    ==>tinkergraph[vertices:0 edges:0]
    
    */

    TinkerGraph graph = TinkerGraph.open();
    check(graph.toString(), "tinkergraph[vertices:0 edges:0]");

    /*-
     
     gremlin> g = graph.traversal()
    ==>graphtraversalsource[tinkergraph[vertices:0 edges:0], standard]
    
     */

    GraphTraversalSource g = graph.traversal();

    check(g.toString(),
        "graphtraversalsource[tinkergraph[vertices:0 edges:0], standard]");

    /*-
     
     gremlin> v1 = g.addV("person").property(id, 1).property("name", "marko").property("age", 29).next()
    ==>v[1]
       
     */
    Vertex v1 = g.addV("person").property(id, 1).property("name", "marko")
        .property("age", 29).next();
    check(v1.toString(), "v[1]");

    /*-
      
    gremlin> v2 = g.addV("software").property(id, 3).property("name", "lop").property("lang", "java").next()
    ==>v[3] 
     
     */

    Vertex v2 = g.addV("software").property(id, 3).property("name", "lop")
        .property("lang", "java").next();
    check(v2.toString(), "v[3]");

    /*-
     
     gremlin> g.addE("created").from(v1).to(v2).property(id, 9).property("weight", 0.4)
    ==>e[9][1-created->3]
     */

    check(
        g.addE("created").from(v1).to(v2).property(id, 9)
            .property("weight", 0.4).toList().toString(),
        "[e[9][1-created->3]]");

    /*-
     
    gremlin> graph = TinkerGraph.open()
        ==>tinkergraph[vertices:0 edges:0]
        gremlin> g = graph.traversal()
        ==>graphtraversalsource[tinkergraph[vertices:0 edges:0], standard]
        gremlin> v1 = g.addV("person").property(T.id, 1).property("name", "marko").property("age", 29).next()
        ==>v[1]
        gremlin> v2 = g.addV("software").property(T.id, 3).property("name", "lop").property("lang", "java").next()
        ==>v[3]
        gremlin> g.addE("created").from(v1).to(v2).property(T.id, 9).property("weight", 0.4)
        ==>e[9][1-created->3]
    */

    graph = TinkerGraph.open();
    g = graph.traversal();
    v1 = g.addV("person").property(T.id, 1).property("name", "marko")
        .property("age", 29).next();
    v2 = g.addV("software").property(T.id, 3).property("name", "lop")
        .property("lang", "java").next();
    Edge e = g.addE("created").from(v1).to(v2).property(id, 9)
        .property("weight", 0.4).next();
    assertEquals(e.toString(), "e[9][1-created->3]");

  }

  /**
   * Graph Traversal - Staying Simple
   */
  @Test
  public void test3_GraphTraversalStayingSimple() {
    Graph graph = TinkerFactory.createModern();
    GraphTraversalSource g = graph.traversal();

    /*-
     
     gremlin> g.V().has('name','marko')
    ==>v[1]
    
     */

    check(g.V().has("name", "marko").toList().toString(), "[v[1]]");

    /*-
    
     gremlin> g.V().has('name','marko').outE('created')
    ==>e[9][1-created->3]
     
     */

    check(g.V().has("name", "marko").outE("created").toList().toString(),
        "[e[9][1-created->3]]");

    /*-
     
     gremlin> g.V().has('name','marko').outE('created').inV()
     
     */

    check(g.V().has("name", "marko").outE("created").inV().toList().toString(),
        "[v[3]]");

    /*-
     
     gremlin> g.V().has('name','marko').out('created')
     
     */

    check(g.V().has("name", "marko").out("created").toList().toString(),
        "[v[3]]");

    /*-
     
     gremlin> g.V().has('name','marko').out('created').values('name')
    ==>lop
     
     */

    check(g.V().has("name", "marko").out("created").values("name").toList()
        .toString(), "[lop]");

  }

  /**
   * Graph Traversal- Increasing Complexity
   */
  @Test
  public void test4_IncreasingComplexity() {
    /*-
     
    gremlin> graph = TinkerFactory.createModern()
    ==>tinkergraph[vertices:6 edges:6]
    
    gremlin> g = graph.traversal()
    ==>graphtraversalsource[tinkergraph[vertices:6 edges:6], standard]
    
    */

    Graph graph = TinkerFactory.createModern();
    check(graph.toString(), "tinkergraph[vertices:6 edges:6]");
    GraphTraversalSource g = graph.traversal();
    check(g.toString(),
        "graphtraversalsource[tinkergraph[vertices:6 edges:6], standard]");

    /*-
     
    gremlin> g.V().has('name',within('vadas','marko')).values('age')
    ==>29
    ==>27
    
     */

    check(g.V().has("name", within("vadas", "marko")).values("age").toList()
        .toString(), "[29, 27]");

    /*-
     
    gremlin> g.V().has('name',within('vadas','marko')).values('age').mean()
    ==>28.0
    
     */

    check(g.V().has("name", within("vadas", "marko")).values("age").mean()
        .next().doubleValue(), 28.0);

    /*-
     
    gremlin> g.V().has('name','marko').out('created')
    ==>v[3]
    
     */

    check(g.V().has("name", "marko").out("created").toList().toString(),
        "[v[3]]");

    /*-
     
     gremlin> g.V().has('name','marko').
           out('created').in('created').
           values('name')
           
    ==>marko
    ==>josh
    ==>peter
    
     */

    // debug = true;
    check(g.V().has("name", "marko").out("created").in("created").values("name")
        .toList().toString(), "[marko, josh, peter]");

  }

  @Test
  public void testDuplicateId() {
    TinkerGraph g1 = TinkerFactory.createClassic();
    TinkerGraph g2 = null;
    try {
      g2 = createMyClassicWithDuplicateIds();
      fail("there should be an exception!");
    } catch (IllegalArgumentException iae) {

    }
    assertNotNull(g1);
    assertNull(g2);
  }

  @Test
  public void testAddEdge() {
    TinkerGraph g1 = TinkerFactory.createClassic();
    Vertex josh = g1.traversal().V("4").next();
    Vertex ripple = g1.traversal().V("5").next();
    josh.addEdge("knows", ripple, T.id, 13, "weight", 1.0f);
    assertEquals("tinkergraph[vertices:6 edges:7]", g1.toString());
  }

  public static TinkerGraph createMyClassicWithDuplicateIds() {
    final Configuration conf = new BaseConfiguration();
    conf.setProperty(TinkerGraph.GREMLIN_TINKERGRAPH_VERTEX_ID_MANAGER,
        TinkerGraph.DefaultIdManager.INTEGER.name());
    conf.setProperty(TinkerGraph.GREMLIN_TINKERGRAPH_EDGE_ID_MANAGER,
        TinkerGraph.DefaultIdManager.INTEGER.name());
    conf.setProperty(TinkerGraph.GREMLIN_TINKERGRAPH_VERTEX_PROPERTY_ID_MANAGER,
        TinkerGraph.DefaultIdManager.INTEGER.name());
    final TinkerGraph g = TinkerGraph.open(conf);
    final Vertex marko = g.addVertex(T.id, 1, "name", "marko", "age", 29);
    final Vertex vadas = g.addVertex(T.id, 1, "name", "vadas", "age", 27);
    final Vertex lop = g.addVertex(T.id, 3, "name", "lop", "lang", "java");
    final Vertex josh = g.addVertex(T.id, 4, "name", "josh", "age", 32);
    final Vertex ripple = g.addVertex(T.id, 5, "name", "ripple", "lang",
        "java");
    final Vertex peter = g.addVertex(T.id, 6, "name", "peter", "age", 35);
    marko.addEdge("knows", vadas, T.id, 7, "weight", 0.5f);
    marko.addEdge("knows", josh, T.id, 8, "weight", 1.0f);
    marko.addEdge("created", lop, T.id, 9, "weight", 0.4f);
    josh.addEdge("created", ripple, T.id, 10, "weight", 1.0f);
    josh.addEdge("created", lop, T.id, 11, "weight", 0.4f);
    peter.addEdge("created", lop, T.id, 12, "weight", 0.2f);
    return g;
  }
}
