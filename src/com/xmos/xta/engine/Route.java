package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.engine.Status.BlockStatus;

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
	private String m_description;
	private ArrayList<Unknown> m_unknowns;
	private ArrayList<Unresolved> m_unresolvedPaths;
	private InstructionDecodeCache m_idc;
	
	public Route(int startPc, TreeSet<Integer> endPcs, ArrayList<BasicBlock> nodes, InstructionDecodeCache idc, String description){

		m_completedPaths = new ArrayList<PathBasicBlock>();
		m_allBasicBlocks = new ArrayList<PathBasicBlock>();
		m_unresolvedPaths = new ArrayList<Unresolved>();
		m_startPc = startPc;
		m_endPcs =endPcs;
		m_description = description;
		m_nodes = nodes;
		m_unknowns = new ArrayList<Unknown>();
		m_idc = idc;
	}
	
	public ArrayList<PathBasicBlock> getCompletedPaths(){
		return m_completedPaths;
	}
	
	public ArrayList<Unresolved> getUnresolvedPaths(){
		return m_unresolvedPaths;
	}
	
	public String getDescription() {
		return m_description;
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
		PathBasicBlock startBasicBlock;
		if(startNode.getInstructions().getFirst().getPc() == m_startPc){
			startBasicBlock = new PathBasicBlock(startNode, null, m_idc);
		} else {
			startBasicBlock = new PathBasicBlock(startNode, null, m_startPc, true, m_idc);
		}
		Stack<Integer> stack = new Stack<Integer>();
		Stack<LinkedList<BasicBlock>> nodeContext = new Stack<LinkedList<BasicBlock>>();
		nodeContext.push(new LinkedList<BasicBlock>());
		followNode(startBasicBlock, stack, nodeContext);
		sortPaths();
		updateDescription();
		return RouteStatus.COMPLETED;
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
	
	private static final boolean debug = false;
	
	@SuppressWarnings("unchecked")
	private void followNode(
		PathBasicBlock currentBasicBlock, 
		Stack<Integer> stack, 
		Stack<LinkedList<BasicBlock>> nodeContext) {
		
		if(debug) System.out.println(currentBasicBlock + " " +nodeContext.size());
		
		if(reachedEnd(currentBasicBlock)){
			int size = currentBasicBlock.getInstructionTrimmed().size()-1;
			int endPc =0;
			PathBasicBlock endBlock = currentBasicBlock;
			while(size>=0){
				if( m_endPcs.contains(currentBasicBlock.getInstructionTrimmed().get(size).getPc())){
					endPc = currentBasicBlock.getInstructionTrimmed().get(size).getPc();
					endBlock = new PathBasicBlock(currentBasicBlock.getBasicBlock(), currentBasicBlock.getParent(), endPc, false, m_idc);
					break;
				}
				size--;
			}
			
			updateConstraints(endBlock);
			
			m_completedPaths.add(endBlock);
			endBlock.setPath(m_pathCount++);
			endBlock.setStatus(BlockStatus.COMPLETE);
			
			if(debug) System.out.println("block complete");
			return;
		} else {

			m_allBasicBlocks.add(currentBasicBlock);
			if(hasResursed(currentBasicBlock, nodeContext)){
				String reason = "Recursion with 0x" + Integer.toHexString(currentBasicBlock.getBasicBlock().getInstructions().getFirst().getPc());
				Unresolved unresolved = new Unresolved(currentBasicBlock, currentBasicBlock.getBasicBlock().getInstructions().getLast(), reason);
				m_unresolvedPaths.add(unresolved);
				currentBasicBlock.setUnresolved(unresolved);
				if(debug) System.out.println(reason);
				currentBasicBlock.setStatus(BlockStatus.ERROR);
				return;
			}
			PathBasicBlock loopBlock = formsLoop(currentBasicBlock, nodeContext);
			if(loopBlock!=null){
				//this must be true by design of the algorithm
				assert(currentBasicBlock.getParent()!=null);
				currentBasicBlock.getParent().setPath(m_pathCount++);
				loopBlock.getLoopPaths().add(currentBasicBlock.getParent());
				loopBlock.getLoopPathAttributes().put(currentBasicBlock.getParent(), new PathAttributes());
				if(debug) System.out.println("formed loop");
				currentBasicBlock.getParent().setStatus(BlockStatus.COMPLETE);
				return;
			}
			XtaInstruction lastInstruction = currentBasicBlock.getInstructionTrimmed().get(currentBasicBlock.getInstructionTrimmed().size()-1);
			if(lastInstruction.getReturn() && stack.size()>0){
				if(debug) System.out.println("following retsp from stack");
				BasicBlock nextNode = getNode(stack.peek());
				Stack<LinkedList<BasicBlock>> newContext = new Stack<LinkedList<BasicBlock>>();
				copyStack(nodeContext, newContext);
				newContext.pop();
				Stack<Integer> newStack = (Stack<Integer>) stack.clone();
				newStack.pop();
				PathBasicBlock nextPathBasicBlock = new PathBasicBlock(nextNode, currentBasicBlock, m_idc);
				followNode(nextPathBasicBlock, newStack, newContext);
			} else {
				if(currentBasicBlock.getBasicBlock().getNextNodes().size() == 0){
					if(debug) System.out.println("Unresolved: unknown next block");
					Unresolved unresolved = new Unresolved(currentBasicBlock, currentBasicBlock.getBasicBlock().getInstructions().getLast(), "no known next block");
					m_unresolvedPaths.add(unresolved);
					currentBasicBlock.setUnresolved(unresolved);
					currentBasicBlock.setStatus(BlockStatus.ERROR);
					return;
				}
				for(BasicBlock nextNode : currentBasicBlock.getBasicBlock().getNextNodes()){
					if(debug) System.out.println("following successor");
					Stack<Integer> newStack = (Stack<Integer>) stack.clone();
					Stack<LinkedList<BasicBlock>> newContext = new Stack<LinkedList<BasicBlock>>();
					copyStack(nodeContext, newContext);
					if(lastInstruction.getBranchLink()){
						newStack.add(lastInstruction.getPc() + lastInstruction.getNumBytes());
						newContext.push(new LinkedList<BasicBlock>());
					} else {
						newContext.peek().add(currentBasicBlock.getBasicBlock());
					}
					PathBasicBlock nextPathBasicBlock = new PathBasicBlock(nextNode, currentBasicBlock, m_idc);
					followNode(nextPathBasicBlock, newStack, newContext);
				}
			}
		}	
	}
	
	private void updateConstraints(PathBasicBlock block) {
		m_allBasicBlocks.add(block);
		block.getConstraint();
		
	}

	private boolean reachedEnd(PathBasicBlock currentBasicBlock) {
		for(XtaInstruction inst : currentBasicBlock.getInstructionTrimmed()){
			if(m_endPcs.contains(inst.getPc())){
				return true;
			}
		}
		return false;
	}
	
	private boolean hasResursed(PathBasicBlock currentBasicBlock, Stack<LinkedList<BasicBlock>> nodeContext) {
		for(int i=0;i<nodeContext.size()-1;i++){
			for(BasicBlock bb : nodeContext.get(i)){
				if(currentBasicBlock.getBasicBlock() == bb){
					return true;
				}
			}
		}
		return false;
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
			if (parent.getBasicBlock() ==  currentNode.getBasicBlock()) {
				return parent;
			}
			parent = parent.getParent();
		}
		return null;
	}

	private BasicBlock getNode(int pc) {
		for(BasicBlock node : m_nodes){
			for(XtaInstruction inst : node.getInstructions()){
				if(inst.getPc() == pc) return node;
			}
		}
		return null;
	}
	
	private void updateDescription(){
		m_description +=  " (0x" + Integer.toHexString(m_startPc) + " => ";
		for(int end : m_endPcs){
			m_description += "0x" + Integer.toHexString(end) + ", ";
		}
		m_description = m_description.substring(0, m_description.length()-2);
		m_description += ")";
	}
	
	private void sortPaths() {
		Collections.sort(m_completedPaths);
		for(PathBasicBlock block : m_allBasicBlocks){
			Collections.sort(block.getLoopPaths());
		}
	}
}
