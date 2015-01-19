package com.xmos.xta.core.engine.analysis.structural;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.core.controlflow.nodes.CfgFunctionReferenceNode;
import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.controlflow.nodes.CfgNullNode;
import com.xmos.libs.core.memorymonitor.MemoryMonitor;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
import com.xmos.libs.core.utils.IteratableMap;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.libs.core.utils.NonIteratableMap;
import com.xmos.libs.core.utils.NonIteratableSet;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.IssueNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.SequenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.analysis.structural.patterns.ConditionalPatternMatcher;
import com.xmos.xta.core.engine.analysis.structural.patterns.ConditionalTransformer;
import com.xmos.xta.core.engine.analysis.structural.patterns.LoopPatternMatcher;
import com.xmos.xta.core.engine.analysis.structural.patterns.NullRemover;
import com.xmos.xta.core.engine.analysis.structural.patterns.PanicTransformer;
import com.xmos.xta.core.engine.analysis.structural.patterns.PatternMatcher;
import com.xmos.xta.core.engine.analysis.structural.patterns.SelfLoopPattern;
import com.xmos.xta.core.engine.analysis.structural.patterns.SequencePatternMatcher;

public class StructuralAnalysisEngine {

    private static boolean debug_structure = false;
    private static boolean sanity_debug    = false;

    private Engine m_engine;
    private List<PatternMatcher> m_patternMatchers;

    public StructuralAnalysisEngine(Engine engine) {
        m_engine = engine;

        MemoryMonitor memoryMonitor = engine.getMemoryMonitor();

        //never ever change the order of this
        m_patternMatchers = new ArrayList<PatternMatcher>();
        m_patternMatchers.add(new SelfLoopPattern(memoryMonitor));
        m_patternMatchers.add(new LoopPatternMatcher(memoryMonitor));
        m_patternMatchers.add(new ConditionalPatternMatcher(memoryMonitor));
        m_patternMatchers.add(new NullRemover(memoryMonitor));
        m_patternMatchers.add(new SequencePatternMatcher(memoryMonitor));    //uses dominators
        m_patternMatchers.add(new ConditionalTransformer(memoryMonitor));    //uses dominators
        m_patternMatchers.add(new PanicTransformer(memoryMonitor));            //uses dominators
    }

    public Node createStructure(Set<CfgNode> workingSet, CfgNode startNode, String name)
        throws AbortedException, MemoryMonitorException {

        assert(workingSet.contains(startNode));
        CfgNode newStartNode = basicBlock(workingSet, startNode, name);
        Node structuralNode = buildStructure(workingSet, newStartNode, "Converting CFG to structure");
        assert(workingSet.size() == 1);
        normaliseGraph(structuralNode);
        return structuralNode;
    }

