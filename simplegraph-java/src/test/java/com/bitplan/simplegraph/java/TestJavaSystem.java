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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * test the Java System
 * 
 * @author wf
 *
 */
public class TestJavaSystem {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.simplegraph.java");

  /**
   * test for the Java System
   */
  @Test
  public void testJavaSystem() {
    JavaSystem js = new JavaSystem();
    JavaSourceNode jsn = (JavaSourceNode) js.moveTo(
        "../simplegraph-java/src/test/java/com/bitplan/simplegraph/java/TestJavaSystem.java");
    // debug = true;
    if (debug) {
      js.forAll(SimpleNode.printDebug);
      CompilationUnit cu = jsn.compilationUnit;
      cu.walk(node -> {
        System.out.println(node.getClass().getName());
      });
    }
    MethodDeclaration md = (MethodDeclaration) js.g().V()
        .hasLabel("MethodDeclaration").has("name", "testJavaSystem").next()
        .property("node").value();
    assertTrue(md.getComment().isPresent());
    assertTrue(md.getComment().get().getContent()
        .contains("test for the Java System"));
  }

  @Test
  public void testJavaParser() throws FileNotFoundException {
    File srcFile = new File(
        "../simplegraph-powerpoint/src/main/java/com/bitplan/simplegraph/powerpoint/PowerPointSystem.java");
    assertTrue(srcFile.exists());
    CompilationUnit cu = JavaParser.parse(srcFile);
    debug=true;
    if (debug)
      cu.walk(node -> {
        if (node instanceof MethodDeclaration) {
          MethodDeclaration md = (MethodDeclaration) node;
          System.out.println(String.format("%s:%s", node.getClass().getSimpleName(),md.getNameAsString()));
          System.out.println(node.toString());
        }
      });
  }
}
