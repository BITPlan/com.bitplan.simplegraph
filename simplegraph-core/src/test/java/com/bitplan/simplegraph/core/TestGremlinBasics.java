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

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.V;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.inV;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.outV;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.values;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Column;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.junit.Test;

/**
 * see http://www.bitplan.com/index.php/Gremlin_Basics
 */
public class TestGremlinBasics {

  public static boolean debug = false;

  @Test
  /**
   * https://stackoverflow.com/questions/51015636/in-gremlin-how-does-map-really-work
   */
  public void testMap() {
    GraphTraversalSource g = TinkerFactory.createModern().traversal();
    if (debug) {
      System.out.println("out()");
      g.V().out().forEachRemaining(v -> SimpleNode.printDebug.accept(v));
      System.out.println("map(out())");
      g.V().map(__.out())
          .forEachRemaining(v -> SimpleNode.printDebug.accept(v));
    }
  }

  /**
   * show the given map entries
   * 
   * @param map
   */
  public void showMap(String title, Map<Object, Object> map) {
    System.out.println(title + ":" + map.values().size());
    for (Entry<Object, Object> entry : map.entrySet()) {
      System.out
          .println(String.format("\t%s=%s", entry.getKey(), entry.getValue()));
    }
  }

  public void showObject(String title, Object object) {
    System.out.println(title + ":" + object.toString());
  }

  @Test
  /**
   * https://groups.google.com/forum/#!topic/gremlin-users/UZMD1qp5mfg
   * https://stackoverflow.com/questions/55771036/it-keyword-in-gremlin-java
   */
  public void testGroupBy() {
    // gremlin:
    // g.V.outE.groupBy{it.inV.next().name}{it.weight}{it.sum().doubleValue()}.cap.orderMap(T.decr)
    GraphTraversalSource g = TinkerFactory.createModern().traversal();
    // debug = true;
    if (debug) {
      g.V().outE().group().by(inV().values("name")).by(values("weight").sum())
          .order(Scope.local).by(Column.values, Order.desc)
          .forEachRemaining(r -> showObject("sum", r));
      g.E().group("weights").by(outV().values("name")).by(values("weight").sum()).cap("weights")
          .order(Scope.local).by(Column.values, Order.desc)
          .forEachRemaining(r -> showObject("sum", r));
      g.E().group("weights").by(V().values("name")).by(values("weight").sum()).cap("weights")
      .order(Scope.local).by(Column.values, Order.desc)
      .forEachRemaining(r -> showObject("sum", r));
    }
  }

  @Test
  public void testGroupByDebug() {
    GraphTraversalSource g = TinkerFactory.createModern().traversal();
    // debug = true;
    if (debug) {
      g.V().outE().group().by(inV().values("name")).by(values("weight").sum())
          .order(Scope.local).by(Column.values, Order.desc)
          .forEachRemaining(r -> showObject("sum", r));

      g.V().outE().group().by().forEachRemaining(m -> showMap("by()", m));
      g.V().outE().group().by(inV().id())
          .forEachRemaining(m -> showMap("by(inV().id())", m));
      g.V().outE().group("edges").by(inV().id()).cap("edges")
          .forEachRemaining(o -> showObject("cap", o));
      // https://stackoverflow.com/a/45112157/1497139
      g.V().has("name", "marko").out("knows").groupCount("a").by("name")
          .group("b").by("name").by(values("age").sum()).cap("a", "b")
          .forEachRemaining(v -> showObject("sum", v));
    }
  }

}
