package com.xmos.xta.console.displayers;

import java.util.HashMap;
import java.util.TreeSet;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeobject.AssemblyLine;
import com.xmos.xeobject.Line;
import com.xmos.xeobject.SourceFile;
import com.xmos.xeobject.SourceLine;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Route;

public class SrcDisplayer extends Displayer {    
	Console m_console;
	XeObject m_xeObject;
	int m_pathId;
	String m_fileName;
	boolean m_displayAssembly;
	
	public SrcDisplayer(Console console, XeObject xeObject, int pathId, 
			String fileName, boolean displayAssembly) {
		m_console = console;
		m_xeObject = xeObject;
		m_pathId = pathId;
		m_fileName = fileName;	
		m_displayAssembly = displayAssembly;
	}
	
    public void displayRoute(Route route, DebugInfo debugInfo, 
    		MemoryImage memoryImage, SymbolTable symbolTable) {
    	
        Block path = getPath(route, m_pathId);
    	if (path == null) {
			m_console.display(String.format("Error: invalid path id: %d\n", m_pathId));
			return;
    	}
    	          	        	        	                
        HashMap<String, TreeSet<Integer>> linesCovered = path.getSourceLinesCovered();            
        if (!linesCovered.containsKey(m_fileName)) {
            // Print the most useful error message
            for (SourceFile sourceFile : m_xeObject.getSourceFiles()) {         
                String currentSourceFileName = sourceFile.getFile().getName();
                if (m_fileName.equals(currentSourceFileName)) {
                    m_console.display(String.format("Error: The requested path does not touch the file: %s\n",
                            m_fileName));
                    return;
                }           
            }
        	m_console.display(String.format("Error: The source file has not been loaded: %s\n", m_fileName));
           	return;
        }
                                                    
        TreeSet<Integer> fileLines = linesCovered.get(m_fileName);                                                                                
		for (SourceFile sourceFile : m_xeObject.getSourceFiles()) {			
			String currentSourceFileName = sourceFile.getFile().getName();
			if (m_fileName.equals(currentSourceFileName)) {
				for (SourceLine sourceLine : sourceFile.getSourceLines()) {						
					if (fileLines.contains(sourceLine.getLineNumber())) {
						m_console.display(String.format("+"));
					}
					else {
                    	m_console.display(String.format(" "));						
					}
					
					m_console.display(String.format(" %d: %s\n", 
							sourceLine.getLineNumber(), 
							sourceLine.getText()));

					if (m_displayAssembly) {
					    for (Line assemblyLine : sourceLine.getChildern()) {					    	
							m_console.display(String.format("       -- %s\n", assemblyLine.getText())); 
							//m_console.display(String.format("       -- %s %s\n", 
							//		assemblyLine.getNmenonic(), 
							//		assemblyLine.getOperandString()));
					    }
					}
				}				
			}			
		}        
    }

	public void notifyAnalysePaths() {
	}

	public void notifyLoadExecutable() {
	}	       

    public void notifyUpdateState() {    	
    }
}
