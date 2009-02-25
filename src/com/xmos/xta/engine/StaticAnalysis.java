package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;

public class StaticAnalysis {
	
	enum RouteStatus {
		COMPLETED, ABORTED, ERROR, ANALYSING
	}
	
	private ArrayList<BasicBlock> m_nodes;
	private int m_startPc;
	private TreeSet<Integer> m_endPcs; 
	private ArrayList<PathBasicBlock> m_completedPaths;
	private ArrayList<PathBasicBlock> m_allBasicBlocks;
	private String m_analysisString = "";
	private Engine m_engine;
	private ArrayList<Unknown> m_unknowns;
	
	
	public StaticAnalysis(int startPc, TreeSet<Integer> endPcs, 
			ArrayList<BasicBlock> nodes, Engine engine){
		m_completedPaths = new ArrayList<PathBasicBlock>();
		m_allBasicBlocks = new ArrayList<PathBasicBlock>();
		m_startPc = startPc;
		m_endPcs =endPcs;
		m_analysisString = "";
		m_nodes = nodes;
		m_engine = engine;
		m_unknowns = new ArrayList<Unknown>();
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
	
	public void analyse(){
		m_allBasicBlocks.clear();
		BasicBlock startNode = getNode(m_startPc);
		if(startNode == null){
			//error
			return;
		}
		PathBasicBlock startBasicBlock = new PathBasicBlock(startNode, null);
		Stack<Integer> stack = new Stack<Integer>();
		Stack<LinkedList<BasicBlock>> nodeContext = new Stack<LinkedList<BasicBlock>>();
		nodeContext.push(new LinkedList<BasicBlock>());
		followNode(startBasicBlock, stack, nodeContext);
		
		//filterPaths();
		sortPaths();
		makeCompleteStatus();
		//return m_status;

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
		
		boolean finalBlock = reachedEnd(currentBasicBlock.getThisBasicBlock().getInstructions(), m_endPcs);
		
		if(finalBlock){
			m_completedPaths.add(currentBasicBlock);
			return;
		} else {
			PathBasicBlock loopBlock = formsLoop(currentBasicBlock, nodeContext);
			if(loopBlock!=null){
				//this must be true by design of the algorithm
				assert(currentBasicBlock.getParent()!=null);
				loopBlock.getLoopPaths().add(currentBasicBlock.getParent());
				//FIXME - may be totally wrong
				currentBasicBlock.getLoopPathAttributes().put(loopBlock, new PathAttributes());
				return;
			}
			XtaInstruction lastInstruction = currentBasicBlock.getThisBasicBlock().getInstructions().getLast();
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
				for(BasicBlock nextNode : currentBasicBlock.getThisBasicBlock().getNextNodes()){
					if(lastInstruction.getBranchLink()){
						Stack<Integer> newStack = (Stack<Integer>) stack.clone();
						newStack.add(lastInstruction.getPc() + lastInstruction.getNumBytes());
						Stack<LinkedList<BasicBlock>> newContext = new Stack<LinkedList<BasicBlock>>();
						copyStack(nodeContext, newContext);
						newContext.push(new LinkedList<BasicBlock>());
						PathBasicBlock nextPathBasicBlock = new PathBasicBlock(nextNode, currentBasicBlock);
						followNode(nextPathBasicBlock, newStack, newContext);
					} else {
						Stack<Integer> newStack = (Stack<Integer>) stack.clone();
						Stack<LinkedList<BasicBlock>> newContext = new Stack<LinkedList<BasicBlock>>();
						copyStack(nodeContext, newContext);
						newContext.peek().add(currentBasicBlock.getThisBasicBlock());
						PathBasicBlock nextPathBasicBlock = new PathBasicBlock(nextNode, currentBasicBlock);
						followNode(nextPathBasicBlock, newStack, newContext);
					}
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
		
		if(!nodeContext.peek().contains(currentNode.getThisBasicBlock())) return null;
		PathBasicBlock parent = currentNode.getParent();
		while (parent != null) {
			if(!nodeContext.peek().contains(parent.getThisBasicBlock())) return null;
			if (parent.getThisBasicBlock() ==  currentNode.getThisBasicBlock()) {
				return parent;
			}
			parent = parent.getParent();
		}
		return null;
	}
	
	
	private boolean reachedEnd(LinkedList<XtaInstruction> instructions, TreeSet<Integer> pcs) {
		for(XtaInstruction inst : instructions){
			if(pcs.contains(inst.getPc())){
				return true;
			}
		}
		return false;
	}

	private BasicBlock getNode(int pc) {
		for(BasicBlock node : m_nodes){
			for(XtaInstruction inst : node.getInstructions()){
				if(inst.getPc() == pc) return node;
			}
		}
		return null;
	}
	
	public void dumpNodes(){
		for(PathBasicBlock bb :m_completedPaths){
			dumpNode(bb, 0);
			System.out.println();
		}
	}

	private void dumpNode(PathBasicBlock bb, int indent){
		String t = "";
		for(int i=0;i< indent;i++){
			t+="\t";
		}
		for(PathBasicBlock pbb : bb.getPathBlocks()){
			for(PathBasicBlock loop : pbb.getLoopPaths()){
				dumpNode(loop, indent+1);
			}
			
			for(XtaInstruction inst : pbb.getThisBasicBlock().getInstructions()){
				for(int pc : pbb.getThisBasicBlock().getIBufferStates().get(0).m_fnops){
					if(pc == inst.getPc()) System.out.println(t+"FNOP");
				}
				System.out.println(t+inst);
			}
		}
		bb.doneWithPathBlocks();
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
	
}
