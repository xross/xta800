package com.xmos.xta.core.engine.analysis.controlflows;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableMap;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.libs.xeloader.SymbolTable;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.binary.InstructionMnemonics;
import com.xmos.xta.core.engine.analysis.controlflows.functionmapper.FunctionMapper;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgFunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgInstructionNode;
import com.xmos.xta.core.engine.analysis.controlflows.nodes.CfgNode;
import com.xmos.xta.core.engine.sections.Sections;
import com.xmos.xta.core.engine.utils.CoreId;

public class ControlFlow {
	private static final boolean DEBUG = false;

	private Set<FunctionContainer> m_functionContainers;
	private Map<CfgFunctionReferenceNode, FunctionContainer> m_containingFunction;
	private Map<FunctionContainer, Set<CfgFunctionReferenceNode>> m_fnRefToCallees;
	private FunctionMapper m_functionMapper;
	
	ControlFlow() {
		m_containingFunction = new NonIteratableMap<CfgFunctionReferenceNode, FunctionContainer>();		
		m_fnRefToCallees = new IteratableMap<FunctionContainer, Set<CfgFunctionReferenceNode>>();
		m_functionMapper = null;
	}	

	public Set<FunctionContainer> getFunctionContainers() {
		assert(m_functionContainers != null);
		return m_functionContainers;
	}

	public FunctionMapper getFunctionMapper() {
		assert(m_functionMapper != null);
		return m_functionMapper;
	}
	
	public List<Stack<CfgFunctionReferenceNode>> getStacks(FunctionContainer fc){
		Stack<CfgFunctionReferenceNode> stack = new Stack<CfgFunctionReferenceNode>();
		List<Stack<CfgFunctionReferenceNode>> foundStacks = new LinkedList<Stack<CfgFunctionReferenceNode>>();
		climb(fc, stack, foundStacks);
		return foundStacks;
	}

	public FunctionContainer getFunctionContainer(CfgNode node) {
		for (FunctionContainer fc : m_functionContainers) {
			if (fc.getFunctionContents().contains(node)) {
				return fc;
			}
		}
		assert(false);
		return null;
	}
	
	public List<FunctionContainer> getFunctionContaining(long pc) {
		List<FunctionContainer> found = new LinkedList<FunctionContainer>();
		for (FunctionContainer fc : m_functionContainers) {
			if (fc.containsPc(pc)) {
				found.add(fc);
			}
		}
		return found;
	}

	boolean initialise(CommandSource src, Notifier notifier, CoreId coreId,
			XeLoader xeLoader, Sections sections, 
			InstructionDecoder instructionDecoder) { 
		
		InstructionMnemonics mnenonics = new InstructionMnemonics(xeLoader.getArchitecture());
				
		FunctionConstructor fc = new FunctionConstructor(
				instructionDecoder, notifier, sections, coreId, src, mnenonics, 
				xeLoader.getSymbolTable(coreId.getNodeChainIndex(), coreId.getCoreNumber()));

		try {
			fc.process();
		} catch (Exception e) {
			notifier.notify(src, Notifier.NOTIFY_ERROR, e.getMessage());
			return false;
		}

		Set<CfgInstructionNode> functionStartNodes = fc.getFunctionStarts();		
		Set<CfgInstructionNode> functionEndNodes = fc.getFunctionEnds();
		
		SuccessorAdder sa = new SuccessorAdder(coreId, instructionDecoder, 
				sections, functionEndNodes, notifier, src, mnenonics);
		sa.process();

		UnresolvedSetter us = new UnresolvedSetter(functionStartNodes);
		us.process();
		
		FunctionReferenceNodeAdder frna = new FunctionReferenceNodeAdder(
				instructionDecoder, notifier, src, xeLoader, coreId, sections);
		frna.process(functionStartNodes);

		Set<FunctionContainer> functionContainers = frna.getFunctionContainers();
		
		FnopAdder fa = new FnopAdder(functionContainers);
		fa.process();
		
		ensureAllFunctionsAreNamed(xeLoader.getSymbolTable(
				coreId.getNodeChainIndex(), coreId.getCoreNumber()),
				functionContainers, sections.getLabels(coreId));

		setBranchTargets(instructionDecoder, functionContainers);
		linkFunctions(functionContainers);

		m_functionContainers = functionContainers;
		
		makeCalleeGraph(functionContainers);
		if (DEBUG) {
			dumpCallGraph();
		}		
		
		FunctionMapper functionMapper;
		try {
			functionMapper = new FunctionMapper(
					frna.getFunctionInstuctionToOriginal(),
					instructionDecoder, functionContainers, 
					sections.getLabels(coreId));

		} catch (AbortedException e) {
			return false;
		}
		m_functionMapper = functionMapper;		
		return true;
	}
		
