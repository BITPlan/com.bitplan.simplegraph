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
package com.bitplan.mediawiki;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import com.bitplan.mediawiki.japi.api.Ii;
import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * I wrap a single MediaWiki Page as a node
 * 
 * @author wf
 *
 */
public class MediaWikiPageNode extends SimpleNodeImpl implements SimpleNode {
  boolean failSafe = true;
  private String pageTitle;
  private MediaWikiSystem ms;
  transient protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.mediawiki");

  /**
   * initialize me from the given pageTitle
   * 
   * @param mediaWikiSystem
   * @param pageTitle
   */
  public MediaWikiPageNode(MediaWikiSystem mediaWikiSystem, String pageTitle,
      String... keys) {
    super(mediaWikiSystem, "wikiPage", keys);
    this.ms = mediaWikiSystem;
    this.pageTitle = pageTitle;
    super.setVertexFromMap();
  }

  @Override
  public Map<String, Object> initMap() {
    map.put("pagetitle", pageTitle);
    try {
      String pageContent = ms.wiki.getPageContent(pageTitle);
      map.put("pagecontent", pageContent);
      // TODO -multi language ?
      if (pageTitle.startsWith("File:")) {
        Ii imageInfo = ms.wiki.getImageInfo(pageTitle);
        map.put("imageInfo", imageInfo);
      }
    } catch (Exception e) {
      if (failSafe)
        LOGGER.log(Level.WARNING, "problem with pageTitle " + pageTitle, e);
      else
        throw new RuntimeException(e);
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

  /**
   * get the image for this page (for File: pages)
   * 
   * @return the image
   * @throws Exception
   */
  public BufferedImage getImage() throws Exception {
    return getImage(null);
  }

  public static String getThumbImageUrl(String url, int size) {
    // https://upload.wikimedia.org/wikipedia/commons/e/e3/Queen_Victoria_by_Bassano.jpg
    // https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/Queen_Victoria_by_Bassano.jpg/170px-Queen_Victoria_by_Bassano.jpg
    String[] parts = url.split("/");
    String thumbUrl = url;
    int len = parts.length;
    if (len > 3) {
      thumbUrl = "";
      for (int i = 0; i < len - 3; i++) {
        thumbUrl = thumbUrl + parts[i] + "/";
      }
      thumbUrl = thumbUrl + "thumb/";
      for (int i = len - 3; i < len - 1; i++) {
        thumbUrl = thumbUrl + parts[i] + "/";
      }
      thumbUrl = thumbUrl + parts[len - 1] + "/" + size + "px-"
          + parts[len - 1];
    }
    return thumbUrl;
  }

  /**
   * get the image for this page (for File: pages)
   * 
   * @param size
   *          - thumbnail size - full image if size is null
   * @return the image
   * @throws Exception
   */
  public BufferedImage getImage(Integer size) throws Exception {
    map = getMap();
    if (map.containsKey("imageInfo")) {
      String imageUrlStr = ((Ii) map.get("imageInfo")).getUrl();
      if (size != null)
        imageUrlStr = getThumbImageUrl(imageUrlStr, size);
      URL imageUrl = new URL(imageUrlStr);
      BufferedImage image = ImageIO.read(imageUrl);
      return image;
    } else {
      return null;
    }
  }

}
