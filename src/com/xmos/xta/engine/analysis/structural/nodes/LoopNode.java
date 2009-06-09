package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.references.types.ReferenceType;

public class LoopNode extends Node {
	private Node m_loopHeader;
	private List<Node> m_loops;

	public LoopNode(LoopNode loop) {
		super();
		m_loopHeader = loop.m_loopHeader;
		m_loops = new ArrayList<Node>(loop.m_loops);
	}

	public LoopNode() {
		super();
		m_loopHeader = null;
		m_loops = new ArrayList<Node>();
	}

	public LoopNode(Node loop, Node base) {
		super();
		m_loopHeader = base;
		m_loops = new ArrayList<Node>();
		m_loops.add(loop);
	}

	public LoopNode(Set<Node> loops, Node base) {
		super();
		m_loopHeader = base;
		m_loops = new ArrayList<Node>();
		for (Node loop : loops) {
			m_loops.add(loop);
		}
	}

	public LoopNode shallowCopy() {
		return new LoopNode(this);
	}
	
	public Node deepCopy(Map<Integer, FunctionNode> startPcs) {
		LoopNode ln = new LoopNode();
		for (Node loop : m_loops) {
			Node clone = loop.deepCopy(startPcs);
			ln.m_loops.add(clone);
		}
		ln.m_loopHeader = m_loopHeader.deepCopy(startPcs);
		ln.m_loopHeader.setIsLoopHeader(true);
		return ln;
	}
	
	public void addLoop(Node loop) {
		m_loops.add(loop);
	}

	public List<XtaInstruction> getInstructions() {
		List<XtaInstruction> insts = new ArrayList<XtaInstruction>(m_loopHeader.getInstructions());
		for (Node al : m_loops) {
			insts.addAll(al.getInstructions());
		}
		return insts;
	}

	public Set<Integer> getPCs() {
		Set<Integer> pcs = new HashSet<Integer>(m_loopHeader.getPCs());
		for (Node loop : m_loops) {
			pcs.addAll(loop.getPCs());
		}
		return pcs;
	}

	public Collection<Node> getContainedNodes() {
		Collection<Node> containedNodes = new HashSet<Node>();
		containedNodes.addAll(m_loopHeader.getContainedNodes());
		for (Node loop : m_loops) {
			containedNodes.addAll(loop.getContainedNodes());
		}
		return containedNodes;
	}

	public String getFunctionName() {
		return "";
	}

	public int getStartPc() {
		return m_loopHeader.getStartPc();
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundLoopNode(this, depth))
			return;

