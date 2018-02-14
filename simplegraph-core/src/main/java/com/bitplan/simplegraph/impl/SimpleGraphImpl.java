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
package com.bitplan.simplegraph.impl;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleNode;

/**
 * default implementation of SimpleGraph interface - wraps a TinkerPop 3 graph
 * @author wf
 *
 */
public class SimpleGraphImpl implements SimpleGraph {
  protected transient boolean debug = false;

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }
  // The TinkerPop 3 graph
  Graph graph;
  
  SimpleNode startNode;
  
  public SimpleGraphImpl() {
    this(null); 
  }
  
  /**
   * construct me from the given simple Graph
   * @param simpleGraph
   */
  public SimpleGraphImpl(SimpleGraph simpleGraph) {
    if (simpleGraph==null)
      graph = TinkerGraph.open(); 
    else
      graph=simpleGraph.graph();
  }
  
  @Override
  public SimpleNode getStartNode() {
    return startNode;
  }

  @Override
  public SimpleNode setStartNode(SimpleNode startNode) {
    this.startNode = startNode;
    return startNode;
  }
  
  @Override
  public Graph graph() {
    return graph;
  }
  
  @Override
  public Vertex addVertex(SimpleNode other) {
    if (other instanceof SimpleNodeImpl) {
      SimpleNodeImpl sother = (SimpleNodeImpl) other;
      Vertex vertex=graph.addVertex(sother.keyValueList.toArray());
      return vertex;
    }
    throw new IllegalArgumentException("addVertex needs a SimpleNodeImpl argument but got "+other.getClass().getName());
  }

}
