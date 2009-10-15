package com.xmos.xta.core.engine.analysis.structural;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import com.xmos.libs.xeloader.XeLoader;
import com.xmos.libs.xeloader.XeLoader.Architecture;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.controlflow.ControlFlowGraph;
import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.NullNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.InstructionNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.core.engine.analysis.structural.patterns.ConditionalPatternMatcher;
import com.xmos.xta.core.engine.analysis.structural.patterns.ConditionalTransformer;
import com.xmos.xta.core.engine.analysis.structural.patterns.LoopPatternMatcher;
import com.xmos.xta.core.engine.analysis.structural.patterns.PatternMatcher;
import com.xmos.xta.core.engine.analysis.structural.patterns.SelfLoopPattern;
import com.xmos.xta.core.engine.analysis.structural.patterns.SequencePatternMatcher;
import com.xmos.xta.core.engine.instructions.InstructionMnemonics;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.sections.Sections;

public class StructuralAnalysisEngine {
	
	private static final boolean SYMBOLIC_LINK = true;
	private static final boolean REGISTER_CONFIG_ON_GLOBAL_TREE = false;
	
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

	public boolean createStructure(Collection<Node> workingSet, Node treeStart, Engine engine, String jobPrefix) {		
		if (workingSet.size() == 1 && workingSet.iterator().next().getNextNodesSize() == 0) 
			return true;
		
		buildDominatorTree(workingSet, treeStart);
		
		boolean working = true;
		boolean dominatorTreeUpToDate = true;
		while (working) {
			engine.provideFeedback(jobPrefix + " - nodes left: " + workingSet.size());
			List<Node> postOrder = new LinkedList<Node>();
			DFSPostOrder(postOrder, treeStart, new HashSet<Node>());
			working = false;
			removeEndNodes(workingSet);
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
						treeStart = pm.getTreeStart();
						assert(workingSet.contains(treeStart));
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

	private void removeEndNodes(Collection<Node> workingSet) {
		
		//System.out.println(StructuralAnalysisEngine.dumpTree("dump", workingSet));
		boolean done = false;
		while(!done){
			done = true;
			for(Node node : workingSet){
				if(node.isEndNode()){
					assert(node.getPrevNodesSize() == 1);
					if(node.getPrevNode().getNextNodesSize() == 1 && node.getNextNodesSize() == 0){
						
						//assert(node.getNextNodesSize() == 0);
						CommonMethods.disconnect(node, node.getPrevNode());
						workingSet.remove(node);
						done = false;
						break;
					}
				}
			}
		}
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
		
		if (REGISTER_CONFIG_ON_GLOBAL_TREE) {
			// If enabled, this allows the printing of the global structure to 
			// display the node times. It is disabled by default for efficency reasons,
			// as printing the global structure is not a user activity. 
			engine.provideFeedback("Registering config...");
			Config config = engine.getConfig();
			Architecture architecture = engine.getXeLoader().getArchitecture();
			setConfig(config, architecture, m_structureGraph.getThreads());
			setConfig(config, architecture, m_structureGraph.getFunctions());
			setConfig(config, architecture, m_structureGraph.getUnlinkedFunctions());			
		}
		
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
			
			
			//System.out.println(dumpTree("hello", basicBlocks));
			
			
			assert(!basicBlocks.isEmpty());
			FnopAnalyser fa = new FnopAnalyser();
			
			fa.analyse(initialBasicBlock, isFunction);
			fa.addFnopsToBasicBlocks(initialBasicBlock);
			assert(basicBlocks.contains(initialBasicBlock));

			if (!createStructure(basicBlocks, initialBasicBlock, engine, jobName)) {
				return null;
			}
			
			assert(basicBlocks.size() == 1);
			Node decomposedGraph = basicBlocks.iterator().next();

			final FunctionNode fn = new FunctionNode(decomposedGraph, functionName);
			
			final TreeSet<FunctionNode> unlinkedFunctions = new TreeSet<FunctionNode>();
			
			fn.traverse(new Stack<Node>(), new DefaultNodeTraverser() {
	   		    public boolean foundFunctionNode(Node node, Stack<Node> ancestors) {
	   		    	if(node != fn){
	   		    		unlinkedFunctions.add((FunctionNode)node);
	   		    	}
	   		    	return true;
	   		    }   			
	   		});

			allFunctionObjects.put(fn, unlinkedFunctions);
			graphs.add(fn);						
			normaliseGraph(fn);
			
			startPcs.put(graphStart.getStartPc(), fn);
			engine.provideWorkDone(1);
		}
		
		return graphs;
	}

	private boolean done;
	public void normaliseGraph(Node fn) {
		
		setParent(fn);
		
		done = false;
		//this bit removes the path within paths 
		while(!done){
			done =true;
			fn.traverse(new Stack<Node>(),  new DefaultNodeTraverser() {
				public boolean foundSequenceNode(Node node, Stack<Node> ancestors) {
					for(Node child : node.getIndirectChildren()){
						if(child.isSequenceNode()){
							int insertPathAt =node.getIndirectChildren().indexOf(child);
							node.getIndirectChildren().remove(insertPathAt);
							for(Node pathChild : child.getIndirectChildren()){
								node.getIndirectChildren().add(insertPathAt++, pathChild);
							}
							done = false;
							return false;
						}
					}
					return true;
				}
				
				public boolean foundFunctionNode(Node node, Stack<Node> ancestors) {
					for(Node child : node.getIndirectChildren()){
						if(child.isSequenceNode()){
							int insertPathAt =node.getIndirectChildren().indexOf(child);
							node.getIndirectChildren().remove(insertPathAt);
							for(Node pathChild : child.getIndirectChildren()){
								node.getIndirectChildren().add(insertPathAt++, pathChild);
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
						seen.addAll(getContainedNodes(bbpm.getBlock()));
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

	//try to remove tree sets from this
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

		Set<Node> intersection = new HashSet<Node>();
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

				//TODO - this should be ok but check
				Set<FunctionNode> intersection = new HashSet<FunctionNode>(reachableNodes);
				
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

	private void setParent(Node node) {
		for (Node child : node.getIndirectChildren()){
			child.setParent(node);
			setParent(child);
		}
	}

	private void setConfig(final Config config, final Architecture architecture, Collection<FunctionNode> nodes) {
		for (Node node : nodes) {
			node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {
				public boolean foundInstructionNode(Node node, Stack<Node> ancestors) {
					((InstructionNodeAttributes)node.getAttributes()).setConfig(config, architecture);				
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
				//if(node.isBlockNode()){
				//	sb.append(node + " [label=\""+node.getInstructions() +"\"];");
				//} 
			if(node.isFunctionNode()){
					sb.append(node + " [label=\""+((FunctionNode)node).getFunctionName() +"\"];");
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
		m_patternMatchers.add(new SequencePatternMatcher());	//uses dominators
		m_patternMatchers.add(new ConditionalTransformer());	//uses dominators
	}

	private Collection<Node> getContainedNodes(Node node) {
		final Collection<Node> nodes = new ArrayList<Node>();
		
		node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {		
			public boolean foundInstructionNode(Node node, Stack<Node> ancestors) {		
				nodes.add(node);
				return true;
			}
	    });
		return nodes;
	}	
}
