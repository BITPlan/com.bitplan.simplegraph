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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.bitplan.triplestore.TripleStoreSystem;

public class TestTripleStore extends BaseTest {

  @Test
  // https://de.wikipedia.org/wiki/Matching_(Graphentheorie)
  public void testReadTree() throws Exception {
    debug = true;
    SimpleSystem royal92 = new TripleStoreSystem();
    // read the SiDIF file for Royal 92 GEDCOM conversion
    royal92.connect("src/test/resources/sidif/royal92.sidif");
    // start with Queen Victoria (Person Id=I1)
    SimpleNode queenVictoria = royal92.moveTo("id=I1");
    if (debug) {
      queenVictoria.printNameValues(System.out);
      System.out.println();
    }
    // Queen Victoria is parent of Family F1
    assertEquals("F1", queenVictoria.getMap().get("parentOf"));
    assertEquals("F42", queenVictoria.getMap().get("childOf"));

    // get Queen Victoria's family (qvf)
    SimpleNode qvf = queenVictoria.out("parentOf").findFirst().get();
    if (debug) {
      qvf.printNameValues(System.out);
      System.out.println();
    }
    assertEquals("1840", qvf.getMap().get("yearMarried"));
    
    // get Queen Victoria's children  (qvc)
    // https://stackoverflow.com/a/15458176/1497139
    List<SimpleNode> qvc = qvf.in("childOf").collect(Collectors.toCollection(ArrayList::new));
    if (debug) {
      qvc.forEach(child -> child.printNameValues(System.out));
    }
    assertEquals(9, qvc.size());
  }

}
