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

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

/**
 * system for parsing Java source code
 * @author wf
 *
 */
public class JavaSystem extends SimpleSystemImpl {

  @Override
  public SimpleSystem connect(String... connectionParams) throws Exception {
    TinkerGraph tg=(TinkerGraph) super.graph();
    tg.createIndex("hashCode", Vertex.class);
    return this;
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    SimpleNode javaSourceNode=new JavaSourceNode(this,nodeQuery,keys);
    optionalStartNode(javaSourceNode);
    return javaSourceNode;
  }


  @Override
  public Class<? extends SimpleNode> getNodeClass() {
    return JavaSourceNode.class;
  }

}
