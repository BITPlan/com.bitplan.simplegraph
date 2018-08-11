package com.bitplan.simplegraph.caldav;

import java.util.Map;
import java.util.stream.Stream;

import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

public class CalendarNode extends SimpleNodeImpl{

	public CalendarNode(SimpleGraph simpleGraph, String kind, String[] keys) {
		super(simpleGraph, kind, keys);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, Object> initMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<SimpleNode> out(String edgeName) {
		// TODO Auto-generated method stub‚‚
		return null;
	}

	@Override
	public Stream<SimpleNode> in(String edgeName) {
		// TODO Auto-generated method stub
		return null;
	}

}
