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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

/**
 * wrap WikiData as a system
 * 
 * @author wf
 *
 */
public class WikiDataSystem extends SimpleSystemImpl {
  transient WikibaseDataFetcher wbdf;
  List<String> languages = new ArrayList<String>();
  transient Map<String,Cache> cacheMap=new HashMap<String,Cache>();
  
  class Cache {
    File file;
    String purpose;
    WikiDataSystem wds;
    /**
     * create a cache with the given file for the given purpose
     * @param file
     * @param purpose
     */
    public Cache(WikiDataSystem wds,File file, String purpose) {
      this.wds=wds;
      this.file=file;
      this.purpose=purpose;
    }
    
    /**
     * flush the cache
     * @throws Exception
     */
    public void flush() throws Exception {
      wds.graph().io(IoCore.graphml()).writeGraph(file.getPath());
    }

    /**
     * reinitialize the cache
     * @throws Exception
     */
    public void reinit() throws Exception {
     // read it
      wds.graph().io(IoCore.graphml()).readGraph(file.getPath());
      // simple nodes references are not o.k.
      // <data
      // key="mysimplenode">com.bitplan.wikidata.WikiDataNode@7905a0b8</data>
      wds.graph().traversal().V().has("mysimplenode").forEachRemaining(node -> {
        Object simpleNodeObject = node.property("mysimplenode").value();
        if (simpleNodeObject instanceof String) {
          Map<String, Object> map = new HashMap<String, Object>();
          node.properties().forEachRemaining(nodeprop -> {
            map.put(nodeprop.key(), nodeprop.value());
          });
          WikiDataNode wikiDataNode = new WikiDataNode(wds);
          wikiDataNode.setVertex(node);
          wikiDataNode.setMap(map);
          map.put("mysimplenode", wikiDataNode);
          node.property("mysimplenode", wikiDataNode);
        }
      });
      
    }
  }
 

  @Override
  public SimpleNode moveTo(String entityId) {
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
    WikiDataNode node = new WikiDataNode(this, entityDocument);
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
    this(null,languageCodes);
  }
  
  /**
   * create a WikiDataSystem with a given fetcher
   * @param wbdf
   * @param languageCodes
   */
  private WikiDataSystem(WikibaseDataFetcher wbdf,String... languageCodes) {
    super.setName("WikiData");
    super.setVersion("0.0.8");
    this.wbdf=wbdf;
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
  public Cache useCache(File cacheFile,String purpose) throws Exception {
    // create a new WikiDataSystem based on the same fetcher
    WikiDataSystem cachews = new WikiDataSystem(wbdf,languages.toArray(new String[0]));
    Cache cache=new Cache(cachews,cacheFile,purpose);
    cacheMap.put(purpose, cache);
    // is there propertyCacheFile?
    if (cacheFile.exists()) {
      cache.reinit();
    }
    return cache;
  }

  /**
   * flush the cache for the given purpose
   * @param purpose
   * @throws Exception
   */
  public void flushCache(String purpose) throws Exception {
    if (!cacheMap.containsKey(purpose))
      throw new IllegalArgumentException("no cache for purpose "+purpose+" in use");
    cacheMap.get(purpose).flush();   
  }

  /**
   * cache the property with the given id
   * 
   * @param propId
   * @param level
   */
  public SimpleNode cache(String itemId, boolean optional) {
    if (!itemId.matches("[QP][0-9]+")) {
      return null;
    }
    String purpose="property";
    if (itemId.startsWith("Q"))
       purpose="entity";
    if (!cacheMap.containsKey(purpose))
      return null;
    Cache cache=cacheMap.get(purpose);
    GraphTraversal<Vertex, Vertex> propVertex = cache.wds.g().V().has("wikidata_id",
        itemId);
    SimpleNode node = null;
    if (propVertex.hasNext()) {
      node = propVertex.next().value("mysimplenode");
    } else {
      if (!optional)
        node = cache.wds.moveTo(itemId);
    }
    return node;
  }

}