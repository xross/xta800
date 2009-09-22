package com.xmos.xta.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.core.sourcemapper.AssemblyLine;
import com.xmos.xta.core.sourcemapper.Function;

public class InputDisassembly implements IEditorInput {
    public static final boolean DEBUG = false;
    public static final String s_headerType = XtaDocumentPartitioner.DISASSEMBLYFUNCTION;
    public static final String s_lineType = XtaDocumentPartitioner.DISASSEMBLY;

    private Input m_input;    
    
    public InputDisassembly() {	
		if (DEBUG) System.out.printf("InputDisassembly()\n");	
		
		int lineIndex = 0;
		m_input = new Input(s_headerType, s_lineType);
        for (Function function : GuiEngine.getEngine().getSourceMapper().getFunctions()) {        	
    		String headerRef = String.format("-");
        	m_input.addHeader(function.getText() + ":", headerRef, lineIndex, 0);        	
        	++lineIndex;

            InputSection section = m_input.getLastSection();
            boolean firstLine = true;
        	for (AssemblyLine line : function.getChildern()) {
        		long pc = line.getLineNumber();
        		String str = "";
        		if (firstLine){
        			str = String.format("%s 0x%05x %s", line.getEmptyText(), pc, line.getText());
        			firstLine = false;
        		} else {
        			str = String.format("%s 0x%05x %s", line.getPreText(), pc, line.getText());
        		}
        		String childRef = String.format("0x%05x", pc);
        		m_input.addLine(str, childRef, pc, lineIndex);
        		++lineIndex;
            	m_input.addPcToSection(pc, section);
        	}
        }
        updateLineNumberMapping();
	}
    
    public void updateLineNumberMapping() {
		long lineIndex = 0;
    	for (InputSection inputSection : m_input.getSections()) {
        	m_input.addLineNumberMapping(lineIndex, lineIndex + 1);
        	++lineIndex;

        	long numInputLines = inputSection.getLines().size();
        	while (numInputLines > 0) {
            	m_input.addLineNumberMapping(lineIndex, lineIndex + 1);
        		++lineIndex;  
        		--numInputLines;
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
