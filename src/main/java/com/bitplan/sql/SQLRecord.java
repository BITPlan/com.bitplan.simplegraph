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
package com.bitplan.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Stream;

import com.bitplan.simplegraph.SimpleGraph;
import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * represents and SQL record
 * 
 * @author wf
 */
public class SQLRecord extends SimpleNodeImpl {

  public SQLRecord(SQLSystem sqlSystem,String... keys) {
    super(sqlSystem,"record");
  }
  
  /**
   * get an SQL record
   * @param simpleGraph
   * @param kind
   * @param rs
   * @param rsMetaData
   */
  public SQLRecord(SimpleGraph simpleGraph, String kind, ResultSet rs,
      ResultSetMetaData rsMetaData) {
    super(simpleGraph, kind);
    int columns;
    try {
      columns = rsMetaData.getColumnCount();
      for (int columnIndex = 1; columnIndex <= columns; columnIndex++) {
        String columnName=rsMetaData.getColumnName(columnIndex);
        map.put(columnName, rs.getObject(columnIndex));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    super.setVertexFromMap(map);
  }

  @Override
  public Map<String, Object> initMap() {
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
