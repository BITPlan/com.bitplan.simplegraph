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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.AbstractVariable;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Opaque;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

/**
 * Simple Network Monitoring Protocol
 * see https://gist.github.com/akirad/5597203
 * 
 * @author wf
 *
 */
public class SNMP {
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.simplegraph.snmp");
  
  private String targetAddr;
  private String commStr;
  private int snmpVersion;
  private String portNum;
  private int retries;
  private int timeOutMSecs;

  static final private byte TAG1 = (byte) 0x9f;
  static final private byte TAG_FLOAT = (byte) 0x78;
  static final private byte TAG_DOUBLE = (byte) 0x79;
  /**
   * 
   * @param targetAddr
   * @throws IOException
   */
  public SNMP(String targetAddr, int retries, int timeOutMSecs)
      throws IOException {
    // Set default value.
    this.targetAddr = targetAddr;
    this.timeOutMSecs = timeOutMSecs;
    this.retries = retries;
    commStr = "public";
    snmpVersion = SnmpConstants.version1;
    portNum = "161";
  }

  public List<VariableBinding> walk(String oidStr) throws IOException {
    List<VariableBinding> bindings = new ArrayList<VariableBinding>();
    Address targetAddress = GenericAddress
        .parse("udp:" + targetAddr + "/" + portNum);
    TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
    Snmp snmp = new Snmp(transport);
    transport.listen();

    // setting up target
    CommunityTarget target = new CommunityTarget();
    target.setCommunity(new OctetString(commStr));
    target.setAddress(targetAddress);
    target.setRetries(retries);
    target.setTimeout(timeOutMSecs);
    target.setVersion(snmpVersion);
    OID oid;
    oid = new OID(oidStr);

    // Get MIB data.
    TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
    List<TreeEvent> events = treeUtils.getSubtree(target, oid);
    if (events != null && events.size() > 0) {
         // Handle the snmpwalk result.
      for (TreeEvent event : events) {
        if (event == null) {
          continue;
        }
        if (event.isError()) {
          System.err.println("oid [" + oid + "] " + event.getErrorMessage());
          continue;
        }

        VariableBinding[] varBindings = event.getVariableBindings();
        if (varBindings == null || varBindings.length == 0) {
          continue;
        }
        for (VariableBinding varBinding : varBindings) {
          if (varBinding == null) {
            continue;
          }
          bindings.add(varBinding);
        }
      }
      snmp.close();
    }
    return bindings;
  }
  /**
   * see https://github.com/fbacchella/jrds
   * 
   * @param variable
   * @return
   */
  public Object getValue(Variable variable) {
    Object retvalue = null;
    if (variable != null) {
      int type = variable.getSyntax();
      if (variable instanceof OID) {
        retvalue = variable;
      } else if (variable instanceof UnsignedInteger32) {
        if (variable instanceof TimeTicks) {
          long epochcentisecond = variable.toLong();
          retvalue = new Double(epochcentisecond / 100.0);
        } else
          retvalue = variable.toLong();
      } else if (variable instanceof Integer32)
        retvalue = variable.toInt();
      else if (variable instanceof Counter64)
        retvalue = variable.toLong();
      else if (variable instanceof OctetString) {
        if (variable instanceof Opaque) {
          retvalue = resolvOpaque((Opaque) variable);
        } else {
          // It might be a C string, try to remove the last 0;
          // But only if the new string is printable
          OctetString octetVar = (OctetString) variable;
          int length = octetVar.length();
          if (length > 1 && octetVar.get(length - 1) == 0) {
            OctetString newVar = octetVar.substring(0, length - 1);
            if (newVar.isPrintable()) {
              variable = newVar;
              LOGGER.log(Level.INFO, "Convertion an octet stream from "
                  + octetVar + " to " + variable);
            }
          }
          retvalue = variable.toString();
        }
      } else if (variable instanceof Null) {
        retvalue = null;
      } else if (variable instanceof IpAddress) {
        retvalue = ((IpAddress) variable).getInetAddress();
      } else {
        LOGGER.log(Level.WARNING,
            "Unknown syntax " + AbstractVariable.getSyntaxString(type));
      }
    }
    return retvalue;
  }

  private Object resolvOpaque(Opaque var) {

    // If not resolved, we will return the data as an array of bytes
    Object value = var.getValue();

    try {
      byte[] bytesArray = var.getValue();
      ByteBuffer bais = ByteBuffer.wrap(bytesArray);
      BERInputStream beris = new BERInputStream(bais);
      byte t1 = bais.get();
      byte t2 = bais.get();
      int l = BER.decodeLength(beris);
      if (t1 == TAG1) {
        if (t2 == TAG_FLOAT && l == 4)
          value = new Float(bais.getFloat());
        else if (t2 == TAG_DOUBLE && l == 8)
          value = new Double(bais.getDouble());
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, var.toString());
    }
    return value;
  }


}
