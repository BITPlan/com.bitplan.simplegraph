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

import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;

/**
 * test the SNMP System
 * @author wf
 *
 */
public class TestSNMPSystem {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.simplegraph.snmp");
  
  String pageCount=".1.3.6.1.2.1.43.10.2.1.4.1";
  @Test
  public void testSNMPSystem() throws Exception {
    // add your printer names here to activate the test
    String[] printers = {  };
    debug=true;
    for (String printer : printers) {
      SNMPSystem ss=new SNMPSystem();
      ss.connect(printer);
      ss.moveTo(pageCount);
      if (debug)
        ss.forAll(SimpleNode.printDebug);
    }
  }
}
