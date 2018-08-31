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

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.junit.Test;

import com.bitplan.rythm.GraphRythmContext;

/**
 * test GraphViz and plant UML representation
 * @author wf
 *
 */
public class TestGraphViz {

  public static boolean debug=true;
  @Test
  public void testTinkerFactoryGraphs() throws Exception {
    Graph graphs[]= {TinkerFactory.createModern(), TinkerFactory.createClassic(),TinkerFactory.createTheCrew()};
    String title[]= {"Modern","Classic","The Crew"};
    int i=0;
    for (Graph graph:graphs) {
      String uml=GraphRythmContext.getInstance().renderUml(graph.traversal(), title[i++]);
      if (debug)
        System.out.println(uml);
    }
  }

}
