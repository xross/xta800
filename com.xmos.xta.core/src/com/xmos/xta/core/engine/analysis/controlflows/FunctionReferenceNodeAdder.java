package com.xmos.xta.core.engine.analysis.controlflows;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.isa.Instruction;
import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableMap;
import com.xmos.libs.xeloader.MemoryImage;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.binary.InstructionMnemonics;
import com.xmos.xta.core.engine.analysis.binary.XtaInstruction;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgFunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgInstructionNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNode;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.sections.BranchInfo;
import com.xmos.xta.core.engine.sections.Sections;
import com.xmos.xta.core.engine.utils.CoreId;

class FunctionReferenceNodeAdder {
	private InstructionDecoder m_instDecoder;
	private Notifier m_notifier;
	private CommandSource m_src;
	private InstructionMnemonics m_mnenonics;
	private MemoryImage m_mi;
	private long m_cpPointer;
	private Map<Reference, Set<BranchInfo>> m_branchMap;
	private Set<FunctionContainer> m_functionContainer;
	private Map<CfgInstructionNode, CfgInstructionNode> m_functionInstuctionToOriginal;

	FunctionReferenceNodeAdder(InstructionDecoder instDecoder, Notifier notifier, CommandSource src,
			XeLoader xeLoader, CoreId coreId, Sections sections){
		m_instDecoder = instDecoder;
		m_notifier = notifier;
		m_src = src;
		m_mi = xeLoader.getMemoryImage(coreId.getNodeChainIndex(), coreId.getCoreNumber());
		m_branchMap = sections.getBranches(coreId);
		m_cpPointer = xeLoader.getSymbolTable(coreId.getNodeChainIndex(), coreId.getCoreNumber()).symbolAddress("_cp");
		m_mnenonics = new InstructionMnemonics(xeLoader.getArchitecture());
		m_functionContainer = new IteratableSet<FunctionContainer>();
		m_functionInstuctionToOriginal = new NonIteratableMap<CfgInstructionNode, CfgInstructionNode>();
	}

	void process(Set<CfgInstructionNode> functionStartNodes) {
		
		List<CfgInstructionNode> remainingInsts = new LinkedList<CfgInstructionNode>(m_instDecoder.getInstructions());
		
		for (CfgInstructionNode startOfFunction : functionStartNodes){
			
			Map<CfgNode, CfgNode> seen = new NonIteratableMap<CfgNode, CfgNode>();
			
			Set<CfgInstructionNode> toDo = new IteratableSet<CfgInstructionNode>();
			
			toDo.add(startOfFunction);
			
			Set<CfgInstructionNode> functionInstructions = new IteratableSet<CfgInstructionNode>();
			Set<CfgFunctionReferenceNode> functionRefNodes = new IteratableSet<CfgFunctionReferenceNode>();
			CfgInstructionNode copyStart = (CfgInstructionNode) getInstruction(startOfFunction, seen, functionInstructions);
			
			while(!toDo.isEmpty()){
				
				CfgInstructionNode inst = toDo.iterator().next();
				remainingInsts.remove(inst);
				CfgNode copyInst = getInstruction(inst, seen, functionInstructions);
				for (CfgNode next : inst.getNextNodes()) {
					if (!seen.containsKey(next)) {
						toDo.add((CfgInstructionNode)next);		
					}
					CfgNode copyNext = getInstruction(next, seen, functionInstructions);
					CfgNode.connect(copyNext, copyInst);
					seen.put(next, copyNext);
				}

				XtaInstruction xtaInst = inst.getXtaInstruction();
								
				if(xtaInst.isLink()){
					boolean foundATarget = false;
					long returnPc = xtaInst.getPc() + xtaInst.getNumBytes();
					CfgInstructionNode originalBLReturn = m_instDecoder.getNode(returnPc);
					boolean found = seen.containsKey(originalBLReturn);
					CfgInstructionNode branchLinkReturn = (CfgInstructionNode)getInstruction(originalBLReturn, seen, functionInstructions);
					//add a function reference node into the graph
					if(m_mnenonics.isImplicitBranchAndLinkLocation(xtaInst)){
						long targetPc = getBranchAndLinkTargetPC(inst);
						CfgInstructionNode target = m_instDecoder.getNode(targetPc);
						CfgFunctionReferenceNode fr = addFunctionReferenceNode(inst, copyInst, target, functionRefNodes);
						foundATarget = true;
						CfgNode.connect(fr, copyInst);
						if (branchLinkReturn != null) {	
							CfgNode.connect(branchLinkReturn, fr);
							if(!found){
								toDo.add(originalBLReturn);		
							}
						} 
					} 
					//see if the user has told us where the branch goes 
					for (Reference ref : m_branchMap.keySet()) {
						if (ref.matches(inst.getPc())) {
							for (BranchInfo refTarget : m_branchMap.get(ref)) {
								for (long targetPc : refTarget.getRef().getPcs()) {
									CfgInstructionNode target = (CfgInstructionNode)getInstruction(m_instDecoder.getNode(targetPc), seen, functionInstructions);
									CfgFunctionReferenceNode fr = addFunctionReferenceNode(inst, copyInst, target, functionRefNodes);
									CfgNode.connect(fr, copyInst);	
									if(target != null) {
										foundATarget = true;
									}
									if(!ref.getBackTrail().isEmpty()){
										fr.setBackTrail(ref);
									}
									if (branchLinkReturn != null) {	
										CfgNode.connect(branchLinkReturn, fr);
										if(!found){
											toDo.add(originalBLReturn);		
										}
									} 
								}
							}
						}
					}
					if(!foundATarget){
						copyInst.setUnresolved(true);
						//TODO remove this as the following code should be unreachable
						CfgFunctionReferenceNode fr = addFunctionReferenceNode(inst, copyInst, null, functionRefNodes);
						fr.setUnresolved(true);
						if (branchLinkReturn != null) {	
							CfgNode.connect(fr, copyInst);
							CfgNode.connect(branchLinkReturn, fr);
							if(!found){
								toDo.add(originalBLReturn);		
							}
						}
					}
				}				
				toDo.remove(inst);
			}
			FunctionContainer fc = new FunctionContainer(copyStart, functionInstructions, functionRefNodes);
			m_functionContainer.add(fc);
			m_functionInstuctionToOriginal.put(copyStart, startOfFunction);
		}
	}

