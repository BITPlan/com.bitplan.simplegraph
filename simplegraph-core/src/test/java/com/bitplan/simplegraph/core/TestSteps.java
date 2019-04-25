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

import static org.junit.Assert.*;

import java.util.List;

import org.apache.tinkerpop.gremlin.structure.Column;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Test;

/**
 * test the gremlin steps
 * @author wf
 *
 */
public class TestSteps {

  public GraphTraversalSource g() {
    Graph graph = TinkerFactory.createModern();
    GraphTraversalSource g = graph.traversal();
    return g;
  }
  
  @Test
  public void testTraversal() {
    Graph graph = TinkerFactory.createModern();
    GraphTraversalSource g = graph.traversal();
    assertEquals(6,g.E().count().next().longValue());
    assertEquals(6,g.V().count().next().longValue());
  }
  
  @Test
  public void testFilter() {
    assertEquals(3,g().V().filter(out()).count().next().longValue());
    assertEquals(4,g().V().filter(in()).count().next().longValue());
    assertEquals(5,g().E().filter(values("weight").is(P.gte(0.4))).count().next().longValue());
  }
  
  @Test
  public void testMap() {
    assertEquals(6,g().V().map(values("name")).count().next().longValue());
    assertEquals(4,g().V().map(hasLabel("person")).count().next().longValue());
    assertEquals(2,g().V().map(has("lang","java")).count().next().longValue());
    List<Edge> outEdges = g().V().map(outE()).toList();
    assertEquals(3,outEdges.size());
    List<Object> edges = g().E().map(has("weight",0.4)).toList();
    assertEquals(2,edges.size());
    for (Object edge:edges) {
      assertTrue(edge instanceof Edge);
    }
   
  }
  
  @Test
  public void testflatMap() {
    assertEquals(6,g().V().flatMap(values("name")).count().next().longValue());
    assertEquals(4,g().V().flatMap(hasLabel("person")).count().next().longValue());
    assertEquals(2,g().V().flatMap(has("lang","java")).count().next().longValue());
    List<Edge> outEdges = g().V().flatMap(outE()).toList();
    assertEquals(6,outEdges.size());
    List<Object> edges = g().E().flatMap(has("weight",0.4)).toList();
    assertEquals(2,edges.size());
    for (Object edge:edges) {
      assertTrue(edge instanceof Edge);
    }
  }
  
  @Test
  public void testSideEffect() {
    assertEquals(6,g().V().sideEffect(addE("sideedge")).outE().hasLabel("sideedge").count().next().longValue());
    assertEquals(0,g().E().hasLabel("sideedge").count().next().longValue());
  }

  @Test
  public void testBranch() {
    GraphTraversal<Vertex, Object> branch = g().V().branch(inE("created").has("weight",0.4));

  }

}
