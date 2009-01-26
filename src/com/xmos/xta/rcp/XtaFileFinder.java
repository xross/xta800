package com.xmos.xta.rcp;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.xmos.xeobject.FileFinder;

public class XtaFileFinder extends FileFinder {    
	public File openFileFinder(String compilationDir, String fileName) {
		
		if (findFileInCancelCache(compilationDir, fileName)) {
			return null;
		}
		
		File cachedFile = findFileInCache(compilationDir, fileName);
		if (cachedFile != null) {
			return cachedFile;
		}
		
		File srcPathFile = findFileInSrcPaths(XtaEngine.getEngine().getSrcPaths(), fileName);
		if (srcPathFile != null) {
			return srcPathFile;
		}
		
		File userSpecifiedFile = askUserForFile(compilationDir, fileName);
		if (userSpecifiedFile != null) {
			return userSpecifiedFile;
		}
		return null;			
	}
		
	private File askUserForFile(String compilationDir, String fileName) {
    	File sourceFile = new File(compilationDir + File.separator + fileName);		
	    Display display = PlatformUI.getWorkbench().getDisplay();
	    Shell parent = new Shell(display);
        FileDialog fileDialog = new FileDialog(parent, SWT.OPEN);
        fileDialog.setText("Open Source File: " + sourceFile.getName());
        String[] filterExt = {sourceFile.getName()};
    
        fileDialog.setFilterExtensions(filterExt);
        String xeFileName = fileDialog.open();      
        if (xeFileName != null) {
        	File file = new File(xeFileName);
        	addToFileCache(compilationDir, fileName, file);
		    return file;
        } else {
        	addToCancelCache(compilationDir, fileName);
        	return null;
        }
	}		
}
