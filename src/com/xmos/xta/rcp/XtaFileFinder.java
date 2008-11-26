package com.xmos.xta.rcp;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

import com.xmos.xeobject.FileFinder;

public class XtaFileFinder implements FileFinder {
    public static final boolean DEBUG = false;
    
	public File openFileFinder(String fileToFind) {
		if (DEBUG) System.out.printf("XtaFileFinder: openFileFinder: %s\n", fileToFind);					
        FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), SWT.OPEN);
        fileDialog.setText("Open Source File: " + fileToFind);
        String[] filterExt = { fileToFind };
        fileDialog.setFilterExtensions(filterExt);
        String xeFileName = fileDialog.open();        
        if (xeFileName != null) {
 		    return new File(xeFileName);
        }
        return null;
	}
}
