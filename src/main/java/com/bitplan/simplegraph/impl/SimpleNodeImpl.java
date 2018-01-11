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

import java.util.stream.Stream;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.bitplan.simplegraph.SimpleGraph;
import com.bitplan.simplegraph.SimpleNode;

/**
 * default implementation of a SimpleNode wraps a Tinkerpop/Gremlin Vertex
 * @author wf
 *
 */
public class SimpleNodeImpl extends SimpleGraphImpl implements SimpleNode {
  
  Vertex vertex;
  SimpleGraph simpleGraph;
  
  /**
   * initialize me for the given graph
   * @param simpleGraph
   */
  public SimpleNodeImpl(SimpleGraph simpleGraph) {
    super(simpleGraph);
    this.simpleGraph=simpleGraph;
  }

  public Vertex getVertex() {
    return vertex;
  }

  public void setVertex(Vertex vertex) {
    this.vertex = vertex;
  }

  @Override
  public SimpleNode asSimpleNode() {
    return this;
  }
  
  @Override
  public Stream<SimpleNode> out(String edgeName) {
    throw new PseudoAbstractCallException(edgeName);
  };
  
  @Override
  public Stream<SimpleNode> in(String edgeName) {
    throw new PseudoAbstractCallException(edgeName);
  };
  


}
