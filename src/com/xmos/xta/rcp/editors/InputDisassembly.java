package com.xmos.xta.rcp.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.xmos.xeobject.Function;
import com.xmos.xeobject.Line;
import com.xmos.xta.rcp.XtaEngine;

public class InputDisassembly implements IEditorInput {
    public static final boolean DEBUG = false;
    public static final String s_headerType = XtaDocumentPartitioner.SOURCE;
    public static final String s_lineType = XtaDocumentPartitioner.DISASSEMBLY;

    private Input m_input;    
    
    public InputDisassembly() {	
		if (DEBUG) System.out.printf("InputDisassembly()\n");	
		
		int lineIndex = 0;
		m_input = new Input(s_headerType, s_lineType);
        for (Function function : XtaEngine.getEngine().getXeObject().getFunctions()) {        	
    		String headerRef = String.format("-");
        	m_input.addHeader(function.getText() + ":", headerRef, lineIndex);
        	
        	m_input.addLineNumberMapping(lineIndex, lineIndex + 1);
        	++lineIndex;

        	for (Line line : function.getChildern()) {
        		String str = String.format("    0x%05x %s", line.getLineNumber(), line.getText());
        		String childRef = String.format("0x%05x", line.getLineNumber());
        		m_input.addLine(str, childRef, line.getLineNumber(), lineIndex);

            	m_input.addLineNumberMapping(lineIndex, lineIndex + 1);
        		++lineIndex;        		
        	}
        }				
	}
	
	public boolean exists() {
		if (DEBUG) System.out.printf("InputDisassembly: exists\n");			
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		if (DEBUG) System.out.printf("InputDisassembly: getImageDescriptor\n");			
		return null;
	}

	public String getName() {
		if (DEBUG) System.out.printf("InputDisassembly: getName\n");			
		return "Source";
	}

	public IPersistableElement getPersistable() {
		if (DEBUG) System.out.printf("InputDisassembly: getPersistable\n");			
		return null;
	}

	public String getToolTipText() {
		if (DEBUG) System.out.printf("InputDisassembly: getToolTipText\n");			
		return getName();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (DEBUG) System.out.printf("InputDisassembly: getAdapter\n");	
		if (adapter == Input.class) {
			return m_input;
		}		
		return null;
	}
}
