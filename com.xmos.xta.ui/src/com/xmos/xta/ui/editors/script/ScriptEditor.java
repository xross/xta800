package com.xmos.xta.ui.editors.script;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.editors.text.TextEditor;

import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;

public class ScriptEditor extends TextEditor implements IPropertyChangeListener {
    public static final String ID = "com.xmos.xta.ui.editors.script.ScriptEditor";
    public static final String EDITORNAME = "Script";
    
    private static final boolean DEBUG = false;
    
    public ScriptEditor() {
		if (DEBUG) System.out.printf("ScriptEditor()\n");
		GuiEngine.addBroadcastListener(this);
    	setDocumentProvider(new DocumentProvider());
    }
    
	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("ScriptEditor: propertyChange: %s\n", event.getProperty());	

		if (event.getProperty().equals(Broadcaster.BROADCAST_REFRESH_SCRIPT)) {
			setInput(GuiEngine.getScriptInput());
		}
	}
}
