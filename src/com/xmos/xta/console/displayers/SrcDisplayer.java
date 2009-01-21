package com.xmos.xta.console.displayers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeSet;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeobject.Line;
import com.xmos.xeobject.SourceFile;
import com.xmos.xeobject.SourceLine;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;

public class SrcDisplayer implements Displayer {
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
    	
    	Block path = DisplayerHelper.getPath(route, m_pathId);
    	
    	if (path == null) {
			m_console.display(String.format("Error: invalid path id: %d\n", m_pathId));
			return;
    	}
    	
        HashMap<File, TreeSet<Integer>> linesCovered = createSrcLinesCovered(path, m_xeObject, false);  

        if( m_fileName.charAt(0) != File.separatorChar){
        	m_fileName =m_xeObject.getExectuableFile().getParent() + File.separatorChar +  m_fileName;
        }
        File traceFile = new File(m_fileName);
        try {
			traceFile = traceFile.getCanonicalFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        if (!linesCovered.containsKey(traceFile)) {
            for (SourceFile sourceFile : m_xeObject.getSourceFiles()) {         
                File currentSourceFile = sourceFile.getFile();
               
                if (traceFile.getAbsolutePath().equals(currentSourceFile.getAbsolutePath())) {
                    m_console.display(String.format("Error: The requested path does not touch the file: %s\n",
                            m_fileName));
                    return;
                }           
            }
        	m_console.display(String.format("Error: The source file has not been loaded: %s\n", m_fileName));
           	return;
        }
                                                    
        TreeSet<Integer> fileLines = linesCovered.get(traceFile);                                                                                
		for (SourceFile sourceFile : m_xeObject.getSourceFiles()) {		
			File source=null;
			try {
				source = sourceFile.getFile().getCanonicalFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			assert(source!=null);
			if (traceFile.equals(source)) {
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
					    }
					}
				}				
			}			
		}        
    }

	public void notify(String message, String argument) {
	}

	private HashMap<File, TreeSet<Integer>> createSrcLinesCovered(Block block,
			XeObject xeObject, boolean recurse) {

		HashMap<File, TreeSet<Integer>> linesCovered = new HashMap<File, TreeSet<Integer>>();
		for (XtaInstruction inst : block.getInstructions(recurse)) {
			for (SourceFile sourceFile : xeObject.getSourceFiles()) {
				File file = sourceFile.getFile();
				try {
					file = file.getCanonicalFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				TreeSet<Integer> sourceLines;
				if (linesCovered.containsKey(file)) {
					sourceLines = linesCovered.get(file);
				} else {
					sourceLines = new TreeSet<Integer>();
					linesCovered.put(file, sourceLines);
				}

				for (SourceLine sourceLine : sourceFile.getSourceLines()) {
					for (Line assembly : sourceLine.getChildern()) {
						if (assembly.getLineNumber() == inst.getPc()) {
							sourceLines.add(sourceLine.getLineNumber());
						}
					}
				}
			}
		}
		return linesCovered;
	}
}
