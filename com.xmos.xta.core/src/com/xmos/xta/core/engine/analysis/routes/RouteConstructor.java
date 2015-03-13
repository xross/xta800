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
import com.xmos.libs.core.elfhandler.IssueRegions;
import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.libs.core.instructiondecoder.Mnemonics;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
import com.xmos.libs.core.utils.BackTrail;
import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.utils.IteratableMap;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.libs.core.utils.NonIteratableSet;
import com.xmos.libs.core.utils.Pair;
import com.xmos.libs.core.xeloader.XeLoader.Architecture;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.CommandSource.Type;
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

        issueNodeAlaysis();
        setUnknowns();
        setSrcLoopCounts();
        checkSupportedInstructions();
        applyDefines();

        m_engine.provideFeedback("Analysis completing...");
    }

    private void buildbranchBackTrailsList() throws AbortedException, MemoryMonitorException {
        for (Reference ref : m_engine.getUserBranches().keySet()) {
            for (BackTrails backTrails : getPathBackTrails(m_engine, m_core, ref)) {
                Set<CfgNode> branchNodes = new IteratableSet<CfgNode>();
                for (Reference nextRef : m_engine.getUserBranches().get(ref)) {
                    for (BackTrails nextBackTrails : getPathBackTrails(m_engine, m_core, nextRef)) {
                        if (backTrails.getInstructionNode().getInstruction().isBranchLink()) {
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
                Pair<FunctionNode, Boolean> fnReturn = makeFunctionNode(true, backTrail);
                FunctionNode fn = fnReturn.first;
                updateRoutes(fn);
            }
    }

    private void analyzeLoop() throws AbortedException, MemoryMonitorException {
        Reference ref = m_routeCoreContainer.getFromReference();
        List<BackTrails> loopStartBackTrailsList = RouteLoopConversion.updateLoopBackTrails(m_engine, m_core, ref, m_exclusionBackTrailsList);
        for (BackTrails backTrails : loopStartBackTrailsList) {
            for (BackTrail backTrail : backTrails) {
                Node node = makeEndpoints(backTrails.getInstructionNode(), backTrail, loopStartBackTrailsList);
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
                Node node = makeEndpoints(backTrails.getInstructionNode(), backTrail, endBackTrailsList);
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






    private Node makeEndpoints(CfgInstructionNode startInst, BackTrail backTrail, List<BackTrails> endBackTrailsList) throws AbortedException, MemoryMonitorException {
        ReachableGraph reachableGraph = new ReachableGraph(false, endBackTrailsList);
        reachableGraph.findReachableGraph(true, null, startInst, backTrail);
        Node node = reachableGraph.convertGraphToStructure(startInst, backTrail);
        return node;
    }

    private Pair<FunctionNode, Boolean> makeFunctionNode(boolean firstInstruction, BackTrail backTrail) throws AbortedException, MemoryMonitorException {
        CfgFunctionReferenceNode fnRef = backTrail.peek();
        if (fnRef.isUnknownFunction())
            return new Pair<FunctionNode, Boolean>(new FunctionNode(), false);
        CfgInstructionNode startInst = fnRef.getFunctionStartInstruction();
        List<BackTrails> endBackTrailsList = getFunctionEndBackTrails(backTrail);
        ReachableGraph reachableGraph = new ReachableGraph(true, endBackTrailsList);
        reachableGraph.findReachableGraph(firstInstruction, null, startInst, backTrail);
        Node node = reachableGraph.convertGraphToStructure(startInst, backTrail);
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
        private Map<NodeDescriptor, CfgInstructionNode> m_nodeToNewNodeLookup;
        private Map<CfgInstructionNode, CfgInstructionNode> m_newNodeToNodeLookup;

        ReachableGraph(boolean functionMode, List<BackTrails> endBackTrailsList) {
            m_functionMode = functionMode;
            m_endBackTrailsList = endBackTrailsList;
            m_workingSet = new IteratableSet<CfgNode>();
            m_currentExclusionsFound = new IteratableSet<CfgNode>();
            m_nodeToNewNodeLookup = new IteratableMap<NodeDescriptor, CfgInstructionNode>();
            m_newNodeToNodeLookup = new IteratableMap<CfgInstructionNode, CfgInstructionNode>();
        }

        void findReachableGraph(boolean firstInstruction, CfgNode previous, CfgNode node, BackTrail backTrail) throws AbortedException, MemoryMonitorException {
            boolean checkExclusions = false;
            while (true) {
                CfgNode newNode;
                if (node instanceof CfgFunctionReferenceNode) {
                    assert(!firstInstruction);
                    newNode = addFunctionReferenceNode(previous, (CfgFunctionReferenceNode)node, backTrail);
                    if (newNode == null)
                        return;
                } else {
                    assert(node instanceof CfgInstructionNode);
                    newNode = addInstructionNode(previous, (CfgInstructionNode)node, backTrail, firstInstruction, checkExclusions);
                    if (newNode == null)
                        return;
                }
                Set<CfgNode> nextNodes = getValidBranches(node, backTrail);
                switch (nextNodes.size()) {
                case 0:
                    assert(newNode instanceof CfgInstructionNode);
                    handleNoNextNode((CfgInstructionNode)newNode, backTrail);
                    return;
                case 1:
                    previous = newNode;
                    node = nextNodes.iterator().next();
                    firstInstruction = false;
                    checkExclusions = true;
                    break; // And we will start from the top again!
                default:
                    // Handle branch
                    for (CfgNode nextNode : nextNodes)
                        findReachableGraph(false, newNode, nextNode, backTrail);
                    return;
                }
            } // while(true)
        }

        Node convertGraphToStructure(CfgInstructionNode startInst, BackTrail backTrail) throws AbortedException, MemoryMonitorException {
            if (removeExclusionBranches().isEmpty())
                return null;
            CfgInstructionNode newStartNode = m_nodeToNewNodeLookup.get(new NodeDescriptor(startInst, backTrail));
            assert(newStartNode != null);
            dumpGraph();
            return StructuralAnalysisEngine.createStructure(m_engine, m_workingSet, newStartNode, "route");
        }

        private CfgContainerNode addFunctionReferenceNode(CfgNode previous, CfgFunctionReferenceNode node, BackTrail backTrail) throws AbortedException, MemoryMonitorException {
            CfgInstructionNode inst = node.getFunctionStartInstruction();

            if (backTrail.recursivelyContains(inst)) {
                previous.setRecursive(true);
                return null;
            }
            CfgContainerNode newNode;
            BackTrail newBackTrail = new BackTrail(backTrail);
            newBackTrail.push(node);

            for (BackTrails backTrails : m_endBackTrailsList)
                if (backTrails.contains(node)) {
                    findReachableGraph(false, previous, inst, newBackTrail);
                    return null;
                }

            if (node.isUnknownFunction()) {
                newNode = connectFunctionNode(previous, new FunctionNode());
                return null;
            }
            Pair<FunctionNode, Boolean> fnReturn = makeFunctionNode(false, newBackTrail);
            FunctionNode fn = fnReturn.first;
            boolean continueAnalysis = fnReturn.second;
            if (!continueAnalysis) {
                newNode = connectFunctionNode(previous, fn);
                if (fn == null) // The function never met an endpoint and didn't return.
                    m_currentExclusionsFound.add(newNode);
                return null;
            }
            return connectFunctionNode(previous, fn);
        }

        private CfgInstructionNode addInstructionNode(CfgNode previous, CfgInstructionNode node, BackTrail backTrail, boolean firstInstruction, boolean checkExclusions) throws AbortedException, MemoryMonitorException {
            boolean nodeOnExcludedList = m_exclusionBackTrailsList.contains(node, backTrail);
            // RL-TODO why do we do thing differently if already added?
            if (m_nodeToNewNodeLookup.containsKey(new NodeDescriptor(node, backTrail))) {
                // We have already added it, so we just need to connect it.
                if (m_functionMode)
                    connectInstructionNode(previous, node, backTrail);
                else if (endBackTrailsListContainNode(node, backTrail))
                    connectNullNode(previous);
                else if (!nodeOnExcludedList)
                    // We've formed a loop, so connect the current to the target thats already in the working set.
                    connectInstructionNode(previous, node, backTrail);

                // This is here to allow you to exclude the start instruction.
                if (checkExclusions && nodeOnExcludedList)
                    m_currentExclusionsFound.add(previous);  // RL-TODO How does this work? Is previous far enough?
                return null;
            }
            if (nodeOnExcludedList && !firstInstruction) {
                CfgInstructionNode newNode = connectInstructionNode(previous, node, backTrail);
                m_currentExclusionsFound.add(newNode);
                return null;
            }
            if (node.getUnresolved()) {
                connectInstructionNode(previous, node, backTrail);
                return null;
            }
            if (endBackTrailsListContainNode(node, backTrail)) {
                if (m_functionMode) {
                    connectInstructionNode(previous, node, backTrail);
                    return null;
                } else if(!m_workingSet.isEmpty()) {
                    connectNullNode(previous);
                    return null;
                }
            }
            return connectInstructionNode(previous, node, backTrail);
        }

        private void handleNoNextNode(CfgInstructionNode node, BackTrail backTrail) throws AbortedException, MemoryMonitorException {
            if (m_branchBackTrailsList.contains(node.getPc())) {
                // Here we have no next nodes (i.e. it is unresolved), but isn't marked as unresolved
                // because there is at least one way we can get to this node where there is a next node.
                node.setUnresolved(true);
            } else if (backTrail.isEmpty()) {
                // There is no caller, hence there are no next instructions.
                node.setUnresolved(true);
            } else {
                // We return to the caller and look at it's next instruction.
                BackTrail newBackTrail = new BackTrail(backTrail);
                CfgFunctionReferenceNode callingFnRef = newBackTrail.pop();
                for (CfgNode nextNode : callingFnRef.getNextNodes())
                    findReachableGraph(false, node, nextNode, newBackTrail);
            }
        }

        private CfgInstructionNode connectInstructionNode(CfgNode previous, CfgInstructionNode node, BackTrail backTrail) {
            NodeDescriptor nd = new NodeDescriptor(node, backTrail);
            CfgInstructionNode newNode = m_nodeToNewNodeLookup.get(nd);
            if (newNode == null) {
                newNode = node.copy();
                m_nodeToNewNodeLookup.put(nd, newNode);
                m_newNodeToNodeLookup.put(newNode, node);
                m_workingSet.add(newNode);
            }
            if (previous != null) {
                if (previous instanceof CfgInstructionNode) {
                    CfgInstructionNode newPrev = (CfgInstructionNode)previous;
                    CfgInstructionNode prev = m_newNodeToNodeLookup.get(newPrev);
                    boolean branchNotTaken = prev.getNextBranchNotTakenNode() == node;
                    assert(node.getPrevBranchNotTakenNode() == prev == branchNotTaken);
                    newPrev.connectNext(newNode, branchNotTaken);
                } else {
                    // We cannot have arrived here via a conditional branch instruction.
                    assert(node.getPrevBranchNotTakenNode() == null);
                    previous.connectNext(newNode);
                }
            }
            return newNode;
        }

        private CfgContainerNode connectFunctionNode(CfgNode previous, FunctionNode node) {
            CfgContainerNode newNode = new CfgContainerNode(node);
            m_workingSet.add(newNode);
            if (previous != null)
                previous.connectNext(newNode);
            return newNode;
        }

        private void connectNullNode(CfgNode previous) {
            //search the working set for a null node
            CfgNullNode nullNode = null;
            for (CfgNode node : m_workingSet)
                if (node instanceof CfgNullNode) {
                    nullNode = (CfgNullNode)node;
                    break;
                }
            if (nullNode == null) {
                nullNode = new CfgNullNode();
                m_workingSet.add(nullNode);
            }
            if (previous != null)
                previous.connectNext(nullNode);
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

        private Set<CfgNode> getValidBranches(CfgNode node, BackTrail backTrail) {
            Set<CfgNode> validNextNodes = new IteratableSet<CfgNode>();
            validNextNodes.addAll(node.getNextNodes());
            validNextNodes.removeAll(m_branchBackTrailsList.getBranchNodesNotIn(backTrail));
            return validNextNodes;
        }

        private boolean endBackTrailsListContainNode(CfgNode node, BackTrail backTrail) {
            if (node instanceof CfgInstructionNode)
                for (BackTrails backTrails : m_endBackTrailsList)
                    if (backTrails.getInstructionNode().equals(node))
                        for (BackTrail bt : backTrails)
                            if (bt.equals(backTrail))
                                return true;
            return false;
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




    private void issueNodeAlaysis() throws AbortedException {
        m_engine.provideFeedback("Packing issueNodes & adding fnops to routes...");
        for (final Route route : m_routeCoreContainer) {
            m_engine.getAbort();
            Node node = route.getNode();
            long startPc = node.getStartPc();

            List<CfgInstructionNode> cfgs = new LinkedList<CfgInstructionNode>();
            for (FunctionControlFlow fcf : m_engine.getControlFlow(m_core).getFunctionContaining(startPc))
                cfgs.add(fcf.getInstructionNode(startPc));

            Architecture arch = m_engine.getXeLoader().getArchitecture();
            IssueRegions issueRegions = new IssueRegions(m_engine.getXeLoader().getSymbolTable(m_core));
            IssueNodeAnalyser instructionBuffer = new IssueNodeAnalyser(arch, issueRegions, startPc, cfgs);
            node.traverse( new UntypedNodeTraverser() {
                public TraverseStatus foundNode(Node node, Object clientState) {
                    IssueNodeAnalyser ibuff = (IssueNodeAnalyser)clientState;
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
                    IssueNodeAnalyser ibuff = (IssueNodeAnalyser)clientState;
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
                            if (instruction.isA(Mnemonics.SSYNC))
                                engine.notify(src, Notifier.NOTIFY_WARNING, String.format(
                                        "ssync (multiple threads) not supported. Pc: 0x%08x", instruction.getPc()));

                            if (instruction.isA(Mnemonics.MSYNC))
                                engine.notify(src, Notifier.NOTIFY_WARNING, String.format(
                                        "msync (multiple threads) not supported. Pc: 0x%08x", instruction.getPc()));

                            if (instruction.isA(Mnemonics.MJOIN)) {
                                engine.notify(src, Notifier.NOTIFY_WARNING, String.format(
                                        "mjoin (multiple threads) not supported. Pc: 0x%08x", instruction.getPc()));
                            }

                            // Checks for interrupt enable..
                            if (instruction.isA(Mnemonics.SETSR)) {

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
        private CfgInstructionNode m_node;
        private BackTrail m_backTrail;

        public NodeDescriptor(CfgInstructionNode node, BackTrail backTrail){
            m_node = node;
            m_backTrail = backTrail;
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
            return m_node.getId() - o.m_node.getId();
        }
    }
}