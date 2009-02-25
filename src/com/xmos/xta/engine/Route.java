package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;

public class Route {
	
	enum RouteStatus {
		COMPLETED, ABORTED, ERROR
	}
	private static int m_pathCount;
	private ArrayList<BasicBlock> m_nodes;
	private int m_startPc;
	private TreeSet<Integer> m_endPcs; 
	private ArrayList<PathBasicBlock> m_completedPaths;
	private ArrayList<PathBasicBlock> m_allBasicBlocks;
	private String m_analysisString = "";
	private ArrayList<Unknown> m_unknowns;
	private ArrayList<Unresolved> m_unresolvedPaths;

	public Route(int startPc, TreeSet<Integer> endPcs, ArrayList<BasicBlock> nodes){
		m_completedPaths = new ArrayList<PathBasicBlock>();
		m_allBasicBlocks = new ArrayList<PathBasicBlock>();
		m_unresolvedPaths = new ArrayList<Unresolved>();
		m_startPc = startPc;
		m_endPcs =endPcs;
		m_analysisString = "";
		m_nodes = nodes;
		m_unknowns = new ArrayList<Unknown>();
	}
	
	public ArrayList<PathBasicBlock> getCompletedPaths(){
		return m_completedPaths;
	}
	
	public ArrayList<Unresolved> getUnresolvedPaths(){
		return m_unresolvedPaths;
	}
	
	public String getStatusString() {
		return m_analysisString;
	}
	
	public ArrayList<Unknown> getUnknowns() {
		return m_unknowns;
	}
	
	public ArrayList<PathBasicBlock> getAllBasicBlocks(){
		return m_allBasicBlocks;
	}
	
	public void invalidateAllBlocks(){
		for(PathBasicBlock block : m_allBasicBlocks){
			block.getBlockStats().invalidateAll();
		}
	}
	
	public RouteStatus analyse(){
		m_allBasicBlocks.clear();
		BasicBlock startNode = getNode(m_startPc);
		if(startNode == null){
			return RouteStatus.ERROR;
		}
		PathBasicBlock startBasicBlock = new PathBasicBlock(startNode, null);
		Stack<Integer> stack = new Stack<Integer>();
		Stack<LinkedList<BasicBlock>> nodeContext = new Stack<LinkedList<BasicBlock>>();
		nodeContext.push(new LinkedList<BasicBlock>());
		followNode(startBasicBlock, stack, nodeContext);
		sortPaths();
		makeCompleteStatus();
		return RouteStatus.COMPLETED;
	}
	
	private void sortPaths() {
		Collections.sort(m_completedPaths);
		for(PathBasicBlock block : m_allBasicBlocks){
			Collections.sort(block.getLoopPaths());
		}
	}
	
	@SuppressWarnings("unchecked")
	private void followNode(
			
		PathBasicBlock currentBasicBlock, 
		Stack<Integer> stack, 
		Stack<LinkedList<BasicBlock>> nodeContext) {
		if(reachedEnd(currentBasicBlock, m_endPcs)){
			m_completedPaths.add(currentBasicBlock);
			currentBasicBlock.setPath(m_pathCount++);
			return;
		} else {
			PathBasicBlock loopBlock = formsLoop(currentBasicBlock, nodeContext);
			if(loopBlock!=null){
				//this must be true by design of the algorithm
				assert(currentBasicBlock.getParent()!=null);
				currentBasicBlock.getParent().setPath(m_pathCount++);
				loopBlock.getLoopPaths().add(currentBasicBlock.getParent());
				loopBlock.getLoopPathAttributes().put(currentBasicBlock.getParent(), new PathAttributes());
				return;
			}
			XtaInstruction lastInstruction = currentBasicBlock.getBasicBlock().getInstructionStream().getLast();
			if(lastInstruction.getReturn() && stack.size()>0){
				BasicBlock nextNode = getNode(stack.peek());
				Stack<LinkedList<BasicBlock>> newContext = new Stack<LinkedList<BasicBlock>>();
				copyStack(nodeContext, newContext);
				newContext.pop();
				Stack<Integer> newStack = (Stack<Integer>) stack.clone();
				newStack.pop();
				PathBasicBlock nextPathBasicBlock = new PathBasicBlock(nextNode, currentBasicBlock);
				followNode(nextPathBasicBlock, newStack, newContext);
			} else {
				for(BasicBlock nextNode : currentBasicBlock.getBasicBlock().getNextNodes()){
					Stack<Integer> newStack = (Stack<Integer>) stack.clone();
					Stack<LinkedList<BasicBlock>> newContext = new Stack<LinkedList<BasicBlock>>();
					copyStack(nodeContext, newContext);
					if(lastInstruction.getBranchLink()){
						newStack.add(lastInstruction.getPc() + lastInstruction.getNumBytes());
						newContext.push(new LinkedList<BasicBlock>());
					} else {
						newContext.peek().add(currentBasicBlock.getBasicBlock());
					}
					PathBasicBlock nextPathBasicBlock = new PathBasicBlock(nextNode, currentBasicBlock);
					followNode(nextPathBasicBlock, newStack, newContext);
				}
			}
		}	
	}

