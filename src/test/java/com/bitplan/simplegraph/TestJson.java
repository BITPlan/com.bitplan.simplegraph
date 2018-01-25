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

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.bitplan.json.JsonPrettyPrinter;
import com.bitplan.json.JsonSystem;

/**
 * test the JSON system
 * @author wf
 *
 */
public class TestJson extends BaseTest {
  @Test
  public void testJson() throws Exception {
    // debug=true;
    // http://json.org/example.html
    // https://raw.githubusercontent.com/LearnWebCode/json-example/master/pets-data.json
    // https://stackoverflow.com/questions/33910605/how-to-convert-sample-json-into-json-schema-in-java
    File[] jsonFiles = { 
        new File("src/test/pets.json"), new File("src/test/menu.json"), new File("src/test/employee.json") };
    
    long[] expectedNodes= {4,20,6};
    int i=0;
    for (File jsonFile : jsonFiles) {
      String json = FileUtils.readFileToString(jsonFile, "UTF-8");
      if (debug)
        System.out.println(JsonPrettyPrinter.prettyPrint(json));
      JsonSystem js = new JsonSystem();
      js.connect("json", json);
      if (debug)
        js.getStartNode().g().V().forEachRemaining(node -> node.properties().forEachRemaining(
            prop -> System.out.println(String.format("%s.%s=%s", node.label(),
                prop.label(), prop.value()))));
      long nodes =js.getStartNode().g().V().count().next().longValue();
      assertEquals(expectedNodes[i++],nodes);
    }
  }
}