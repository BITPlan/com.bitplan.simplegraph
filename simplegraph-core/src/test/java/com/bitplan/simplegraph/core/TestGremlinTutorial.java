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

import java.util.logging.Logger;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.junit.Test;

/**
 * Testcases derived from
 * http://tinkerpop.apache.org/docs/3.3.0/tutorials/getting-started/
 * 
 * @author wf
 *
 */
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
  public void testTheFirstFiveMinutes() {

    /*-
     
     gremlin> graph = TinkerFactory.createModern()
     ==>tinkergraph[vertices:6 edges:6]
     gremlin> g = graph.traversal()
     ==>graphtraversalsource[tinkergraph[vertices:6 edges:6], standard]
     
     */

    Graph graph = TinkerFactory.createModern();
    GraphTraversalSource g = graph.traversal();
    check(g.toString(),
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

    check(g.V("1").out("knows").has("age",P.gt(30)).values("name").toList().toString(),
        "[josh]");
  }

}
