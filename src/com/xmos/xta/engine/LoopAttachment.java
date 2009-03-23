package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class LoopAttachment {

	//all the loops attached on to this path basic block
	private HashSet<PathBasicBlock> m_allAttachedLoops;
	
	//the loop
	private PathBasicBlock m_loop;
	
	//the loops that contain m_loop
	private ArrayList<LoopAttachment> m_subLoops;
	
	//a list of all the loops in the m_subLoop list
	private ArrayList<PathBasicBlock> m_loopList;

	private boolean m_absolute;
	
	//the absolute iterations if this loop
	private int m_absoluteIterations;
	
	//the relative iterations of this loop
	private int m_relativeIterations;
	
	//the max combined iterations of all the sub loops;
	private int maxSubLoopIterations;
	

	public LoopAttachment(PathBasicBlock loop){
		m_loop = loop;
		m_subLoops = new ArrayList<LoopAttachment>();
		if(loop == null){
			m_allAttachedLoops = new HashSet<PathBasicBlock>();
		}
		m_absoluteIterations = -1;
		m_relativeIterations = -1;
		maxSubLoopIterations = -1;
	}
	
	
	public void setMaxSubLoopIterations(int count){
		maxSubLoopIterations = count;
	}
	
	public int getMaxSubLoopIterations(){
		return maxSubLoopIterations;
	}
	
	private LoopAttachment findLoop(PathBasicBlock loop){
		if(m_loop == loop){
			return this;
		} else {
			for(LoopAttachment la : m_subLoops){
				LoopAttachment subLoop = la.findLoop(loop);
				if(subLoop != null){
					return subLoop;
				}
			}
		}
		return null;
	}
	
	public boolean setLoopIterations(PathBasicBlock loop, boolean absolute, int count){
		
		LoopAttachment la = findLoop(loop);
		
		if(la!= null){
			la.m_absolute = absolute;
			if(absolute){
				la.m_absoluteIterations = count;
				la.m_relativeIterations = -1;
			} else {
				la.m_relativeIterations = count;
				la.m_absoluteIterations = -1;
			}
			return true;
		} else {
			return false;
		}
		
		/*
		if(loop == m_loop){
			m_absolute = absolute;
			if(absolute){
				m_absoluteIterations = count;
				m_relativeIterations = -1;
			} else {
				m_relativeIterations = count;
				m_absoluteIterations = -1;
			}
			return true;
		} else {
			for(LoopAttachment la : m_subLoops){
				if(la.setLoopIterations(loop, absolute, count)) return true;
			}
			return false;
		}
		*/
	}
	
	public int getLoopAbsoluteIterations(PathBasicBlock loop) {
		
		
		
		if(loop == m_loop){
			if(m_absolute){
				return m_absoluteIterations;
			} else {
				return -1;
			}
		} else {
			for(LoopAttachment la : m_subLoops){
				int iterations = la.getLoopAbsoluteIterations(loop);
				if(iterations != -1){
					return iterations;
				}
			}
		}
		return -1;
	}
	public int getLoopRelativeIterations(PathBasicBlock loop) {
		if(loop == m_loop){
			if(!m_absolute){
				return m_relativeIterations;
			} else {
				return -1;
			}
		} else {
			for(LoopAttachment la : m_subLoops){
				int iterations = la.getLoopRelativeIterations(loop);
				if(iterations != -1){
					return iterations;
				}
			}
		}
		return -1;
	}
	public int getLoopIterations(PathBasicBlock loop) {
		int absoluteLoops = getLoopAbsoluteIterations(loop);
		if(absoluteLoops != -1){
			return absoluteLoops;
		}
		int relativeLoops = getLoopRelativeIterations(loop);
		if(relativeLoops != -1){
			return relativeLoops;
		}
		return 0;
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
			//TODO may have to move this out of the loop in order to maintain order
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
	
	
	/**
	 * 
	 * @param base
	 * @return
	 */
	public ArrayList<PathBasicBlock> getLoopPaths(PathBasicBlock base) {
		

		if (m_loop == base ){
			return getLoopList();
		}

		for (LoopAttachment subLoop : m_subLoops) {
			ArrayList<PathBasicBlock> subLoopPaths = subLoop.getLoopPaths(base);
			if (subLoopPaths != null){
				return subLoopPaths;
			}
		}
		
		
		// Top of the tree
		if (m_loop == null){
			return getLoopList();
		}
		else{
			return null;
		}
	}


	
	
	public boolean contains(PathBasicBlock pathBasicBlock) {
		return m_allAttachedLoops.contains(pathBasicBlock);
	}






	
}
