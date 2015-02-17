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
    private final CommandSource m_src;
    private final RouteCoreContainer m_routeCoreContainer;    // We will be adding routes to m_routeCoreContainer.m_routes for this core.
    private final CoreId m_core;
    private final Engine m_engine;
    private Map<BackTrails, Set<CfgNode>> m_branchToNext;
    private List<BackTrails> m_branchBackTrailsList;
    private List<BackTrails> m_exclusionBackTrailsList;
    
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

   public static void analyze(CommandSource src, RouteCoreContainer routeCoreContainer, CoreId core, Engine engine)
            throws Exception {
        // The routeCoreContainer holds the information we will need.
        // We will be adding the routeCoreContainer.List<Route>
        RouteConstructor routeConstructor = new RouteConstructor(src, routeCoreContainer, core, engine);
        routeConstructor.analyze();
    }

   private RouteConstructor(CommandSource src, RouteCoreContainer routeCoreContainer, CoreId core, Engine engine) throws Exception {
        m_src = src;
        m_routeCoreContainer = routeCoreContainer;
        m_core = core;
        m_engine = engine;
        m_branchToNext = new IteratableMap<BackTrails, Set<CfgNode>>();
        m_branchBackTrailsList = new LinkedList<BackTrails>();
        m_exclusionBackTrailsList = new LinkedList<BackTrails>();
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
        for(Reference ref : m_engine.getUserBranches().keySet()){
            List<BackTrails> backTrailsList = getPathBackTrails(m_engine, m_core, ref);
            m_branchBackTrailsList.addAll(backTrailsList);
            for(BackTrails backTrails : backTrailsList){
                m_branchToNext.put(backTrails, new IteratableSet<CfgNode>());

                for(Reference nextRef : m_engine.getUserBranches().get(ref)){
                    for(BackTrails nextBackTrails : getPathBackTrails(m_engine, m_core, nextRef)){
                        if(backTrails.getInstruction().getInstruction().isLink()){
                            for(CfgNode nextFrn : backTrails.getInstruction().getNextNodes()){
                                if(nextFrn instanceof CfgFunctionReferenceNode){
                                    CfgFunctionReferenceNode frn = (CfgFunctionReferenceNode)nextFrn;
                                    if(frn.getFunctionStartInstruction() == nextBackTrails.getInstruction()){
                                        m_branchToNext.get(backTrails).add(frn);
                                    }
                                }
                            }
                        } else {
                            m_branchToNext.get(backTrails).add(nextBackTrails.getInstruction());
                        }
                    }
                }
            }
        }
    }

    private void buildexclusionBackTrailsList() throws AbortedException, MemoryMonitorException {
        for (ExclusionInfo info : m_engine.getExclusions()) {
            Reference ref = info.getReference();
            if (ref.getCoreId().equals(m_core))
                m_exclusionBackTrailsList.addAll(getPathBackTrails(m_engine, m_core, ref));
        }
    }

    private void analyzeFunction() throws AbortedException, MemoryMonitorException {
        List<BackTrails> startBackTrailsList = getPathBackTrails(m_engine, m_core, m_routeCoreContainer.getFromReference());
        for (BackTrails backTrails : startBackTrailsList)
            for (BackTrail backTrail : backTrails)
                updateRoutes(makeFunction(backTrail, true).first);
    }

    private void analyzeLoop() throws AbortedException, MemoryMonitorException {
        Reference ref = m_routeCoreContainer.getFromReference();
        List<BackTrails> loopStartBackTrailsList = RouteLoopConversion.updateLoopBackTrails(m_engine, m_core, ref, m_exclusionBackTrailsList);
        analyzePath(loopStartBackTrailsList, loopStartBackTrailsList);
    }

    private void analyzeEndpoints() throws AbortedException, MemoryMonitorException {
        List<BackTrails> startBackTrailsList = getPathBackTrails(m_engine, m_core, m_routeCoreContainer.getFromReference());
        List<BackTrails> endBackTrailsList = new LinkedList<BackTrails>();
        for (Reference end : m_routeCoreContainer.getToReferences())
            endBackTrailsList.addAll(getPathBackTrails(m_engine, m_core, end));
        analyzePath(startBackTrailsList, endBackTrailsList);
    }

    private void analyzePath(List<BackTrails> startBackTrailsList, List<BackTrails> endBackTrailsList) throws AbortedException, MemoryMonitorException {
        for (BackTrails backTrails : startBackTrailsList) {
            for (BackTrail backTrail : backTrails) {
                Node node = makeEndpoints(backTrail, backTrails.getInstruction(), endBackTrailsList);
                updateRoutes(node);
            }
        }
    }

    private void updateRoutes(Node node) {
        if (node != null) {
            setParent(node);
            if (!alreadyContainRoute(node)) {
                Route route = new Route(m_src, m_engine.getXeLoader().getArchitecture(), node, m_routeCoreContainer);
                m_routeCoreContainer.add(route);
            }
        }
    }

    private Pair<FunctionNode, Boolean> makeFunction(BackTrail currentBackTrail, boolean start) throws AbortedException, MemoryMonitorException {

        List<BackTrails> endBackTrailsList = getFunctionEndBackTrails(currentBackTrail);

        CfgFunctionReferenceNode fnRef = currentBackTrail.peek();

        if (!fnRef.isKnownFunction())
            return new Pair<FunctionNode, Boolean>(new FunctionNode(), false);

        CfgInstructionNode startInst = fnRef.getFunctionStartInstruction();
        Set<CfgNode> workingSet = new IteratableSet<CfgNode>();
        Set<CfgNode> currentExclusionsFound = new IteratableSet<CfgNode>();
        Map<NodeDescriptor, CfgNode> nodeToWorkingSet = new IteratableMap<NodeDescriptor, CfgNode>();

        NodeDescriptor startNodeDescriptor = new NodeDescriptor(startInst, currentBackTrail);


        findReachableGraph(null, currentBackTrail, startInst, workingSet,
                currentExclusionsFound, RouteMode.FUNCTION,
                endBackTrailsList, nodeToWorkingSet, start);

        removeExclusionBranches(workingSet, currentExclusionsFound);

        if (workingSet.isEmpty())
            return new Pair<FunctionNode, Boolean>(null, false);

        //if working set contains an end node then we will continue else we wont
        boolean continueAfterFunction = true; // RL-TODO changing this to false changes output for test/Errors/Unresolved/function_pointer

        for (BackTrails backTrails : endBackTrailsList) {
            for (CfgNode node : workingSet) {
                if (node instanceof CfgInstructionNode) {
                    if (((CfgInstructionNode)node).getInstruction() == backTrails.getInstruction().getInstruction()) {
                        continueAfterFunction = true;
                        break;
                    }
                }
            }
        }
        Node node = convertGraphToStructure(workingSet, startNodeDescriptor, nodeToWorkingSet);
        FunctionNode fn = new FunctionNode(node, fnRef.getFunctionName(), fnRef.getFunctionCallPc());
        return new Pair<FunctionNode, Boolean>(fn, continueAfterFunction);
    }

    private Node makeEndpoints(BackTrail currentBackTrail,
            CfgInstructionNode startInst,
            List<BackTrails> endBackTrails)
        throws AbortedException, MemoryMonitorException {

        Set<CfgNode> workingSet = new IteratableSet<CfgNode>();
        Set<CfgNode> currentExclusionsFound = new IteratableSet<CfgNode>();

        Map<NodeDescriptor, CfgNode> nodeToWorkingSet = new IteratableMap<NodeDescriptor, CfgNode>();
        NodeDescriptor startNodeDescriptor = new NodeDescriptor(startInst, currentBackTrail);
        findReachableGraph(null, currentBackTrail, startInst, workingSet,
                currentExclusionsFound,
                RouteMode.ENDPOINTS, endBackTrails, nodeToWorkingSet, true);
        removeExclusionBranches(workingSet, currentExclusionsFound);

        if(workingSet.isEmpty()){
            return null;
        }
        Node node = convertGraphToStructure(workingSet, startNodeDescriptor, nodeToWorkingSet);

        return node;
    }

    private boolean isCurrentNodeUnresolved(CfgNode currentNode) {
        return currentNode.getUnresolved();
    }

    private boolean isFunctionReferenceNode(CfgNode node) {
        return (node instanceof CfgFunctionReferenceNode);
    }

    private boolean isCurrentNodeAnEndNode(
            List<BackTrails> endBackTrailsList,
            BackTrail currentBackTrail,
            CfgNode node,
            RouteMode routeMode,
            CfgNode previous,
            CfgNode currentNode,
            Set<CfgNode> workingSet) {

        if(backTrailsContainNode(endBackTrailsList, currentBackTrail, node)){
            switch (routeMode) {
            case FUNCTION:
                addToWorkingSet(previous, currentNode, workingSet);
                return true;
            case ENDPOINTS:
                if(!workingSet.isEmpty()){
                    connectToNullNode(previous, workingSet);
                    return true;
                }
            case LOOP:
                break;
            }
        }
        return false;
    }

    private boolean isFormingRecursion(BackTrail currentBackTrail, CfgNode previous, CfgFunctionReferenceNode frn) {
        List<CfgInstructionNode> functionCallInstructions = new LinkedList<CfgInstructionNode>();
        for (CfgFunctionReferenceNode f : currentBackTrail) {
            functionCallInstructions.add(f.getFunctionStartInstruction());
        }
        if (functionCallInstructions.contains(frn.getFunctionStartInstruction())) {
            //recursion detected
            previous.setRecursive(true);
            return true;
        } else {
            return false;
        }
    }

    private void findReachableGraph(
            CfgNode previous,
            BackTrail currentBackTrail,
            CfgNode node,
            Set<CfgNode> workingSet,
            Set<CfgNode> currentExclusionsFound,
            RouteMode routeMode,
            List<BackTrails> endBackTrailsList,
            Map<NodeDescriptor, CfgNode> nodeToWorkingSet,
            boolean firstInstruction)
    throws AbortedException, MemoryMonitorException {

        boolean repeat = true;

        while(repeat){

            repeat = false;

            CfgNode currentNode ;

            if(isFunctionReferenceNode(node)){

                CfgFunctionReferenceNode frn = ((CfgFunctionReferenceNode)node);

                if(isFormingRecursion(currentBackTrail, previous, frn)){
                    return;
                }

                BackTrail newBackTrail = increaseBackTrail(currentBackTrail, frn);

                if (containsEndPoint(node, endBackTrailsList)) {
                    CfgInstructionNode inst = frn.getFunctionStartInstruction();
                    findReachableGraph(previous, newBackTrail, inst, workingSet,
                            currentExclusionsFound, routeMode,
                            endBackTrailsList, nodeToWorkingSet, false);
                    return;
                } else {

                    if (frn.isKnownFunction()){
                        Pair<FunctionNode, Boolean> fnReturn = makeFunction(newBackTrail, false);
                        boolean continueAnalysis = fnReturn.second;
                        FunctionNode fn = fnReturn.first;

                        if (fn != null) {
                            if (continueAnalysis) {
                                //this is the case where the exclusions didn't stop the control flow reaching the end of a function the continues
                                currentNode = new CfgContainerNode(fn);
                            } else {
                                CfgNode terminalFnNode = new CfgContainerNode(fn);
                                addToWorkingSet(previous, terminalFnNode, workingSet);
                                return;
                            }
                        } else {
                            //if the function never met an endpoint and didn't return
                            CfgNode unknownFnNode = new CfgContainerNode(fn);
                            currentExclusionsFound.add(unknownFnNode);
                            addToWorkingSet(previous, unknownFnNode, workingSet);
                            return;
                        }
                    } else {
                        CfgNode fnNode = new CfgContainerNode(new FunctionNode());
                        addToWorkingSet(previous, fnNode, workingSet);
                        return;
                    }

                }
            } else {
                currentNode = getCurrentNode(currentBackTrail, node, nodeToWorkingSet);
            }


            if (!firstInstruction && exclusionBackTrailsListContainNode(currentBackTrail, node)){
                currentExclusionsFound.add(currentNode);
                addToWorkingSet(previous, currentNode, workingSet);
                return;
            }

            firstInstruction = false;

            if(isCurrentNodeUnresolved(currentNode)){
                addToWorkingSet(previous, currentNode, workingSet);
                return;
            }

            //this adds to the loop forming end nodes
            if(isCurrentNodeAnEndNode(endBackTrailsList, currentBackTrail, node, routeMode, previous, currentNode, workingSet)){
                return;
            }

            addToWorkingSet(previous, currentNode, workingSet);

            //nextNodes is the set of nodes that follow this currentNode, it should include any user added next nodes.
            Set<CfgNode> nextNodes = getValidBranches(currentBackTrail, node);

            if (nextNodes.size() == 0) {
                handelEndOfFunction(previous, currentBackTrail, workingSet,
                        currentExclusionsFound, routeMode,
                        endBackTrailsList, nodeToWorkingSet,
                        currentNode);
            } else if (nextNodes.size() == 1) {

                CfgNode next = nextNodes.iterator().next();
                node = next;
                previous = currentNode;


                CfgNode nextWorkingSetNode = containsNode(next, currentBackTrail, nodeToWorkingSet);
                if (nextWorkingSetNode == null) {
                    repeat = true;
                } else {
                    handleNodeAlreadyInWorkingSet(routeMode, endBackTrailsList, currentBackTrail, workingSet, currentNode, next, nextWorkingSetNode);

                    //this is here to allow you to exclude the start instruction
                    if (exclusionBackTrailsListContainNode(currentBackTrail, next)) {
                        currentExclusionsFound.add(currentNode);
                    }

                }
            } else if (nextNodes.size() > 1) {
                handleBranch(currentBackTrail, workingSet,
                        currentExclusionsFound, routeMode, endBackTrailsList,
                        nodeToWorkingSet,
                        currentNode, nextNodes);
            }

        }

        //if nextNodes
    }

    private BackTrail increaseBackTrail(BackTrail currentBackTrail, CfgFunctionReferenceNode frn) {
        BackTrail newBackTrail = new BackTrail();
        newBackTrail.addAll(currentBackTrail);
        newBackTrail.add(frn);
        return newBackTrail;
    }

    private boolean containsEndPoint(CfgNode node, List<BackTrails> endBackTrailsList) {
        for(BackTrails backTrails : endBackTrailsList) {
            for(BackTrail backTrail : backTrails){
                if(backTrail.contains(node)){
                    return true;
                }
            }
        }
        return false;
    }



    private void handleBranch(BackTrail currentBackTrail,Set<CfgNode> workingSet,
            Set<CfgNode> currentExclusionsFound, RouteMode routeMode,
            List<BackTrails> endBackTrailsList, Map<NodeDescriptor, CfgNode> nodeToWorkingSet, CfgNode currentNode,
            Set<CfgNode> nextNodes) throws AbortedException,
            MemoryMonitorException {

        for (CfgNode next : nextNodes) {
            CfgNode nextWorkingSetNode = containsNode(next, currentBackTrail, nodeToWorkingSet);

            if (nextWorkingSetNode == null) {
                findReachableGraph(currentNode, currentBackTrail, next, workingSet,
                        currentExclusionsFound, routeMode, endBackTrailsList,
                        nodeToWorkingSet, false);
            } else {
                handleNodeAlreadyInWorkingSet(routeMode, endBackTrailsList, currentBackTrail, workingSet, currentNode, next, nextWorkingSetNode);
            }
        }
    }

    private void handelEndOfFunction(CfgNode previous,
            BackTrail currentBackTrail,
            Set<CfgNode> workingSet,
            Set<CfgNode> currentExclusionsFound, RouteMode routeMode,
            List<BackTrails> endBackTrailsList,
            Map<NodeDescriptor, CfgNode> nodeToWorkingSet, CfgNode currentNode)
            throws AbortedException, MemoryMonitorException {



        if (isNodeContainedInbranchBackTrailsList(currentNode)) {
            // Here we have no next nodes (i.e. it is unresolved), but isn't marked
            // as unresolved because there is at least one way we can get to this node
            // where there is a next node.
            addToWorkingSet(previous, currentNode, workingSet);
            currentNode.setUnresolved(true);
        } else {
            if(currentBackTrail.size() == 1){
                currentNode.setUnresolved(true);
            } else {
                CfgFunctionReferenceNode callingFnRef = currentBackTrail.peek();
                for (CfgNode followingFunction : callingFnRef.getNextNodes()) {
                    BackTrail newBackTrail = decreaseBackTrail(currentBackTrail);
                    findReachableGraph(currentNode, newBackTrail, followingFunction, workingSet,
                            currentExclusionsFound, routeMode, endBackTrailsList,
                            nodeToWorkingSet, false);
                }
            }
        }
    }

    private BackTrail decreaseBackTrail(BackTrail currentBackTrail) {
        BackTrail decreasedBackTrail = new BackTrail();
        decreasedBackTrail.addAll(currentBackTrail);
        decreasedBackTrail.pop();
        return decreasedBackTrail;
    }

    private void handleNodeAlreadyInWorkingSet(
            RouteMode routeMode,
            List<BackTrails> endBackTrailsList,
            BackTrail currentBackTrail,
            Set<CfgNode> workingSet,
            CfgNode currentNode,
            CfgNode nextNode,
            CfgNode nextNodeFromWorkingSet) {

        if (routeMode == RouteMode.FUNCTION){
            currentNode.connectNext(nextNodeFromWorkingSet);
            return;
        }

        boolean nextNodeIsAnEndNode = backTrailsContainNode(endBackTrailsList, currentBackTrail, nextNode);
        if (nextNodeIsAnEndNode) {
            connectToNullNode(currentNode, workingSet);
            return;
        }

        if ( !exclusionBackTrailsListContainNode(currentBackTrail, nextNode)) {
            // We've formed a loop, so connect the current to the target thats already in the working set..
            currentNode.connectNext(nextNodeFromWorkingSet);
        }
    }

    private void connectToNullNode(CfgNode currentNode, Set<CfgNode> workingSet) {
        //search the working set for a null node
        CfgNullNode nullNode = null;

        for(CfgNode node : workingSet){
            if(node instanceof CfgNullNode){
                nullNode = (CfgNullNode)node;
                break;
            }
        }
        if (nullNode == null) {
            nullNode = new CfgNullNode();
        }
        addToWorkingSet(currentNode, nullNode, workingSet);
    }

    private Set<CfgNode> getValidBranches(BackTrail currentBackTrail, CfgNode node) {

        Set<CfgNode> validNextNodes = new IteratableSet<CfgNode>();

        validNextNodes.addAll(node.getNextNodes());

        for (BackTrails backTrails : m_branchBackTrailsList){
            if (!backTrails.contains(currentBackTrail)){
                validNextNodes.removeAll(m_branchToNext.get(backTrails));
            }
        }
        return validNextNodes;
    }

    private boolean alreadyContainRoute(Node newGraph) {
        final List<String> newNodeList = createRouteDescription(newGraph);

        for (Route route : m_routeCoreContainer){
            List<String> nodeList = createRouteDescription(route.getNode());
            if (newNodeList.equals(nodeList)){
                return true;
            }
        }
        return false;
    }

    private List<String> createRouteDescription(Node node) {
        final List<String> nodeList = new LinkedList<String>();

        node.traverse(new UntypedNodeTraverser() {
            public TraverseStatus foundNode(Node node, Object clientState) {
                if(node.isIssueNode()){
                    nodeList.add(node.getTypeName()+node.getStartPc());
                } else {
                    nodeList.add(node.getTypeName());
                }
                return TraverseStatus.CONTINUE;
            }
        },
        null  // No clientState
        );
        return nodeList;
    }

    private Node convertGraphToStructure(Set<CfgNode> workingSet, NodeDescriptor startNodeDescriptor,
            Map<NodeDescriptor, CfgNode> nodeToWorkingSet)
        throws AbortedException, MemoryMonitorException {

        CfgNode originalStartNode = null;
        for (NodeDescriptor nd : nodeToWorkingSet.keySet()) {
            if (startNodeDescriptor.equals(nd) && workingSet.contains(nodeToWorkingSet.get(nd))) {
                originalStartNode = nodeToWorkingSet.get(nd);
                break;
            }
        }
        assert(originalStartNode != null);

        dumpGraph(workingSet);

        assert(originalStartNode instanceof CfgInstructionNode);
        return StructuralAnalysisEngine.createStructure(m_engine, workingSet, (CfgInstructionNode)originalStartNode, "route");
    }

    //TODO clean up the next function
    private List<BackTrails> getFunctionEndBackTrails(BackTrail backTrail) {
        List<BackTrails> endBackTrailsList = new LinkedList<BackTrails>();
        Set<CfgNode> seen = new NonIteratableSet<CfgNode>();
        Set<CfgInstructionNode> endNodes = new IteratableSet<CfgInstructionNode>();
        Set<CfgNode> following = new IteratableSet<CfgNode>();

        following.add(backTrail.peek().getFunctionStartInstruction());
        while(!following.isEmpty()){
            CfgNode current = following.iterator().next();
            seen.add(current);
            if(current.getNextNodes().size() == 0){
                if(current instanceof CfgInstructionNode){
                    endNodes.add((CfgInstructionNode)current);
                } else {
                    assert(false);
                }
            } else {
                for(CfgNode next : current.getNextNodes()){
                    if(!seen.contains(next)){
                        following.add(next);
                    }
                }
            }
            following.remove(current);
        }
        for(CfgInstructionNode endInst : endNodes){
            List<BackTrail> backTrails = new LinkedList<BackTrail>();
            backTrails.add(backTrail);
            BackTrails end = new BackTrails(endInst, backTrails);
            endBackTrailsList.add(end);
        }
        return endBackTrailsList;
    }

    private void removeExclusionBranches(Set<CfgNode> workingSet, Set<CfgNode> currentExclusions) {
        while(!currentExclusions.isEmpty()) {
            CfgNode current = currentExclusions.iterator().next();
            currentExclusions.remove(current);
            workingSet.remove(current);
            Set<CfgNode> prevNodes = new IteratableSet<CfgNode>(current.getPrevNodes());
            for(CfgNode prev : prevNodes) {
                prev.disconnectNext(current);
                if(prev.getNextNodes().isEmpty())
                    currentExclusions.add(prev);
            }
        }
    }

    private CfgNode containsNode(CfgNode next, BackTrail currentBackTrail,
            Map<NodeDescriptor, CfgNode> nodeToWorkingSet){

        NodeDescriptor nd = new NodeDescriptor(next, currentBackTrail);
        return nodeToWorkingSet.get(nd);
    }

    private CfgNode getCurrentNode(BackTrail currentBackTrail, CfgNode node, Map<NodeDescriptor, CfgNode> nodeToWorkingSet) throws AbortedException {

        NodeDescriptor nd = new NodeDescriptor(node, currentBackTrail);
        if (nodeToWorkingSet.containsKey(nd))
            return nodeToWorkingSet.get(nd);

        assert((node instanceof CfgInstructionNode) ||
               (node instanceof CfgFunctionReferenceNode));
        CfgNode copy = node.copy();    // RL-TODO Why do we want a copy? Do we want to keep the edges?
        nodeToWorkingSet.put(nd, copy);
        return copy;
    }

   private boolean exclusionBackTrailsListContainNode(BackTrail currentBackTrail, CfgNode node) {
       return backTrailsContainNode(m_exclusionBackTrailsList, currentBackTrail, node);
   }
    
    private boolean backTrailsContainNode(List<BackTrails> backTrailsList, BackTrail currentBackTrail, CfgNode currentNode){
        if (!(currentNode instanceof CfgInstructionNode))
            return false;
        for (BackTrails backTrails : backTrailsList) {
            if (backTrails.getInstruction().equals(currentNode)) {
                for (BackTrail backTrail : backTrails) {
                    if (backTrail.equals(currentBackTrail)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void addToWorkingSet(CfgNode parent, CfgNode child, Set<CfgNode> workingSet) {
        if (parent!= null) {
            parent.connectNext(child);
        }
        workingSet.add(child);
    }

    private void dumpGraph(Set<CfgNode> workingSet) {
        if (!debug_route) return;
        System.out.print("digraph g {");
        for (CfgNode node : workingSet) {
            if (node instanceof CfgInstructionNode){
                System.out.print("node" +node.getId() + " [label=\"" + ((CfgInstructionNode)node).getInstruction() + "\"];");
            } else {
                System.out.print("node" +node.getId() + " [label=\"" + node + "\"];");
            }
            for(CfgNode next : node.getNextNodes()){
                System.out.print("node" +node.getId() + " -> node" + next.getId() + ";");
                if(!workingSet.contains(next)){
                    System.out.print("node" +next.getId() + " [label=\"" + next + "\", style=filled, color=blue];");
                }
            }
            for(CfgNode prev : node.getPrevNodes()){
                System.out.print("node" +prev.getId() + " -> node" + node.getId() + "[style=dotted];");
                if(!workingSet.contains(prev)){
                    System.out.print("node" +prev.getId() + " [label=\"" + prev + "\", style=filled, color=red];");
                }
            }
        }
        System.out.println("}");
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
                if (node.getAttributes(false).hasUnknowns()) {
                    setDisplayAsUnknownOnParentNodes(node);
                }
                return TraverseStatus.CONTINUE;
            }
        },
        null  // No clientState
        );

    }

    private void setDisplayAsUnknownOnParentNodes(Node node) {
        Node parent = node;
        while (parent != null) {
            if(parent.getAttributes(false).getDisplayAsUnknown() == false){
                parent.getAttributes(true).setDisplayAsUnknown(true);
            }
            parent.getAttributes(true).setDisplayAsUnknown(true);
            parent = parent.getParent();
        }
    }

    private boolean isNodeContainedInbranchBackTrailsList(CfgNode node) {
        if (node instanceof CfgInstructionNode) {
            CfgInstructionNode instructionNode = (CfgInstructionNode)node;
            for (BackTrails backTrail : m_branchBackTrailsList) {
                if (backTrail.getInstruction().getPc() == instructionNode.getPc()) {
                    return true;
                }
            }
        }
        return false;
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
