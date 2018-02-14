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
package com.bitplan.simplegraph.map;

import java.util.HashMap;
import java.util.Map;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

public class MapSystem extends SimpleSystemImpl {

  @Override
  public SimpleSystem connect(String... connectionParams) throws Exception {
    return this;
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Class<? extends SimpleNode> getNodeClass() {
    return MapNode.class;
  }
  
  /**
   * initialize a mapNode with the given key/value pairs
   * @param keyValues
   * @return a mapNode
   */
  public MapNode initMap(String kind,Object ...keyValues) {
    if (keyValues.length%2 !=0)
      throw new IllegalArgumentException("keyValues should come in pairs but odd "+keyValues.length+" supplied");
    Map<String,Object> map=new HashMap<String,Object>();
    for (int i=0;i<keyValues.length;i+=2) {
      map.put(keyValues[i].toString(), keyValues[i+1]);
    }
    return new MapNode(this,kind,map);
  }

}
