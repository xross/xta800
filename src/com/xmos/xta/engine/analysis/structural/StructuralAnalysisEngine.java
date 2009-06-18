package com.xmos.xta.engine.analysis.structural;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.controlflow.ControlFlowGraph;
import com.xmos.xta.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.engine.analysis.structural.patterns.LoopPatternMatcher;
import com.xmos.xta.engine.analysis.structural.patterns.PathPatternMatcher;
import com.xmos.xta.engine.analysis.structural.patterns.PatternMatcher;
import com.xmos.xta.engine.analysis.structural.patterns.SectionPatternMatcher;
import com.xmos.xta.engine.analysis.structural.patterns.SectionTransformer;
import com.xmos.xta.engine.analysis.structural.patterns.SelfLoopPattern;
import com.xmos.xta.engine.analysis.structural.patterns.TerminalPatternMatcher;
import com.xmos.xta.engine.analysis.structural.patterns.UpWedge;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class StructuralAnalysisEngine {
	private static final boolean DEBUG = false;

	private ControlFlowGraph m_controlFlowGraph;
	private Map<Integer, FunctionNode> m_functionStartPcs;
	private Map<Integer, FunctionNode> m_threadStartPcs;
	private StructureGraph m_structureGraph;
	private Map<FunctionNode, FunctionNode> m_cfgToStruct;
	private List<PatternMatcher> m_patternMatchers;
	
	
	public StructuralAnalysisEngine(ControlFlowGraph controlFlowGraph) {
		m_controlFlowGraph = controlFlowGraph;
		m_cfgToStruct = new TreeMap<FunctionNode, FunctionNode>();
		setUpPatternMatcher();
	}

	public StructuralAnalysisEngine() {
		setUpPatternMatcher();
	}

	public boolean analyse(Engine engine) {
		m_functionStartPcs = new TreeMap<Integer, FunctionNode>();
		m_threadStartPcs = new TreeMap<Integer, FunctionNode>();
		return buildStructureGraph(engine);
	}

	public StructureGraph getStructureGraph() {
		return m_structureGraph;
	}

	public boolean createStructure(Collection<Node> workingSet, Node treeStart, Engine engine, boolean debug) {
		if(workingSet.size() == 1) return true;
		boolean working = true;
		boolean dominatorTreeUpToDate = false;
		while (working) {
			List<Node> postOrder = new LinkedList<Node>();
			DFSPostOrder(postOrder, treeStart, new HashSet<Node>());
			working = false;
			for (PatternMatcher pm : m_patternMatchers) {
				
				if (!dominatorTreeUpToDate && pm.usesDominatorTree()) {
					buildDominatorTree(workingSet, treeStart);
					dominatorTreeUpToDate = true;
				}
				for (Node node : postOrder) {
					if (engine.getAbort()) {
						return false;
					}
					if (pm.isMatch(node, workingSet, false, treeStart)) {
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

	private boolean buildStructureGraph(Engine engine) {
		Collection<Node> functions = buildGraph(m_controlFlowGraph.getFunctionStartInstructions(), 
				m_functionStartPcs, engine, true);
		if (functions == null) {
			return false;
		}
		Collection<Node> threads = buildGraph(m_controlFlowGraph.getThreadStartInstructions(), 
				m_threadStartPcs, engine, false);
		if (threads == null) {
			return false;
		}		
		m_structureGraph = new StructureGraph(threads);
		
		engine.provideFeedback("Linking functions...");
		linkFunctions();
		
		engine.provideFeedback("Setting parent nodes...");
		setParents();

		engine.provideFeedback("Complete");
		return true;
	}

	private Collection<Node> buildGraph(Collection<Node> graphStartNodes, Map<Integer, FunctionNode> startPcs,
			Engine engine, boolean isFunction) {

		Collection<Node> graphs = new HashSet<Node>();

		List<Node> orderedGraphs = new LinkedList<Node>(graphStartNodes);
		Collections.sort(orderedGraphs);
		for (Node graphStart : orderedGraphs) {

			String functionName = graphStart.getInstructions().getFirst().getFunctionName();

			engine.provideFeedback("Processing " + functionName);

			//TODO
			Collection<Node> currentGraph = new TreeSet<Node>();
			collectCurrentGraphNodes(graphStart, currentGraph);
			
			Collection<Node> basicBlocks = new TreeSet<Node>();
			
			Node initialBasicBlock = reduceGraphToBasicBlocks(basicBlocks, graphStart, currentGraph);
			
			
			//now do fnop analysis
			
			FnopAnalyser fa = new FnopAnalyser();
			fa.analyse(initialBasicBlock, isFunction);
			
			assert(basicBlocks.contains(initialBasicBlock));
			
			if (DEBUG) 
				dumpTree(basicBlocks);
			
			if (!createStructure(basicBlocks, initialBasicBlock, engine, false)) {
				return null;
			}
			
			Node decomposedGraph = basicBlocks.iterator().next();
		
			
			FunctionNode fn = new FunctionNode(decomposedGraph, functionName);
			graphs.add(fn);
			
			setParent(fn);
			
			startPcs.put(graphStart.getStartPc(), fn);
		}
		return graphs;
	}

	private Node reduceGraphToBasicBlocks(Collection<Node> basicBlocks, Node initialBasicBlock,
			Collection<Node> currentGraph) {

		Set<Node> seen = new HashSet<Node>();
		Map<Node, Node> cfgToStructure = new HashMap<Node, Node>();
		Map<Node, Node> endPoint = new HashMap<Node, Node>();
		Node treeStart = initialBasicBlock;

		for (Node node : currentGraph) {
			if (!seen.contains(node)) {
				if (!node.isFunctionNode()) {
					BasicBlockMatcher bbpm = new BasicBlockMatcher();
					
					if (bbpm.match(node, treeStart, currentGraph)) {
						seen.addAll(bbpm.getBlock().getContainedNodes());
						basicBlocks.add(bbpm.getBlock());
						treeStart = bbpm.getTreeStart();
						cfgToStructure.put(node, bbpm.getBlock());
						endPoint.put(bbpm.getBlock(), bbpm.getLastBlock());
					}

				} else {
					seen.add(node);
					Node clone = node.shallowCopy();
					
					m_cfgToStruct.put((FunctionNode) clone, (FunctionNode) node);
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
				node.getBranchTargets().add(cfgToStructure.get(branch));
			}

			for (Node next : lastInstruction.getNextNodes()) {
				CommonMethods.connect(cfgToStructure.get(next), node);
			}
		}
		return treeStart;
	}

	private void collectCurrentGraphNodes(Node node, Collection<Node> seen) {
		seen.add(node);
		for (Node next : node.getNextNodes()) {
			if (!seen.contains(next)) {
				collectCurrentGraphNodes(next, seen);
			}
		}
	}

	private void buildDominatorTree(Collection<Node> reachableNodes, Node treeStart) {

		for (Node node : reachableNodes) {
			node.getDominators().clear();
			if (treeStart == node) {
				node.setDominatorSetComplete(false);
				node.getDominators().add(node);
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

				if (node.getDominators().size() == 1) {
					continue;
				}
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
					node.getDominators().clear();
					node.getDominators().addAll(intersection);
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
			total += node.getPrevNodes().size() - node.getNextNodes().size();
		}
		return true;
	}

	private void linkFunctions() {
		for (FunctionNode fn : m_cfgToStruct.keySet()) {
			if (m_cfgToStruct.get(fn).getBranchTarget() != null) {
				int pc = m_cfgToStruct.get(fn).getBranchTarget().getStartPc();
				String fnName = m_cfgToStruct.get(fn).getBranchTarget().getInstructions().get(0).getFunctionName();
				fn.setFunction(fnName, m_functionStartPcs.get(pc).getFunction().deepCopy(m_functionStartPcs));
			}
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

	private static int g = 0;
	public static void dumpTree(Collection<Node> workingSet) {
		if (workingSet.size() > 1) {
			System.out.println("digraph g" + g++ + " {");
			for (Node node : workingSet) {
				for (Node next : node.getNextNodes()) {
					if (node.getBranchTargets().contains(next)) {
						System.out.println(node + " -> " + next + " [style=dotted];");
					} else {
						System.out.println(node + " -> " + next + ";");
					}
				}
				System.out.print(node + " [label=\"" );
				for(XtaInstruction inst : node.getInstructions()){
					System.out.print(inst + "\\n");
				}
				System.out.print("\"");
				System.out.println("];");
			}
			System.out.println("}\n");
		}
	}
	
	private void setUpPatternMatcher() {
		m_patternMatchers = new ArrayList<PatternMatcher>();
		m_patternMatchers.add(new SelfLoopPattern());
		m_patternMatchers.add(new LoopPatternMatcher());
		m_patternMatchers.add(new SectionPatternMatcher());
		m_patternMatchers.add(new SectionTransformer());
		m_patternMatchers.add(new PathPatternMatcher());
		m_patternMatchers.add(new UpWedge());
		m_patternMatchers.add(new TerminalPatternMatcher());
	}
}
