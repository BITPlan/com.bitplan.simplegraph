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

import java.util.Map;
import java.util.stream.Stream;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bitplan.simplegraph.core.Keys;
import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

public class XmlNode extends SimpleNodeImpl {

  private Node node;

  public XmlNode(SimpleGraph simpleGraph, String kind, String[] keys) {
    super(simpleGraph, kind, keys);
  }

  /**
   * construct me from a node
   * 
   * @param simpleGraph
   * @param node
   */
  public XmlNode(SimpleGraph simpleGraph, Node node) {
    this(simpleGraph, node.getNodeName(), Keys.EMPTY_KEYS);
    init(node);
  }

  public XmlNode(XmlSystem simpleGraph, Document doc, Element documentElement) {
    this(simpleGraph, documentElement.getNodeName(), Keys.EMPTY_KEYS);
    init(documentElement);
  }

  /**
   * initialize me from a node
   * 
   * @param node
   */
  private void init(Node node) {
    this.node = node;
    super.setVertexFromMap();
    NodeList nodeList = node.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node subNode = nodeList.item(i);
      if (subNode instanceof Element) {
        XmlNode xmlNode = new XmlNode(this.getSimpleGraph(), subNode);
        Vertex subVertex = xmlNode.getVertex();
        subVertex.property("XmlSystem.childIndex",i);
        this.getVertex().addEdge("child",subVertex );
      }
    }
  }

  @Override
  public Map<String, Object> initMap() {
    NamedNodeMap attrs = node.getAttributes();
    if (attrs != null)
      for (int i = 0; i < attrs.getLength(); i++) {
        Node attr = attrs.item(i);
        map.put(attr.getNodeName(), attr.getNodeValue());
      }
    if (node.getChildNodes().getLength() == 1)
      map.put("text", node.getTextContent());
    return map;
  }

}
