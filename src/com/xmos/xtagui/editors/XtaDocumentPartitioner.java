package com.xmos.xtagui.editors;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;

public class XtaDocumentPartitioner implements IDocumentPartitioner {
    public static final boolean DEBUG = false;
    public static final String SECTION = "PositionCategory.Section";

    private Input m_input;
	
	XtaDocumentPartitioner(Input input) {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner()\n");		
		m_input = input;
	}
	
	public ITypedRegion[] computePartitioning(int offset, int length) {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner: computePartitioning\n");		
		return null;
	}

	public void connect(IDocument document) {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner: connect\n");		
		
		int sectionOffset = 0;
		int sectionLength = 0;
		
		document.addPositionCategory(SECTION);
		
		for (InputSection inputSection : m_input.getSections()) {	
			try {
				sectionOffset = document.getLength();

				String header = inputSection.getHeader() + "\n";
				document.replace(sectionOffset, 0, header);
				
				for (String line : inputSection.getLines()) {
					line += "\n";
					document.replace(document.getLength(), 0, line);
				} 

				int numLines = inputSection.getLines().size();
				if (numLines > 0) {
				    sectionLength = document.getLength() - sectionOffset;
				    Position position = new Position(sectionOffset, sectionLength);				
				    document.addPosition(SECTION, position);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}				
		}	
	}

	public void disconnect() {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner: disconnect\n");		
	}

	public void documentAboutToBeChanged(DocumentEvent event) {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner: documentAboutToBeChanged\n");		
	}

	public boolean documentChanged(DocumentEvent event) {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner: documentChanged\n");		
		return false;
	}

	public String getContentType(int offset) {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner: getContentType\n");		
		return null;
	}

	public String[] getLegalContentTypes() {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner: getLegalContentTypes\n");		
		return null;
	}

	public ITypedRegion getPartition(int offset) {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner: getPartition\n");		
		return null;
	}

}
