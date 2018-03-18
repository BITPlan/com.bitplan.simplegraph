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
package com.bitplan.simplegraph.github;


import java.io.File;
import java.util.logging.Level;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.PropertiesImpl;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;
import com.bitplan.simplegraph.json.JsonNode;
import com.bitplan.simplegraph.json.JsonSystem;

/**
 * wraps the GitHub access via the GitHub Java API 
 * http://github-api.kohsuke.org/
 * @author wf
 *
 */
public class GitHubSystem extends SimpleSystemImpl {

  private static final String GITHUB_APIV4 = "https://api.github.com/graphql";
  JsonSystem js;
  
  /**
   * get the GitHub JsonSystem
   * @return the JsonSystem
   * @throws Exception
   */
  public JsonSystem getGitHubJsonSystem() throws Exception {
    PropertiesImpl properties = new PropertiesImpl("github");
    String token = (String) properties.getProperty("oauth");

    JsonSystem js = new JsonSystem();
    js.setDebug(debug);
    js.connect("Authorization: bearer " + token);
    return js;
  }
  
  @Override
  public SimpleSystem connect(String... connectionParams) throws Exception {
    js=getGitHubJsonSystem();
    return this;
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    SimpleNode result=null;
    if ("".equals(nodeQuery))
      result=js.moveTo(GITHUB_APIV4);
    else {
      String queryJson=String.format("{ \"query\": \"%s\" }",nodeQuery.replaceAll("\"","\\\\\"").replaceAll("\n",""));
      if (debug)
        LOGGER.log(Level.INFO, queryJson);
      result=js.post(GITHUB_APIV4,queryJson);
    }
    this.optionalStartNode(result);
    return result;
  }

  @Override
  public Class<? extends SimpleNode> getNodeClass() {
    return JsonNode.class;
  }
  static boolean first=true;
  /**
   * is the authentication available?
   * @return
   */
  public static boolean hasAuthentication() {
    File authFile=PropertiesImpl.getPropertyFile("github");
    boolean result=authFile.canRead();
    if (first && !result) {
      first=false;
      LOGGER.log(Level.WARNING, String.format("To use the github System you might want to create the file %s with an entry oauth=<token>\nThe token can be obtained from https://github.com/settings/tokens",authFile.getAbsolutePath()));
    }
    return result;
  }
}
