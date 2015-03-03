package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;

import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.BlockNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class BlockNode extends Node {
    private List<IssueNode> m_issueNodes;
    private BlockNodeAttributes m_attributes;
    private boolean m_ownsOwnAttrib;

    public BlockNode(List<IssueNode> issueNodes) {
        super();
        m_issueNodes = issueNodes;
        m_attributes = new BlockNodeAttributes(this);
        m_ownsOwnAttrib = true;
    }


    public BlockNode() {
        super();
        m_issueNodes = new LinkedList<IssueNode>();
        m_ownsOwnAttrib = false;
    }

    public Node copyRouteStructure() {
        BlockNode bn = new BlockNode();
        bn.copyBase(this);
        for (IssueNode issueNode : m_issueNodes){
            bn.m_issueNodes.add((IssueNode) issueNode.copyRouteStructure());
        }
        bn.m_attributes = m_attributes;
        m_ownsOwnAttrib = false;
        return bn;
    }

    public List<IssueNode> getInstructionList(){
        return m_issueNodes;
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
        for (Node issueNode : m_issueNodes){
            insts.addAll(issueNode.getInstructions());
        }
        return insts;
    }

    public long getStartPc() {
        return getInstructions().getFirst().getPc();
    }

    public TraverseStatus traverse(NodeTraverser nodeTraverser, Object clientState) {
        TraverseStatus status = nodeTraverser.foundBlockNode(this, clientState);
        if (status == TraverseStatus.CONTINUE)
            for (Node node : m_issueNodes) {
                status = node.traverse(nodeTraverser, clientState);
                if (status != TraverseStatus.CONTINUE)
                    break;  // ABORT or NO_CHILDREN
            }
        if (status != TraverseStatus.ABORT)
            status = nodeTraverser.foundBlockNodeExit(this, clientState);
        return (status == TraverseStatus.ABORT)? TraverseStatus.ABORT : TraverseStatus.CONTINUE;
    }

    public void insertFnopsBefore(IssueNode child, int num) {
        int i = m_issueNodes.indexOf(child);
        assert(i != -1);
        for (; num>0; --num)
            m_issueNodes.add(i, child.createFnopNode());
    }

    public TimeDistributionBuckets getTimeSpread() {
        TimeUnit fixedTime = m_attributes.getFixedTime();
        if (fixedTime != null) {
            return new TimeDistributionBuckets(fixedTime.getCycles());
        }

        long time = 0;
        for (IssueNode issueNode : m_issueNodes) {
            time += issueNode.getAttributes(false).getFixedTime().getCycles();
        }
        return new TimeDistributionBuckets(time);
    }

    //below here the methods have been verified useful

    public List<Node> iChildren() {
        return new LinkedList<Node>(m_issueNodes);
    }

    public void buildListUp(List<Node> nodeList, Node start) {
        if(getParent() != null){
            getParent().buildListUp(nodeList, this);
        }
        for(IssueNode issueNode : m_issueNodes){
            if(issueNode == start) break;
            issueNode.buildListDown(nodeList);
        }
    }

    public void buildListDown(List<Node> nodeList) {
        for(IssueNode issueNode : m_issueNodes){
            issueNode.buildListDown(nodeList);
        }
    }
}
