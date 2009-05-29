package com.xmos.xta.engine.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.xmos.xta.engine.analysis.nodes.Function;
import com.xmos.xta.engine.analysis.nodes.Node;
import com.xmos.xta.engine.analysis.patterns.CommonMethods;
import com.xmos.xta.engine.analysis.patterns.LoopPatternMatcher;
import com.xmos.xta.engine.analysis.patterns.PathPatternMatcher;
import com.xmos.xta.engine.analysis.patterns.PatternMatcher;
import com.xmos.xta.engine.analysis.patterns.SectionPatternMatcher;
import com.xmos.xta.engine.analysis.patterns.SectionTransformer;
import com.xmos.xta.engine.analysis.patterns.SelfLoopPattern;
import com.xmos.xta.engine.analysis.patterns.TerminalPatternMatcher;
import com.xmos.xta.engine.analysis.patterns.UpWedge;

public class StructuralAnalysisEngine {
	
	private ControlFlowGraph m_cfg;
	private Map<Integer, Function> m_functionStartPcs;
	private Map<Integer, Function> m_threadStartPcs;
	private static final boolean VERBOSE_ANALYSIS = true;
	private StructureGraph m_sg;
	
	public StructuralAnalysisEngine(ControlFlowGraph cfg){
		m_cfg = cfg;
	}
	
	/**
	 * By the time this is called all the instructions successors should have been set and
	 * all unknown branch and link targets should have a pseudo function in place. 
	 * @return true for success
	 */
	public boolean analyse(){
		m_functionStartPcs = new HashMap<Integer, Function>();
		m_threadStartPcs = new HashMap<Integer, Function>();
		buildStructureGraph();
		linkFunctions();
		return true;
	}
	
	public StructureGraph getStructureGraph(){
		return m_sg;
	}
	
	private void linkFunctions() {
		for(Function fn : m_cfg.getAllFunctions()){
			if(fn.isFunctionKnown()){
				String fnName = fn.getBranchTarget().getInstructions().get(0).getFunctionName();
				fn.setFunction(fnName, m_functionStartPcs.get(fn.getBranchTarget().getStartPc()).getFunction());
				//
				CommonMethods.displayStructureTree(fn);
			}
		}
	}
	
	private void buildStructureGraph(){
		Collection<Node> functions = buildGraph(m_cfg.getFunctionStartInstructions(), m_functionStartPcs);
		Collection<Node> threads = buildGraph(m_cfg.getThreadStartInstructions(), m_threadStartPcs);
		m_sg = new StructureGraph(functions, threads);
	}
	
	private Collection<Node> buildGraph(Collection<Node> graphStartNodes, Map<Integer, Function> startPcs) {
		Collection<Node> graphs = new HashSet<Node>();
		int i=0;
		for (Node graphStart : graphStartNodes) {

			int graphStartPc = graphStart.getStartPc();
			if (VERBOSE_ANALYSIS) System.out.println("Analysing function at 0x" + Integer.toHexString(graphStartPc) + " -- " + i++ + " of " + graphStartNodes.size());
			Collection<Node> currentGraph = new HashSet<Node>();

			collectCurrentGraphNodes(graphStart, currentGraph);
			Collection<Node> basicBlocks = new HashSet<Node>();
			
			Node initialBasicBlock = reduceGraphToBasicBlocks(basicBlocks, graphStart, currentGraph);		
			if (VERBOSE_ANALYSIS) System.out.println("\t\t" + basicBlocks.size() + " basic blocks " + currentGraph.size() + " nodes\n");
			decompose(basicBlocks, initialBasicBlock);
			Node decomposedGraph = basicBlocks.iterator().next();
			
			String functionName = graphStart.getInstructions().get(0).getFunctionName();

			Function fn = new Function(decomposedGraph, functionName);
			
			graphs.add(fn);
			
			startPcs.put(graphStartPc, fn);
		}
		
		return graphs;
	}
	
