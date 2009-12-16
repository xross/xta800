package com.xmos.xta.ui.input;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.xmos.xta.core.engine.sourcemapper.SourceFile;
import com.xmos.xta.core.engine.sourcemapper.SourceLine;
import com.xmos.xta.ui.editors.XtaDocumentPartitioner;

public class InputSource implements IEditorInput {
    public static final boolean DEBUG = false;
    public static final String s_headerType = XtaDocumentPartitioner.SOURCE;
    public static final String s_lineType = XtaDocumentPartitioner.DISASSEMBLY;

    private Input m_input;
    private SourceFile m_sourceFile;
    
    public InputSource(SourceFile sourceFile) {	
		if (DEBUG) System.out.printf("InputSource( " + sourceFile.getFileName() + ")\n");
        m_sourceFile = sourceFile;
				
		long lineIndex = 0;
		m_input = new Input(s_headerType, s_lineType);
        for (SourceLine sourceLine : sourceFile.getSourceLines()){   
        	String headerRef = String.format("%s:%d", sourceFile.getFileName(), sourceLine.getLineNumber());
        	m_input.addHeader(sourceLine.getText(), headerRef, lineIndex, sourceLine.getLineNumber());        	
        	++lineIndex;
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

	public SourceFile getSourceFile() {
		return m_sourceFile;
	}
}
