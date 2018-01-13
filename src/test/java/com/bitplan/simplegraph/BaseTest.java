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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.bitplan.filesystem.FileSystem;

/**
 * base class for tests holding global configs e.g. debug
 * 
 * @author wf
 *
 */
public class BaseTest {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.simplegraph");
  protected SimpleSystem fs;

  /**
   * get a File node for the given path
   * 
   * @param path
   * @return
   * @throws Exception
   */
  public SimpleNode getFileNode(String path, int levels) throws Exception {
    // create a new FileSystem access supplying the result as a SimpleSystem API
    // the "global" variable "fs" is set as a side effect
    fs = new FileSystem();
    // connect to this system with no extra information (e.g. no credentials)
    // and move to the path node
    SimpleNode start = fs.connect("").moveTo("src");
    // do gremlin style out traversals recursively to the given depth
    start.recursiveOut("files", levels);
    return start;
  }

  /**
   * show property values of a vertex for debug
   * 
   * @param vertex
   */
  protected void logPropertyValues(Vertex vertex) {
    if (debug)
      for (String key : vertex.keys()) {
        LOGGER.log(Level.INFO,
            String.format("%s = %s", key, vertex.property(key).value()));
      }
  }
}
