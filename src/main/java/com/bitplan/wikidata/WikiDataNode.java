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

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;

import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * wrap a wikiData Node
 * @author wf
 *
 */
public class WikiDataNode extends SimpleNodeImpl {
  private EntityDocument doc;

  /**
   * create a wiki Data node
   * 
   * @param ws
   * @param entityDocument
   */
  public WikiDataNode(WikiDataSystem ws, EntityDocument entityDocument) {
    super(ws,"wikidata");
    this.doc = entityDocument;
    super.setVertexFromMap();
  }

  @Override
  public Map<String, Object> initMap() {
    if (doc instanceof StatementDocument) {
      StatementDocument sdoc = ((StatementDocument) doc);

      // loop over all statements
      Iterator<Statement> siterator = sdoc.getAllStatements();
      // move over all statements
      while (siterator.hasNext()) {
        // get the current statement
        Statement s = siterator.next();
        // get the claim of the Statement
        Claim claim = s.getClaim();
        EntityIdValue subject = claim.getSubject(); // Subject
        Snak snak = claim.getMainSnak();
        map.put(snak.getPropertyId().getId(), snak.getValue());
      }
    }
    return map;
  }

  @Override
  public Stream<SimpleNode> out(String edgeName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Stream<SimpleNode> in(String edgeName) {
    // TODO Auto-generated method stub
    return null;
  }
}