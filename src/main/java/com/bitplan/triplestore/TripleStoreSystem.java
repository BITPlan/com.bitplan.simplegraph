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

import java.io.File;

import org.sidif.triple.Triple;
import org.sidif.triple.TripleQuery;
import org.sidif.triple.TripleStore;
import org.sidif.util.TripleStoreBuilder;

import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

public class TripleStoreSystem extends SimpleSystemImpl {
  TripleStore tripleStore;
  TripleQuery query;

  @Override
  public SimpleNode moveTo(String nodeQuery, String ...keys) {
    String[] parts = nodeQuery.split("=");
    String predicate = parts[0];
    String object = parts[1];
    Triple triple = query.selectSingle(null, predicate, object);
    SimpleNode node = new TripleNode(this, triple, keys);
    return node;
  }

  @Override
  public SimpleSystem connect(String ... params) throws Exception {
    if (params.length!=1)
      throw new IllegalArgumentException("only one connection parameter (path to SiDIF file) allowed at this time");
    File sidifFile = new File(params[0]);
    tripleStore = TripleStoreBuilder.fromFile(sidifFile);
    query = tripleStore.query();
    return this;
  }

  public TripleStoreSystem() {
    super.setName("SiDIF TripleStore");
    super.setVersion("0.0.8");
  }

}

