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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.bitplan.simplegraph.SimpleGraph;
import com.bitplan.simplegraph.SimpleNode;

/**
 * default implementation of a SimpleNode wraps a Tinkerpop/Gremlin Vertex
 * @author wf
 *
 */
public abstract class SimpleNodeImpl extends SimpleGraphImpl implements SimpleNode {
  
  Vertex vertex;
  SimpleGraph simpleGraph;
  String kind;
  protected Map<String, Object> map = new HashMap<String, Object>();
  
  /**
   * initialize me for the given graph
   * @param simpleGraph
   * @param kind 
   */
  public SimpleNodeImpl(SimpleGraph simpleGraph, String kind) {
    super(simpleGraph);
    this.simpleGraph=simpleGraph;
    this.kind=kind;
  }

  /**
   * get the Vertex
   */
  public Vertex getVertex() {
    return vertex;
  }

  @Override
  public Vertex setVertexFromMap() {
    initMap();
    List<Object> keyValueList=new ArrayList<Object>();
    // first add a key value pair for my kind
    addKeyValue(keyValueList,T.label,this.kind);
    // now add the pointer to me
    // TODO - filter / ignore me when saving
    addKeyValue(keyValueList,"mysimplenode",this);
    // add all key values from my map
    for (Entry<String, Object> entry:map.entrySet()) {
      if (entry.getValue()!=null)
        addKeyValue(keyValueList,entry.getKey(),entry.getValue());
    }
    // create a vertex with the given data for later traversal
    vertex=this.graph.addVertex(keyValueList.toArray());
    return vertex;
  }
  
  /**
   * add a pair of values
   * @param keyValueList
   * @param name
   * @param value
   */
  private void addKeyValue(List<Object> keyValueList,Object name,Object value) {
    keyValueList.add(name);
    keyValueList.add(value);
  }

  @Override
  public Map<String, Object> getMap() {
    return map;
  }
  
}
