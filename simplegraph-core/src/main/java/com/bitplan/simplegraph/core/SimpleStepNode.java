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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * SimpleStepNodes have some support for adding edges via in and out steps
 * the steps are not "true" gremlin steps but just convenience methods
 * 
 * @author wf
 *
 */
public interface SimpleStepNode extends SimpleNode {
  /**
   * allowed edge directions
   */
  public static enum EdgeDirection {
    IN, OUT
    // ,BOTH
  };
  
  public Stream<SimpleStepNode> out(String edgeName);

  public Stream<SimpleStepNode> in(String edgeName);
  
  /**
   * recursive out handling
   * 
   * @param edgeName
   * @param recursionDepth
   * @return - the stream of nodes
   */
  public default Stream<SimpleStepNode> recursiveOut(String edgeName,
      int recursionDepth) {
    // get the neighbor nodes with wrapped vertices following the edge with the
    // given name
    // prepare a list of simple nodes for the recursive results
    List<SimpleStepNode> recursiveOuts = new ArrayList<SimpleStepNode>();
    Stream<SimpleStepNode> outs = this.out(edgeName);
    // if we still have recursion levels left over
    if (recursionDepth > 0) {
      outs.forEach(simpleNode -> {
        recursiveOuts.add(simpleNode);
        // get the edge nodes for this level
        Stream<SimpleStepNode> levelOuts = simpleNode.recursiveOut(edgeName,
            recursionDepth - 1);
        // add them all to the recursive result
        levelOuts
            .forEach(levelSimpleNode -> recursiveOuts.add(levelSimpleNode));
      });
    }
    return recursiveOuts.stream();
  }

}
