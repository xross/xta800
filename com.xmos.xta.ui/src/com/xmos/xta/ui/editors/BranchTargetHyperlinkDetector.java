package com.xmos.xta.ui.editors;

import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

import com.xmos.xta.ui.GuiEngine;

public class BranchTargetHyperlinkDetector implements IHyperlinkDetector {
	
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {

		IDocument document = textViewer.getDocument();

		IRegion lineInfo;
		String line;
		try {
			lineInfo = document.getLineInformationOfOffset(region.getOffset());
			line = document.get(lineInfo.getOffset(), lineInfo.getLength());

		} catch (BadLocationException ex) {
			return null;
		}
		
		int begin = line.indexOf("<");
		int end = line.indexOf(">");
		if ((end < 0) || (begin < 0) || (end == begin + 1)) {
			return null;
		}
		
		String text = line.substring(begin + 1, end);
		IRegion hyperLinkRegion = new Region(lineInfo.getOffset() + begin + 1, text.length());
		
		String pcString;
		Map<String, Long> labels = GuiEngine.getEngine().getLabels();
		if (labels.containsKey(text)) {
			pcString = "0x" + Long.toHexString(labels.get(text));
		} else {
			pcString = text;
		}
		
		return new IHyperlink[] {new BranchTargetHyperlink(hyperLinkRegion, pcString)};
	}
}
