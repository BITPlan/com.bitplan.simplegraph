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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * The SimpleNode interface represents a Vertex in a graph
 * https://en.wikipedia.org/wiki/Vertex_(graph_theory)
 * it wraps an Apache TinkerPop vertex
 * @author wf
 *
 */
public interface SimpleNode extends SimpleGraph {
  public enum EdgeDirection{IN,OUT,BOTH};
  
  // this is the only operation that needs to be implemented for a class wanting
  // to behave like a SimpleNode
  public SimpleNode asSimpleNode();

  // interface to Tinkertop/Gremlin wrapped Vertex
  public default Vertex getVertex() {
    return this.asSimpleNode().getVertex();
  };
  
  // interfaces with default implementation
  public default Map<String, Object> getMap() {
    return this.asSimpleNode().getMap();
  };

  public default Stream<SimpleNode> out(String edgeName) {
    return this.asSimpleNode().out(edgeName);
  };
  
  public default Stream<SimpleNode> in(String edgeName) {
    return this.asSimpleNode().in(edgeName);
  };
  
  public default Stream<SimpleNode> recursiveOut(String edgeName,int recursionDepth) {
    // get the neighbor nodes with wrapped vertices following the edge with the given name
    // prepare a list of simple nodes for the recursive results
    List<SimpleNode> recursiveOuts=new ArrayList<SimpleNode>();
    Stream<SimpleNode> outs = this.out(edgeName);
    // if we still have recursion levels left over
    if (recursionDepth>0) {
      outs.forEach(simpleNode->{
        recursiveOuts.add(simpleNode);
        // get the edge nodes for this level
        Stream<SimpleNode> levelOuts = simpleNode.recursiveOut(edgeName,recursionDepth-1);
        // add them all to the recursive result
        levelOuts.forEach(levelSimpleNode->recursiveOuts.add(levelSimpleNode));
      });
    }
    return recursiveOuts.stream();
  }
 
  // show name values
  public default void printNameValues(PrintStream out) {
    Map<String, Object> map = this.getMap();
    for (String key : map.keySet()) {
      out.println(String.format("%s = %s", key, map.get(key)));
    }
  }
}
