package com.xmos.xta.engine;

import java.util.List;
import java.util.Vector;

import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.references.CodeReference;

public class BlockCommonComponents {
	private Config m_config;
    private NodeCoreInstructionCaches m_nodeCoreInstructionCache;
    private MemoryImage m_memoryImage;
    private SymbolTable m_symbolTable;
    private List<CodeReference> m_exclusions;
    private Route m_route;
    private boolean m_resolveExpressions;
    private Block.Mode m_mode;
    private Engine m_engine;    
    
	public BlockCommonComponents(Config m_config,
			NodeCoreInstructionCaches nodeCoreInstructionCache,
			MemoryImage image,
			SymbolTable table, List<CodeReference> m_exclusions, Route m_route,
			boolean expressions, Block.Mode m_mode, Engine engine) {
		super();
		this.m_config = m_config;
		m_nodeCoreInstructionCache = nodeCoreInstructionCache;
		m_memoryImage = image;
		m_symbolTable = table;
		this.m_exclusions = m_exclusions;
		this.m_route = m_route;
		setResolveExpressions(expressions);
		this.setMode(m_mode);
		this.setEngine(engine);
	}
	
	public Config getConfig() {
		return m_config;
	}
	public void setConfig(Config m_config) {
		this.m_config = m_config;
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
	public void setResolveExpressions(boolean m_resolveExpressions) {
		this.m_resolveExpressions = m_resolveExpressions;
	}
	public boolean isResolveExpressions() {
		return m_resolveExpressions;
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
    
    
    
}
