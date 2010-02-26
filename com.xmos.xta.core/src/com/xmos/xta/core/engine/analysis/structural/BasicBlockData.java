package com.xmos.xta.core.engine.analysis.structural;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class BasicBlockData {

	private int m_staticalyDeterministicIbuffer;
	private int m_exclusivlyBranchedTo;
	private int m_totalBasicBlocks;
	private double m_totalSize;
	private int m_blockCount;
	private int m_longestBlock;

	public BasicBlockData(){
		m_staticalyDeterministicIbuffer = 0;
		m_exclusivlyBranchedTo = 0 ;
		m_totalBasicBlocks = 0;
		m_totalSize = 0.0;
		m_blockCount = 0;
		m_longestBlock = 0;
	}


	public void add(Collection<Node> basicBlocks, int nodeId, int coreId) {

		for(Node node : basicBlocks){
			m_longestBlock = Math.max(m_longestBlock, node.getInstructions().size());
			m_totalSize += node.getInstructions().size();
			m_blockCount++;

			if (!node.isFunctionNode()) {
				m_totalBasicBlocks++;
				if (allPredecessorsBranchTo(node)) {
					m_exclusivlyBranchedTo++;
				}
				if(staticalyDeterministicIbuffe(node)){
					m_staticalyDeterministicIbuffer++;
				}
			}
		}
	}

	public void printResults(){
		System.out.println("Number of Basic Blocks: " + m_totalBasicBlocks);
		System.out.println("Number of Basic Blocks that are statically deterministic: " + m_staticalyDeterministicIbuffer + " (" +
				((m_staticalyDeterministicIbuffer*100)/m_totalBasicBlocks) + "%)");
		System.out.println("Number of Basic Blocks exclusivly branched to: " + m_exclusivlyBranchedTo+ " (" +
				((m_exclusivlyBranchedTo*100)/m_totalBasicBlocks) + "%)");
		System.out.println("Average number of instructions per block: " + (m_totalSize / m_blockCount) + " longest: " + m_longestBlock);

	}

	private boolean allPredecessorsBranchTo(Node node) {
		boolean allBranch= true;
		for(Node prev : node.getPrevNodes()){
			if(!prev.getBranchTargets().contains(node)){
				allBranch = false;
			}
		}
		return allBranch;
	}

	private boolean staticalyDeterministicIbuffe(Node node) {
		if(node.getPrevNodesSize() == 1){
			return true;
		}
		return allPredecessorsBranchTo(node);
	}


	class NodeCoreCollection<E> {
		private Map<String, E> m_collectedObjects;

		public NodeCoreCollection() {
			m_collectedObjects = new HashMap<String, E>();
		}

		public NodeCoreCollection(NodeCoreCollection <E> old) {
			m_collectedObjects = new HashMap<String, E>(old.m_collectedObjects);
		}

		public void add(int nodeId, int coreId, E newObject) {
			m_collectedObjects.put(nodeCoreToString(nodeId, coreId), newObject);
		}

		public E get(int nodeId, int coreId) {
			return m_collectedObjects.get(nodeCoreToString(nodeId, coreId));
		}

		private String nodeCoreToString(int nodeId, int coreId) {
			return nodeId + ":" + coreId;
		}
	}
}
