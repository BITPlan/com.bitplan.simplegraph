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

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

/**
 * allows Access to Microsoft Excel Tables
 * 
 * @author wf
 *
 */
public class ExcelSystem extends SimpleSystemImpl {
  /**
   * initialize me
   */
  public ExcelSystem() {
    super.setName("ExcelSystem");
    super.setVersion("0.0.1");
  }

  @Override
  public SimpleSystem connect(String... connectionParams) throws Exception {
    return this;
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    WorkBookNode workBookNode=new WorkBookNode(this,nodeQuery);
    if (this.getStartNode()==null) {
      this.setStartNode(workBookNode);
    }
    return workBookNode;
  }

  @Override
  public Class<? extends SimpleNode> getNodeClass() {
    return WorkBookNode.class;
  }

  public Workbook createWorkBook(GraphTraversalSource g) {
    // delegate the call to static function of Excel
    return Excel.createWorkBook(g);
  }

  public void save(Workbook wb, String fileName) throws Exception {
    Excel.save(wb,fileName);
    
  }

 

}
