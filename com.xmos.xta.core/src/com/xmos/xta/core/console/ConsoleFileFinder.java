package com.xmos.xta.core.console;

import java.io.File;

import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.sourcemapper.FileFinder;

public class ConsoleFileFinder extends FileFinder {
	private CommandSource m_src;
    private Engine m_engine; 
    
    public ConsoleFileFinder(CommandSource src, Engine engine) {
    	m_src = src;
    	m_engine = engine;
    }
    
	public File openFileFinder(String compilationDir, String fileName) {
		
		if(!findFileInCancelCache(compilationDir, fileName)){
			File srcPathFile = findFileInSrcPaths(m_engine.getSrcPaths(), fileName);
			if (srcPathFile != null) {
				return srcPathFile;
			}
					
			addToCancelCache(compilationDir, fileName);
			m_engine.notify(m_src, Notifier.NOTIFY_WARNING,
					String.format("failed to find source file: %s", fileName));
		}
        return null;
	}	
}
