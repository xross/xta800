package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.core.controlflow.ControlFlow;
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
import com.xmos.libs.core.xeloader.XeLoader.Architecture;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.CommandSource.Type;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer.RouteMode;
import com.xmos.xta.core.engine.analysis.structural.CachedFunctionMap;
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

    private RouteCoreContainer m_coreContainer;         // We will be adding to m_coreContainer.m_routes for this core.
    private ControlFlow m_controlFlow;                  // Made during the load phase.
    private CachedFunctionMap m_cachedFunctionMap;      // Keep a copy of a previous function results for this binary.
    private Architecture m_arch;
    StructuralAnalysisEngine m_sae;
    

    public static List<BackTrails> getPathBackTrails(ControlFlow controlFlow,
            CachedFunctionMap cachedFunctionMap, Reference reference)
            throws AbortedException, MemoryMonitorException {

        ControlFlowReferenceMatcher cfrm = new ControlFlowReferenceMatcher(controlFlow, cachedFunctionMap);
        return cfrm.matchReference(reference);
    }

    public static void analyze(CommandSource src, RouteCoreContainer routeCoreContainer, CoreId core, Engine engine)  throws Exception {
        // The routeCoreContainer hold information we will need.
        // We will be adding the routeCoreContainer.List<Route>
        RouteConstructor routeConstructor = new RouteConstructor(engine, core, routeCoreContainer);
        routeConstructor.analyze(engine, src, core);
    }

    static void setParent(Node node) {
        for (Node child : node.iChildren()){
            child.setParent(node);
            setParent(child);
        }
    }

    private RouteConstructor(Engine engine, CoreId core, RouteCoreContainer routeCoreContainer) throws Exception {
        m_coreContainer = routeCoreContainer;
        m_controlFlow = engine.getControlFlow(core);
        m_cachedFunctionMap = engine.getCachedFunctionMap(core);
        m_arch = engine.getXeLoader().getArchitecture();
        m_sae = new StructuralAnalysisEngine(engine);
    }

    private void analyze(Engine engine, CommandSource src, CoreId core) throws Exception {
        engine.provideFeedback("Starting analysis...");

        Map<BackTrails, Set<CfgNode>> branchToNext = new IteratableMap<BackTrails, Set<CfgNode>>();
        List<BackTrails> branchBackTrailsList = buildbranchBackTrailsList(engine, branchToNext);
        List<BackTrails> exclusionBackTrailsList = buildexclusionBackTrailsList(engine, core);
        
        switch (m_coreContainer.parentRouteContainer().getMode()) {
        case FUNCTION:
            analyzeFunction(src, branchBackTrailsList, branchToNext, exclusionBackTrailsList);
            break;

        case LOOP:
            analyzeLoop(src, branchBackTrailsList, branchToNext, exclusionBackTrailsList);
            break;

        case ENDPOINTS:
            analyzeEndpoints(src, branchBackTrailsList, branchToNext, exclusionBackTrailsList);
            break;

        default:
            assert(false);
        }

        InstructionIssueAnayser(engine);
        FnopAdder(engine);
        setUnknowns(engine);
        setSrcLoopCounts(engine);
        checkSupportedInstructions(engine, src);
        applyDefines(engine, src);

        engine.provideFeedback("Analysis complete");
    }

    private void analyzeFunction(CommandSource src, List<BackTrails> branchBackTrailsList,
            Map<BackTrails, Set<CfgNode>> branchToNext, List<BackTrails> exclusionBackTrailsList)
        throws AbortedException, MemoryMonitorException {

        List<BackTrails> startBackTrailsList = getPathBackTrails(
                m_controlFlow, m_cachedFunctionMap, m_coreContainer.getFromReference());

        for (BackTrails backTrails : startBackTrailsList) {
            for (BackTrail backTrail : backTrails.getBackTrails()) {

                FunctionNode newFunction = makeFunction(backTrail, exclusionBackTrailsList,
                        branchBackTrailsList, true, branchToNext).first;

                updateRoutes(src, newFunction);
            }
        }
    }

    private void analyzeLoop(CommandSource src, List<BackTrails> branchBackTrailsList,
            Map<BackTrails, Set<CfgNode>> branchToNext, List<BackTrails> exclusionBackTrailsList)
        throws AbortedException, MemoryMonitorException {

        List<BackTrails> loopstartBackTrailsList = new ArrayList<BackTrails>();

        RouteLoopConversion converter = new RouteLoopConversion(m_controlFlow, m_cachedFunctionMap);
        converter.updateLoopBackTrails(m_coreContainer.getFromReference(), loopstartBackTrailsList, exclusionBackTrailsList);

        analyzePath(src, branchToNext, branchBackTrailsList,
                exclusionBackTrailsList, loopstartBackTrailsList, loopstartBackTrailsList);
    }

    private void analyzeEndpoints(CommandSource src, List<BackTrails> branchBackTrailsList,
            Map<BackTrails, Set<CfgNode>> branchToNext, List<BackTrails> exclusionBackTrailsList)
        throws AbortedException, MemoryMonitorException {

        List<BackTrails> startBackTrailsList = getPathBackTrails(
                m_controlFlow, m_cachedFunctionMap, m_coreContainer.getFromReference());

        List<BackTrails> endBackTrailsList = new LinkedList<BackTrails>();
        for (Reference end : m_coreContainer.getToReferences()) {
            endBackTrailsList.addAll(getPathBackTrails(m_controlFlow, m_cachedFunctionMap, end));
        }

        analyzePath(src, branchToNext, branchBackTrailsList,
            exclusionBackTrailsList, startBackTrailsList, endBackTrailsList);
    }

    private void analyzePath(CommandSource src,
            Map<BackTrails, Set<CfgNode>> branchToNext,
            List<BackTrails> branchBackTrailsList,
            List<BackTrails> exclusionBackTrailsList,
            List<BackTrails> startBackTrailsList,
            List<BackTrails> endBackTrailsList) throws AbortedException, MemoryMonitorException {

        for (BackTrails backTrails : startBackTrailsList) {
            for (BackTrail backTrail : backTrails.getBackTrails()) {

                Node node = makeEndpoints(backTrail, backTrails.getInstruction(),
                        endBackTrailsList, exclusionBackTrailsList, branchBackTrailsList, branchToNext);

                updateRoutes(src, node);
            }
        }
    }

    private void updateRoutes(CommandSource src, Node node) {
        if (node != null) {
            setParent(node);
            if (!alreadyContainRoute(node)) {
                Route route = new Route(src, m_arch, node, m_coreContainer);
                m_coreContainer.add(route);
            }
        }
    }

    private List<BackTrails> buildbranchBackTrailsList(Engine engine, Map<BackTrails, Set<CfgNode>> branchToNext) throws AbortedException, MemoryMonitorException {
       List<BackTrails> branchBackTrailsList = new LinkedList<BackTrails>();
        for(Reference ref : engine.getUserBranches().keySet()){
            List<BackTrails> backTrailsList = getPathBackTrails(m_controlFlow, m_cachedFunctionMap, ref);
            branchBackTrailsList.addAll(backTrailsList);
            for(BackTrails backTrails : backTrailsList){
                branchToNext.put(backTrails, new IteratableSet<CfgNode>());

                for(Reference nextRef : engine.getUserBranches().get(ref)){
                    for(BackTrails nextBackTrails : getPathBackTrails(m_controlFlow, m_cachedFunctionMap, nextRef)){
                        if(backTrails.getInstruction().getInstruction().isLink()){
                            for(CfgNode nextFrn : backTrails.getInstruction().getNextNodes()){
                                if(nextFrn instanceof CfgFunctionReferenceNode){
                                    CfgFunctionReferenceNode frn = (CfgFunctionReferenceNode)nextFrn;
                                    if(frn.getFunctionStartInstruction() == nextBackTrails.getInstruction()){
                                        branchToNext.get(backTrails).add(frn);
                                    }
                                }
                            }
                        } else {
                            branchToNext.get(backTrails).add(nextBackTrails.getInstruction());
                        }
                    }
                }
            }
        }
        return branchBackTrailsList;
    }

    private List<BackTrails> buildexclusionBackTrailsList(Engine engine, CoreId core) throws AbortedException, MemoryMonitorException {
        List<BackTrails> backTrailsList = new LinkedList<BackTrails>();
        for (ExclusionInfo info : engine.getExclusions()) {
            Reference ref = info.getReference();
            if (ref.getCoreId().equals(core))
                backTrailsList.addAll(getPathBackTrails(m_controlFlow, m_cachedFunctionMap, ref));
        }
        return backTrailsList;
    }

    private Pair<FunctionNode, Boolean> makeFunction(
            BackTrail currentBackTrail,
            List<BackTrails> exclusionBackTrailsList,
            List<BackTrails> branchBackTrailsList, boolean start,
            Map<BackTrails, Set<CfgNode>> branchToNext)
        throws AbortedException, MemoryMonitorException {

        List<BackTrails> endBackTrailsList = getFunctionEndBackTrails(currentBackTrail);

        CfgFunctionReferenceNode fnRef = currentBackTrail.peek();

        if (!fnRef.isKnownFunction()) {
            return new Pair<FunctionNode, Boolean>(new FunctionNode(), false);
        }
        CfgInstructionNode startInst = fnRef.getFunctionStartInstruction();

        Set<CfgNode> workingSet = new IteratableSet<CfgNode>();
        Set<CfgNode> currentExclusionsFound = new IteratableSet<CfgNode>();
        Map<NodeDescriptor, CfgNode> nodeToWorkingSet = new IteratableMap<NodeDescriptor, CfgNode>();

        NodeDescriptor startNodeDescriptor = new NodeDescriptor(startInst, currentBackTrail);


        findReachableGraph(null, currentBackTrail, startInst, workingSet,
                exclusionBackTrailsList, currentExclusionsFound, RouteMode.FUNCTION,
                endBackTrailsList, nodeToWorkingSet, start, branchBackTrailsList, branchToNext);

        removeExclusionBranches(workingSet, currentExclusionsFound);

        if (workingSet.isEmpty()) {
            return new Pair<FunctionNode, Boolean>(null, false);
        }

        //if working set contains an end node then we will continue else we wont
        boolean continueAfterFunction = true;

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

        return new Pair<FunctionNode, Boolean>(
                new FunctionNode(node,
                    fnRef.getFunctionName(),
                    fnRef.getFunctionCallPc()),
                    continueAfterFunction);
    }

    private Node makeEndpoints(BackTrail currentBackTrail,
            CfgInstructionNode startInst,
            List<BackTrails> endBackTrails,
            List<BackTrails> exclusionBackTrailsList,
            List<BackTrails> branchBackTrailsList,
            Map<BackTrails, Set<CfgNode>> branchToNext)
        throws AbortedException, MemoryMonitorException {

        Set<CfgNode> workingSet = new IteratableSet<CfgNode>();
        Set<CfgNode> currentExclusionsFound = new IteratableSet<CfgNode>();

        Map<NodeDescriptor, CfgNode> nodeToWorkingSet = new IteratableMap<NodeDescriptor, CfgNode>();
        NodeDescriptor startNodeDescriptor = new NodeDescriptor(startInst, currentBackTrail);
        findReachableGraph(null, currentBackTrail, startInst, workingSet,
                exclusionBackTrailsList, currentExclusionsFound,
                RouteMode.ENDPOINTS, endBackTrails, nodeToWorkingSet, true, branchBackTrailsList, branchToNext);
        removeExclusionBranches(workingSet, currentExclusionsFound);

        if(workingSet.isEmpty()){
            return null;
        }
        Node node = convertGraphToStructure(workingSet, startNodeDescriptor, nodeToWorkingSet);

        return node;
    }


    /**
     * Return true if the current node is a valid exclusion. The current node is a valid exclusion if
     * it is in the set of excluded BackTrail nodes and is not the first instruction to be explored.
     * @param firstInstruction
     */
    private boolean isCurrentNodeAnExcludedNode(
            List<BackTrails> exclusionBackTrailsList,
            BackTrail currentBackTrail,
            CfgNode node, boolean firstInstruction) {

        return (backTrailsContainNode(exclusionBackTrailsList, currentBackTrail, node)&& !firstInstruction);
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
            List<BackTrails> exclusionBackTrailsList,
            Set<CfgNode> currentExclusionsFound,
            RouteMode routeMode,
            List<BackTrails> endBackTrailsList,
            Map<NodeDescriptor, CfgNode> nodeToWorkingSet,
            boolean firstInstruction,
            List<BackTrails> branchBackTrailsList,
            Map<BackTrails, Set<CfgNode>> branchToNext)
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
                            exclusionBackTrailsList, currentExclusionsFound, routeMode,
                            endBackTrailsList, nodeToWorkingSet, false, branchBackTrailsList, branchToNext);
                    return;
                } else {

                    if (frn.isKnownFunction()){
                        Pair<FunctionNode, Boolean> fnReturn = makeFunction(newBackTrail, exclusionBackTrailsList, branchBackTrailsList, false, branchToNext);
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


            if(isCurrentNodeAnExcludedNode(exclusionBackTrailsList, currentBackTrail, node, firstInstruction)){

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
            Set<CfgNode> nextNodes = getValidBranches(branchBackTrailsList, currentBackTrail, node, branchToNext);

            if (nextNodes.size() == 0) {
                handelEndOfFunction(previous, currentBackTrail, workingSet,
                        exclusionBackTrailsList, currentExclusionsFound, routeMode,
                        endBackTrailsList, nodeToWorkingSet,
                        branchBackTrailsList, branchToNext, currentNode);
            } else if (nextNodes.size() == 1) {

                CfgNode next = nextNodes.iterator().next();
                node = next;
                previous = currentNode;


                CfgNode nextWorkingSetNode = containsNode(next, currentBackTrail, nodeToWorkingSet);
                if (nextWorkingSetNode == null) {
                    repeat = true;
                } else {
                    handleNodeAlreadyInWorkingSet(routeMode, exclusionBackTrailsList,
                            endBackTrailsList, currentBackTrail, workingSet, currentNode,
                            next, nextWorkingSetNode);

                    //this is here to allow you to exclude the start instruction
                    if (backTrailsContainNode(exclusionBackTrailsList, currentBackTrail, next)) {
                        currentExclusionsFound.add(currentNode);
                    }

                }
            } else if (nextNodes.size() > 1) {
                handleBranch(currentBackTrail, workingSet, exclusionBackTrailsList,
                        currentExclusionsFound, routeMode, endBackTrailsList,
                        nodeToWorkingSet,
                        branchBackTrailsList, branchToNext, currentNode, nextNodes);
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
            for(BackTrail backTrail : backTrails.getBackTrails()){
                if(backTrail.contains(node)){
                    return true;
                }
            }
        }
        return false;
    }



    private void handleBranch(BackTrail currentBackTrail,Set<CfgNode> workingSet,
            List<BackTrails> exclusionBackTrailsList, Set<CfgNode> currentExclusionsFound, RouteMode routeMode,
            List<BackTrails> endBackTrailsList, Map<NodeDescriptor, CfgNode> nodeToWorkingSet,
            List<BackTrails> branchBackTrailsList,
            Map<BackTrails, Set<CfgNode>> branchToNext, CfgNode currentNode,
            Set<CfgNode> nextNodes) throws AbortedException,
            MemoryMonitorException {

        for (CfgNode next : nextNodes) {
            CfgNode nextWorkingSetNode = containsNode(next, currentBackTrail, nodeToWorkingSet);

            if (nextWorkingSetNode == null) {
                findReachableGraph(currentNode, currentBackTrail, next, workingSet,
                        exclusionBackTrailsList, currentExclusionsFound, routeMode, endBackTrailsList,
                        nodeToWorkingSet, false, branchBackTrailsList, branchToNext);
            } else {
                handleNodeAlreadyInWorkingSet(routeMode, exclusionBackTrailsList,
                        endBackTrailsList, currentBackTrail, workingSet, currentNode,
                        next, nextWorkingSetNode);
            }
        }
    }

    private void handelEndOfFunction(CfgNode previous,
            BackTrail currentBackTrail,
            Set<CfgNode> workingSet, List<BackTrails> exclusionBackTrailsList,
            Set<CfgNode> currentExclusionsFound, RouteMode routeMode,
            List<BackTrails> endBackTrailsList,
            Map<NodeDescriptor, CfgNode> nodeToWorkingSet,
            List<BackTrails> branchBackTrailsList,
            Map<BackTrails, Set<CfgNode>> branchToNext, CfgNode currentNode)
            throws AbortedException, MemoryMonitorException {



        if (isNodeContainedInbranchBackTrailsList(currentNode, branchBackTrailsList)) {
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
                            exclusionBackTrailsList, currentExclusionsFound, routeMode, endBackTrailsList,
                            nodeToWorkingSet, false, branchBackTrailsList, branchToNext);
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
            List<BackTrails> exclusionBackTrailsList,
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

        boolean nextNodeIsAnExcludedNode = backTrailsContainNode(exclusionBackTrailsList, currentBackTrail, nextNode);
        if ( !nextNodeIsAnExcludedNode) {
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

    private Set<CfgNode> getValidBranches(List<BackTrails> branchBackTrailsList,
            BackTrail currentBackTrail, CfgNode node,
            Map<BackTrails, Set<CfgNode>> branchToNext) {

        Set<CfgNode> validNextNodes = new IteratableSet<CfgNode>();

        validNextNodes.addAll(node.getNextNodes());

        for (BackTrails backTrails : branchBackTrailsList){
            if (!backTrails.contains(currentBackTrail)){
                validNextNodes.removeAll(branchToNext.get(backTrails));
            }
        }
        return validNextNodes;
    }

    private boolean alreadyContainRoute(Node newGraph) {
        final List<String> newNodeList = createRouteDescription(newGraph);

        for (Route route : m_coreContainer){
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
        return m_sae.createStructure(workingSet, (CfgInstructionNode)originalStartNode, "route");
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

    private CfgNode getCurrentNode(BackTrail currentBackTrail,
            CfgNode node, Map<NodeDescriptor, CfgNode> nodeToWorkingSet)
        throws AbortedException {

        NodeDescriptor nd = new NodeDescriptor(node, currentBackTrail);
        if (nodeToWorkingSet.containsKey(nd)){
            return nodeToWorkingSet.get(nd);

        } else {
            assert((node instanceof CfgInstructionNode) ||
                   (node instanceof CfgFunctionReferenceNode));

            CfgNode copy = node.copy();    // RL-TODO Why do we want a copy? Do we want to keep the edges?
            nodeToWorkingSet.put(nd, copy);
            return copy;
        }
    }

    private boolean backTrailsContainNode(List<BackTrails> backTrailsList,
            BackTrail currentBackTrail, CfgNode currentNode){

        if (!(currentNode instanceof CfgInstructionNode)) {
            return false;
        }
        for (BackTrails backTrails : backTrailsList) {
            if (backTrails.getInstruction().equals(currentNode)) {
                for (BackTrail backTrail : backTrails.getBackTrails()) {
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

    private void InstructionIssueAnayser(Engine engine) throws AbortedException {
        engine.provideFeedback("We only handle single issue routes - skip InstructionIssueAnayser.");
        engine.getAbort();
    }

    private void FnopAdder(Engine engine) throws AbortedException {
        engine.provideFeedback("Adding fnops to routes...");
        for (final Route route : m_coreContainer) {
            engine.getAbort();
            Node node = route.getNode();
            long startPc = node.getStartPc();

            List<CfgInstructionNode> cfgs = new LinkedList<CfgInstructionNode>();
            for (FunctionControlFlow fcf : m_controlFlow.getFunctionContaining(startPc))
                cfgs.add(fcf.getInstructionNode(startPc));

            InstructionBuffer instructionBuffer = new InstructionBuffer(m_arch, startPc, cfgs);
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

    private void setUnknowns(Engine engine) throws AbortedException {
        engine.provideFeedback("Setting unknowns on routes...");

        for (final Route route : m_coreContainer) {
            engine.getAbort();
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

    private void setSrcLoopCounts(Engine engine) throws AbortedException {
        CoreId coreId = m_coreContainer.getCoreId();
        // Sets the loop iteration counts specified at compile time.
        for (SrcLevelLoopInfo srcLevelLoopInfo : engine.getSrcLevelLoops(coreId)) {
            engine.provideFeedback(String.format("Setting loop count at pc: 0x%x", srcLevelLoopInfo.getPc()));
            engine.getAbort();

            Reference reference = ReferenceFactory.createReference(
                    String.format("0x%x", srcLevelLoopInfo.getPc()),
                    ReferenceClass.INSTRUCTION, coreId);

            CommandSource src = new CommandSource(Type.COMPILER);
            for (Route route : m_coreContainer) {
                try {
                    route.getRouteUnknowns().setLoopIterations(src, engine,
                            reference, srcLevelLoopInfo.getNumIterations(), true, Source.BINARY);
                } catch (Exception e) {}
            }
        }
    }

    private void checkSupportedInstructions(final Engine engine, final CommandSource src) throws AbortedException {
        engine.provideFeedback("Checking for unsupported instructions...");
        final InstructionMnemonics mnenonics = new InstructionMnemonics(m_arch);
        for (Route route : m_coreContainer) {
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

    private void applyDefines(Engine engine, CommandSource src) throws AbortedException {
        engine.provideFeedback("Applying defines to route...");
        engine.getAbort();

        RouteDefines defines = new RouteDefines();
        for (Route route : m_coreContainer) {
            for (FunctionTimeInfo info : engine.getFunctionTimeDefines()) {
                if (coreMatch(route, info.getFunction())) {
                    defines.applyFunctionDefine(src, route, info, Source.ADD);
                }
            }
            for (PathTimeInfo info : engine.getPathTimeDefines()) {
                if (coreMatch(route, info.getFrom()) &&
                    coreMatch(route, info.getTo())) {
                    defines.applyPathDefine(engine, src, route, info, Source.ADD);
                }
            }
        }
        for (LoopInfo info : engine.getLoopDefines()) {
            try {
                for (Route route : m_coreContainer) {
                    if (coreMatch(route, info.getReference())) {
                        route.getRouteUnknowns().checkSetLoopIterations(
                            info.getReference(), info.getIterations(), engine, src);
                    }
                }
                for (Route route : m_coreContainer) {
                    if (coreMatch(route, info.getReference())) {
                        route.getRouteUnknowns().setLoopIterations(src, engine,
                            info.getReference(), info.getIterations(), false, Source.ADD);
                    }
                }
            } catch (Exception e) {}
        }
        for (LoopPathInfo info : engine.getLoopPathDefines()) {
            try {
                for (Route route : m_coreContainer) {
                    if (coreMatch(route, info.getReference())) {
                        route.getRouteUnknowns().checkSetLoopPathIterations(
                            info.getReference(), info.getIterations(), engine, src);
                    }
                }
                for (Route route : m_coreContainer) {
                    if (coreMatch(route, info.getReference())) {
                        route.getRouteUnknowns().setLoopPathIterations(
                            info.getReference(), info.getIterations(), Source.ADD);
                    }
                }
            } catch (Exception e) {}
        }
        for (LoopScopeInfo info : engine.getLoopScopeDefines()) {
            try {
                for (Route route : m_coreContainer) {
                    if (coreMatch(route, info.getReference())) {
                        route.getRouteUnknowns().checkSetLoopScope(
                            info.getReference(), info.getAbsolute(), engine, src);
                    }
                }
                for (Route route : m_coreContainer) {
                    if (coreMatch(route, info.getReference())) {
                        route.getRouteUnknowns().setLoopScope(
                            info.getReference(), info.getAbsolute(), Source.ADD);
                    }
                }
            } catch (Exception e) {}
        }
        for (InstructionTimeInfo info : engine.getInstructionTimeDefines()) {
            try {
                for (Route route : m_coreContainer) {
                    if (coreMatch(route, info.getReference())) {
                        if (!route.getRouteUnknowns().canInstructionPause(info.getReference()))
                            throw new Exception();
                    }
                }
                for (Route route : m_coreContainer) {
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

    private boolean isNodeContainedInbranchBackTrailsList(CfgNode node, List<BackTrails> branchBackTrailsList) {
        if (node instanceof CfgInstructionNode) {
            CfgInstructionNode instructionNode = (CfgInstructionNode)node;
            for (BackTrails backTrail : branchBackTrailsList) {
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
