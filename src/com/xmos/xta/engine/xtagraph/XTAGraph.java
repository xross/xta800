package com.xmos.xta.engine.xtagraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import com.xmos.xta.engine.BasicBlock;
import com.xmos.xta.engine.xtagraph.nodes.Block;
import com.xmos.xta.engine.xtagraph.nodes.Node;
import com.xmos.xta.engine.xtagraph.patterns.CommonMethods;

public class XTAGraph {	
	
	private Collection<ThreadGraph> m_threads;

	public XTAGraph(Collection<BasicBlock> startBasicBlocks) {
		m_threads = new HashSet<ThreadGraph>();
		basicBlocksToNodes(startBasicBlocks);
	}
	
	public XTAGraph(Collection<Node> nodes, Collection<Node> threadStartNodes) {
		m_threads = new HashSet<ThreadGraph>();
		for(Node node : threadStartNodes){
			ThreadGraph tg = new ThreadGraph(node);
			m_threads.add(tg);
		}
	}
	
	public void decomposeAll(){
		for(ThreadGraph tg : m_threads){
			tg.decompose();
		}
	}
	
	public void displayStructureTreeAll(){
		for(ThreadGraph tg : m_threads){
			tg.displayStructureTree();
		}
	}
	
	public Collection<ThreadGraph> getThreads(){
		return m_threads;
	}
	
	private void basicBlocksToNodes(Collection<BasicBlock> startBasicBlocks){
		for(BasicBlock b : startBasicBlocks){
			Collection<Node> cfg = new HashSet<Node>();
			Map<BasicBlock, Block> seenNodes = new HashMap<BasicBlock, Block>();
			Block root = new Block(b);
			cfg.add(root);
			seenNodes.put(b, root);
			for(BasicBlock nextNextBlock : b.getNextNodes()){
				addBlock(nextNextBlock,root, seenNodes, cfg);
			}
			
			for(BasicBlock bl : seenNodes.keySet()){
				if(bl.getBranchLinkReturn()!= null){
					Node blNode = seenNodes.get(bl);
					Node returnNode = seenNodes.get(bl.getBranchLinkReturn());
					blNode.setBranchLinkReturn(returnNode);
				}
			}
			ThreadGraph tg = new ThreadGraph(root);
			m_threads.add(tg);
		}
	}
	
	private void addBlock(BasicBlock nextBlock, Block root, Map<BasicBlock, Block> seenNodes, Collection<Node> cfg) {
		if(seenNodes.containsKey(nextBlock)){
			Block b = seenNodes.get(nextBlock);
			CommonMethods.connect(b, root);
		} else {
			Block block = new Block(nextBlock);
			cfg.add(block);
			seenNodes.put(nextBlock, block);
			CommonMethods.connect(block, root);
			for(BasicBlock nextNextBlock : nextBlock.getNextNodes()){
				addBlock(nextNextBlock,block, seenNodes, cfg);
			}
		}
	}

}
