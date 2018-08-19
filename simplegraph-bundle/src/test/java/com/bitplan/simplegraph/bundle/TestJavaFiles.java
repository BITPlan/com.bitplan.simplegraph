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
package com.bitplan.simplegraph.bundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.filesystem.FileNode;
import com.bitplan.simplegraph.filesystem.FileSystem;
import com.bitplan.simplegraph.java.JavaSystem;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

/**
 * test the Java files
 * @author wf
 *
 */
public class TestJavaFiles {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.simplegraph.bundle");

  @Test
  public void testJavaFiles() {
    FileSystem fs = new FileSystem();
    fs.connect();
    FileNode fileRoot = fs.moveTo("..");
    fileRoot.recursiveOut("files", Integer.MAX_VALUE);
    long javaFileCount = fs.g().V().hasLabel("file").has("ext", "java").count()
        .next().longValue();
    if (debug)
      System.out.println(javaFileCount);
    assertTrue(javaFileCount >= 80);
    JavaSystem js = new JavaSystem();
    fs.g().V().hasLabel("file").has("ext", "java")
        .forEachRemaining(javaFile -> {
          SimpleNode javaSrcNode=js.moveTo(javaFile.property("path").value().toString());
        });
    // js.g().V().hasLabel("MethodDeclaration").forEachRemaining(SimpleNode.printDebug);
    assertEquals(20,js.g().V().hasLabel("MethodDeclaration").has("name", "connect").count().next().longValue());
    // debug = true;
    js.g().V().hasLabel("MethodDeclaration").has("name", "connect")
        .forEachRemaining(jnode -> {
          MethodDeclaration md = (MethodDeclaration) jnode.property("node")
              .value();
          if (debug) {
            System.out.println(md.getNameAsString());
            Optional<BlockStmt> obody = md.getBody();
            Optional<Node> ocd = md.getParentNode();
            if (ocd.isPresent()) {
                ClassOrInterfaceDeclaration cd = (ClassOrInterfaceDeclaration) md.getParentNode().get();
                System.out.println(cd.getNameAsString());
            }
            if (obody.isPresent()) {
              System.out.println(md.getBody().get().toString());
            }
          }
        });
  }

}
