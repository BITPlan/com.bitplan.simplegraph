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
package com.bitplan.simplegraph.carddav;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;

import ezvcard.VCard;

/**
 * test the CardDavSysstem
 * 
 * @author wf
 *
 */
public class TestCarddavSystem {
  static boolean debug=false;
  /**
   * test vcards from https://www.w3.org/2002/12/cal/vcard-examples/
   */

  /**
   * BEGIN:VCARD VERSION:3.0 N:Doe;John;;; FN:John Doe ORG:Example.com Inc.;
   * TITLE:Imaginary test person
   * EMAIL;type=INTERNET;type=WORK;type=pref:johnDoe@example.org
   * TEL;type=WORK;type=pref:+1 617 555 1212 TEL;type=WORK:+1 (617) 555-1234
   * TEL;type=CELL:+1 781 555 1212 TEL;type=HOME:+1 202 555 1212
   * item1.ADR;type=WORK:;;2 Enterprise Avenue;Worktown;NY;01111;USA
   * item1.X-ABADR:us item2.ADR;type=HOME;type=pref:;;3 Acacia
   * Avenue;Hoemtown;MA;02222;USA item2.X-ABADR:us NOTE:John Doe has a long and
   * varied history\, being documented on more police files that anyone else.
   * Reports of his death are alas numerous.
   * item3.URL;type=pref:http\://www.example/com/doe
   * item3.X-ABLabel:_$!<HomePage>!$_
   * item4.URL:http\://www.example.com/Joe/foaf.df item4.X-ABLabel:FOAF
   * item5.X-ABRELATEDNAMES;type=pref:Jane Doe item5.X-ABLabel:_$!<Friend>!$_
   * CATEGORIES:Work,Test group
   * X-ABUID:5AD380FD-B2DE-4261-BA99-DE1D1DB52FBE\:ABPerson END:VCARD
   * @throws Exception 
   */
  @Test
  public void testJohnDoe() throws Exception {
    CarddavSystem cs=new CarddavSystem();
    cs.connect();
    String jdvcf="https://www.w3.org/2002/12/cal/vcard-examples/john-doe.vcf";
    VCardNode jdNode=(VCardNode) cs.moveTo(jdvcf);
    assertNotNull(jdNode);
    if (debug)
      cs.forAll(SimpleNode.printDebug);
    VCard vcard=jdNode.vcard;
    assertEquals(vcard.getProperties().size()+1,cs.g().V().count().next().longValue());
    assertEquals(1,cs.g().V().hasLabel("StructuredName").count().next().longValue());
    assertEquals(4,cs.g().V().hasLabel("Telephone").count().next().longValue());
  }

}
