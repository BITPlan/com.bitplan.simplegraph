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
package com.bitplan.simplegraph.map;

import java.util.HashMap;
import java.util.Map;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

/**
 * simple system with works based on nodes that are key/value maps
 * @author wf
 *
 */
public class MapSystem extends SimpleSystemImpl {

  @Override
  public SimpleSystem connect(String... connectionParams) throws Exception {
    return this;
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    throw new IllegalStateException("moveTo with nodeQuery not implemented");
  }

  @Override
  public Class<? extends SimpleNode> getNodeClass() {
    return MapNode.class;
  }
  
  /**
   * initialize a mapNode with the given key/value pairs
   * @param keyValues
   * @return a mapNode
   */
  public MapNode initMap(String kind,Object ...keyValues) {
    if (keyValues.length%2 !=0)
      throw new IllegalArgumentException("keyValues should come in pairs but odd "+keyValues.length+" supplied");
    Map<String,Object> map=new HashMap<String,Object>();
    for (int i=0;i<keyValues.length;i+=2) {
      map.put(keyValues[i].toString(), keyValues[i+1]);
    }
    MapNode mapNode=new MapNode(this,kind,map);
    super.optionalStartNode(mapNode);
    return mapNode;
  }

  /**
   * create a new MapNode based on an existing vertex
   * @param vertex
   * @param keys 
   * @return the mapNode
   */
  public MapNode moveTo(Vertex vertex, String ... keys) {
    String kind=vertex.label();
    MapNode mapNode=new MapNode(this,kind,SimpleNodeImpl.vertexToMap(vertex,keys),keys);
    super.optionalStartNode(mapNode);
    return mapNode;
  }

}
