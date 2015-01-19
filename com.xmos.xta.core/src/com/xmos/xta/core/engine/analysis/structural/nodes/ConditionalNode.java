package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.libs.core.utils.NonIteratableMap;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.ConditionalNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;

public class ConditionalNode extends Node {
    private boolean m_ownsOwnAttrib;
    private ConditionalNodeAttributes m_attributes;
    private Collection<Node> m_options;
    private Map<Node, Boolean> m_alive;

    public ConditionalNode() {
        super();
        m_options = new IteratableSet<Node>();
        m_alive = new NonIteratableMap<Node, Boolean>();
        m_ownsOwnAttrib = false;
    }

    public ConditionalNode(Collection<Node> options, boolean isOptional) {
        super();
        m_options = options;
        m_alive = new NonIteratableMap<Node, Boolean>();
        if (isOptional){
            m_options.add(new BlockNode());
        }
        for (Node option : m_options){
            m_alive.put(option, true);
        }
        m_ownsOwnAttrib = true;
        m_attributes = new ConditionalNodeAttributes(this);
    }


    public Node copyRouteStructure() {
        ConditionalNode sn = new ConditionalNode();
        for (Node option : m_options) {
            Node clone = option.copyRouteStructure();
            sn.m_options.add(clone);
            sn.m_alive.put(clone, m_alive.get(option));
        }
        sn.m_attributes = m_attributes;
        return sn;
    }

    public String getTypeName() {
        return "cond";
    }

    public NodeAttributes getAttributes(boolean modifier) {
        if(modifier && !m_ownsOwnAttrib){
            m_ownsOwnAttrib = true;
            m_attributes = new ConditionalNodeAttributes(this);
        }
        return m_attributes;
    }

    public Collection<Node> getExcludedOptions() {
        Collection<Node> excludedOptions = new IteratableSet<Node>();
        for (Node option : m_options) {
            if (!m_alive.get(option)) {
                excludedOptions.add(option);
            }
        }
        return excludedOptions;
    }

    public LinkedList<Instruction> getInstructions() {
        LinkedList<Instruction> list = new LinkedList<Instruction>();
        for (Node node : getAliveOptions()){
            list.addAll(node.getInstructions());
        }
        return list;
    }

    public long getStartPc() {
        assert(false);
        return 0;
    }

    public TraverseStatus traverse(NodeTraverser nodeTraverser, Object clientState) {
        TraverseStatus status = nodeTraverser.foundConditionalNode(this, clientState);
        if (status == TraverseStatus.CONTINUE) {
            for (Node node : getAliveOptions()) {
                status = node.traverse(nodeTraverser, clientState);
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

    public Node getOption(Case c, Node ignoreIterationsAboveCond) {
        // Returns the best/worst case option of this conditional.
        Node option = getOption(c, ignoreIterationsAboveCond, m_options);
        return option;
    }

    public Node getUnsetOption(Case c, Node ignoreIterationsAboveCond) {
        // Gets the best/worst case option taken from each of the
        // option nodes that does not have its iteration
        // count explicitly set by the user.
        List<Node> allUnsetOptions = getAllUnsetOptions();
        if (allUnsetOptions.isEmpty())
            return null;

        return getOption(c, ignoreIterationsAboveCond, allUnsetOptions);
    }

    public boolean isCondNode() {
        return true;
    }

    public List<Node> getAllUnsetOptions() {
        List<Node> unsetOptions = new LinkedList<Node>();
        for (Node option : getAliveOptions()) {
            if (!getAttributes(false).getSetOption(option)) {
                unsetOptions.add(option);
            }
        }
        return unsetOptions;
    }

    public TimeDistributionBuckets getTimeSpread() {
        TimeDistributionBuckets tdb = new TimeDistributionBuckets();
        Collection<TimeDistributionBuckets> tdbs = new LinkedList<TimeDistributionBuckets>();
        for(Node node : getAliveOptions()){
            tdbs.add(node.getTimeSpread());
        }
        tdb.merge(tdbs);
        return tdb;
    }

    public List<Node> iChildren() {
        return new LinkedList<Node>(getAliveOptions());
    }

    public void buildListUp(List<Node> nodeList, Node start) {
        if (m_parent!= null) {
            m_parent.buildListUp(nodeList, this);
        }
    }

    public void buildListDown(List<Node> nodeList) {
        return;
    }

    public void setAlive(Node currentNode, boolean b) {
        assert(m_alive.containsKey(currentNode));
        m_alive.put(currentNode, b);
    }

    public Node getSuccessor(Node child) {
        return m_parent.getSuccessor(this);
    }

    private Collection<Node> getAliveOptions() {
        Collection<Node> aliveOptions = new IteratableSet<Node>();
        for (Node option : m_options) {
            if (m_alive.get(option)) {
                aliveOptions.add(option);
            }
        }
        return aliveOptions;
    }

    private Node getOption(Case c, Node ignoreIterationsAboveCond, Collection<Node> nodes) {
        // Finds the best/worst case of the real options..
        Iterator<Node> iter = nodes.iterator();
        Node caseNode = iter.next();
        Status s = new Status();
        double caseTime = caseNode.getAttributes(false).getTimeTaken(c, s, ignoreIterationsAboveCond);
        if (c == Case.WORST && s.isUnresolved()) {
            return caseNode;
        } else if (c == Case.BEST && (s.isZero() || s.isUnresolved())) {
            caseTime = Double.MAX_VALUE;
        }

        while (iter.hasNext()) {
            Node node = iter.next();
            Status status = new Status();
            double time = node.getAttributes(false).getTimeTaken(c, status, ignoreIterationsAboveCond);
            switch (c) {
            case WORST:
                if (status.isUnresolved()) {
                    return node;
                } else if ((time > caseTime)) {
                    caseNode = node;
                    caseTime = time;
                }
                break;

            case BEST:
                if (time < caseTime && !status.isZero() && !status.isUnresolved()) {
                    caseNode = node;
                    caseTime = time;
                }
                break;
            }
            assert(time >= 0);
        }
        return caseNode;
    }
}
