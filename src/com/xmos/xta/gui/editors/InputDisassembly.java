package com.xmos.xta.gui.editors;

import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.sourcemapper.Function;
import com.xmos.xta.sourcemapper.Line;

public class InputDisassembly implements IEditorInput {
    public static final boolean DEBUG = false;
    public static final String s_headerType = XtaDocumentPartitioner.SOURCE;
    public static final String s_lineType = XtaDocumentPartitioner.DISASSEMBLY;

    private Input m_input;    
    private HashMap<Integer, InputLine> m_pcToLine;
    
    public InputDisassembly() {	
		if (DEBUG) System.out.printf("InputDisassembly()\n");	
		
		int lineIndex = 0;
		m_input = new Input(s_headerType, s_lineType);
        for (Function function : GuiEngine.getEngine().getSourceMapper().getFunctions()) {        	
    		String headerRef = String.format("-");
        	m_input.addHeader(function.getText() + ":", headerRef, lineIndex, 0);        	
        	++lineIndex;

            InputSection section = m_input.getLastSection();
        	for (Line line : function.getChildern()) {
        		int pc = line.getLineNumber();
				String str = String.format("    0x%05x %s", pc, line.getText());
        		String childRef = String.format("0x%05x", pc);
        		m_input.addLine(str, childRef, pc, lineIndex);
        		++lineIndex;
            	m_input.addPcToSecion(pc, section);
        	}
        }
        updateLineNumberMapping();
        m_pcToLine = new HashMap<Integer, InputLine>();
        for(InputSection inputSection : m_input.getSections()){
        	m_pcToLine.putAll(inputSection.getPcs());
        }
	}
    
    public HashMap<Integer, InputLine> getPcsToLines(){
    	return m_pcToLine;
    }
    
    public void updateLineNumberMapping() {
		int lineIndex = 0;
    	for (InputSection inputSection : m_input.getSections()) {
        	m_input.addLineNumberMapping(lineIndex, lineIndex + 1);
        	++lineIndex;

        	int numInputLines = inputSection.getLines().size();
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