		m_loopHeader.traverse(depth + 1, nodeTraverser);			
		for (Node node : m_loops) {
			node.traverse(depth + 1, nodeTraverser);
		}
	}

	public String toString() {
		return "Loop" + m_id;
	}

	public List<Node> getChildren() {
		List<Node> children = new LinkedList<Node>();
		children.add(m_loopHeader);
		for (Node node : m_loops) {
			children.add(node);					
		}
		return children;
	}

	public Collection<Node> whatsNext(Node node) {
		Collection<Node> next = new HashSet<Node>();
		if (m_loopHeader == node) {
			next.addAll(getParent().whatsNext(this));
			next.addAll(m_loops);
		} else {
			next.add(this);
		}
		return next;
	}
		
	public boolean isLoopNode() {
		return true;
	}
	
	public void setNumIterations(long numIterations) {
	    if (!m_numIterationsDone) {
	    	m_numIterationsDone = true;
	    	
	    	m_loopHeader.setNumIterations(numIterations + 1);
			for (Node child : m_loops) {
				child.setNumIterations(numIterations);
			}
	    }
	}
	
	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, Node node, 
			boolean calledFromAbove, FunctionCallStatus functionCallStatus) {
		
		if (!calledFromAbove) {
		    return super.checkBackTrail(referenceTypes, node, calledFromAbove, functionCallStatus);
		}
		return false;
	}		
	
	// --------------------------------------------------------------------- //
	// Statistics Gathering Methods
	// --------------------------------------------------------------------- //
	public double getTimeTakenRelative(Case c) {
		if (!m_isAbsolute) {
			// This loop is relative, so take the relative component 
			// only and multiply up by the number of iterations.
			double time = m_loopHeader.getTimeTaken(c) * m_loopHeader.getNumIterations();
			for (Node node : m_loops) {
				time += node.getTimeTakenRelative(c) * node.getNumIterations();
			}
			return time;			
		} else {
    		// This loop is absolute, so both the relative and absolute components  
			// beneath here will contribute to the absolute time.
	    	return 0;
		}
	}

	public double getTimeTakenAbsolute(Case c) {
		if (m_isAbsolute) {
			// This loop is absolute, so both the relative and absolute components
			// beneath contribute toward the absolute component at this level.			
			double time = m_loopHeader.getTimeTaken(c) * m_loopHeader.getNumIterations();
			for (Node node : m_loops) {
				time += node.getTimeTaken(c) * node.getNumIterations();
			}
			return time;
			
		} else {
			// This loop is relative, so only take the absolute component here, and
			// do not multiply up the iterations.
			double time = m_loopHeader.getTimeTakenAbsolute(c);
			for (Node node : m_loops) {
				time += node.getTimeTakenAbsolute(c);
			}
			return time;			
		}
	}
	
	public long getNumCyclesRelative(Case c) {
		if (!m_isAbsolute) {
			// This loop is relative, so take the relative component 
			// only and multiply up by the number of iterations.
			long count = m_loopHeader.getNumCyclesRelative(c) * m_loopHeader.getNumIterations();
			for (Node node : m_loops) {
				count += node.getNumCyclesRelative(c) * node.getNumIterations();
			}
			return count;			
		} else {
    		// This loop is absolute, so both the relative and absolute components  
			// beneath here will contribute to the absolute time.
	    	return 0;
		}
	}
	
	public long getNumCyclesAbsolute(Case c) {
		if (m_isAbsolute) {
			// This loop is absolute, so both the relative and absolute components
			// beneath contribute toward the absolute component at this level.			
			long count = m_loopHeader.getNumCyclesAbsolute(c) * m_loopHeader.getNumIterations();
			for (Node node : m_loops) {
				count += node.getNumCyclesAbsolute(c) * node.getNumIterations();
			}
			return count;
			
		} else {
			// This loop is relative, so only take the absolute component here, and
			// do not multiply up the iterations.
			long count = m_loopHeader.getNumCyclesAbsolute(c);
			for (Node node : m_loops) {
				count += node.getNumCyclesAbsolute(c);
			}
			return count;			
		}
	}	

	public long getNumInstructionsRelative(Case c) {
		if (!m_isAbsolute) {
			// This loop is relative, so take the relative component 
			// only and multiply up by the number of iterations.
			long count = m_loopHeader.getNumInstructionsRelative(c) * m_loopHeader.getNumIterations();
			for (Node node : m_loops) {
				count += node.getNumInstructionsRelative(c) * node.getNumIterations();
			}
			return count;			
		} else {
    		// This loop is absolute, so both the relative and absolute components  
			// beneath here will contribute to the absolute time.
	    	return 0;
		}
	}

	public long getNumInstructionsAbsolute(Case c) {
		if (m_isAbsolute) {
			// This loop is absolute, so both the relative and absolute components
			// beneath contribute toward the absolute component at this level.			
			long count = m_loopHeader.getNumInstructionsAbsolute(c) * m_loopHeader.getNumIterations();
			for (Node node : m_loops) {
				count += node.getNumInstructionsAbsolute(c) * node.getNumIterations();
			}
			return count;
			
		} else {
			// This loop is relative, so only take the absolute component here, and
			// do not multiply up the iterations.
			long count = m_loopHeader.getNumInstructionsAbsolute(c);
			for (Node node : m_loops) {
				count += node.getNumInstructionsAbsolute(c);
			}
			return count;			
		}
	}	
	
	public long getNumFNopsRelative(Case c) {
		if (!m_isAbsolute) {
			// This loop is relative, so take the relative component 
			// only and multiply up by the number of iterations.
			long count = m_loopHeader.getNumFNopsRelative(c) * m_loopHeader.getNumIterations();
			for (Node node : m_loops) {
				count += node.getNumFNopsRelative(c) * node.getNumIterations();
			}
			return count;			
		} else {
    		// This loop is absolute, so both the relative and absolute components  
			// beneath here will contribute to the absolute time.
	    	return 0;
		}
	}

	public long getNumFNopsAbsolute(Case c) {
		if (!m_isAbsolute) {
			// This loop is relative, so take the relative component 
			// only and multiply up by the number of iterations.
			long count = m_loopHeader.getNumFNopsAbsolute(c) * m_loopHeader.getNumIterations();
			for (Node node : m_loops) {
				count += node.getNumFNopsAbsolute(c) * node.getNumIterations();
			}
			return count;			
		} else {
    		// This loop is absolute, so both the relative and absolute components  
			// beneath here will contribute to the absolute time.
	    	return 0;
		}
<<<<<<< HEAD:src/com/xmos/xta/engine/analysis/structural/nodes/LoopNode.java
	}	
