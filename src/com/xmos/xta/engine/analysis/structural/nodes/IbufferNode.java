package com.xmos.xta.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.analysis.controlflow.InstructionBuffer;

public abstract class IbufferNode extends Node {

	
	boolean m_deterministicFnops;
	IBufferSequence m_ibs;
	
	
	
	
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
	
	
}
