package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.xmos.xta.engine.analysis.controlflow.InstructionBuffer;

public abstract class IBufferNode extends Node {
	
	IBufferSequence m_worstIbufferSequence;
	Collection<IBufferSequence> m_ibufferSequences;
	
	public IBufferNode(){
		super();
		m_ibufferSequences = new HashSet<IBufferSequence>();
	}

	public Collection<InstructionBuffer> getEndInstructionBuffers() {
		Set<InstructionBuffer> set = new HashSet<InstructionBuffer>();
		for(IBufferSequence ibs :m_ibufferSequences ){
			set.add(ibs.getInstructionBuffer());
		}
		return set;
	}
	
	public boolean addEndInstructionBuffer(int numStartBytes, InstructionBuffer endInstBuffer, List<Integer> fnopPcs) {
		for(IBufferSequence i : m_ibufferSequences){
			if(i.getStartIbuffer() == numStartBytes){
				return false;
			}
		}
		IBufferSequence ibs = new IBufferSequence(numStartBytes, fnopPcs, endInstBuffer);
		m_ibufferSequences.add(ibs);
		
		if(m_ibufferSequences.size() == 1){
			m_worstIbufferSequence = m_ibufferSequences.iterator().next();
		} else {
			int worstCount = m_worstIbufferSequence.m_fnopPcs.size();
			for(IBufferSequence currentState : m_ibufferSequences){
				if(currentState.m_fnopPcs.size() > worstCount){
					m_worstIbufferSequence = currentState;
				}
			}
		}
		return true;
	}
	
	public int getNodeFnops(){
		return m_worstIbufferSequence.m_fnopPcs.size();
	}
	
	public int fnopCount(int pc){
		if(m_worstIbufferSequence == null) return 0;
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
		
		public IBufferSequence(int startIbuffer, List<Integer> fnopPcs, InstructionBuffer ib) {
			super();
			m_startIbuffer = startIbuffer;
			m_fnopPcs = fnopPcs;
			m_endInstructionBuffer = ib;
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

	public abstract void insertFnops();
}
