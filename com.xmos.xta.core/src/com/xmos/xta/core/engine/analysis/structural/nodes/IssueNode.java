package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.IssueNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverserWithAncestors;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;

public class IssueNode extends Node {
    private boolean m_ownsOwnAttrib;
    private IssueNodeAttributes m_attributes;
    private Instruction m_inst;

    public IssueNode(CfgInstructionNode node) {
        super(node);
        m_inst = node.getInstruction();
        m_attributes = new IssueNodeAttributes(this);
        m_ownsOwnAttrib = true;
    }

    public IssueNode(IssueNode node) {
        super(node);
        m_inst = node.m_inst;
        m_attributes = node.m_attributes;
        m_ownsOwnAttrib = false;
    }

    public String toString(){
        return this.getInstruction().toString();
    }

    public Node copyRouteStructure() {
        IssueNode in = new IssueNode(this);
        in.m_attributes = m_attributes;
        return in;
    }

    public String getTypeName() {
        return "instruction";
    }

    public NodeAttributes getAttributes(boolean modifier) {
        if(modifier && !m_ownsOwnAttrib){
            m_ownsOwnAttrib = true;
            m_attributes = new IssueNodeAttributes(this);
        }
        return m_attributes;
    }

    public LinkedList<Instruction> getInstructions() {
        LinkedList<Instruction> inst = new LinkedList<Instruction>();
        inst.add(m_inst);
        return inst;
    }

    public long getStartPc() {
        return m_inst.getPc();
    }

    public TraverseStatus traverse(Stack<Node> ancestors, NodeTraverserWithAncestors nodeTraverser) {
        TraverseStatus status = nodeTraverser.foundIssueNode(this, ancestors);
        if (status == TraverseStatus.ABORT) {
            return status;
        }
        return TraverseStatus.CONTINUE;
    }

    public TraverseStatus traverse(NodeTraverser nodeTraverser) {
        TraverseStatus status = nodeTraverser.foundIssueNode(this);
        if (status == TraverseStatus.ABORT) {
            return status;
        }
        return TraverseStatus.CONTINUE;
    }
    public boolean isIssueNode() {
        return true;
    }

    public String getDescription() {
        return String.format("0x%x: %s %s",
                m_inst.getPc(),
                m_inst.getMnemonic(),
                m_inst.getOperandString());
    }

    public Instruction getInstruction() {
        return m_inst;
    }

    public TimeDistributionBuckets getTimeSpread() {
        return new TimeDistributionBuckets(m_attributes.getFixedTime().getCycles());
    }

    public List<Node> iChildren() {
        List<Node> children = new LinkedList<Node>();
        return children;
    }

    public void buildListDown(List<Node> nodeList) {
        nodeList.add(this);
    }

    public void buildListUp(List<Node> nodeList, Node start) {
        assert(start == null);
        if(m_parent!= null){
            m_parent.buildListUp(nodeList, this);
        }
        nodeList.add(this);
    }

    public long getPC() {
        return m_inst.getPc();
    }
}