	private void copyStack(Stack<LinkedList<BasicBlock>> nodeContext, Stack<LinkedList<BasicBlock>> temp) {
		Iterator<LinkedList<BasicBlock>> iter = nodeContext.iterator();
		while (iter.hasNext()) {
			LinkedList<BasicBlock> pcs = iter.next();
			LinkedList<BasicBlock> copy = new LinkedList<BasicBlock>(pcs);
			temp.add(copy);
		}
	}

	private PathBasicBlock formsLoop(PathBasicBlock currentNode, Stack<LinkedList<BasicBlock>> nodeContext) {
		if(!nodeContext.peek().contains(currentNode.getBasicBlock())) return null;
		PathBasicBlock parent = currentNode.getParent();
		while (parent != null) {
			if(!nodeContext.peek().contains(parent.getBasicBlock())) return null;
			if (parent.getBasicBlock() ==  currentNode.getBasicBlock()) {
				return parent;
			}
			parent = parent.getParent();
		}
		return null;
	}
	
	private boolean reachedEnd(PathBasicBlock currentBasicBlock, TreeSet<Integer> pcs) {
		for(XtaInstruction inst : currentBasicBlock.getBasicBlock().getInstructionStream()){
			if(pcs.contains(inst.getPc())){
				return true;
			}
		}
		return false;
	}

	private BasicBlock getNode(int pc) {
		for(BasicBlock node : m_nodes){
			for(XtaInstruction inst : node.getInstructionStream()){
				if(inst.getPc() == pc) return node;
			}
		}
		return null;
	}

	public boolean setLoopIterations(int pathId, boolean absolute, int iterations) {
		for (PathBasicBlock block : m_allBasicBlocks) {
			for (PathBasicBlock loopPath : block.getLoopPaths()) {
				if (loopPath.getPathId() == pathId) {
					PathAttributes attributes = block.getLoopPathAttributes(loopPath);
					attributes.setNumIterations(iterations, absolute);
					return true;
				}
			}
		}	
		return false;		
	}
	
	private void makeCompleteStatus(){
		m_analysisString = "from: 0x" + Integer.toHexString(m_startPc) + " to: ";
		for(int end : m_endPcs){
			m_analysisString += "0x" + Integer.toHexString(end) + ", ";
		}
		m_analysisString = m_analysisString.substring(0,m_analysisString.length()-2);
		return;
	}

	public PathBasicBlock getBlock(int id) {
		for(PathBasicBlock pbb : m_allBasicBlocks){
			if(pbb.getBlockId() == id){
				return pbb;
			}
		}
		return null;
	}

	public PathBasicBlock getPath(int id) {
		for(PathBasicBlock pbb : m_allBasicBlocks){
			if(pbb.getPathId() == id){
				return pbb;
			}
		}
		return null;
	}
}
