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
package com.bitplan.simplegraph.snmp;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import org.snmp4j.smi.VariableBinding;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

/**
 * wrap Simple Network Management Protocol
 * 
 * @author wf
 *
 */
public class SNMPSystem extends SimpleSystemImpl {

  SNMP snmp;

  @Override
  public SimpleSystem connect(String... connectionParams) throws Exception {
    InetAddress address = InetAddress.getByName(connectionParams[0]);
    // TODO use args4j?
    snmp = new SNMP(address.getHostAddress(), 2, 500);
    return this;
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    List<VariableBinding> bindings;
    try {
      bindings = snmp.walk(nodeQuery);
      SimpleNode firstNode = null;
      for (VariableBinding binding : bindings) {
        SimpleNode oidNode = new SNMPNode(this, binding);
        if (firstNode == null)
          firstNode = oidNode;
      }
      if (this.getStartNode() == null)
        this.setStartNode(firstNode);
      return firstNode;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Class<? extends SimpleNode> getNodeClass() {
    return SNMPNode.class;
  }

}
