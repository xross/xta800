package com.xmos.xta.core.console.commands.list;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.libs.dwarfhandler.DebugFile;
import com.xmos.libs.xeloader.DebugInfo;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class Sources extends Command {
	public Sources() {
	}

    public int numArgs() {
    	return 0;
    }

    public String name() {
    	return "sources";
    }

    public String commandLine() {
    	return "sources";
    }

    public String description(Engine engine, boolean detailed) {
        return "sources: Lists the source files";
    }

    public Command clone(String[] args, int index) {
        return new Sources();
    }

    public void execute(Shell shell, final Engine engine, final Console console) {
		if (!isExecutableLoaded(engine, console))
			return;

		Collection<SourceFile> sourcesFiles = getSourceFiles(engine);
		for (SourceFile sourceFile : sourcesFiles) {
	    	console.display(String.format("compilation dir: %s, filename: %s",
	    			sourceFile.m_baseDir, sourceFile.m_fileName));

  	    	if (!sourceFile.m_found) {
  	    		console.display(" (File not found)");
  	    	}
  	    	console.display("\n");			
		}
    }
    
    private Collection<SourceFile> getSourceFiles(Engine engine) {
    	Collection<SourceFile> sourceFiles = new ArrayList<SourceFile>();

		for (NodeCorePair activeCore : engine.getActiveCores()) {			
		    DebugInfo debugInfo = engine.getXeLoader().getDebugInfo(activeCore.getNode(), activeCore.getCore());
		    assert(debugInfo != null);

		    for (DebugFile debugFile : debugInfo.getDebugEngine().getDebugFiles()) {
		    	SourceFile sourceFile = new SourceFile(debugFile.getBaseDir(), debugFile.getFileName(), debugFile.getFile() != null);

		    	if (!sourceFiles.contains(sourceFile)) {
			    	sourceFiles.add(sourceFile);		    		
		    	}
	    	}				
		}   		
		return sourceFiles;
    }
    
    private static class SourceFile {
    	public String m_baseDir;
    	public String m_fileName;
    	public boolean m_found;
    	
    	public SourceFile(String baseDir, String fileName, boolean found) {
    		m_baseDir = baseDir;
    		m_fileName = fileName;
    		m_found = found;
    	}  
    	
        public boolean equals(Object obj) {
        	if (obj == null)
        	return false;

            if(!(obj instanceof SourceFile))
                return false;

            SourceFile sourceFile = (SourceFile)obj;
            return m_baseDir.equals(sourceFile.m_baseDir) &&
                   m_fileName.equals(sourceFile.m_fileName);
         }
    }
}
