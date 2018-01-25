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

import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;
import com.google.gson.JsonParser;

/**
 * i wrap a Json Tree as a Gremlin Graph
 * 
 * @author wf
 *
 */
public class JsonSystem extends SimpleSystemImpl {
  JsonParser parser = new JsonParser();
  String json;

  @Override
  public SimpleSystem connect(String... params) throws Exception {
    if (params.length >= 2 && ("json".equals(params[0]))) {
      this.json = params[1];
      this.setStartNode(new JsonNode(this, "jsonroot", parser.parse(json)));
      /* if (params.length >= 3 && "tree".equals(params[2])) {
        treeWalk((JsonNode) this.getStartNode());
      }*/
    }
    return this;
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Class<? extends SimpleNode> getNodeClass() {
    // TODO Auto-generated method stub
    return null;
  }

}
