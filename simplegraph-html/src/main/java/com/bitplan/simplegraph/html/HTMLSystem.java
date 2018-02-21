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

import org.htmlcleaner.HtmlCleaner;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

public class HTMLSystem extends SimpleSystemImpl {
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
		SimpleNode htmlNode = new HTMLNode (this,nodeQuery,"html",keys);
		if (this.getStartNode()==null)
			this.setStartNode(htmlNode);
		return htmlNode;
	}

	@Override
	public Class<? extends SimpleNode> getNodeClass() {
		return HTMLNode.class;
	}
	
	public static HTMLSystem forUrl(String url) throws Exception {
		HTMLSystem hs = new HTMLSystem();
		hs.connect();
		HTMLNode htmlNode = (HTMLNode) hs.moveTo(url);
		htmlNode.recursiveOut("child", Integer.MAX_VALUE);
		return hs;
	}

}
