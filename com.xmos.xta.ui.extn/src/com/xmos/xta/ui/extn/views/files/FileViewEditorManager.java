package com.xmos.xta.ui.extn.views.files;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sourcemapper.SourceFile;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.GuiFileFinder;
import com.xmos.xta.ui.editors.XtaEditor;
import com.xmos.xta.ui.editors.source.XtaSourceEditor;
import com.xmos.xta.ui.input.InputSource;

public class FileViewEditorManager {
    // Keep a map to ensure source files can't be opened twice
    private static Map<String, IEditorInput> s_editorInputs;
    private static FileViewEditorManager s_editorManager;
    
	static {
		s_editorManager = new FileViewEditorManager();
		s_editorInputs = new HashMap<String, IEditorInput>();        
	}

	public static FileViewEditorManager getFileViewEditorManager() {
		return s_editorManager;		
	}
	
	public static void clear() {
        closeEditors();
		s_editorInputs.clear();
	}

	public static void refresh() {
		refreshEditors();
	}

	public static IEditorInput getInput(String fileName) {
		if (s_editorInputs.containsKey(fileName)) {
			return s_editorInputs.get(fileName);
		} else {
			SourceFile sourceFile = getSourceFile(fileName);
			if (sourceFile != null) {
				IEditorInput input = new InputSource(sourceFile); 
				s_editorInputs.put(fileName, input);
				return input;
			} else {
				return null;
			}
		}
	}

	private static SourceFile getSourceFile(String fileName) {
		Engine engine = GuiEngine.getEngine();
		SourceMapper sourceMapper = engine.getSourceMapper();
		SourceFile sourceFile = sourceMapper.getSourceFile(fileName);
		if (sourceFile.getDebugFile().getFile() == null) {
			File file = sourceMapper.findFileForSourceFile(sourceFile, new GuiFileFinder());
			if (file == null)
				return null;

			engine.updateSourceMappers(GuiEngine.s_guiSrc, GuiEngine.getNotifier(), fileName, file);
		}
		return sourceFile;
	}

    private static void closeEditors() {
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		// Need to close any xta editors already open..
		for (IEditorReference editorReference : page.getEditorReferences()) {
			if (editorReference.getId().equals(XtaSourceEditor.ID)) {
				page.closeEditor(editorReference.getEditor(false), false);
			}
		}
    }

	private static void refreshEditors() {
		List<String> toRemove = new ArrayList<String>();
		for (String fileName : s_editorInputs.keySet()) {
			if (!refreshEditor(fileName)) {
				toRemove.add(fileName);
			}
		}
		
		for (String fileName : toRemove) {
			s_editorInputs.remove(fileName);
		}
	}

	private static boolean refreshEditor(String fileName) {
		SourceFile sourceFile = getSourceFile(fileName);
		if (sourceFile == null) {
			return false;
		}

		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		for (IEditorReference editorReference : page.getEditorReferences()) {
			if (editorReference.getId().equals(XtaSourceEditor.ID)) {
				XtaEditor editor = (XtaEditor)editorReference.getEditor(false);
				if (editor.getPartName().equals(sourceFile.getShortFileName())) {
					IEditorInput input = new InputSource(sourceFile); 
					s_editorInputs.put(fileName, input);
					editor.refreshInput(input);
					return true;
				}
			}
		}
		return false;
	}
}
