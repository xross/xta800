package com.xmos.xta.engine;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.references.CodeReference;

public class BlockCommonComponents {
    private NodeCoreInstructionCaches m_nodeCoreInstructionCache;
    private MemoryImage m_memoryImage;
    private SymbolTable m_symbolTable;
    private List<CodeReference> m_exclusions;
    private Route m_route;
    private Block.Mode m_mode;
    private Engine m_engine;    
    private HashSet<Integer> m_endPcs;
    
    
	public BlockCommonComponents(NodeCoreInstructionCaches nodeCoreInstructionCache,
			MemoryImage image, SymbolTable table, List<CodeReference> m_exclusions, Route m_route,
			Engine engine, HashSet<Integer> pcs) {
		super();
		m_nodeCoreInstructionCache = nodeCoreInstructionCache;
		m_memoryImage = image;
		m_symbolTable = table;
		m_endPcs = pcs;
		this.m_exclusions = m_exclusions;
		this.m_route = m_route;
		this.setMode(m_mode);
		this.setEngine(engine);
	}
	
	public NodeCoreInstructionCaches getNodeCoreInstructionCaches() {
		return m_nodeCoreInstructionCache;
	}
	public void setNodeCoreInstructionCaches(NodeCoreInstructionCaches nodeCoreInstructionCache) {
		m_nodeCoreInstructionCache = nodeCoreInstructionCache;
	}
	
	public MemoryImage getMemoryImage() {
		return m_memoryImage;
	}
	public void setMemoryImage(MemoryImage image) {
		m_memoryImage = image;
	}
	public SymbolTable getSymbolTable() {
		return m_symbolTable;
	}
	public void setSymbolTable(SymbolTable table) {
		m_symbolTable = table;
	}
	public List<CodeReference> getExclusions() {
		return m_exclusions;
	}
	public void setExclusions(Vector<CodeReference> m_exclusions) {
		this.m_exclusions = m_exclusions;
	}
	public void setRoute(Route m_route) {
		this.m_route = m_route;
	}
	public Route getRoute() {
		return m_route;
	}
	public void setMode(Block.Mode m_mode) {
		this.m_mode = m_mode;
	}
	public Block.Mode getMode() {
		return m_mode;
	}

	public void setEngine(Engine m_engine) {
		this.m_engine = m_engine;
	}

	public Engine getEngine() {
		return m_engine;
	}
    
    public HashSet<Integer> getEndPcs(){
    	return m_endPcs;
    }
    
}