	private void makeCalleeGraph(Set<FunctionContainer> functionContainers) {

		for (FunctionContainer fc: functionContainers){			
			m_fnRefToCallees.put(fc, new IteratableSet<CfgFunctionReferenceNode>());
		}

		for (FunctionContainer fc: functionContainers) {
			for (CfgFunctionReferenceNode containedRef : fc.getFunctionReferenceNodes()){			
				m_containingFunction.put(containedRef, fc);
				
				FunctionContainer containedFnCont = containedRef.getFunctionContainer();
				if (containedFnCont != null) {
					m_fnRefToCallees.get(containedFnCont).add(containedRef);
				}
			}
		}
		
		for (FunctionContainer fc : m_fnRefToCallees.keySet()) {
			if (m_fnRefToCallees.get(fc).isEmpty()) {
				CfgFunctionReferenceNode uncalledFunctionRef = new CfgFunctionReferenceNode();
				uncalledFunctionRef.setFunctionContainer(fc);
				m_fnRefToCallees.put(fc, new IteratableSet<CfgFunctionReferenceNode>());
				m_fnRefToCallees.get(fc).add(uncalledFunctionRef);
			}
		}
	}
	
	private void climb(FunctionContainer fc,
			Stack<CfgFunctionReferenceNode> stack,
			List<Stack<CfgFunctionReferenceNode>> foundStacks) {
		
		if (fc != null && m_fnRefToCallees.containsKey(fc)) {
			for (CfgFunctionReferenceNode previous : m_fnRefToCallees.get(fc)) {
				
				if (!stack.contains(previous)) {
					stack.push(previous);
					FunctionContainer prev = m_containingFunction.get(previous);
					climb(prev, stack, foundStacks);
					stack.pop();
				}
			}
		} else {
			Stack<CfgFunctionReferenceNode> copy = new Stack<CfgFunctionReferenceNode>();
			copy.addAll(stack);
			Collections.reverse(copy);
			foundStacks.add(copy);
		}		
	}
	
	private void dumpCallGraph() {
		System.out.println("digraph {");
		for (FunctionContainer fc : m_functionContainers) {
			
			for (CfgFunctionReferenceNode frn : fc.getFunctionReferenceNodes()) {
				if (frn.isKnownFunction()){
					System.out.println(fc.getFunctionName() + " -> " + frn.getFunctionName() + ";");
				}
			}
		}
		System.out.println("}");
	}	
	
	private void linkFunctions(Set<FunctionContainer> functionContainers) {
		Map<Long, FunctionContainer> pcToFunctionStartNode = new NonIteratableMap<Long, FunctionContainer>();
		for(FunctionContainer fc : functionContainers){
			pcToFunctionStartNode.put(fc.getStartInstruction().getPc(), fc);
		}
		for (FunctionContainer fc : functionContainers){
			for (CfgFunctionReferenceNode g : fc.getFunctionReferenceNodes()){
				if(g.isKnownFunction()){
					FunctionContainer link = pcToFunctionStartNode.get(g.getFunctionStartPc());
					g.setFunctionContainer(link);
				}
			}
		}
	}

	private void setBranchTargets(InstructionDecoder id, Set<FunctionContainer> functionContainers) {
		for (CfgInstructionNode inst : id.getInstructions()) {
			if (inst.getXtaInstruction().getBranchPcRelative()) {
				long branchTarget = inst.getXtaInstruction().getBranchPcRelativeTargetPc();
				inst.getXtaInstruction().setBranchPcRelativeTargetFunction("0x" + Long.toHexString(branchTarget));
				for (FunctionContainer fc : functionContainers) {
					if (branchTarget == fc.getStartInstruction().getPc()) {
						inst.getXtaInstruction().setBranchPcRelativeTargetFunction(fc.getFunctionName());
						break;
					}
				}
			}
		}
	}	
	
	private void ensureAllFunctionsAreNamed( SymbolTable symbolTable, 
			Set<FunctionContainer> functionContainers, Map<String, Set<Long>> labelToPc) {

		Map<Long, String> pcToLabel = new NonIteratableMap<Long, String>();
		for (String label : labelToPc.keySet()) {
			for (Long pc : labelToPc.get(label)) {
				pcToLabel.put(pc, label);
			}
		}
		Set<String> seen = new NonIteratableSet<String>();
		for(FunctionContainer fc : functionContainers){
			CfgInstructionNode inst = fc.getStartInstruction();
			String functionName = symbolTable.functionName(inst.getPc());
			if (seen.contains(functionName)) {
				String label = pcToLabel.get(inst.getXtaInstruction().getPc());
				if(label != null){
					functionName = label;
				}
			}			
			fc.setFunctionName(functionName);
			seen.add(functionName);
		}
	}		
}
