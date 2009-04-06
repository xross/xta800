package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.xmos.xta.engine.unknowns.LoopIterationsUnknown;

public class LoopAttachment {

	//all the loops attached on to this path basic block
	private HashSet<PathBasicBlock> m_allAttachedLoops;
	
	//the loops that contain m_loop
	private ArrayList<LoopAttachment> m_subLoops;
	
	//a list of all the loops in the m_subLoop list
	private ArrayList<PathBasicBlock> m_loopList;

	//the scope of the attached loops
	private Boolean m_absolute;
	
	//the maximum iterations on the sub loops
	private Long m_maxIterations;
	
	//this loop
	private PathBasicBlock m_loop;
	
	//the parent of this loop
	private LoopAttachment m_parent;
	
	//the user defined iterations if this loop
	private Long m_hardIterations;
	
	//the relative iterations of this loop
	private Long m_softIterations;
	
	public LoopAttachment(PathBasicBlock loop, LoopAttachment parent){
		m_loop = loop;
		m_parent = parent;
		m_subLoops = new ArrayList<LoopAttachment>();
		if (loop == null){
			m_allAttachedLoops = new HashSet<PathBasicBlock>();
		}
		m_hardIterations = null;
		m_softIterations = null;
		m_maxIterations = null;
		
		m_absolute = new Boolean(false);
	}
	
	private LoopAttachment getParent(){
		return m_parent;
	}
	
	private LoopAttachment findLoop(PathBasicBlock loop){
		if (m_loop == loop){
			return this;
		} else {
			for (LoopAttachment la : m_subLoops){
				LoopAttachment subLoop = la.findLoop(loop);
				if (subLoop != null){
					return subLoop;
				}
			}
		}
		return null;
	}
		
