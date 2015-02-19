package com.xmos.xta.core.engine.analysis.routes;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.core.controlflow.FunctionControlFlow;
import com.xmos.libs.core.controlflow.nodes.CfgFunctionReferenceNode;
import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.controlflow.nodes.CfgNullNode;
import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.libs.core.instructiondecoder.InstructionMnemonics;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
import com.xmos.libs.core.utils.BackTrail;
import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.utils.IteratableMap;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.libs.core.utils.NonIteratableSet;
import com.xmos.libs.core.utils.Pair;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.CommandSource.Type;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer.RouteMode;
import com.xmos.xta.core.engine.analysis.structural.CfgContainerNode;
import com.xmos.xta.core.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.core.engine.analysis.structural.nodes.ConditionalNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.IssueNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.Source;
import com.xmos.xta.core.engine.defines.ExclusionInfo;
import com.xmos.xta.core.engine.defines.FunctionTimeInfo;
import com.xmos.xta.core.engine.defines.InstructionTimeInfo;
import com.xmos.xta.core.engine.defines.LoopInfo;
import com.xmos.xta.core.engine.defines.LoopPathInfo;
import com.xmos.xta.core.engine.defines.LoopScopeInfo;
import com.xmos.xta.core.engine.defines.PathTimeInfo;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.sections.SrcLevelLoopInfo;

public class RouteConstructor {
    private static boolean debug_route = false;
    private static final int SR_IEBLE_SHIFT = 1;
    private static final int SR_IEBLE_SIZE = 1;

    // We could pass these as arguments, but we pass them via 'this' instead.
    private final Engine m_engine;
    private final RouteCoreContainer m_routeCoreContainer;    // We will be adding routes to m_routeCoreContainer.m_routes for this core.
    private final CommandSource m_src;    // Could be accessed via m_routeCoreContainer.
    private final CoreId m_core;;         // Could be accessed via m_routeCoreContainer.

    private BackTrailsList m_branchBackTrailsList;
    private BackTrailsList m_exclusionBackTrailsList;

    public static List<BackTrails> getPathBackTrails(Engine engine, CoreId core, Reference reference)
            throws AbortedException, MemoryMonitorException {
        return ControlFlowReferenceMatcher.getPathBackTrails(engine.getControlFlow(core), engine.getCachedFunctionMap(core), reference);
    }

    static void setParent(Node node) {
        for (Node child : node.iChildren()){
            child.setParent(node);
            setParent(child);
        }
    }

    public static void analyze(RouteCoreContainer routeCoreContainer, Engine engine)
            throws Exception {
        // The routeCoreContainer holds the information we will need.
        // We will be adding the routeCoreContainer.List<Route>
        RouteConstructor routeConstructor = new RouteConstructor(routeCoreContainer, engine);
        routeConstructor.analyze();
    }

    private RouteConstructor(RouteCoreContainer routeCoreContainer, Engine engine) throws Exception {
        m_engine = engine;
        m_routeCoreContainer = routeCoreContainer;
        m_src = routeCoreContainer.parentRouteContainer().getCommandSrc();
        m_core = routeCoreContainer.getCoreId();
        m_branchBackTrailsList = new BackTrailsList(true);      // We will be adding branchNodes.
        m_exclusionBackTrailsList = new BackTrailsList(false);  // We will not be adding branchNodes.
    }

    private void analyze() throws Exception {
        m_engine.provideFeedback("Starting analysis...");
        buildbranchBackTrailsList();
        buildexclusionBackTrailsList();

        switch (m_routeCoreContainer.parentRouteContainer().getMode()) {
        case FUNCTION:
            analyzeFunction();
            break;

        case LOOP:
            analyzeLoop();
            break;

        case ENDPOINTS:
            analyzeEndpoints();
            break;

        default:
            assert(false);
        }

        InstructionIssueAnayser();
        FnopAdder();
        setUnknowns();
        setSrcLoopCounts();
        checkSupportedInstructions();
        applyDefines();

        m_engine.provideFeedback("Analysis complete");
    }

