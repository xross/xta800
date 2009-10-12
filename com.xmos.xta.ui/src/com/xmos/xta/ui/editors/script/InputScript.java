package com.xmos.xta.ui.editors.script;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.xmos.xta.core.engine.Scripter;
import com.xmos.xta.ui.GuiEngine;

public class InputScript implements IEditorInput {
    public static final boolean DEBUG = false;
    
    private String m_input;
    
    public InputScript() {	
		if (DEBUG) System.out.printf("InputScript()\n");	
		
		Scripter scripter = new Scripter(GuiEngine.getEngine());
		m_input = scripter.createScript();
	}
    
	public boolean exists() {
		if (DEBUG) System.out.printf("InputScript: exists\n");			
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		if (DEBUG) System.out.printf("InputScript: getImageDescriptor\n");			
		return null;
	}

	public String getName() {
		if (DEBUG) System.out.printf("InputScript: getName\n");			
		return "Script";
	}
	
	public IPersistableElement getPersistable() {
		if (DEBUG) System.out.printf("InputScript: getPersistable\n");			
		return null;
	}

	public String getToolTipText() {
		if (DEBUG) System.out.printf("InputScript: getToolTipText\n");			
		return getName();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (DEBUG) System.out.printf("InputScript: getAdapter\n");	
		if (adapter == String.class) {
			return m_input;
		}		
		return null;
	}
}
