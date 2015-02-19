package com.xmos.xta.core.engine.analysis.routes;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.utils.BackTrail;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.libs.core.utils.Pair;

public class BackTrailsList {
    private List< Pair<BackTrails,Set<CfgNode>> > m_backTrailsList;
    private boolean m_addBranchToNodes;

    public BackTrailsList(boolean addBranchToNodes) {
        m_backTrailsList = new LinkedList<Pair<BackTrails,Set<CfgNode>>>();
        m_addBranchToNodes = addBranchToNodes;
    }

    public void add(List<BackTrails> backTrailsList) {
        assert(!m_addBranchToNodes);
        for(BackTrails backTrails: backTrailsList)
            m_backTrailsList.add( new Pair<BackTrails,Set<CfgNode>>(backTrails,null));
    }

    public void add(BackTrails backTrails) {
        assert(!m_addBranchToNodes);
        m_backTrailsList.add( new Pair<BackTrails,Set<CfgNode>>(backTrails,null));
    }

    public void add(BackTrails backTrails, Set<CfgNode> branchNodes) {
        assert(m_addBranchToNodes);
        m_backTrailsList.add( new Pair<BackTrails,Set<CfgNode>>(backTrails, branchNodes));
    }

    public List<BackTrails> getList() {
        List<BackTrails> backTrailsList = new LinkedList<BackTrails>();
        for (Pair<BackTrails,Set<CfgNode>> pair : m_backTrailsList)
            backTrailsList.add(pair.first);
        return backTrailsList;
    }

    public boolean contains(BackTrail backTrail) {
        for (Pair<BackTrails,Set<CfgNode>> pair : m_backTrailsList)
            if (pair.first.contains(backTrail))
                return true;
        return false;
    }

    public boolean contains(CfgNode node, BackTrail backTrail) {
        if (node instanceof CfgInstructionNode)
            for (Pair<BackTrails,Set<CfgNode>> pair : m_backTrailsList)
                if (pair.first.getInstructionNode().equals(node))
                    for (BackTrail backT : pair.first)
                        if (backT.equals(backTrail))
                            return true;
        return false;
    }

    public boolean contains(long instNodePc) {
        for (Pair<BackTrails,Set<CfgNode>> pair : m_backTrailsList)
            if (pair.first.getInstructionNode().getPc() == instNodePc)
                return true;
        return false;
    }

    public Set<CfgNode> getBranchNodesNotIn(BackTrail backTrail) {
        Set<CfgNode> branchNodes = new IteratableSet<CfgNode>();
        for (Pair<BackTrails,Set<CfgNode>> pair : m_backTrailsList)
            if (!pair.first.contains(backTrail))
                branchNodes.addAll(pair.second);
        return branchNodes;
    }
}
