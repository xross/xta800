package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Aborter;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.BlockNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser.TraverseStatus;
import com.xmos.xta.core.engine.instructions.NullInstruction;
import com.xmos.xta.core.engine.instructions.XtaInstruction;

public class BlockNode extends IBufferNode {
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

	public BlockNode shallowCopy() {
		return new BlockNode(this);
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

	public List<Node> getIndirectChildren() {
		return new LinkedList<Node>(m_insts);
	}

	public Collection<NodeDescriptor> whatsNext(NodeDescriptor nodeDescriptor) {
		assert(m_insts.contains(nodeDescriptor.getNode()));
		
		int index = m_insts.indexOf(nodeDescriptor.getNode());
		if (index < m_insts.size() - 1) {
			Collection<NodeDescriptor> next = new TreeSet<NodeDescriptor>();
			next.add(new NodeDescriptor(m_insts.get(index + 1), nodeDescriptor.getAncestors()));
			return next;
			
		} else {
			NodeDescriptor thisNodeDescriptor = new NodeDescriptor(this, nodeDescriptor.getAncestors());
			thisNodeDescriptor.getAncestors().pop();
			
			if (!thisNodeDescriptor.getAncestors().isEmpty()) {
				return thisNodeDescriptor.getAncestors().peek().whatsNext(thisNodeDescriptor);
			}
			return new TreeSet<NodeDescriptor>();
		}
	}

	public Collection<NodeDescriptor> whatsBefore(NodeDescriptor nodeDescriptor) {
		assert(m_insts.contains(nodeDescriptor.getNode()));
		
		int index = m_insts.indexOf(nodeDescriptor.getNode());
		if (index > 0) {
			Collection<NodeDescriptor> before = new TreeSet<NodeDescriptor>();
			before.add(new NodeDescriptor(m_insts.get(index - 1), nodeDescriptor.getAncestors()));
			return before;
			
		} else {
			NodeDescriptor thisNodeDescriptor = new NodeDescriptor(this, nodeDescriptor.getAncestors());
			thisNodeDescriptor.getAncestors().pop();

			if (!thisNodeDescriptor.getAncestors().isEmpty()) {
				return thisNodeDescriptor.getAncestors().peek().whatsBefore(thisNodeDescriptor);
			}
			return new TreeSet<NodeDescriptor>();
		}
	}

	public List<NodeDescriptor> getDirectChildren(Stack<Node> ancestors) {
		List<NodeDescriptor> list = new LinkedList<NodeDescriptor>();		
		ancestors.push(this);
		list.add(new NodeDescriptor(m_insts.get(0), ancestors));
		ancestors.pop();
		return list;
	}

	public void insertFnops() {
		List<InstructionNode> insts = new LinkedList<InstructionNode>();
		for(InstructionNode inst : m_insts){
			List<Long> fnops =  m_worstIbufferSequence.getfnops();
			XtaInstruction xtaInst = inst.getXtaInstruction();
			for (long pc : fnops){
				if(xtaInst.getPc() == pc){
					XtaInstruction fnop = new NullInstruction("--FNOP--", 0, xtaInst.getPc(), 0);
					InstructionNode fnopNode = new InstructionNode(fnop);
					insts.add(fnopNode);
				}
			}
			insts.add(inst);
		}
		m_insts = insts;
	}

    public Set<NodeDescriptor> getEndInstructions(Stack<Node> ancestors) {
	    Set<NodeDescriptor> set = new TreeSet<NodeDescriptor>();
		
		ancestors.push(this);
		if (m_insts.get(m_insts.size() - 1).getXtaInstruction().isFnop()) {
			set.add(new NodeDescriptor(m_insts.get(m_insts.size() - 2), ancestors));
			
		} else {
			set.add(new NodeDescriptor(m_insts.get(m_insts.size() - 1), ancestors));
		}
		ancestors.pop();

		return set;
	}
	
    public TimeDistributionBuckets getTimeSpread() {
	    long time = 0;
	    for(InstructionNode inst : m_insts){
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
}
