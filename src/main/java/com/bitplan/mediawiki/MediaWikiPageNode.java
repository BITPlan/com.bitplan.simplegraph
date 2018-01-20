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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import com.bitplan.mediawiki.japi.api.Api;
import com.bitplan.mediawiki.japi.api.Ii;
import com.bitplan.mediawiki.japi.api.Property;
import com.bitplan.mediawiki.japi.api.Query;
import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * I wrap a single Wikipedia Page as a node
 * 
 * @author wf
 *
 */
public class MediaWikiPageNode extends SimpleNodeImpl implements SimpleNode {

  private String pageTitle;
  private MediaWikiSystem ms;

  /**
   * initialize me from the given pageTitle
   * 
   * @param mediaWikiSystem
   * @param pageTitle
   */
  public MediaWikiPageNode(MediaWikiSystem mediaWikiSystem, String pageTitle) {
    super(mediaWikiSystem, "wikiPage");
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
      List<Property> props = this.getSMWProperties();
      if (props != null)
        for (Property prop : props) {
          map.put(prop.getProperty(), prop);
        }
      // TODO -multi language ?
      if (pageTitle.startsWith("File:")) {
        Ii imageInfo = ms.wiki.getImageInfo(pageTitle);
        map.put("imageInfo",imageInfo);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return map;
  }

  /**
   * get the semantic MediaWiki properties of this page
   * 
   * @return
   * @throws Exception
   */
  public List<Property> getSMWProperties() throws Exception {
    String params = "&subject=" + URLEncoder.encode(pageTitle, "UTF-8");
    Api api = ms.wiki.getActionResult("browsebysubject", params, null, null,
        "json");
    if (api != null) {
      Query query = api.getQuery();
      if (query != null) {
        List<Property> data = query.getData();
        return data;
      }
    }
    return null;
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
   * @return the image
   * @throws Exception
   */
  public BufferedImage getImage() throws Exception {
    String imageUrlStr = ((Ii) getMap().get("imageInfo")).getUrl();
    URL imageUrl=new URL(imageUrlStr);
    BufferedImage image=ImageIO.read(imageUrl);
    return image;
  }

}
