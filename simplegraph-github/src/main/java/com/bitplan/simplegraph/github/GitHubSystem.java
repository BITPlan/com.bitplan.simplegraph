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
package com.bitplan.simplegraph.github;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;
import com.bitplan.simplegraph.json.JsonNode;
import com.bitplan.simplegraph.json.JsonSystem;

/**
 * wraps the GitHub access via the GitHub GraphQL API
 * @author wf
 *
 */
public class GitHubSystem extends SimpleSystemImpl {

  private static final String GITHUB_APIV4 = "https://api.github.com/graphql";
  JsonSystem js;
  
  /**
   * Get the authentication file
   * @return the file object for the access token json
   */
  public static File getAuthFile() {
    String home = System.getProperty("user.home");
    return new File(home + "/.github/access_token.json");
  }

  /**
   * get the GitHub JsonSystem
   * @return the JsonSystem
   * @throws Exception
   */
  public JsonSystem getGitHubJsonSystem() throws Exception {
    File authFile = getAuthFile();
    if (!authFile.exists()) {
      throw new IllegalStateException("GitHub authentication file not found at " + authFile.getAbsolutePath());
    }

    // Eat our own dog food: Use JsonSystem to parse the auth file
    String jsonContent = new String(Files.readAllBytes(Paths.get(authFile.toURI())));
    JsonSystem authJs = JsonSystem.of(null, jsonContent);
    SimpleNode authNode = authJs.getStartNode();
    
    // Extract the token
    String token = (String) authNode.getMap().get("access_token");
    
    if (token == null || token.trim().isEmpty()) {
       throw new IllegalStateException("access_token not found in " + authFile.getAbsolutePath());
    }

    // Create the actual System for the API Query
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
      // escape quotes and remove newlines for GraphQL query wrapping
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
   * @return true if authFile is available
   */
  public static boolean hasAuthentication() {
    File authFile=getAuthFile();
    boolean result=authFile.canRead();
    if (first && !result) {
      first=false;
      LOGGER.log(Level.WARNING, String.format("To use the github System you might want to create the file %s with the content {\"access_token\": \"<github_pat_...>\"} \nThe token can be obtained from https://github.com/settings/tokens",authFile.getAbsolutePath()));
    }
    return result;
  }
}