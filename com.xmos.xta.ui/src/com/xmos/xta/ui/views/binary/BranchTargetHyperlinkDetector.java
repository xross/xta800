package com.xmos.xta.ui.views.binary;

import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

import com.xmos.libs.util.Conversion;
import com.xmos.xta.ui.GuiEngine;

public class BranchTargetHyperlinkDetector implements IHyperlinkDetector {
    public static final boolean DEBUG = false;
	
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (DEBUG) System.out.printf("BranchTargetHyperlinkDetector: detectHyperlinks\n");

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
		Map<String, Set<Long>> labels = GuiEngine.getEngine().getLabels();
		if (labels.containsKey(text)) {
			pcString = Conversion.pcSetToString(labels.get(text));
		} else {
			pcString = text;
		}
		
		return new IHyperlink[] {new BranchTargetHyperlink(hyperLinkRegion, pcString)};
	}
}
