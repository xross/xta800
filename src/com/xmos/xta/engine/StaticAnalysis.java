package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;

public class StaticAnalysis {
	
	private ArrayList<Node> m_nodes;
	private int m_startPc;
	private TreeSet<Integer> m_endPcs; 
	private ArrayList<LinkedList<Node>> m_pathList;
	private boolean m_analysisReady;
	
	public StaticAnalysis(){
		m_pathList = new ArrayList<LinkedList<Node>>();
		m_analysisReady = false;
	}
	
	public void setNodes(ArrayList<Node> nodes){
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
		Node startNode = getNode(m_startPc);
		
		if(startNode == null){
			//error
			return;
		}
		LinkedList<Node> emptyList = new LinkedList<Node>();
		Stack<Integer> stack = new Stack<Integer>();
		Stack<LinkedList<Node>> nodeContext = new Stack<LinkedList<Node>>();
		nodeContext.push(new LinkedList<Node>());
		followNode(startNode, emptyList, stack, nodeContext);
		
	}
	
	private void followNode(Node currentNode, LinkedList<Node> nodeList, 
			Stack<Integer> stack, Stack<LinkedList<Node>> nodeContext) {
		
		nodeList.add(currentNode);	
		
		boolean finalBlock = reachedEnd(currentNode.getInstructions(), m_endPcs);
		
		if(finalBlock){
			m_pathList.add(nodeList);
			return;
		} else {
			
			if(formsRecursion(currentNode, nodeContext)){
				//error
				System.err.println("error");
				
				return;
			}
			
			int loopIndex = formsLoop(currentNode, nodeContext);
			
			if(loopIndex!=-1){
				LinkedList<Node> currentContext = nodeContext.peek();
				LinkedList<Node> trimedLoop = new LinkedList<Node>( currentContext.subList(loopIndex, currentContext.size()));
				
				Node formsLoopWith = currentContext.get(loopIndex -1);
				formsLoopWith.addLoop(trimedLoop);
				return;
			}
			
			XtaInstruction lastInstruction = currentNode.getInstructions().getLast();
			
			
			if(lastInstruction.getReturn() && stack.size()>0){
				//known returns
				Node nextNode = getNode(stack.peek());
				
				
				Stack<LinkedList<Node>> newContext = new Stack<LinkedList<Node>>();
				copyStack(nodeContext, newContext);
				newContext.pop();

				Stack<Integer> newStack = (Stack<Integer>) stack.clone();
				newStack.pop();
				
				LinkedList<Node> currentList = new LinkedList<Node>(nodeList);
				
				followNode(nextNode, currentList, newStack, newContext);
				
			} else {
				for(Node nextNode : currentNode.getNextNodes()){
					if(lastInstruction.getBranchLink()){
						
						Stack<Integer> newStack = (Stack<Integer>) stack.clone();
						newStack.add(lastInstruction.getPc() + lastInstruction.getNumBytes());
						
						Stack<LinkedList<Node>> newContext = new Stack<LinkedList<Node>>();
						copyStack(nodeContext, newContext);
						newContext.push(new LinkedList<Node>());
						
						LinkedList<Node> currentList = new LinkedList<Node>(nodeList);
						
						followNode(nextNode, currentList, newStack, newContext);
					} else {
						Stack<Integer> newStack = (Stack<Integer>) stack.clone();
						
						Stack<LinkedList<Node>> newContext = new Stack<LinkedList<Node>>();
						copyStack(nodeContext, newContext);
						newContext.peek().add(currentNode);
						
						LinkedList<Node> currentList = new LinkedList<Node>(nodeList);
						
						followNode(nextNode, currentList, newStack, newContext);
					}
				}
			}
		}	
	}

	private void copyStack(Stack<LinkedList<Node>> nodeContext, Stack<LinkedList<Node>> temp) {
		Iterator<LinkedList<Node>> iter = nodeContext.iterator();
		while (iter.hasNext()) {
			LinkedList<Node> pcs = iter.next();
			LinkedList<Node> copy = new LinkedList<Node>(pcs);
			temp.add(copy);
		}
	}

	private boolean formsRecursion(Node currentNode, Stack<LinkedList<Node>> nodeContext) {
		LinkedList<Node> notInContext = new LinkedList<Node>();
		for(LinkedList<Node> list : nodeContext){
			if(nodeContext.indexOf(list)< nodeContext.size()-1){
				notInContext.addAll(list);
			}
		}
		return notInContext.contains(currentNode);
	}
	
	private int formsLoop(Node currentNode, Stack<LinkedList<Node>> nodeContext) {
		LinkedList<Node> currentContext = nodeContext.peek();
		return currentContext.indexOf(currentNode);
	}

	private boolean reachedEnd(LinkedList<XtaInstruction> instructions, TreeSet<Integer> pcs) {
		for(XtaInstruction inst : instructions){
			if(pcs.contains(inst.getPc())){
				return true;
			}
		}
		return false;
	}

	private Node getNode(int pc) {
		for(Node node : m_nodes){
			for(XtaInstruction inst : node.getInstructions()){
				if(inst.getPc() == pc) return node;
			}
			
		}
		return null;
	}
	
	public void dumpNodes(){
		for(LinkedList<Node> nodeList : m_pathList){
			System.out.println("path " + m_pathList.indexOf(nodeList) + ":");
			dumpNode(nodeList, 0);
		}
	}
	
	private void dumpNode(LinkedList<Node> nodeList, int indent){
		if(indent > 4) return;
		String t = "";
		for(int i=0;i<indent;i++){
			t += "\t";
		}
		int nodeIndex = 0;
		for(Node node : nodeList){
			System.out.println(t+"node " + nodeIndex++);
			for(XtaInstruction inst : node.getInstructions()){
				System.out.println(t + inst);
			}
			int loopIndex =0;
			for(LinkedList<Node> loop : node.getLoops()){
				System.out.println(t+"\tloop " + loopIndex++);
				dumpNode(loop, indent+1);
			}
		}
	}
}
