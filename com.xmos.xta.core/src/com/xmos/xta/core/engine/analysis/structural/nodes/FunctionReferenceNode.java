package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.binary.XtaInstruction;
import com.xmos.xta.core.engine.analysis.controlflows.FunctionContainer;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverserWithAncestors;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.references.Reference;

public class FunctionReferenceNode extends Node {

	//the first instruction of the function - from the global tree
	private InstructionNode m_functionStartInstruction;
	
	//the instruction calling this function - from function container
	private InstructionNode m_functionCallInstruction;
	
	private String m_functionName;
	private boolean m_isKnownFunctionTarget;
	
	//private FunctionNode m_cachedFunctionNode;

	private Set<Reference> m_appliesForReferences;

	private FunctionContainer m_functionContainer;
	
	public FunctionReferenceNode(FunctionReferenceNode old){
		m_functionCallInstruction =old.m_functionCallInstruction;
		m_functionName = old.m_functionName;
		m_functionContainer = old.m_functionContainer;
		m_functionStartInstruction = old.m_functionStartInstruction;
		m_isKnownFunctionTarget = old.m_isKnownFunctionTarget;
		m_appliesForReferences = old.m_appliesForReferences;
	}

	public FunctionReferenceNode(InstructionNode source, InstructionNode target) {
		m_appliesForReferences = new NonIteratableSet<Reference>();
		m_functionCallInstruction = source;
		m_functionStartInstruction = target;
		m_functionName = "(unlinked to known function)";
		m_isKnownFunctionTarget = true;
	}
	
	public FunctionReferenceNode(InstructionNode source) {
		m_appliesForReferences = new NonIteratableSet<Reference>();
		m_functionCallInstruction = source;
		m_functionName = "(Call to unknown function)";
		m_isKnownFunctionTarget = false;
	}

	public FunctionReferenceNode() {
		m_functionStartInstruction = null;
		m_functionCallInstruction = null;
		m_isKnownFunctionTarget = true;
		m_appliesForReferences = new NonIteratableSet<Reference>();
		m_functionName = "(Call graph root)";
	}

	public String toString(){
		return "ref:"+m_functionName;
	}
	
	public void getBackTrail(StringBuffer refStr) {
		super.getBackTrail(refStr);
		refStr.append(String.format("CALL:0x%x,", m_functionCallInstruction.getPC()));
	}
	
	public InstructionNode getFunctionStartInstruction(){
		return m_functionStartInstruction;
	}

	public long getFunctionCallPc() {
		if (m_functionCallInstruction != null) {
			return m_functionCallInstruction.getPC();
		} else {
			return 0x0;
		}
	}
	
	public long getFunctionStartPc() {
		if (m_isKnownFunctionTarget) {
			return m_functionStartInstruction.getPC();
		} else {
			return 0x0;
		}
	}

	public boolean isKnownFunction(){
		return m_isKnownFunctionTarget;
	}

	public String getFunctionName(){
		return m_functionName;
	}

	public FunctionContainer getFunctionContainer(){
		return m_functionContainer;
	}
	
	public void setFunction(FunctionContainer fc) {
		m_functionName = fc.getFunctionName();
		m_functionContainer = fc;
		m_isKnownFunctionTarget = true;
		m_functionStartInstruction = fc.getStartInstruction();
		assert(m_functionName != null);
	}

	public Node copyRouteStructure(Aborter aborter) throws AbortedException {
		FunctionReferenceNode newFrn = new FunctionReferenceNode(this);
		return newFrn;
	}

	public NodeAttributes getAttributes() {
		return new NodeAttributes(this);
	}

	public LinkedList<XtaInstruction> getInstructions() {
		assert(false);
		return null;
	}

	public InstructionNode getStartInstruction() {
		return m_functionStartInstruction;
	}

	public long getStartPc() {
		assert(false);
		return 0;
	}

	public TimeDistributionBuckets getTimeSpread() {
		assert(false);
		return new TimeDistributionBuckets();	
	}

	public String getTypeName() {
		return "Function_Ref";
	}

	public TraverseStatus traverse(Stack<Node> ancestors,
			NodeTraverserWithAncestors nodeTraverser) {
		assert(false);
		return TraverseStatus.ABORT;
	}
	
	public TraverseStatus traverse(NodeTraverser nodeTraverser) {
		return TraverseStatus.CONTINUE;
	}

	public List<Node> iChildren() {
		return new LinkedList<Node>();
	}

	public void buildListUp(List<Node> nodeList, Node start) {
		if(m_parent!= null){
			m_parent.buildListUp(nodeList, this);
		}
		nodeList.add(this);

	}

	public void buildListDown(List<Node> nodeList) {
		return;
	}

	public void setBackTrail(Reference ref) {
		m_appliesForReferences.add(ref);
	}

	public Set<Reference> getBackTrails() {
		return m_appliesForReferences;
	}

	public Node getSuccessor(Node child) {
        assert(false);
        return null;
	}		
}
