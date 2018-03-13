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

import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;

import graphql.GraphQL;

/**
 * test the GitHub System
 * 
 * @author wf
 *
 */
public class TestGitHubSystem {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger
      .getLogger("com.bitplan.simplegraph.github");

  @Test
  public void testGitHubSystem() throws Exception {
    GitHubSystem ghs = new GitHubSystem();
    ghs.connect();
    ghs.moveTo("");
    ghs.forAll(SimpleNode.printDebug);
  }

  @Test
  public void testGitHubGraphQLApi() {
    // https://developer.github.com/v4/explorer/
    // https://developer.github.com/v4/guides/forming-calls/#the-graphql-endpoint
    // https://api.github.com/graphql
    // https://stackoverflow.com/questions/tagged/graphql-java
    // https://github.com/graphcool/get-graphql-schema
    // https://developer.github.com/v4/guides/forming-calls/#example-query
    // GraphQL.newGraphQL(graphQLSchema);
  }

 
}
