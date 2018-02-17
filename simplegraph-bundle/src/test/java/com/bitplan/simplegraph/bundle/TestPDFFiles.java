package com.bitplan.simplegraph.bundle;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Ignore;
import org.junit.Test;

import com.bitplan.gremlin.RegexPredicate;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.filesystem.FileNode;
import com.bitplan.simplegraph.filesystem.FileSystem;
import com.bitplan.simplegraph.impl.Holder;
import com.bitplan.simplegraph.pdf.PdfSystem;

public class TestPDFFiles {

	@Ignore
	public void testPDFFiles() throws Exception {
		FileSystem fs = new FileSystem();
		fs.connect();
		FileNode fileRoot = fs.moveTo("examples/pdfs");
		fileRoot.recursiveOut("files", Integer.MAX_VALUE);
		assertEquals(3584, fs.g().V().count().next().longValue());
		PdfSystem ps = new PdfSystem();
		ps.connect();
		Holder<Integer> count = new Holder<Integer>(0);
		int limit=10;
		fs.g().V().hasLabel("file").has("ext", "pdf").forEachRemaining(file -> {
			if (count.getFirstValue() < limit) {
				File pdfFile = new File(file.property("path").value().toString());
				SimpleNode pdfNode = ps.moveTo(pdfFile);
				pdfNode.out("pages");
				pdfNode.property("name", pdfFile.getName());
			}
			count.setValue(count.getFirstValue() + 1);
		});
		ps.forAll(SimpleNode.printDebug);
		assertEquals(23, ps.g().V().hasLabel("page").count().next().longValue());
		assertEquals(3,
				ps.g().V().hasLabel("page").has("text", RegexPredicate.regex(".*QA .*")).count().next().longValue());
		
		ps.g().V().hasLabel("page").has("text", RegexPredicate.regex(".*QA .*")).in("pages").dedup().forEachRemaining(pdf ->
		   System.out.println(pdf.property("name").value()));
	}

}
