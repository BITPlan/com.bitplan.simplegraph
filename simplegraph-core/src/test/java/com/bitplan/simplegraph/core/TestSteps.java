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
import java.util.Map;
import java.util.Map.Entry;

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
 * 
 * @author wf
 *
 */
public class TestSteps {

  /**
   * common access to GraphTraversalSource
   * 
   * @return - the graph traversal
   */
  public GraphTraversalSource g() {
    Graph graph = TinkerFactory.createModern();
    GraphTraversalSource g = graph.traversal();
    return g;
  }

  @Test
  public void testTraversal() {
    assertEquals(6, g().E().count().next().longValue());
    assertEquals(6, g().V().count().next().longValue());
  }

  @Test
  public void testFilter() {
    assertEquals(3, g().V().filter(out()).count().next().longValue());
    assertEquals(4, g().V().filter(in()).count().next().longValue());
    assertEquals(5, g().E().filter(values("weight").is(P.gte(0.4))).count()
        .next().longValue());
  }

  @Test
  public void testMap() {
    assertEquals(6, g().V().map(values("name")).count().next().longValue());
    assertEquals(4, g().V().map(hasLabel("person")).count().next().longValue());
    assertEquals(2,
        g().V().map(has("lang", "java")).count().next().longValue());
    List<Edge> outEdges = g().V().map(outE()).toList();
    assertEquals(3, outEdges.size());
    List<Object> edges = g().E().map(has("weight", 0.4)).toList();
    assertEquals(2, edges.size());
    for (Object edge : edges) {
      assertTrue(edge instanceof Edge);
    }

  }

  @Test
  public void testflatMap() {
    assertEquals(6, g().V().flatMap(values("name")).count().next().longValue());
    assertEquals(4,
        g().V().flatMap(hasLabel("person")).count().next().longValue());
    assertEquals(2,
        g().V().flatMap(has("lang", "java")).count().next().longValue());
    List<Edge> outEdges = g().V().flatMap(outE()).toList();
    assertEquals(6, outEdges.size());
    List<Object> edges = g().E().flatMap(has("weight", 0.4)).toList();
    assertEquals(2, edges.size());
    for (Object edge : edges) {
      assertTrue(edge instanceof Edge);
    }
  }

  @Test
  public void testSideEffect() {
    assertEquals(6, g().V().sideEffect(addE("sideedge")).outE()
        .hasLabel("sideedge").count().next().longValue());
    assertEquals(0, g().E().hasLabel("sideedge").count().next().longValue());
  }

  @Test
  public void testBranch() {
    GraphTraversal<Vertex, Object> branch = g().V()
        .branch(inE("created").has("weight", 0.4));

  }

  @Test
  public void testId() {
    List<Object> vids = g().V().id().toList();
    assertEquals(6, vids.size());
    assertEquals("[1, 2, 3, 4, 5, 6]", vids.toString());
    List<Object> eids = g().E().id().toList();
    assertEquals(6, eids.size());
    assertEquals("[7, 8, 9, 10, 11, 12]", eids.toString());
  }

  @Test
  public void testLabel() {
    List<String> vlabels = g().V().label().toList();
    assertEquals(6, vlabels.size());
    assertEquals("[person, person, software, person, software, person]",
        vlabels.toString());
    List<String> elabels = g().E().label().toList();
    assertEquals(6, elabels.size());
    assertEquals("[knows, knows, created, created, created, created]",
        elabels.toString());
  }

  @Test
  public void testMatch() {
    GraphTraversal<Vertex, Map<String, Object>> match = g().V()
        .match(__.as("v"));

  }

  @Test
  public void testIn() {
    assertEquals("[v[1], v[1], v[4], v[6], v[1], v[4]]",
        g().V().in().toList().toString());
    assertEquals("[v[1], v[4], v[6], v[4]]",
        g().V().in("created").toList().toString());
    assertEquals("[v[1], v[1]]", g().V().in("knows").toList().toString());
    assertEquals("[v[1], v[1], v[4], v[6], v[1], v[4]]",
        g().V().in("created", "knows").toList().toString());
  }

