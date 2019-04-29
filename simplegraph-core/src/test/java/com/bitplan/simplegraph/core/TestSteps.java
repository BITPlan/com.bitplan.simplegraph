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

import static org.apache.tinkerpop.gremlin.process.traversal.P.gt;
import static org.apache.tinkerpop.gremlin.process.traversal.P.gte;
import static org.apache.tinkerpop.gremlin.process.traversal.P.inside;
import static org.apache.tinkerpop.gremlin.process.traversal.P.lt;
import static org.apache.tinkerpop.gremlin.process.traversal.P.lte;
import static org.apache.tinkerpop.gremlin.process.traversal.P.neq;
import static org.apache.tinkerpop.gremlin.process.traversal.P.outside;
import static org.apache.tinkerpop.gremlin.process.traversal.P.within;
import static org.apache.tinkerpop.gremlin.process.traversal.P.without;
import static org.apache.tinkerpop.gremlin.process.traversal.P.*;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.addE;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.bothE;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.count;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.has;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.hasLabel;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.in;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.inE;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.label;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.out;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.outE;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.values;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.BulkSet;
import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalExplanation;
import org.apache.tinkerpop.gremlin.structure.Edge;
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
  public static boolean debug = false;

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
  public void testHasNext() {
    assertTrue(g().V(1).hasNext());
    assertFalse(g().V(7).hasNext());
  }

  @Test
  public void testNext() {
    assertEquals("v[1]", g().V().next().toString());
    assertEquals("v[1]", g().V(1).next().toString());
    assertEquals("[v[1], v[2]]", g().V(1, 2, 3).next(2).toString());
  }

  @Test
  public void testTryNext() {
    assertTrue(g().V(1).tryNext().isPresent());
    assertFalse(g().V(7).tryNext().isPresent());
  }

  @Test
  public void testToList() {
    List<Vertex> vlist = g().V().toList();
    assertEquals("[v[1], v[2], v[3], v[4], v[5], v[6]]", vlist.toString());
    List<Edge> elist = g().E(7, 8, 9).toList();
    assertEquals("[e[7][1-knows->2], e[8][1-knows->4], e[9][1-created->3]]",
        elist.toString());
  }

  @Test
  public void testToSet() {
    Set<Vertex> vset = g().V(1, 2, 2, 3, 4).toSet();
    assertEquals("[v[1], v[2], v[3], v[4]]", vset.toString());
    Set<Edge> set = g().E(7, 8, 9, 7, 8, 9).toSet();
    assertEquals("[e[7][1-knows->2], e[8][1-knows->4], e[9][1-created->3]]",
        set.toString());
  }

  @Test
  public void testToBulkSet() {
    BulkSet<Vertex> vset = g().V(1, 2, 2, 3, 4).toBulkSet();
    assertEquals(2, vset.asBulk().get(g().V(2).next()).longValue());
  }

  @Test
  public void testFill() {
    List<Vertex> vlist = new LinkedList<Vertex>();
    List<Vertex> rvlist = g().V().fill(vlist);
    assertEquals(vlist, rvlist);
    assertEquals("[v[1], v[2], v[3], v[4], v[5], v[6]]", vlist.toString());
  }

  @Test
  public void testIterate() throws IOException {
    // read and write without iterate doesn't have an effect
    File kryoFile = File.createTempFile("modern", ".kryo");
    g().io(kryoFile.getPath()).write();
    GraphTraversalSource newg = TinkerGraph.open().traversal();
    newg.io(kryoFile.getPath()).read();
    assertEquals(0, newg.V().count().next().longValue());

    // read and write with iterate does really write and read
    g().io(kryoFile.getPath()).write().iterate();
    newg = TinkerGraph.open().traversal();
    newg.io(kryoFile.getPath()).read().iterate();
    assertEquals(6, newg.V().count().next().longValue());
  }

  @Test
  public void testFilter() {
    assertEquals(3, g().V().filter(out()).count().next().longValue());
    assertEquals(4, g().V().filter(in()).count().next().longValue());
    assertEquals(5, g().E().filter(values("weight").is(P.gte(0.4))).count()
        .next().longValue());
  }

  @Test
  public void testPromise() {
    try {
      CompletableFuture<Object> cf = g().V().promise(t -> t.next());
      cf.join();
      assertTrue(cf.isDone());
    } catch (Exception e) {
      assertEquals(
          "Only traversals created using withRemote() can be used in an async way",
          e.getMessage());
    }
  }

  @Test
  public void testExplain() {
    TraversalExplanation te = g().V().explain();
    String explainText = te.prettyPrint();
    if (debug)
      System.out.println(explainText);
    // the explainText is not deterministic - it changes depending
    // whether testExplain is called stand alone or in the context of
    // other unit tests. So we only check that it contains "Original Traversal"
    assertTrue(explainText.contains("Original Traversal"));
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
  public void testOrder() {
    assertEquals("[josh, lop, marko, peter, ripple, vadas]",
        g().V().values("name").order().toList().toString());
    assertEquals("[vadas, ripple, peter, marko, lop, josh]",
        g().V().values("name").order().by(Order.desc).toList().toString());
    assertEquals("[vadas, marko, josh, peter]", g().V().hasLabel("person")
        .order().by("age", Order.asc).values("name").toList().toString());
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
    assertEquals("[v[4], v[3]]", g().E(11).bothV().toList().toString());
    assertEquals("[v[1], v[3]]", g().E(9).bothV().toList().toString());
  }

  @Test
  public void testCoalesce() {
    // System.out.println(g().V().property("nickname", "okram").next());

  }

  @Test
  public void testMax() {
    assertEquals(35, g().V().values("age").max().next());
    assertEquals(1.0, g().E().values("weight").max().next());
    assertEquals("vadas", g().V().values("name").max().next());
  }

  @Test
  public void testMin() {
    assertEquals(27, g().V().values("age").min().next());
    assertEquals(0.2, g().E().values("weight").min().next());
    assertEquals("josh", g().V().values("name").min().next());
  }

  @Test
  public void testMean() {
    assertEquals(30.75, g().V().values("age").mean().next());
    assertEquals(0.583, g().E().values("weight").mean().next().doubleValue(),
        0.001);
    try {
      assertEquals("josh", g().V().values("name").mean().next());
    } catch (Exception e) {
      assertEquals("java.lang.String cannot be cast to java.lang.Number",
          e.getMessage());
    }
  }

  @Test
  public void testSum() {
    assertEquals(123, g().V().values("age").sum().next().intValue());
    assertEquals(3.5, g().E().values("weight").sum().next().doubleValue(),
        0.01);
  }

  @Test
  public void testCap() {
    assertEquals("[{software=2, person=4}]",
        g().V().groupCount("a").by(label()).cap("a").toList().toString());
    assertEquals("[{a={software=2, person=4}, b={0=3, 1=1, 2=1, 3=1}}]",
        g().V().groupCount("a").by(label()).groupCount("b").by(outE().count())
            .cap("a", "b").toList().toString());
  }

  @Test
  public void testCount() {
    assertEquals(6, g().V().count().next().longValue());
    assertEquals(4, g().V().hasLabel("person").count().next().intValue());
    assertEquals(2, g().V().hasLabel("software").count().next().intValue());
    assertEquals(4, g().E().hasLabel("created").count().next().intValue());
    assertEquals(2, g().E().hasLabel("knows").count().next().intValue());
  }

  @Test
  public void testFold() {
    List<Object> knowsList1 = g().V(1).out("knows").values("name").fold()
        .next();
    assertEquals("[vadas, josh]", knowsList1.toString());
  }

  @Test
  public void testAddE() {
    Vertex marko = g().V().has("name", "marko").next();
    Vertex peter = g().V().has("name", "peter").next();
    assertEquals("e[13][1-knows->6]",
        g().V(marko).addE("knows").to(peter).next().toString());
    assertEquals("e[13][1-knows->6]",
        g().addE("knows").from(marko).to(peter).next().toString());
  }

  @Test
  public void testAddV() {
    assertEquals("[marko, vadas, lop, josh, ripple, peter, stephen]",
        g().addV("person").property("name", "stephen").V().values("name")
            .toList().toString());
  }

  @Test
  public void testProperty() {
    assertEquals("[v[3]]", g().V().has("name", "lop").property("version", "1.0")
        .V().has("version").toList().toString());
  }

  @Test
  public void testAggregate() {
    assertEquals("[ripple]",
        g().V(1).out("created").aggregate("x").in("created").out("created")
            .where(without("x")).values("name").toList().toString());
    assertEquals("[{vadas=1, josh=1}]", g().V().out("knows").aggregate("x")
        .by("name").cap("x").toList().toString());
  }

  @Test
  public void testAnd() {
    assertEquals("[marko]", g().V().and(outE("knows"), values("age").is(lt(30)))
        .values("name").toList().toString());
  }

  @Test
  public void testCoin() {
    // 0% chance
    assertEquals("[]", g().V().coin(0.0).toList().toString());
    // 100% chance
    assertEquals("[v[1], v[2], v[3], v[4], v[5], v[6]]",
        g().V().coin(1.0).toList().toString());
    // 50 % chance
    int tosses = 1000;
    double sixsigma = 0.33; // 1 out of a million chance that the average will
                            // deviate more than this
    int sum = 0;
    for (int i = 1; i <= tosses; i++)
      sum += g().V().coin(0.5).toList().size();
    double avg = sum * 1.0 / tosses;
    assertTrue(avg < 3.0 + sixsigma);
    assertTrue(avg > 3.0 - sixsigma);
  }

  @Test
  public void testHas() {
    assertEquals(6, g().V().has("name").count().next().longValue());
    assertEquals("[29, 27]",
        (g().V().has("age", inside(20, 30)).values("age").toList().toString()));
    assertEquals("[32, 35]", (g().V().has("age", outside(20, 30)).values("age")
        .toList().toString()));
    assertEquals("[{name=[marko], age=[29]}, {name=[josh], age=[32]}]", (g().V()
        .has("name", within("josh", "marko")).valueMap().toList().toString()));
    assertEquals("[lop, ripple]",
        g().V().hasNot("age").values("name").toList().toString());
  }

  @Test
  public void testIs() {
    assertEquals("[32]", g().V().values("age").is(32).toList().toString());
    assertEquals("[29, 27]",
        g().V().values("age").is(lte(30)).toList().toString());
    assertEquals("[32, 35]",
        g().V().values("age").is(inside(30, 40)).toList().toString());
    assertEquals("[ripple]", g().V().where(in("created").count().is(1))
        .values("name").toList().toString());
    assertEquals("[lop]", g().V().where(in("created").count().is(gte(2)))
        .values("name").toList().toString());
    assertEquals("[lop, ripple]",
        g().V().where(in("created").values("age").mean().is(inside(30d, 35d)))
            .values("name").toList().toString());
  }

  @Test
  public void testOr() {
    assertEquals("[marko, lop, josh, peter]",
        g().V().or(outE("created"), inE("created").count().is(gt(1)))
            .values("name").toList().toString());
    assertEquals("[vadas, peter]",
        g().V().or(values("age").is(gt(33)), values("age").is(lt(29)))
            .values("name").toList().toString());
  }

  @Test
  public void testWhere() {
    assertEquals("[v[4], v[6]]", g().V(1).as("a").out("created").in("created")
        .where(neq("a")).toList().toString());
    String names[] = { "josh", "peter" };
    assertEquals("[josh, peter]",
        g().withSideEffect("a", Arrays.asList(names)).V(1).out("created")
            .in("created").values("name").where(within("a")).toList()
            .toString());
    assertEquals("[josh]",
        g().V(1).out("created").in("created")
            .where(out("created").count().is(gt(1))).values("name").toList()
            .toString());
  }

  @Test
  public void testAs() {
    assertEquals(
        "[{a=v[1], b=v[3]}, {a=v[4], b=v[5]}, {a=v[4], b=v[3]}, {a=v[6], b=v[3]}]",
        g().V().as("a").out("created").as("b").select("a", "b").toList()
            .toString());
    assertEquals(
        "[{a=marko, b=lop}, {a=josh, b=ripple}, {a=josh, b=lop}, {a=peter, b=lop}]",
        g().V().as("a").out("created").as("b").select("a", "b").by("name")
            .toList().toString());
  }

  @Test
  public void testBy() {
    assertEquals("[{1=[v[2], v[5], v[6]], 3=[v[1], v[3], v[4]]}]",
        g().V().group().by(bothE().count()).toList().toString());
    assertEquals("[{1=[vadas, ripple, peter], 3=[marko, lop, josh]}]",
        g().V().group().by(bothE().count()).by("name").toList().toString());
    assertEquals("[{1=3, 3=3}]",
        g().V().group().by(bothE().count()).by(count()).toList().toString());
  }

  @Test
  public void testOption() {

  }

  @Test
  public void testChoose() {
    assertEquals("[marko, ripple, lop, lop]",
        g().V().hasLabel("person")
            .choose(values("age").is(lte(30)), in(), out()).values("name")
            .toList().toString());
    assertEquals("[marko, ripple, lop]",
        g().V().hasLabel("person").choose(values("age")).option(27, in())
            .option(32, out()).values("name").toList().toString());
  }

  @Test
  public void testRepeat() {
    assertEquals("[path[marko, josh, ripple], path[marko, josh, lop]]",
        g().V(1).repeat(out()).times(2).path().by("name").toList().toString());

    assertEquals(
        "[path[marko, josh, ripple], path[josh, ripple], path[ripple]]",
        g().V().until(has("name", "ripple")).repeat(out()).path().by("name")
            .toList().toString());
  }

  @Test
  public void testEmit() {
    assertEquals(
        "[path[marko, lop], path[marko, vadas], path[marko, josh], path[marko, josh, ripple], path[marko, josh, lop]]",
        g().V(1).repeat(out()).times(2).emit().path().by("name").toList()
            .toString());
    assertEquals(
        "[path[marko], path[marko, lop], path[marko, vadas], path[marko, josh], path[marko, josh, ripple], path[marko, josh, lop]]",
        g().V(1).emit().repeat(out()).times(2).path().by("name").toList()
            .toString());
    assertEquals(
        "[path[marko, lop], path[marko, josh, ripple], path[marko, josh, lop]]",
        g().V(1).repeat(out()).times(2).emit(has("lang")).path().by("name")
            .toList().toString());
    assertEquals(
        "[path[marko, lop], path[marko, vadas], path[marko, josh], path[marko, josh, ripple], path[marko, josh, lop]]",
        g().V(1).repeat(out()).times(2).emit().path().by("name").toList()
            .toString());
  }

  @Test
  public void testUnion() {
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
