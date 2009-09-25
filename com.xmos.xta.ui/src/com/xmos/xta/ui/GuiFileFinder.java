package com.xmos.xta.ui;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.core.engine.sourcemapper.FileFinder;

public class GuiFileFinder extends FileFinder {  
	private String m_xeFileName;
	
	public GuiFileFinder() {
		m_xeFileName = "";
	}
	
	public File openFileFinder(String compilationDir, String fileName) {		
		File cachedFile = findFileInCache(compilationDir, fileName);
		if (cachedFile != null) {
			return cachedFile;
		}
		
		File srcPathFile = findFileInSrcPaths(GuiEngine.getEngine().getSrcPaths(), fileName);
		if (srcPathFile != null) {
			return srcPathFile;
		}
		
		File userSpecifiedFile = askUserForFile(compilationDir, fileName);
		if (userSpecifiedFile != null) {
			return userSpecifiedFile;
		}
		return null;			
	}
		
	private File askUserForFile(final String compilationDir, final String fileName) {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
		    	File sourceFile = new File(compilationDir + File.separator + fileName);		
			    Display display = PlatformUI.getWorkbench().getDisplay();
			    Shell parent = new Shell(display);
		        FileDialog fileDialog = new FileDialog(parent, SWT.OPEN);
		        fileDialog.setText("Open Source File: " + sourceFile.getName());
		        String[] filterExt = {sourceFile.getName()};		    
		        fileDialog.setFilterExtensions(filterExt);
		        m_xeFileName = fileDialog.open();      
			}
		});
				
        if (m_xeFileName != null) {
        	File file = new File(m_xeFileName);
        	addToFileCache(compilationDir, fileName, file);
		    return file;
        } else {
        	return null;
        }
	}		
}
