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
package com.bitplan.json;

import javax.ws.rs.core.MediaType;

import com.bitplan.simplegraph.SimpleGraph;
import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * i wrap a Json Tree as a Gremlin Graph
 * 
 * @author wf
 *
 */
public class JsonSystem extends SimpleSystemImpl {
  JsonParser parser = new JsonParser();
  String json;
  Client client = null;

  /**
   * create a JsonSystem using the same simpleGraph
   * 
   * @param graph
   */
  public JsonSystem(SimpleGraph graph) {
    super(graph);
  }

  /**
   * create a new JsonSystem with it's own graph
   */
  public JsonSystem() {
    super();
  }

  @Override
  public SimpleSystem connect(String... params) throws Exception {
    if (params.length >= 2 && ("json".equals(params[0]))) {
      this.json = params[1];
      this.setStartNode(new JsonNode(this, "jsonroot", parser.parse(json)));
      /*
       * if (params.length >= 3 && "tree".equals(params[2])) {
       * treeWalk((JsonNode) this.getStartNode()); }
       */
    }
    return this;
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    SimpleNode result=null;
    if (client == null)
      client = Client.create();
    WebResource resource = client.resource(nodeQuery);
    ClientResponse response = resource.accept(MediaType.APPLICATION_JSON_TYPE)
        .get(ClientResponse.class);
    if (response.getStatus() == 200) {
      String json=response.getEntity(String.class);
      result=new JsonNode(this, "jsonroot", parser.parse(json));
      if (this.getStartNode()==null)
        this.setStartNode(result);
    }
    return result;
  }

  @Override
  public Class<? extends SimpleNode> getNodeClass() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * create a new JsonSystem based on the given graph and json string
   * 
   * @param graph
   *          - the graph to link to (may be null for creating an independent
   *          system)
   * @param json
   *          - the json string to parse
   * @return - the JsonSystem with it's start node set to the root of the parse
   *         tree
   * @throws Exception
   */
  public static JsonSystem of(SimpleGraph graph, String json) {
    JsonSystem js = new JsonSystem(graph);
    js.setDebug(graph.isDebug());
    try {
      js.connect("json", json);
      if (graph.isDebug())
        js.getStartNode().forAll(SimpleNode.printDebug);
      return js;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
