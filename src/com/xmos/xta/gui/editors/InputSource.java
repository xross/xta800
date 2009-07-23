package com.xmos.xta.gui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.xmos.xta.sourcemapper.AssemblyLine;
import com.xmos.xta.sourcemapper.Line;
import com.xmos.xta.sourcemapper.SourceFile;
import com.xmos.xta.sourcemapper.SourceLine;

public class InputSource implements IEditorInput {
    public static final boolean DEBUG = false;
    public static final String s_headerType = XtaDocumentPartitioner.SOURCE;
    public static final String s_lineType = XtaDocumentPartitioner.DISASSEMBLY;

    private Input m_input;
    
    
    
    public InputSource(SourceFile sourceFile) {	
		if (DEBUG) System.out.printf("InputSource()\n");
				
		int lineIndex = 0;
		m_input = new Input(s_headerType, s_lineType);
        for (SourceLine sourceLine : sourceFile.getSourceLines()){   

        	String headerRef = String.format("%s:%d", sourceFile.getFileName(), sourceLine.getLineNumber());
        	m_input.addHeader(sourceLine.getText(), headerRef, lineIndex, sourceLine.getLineNumber());        	
        	++lineIndex;
        	boolean firstLine = true;
            for (AssemblyLine line : sourceLine.getChildern()) {
        		int pc = line.getLineNumber();
        		String str = "";
        		if(firstLine){
        			str = String.format("%s 0x%05x %s", line.getEmptyText(), pc, line.getText());
        			firstLine = false;
        		} else {
        			str = String.format("%s 0x%05x %s", line.getPreText(), pc, line.getText());
        		}
        		String childRef = String.format("0x%05x", pc);
        		m_input.addLine(str, childRef, pc, lineIndex);
        		++lineIndex;
        	}
            // Only associate statement PCs from debug with this source line
            InputSection section = m_input.getLastSection();
            for (Integer pc : sourceLine.getStatementPcs()) {
            	m_input.addPcToSecion(pc, section);
            }
        }
        updateLineNumberMapping();
        
	}
    
   
    
    public void updateLineNumberMapping() {
		int lineIndex = 0;
		int sourceLineIndex = 1;
    	for (InputSection inputSection : m_input.getSections()) {
        	m_input.addLineNumberMapping(lineIndex, sourceLineIndex);
        	lineIndex += inputSection.getLines().size() + 1;
        	++sourceLineIndex;
    	}
    }    
	
	public boolean exists(){
		if (DEBUG) System.out.printf("InputSource: exists\n");			
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		if (DEBUG) System.out.printf("InputSource: getImageDescriptor\n");			
		return null;
	}

	public String getName() {
		if (DEBUG) System.out.printf("InputSource: getName\n");			
		return "Source";
	}

	public IPersistableElement getPersistable() {
		if (DEBUG) System.out.printf("InputSource: getPersistable\n");			
		return null;
	}

	public String getToolTipText() {
		if (DEBUG) System.out.printf("InputSource: getToolTipText\n");			
		return getName();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (DEBUG) System.out.printf("InputSource: getAdapter\n");			
		if (adapter == Input.class) {
			return m_input;
		}		
		return null;
	}

}
