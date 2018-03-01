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
package com.bitplan.simplegraph.excel;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.bitplan.simplegraph.core.Keys;
import com.bitplan.simplegraph.core.SimpleGraph;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

public class RowNode extends SimpleNodeImpl {

  private int rowIndex;
  private List<Object> row;
  private List<Object> titleRow;

  public RowNode(SimpleGraph simpleGraph, String kind, String[] keys) {
    super(simpleGraph, kind, keys);
  }

  public RowNode(WorkBookNode workBookNode, List<Object> titleRow,
      List<Object> row, int rowIndex) {
    this(workBookNode.getSimpleGraph(),"row",Keys.EMPTY_KEYS);
    this.titleRow=titleRow;
    this.rowIndex=rowIndex;
    this.row=row;
    super.setVertexFromMap();
  }

  @Override
  public Map<String, Object> initMap() {
    map.put("row", this.rowIndex);
    for (int colIndex=0;colIndex<=titleRow.size();colIndex++) {
      if (row.size()>colIndex) {
         String name="?";
         Object nameO=titleRow.get(colIndex);
         if (nameO!=null)
           name=nameO.toString();
         Object value=row.get(colIndex);
         map.put(name, value);
      }
    }
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