    private void buildbranchBackTrailsList() throws AbortedException, MemoryMonitorException {
        for (Reference ref : m_engine.getUserBranches().keySet()) {
            for (BackTrails backTrails : getPathBackTrails(m_engine, m_core, ref)) {
                Set<CfgNode> branchNodes = new IteratableSet<CfgNode>();
                for (Reference nextRef : m_engine.getUserBranches().get(ref)) {
                    for (BackTrails nextBackTrails : getPathBackTrails(m_engine, m_core, nextRef)) {
                        if (backTrails.getInstructionNode().getInstruction().isLink()) {
                            for (CfgNode nextNode : backTrails.getInstructionNode().getNextNodes()) {
                                if (nextNode instanceof CfgFunctionReferenceNode) {
                                    CfgFunctionReferenceNode frn = (CfgFunctionReferenceNode)nextNode;
                                    if (frn.getFunctionStartInstruction() == nextBackTrails.getInstructionNode()) {
                                        branchNodes.add(frn);
                                    }
                                }
                            }
                        } else {
                            branchNodes.add(nextBackTrails.getInstructionNode());
                        }
                    }
                }
                m_branchBackTrailsList.add(backTrails, branchNodes);
            }
        }
    }

    private void buildexclusionBackTrailsList() throws AbortedException, MemoryMonitorException {
        for (ExclusionInfo info : m_engine.getExclusions()) {
            Reference ref = info.getReference();
            if (ref.getCoreId().equals(m_core))
                m_exclusionBackTrailsList.add(getPathBackTrails(m_engine, m_core, ref));
        }
    }

    private void analyzeFunction() throws AbortedException, MemoryMonitorException {
        List<BackTrails> startBackTrailsList = getPathBackTrails(m_engine, m_core, m_routeCoreContainer.getFromReference());
        for (BackTrails backTrails : startBackTrailsList)
            for (BackTrail backTrail : backTrails) {
                Pair<FunctionNode, Boolean> fnReturn = makeFunction(true, backTrail);
                FunctionNode fn = fnReturn.first;
                updateRoutes(fn);
            }
    }

    private void analyzeLoop() throws AbortedException, MemoryMonitorException {
        Reference ref = m_routeCoreContainer.getFromReference();
        List<BackTrails> loopStartBackTrailsList = RouteLoopConversion.updateLoopBackTrails(m_engine, m_core, ref, m_exclusionBackTrailsList);
        for (BackTrails backTrails : loopStartBackTrailsList) {
            for (BackTrail backTrail : backTrails) {
                Node node = makeEndpoints(backTrail, backTrails.getInstructionNode(), loopStartBackTrailsList);
                updateRoutes(node);
            }
        }
    }

    private void analyzeEndpoints() throws AbortedException, MemoryMonitorException {
        List<BackTrails> startBackTrailsList = getPathBackTrails(m_engine, m_core, m_routeCoreContainer.getFromReference());
        List<BackTrails> endBackTrailsList = new LinkedList<BackTrails>();
        for (Reference end : m_routeCoreContainer.getToReferences())
            endBackTrailsList.addAll(getPathBackTrails(m_engine, m_core, end));
        for (BackTrails backTrails : startBackTrailsList) {
            for (BackTrail backTrail : backTrails) {
                Node node = makeEndpoints(backTrail, backTrails.getInstructionNode(), endBackTrailsList);
                updateRoutes(node);
            }
        }
    }

    private void updateRoutes(Node node) {
        if (node == null)
            return;
        setParent(node);
        for (Route route : m_routeCoreContainer)
            if (route.sameDescription(node))
                return;  // Already contains route.
        Route newRoute = new Route(m_engine.getXeLoader().getArchitecture(), node, m_routeCoreContainer);
        m_routeCoreContainer.add(newRoute);
    }






    private Node makeEndpoints(BackTrail currentBackTrail, CfgInstructionNode startInst, List<BackTrails> endBackTrailsList) throws AbortedException, MemoryMonitorException {
        ReachableGraph reachableGraph = new ReachableGraph(false, endBackTrailsList);
        reachableGraph.findReachableGraph(true, null, currentBackTrail, startInst);
        Node node = reachableGraph.convertGraphToStructure(currentBackTrail, startInst);
        return node;
    }