  @Test
  public void testOut() {
    assertEquals("[v[3], v[2], v[4], v[5], v[3], v[3]]",
        g().V().out().toList().toString());
    assertEquals("[v[3], v[5], v[3], v[3]]",
        g().V().out("created").toList().toString());
    assertEquals("[v[2], v[4]]", g().V().out("knows").toList().toString());
    assertEquals("[v[3], v[2], v[4], v[5], v[3], v[3]]",
        g().V().out("created", "knows").toList().toString());
  }

  @Test
  public void testBoth() {
    assertEquals("[v[5], v[3], v[1]]", g().V(4).both().toList().toString());
    assertEquals("[v[5], v[3]]", g().V(4).both("created").toList().toString());
    assertEquals("[v[1]]", g().V(4).both("knows").toList().toString());
    assertEquals("[v[5], v[3], v[1]]",
        g().V(4).both("created", "knows").toList().toString());
  }

  @Test
  public void testInE() {
    assertEquals(
        "[e[7][1-knows->2], e[9][1-created->3], e[11][4-created->3], e[12][6-created->3], e[8][1-knows->4], e[10][4-created->5]]",
        g().V().inE().toList().toString());
    assertEquals(
        "[e[9][1-created->3], e[11][4-created->3], e[12][6-created->3], e[10][4-created->5]]",
        g().V().inE("created").toList().toString());
    assertEquals("[e[7][1-knows->2], e[8][1-knows->4]]",
        g().V().inE("knows").toList().toString());
    assertEquals(
        "[e[7][1-knows->2], e[9][1-created->3], e[11][4-created->3], e[12][6-created->3], e[8][1-knows->4], e[10][4-created->5]]",
        g().V().inE("created", "knows").toList().toString());
  }

  @Test
  public void testOutE() {
    assertEquals("[e[9][1-created->3], e[7][1-knows->2], e[8][1-knows->4]]",
        g().V(1).outE().toList().toString());
    assertEquals("[e[9][1-created->3]]",
        g().V(1).outE("created").toList().toString());
    assertEquals("[e[7][1-knows->2], e[8][1-knows->4]]",
        g().V(1).outE("knows").toList().toString());
    assertEquals("[e[9][1-created->3], e[7][1-knows->2], e[8][1-knows->4]]",
        g().V(1).outE("created", "knows").toList().toString());
  }

  @Test
  public void testBothE() {
    assertEquals("[e[10][4-created->5], e[11][4-created->3], e[8][1-knows->4]]",
        g().V(4).bothE().toList().toString());
    assertEquals("[e[10][4-created->5], e[11][4-created->3]]",
        g().V(4).bothE("created").toList().toString());
    assertEquals("[e[8][1-knows->4]]",
        g().V(4).bothE("knows").toList().toString());
    assertEquals("[e[10][4-created->5], e[11][4-created->3], e[8][1-knows->4]]",
        g().V(4).bothE("created", "knows").toList().toString());
  }

  @Test
  public void testInV() {
    assertEquals("[v[2], v[4], v[3], v[5], v[3], v[3]]",
        g().E().inV().toList().toString());
    assertEquals("[v[3]]", g().E(9).inV().toList().toString());
  }
  
  @Test
  public void testOutV() {
    assertEquals("[v[1], v[1], v[1], v[4], v[4], v[6]]",
        g().E().outV().toList().toString());
    assertEquals("[v[1]]", g().E(9).outV().toList().toString());
  }
  
  @Test
  public void testBothV() {
    assertEquals("[v[4], v[3]]",
        g().E(11).bothV().toList().toString());
    assertEquals("[v[1], v[3]]", g().E(9).bothV().toList().toString());
  }

  /**
   * show the given map entries
   * 
   * @param map
   */
  public void showMap(String title, Map<?, Object> map) {
    System.out.println(title + ":" + map.values().size());
    for (Entry<?, Object> entry : map.entrySet()) {
      System.out
          .println(String.format("\t%s=%s", entry.getKey(), entry.getValue()));
    }
  }
}
