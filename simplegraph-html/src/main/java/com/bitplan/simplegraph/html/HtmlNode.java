package com.bitplan.simplegraph.html;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.htmlcleaner.TagNode;

import com.bitplan.simplegraph.core.Keys;
import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

public class HtmlNode extends SimpleNodeImpl {
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

	@Override
	public Stream<SimpleNode> out(String edgeName) {
		List<SimpleNode> children=new ArrayList<SimpleNode>();
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
	public Stream<SimpleNode> in(String edgeName) {
		// TODO Auto-generated method stub
		return null;
	}

}