    private Pair<FunctionNode, Boolean> makeFunction(boolean firstInstruction, BackTrail currentBackTrail) throws AbortedException, MemoryMonitorException {
        CfgFunctionReferenceNode fnRef = currentBackTrail.peek();
        if (!fnRef.isKnownFunction())
            return new Pair<FunctionNode, Boolean>(new FunctionNode(), false);
        CfgInstructionNode startInst = fnRef.getFunctionStartInstruction();
        List<BackTrails> endBackTrailsList = getFunctionEndBackTrails(currentBackTrail);
        ReachableGraph reachableGraph = new ReachableGraph(true, endBackTrailsList);
        reachableGraph.findReachableGraph(firstInstruction, null, currentBackTrail, startInst);
        Node node = reachableGraph.convertGraphToStructure(currentBackTrail, startInst);
        if (node == null)
            return new Pair<FunctionNode, Boolean>(null, false);
        FunctionNode fn = new FunctionNode(node, fnRef.getFunctionName(), fnRef.getFunctionCallPc());
        return new Pair<FunctionNode, Boolean>(fn, true);
    }

    private List<BackTrails> getFunctionEndBackTrails(BackTrail backTrail) {
        List<BackTrails> endBackTrailsList = new LinkedList<BackTrails>();
        Set<CfgNode> following = new IteratableSet<CfgNode>();
        following.add(backTrail.peek().getFunctionStartInstruction());
        Set<CfgNode> seen = new NonIteratableSet<CfgNode>();
        while (!following.isEmpty()) {
            CfgNode current = following.iterator().next();
            following.remove(current);
            seen.add(current);
            if (current.getNextNodes().size() == 0) {
                assert(current instanceof CfgInstructionNode);
                endBackTrailsList.add(new BackTrails((CfgInstructionNode)current, backTrail));
            } else {
                for(CfgNode next : current.getNextNodes())
                    if(!seen.contains(next))
                        following.add(next);
            }
        }
        return endBackTrailsList;
    }




    class ReachableGraph {
        private boolean m_functionMode;
        private List<BackTrails> m_endBackTrailsList;
        private Set<CfgNode> m_workingSet;
        private Set<CfgNode> m_currentExclusionsFound;
        private Map<NodeDescriptor, CfgNode> m_nodeToWorkingSet;  // RL-TODO simplify how this is used.

        ReachableGraph(boolean functionMode, List<BackTrails> endBackTrailsList) {
            m_functionMode = functionMode;
            m_endBackTrailsList = endBackTrailsList;
            m_workingSet = new IteratableSet<CfgNode>();
            m_currentExclusionsFound = new IteratableSet<CfgNode>();
            m_nodeToWorkingSet = new IteratableMap<NodeDescriptor, CfgNode>();
        }

