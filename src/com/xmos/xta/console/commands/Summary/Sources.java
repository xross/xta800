package com.xmos.xta.console.commands.Summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import com.xmos.dwarfhandler.DebugFile;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeobject.Line;
import com.xmos.xeobject.SourceFile;
import com.xmos.xeobject.SourceLine;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.PathBasicBlock;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.XtaInstruction;

public class Sources extends Command {
	public Sources() {}
	
    public int numArgs()        { return 0; }        
    public String name()        { return "sources"; }        
    public String commandLine() { return "sources"; }        
    
    public String description(Engine engine, boolean detailed) {
    	return "sources: Show path(s) + sources";
    }

    public Command clone(String[] args, int index, boolean commandLineMode) {
   		return new Sources();
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!isRouteAnalysed(engine, console))
			return;		    	

		displaySources(console, engine.getRoute(), engine.getDebugInfo(), engine.getXeObject());
    }

    private void displaySources(Console console, Route route, DebugInfo debugInfo, XeObject xeObject) {
    	ArrayList<String> files = new ArrayList<String>();
  	    for (DebugFile debugFile : debugInfo.getDebugEngine().getDebugFiles()) {
        	TreeSet<Integer> pathIds = new TreeSet<Integer>();
            for (PathBasicBlock path : route.getCompletedPaths()) {
            	buildPathsWithinSourcesSet(path, pathIds, debugFile.getFileName(), xeObject);
            }    	
            if (pathIds.size() > 0) {
            	String file = String.format("%s: ", debugFile.getFileName()); 
            	for (Integer pathId : pathIds) {
            		file += String.format("%d ", pathId);
            	}
            	files.add(file);
            }            
    	}
  	    
  	  Collections.sort(files);
	    for(String file : files){
	    	console.display(file + "\n"); 
	    }
    }   	
    
    private void buildPathsWithinSourcesSet(PathBasicBlock path, Set<Integer> pathSet, String sourceFileName, XeObject xeObject) {	
        if (pathWithinSourceFile(path, sourceFileName, xeObject)) {
        	pathSet.add(path.getPathId());
        }

        for (PathBasicBlock loopPath : path.getLoopPaths()) {
	    	buildPathsWithinSourcesSet(loopPath, pathSet, sourceFileName, xeObject);
   		}
    }    
    
    private boolean pathWithinSourceFile(PathBasicBlock path, String sourceFileName, XeObject xeObject) {
        //TODO make this faster
        HashMap<String, TreeSet<Integer>> linesCovered = new HashMap<String, TreeSet<Integer>>();
        TreeSet<Integer> bob = new TreeSet<Integer>();
        for(XtaInstruction inst : path.getInstructions(false)){
            for(SourceFile sourceFile: xeObject.getSourceFiles()){
                if(sourceFile.getFile().getName().equals(sourceFileName)){
                    for(SourceLine sourceLine : sourceFile.getSourceLines()){
                        for(Line assembly :sourceLine.getChildern()){
                            if(assembly.getLineNumber() == inst.getPc()){
                                bob.add(sourceLine.getLineNumber());
                            }
                        }
                    }
                }
            }
        }
        linesCovered.put(sourceFileName, bob);
        
        if (linesCovered.containsKey(sourceFileName) && 
    		(linesCovered.get(sourceFileName).size() > 0)) {
        	return true;
        }        
        return false;
    }           
}
