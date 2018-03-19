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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * non abstract version of SimpleNodeImpl
 * @author wf
 *
 */
public class MapNode extends SimpleNodeImpl {
 
  /**
   * initialize me from a map
   * @param keys 
   */
  public MapNode(SimpleGraph graph,String kind,Map<String, Object> map, String ... keys) {
    super(graph,kind,keys);
    this.map=map;
    super.setVertexFromMap(map);
  }
  
  @Override
  public Map<String, Object> initMap() {
    return map;
  }

  @Override
  public Stream<SimpleNode> out(String edgeName) {
    return inOrOut(this.g().V(this.getVertex().id()).in(edgeName));
  }

  @Override
  public Stream<SimpleNode> in(String edgeName) {
    return inOrOut(this.g().V(this.getVertex().id()).out(edgeName));
  }
  
  /**
   * get my vertices
   * @param graphTraversal
   * @return
   */
  protected Stream<SimpleNode> inOrOut(GraphTraversal<Vertex, Vertex> graphTraversal) {
    List<SimpleNode> links = new ArrayList<SimpleNode>();
    graphTraversal.forEachRemaining(vertex->{
      Object simpleNodeObject = vertex.property("mysimplenode").value();
      if (simpleNodeObject instanceof SimpleNode)
        links.add((SimpleNode) simpleNodeObject);
    });
    return links.stream();
  }

}
