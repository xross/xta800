package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.xta.engine.analysis.controlflow.InstructionBuffer;

public abstract class IBufferNode extends Node {
	
	IBufferSequence m_worstIbufferSequence;
	Collection<IBufferSequence> m_ibufferSequences;
	private HashSet<Integer> startBytes = new HashSet<Integer>();
	
	public IBufferNode(){
		super();
		m_ibufferSequences = new TreeSet<IBufferSequence>();
	}

	public Collection<InstructionBuffer> getEndInstructionBuffers() {
		Set<InstructionBuffer> set = new TreeSet<InstructionBuffer>();
		for(IBufferSequence ibs :m_ibufferSequences ){
			set.add(ibs.getInstructionBuffer());
		}
		return set;
	}
	
	
	public boolean addEndInstructionBuffer(int numStartBytes, InstructionBuffer endInstBuffer, List<Integer> fnopPcs) {
		
		if(startBytes.contains(numStartBytes)) return false;
		startBytes.add(numStartBytes);
		
		IBufferSequence ibs = new IBufferSequence(fnopPcs, endInstBuffer);
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
	
	public Collection<IBufferNode> getNextIbufferNodes(){
		Collection<IBufferNode> ibuffNodes = new TreeSet<IBufferNode>();
		for(Node node : getNextNodes()){
			assert(node instanceof IBufferNode);
			ibuffNodes.add((IBufferNode)node);
		}
		return ibuffNodes;
	}
	
	private static int id;
	static class IBufferSequence implements Comparable<IBufferSequence>{
		
		
		private int m_id;
		private List<Integer> m_fnopPcs;
		private InstructionBuffer m_endInstructionBuffer;
		
		public IBufferSequence(List<Integer> fnopPcs, InstructionBuffer ib) {
			super();
			m_id = id++;
			m_fnopPcs = fnopPcs;
			m_endInstructionBuffer = ib;
		}
		
		List<Integer> getfnops(){
			return m_fnopPcs;
		}

		InstructionBuffer getInstructionBuffer(){
			return m_endInstructionBuffer;
		}

		public int compareTo(IBufferSequence o) {
			return Integer.signum(m_id - o.m_id);
		}
	}

	public abstract void insertFnops();
}
