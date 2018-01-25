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
package com.bitplan.smw;

import java.net.URLEncoder;

import com.bitplan.json.JsonPrettyPrinter;
import com.bitplan.json.JsonSystem;
import com.bitplan.mediawiki.MediaWikiPageNode;
import com.bitplan.mediawiki.MediaWikiSystem;
import com.bitplan.mediawiki.japi.SSLWiki;
import com.bitplan.mediawiki.japi.api.Api;
import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.SimpleSystem;

/**
 * Semantic MediaWiki system wrapper
 * 
 * @author wf
 *
 */
public class SMWSystem extends MediaWikiSystem {
 
  @Override
  public SimpleSystem connect(String... connectionParams) throws Exception {
    return super.connect(connectionParams);
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    if (nodeQuery.trim().startsWith("["))
      return moveToAsk(nodeQuery, keys);
    else
      return new MediaWikiPageNode(this, nodeQuery, keys);
  }

  /**
   * get the result of an ask query
   * @param askQuery
   * @param keys
   * @return
   */
  private SimpleNode moveToAsk(String askQuery, String[] keys) {
    try {
      SSLWiki wiki = getWiki();
      wiki.setFormat("json");
      wiki.setDebug(isDebug());
      Api result;

      result = wiki.getActionResult("ask",
          "&query=" + URLEncoder.encode(askQuery, "UTF-8"));

      String json = result.getRawJson();
      if (this.isDebug())
        System.out.println(JsonPrettyPrinter.prettyPrint(json));
      JsonSystem js=new JsonSystem();
      js.setDebug(this.isDebug());
      js.connect("json",json);
      if (isDebug())
      js.getStartNode().g().V().forEachRemaining(node -> node.properties().forEachRemaining(
          prop -> System.out.println(String.format("%s.%s=%s", node.label(),
              prop.label(), prop.value()))));
      return js.getStartNode();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
