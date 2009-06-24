package com.xmos.xta.engine.analysis.structural;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.NodeCoreCollection;
import com.xmos.xta.engine.analysis.controlflow.ControlFlowGraph;
import com.xmos.xta.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
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

	private ControlFlowGraph m_controlFlowGraph;
	private Map<Integer, FunctionNode> m_functionStartPcs;
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

	public boolean analyse(Engine engine, TreeMap<String, InstructionNode> treeMap) {
		m_functionStartPcs = new TreeMap<Integer, FunctionNode>();
		return buildStructureGraph(engine, treeMap);
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
			DFSPostOrder(postOrder, treeStart, new TreeSet<Node>());
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

	private boolean buildStructureGraph(Engine engine, TreeMap<String, InstructionNode> treeMap) {
		Collection<Node> functions = buildGraph(m_controlFlowGraph.getFunctionStartInstructions(), 
				m_functionStartPcs, engine, true, treeMap);
		if (functions == null) {
			return false;
		}
		Collection<Node> threads = buildGraph(m_controlFlowGraph.getThreadStartInstructions(), 
				m_functionStartPcs, engine, false, treeMap);
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

	private Collection<Node> buildGraph(Collection<InstructionNode> graphStartNodes, Map<Integer, FunctionNode> startPcs,
			Engine engine, boolean isFunction, TreeMap<String, InstructionNode> treeMap) {

		Collection<Node> graphs = new TreeSet<Node>();

		List<Node> orderedGraphs = new LinkedList<Node>(graphStartNodes);
		Collections.sort(orderedGraphs);
		for (Node graphStart : orderedGraphs) {

			
			//String functionName = graphStart.getInstructions().getFirst().getFunctionName();
			String functionName = getFunctionName(graphStart, treeMap);
			
			
			engine.provideFeedback("Processing " + functionName);

			Collection<Node> currentGraph = new TreeSet<Node>();
			collectCurrentGraphNodes(graphStart, currentGraph);
						
			Collection<Node> basicBlocks = new TreeSet<Node>();

			Node initialBasicBlock = reduceGraphToBasicBlocks(basicBlocks, graphStart, currentGraph);
			assert(!basicBlocks.isEmpty());
			FnopAnalyser fa = new FnopAnalyser();
			
			fa.analyse(initialBasicBlock, isFunction);
			fa.addFnopsToBasicBlocks(initialBasicBlock);
			
			assert(basicBlocks.contains(initialBasicBlock));
			
			if (!createStructure(basicBlocks, initialBasicBlock, engine, false)) {
				return null;
			}
			if(basicBlocks.size() != 1) {
				dumpTree(basicBlocks);
			}
			assert(basicBlocks.size() == 1);
			Node decomposedGraph = basicBlocks.iterator().next();
			

			FunctionNode fn = new FunctionNode(decomposedGraph, functionName);
			graphs.add(fn);
			
			setParent(fn);
			
			startPcs.put(graphStart.getStartPc(), fn);
			
		}
		return graphs;
	}

	private String getFunctionName(Node graphStart,
			TreeMap<String, InstructionNode> treeMap) {
		for(String functionName : treeMap.keySet()){
			InstructionNode instNode = treeMap.get(functionName);
			if(instNode.getStartPc() == graphStart.getStartPc()){
				return functionName;
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
				node.addBranchTarget(cfgToStructure.get(branch));
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

		Set<Node> intersection = new TreeSet<Node>();
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
				String fnName = m_functionStartPcs.get(pc).getFunctionName();
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
	private static final boolean verbose = true;
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
				if(verbose){
					System.out.print(node + " [label=\"" );
					System.out.print(node + ": \\n");
					if(node.isBlockNode() || node.isInstructionNode()){
						for(XtaInstruction inst : node.getInstructions()){
							System.out.print(inst  +"\\n");
						}
					}
					for(Node dom : node.getDominators()){
						System.out.print("dominator:" + dom + "\\n");
					}
					System.out.print( "\"");
					System.out.println("];");
				}
			}
			System.out.println("}\n");
			
		} else {
			System.out.println("# graph has only " + workingSet.size() + " nodes");
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
