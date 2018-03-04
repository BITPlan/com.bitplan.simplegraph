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
package com.bitplan.simplegraph.xml;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

/**
 * wraps an XML document
 * @author wf
 *
 */
public class XmlSystem extends SimpleSystemImpl {

  private DocumentBuilderFactory dbFactory;
  private DocumentBuilder docBuilder;

  @Override
  public SimpleSystem connect(String... connectionParams) throws Exception {
    dbFactory = DocumentBuilderFactory.newInstance();
    docBuilder = dbFactory.newDocumentBuilder();
    return this;
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    XmlNode node;
    try {
      Document doc = docBuilder.parse(nodeQuery);
      node = new XmlNode(this,doc,doc.getDocumentElement());
      super.optionalStartNode(node);
    } catch (SAXException | IOException e) {
      throw new RuntimeException(e);
    }
    return node;
  }

  @Override
  public Class<? extends SimpleNode> getNodeClass() {
    return XmlNode.class;
  }

}
