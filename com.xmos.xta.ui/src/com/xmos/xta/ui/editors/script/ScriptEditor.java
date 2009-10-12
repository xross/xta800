package com.xmos.xta.ui.editors.script;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;

public class ScriptEditor extends TextEditor implements IPropertyChangeListener {
    private static final boolean DEBUG = false;
    
    public static final String ID = "com.xmos.xta.ui.editors.script.ScriptEditor";
    public static final String EDITORNAME = "Script";
    public static final String RUN_IN_CONSOLE = "com.xmos.xta.ui.action.RunInConsole";
        
    public ScriptEditor() {
		if (DEBUG) System.out.printf("ScriptEditor()\n");
		GuiEngine.addBroadcastListener(this);
    	setDocumentProvider(new DocumentProvider());
    	setSourceViewerConfiguration(new Configuration());
    }
    
	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("ScriptEditor: propertyChange: %s\n", event.getProperty());	

		if (event.getProperty().equals(Broadcaster.BROADCAST_REFRESH_SCRIPT)) {
			setInput(GuiEngine.getScriptInput());
		}
	}
	
	public boolean isEditable() {
		if (DEBUG) System.out.printf("ScriptEditor: isEditable\n"); 
		return true;
	}

	public boolean isDirty() {		
		if (DEBUG) System.out.printf("ScriptEditor: isDirty\n"); 
		return false;
	}
	
	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		if (DEBUG) System.out.printf("ScriptEditor: editorContextMenuAboutToShow\n");
		super.editorContextMenuAboutToShow(menu);

		menu.add(new Separator(ITextEditorActionConstants.GROUP_REST));
     	addAction(menu, RUN_IN_CONSOLE);
	}
	
	protected void createActions() {
		if (DEBUG) System.out.printf("ScriptEditor: createActions\n"); 
     	super.createActions();
     	
     	setAction(RUN_IN_CONSOLE, new Action("Run In Console") {
     		public void run() {     			
   				ISourceViewer viewer = getSourceViewer();
   				if (viewer != null) {
   	     			String command = viewer.getDocument().get();     			
   					GuiEngine.runCommandInConsole(command);     			   					
   				}
     		}
     	});
    }    	
}
