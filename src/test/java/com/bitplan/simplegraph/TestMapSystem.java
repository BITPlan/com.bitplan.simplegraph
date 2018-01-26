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

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.bitplan.map.MapNode;
import com.bitplan.map.MapSystem;

/**
 * test the Map System
 * @author wf
 */
public class TestMapSystem extends BaseTest {
  private static final String WIKIDATA_URL_PREFIX = "https://www.wikidata.org/wiki/";
  public Map<String,Object> initMap(Object ...keyValues) {
    if (keyValues.length%2 !=0)
      throw new IllegalArgumentException("keyValues should come in pairs but odd "+keyValues.length+" supplied");
    Map<String,Object> map=new HashMap<String,Object>();
    for (int i=0;i<keyValues.length;i+=2) {
      map.put(keyValues[i].toString(), keyValues[i+1]);
    }
    return map;
  }
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void testMapSystem() throws Exception {
    // create a map system and connect to init
    MapSystem ms=new MapSystem();
    ms.connect();
    // init some maps of carbrands and cars each map shall later represent a
    // vertex in the graph with it's properties
    Map[] carbrandmaps= {
        initMap("name","Ferrari","country","Italy","wikidataid","Q27586"),
        initMap("name","Porsche","country","Germany","wikidataid","Q40993"),
        initMap("name","Ford","cuntry","United States","wikidataid","Q44294")
    };
    Map[] carmakemaps= {
        initMap("name","308","year",1984,"wikidataid","Q1407659","brand","Ferrari"),
        initMap("name","328","year",1989,"wikidataid","Q1407669","brand","Ferrari"),
        initMap("name","901","year",1964,"wikidataid","Q2104537","brand","Porsche"),
        initMap("name","2017 GT","year",2017,"wikidataid","Q23856323","brand","Ford")
    };
    // create MapNodes with the given kind "carbrand" or "car" based on the maps
    MapNode startNode=null;
    for (Map map:carbrandmaps) {
      startNode=new MapNode(ms,"carbrand",map);
    }
    for (Map map:carmakemaps) {
      MapNode mapNode=new MapNode(ms,"car",map);
      // link the node of this car to it's carbrand node using the Gremlin graph traversal
      // language - this is the key action for this example
      ms.g().V().hasLabel("carbrand").has("name",map.get("brand")).forEachRemaining(brandNode->{
        brandNode.addEdge("brand", mapNode.getVertex());
      });
    }
    // set a start node for the system
    // any node will do and for this example it is not really necessary - each node
    // has the full graph accesible
    ms.setStartNode(startNode);
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
    String graphViz = TestRythm.generateGraphViz(ms.getStartNode(), "brand", "name", "wikidataid",
        WIKIDATA_URL_PREFIX,"RL","CarGraph");
    // uncomment if you'd like to see the graph source code
    // the rendered graph is available at http://www.bitplan.com/index.php?title=SimpleGraph#CarGraph
    // debug = true;
    if (debug)
      System.out.println(graphViz.trim());
    // check that the graph contains one of the expected graphviz code lines
    assertTrue(graphViz.contains("\"Q27586\" [ label=\"Ferrari\" URL=\"https://www.wikidata.org/wiki/Q27586\"]"));
  }

}
