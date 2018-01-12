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

import static org.junit.Assert.assertEquals;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import com.bitplan.filesystem.FileNode;
import com.bitplan.filesystem.FileSystem;

/**
 * test navigating the Filesystem with SimpleGraph approaches
 * @author wf
 *
 */
public class TestFileSystem {
  boolean debug=true;
  @Test
  public void testFileSystem() throws Exception {
    SimpleSystem fs=new FileSystem();
    FileNode start = (FileNode) fs.connect("").moveTo("src");
    if (debug)
      start.printNameValues(System.out);
    start.recursiveOut("files",Integer.MAX_VALUE).forEach(childFile->{
      if (debug)
        childFile.printNameValues(System.out);
    });
    long filecount = start.g().V().count().next().longValue();
    if (debug)
      System.out.println(filecount);
    assertEquals(28,filecount);
    GraphTraversal<Vertex, Vertex> javaFiles = start.g().V().has("ext", "java");
    long javaFileCount=javaFiles.count().next().longValue();
    assertEquals(11,javaFileCount);
    javaFiles = start.g().V().has("ext", "java");
    javaFiles.forEachRemaining(javaFile-> {
      for (String key:javaFile.keys()) {
        if (debug)
          System.out.println(String.format("%s = %s", key, javaFile.property(key).value()));
      }
    });
    //Vertex txtFile=start.g().V().has("ext","txt").next();
    // Vertex etcDir= txtFile.in("parent").findFirst().get();
    //assertEquals("src/etc",etcDir.getMap().get("path"));
  }

}
