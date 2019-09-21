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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.bitplan.simplegraph.core.Keys;
import com.bitplan.simplegraph.core.SimpleStepNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;

/**
 * a wrapper for a PDF file or page
 * 
 * @author wf
 *
 */
public class PdfNode extends SimpleNodeImpl implements SimpleStepNode {

  private PDF pdf;
  int pageNo;

  /**
   * create a PdfNode for the given pdfAnalysis
   * 
   * @param pdfSystem
   * @param pdf
   * @param pageNo
   */
  public PdfNode(PdfSystem pdfSystem, String kind, PDF pdf, int pageNo) {
    super(pdfSystem, kind, Keys.EMPTY_KEYS);
    this.pdf = pdf;
    this.pageNo = pageNo;
    super.setVertexFromMap();
  }

  @Override
  public Map<String, Object> initMap() {
    if (pdf.error == null) 
      if (pageNo < 0) {
        map.put("NumberOfPages", pdf.pages);
      } else {
        String parsedText = pdf.getPageText(pageNo);
        map.put("text", parsedText);
        map.put("pageNumber", pageNo);
        try {
          pdf.close();
        } catch (IOException e) {
          pdf.error=e;
        }
      }
    return map;
  }

  @Override
  public Stream<SimpleStepNode> out(String edgeName) {
    List<SimpleStepNode> pages = new ArrayList<SimpleStepNode>();
    if ("pages".equals(edgeName)) {
      for (int pageNo = 1; pageNo <= pdf.pages; pageNo++) {
        SimpleStepNode pageNode = new PdfNode((PdfSystem) this.getSimpleGraph(),
            "page", pdf, pageNo);
        this.getVertex().addEdge("pages", pageNode.getVertex());
        pages.add(pageNode);
      }
    }
    return pages.stream();
  }

  @Override
  public Stream<SimpleStepNode> in(String edgeName) {
    return null;
  }

}
