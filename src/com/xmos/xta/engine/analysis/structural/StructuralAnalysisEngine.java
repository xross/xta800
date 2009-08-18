package com.xmos.xta.engine.analysis.structural;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.xeloader.XeLoader;
import com.xmos.xeloader.XeLoader.Architecture;
import com.xmos.xta.engine.CommandSource;
import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.analysis.controlflow.ControlFlowGraph;
import com.xmos.xta.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.NullNode;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.InstructionNodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.engine.analysis.structural.patterns.LoopPatternMatcher;
import com.xmos.xta.engine.analysis.structural.patterns.SequencePatternMatcher;
import com.xmos.xta.engine.analysis.structural.patterns.PatternMatcher;
import com.xmos.xta.engine.analysis.structural.patterns.ConditionalPatternMatcher;
import com.xmos.xta.engine.analysis.structural.patterns.ConditionalTransformer;
import com.xmos.xta.engine.analysis.structural.patterns.SelfLoopPattern;
import com.xmos.xta.engine.instructions.InstructionMnemonics;
import com.xmos.xta.engine.instructions.XtaInstruction;
import com.xmos.xta.engine.sections.Sections;
import com.xmos.xta.engine.sections.SrcStringInfo;

public class StructuralAnalysisEngine {
    private static final boolean DEBUG = false;
	private static final boolean SYMBOLIC_LINK = false;
	
	private ControlFlowGraph m_controlFlowGraph;
	private Map<Long, FunctionNode> m_functionStartPcs;
	private StructureGraph m_structureGraph;
	private List<PatternMatcher> m_patternMatchers;
	private Architecture m_arch;
	
	public StructuralAnalysisEngine(ControlFlowGraph controlFlowGraph, XeLoader xeLoader) {
		m_controlFlowGraph = controlFlowGraph;
		m_arch = xeLoader.getArchitecture();
		setUpPatternMatcher();
	}

	public StructuralAnalysisEngine() {
		setUpPatternMatcher();
	}

	public boolean analyse(CommandSource src, Engine engine, TreeMap<String, LinkedList<InstructionNode>> treeMap, 
			Map<Long, TreeSet<Node>> pcToEndPoints, Sections sections, int nodeId, int coreId) {
		m_functionStartPcs = new TreeMap<Long, FunctionNode>();
		return buildStructureGraphs(src, engine, treeMap, pcToEndPoints, sections, nodeId, coreId);
	}

	public StructureGraph getStructureGraph() {
		return m_structureGraph;
	}

	public boolean createStructure(Collection<Node> workingSet, Node treeStart, Engine engine, boolean debug, String jobPrefix) {
		
		if(workingSet.size() == 1 && workingSet.iterator().next().getNextNodesSize() == 0) return true;
		buildDominatorTree(workingSet, treeStart);
//		System.out.println(dumpTree("tree", workingSet));
		
		boolean working = true;
		boolean dominatorTreeUpToDate = false;
		while (working) {
			engine.provideFeedback(jobPrefix + " - nodes left: " + workingSet.size());
			List<Node> postOrder = new LinkedList<Node>();
			DFSPostOrder(postOrder, treeStart, new TreeSet<Node>());
			working = false;

			for (PatternMatcher pm : m_patternMatchers) {
				if (!dominatorTreeUpToDate && pm.usesDominatorTree()) {
					buildDominatorTree(workingSet, treeStart);
					dominatorTreeUpToDate = true;
				}
				if (pm instanceof ConditionalTransformer) {
					Collections.reverse(postOrder);
				}
				for (Node node : postOrder) {
					if (engine.getAbort()) {
						return false;
					}
					if (pm.isMatch(node, workingSet, treeStart)) {
//						System.out.println(dumpTree(pm.getClass().getSimpleName(), workingSet));
						treeStart = pm.getTreeStart();
						assert (connectivitySanityCheck(workingSet));
						dominatorTreeUpToDate = false;
						working = true;
						break;
					}
				}
				if (working)
					break;
			}
		}
		
		return true;
	}

