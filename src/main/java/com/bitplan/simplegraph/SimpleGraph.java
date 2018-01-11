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

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;

/**
 * The Simple Graph interface is inspired by the mathematical concept of a
 * directed graph: https://en.wikipedia.org/wiki/Graph_(discrete_mathematics)
 * it wraps an Apache Tinkerpop / Gremlin Graph
 *
 * @author wf
 *
 */
public interface SimpleGraph {
  // this is the only function you need to implement
  public SimpleGraph asSimpleGraph();

  // access my wrapped graph
  public Graph graph();
    
  // these are the interfaces with default implementations
  // convenience method to access to Gremlin wrapped objects,
  public default GraphTraversalSource g() {
    return this.graph().traversal();
  }

  // default implementations for SimpleGraph API
  public default SimpleNode getStartNode() {
    return this.sg().getStartNode();
  };

  public default SimpleNode setStartNode(SimpleNode startNode) {
    return this.setStartNode(startNode);
  };

  public default SimpleGraph sg() {
    return this.asSimpleGraph();
  };

}
