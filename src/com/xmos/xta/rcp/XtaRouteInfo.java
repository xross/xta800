package com.xmos.xta.rcp;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.PathBasicBlock;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.Unresolved;
import com.xmos.xta.rcp.nodes.BlockNode;
import com.xmos.xta.rcp.nodes.LoopNode;
import com.xmos.xta.rcp.nodes.Node;
import com.xmos.xta.rcp.nodes.PathNode;
import com.xmos.xta.rcp.nodes.RouteNode;

public class XtaRouteInfo {
	
	private static final int MAXPATHS = 100;
	private Route m_route;
	private DebugInfo m_debugInfo;
	private MemoryImage m_memoryImage; 
	private SymbolTable m_symbolTable;
	private Node m_routeNode;
	private Node m_unresolvedNode;
	private Node m_selectedNode;
	
	public XtaRouteInfo(Route route, DebugInfo debugInfo,
			MemoryImage memoryImage, SymbolTable symbolTable) {
		m_route = route;
		m_debugInfo = debugInfo;
		m_memoryImage = memoryImage;
		m_symbolTable = symbolTable;
		m_routeNode = null;
		m_selectedNode = null;
		m_selectedNode = null;
		buildRouteNode();
	}
	
	public void buildRouteNode(){
		RouteNode routeNode = new RouteNode("complete");
		int i = 0;
        for (PathBasicBlock block : m_route.getCompletedPaths()) {
        	if (i >= MAXPATHS) break;
        	i++;
        	routeNode.addNode(createPath(block, routeNode, true));
        }		
        m_routeNode = routeNode;
		m_selectedNode = routeNode;
		
		m_unresolvedNode = new RouteNode("unresolved");
		for (Unresolved unresolved : m_route.getUnresolvedPaths()) {
        	if (i >= MAXPATHS) break;
        	i++;
			m_unresolvedNode.addNode(createPath(unresolved.getBlock(), m_unresolvedNode, true));
		}
	}
	
	private PathNode createPath(PathBasicBlock block, Node parent, boolean isRoot) {
        PathNode pathNode = new PathNode(block, parent, isRoot);     
        for (PathBasicBlock pathBlock : block.getPathBlocks()) {            
        	if(pathBlock.numLoopPaths(block) > 0){
        		 LoopNode loopNode = new LoopNode(pathBlock, pathNode);
                 pathNode.addNode(loopNode);
                                 
            	 for (PathBasicBlock loopPath : pathBlock.getLoopPaths(block)) {
            		 
            			 loopNode.addNode(createPath(loopPath, loopNode, false));                    
                 }
        	}
        	if(pathBlock.getNumInstructionsThis()>0)
        		pathNode.addNode(new BlockNode(pathBlock, pathNode));            
        }
        return pathNode;
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

	public Node getSelectedNode() {
		return m_selectedNode;
	}	

	public Node getUnresolvedNode() {
		return m_unresolvedNode;
	}

	public void setSelectedNode(Node selectedNode) {
		m_selectedNode = selectedNode;
	}

	public void setRoute(Route route) {
		m_route = route;
	}	
	
	public void resetRouteInfo(){
		
	}
}
