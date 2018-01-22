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

import java.util.HashMap;
import java.util.Map;

import com.bitplan.simplegraph.SimpleSystem;

/**
 * implementation of a simple System that is to be wrapped to be handled using
 * Apache TinkerPop3 hiding some of the complexity
 * @author wf
 *
 */
public abstract class SimpleSystemImpl extends SimpleGraphImpl implements SimpleSystem {
  
  String name;
  String version;
  protected transient Map<String, Cache> cacheMap = new HashMap<String, Cache>();
  
  public SimpleSystemImpl() {
    super();
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getVersion() {
    return version;
  }
  
  public void setVersion(String version) {
    this.version = version;
  }
  
  /**
   * flush the cache for the given purpose
   * 
   * @param purpose
   * @throws Exception
   */
  public void flushCache(String purpose) throws Exception {
    if (!cacheMap.containsKey(purpose))
      throw new IllegalArgumentException(
          "no cache for purpose " + purpose + " in use");
    cacheMap.get(purpose).flush();
  }
  
 
  
  @Override
  public SimpleSystem close(String ... closeParams) throws Exception {
    for (String purpose:this.cacheMap.keySet()) {
      this.flushCache(purpose);
    }
    return this;
  }

}
