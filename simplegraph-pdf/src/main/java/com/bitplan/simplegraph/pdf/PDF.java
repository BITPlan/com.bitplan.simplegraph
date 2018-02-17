package com.bitplan.simplegraph.pdf;

import java.io.InputStream;
import java.net.URL;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Portable Document File extractor help
 */
class PDF {
	public Throwable error;
	PDDocument doc;
	PDFTextStripper pdfStripper;

	/**
	 * construct this PDF from the given url
	 */
	public PDF(String url) {
		try {
			// might want to switch off logging here to improve performance
			String[] loggers = { "org.apache.pdfbox.util.PDFStreamEngine", "org.apache.pdfbox.util",
					"org.apache.pdfbox.util.PDFStreamEngine", "org.apache.pdfbox.pdfparser.PDFObjectStreamParser",
					"org.apache.pdfbox.cos.COSDocument", "org.apache.pdfbox.pdmodel.font.PDSimpleFont",
					"org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap",
					"org.apache.pdfbox.pdmodel.graphics.color.PDSeparation",
					"org.apache.pdfbox.pdmodel.graphics.color.PDColorState",
					"org.apache.pdfbox.pdmodel.graphics.color.PDICCBased",
					"org.apache.pdfbox.pdfparser.PDFObjectStreamParser" };
			/*for (String logger : loggers) {
				org.apache.log4j.Logger logpdfengine = org.apache.log4j.Logger.getLogger(logger);
				logpdfengine.setLevel(org.apache.log4j.Level.OFF);
			}*/
			InputStream is = new URL(url).openStream();
			doc = PDDocument.load(is);
			pdfStripper = new PDFTextStripper();
		} catch (Throwable th) {
			error = th;
		}
	}

	public String getPageText(int page) {
		String result = "?";
		try {
			pdfStripper.setStartPage(page);
			pdfStripper.setEndPage(page);
			result = pdfStripper.getText(doc);
		} catch (Throwable th) {
			error = th;
			result = "Error: " + th.getMessage();
		}
		return result;
	}
}