	private boolean buildStructureGraphs(CommandSource src, Engine engine, TreeMap<String,
			LinkedList<InstructionNode>> treeMap, Map<Long, TreeSet<Node>> pcToEndPoints,
			Sections sections, int nodeId, int coreId) {
		
		Map<FunctionNode, TreeSet<FunctionNode>> allFunctionObjects = new TreeMap<FunctionNode, TreeSet<FunctionNode>>();
		
		Collection<FunctionNode> threads = buildGraph(m_controlFlowGraph.getThreadStartInstructions(), 
				m_functionStartPcs, engine, false, treeMap, allFunctionObjects);
		if (threads == null) {
			return false;
		}
		
		Collection<FunctionNode> functions = buildGraph(m_controlFlowGraph.getFunctionStartInstructions(), 
				m_functionStartPcs, engine, true, treeMap, allFunctionObjects);
		if (functions == null) {
			return false;
		}
		
		m_structureGraph = new StructureGraph(threads, functions, pcToEndPoints);
		
		removeNoLoad(engine, allFunctionObjects, sections.getNoLoads(nodeId, coreId),
				sections.getUnreachableFunctions(nodeId, coreId));

		engine.provideFeedback("Analysing for recursion...");
		detectRecursion(engine, allFunctionObjects, sections.getRecursiveFunctions(nodeId, coreId));
		
		engine.provideFeedback("Linking functions...");
		linkFunctions(engine, allFunctionObjects);
		
		if (!allFunctionObjects.isEmpty()) {
			String warning = "node " + nodeId + ", core " + coreId + ": functions (";
			boolean first = true;
			for (FunctionNode fn : allFunctionObjects.keySet()) {
				if (!first) {
					warning += ",";
				}
				first = false;

				warning += " " + fn.getFunctionName();
			}
			warning += " ) could not be reached. Check branch tables and indirect calls.";
		
			engine.notify(src, Notifier.NOTIFY_WARNING, warning);
		}
		
		engine.provideFeedback("Setting parent nodes...");
		setParents();

		engine.provideFeedback("Registering config...");
		setConfig(engine.getConfig(), m_structureGraph.getThreads());
		setConfig(engine.getConfig(), m_structureGraph.getFunctions());
		setConfig(engine.getConfig(), m_structureGraph.getUnlinkedFunctions());
		
		engine.provideFeedback("Complete");
		return true;
	}

	private Collection<FunctionNode> buildGraph(Collection<InstructionNode> graphStartNodes, Map<Long, FunctionNode> startPcs,
			Engine engine, boolean isFunction, TreeMap<String, LinkedList<InstructionNode>> treeMap,
			Map<FunctionNode, TreeSet<FunctionNode>> allFunctionObjects) {

		Collection<FunctionNode> graphs = new TreeSet<FunctionNode>();

		List<Node> orderedGraphs = new LinkedList<Node>(graphStartNodes);
		Collections.sort(orderedGraphs);
		
		if(isFunction){
			engine.startAnalysis("Analysing functions", orderedGraphs.size());
		} else {
			engine.startAnalysis("Analysing threads", orderedGraphs.size());
		}
		
		for (Node graphStart : orderedGraphs) {

			String functionName = getFunctionName(graphStart, treeMap);
			String jobName = "Processing " + functionName;
			//There are the only two sets that need to be ordered as they determine the number of the blocks
			Collection<Node> currentGraph = new TreeSet<Node>();
			Collection<Node> basicBlocks = new TreeSet<Node>();
			
			collectCurrentGraphNodes(graphStart, currentGraph);

			Node initialBasicBlock = reduceGraphToBasicBlocks(basicBlocks, graphStart, currentGraph);
			
			if (DEBUG) System.out.println(dumpTree(functionName, basicBlocks));

			long initialPc = graphStart.getStartPc();
			
			assert(!basicBlocks.isEmpty());
			FnopAnalyser fa = new FnopAnalyser();
			
			fa.analyse(initialBasicBlock, isFunction);
			fa.addFnopsToBasicBlocks(initialBasicBlock);
			assert(basicBlocks.contains(initialBasicBlock));

			if (!createStructure(basicBlocks, initialBasicBlock, engine, false, jobName)) {
				return null;
			}
			
			if (basicBlocks.size() != 1 || initialPc != basicBlocks.iterator().next().getStartPc()) {
				System.out.println(dumpTree(functionName + "_after", basicBlocks));
			}
			assert(basicBlocks.size() == 1);
			Node decomposedGraph = basicBlocks.iterator().next();

			final FunctionNode fn = new FunctionNode(decomposedGraph, functionName);
			
			final TreeSet<FunctionNode> unlinkedFunctions = new TreeSet<FunctionNode>();
			
			fn.traverse(0, new DefaultNodeTraverser() {
	   		    public boolean foundFunctionNode(Node node, int depth) {
	   		    	if(node != fn){
	   		    		unlinkedFunctions.add((FunctionNode)node);
	   		    	}
	   		    	return true;
	   		    }   			
	   		});

			allFunctionObjects.put(fn, unlinkedFunctions);

			graphs.add(fn);
			
			setParent(fn);
			
			normaliseGraph(fn);
			
			startPcs.put(graphStart.getStartPc(), fn);
			engine.provideWorkDone(1);
		}
		
		return graphs;
	}

