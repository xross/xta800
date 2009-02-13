package com.xmos.xta.console.displayers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import com.xmos.xeobject.Line;
import com.xmos.xeobject.SourceFile;
import com.xmos.xeobject.SourceLine;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.XtaInstruction;

public class SrcDisplayer {
	private static final boolean DEBUG = false;

	private Engine m_engine;
	private Console m_console;
	private int m_id;
	private Command.IdType m_idType;
	private boolean m_displayAssembly;

	public SrcDisplayer(Engine engine, Console console, int pathId, Command.IdType idType, boolean displayAssembly) {		
		m_engine = engine;
		m_console = console;
		m_id = pathId;
		m_idType = idType;
		m_displayAssembly = displayAssembly;
	}

	public void display() {    	
        Vector<XtaInstruction> instructions = null;    		
        switch (m_idType) {
        case BLOCK:
        	Block block = m_engine.getRoute().getBlock(m_id);			
        	instructions = block.getInstructionsThis();
        	break;
        case PATH:
        	Block path = m_engine.getRoute().getPath(m_id);			
        	instructions = path.getInstructions(false);
        	break;
        default:
        	assert(false);
        }

		XeObject xeObject = m_engine.getXeObject();
        HashMap<String, TreeSet<Integer>> linesCovered = createSrcLinesCovered(instructions, xeObject);                                                                   
        
        if (DEBUG) 
        	displaySrcLinesCovered(linesCovered);
        
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

	private HashMap<String, TreeSet<Integer>> createSrcLinesCovered(Vector<XtaInstruction> instructions,
			XeObject xeObject) {

		HashMap<String, TreeSet<Integer>> linesCovered = new HashMap<String, TreeSet<Integer>>();
		for (XtaInstruction inst : instructions) {
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
	
	private void displaySrcLinesCovered(Map<String, TreeSet<Integer>> linesCovered) {
        Iterator<Map.Entry<String, TreeSet<Integer>>> iter =
        	linesCovered.entrySet().iterator();

        while (iter.hasNext()) {
        	Map.Entry<String, TreeSet<Integer>> pair = iter.next();
            String fileName = pair.getKey();
            TreeSet<Integer> lineNumbers = pair.getValue();
		
            System.out.printf("SrcLinesCovered: %s: ", fileName);
            for (Integer lineNumber : lineNumbers) {
                System.out.print(lineNumber + " ");            	
            }
            System.out.print("\n");
        }
	}
}
