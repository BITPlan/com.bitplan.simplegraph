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
