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

import org.junit.Test;

import com.bitplan.smw.SMWSystem;

/**
 * test Semantic Mediawiki access
 * 
 * @author wf
 *
 */
public class TestSMW extends BaseTest {

  @Test
  public void testAsk() throws Exception {
    SMWSystem smw = new SMWSystem();
    smw.setDebug(debug);
    smw.connect("https://www.semantic-mediawiki.org", "/w");
    String query = "[[Docinfo language::de]][[Has datatype ID::+]]"
        + " |?Docinfo language=language" + " |?Has datatype name=Datatype"
        + " |?Has description=Description" + " |?Has datatype ID=ID"
        + " |?Has component=Provided by|sort=Has datatype ID|order=desc|limit=200";
    SimpleNode askResult = smw.moveTo(query);
    long nodeCount = askResult.g().V().count().next().longValue();
    assertEquals(38, nodeCount);
    Object metaCount = askResult.g().V().hasLabel("meta").next()
        .property("count").value();
    assertNotNull(metaCount);
    assertEquals(14, Integer.parseInt(metaCount.toString()));
    debug = true;
    if (debug)
      askResult.g().V().has("fullurl")
          .forEachRemaining(dt -> dt.properties().forEachRemaining(
              prop -> System.out.println(String.format("%s.%s=%s", dt.label(),
                  prop.label(), prop.value()))));
  }

}
