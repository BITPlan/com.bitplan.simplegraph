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
package com.bitplan.simplegraph.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.bitplan.simplegraph.SimpleNode;
import com.bitplan.simplegraph.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

public class SQLSystem extends SimpleSystemImpl {
  String driver;
  String connection;
  String user;
  String password;
  private Connection dbConnection;

  @Override
  public SimpleSystem connect(String... connectionParams) throws Exception {
    if (connectionParams.length > 0)
      driver = connectionParams[0];
    if (connectionParams.length > 1)
      connection = connectionParams[1];
    if (connectionParams.length > 2)
      user = connectionParams[2];
    if (connectionParams.length > 3)
      password = connectionParams[3];
    Class.forName(driver);
    dbConnection = DriverManager.getConnection(connection, user, password);
    return this;
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    SimpleNode sqlRecord=null;
    try {
      dbConnection.setAutoCommit(false);
      Statement stmt = dbConnection.createStatement();
      ResultSet rs = stmt.executeQuery(nodeQuery);
      ResultSetMetaData rsMetaData = rs.getMetaData();
      String kind=rsMetaData.getTableName(1);
      while (rs.next()) {
        sqlRecord=new SQLRecord(this,kind,rs,rsMetaData);
        if (this.getStartNode()==null)
          this.setStartNode(sqlRecord);
      }
      stmt.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return sqlRecord;
  }

  @Override
  public Class<? extends SimpleNode> getNodeClass() {
    return SQLRecord.class;
  }

  /**
   * execute the given list of sql Lines in the given sqlScript
   * @param sqlScript
   * @throws SQLException
   */
  public void execute(String sqlScript) throws SQLException {
    dbConnection.setAutoCommit(false);
    Statement stmt = dbConnection.createStatement();
    String[] sqlLines = sqlScript.split("\n");
    for (String sqlLine:sqlLines) {
      stmt.execute(sqlLine);
    }
    stmt.close();
    dbConnection.commit();
  }
  
  @Override
  public SimpleSystem close(String ...closeParams) throws Exception {
    dbConnection.close();
    super.close(closeParams);
    return this;
  }

}
