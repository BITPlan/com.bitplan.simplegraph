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

import java.util.Map;
import java.util.stream.Stream;

import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import com.bitplan.simplegraph.core.Keys;
import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * the SNMP node
 * @author wf
 *
 */
public class SNMPNode extends SimpleNodeImpl {

  private VariableBinding binding;

  public SNMPNode(SimpleGraph simpleGraph, String kind, String[] keys) {
    super(simpleGraph, kind, keys);
  }

  /**
   * get the snmpSystem
   * @param snmpSystem
   * @param binding
   */
  public SNMPNode(SNMPSystem snmpSystem, VariableBinding binding) {
    this(snmpSystem, binding.getOid().format(), Keys.EMPTY_KEYS);
    this.binding = binding;
    super.setVertexFromMap();
  }
  
  public SNMPSystem getSNMPSystem() {
    return (SNMPSystem)super.getSimpleGraph();
  }

  
  @Override
  public Map<String, Object> initMap() {
    Variable variable = binding.getVariable();
    Object value =this.getSNMPSystem().snmp.getValue(variable);
    map.put("value", value);
    return map;
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
