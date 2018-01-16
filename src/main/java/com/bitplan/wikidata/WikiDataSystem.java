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

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

/**
 * wrap WikiData as a system
 * @author wf
 *
 */
public class WikiDataSystem extends SimpleSystemImpl {
  transient WikibaseDataFetcher wbdf;

  @Override
  public SimpleNode moveTo(String entityId) throws Exception {
    EntityDocument entityDocument = wbdf.getEntityDocument(entityId);
    if (entityDocument == null)
      throw new IllegalArgumentException("Entity Document for " + entityId + " not found");
    return new WikiDataNode(this, entityDocument);
  }

  /**
   * create a new WikiDataSystem
   */
  public WikiDataSystem() {
    super.setName("WikiData");
    super.setName("0.0.8");
  }

  @Override
  public SimpleSystem connect(String connectionString) throws Exception {
    wbdf = WikibaseDataFetcher.getWikidataDataFetcher();
    return this;
  }

}