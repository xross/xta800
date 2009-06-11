package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.xmos.xta.engine.analysis.controlflow.InstructionBuffer;

public abstract class IbufferNode extends Node {
	
	Map<Integer, Integer> m_startToEndIbuffer;
	boolean m_deterministicFnops;
	IBufferSequence m_ibs;
	
	Collection<IBufferSequence> m_ibufferSequences;
	
	public IbufferNode(){
		super();
		m_deterministicFnops = false;
		m_startToEndIbuffer = new HashMap<Integer, Integer>();
		m_ibufferSequences = new HashSet<IBufferSequence>();
	}
	
	//this whole function is a complete mess and will be refactored
	
	public boolean isFnopsDeterministic(){
		return m_deterministicFnops;
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
		IBufferSequence ibs = new IBufferSequence(numStartBytes, endInstBuffer.getNumBytes(), fnopPcs, endInstBuffer);
		m_ibufferSequences.add(ibs);
		
		if(m_ibufferSequences.size() == 1){
			m_ibs = m_ibufferSequences.iterator().next();
			m_deterministicFnops = true;
		} else {
			m_deterministicFnops = false;
		}
		return true;
	}
	
	public int fnopCount(int pc){
		if(isFnopsDeterministic()){
			int count =0;
			for(int fnopPc : m_ibs.getfnops()){
				if(fnopPc == pc) count++;
			}
			return count;
		} else {
			return 0;
		}
	}
	
	public Collection<IbufferNode> getNextIbufferNodes(){
		Collection<IbufferNode> ibuffNodes = new HashSet<IbufferNode>();
		for(Node node : getNextNodes()){
			assert(node instanceof IbufferNode);
			ibuffNodes.add((IbufferNode)node);
		}
		return ibuffNodes;
	}
	
	public int iBufferTransition(int byteNum) {
		assert(m_startToEndIbuffer.containsKey(byteNum));
		return m_startToEndIbuffer.get(byteNum);
	}
	
	class IBufferSequence {
		private int m_startIbuffer;
		private int m_endIbuffer;
		private List<Integer> m_fnopPcs;
		private InstructionBuffer m_endInstructionBuffer;
		
		public IBufferSequence(int startIbuffer, int endIbuffer, List<Integer> fnopPcs, InstructionBuffer ib) {
			super();
			m_startIbuffer = startIbuffer;
			m_endIbuffer = endIbuffer;
			m_fnopPcs = fnopPcs;
			m_endInstructionBuffer = ib;
		}
		
		public int getEndIbuffer(){
			return m_endIbuffer;
		}
		public List<Integer> getfnops(){
			return m_fnopPcs;
		}
		public int getStartIbuffer(){
			return m_startIbuffer;
		}
		public InstructionBuffer getInstructionBuffer(){
			return m_endInstructionBuffer;
		}
	}
}
