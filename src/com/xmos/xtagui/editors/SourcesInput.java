package com.xmos.xtagui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class SourcesInput implements IEditorInput {
    public static final boolean DEBUG = false;

    public SourcesInput() {	
		if (DEBUG) System.out.printf("SourcesInput()\n");			
	}
	
	public boolean exists() {
		if (DEBUG) System.out.printf("SourcesInput: exists\n");			
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		if (DEBUG) System.out.printf("SourcesInput: getImageDescriptor\n");			
		return null;
	}

	public String getName() {
		if (DEBUG) System.out.printf("SourcesInput: getName\n");			
		return "Source";
	}

	public IPersistableElement getPersistable() {
		if (DEBUG) System.out.printf("SourcesInput: getPersistable\n");			
		return null;
	}

	public String getToolTipText() {
		if (DEBUG) System.out.printf("SourcesInput: getToolTipText\n");			
		return getName();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (DEBUG) System.out.printf("SourcesInput: getAdapter\n");			
		return null;
	}

}
