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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import com.bitplan.json.JsonPrettyPrinter;
import com.bitplan.json.JsonSystem;
import com.bitplan.map.MapNode;
import com.bitplan.mediawiki.MediaWikiPageNode;
import com.bitplan.mediawiki.MediaWikiSystem;
import com.bitplan.mediawiki.japi.SSLWiki;
import com.bitplan.mediawiki.japi.api.Api;
import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.SimpleSystem;
import com.bitplan.simplegraph.impl.Holder;

/**
 * Semantic MediaWiki system wrapper
 * 
 * @author wf
 *
 */
public class SMWSystem extends MediaWikiSystem {
  // shall we return the json result as a graph (raw mode)?
  boolean rawMode = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.smwsystem");

  @Override
  public SimpleSystem connect(String... connectionParams) throws Exception {
    if ((connectionParams.length >= 4) && ("raw".equals(connectionParams[4])))
      rawMode = true;
    return super.connect(connectionParams);
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    String mode = getPatternMatchGroup("^(.+?)=", nodeQuery, 1);
    // remove the "<mode>=" part from the query if there is one
    if (mode != null)
      nodeQuery = nodeQuery.substring(mode.length() + 1);
    if ("ask".equals(mode) || fixAsk(nodeQuery).startsWith("[")) {
      SimpleNode rawNode = moveToAsk(nodeQuery, keys);
      if (rawMode)
        return rawNode;
      else
        return conceptAlizePrintRequests(getConcept(nodeQuery), rawNode);
    } else if ("browsebysubject".equals(mode)) {
      String json = getActionJson("browsebysubject", "subject", nodeQuery);
      JsonSystem js = JsonSystem.of(this, json);
      return js.getStartNode();
    } else if (mode == null || "page".equals(mode)) {
      return new MediaWikiPageNode(this, nodeQuery, keys);
    } else {
      throw new IllegalArgumentException("invalid mode " + mode);
    }
  }

  /**
   * get the json result for the given action
   * 
   * @param action
   * @param actionQuery
   * @return the raw json string
   * @throws Exception
   * @throws UnsupportedEncodingException
   */
  public String getActionJson(String action, String param, String actionQuery) {
    SSLWiki wiki = getWiki();
    wiki.setFormat("json");
    wiki.setDebug(isDebug());
    Api result;
    try {
      result = wiki.getActionResult(action,
          "&" + param + "=" + URLEncoder.encode(actionQuery, "UTF-8"));

      String json = result.getRawJson();
      if (this.isDebug())
        System.out.println(JsonPrettyPrinter.prettyPrint(json));
      return json;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * get the result of an ask query
   * 
   * @param askQuery
   * @param keys
   * @return
   */
  private SimpleNode moveToAsk(String askQuery, String[] keys) {
    // make Query fit for API
    askQuery = fixAsk(askQuery);
    String json = this.getActionJson("ask", "query", askQuery);
    JsonSystem js = JsonSystem.of(this, json);
    return js.getStartNode();
  }

  /**
   * get the group with the given index in the given regular expression when
   * matched against the given string toMatch
   * 
   * @param regex
   *          - the regular expession
   * @param toMatch
   *          - the String to match
   * @param groupIndex
   *          - the index of the group to fetch
   * @return - the result or null if there is no such group
   */
  public static String getPatternMatchGroup(String regex, String toMatch,
      int groupIndex) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(toMatch);
    if (matcher.find()) {
      if (matcher.groupCount() > 0) {
        String concept = matcher.group(groupIndex);
        return concept;
      }
    }
    return null;
  }

  /**
   * get the Concept from the given ask Query
   * 
   * @param askQuery
   * @return -the concept
   */
  public static String getConcept(String askQuery) {
    return getPatternMatchGroup("\\[\\[Concept:(.+)\\]\\]", askQuery, 1);
  }

  class PrintRequest {
    String label;
    String key;
    String redi;
    String typeid;
    Integer mode;
    String format;

    /**
     * assign the value of the property with the given label to the given target
     * 
     * @param pr
     * @param label
     * @param target
     * @return the value
     */
    public Object get(Vertex pr, String label) {
      if (pr.property(label).isPresent()) {
        Object value = pr.property(label).value();
        return value;
      }
      return null;
    }

    public PrintRequest(Vertex pr) {
      label = (String) get(pr, "label");
      key = (String) get(pr, "key");
      redi = (String) get(pr, "redi");
      typeid = (String) get(pr, "typeid");
      Number modeNumber = (Number) get(pr, "mode");
      if (modeNumber != null)
        mode = modeNumber.intValue();
      format = (String) get(pr, "format");
    }
  }

  /**
   * tag the print Requests with the concept from the nodeQuery (if any) and
   * recreate nodes
   * 
   * @param concept
   * @param rawNode
   * @return
   */
  @SuppressWarnings("rawtypes")
  public SimpleNode conceptAlizePrintRequests(String concept,
      SimpleNode rawNode) {
    // if there is no concept we will not tag
    if (concept == null)
      return rawNode;
    Holder<MapNode> conceptNodeHolder = new Holder<MapNode>();
    final Map<String, PrintRequest> prMap = new HashMap<String, PrintRequest>();
    this.g().V().hasLabel("printrequests").forEachRemaining(pr -> {
      String label = pr.property("label").value().toString();
      prMap.put(label, new PrintRequest(pr));
    });
    this.g().V().hasLabel("printouts").forEachRemaining(node -> {
      Map<String, Object> conceptMap = new HashMap<String, Object>();
      conceptMap.put("isA", concept);
      for (String key : prMap.keySet()) {
        PrintRequest pr = prMap.get(key);
        if (pr != null)
          switch (pr.typeid) {
          case "_wpg":
            
            break;
          case "_txt":
            putValue(node, key, conceptMap);
            break;
          default:
            // unsupported type id
            LOGGER.log(Level.WARNING, "unknown typeid " + pr.typeid);
            putValue(node, key, conceptMap);
          }
      }
      ;
      conceptNodeHolder.add(new MapNode(this, concept, conceptMap));
    });
    /*
     * if (debug) { this.g().V().forEachRemaining(SimpleNode.printDebug); long
     * printOutCount =
     * this.g().V().hasLabel("printouts").count().next().longValue();
     * System.out.println("found "+printOutCount+" printouts");
     * this.g().V().hasLabel("printouts").forEachRemaining(SimpleNode.printDebug
     * ); }
     */
    return conceptNodeHolder.getFirstValue();
  }

  /**
   * put the given value
   * @param node
   * @param key
   * @param conceptMap
   */
  public void putValue(Vertex node, String key,
      Map<String, Object> conceptMap) {
    VertexProperty<Object> prop = node.property(key);
    if (!prop.isPresent()) {
      LOGGER.log(Level.WARNING,String.format("proprerty %s does not exist",key));
    } else {
      Object value = prop.value();
      if (value instanceof ArrayList) {
        ArrayList objectArray = (ArrayList) value;
        if (objectArray.size() == 1) {
          conceptMap.put(key, objectArray.get(0));
        } else {
          conceptMap.put(key, objectArray);
        }
      } else {
        conceptMap.put(key, value);
      }
    }
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
      part = part.replaceAll(" ", "_");
      fixedAsk += part;
    }
    return fixedAsk;
  }
}
