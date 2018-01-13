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
package com.bitplan.triplestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.sidif.triple.Triple;
import org.sidif.triple.TripleQuery;

import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

public class TripleNode extends SimpleNodeImpl {
  TripleStoreSystem system;
  Triple triple;

  /**
   * get the triplenode
   * 
   * @param system
   * @param triple
   */
  public TripleNode(TripleStoreSystem system, Triple triple) {
    super(system);
    this.system = system;
    this.triple = triple;
  }

  @Override
  public Map<String, Object> getMap() {
    Map<String, Object> map = new HashMap<String, Object>();
    List<Triple> triples = system.query.select(triple.getSubject(), null, null);
    for (Triple triple : triples) {
      Object value = triple.getObject();
      // FIXME - value might have a type
      map.put(triple.getPredicate().toString(), value.toString());
    }
    return map;
  }

  @Override
  public Stream<SimpleNode> out(String edgeName) {
    // translate the graph out vertice step to a triple query
    List<Triple> triples = system.query.select(triple.getSubject(), edgeName,
        null);
    return triplesAsStream(triples, EdgeDirection.OUT);
  }

  @Override
  public Stream<SimpleNode> in(String edgeName) {
    // translate the graph in vertice step to a triple query
    List<Triple> triples = system.query.select(null, edgeName,
        triple.getSubject());
    return triplesAsStream(triples, EdgeDirection.IN);
  }

  /**
   * get the stream of SimpleNodes for the given triples
   * 
   * @param triples
   * @param edgeDirection
   * @return the stream of SimpleNodes
   */
  protected Stream<SimpleNode> triplesAsStream(List<Triple> triples,
      EdgeDirection edgeDirection) {
    List<SimpleNode> nodes = new ArrayList<SimpleNode>();
    for (Triple triple : triples) {
      Triple subject = null;
      switch (edgeDirection) {
      case IN:
        subject = triple;
        break;
      case OUT:
        Object edgeTarget = triple.getObject();
        TripleQuery query = system.tripleStore.query();
        subject = query.selectSingle(edgeTarget.toString(), "isA", null);
        break;
      case BOTH:
        throw new IllegalStateException("BOTH not supported yet");
      }
      if (subject != null)
        nodes.add(new TripleNode(system, subject));
    }
    return nodes.stream();
  }
}
