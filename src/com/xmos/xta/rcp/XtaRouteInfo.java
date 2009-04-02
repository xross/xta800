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
	
	private Route m_selectedRoute;
	private Engine m_engine;
	
	private List<Node> m_routes;
	private Node m_routeNode;
	private Node m_unresolvedNode;
	private Node m_selectedNode;
	
	public XtaRouteInfo(Engine engine) {
		m_engine = engine;
		m_routeNode = null;
		m_selectedNode = null;
		m_selectedNode = null;
		
		m_routes = new ArrayList<Node>();
		buildRouteNode(null);
	}
	
	public void update(Route selectedRoute) {
		m_routes.clear();
		for (Route route : m_engine.getRoutes()) {
			m_routes.add(new RouteNode(route));
		}
		buildRouteNode(selectedRoute);
	}

	private void buildRouteNode(Route selectedRoute) {
		m_routeNode = null;
		m_unresolvedNode = null;
		m_selectedRoute = selectedRoute;
		if (m_selectedRoute == null)
			return;
	
		if (m_selectedRoute.getAllBasicBlocks().size() > 2000){
			return;
		}
		
		int routeId = m_selectedRoute.getRouteId();
		PathGroupNode routeNode = new PathGroupNode("complete", routeId);
		int i = 0;
        for (PathBasicBlock block : m_selectedRoute.getCompletedPaths()) {
        	i++;
        	routeNode.addNode(createPath(block, routeNode, true));
        }		
        m_routeNode = routeNode;
		m_selectedNode = routeNode;
		
		m_unresolvedNode = new PathGroupNode("unresolved", routeId);
		for (Unresolved unresolved : m_selectedRoute.getUnresolvedPaths()) {
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
	
	public Route getRoute() {
		return m_selectedRoute;
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

	public Node getSelectedNode() {
		return m_selectedNode;
	}	

	public Node getUnresolvedNode() {
		return m_unresolvedNode;
	}

	public List<Node> getRoutes() {
		return m_routes;
	}

	public void setSelectedNode(Node selectedNode) {
		m_selectedNode = selectedNode;
	}

	public void setRoute(Route route) {
		m_selectedRoute = route;
	}

	public void resetState() {
		m_routeNode = null;
		m_selectedNode = null;
		m_selectedRoute = null;
		m_unresolvedNode = null;
		m_routes.clear();
	}	
}
