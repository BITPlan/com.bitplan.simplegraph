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
package com.bitplan.simplegraph.wikidata;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
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
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueItemId;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleStepNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * wrap a wikiData Node
 * 
 * @author wf
 *
 */
public class WikiDataNode extends SimpleNodeImpl implements SimpleStepNode {
  private EntityDocument doc;
  /**
   * lookup maps for EntityIds
   */
  Map<String, EntityIdValue> entityIdByName = new HashMap<String, EntityIdValue>();
  Map<String, EntityIdValue> entityIdById = new HashMap<String, EntityIdValue>();
  SimpleDateFormat isoDateFormat=new SimpleDateFormat("yyyy-MM-dd");
  //SimpleDateFormat isoDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  
  /**
   * create a wiki Data node
   * 
   * @param ws
   * @param entityDocument
   */
  public WikiDataNode(WikiDataSystem ws, EntityDocument entityDocument,
      String... keys) {
    super(ws, "wikidata", keys);
    this.doc = entityDocument;
    super.setVertexFromMap();
  }

  /**
   * create me from the given map
   * 
   * @param ws
   * @param keys
   * @param entityDocument
   * @param map
   */
  public WikiDataNode(WikiDataSystem ws, String... keys) {
    super(ws, "wikidata", keys);
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
        PropertyIdValue propId = snak.getPropertyId();
        this.entityIdById.put(propId.getId(), propId);
        Value value = snak.getValue();

        // handle properties with multiple values
        // like child
        if (map.containsKey(propId.getId())) {
          // get the current property
          Object prop = map.get(propId.getId());
          // prepare for multiple entries
          List<Object> propList = null;
          // is the propery a list already?
          if (prop instanceof List) {
            propList = (List<Object>) prop;
          } else {
            // replace single element with list
            propList = new ArrayList<Object>();
            propList.add(prop);
            map.put(propId.getId(), propList);
          }
          propList.add(value);
          // System.out.println(propId);
        } else {
          // first time single value
          map.put(propId.getId(), value);
        }
        // optionally cache values
        // first the property
        SimpleNode propNode = this.getSystem().cache(propId, true);
        if (propNode != null) {
          String propLabel = propNode.getMap().get("label_en").toString();
          if (propLabel != null) {
            this.entityIdByName.put(propLabel, propId);
          }
        }
        // potentially the value
        // https://www.mediawiki.org/wiki/Wikibase/DataModel#Datatypes_and_their_Values
        // https://wikidata.github.io/Wikidata-Toolkit/org/wikidata/wdtk/datamodel/interfaces/EntityIdValue.html
        if (value instanceof ItemIdValue) {
          ItemIdValue itemIdValue = (ItemIdValue) value;
          this.entityIdById.put(itemIdValue.getId(), itemIdValue);
          boolean cacheOptional = true;
          if (keys.getKeysList().isPresent())
            cacheOptional = !keys.hasKey(propId.getId());
          SimpleNode itemNode = this.getSystem().cache(itemIdValue,
              cacheOptional);
        }
      }
    }
    return map;
  }

  /**
   * get the property value for the given key
   * 
   * @param key
   * @return - the property value
   */
  public Object getProperty(String key) {
    String propId = key;
    if (!key.startsWith("P") && entityIdByName.containsKey(key)) {
      propId = entityIdByName.get(key).getId();
    }
    Object value = map.get(propId);
    return typeConvert(value);
  }

  /**
   * convert the types according to
   * https://www.mediawiki.org/wiki/Wikibase/DataModel#Datatypes_and_their_Values
   * 
   * @param value
   * @return the converted type
   */
  public Object typeConvert(Object value) {
    if (value instanceof StringValue) {
      StringValue svalue = (StringValue) value;
      return svalue.getString();
    } else if (value instanceof TimeValue) {
      TimeValue timeValue=(TimeValue) value;
      int year=(int) timeValue.getYear();
      int month=timeValue.getMonth();
      int day=timeValue.getDay();
      int hour=timeValue.getHour();
      int minute=timeValue.getMinute();
      int sec=timeValue.getSecond();
      GregorianCalendar timeDate=new GregorianCalendar(year,month,day,hour,minute,sec);
      // TODO use timeValue.getPrecision() to select display
      return this.isoDateFormat.format(timeDate.getTime());
    } else if (value instanceof ItemIdValue) {
      ItemIdValue itemIdValue = (ItemIdValue) value;
      String itemId = itemIdValue.getId();
      String valueStr = itemId;
      SimpleNode itemNode = getSystem().cache(itemIdValue, true);
      if (itemNode != null) {
        Map<String, Object> itemMap = itemNode.getMap();
        if (itemMap.containsKey("label_en")) {
          valueStr = itemMap.get("label_en").toString()+" ("+itemId+")";
        }
      }
      return valueStr;
    }
    return value;
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
  public SimpleStepNode getEntityNode(JacksonValueItemId itemValue) {
    SimpleStepNode node = (SimpleStepNode) this.getSystem().moveTo(itemValue.getId(),keys.getKeys());
    return node;
  }

  @Override
  public Stream<SimpleStepNode> out(String edgeName) {
    List<SimpleStepNode> outs = new ArrayList<SimpleStepNode>();
    String propertyId = edgeName;
    if (!edgeName.matches("P[0-9]+")) {
      EntityIdValue entityIdValue = this.entityIdByName.get(edgeName);
      if (entityIdValue != null) {
        propertyId = entityIdValue.getId();
      }
    }
    if (map.containsKey(propertyId)) {
      Object outValue = map.get(propertyId);
      if (outValue instanceof JacksonValueItemId) {
        outs.add(getEntityNode((JacksonValueItemId) outValue));
      } else {
        @SuppressWarnings("unchecked")
        List<JacksonValueItemId> entityValues = (List<JacksonValueItemId>) outValue;
        entityValues
            .forEach(entityValue -> outs.add(getEntityNode(entityValue)));
      }
    }
    return outs.stream();
  }

  @Override
  public Stream<SimpleStepNode> in(String edgeName) {
    return null;
  }
  
  

  // show name values
  public void printNameValues(PrintStream out) {
    Map<String, Object> map = this.getMap();
    for (String key : map.keySet()) {
      if (this.keys.hasKey(key)) {
        String label = key;
        EntityIdValue entityId = this.entityIdById.get(key);
        if (entityId != null) {
          SimpleNode entityNode = getSystem().cache(entityId, true);
          if (entityNode != null) {
            label = entityNode.getMap().get("label_en").toString();
          }
        }
        String valueStr=this.getProperty(key).toString();
        out.println(
            String.format("%s (%s) = %s", label, key, valueStr));
      }
    }
  }
}