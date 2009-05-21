package com.xmos.xta.engine.xtagraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.engine.xtagraph.nodes.Block;
import com.xmos.xta.engine.xtagraph.nodes.Function;
import com.xmos.xta.engine.xtagraph.nodes.Node;
import com.xmos.xta.engine.xtagraph.patterns.CommonMethods;
import com.xmos.xta.engine.xtagraph.patterns.DownWedge;
import com.xmos.xta.engine.xtagraph.patterns.LoopPatternMatcher;
import com.xmos.xta.engine.xtagraph.patterns.SectionPatternMatcher;
import com.xmos.xta.engine.xtagraph.patterns.SectionTransformer;
import com.xmos.xta.engine.xtagraph.patterns.PathPatternMatcher;
import com.xmos.xta.engine.xtagraph.patterns.PatternMatcher;
import com.xmos.xta.engine.xtagraph.patterns.SelfLoopPattern;
import com.xmos.xta.engine.xtagraph.patterns.TerminalPatternMatcher;
import com.xmos.xta.engine.xtagraph.patterns.UpWedge;

public class ThreadGraph {

	private Node m_threadStartNode;
	private Collection<Function> m_functions;
	private Collection<Node> m_reachableNodes;
	private Collection<Function> m_nonlinkedFunctions;
	private Map<Integer, Function> m_functionNames;

	public ThreadGraph(Node startNode) {
		m_functionNames = new HashMap<Integer, Function>();
		m_nonlinkedFunctions = new HashSet<Function>();
		m_threadStartNode = startNode;
		m_reachableNodes = new HashSet<Node>();
		m_functions = new HashSet<Function>();
		collectGraphNodes(startNode);
	}

	private boolean buildFunctions() {
		
		Collection<Node> functionStartNodes = new HashSet<Node>();
		Collection<Node> functionEndNodes = new HashSet<Node>();
		List<Node> graphStartNodes = new LinkedList<Node>(m_reachableNodes);
		
		findFunctionNodes(functionStartNodes, functionEndNodes);
		
		if (!functionStartNodes.contains(m_threadStartNode)) {
			functionStartNodes.add(m_threadStartNode);
		}
		
		/*
		for(Node s : functionStartNodes){
			XtaInstruction i = s.getInstructions().get(0);
			System.out.println("0x" +Integer.toHexString(i.getPc()) + " " + i.getFunctionName());
		}
		*/
		
		graphStartNodes.retainAll(functionStartNodes);
		Collections.sort(graphStartNodes);
		
		for (Node functionStart : graphStartNodes) {
			
			Map<Node, Node> functionNodes = new HashMap<Node, Node>();
			Collection<Node> currentGraph = new HashSet<Node>();

			collectCurrentGraphNodes(functionStart, null, functionNodes, functionEndNodes, currentGraph);
			currentGraph.addAll(functionNodes.values());
			
			decompose(currentGraph, functionNodes.get(functionStart));
			
			Node decomposedGraph = currentGraph.iterator().next();
			
			String fucntionName = null;
			
			if(functionStart.getInstructions().size() > 0){
				fucntionName = functionStart.getInstructions().get(0).getFunctionName();
			}

			Function fn = new Function(decomposedGraph, fucntionName);
			
			m_functions.add(fn);
			m_functionNames.put(functionStart.getStartPc(), fn);
			
			//System.out.println("adding 0x" + Integer.toHexString(functionStart.getStartPc()) + " -> " + fucntionName);
		}
		return true;
	}

	public void decompose() {
		if (buildFunctions()) {
			linkFunctions();
			displayStructureTree();
		} else {
			System.out.println("could not complete");
		}

	}

	private void linkFunctions() {
		for (Function func : m_nonlinkedFunctions) {
			String fnName = func.getBranchTarget().getInstructions().get(0).getFunctionName();
			func.setFunction(fnName, m_functionNames.get(func.getBranchTarget().getStartPc()).getFunction());
		}
	}

