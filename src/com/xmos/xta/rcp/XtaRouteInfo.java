package com.xmos.xta.rcp;

import java.util.HashMap;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Block;
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
	private Node m_selectedNode;
	private Node m_unresolvedNode;

	private static HashMap <Block, BlockNode> m_blockLookUp;
	private static HashMap <Block, LoopNode> m_loopLookUp;
	
	public XtaRouteInfo(Route route, DebugInfo debugInfo,
			MemoryImage memoryImage, SymbolTable symbolTable) {
		m_route = route;
		m_debugInfo = debugInfo;
		m_memoryImage = memoryImage;
		m_symbolTable = symbolTable;
		m_routeNode = null;
		m_selectedNode = null;
		m_unresolvedNode = null;
		buildRouteNode();
	}
	
	public void buildRouteNode(){
		m_blockLookUp = new HashMap<Block, BlockNode>();
		m_loopLookUp = new HashMap<Block, LoopNode>();
		RouteNode routeNode = new RouteNode("complete");
		int i = 0;
        for (Block block : m_route.completePaths()) {
        	if (i==MAXPATHS) break;
        	i++;
        	routeNode.addNode(createPath(block, routeNode, true));
        }		
        m_routeNode = routeNode;
		m_selectedNode = routeNode;
		
		m_unresolvedNode = new RouteNode("unresolved");
		for (Unresolved unresolved : m_route.unresolvedPaths()) {
        	if (i==MAXPATHS) break;
        	i++;
			m_unresolvedNode.addNode(createPath(unresolved.getBlock(), m_unresolvedNode, true));
		}
        m_blockLookUp = null;
        m_loopLookUp = null;
	}
	
	private PathNode createPath(Block block, Node parent, boolean isRoot) {

        PathNode pathNode = new PathNode(block, parent, isRoot);      
        for (Block pathBlock : block.getPathBlocks()) {            
            if (pathBlock.numLoopPaths() > 0) {
            	LoopNode loopNode;
            	if (!m_loopLookUp.containsKey(pathBlock)){
            		loopNode = new LoopNode(pathBlock, pathNode);
            		m_loopLookUp.put(pathBlock, loopNode);
            		for (Block loopPath : pathBlock.getLoopPaths()) {
	                	loopNode.addNode(createPath(loopPath, loopNode, false));                    
	                }
            	} else {
            		loopNode = m_loopLookUp.get(pathBlock);
            	}
                pathNode.addNode(loopNode);
            }
            if (!m_blockLookUp.containsKey(pathBlock)){
            	BlockNode blockNode = new BlockNode(pathBlock, pathNode);
            	m_blockLookUp.put(pathBlock, blockNode);
            } 
        	pathNode.addNode(m_blockLookUp.get(pathBlock));    
        }
        block.doneWithPathBlocks();
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
