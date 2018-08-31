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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.htmlcleaner.TagNode;

import com.bitplan.simplegraph.core.Keys;
import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleStepNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * wrap HTML
 * @author wf
 *
 */
public class HtmlNode extends SimpleNodeImpl implements SimpleStepNode {
	TagNode rootNode;

	public TagNode getRootNode() {
		return rootNode;
	}

	public HtmlNode(SimpleGraph simpleGraph, String kind, String... keys) {
		super(simpleGraph, kind, keys);
	}
	
	public HtmlNode(SimpleGraph simpleGraph, TagNode rootNode) {
		super(simpleGraph,rootNode.getName(),Keys.EMPTY_KEYS);
		initRootNode(rootNode);
	}
	
	public void initRootNode(TagNode rootNode) {
		this.rootNode=rootNode;
		super.setVertexFromMap();
	}

	/**
	 * create me from the given system with the given nodeQuery (url-String)
	 * @param htmlSystem
	 * @param nodeQuery
	 * @param kind - always "html"
	 * @param keys
	 */
	public HtmlNode(HtmlSystem htmlSystem, String nodeQuery, String kind, String... keys) {
		this(htmlSystem, kind, keys);
		URL url;
		try {
			url = new URL(nodeQuery);
			this.initRootNode(htmlSystem.getCleaner().clean(url));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, Object> initMap() {
		Map<String, String> rMap = rootNode.getAttributes();
		// repack the map <string,string> -> <string,object>
		rMap.entrySet().forEach(e -> map.put(e.getKey(), e.getValue()));
		return super.map;
	}

	@SuppressWarnings("deprecation")
  @Override
	public Stream<SimpleStepNode> out(String edgeName) {
		List<SimpleStepNode> children=new ArrayList<SimpleStepNode>();
		if ("child".equals(edgeName)) {
			rootNode.getChildren().forEach(child -> {
				HtmlNode childNode=new HtmlNode (this,child);
				children.add(childNode);
			});
			return children.stream();
		}
		return null;
	}

	@Override
	public Stream<SimpleStepNode> in(String edgeName) {
		return null;
	}

}
