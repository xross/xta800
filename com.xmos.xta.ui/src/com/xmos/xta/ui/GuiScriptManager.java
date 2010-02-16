package com.xmos.xta.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.scripter.Scripter;
import com.xmos.xta.ui.editors.script.InputScript;
import com.xmos.xta.ui.editors.script.ScriptEditor;

public class GuiScriptManager {
	/* Ensure there is only one editor per file */
	private static Map<String, InputScript> s_scriptInputs;

	private static String s_scriptFileName;
	
	static {
        s_scriptInputs = new HashMap<String, InputScript>();
        s_scriptFileName = "script.xta";
	}

	public static String getScriptFileName() {
		return s_scriptFileName;
	}
	
	public static IEditorInput getScriptInput(String fileName) {
		InputScript input = s_scriptInputs.get(fileName);
		if (input == null) {
			input = new InputScript(fileName);
			s_scriptInputs.put(fileName, input);
		}
		return input;
	}

	public static void generateAndOpenScript() {
		String scriptFileName = chooseScriptFileName();
		if (scriptFileName == null) {
			return;
		}

		IEditorPart scriptEditor = getScriptEditor(scriptFileName);
		if ((scriptEditor != null) && scriptEditor.isDirty()) {
			if (!loseScriptChanges()) {
				return;
			}
		}

	    Scripter scripter = GuiEngine.getEngine().getScripter();
		String script = scripter.dump();
		if (!doSaveAs(scriptFileName, script)) {
			return;
		}
		openScriptFile(scriptFileName);
	}
	
	public static boolean openScriptFile(String scriptFileName) {
		boolean success = true;
		IEditorInput input = getScriptInput(scriptFileName);
		
		IEditorPart scriptEditor = getScriptEditor(scriptFileName);
		if (scriptEditor != null) {
			((ScriptEditor)scriptEditor).setInput(input);
			
		} else {
			try {
				final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				page.openEditor(input, ScriptEditor.ID);
			    s_scriptFileName = scriptFileName;
			} catch (PartInitException e) {
				success = false;
			}
		}
		return success;
	}
	
	public static String chooseScriptFileName() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell parent = new Shell(display);
		FileDialog dialog = new FileDialog(parent, SWT.SAVE);
		dialog.setFilterNames(new String[] { "XTA script files", "All Files" });
		dialog.setFilterExtensions(new String[] { "*.xta", "*.*" });
		dialog.setFileName(s_scriptFileName);
		String scriptFileName = dialog.open();
		return scriptFileName;
	}

	public static boolean doSaveAs(String fileName, String script) {
		File file = new File(fileName);
		if (file.exists()) {
			if (!overwriteFile(fileName)) {
				return false;
			}
		}
		return writeToFile(fileName, script);
	}

	public static boolean writeToFile(String fileName, String contents) {
		try {
		    FileWriter fstream = new FileWriter(fileName);
		    BufferedWriter out = new BufferedWriter(fstream);
		    out.write(contents);
		    out.close();
		} catch (Exception e) {
			GuiEngine.getEngine().notify(GuiEngine.s_guiSrc, Notifier.NOTIFY_ERROR,
					String.format("Failed to write to '%s'", fileName));
			return false;
		}
	    return true;
	}

	private static IEditorPart getScriptEditor(String scriptFileName) {
		File file = new File(scriptFileName);
		String fileName = file.getName();
		
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorReference[] editorReferences = page.getEditorReferences();
		for (IEditorReference editorRef : editorReferences) {
			if (editorRef.getPartName().equals(fileName)) {
				return editorRef.getEditor(true);
			}
		}
		return null;
	}

	/**
	 * Prompt the user to see whether they are happy to lose their unsaved script changes.
	 * 
	 * Returns true if ok to lose changes, false otherwise.
	 */
	private static boolean loseScriptChanges() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		Shell shell = new Shell(display);

		MessageDialog dialog = new MessageDialog(shell, "Script not saved",
				null, "Are you sure you want to lose changes?",
				MessageDialog.WARNING, new String[] {"Yes", "No"}, 0);
		int result = dialog.open();
		if (result == 0) {
			return true;
		}
		return false;
	}

	private static boolean overwriteFile(String fileName) {
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
