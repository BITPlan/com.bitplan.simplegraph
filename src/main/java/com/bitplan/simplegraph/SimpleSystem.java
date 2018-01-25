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

/**
 * a System that allows access to a graph that is wrapped by the system
 * 
 * @author wf
 *
 */
public interface SimpleSystem extends SimpleGraph {
  /**
   * get the name of this system
   * @return the name
   */
  String getName();
  
  /**
   * get the version of this system
   * @return the version
   */
  String getVersion();
  
  public boolean isDebug();

  public void setDebug(boolean debug);
  
  /**
   * connect to system 
   * @param connectionParams - optionally specify system specific connection parameters
   * @return - the SimpleSystem interface for the system connected to
   * @throws Exception
   */
  SimpleSystem connect(String ... connectionParams) throws Exception;
  
  /**
   * move to the node specified by the system specific node query
   * typically supplying the id of a node is sufficient
   * @param nodeQuery
   * @param keys - optionally limit the information to be gathered on the node to the given keys for properties/neighbours
   * @return the node moved to
   */
  SimpleNode moveTo(String nodeQuery,String ...keys);
  
  /**
   * close the system
   * @param closeParams
   * @return
   * @throws Exception
   */
  public default SimpleSystem close(String ... closeParams)  throws Exception {
    return this;
  }
  
  /**
   * get the main node class for this system
   * @return the main node class
   */
  public Class<? extends SimpleNode> getNodeClass();
 
}
