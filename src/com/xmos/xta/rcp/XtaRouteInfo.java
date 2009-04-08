package com.xmos.xta.rcp;

import java.util.ArrayList;
import java.util.List;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.PathBasicBlock;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.Unresolved;
import com.xmos.xta.rcp.nodes.BlockNode;
import com.xmos.xta.rcp.nodes.LoopNode;
import com.xmos.xta.rcp.nodes.Node;
import com.xmos.xta.rcp.nodes.PathGroupNode;
import com.xmos.xta.rcp.nodes.PathNode;
import com.xmos.xta.rcp.nodes.RouteNode;

public class XtaRouteInfo {
	
	private Engine m_engine;
	
	private List<Node> m_routes;
	private Node m_routeNode;
	private Node m_unresolvedNode;
	private List<Node> m_selectedNodes;
	
	public XtaRouteInfo(Engine engine) {
		m_engine = engine;
		m_routeNode = null;
		m_selectedNodes = new ArrayList<Node>();
		m_routes = new ArrayList<Node>();
		buildRouteNode();
	}
	
	public void update() {
		m_routes.clear();
		for (Route route : m_engine.getRoutes()) {
			m_routes.add(new RouteNode(route));
		}
		buildRouteNode();
	}

	private void buildRouteNode() {
		m_routeNode = null;
		m_unresolvedNode = null;
		Route selectedRoute = m_engine.getSelectedRoute();
		if (selectedRoute == null)
			return;
	
		if (selectedRoute.getAllBasicBlocks().size() > 2000){
			return;
		}
		
		int routeId = selectedRoute.getRouteId();
		PathGroupNode routeNode = new PathGroupNode("complete", routeId);
		int i = 0;
        for (PathBasicBlock block : selectedRoute.getCompletedPaths()) {
        	i++;
        	routeNode.addNode(createPath(block, routeNode, true));
        }		
        m_routeNode = routeNode;
		m_selectedNodes.clear();
		m_selectedNodes.add(routeNode);
		
		m_unresolvedNode = new PathGroupNode("unresolved", routeId);
		for (Unresolved unresolved : selectedRoute.getUnresolvedPaths()) {
        	i++;
			m_unresolvedNode.addNode(createPath(unresolved.getBlock(), m_unresolvedNode, true));
		}
	}
	
	private PathNode createPath(PathBasicBlock block, Node parent, boolean isRoot) {
        PathNode pathNode = new PathNode(block, parent, isRoot);     
        for (PathBasicBlock pathBlock : block.getPathBlocks()) {            
        	if (pathBlock.numLoopPaths(block) > 0){
        		LoopNode loopNode = new LoopNode(pathBlock, pathNode);
                pathNode.addNode(loopNode);
                                 
            	for (PathBasicBlock loopPath : pathBlock.getLoopPaths(block)) {
            		loopNode.addNode(createPath(loopPath, loopNode, false));                    
                }
        	}
        	if (pathBlock.getNumInstructionsThis()>0)
        		pathNode.addNode(new BlockNode(pathBlock, pathNode));            
        }
        return pathNode;
	}
	
	public DebugInfo getDebugInfo() {
		return m_engine.getDebugInfo();
	}

	public MemoryImage getMemoryImage() {
		return m_engine.getMemoryImage();
	}

	public SymbolTable getSymbolTable() {
		return m_engine.getSymbolTable();
	}
	
	public Node getRouteNode() {
		return m_routeNode;
	}	

	public List<Node> getSelectedNodes() {
		return m_selectedNodes;
	}	

	public Node getUnresolvedNode() {
		return m_unresolvedNode;
	}

	public List<Node> getRoutes() {
		return m_routes;
	}

	public void setSelectedNodes(List<Node> selectedNodes) {
		m_selectedNodes = selectedNodes;
	}

	public void resetState() {
		m_routeNode = null;
		m_selectedNodes.clear();
		m_unresolvedNode = null;
		m_routes.clear();
	}	
}
