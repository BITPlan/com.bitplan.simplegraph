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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.Holder;

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

  // https://developer.github.com/v4/explorer/
  // https://developer.github.com/v4/guides/forming-calls/#the-graphql-endpoint
  // https://developer.github.com/v4/guides/forming-calls/#example-query
  // https://api.github.com/graphql
  // https://stackoverflow.com/questions/49324611/github-v4-graphql-api-with-java-using-graphql-java
  public GraphTraversalSource doquery(String query) throws Exception {
    GitHubSystem ghs = new GitHubSystem();
    ghs.connect();
    ghs.js.setDebug(debug);
    ghs.moveTo(query);
    if (debug) {
      ghs.js.forAll(SimpleNode.printDebug);
    }
    return ghs.js.g();
  }
  
 

  @Test
  public void testGitHubSystem() throws Exception {
    if (!GitHubSystem.hasAuthentication())
      return;
    GitHubSystem ghs = new GitHubSystem();
    ghs.connect();
    ghs.moveTo("");
    if (debug) {
      ghs.forAll(SimpleNode.printDebug);
      ghs.js.getStartNode().g().V().hasLabel("fields").forEachRemaining(
          node -> System.out.println(node.property("name").value().toString()));
    }
    long fieldCount = ghs.js.getStartNode().g().V().hasLabel("fields").count()
        .next().longValue();
    assertEquals(1595, fieldCount);
  }

  @Test
  public void testViewerLogin() throws Exception {
    if (!GitHubSystem.hasAuthentication())
      return;
    String query = "query { viewer { login } }";
    debug = true;
    GraphTraversalSource g = doquery(query);
    List<Object> logins = g.V().hasLabel("viewer").values("login").toList();
    assertEquals(1, logins.size());
  }

  /**
   * see
   * https://developer.github.com/v4/guides/forming-calls/#communicating-with-graphql
   * 
   * @throws Exception
   */
  @Test
  public void testFindIssues() throws Exception {
    if (!GitHubSystem.hasAuthentication())
      return;
    String query = "query {\n"
        + "  repository(owner:\"octocat\", name:\"Hello-World\") {\n"
        + "    issues(last:20, states:CLOSED) {\n" + "      edges {\n"
        + "        node {\n" + "          title\n" + "          url\n"
        + "          labels(first:5) {\n" + "            edges {\n"
        + "              node {\n" + "                name\n"
        + "              }\n" + "            }\n" + "          }\n"
        + "        }\n" + "      }\n" + "    }\n" + "  }\n" + "}";
    // debug=true;
    GraphTraversalSource g = doquery(query);
    long issueCount = g.V().hasLabel("node").count().next().longValue();
    assertEquals(21, issueCount);
    Holder<Integer> countIssuesWithNoUrl=new Holder<Integer>(0);
    g.V().hasLabel("node").forEachRemaining(node -> {
      if (node.property("url").isPresent()) {
        assertTrue(node.property("url").value().toString()
            .startsWith("https://github.com/octocat/Hello-World/issues/"));
      } else {
        countIssuesWithNoUrl.setValue(countIssuesWithNoUrl.getFirstValue()+1);
        if (debug)
          Stream.of(node).forEach(SimpleNode.printDebug);
      }
    });
    assertEquals(1,countIssuesWithNoUrl.getFirstValue().intValue());
  }
  
  @Test
  public void testVariables() throws Exception {
    if (!GitHubSystem.hasAuthentication())
      return;
    String query="query($number_of_repos:Int!) {\n" + 
        "  viewer {\n" + 
        "    name\n" + 
        "     repositories(last: $number_of_repos) {\n" + 
        "       nodes {\n" + 
        "         name\n" + 
        "       }\n" + 
        "     }\n" + 
        "   }\n" + 
        "}";
    debug=true;
    GraphTraversalSource g = doquery(query);
  }
  
  @Test
  public void testMutation() throws Exception {
    if (!GitHubSystem.hasAuthentication())
      return;
    String query="query FindIssueID {\n" + 
        "  repository(owner:\"octocat\", name:\"Hello-World\") {\n" + 
        "    issue(number:349) {\n" + 
        "      id\n" + 
        "    }\n" + 
        "  }\n" + 
        "}";
    debug=true;
    GraphTraversalSource g = doquery(query);
    String id=g.V().hasLabel("issue").values("id").next().toString();
    assertEquals(id,"MDU6SXNzdWUyMzEzOTE1NTE=");
    String mutation="{\n" + 
        "  \"data\": {\n" + 
        "    \"addReaction\": {\n" + 
        "      \"reaction\": {\n" + 
        "        \"content\": \"HOORAY\"\n" + 
        "      },\n" + 
        "      \"subject\": {\n" + 
        "        \"id\": \""+id+"\"\n" + 
        "      }\n" + 
        "    }\n" + 
        "  }\n" + 
        "}";
    g = doquery(mutation);
  }

}
