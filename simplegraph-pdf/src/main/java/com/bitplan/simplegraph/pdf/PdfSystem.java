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
package com.bitplan.simplegraph.pdf;

import java.io.File;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

/**
 * wrapper for PDF Files
 * 
 * @author wf
 *
 */
public class PdfSystem extends SimpleSystemImpl {

  @Override
  public SimpleSystem connect(String... connectionParams) throws Exception {
    return this;
  }

  /**
   * move to the given PDF File
   * 
   * @param pdfFile
   *          - the pdfFile to analyze
   * @return a simple node for this pdf file
   */
  public SimpleNode moveTo(File pdfFile) {
    PdfNode pdfNode = new PdfNode(this, "pdf", new PDF(pdfFile), -1);
    return pdfNode;
  }

  @Override
  public SimpleNode moveTo(String nodeQuery, String... keys) {
    PdfNode pdfNode = new PdfNode(this, "pdf", new PDF(nodeQuery), -1);
    if (pdfNode != null && this.getStartNode() == null)
      this.setStartNode(pdfNode);
    return pdfNode;
  }

  @Override
  public Class<? extends SimpleNode> getNodeClass() {
    return PdfNode.class;
  }

}
