package com.xmos.xta.engine.analysis.structural;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.xta.engine.analysis.structural.nodes.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.nodes.LoopNode;
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
	}

	public Collection<Node> getFunctions() {
		return m_functions;
	}

	public Collection<Node> getThreads() {
		return m_threads;
	}

	public boolean setLoopIterations(Reference reference, long iterations) {
/*
		 - Find the node in the tree at which we want to assign the iteration count.		
		 - Check that this node is within a loop node (by looking up). Fail if it is not.		
		 - From this nodes loop node, clear all the done bits in all nodes below.
		 - Call 'SetIterations' on the assignment node:
		
		 Node::setIterations(value) {
		     if (!done) {
		         Set the iteration value on this node.
		         Mark this node as done.
		         Call 'SetIterations' on all the children.
		         Call 'SetIterations' on the parent.
		     }
		 }
		
		 The above implementation needs to be different depending on the type of node:
	     
		 SectionNode::setIterations(value) {
	       if (!done) {
    		   Set the iteration value on this node.
	    	   Mark this node as done.
	    	   
	           if (none of the options are already done) {
	               Call setIterations on the longest path.
	           }
	           Call 'setIterations' on the parent.
	       }
	     }
	     
		 LoopNode::setIterations(value) {
	       if (!done) {
		         Set the iteration value on this node.
		         Mark this node as done.
		         Call 'SetIterations' on all the children.
	       }
	     }
*/		
		return false;
	}

	public boolean unsetLoopIterations(Reference reference) {
		return false;
	}
	
	public boolean setLoopMaxIterations(Reference reference, long iterations) {
		Collection<Node> nodes = getLoopNodes(reference);
		if (nodes.size() > 0) {
		    for (Node node : nodes) {
			    ((LoopNode)node).setNumIterations(iterations);
		    }
		    return true;
		}
		return false;
	}

	public boolean unsetLoopMaxIterations(Reference reference) {
		Collection<Node> nodes = getLoopNodes(reference);
		if (nodes.size() > 0) {
		    for (Node node : nodes) {
			    ((LoopNode)node).setNumIterationsSet(false);
		    }
		    return true;
		}
		return false;
	}
	
	public boolean setLoopScope(Reference reference, boolean isAbsolute) {
		Collection<Node> nodes = getLoopNodes(reference);
		if (nodes.size() > 0) {
		    for (Node node : nodes) {
			    ((LoopNode)node).setScope(isAbsolute);
		    }
		    return true;
		}
		return false;
	}
	
	private Collection<Node> getLoopNodes(Reference reference) {
		Collection<Node> nodes = getLoopNodesContainingPc(reference.getPc());
		return checkBackTrail(nodes, reference);
	}
	
	private Collection<Node> getLoopNodesContainingPc(final int pc) {
		final Collection<Node> loopNodes = new ArrayList<Node>();		
		for (Node threadNode : m_threads) {					
			threadNode.traverse(0, new DefaultNodeTraverser() {
				public boolean foundLoopNode(Node node, int depth) {
					if (node.getPCs().contains(pc)) {
						loopNodes.add(node);
					}
					return true;
				}
		    });
		}
		return loopNodes;
	}
	
	private Collection<Node> checkBackTrail(Collection<Node> nodes, Reference reference) {
		return nodes;
	}
}
