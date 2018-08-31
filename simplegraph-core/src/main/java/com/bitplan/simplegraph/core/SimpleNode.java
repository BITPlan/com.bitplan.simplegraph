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

import java.io.PrintStream;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * The SimpleNode interface represents a Vertex in a graph
 * https://en.wikipedia.org/wiki/Vertex_(graph_theory) it wraps an Apache
 * TinkerPop vertex
 * 
 * @author wf
 *
 */
public interface SimpleNode extends SimpleGraph {
  
  static String SELF_LABEL="mysimplenode";
  
  static Consumer<Vertex> printDebug = vertex -> vertex.properties()
      .forEachRemaining(prop -> System.out.println(String.format("%s.%s=%s",
          vertex.label(), prop.label(), prop.value())));
  
  static Consumer<Vertex> printObjectDebug = vertex -> vertex.properties()
      .forEachRemaining(prop -> System.out.println(String.format("%s.%s=%s (%s)",
          vertex.label(), prop.label(), prop.value(),prop.value().getClass().getName())));

  static Consumer<Map<?, Object>> printMapDebug = map -> {
    map.keySet().forEach(
        key -> System.out.println(String.format("%s=%s", key, map.get(key))));
  };

  // interface to Tinkertop/Gremlin wrapped Vertex
  public Vertex getVertex();

  public Vertex setVertex(Vertex vertex);

  public void setMap(Map<String, Object> map);

  // fill my Vertex using my Map - this is the standard way to set the Nodes
  // Vertex
  public Vertex setVertexFromMap();

  public Map<String, Object> getMap();

  // fill the map with it's data
  public Map<String, Object> initMap();

  /**
   * set the property value for the given key with traditional java style syntax
   * 
   * @param key
   * @param value
   */
  public default void setProperty(String key, Object value) {
    property(key, value);
  }

  /**
   * get the property value for the given key with traditional java style syntax
   * 
   * @param key
   * @return - the property value
   */
  public default Object getProperty(String key) {
    return property(key);
  }

  /**
   * add the key value pair to the vertex and map tinkerpop style syntax
   * 
   * @param key
   * @param value
   */
  public default void property(String key, Object value) {
    getVertex().property(key, value);
    getMap().put(key, value);
  }

  /**
   * tinkerpop compatible getter
   * 
   * @param key
   * @return - the property
   */
  public default Object property(String key) {
    return getMap().get(key);
  }

  // interfaces with default implementation

  // show name values
  public default void printNameValues(PrintStream out) {
    Map<String, Object> map = this.getMap();
    for (String key : map.keySet()) {
      out.println(String.format("%s = %s", key, map.get(key)));
    }
  }

  /**
   * forAll
   */
  public default void forAll(Consumer<Vertex> consumer) {
    g().V().forEachRemaining(consumer);
  };
  
  /**
   * get the selfLabel for this Node
   * @return - the self label
   */
  public default String getSelfLabel() {
    return SELF_LABEL;
  }
  
  /**
   * get the Keys
   * @return the keys
   */
  public Keys getKeys();

  /**
   * get the SimpleNode of a Vertex 
   * @param vertex
   * @return - the simpleNode if available or null if not
   */
  public static SimpleNode of(Vertex vertex) {
    if (vertex.property(SimpleNode.SELF_LABEL).isPresent()) {
      return (SimpleNode) vertex.property(SimpleNode.SELF_LABEL).value();
    }
    return null;
  }

  /**
   * get the SimpleNode of a Vertex for the given type
   * @param vertex
   * @param type
   * @return the casted simpleNode
   */
  @SuppressWarnings("unchecked")
  public static <T> T of(Vertex vertex, Class<? extends SimpleNode> type) {
    SimpleNode simpleNode=of(vertex);
    if (simpleNode !=null && simpleNode.getClass().isAssignableFrom(type)) {
      return (T) simpleNode;
    } else {
      return null;
    }
  }

}
