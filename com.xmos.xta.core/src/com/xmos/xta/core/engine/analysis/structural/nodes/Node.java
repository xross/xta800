package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;

public abstract class Node implements Comparable<Node> {
    public static int s_numNodes;

    private int m_id;
    private Node m_parent;
    private boolean m_unresolved;
    private boolean m_recursion;
    private boolean m_isFolded;

    public enum Case {
        WORST,
        BEST,
    }

    public abstract String                  getTypeName();
    public abstract NodeAttributes          getAttributes(boolean modifyer);
    public abstract Node                    copyRouteStructure(); // must call Node.copy()
    public abstract TraverseStatus          traverse(NodeTraverser nodeTraverser, Object clientState);
    public abstract LinkedList<Instruction> getInstructions();
    public abstract TimeDistributionBuckets getTimeSpread();

    //need to take a look at this one
    public abstract long                    getStartPc();

    public abstract List<Node>              iChildren();
    public abstract void                    buildListUp(List<Node> nodeList, Node start);
    public abstract void                    buildListDown(List<Node> nodeList);

    public Node() {
        m_id = s_numNodes++;
        m_isFolded = false;
        m_unresolved = false;
        m_recursion = false;
    }

    public Node(Node node) {
        m_id = s_numNodes++;
        m_isFolded = false;
        m_unresolved = node.m_unresolved;
        m_recursion = node.m_recursion;
    }

    public Node(CfgNode node) {
        m_id = s_numNodes++;
        m_isFolded = false;
        m_unresolved = node.getUnresolved();
        m_recursion = node.getRecursive();
    }

    void copyBase(Node node) {
        // we don't touch the id.
        m_parent     = node.m_parent;
        m_unresolved = node.m_unresolved;
        m_recursion  = node.m_recursion;
        m_isFolded   = node.m_isFolded;
    };

    public boolean isCondNode()             { return false; }
    public boolean isIssueNode()            { return false; }
    public boolean isFunctionNode()         { return false; }
    public boolean isLoopNode()             { return false; }
    public boolean isBlockNode()            { return false; }
    public boolean isSelfLoopNode()         { return false; }
    public boolean isNullNode()             { return false; }
    public boolean isInfiniteLoopNode()     { return false; }
    public boolean isInfiniteSelfLoopNode() { return false; }
    public boolean isSequenceNode()         { return false; }

    public String toString() {
        return getTypeName() + m_id;
    }

    public void setUnresolved(boolean unresolved) {
        m_unresolved = unresolved;
    }

    public boolean getUnresolved() {
        return m_unresolved;
    }

    public void setRecursion(boolean recursion) {
        m_recursion = recursion;
    }

    public boolean getRecursion() {
        return m_recursion;
    }

    public int getId() {
        return m_id;
    }

    public int compareTo(Node node) {
        return m_id - node.m_id;
    }

    public Node getParent(){
        return m_parent;
    }

    public void setParent(Node node) {
        m_parent = node;
    }

    public Node getEnclosingLoopNode() {
        Node node = this;
        while ((node != null) && !node.isLoopNode() && !node.isSelfLoopNode()) {
            node = node.getParent();
        }
        return node;
    }

    public Node getEnclosingCondNode() {
        Node node = m_parent;
        while ((node != null) && !node.isCondNode()) {
            node = node.getParent();
        }
        return node;
    }

    public Node getEnclosingOptionNode() {
        Node node = this;
        while ((node.getParent() != null) && !node.getParent().isCondNode()) {
            node = node.getParent();
        }
        return node;
    }

    public Node getEnclosingInfiniteLoopNode() {
        Node node = this;
        while ((node != null) && !node.isInfiniteLoopNode() && !node.isInfiniteSelfLoopNode()) {
            node = node.getParent();
        }
        return node;
    }

    public String getBackTrace(String backTrace) {
        if (m_parent != null) {
            return m_parent.getBackTrace(backTrace);
        }
        return backTrace;
    }

    public String getDescription() {
        return "";
    }

    public String getFunctionName() {
        return "";
    }

    public Node getOption(Case c, Node ignoreIterationsAboveCond) {
        assert(false);
        return null;
    }

    public Node getUnsetOption(Case c, Node ignoreIterationsAboveCond) {
        assert(false);
        return null;
    }

    public List<Node> getAllUnsetOptions() {
        assert(false);
        return null;
    }

    public void setFolded(boolean folded, boolean applyToChildren) {
        m_isFolded = folded;
        if (applyToChildren) {
            for (Node child : iChildren()) {
                child.setFolded(folded, applyToChildren);
            }
        }
    }

    public boolean isFolded() {
        return m_isFolded;
    }

    public String getReferenceStr() {
        StringBuffer refStr = new StringBuffer();
        getBackTrail(refStr);
        if (isFunctionNode()) {
            refStr.append(((FunctionNode)this).getFunctionName());
        } else {
            refStr.append(String.format("0x%x", getStartPc()));
        }
        return refStr.toString();
    }

    public void getBackTrail(StringBuffer refStr) {
        if (m_parent != null) {
            m_parent.getBackTrail(refStr);
        }
    }

    public boolean canReplaceChildren() {
        return false;
    }

    public void replaceChild(Node oldNode, Node newNode) {
        assert(false);
    }

    public void replaceChildren(List<Node> children) {
        assert(false);
    }

    private Set<Node> m_branchReturnNodes;
    public void addBranchReturnNode(IssueNode node) {
        if (m_branchReturnNodes == null){
            m_branchReturnNodes = new IteratableSet<Node>();
        }
        m_branchReturnNodes.add(node);

    }

    public Set<Node> getBranchReturnNodes(){
        if(m_branchReturnNodes == null){
            m_branchReturnNodes = new IteratableSet<Node>();
        }
        return m_branchReturnNodes;
    }

    public Node getSuccessor(Node child) {
        boolean found = false;
        for (Node node : iChildren()) {
            if (found) {
                return node;
            }
            if (node == child) {
                found = true;
            }
        }
        return (m_parent != null) ? m_parent.getSuccessor(this) : null;
    }

    public Node getSuccessor() {
        return m_parent.getSuccessor(this);
    }
}

