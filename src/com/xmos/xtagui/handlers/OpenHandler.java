package com.xmos.xtagui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.xmos.xta.engine.Engine;
import com.xmos.xtagui.XtaEngine;
import com.xmos.xtagui.editors.Sources;
import com.xmos.xtagui.editors.SourcesInput;

public class OpenHandler extends AbstractHandler implements IHandler {
    public static final boolean DEBUG = true;
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		
		createEditor(event);
		
        FileDialog fileDialog = new FileDialog(window.getShell(), SWT.OPEN);
        fileDialog.setText("Open Xe File");
        String[] filterExt = { "*.xe" };
        fileDialog.setFilterExtensions(filterExt);
        String xeFileName = fileDialog.open();
        
        if (xeFileName != null) {
        	if (DEBUG) {
        		System.out.printf("OpenHandler: File open: %s\n", xeFileName);
        	}
        	Engine engine = XtaEngine.getEngine();
        	boolean success = engine.loadExecutable(xeFileName);
        	if (DEBUG) {
        		if (success) {
                    System.out.printf("OpenHandler: Successfully loaded executable: %s\n", xeFileName);
        		} else {
                    System.out.printf("OpenHandler: Failed to open file: %s\n", xeFileName);
        		}
        	}
        } else {
        	if (DEBUG) {
        		System.out.printf("OpenHandler: File open: null\n");
        	}
        }
		return null;
	}

	private void createEditor(ExecutionEvent event) {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		try {
			IEditorInput sourcesInput = new SourcesInput();
			page.openEditor(sourcesInput, Sources.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}