	private boolean done;
	public void normaliseGraph(Node fn) {
		done = false;
		//this bit removes the path within paths 
		while(!done){
			done =true;
			fn.traverse(0,  new DefaultNodeTraverser() {
				public boolean foundSequenceNode(Node node, int depth) {
					for(Node child : node.getChildren()){
						if(child.isSequenceNode()){
							int insertPathAt =node.getChildren().indexOf(child);
							node.getChildren().remove(insertPathAt);
							for(Node pathChild : child.getChildren()){
								node.getChildren().add(insertPathAt++, pathChild);
							}
							done = false;
							return false;
						}
					}
					return true;
				}
				
				public boolean foundFunctionNode(Node node, int depth) {
					for(Node child : node.getChildren()){
						if(child.isSequenceNode()){
							int insertPathAt =node.getChildren().indexOf(child);
							node.getChildren().remove(insertPathAt);
							for(Node pathChild : child.getChildren()){
								node.getChildren().add(insertPathAt++, pathChild);
							}
							done = false;
							return false;
						}
					}
					return true;
				}
		    });
			setParent(fn);
		}
		
	}

	private String getFunctionName(Node graphStart,
			TreeMap<String, LinkedList<InstructionNode>> treeMap) {
		for(String functionName : treeMap.keySet()){
			for(InstructionNode fnInst : treeMap.get(functionName)){
				if(fnInst.getStartPc() == graphStart.getStartPc()){
					return functionName;
				}
			}
		}
		assert(false);
		return null;
	}

	private Node reduceGraphToBasicBlocks(Collection<Node> basicBlocks, Node initialBasicBlock,
			Collection<Node> currentGraph) {

		Set<Node> seen = new TreeSet<Node>();
		Map<Node, Node> cfgToStructure = new TreeMap<Node, Node>();
		Map<Node, Node> endPoint = new TreeMap<Node, Node>();
		Node treeStart = initialBasicBlock;
		for (Node node : currentGraph) {
			if (!seen.contains(node)) {
				if (node.isInstructionNode()) {
					BasicBlockMatcher bbpm = new BasicBlockMatcher();
					if (bbpm.match(node, treeStart, currentGraph)) {
						seen.addAll(bbpm.getBlock().getContainedNodes());
						BlockNode basicBlock = bbpm.getBlock();
						basicBlocks.add(basicBlock);
						treeStart = bbpm.getTreeStart();
						cfgToStructure.put(node, basicBlock);
						endPoint.put(basicBlock, bbpm.getLastBlock());
					}

				} else if(node.isFunctionNode()){
					seen.add(node);
					Node clone = node.shallowCopy();
					basicBlocks.add(clone);
					cfgToStructure.put(node, clone);
					endPoint.put(clone, node);
				} else {
					assert(node instanceof NullNode);
					seen.add(node);
					Node clone = node.shallowCopy();
					basicBlocks.add(clone);
					cfgToStructure.put(node, clone);
					endPoint.put(clone, node);
				}
			}
		}
		
		// now add all the links

		for (Node node : basicBlocks) {
			
			Node lastInstruction = endPoint.get(node);

			for (Node branch : lastInstruction.getBranchTargets()) {
				node.addBranchTarget(cfgToStructure.get(branch));
			}

			for (Node next : lastInstruction.getNextNodes()) {
				Node root = node;
				Node leaf = cfgToStructure.get(next);
				CommonMethods.connect(leaf, root);
			}
		}
		
		for (Node node : basicBlocks) {
			if(haltsProcessor(node)){
				node.setInfinite(true);
			}
		}
		return treeStart;
	}