	private void findFunctionNodes(Collection<Node> functionStartNodes, Collection<Node> functionEndNodes) {
		for (Node node : m_reachableNodes) {
			if (node.getInstructions().get(node.getInstructions().size() - 1).getBranchLink()) {
				Node startNode = node.getNextNodes().iterator().next();
				if (!functionStartNodes.contains(startNode))
					functionStartNodes.add(startNode);
			}
			if (node.getInstructions().get(node.getInstructions().size() - 1).getReturn()) {
				if (!functionEndNodes.contains(node))
					functionEndNodes.add(node);
			}
		}
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

	public boolean isThreadStart(Node node) {
		if (node == m_threadStartNode) {
			return true;
		} else if (node.getContaintedNodes().contains(m_threadStartNode)) {
			return true;
		} else
			return false;
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

	public void decompose(Collection<Node> workingSet, Node treeStart) {
		List<PatternMatcher> patternMatchers = new ArrayList<PatternMatcher>();
		patternMatchers.add(new SelfLoopPattern());
		patternMatchers.add(new LoopPatternMatcher());
		patternMatchers.add(new PathPatternMatcher());
		patternMatchers.add(new SectionPatternMatcher());
		patternMatchers.add(new SectionTransformer());
		patternMatchers.add(new UpWedge());
		patternMatchers.add(new TerminalPatternMatcher());
		//patternMatchers.add(new DownWedge());
		
		boolean working = true;
		while (working) {
			buildDominatorTree(workingSet, treeStart);
			//CommonMethods.printGraph(workingSet);
			List<Node> postOrder = new LinkedList<Node>();
			DFSPostOrder(postOrder, treeStart, new HashSet<Node>());
			working = false;
			for (PatternMatcher pm : patternMatchers) {
				for (Node node : postOrder) {
					if (pm.isMatch(node, workingSet, false, treeStart)) {
						treeStart = pm.getTreeStart();
						if (!singleGraphSanityCheck(workingSet)) {
							System.out.println("failed singleGraph sanity check on " + pm.getClass().getSimpleName() + " on " + node);
							return;
						} else if(!connectivitySanityCheck(workingSet)){
							System.out.println("failed connectivity sanity check on " + pm.getClass().getSimpleName() + " on " + node);
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

	private boolean singleGraphSanityCheck(Collection<Node> workingSet) {
		int initialNodeCount = 0;
		for (Node node : workingSet) {
			if (node.getPrevNodes().size() == 0) {
				initialNodeCount++;
			}
		}
		return (initialNodeCount < 2);
	}

	private boolean connectivitySanityCheck(Collection<Node> workingSet){
		int prevCount = 0;
		int nextCount = 0;

		for (Node node : workingSet) {
			prevCount += node.getPrevNodes().size();
			nextCount += node.getNextNodes().size();
		}
		if (prevCount > nextCount) {
			System.out.println("# to many prevs");
			return false;
		} else if (prevCount < nextCount) {
			System.out.println("# to many nexts");
			return false;
		} else {
			return true;
		}
	}
	
	public int getGraphSize() {
		return m_reachableNodes.size();
	}

	// --------------------------------------------------------------------- //
	// Helper Debug Methods.
	// --------------------------------------------------------------------- //

	public void displayControlFlowGraph() {
		for (Node node : m_reachableNodes) {
			System.out.printf("-----------------------------------------\n");
			System.out.printf("%s -> ", node.toString());
			for (Node nextNode : node.getNextNodes()) {
				System.out.printf("[%s]", nextNode.toString());
			}
			System.out.printf("\n");
			if (node instanceof Block) {
				((Block) node).display(0);
			}
		}
	}

	public void displayStructureTree() {
		for (Function start : m_functions) {
			start.display(0);
			System.out.println();
		}

	}

	/**
	 * This takes the master graph and crawls it for nodes that are reachable
	 * from the start node
	 */
	private void collectGraphNodes(Node node) {
		m_reachableNodes.add(node);
		for (Node next : node.getNextNodes()) {
			if (!m_reachableNodes.contains(next)) {
				collectGraphNodes(next);
			}
		}
	}
}
