package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.BlockNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser.TraverseStatus;
import com.xmos.xta.core.engine.instructions.XtaInstruction;

public class BlockNode extends Node {
	private BlockNodeAttributes m_attributes;	
	private List<InstructionNode> m_insts;

	private static final boolean COPY_INSTRUCTIONS = true;
	
	public BlockNode(BlockNode block) {
		m_insts = block.m_insts;
		m_attributes = new BlockNodeAttributes(this);
	}
	
	public BlockNode(List<InstructionNode> insts) {
		super();
		m_insts = insts;
        m_attributes = new BlockNodeAttributes(this);
	}
		
	//this is for empty basic blocks for conditionals
	public BlockNode() {
		m_insts = new LinkedList<InstructionNode>();
		m_attributes = new BlockNodeAttributes(this);
	}

	public BlockNode copyStructure() {
		return new BlockNode(this);
	}
	
	public List<InstructionNode> getInstructionList(){
		return m_insts;
	}
	
	public Node deepCopy(Aborter aborter) throws AbortedException {
		List<InstructionNode> insts;
		if (COPY_INSTRUCTIONS){
			insts = new LinkedList<InstructionNode>();
			for (InstructionNode inst : m_insts){
				insts.add((InstructionNode) inst.deepCopy(aborter));
			}
		} else {
			insts = m_insts;
		}
		return new BlockNode(insts);
	}
	
    public String getTypeName() {
        return "block";
    }
    
    public NodeAttributes getAttributes() {
    	return m_attributes;
    }
    
	public boolean isBlockNode() {
		return true;
	}
    
	public LinkedList<XtaInstruction> getInstructions() {
		LinkedList<XtaInstruction> insts = new LinkedList<XtaInstruction>();
		for (Node inst : m_insts){
			insts.addAll(inst.getInstructions());
		}
		return insts;
	}

	public XtaInstruction getStartInstruction() {
		return m_insts.get(0).getXtaInstruction();
	}

	public long getStartPc() {
		return getInstructions().getFirst().getPc();
	}	
		
	public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverser nodeTraverser) {
		TraverseStatus status = nodeTraverser.foundBlockNode(this, ancestors);
		if (status == TraverseStatus.CONTINUE) {
			ancestors.push(this);
			for (Node instruction : m_insts) {
				status = instruction.traverse(ancestors, nodeTraverser);
				if (status != TraverseStatus.CONTINUE) {
					break;
				}
			}		
			ancestors.pop();
		}
		
		if (status == TraverseStatus.ABORT) {
			return status;
		}
		return TraverseStatus.CONTINUE;
	}

    public TimeDistributionBuckets getTimeSpread() {
		TimeUnit fixedTime = m_attributes.getFixedTime();
		if (fixedTime != null) {
			return new TimeDistributionBuckets(fixedTime.getCycles());
		}

		long time = 0;
		for (InstructionNode inst : m_insts) {
			time += inst.getAttributes().getFixedTime().getCycles();
		}
    	return new TimeDistributionBuckets(time);
    }

	public void replaceChild(Node oldNode, Node newNode) {
		assert(oldNode instanceof InstructionNode);
		assert(newNode instanceof InstructionNode);
		int index = m_insts.indexOf(oldNode);
		m_insts.remove(index);
		m_insts.add(index, (InstructionNode)newNode);
	}
	
	public void replaceChildren(List<Node> children) {
		m_insts.clear();
		for (Node child : children) {
			assert(child instanceof InstructionNode);
			m_insts.add((InstructionNode)child);
		}
	}
	
	//below here the methods have been verified useful
	
	public List<Node> iChildren() {
		return new LinkedList<Node>(m_insts);
	}

	public Set<Node> structurGraphNext(Node currentNode) {
		assert(m_insts.contains(currentNode));
		int index = m_insts.indexOf(currentNode);
		if (index < m_insts.size() - 1) {
			Set<Node> next = new TreeSet<Node>();
			next.add(m_insts.get(index +1));
			return next;
		} else {
			assert(getParent() != null);
			return getParent().structurGraphNext(this);
		}
	}	
	
	public Set<InstructionNode> getInstructionNodes() {
		Set<InstructionNode> insts = new TreeSet<InstructionNode>(m_insts);
		return insts;
	}
	
	public Set<FunctionReferenceNode> getFunctionReferencesNodes() {
		Set<FunctionReferenceNode> fnRef = new TreeSet<FunctionReferenceNode>();
		return fnRef;
	}
	
	public void buildListUp(List<InstructionNode> nodeList,
			Stack<FunctionReferenceNode> stack, Node start) {
		m_parent.buildListUp(nodeList, stack, this);
		for(InstructionNode inst : m_insts){
			if(inst == start) break;
			nodeList.add(inst);
		}
	}
	public void buildListDown(List<InstructionNode> nodeList) {
		for(InstructionNode inst : m_insts){
			nodeList.add(inst);
		}
		
	}

}
