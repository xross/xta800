package com.xmos.xta.core.engine.analysis.structural;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.memorymonitor.MemoryMonitorException;
import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableMap;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.controlflow.FunctionContainer;
import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.NullNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.SequenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.core.engine.analysis.structural.patterns.ConditionalPatternMatcher;
import com.xmos.xta.core.engine.analysis.structural.patterns.ConditionalTransformer;
import com.xmos.xta.core.engine.analysis.structural.patterns.LoopPatternMatcher;
import com.xmos.xta.core.engine.analysis.structural.patterns.NullRemover;
import com.xmos.xta.core.engine.analysis.structural.patterns.PanicTransformer;
import com.xmos.xta.core.engine.analysis.structural.patterns.PatternMatcher;
import com.xmos.xta.core.engine.analysis.structural.patterns.SelfLoopPattern;
import com.xmos.xta.core.engine.analysis.structural.patterns.SequencePatternMatcher;

public class StructuralAnalysisEngine {

	private static boolean debug_structure = false;
	private static boolean sanity_debug = false;

	private List<PatternMatcher> m_patternMatchers;

	public StructuralAnalysisEngine() {
		//never ever change the order of this
		m_patternMatchers = new ArrayList<PatternMatcher>();
		m_patternMatchers.add(new SelfLoopPattern());
		m_patternMatchers.add(new LoopPatternMatcher());
		m_patternMatchers.add(new ConditionalPatternMatcher());
		m_patternMatchers.add(new NullRemover());
		m_patternMatchers.add(new SequencePatternMatcher());	//uses dominators
		m_patternMatchers.add(new ConditionalTransformer());	//uses dominators
		m_patternMatchers.add(new PanicTransformer());			//uses dominators
	}

