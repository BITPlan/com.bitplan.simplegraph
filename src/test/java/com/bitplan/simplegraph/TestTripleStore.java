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
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;

import com.bitplan.triplestore.TripleStoreSystem;

/**
 * test the TripleStore access
 * 
 * @author wf
 *
 */
public class TestTripleStore extends BaseTest {
  static SimpleSystem royal92;

  /**
   * read the SiDIF File
   * 
   * @return
   * @throws Exception
   */
  public static SimpleSystem readSiDIF() throws Exception {
    if (royal92 == null) {
      royal92 = new TripleStoreSystem();
      // read the SiDIF file for Royal 92 GEDCOM conversion
      royal92.connect("src/test/resources/sidif/royal92.sidif");
    }
    return royal92;
  }

  /**
   * get the children for a given node
   * 
   * @param person
   * @param generations
   * @return the children
   */
  public static List<SimpleNode> children(SimpleNode person, int generations) {
    List<SimpleNode> rootChildren = new ArrayList<SimpleNode>();
    return children(rootChildren, person, generations);
  }

  /**
   * recursive move vis parentOf -> childOf
   * 
   * @param rootChildren
   * @param person
   * @param generations
   * @return the list of children
   */
  public static List<SimpleNode> children(List<SimpleNode> rootChildren,
      SimpleNode person, int generations) {
    Optional<SimpleNode> oFamily = person.out("parentOf").findFirst();
    if (oFamily.isPresent()) {
      SimpleNode family = oFamily.get();
      List<SimpleNode> personChildren = family.in("childOf")
          .collect(Collectors.toCollection(ArrayList::new));
      rootChildren.addAll(personChildren);
      if (generations > 1) {
        for (SimpleNode child : personChildren) {
          rootChildren.addAll(children(child, generations - 1));
        }
      }
    }
    return rootChildren;
  }

  @Test
  public void testReadChildren() throws Exception {
    debug = true;
    SimpleSystem royal92 = readSiDIF();
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

    // get Queen Victoria's children (qvc)
    // https://stackoverflow.com/a/15458176/1497139
    List<SimpleNode> qvc = qvf.in("childOf")
        .collect(Collectors.toCollection(ArrayList::new));
    if (debug) {
      qvc.forEach(child -> child.printNameValues(System.out));
      System.out.println(String.format("%3d children found", qvc.size()));
    }
    assertEquals(9, qvc.size());
  }

  @Test
  public void testReadTree() throws Exception {
    SimpleSystem royal92 = readSiDIF();
    // start with Queen Victoria (Person Id=I1)
    SimpleNode queenVictoria = royal92.moveTo("id=I1");
    List<SimpleNode> descendants2 = children(queenVictoria, 2);
    if (debug) {
      descendants2.forEach(d -> d.printNameValues(System.out));
      System.out.println(
          String.format("%3d grandchildren found", descendants2.size()));
    }
    assertEquals(49, descendants2.size());
    List<SimpleNode> descendants7 = children(queenVictoria, 7);
    if (debug)
      System.out
          .println(String.format("%4d descendants found", descendants7.size()));
    assertEquals(294,descendants7.size());
  }
  // TODO // https://de.wikipedia.org/wiki/Matching_(Graphentheorie)
}
