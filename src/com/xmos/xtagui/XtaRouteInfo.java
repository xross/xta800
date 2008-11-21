package com.xmos.xtagui;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Route;
import com.xmos.xtagui.nodes.Node;

public class XtaRouteInfo {
	private Route m_route; 
	private DebugInfo m_debugInfo;
	private MemoryImage m_memoryImage; 
	private SymbolTable m_symbolTable;
	private Node m_routeNode;
	
	public XtaRouteInfo(Route route, DebugInfo debugInfo,
			MemoryImage memoryImage, SymbolTable symbolTable, Node routeNode) {
		m_route = route;
		m_debugInfo = debugInfo;
		m_memoryImage = memoryImage;
		m_symbolTable = symbolTable;
		m_routeNode = routeNode;
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
	
	public Node getRouteNode() {
		return m_routeNode;
	}	
}
