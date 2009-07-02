package com.xmos.xta.console.commands.print;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.StructureGraph;
import com.xmos.xta.sourcemapper.Line;
import com.xmos.xta.sourcemapper.SourceFile;
import com.xmos.xta.sourcemapper.SourceLine;
import com.xmos.xta.sourcemapper.SourceMapper;

public class Sources extends Command {
	private int m_routeId;
	
	public Sources() {
		m_routeId = 0;
	}
	
	public Sources(int routeId) {
		m_routeId = routeId;		
	}
	
    public int numArgs()        { return 1; }        
    public String name()        { return "sources"; }        
    public String commandLine() { return "sources " + getRouteId(m_routeId); }        
    
    public String description(Engine engine, boolean detailed) {
    	return "sources <route id>: Shows which paths touch which sources";
    }

    public Command clone(String[] args, int index) {
		if ((index + numArgs()) >= args.length) 
			return null;
    	
    	try {
       		return new Sources(getRouteId(args[index + 1]));
	    } catch (NumberFormatException e) {
	        return null;
	    }    	
    }
    
    public void execute(Shell shell, Engine engine, Console console) {
		if (!isExecutableLoaded(engine, console))
			return;			    	

		if (!validateRouteId(engine, console, m_routeId))
			return;		    	

		displaySources(engine, console);
    }

    private void displaySources(Engine engine, Console console) {
       	for (String sources : getSources(engine)) {
       		console.display(String.format("- %s\n", sources));
        }    	
    }   	

    private Set<String> getSources(Engine engine) {
    	final Set<String> sources = new TreeSet<String>();   
    	final SourceMapper sourceMapper = engine.getSourceMapper();
    	StructureGraph structureGraph = engine.getStructureGraph();
    	
		Collection<FunctionNode> nodes = structureGraph.getThreads();	
		for (Node node : nodes) {
		    node.traverse(0, new DefaultNodeTraverser() {
				public boolean foundInstructionNode(Node node, int depth) {					
		            for (SourceFile sourceFile: sourceMapper.getSourceFiles()) {
		                for (SourceLine sourceLine : sourceFile.getSourceLines()) {
		                    for (Line assembly :sourceLine.getChildern()) {
		                        	
		                      	int pc = node.getInstructions().getFirst().getPc();
		                        if (assembly.getLineNumber() == pc) {
		                          	sources.add(sourceFile.getFileName());
		                        }
		                    }
		                }
		            }					
					sources.add(node.getInstructions().getFirst().getFunctionName());
					return true;
				}
		    });
		}				    	
    	return sources;
    }    
}