      private CfgNode basicBlock(Set<CfgNode> workingSet, CfgNode originalStartNode, String string) {
        dumpTree("before_basicBlock_"+ string, workingSet, false);

        Set<CfgNode> toDo = new IteratableSet<CfgNode>();
        toDo.add(originalStartNode);

        CfgNode firstNode = null;
        Set<CfgNode> seenNodes = new NonIteratableSet<CfgNode>();
        Set<CfgNode> newWorkingSet = new IteratableSet<CfgNode>();

        Map<CfgNode, Set<CfgNode>> nexts = new IteratableMap<CfgNode, Set<CfgNode>>();
        Map<CfgNode, CfgNode> instToBlock = new NonIteratableMap<CfgNode, CfgNode>();

        while (!toDo.isEmpty()) {

            CfgNode current = toDo.iterator().next();
            toDo.remove(current);

            List<IssueNode> issueNodes = new LinkedList<IssueNode>();
            issueNodes.add(new IssueNode((CfgInstructionNode)current));

            seenNodes.add(current);

            CfgNode startOfBlock = current;

            boolean firstInBlock = true;
            while (nextIsInSameBlock(current, workingSet, seenNodes, firstInBlock)) {
                CfgNode next = getNextNodes(current, workingSet).iterator().next();
                issueNodes.add(new IssueNode((CfgInstructionNode)next));
                current = next;
                seenNodes.add(current);
                firstInBlock = false;
            }

            BlockNode strBasicBlock = new BlockNode(issueNodes);
            CfgNode basicBlock = new CfgContainerNode(strBasicBlock);

            instToBlock.put(startOfBlock, basicBlock);
            nexts.put(basicBlock, current.getNextNodes());
            if (newWorkingSet.isEmpty()) {
                firstNode = basicBlock;
            }
            newWorkingSet.add(basicBlock);

            for (CfgNode next : current.getNextNodes()) {
                if (next instanceof CfgFunctionReferenceNode) {
                    FunctionNode fnNode = new FunctionNode();
                    CfgContainerNode cnNode = new CfgContainerNode(fnNode);
                    newWorkingSet.add(cnNode);
                    seenNodes.add(cnNode);
                    instToBlock.put(next, cnNode);
                    nexts.put(cnNode, new IteratableSet<CfgNode>(next.getNextNodes()));
                    for (CfgNode nextNext : next.getNextNodes()) {
                        if (!seenNodes.contains(nextNext)) {
                            toDo.add(nextNext);
                        }
                    }

                } else if (next instanceof CfgContainerNode) {
                    next.getPrevNodes().clear();
                    newWorkingSet.add(next);
                    seenNodes.add(next);
                    instToBlock.put(next, next);
                    nexts.put(next, new IteratableSet<CfgNode>(next.getNextNodes()));
                    for (CfgNode nextNext : next.getNextNodes()){
                        if (!seenNodes.contains(nextNext)){
                            toDo.add(nextNext);
                        }
                    }
                    next.getNextNodes().clear();

                } else if (next instanceof CfgNullNode) {
                    next.getPrevNodes().clear();
                    newWorkingSet.add(next);
                    seenNodes.add(next);
                    instToBlock.put(next, next);
                    nexts.put(next, new IteratableSet<CfgNode>(next.getNextNodes()));
                    next.getNextNodes().clear();

                } else if (!seenNodes.contains(next)) {
                    assert(next instanceof CfgInstructionNode);
                    toDo.add(next);
                }
            }
        }

        for (CfgNode node : nexts.keySet()) {
            for (CfgNode next : nexts.get(node)) {
                CfgNode.connect(instToBlock.get(next), node);
            }
        }

        collateTerminals(firstNode, newWorkingSet);

        workingSet.clear();
        workingSet.addAll(newWorkingSet);
        dumpTree("after_basicBlock_"+ string, workingSet, false);
        return firstNode;
    }

      private void collateTerminals(CfgNode firstNode, Set<CfgNode> newWorkingSet) {

        Set<CfgNode> terminals = findEndPoints(firstNode);
        if (terminals.size() < 2)
            return;

        // Creates a new null node which all the terminals will connect to..
        CfgNullNode newNullNode = new CfgNullNode();

        for (CfgNode node : terminals){
            if (node instanceof CfgNullNode) {
                // This is already a null node, so we need to connect this
                // node predecessors to the new null node...
                for (CfgNode previousNode : node.getPrevNodes()) {
                    previousNode.removeNextNode(node);
                    CfgNode.connect(newNullNode, previousNode);
                }
                node.getPrevNodes().clear();
                newWorkingSet.remove(node);

            } else {
                // Connects the terminal to the new null node.
                CfgNode.connect(newNullNode, node);
            }
        }
        newWorkingSet.add(newNullNode);
      }

