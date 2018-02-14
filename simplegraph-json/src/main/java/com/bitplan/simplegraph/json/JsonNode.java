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
package com.bitplan.simplegraph.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.bitplan.simplegraph.core.Keys;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Json Node wraps a Json Element
 * 
 * @author wf
 *
 */
public class JsonNode extends SimpleNodeImpl {
  boolean debug;
  JsonElement jElement;

  /**
   * create me
   * 
   * @param js
   * @param kind
   * @param keys
   */
  public JsonNode(SimpleSystem js, String kind, String[] keys) {
    super(js, kind, keys);
    debug=js.isDebug();
  }

  /**
   * construct me from the given jsonElement with the given kind
   * 
   * @param jsonSystem
   * @param kind
   * @param jElement
   */
  public JsonNode(SimpleSystem jsonSystem, String kind, JsonElement jElement) {
    this(jsonSystem, kind, Keys.EMPTY_KEYS);
    this.jElement = jElement;
    super.setVertexFromMap();
    addSubNodes(jElement);
  }

  @Override
  public Map<String, Object> initMap() {
    if (jElement.isJsonObject()) {
      jElement.getAsJsonObject().entrySet().forEach(entry -> {
        JsonElement jMapElement = entry.getValue();
        String key = entry.getKey();
        // get primitives or array of primitives below me
        Object value = mapValue(jMapElement);
        if (value!=null) {
          if (debug)
            System.out.println(String.format("putting %s=%s",key,value));
          map.put(key, value);
        }
      });
    }
    return map;
  }
  
  /**
   * add the subnodes for the given element
   * @param pElement
   */
  private void addSubNodes(JsonElement pElement) {
    if (pElement.isJsonObject()) {
      pElement.getAsJsonObject().entrySet().forEach(entry -> {
        JsonElement jSubNode = entry.getValue();
        String key = entry.getKey();
        if (jSubNode.isJsonObject()) {
          addNode(key, jSubNode.getAsJsonObject());
        } else if (jSubNode.isJsonArray()) {
          JsonArray jarray = jSubNode.getAsJsonArray();
          jarray.forEach(jArrayElement -> {
            if (jArrayElement.isJsonObject()) {
              addNode(key,jArrayElement.getAsJsonObject());
            }
          });
        }
      });
    }
  }
  
  /**
   * get the object to be put in the map
   * @param pElement
   * @return the object
   */
  public Object mapValue(JsonElement pElement) {
    if (pElement.isJsonPrimitive()) {
      return getJsonPrimitiveValue(pElement.getAsJsonPrimitive());
    } else if (pElement.isJsonArray()) {
      JsonArray jarray = pElement.getAsJsonArray();
      List<Object> arrayContent = new ArrayList<Object>();
      jarray.forEach(jArrayElement -> {
        Object value = mapValue(jArrayElement);
        if (value!=null)
          arrayContent.add(value);
      });
      if (arrayContent.size()==0)
        return null;
      return arrayContent;
    } else { // objects and nulls
      return null;
    }
  }

  /**
   * add the given node with the given key
   * 
   * @param key
   * @param jObject
   * @return
   */
  public JsonNode addNode(String key, JsonObject jObject) {
    if (debug)
      System.out.println(String.format("adding node %s",key));
    JsonNode subNode = new JsonNode((JsonSystem)this.getSimpleGraph(), key, jObject);
    this.getVertex().addEdge(key, subNode.getVertex());
    return subNode;
  }

  /**
   * get the JsonPrimitive Value for the given jsonPrimitive
   * 
   * @param jsonPrimitive
   * @return
   */
  private Object getJsonPrimitiveValue(JsonPrimitive jsonPrimitive) {
    if (jsonPrimitive.isBoolean())
      return jsonPrimitive.getAsBoolean();
    else if (jsonPrimitive.isString())
      return jsonPrimitive.getAsString();
    else if (jsonPrimitive.isNumber()) {
      return jsonPrimitive.getAsNumber();
    } else {
      throw new IllegalArgumentException(
          "unknown JsonPrimitive kind " + jsonPrimitive.toString());
    }
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