	private boolean haltsProcessor(Node basicBlock) {
		LinkedList<XtaInstruction> instruction = basicBlock.getInstructions();
		if(instruction.size() > 1){
			InstructionMnemonics im = new InstructionMnemonics(m_arch);
			if(im.isClre(instruction.get(instruction.size()-2)) && im.isWait(instruction.getLast())){
				return true;
			} 
		} 
		return false;
	}

	private void collectCurrentGraphNodes(Node node, Collection<Node> seen) {
		Collection<Node> toDo = new TreeSet<Node>();
		toDo.add(node);
		
		while (toDo.size() > 0) {
			Collection<Node> nextToDo = new TreeSet<Node>();
			for (Node doNode : toDo) {
				seen.add(doNode);
				
				for (Node next : doNode.getNextNodes()) {
					if (!seen.contains(next)) {
						nextToDo.add(next);
					}
				}
			}
			toDo = nextToDo;
		}
	}

	private void buildDominatorTree(Collection<Node> reachableNodes, Node treeStart) {

		for (Node node : reachableNodes) {
			node.clearDominators();
			if (treeStart == node) {
				node.setDominatorSetComplete(false);
				node.addDominator(node);
			} else {
				node.setDominatorSetComplete(true);
			}
		}

		Set<Node> intersection = new TreeSet<Node>();
		boolean stillChanging = true;

		while (stillChanging) {
			stillChanging = false;
			for (Node node : reachableNodes) {
				boolean intersectionIsCompleteSet = true;

				if (treeStart == node)
					continue;

				intersection.clear();

				for (Node pred : node.getPrevNodes()) {
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

	private void DFSPostOrder(List<Node> nodes, Node entry, Set<Node> visited) {
		nodes.add(entry);

		List<Node> next = new LinkedList<Node>(entry.getNextNodes());
		Collections.sort(next);
		for (Node x : next) {
			if (!visited.contains(x)) {
				visited.add(x);
				DFSPostOrder(nodes, x, visited);
			}
		}
	}

	private boolean connectivitySanityCheck(Collection<Node> workingSet) {
		int total = 0;
		for (Node node : workingSet) {
			total += node.getPrevNodesSize() - node.getNextNodesSize();
		}
		return true;
	}

	private void removeNoLoad(Engine engine, Map<FunctionNode, TreeSet<FunctionNode>> allFunctionObjects,
			Collection<SrcStringInfo> noLoads, Collection<String> unreachableFunctions){
		
		if (noLoads.size() == 0)
			return;
		Set<String> noLoadFunctionNames = new TreeSet<String>();
		for (SrcStringInfo noLoad : noLoads) {
			noLoadFunctionNames.add(noLoad.getString());
		}
		
		Map<FunctionNode, TreeSet<FunctionNode>> links = createLinkedFunctions(allFunctionObjects);

		Collection<FunctionNode> functionsToRemove = new TreeSet<FunctionNode>();

		// Detect which functions are now unreachable for all threads 
		Collection<FunctionNode> threadStarts = new TreeSet<FunctionNode>();
		TreeSet<FunctionNode> seen = new TreeSet<FunctionNode>();
		for (InstructionNode threadStart : m_controlFlowGraph.getThreadStartInstructions()) {
			FunctionNode start = m_functionStartPcs.get(threadStart.getStartPc());
			crawlFunctionGraph(start, links, seen);
			threadStarts.add(start);
		}
		findAllUnreachableFunctions(threadStarts, seen, links, noLoadFunctionNames, functionsToRemove);
		
		Set<Long> removedFunctionPcs = new TreeSet<Long>();
		for (FunctionNode fn : functionsToRemove) {
			removedFunctionPcs.add(fn.getStartPc());
			unreachableFunctions.add(fn.getFunctionName());
		}
//		System.out.println("Pruning unreacheable functions: " + unreachableFunctions);
		
		// Remove the functions which are not to be loaded
		for (FunctionNode fn : allFunctionObjects.keySet()) {
			Collection<FunctionNode> toRemove = new TreeSet<FunctionNode>();
			for (FunctionNode fnLink : allFunctionObjects.get(fn)) {
				if (fnLink.getBranchTarget() != null) {
					long startPc = fnLink.getBranchTarget().getStartPc();
					if (removedFunctionPcs.contains(startPc)) {
						toRemove.add(fnLink);
						fnLink.setFunctionName(m_functionStartPcs.get(startPc).getFunctionName() +  " not loaded");
						fnLink.setUnresolved(true);
					}
				}
			}
			allFunctionObjects.get(fn).removeAll(toRemove);
			
			if (removedFunctionPcs.contains(fn.getStartPc())) {
				allFunctionObjects.get(fn).clear();
			}
		}
	}

	private void findAllUnreachableFunctions(Collection<FunctionNode> threadStarts, TreeSet<FunctionNode> allFunctions,
			Map<FunctionNode, TreeSet<FunctionNode>> links,
			Set<String> noLoadFunctionNames, Collection<FunctionNode> toRemove) {

		boolean changeDetected = true;
		while (changeDetected) {
			changeDetected = false;

			for (FunctionNode fn : allFunctions) {
				if (noLoadFunctionNames.contains(fn.getFunctionName())) {
					toRemove.add(fn);
					changeDetected = true;

				} else if (!canReach(threadStarts, fn, links, toRemove)) {
					toRemove.add(fn);
					changeDetected = true;

				}
			}
			allFunctions.removeAll(toRemove);
		}
	}

	private boolean canReach(Collection<FunctionNode> threadStarts, FunctionNode target,
			Map<FunctionNode, TreeSet<FunctionNode>> links, Collection<FunctionNode> toRemove) {

		Collection<FunctionNode> toDo = new TreeSet<FunctionNode>();
		Collection<FunctionNode> seen = new TreeSet<FunctionNode>();
		
		for (FunctionNode start : threadStarts) {
			toDo.add(start);
		
			while (toDo.size() > 0) {
				Collection<FunctionNode> nextToDo = new TreeSet<FunctionNode>();
				if (toDo.contains(target))
					return true;
				
				for (FunctionNode doNode : toDo) {
					seen.add(doNode);
					
					TreeSet<FunctionNode> targets = links.get(doNode);
					if (targets != null) {
						for (FunctionNode next : targets) {
							if (!seen.contains(next) && !toRemove.contains(next)) {
								nextToDo.add(next);
							}
						}
					}
				}
				toDo = nextToDo;
			}
		}
		return false;
	}

	private void detectRecursion(Engine engine, Map<FunctionNode, TreeSet<FunctionNode>> allFunctionObjects,
			Collection<String> recursiveFunctions){
		
		Map<FunctionNode, TreeSet<FunctionNode>> links = createLinkedFunctions(allFunctionObjects);
		
		Collection<FunctionNode> loops = new TreeSet<FunctionNode>();
		for (InstructionNode threadStart : m_controlFlowGraph.getThreadStartInstructions()){
			Map<FunctionNode, TreeSet<FunctionNode>> fnDominators = new TreeMap<FunctionNode, TreeSet<FunctionNode>>();
			FunctionNode start = m_functionStartPcs.get(threadStart.getStartPc());
			TreeSet<FunctionNode> seen = new TreeSet<FunctionNode>();
			crawlFunctionGraph(start, links, seen);
			
			buildFunctionDominatorTree(seen, start, fnDominators);
			
			for (FunctionNode fn : seen){
				if (links.get(fn) != null){
					for (FunctionNode next : links.get(fn)){
						if (!fnDominators.get(next).contains(fn)|| links.get(fn).contains(fn)){
							loops.add(fn);
						}
					}
				}
			}
		}

		Set<Long> recursiveFunctionStartPcs = new TreeSet<Long>();
		if (!loops.isEmpty()){
			for (FunctionNode fn : loops){
				recursiveFunctions.add(fn.getFunctionName());
				recursiveFunctionStartPcs.add(fn.getStartPc());
			}
		}
		
		//remove all function links from the allFunctions
		for (FunctionNode fn : allFunctionObjects.keySet()){
			Collection<FunctionNode> toRemove = new TreeSet<FunctionNode>();
			for (FunctionNode fnLink : allFunctionObjects.get(fn)) {
				if (fnLink.getBranchTarget() != null &&
						recursiveFunctionStartPcs.contains(fnLink.getBranchTarget().getStartPc())) {
					
					toRemove.add(fnLink);
					fnLink.setFunctionName("Recursive Call");
					fnLink.setUnresolved(true);
				}
			}
			allFunctionObjects.get(fn).removeAll(toRemove);
		}
	}

	private Map<FunctionNode, TreeSet<FunctionNode>> createLinkedFunctions(
			Map<FunctionNode, TreeSet<FunctionNode>> allFunctionObjects) {

		Map<FunctionNode, TreeSet<FunctionNode>> links = new TreeMap<FunctionNode, TreeSet<FunctionNode>>();
		
		for (FunctionNode fn : allFunctionObjects.keySet()){
			for (FunctionNode linksTo : allFunctionObjects.get(fn)){
				if (linksTo.getBranchTarget() != null){
					long pc = linksTo.getBranchTarget().getStartPc();
					FunctionNode linkingFn = m_functionStartPcs.get(pc);
					TreeSet<FunctionNode> fnLinks = links.get(fn);
					if (fnLinks == null){
						fnLinks = new TreeSet<FunctionNode>();
						links.put(fn, fnLinks);
					}
					if (!fnLinks.contains(linkingFn)){
						fnLinks.add(m_functionStartPcs.get(pc));
					}
				}
			}
		}
		return links;
	}

	private void buildFunctionDominatorTree(Collection<FunctionNode> reachableNodes, 
			FunctionNode treeStart, Map<FunctionNode, TreeSet<FunctionNode>> fnDominators) {
		for (FunctionNode node : reachableNodes) {
			fnDominators.put(node, new TreeSet<FunctionNode>());
			if (treeStart == node) {
				fnDominators.get(node).add(node);
			} else {
				fnDominators.get(node).addAll(reachableNodes);
			}
		}
		boolean stillChanging = true;
		while (stillChanging) {
			stillChanging = false;
			for (FunctionNode node : reachableNodes) {

				if (treeStart == node)
					continue;

				Set<FunctionNode> intersection = new TreeSet<FunctionNode>(reachableNodes);
				
				//TODO - find out why this is!!!!
				assert(node.getPrevNodesSize() == 0);
				for (Node pred : node.getPrevNodes()) {
					intersection.retainAll(fnDominators.get(pred));
				}
				intersection.add(node);
				if (!intersection.equals(fnDominators.get(node))) {
					fnDominators.get(node).clear();
					fnDominators.get(node).addAll(intersection);
					stillChanging = true;
				}
			}
		}
	}
	
	private void crawlFunctionGraph(FunctionNode fn, Map<FunctionNode, TreeSet<FunctionNode>> links, Set<FunctionNode> seen) {
		seen.add(fn);
		if (links.get(fn)!= null) {
			for (FunctionNode nextFn : links.get(fn)) {	
				if (!seen.contains(nextFn)) {
					crawlFunctionGraph(nextFn, links, seen);
				}
			}
		}
	}
	
	private void linkFunctions(Engine engine, Map<FunctionNode, TreeSet<FunctionNode>> allFunctionObjects) {
		int totalWork = 0;
		Set<FunctionNode> unlinkedFunctions = new TreeSet<FunctionNode>(m_structureGraph.getFunctions());
		for (FunctionNode fn : allFunctionObjects.keySet()) {
			for (FunctionNode linkedFunction : allFunctionObjects.get(fn)) {
				Node branchTarget = linkedFunction.getBranchTarget();
				if (branchTarget != null) {
					FunctionNode toRemove = null;
					for (FunctionNode unlinkedFn : unlinkedFunctions) {
						if (unlinkedFn.getStartPc() == branchTarget.getStartPc()) {
							toRemove = unlinkedFn;
							break;
						}
					}
					if (toRemove != null) {
						unlinkedFunctions.remove(toRemove);
					}
				}
			}
		}
		
		m_structureGraph.setUnlinkedFunctions(unlinkedFunctions);
		
		for (FunctionNode fn : allFunctionObjects.keySet()){
			totalWork += allFunctionObjects.get(fn).size();
		}
		engine.startAnalysis("Linking functions...", totalWork);
		
		boolean wasEmptyLastTime = false;
		while (!allFunctionObjects.isEmpty() && !engine.getAbort()) {
			
			Set<FunctionNode> leafs = new TreeSet<FunctionNode>();
			for (FunctionNode fn : allFunctionObjects.keySet()) {
				if (allFunctionObjects.get(fn).isEmpty()) {
					leafs.add(fn);
				}
			}
			for (FunctionNode fn : allFunctionObjects.keySet()) {
				for (FunctionNode leaf : leafs) {
					Collection<FunctionNode> toRemove = new TreeSet<FunctionNode>();
					for (FunctionNode fnLink : allFunctionObjects.get(fn)) {
						if (fnLink.getBranchTarget() != null) {
							if (fnLink.getBranchTarget().getStartPc() == leaf.getStartPc()) {
								if (SYMBOLIC_LINK){
									//symbolic link
									fnLink.setFunction(leaf.getFunctionName(), leaf.getFunction());
								} else {
									//copying link
									List<Node> nodes = new LinkedList<Node>();
									for (Node node : leaf.getFunction()) {
										nodes.add(node.deepCopy());
									}
									fnLink.setFunction(leaf.getFunctionName(), nodes);
								}
								
								toRemove.add(fnLink);
								engine.provideWorkDone(1);
							}
						} else {
							toRemove.add(fnLink);
							engine.provideWorkDone(1);
						}
					}
					allFunctionObjects.get(fn).removeAll(toRemove);
				}
			}
			
			for (FunctionNode leaf : leafs) {
				allFunctionObjects.remove(leaf);
			}
			if (wasEmptyLastTime && leafs.isEmpty()) {
				break;
			}
			
			wasEmptyLastTime = leafs.isEmpty();
		}
	}

	private void setParents() {
		for (Node node : m_structureGraph.getThreads()) {
			setParent(node);
		}
	}

	private void setParent(Node node) {
		for (Node child : node.getChildren()){
			child.setParent(node);
			setParent(child);
		}
	}

	private void setConfig(final Config config, Collection<FunctionNode> nodes) {
		for (Node node : nodes) {
			node.traverse(0, new DefaultNodeTraverser() {
				public boolean foundInstructionNode(Node node, int depth) {
					((InstructionNodeAttributes)node.getAttributes()).setConfig(config);				
					return true;
				}
			});					
		}
	}
	
	public static String dumpTree(String text, Collection<Node> workingSet) {		
		
		StringBuffer sb = new StringBuffer();
		if (workingSet.size() > 1) {
			sb.append("digraph "+ text + "{");
			for (Node node : workingSet) {
				for (Node next : node.getNextNodes()) {
					if (node.getBranchTargets().contains(next)) {
						sb.append(node + " -> " + next + " [style=dotted];");
					} else {
						sb.append(node + " -> " + next + ";");
					}
				}
				
				if(node.isBlockNode() || node.isInstructionNode()){
					sb.append(node + " [label=\"" + node + "\\n");
					
					
					sb.append("branch targets: " + node.getBranchTargets() + "\\n");
					/*
					sb.append("dominated by: ");
					for(Node dom : node.getDominators()){
						sb.append( dom + " ");
					}
					sb.append("\\n");
					*/
					
					for(XtaInstruction inst : node.getInstructions()){
						sb.append(inst + "\\n");  
					}
					sb.append("\"]; ");
					
				} else if(node.isFunctionNode()){
					sb.append(node + " [label=\"" + node + "\\n");
					sb.append(((FunctionNode)node).getFunctionName() + "\\n");
					sb.append("\"]; ");
				}else if(node.isSequenceNode()){
					sb.append(node + " [label=\"" + node + "\\n");
					sb.append("0x" + Long.toHexString(node.getStartPc()) + "\\n");
					sb.append("\"]; ");
				}
			}
			sb.append("}\n");

		} else {
			sb.append("# graph has only " + workingSet.size() + " nodes");
		}
		
		return sb.toString();
	}
	
	private void setUpPatternMatcher() {
		m_patternMatchers = new ArrayList<PatternMatcher>();
		m_patternMatchers.add(new SelfLoopPattern());
		m_patternMatchers.add(new LoopPatternMatcher());
		m_patternMatchers.add(new ConditionalPatternMatcher());
		m_patternMatchers.add(new SequencePatternMatcher());
		m_patternMatchers.add(new ConditionalTransformer());
	}
}