    private Node buildStructure(Collection<CfgNode> workingSet, CfgNode treeStart, String jobPrefix)
        throws AbortedException, MemoryMonitorException {

        if (workingSet.size() == 1 && workingSet.iterator().next().getNextNodesSize() == 0) {
            CfgNode node = workingSet.iterator().next();
            assert(node instanceof CfgContainerNode);
            return ((CfgContainerNode)node).getNode();
        }

        buildDominatorTree(workingSet, treeStart);

        boolean working = true;
        boolean dominatorTreeUpToDate = true;

        while (working) {

            String feedback = jobPrefix + " - nodes left: " + workingSet.size();
            if (m_engine.getVerbosity() >= Config.VERBOSITY_DISPLAY_MEMORY_USAGE) {
                feedback += String.format("\nMemory Usage [max/total/free]: %d/%d/%d",
                    Runtime.getRuntime().maxMemory(),
                    Runtime.getRuntime().totalMemory(),
                    Runtime.getRuntime().freeMemory());
            }
            m_engine.provideFeedback(feedback);

            List<CfgNode> postOrder = new LinkedList<CfgNode>();
            DFSPostOrder(postOrder, treeStart, new NonIteratableSet<CfgNode>());

            working = false;
            for (PatternMatcher pm : m_patternMatchers) {
                if (!dominatorTreeUpToDate && pm.usesDominatorTree()) {
                    buildDominatorTree(workingSet, treeStart);
                    dominatorTreeUpToDate = true;
                }
                if (pm instanceof ConditionalTransformer) {
                    Collections.reverse(postOrder);
                }
                for (CfgNode node : postOrder) {

                    m_engine.getAbort();
                    if (m_engine.getMemoryMonitor().outOfMemoryAbort()) {
                        throw new MemoryMonitorException();
                    }

                    if (pm.isMatch(node, workingSet, treeStart)) {
                        treeStart = pm.getTreeStart();
                        assert(workingSet.contains(treeStart));
                        assert(connectivitySanityCheck(workingSet));
                        dominatorTreeUpToDate = false;
                        working = true;
                        break;
                    }
                }
                if (working)
                    break;
            }
        }

        CfgNode node = workingSet.iterator().next();
        assert(node instanceof CfgContainerNode);
        return ((CfgContainerNode)node).getNode();
    }

    private boolean done;
    private void normaliseGraph(Node fn) {
        setParent(fn);
        done = false;
        while(!done){
            done =true;
            fn.traverse(new DefaultNodeTraverser() {
                public TraverseStatus foundSequenceNode(Node node, Object clientState) {

                    for(Node child : node.iChildren()){
                        if(child instanceof SequenceNode){
                            SequenceNode sn = (SequenceNode)child;
                            int insertPathAt =node.iChildren().indexOf(child);
                            node.iChildren().remove(insertPathAt);
                            for(Node pathChild : sn.getPath()){
                                node.iChildren().add(insertPathAt++, pathChild);
                            }
                            done = false;
                            return TraverseStatus.NO_CHILDREN;
                        }
                    }
                    return TraverseStatus.CONTINUE;
                }

                public TraverseStatus foundFunctionNode(Node node, Object clientState) {
                    for(Node child : node.iChildren()){
                        if(child instanceof SequenceNode){
                            SequenceNode sn = (SequenceNode)child;
                            int insertPathAt = node.iChildren().indexOf(child);
                            node.iChildren().remove(insertPathAt);
                            for(Node pathChild : sn.getPath()){
                                node.iChildren().add(insertPathAt++, pathChild);
                            }
                            done = false;
                            return TraverseStatus.NO_CHILDREN;
                        }
                    }
                    return TraverseStatus.CONTINUE;
                }
            },
            null    // No clientState
            );
            setParent(fn);
        }
        setParent(fn);
    }

    private void buildDominatorTree(Collection<CfgNode> reachableNodes, CfgNode treeStart) {
        //try to remove tree sets from this
        for (CfgNode node : reachableNodes) {
            node.clearDominators();
            if (treeStart == node) {
                node.setDominatorSetComplete(false);
                node.addDominator(node);
            } else {
                node.setDominatorSetComplete(true);
            }
        }

        Set<CfgNode> intersection = new IteratableSet<CfgNode>();
        boolean stillChanging = true;

        while (stillChanging) {
            stillChanging = false;
            for (CfgNode node : reachableNodes) {
                boolean intersectionIsCompleteSet = true;

                if (treeStart == node)
                    continue;

                intersection.clear();

                for (CfgNode pred : node.getPrevNodes()) {
                    if (!pred.isDominatorSetComplete()) {
                        if (intersectionIsCompleteSet) {
                            intersection.addAll(pred.getDominators());
                            intersectionIsCompleteSet = false;
                        } else {
                            intersection.retainAll(pred.getDominators());
                        }
                    }
                }

                intersection.add(node);

                if (!intersection.equals(node.getDominators())) {
                    node.clearDominators();
                    node.addAllDominators(intersection);
                    node.setDominatorSetComplete(intersectionIsCompleteSet);
                    stillChanging = true;
                }
            }
        }
    }

