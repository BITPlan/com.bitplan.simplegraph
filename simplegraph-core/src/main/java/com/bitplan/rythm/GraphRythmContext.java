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
package com.bitplan.rythm;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import com.bitplan.simplegraph.core.SimpleNode;

/**
 * specific RythmContext for Graph related environment
 * 
 * @author wf
 *
 */
public class GraphRythmContext extends RythmContext {
  /**
   * generate a GraphViz graph for the given parameters
   * 
   * @param start
   * @param edge
   * @param property
   * @param idProperty
   * @param urlPrefix
   * @param rankDir
   * @param graphname
   * @return
   * @throws Exception
   */
  public String renderGraphViz(SimpleNode start, String edge, String property,
      String idProperty, String urlPrefix, String rankDir, String graphname)
      throws Exception {
    // prepare the Map of information to be supplied for the Rythm template
    Map<String, Object> rootMap = new HashMap<String, Object>();
    // the SimpleNode to start with
    rootMap.put("start", start);
    // the edges to select
    rootMap.put("edge", edge);
    // the property to show (for edges/vertices)
    rootMap.put("property", property);
    // the property to derive the URL from
    rootMap.put("idProperty", idProperty);
    // the prefix to prepend to the idProperty to get the final url
    rootMap.put("urlPrefix", urlPrefix);
    // style of graph e.g. TB, BT, RL, LR (Top-Bottom, Bottom-Top, Right-Left,
    // Left-Right see. graphviz rankdir
    rootMap.put("rankdir", rankDir);
    // the name of the graph
    rootMap.put("graphname", graphname);
    String graphViz=renderTemplate("graphvizTree.rythm",rootMap);
    return graphViz;
  }
  
  /**
   * render an UML version of the given graph
   * @param g
   * @param string
   * @return - the plantuml string
   * @throws Exception
   */
  public String renderUml(GraphTraversalSource g, String title) throws Exception {
    Map<String, Object> rootMap = new HashMap<String, Object>();
    rootMap.put("g", g);
    rootMap.put("title", title);
    String uml = this.renderTemplate("plantuml.rythm", rootMap);
    return uml;
  }
  
  /**
   * render the given map via the given template
   * @param templateName
   * @param rootMap
   * @return the render result
   * @throws Exception
   */
  public String renderTemplate(String templateName, Map<String, Object> rootMap) throws Exception {
    // choose a Rythm template that will work on our graph
    String templatePath = "/com/bitplan/rythm/"+templateName;
    URL templateResource = this.getClass().getResource(templatePath);
    if (templateResource == null)
      throw new RuntimeException("template " + templatePath + " not found");
    // let Rythm do the rendering according to the template
    String result = render(templateResource, rootMap);
    return result;
  }

  private static GraphRythmContext instance = null;

  /**
   * get the singleton
   * 
   * @return the instance
   */
  public static GraphRythmContext getInstance() {
    if (instance == null) {
      instance = new GraphRythmContext();
    }
    return instance;
  }

}
