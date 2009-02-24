package com.xmos.xta.engine;

import java.util.ArrayList;

public class PathBasicBlock {
	private BasicBlock m_basicBlock;
	private PathBasicBlock m_parentBasicBlock;
	private int m_seen;
	
	
	
	public PathBasicBlock(BasicBlock basicBlock, PathBasicBlock parentBasicBlock){
		m_basicBlock = basicBlock;
		m_parentBasicBlock = parentBasicBlock;
		m_seen = 0;
	}

	public BasicBlock getThisBasicBlock(){
		return m_basicBlock;
	}
	
	public PathBasicBlock getParent(){
		return m_parentBasicBlock;
	}
	
	
	public ArrayList<PathBasicBlock> getPathBlocks() {
		ArrayList<PathBasicBlock> paths = new ArrayList<PathBasicBlock>();
		PathBasicBlock block = this;

		do {
			paths.add(0, block);
			
			if (block.m_basicBlock.getLoopPaths().contains(this)) {
				break;
			}
			block = block.m_parentBasicBlock;
		} while (block != null);

		if (block != null)
			block.m_seen++;

		return paths;
	}
	
	public void doneWithPathBlocks() {
		PathBasicBlock block = this;

		do {
			if (block.m_basicBlock.getLoopPaths().contains(this)) {
				break;
			}
			block = block.m_parentBasicBlock;
		} while (block != null);

		if (block != null) {
			assert (block.m_seen > 0);
			block.m_seen--;
		}
	}
	
	public ArrayList<PathBasicBlock> getLoopPaths() {
		if (m_seen == 0) {
			return m_basicBlock.getLoopPaths();
		} else {
			return new ArrayList<PathBasicBlock>();
		}
	}
	
	
}