        void findReachableGraph(boolean firstInstruction, CfgNode previous, BackTrail currentBackTrail, CfgNode node) throws AbortedException, MemoryMonitorException {
            while(true) {
                CfgNode currentNode = null;
                if (node instanceof CfgFunctionReferenceNode) {
                    CfgFunctionReferenceNode frn = ((CfgFunctionReferenceNode)node);
                    CfgInstructionNode inst = frn.getFunctionStartInstruction();

                    if (currentBackTrail.recursivelyContains(inst)) {
                        previous.setRecursive(true);
                        return;
                    }

                    BackTrail newBackTrail = new BackTrail(currentBackTrail);
                    newBackTrail.push(frn);

                    for (BackTrails backTrails : m_endBackTrailsList)
                        if (backTrails.contains(frn)) {
                            findReachableGraph(false, previous, newBackTrail, inst);
                            return;
                        }

                    if (!frn.isKnownFunction()) {
                        currentNode = new CfgContainerNode(new FunctionNode());
                        connect(previous, currentNode);
                        return;
                    }
                    Pair<FunctionNode, Boolean> fnReturn = makeFunction(false, newBackTrail);
                    FunctionNode fn = fnReturn.first;
                    boolean continueAnalysis = fnReturn.second;
                    if (!continueAnalysis) {
                        currentNode = new CfgContainerNode(fn);
                        connect(previous, currentNode);
                        if (fn == null) // The function never met an endpoint and didn't return.
                            m_currentExclusionsFound.add(currentNode);
                        return;
                    }
                    assert(!m_exclusionBackTrailsList.contains(frn, currentBackTrail));
                    assert(!endBackTrailsListContainNode(currentBackTrail, frn));
                    currentNode = new CfgContainerNode(fn);
                    connect(previous, currentNode);
                } else {
                    if (m_exclusionBackTrailsList.contains(node, currentBackTrail) && !firstInstruction) {
                        currentNode = copyNode(node, currentBackTrail);
                        connect(previous, currentNode);
                        m_currentExclusionsFound.add(currentNode);
                        return;
                    }
                    firstInstruction = false;
                    if (node.getUnresolved()) {
                        currentNode = copyNode(node, currentBackTrail);
                        connect(previous, currentNode);
                        return;
                    }
                    // This adds to the loop forming end nodes.
                    if (endBackTrailsListContainNode(currentBackTrail, node)) {
                        if (m_functionMode) {
                            currentNode = copyNode(node, currentBackTrail);
                            connect(previous, currentNode);
                            return;
                        } else if(!m_workingSet.isEmpty()) {
                            connect(previous, getNullNode());
                            return;
                        }
                    }
                    currentNode = copyNode(node, currentBackTrail);
                    connect(previous, currentNode);
                }
                // nextNodes is the set of nodes that follow this currentNode, it should include any user added next nodes.
                Set<CfgNode> nextNodes = getValidBranches(currentBackTrail, node);
                switch (nextNodes.size()) {
                case 0:
                    if (currentNode instanceof CfgInstructionNode &&
                            m_branchBackTrailsList.contains(((CfgInstructionNode)currentNode).getPc())) {
                        // Here we have no next nodes (i.e. it is unresolved), but isn't marked as unresolved
                        // because there is at least one way we can get to this node where there is a next node.
                        currentNode.setUnresolved(true);
                    } else  if (currentBackTrail.size() == 1) {
                        currentNode.setUnresolved(true);
                    } else {
                        BackTrail newBackTrail = new BackTrail(currentBackTrail);
                        CfgFunctionReferenceNode callingFnRef = newBackTrail.pop();
                        for (CfgNode followingFunction : callingFnRef.getNextNodes())
                            findReachableGraph(false, currentNode, newBackTrail, followingFunction);
                    }
                    return;
                case 1:
                    CfgNode next = nextNodes.iterator().next();
                    CfgNode nextWorkingSetnode = m_nodeToWorkingSet.get(new NodeDescriptor(next, currentBackTrail));
                    if (nextWorkingSetnode != null) {
                        handleNodeAlreadyInWorkingSet(currentBackTrail, currentNode, next, nextWorkingSetnode);
                        // This is here to allow you to exclude the start instruction.
                        if (m_exclusionBackTrailsList.contains(next, currentBackTrail))
                            m_currentExclusionsFound.add(currentNode);
                        return;
                    }
                    // else
                    //    findReachableGraph(false, currentNode, currentBackTrail, nextNode);
                    previous = currentNode;
                    node = next;
                    break; // And we will start from the top again!
                default:
                    // Handle branch
                    for (CfgNode nextNode : nextNodes) {
                        CfgNode nextWorkingSetNode = m_nodeToWorkingSet.get(new NodeDescriptor(nextNode, currentBackTrail));
                        if (nextWorkingSetNode != null)
                            handleNodeAlreadyInWorkingSet(currentBackTrail, currentNode, nextNode, nextWorkingSetNode);
                        else
                            findReachableGraph(false, currentNode, currentBackTrail, nextNode);
                    }
                    return;
                }
            } // while(true)
        }

        Node convertGraphToStructure(BackTrail currentBackTrail, CfgInstructionNode startInst) throws AbortedException, MemoryMonitorException {
            if (removeExclusionBranches().isEmpty())
                return null;
            NodeDescriptor startNodeDescriptor = new NodeDescriptor(startInst, currentBackTrail);
            CfgNode originalStartNode = null;
            for (NodeDescriptor nd : m_nodeToWorkingSet.keySet()) {
                if (startNodeDescriptor.equals(nd) && m_workingSet.contains(m_nodeToWorkingSet.get(nd))) {
                    originalStartNode = m_nodeToWorkingSet.get(nd);
                    break;
                }
            }
            assert(originalStartNode != null && originalStartNode instanceof CfgInstructionNode);
            dumpGraph();
            return StructuralAnalysisEngine.createStructure(m_engine, m_workingSet, (CfgInstructionNode)originalStartNode, "route");
        }

