package com.bitplan.simplegraph.pdf;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.simplegraph.core.SimpleNode;

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
