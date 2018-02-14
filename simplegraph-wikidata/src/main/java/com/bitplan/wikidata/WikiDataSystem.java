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

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.wikidata.wdtk.datamodel.implementation.ItemIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.PropertyIdValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.Cache;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

/**
 * wrap WikiData as a system
 * 
 * @author wf
 *
 */
public class WikiDataSystem extends SimpleSystemImpl {
  public static final String PURPOSE_PROPERTY = "http://www.wikidata.org/ontology#Property";
  public static final String PURPOSE_ITEM = "http://www.wikidata.org/ontology#Item";
  transient WikibaseDataFetcher wbdf;
  List<String> languages = new ArrayList<String>();

  public static String siteiri = "http://www.wikidata.org/entity/";
  transient static boolean debug = true;
  transient protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.wikidata");

  @Override
  public SimpleNode moveTo(String entityId, String... keys) {
    if (wbdf == null)
      throw new IllegalStateException("not connected");
    EntityDocument entityDocument;
    try {
      entityDocument = wbdf.getEntityDocument(entityId);
    } catch (MediaWikiApiErrorException e) {
      throw new RuntimeException(e);
    }
    if (entityDocument == null)
      throw new IllegalArgumentException(
          "Entity Document for " + entityId + " not found");
    WikiDataNode node = new WikiDataNode(this, entityDocument, keys);
    if (this.getStartNode() == null)
      this.setStartNode(node);
    return node;
  }

  /**
   * create a new default WikiDataSystem using english as the language
   */
  public WikiDataSystem() {
    this("en");
  }

  /**
   * create a new WikiDataSystem using the given languages
   * 
   * @param languages
   */
  public WikiDataSystem(String... languageCodes) {
    this(null, languageCodes);
  }

  /**
   * create a WikiDataSystem with a given fetcher
   * 
   * @param wbdf
   * @param languageCodes
   */
  private WikiDataSystem(WikibaseDataFetcher wbdf, String... languageCodes) {
    super.setName("WikiData");
    super.setVersion("0.0.8");
    this.wbdf = wbdf;
    for (String languageCode : languageCodes) {
      addLanguage(languageCode);
    }
  }

  @Override
  public SimpleSystem connect(String... connectionParams) throws Exception {
    wbdf = WikibaseDataFetcher.getWikidataDataFetcher();
    return this;
  }

  public void addLanguage(String languageCode) {
    languages.add(languageCode);
  }

  public List<String> getLanguages() {
    return languages;
  }

  /**
   * enable the use of a cache for the given purpose
   * 
   * @param cacheFile
   * @param purpose
   * @return
   * @throws Exception
   */
  public Cache useCache(File cacheFile, String purpose) throws Exception {
    // create a new WikiDataSystem based on the same fetcher
    WikiDataSystem cachews = new WikiDataSystem(wbdf,
        languages.toArray(new String[0]));
    Cache cache = new Cache(cachews, cacheFile, purpose);
    cacheMap.put(purpose, cache);
    // is there propertyCacheFile?
    if (cacheFile.exists()) {
      cache.reinit();
    }
    return cache;
  }

  

  /**
   * cache the entity with the given id
   * 
   * @param entityId
   *          - Property or Item
   * @param level
   */
  public SimpleNode cache(EntityIdValue entityId, boolean optional) {
    SimpleNode node = null;
    String purpose = entityId.getEntityType();
    // do we have a cache for the given purpose?
    if (cacheMap.containsKey(purpose)) {
      Cache cache = cacheMap.get(purpose);
      String wikidata_id=entityId.getId();
      // try to find the
      GraphTraversal<Vertex, Vertex> propVertex = cache.getSystem().g().V()
          .has("wikidata_id", wikidata_id);
      if (propVertex.hasNext()) {
        node = propVertex.next().value("mysimplenode");
      } else {
        // no cache entry
        // if the caching is non optional get the value
        if (!optional) {
          node = cache.getSystem().moveTo(entityId.getId());
          if (debug)
            LOGGER.log(Level.INFO, String.format("caching %s=%s",
                entityId.getIri(), node.getMap().get("label_en")));
        }
      }
    }
    return node;
  }

  /**
   * create an EntityIdValue
   * 
   * @param id
   *          - the id to create the entityId value for
   * @return the EntityId Value
   */
  public static Optional<EntityIdValue> createEntityIdValue(String id) {
    if (!id.matches("^[QP][0-9]+$")) {
      return Optional.empty();
    }
    // https://wikidata.github.io/Wikidata-Toolkit/org/wikidata/wdtk/datamodel/implementation/PropertyIdValueImpl.html
    // https://stackoverflow.com/a/2599471/1497139
    try {
      Constructor<? extends EntityIdValue> constructor;
      Class<? extends EntityIdValue> clazz=ItemIdValueImpl.class;
      if (id.startsWith("P")) {
        clazz=PropertyIdValueImpl.class;
      };
      constructor=clazz.getDeclaredConstructor(String.class,String.class);
      constructor.setAccessible(true);      
      EntityIdValue idValue = constructor.newInstance(id, siteiri);
      return Optional.of(idValue);
    } catch (Throwable th) {
      throw new RuntimeException(th);
    }
  }

  /**
   * get a cache result by id
   * 
   * @param id
   *          - a property or item id
   * @param optional
   * @return the node
   */
  public Optional<SimpleNode> cache(String id, boolean optional) {
    Optional<EntityIdValue> idValue = createEntityIdValue(id);
    if (idValue.isPresent()) {
      SimpleNode node = this.cache(idValue.get(), optional);
      return Optional.of(node);
    } else {
      return Optional.empty();
    }
  }
  
  @Override 
  public Class<? extends SimpleNode> getNodeClass() {
    return WikiDataNode.class;
  }

}