	public Node createStructure(Collection<Node> workingSet, Node treeStart,
			Engine engine, String jobPrefix) throws AbortedException, MemoryMonitorException {
		
		if (workingSet.size() == 1 && workingSet.iterator().next().getNextNodesSize() == 0) {
			return workingSet.iterator().next();
		}

		buildDominatorTree(workingSet, treeStart);

		boolean working = true;		
		boolean dominatorTreeUpToDate = true;
		
		while (working) {
			
			String feedback = jobPrefix + " - nodes left: " + workingSet.size();
			if (engine.getVerbosity() >= Config.VERBOSITY_DISPLAY_MEMORY_USAGE) {
				feedback += String.format("\nMemory Usage [max/total/free]: %d/%d/%d",
					Runtime.getRuntime().maxMemory(),
					Runtime.getRuntime().totalMemory(),
					Runtime.getRuntime().freeMemory());
			}
			engine.provideFeedback(feedback);
						
			List<Node> postOrder = new LinkedList<Node>();
			DFSPostOrder(postOrder, treeStart, new NonIteratableSet<Node>());
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
					
					engine.getAbort();
					if (engine.getMemoryMonitor().outOfMemoryAbort()) {
						throw new MemoryMonitorException();
					}
					
					if (pm.isMatch(node, workingSet, treeStart, engine)) {
						assert(sanityCheckGraph(workingSet));
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
		return workingSet.iterator().next();
	}
	
	private boolean sanityCheckGraph(Collection<Node> workingSet){
		if(sanity_debug){
			Set<Node> seen = new NonIteratableSet<Node>();
			for(Node node : workingSet){
				Set<Node> containedNodes = CommonMethods.getAllContainedNodes(node);
				for(Node contained : containedNodes){
					if(seen.contains(contained) && !(contained instanceof InstructionNode)){
						for(Node n :  CommonMethods.getAllContainedNodes(node)){
							if(!(n instanceof InstructionNode)){
								System.err.println("\t" + n);
							}
						}
						return false;
					} else {
						seen.add(contained);
					}
				}
			}
		}
		return true;
	}

	private void buildDominatorTree(Collection<Node> reachableNodes, Node treeStart) {
		//try to remove tree sets from this
		for (Node node : reachableNodes) {
			node.clearDominators();
			if (treeStart == node) {
				node.setDominatorSetComplete(false);
				node.addDominator(node);
			} else {
				node.setDominatorSetComplete(true);
			}
		}

		Set<Node> intersection = new IteratableSet<Node>();
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

	
	
	private boolean done;
	public void normaliseGraph(Node fn) {
		setParent(fn);
		done = false;
		while(!done){
			done =true;
			fn.traverse(new DefaultNodeTraverser() {
				public TraverseStatus foundSequenceNode(Node node) {

					for(Node child : node.iChildren()){
						if(child instanceof SequenceNode){
							SequenceNode sn = (SequenceNode)child;
							int insertPathAt =node.iChildren().indexOf(child);
							node.iChildren().remove(insertPathAt);
							for(Node pathChild : sn.getPath()){
								node.iChildren().add(insertPathAt++, pathChild);
							}
							done = false;
							return TraverseStatus.NO_CHILDREN;
						}
					}
					return TraverseStatus.CONTINUE;
				}

				public TraverseStatus foundFunctionNode(Node node) {
					for(Node child : node.iChildren()){
						if(child instanceof SequenceNode){
							SequenceNode sn = (SequenceNode)child;
							int insertPathAt = node.iChildren().indexOf(child);
							node.iChildren().remove(insertPathAt);
							for(Node pathChild : sn.getPath()){
								node.iChildren().add(insertPathAt++, pathChild);
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
		setParent(fn);
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
	
	private void setParent(Node node) {
		for (Node child : node.iChildren()){
			child.setParent(node);
			setParent(child);
		}
	}
	
	private boolean connectivitySanityCheck(Collection<Node> workingSet) {
		if(sanity_debug){
			int total = 0;
			for (Node node : workingSet) {
				total += node.getPrevNodes().size() - node.getNextNodes().size();
			}
			return (total == 0);
		}
		return true;
	}
	
	private void dumpTree(String text, Collection<Node> workingSet, boolean override) {
		if(debug_structure || override){
			StringBuffer sb = new StringBuffer();
			if (workingSet.size() > 1) {
				sb.append("digraph "+ text + "{");
				for (Node node : workingSet) {
					sb.append("node" + node.getId() + " [label=\"" + node + "\"];");
					for (Node next : node.getNextNodes()) {
						sb.append("node" + node.getId() + " -> node" + next.getId()+ ";");
					}
				}
				sb.append("}\n");

			} else {
				sb.append("# graph has only " + workingSet.size() + " nodes - " + text + "\n");
			}
			System.out.println( sb.toString());
		}
		return;
	}

	public Node makeFnContainerIntoStructure(FunctionContainer functionContainer, Engine engine) 
	    throws AbortedException, MemoryMonitorException {
		
		Set<Node> workingSet = new IteratableSet<Node>();
		workingSet.addAll(functionContainer.getFunctionContents());
		assert(workingSet.contains(functionContainer.getStartInstruction()));
		dumpTree("started_"+ functionContainer.getFunctionName(), workingSet, false);
		
		Node startNode = basicBlock(workingSet, functionContainer.getStartInstruction(), engine, functionContainer.getFunctionName());
		Node structureNode = null;
		structureNode = createStructure(workingSet, startNode, engine, "Converting CFG to structure");
		assert(workingSet.size() == 1);
		dumpTree("finished_"+ functionContainer.getFunctionName(), workingSet, false);
		Node structuralNode = workingSet.iterator().next();
		normaliseGraph(structuralNode);
		return structureNode;
	}
	
	
	public Node basicBlock(Set<Node> workingSet, Node originalStartNode, Engine engine, String string) {
		
		dumpTree("start_"+ string, workingSet, false);
		Set<Node> toDo = new IteratableSet<Node>();
		toDo.add(originalStartNode);
		Node firstNode = null;
		Set<Node> seenNodes = new NonIteratableSet<Node>();
		Set<Node> newWorkingSet = new IteratableSet<Node>();
		assert(!toDo.isEmpty());
		Map<Node, Set<Node>> nexts = new IteratableMap<Node, Set<Node>>();
		Map<Node, Node> instToBlock = new NonIteratableMap<Node, Node>();
		while(!toDo.isEmpty()){

			Node current = toDo.iterator().next();
			toDo.remove(current);

			List<InstructionNode> insts = new LinkedList<InstructionNode>();
			insts.add((InstructionNode)current);
			
			seenNodes.add(current);
			while(nextIsInSameBlock(current, workingSet, seenNodes, insts) ){
				Node next = getNextNodes(current, workingSet).iterator().next();
				insts.add((InstructionNode)next);
				current = next;
			}
			
			seenNodes.addAll(insts);

			BlockNode basicBlock = new BlockNode(insts);

			InstructionNode startOfBlock = basicBlock.getInstructionList().get(0);
			instToBlock.put(startOfBlock, basicBlock);
			nexts.put(basicBlock, current.getNextNodes());
			if(newWorkingSet.isEmpty()){
				firstNode = basicBlock;
			}
			newWorkingSet.add(basicBlock);

			for(Node next : current.getNextNodes()){
				if(next instanceof FunctionReferenceNode){
					//FunctionReferenceNode fnRef = (FunctionReferenceNode) next;
					//FunctionNode fnNode= new FunctionNode(fnRef.getFunctionContainer().getCachedFunction(engine), fnRef);
					FunctionNode fnNode= new FunctionNode();
					
					newWorkingSet.add(fnNode);
					seenNodes.add(fnNode);
					instToBlock.put(next, fnNode);
					nexts.put(fnNode, new IteratableSet<Node>(next.getNextNodes()));
					for(Node nextNext : next.getNextNodes()){
						if(!seenNodes.contains(nextNext)){
							toDo.add(nextNext);
						}
					}
				} else if(next instanceof FunctionNode){
					next.getPrevNodes().clear();
					newWorkingSet.add(next);
					seenNodes.add(next);
					instToBlock.put(next, next);
					nexts.put(next, new IteratableSet<Node>(next.getNextNodes()));
					for(Node nextNext : next.getNextNodes()){
						if(!seenNodes.contains(nextNext)){
							toDo.add(nextNext);
						}
					}
					next.getNextNodes().clear();
				} else if(next instanceof NullNode){
					next.getPrevNodes().clear();
					newWorkingSet.add(next);
					seenNodes.add(next);
					instToBlock.put(next, next);
					nexts.put(next, new IteratableSet<Node>(next.getNextNodes()));
					next.getNextNodes().clear();
				} else if(!seenNodes.contains(next)){
					toDo.add(next);
				}
			}
		}
		for(Node node : nexts.keySet()){
			for(Node next : nexts.get(node)){
				CommonMethods.connect(instToBlock.get(next), node);
			}
		}
		
		Set<Node> seen = new NonIteratableSet<Node>();
		Set<Node> terminals = new IteratableSet<Node>();
		findEndPoints(firstNode, seen, terminals);
		
		NullNode n = new NullNode();
			
		if(terminals.size() > 1){	
			for(Node node : terminals){
				CommonMethods.connect(n, node);
			}
			newWorkingSet.add(n);
		}
		
		workingSet.clear();
		workingSet.addAll(newWorkingSet);
		dumpTree("after_BB_"+ string, workingSet, false);
		return firstNode;
	}
	
	private void findEndPoints(Node inst, Set<Node> seen, Set<Node> terminals) {
		Set<Node> following = new IteratableSet<Node>();
		following.add(inst);
		while(!following.isEmpty()){
			Node current = following.iterator().next();
			seen.add(current);
			if(current.getNextNodesSize() == 0){
				terminals.add(current);
			} else {
				for(Node next : current.getNextNodes()){
					if(!seen.contains(next)){
						following.add(next);
					}
				}
			}
			following.remove(current);
		}
	}

	private boolean nextIsInSameBlock(Node current, Set<Node> workingSet, Set<Node> seenNodes, List<InstructionNode> insts) {
		if(getNextNodes(current, workingSet).size() != 1){
			return false;
		}
		Node next = getNextNodes(current, workingSet).iterator().next();
		
		if(seenNodes.contains(next)){
			return false;
		}
		
		if(next instanceof NullNode) {
			return false;
		}
		
		if(!(next instanceof InstructionNode)) {
			return false;
		}
		if(getPrevNodes(next, workingSet).size() > 1){
			return false;
		}
		if( getPrevNodes(current, workingSet).size() > 1 && insts.get(0) != current){
			return false;
		}

		if(current instanceof InstructionNode){
			if(((InstructionNode)current).getXtaInstruction().getReturn()){
				return false;
			}
			if(((InstructionNode)current).getXtaInstruction().isLink()){
				return false;
			}
		}
		return true;
	}
	
	private Set<Node> getNextNodes(Node node, Set<Node> workingSet){
		Set<Node> next = new IteratableSet<Node>(node.getNextNodes());
		next.retainAll(workingSet);
		return next;
	}

	private Set<Node> getPrevNodes(Node node, Set<Node> workingSet){
		Set<Node> prev = new IteratableSet<Node>(node.getPrevNodes());
		prev.retainAll(workingSet);
		return prev;
	}
}