	/**
	 * This step is in the flow in order to reduce the graph of instructions into basic blocks.
	 */
	private Node reduceGraphToBasicBlocks(Collection<Node> basicBlocks, Node initialBasicBlock, Collection<Node> currentGraph) {
		
		Set<Node> seen = new HashSet<Node>();
		Map<Node, Node> cfgToStructure = new HashMap<Node, Node>();
		Map<Node, Node> endPoint = new HashMap<Node, Node>();
		Node treeStart = initialBasicBlock;
		
		for (Node node : currentGraph) {
			if(!seen.contains(node)){
				if(!node.isFunction()){
					BasicBlockMatcher bbpm = new BasicBlockMatcher();
					if(bbpm.match(node, treeStart, currentGraph)){
						seen.addAll(bbpm.getBlock().getContaintedNodes());
						basicBlocks.add(bbpm.getBlock());
						treeStart = bbpm.getTreeStart();
						cfgToStructure.put(node, bbpm.getBlock());
						endPoint.put(bbpm.getBlock(), bbpm.getLastBlock());
					}
				
				} else {
					seen.add(node);
					Node clone = node.clone();
					basicBlocks.add(clone);
					cfgToStructure.put(node, clone);
					endPoint.put(clone, node);
				}
			}
		}
		//now add all the links
		for(Node node : basicBlocks){
			Node lastInstruction = endPoint.get(node);
			for(Node next : lastInstruction.getNextNodes()){
				CommonMethods.connect(cfgToStructure.get(next), node);
			}	
		}
		return treeStart;
	}

	private void collectCurrentGraphNodes(Node node, Collection<Node> seen){
		seen.add(node);
		for(Node next : node.getNextNodes()){
			if(!seen.contains(next)){
				collectCurrentGraphNodes(next, seen);
			}
		}
	}
	
	private void decompose(Collection<Node> workingSet, Node treeStart) {
		List<PatternMatcher> patternMatchers = new ArrayList<PatternMatcher>();
		patternMatchers.add(new SelfLoopPattern());
		patternMatchers.add(new LoopPatternMatcher());
		patternMatchers.add(new SectionPatternMatcher());
		patternMatchers.add(new SectionTransformer());
		patternMatchers.add(new PathPatternMatcher());
		patternMatchers.add(new UpWedge());
		patternMatchers.add(new TerminalPatternMatcher());
		boolean working = true;
		boolean dominatorTreeUpToDate = false;
		
		while (working) {
			
			List<Node> postOrder = new LinkedList<Node>();
			DFSPostOrder(postOrder, treeStart, new HashSet<Node>());
			working = false;
			for (PatternMatcher pm : patternMatchers) {
				if(!dominatorTreeUpToDate && pm.usesDominatorTree()){
					buildDominatorTree(workingSet, treeStart);
					dominatorTreeUpToDate = true;
				}
				for (Node node : postOrder) {
					if (pm.isMatch(node, workingSet, false, treeStart)) {
						treeStart = pm.getTreeStart();
						if(!connectivitySanityCheck(workingSet)){
							System.err.println("failed connectivity sanity check on " + pm.getClass().getSimpleName() + " on " + node);
							return;
						}
						dominatorTreeUpToDate = false;
						working = true;
						break;
					}
				}
				if (working)
					break;
			}
		}
	}
	
	private void buildDominatorTree(Collection<Node> reachableNodes, Node treeStart) {
		for (Node node : reachableNodes) {
			node.getDominators().clear();
			if (treeStart == node) {
				node.getDominators().add(node);
			} else {
				node.getDominators().addAll(reachableNodes);
			}
		}
		
		Set<Node> intersection = new HashSet<Node>();
		boolean stillChanging = true;
		while (stillChanging) {
			stillChanging = false;
			for (Node node : reachableNodes) {

				if (treeStart == node)
					continue;
				
				intersection.clear();
				
				if(node.getPrevNodes().size() > 0){
					intersection.addAll(node.getPrevNodes().iterator().next().getDominators());
					for (Node pred : node.getPrevNodes()) {
						intersection.retainAll(pred.getDominators());
					}
				} 
				
				intersection.add(node);

				if (!intersection.equals(node.getDominators())) {
					node.getDominators().clear();
					node.getDominators().addAll(intersection);
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

	//FIXME - keep this until we are happy with the engine
	private boolean connectivitySanityCheck(Collection<Node> workingSet){
		int total = 0;
		for (Node node : workingSet) {
			total += node.getPrevNodes().size()-node.getNextNodes().size();
		}
		if (total != 0) return false;
		return true;
	}
	
}
