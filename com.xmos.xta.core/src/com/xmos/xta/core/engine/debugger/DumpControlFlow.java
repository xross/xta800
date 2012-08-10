package com.xmos.xta.core.engine.debugger;

import java.util.Set;

import com.xmos.libs.core.controlflow.ControlFlow;
import com.xmos.libs.core.controlflow.FunctionControlFlow;
import com.xmos.libs.core.controlflow.nodes.CfgFunctionReferenceNode;
import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.libs.core.utils.NonIteratableSet;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;

public class DumpControlFlow {
	private Engine m_engine;
	
	DumpControlFlow(Engine engine) {
		m_engine = engine;
	}
	
	public void dump(CommandSource src, String functionName, Console console) {
		boolean hit = false;		
		for (CoreId coreId : m_engine.getXeLoader().getCoreIds()) {					
			ControlFlow controlFlow = m_engine.getControlFlow(coreId);
		    for (FunctionControlFlow functionControlFlow : controlFlow.getFunctionControlFlows()) {
			    if (functionControlFlow.getFunctionName().equals(functionName)) {
				    console.display(getControlFlow(coreId, functionControlFlow));
				    hit = true;
			    }
		    }
		}
		
		if (!hit) { 
		    m_engine.notify(src, Notifier.NOTIFY_ERROR, String.format("Function: %s not found on any active tile", functionName));
		}
	}
	
	private String getControlFlow(CoreId coreId, FunctionControlFlow functionControlFlow) {
		StringBuffer stringBuffer = new StringBuffer();
		
		Set<CfgNode> seen = new NonIteratableSet<CfgNode>();
		Set<CfgNode> todo = new IteratableSet<CfgNode>();
		
		CfgInstructionNode startInstructionNode = functionControlFlow.getStartInstructionNode();
		
		stringBuffer.append("############################################\n");
		stringBuffer.append("# " + coreId.getCoreReference() + "\n");
		stringBuffer.append("############################################\n");
		
		todo.add(startInstructionNode);
		stringBuffer.append("digraph g {\n");
		
		while (!todo.isEmpty()) {
			CfgNode currentNode = todo.iterator().next();
			seen.add(currentNode);
			if (currentNode instanceof CfgFunctionReferenceNode) {
				stringBuffer.append("node" + currentNode.getId() + " [label=\""
						+ currentNode
						+ "\", fillcolor=red, style=filled, shape=box];\n");
				
			} else if (currentNode instanceof CfgInstructionNode){				
				stringBuffer.append("node" + currentNode.getId() + " [label=\""
						+ ((CfgInstructionNode)currentNode).getInstruction()
						+ "\", fillcolor=lightblue, style=filled, shape=box];\n");
				
			} else {
				stringBuffer.append("node" + currentNode.getId() + " [label=\""
						+ currentNode
						+ "\", style=filled, shape=box];\n");
				
			}
			for (CfgNode next : currentNode.getNextNodes()) {
				stringBuffer.append("node" + currentNode.getId() + " -> node"
						+ next.getId() + "\n");
				
				if (!seen.contains(next)) {
					todo.add(next);
				}
			}
			todo.remove(currentNode);
		}
		stringBuffer.append("}\n");
		return stringBuffer.toString();
	}			
}
