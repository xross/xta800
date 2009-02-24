package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;

public class StaticAnalysis {
	
	private ArrayList<BasicBlock> m_nodes;
	private int m_startPc;
	private TreeSet<Integer> m_endPcs; 
	//private ArrayList<LinkedList<BasicBlock>> m_pathList;
	
	private ArrayList<PathBasicBlock> m_completedPaths;
	
	private boolean m_analysisReady;
	
	public StaticAnalysis(){
		

		m_completedPaths = new ArrayList<PathBasicBlock>();
		m_analysisReady = false;
	}
	
	public void setNodes(ArrayList<BasicBlock> nodes){
		m_nodes = nodes;
	}
	
	public void setEndPoints(int startPc, TreeSet<Integer> endPcs){
		m_endPcs = endPcs;
		m_startPc = startPc;
		if(m_nodes!= null){
			m_analysisReady = true;
		} else {
			m_analysisReady = false;
		}
	}
	
	public void Analyse(){
		if(!m_analysisReady) {
			//error
			return;
		}
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
		
	}
	
	private void followNode(
		PathBasicBlock currentBasicBlock, 
		Stack<Integer> stack, 
		Stack<LinkedList<BasicBlock>> nodeContext) {
		
		
		
		//nodeList.add(currentBasicBlock);	
		
		boolean finalBlock = reachedEnd(currentBasicBlock.getThisBasicBlock().getInstructions(), m_endPcs);
		
		if(finalBlock){
			m_completedPaths.add(currentBasicBlock);
			return;
		} else {
			
			if(formsRecursion(currentBasicBlock, nodeContext)){
				//error
				//System.err.println("error");
				
				return;
			}
			
			//int loopIndex = formsLoop(currentBasicBlock, nodeContext);
			
			//PathBasicBlock loopBlock = formsLoop(currentBasicBlock, nodeContext);
			
			
			//if(loopBlock!=null){
				
				//System.
				
				//LinkedList<BasicBlock> currentContext = nodeContext.peek();
				
				//BasicBlock formsLoopWith = currentContext.get(loopIndex -1);
				
				//PathBasicBlock nextPathBasicBlock = new PathBasicBlock(formsLoopWith, currentBasicBlock);
				
				//loopBlock.getLoopPaths().add(nextPathBasicBlock);
			//	return;
			//}
			
			XtaInstruction lastInstruction = currentBasicBlock.getThisBasicBlock().getInstructions().getLast();
			
			
			if(lastInstruction.getReturn() && stack.size()>0){
				//known returns
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

	private boolean formsRecursion(PathBasicBlock currentNode, Stack<LinkedList<BasicBlock>> nodeContext) {
		LinkedList<BasicBlock> notInContext = new LinkedList<BasicBlock>();
		for(LinkedList<BasicBlock> list : nodeContext){
			if(nodeContext.indexOf(list)< nodeContext.size()-1){
				notInContext.addAll(list);
			}
		}
		return notInContext.contains(currentNode.getThisBasicBlock());
	}
	
	private int formsLoop(PathBasicBlock currentNode, Stack<LinkedList<BasicBlock>> nodeContext) {
		LinkedList<BasicBlock> currentContext = nodeContext.peek();
		return currentContext.indexOf(currentNode.getThisBasicBlock());
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
			for(XtaInstruction inst : pbb.getThisBasicBlock().getInstructions()){
				for(int pc : pbb.getThisBasicBlock().getIBufferStates().get(0).m_fnops){
					if(pc == inst.getPc()) System.out.println(t+"FNOP");
				}
				System.out.println(t+inst);
			}
			for(PathBasicBlock loop : pbb.getLoopPaths()){
				dumpNode(loop, indent+1);
			}
		}
	}

}
