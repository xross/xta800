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
	private Map<Integer, Function> m_graphStartPcs;
	
	public StructuralAnalysisEngine(ControlFlowGraph cfg){
		m_cfg = cfg;
	}
	
	/**
	 * By the time this is called all the instructions successors should have been set and
	 * all unknown branch and link targets should have a pseudo function in place. 
	 * @return true for success
	 */
	public boolean analyse(){
		m_graphStartPcs = new HashMap<Integer, Function>();
		//first extract all the function from the memory image
		buildFunctions();
		//then extract the threads from the memory image
		buildThreads();
		//perform the link stage
		LinkFunctions();
		return true;
	}
	
	private void LinkFunctions() {
		for (Function func : m_cfg.getAllFunctions()) {
			if(func.isFunctionKnown()){
				String fnName = func.getBranchTarget().getInstructions().get(0).getFunctionName();
				func.setFunction(fnName, m_graphStartPcs.get(func.getBranchTarget().getStartPc()).getFunction());
			}
		}
	}
	
	private void buildFunctions(){
		buildGraph(m_cfg.getFunctionStartInstructions());
	}
	
	private void buildThreads() {
		buildGraph(m_cfg.getThreadStartInstructions());
	}
	
	private boolean buildGraph(Collection<Node> graphStartNodes) {
		
		for (Node graphStart : graphStartNodes) {

			Collection<Node> currentGraph = new HashSet<Node>();

			collectCurrentGraphNodes(graphStart, currentGraph);
			
			decompose(currentGraph, graphStart);
			
			Node decomposedGraph = currentGraph.iterator().next();
			
			String functionName = null;
			
			if(graphStart.getInstructions().size() > 0){
				functionName = graphStart.getInstructions().get(0).getFunctionName();
			}

			Function fn = new Function(decomposedGraph, functionName);
			m_graphStartPcs.put(graphStart.getStartPc(), fn);
		}
		return true;
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
		patternMatchers.add(new PathPatternMatcher());
		patternMatchers.add(new SectionPatternMatcher());
		patternMatchers.add(new SectionTransformer());
		patternMatchers.add(new UpWedge());
		patternMatchers.add(new TerminalPatternMatcher());
		
		boolean working = true;
		while (working) {
			buildDominatorTree(workingSet, treeStart);
			List<Node> postOrder = new LinkedList<Node>();
			DFSPostOrder(postOrder, treeStart, new HashSet<Node>());
			working = false;
			for (PatternMatcher pm : patternMatchers) {
				for (Node node : postOrder) {
					if (pm.isMatch(node, workingSet, false, treeStart)) {
						treeStart = pm.getTreeStart();
						if(!connectivitySanityCheck(workingSet)){
							System.err.println("failed connectivity sanity check on " + pm.getClass().getSimpleName() + " on " + node);
							return;
						}
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
		boolean stillChanging = true;
		while (stillChanging) {
			stillChanging = false;
			for (Node node : reachableNodes) {

				if (treeStart == node)
					continue;

				Set<Node> intersection = new HashSet<Node>(reachableNodes);
				for (Node pred : node.getPrevNodes()) {
					intersection.retainAll(pred.getDominators());
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

	private boolean connectivitySanityCheck(Collection<Node> workingSet){
		int total = 0;
		for (Node node : workingSet) {
			total += node.getPrevNodes().size()-node.getNextNodes().size();
		}
		if (total != 0) return false;
		return true;
	}
	
}