        private void handleNodeAlreadyInWorkingSet(BackTrail currentBackTrail, CfgNode currentNode, CfgNode nextNode, CfgNode nextNodeFromWorkingSet) {
            if (m_functionMode)
                currentNode.connectNext(nextNodeFromWorkingSet);   // BUT what if we use connect() and add it again?
            else if (endBackTrailsListContainNode(currentBackTrail, nextNode))
                connect(currentNode, getNullNode());
            else if ( !m_exclusionBackTrailsList.contains(nextNode, currentBackTrail))
                // We've formed a loop, so connect the current to the target thats already in the working set..
                currentNode.connectNext(nextNodeFromWorkingSet);   // BUT what if we use connect() and add it again?
        }

        private CfgNode getNullNode() {
            //search the working set for a null node
            for (CfgNode node : m_workingSet)
                if (node instanceof CfgNullNode)
                    return node;
            return new CfgNullNode();
        }

        private Set<CfgNode> getValidBranches(BackTrail currentBackTrail, CfgNode node) {
            Set<CfgNode> validNextNodes = new IteratableSet<CfgNode>();
            validNextNodes.addAll(node.getNextNodes());
            validNextNodes.removeAll(m_branchBackTrailsList.getBranchNodesNotIn(currentBackTrail));
            return validNextNodes;
        }

        private Set<CfgNode> removeExclusionBranches() {
            while (!m_currentExclusionsFound.isEmpty()) {
                CfgNode current = m_currentExclusionsFound.iterator().next();
                m_currentExclusionsFound.remove(current);
                m_workingSet.remove(current);
                Set<CfgNode> prevNodes = new IteratableSet<CfgNode>(current.getPrevNodes());
                for (CfgNode prev : prevNodes) {
                    prev.disconnectNext(current);
                    if (prev.getNextNodes().isEmpty())
                        m_currentExclusionsFound.add(prev);
                }
            }
            return m_workingSet;
        }

        private CfgNode copyNode(CfgNode node, BackTrail currentBackTrail) throws AbortedException {
            assert(node instanceof CfgInstructionNode || node instanceof CfgFunctionReferenceNode);
            NodeDescriptor nd = new NodeDescriptor(node, currentBackTrail);
            if (m_nodeToWorkingSet.containsKey(nd))
                return m_nodeToWorkingSet.get(nd);
            CfgNode copy = node.copy();    // RL-TODO Why do we want a copy? Do we want to keep the edges?
            m_nodeToWorkingSet.put(nd, copy);
            return copy;
        }

        private boolean endBackTrailsListContainNode(BackTrail currentBackTrail, CfgNode node) {
            return backTrailsContainNode(m_endBackTrailsList, currentBackTrail, node);
        }
        private boolean backTrailsContainNode(List<BackTrails> backTrailsList, BackTrail currentBackTrail, CfgNode currentNode) {
            if (currentNode instanceof CfgInstructionNode)
                for (BackTrails backTrails : backTrailsList)
                    if (backTrails.getInstructionNode().equals(currentNode))
                        for (BackTrail backTrail : backTrails)
                            if (backTrail.equals(currentBackTrail))
                                return true;
            return false;
        }


       private void connect(CfgNode parent, CfgNode child) {
            if (parent!= null)
                parent.connectNext(child);
            m_workingSet.add(child);
        }

        private void dumpGraph() {
            if (!debug_route) return;
            System.out.print("digraph g {");
            for (CfgNode node : m_workingSet) {
                if (node instanceof CfgInstructionNode){
                    System.out.print("node" +node.getId() + " [label=\"" + ((CfgInstructionNode)node).getInstruction() + "\"];");
                } else {
                    System.out.print("node" +node.getId() + " [label=\"" + node + "\"];");
                }
                for(CfgNode next : node.getNextNodes()){
                    System.out.print("node" +node.getId() + " -> node" + next.getId() + ";");
                    if(!m_workingSet.contains(next)){
                        System.out.print("node" +next.getId() + " [label=\"" + next + "\", style=filled, color=blue];");
                    }
                }
                for(CfgNode prev : node.getPrevNodes()){
                    System.out.print("node" +prev.getId() + " -> node" + node.getId() + "[style=dotted];");
                    if(!m_workingSet.contains(prev)){
                        System.out.print("node" +prev.getId() + " [label=\"" + prev + "\", style=filled, color=red];");
                    }
                }
            }
            System.out.println("}");
        }
    }





