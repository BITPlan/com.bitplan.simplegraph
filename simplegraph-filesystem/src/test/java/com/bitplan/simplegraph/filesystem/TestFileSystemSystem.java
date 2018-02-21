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
package com.bitplan.simplegraph.filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import com.bitplan.gremlin.RegexPredicate;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.filesystem.FileNode;
import com.bitplan.simplegraph.filesystem.FileSystem;

/**
 * test navigating the Filesystem with SimpleGraph approaches
 * @author wf
 *
 */
public class TestFileSystem {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.filesystem");
  private static FileSystem fs;

 
  @Test
  public void testFileSystemTree() throws Exception {
    SimpleSystem fs=new FileSystem();
    assertEquals("FileSystem",fs.getName());
    assertEquals("0.0.1",fs.getVersion());
    FileNode start = (FileNode) fs.connect("").moveTo("../simplegraph-filesystem/src");
    if (debug)
      start.printNameValues(System.out);
    start.recursiveOut("files",Integer.MAX_VALUE).forEach(childFile->{
      if (debug)
        childFile.printNameValues(System.out);
    });
    long filecount = start.g().V().count().next().longValue();
    if (debug)
      LOGGER.log(Level.INFO,""+filecount);
    assertEquals(17,filecount);
    GraphTraversal<Vertex, Vertex> javaFiles = start.g().V().has("ext", "java");
    long javaFileCount=javaFiles.count().next().longValue();
    assertEquals(4,javaFileCount);
    javaFiles = start.g().V().has("ext", "java");
    if (debug)
      javaFiles.forEachRemaining(SimpleNode.printDebug);
  }
  /**
   * get a File node for the given path
   * 
   * @param path
   * @return
   * @throws Exception
   */
  public static SimpleNode getFileNode(String path, int levels) throws Exception {
    // create a new FileSystem access supplying the result as a SimpleSystem API
    // the "global" variable "fs" is set as a side effect
    fs = new FileSystem();
    // connect to this system with no extra information (e.g. no credentials)
    // and move to the path node
    SimpleNode start = fs.connect("").moveTo(path);
    // do gremlin style out traversals recursively to the given depth
    start.recursiveOut("files", levels);
    return start;
  }
  
  @Test
  public void testFullyQualifiedPath() throws Exception {
    // debug=true;
    SimpleNode start=getFileNode("../simplegraph-filesystem/src",2);
    long nodeCount=start.g().V().count().next().longValue();
    assertEquals(7,nodeCount);
    if (debug) {
      LOGGER.log(Level.INFO,"src has "+nodeCount+" subdirectories on the next two levels");
      start.forAll(SimpleNode.printDebug);
    }
  }
  
  @Test
  public void testFileSystemParent() throws Exception {
    // debug=true;
    // starting from the file "src/etc/header" move up the hierarchy to steps
    SimpleSystem fs=new FileSystem();
    FileNode start = (FileNode) fs.connect("").moveTo("src/etc/header.txt");
    FileNode src=(FileNode) start.in("parent").findFirst().get().in("parent").findFirst().get();
    if (debug)
      src.printNameValues(System.out);
    assertEquals("src",src.getMap().get("name"));
    assertEquals("src",src.getVertex().property("name").value());
  }

  @Test
  public void testEdgeDirection() {
    // we want three directions even if we only use two a this time
    // TODO: BOTH is not covered although Coverage tells you so!
    assertEquals(2,SimpleNode.EdgeDirection.values().length);
  }
  
  @Test
  public void testInvalidEdgeName() throws Exception  {
    SimpleSystem fs=new FileSystem();
    FileNode start = (FileNode) fs.connect("").moveTo("src/test");
    // link is an invalid edge for Files
    try {
      start.out("link");
      fail("Exception expected");
    } catch (Throwable th) {
      // debug=true;
      if (debug)
        System.out.println(th.getMessage());
      assertEquals("unknown edgeName link",th.getMessage());
    }
  }
  
  @Test
  public void testRegularExpession() throws Exception {
    SimpleNode start=getFileNode("src/test/java",3);
    start.g().V().has("ext","java").has("name",RegexPredicate.regex("Test.*")).forEachRemaining(node->{
      FileNode fileNode=(FileNode) node.property("mysimplenode").value(); 
      if (debug)
        fileNode.printNameValues(System.out);
      assertTrue(fileNode.getMap().get("name").toString().startsWith("Test"));
    });
  }
  
}
