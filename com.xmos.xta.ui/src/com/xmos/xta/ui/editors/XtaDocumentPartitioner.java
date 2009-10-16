package com.xmos.xta.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitionerExtension;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TypedRegion;

public class XtaDocumentPartitioner implements IDocumentPartitioner,
                                               IDocumentPartitionerExtension {
    public static final boolean DEBUG = false;
    
    public static final String SECTION     = "PositionCategory.Section";
    public static final String SOURCE      = "SOURCE";
    public static final String DISASSEMBLY = "DISASSEMBLY";
    public static final String DISASSEMBLYFUNCTION = "DISASSEMBLYFUNCTION";
    
    private Input m_input;
    private TypedRegion[] m_regions;
    private String[] m_regionTypes;
	
	XtaDocumentPartitioner(Input input) {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner()\n");		
		m_input = input;
	}

	public ITypedRegion[] computePartitioning(int offset, int length) {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner: computePartitioning: %d, %d\n", offset, length);	
		return getRegions(offset, length);
	}

	public void connect(IDocument document) {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner: connect\n");		
	
		partitionDocument(document, true);

		m_regionTypes = new String[3];
		m_regionTypes[0] = SOURCE;
		m_regionTypes[1] = DISASSEMBLY;
		m_regionTypes[2] = DISASSEMBLYFUNCTION;
	}

	public void partitionDocument(IDocument document, boolean buildDocument) {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner: partitionDocument\n");		
		List<TypedRegion> regionList = new ArrayList<TypedRegion>();
		
		try {
			document.removePositionCategory(SECTION);
		} catch (BadPositionCategoryException e) {}
		document.addPositionCategory(SECTION);
		
		int previousOffset = 0;
		String previousType = XtaDocumentPartitioner.DISASSEMBLY;
		int documentLength = 0;
		
		if (m_input.getSections().size() > 0) {
			previousType = m_input.getSections().get(0).getHeaderType();
		}

		for (InputSection inputSection : m_input.getSections()) {
			int numLines = inputSection.getLines().size();
			String headerType = inputSection.getHeaderType();
						
			// Adds the SOURCE region for the previous lines.
			if (numLines > 0 || !headerType.equals(previousType)) {
				addTypedRegion(regionList, previousOffset, documentLength - previousOffset, previousType);
			}
			
			int headerFrom = documentLength;
			String header = inputSection.getHeader().getLine() + "\n";
			if (buildDocument) updateDocument(document, header);
			documentLength += header.length();
			
			if (numLines > 0) {
				addTypedRegion(regionList, headerFrom, header.length(), inputSection.getHeaderType());
			}
			
			if (numLines > 0) {
				documentLength = partitionLines(document, regionList, buildDocument, documentLength, inputSection);
				addPositionMarker(document, headerFrom, documentLength);
			}
			
			if (numLines > 0) {
				previousOffset = documentLength;
			}
			
			if (headerType != previousType) {
				previousType = headerType;
			}
		}

		addTypedRegion(regionList, previousOffset, documentLength - previousOffset, previousType);
		
		m_regions = new TypedRegion[regionList.size()];
		m_regions = regionList.toArray(m_regions);
	}

	private int partitionLines(IDocument document, List<TypedRegion> regionList, boolean buildDocument,
			int documentLength, InputSection inputSection) {

		int from = documentLength;
		String previousType = inputSection.getLines().get(0).getHeaderType();
		
		for (InputLine line : inputSection.getLines()) {
			String lineType = line.getHeaderType();
			if (!previousType.equals(lineType)) {
				addTypedRegion(regionList, from, documentLength - from, previousType);
				previousType = lineType;
				from = documentLength;
			}

			String str = line.getLine() + "\n";
			if (buildDocument) updateDocument(document, str);
			documentLength += str.length();			
		}
		addTypedRegion(regionList, from, documentLength - from, previousType);
		return documentLength;
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

	public IRegion documentChanged2(DocumentEvent event) {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner: documentChanged2\n");

		boolean hit = false;
		int regionSizeChange = event.fText.length() - event.fLength;
	    for (int index = 0; index < m_regions.length; ++index) {
	    	TypedRegion region = m_regions[index];

	    	if (hit) {
	    		TypedRegion newRegion = new TypedRegion(region.getOffset() + regionSizeChange,
	    				region.getLength(), region.getType());
	    		m_regions[index] = newRegion;
	    	} else {
    		
		    	if ((event.fOffset >= region.getOffset()) &&
					(event.fOffset < (region.getOffset() + region.getLength()))) {
		    		hit = true;
		    		
		    		TypedRegion newRegion = new TypedRegion(region.getOffset(),
		    				region.getLength() + regionSizeChange, region.getType());
		    		m_regions[index] = newRegion;
				}
	    	}
	    }
	    
		return null;
	}

	public String getContentType(int offset) {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner: getContentType: %d\n", offset);	
		return getRegion(offset).getType();
	}

	public String[] getLegalContentTypes() {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner: getLegalContentTypes\n");		
		return m_regionTypes;
	}

	public ITypedRegion getPartition(int offset) {
		if (DEBUG) System.out.printf("XtaDocumentPartitioner: getPartition: %d\n", offset);		
		return getRegion(offset);
	}

	public Input getInput() {
		return m_input;
	}
	
	private void updateDocument(IDocument document, String str) {
		try {
		    document.replace(document.getLength(), 0, str);
		} catch (Exception e) {
			e.printStackTrace();
		}				
	}
	private void addPositionMarker(IDocument document, int from, int to) {
	    Position position = new Position(from, to - from);				
		try {
    	    document.addPosition(SECTION, position);		
		} catch (Exception e) {
			e.printStackTrace();
		}				
	}	
	
	private void addTypedRegion(List<TypedRegion> regionList, int offset, int length, String type) {		
		if (length > 0) {
		    regionList.add(new TypedRegion(offset, length, type));
		}
	}	
	
	private TypedRegion getRegion(int offset) { 
	    for (TypedRegion region : m_regions) {
		    if (hitRegion(region, offset)) {
			    return region;
	        }
	    }
		assert(false);
		return null;
	}
	
	private TypedRegion[] getRegions(int offset, int length) {
		int startRegion = 0;
		int numRegions = 0;
		int index = 0;
				
	    for (TypedRegion region : m_regions) {
	    	if (numRegions > 0) {
	    		if (overlapsRegion(region, offset, length)) {
	    			++numRegions;
	    		} else {
	    			break;
	    		}	    		
	    	} else {	    	
		        if (hitRegion(region, offset)) {
		    	    startRegion = index;
		    	    ++numRegions;
    	        }
	    	}
	    	++index;
	    }
	    
		List<TypedRegion> list= new ArrayList<TypedRegion>();	    
	    for (index = 0; index < numRegions; ++index) {	    	
	    	TypedRegion fromRegion = m_regions[startRegion + index];
	    	
	    	int regionOffset = fromRegion.getOffset();
	    	int regionLength = fromRegion.getLength();
	    	if (index == 0) {
	    		regionOffset = Math.max(offset, regionOffset);
	    	}
	    	if (index == (numRegions - 1)) {
	    		// Use +1 since the length come in as line regions which don't include newline
	    		// whilst all regions include line length
	    		regionLength = Math.min(length + 1, regionLength);
	    	}	    	
	    	list.add(new TypedRegion(regionOffset, regionLength, fromRegion.getType()));
	    }
	    
		TypedRegion[] regions = new TypedRegion[list.size()];
		list.toArray(regions);	    
		return regions;
	}	
	
	private boolean hitRegion(TypedRegion region, int offset) {
	    if ((offset >= region.getOffset()) &&
		    (offset < (region.getOffset() + region.getLength()))) {
		    return true;
	    }
	    return false;
	}
	
	private boolean overlapsRegion(TypedRegion region, int offset, int length) {
		int start = offset;
		int end = offset + length - 1;
		
		int regionStart = region.getOffset();
		int regionEnd = region.getOffset() + region.getLength() - 1;
		
        if (((start >= regionStart) && (start < regionEnd)) ||
            ((end >= regionStart) && (start < regionEnd)) || 
            ((start < regionStart) && (end >= regionEnd))) {
        	return true;
        }
		return false;
	}
}