    private void InstructionIssueAnayser() throws AbortedException {
        m_engine.provideFeedback("We only handle single issue routes - skip InstructionIssueAnayser.");
        m_engine.getAbort();
    }

    private void FnopAdder() throws AbortedException {
        m_engine.provideFeedback("Adding fnops to routes...");
        for (final Route route : m_routeCoreContainer) {
            m_engine.getAbort();
            Node node = route.getNode();
            long startPc = node.getStartPc();

            List<CfgInstructionNode> cfgs = new LinkedList<CfgInstructionNode>();
            for (FunctionControlFlow fcf : m_engine.getControlFlow(m_core).getFunctionContaining(startPc))
                cfgs.add(fcf.getInstructionNode(startPc));

            InstructionBuffer instructionBuffer = new InstructionBuffer(m_engine.getXeLoader().getArchitecture(), startPc, cfgs);
            node.traverse( new UntypedNodeTraverser() {
                public TraverseStatus foundNode(Node node, Object clientState) {
                    InstructionBuffer ibuff = (InstructionBuffer)clientState;
                    // First do any general node handling.
                    ibuff.startNode(node);
                    // Now handle specific nodes types.
                    if (node.isCondNode())
                        ibuff.createForks(node.iChildren());
                    else if (node.isLoopNode() || node.isInfiniteLoopNode())
                        ibuff.createLoops(node.iChildren());
                    else if (node.isSelfLoopNode() || node.isInfiniteSelfLoopNode())
                        ibuff.createSelfLoops(node.iChildren());
                    else if (node.isIssueNode())
                        ibuff.handleIssueNode(node);
                    return TraverseStatus.CONTINUE;
                }
                public TraverseStatus foundNodeExit(Node node, Object clientState) {
                    InstructionBuffer ibuff = (InstructionBuffer)clientState;
                    // Handle specific nodes types.
                    if (node.isCondNode())
                        ibuff.joinForks(((ConditionalNode)node).iChildren());
                    else if (node.isLoopNode() || node.isInfiniteLoopNode())
                        ibuff.joinLoops(node.iChildren());
                    else if (node.isSelfLoopNode() || node.isInfiniteSelfLoopNode())
                        ibuff.joinSelfLoops(node.iChildren());
                    // And finalize any general node handling.
                    ibuff.endNode(node);
                    return TraverseStatus.CONTINUE;
                }
            },
            instructionBuffer    // clientState
            );
            refreshDisplayedUnknowns(node);
        }
    }

    private void setUnknowns() throws AbortedException {
        m_engine.provideFeedback("Setting unknowns on routes...");

        for (final Route route : m_routeCoreContainer) {
            m_engine.getAbort();
            Node node = route.getNode();
            node.traverse( new DefaultNodeTraverser() {
                // Sets all the pausing instructions to unknown.
                public TraverseStatus foundIssueNode(Node node, Object clientState) {
                    if (((IssueNode)node).canPause()) {
                        NodeAttributes nodeAttributes = node.getAttributes(true);
                        nodeAttributes.addUnknown(route, UnknownFactory.Type.INSTRUCTION_TIME);
                    }
                    return TraverseStatus.CONTINUE;
                }
                // Sets all the loop iterations to unknown.
                public TraverseStatus foundLoopNode(Node node, Object clientState) {
                    NodeAttributes nodeAttributes = node.getAttributes(true);
                    nodeAttributes.addUnknown(route, UnknownFactory.Type.LOOP_ITERATIONS);
                    return TraverseStatus.CONTINUE;
                }
                // Sets all the self loop iterations to unknown.
                public TraverseStatus foundSelfLoopNode(Node node, Object clientState) {
                    NodeAttributes nodeAttributes = node.getAttributes(true);
                    nodeAttributes.addUnknown(route, UnknownFactory.Type.LOOP_ITERATIONS);
                    return TraverseStatus.CONTINUE;
                }
            },
            null    // No clientState
            );
            refreshDisplayedUnknowns(node);
        }
    }

