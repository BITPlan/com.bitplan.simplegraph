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
import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * The Simple Graph interface is inspired by the mathematical concept of a
 * graph: https://en.wikipedia.org/wiki/Graph_(discrete_mathematics)
 * 
 * it wraps an Apache Tinkerpop / Gremlin Graph
 * and allows generic graph traversals
 * 
 * a graph can also be used in cases where you'd normally use lists, tables and trees - the graph
 * is the more general concept
 *
 * @author wf
 *
 */
public interface SimpleGraph {

  // access my wrapped graph
  public Graph graph();
    
  // these are the interfaces with default implementations
  // convenience method to access to Gremlin wrapped objects,
  public default GraphTraversalSource g() {
    return this.graph().traversal();
  }
  
  public Vertex addVertex(SimpleNode other); 

  // SimpleGraph API
  /**
   * get the "start" node of this graph
   * @return the start node
   */
  public SimpleNode getStartNode();
 
  /**
   * set the "start" node of this graph
   * @param startNode
   * @return
   */
  public SimpleNode setStartNode(SimpleNode startNode);

}
