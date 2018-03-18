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
package com.bitplan.simplegraph.smw;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import com.bitplan.mediawiki.japi.SSLWiki;
import com.bitplan.mediawiki.japi.api.Api;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.Holder;
import com.bitplan.simplegraph.json.JsonPrettyPrinter;
import com.bitplan.simplegraph.json.JsonSystem;
import com.bitplan.simplegraph.map.MapNode;
import com.bitplan.simplegraph.mediawiki.MediaWikiPageNode;
import com.bitplan.simplegraph.mediawiki.MediaWikiSystem;

import gov.nasa.worldwind.geom.Angle;

/**
 * Semantic MediaWiki system wrapper
 * 
 * @author wf
 *
 */
public class SmwSystem extends MediaWikiSystem {
  // shall we return the json result as a graph (raw mode)?
  boolean rawMode = false;
  private String json;
  private JsonSystem js;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.smwsystem");

  public String getJson() {
    return json;
  }

  protected void setJson(String json) {
    this.json = json;
  }

  @Override
  public SimpleSystem connect(String... connectionParams) throws Exception {
    if ((connectionParams.length >= 4) && ("raw".equals(connectionParams[4])))
      rawMode = true;
    return super.connect(connectionParams);
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    SimpleNode result = null;
    String mode = getPatternMatchGroup("^(.+?)=", nodeQuery, 1);
    // remove the "<mode>=" part from the query if there is one
    if (mode != null)
      nodeQuery = nodeQuery.substring(mode.length() + 1);
    if ("ask".equals(mode) || fixAsk(nodeQuery).startsWith("[")) {
      JsonSystem askJson = getAskJsonResult(nodeQuery);
      if (rawMode)
        result = askJson.getStartNode();
      else
        result = conceptAlizePrintRequests(getConcept(nodeQuery), askJson);
    } else if ("browsebysubject".equals(mode)) {
      setJson(getActionJson("browsebysubject", "subject", nodeQuery));
      js = JsonSystem.of(this, getJson());
      result = js.getStartNode();
    } else if (mode == null || "page".equals(mode)) {
      result = new MediaWikiPageNode(this, nodeQuery, keys);
    } else {
      throw new IllegalArgumentException("invalid mode " + mode);
    }
    if (this.getStartNode() == null)
      this.setStartNode(result);
    return result;
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
   * @return the JsonSystem
   */ 
  protected JsonSystem getAskJsonResult(String askQuery) {
    // make Query fit for API
    askQuery = fixAsk(askQuery);
    setJson(this.getActionJson("ask", "query", askQuery));
    JsonSystem result = JsonSystem.of(null, getJson());
    return result;
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
    // non greedy search!
    // see
    // https://stackoverflow.com/questions/5319840/greedy-vs-reluctant-vs-possessive-quantifiers
    return getPatternMatchGroup("\\[\\[Concept:(.+?)\\]\\]", askQuery, 1);
  }

  /**
   * base class for SemanticMediaWiki Json serialization values
   * 
   * @author wf
   *
   */
  public static class SMWVertex {
    /**
     * assign the value of the property with the given label to the given target
     * 
     * @param vertex
     * @param label
     * @param target
     * @return the value
     */
    public Object get(Vertex vertex, String label) {
      if (vertex.property(label).isPresent()) {
        Object value = vertex.property(label).value();
        return value;
      }
      return null;
    }

    /**
     * get the string value for the given vertex and label
     * 
     * @param vertex
     * @param label
     * @return the string
     */
    public String getString(Vertex vertex, String label) {
      return (String) get(vertex, label);
    }

    /**
     * get the integer number from the given vertex with the given label
     * 
     * @param vertex
     * @param label
     * @return the Integer number or null
     */
    public Integer getInteger(Vertex vertex, String label) {
      Number number = (Number) get(vertex, label);
      if (number == null)
        return null;
      else
        return number.intValue();
    }

    /**
     * get the double number from the given vertex with the given label
     * 
     * @param vertex
     * @param label
     * @return the Double number or null
     */
    public Double getDouble(Vertex vertex, String label) {
      Number number = (Number) get(vertex, label);
      if (number == null)
        return null;
      else
        return number.doubleValue();
    }
  }

  public static class PrintRequest extends SMWVertex {
    String label;
    String key;
    String redi;
    String typeid;
    Integer mode;
    String format;

    public PrintRequest(Vertex pr) {
      label = getString(pr, "label");
      key = getString(pr, "key");
      redi = getString(pr, "redi");
      typeid = getString(pr, "typeid");
      mode = getInteger(pr, "mode");
      format = (String) get(pr, "format");
    }
  }

  public static class Qty extends SMWVertex {
    String unit;
    Double value;

    public String getUnit() {
      return unit;
    }

    public void setUnit(String unit) {
      this.unit = unit;
    }

    public Double getValue() {
      return value;
    }

    public void setValue(Double value) {
      this.value = value;
    }

    public Qty(Vertex v) {
      unit = this.getString(v, "unit");
      value = this.getDouble(v, "value");
    }

    /**
     * return the Quantity as a string
     */
    public String toString() {
      String qtyString = String.format(Locale.US, "%8.2f %s", value, unit);
      return qtyString;
    }
  }

  public static class Geo extends SMWVertex {
    Double lat;
    Double lon;

    private Angle latangle;
    private Angle lonangle;

    public Double getLat() {
      return lat;
    }

    public void setLat(Double lat) {
      this.lat = lat;
    }

    public Double getLon() {
      return lon;
    }

    public void setLon(Double lon) {
      this.lon = lon;
    }

    public Geo(Vertex v) {
      lat = this.getDouble(v, "lat");
      lon = this.getDouble(v, "lon");
      init();
    }

    public Geo(double lat, double lon) {
      this.lat = lat;
      this.lon = lon;
      init();
    }

    public void init() {
      latangle = Angle.fromDegreesLatitude(Math.abs(lat));
      lonangle = Angle.fromDegreesLongitude(Math.abs(lon));
    }

    /**
     * return the GEO coordinates
     */
    public String toString() {
      String dmsString = String.format("%s %s %s %s",
          latangle.toFormattedDMSString(), lat >= 0.0 ? "N" : "S",
          lonangle.toFormattedDMSString(), lon >= 0.0 ? "E" : "W");
      return dmsString;
    }
  }

  /**
   * a WikiPage serialization
   * 
   * @author wf
   *
   */
  public static class WikiPage extends SMWVertex {
    // see e.g. https://www.semantic-mediawiki.org/wiki/Serialization_(JSON)
    /**
     * "fulltext": "Help:Type URL", "fullurl":
     * "https://www.semantic-mediawiki.org/wiki/Help:Type_URL", "namespace": 12,
     * "exists": "1", "displaytitle": "Help:Datatype \"URL\""
     */
    boolean exists;
    String displayTitle;
    Integer namespace;
    public String fullurl;
    public String fulltext;

    public boolean isExists() {
      return exists;
    }

    public void setExists(boolean exists) {
      this.exists = exists;
    }

    public String getDisplayTitle() {
      return displayTitle;
    }

    public void setDisplayTitle(String displayTitle) {
      this.displayTitle = displayTitle;
    }

    public Integer getNamespace() {
      return namespace;
    }

    public void setNamespace(Integer namespace) {
      this.namespace = namespace;
    }

    public String getFullurl() {
      return fullurl;
    }

    public void setFullurl(String fullurl) {
      this.fullurl = fullurl;
    }

    public String getFulltext() {
      return fulltext;
    }

    public void setFulltext(String fulltext) {
      this.fulltext = fulltext;
    }

    /**
     * construct me from a vertex
     * 
     * @param wp
     */
    public WikiPage(Vertex wp) {
      displayTitle = getString(wp, "displayTitle");
      setFullurl(getString(wp, "fullurl"));
      fulltext = getString(wp, "fulltext");
      namespace = getInteger(wp, "namespace");
      exists = "1".equals(getString(wp, "exists"));
    }

    public String toString() {
      return fulltext;
    }
  }

  /**
   * tag the print Requests with the concept from the nodeQuery (if any) and
   * recreate nodes
   * 
   * @param concept
   * @param askJson.
   * @return
   */
  public SimpleNode conceptAlizePrintRequests(String concept,
      JsonSystem askJson) {
    // if there is no concept we will not tag
    if (concept == null)
      return askJson.getStartNode();
    Holder<MapNode> conceptNodeHolder = new Holder<MapNode>();
    final Map<String, PrintRequest> prMap = new HashMap<String, PrintRequest>();
    askJson.g().V().hasLabel("printrequests").forEachRemaining(pr -> {
      String label = pr.property("label").value().toString();
      PrintRequest prq = new PrintRequest(pr);
      prMap.put(label, prq);
    });
    /*
     * this.g().V().hasLabel("results").outE().forEachRemaining(edge -> { Vertex
     * jsonVertex = edge.outVertex(); JsonNode jsonNode = (JsonNode)
     * jsonVertex.property("mysimplenode") .value();
     */

    askJson.g().V().hasLabel("results").out().forEachRemaining(rNode -> {
      Map<String, Object> conceptMap = new HashMap<String, Object>();
      conceptMap.put("isA", concept);
      conceptMap.put("wikipage", new WikiPage(rNode));
      rNode.vertices(Direction.OUT, "printouts").forEachRemaining(node -> {
        try {
          for (String key : prMap.keySet()) {
            PrintRequest pr = prMap.get(key);
            if (pr != null)
              switch (pr.typeid) {
              // Annotation URI
              // https://www.semantic-mediawiki.org/wiki/Help:Type_Annotation
              // URI
              // Holds URIs, but has some technical differences during export
              // compared to the "URL" type:
              case "_anu": // Annotation URI
                putValue(node, key, conceptMap);
                break;
              // Boolean
              // https://www.semantic-mediawiki.org/wiki/Help:Type_Boolean
              // Holds boolean (true/false) values:
              case "_boo": // Boolean
                VertexProperty<Object> bProperty = node.property(key);
                if (bProperty.isPresent()) {
                  String bString = bProperty.value().toString();
                  conceptMap.put(key, "[t]".equals(bString));
                }
                break;
              // Code
              // https://www.semantic-mediawiki.org/wiki/Help:Type_Code
              // Holds technical, pre-formatted texts (similar to type Text):
              case "_cod": // Code
                putValue(node, key, conceptMap);
                break;
              // Date
              // https://www.semantic-mediawiki.org/wiki/Help:Type_Date
              // Holds particular points in time:
              case "_dat": // Date
                Iterator<Edge> datE = node.edges(Direction.OUT, key);
                if (datE.hasNext()) {
                  Vertex datV = datE.next().inVertex();
                  String dateStr = datV.property("raw").value().toString();
                  Date date = SmwSystem.getTime(dateStr);
                  conceptMap.put(key, date);
                }
                break;
              // Email
              // https://www.semantic-mediawiki.org/wiki/Help:Type_Email
              // Holds e-mail addresses:
              case "_ema": // Email
                putValue(node, key, conceptMap);
                break;
              // External identifier
              // https://www.semantic-mediawiki.org/wiki/Help:Type_External
              // identifier
              // Holds a value that associates it with with a external URI for
              // formatting:
              case "_eid": // External identifier
                putValue(node, key, conceptMap);
                break;
              // Geographic coordinate
              // https://www.semantic-mediawiki.org/wiki/Help:Type_Geographic
              // coordinate
              // Holds coordinates describing geographic locations:
              case "_geo": // Geographic coordinate
                Iterator<Edge> geoEdges = node.edges(Direction.OUT, key);
                if (geoEdges.hasNext()) {
                  Geo geo = new Geo(geoEdges.next().inVertex());
                  conceptMap.put(key, geo);
                }
                break;
              // Monolingual text
              // https://www.semantic-mediawiki.org/wiki/Help:Type_Monolingual
              // text
              // Holds a text value that associates the annotation with a
              // specific
              // language code:
              case "_mlt_rec": // Monolingual text
                break;
              // Number
              // https://www.semantic-mediawiki.org/wiki/Help:Type_Number
              // Holds integer and decimal numbers, with an optional exponent:
              case "_num": // Number
                break;
              // Page
              // https://www.semantic-mediawiki.org/wiki/Help:Type_Page
              // Holds names of wiki pages, and displays them as a link:
              case "_wpg": // Page
                Iterator<Edge> pageEdges = null;
                Vertex pageVertex=null;
                // mode 2 is for the main label
                if (pr.mode == 2) {
                  // WikiPage information is in node "above" results - we do not know the label of it
                  // but since there is only one we can safely use it anyway
                  pageEdges = node.edges(Direction.IN);
                  if (pageEdges.hasNext()) {
                    pageVertex=pageEdges.next().outVertex();
                  }
                } else {
                  // WikiPage information is in the node below us the label is in key
                  pageEdges = node.edges(Direction.OUT, key);
                  if (pageEdges.hasNext()) {
                    pageVertex=pageEdges.next().inVertex();
                  }
                }
                if (pageVertex!=null) {
                  WikiPage wikiPage = new WikiPage(pageVertex);
                  if ("".equals(key))
                    key="page"; // convention
                  conceptMap.put(key, wikiPage);
                }
                break;
              // Quantity
              // https://www.semantic-mediawiki.org/wiki/Help:Type_Quantity
              // Holds values that describe quantities, containing both a number
              // and
              // a unit:
              case "_qty": // Quantity
                Iterator<Edge> qtyEdges = node.edges(Direction.OUT, key);
                if (qtyEdges.hasNext()) {
                  Qty qty = new Qty(qtyEdges.next().inVertex());
                  conceptMap.put(key, qty);
                }
                break;
              // Record
              // https://www.semantic-mediawiki.org/wiki/Help:Type_Record
              // Holds compound property values that consist of a short list of
              // values with fixed type and order:
              case "_rec": // Record
                break;
              // Reference
              // https://www.semantic-mediawiki.org/wiki/Help:Type_Reference
              // Holds a value that associates it to individual defined
              // provenance
              // metadata record:
              case "_ref_rec": // Reference
                break;
              // Telephone number
              // https://www.semantic-mediawiki.org/wiki/Help:Type_Telephone
              // number
              // Holds international telephone numbers based on the <span
              // class="plainlinks">[https://tools.ietf.org/html/rfc3966 RFC
              // 3966]</span> standard:
              case "_tel": // Telephone number
                break;
              // Temperature
              // https://www.semantic-mediawiki.org/wiki/Help:Type_Temperature
              // Holds temperature values (similar to type Quantity):
              case "_tem": // Temperature
                break;
              // Text
              // https://www.semantic-mediawiki.org/wiki/Help:Type_Text
              // Holds text of arbitrary length:
              case "_txt": // Text
                putValue(node, key, conceptMap);
                break;
              // URL
              // https://www.semantic-mediawiki.org/wiki/Help:Type_URL
              // Holds URIs, URNs and URLs:
              case "_uri": // URL
                putValue(node, key, conceptMap);
                break;
              default:
                // unsupported type id
                LOGGER.log(Level.WARNING, "unknown typeid " + pr.typeid);
                putValue(node, key, conceptMap);
              }
          } // for all properties
          // add the concept
          conceptNodeHolder.add(new MapNode(this, concept, conceptMap));
        } catch (Throwable th) {
          LOGGER.log(Level.SEVERE, th.getMessage(), th);
        }
      });
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
   * convert an SMW timeString to a Java Date
   * 
   * @param timeString
   * @return the Date
   */
  public static Date getTime(String timeString) {
    timeString = timeString.substring(2);
    String formats[] = { "yyyy/M/d/H/m/s/S", "yyyy/M/d" };
    Date timeRawDate = null;
    for (String format : formats) {
      SimpleDateFormat sdf = new SimpleDateFormat(format);
      try {
        timeRawDate = sdf.parse(timeString);
      } catch (java.text.ParseException jtpe) {
        // ignore - just leave null
      }
      if (timeRawDate != null)
        break;
    }
    return timeRawDate;
  }

  /**
   * put the given value
   * 
   * @param node
   * @param key
   * @param conceptMap
   */
  @SuppressWarnings("rawtypes")
  public void putValue(Vertex node, String key,
      Map<String, Object> conceptMap) {
    VertexProperty<Object> prop = node.property(key);
    if (!prop.isPresent()) {
      LOGGER.log(Level.WARNING,
          String.format("property %s does not exist", key));
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
   * fix an ask String to be usable for the API
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
      // remove whitespace in query parts
      part = part.replaceAll("\\]\\s*\\[", "][");
      // replace blanks with _
      part = part.replaceAll(" ", "_");
      fixedAsk += part;
    }
    return fixedAsk;
  }
}
