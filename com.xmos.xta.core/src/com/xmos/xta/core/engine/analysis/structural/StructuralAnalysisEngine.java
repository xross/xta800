package com.xmos.xta.core.engine.analysis.structural;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
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
	
	private static final boolean REGISTER_CONFIG_ON_GLOBAL_TREE = false;
	
	private StructureGraph m_structureGraph;
	private List<PatternMatcher> m_patternMatchers;
	private Architecture m_arch;
	
	public StructuralAnalysisEngine(XeLoader xeLoader) {
		m_arch = xeLoader.getArchitecture();
		setUpPatternMatcher();
	}

	public StructuralAnalysisEngine() {
		setUpPatternMatcher();
	}

	public StructureGraph getStructureGraph() {
		return m_structureGraph;
	}

	public boolean createStructure(Collection<Node> workingSet, Node treeStart, 
			Engine engine, String jobPrefix) throws AbortedException {
		
		if (workingSet.size() == 1 && workingSet.iterator().next().getNextNodesSize() == 0) {
			return true;
		}
		
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
					engine.getAbort();

					if (pm.isMatch(node, workingSet, treeStart)) {
						treeStart = pm.getTreeStart();
						assert (workingSet.contains(treeStart));
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

	public boolean analyse(Engine engine, Set<InstructionNode> functionStartNodes, 
			Map<String, List<InstructionNode>> functionNameToInstructions, Sections sections, 
			int nodeId,  int coreId) throws AbortedException {
		
		
		//for each function node there is a set of function references in this map
		Map<FunctionNode, Set<FunctionReferenceNode>> functionReferencesWithinFunction 
			= new TreeMap<FunctionNode, Set<FunctionReferenceNode>>();
	
		
		Map<FunctionNode, Set<InstructionNode>> functionToEndNodes = new TreeMap<FunctionNode, Set<InstructionNode>>();
		Map<FunctionNode, InstructionNode> functionToStartNode = new TreeMap<FunctionNode, InstructionNode>();
		
		buildGraph(functionStartNodes, engine, functionNameToInstructions, functionReferencesWithinFunction, 
				functionToEndNodes, functionToStartNode);

		engine.provideFeedback("Linking functions...");
		linkFunctions(engine, functionReferencesWithinFunction);
		
		//dumpCallTree(functionReferencesWithinFunction);
		
		engine.provideFeedback("Analysing call graph...");
		detectRecursion(functionReferencesWithinFunction, sections.getRecursiveFunctions(nodeId, coreId));
		
		assert(functionToEndNodes != null);
		assert(!functionToEndNodes.isEmpty());
		
		m_structureGraph = new StructureGraph(functionToEndNodes, functionToStartNode, functionReferencesWithinFunction);
		
		
		if (REGISTER_CONFIG_ON_GLOBAL_TREE) {
			// If enabled, this allows the printing of the global structure to 
			// display the node times. It is disabled by default for efficiency reasons,
			// as printing the global structure is not a user activity. 
			engine.provideFeedback("Registering config...");
			Config config = engine.getConfig();
			Architecture architecture = engine.getXeLoader().getArchitecture();
			setConfig(config, architecture, m_structureGraph.getFunctions());

		}
		assert(linkSanityCheck(functionReferencesWithinFunction));
		engine.provideFeedback("Complete");
		return true;
	}
	
	private void dumpCallTree(
			Map<FunctionNode, Set<FunctionReferenceNode>> functionReferencesWithinFunction) {
		System.out.println("digraph g {");
		for(FunctionNode fn : functionReferencesWithinFunction.keySet()){
			for(FunctionReferenceNode frn : functionReferencesWithinFunction.get(fn)){
				if(frn.isKnownFunction()){
					System.out.println(fn.getFunctionName() + " -> " + frn.getFunctionName());
				}
			}
		}
		System.out.println("}");
		
	}

	private void buildGraph (
			Set<InstructionNode> functionStartNodes,
			Engine engine,  
			Map<String, List<InstructionNode>> functionNameToInstructions,
			Map<FunctionNode, Set<FunctionReferenceNode>> functionReferencesWithinFunction, 
			Map<FunctionNode, Set<InstructionNode>> functionToEndNodes, 
			Map<FunctionNode, InstructionNode> functionToStartNode) throws AbortedException {
		
		//TODO not sure that sorting is necessary
		List<Node> orderedGraphs = new LinkedList<Node>(functionStartNodes);
		Collections.sort(orderedGraphs);
		
		engine.startAnalysis("Analysing functions", orderedGraphs.size());

		
		for (Node graphStart : orderedGraphs) {
			String functionName = getFunctionName(graphStart, functionNameToInstructions);
			String jobName = "Processing " + functionName;
			
			Collection<Node> currentGraph = new TreeSet<Node>();
			Set<FunctionReferenceNode> functionReferences = new TreeSet<FunctionReferenceNode>();
			
			findAllNodesInCurrentFunction(graphStart, currentGraph);
			Set<InstructionNode> endNodes = new TreeSet<InstructionNode>();
			for(Node node : currentGraph){
				if(node instanceof NullNode){
					
					for(Node prev : node.getPrevNodes()){
						if(prev instanceof InstructionNode){
							endNodes.add((InstructionNode) prev);
						}
					}
					
				}
			}
			
			Collection<Node> basicBlocks = new TreeSet<Node>();
			
			Node initialBasicBlock = reduceGraphToBasicBlocks(basicBlocks, graphStart, currentGraph, functionReferences);

			/*TODO --make the fnop analyser work efficiently
			FnopAnalyser fa = new FnopAnalyser();
			fa.analyse(initialBasicBlock);
			fa.addFnopsToBasicBlocks(initialBasicBlock);
			*/

			if (createStructure(basicBlocks, initialBasicBlock, engine, jobName)) {
				assert(basicBlocks.size() == 1);
				
				Node decomposedGraph = basicBlocks.iterator().next();

				FunctionNode fn = new FunctionNode(decomposedGraph, functionName);
				
				functionReferencesWithinFunction.put(fn, functionReferences);	
				functionToEndNodes.put(fn, endNodes);
				functionToStartNode.put(fn, (InstructionNode)graphStart);
				normaliseGraph(fn);
				
			} else {
				//report an error
			}
			
			engine.provideWorkDone(1);
		}
		
		return;
	}

	private boolean done;
	
	public void normaliseGraph(Node fn) {
		
		setParent(fn);
		
		done = false;
		//this bit removes the path within paths 
		while(!done){
			done =true;
			fn.traverse(new Stack<Node>(),  new DefaultNodeTraverser() {
				public TraverseStatus foundSequenceNode(Node node, Stack<Node> ancestors) {
					for(Node child : node.getIndirectChildren()){
						if(child.isSequenceNode()){
							int insertPathAt =node.getIndirectChildren().indexOf(child);
							node.getIndirectChildren().remove(insertPathAt);
							for(Node pathChild : child.getIndirectChildren()){
								node.getIndirectChildren().add(insertPathAt++, pathChild);
							}
							done = false;
							return TraverseStatus.NO_CHILDREN;
						}
					}
					return TraverseStatus.CONTINUE;
				}
				
				public TraverseStatus foundFunctionNode(Node node, Stack<Node> ancestors) {
					for(Node child : node.getIndirectChildren()){
						if(child.isSequenceNode()){
							int insertPathAt =node.getIndirectChildren().indexOf(child);
							node.getIndirectChildren().remove(insertPathAt);
							for(Node pathChild : child.getIndirectChildren()){
								node.getIndirectChildren().add(insertPathAt++, pathChild);
							}
							done = false;
							return TraverseStatus.NO_CHILDREN;
						}
					}
					return TraverseStatus.CONTINUE;
				}
		    });
			setParent(fn);
		}		
	}

	private String getFunctionName(Node graphStart,
			Map<String, List<InstructionNode>> functionNameToInstructions) {
		for(String functionName : functionNameToInstructions.keySet()){
			for(InstructionNode fnInst : functionNameToInstructions.get(functionName)){
				if(fnInst.getStartPc() == graphStart.getStartPc()){
					return functionName;
				}
			}
		}
		assert(false);
		return null;
	}

	private Node reduceGraphToBasicBlocks(Collection<Node> basicBlocks, Node initialBasicBlock,
			Collection<Node> currentGraph, Set<FunctionReferenceNode> functionReferences) {

		Set<Node> seen = new TreeSet<Node>();
		Map<Node, Node> cfgToStructure = new TreeMap<Node, Node>();
		Map<Node, Node> endPoint = new TreeMap<Node, Node>();
		Node treeStart = initialBasicBlock;
		for (Node node : currentGraph) {
			if (!seen.contains(node)) {
				if (node instanceof InstructionNode) {
					BasicBlockMatcher bbpm = new BasicBlockMatcher();
					if (bbpm.match(node, treeStart, currentGraph)) {
						seen.addAll(getContainedNodes(bbpm.getBlock()));
						BlockNode basicBlock = bbpm.getBlock();
						basicBlocks.add(basicBlock);
						treeStart = bbpm.getTreeStart();
						cfgToStructure.put(node, basicBlock);
						endPoint.put(basicBlock, bbpm.getLastBlock());
					}

				} else if(node instanceof FunctionReferenceNode){
					seen.add(node);
					FunctionReferenceNode fnRef = (FunctionReferenceNode)node;
					FunctionReferenceNode copy = new FunctionReferenceNode(fnRef);
					functionReferences.add(copy);
					basicBlocks.add(copy);
					cfgToStructure.put(node, copy);
					endPoint.put(copy, node);

				} else {
					assert(node instanceof NullNode);
					seen.add(node);
					NullNode copy = new NullNode();
					basicBlocks.add(copy);
					cfgToStructure.put(node, copy);
					endPoint.put(copy, node);
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
		assert(!basicBlocks.isEmpty());
		assert(basicBlocks.contains(treeStart));
		return treeStart;
	}

	private boolean haltsProcessor(Node basicBlock) {
		if(basicBlock instanceof FunctionReferenceNode) return false;
		LinkedList<XtaInstruction> instruction = basicBlock.getInstructions();
		if (instruction.size() > 1) {
			InstructionMnemonics im = new InstructionMnemonics(m_arch);
			if (im.isClre(instruction.get(instruction.size() - 2)) && im.isWait(instruction.getLast())) {
				return true;
			}
		}
		return false;
	}

	private void findAllNodesInCurrentFunction(Node node, Collection<Node> currentGraph) {
		Collection<Node> toDo = new TreeSet<Node>();
		toDo.add(node);
		while (toDo.size() > 0) {
			Collection<Node> nextToDo = new TreeSet<Node>();
			for (Node doNode : toDo) {
				currentGraph.add(doNode);
				for (Node next : doNode.getNextNodes()) {
					if (!currentGraph.contains(next)) {
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
	
	private void linkFunctions(Engine engine,
			Map<FunctionNode, Set<FunctionReferenceNode>> functionReferencesWithinFunction) {
		
		Map<Long, FunctionNode> pcToFunctionStartNode = new HashMap<Long, FunctionNode>();
		for (FunctionNode f : functionReferencesWithinFunction.keySet()) {
			pcToFunctionStartNode.put(f.getStartPc(), f);
		}
		
		for (FunctionNode f : functionReferencesWithinFunction.keySet()) {
			
			for (FunctionReferenceNode g : functionReferencesWithinFunction.get(f)) {
				
				if(g.isKnownFunction()){
					FunctionNode link = pcToFunctionStartNode.get(g.getFunctionStartPc());
					String functionName = link.getFunctionName();
					
					g.setFunction(functionName, link);
					
					link.addCallingFunction(f);
					
				}
			}

		}	
		assert(linkSanityCheck(functionReferencesWithinFunction));
		
	}
	private boolean linkSanityCheck(Map<FunctionNode, Set<FunctionReferenceNode>> functionReferencesWithinFunction){
		boolean allLinkedProperly = true;
		for(Set<FunctionReferenceNode> functionReferences : functionReferencesWithinFunction.values()){
			for(FunctionReferenceNode fnRef : functionReferences){
				allLinkedProperly &= fnRef.verifyLinkProcess();
			}
		}
		return allLinkedProperly;
	}
	
	private void detectRecursion(Map<FunctionNode, Set<FunctionReferenceNode>> functionReferencesWithinFunction,
			Collection<String> recursiveFunctions) {

		//first build up a set of functions that cannot be called (i.e. thread starts)
		//FIXME this is not necessarily true
		Set<FunctionNode> nonBranchedToFunctions = new HashSet<FunctionNode>();
		
		nonBranchedToFunctions.addAll(functionReferencesWithinFunction.keySet());
		
		for (FunctionNode f : functionReferencesWithinFunction.keySet()) {
			for(FunctionReferenceNode fr : functionReferencesWithinFunction.get(f)){
				if(fr.isKnownFunction()){
					nonBranchedToFunctions.remove(fr.getFunctionNode());
				}
			}
		}
		System.out.println("functions that were not branched to are: " + nonBranchedToFunctions);
		Set<FunctionReferenceNode> recursiveFunctionNodes = new HashSet<FunctionReferenceNode>();
		Set<FunctionNode> seen = new HashSet<FunctionNode>();
		for(FunctionNode start : nonBranchedToFunctions){
			walk(start, seen, functionReferencesWithinFunction, recursiveFunctionNodes);
		}
		
		System.out.println(recursiveFunctions);
		assert(linkSanityCheck(functionReferencesWithinFunction));
	}
	
	private void walk(
			FunctionNode start,
			Set<FunctionNode> seen,
			Map<FunctionNode, Set<FunctionReferenceNode>> functionReferencesWithinFunction,
			Set<FunctionReferenceNode> recursiveFunctionReferenceNodes) {
		seen.add(start);
		for(FunctionReferenceNode frn : functionReferencesWithinFunction.get(start)){
			if(frn.isKnownFunction()){
				FunctionNode next = frn.getFunctionNode();
				if(seen.contains(start)){
					recursiveFunctionReferenceNodes.add(frn);
					//start.setFormsRecursion();
					//start.setUnresolved(true);
				} else {
					walk(next, seen, functionReferencesWithinFunction, recursiveFunctionReferenceNodes);
				}
			}
		}
		seen.remove(start);
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
				public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {
					((InstructionNodeAttributes)node.getAttributes()).setConfig(config, architecture);				
					return TraverseStatus.CONTINUE;
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
						if(node.isBlockNode()){
							
							BlockNode b = (BlockNode)node;
							
							if(b.getInstructions().size() > 0 && b.getInstructions().getLast().getNumBytes() == 4){
								sb.append(node + " -> " + next + " [style=dotted, penwidth=5, color=red];");
							} else {
								sb.append(node + " -> " + next + " [style=dotted];");
							}
							
						} else {
							sb.append(node + " -> " + next + " [style=dotted];");
						}

					} else {
						sb.append(node + " -> " + next + ";");
					}
				}
				
				if (node.isFunctionNode()) {
					//sb.append(node + " [label=\""+ ((FunctionNode) node).getFunctionName()+ "\", style=filled, color=green];");
					sb.append(node + " [label=\"func\"];");
				} else if(node.isBlockNode()){
					
					BlockNode b = (BlockNode)node;
					
					int bytes = 0;
					for(XtaInstruction inst : b.getInstructions()){
						bytes +=inst.getNumBytes();
					}
					sb.append(node + " [label=\"bytes: " + bytes + "\"];");
				}
			}
			sb.append("}\n");

		} else {
			sb.append("# graph has only " + workingSet.size() + " nodes");
		}
		return sb.toString();
	}
	
	//TODO work out what endNodes are for
	private void removeEndNodes(Collection<Node> workingSet) {
		boolean done = false;
		while (!done) {
			done = true;
			for (Node node : workingSet) {
				if (node.isEndNode()) {
					assert(node.getPrevNodesSize() == 1);
					if (node.getPrevNode().getNextNodesSize() == 1 && node.getNextNodesSize() == 0) {
						CommonMethods.disconnect(node, node.getPrevNode());
						workingSet.remove(node);
						done = false;
						break;
					}
				}
			}
		}
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
			public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {		
				nodes.add(node);
				return TraverseStatus.CONTINUE;
			}
	    });
		return nodes;
	}
}
