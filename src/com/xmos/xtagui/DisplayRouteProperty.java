package com.xmos.xtagui;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Route;

public class DisplayRouteProperty {
	private Route m_route; 
	private DebugInfo m_debugInfo;
	private MemoryImage m_memoryImage; 
	private SymbolTable m_symbolTable;
	
	public DisplayRouteProperty(Route route, DebugInfo debugInfo,
			MemoryImage memoryImage, SymbolTable symbolTable) {
		m_route = route;
		m_debugInfo = debugInfo;
		m_memoryImage = memoryImage;
		m_symbolTable = symbolTable;
	}
	
	public Route getRoute() {
		return m_route;
	}

	public DebugInfo getDebugInfo() {
		return m_debugInfo;
	}

	public MemoryImage getMemoryImage() {
		return m_memoryImage;
	}

	public SymbolTable getSymbolTable() {
		return m_symbolTable;
	}
}
