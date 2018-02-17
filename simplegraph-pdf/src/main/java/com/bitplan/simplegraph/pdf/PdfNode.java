package com.bitplan.simplegraph.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.bitplan.simplegraph.core.Keys;
import com.bitplan.simplegraph.core.SimpleNode;
import com.bitplan.simplegraph.impl.SimpleNodeImpl;
import com.bitplan.simplegraph.pdf.PdfSystem.PdfAnalysis;

public class PdfNode extends SimpleNodeImpl {

	private PdfAnalysis pdfAnalysis;
	int pageNo;

	/**
	 * create a PdfNode for the given pdfAnalysis
	 * 
	 * @param pdfSystem
	 * @param pdfAnalysis
	 * @param pageNo
	 */
	public PdfNode(PdfSystem pdfSystem, String kind, PdfAnalysis pdfAnalysis, int pageNo) {
		super(pdfSystem, kind, Keys.EMPTY_KEYS);
		this.pdfAnalysis = pdfAnalysis;
		this.pageNo = pageNo;
		super.setVertexFromMap();
	}

	@Override
	public Map<String, Object> initMap() {
		if (pageNo < 0) {
			map.put("NumberOfPages", pdfAnalysis.pages);
		} else {
			try {
				String parsedText = pdfAnalysis.getPageText(pageNo);
				map.put("text", parsedText);
				map.put("pageNumber", pageNo);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return map;
	}

	@Override
	public Stream<SimpleNode> out(String edgeName) {
		List<SimpleNode> pages = new ArrayList<SimpleNode>();
		if ("pages".equals(edgeName)) {
			for (int pageNo = 1; pageNo <= pdfAnalysis.pages; pageNo++) {
				SimpleNode pageNode = new PdfNode((PdfSystem) this.getSimpleGraph(), "page", pdfAnalysis, pageNo);
				this.getVertex().addEdge("pages", pageNode.getVertex());
				pages.add(pageNode);
			}
		}
		return pages.stream();
	}

	@Override
	public Stream<SimpleNode> in(String edgeName) {
		// TODO Auto-generated method stub
		return null;
	}

}
