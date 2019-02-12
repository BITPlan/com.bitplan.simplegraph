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
package com.bitplan.simplegraph.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.Holder;

/**
 * test the JSON system
 * 
 * @author wf
 *
 */
public class TestJsonSystem {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.simplegraph.json");

  @Test
  public void testJson() throws Exception {
    // debug = true;
    // http://json.org/example.html
    // https://raw.githubusercontent.com/LearnWebCode/json-example/master/pets-data.json
    // https://stackoverflow.com/questions/33910605/how-to-convert-sample-json-into-json-schema-in-java
    File jroot=new File("../simplegraph-json/src/test");
    File[] jsonFiles = { new File(jroot,"pets.json"),
        new File(jroot,"menu.json"), new File(jroot,"employee.json"),
        new File(jroot,"datatypes.json") };

    long[] expectedNodes = { 4, 20, 6, 43 };
    int i = 0;
    for (File jsonFile : jsonFiles) {
      String json = FileUtils.readFileToString(jsonFile, "UTF-8");
      if (debug)
        System.out.println(JsonPrettyPrinter.prettyPrint(json));
      JsonSystem js = new JsonSystem();
      js.connect("json", json);
      if (debug)
        js.getStartNode().forAll(SimpleNode.printDebug);
      long nodes = js.getStartNode().g().V().count().next().longValue();
      assertEquals(expectedNodes[i++], nodes);
      switch (i) {
      case 4: // datatypes
        assertEquals(1, js.getStartNode().g().V().hasLabel("results").count()
            .next().longValue());
        long resultsCount = js.getStartNode().g().V().hasLabel("results").out()
            .count().next().longValue();
        assertEquals(17, resultsCount);
        js.getStartNode().g().V().hasLabel("results").out()
            .forEachRemaining(rNode -> {
              if (debug) {
                /**
                 * "fulltext": "Help:Type URL", "fullurl":
                 * "https://www.semantic-mediawiki.org/wiki/Help:Type_URL",
                 * "namespace": 12, "exists": "1", "displaytitle":
                 * "Help:Datatype \"URL\""
                 */
                System.out.println(String.format("%s=%s (%s)", rNode.label(),
                    rNode.property("fulltext").value(),
                    rNode.property("displaytitle").value()));
                rNode.vertices(Direction.OUT, "printouts")
                    .forEachRemaining(pr -> {
                      pr.keys().forEach(key -> {
                        System.out.println(String.format("\t%s=%s", key,
                            pr.property(key).value()));
                      });
                    });
              }
            });
        break;
      }
    }
  }

  @Test
  public void testGoogleMapsJsonApi() throws Exception {
    String url = "https://maps.googleapis.com/maps/api/geocode/json?address=Cologne%20Cathedral";
    String json = IOUtils.toString(new URL(url), "UTF-8");
    // debug = true;
    if (debug)
      System.out.println(json);
    JsonSystem js = new JsonSystem();
    SimpleSystem dom = js.connect("json", json);
    if (debug)
      js.getStartNode().forAll(SimpleNode.printDebug);
    long nodeCount = js.g().V().count().next().longValue();
    // error message QUOTA ... has only a few nodes - we expect more
    if (nodeCount > 5) {
      Holder<String> latHolder = new Holder<String>();
      dom.g().V().hasLabel("location").forEachRemaining(
          v -> latHolder.add(v.property("lat").value().toString()));
      String lat = latHolder.getFirstValue();
      if (debug)
        System.out.println(lat);
      assertTrue(lat.startsWith("50.9412"));
    }
  }
  
  @Test
  public void testOpenChargeMapApi() throws Exception {
    String apiUrl="http://api.openchargemap.io/v2/poi/?output=json&latitude=50.598&longitude=7.434&maxresults=10";
    JsonSystem js=new JsonSystem();
    js.connect();
    js.moveTo(apiUrl);
    long nodes = js.g().V().count().next().longValue();
    assertEquals(190,nodes);
    if (debug)
      SimpleNode.dumpGraph(js.graph());
  }
}