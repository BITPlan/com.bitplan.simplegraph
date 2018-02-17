package com.bitplan.simplegraph.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.core.SimpleSystem;
import com.bitplan.simplegraph.impl.SimpleSystemImpl;

public class PdfSystem extends SimpleSystemImpl {

	class PdfAnalysis {
		PDDocument pdDoc = null;
		PDFTextStripper pdfStripper = null;
		COSDocument cosDoc = null;
		int pages;

		public PdfAnalysis(InputStream in) throws IOException {
			pdDoc = PDDocument.load(in);
			in.close();
			pdfStripper = new PDFTextStripper();
			pages = pdDoc.getNumberOfPages();
		}

		public String getPageText(int pageNo) throws IOException {
			// https://stackoverflow.com/a/23814989/6204861
			pdfStripper.setStartPage(pageNo);
			pdfStripper.setEndPage(pageNo);
			return pdfStripper.getText(pdDoc);
		}
	}

	@Override
	public SimpleSystem connect(String... connectionParams) throws Exception {
		return this;
	}

	public SimpleNode moveTo(File pdfFile) {
		FileInputStream in;
		try {
			in = new FileInputStream(pdfFile);
			PdfNode pdfNode = new PdfNode(this, "pdf", new PdfAnalysis(in), -1);
			return pdfNode;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public SimpleNode moveTo(String nodeQuery, String... keys) {
		PdfNode pdfNode = null;
		InputStream in;
		try {
			in = new URL(nodeQuery).openStream();
			pdfNode = new PdfNode(this, "pdf", new PdfAnalysis(in), -1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (pdfNode != null && this.getStartNode() == null)
			this.setStartNode(pdfNode);
		return pdfNode;
	}

	@Override
	public Class<? extends SimpleNode> getNodeClass() {
		return PdfNode.class;
	}

}
