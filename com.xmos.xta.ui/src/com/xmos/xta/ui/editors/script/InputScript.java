package com.xmos.xta.ui.editors.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class InputScript implements IEditorInput {
    public static final boolean DEBUG = false;
    
    private String m_fileName;
    
    public InputScript(String fileName) {	
		if (DEBUG) System.out.printf("InputScript()\n");	
		m_fileName = fileName;
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
		File file = new File(m_fileName);
		return file.getName();
	}
	
	public IPersistableElement getPersistable() {
		if (DEBUG) System.out.printf("InputScript: getPersistable\n");			
		return null;
	}

	public String getToolTipText() {
		if (DEBUG) System.out.printf("InputScript: getToolTipText\n");			
		return m_fileName;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (DEBUG) System.out.printf("InputScript: getAdapter\n");
		if (adapter == String.class) {
			String input = "";
		    try {
		    	FileReader fstream = new FileReader(m_fileName);
		    	BufferedReader in = new BufferedReader(fstream);

			    String line = in.readLine();
			    while (line != null) {
			    	input += line + "\n";
			    	line = in.readLine();
			    }
				in.close();
			} catch (IOException e) { }
			return input;
		}
		
		return null;
	}
}