    private void setSrcLoopCounts() throws AbortedException {
        // Sets the loop iteration counts specified at compile time.
        for (SrcLevelLoopInfo srcLevelLoopInfo : m_engine.getSrcLevelLoops(m_core)) {
            m_engine.provideFeedback(String.format("Setting loop count at pc: 0x%x", srcLevelLoopInfo.getPc()));
            m_engine.getAbort();

            Reference reference = ReferenceFactory.createReference(
                    String.format("0x%x", srcLevelLoopInfo.getPc()),
                    ReferenceClass.INSTRUCTION, m_core);

            CommandSource src = new CommandSource(Type.COMPILER);
            for (Route route : m_routeCoreContainer) {
                try {
                    route.getRouteUnknowns().setLoopIterations(src, m_engine,
                            reference, srcLevelLoopInfo.getNumIterations(), true, Source.BINARY);
                } catch (Exception e) {}
            }
        }
    }

    private void checkSupportedInstructions() throws AbortedException {
        m_engine.provideFeedback("Checking for unsupported instructions...");
        final InstructionMnemonics mnenonics = new InstructionMnemonics(m_engine.getXeLoader().getArchitecture());
        final Engine engine = m_engine;
        final CommandSource src = m_src;
        for (Route route : m_routeCoreContainer) {
            engine.getAbort();
            if (route.getRouteTiming().getWorstCaseTimeStatus().isComplete()) {

                Node node = route.getNode();
                node.traverse(new DefaultNodeTraverser() {
                    public TraverseStatus foundIssueNode(Node node, Object clientState) {
                        for (Instruction instruction : ((IssueNode)node).getInstructions()) {

                            // Checks for threading instructions...
                            if (mnenonics.isSsync(instruction))
                                engine.notify(src, Notifier.NOTIFY_WARNING, String.format(
                                        "ssync (multiple threads) not supported. Pc: 0x%08x", instruction.getPc()));

                            if (mnenonics.isMsync(instruction))
                                engine.notify(src, Notifier.NOTIFY_WARNING, String.format(
                                        "msync (multiple threads) not supported. Pc: 0x%08x", instruction.getPc()));

                            if (mnenonics.isMjoin(instruction)) {
                                engine.notify(src, Notifier.NOTIFY_WARNING, String.format(
                                        "mjoin (multiple threads) not supported. Pc: 0x%08x", instruction.getPc()));
                            }

                            // Checks for interrupt enable..
                            if (mnenonics.isSetSr(instruction)) {

                                long op1Field = instruction.getOperandField("op1");
                                int shift = SR_IEBLE_SHIFT;
                                int mask = (1 << SR_IEBLE_SIZE) - 1;
                                if (((op1Field >> shift) & mask) == 1)
                                    engine.notify(src, Notifier.NOTIFY_WARNING, String.format(
                                            "interrupt enable detected. Pc: 0x%08x", instruction.getPc()));
                            }
                        }
                        return TraverseStatus.CONTINUE;
                    }
                },
                null    // No clientState
                );
            }
        }
    }

