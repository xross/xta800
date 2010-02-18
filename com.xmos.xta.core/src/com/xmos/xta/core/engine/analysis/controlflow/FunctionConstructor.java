package com.xmos.xta.core.engine.analysis.controlflow;

import java.util.Set;
import java.util.TreeSet;

import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.instructions.InstructionMnemonics;
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

	private static final int RAMBASE = 0x10000;

	public FunctionConstructor(InstructionDecoder instDecoder, Notifier notifier, Sections sections,
			int node, int core, CommandSource src, InstructionMnemonics mnenonics) {
		m_instDecoder = instDecoder;
		m_notifier = notifier;
		m_functionStarts = new TreeSet<InstructionNode>();
		m_functionEnds = new TreeSet<InstructionNode>();
		m_sections = sections;
		m_mnenonics = mnenonics;
		m_nodeId = node;
		m_coreId = core;
		m_src = src;
	}

	public boolean process() {

		addBranchTargetsToFunctionStarts();
		addThreadStartsToFunctionStarts();

		addThreadStopsToFunctionEnds();
		addRetspsToFunctionEnds();
		addHaltsToFunctionEnds();

		return ensureAtLeastOneStartExists();
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
			if(i.getXtaInstruction().getBranchLink() && i.getXtaInstruction().getBranchPcRelative()){
				long pc = i.getXtaInstruction().getBranchPcRelativeTargetPc();
				InstructionNode threadNode = m_instDecoder.getNode(pc);
				if (threadNode != null) {
					m_functionStarts.add(threadNode);
				} else {
					m_notifier.notify(m_src, Notifier.NOTIFY_WARNING, "target ("+ String.format("0x%x", pc) +
							") of: " + i.getXtaInstruction().toString() + " not found in executable sections");
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
				System.out.println("could not find 0x" + Long.toHexString(pc));
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
