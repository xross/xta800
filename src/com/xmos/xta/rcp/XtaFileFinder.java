package com.xmos.xta.rcp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

import com.xmos.xeobject.FileFinder;

public class XtaFileFinder implements FileFinder {
    public static final boolean DEBUG = false;
    
    private List<File> m_fileCache;
    
    public XtaFileFinder() {
    	m_fileCache = new ArrayList<File>();
    }
    
	public File openFileFinder(String fileToFind) {
		if (DEBUG) System.out.printf("XtaFileFinder: openFileFinder: %s\n", fileToFind);
		
		File cachedFile = findFileInCache(fileToFind);
		if (cachedFile != null) {
			return cachedFile;
		}
		
        FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), SWT.OPEN);
        fileDialog.setText("Open Source File: " + fileToFind);
        String[] filterExt = { fileToFind };
        fileDialog.setFilterExtensions(filterExt);
        String xeFileName = fileDialog.open();        
        if (xeFileName != null) {
        	File file = new File(xeFileName);
        	m_fileCache.add(file);
 		    return file;
        }
        return null;
	}

    private File findFileInCache(String fileToFind) {    	
	    for (File file : m_fileCache) {
	    	if (file.getName().equals(fileToFind)) {
	    		return file;
	    	}		
	    }
	    return null;
    }
}