	Map<CfgInstructionNode, CfgInstructionNode> getFunctionInstuctionToOriginal(){
		return m_functionInstuctionToOriginal;
	}
	
	private CfgNode getInstruction(CfgNode next, Map<CfgNode, CfgNode> seen, Set<CfgInstructionNode> functionInstructions) {
		assert(next instanceof CfgInstructionNode);
		if(seen.containsKey(next)){
			return seen.get(next);
		} else {
			CfgNode copy = new CfgInstructionNode((CfgInstructionNode)next);
			seen.put(next, copy);
			functionInstructions.add((CfgInstructionNode)copy);
			return copy;
		}
	}

	public Set<FunctionContainer> getFunctionContainers(){
		return m_functionContainer;
	}

	private CfgFunctionReferenceNode addFunctionReferenceNode(
			CfgInstructionNode inst, CfgNode parent, CfgInstructionNode target, Set<CfgFunctionReferenceNode> functionRefNodes) {
		CfgFunctionReferenceNode fr;
		if(target!= null){
			fr = new CfgFunctionReferenceNode(inst, target);
		} else {
			fr = new CfgFunctionReferenceNode(inst);
		}
		assert(fr != null);
		CfgNode.connect(fr, parent);
		functionRefNodes.add(fr);
		return fr;
	}

	private long getBranchAndLinkTargetPC(CfgInstructionNode inst) {
		long targetPc;
		if (m_mnenonics.isBlacpShort(inst.getXtaInstruction())) {
			long imm = XtaInstruction.getBits(16, 10, inst.getXtaInstruction().getOpcode()) << Instruction.NUM_BYTES_PER_WORD_LOG2;
			try {
				targetPc = m_mi.readInstructionWord(m_cpPointer + imm);
			} catch (Exception e) {
				m_notifier.notify(m_src, Notifier.NOTIFY_WARNING, String.format("CP address 0x%x out of bounds", (m_cpPointer + imm)));
			}
			targetPc = m_cpPointer + imm;
		} else if (m_mnenonics.isBlacpLong(inst.getXtaInstruction())) {
			long imm = XtaInstruction.getBitsHL(16, 10, 0, 10, inst.getXtaInstruction().getOpcode()) << Instruction.NUM_BYTES_PER_WORD_LOG2;
			try {
				targetPc = m_mi.readInstructionWord(m_cpPointer + imm);
			} catch (Exception e) {
				m_notifier.notify(m_src, Notifier.NOTIFY_WARNING, String.format("CP address 0x%x out of bounds", (m_cpPointer + imm)));
			}
			targetPc = m_cpPointer + imm;
		} else if(inst.getXtaInstruction().getBranchPcRelative()){
			targetPc = inst.getXtaInstruction().getBranchPcRelativeTargetPc();
		} else {
			assert(false);
			targetPc = 0;
		}
		return targetPc;
	}
}
