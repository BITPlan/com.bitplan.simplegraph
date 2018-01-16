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
package com.bitplan.simplegraph;

import static org.junit.Assert.*;

import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.junit.Ignore;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.bitplan.wikidata.WikiDataSystem;

/**
 * test access to WikiData toolkit
 * 
 * @author wf
 *
 */
public class TestWikiData extends BaseTest {

  private SimpleNode queenVictoria;
  private WikiDataSystem wikiDataSystem;

  @Test
  public void testWikiDatabaseFetcher() throws MediaWikiApiErrorException {
    // debug=true;
    WikibaseDataFetcher wbdf = WikibaseDataFetcher.getWikidataDataFetcher();
    if (debug)
      System.out.println("*** Fetching data for entity Q42:");
    EntityDocument q42 = wbdf.getEntityDocument("Q42");
    assertNotNull(q42);
    if (debug) {
      System.out.println(String.format("id: %s, rev: %s",
          q42.getEntityId().toString(), q42.getRevisionId()));
    }
    // <http://www.wikidata.org/entity/Q42 (item)>
    assertEquals("http://www.wikidata.org/entity/Q42 (item)",
        q42.getEntityId().toString());
  }

  /**
   * get the WikiData node for QueenVictoria as a SimpleNode
   * 
   * @return
   * @throws Exception
   */
  public SimpleNode getQueenVictoria() throws Exception {
    if (queenVictoria == null) {
      wikiDataSystem = new WikiDataSystem();
      wikiDataSystem.connect("");
      queenVictoria = wikiDataSystem.moveTo("Q9439");
    }
    return queenVictoria;
  }

  /**
   * test that we can lookup property names from a property cache
   * @throws Exception 
   */
  @Ignore
  public void testPropertyCache() throws Exception {
    
    queenVictoria=this.getQueenVictoria();
    queenVictoria.getVertex().properties().forEachRemaining(prop->{
      System.out.println(String.format("%s=%s", prop.label(),prop.value().toString()));
      SimpleNode propNode;
      try {
        propNode = wikiDataSystem.moveTo(prop.label());
        queenVictoria.getVertex().addEdge("myProperty", propNode.getVertex());
        propNode.printNameValues(System.out);
      } catch (Exception e) {
        // lambdas and exceptions - oh no ...
      }
    });
    wikiDataSystem.graph().io(IoCore.graphml()).writeGraph("QueenVictoriaWikiData.xml");
  }

  @Test
  public void testQueenVictoria() throws Exception {
    debug = true;
    queenVictoria=this.getQueenVictoria();
    if (debug)
      queenVictoria.printNameValues(System.out);
  }
  
  @Test
  public void testUnknownEntityId()  {
    wikiDataSystem = new WikiDataSystem();
    Exception foundException=null;
    try {
      wikiDataSystem.connect("");
      queenVictoria = wikiDataSystem.moveTo("Q6");
    } catch (Exception e) {
      foundException=e;
    }
    assertNotNull(foundException);
    assertTrue(foundException instanceof java.lang.IllegalArgumentException);
    assertEquals("Entity Document for Q6 not found",foundException.getMessage());
  }

}
