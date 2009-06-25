package com.xmos.xta.console;

import java.io.File;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.sourcemapper.FileFinder;

public class ConsoleFileFinder extends FileFinder {
    private Engine m_engine; 
    private Console m_console;
    
    public ConsoleFileFinder(Engine engine, Console console) {
    	m_engine = engine;
    	m_console = console;
    }
    
	public File openFileFinder(String compilationDir, String fileName) {
		if (findFileInCancelCache(compilationDir, fileName)) {
			return null;
		}
				
		File srcPathFile = findFileInSrcPaths(m_engine.getSrcPaths(), fileName);
		if (srcPathFile != null) {
			return srcPathFile;
		}
				
		addToCancelCache(compilationDir, fileName);
		m_console.display(String.format("Warning: Failed to find source file: %s\n", fileName));
        return null;
	}	
}
