/*
 * Copyright (c) 2018-2025 BITPlan GmbH
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

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import com.bitplan.mediawiki.japi.api.Ii;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.bitplan.simplegraph.core.SimpleNode;
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
        imageInfo.setUrl(plainUrl(imageInfo.getUrl()));
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

  /**
   * get the image for this page (for File: pages)
   * 
   * @return the image
   * @throws Exception
   */
  public BufferedImage getImage() throws Exception {
    return getImage(null);
  }

  /**
   * the given url without its query string
   *
   * wikimedia appends tracking parameters to the image urls it reports
   *
   * @param url
   * @return the url up to the question mark
   */
  public static String plainUrl(String url) {
    String result = url;
    if (url != null) {
      int q = url.indexOf('?');
      if (q >= 0)
        result = url.substring(0, q);
    }
    return result;
  }

  /**
   * the user agent wikimedia asks api clients to send
   */
  public static final String USER_AGENT = "com.bitplan.simplegraph (https://github.com/BITPlan/com.bitplan.simplegraph)";

  /**
   * open a connection that identifies this library
   *
   * @param url
   * @return the input stream of the response
   * @throws Exception
   */
  public static InputStream openStream(String url) throws Exception {
    HttpURLConnection connection = (HttpURLConnection) new URL(url)
        .openConnection();
    connection.setRequestProperty("User-Agent", USER_AGENT);
    return connection.getInputStream();
  }

  /**
   * ask the imageinfo api for the thumbnail of the given width
   *
   * the sizes wikimedia serves are limited, so the thumbnail returned may be
   * wider than asked for
   *
   * @param size
   *          - the wanted width in pixels
   * @return the url of the thumbnail or null if there is none
   * @throws Exception
   */
  public String getThumbUrl(int size) throws Exception {
    String apiUrl = ms.wiki.getSiteurl() + ms.wiki.getScriptPath()
        + "/api.php?action=query&format=json&prop=imageinfo&iiprop=url&titles="
        + URLEncoder.encode(pageTitle, "UTF-8") + "&iiurlwidth=" + size;
    String result = null;
    try (InputStream stream = openStream(apiUrl)) {
      JsonObject json = new JsonParser()
          .parse(new java.io.InputStreamReader(stream, "UTF-8"))
          .getAsJsonObject();
      JsonObject pages = json.getAsJsonObject("query").getAsJsonObject("pages");
      for (String pageId : pages.keySet()) {
        JsonObject imageInfo = pages.getAsJsonObject(pageId)
            .getAsJsonArray("imageinfo").get(0).getAsJsonObject();
        if (imageInfo.has("thumburl"))
          result = plainUrl(imageInfo.get("thumburl").getAsString());
      }
    }
    return result;
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
      if (size != null) {
        String thumbUrl = getThumbUrl(size);
        if (thumbUrl != null)
          imageUrlStr = thumbUrl;
      }
      try (InputStream imageStream = openStream(imageUrlStr)) {
        return ImageIO.read(imageStream);
      }
    } else {
      return null;
    }
  }

}
