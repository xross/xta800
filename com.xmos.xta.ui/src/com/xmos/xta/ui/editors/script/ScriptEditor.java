package com.xmos.xta.ui.editors.script;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.GuiScriptManager;

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
    
    public void dispose() {
    	GuiEngine.removeBroadcastListener(this);
    	super.dispose();
    }
    
	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("ScriptEditor: propertyChange: %s\n", event.getProperty());	
	}

    public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("ScriptEditor: createPartControl\n"); 
    	super.createPartControl(parent);
    }

	public boolean isEditable() {
		if (DEBUG) System.out.printf("ScriptEditor: isEditable\n");
		return true;
	}

	public boolean isSaveAsAllowed() {
		return true;
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
	
	public void doSaveAs() {
		String newFileName = GuiScriptManager.chooseScriptFileName();
		if (newFileName == null) {
			return;
		}
		File file = new File(newFileName);
		if (file.exists()) {
			if (!overwriteFile(newFileName)) {
				return;
			}
		}

		String contents = getSourceViewer().getDocument().get();
		GuiScriptManager.writeToFile(newFileName, contents);
		
		IEditorInput newInput = GuiScriptManager.getScriptInput(newFileName);
		setInput(newInput);
	}

	private boolean overwriteFile(String fileName) {
		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell shell = new Shell(display);

		MessageDialog dialog = new MessageDialog(shell, "File exists",
				null, fileName + " already exists. Do you want to replace it?",
				MessageDialog.WARNING, new String[] {"Yes", "No"}, 0);
		int result = dialog.open();
		if (result == 0) {
			return true;
		}
		return false;
	}
}
