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
	
	private ControlFlowGraph m_mi;
	private Map<Integer, Function> m_graphStartPcs;
	private Collection<Function> m_nonlinkedFunctions;
	
	public StructuralAnalysisEngine(ControlFlowGraph mi){
		m_mi = mi;
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
		for (Function func : m_nonlinkedFunctions) {
			String fnName = func.getBranchTarget().getInstructions().get(0).getFunctionName();
			func.setFunction(fnName, m_graphStartPcs.get(func.getBranchTarget().getStartPc()).getFunction());
		}
	}
	
	private void buildFunctions(){
		Collection<Node> functionStartNodes = new HashSet<Node>();
		Collection<Node> functionEndNodes = new HashSet<Node>();
		findFunctionNodes(m_mi.getStartOfMemory(), new HashSet<Node>(), functionStartNodes, functionEndNodes);
		buildGraph(functionStartNodes, functionEndNodes);
	}
	
	private void buildThreads() {
		buildGraph(m_mi.getThreadStartInstructions(), m_mi.getThreadEndInstructions());
	}
	
	private boolean buildGraph(Collection<Node> graphStartNodes, Collection<Node> graphEndNodes) {
		
		for (Node functionStart : graphStartNodes) {
			
			Map<Node, Node> functionNodes = new HashMap<Node, Node>();
			Collection<Node> currentGraph = new HashSet<Node>();

			collectCurrentGraphNodes(functionStart, null, functionNodes, graphEndNodes, currentGraph);
			
			currentGraph.addAll(functionNodes.values());
			
			decompose(currentGraph, functionNodes.get(functionStart));
			
			Node decomposedGraph = currentGraph.iterator().next();
			
			String functionName = null;
			
			if(functionStart.getInstructions().size() > 0){
				functionName = functionStart.getInstructions().get(0).getFunctionName();
			}

			Function fn = new Function(decomposedGraph, functionName);
			m_graphStartPcs.put(functionStart.getStartPc(), fn);
		}
		return true;
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
	
	private void collectCurrentGraphNodes(Node node, Node previousNode, Map<Node, Node> newFunctionNodes, 
			Collection<Node> functionEnds, Collection<Node> newNodes) {

		if (newFunctionNodes.keySet().contains(node)) {
			assert (previousNode != null);
			CommonMethods.connect(newFunctionNodes.get(node), previousNode);
			return;
		}

		Node clonedNode = node.clone();
		newFunctionNodes.put(node, clonedNode);
		if (previousNode != null) {
			CommonMethods.connect(clonedNode, previousNode);
		}

		if (functionEnds.contains(node)) {
			return;
		} else {
			if (node.getBranchLinkReturn() != null) {

				assert (node.getNextNodes().size() == 1);
				Function fn = new Function(node.getNextNodes().iterator().next());
				m_nonlinkedFunctions.add(fn);
				newNodes.add(fn);
				CommonMethods.connect(fn, clonedNode);
				collectCurrentGraphNodes(node.getBranchLinkReturn(), fn, newFunctionNodes, functionEnds, newNodes);
			} else {
				for (Node next : node.getNextNodes()) {
					collectCurrentGraphNodes(next, clonedNode, newFunctionNodes, functionEnds, newNodes);
				}
			}
		}
	}

	/**
	 * This finds all the places that branch and links call and all the retsps given the start of memory.
	 * It can only find nodes that are reachable.
	 */
	private void findFunctionNodes(Node node, Collection<Node> seen, Collection<Node> functionStartNodes, Collection<Node> functionEndNodes) {
		//TODO fix this call - maybe add a method to Node to determine if it ends in a branch and link
		if (node.getInstructions().get(node.getInstructions().size() - 1).getBranchLink()) {
			Node startNode = node.getNextNodes().iterator().next();
			if (!functionStartNodes.contains(startNode)) {
				functionStartNodes.add(startNode);
			}
		}
		if (node.getInstructions().get(node.getInstructions().size() - 1).getReturn()) {
			if (!functionEndNodes.contains(node)) {
				functionEndNodes.add(node);
			}
		}
		for (Node next : node.getNextNodes()) {
			if (!seen.contains(next)) {
				seen.add(next);
				findFunctionNodes(next, seen, functionStartNodes, functionEndNodes);
			}
		}
	}
	
}
