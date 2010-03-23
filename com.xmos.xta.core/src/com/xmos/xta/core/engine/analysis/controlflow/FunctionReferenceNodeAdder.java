package com.xmos.xta.core.engine.analysis.controlflow;

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
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.core.engine.instructions.InstructionMnemonics;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.sections.BranchInfo;
import com.xmos.xta.core.engine.sections.Sections;

public class FunctionReferenceNodeAdder {
	private InstructionDecoder m_instDecoder;
	private Notifier m_notifier;
	private CommandSource m_src;
	private InstructionMnemonics m_mnenonics;
	private MemoryImage m_mi;
	private long m_cpPointer;
	private Map<Reference, Set<BranchInfo>> m_branchMap;
	private Set<FunctionContainer> m_functionContainer;
	private Map<InstructionNode, InstructionNode> m_functionInstuctionToOriginal;

	//private int m_executableBytes;
	//private int m_unreachableBytes;
	
	public FunctionReferenceNodeAdder(InstructionDecoder instDecoder, Notifier notifier, CommandSource src,
			XeLoader xeLoader, int nodeId, int coreId, Sections sections){
		m_instDecoder = instDecoder;
		m_notifier = notifier;
		m_src = src;
		m_mi = xeLoader.getMemoryImage(nodeId, coreId);
		m_branchMap = sections.getBranches(nodeId, coreId);
		m_cpPointer = xeLoader.getSymbolTable(nodeId, coreId).symbolAddress("_cp");
		m_mnenonics = new InstructionMnemonics(xeLoader.getArchitecture());
		m_functionContainer = new IteratableSet<FunctionContainer>();
		m_functionInstuctionToOriginal = new NonIteratableMap<InstructionNode, InstructionNode>();
	}


	public void process(Set<InstructionNode> functionStartNodes) {
		
		List<InstructionNode> remainingInsts = new LinkedList<InstructionNode>(m_instDecoder.getInstructions());
		
		//m_executableBytes = countBytes(remainingInsts);
		
		for(InstructionNode startOfFunction : functionStartNodes){
			Map<Node, Node> seen = new NonIteratableMap<Node, Node>();
			
			Set<InstructionNode> toDo = new IteratableSet<InstructionNode>();
			
			toDo.add(startOfFunction);
			
			Set<InstructionNode> functionInstructions = new IteratableSet<InstructionNode>();
			Set<FunctionReferenceNode> functionRefNodes = new IteratableSet<FunctionReferenceNode>();
			InstructionNode copyStart = (InstructionNode) getInstruction(startOfFunction, seen, functionInstructions);
			
			while(!toDo.isEmpty()){
				
				InstructionNode inst = toDo.iterator().next();
				remainingInsts.remove(inst);
				Node parent = getInstruction(inst, seen, functionInstructions);
				for(Node next : inst.getNextNodes()){
					if(!seen.containsKey(next)){
						toDo.add((InstructionNode)next);		
					}
					Node copyInst = getInstruction(next, seen, functionInstructions);
					CommonMethods.connect(copyInst, parent);
					seen.put(next, copyInst);
				}
				
				XtaInstruction xtaInst = inst.getXtaInstruction();
				
				if(xtaInst.isLink()){
					long returnPc = xtaInst.getPc() + xtaInst.getNumBytes();
					InstructionNode originalBLReturn = m_instDecoder.getNode(returnPc);
					boolean found = seen.containsKey(originalBLReturn);
					InstructionNode branchLinkReturn = (InstructionNode) getInstruction(originalBLReturn, seen, functionInstructions);
					//add a function reference node into the graph
					if(m_mnenonics.isImplicitBranchAndLinkLocation(xtaInst)){
						long targetPc = getBranchAndLinkTargetPC(inst);
						InstructionNode target = m_instDecoder.getNode(targetPc);
						FunctionReferenceNode fr = addFunctionReferenceNode(inst, parent, target, functionRefNodes);
						CommonMethods.connect(fr, parent);
						if (branchLinkReturn != null) {	
							CommonMethods.connect(branchLinkReturn, fr);
							if(!found){
								toDo.add(originalBLReturn);		
							}
						} 
					} else {
						//see if the user has told us where the branch goes 
						boolean foundATarget = false;
						for (Reference ref : m_branchMap.keySet()) {
							if (ref.matches(inst.getPC())) {
								for (BranchInfo refTarget : m_branchMap.get(ref)) {
									for (long targetPc : refTarget.getRef().getPcs()) {
										InstructionNode target = (InstructionNode) getInstruction(m_instDecoder.getNode(targetPc), seen, functionInstructions);
										FunctionReferenceNode fr = addFunctionReferenceNode(inst, parent, target, functionRefNodes);
										CommonMethods.connect(fr, parent);	
										if(target != null) {
											foundATarget = true;
										}
										if(!ref.getBackTrail().isEmpty()){
											fr.setBackTrail(ref);
										}
										if (branchLinkReturn != null) {	
											CommonMethods.connect(branchLinkReturn, fr);
											if(!found){
												toDo.add(originalBLReturn);		
											}
										} 
									}
								}
							}
						}
						if(!foundATarget){
							FunctionReferenceNode fr = addFunctionReferenceNode(inst, parent, null, functionRefNodes);
							fr.setUnresolved(true);
							if (branchLinkReturn != null) {	
								CommonMethods.connect(fr, parent);
								CommonMethods.connect(branchLinkReturn, fr);
								if(!found){
									toDo.add(originalBLReturn);		
								}
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
		
		//m_unreachableBytes = countBytes(remainingInsts);
		
	}

	public Map<InstructionNode, InstructionNode> getFunctionInstuctionToOriginal(){
		return m_functionInstuctionToOriginal;
	}
	
	/*
	public int getExecuableBytes(){
		return m_executableBytes;
	}
	
	public int getUnreachableBytes(){
		return m_unreachableBytes;
	}
	
	
	private int countBytes(List<InstructionNode> insts) {
		int totalBytes = 0;
		for(InstructionNode inst : insts){
			totalBytes += inst.getXtaInstruction().getNumBytes();
		}
		return totalBytes;
	}
	 */

	private Node getInstruction(Node next, Map<Node, Node> seen, Set<InstructionNode> functionInstructions) {
		assert(next instanceof InstructionNode);
		if(seen.containsKey(next)){
			return seen.get(next);
		} else {
			Node copy = new InstructionNode((InstructionNode)next);
			seen.put(next, copy);
			functionInstructions.add((InstructionNode) copy);
			return copy;
		}
	}


	public Set<FunctionContainer> getFunctionContainers(){
		return m_functionContainer;
	}

	private FunctionReferenceNode addFunctionReferenceNode(
			InstructionNode inst, Node parent, InstructionNode target, Set<FunctionReferenceNode> functionRefNodes) {
		FunctionReferenceNode fr;
		if(target!= null){
			fr = new FunctionReferenceNode(inst, target);
		} else {
			fr = new FunctionReferenceNode(inst);
		}
		assert(fr != null);
		CommonMethods.connect(fr, parent);
		functionRefNodes.add(fr);
		return fr;
	}

	private long getBranchAndLinkTargetPC(InstructionNode inst) {
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
