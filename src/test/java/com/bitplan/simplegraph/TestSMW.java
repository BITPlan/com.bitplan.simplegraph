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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bitplan.smw.SMWSystem;

/**
 * test Semantic Mediawiki access
 * 
 * @author wf
 *
 */
public class TestSMW extends BaseTest {
  
  /**
   * get the Semantic Mediawiki System unde test
   * @throws Exception 
   */
  public  SMWSystem getSMWSystem() throws Exception {
    SMWSystem smw = new SMWSystem();
    // debug=true;
    smw.setDebug(debug);
    smw.connect("https://www.semantic-mediawiki.org", "/w");
    return smw;
  }
  
  @Test
  public void testPage() throws Exception {
    SMWSystem smwSystem=getSMWSystem();
    SimpleNode pageNode = smwSystem.moveTo("page=Sol");
    pageNode.forAll(SimpleNode.printDebug);
  }

  @Test
  public void testAsk() throws Exception {
    SMWSystem smwSystem=getSMWSystem();
    // see https://www.semantic-mediawiki.org/wiki/Help:Concepts
    String query = "{{#ask:[[Concept:Semantic Web Events 2012]]\n"
        + "|?Has_Wikidata_item_ID=WikiDataId\n"
        + "|?Has planned finish=finish\n" + "|?Has planned start=start\n"
        + "|?Has_location=location\n" + "|format=table\n" + "}}";
    SimpleNode askResult = smwSystem.moveTo(query);
    long printOutCount = askResult.g().V().hasLabel("printouts").count().next()
        .longValue();
    assertEquals(2, printOutCount);
    long nodeCount = askResult.g().V().count().next().longValue();
    assertEquals(17, nodeCount);
    Object metaCount = askResult.g().V().hasLabel("meta").next()
        .property("count").value();
    assertNotNull(metaCount);
    assertEquals(2, Integer.parseInt(metaCount.toString()));
    debug = true;
    if (debug)
      askResult.g().V().has("isA", "Semantic Web Events 2012")
          .forEachRemaining(SimpleNode.printDebug);
  }

  @Test
  public void testFixAsk() {
    String askQuery = "{{#ask:[[Concept:Semantic Web Events 2012]]\n"
        + "|?Has_location\n" + "|format=table\n" + "}}";
    String fixedAsk = SMWSystem.fixAsk(askQuery);
    if (debug)
      System.out.println(fixedAsk);
    assertTrue("[[Concept:Semantic Web Events 2012]]|?Has_location|format=table"
        .equals(fixedAsk));
    String concept = SMWSystem.getConcept(askQuery);
    assertEquals("Semantic Web Events 2012", concept);
  }

  @Test
  public void testBrowseBySubject() throws Exception {
    SMWSystem smwSystem=getSMWSystem();
    String subject = "SMWCon_Fall_2012/Filtered_result_format";
    SimpleNode browseNode = smwSystem.moveTo("browsebysubject="+subject);
    List<String> properties=new ArrayList<String>();
    browseNode.g().V().has("property").forEachRemaining(prop -> {
      String propName = prop.property("property").value().toString();
      if (!propName.startsWith("_")) {
        properties.add(propName);
        if (debug)
          System.out.println(String.format("* [[Property:%s]]", propName));
      }
    });
    assertEquals(12,properties.size());
    assertTrue(properties.contains("Has_speaker"));
  }

}
