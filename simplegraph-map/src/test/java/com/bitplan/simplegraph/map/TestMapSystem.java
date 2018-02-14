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
package com.bitplan.simplegraph.map;

import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.rythm.RythmContext;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.map.MapNode;
import com.bitplan.simplegraph.map.MapSystem;

/**
 * test the Map System
 * 
 * @author wf
 */
public class TestMapSystem {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.map");
  private static final String WIKIDATA_URL_PREFIX = "https://www.wikidata.org/wiki/";

  /**
   * get the MapSystem example for Cars and their makes
   * 
   * @return - the example carbarnd/carmake system
   * @throws Exception
   */
  public static MapSystem getCarMapSystem() throws Exception {
    // create a map system and connect to init
    MapSystem ms = new MapSystem();
    ms.connect();
    // init some maps of carbrands and cars each map shall later represent a
    // vertex in the graph with it's properties
    MapNode startNode = ms.initMap("carbrand", "name", "Ferrari", "country",
        "Italy", "wikidataid", "Q27586");
    ms.initMap("carbrand", "name", "Porsche", "country", "Germany",
        "wikidataid", "Q40993");
    ms.initMap("carbrand", "name", "Ford", "cuntry", "United States",
        "wikidataid", "Q44294");

    ms.initMap("carmake", "name", "308", "year", 1984, "wikidataid", "Q1407659",
        "brand", "Ferrari");
    ms.initMap("carmake", "name", "328", "year", 1989, "wikidataid", "Q1407669",
        "brand", "Ferrari");
    ms.initMap("carmake", "name", "901", "year", 1964, "wikidataid", "Q2104537",
        "brand", "Porsche");
    ms.initMap("carmake", "name", "2017 GT", "year", 2017, "wikidataid",
        "Q23856323", "brand", "Ford");
    // link the node of each car to it's carbrand node using the Gremlin graph
    // traversal
    // language - this is the key action for this example
    // debug=true;
    ms.g().V().hasLabel("carbrand").dedup().forEachRemaining(carbrand -> {
      String brandname = carbrand.property("name").value().toString();
      if (debug)
        System.out.println("linking " + brandname);
      ms.g().V().hasLabel("carbrand").has("name", brandname)
          .forEachRemaining(brandNode -> {
            brandNode.addEdge("brand", carbrand);
          });
    });

    // set a start node for the system
    // any node will do and for this example it is not really necessary - each
    // node
    // has the full graph accessible
    ms.setStartNode(startNode);
    return ms;
  }

  @Test
  public void testMapSystem() throws Exception {
    MapSystem ms = getCarMapSystem();
    // uncomment if you'd like to see all the node details
    // debug=true;
    if (debug)
      ms.g().V().forEachRemaining(SimpleNode.printDebug);
    // generate a graphviz graph based on this start node
    // show the "brand" edges
    // show the "name" for each node
    // use wikidataid as the identifier
    // and extend to a full url using the WIKIDATA_URL_PREFIX
    // use the rankDir RL = right left
    // and name the graph "CarGraph"
    String graphViz = RythmContext.getInstance().renderGraphViz(ms.getStartNode(), "brand",
        "name", "wikidataid", WIKIDATA_URL_PREFIX, "RL", "CarGraph");
    // uncomment if you'd like to see the graph source code
    // the rendered graph is available at
    // http://www.bitplan.com/index.php?title=SimpleGraph#CarGraph
    // debug = true;
    if (debug)
      System.out.println(graphViz.trim());
    // check that the graph contains one of the expected graphviz code lines
    assertTrue(graphViz.contains(
        "\"Q27586\" [ label=\"Ferrari\" URL=\"https://www.wikidata.org/wiki/Q27586\"]"));
  }

}
