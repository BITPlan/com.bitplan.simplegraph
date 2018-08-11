package com.bitplan.simplegraph.caldav;

import java.util.Map;
import java.util.stream.Stream;

import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;

public class CalendarEntryNode extends SimpleNodeImpl{
	  // File to be wrapped in a Node
	  CalendarComponent calendarComponent;
	  // System to be used
	  CaldavSystem caldavSystem;

	public CalendarEntryNode(SimpleGraph simpleGraph, String kind, String[] keys) {
		super(simpleGraph, kind, keys);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Map<String, Object> initMap() {
		PropertyList<Property> pList = calendarComponent.getProperties();
		pList.forEach (p -> {
			map.put(p.getName(),p.getValue());
		});
	    return map;
	}

	@Override
	public Stream<SimpleNode> out(String edgeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<SimpleNode> in(String edgeName) {
		// TODO Auto-generated method stub
		return null;
	}

}
