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
package com.bitplan.simplegraph.impl;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.apache.tinkerpop.gremlin.structure.io.IoCore;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;

/**
 * Generic to Cache graphs
 * 
 * @author wf
 *
 */
public class Cache {
  File file;
  String purpose;
  private SimpleSystem system;

  public SimpleSystem getSystem() {
    return system;
  }

  public void setSystem(SimpleSystem system) {
    this.system = system;
  }

  /**
   * create a cache with the given file for the given purpose
   * 
   * @param file
   * @param purpose
   */
  public Cache(SimpleSystem system, File file, String purpose) {
    this.setSystem(system);
    this.file = file;
    this.purpose = purpose;
  }

  /**
   * flush the cache
   * 
   * @throws Exception
   */
  public void flush() throws Exception {
    getSystem().graph().io(IoCore.graphml()).writeGraph(file.getPath());
  }

  /**
   * reinitialize the cache
   * 
   * @throws Exception
   */
  public void reinit() throws Exception {
    // read it
    system.graph().io(IoCore.graphml()).readGraph(file.getPath());
    final Holder<Exception> exceptionHolder=new Holder<Exception>();
    // simple nodes references are not o.k.
    // <data
    // key="mysimplenode">com.bitplan.wikidata.WikiDataNode@7905a0b8</data>
    system.graph().traversal().V().has("mysimplenode")
        .forEachRemaining(node -> {
          try {
            Object simpleNodeObject = node.property("mysimplenode").value();
            if (simpleNodeObject instanceof String) {
              Map<String, Object> map = new HashMap<String, Object>();
              node.properties().forEachRemaining(nodeprop -> {
                map.put(nodeprop.key(), nodeprop.value());
              });
              String[] varargs= {};
              Constructor<? extends SimpleNode> constructor = system
                  .getNodeClass().getDeclaredConstructor(system.getClass(),varargs.getClass());
              SimpleNode newNode = constructor.newInstance(system,varargs);
              newNode.setVertex(node);
              newNode.setMap(map);
              map.put("mysimplenode", newNode);
              node.property("mysimplenode", newNode);
            }
          } catch (Exception e) {
            exceptionHolder.setValue(e);
          }
        });
    // TODO - better collect all exceptions than throwing only one
    if (exceptionHolder.isPresent())
      throw exceptionHolder.getFirstValue();
  }

}