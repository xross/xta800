package com.xmos.xta.rcp.editors;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.xmos.xeobject.Line;
import com.xmos.xeobject.SourceFile;
import com.xmos.xeobject.SourceLine;

public class InputSource implements IEditorInput {
    public static final boolean DEBUG = false;
    public static final String s_headerType = XtaDocumentPartitioner.SOURCE;
    public static final String s_lineType = XtaDocumentPartitioner.DISASSEMBLY;

    private Input m_input;
    
    public InputSource(SourceFile sourceFile) {	
		if (DEBUG) System.out.printf("InputSource()\n");
		
		File file = sourceFile.getFile();
		
		try {
			file = file.getCanonicalFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String fileName = file.getAbsolutePath();
		
		int lineIndex = 0;
		m_input = new Input(s_headerType, s_lineType);
        for (SourceLine sourceLine : sourceFile.getSourceLines()){        	
    		String headerRef = String.format("%s:%d", fileName, sourceLine.getLineNumber());
        	m_input.addHeader(sourceLine.getText(), headerRef, lineIndex);
        	++lineIndex;
        	
            for (Line line : sourceLine.getChildern()) {
        		String str = String.format("    0x%05x %s", line.getLineNumber(), line.getText());
        		String childRef = String.format("0x%05x", line.getLineNumber());
            	m_input.addLine(str, childRef, line.getLineNumber(), lineIndex);                	
        		++lineIndex;        		
            }
        }
	}
	
	public boolean exists() {
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
