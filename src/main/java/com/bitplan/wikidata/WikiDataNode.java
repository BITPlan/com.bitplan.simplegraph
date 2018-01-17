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
package com.bitplan.wikidata;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueItemId;

import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * wrap a wikiData Node
 * 
 * @author wf
 *
 */
public class WikiDataNode extends SimpleNodeImpl {
  private EntityDocument doc;
  Map<String, String> propIdByName = new HashMap<String, String>();

  /**
   * create a wiki Data node
   * 
   * @param ws
   * @param entityDocument
   */
  public WikiDataNode(WikiDataSystem ws, EntityDocument entityDocument) {
    super(ws, "wikidata");
    this.doc = entityDocument;
    super.setVertexFromMap();
  }

  /**
   * create me from the given map
   * 
   * @param ws
   * @param map
   */
  public WikiDataNode(WikiDataSystem ws) {
    super(ws, "wikidata");
  }

  protected WikiDataSystem getSystem() {
    return (WikiDataSystem) super.simpleGraph;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Object> initMap() {
    DataObjectFactoryImpl df = new DataObjectFactoryImpl();
    if (doc instanceof TermedDocument) {
      TermedDocument tdoc = ((TermedDocument) doc);
      for (String languageCode : getSystem().getLanguages()) {
        MonolingualTextValue langlabel = tdoc.getLabels().get(languageCode);
        if (langlabel != null) {
          String langValue = df
              .getStringValue(tdoc.getLabels().get(languageCode).getText())
              .getString();
          map.put("label_" + languageCode, langValue);
        }
      }
    }
    if (doc instanceof ItemDocument) {
      ItemDocument idoc = ((ItemDocument) doc);
      Map<String, SiteLink> sitelinks = idoc.getSiteLinks();
      for (String languageCode : getSystem().getLanguages()) {
        SiteLink languageWikiLink = sitelinks.get(languageCode + "wiki");
        if (languageWikiLink != null) {
          map.put("wiki_" + languageCode,
              df.getStringValue(languageWikiLink.getPageTitle()));
        }
      }
    }

    if (doc instanceof StatementDocument) {
      StatementDocument sdoc = ((StatementDocument) doc);

      // loop over all statements
      Iterator<Statement> siterator = sdoc.getAllStatements();
      EntityIdValue subject = null;
      // move over all statements
      while (siterator.hasNext()) {
        // get the current statement
        Statement s = siterator.next();
        // get the claim of the Statement
        Claim claim = s.getClaim();
        if (subject == null) {
          subject = claim.getSubject(); // Subject - TODO - check under which
                                        // circumstances there might be
                                        // different ones
          map.put("wikidata_id", subject.getId());
          map.put("wikidata_type", subject.getEntityType());
        }
        Snak snak = claim.getMainSnak();
        String propId = snak.getPropertyId().getId();
        Value value = snak.getValue();

        // handle properties with multiple values
        // like child
        if (map.containsKey(propId)) {
          // get the current property
          Object prop = map.get(propId);
          // prepare for multiple entries
          List<Object> propList = null;
          // is the propery a list already?
          if (prop instanceof List) {
            propList = (List<Object>) prop;
          } else {
            // replace single element with list
            propList = new ArrayList<Object>();
            propList.add(prop);
            map.put(propId, propList);
          }
          propList.add(value);
          // System.out.println(propId);
        } else {
          // first time single value
          map.put(propId, value);
        }
        SimpleNode propNode = this.getSystem().cacheProperty(propId, true);
        if (propNode != null) {
          String propLabel = propNode.getMap().get("label_en").toString();
          if (propLabel != null) {
            this.propIdByName.put(propLabel, propId);
          }
        }
      }
    }
    return map;
  }

  /**
   * get the Wikidata entity id
   * 
   * @param entityIri
   * @return - the entityId
   */
  public String getWikiDataEntityId(String entityIri) {
    entityIri = entityIri.replace("http://www.wikidata.org/entity/", "");
    entityIri = entityIri.replace(" (item)", "");
    return entityIri;
  }

  /*
   * public SimpleNode getEntityNode(String entityIri) { SimpleNode node =
   * this.getSystem().moveTo(this.getWikiDataEntityId(entityIri)); return node;
   * }
   */

  /**
   * get the entity node for the given value
   * 
   * @param itemValue
   * @return the entity Node
   */
  public SimpleNode getEntityNode(JacksonValueItemId itemValue) {
    SimpleNode node = this.getSystem().moveTo(itemValue.getId());
    return node;
  }

  @Override
  public Stream<SimpleNode> out(String edgeName) {
    List<SimpleNode> outs = new ArrayList<SimpleNode>();
    String propertyId = edgeName;
    if (!edgeName.matches("P[0-9]+")) {
      propertyId=this.propIdByName.get(edgeName);
    }
    Object outValue = map.get(propertyId);
    if (outValue instanceof JacksonValueItemId) {
      outs.add(getEntityNode((JacksonValueItemId) outValue));
    } else {
      @SuppressWarnings("unchecked")
      List<JacksonValueItemId> entityValues = (List<JacksonValueItemId>) outValue;
      entityValues.forEach(entityValue -> outs.add(getEntityNode(entityValue)));
    }
    return outs.stream();
  }

  @Override
  public Stream<SimpleNode> in(String edgeName) {
    // TODO Auto-generated method stub
    return null;
  }

  // show name values
  public void printNameValues(PrintStream out) {
    Map<String, Object> map = this.getMap();
    for (String key : map.keySet()) {
      String label = key;
      SimpleNode propNode = getSystem().cacheProperty(key, true);
      if (propNode != null) {
        label = propNode.getMap().get("label_en").toString();
      }
      out.println(String.format("%s (%s) = %s", label, key, map.get(key)));
    }
  }
}