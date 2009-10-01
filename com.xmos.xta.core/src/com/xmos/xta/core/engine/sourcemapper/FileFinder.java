package com.xmos.xta.core.engine.sourcemapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.xmos.libs.dwarfhandler.DebugFile;

public abstract class FileFinder {
    private List<DebugFile> m_fileCache;
    private List<DebugFile> m_cancelCache;

    public FileFinder() {
    	m_fileCache = new ArrayList<DebugFile>();
    	m_cancelCache = new ArrayList<DebugFile>();
    }
    
	public abstract File openFileFinder(String compilationDir, String fileName);
    	
	protected void addToFileCache(String compilationDir, String fileName, File file) {
    	DebugFile debugFile = new DebugFile(compilationDir, fileName, "");
    	debugFile.setFile(file);
    	m_fileCache.add(debugFile);		
	}
	
	protected File findFileInCache(String compilationDir, String fileName) {    	
	    for (DebugFile debugFile : m_fileCache) {
	    	if (debugFile.getBaseDir().equals(compilationDir) &&
	            debugFile.getFileName().equals(fileName)) {
	    		return debugFile.getFile();
	    	}		
	    }
	    return null;
    }
    
	protected void addToCancelCache(String compilationDir, String fileName) {
    	DebugFile debugFile = new DebugFile(compilationDir, fileName, "");
    	m_cancelCache.add(debugFile);		
	}	
	
	protected boolean findFileInCancelCache(String compilationDir, String fileName) {
	    for (DebugFile debugFile : m_cancelCache) {
	    	if (debugFile.getBaseDir().equals(compilationDir) &&
	            debugFile.getFileName().equals(fileName)) {
	    		return true;
	    	}		
	    }
	    return false;		
	}	
}
