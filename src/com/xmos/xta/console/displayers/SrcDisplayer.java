package com.xmos.xta.console.displayers;

import java.util.HashMap;
import java.util.TreeSet;

import com.xmos.xeobject.Line;
import com.xmos.xeobject.SourceFile;
import com.xmos.xeobject.SourceLine;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.console.Console;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.XtaInstruction;

public class SrcDisplayer {
	private Engine m_engine;
	private Console m_console;
	private int m_pathId;
	private boolean m_displayAssembly;

	public SrcDisplayer(Engine engine, Console console, int pathId, boolean displayAssembly) {
		m_engine = engine;
		m_console = console;
		m_pathId = pathId;
		m_displayAssembly = displayAssembly;
	}

	public void display() {    	
		XeObject xeObject = m_engine.getXeObject();

    	Block path = Block.getPath(m_engine.getRoute(), m_pathId);
        HashMap<String, TreeSet<Integer>> linesCovered = createSrcLinesCovered(path, xeObject, false);                                                                   
        
		for (SourceFile sourceFile : xeObject.getSourceFiles()) {									
	        TreeSet<Integer> fileLines = linesCovered.get(sourceFile.getCompilationName());    
	        if (fileLines.size() > 0) {
				m_console.display("----------------------------------\n");
				m_console.display(sourceFile.getFileName() + " (" + sourceFile.getCompilationDir() + ")\n");
				m_console.display("----------------------------------\n");	        	
	        	
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

	private HashMap<String, TreeSet<Integer>> createSrcLinesCovered(Block block,
			XeObject xeObject, boolean recurse) {

		HashMap<String, TreeSet<Integer>> linesCovered = new HashMap<String, TreeSet<Integer>>();
		for (XtaInstruction inst : block.getInstructions(recurse)) {
			for (SourceFile sourceFile : xeObject.getSourceFiles()) {
				
				String compilationName = sourceFile.getCompilationName();
				TreeSet<Integer> sourceLines;
				if (linesCovered.containsKey(compilationName)) {
					sourceLines = linesCovered.get(compilationName);
				} else {
					sourceLines = new TreeSet<Integer>();
					linesCovered.put(compilationName, sourceLines);
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
