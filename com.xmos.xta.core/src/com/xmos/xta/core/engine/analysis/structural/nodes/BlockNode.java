package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.BlockNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverserWithAncestors;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class BlockNode extends Node {
    private boolean m_ownsOwnAttrib;
    private BlockNodeAttributes m_attributes;
    private List<InstructionNode> m_insts;

    public BlockNode(List<InstructionNode> insts) {
        super();
        m_insts = insts;
        m_attributes = new BlockNodeAttributes(this);
        m_ownsOwnAttrib = true;
    }


    public BlockNode() {
        super();
        m_insts = new LinkedList<InstructionNode>();
        m_ownsOwnAttrib = false;
    }

    public Node copyRouteStructure() {
        BlockNode bn = new BlockNode();
        for (InstructionNode inst : m_insts){
            bn.m_insts.add((InstructionNode) inst.copyRouteStructure());
        }
        bn.m_attributes = m_attributes;
        return bn;
    }

    public List<InstructionNode> getInstructionList(){
        return m_insts;
    }

    public String getTypeName() {
        return "block";
    }

    public NodeAttributes getAttributes(boolean modifier) {
        if(modifier && !m_ownsOwnAttrib){
            m_ownsOwnAttrib = true;
            m_attributes = new BlockNodeAttributes(this);
        }
        return m_attributes;
    }

    public boolean isBlockNode() {
        return true;
    }

    public LinkedList<Instruction> getInstructions() {
        LinkedList<Instruction> insts = new LinkedList<Instruction>();
        for (Node inst : m_insts){
            insts.addAll(inst.getInstructions());
        }
        return insts;
    }

    public long getStartPc() {
        return getInstructions().getFirst().getPc();
    }

    public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverserWithAncestors nodeTraverser) {
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

    public TraverseStatus traverse(NodeTraverser nodeTraverser) {
        TraverseStatus status = nodeTraverser.foundBlockNode(this);
        if (status == TraverseStatus.CONTINUE) {
            for (Node instruction : m_insts) {
                status = instruction.traverse(nodeTraverser);
                if (status != TraverseStatus.CONTINUE) {
                    break;
                }
            }
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
            time += inst.getAttributes(false).getFixedTime().getCycles();
        }
        return new TimeDistributionBuckets(time);
    }

    //below here the methods have been verified useful

    public List<Node> iChildren() {
        return new LinkedList<Node>(m_insts);
    }

    public void buildListUp(List<Node> nodeList, Node start) {
        if(m_parent!= null){
            m_parent.buildListUp(nodeList, this);
        }
        for(InstructionNode inst : m_insts){
            if(inst == start) break;
            inst.buildListDown(nodeList);
        }
    }

    public void buildListDown(List<Node> nodeList) {
        for(InstructionNode inst : m_insts){
            inst.buildListDown(nodeList);
        }
    }
}
