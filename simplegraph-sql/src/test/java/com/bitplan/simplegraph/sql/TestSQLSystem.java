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

import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.sql.SQLSystem;

/**
 * test the access to the SQL system
 * 
 * @author wf
 *
 */
public class TestSQLSystem {
  public static boolean debug = false;
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.simplegraph.sql");

  private static final String DB_DRIVER = "org.h2.Driver";
  private static final String DB_CONNECTION = "jdbc:h2:mem:test";
  private static final String DB_USER = "";
  private static final String DB_PASSWORD = "";
  private String createSQL = "CREATE TABLE PERSON(id int primary key, name varchar(255), firstname varchar(255),email varchar(255))\n"
      + "INSERT INTO PERSON(id, name,firstname,email) VALUES(1, 'Doe','John','john@doe.com')\n"
      + "INSERT INTO PERSON(id, name,firstname,email) VALUES(2, 'Mayer','Tom','tom@mayer.com')\n"
      + "INSERT INTO PERSON(id, name,firstname,email) VALUES(3, 'Ford','Bob','bob@ford.com')\n";

  @Test
  public void testSQL() throws Exception {
    SQLSystem sql = new SQLSystem();
    sql.connect(DB_DRIVER, DB_CONNECTION, DB_USER, DB_PASSWORD);
    sql.execute(createSQL);
    sql.moveTo("select * from PERSON");
    long pCount = sql.getStartNode().g().V().count().next().longValue();
    debug = true;
    if (debug) {
      System.out.println(String.format("found %3d records",pCount));
      sql.getStartNode().forAll(SimpleNode.printDebug);
    }
    sql.close();
  }

}
