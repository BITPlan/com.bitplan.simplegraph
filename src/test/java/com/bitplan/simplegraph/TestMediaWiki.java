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

import org.junit.Test;

import com.bitplan.mediawiki.MediaWikiSystem;
import com.bitplan.mediawiki.japi.api.Ii;

/**
 * test access to MediaWiki
 * @author wf
 *
 */
public class TestMediaWiki extends BaseTest {

  @Test
  public void testGetImageInfo() throws Exception {
    debug=true;
    MediaWikiSystem mws=new MediaWikiSystem();
    SimpleNode pageNode = mws.connect("https://commons.wikimedia.org","/w").moveTo("File:Queen Victoria by Bassano.jpg");
    if (debug)
      pageNode.printNameValues(System.out);
    Ii imageInfo=(Ii) pageNode.getMap().get("imageInfo");
    assertEquals("https://upload.wikimedia.org/wikipedia/commons/e/e3/Queen_Victoria_by_Bassano.jpg",imageInfo.getUrl());
  }

}
