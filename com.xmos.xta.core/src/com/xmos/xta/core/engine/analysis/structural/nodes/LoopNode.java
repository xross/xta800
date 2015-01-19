package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.libs.core.utils.NonIteratableMap;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.LoopNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;

public class LoopNode extends Node {
    private boolean m_ownsOwnAttrib;
    private LoopNodeAttributes m_attributes;
    private Node m_loopHeader;
    private List<Node> m_loops;
    private Map<Node, Boolean> m_alive;

    public LoopNode() {
        super();
        m_loopHeader = null;
        m_loops = new ArrayList<Node>();
        m_alive = new NonIteratableMap<Node, Boolean>();
        m_ownsOwnAttrib = false;
    }

    public LoopNode(Collection<Node> loops, Node base) {
        super();
        m_loopHeader = base;
        m_loopHeader.getAttributes(true).setIsLoopHeader(true);
        m_alive = new NonIteratableMap<Node, Boolean>();
        m_loops = new ArrayList<Node>();
        for (Node loop : loops) {
            m_loops.add(loop);
            m_alive.put(loop, true);
        }
        m_ownsOwnAttrib = true;
        m_attributes = new LoopNodeAttributes(this);
    }

    public Node copyRouteStructure() {
        LoopNode ln = new LoopNode();
        for (Node loop : m_loops) {
            Node clone = loop.copyRouteStructure();
            ln.m_loops.add(clone);
            ln.m_alive.put(clone, m_alive.get(loop));
        }
        ln.m_loopHeader = m_loopHeader.copyRouteStructure();
        ln.m_loopHeader.getAttributes(true).setIsLoopHeader(true);
        return ln;
    }

    public String getTypeName() {
        return "loop";
    }

    public NodeAttributes getAttributes(boolean modifier) {
        if(modifier && !m_ownsOwnAttrib){
            m_ownsOwnAttrib = true;
            m_attributes = new LoopNodeAttributes(this);
        }
        return m_attributes;
    }

    public Node getLoopHeader() {
        return m_loopHeader;
    }

    public Collection<Node> getExcludedLoops() {
        Collection<Node> excludedLoops = new IteratableSet<Node>();
        for (Node loop : m_loops) {
            if (!m_alive.get(loop)) {
                excludedLoops.add(loop);
            }
        }
        return excludedLoops;
    }

    public LinkedList<Instruction> getInstructions() {
        LinkedList<Instruction> insts = new LinkedList<Instruction>(m_loopHeader.getInstructions());
        for (Node al : getAliveLoops()) {
            insts.addAll(al.getInstructions());
        }
        return insts;
    }

    public long getStartPc() {
        return m_loopHeader.getStartPc();
    }

    public TraverseStatus traverse(NodeTraverser nodeTraverser, Object clientState) {
        TraverseStatus status = nodeTraverser.foundLoopNode(this, clientState);
        if (status == TraverseStatus.CONTINUE) {
            status = m_loopHeader.traverse( nodeTraverser, clientState);
            if (status == TraverseStatus.CONTINUE)
                for (Node node :  getAliveLoops()) {
                    status = node.traverse(nodeTraverser, clientState);
                    if (status != TraverseStatus.CONTINUE)
                        break;  // ABORT or NO_CHILDREN
                }
        }
        if (status != TraverseStatus.ABORT)
            status = nodeTraverser.foundLoopNodeExit(this, clientState);
        return (status == TraverseStatus.ABORT)? TraverseStatus.ABORT : TraverseStatus.CONTINUE;
    }

    public boolean isLoopNode() {
        return true;
    }

    public TimeDistributionBuckets getTimeSpread() {
        TimeDistributionBuckets tdb = new TimeDistributionBuckets();
        List<TimeDistributionBuckets> loopList = new LinkedList<TimeDistributionBuckets>();
        TimeDistributionBuckets loopHeaderBucket = m_loopHeader.getTimeSpread();
        loopHeaderBucket.repeat(m_attributes.getLoopIterations()+1);
        loopList.add(loopHeaderBucket);
        for (Node loop : getAliveLoops()){
            if (m_attributes.getLoopIterations() > 0) {
                TimeDistributionBuckets b = loop.getTimeSpread();
                b.repeat(m_attributes.getLoopIterations());
                loopList.add(b);
            }
        }
        tdb.add(loopList);
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

    public List<Node> iChildren() {
        List<Node> children = new LinkedList<Node>();
        children.add(m_loopHeader);
        for (Node node : getAliveLoops()) {
            children.add(node);
        }
        return children;
    }

    public void buildListUp(List<Node> nodeList, Node start) {
        if (m_parent!= null){
            m_parent.buildListUp(nodeList, this);
        }

        if (m_loops.contains(start)){
            m_loopHeader.buildListDown(nodeList);
        }
    }

    public void buildListDown(List<Node> nodeList) {
        m_loopHeader.buildListDown(nodeList);
        for (Node node : m_loops){
            node.buildListDown(nodeList);
        }
    }

    public void setAlive(Node currentNode, boolean alive) {
        assert(m_alive.containsKey(currentNode));
        m_alive.put(currentNode, alive);
    }

    private Collection<Node> getAliveLoops() {
        Collection<Node> aliveLoops = new IteratableSet<Node>();
        for (Node loop : m_loops) {
            if (m_alive.get(loop)) {
                aliveLoops.add(loop);
            }
        }
        return aliveLoops;
    }
}
