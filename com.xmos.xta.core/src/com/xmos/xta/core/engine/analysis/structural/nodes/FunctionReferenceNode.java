package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverserWithAncestors;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.references.Reference;

public class FunctionReferenceNode extends Node {

	private InstructionNode m_functionStartInstruction;
	private long m_functionCallPc;

	private String m_functionName;
	private FunctionNode m_functionNode;
	private boolean m_isKnownFunctionTarget;
	private boolean m_isRecursive;
	private boolean m_cfgRef = false;
	private FunctionNode m_recursiveFunction;
	private FunctionNode m_unknownFunction;
	private Set<Reference> m_appliesForReferences;
	
	public FunctionReferenceNode(FunctionReferenceNode old){
		m_functionCallPc =old.m_functionCallPc;
		m_functionName = old.m_functionName;
		m_functionNode = old.m_functionNode;
		m_functionStartInstruction = old.m_functionStartInstruction;
		m_isKnownFunctionTarget = old.m_isKnownFunctionTarget;
		m_isRecursive = old.m_isRecursive;
		m_alias = old.m_alias;
		m_recursiveFunction = old.m_recursiveFunction;
		m_unknownFunction = old.m_unknownFunction;
		m_appliesForReferences = old.m_appliesForReferences;
	}

	public FunctionReferenceNode(InstructionNode source, InstructionNode target) {
		m_isRecursive = false;
		m_functionNode = null;
		m_appliesForReferences = new TreeSet<Reference>();
		m_functionCallPc = source.getXtaInstruction().getPc();
		m_functionStartInstruction = target;
		if(target == null){
			m_functionName = "(Call to unknown function)";
			m_isKnownFunctionTarget = false;
		} else {
			m_functionName = "(unlinked to known function)";
			m_isKnownFunctionTarget = true;
			
		}
		m_recursiveFunction = null;
		m_unknownFunction = null;
	}

	public FunctionReferenceNode() {
		m_functionName = null;
		m_functionNode = null;
		m_functionStartInstruction = null;
		m_functionCallPc = 0x0;
		m_isKnownFunctionTarget = false;
		m_isRecursive = false;
		m_recursiveFunction = null;
		m_unknownFunction = null;
		m_appliesForReferences = new TreeSet<Reference>();;
	}

	public void getBackTrail(StringBuffer refStr) {
		super.getBackTrail(refStr);
		refStr.append(String.format("CALL:0x%x,", m_functionCallPc));
	}

	public long getFunctionCallPc(){
		return m_functionCallPc;
	}
	
	public void reset(){
		m_recursiveFunction = null;
		m_unknownFunction = null;
	}

	public void setRecursive(boolean b) {
		m_isRecursive = b;
	}

	public boolean isCfgRef(){
		return m_cfgRef;
	}

	public void setCfgRef(){
		m_cfgRef = true;
	}

	public String toString(){
		String ret = "fnRef" + getId() + " ";
		if(m_isKnownFunctionTarget){
			ret += "(Call to " + m_functionName +")";
		} else {
			ret += "(Call to unknown function)";
		}
		return ret;
	}
	
	public InstructionNode getFunctionStartInstruction(){
		return m_functionStartInstruction;
	}