=======
	}
	
	public void addLoop(Node loop) {
		m_loops.add(loop);
	}

	public LinkedList<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> insts = new LinkedList<XtaInstruction>(m_loopHeader.getInstructions());
		for (Node al : m_loops) {
			insts.addAll(al.getInstructions());
		}
		return insts;
	}

	public Set<Integer> getPCs() {
		Set<Integer> pcs = new HashSet<Integer>(m_loopHeader.getPCs());
		for (Node loop : m_loops) {
			pcs.addAll(loop.getPCs());
		}
		return pcs;
	}

	public Collection<Node> getContainedNodes() {
		Collection<Node> containedNodes = new HashSet<Node>();
		containedNodes.addAll(m_loopHeader.getContainedNodes());
		for (Node loop : m_loops) {
			containedNodes.addAll(loop.getContainedNodes());
		}
		return containedNodes;
	}

	public String getFunctionName() {
		return "";
	}

	public int getStartPc() {
		return m_loopHeader.getStartPc();
	}

	public void traverse(int depth, NodeTraverser nodeTraverser) {
		if (!nodeTraverser.foundLoopNode(this, depth))
			return;

		m_loopHeader.traverse(depth + 1, nodeTraverser);			
		for (Node node : m_loops) {
			node.traverse(depth + 1, nodeTraverser);
		}
	}

	public String toString() {
		return "Loop" + m_id;
	}

	public List<Node> getChildren() {
		List<Node> children = new LinkedList<Node>();
		children.add(m_loopHeader);
		for (Node node : m_loops) {
			children.add(node);					
		}
		return children;
	}

	public Collection<Node> whatsNext(Node node) {
		Collection<Node> next = new HashSet<Node>();
		if (m_loopHeader == node) {
			next.addAll(getParent().whatsNext(this));
			next.addAll(m_loops);
		} else {
			next.add(this);
		}
		return next;
	}
		
	public boolean isLoopNode() {
		return true;
	}
	
	public void setNumIterations(long numIterations) {
	    if (!m_numIterationsDone) {
	    	m_numIterationsDone = true;
	    	
	    	m_loopHeader.setNumIterations(numIterations + 1);
			for (Node child : m_loops) {
				child.setNumIterations(numIterations);
			}
	    }
	}
	
	boolean checkBackTrail(Stack<ReferenceType> referenceTypes, Node node, 
			boolean calledFromAbove, FunctionCallStatus functionCallStatus) {
		
		if (!calledFromAbove) {
		    return super.checkBackTrail(referenceTypes, node, calledFromAbove, functionCallStatus);
		}
		return false;
	}


	public List<XtaInstruction> getInstructionStream(Case c) {
		
		LinkedList<XtaInstruction> insts = new LinkedList<XtaInstruction>();
		
		List<Node> loopList = new LinkedList<Node>(m_loops);
		Collections.sort(loopList);
		for (Node loop : loopList) {
			for(int i=0;i<loop.getNumInstructions(c);i++){
				insts.addAll(m_loopHeader.getInstructionStream(c));
				insts.addAll(loop.getInstructionStream(c));
			}
		}
		
		insts.addAll(m_loopHeader.getInstructionStream(c));
		
		return insts;
	}		
>>>>>>> 9414cf08dc0923f0a33963a19b05a32314c9336d:src/com/xmos/xta/engine/analysis/structural/nodes/LoopNode.java
}
