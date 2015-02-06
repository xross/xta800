package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;

import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;

public class SequenceNode extends Node {

    private boolean m_ownsOwnAttrib;
    private NodeAttributes m_attributes;
    List<Node> m_nodes;

    public SequenceNode(List<Node> nodes) {
        super();
        assert (nodes != null);
        m_nodes = nodes;
        m_ownsOwnAttrib = true;
        m_attributes = new NodeAttributes(this);
    }

    public SequenceNode() {
        super();
        m_nodes = new LinkedList<Node>();
        m_ownsOwnAttrib = false;
    }

    public Node copyRouteStructure() {
        SequenceNode pn = new SequenceNode();
        pn.copyBase(this);
        for (Node path : m_nodes) {
            Node clone = path.copyRouteStructure();
            pn.m_nodes.add(clone);
        }
        pn.m_attributes = m_attributes;
        return pn;
    }
    public boolean isSequenceNode() {
        return true;
    }
    public String getTypeName() {
        return "seq";
    }

    public NodeAttributes getAttributes(boolean modifier) {
        if(modifier && !m_ownsOwnAttrib){
            m_ownsOwnAttrib = true;
            m_attributes = new NodeAttributes(this);
        }
        return m_attributes;
    }

    public LinkedList<Instruction> getInstructions() {
        LinkedList<Instruction> list = new LinkedList<Instruction>();
        for (Node node : m_nodes) {
            list.addAll(node.getInstructions());
        }
        return list;
    }

    public long getStartPc() {
        return m_nodes.get(0).getStartPc();
    }

    public TraverseStatus traverse(NodeTraverser nodeTraverser, Object clientState) {
        TraverseStatus status = nodeTraverser.foundSequenceNode(this, clientState);
        if (status == TraverseStatus.CONTINUE)
            for (Node node : m_nodes) {
                status = node.traverse(nodeTraverser, clientState);
                if (status != TraverseStatus.CONTINUE)
                    break;  // ABORT or NO_CHILDREN
            }
        if (status != TraverseStatus.ABORT)
            status = nodeTraverser.foundSequenceNodeExit(this, clientState);
        return (status == TraverseStatus.ABORT)? TraverseStatus.ABORT : TraverseStatus.CONTINUE;
    }

    public TimeDistributionBuckets getTimeSpread() {
        TimeDistributionBuckets tdb = new TimeDistributionBuckets();
        List<TimeDistributionBuckets> tsbPath = new LinkedList<TimeDistributionBuckets>();
        for (Node node : m_nodes) {
            tsbPath.add(node.getTimeSpread());
        }
        tdb.add(tsbPath);
        return tdb;
    }

    public List<Node> getPath() {
        return m_nodes;
    }

    public boolean canReplaceChildren() {
        return true;
    }

    public void replaceChild(Node oldNode, Node newNode) {
        int index = m_nodes.indexOf(oldNode);
        m_nodes.remove(index);
        m_nodes.add(index, newNode);
    }

    public void replaceChildren(List<Node> children) {
        m_nodes.clear();
        m_nodes.addAll(children);
    }

    //below here the methods have been verified useful

    public List<Node> iChildren() {
        return m_nodes;
    }

    public void buildListUp(List<Node> nodeList, Node start) {
        if(getParent() != null){
            getParent().buildListUp(nodeList, this);
        }
        for(Node node : m_nodes){
            if(node == start) break;
            node.buildListDown(nodeList);
        }
    }

    public void buildListDown(List<Node> nodeList) {
        for(Node node : m_nodes){
            node.buildListDown(nodeList);
        }

    }



}
