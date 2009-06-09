package com.xmos.xta.engine.analysis.structural;

import java.util.Collection;

import com.xmos.xta.engine.analysis.structural.nodes.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.references.Reference;

public class StructureGraph {
	private Collection<Node> m_functions;
	private Collection<Node> m_threads;
	
	public StructureGraph(Collection<Node> functions, Collection<Node> threads) {
		super();
		assert(threads.size() > 0);
		m_functions = functions;
		m_threads = threads;
		removeLinks();
	}

	public Collection<Node> getFunctions() {
		return m_functions;
	}

	public Collection<Node> getThreads() {
		return m_threads;
	}

	public boolean setLoopIterations(Reference reference, long iterations) {
		boolean loopIterationsSet = false;
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();		
		
		for (Node threadNode : getThreads()) {
			Collection<Node> referencedNodes = 
				referenceMatcher.getNodes(threadNode, reference);
			
			for (Node referencedNode : referencedNodes) {						
				Node enclosingLoopNode = referencedNode.isLoopNode() ? 
						referencedNode : 
						referencedNode.getEnclosingLoopNode();
									
				if (enclosingLoopNode != null) {				
					enclosingLoopNode.setNumIterationsDone(false);
					referencedNode.setNumIterations(iterations);
					loopIterationsSet = true;
				}
			}			
		}		
		return loopIterationsSet;
	}

	public boolean unsetLoopIterations(Reference reference) {
		return false;
	}

	public boolean setLoopScope(Reference reference, boolean isAbsolute) {
		boolean loopScopeSet = false;
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();		
		
		for (Node threadNode : getThreads()) {
			Collection<Node> referencedNodes = 
				referenceMatcher.getNodes(threadNode, reference);
			
		    for (Node referencedNode : referencedNodes) {
				Node enclosingLoopNode = referencedNode.isLoopNode() ? 
						referencedNode : 
						referencedNode.getEnclosingLoopNode();

				if (enclosingLoopNode != null) {				
					enclosingLoopNode.setIsAbsolute(isAbsolute);
	    		    loopScopeSet = true;
				}
			}			
		}		
		return loopScopeSet;
	}
	
	public void removeLinks() {	
		for (Node node : getThreads()) {
		    node.traverse(0, new DefaultNodeTraverser() {
		    	public boolean foundBlockNode(Node node, int depth) {
		    		node.freeMemory();
		        	return true;
		    	}
		        public boolean foundFunctionNode(Node node, int depth) {
		        	node.freeMemory();
		        	return true;
		        }
		        public boolean foundInstructionNode(Node node, int depth) {
		        	node.freeMemory();
		        	return true;
		        }
		        public boolean foundPathNode(Node node, int depth) {
		        	node.freeMemory();
		        	return true;
		        }
		        public boolean foundLoopNode(Node node, int depth) {
		        	node.freeMemory();
		        	return true;
		        }
		        public boolean foundSectionNode(Node node, int depth) {
		        	node.freeMemory();
		        	return true;
		        }
		        public boolean foundTerminalNode(Node node, int depth) {
		        	node.freeMemory();
		        	return true;
		        }
		    });	   
		}	
	}	
}
