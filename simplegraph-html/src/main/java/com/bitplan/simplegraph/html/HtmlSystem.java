package com.bitplan.simplegraph.html;

import org.htmlcleaner.HtmlCleaner;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

public class HtmlSystem extends SimpleSystemImpl {
	HtmlCleaner cleaner;
	/*
	 * expose native API
	 */
	public HtmlCleaner getCleaner() {
		return cleaner;
	}

	@Override
	public SimpleSystem connect(String... connectionParams) throws Exception {
		// no connection parameters needed yet
		cleaner = new HtmlCleaner();
		return this;
	}

	@Override
	public SimpleNode moveTo(String nodeQuery, String... keys) {
		SimpleNode htmlNode = new HtmlNode (this,nodeQuery,"html",keys);
		if (this.getStartNode()==null)
			this.setStartNode(htmlNode);
		return htmlNode;
	}

	@Override
	public Class<? extends SimpleNode> getNodeClass() {
		return HtmlNode.class;
	}
	
	public static HtmlSystem forUrl(String url) throws Exception {
		HtmlSystem hs = new HtmlSystem();
		hs.connect();
		HtmlNode htmlNode = (HtmlNode) hs.moveTo(url);
		htmlNode.recursiveOut("child", Integer.MAX_VALUE);
		return hs;
	}

}
