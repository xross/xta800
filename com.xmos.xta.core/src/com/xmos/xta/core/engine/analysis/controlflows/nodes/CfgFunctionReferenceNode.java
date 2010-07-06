package com.xmos.xta.core.engine.analysis.controlflows.nodes;

import java.util.Set;

import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.analysis.controlflows.FunctionContainer;
import com.xmos.xta.core.engine.references.Reference;

public class CfgFunctionReferenceNode extends CfgNode {
	
	//the first instruction of the function - from the global tree
	private CfgInstructionNode m_functionStartInstruction;
	
	//the instruction calling this function - from function container
	private CfgInstructionNode m_functionCallInstruction;
	
	private String m_functionName;
	private boolean m_isKnownFunctionTarget;
	private FunctionContainer m_functionContainer;
	private Set<Reference> m_appliesForReferences;
	
	public CfgNode clone() {
		return new CfgFunctionReferenceNode(this);
	}
	
    public String getTypeName() {
        return "CfgFunctionReferenceNode";
    }
	
	public CfgFunctionReferenceNode(CfgFunctionReferenceNode node) {
		super(node);
		m_functionCallInstruction  = node.m_functionCallInstruction;
		m_functionName             = node.m_functionName;
		m_functionContainer        = node.m_functionContainer;
		m_functionStartInstruction = node.m_functionStartInstruction;
		m_isKnownFunctionTarget    = node.m_isKnownFunctionTarget;
		m_appliesForReferences     = node.m_appliesForReferences;
	}

	public CfgFunctionReferenceNode(CfgInstructionNode source, CfgInstructionNode target) {
		super();
		m_functionCallInstruction = source;
		m_functionStartInstruction = target;
		m_functionName = "(unlinked to known function)";
		m_isKnownFunctionTarget = true;
		m_appliesForReferences = new NonIteratableSet<Reference>();
	}
	
	public CfgFunctionReferenceNode(CfgInstructionNode source) {
		super();
		m_functionCallInstruction = source;
		m_functionName = "(Call to unknown function)";
		m_isKnownFunctionTarget = false;
		m_appliesForReferences = new NonIteratableSet<Reference>();
	}	
	
	public CfgFunctionReferenceNode() {
		super();
		m_functionStartInstruction = null;
		m_functionCallInstruction = null;
		m_isKnownFunctionTarget = true;
		m_appliesForReferences = new NonIteratableSet<Reference>();
		m_functionName = "(Call graph root)";
	}

	public String toString() {
		return getTypeName() + getId() + "[" + m_functionName + "]";
	}
	
	public void setBackTrail(Reference ref) {
		m_appliesForReferences.add(ref);
	}

	public Set<Reference> getBackTrails() {
		return m_appliesForReferences;
	}	
	
	public boolean isKnownFunction(){
		return m_isKnownFunctionTarget;
	}

	public String getFunctionName(){
		return m_functionName;
	}
	
	public long getFunctionStartPc() {
		if (m_isKnownFunctionTarget) {
			return m_functionStartInstruction.getPc();
		} else {
			return 0x0;
		}
	}

	public long getFunctionCallPc() {
		if (m_functionCallInstruction != null) {
			return m_functionCallInstruction.getPc();
		} else {
			return 0x0;
		}
	}
	
	public CfgInstructionNode getFunctionStartInstruction(){
		return m_functionStartInstruction;
	}
	
	public void setFunctionContainer(FunctionContainer fc) {
		m_functionName = fc.getFunctionName();
		m_functionContainer = fc;
		m_isKnownFunctionTarget = true;
		m_functionStartInstruction = fc.getStartInstruction();
		assert(m_functionName != null);
	}
	
	public FunctionContainer getFunctionContainer(){
		return m_functionContainer;
	}	
}
