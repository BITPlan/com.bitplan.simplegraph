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
package com.bitplan.simplegraph.excel;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.Holder;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

/**
 * allows Access to Microsoft Excel Tables
 * 
 * @author wf
 *
 */
public class ExcelSystem extends SimpleSystemImpl {

  @Override
  public SimpleSystem connect(String... connectionParams) throws Exception {
    return this;
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    WorkBookNode workBookNode = new WorkBookNode(this, nodeQuery);
    if (this.getStartNode() == null) {
      this.setStartNode(workBookNode);
    }
    return workBookNode;
  }

  @Override
  public Class<? extends SimpleNode> getNodeClass() {
    return WorkBookNode.class;
  }

  class EdgeInfo {
    Property inProp;
    Property outProp;
  }

  /**
   * convert me to a "proper" graph again to allow "round-trip" handling
   * 
   * @return - the graph
   */
  public Graph asGraph() {
    Graph graph = TinkerGraph.open();
    // first get the content of the vertex sheets
    g().V().has("sheetname").forEachRemaining(vSheetNode -> {
      SheetNode sheetNode = SimpleNode.of(vSheetNode, SheetNode.class);
      if (!sheetNode.isForEdge()) {
        // Vertex sheet
        String label = sheetNode.property("sheetname").toString();
        vSheetNode.edges(Direction.OUT, "rows").forEachRemaining(rowEdge -> {
          Vertex vRowNode = rowEdge.inVertex();
          // SimpleNode.printDebug.accept(vRowNode);
          VertexProperty<Object> idProperty = vRowNode.property("id");
          if (idProperty.isPresent()) {
            Object id = idProperty.value();
            final Vertex v = graph.addVertex(T.label, label, T.id, id);
            vRowNode.properties().forEachRemaining(prop -> {
              switch (prop.key()) {
              case SimpleNode.SELF_LABEL:
              case "row":
                break;
              case "id":
                break;
              default:
                v.property(prop.key(), prop.value());
              }
            });
          }
        });
      }
    });
    // second pass - do the edges
    g().V().has("sheetname").forEachRemaining(vSheetNode -> {
      SheetNode sheetNode = SimpleNode.of(vSheetNode, SheetNode.class);
      if (sheetNode.isForEdge()) {
        // Vertex sheet
        String label = sheetNode.property("sheetname").toString();
        vSheetNode.edges(Direction.OUT, "rows").forEachRemaining(rowEdge -> {
          Vertex vRowNode = rowEdge.inVertex();
          SimpleNode.printDebug.accept(vRowNode);
          EdgeInfo edgeInfo = new EdgeInfo();
          vRowNode.properties().forEachRemaining(prop -> {
            if (prop.key().startsWith("in ("))
              edgeInfo.inProp = prop;
            if (prop.key().startsWith("out ("))
              edgeInfo.outProp = prop;
          });

          if (edgeInfo.inProp != null && edgeInfo.outProp != null) {
            Vertex inVertex = graph.traversal().V(edgeInfo.inProp.value())
                .next();
            Vertex outVertex = graph.traversal().V(edgeInfo.outProp.value())
                .next();
            Edge e = outVertex.addEdge(label, inVertex);
            vRowNode.properties().forEachRemaining(prop -> {
              if (!(prop.equals(edgeInfo.inProp)
                  || prop.equals(edgeInfo.outProp))) {
                switch (prop.key()) {
                case SimpleNode.SELF_LABEL:
                case "row":
                  break;
                default:
                  e.property(prop.key(), prop.value());
                }
              }
            });
          }
        });
      }
    });
    return graph;
  }

  /**
   * create a workbook based on the given GraphTraversalSource
   * 
   * @param g
   * @return the Workbook
   */
  public Workbook createWorkBook(GraphTraversalSource g) {
    // delegate the call to static function of Excel
    return Excel.createWorkBook(g);
  }

  /**
   * save the workbook with the given filename
   * 
   * @param wb
   * @param fileName
   * @throws Exception
   */
  public void save(Workbook wb, String fileName) throws Exception {
    Excel.save(wb, fileName);
  }

}
