package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import com.xmos.xta.engine.analysis.controlflow.InstructionBuffer;

public abstract class IBufferNode extends Node {
	
	IBufferSequence m_worstIbufferSequence;
	Collection<IBufferSequence> m_ibufferSequences;
	
	public IBufferNode(){
		super();
		m_ibufferSequences = new HashSet<IBufferSequence>();
		m_worstIbufferSequence = new IBufferSequence();
	}
	
	public IBufferNode(IBufferNode node){
		m_ibufferSequences = new HashSet<IBufferSequence>(node.m_ibufferSequences);
		m_worstIbufferSequence = node.m_worstIbufferSequence;
	}

	public Collection<InstructionBuffer> getEndInstructionBuffers() {
		Set<InstructionBuffer> set = new HashSet<InstructionBuffer>();
		for(IBufferSequence ibs :m_ibufferSequences ){
			set.add(ibs.getInstructionBuffer());
		}
		return set;
	}
	
	public boolean addEndInstructionBuffer(int numStartBytes, InstructionBuffer endInstBuffer, List<Integer> fnopPcs, Node parent) {
		for(IBufferSequence i : m_ibufferSequences){
			if(i.getStartIbuffer() == numStartBytes){
				return false;
			}
		}
		IBufferSequence ibs = new IBufferSequence(numStartBytes, fnopPcs, endInstBuffer, parent);
		m_ibufferSequences.add(ibs);
		
		if(ibs.m_fnopPcs.size() >= m_worstIbufferSequence.m_fnopPcs.size()){
			m_worstIbufferSequence = ibs;
		}

		return true;
	}
	
	public int getNodeFnops(){
		return m_worstIbufferSequence.m_fnopPcs.size();
	}
	
	public int fnopCount(int pc){
		int count =0;
		for(int fnopPc : m_worstIbufferSequence.getfnops()){
			if(fnopPc == pc) count++;
		}
		return count;

	}
	
	public long getFNOPDifference(){
		long worstFnopCount = 0;
		long bestFnopCount = Integer.MAX_VALUE;
		for(IBufferSequence ibs : m_ibufferSequences){
			worstFnopCount = Math.max(ibs.getfnops().size(), worstFnopCount);
			bestFnopCount = Math.min(ibs.getfnops().size(), bestFnopCount);
		}
		return Math.abs(worstFnopCount - bestFnopCount);
	}
	
	public Collection<IBufferNode> getNextIbufferNodes(){
		Collection<IBufferNode> ibuffNodes = new HashSet<IBufferNode>();
		for(Node node : getNextNodes()){
			assert(node instanceof IBufferNode);
			ibuffNodes.add((IBufferNode)node);
		}
		return ibuffNodes;
	}
	
	class IBufferSequence {
		private int m_startIbuffer;
		private List<Integer> m_fnopPcs;
		private InstructionBuffer m_endInstructionBuffer;
		private Node m_parent;
		
		public IBufferSequence(){
			m_fnopPcs = new LinkedList<Integer>();
		}
		
		public IBufferSequence(int startIbuffer, List<Integer> fnopPcs, InstructionBuffer ib, Node parent) {
			super();
			m_startIbuffer = startIbuffer;
			m_fnopPcs = fnopPcs;
			m_endInstructionBuffer = ib;
			m_parent = parent;
		}
		Node getParent(){
			return m_parent;
		}
		
		List<Integer> getfnops(){
			return m_fnopPcs;
		}
		int getStartIbuffer(){
			return m_startIbuffer;
		}
		InstructionBuffer getInstructionBuffer(){
			return m_endInstructionBuffer;
		}
	}
}
