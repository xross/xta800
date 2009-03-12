package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Collections;

public class LoopAttachment {

	private PathBasicBlock m_loop;
	private ArrayList<LoopAttachment> m_subLoops;
	
	public LoopAttachment(PathBasicBlock loop){
		m_loop = loop;
		m_subLoops = new ArrayList<LoopAttachment>();
	}
	
	
	
	public void addLoop(PathBasicBlock newLoop){
		
		for(LoopAttachment la : m_subLoops){
			if(isSubsetOf(la.getLoop(), newLoop)){
				LoopAttachment newLoopAttachment = new LoopAttachment(newLoop);
				newLoopAttachment.m_subLoops.add(la);
				m_subLoops.set(m_subLoops.indexOf(la), newLoopAttachment);
				return;
			} else if (isSubsetOf(newLoop, la.getLoop())){
				la.addLoop(newLoop);
				return;
			} 
		}
		m_subLoops.add(new LoopAttachment(newLoop));
	}
	
	public ArrayList<LoopAttachment> getSubLoops(){
		return m_subLoops;
	}
	
	public void setLoop(PathBasicBlock loop){
		m_loop = loop;
	}
	
	/**
	 * returns a list of all the loops attached at and below this attachment
	 */
	public ArrayList<PathBasicBlock> getLoopList(){
		ArrayList<PathBasicBlock> list = new ArrayList<PathBasicBlock>();
		for(LoopAttachment ls : m_subLoops){
			list.add(ls.getLoop());
		}
		Collections.sort(list);
		return list;
	}
	
	/**
	 * gets the PathBasicBlock that forms the loop
	 * @return
	 */
	public PathBasicBlock getLoop(){
		return m_loop;
	}
	
	
	/**
	 * returns true if for every element of the list a there is the same element in the list b, i.e.
	 * the list b is the list a with a tail.
	 * @param a
	 * @param b
	 * @return true if a is a sub set of b
	 */
	private boolean isSubsetOf(PathBasicBlock a, PathBasicBlock b){
		ArrayList<PathBasicBlock> aList = a.getPathBlocks();
		ArrayList<PathBasicBlock> bList = b.getPathBlocks();
		if (aList.size() > bList.size()){
			//a cannot be a subset of b is it is bigger
			return false;
		} else if (aList.size() < bList.size()){
			int size = aList.size();
			for(int i=0;i<size;i++){
				if(aList.get(i).getBasicBlock() != bList.get(i).getBasicBlock()){
					return false;
				}
			}
			return true;
		} else {
			//they must be equal and thus different
			return false;
		}
	}

	public ArrayList<PathBasicBlock> getLoopPaths(PathBasicBlock base) {
		
		if (m_loop == base)
			return getLoopList();
		
		for (LoopAttachment subLoop : m_subLoops) {
			ArrayList<PathBasicBlock> subLoopPaths = subLoop.getLoopPaths(base);
			if (subLoopPaths != null)
				return subLoopPaths;
		}
		
		// Top of the tree
		if (m_loop == null)
			return getLoopList();
		else
			return null;
	}

	public boolean contains(PathBasicBlock pathBasicBlock) {
		if (m_loop == pathBasicBlock)
			return true;
		
		for (LoopAttachment subLoop : m_subLoops) {
			boolean contains = subLoop.contains(pathBasicBlock);
			if (contains)
				return true;
		}
		
		return false;
	}
	
	
	
	
	
	
	
	
}
