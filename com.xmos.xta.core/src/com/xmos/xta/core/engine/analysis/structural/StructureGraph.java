package com.xmos.xta.core.engine.analysis.structural;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;

public class StructureGraph {
	private Collection<FunctionNode> m_threads;
	private Collection<FunctionNode> m_functions;
	private Collection<FunctionNode> m_unlinkedFunctions;
	private Node m_searchNode;
	private Map<Long, Set<Node>> m_pcToEndPoints;
	private Map<Long, Set<InstructionNode>> m_pcToInstructionNodes;
	
	public StructureGraph(Collection<FunctionNode> threads, Collection<FunctionNode> functions,
			Map<Long, Set<Node>> pcToEndPoints, Set<FunctionNode> unlinkedFunctions) {
		super();
		m_threads = threads;
		m_functions = functions;
		m_pcToEndPoints = pcToEndPoints;
		m_unlinkedFunctions = unlinkedFunctions;
		removeLinks();
		
		mapPcsToInstructionNodes();
	}

	private void mapPcsToInstructionNodes() {
		m_pcToInstructionNodes = new TreeMap<Long, Set<InstructionNode>>();
		
		for (FunctionNode node : m_threads) {
			mapPcsToInstructionNodes(node, "t");
		}
		for (FunctionNode node : m_unlinkedFunctions) {
			mapPcsToInstructionNodes(node, "uf");
		}
	}

	private void mapPcsToInstructionNodes(FunctionNode node, final String name) {
		node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {		
			public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {
				InstructionNode inst = (InstructionNode)node;
				if (!inst.getXtaInstruction().isFnop()) {
					long pc = inst.getXtaInstruction().getPc();
					Set<InstructionNode> nodes = m_pcToInstructionNodes.get(pc);
					if (nodes == null) {
						nodes = new TreeSet<InstructionNode>();
						m_pcToInstructionNodes.put(pc, nodes);
					}
					nodes.add(inst);
				}
				return TraverseStatus.CONTINUE;
			}
	    });
	}

	public Map<Long, Set<Node>> getPcToEndPoints() {
		return m_pcToEndPoints;
	}

	public Map<Long, Set<InstructionNode>> getPcToInstructionNodes() {
		return m_pcToInstructionNodes;
	}

	public Collection<FunctionNode> getThreads() {
		return m_threads;
	}
	
	public Collection<FunctionNode> getFunctions() {
		return m_functions;
	}
	
	public Collection<FunctionNode> getUnlinkedFunctions() {
		return m_unlinkedFunctions;
	}
	
	public void removeLinks() {	
		for (Node node : getThreads()) {
		    node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
				public TraverseStatus foundNode(Node node, Stack<Node> ancestors) {
					node.freeMemory();
					return TraverseStatus.CONTINUE;
				}
		    });
		}	
		for (Node node : getUnlinkedFunctions()) {
		    node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
				public TraverseStatus foundNode(Node node, Stack<Node> ancestors) {
					node.freeMemory();
					return TraverseStatus.CONTINUE;
				}
		    });
		}	
	}
	
    public Node getNode(final int nodeId) {
    	m_searchNode = null;
		for (Node node : getThreads()) {					
		    node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
				public TraverseStatus foundNode(Node node, Stack<Node> ancestors) {
					if (node.getId() == nodeId) {
						m_searchNode = node;
						return TraverseStatus.NO_CHILDREN;
					}
					return TraverseStatus.CONTINUE;
				}
		    });
		}
		if(m_searchNode != null){
			return m_searchNode;  
		}
        
        for (Node node : getUnlinkedFunctions()) {					
		    node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
				public TraverseStatus foundNode(Node node, Stack<Node> ancestors) {
					if (node.getId() == nodeId) {
						m_searchNode = node;
						return TraverseStatus.NO_CHILDREN;
					}
					return TraverseStatus.CONTINUE;
				}
		    });
		}
        return m_searchNode;   	
    }

}