	public long getFunctionStartPc(){
		if(m_isKnownFunctionTarget){
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

	public FunctionNode getFunctionNode(){
		if(m_isRecursive){
			String fnName = getFunctionName() + "(Recursive)";
			m_recursiveFunction = new FunctionNode();
			m_recursiveFunction.setFunctionName(fnName);
			return m_recursiveFunction;
		} else {
			if(m_isKnownFunctionTarget){
				return m_functionNode;
			} else {
				String fnName = "(Call to unknown function)";
				m_unknownFunction = new FunctionNode();
				m_unknownFunction.setFunctionName(fnName);
				return m_unknownFunction;
			}
		}
	}

	public void setFunction(String functionName, FunctionNode link) {
		m_functionName = functionName;
		m_functionNode = link;
		m_isKnownFunctionTarget = true;
		assert(m_functionNode != null);
		assert(m_functionName != null);
	}

	public Node copyStructure() {
		assert(verifyLinkProcess());
		FunctionReferenceNode f = new FunctionReferenceNode(this);
		assert(f.verifyLinkProcess());
		return f;
	}

	public Node deepCopy(Aborter aborter) throws AbortedException {
		FunctionReferenceNode newFrn = new FunctionReferenceNode(this);
		if(getFunctionNode() != null){
			newFrn.m_functionNode = (FunctionNode) getFunctionNode().deepCopy(aborter);
		}
		return newFrn;
	}

	public NodeAttributes getAttributes() {
		return new NodeAttributes(this);
	}

	public LinkedList<XtaInstruction> getInstructions() {
		if(isKnownFunction()){
			return getFunctionNode().getInstructions();
		} else {
			return new LinkedList<XtaInstruction>();
		}
	}

	public XtaInstruction getStartInstruction() {
		assert(false);
		return null;
	}

	public long getStartPc() {
		assert(false);
		return 0;
	}

	public TimeDistributionBuckets getTimeSpread() {
		if(getFunctionNode() != null){
			return m_functionNode.getTimeSpread();
		} else {
			return new TimeDistributionBuckets();
		}
		
	}

	public String getTypeName() {
		if(m_isKnownFunctionTarget){
			return "Function_Ref(" + getFunctionNode() +")";
		} else {
			return "Function_Ref";
		}
	}

	public void replaceChild(Node oldNode, Node newNode) {
		assert(false);
	}

	public void replaceChildren(List<Node> newChildren) {
		assert(false);
	}

	public TraverseStatus traverse(Stack<Node> ancestors,
			NodeTraverserWithAncestors nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundFunctionReferenceNode(this, ancestors);
		if(getFunctionNode() != null ){
			if (status == TraverseStatus.CONTINUE) {
				status = getFunctionNode().traverse(ancestors, nodeTraverser);

			}
		}

		if (status == TraverseStatus.ABORT) {
			return status;
		}
		return TraverseStatus.CONTINUE;
	}
	
	public TraverseStatus traverse(NodeTraverser nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundFunctionReferenceNode(this);
		if(getFunctionNode() != null ){
			if (status == TraverseStatus.CONTINUE) {
				status = getFunctionNode().traverse(nodeTraverser);
			}
		}

		if (status == TraverseStatus.ABORT) {
			return status;
		}
		return TraverseStatus.CONTINUE;
	}
	/**
	 * This is to verify that after the link process has happened that all branches are linked to their
	 * respective function
	 */
	public boolean verifyLinkProcess() {
		if(m_isKnownFunctionTarget){
			assert(m_functionName != null);
			return (getFunctionNode() != null);
		} else {
			return (getFunctionNode() == m_unknownFunction);
		}
	}

	public List<Node> iChildren() {
		if(getFunctionNode() != null){
			List<Node> children = new LinkedList<Node>();
			children.add(getFunctionNode());
			return children;
		} else {
			return new LinkedList<Node>();
		}
	}

	public Set<Node> structurGraphNext(Node childNode) {
		assert(getParent() != null);
		return getParent().structurGraphNext(childNode);
	}

	public Set<InstructionNode> getInstructionNodes() {
		Set<InstructionNode> insts = new TreeSet<InstructionNode>();
		insts.addAll(getFunctionNode().getInstructionNodes());
		return insts;
	}

	public Set<FunctionReferenceNode> getFunctionReferencesNodes() {
		Set<FunctionReferenceNode> fnRef = new TreeSet<FunctionReferenceNode>();
		addFunctionRefToRefSet(fnRef, getFunctionNode());
		return fnRef;
	}


	private FunctionReferenceNode m_alias;
	public void setAlias(FunctionReferenceNode copy) {
		m_alias = copy;
	}

	public FunctionReferenceNode getAlias(){
		return m_alias;
	}

	public void buildListUp(List<Node> nodeList,
			Stack<FunctionReferenceNode> stack, Node start) {
		if(m_parent!= null){
			m_parent.buildListUp(nodeList, stack, this);
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

}
