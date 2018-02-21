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
package com.bitplan.simplegraph.mediawiki;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.mediawiki.japi.api.Ii;
import com.bitplan.simplegraph.mediawiki.MediaWikiPageNode;
import com.bitplan.simplegraph.mediawiki.MediaWikiSystem;

/**
 * test access to MediaWiki
 * 
 * @author wf
 *
 */
public class TestMediaWikiSystem  {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.mediawiki");
  
  @Test
  public void testGetPage() throws Exception {
    //debug=true;
    MediaWikiSystem mws = new MediaWikiSystem();
    MediaWikiPageNode pageNode = (MediaWikiPageNode) mws
        .connect("https://en.wikipedia.org", "/w")
        .moveTo("Cologne");
    if (debug)
      pageNode.printNameValues(System.out);
    String pageContent=pageNode.getProperty("pagecontent").toString();
    assertTrue(pageContent.contains("[[Category:Cities in North Rhine-Westphalia]]"));
  }

  @Test
  public void testGetImageInfo() throws Exception {
    // debug = true;
    MediaWikiSystem mws = new MediaWikiSystem();
    MediaWikiPageNode pageNode = (MediaWikiPageNode) mws
        .connect("https://commons.wikimedia.org", "/w")
        .moveTo("File:Queen Victoria by Bassano.jpg");
    if (debug)
      pageNode.printNameValues(System.out);
    String url = ((Ii) pageNode.getMap().get("imageInfo")).getUrl();
    assertEquals(
        "https://upload.wikimedia.org/wikipedia/commons/e/e3/Queen_Victoria_by_Bassano.jpg",
        url);
    BufferedImage queenVictoriaImage = pageNode.getImage(600);
    assertNotNull(queenVictoriaImage);
    assertEquals(600, queenVictoriaImage.getWidth());
    assertEquals(847, queenVictoriaImage.getHeight());
  }

  @Test
  public void testThumbImageUrl() throws Exception {
    String url = "https://upload.wikimedia.org/wikipedia/commons/e/e3/Queen_Victoria_by_Bassano.jpg";
    String thumbUrl = MediaWikiPageNode.getThumbImageUrl(url, 170);
    assertEquals(
        "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/Queen_Victoria_by_Bassano.jpg/170px-Queen_Victoria_by_Bassano.jpg",
        thumbUrl);
  }
}