	public boolean setLoopIterations(PathBasicBlock loop, Long count){
		assert(loop != null);
		if (m_loop == loop) {
			if (count == null) {
				// Clear iteration count
			    m_hardIterations = null;
			    getParent().reAssignLoopIterations();
				return true;
				
			} else {			
				// Set iteration count
				if (withinMaxRange(count)) {
				    m_hardIterations = count;
				    getParent().reAssignLoopIterations();
				    return true;
				}
	            return false;
			}
		}
		for (LoopAttachment la : m_subLoops) {						
			if (la.setLoopIterations(loop, count)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * this gets the soft loop iterations for a given loop
	 */
	public long getSoftLoopIterations(PathBasicBlock loop) throws Exception {
		assert(loop != null);
		LoopAttachment la = findLoop(loop);
		if (la!= null){
				if (la.m_softIterations!= null){
					return la.m_softIterations;
				} else {
					throw new Exception();
				}
		} else {
			//loop not found
			throw new Exception();
		}
	}
	
	/**
	 * this gets the hard loop iterations for a given loop
	 */
	public long getHardLoopIterations(PathBasicBlock loop) throws Exception {
		assert(loop != null);
		LoopAttachment la = findLoop(loop);
		if (la!= null){
				if (la.m_hardIterations!= null){
					return la.m_hardIterations;
				} else {
					throw new Exception();
				}
		} else {
			//loop not found
			throw new Exception();
		}
	}
	
	/**
	 * this gets the total loop iterations for a given loop 
	 */
	public long getLoopIterations(PathBasicBlock loop) throws Exception{
		
		long soft = 0;
		long hard = 0;
		
		boolean softError = false;
		boolean hardError = false;
		
		try {
			hard = getHardLoopIterations(loop);
		} catch (Exception e) {
			hardError = true;
		}
		
		try {
			soft = getSoftLoopIterations(loop);
		} catch (Exception e) {
			softError = true;
		}
		
		if (softError && hardError){
			throw new Exception();
		}
		return soft + hard; 
	}
	
	public boolean isAbsolute(PathBasicBlock loop) throws Exception {
		assert(loop != null);
		LoopAttachment la = findLoop(loop);
		if (la!= null){
			if (la.getParent() != null){
				return la.getParent().m_absolute;
			}
		}
		return false;
	}
	
	public void addLoop(PathBasicBlock newLoop){		
		if (m_allAttachedLoops != null){
			m_allAttachedLoops.add(newLoop);
		}
		for (LoopAttachment la : m_subLoops){
			int a = la.getLoop().getPathBlocks().size();
			int b = newLoop.getPathBlocks().size();
			if (a<b) {
				if (la.getLoop().getPathBlocks().get(a-1).getBasicBlock() 
						== newLoop.getPathBlocks().get(a-1).getBasicBlock()){
					LoopAttachment newLoopAttachment = new LoopAttachment(newLoop, la);
					newLoopAttachment.m_subLoops.add(la);
					m_subLoops.set(m_subLoops.indexOf(la), newLoopAttachment);
					return;
				}
			} else if (b<a) {
				if (newLoop.getPathBlocks().get(b-1).getBasicBlock() 
						== la.getLoop().getPathBlocks().get(b-1).getBasicBlock()){
					la.addLoop(newLoop);
					return;
				}
			} 
		}
		m_subLoops.add(new LoopAttachment(newLoop, this));
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
	public List<PathBasicBlock> getLoopList(){
		if (m_loopList == null){
			m_loopList = new ArrayList<PathBasicBlock>();
			for (LoopAttachment ls : m_subLoops){
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
	public List<PathBasicBlock> getLoopPaths(PathBasicBlock base) {
		if (m_loop == base ){
			return getLoopList();
		}
		for (LoopAttachment subLoop : m_subLoops) {
			List<PathBasicBlock> subLoopPaths = subLoop.getLoopPaths(base);
			if (subLoopPaths != null){
				return subLoopPaths;
			}
		}
		// Top of the tree
		if (m_loop == null){
			return getLoopList();
		} else {
			return null;
		}
	}

	/**
	 * returns true if block is contained within any of the loop end points
	 */
	public boolean containsLoop(PathBasicBlock block) {
		return m_allAttachedLoops.contains(block);
	}

	public boolean containsBlock(PathBasicBlock block){
		for(PathBasicBlock loopBlock: m_allAttachedLoops){
			if(loopBlock.getPathBlocks().contains(block)){
				return true;
			}
		}
		return false;
	}
	
	public boolean setLoopMax(int pc, Long count) {
		//starting from the inner most loop try to set the loopmax
		
		for (LoopAttachment la : m_subLoops){
			for (PathBasicBlock loopBlock : la.getLoop().getPathBlocks()){
				if (loopBlock.containsPc(pc)){			
					if (count == null) {
						// Clear iteration count.
					    m_maxIterations = null;
					    m_softIterations = null;
					    return true;	
					    
					} else {				
						// Set iterations count.
					    if (withinHardIterationsRange(count)) {					
					        m_maxIterations = count;
					        return true;
					    }
						return false;
					}
				}
			}	
		}
		
		//FIXME - this is wrong
		boolean found = false;
		for (LoopAttachment la : m_subLoops){
			found |= la.setLoopMax(pc, count);
		}
		return found;
	}
	
	public long getLoopMax(PathBasicBlock loop) throws Exception {
		assert(loop != null);
		LoopAttachment la = findLoop(loop);
		if (la!= null){
				if (la.m_softIterations!= null){
					return la.getParent().m_maxIterations;
				} else {
					throw new Exception();
				}
		} else {
			//loop not found
			throw new Exception();
		}
	}

	/**
	 * set the scope of all the loops attached at a given level
	 */
	public void setLoopScope(int pc, boolean absolute){
		//starting from the inner most loop try to set the scope
		
		for (LoopAttachment la : m_subLoops){
			for (PathBasicBlock loopBlock : la.getLoop().getPathBlocks()){
				if (loopBlock.containsPc(pc)){
					m_absolute = new Boolean(absolute);
					return;
				}
			}	
		}
		
		//FIXME - this is wrong
		for (LoopAttachment la : m_subLoops){
			la.setLoopScope(pc, absolute);
		}
	}
	
	public boolean getLoopScope(PathBasicBlock loop) {
		LoopAttachment la = findLoop(loop);
		if (la.getParent().m_absolute!= null){
			return la.getParent().m_absolute;
		}
		return false;
	}
	
	public void reAssignLoopIterations(){
		int hardCount = 0;
		for (LoopAttachment la : m_subLoops){
			if(la.m_hardIterations != null){
				hardCount += la.m_hardIterations;
			}
		}
		if (m_maxIterations != null){
			if (m_subLoops.size() > 0){
				long remaining = m_maxIterations - hardCount;
				LoopAttachment longestLoop = m_subLoops.get(0);
				double longestTime = longestLoop.getLoop().getTimeTaken(true);
				for(LoopAttachment la : m_subLoops){
					double loopTime = la.getLoop().getTimeTaken(true);
					if(longestTime < loopTime) {
						longestLoop = la;
						longestTime = loopTime;
					}
				}
				for(LoopAttachment la : m_subLoops){
					la.m_softIterations = new Long(0);
				}
				longestLoop.m_softIterations = new Long(remaining);
			}
		} else {
			for (LoopAttachment la : m_subLoops){
				la.m_softIterations = null;
			}			
		}
	}
	
	public LoopIterationsUnknown getLoopUnknown(){
		
		if (m_hardIterations == null && m_softIterations == null){
			return new LoopIterationsUnknown(m_loop.getPathBlocks().get(0),m_loop);
		}
		return null;
	}
	
	public List<LoopIterationsUnknown> getUnknowns() {
		List<LoopIterationsUnknown> unknowns = new ArrayList<LoopIterationsUnknown>();
		for (LoopAttachment la : m_subLoops){
			unknowns.addAll(la.getUnknowns());
			if (la.getUnknowns() != null){
				LoopIterationsUnknown lu = la.getLoopUnknown();
				if (lu != null){
					unknowns.add(lu);
				}
			}
		}
		return unknowns;
	}	
	
	public List<LoopIterationsUnknown> getUnknowns(PathBasicBlock base) {
		List<LoopIterationsUnknown> unknowns = new ArrayList<LoopIterationsUnknown>();

		try {
			LoopAttachment la = findLoop(base);
			unknowns.addAll(la.getUnknowns());
			if (la.getUnknowns() != null){
				LoopIterationsUnknown lu = la.getLoopUnknown();
				if (lu != null){
					unknowns.add(lu);
				}
			}
		} catch (Exception e) {
		}

		return unknowns;
	}
	
	/**
	 * returns a the sum of all the hard loop iterations set in the sub loops of this loop
	 */	
	private long getCurrentHardTotal() {			
		long total = 0;
		for (LoopAttachment la : m_subLoops) {
			if (la.m_hardIterations != null) {
				total += la.m_hardIterations;
			}
		}
		return total;
	}

	/**
	 * Checks that setting the current loop iteration to 'count' will not
	 * cause the hard iterations counts of all the sub loops to overflow the max.
	 */	
	private boolean withinMaxRange(long count) {
		long total = m_parent.getCurrentHardTotal();
		if (m_hardIterations != null) 
			total -= m_hardIterations;
		
		long newTotal = total + count;
		if (newTotal < 0)
			return false;
		
		if (m_parent.m_maxIterations != null) {
			if (newTotal > m_parent.m_maxIterations)
				return false;
		}
		return true;
	}
	
	/**
	 * Checks that setting the loop max iterations to 'count' will not
	 * conflict with the the hard iterations counts of all the sub loops.
	 */	
	private boolean withinHardIterationsRange(long count) {
		long total = getCurrentHardTotal();		
	    if (count < total) {
			return false;
		}
		return true;
	}	
}
