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
package com.bitplan.simplegraph.java;

import java.util.Map;
import java.util.stream.Stream;

import com.bitplan.simplegraph.core.Keys;
import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithIdentifier;

/**
 * wraps a node of the abstract syntax tree
 * 
 * @author wf
 *
 */
public class JavaASTNode extends SimpleNodeImpl {
  Node node;

  public JavaASTNode(SimpleGraph simpleGraph, String kind, String[] keys) {
    super(simpleGraph, kind, keys);
  }

  public JavaASTNode(SimpleGraph simpleGraph, Node node) {
    this(simpleGraph, node.getClass().getSimpleName(), Keys.EMPTY_KEYS);
    this.node = node;
    this.setVertexFromMap();
  }

  @Override
  public Map<String, Object> initMap() {
    if (node instanceof NodeWithIdentifier) {
      NodeWithIdentifier<?> nwi = (NodeWithIdentifier<?>) node;
      map.put("name", nwi.getIdentifier());
    }
    if (node instanceof MethodDeclaration) {
      MethodDeclaration md = (MethodDeclaration) node;
      map.put("name", md.getNameAsString());
    }
    map.put("hashCode", node.hashCode());
    map.put("node", node);
    return map;
  }

}
