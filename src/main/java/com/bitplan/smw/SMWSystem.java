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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
  // shall we return the json result as a graph (raw mode)?
  boolean rawMode = false;

  @Override
  public SimpleSystem connect(String... connectionParams) throws Exception {
    if ((connectionParams.length >= 4) && ("raw".equals(connectionParams[4])))
      rawMode = true;
    return super.connect(connectionParams);
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    if (fixAsk(nodeQuery).startsWith("[")) {
      SimpleNode rawNode = moveToAsk(nodeQuery, keys);
      if (rawMode)
        return rawNode;
      else
        return tagPrintRequests(nodeQuery, rawNode);
    } else
      return new MediaWikiPageNode(this, nodeQuery, keys);
  }

  /**
   * get the result of an ask query
   * 
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
      // make Query fit for API
      askQuery = fixAsk(askQuery);
      result = wiki.getActionResult("ask",
          "&query=" + URLEncoder.encode(askQuery, "UTF-8"));

      String json = result.getRawJson();
      if (this.isDebug())
        System.out.println(JsonPrettyPrinter.prettyPrint(json));
      // create a JsonSystem based on my graph
      JsonSystem js = new JsonSystem(this);
      js.setDebug(this.isDebug());
      js.connect("json", json);
      if (isDebug())
        js.getStartNode().forAll(SimpleNode.printDebug);
      return js.getStartNode();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * get the Concept from the given ask Query
   * 
   * @param askQuery
   * @return -the concept
   */
  public static String getConcept(String askQuery) {
    Pattern pattern = Pattern.compile("\\[\\[Concept:(.+)\\]\\]");
    Matcher matcher = pattern.matcher(askQuery);
    if (matcher.find()) {
      if (matcher.groupCount() > 0) {
        String concept = matcher.group(1);
        return concept;
      }
    }
    return null;
  }

  /**
   * tag the print Requests with the concept from the nodeQuery (if any)
   * 
   * @param nodeQuery
   * @param rawNode
   * @return
   */
  private SimpleNode tagPrintRequests(String askQuery, SimpleNode rawNode) {
    String concept = getConcept(askQuery);
    // if there is no concept we will not tag
    if (concept == null)
      return rawNode;
    this.g().V().hasLabel("printouts")
        .forEachRemaining(node -> node.property("isA", concept));
    /*
     * if (debug) { this.g().V().forEachRemaining(SimpleNode.printDebug); long
     * printOutCount =
     * this.g().V().hasLabel("printouts").count().next().longValue();
     * System.out.println("found "+printOutCount+" printouts");
     * this.g().V().hasLabel("printouts").forEachRemaining(SimpleNode.printDebug
     * ); }
     */
    return rawNode;
  }

  /**
   * fix an ask String to be useable for the API
   * 
   * @param ask
   *          - a "normal" ask query
   * @return - the fixed asked query
   */
  public static String fixAsk(String ask) {
    // ^\\s*\\{\\{
    // remove {{ with surrounding white space at beginning
    String fixedAsk = ask.replaceAll("^\\s*\\{\\{", "");
    // remove #ask:
    fixedAsk = fixedAsk.replaceAll("#ask:", "");
    // remove }} with surrounding white space at end
    fixedAsk = fixedAsk.replaceAll("\\}\\}\\s*$", "");
    // split by lines (with side effect to remove newlines)
    String[] parts = fixedAsk.split("\n");
    fixedAsk = "";
    for (String part : parts) {
      // remove whitespace around part
      part = part.trim();
      // remove whitespace around pipe sign
      part = part.replaceAll("\\s*\\|\\s*", "|");
      // remove whitespace around assignment =
      part = part.replaceAll("\\s*=\\s*", "=");
      // replace blanks with _
      part = part.replaceAll(" ","_");
      fixedAsk += part;
    }
    return fixedAsk;
  }
}
