package com.xmos.xta.core.engine.analysis.controlflow;

import java.util.Map;
import java.util.Set;

import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.libs.xeloader.SymbolTable;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.instructions.InstructionMnemonics;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.sections.BranchInfo;
import com.xmos.xta.core.engine.sections.Sections;

public class FunctionConstructor {

	private InstructionDecoder m_instDecoder;
	private Notifier m_notifier;
	private int m_nodeId;
	private int m_coreId;
	private CommandSource m_src;
	private Set<InstructionNode> m_functionStarts;
	private Set<InstructionNode> m_functionEnds;
	private Sections m_sections;
	private InstructionMnemonics m_mnenonics;
	private SymbolTable m_symbolTable;

	private static final int RAMBASE = 0x10000;

	public FunctionConstructor(InstructionDecoder instDecoder, Notifier notifier, Sections sections,
			int node, int core, CommandSource src, InstructionMnemonics mnenonics, SymbolTable symbolTable) {
		m_instDecoder = instDecoder;
		m_notifier = notifier;
		m_functionStarts = new IteratableSet<InstructionNode>();
		m_functionEnds = new NonIteratableSet<InstructionNode>();
		m_sections = sections;
		m_mnenonics = mnenonics;
		m_nodeId = node;
		m_coreId = core;
		m_src = src;
		m_symbolTable = symbolTable;
	}

	public boolean process() {

		addBranchTargetsToFunctionStarts();
		addThreadStartsToFunctionStarts();
		addUserBranchTargetsToFunctionStarts();
		
		//last resort
		addUnreachableFunctionStarts();
		
		addThreadStopsToFunctionEnds();
		addRetspsToFunctionEnds();
		addHaltsToFunctionEnds();

		return ensureAtLeastOneStartExists();
	}

	private void addUnreachableFunctionStarts() {
		
		for(String functionName : m_symbolTable.getFunctionNames()){
			
			InstructionNode threadNode = m_instDecoder.getNode(m_symbolTable.symbolAddress(functionName));
			if (threadNode != null) {
				m_functionStarts.add(threadNode);
			} else {
				m_notifier.notify(m_src, Notifier.NOTIFY_WARNING,
						"failed to find instruction at PC 0x" + Long.toHexString(RAMBASE));
			}
		}
	}

	public Set<InstructionNode> getFunctionStarts(){
		return m_functionStarts;
	}

	public Set<InstructionNode> getFunctionEnds(){
		return m_functionEnds;
	}

	private boolean ensureAtLeastOneStartExists() {
		if(m_functionStarts.isEmpty()){
			m_notifier.notify(m_src, Notifier.NOTIFY_WARNING,
						"failed to find any timable code defaulting to ram base at PC 0x" + Long.toHexString(RAMBASE));
			InstructionNode threadNode = m_instDecoder.getNode(RAMBASE);
			if (threadNode != null) {
				m_functionStarts.add(threadNode);
			} else {
				m_notifier.notify(m_src, Notifier.NOTIFY_WARNING,
						"failed to find instruction at PC 0x" + Long.toHexString(RAMBASE));
			}
		}
		return !m_functionStarts.isEmpty();
	}

	private void addBranchTargetsToFunctionStarts(){
		for(InstructionNode i : m_instDecoder.getInstructions()){
			if(i.getXtaInstruction().getBranchLink()){
				
				if(i.getXtaInstruction().getBranchPcRelative()){
					long pc = i.getXtaInstruction().getBranchPcRelativeTargetPc();
					InstructionNode threadNode = m_instDecoder.getNode(pc);
					if (threadNode != null) {
						m_functionStarts.add(threadNode);
					} else {
						if(!m_mnenonics.isLdap(i.getXtaInstruction())){
							m_notifier.notify(m_src, Notifier.NOTIFY_WARNING, "target ("+ String.format("0x%x", pc) +
									") of: " + i.getXtaInstruction().toString() + " not found in executable sections");
						} 
					}
				}
			} 
		}
	}
	
	
	private void addUserBranchTargetsToFunctionStarts(){
		Map<Reference, Set<BranchInfo>> branchMap = m_sections.getBranches(m_nodeId, m_coreId);	
		for(InstructionNode instruction : m_instDecoder.getInstructions()){
			XtaInstruction inst = instruction.getXtaInstruction();
			if(inst.isLink()){
				for (Reference ref : branchMap.keySet()) {
					if (ref.matches(inst.getPc())) {
						for (BranchInfo target : branchMap.get(ref)) {
							for (long pc : target.getRef().getPcs()) {
								m_functionStarts.add(m_instDecoder.getNode(pc)); 
							}
						}	
					}
				}
			}
		}
	}

	private void addThreadStartsToFunctionStarts(){
		
		for (long pc : m_sections.getThreadStartPoints(m_nodeId, m_coreId)) {
			InstructionNode threadNode = m_instDecoder.getNode(pc);
			
			if (threadNode != null) {
				m_functionStarts.add(threadNode);
			} else {
				m_notifier.notify(m_src, Notifier.NOTIFY_WARNING,
						"failed to find thread start at PC 0x" + Long.toHexString(pc));
			}
		}
		return;
	}

	private void addThreadStopsToFunctionEnds(){
		for (long pc : m_sections.getThreadStopPoints(m_nodeId, m_coreId)) {
			InstructionNode threadNode = m_instDecoder.getNode(pc);
			if (threadNode != null) {
				m_functionEnds.add(threadNode);
				threadNode.setUnresolved(true);
			} else {
				m_notifier.notify(m_src, Notifier.NOTIFY_WARNING,
						"failed to find thread stop at PC 0x" + Long.toHexString(pc));
			}
		}
		return;
	}

	private void addRetspsToFunctionEnds(){
		for(InstructionNode i : m_instDecoder.getInstructions()){
			if(i.getXtaInstruction().getReturn()){
				m_functionEnds.add(i);
			}
		}
	}

	private void addHaltsToFunctionEnds(){
		boolean previousInstructionWasClre = true;
		for(InstructionNode i : m_instDecoder.getInstructions()){
			if( m_mnenonics.isFreet(i.getXtaInstruction())){
				m_functionEnds.add(i);
			}
			if(previousInstructionWasClre && m_mnenonics.isWait(i.getXtaInstruction())){
				m_functionEnds.add(i);
			}
			previousInstructionWasClre = m_mnenonics.isClre(i.getXtaInstruction());
		}
	}

}
