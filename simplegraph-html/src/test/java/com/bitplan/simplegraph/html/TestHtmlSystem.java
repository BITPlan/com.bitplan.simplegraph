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
package com.bitplan.simplegraph.html;

import static org.junit.Assert.assertEquals;

import java.util.logging.Logger;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import com.bitplan.gremlin.RegexPredicate;
import com.bitplan.simplegraph.core.SimpleNode;

/**
 * test the HTML System
 * @author wf
 *
 */
public class TestHtmlSystem {
	public static boolean debug = false;
	protected static Logger LOGGER = Logger.getLogger("com.bitplan.simplegraph.html");

	@Test
	public void testRootNodeAttributes() throws Exception {
		HtmlSystem hs = HtmlSystem.forUrl("http://agilemanifesto.org/");
		HtmlNode htmlNode=(HtmlNode) hs.getStartNode();
		assertEquals("html", htmlNode.getRootNode().getName());
		//debug = true;
		if (debug) {
			htmlNode.forAll(SimpleNode.printDebug);
		}
		GraphTraversal<Vertex, Vertex> links = hs.g().V().hasLabel("a");
		assertEquals(72,links.count().next().longValue());
		links = hs.g().V().hasLabel("a");
		links.forEachRemaining(link->System.out.println(link.property("href").value()));
		//links.forEach(link -> System.out.println(link));
		//links.entrySet().forEach(entry->System.out.println(entry.getKey()+"="+entry.getValue()));
	}

	@Test
	public void testGetRecipes() throws Exception{
		HtmlSystem hs = HtmlSystem.forUrl("https://www1.wdr.de/verbraucher/rezepte/alle-rezepte/sauerbraten-vom-rind-100.html");
		HtmlNode htmlNode=(HtmlNode) hs.getStartNode();
		assertEquals("html", htmlNode.getRootNode().getName());
		//debug = true;
		if (debug) {
			htmlNode.forAll(SimpleNode.printDebug);
		}
		GraphTraversal<Vertex, Vertex> links = hs.g().V().hasLabel("a").has("href",RegexPredicate.regex(".*pdf"));
		assertEquals(2,links.count().next().longValue());
		links = hs.g().V().hasLabel("a").has("href",RegexPredicate.regex(".*pdf"));
		links.forEachRemaining(link->System.out.println(link.property("href").value()));
	}

}
