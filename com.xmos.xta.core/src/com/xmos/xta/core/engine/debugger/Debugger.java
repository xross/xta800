package com.xmos.xta.core.engine.debugger;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.xmos.libs.memorymonitor.MemoryMonitorException;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.controlflows.FunctionContainer;
import com.xmos.xta.core.engine.analysis.controlflows.ControlFlow;
import com.xmos.xta.core.engine.analysis.routes.PreanalysisReferenceMatcher;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.StackNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.utils.CoreId;

public class Debugger {
	private Engine m_engine; 
	
	public Debugger(Engine engine) {
		m_engine = engine;
	}

	public void dumpControlFlow(CommandSource src, String functionName, Console console) {
		boolean hit = false;		
		for (CoreId activeCore : m_engine.getActiveCores()) {
			
			ControlFlow controlFlow = m_engine.getControlFlow(activeCore);
		    for (FunctionContainer functionContainer : controlFlow.getFunctionContainers()) {
			    if (functionContainer.getFunctionName().equals(functionName)) {
				    functionContainer.dumpGraph(console);
				    hit = true;
			    }
		    }
		}
		
		if (!hit) { 
		    m_engine.notify(src, Notifier.NOTIFY_ERROR, String.format("Function: %s not found on any active core", functionName));
		}
	}
	
	public boolean validateReference(String refString) {
		boolean hit = false;
		for (CoreId activeCore : m_engine.getActiveCores()) {
			Reference reference = ReferenceFactory.createReference(refString, 
					ReferenceClass.ANY, activeCore);
			
			if (reference.resolve(m_engine)) {
				hit = true;
			}
		}
		return hit;
	}

	public Collection<String> getGlobalReferences(CommandSource src, String refString) {
		Collection<String> matchingReferences = new ArrayList<String>();
		
		for (CoreId activeCore : m_engine.getActiveCores()) {
			Reference reference = ReferenceFactory.createReference(refString, 
					ReferenceClass.ANY, activeCore);

			if (reference.resolve(m_engine)) {
			    PreanalysisReferenceMatcher prm = new PreanalysisReferenceMatcher();
			    try {
					for (StackNode stackNode : prm.matchReference(m_engine.getControlFlow(activeCore), reference, m_engine)) {
						matchingReferences.addAll(stackNode.getBackTrails());
					}
				} catch (AbortedException e) {
				} catch (MemoryMonitorException e) {					
				}
			}
		}
		return matchingReferences;		    
	}
	
	public Collection<String> getRouteReferences(CommandSource src, int routeId, String refString) {
		Collection<Route> routes = m_engine.getRoute(routeId);
		if (routes.isEmpty()) {
			return new ArrayList<String>();
		}
		
		Collection<String> matchingReferences = new ArrayList<String>();		
	    try {
			for (Route route : routes) {
				Reference reference = ReferenceFactory.createReference(refString, 
						ReferenceClass.ANY,
				        route.getRouteCoreContainer().getCoreId());

				if (reference.resolve(m_engine)) {
					Set<Node> nodes = route.getReferencedNodes(reference);
					for (Node node : nodes) {
						matchingReferences.add(node.getReferenceStr());
					}
				}
			}	    
	    } catch (AbortedException e) { 
			return matchingReferences;			
		}
		return matchingReferences;
	}	
}
