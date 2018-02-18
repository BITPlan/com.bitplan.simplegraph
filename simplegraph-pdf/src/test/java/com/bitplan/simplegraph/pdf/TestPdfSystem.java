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

import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;

/**
 * test the PDF System
 * @author wf
 *
 */
public class TestPdfSystem {
	public static boolean debug = false;
	protected static Logger LOGGER = Logger.getLogger("com.bitplan.simplegraph.pdf");

	@Test
	public void testPDF() throws Exception {
		PdfSystem ps = new PdfSystem();
		ps.connect();
		SimpleNode pdfNode = ps.moveTo("http://eprints.nottingham.ac.uk/249/1/cajun.pdf");
		pdfNode.out("pages");
		debug=true;
		if (debug) {
			ps.forAll(SimpleNode.printDebug);
			System.out.println(ps.getStartNode().g().V().hasLabel("pdf").next().property("NumberOfPages").value());
			ps.getStartNode().g().V().hasLabel("page")
					.forEachRemaining(page -> System.out.println(page.property("text").value()));
		}
	}

}