    private void applyDefines() throws AbortedException {
        m_engine.provideFeedback("Applying defines to route...");
        m_engine.getAbort();

        RouteDefines defines = new RouteDefines();
        for (Route route : m_routeCoreContainer) {
            for (FunctionTimeInfo info : m_engine.getFunctionTimeDefines()) {
                if (coreMatch(route, info.getFunction())) {
                    defines.applyFunctionDefine(m_src, route, info, Source.ADD);
                }
            }
            for (PathTimeInfo info : m_engine.getPathTimeDefines()) {
                if (coreMatch(route, info.getFrom()) &&
                    coreMatch(route, info.getTo())) {
                    defines.applyPathDefine(m_engine, m_src, route, info, Source.ADD);
                }
            }
        }
        for (LoopInfo info : m_engine.getLoopDefines()) {
            try {
                for (Route route : m_routeCoreContainer) {
                    if (coreMatch(route, info.getReference())) {
                        route.getRouteUnknowns().checkSetLoopIterations(
                            info.getReference(), info.getIterations(), m_engine, m_src);
                    }
                }
                for (Route route : m_routeCoreContainer) {
                    if (coreMatch(route, info.getReference())) {
                        route.getRouteUnknowns().setLoopIterations(m_src, m_engine,
                            info.getReference(), info.getIterations(), false, Source.ADD);
                    }
                }
            } catch (Exception e) {}
        }
        for (LoopPathInfo info : m_engine.getLoopPathDefines()) {
            try {
                for (Route route : m_routeCoreContainer) {
                    if (coreMatch(route, info.getReference())) {
                        route.getRouteUnknowns().checkSetLoopPathIterations(
                            info.getReference(), info.getIterations(), m_engine, m_src);
                    }
                }
                for (Route route : m_routeCoreContainer) {
                    if (coreMatch(route, info.getReference())) {
                        route.getRouteUnknowns().setLoopPathIterations(
                            info.getReference(), info.getIterations(), Source.ADD);
                    }
                }
            } catch (Exception e) {}
        }
        for (LoopScopeInfo info : m_engine.getLoopScopeDefines()) {
            try {
                for (Route route : m_routeCoreContainer) {
                    if (coreMatch(route, info.getReference())) {
                        route.getRouteUnknowns().checkSetLoopScope(
                            info.getReference(), info.getAbsolute(), m_engine, m_src);
                    }
                }
                for (Route route : m_routeCoreContainer) {
                    if (coreMatch(route, info.getReference())) {
                        route.getRouteUnknowns().setLoopScope(
                            info.getReference(), info.getAbsolute(), Source.ADD);
                    }
                }
            } catch (Exception e) {}
        }
        for (InstructionTimeInfo info : m_engine.getInstructionTimeDefines()) {
            try {
                for (Route route : m_routeCoreContainer) {
                    if (coreMatch(route, info.getReference())) {
                        if (!route.getRouteUnknowns().canInstructionPause(info.getReference()))
                            throw new Exception();
                    }
                }
                for (Route route : m_routeCoreContainer) {
                    if (coreMatch(route, info.getReference())) {
                        route.getRouteUnknowns().setFixedTime(info.getReference(),
                                info.getTimeUnit(), UnknownFactory.Type.INSTRUCTION_TIME, Source.ADD);
                    }
                }
            } catch (Exception e) {}
        }
    }

    private boolean coreMatch(Route route, Reference reference) {
        return reference.getCoreId().equals(route.parentRouteCoreContainer().getCoreId());
    }

    private void refreshDisplayedUnknowns(Node node) {
        // Clears the display unknown flags on all nodes..
        node.traverse(new UntypedNodeTraverser() {
            public TraverseStatus foundNode(Node node, Object clientState) {
                node.getAttributes(true).setDisplayAsUnknown(false);
                return TraverseStatus.CONTINUE;
            }
        },
        null  // No clientState
        );

        // On each of the unknown nodes, mark all of their parents to 'display unknown'.
        node.traverse(new UntypedNodeTraverser() {
            public TraverseStatus foundNode(Node node, Object clientState) {
                if (node.getAttributes(false).hasUnknowns())
                    setDisplayAsUnknownOnParentNodes(node);
                return TraverseStatus.CONTINUE;
            }
        },
        null  // No clientState
        );

    }

    private void setDisplayAsUnknownOnParentNodes(Node node) {
        Node parent = node;
        while (parent != null) {
            if (parent.getAttributes(false).getDisplayAsUnknown() == false)
                parent.getAttributes(true).setDisplayAsUnknown(true);
            parent.getAttributes(true).setDisplayAsUnknown(true);
            parent = parent.getParent();
        }
    }

    private static class NodeDescriptor implements Comparable<NodeDescriptor>{
        private CfgNode m_node;
        private BackTrail m_backTrail;
        private int m_id;

        public NodeDescriptor(CfgNode node, BackTrail backTrail){
            m_node = node;
            m_backTrail = backTrail;
            m_id = node.getId();
        }

        public boolean equals(Object obj) {
            if(obj instanceof NodeDescriptor){
                NodeDescriptor comp = (NodeDescriptor)obj;
                if(comp.m_node == m_node && comp.m_backTrail.equals(m_backTrail)){
                    return true;
                }

            }
            return false;
        }

        public int compareTo(NodeDescriptor o) {
            return m_id - o.m_id;
        }
    }
}