    private void DFSPostOrder(List<CfgNode> nodes, CfgNode entry, Set<CfgNode> visited) {
        nodes.add(entry);

        List<CfgNode> next = new LinkedList<CfgNode>(entry.getNextNodes());
        Collections.sort(next);
        for (CfgNode x : next) {
            if (!visited.contains(x)) {
                visited.add(x);
                DFSPostOrder(nodes, x, visited);
            }
        }
    }

    private void setParent(Node node) {
        for (Node child : node.iChildren()){
            child.setParent(node);
            setParent(child);
        }
    }

    private Set<CfgNode> findEndPoints(CfgNode inst) {
        Set<CfgNode> terminals = new IteratableSet<CfgNode>();
        Set<CfgNode> seen = new NonIteratableSet<CfgNode>();

        Set<CfgNode> following = new IteratableSet<CfgNode>();
        following.add(inst);

        while (!following.isEmpty()) {
            CfgNode current = following.iterator().next();
            seen.add(current);
            if (current.getNextNodesSize() == 0) {
                terminals.add(current);
            } else {
                for (CfgNode next : current.getNextNodes()) {
                    if (!seen.contains(next)) {
                        following.add(next);
                    }
                }
            }
            following.remove(current);
        }
        return terminals;
    }

    private boolean nextIsInSameBlock(CfgNode current, Set<CfgNode> workingSet,
            Set<CfgNode> seenNodes, boolean firstInblock) {

        if (getNextNodes(current, workingSet).size() != 1){
            return false;
        }
        CfgNode next = getNextNodes(current, workingSet).iterator().next();

        if (seenNodes.contains(next)) {
            return false;
        }
        if (next instanceof CfgNullNode) {
            return false;
        }
        if (!(next instanceof CfgInstructionNode)) {
            return false;
        }
        if (getPrevNodes(next, workingSet).size() > 1){
            return false;
        }
        if (getPrevNodes(current, workingSet).size() > 1 && !firstInblock){
            return false;
        }

        if (current instanceof CfgInstructionNode) {
            if (((CfgInstructionNode)current).getInstruction().getReturn()) {
                return false;
            }
            if (((CfgInstructionNode)current).getInstruction().isLink()) {
                return false;
            }
        }
        return true;
    }

    private Set<CfgNode> getNextNodes(CfgNode node, Set<CfgNode> workingSet){
        Set<CfgNode> next = new IteratableSet<CfgNode>(node.getNextNodes());
        next.retainAll(workingSet);
        return next;
    }

    private Set<CfgNode> getPrevNodes(CfgNode node, Set<CfgNode> workingSet){
        Set<CfgNode> prev = new IteratableSet<CfgNode>(node.getPrevNodes());
        prev.retainAll(workingSet);
        return prev;
    }

    private boolean connectivitySanityCheck(Collection<CfgNode> workingSet) {
        if (sanity_debug) {
            int total = 0;
            for (CfgNode node : workingSet) {
                total += node.getPrevNodes().size() - node.getNextNodes().size();
            }
            return (total == 0);
        }
        return true;
    }

    private void dumpTree(String text, Collection<CfgNode> workingSet, boolean override) {
        if (debug_structure || override) {
            StringBuffer sb = new StringBuffer();
            if (workingSet.size() > 1) {
                sb.append("digraph "+ text + "{\n");
                for (CfgNode node : workingSet) {
                    sb.append("node" + node.getId() + " [label=\"" + node + "\"];\n");
                    for (CfgNode next : node.getNextNodes()) {
                        sb.append("node" + node.getId() + " -> node" + next.getId()+ ";\n");
                    }
                }
                sb.append("}\n");

            } else {
                sb.append("# graph has only " + workingSet.size() + " nodes - " + text + "\n");
            }
            System.out.println( sb.toString());
        }
    }
}
