package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class LoopAttachment {

	private HashSet<PathBasicBlock> m_allAttachedLoops;
	private PathBasicBlock m_loop;
	private ArrayList<LoopAttachment> m_subLoops;
	private ArrayList<PathBasicBlock> m_loopList;
	
	public LoopAttachment(PathBasicBlock loop){
		m_loop = loop;
		m_subLoops = new ArrayList<LoopAttachment>();
		if(loop == null){
			m_allAttachedLoops = new HashSet<PathBasicBlock>();
		}
	}

	public void addLoop(PathBasicBlock newLoop){
		
		if(m_allAttachedLoops != null){
			m_allAttachedLoops.add(newLoop);
		}
		for(LoopAttachment la : m_subLoops){
			int a = la.getLoop().getPathBlocks().size();
			int b = newLoop.getPathBlocks().size();
			if(a<b){
				if(la.getLoop().getPathBlocks().get(a-1).getBasicBlock() 
						== newLoop.getPathBlocks().get(a-1).getBasicBlock()){
					LoopAttachment newLoopAttachment = new LoopAttachment(newLoop);
					newLoopAttachment.m_subLoops.add(la);
					m_subLoops.set(m_subLoops.indexOf(la), newLoopAttachment);
					return;
				}
			} else if(b<a){
				if(newLoop.getPathBlocks().get(b-1).getBasicBlock() 
						== la.getLoop().getPathBlocks().get(b-1).getBasicBlock()){
					la.addLoop(newLoop);
					return;
				}
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
		if(m_loopList == null){
			m_loopList = new ArrayList<PathBasicBlock>();
			for(LoopAttachment ls : m_subLoops){
				m_loopList.add(ls.getLoop());
			}
			Collections.sort(m_loopList);
		}
		return m_loopList;
	}
	/**
	 * gets the PathBasicBlock that forms the loop
	 * @return
	 */
	public PathBasicBlock getLoop(){
		return m_loop;
	}

	public ArrayList<PathBasicBlock> getLoopPaths(PathBasicBlock base) {
		if (m_loop == base )
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
		return m_allAttachedLoops.contains(pathBasicBlock);
	}
	
}
