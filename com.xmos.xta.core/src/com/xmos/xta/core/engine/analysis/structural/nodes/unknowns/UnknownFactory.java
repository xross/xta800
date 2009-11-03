package com.xmos.xta.core.engine.analysis.structural.nodes.unknowns;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.Source;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;
import com.xmos.xta.core.engine.references.Reference;

public class UnknownFactory {
	public enum Type {
    	LOOP_ITERATIONS,
    	LOOP_PATH_ITERATIONS,
    	LOOP_SCOPE,
    	INSTRUCTION_TIME,
    	PATH_TIME,
    	FUNCTION_TIME,
    }	
	
    public static Unknown createUnknown(Node node, Type type, State state) {    	
        return create(node, type, state, Source.EXPLICIT, new Reference[]{});	
    }
    
    public static Unknown createKnown(Node node, Type type, State state, Source source, Reference[] references) {
        return create(node, type, state, source, references);
    }
     
    private static Unknown create(Node node, Type type, State state, Source source, Reference[] references) {
    	switch (type) {
    	case LOOP_ITERATIONS:      
    		return new LoopUnknown(node, type, state, source, references);
    		
    	case LOOP_PATH_ITERATIONS: 
    		return new LoopPathUnknown(node, type, state, source, references);
    		
    	case LOOP_SCOPE:           
    		return new LoopScopeUnknown(node, type, state, source, references); 
    		
    	case INSTRUCTION_TIME:
    		return new InstructionTimeUnknown(node, type, state, source, references); 
    		
    	case PATH_TIME:
    		return new PathTimeUnknown(node, type, state, source, references);  
    		
    	case FUNCTION_TIME:
    		return new FunctionTimeUnknown(node, type, state, source, references);    	
    	}
    	assert(false);
    	return null;
    }
}
