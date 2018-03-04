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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.stream.Stream;

import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

/**
 * wraps Java Parser for Java Source Code
 * @author wf
 *
 */
public class JavaSourceNode extends SimpleNodeImpl {

  private File sourceFile;
  CompilationUnit compilationUnit;

  /**
   * create a JavaSource Node based on the given nodeQuery 
   * @param simpleGraph
   * @param nodeQuery
   * @param keys
   */
  public JavaSourceNode(SimpleGraph simpleGraph, String nodeQuery, String[] keys) {
    super(simpleGraph, "java", keys);
    sourceFile=new File(nodeQuery);
    try {
      compilationUnit=JavaParser.parse(sourceFile);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    super.setVertexFromMap();
    compilationUnit.walk(node->{
      JavaASTNode jASTNode = new JavaASTNode(this.getSimpleGraph(),node);
      // node.getParentNode();
    });
  }

  @Override
  public Map<String, Object> initMap() {
    map.put("path", sourceFile.getPath());
    return map;
  }

  @Override
  public Stream<SimpleNode> out(String edgeName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Stream<SimpleNode> in(String edgeName) {
    // TODO Auto-generated method stub
    return null;
  }

}
