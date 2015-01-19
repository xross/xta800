package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.InfiniteLoopNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;

public class InfiniteLoopNode extends Node {
    private boolean m_ownsOwnAttrib;
    private NodeAttributes m_attributes;
    private Node m_loopHeader;
    private List<Node> m_loops;

    public InfiniteLoopNode() {
        super();
        m_loopHeader = null;
        m_loops = new ArrayList<Node>();
        m_ownsOwnAttrib = false;
    }

    public InfiniteLoopNode(Collection<Node> loops, Node base) {
        super();
        m_loopHeader = base;
        m_loopHeader.getAttributes(true).setIsLoopHeader(true);
        m_loops = new ArrayList<Node>();
        for (Node loop : loops) {
            m_loops.add(loop);
        }
        m_ownsOwnAttrib = true;
        m_attributes = new InfiniteLoopNodeAttributes(this);
    }

    public Node copyRouteStructure() {
        InfiniteLoopNode ln = new InfiniteLoopNode();
        for (Node loop : m_loops) {
            Node clone = loop.copyRouteStructure();
            ln.m_loops.add(clone);
        }
        ln.m_loopHeader = m_loopHeader.copyRouteStructure();
        ln.m_loopHeader.getAttributes(true).setIsLoopHeader(true);
        ln.m_attributes = m_attributes;
        return ln;
    }

    public String getTypeName() {
        return "infiniteloop";
    }

    public NodeAttributes getAttributes(boolean modifier) {
        if(modifier && !m_ownsOwnAttrib){
            m_ownsOwnAttrib = true;
            m_attributes = new InfiniteLoopNodeAttributes(this);
        }
        return m_attributes;
    }

    public LinkedList<Instruction> getInstructions() {
        LinkedList<Instruction> insts = new LinkedList<Instruction>(m_loopHeader.getInstructions());
        for (Node al : m_loops) {
            insts.addAll(al.getInstructions());
        }
        return insts;
    }

    public long getStartPc() {
        return m_loopHeader.getStartPc();
    }

    public TraverseStatus traverse(NodeTraverser nodeTraverser) {
        TraverseStatus status = nodeTraverser.foundInfiniteLoopNode(this);
        if (status == TraverseStatus.CONTINUE) {
            m_loopHeader.traverse(nodeTraverser);
            for (Node node : m_loops) {
                status = node.traverse(nodeTraverser);
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
    public boolean isInfiniteLoopNode() {
        return true;
    }

    public TimeDistributionBuckets getTimeSpread() {
        TimeDistributionBuckets tdb = new TimeDistributionBuckets();
        tdb.setInfinite();
        return tdb;
    }

    public boolean canReplaceChildren() {
        return true;
    }

    public void replaceChild(Node oldNode, Node newNode) {
        if (oldNode == m_loopHeader) {
            m_loopHeader = newNode;

        } else {
            int index = m_loops.indexOf(oldNode);
            m_loops.remove(index);
            m_loops.add(index, newNode);
        }
    }

    public void replaceChildren(List<Node> children) {
        if (!children.isEmpty()) {
            m_loopHeader = children.get(0);
            for (int index = 1; index < children.size(); ++index) {
                m_loops.add(children.get(index));
            }
        }
    }

    //below here the methods have been verified useful

    public List<Node> iChildren() {
        List<Node> children = new LinkedList<Node>();
        children.add(m_loopHeader);
        for (Node node : m_loops) {
            children.add(node);
        }
        return children;
    }

    public void buildListUp(List<Node> nodeList, Node start) {
        if(m_parent!= null){
            m_parent.buildListUp(nodeList, this);
        }
        if(m_loops.contains(start)){
            m_loopHeader.buildListDown(nodeList);
        }

    }

    public void buildListDown(List<Node> nodeList) {
        m_loopHeader.buildListDown(nodeList);
        for(Node node : m_loops){
            node.buildListDown(nodeList);
        }
    }
}
