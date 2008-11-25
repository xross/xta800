package com.xmos.xta.rcp;

import java.util.ArrayList;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Route;
import com.xmos.xta.rcp.nodes.BlockNode;
import com.xmos.xta.rcp.nodes.LoopNode;
import com.xmos.xta.rcp.nodes.Node;
import com.xmos.xta.rcp.nodes.PathNode;
import com.xmos.xta.rcp.nodes.RouteNode;

public class XtaDisplayer implements Displayer {
    public static final boolean DEBUG = false;	
	
	private static XtaDisplayer m_displayer;
	private ArrayList<IPropertyChangeListener> m_listeners;
	private XtaRouteInfo m_routeInfo;
	
	private XtaDisplayer() {
		m_listeners = new ArrayList<IPropertyChangeListener>();
	}
	
	public static XtaDisplayer getInstance() {
		if (m_displayer == null) {
			m_displayer = new XtaDisplayer();					
		}
		return m_displayer;		
	}

	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		if(!m_listeners.contains(listener))
			m_listeners.add(listener);
	}

	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		m_listeners.remove(listener);
	}
		
	public void displayRoute(Route route, DebugInfo debugInfo,
			MemoryImage memoryImage, SymbolTable table) {		
		if (DEBUG) System.out.printf("XtaDisplayer: displayRoute\n");		
		createHierarchy(route, debugInfo, memoryImage, table);
		broadcastPropertyChange(new PropertyChangeEvent(this, "DisplayRoute", null, null));
	}

	public void notifyAnalysePaths() {
		if (DEBUG) System.out.printf("XtaDisplayer: notifyAnalysePaths\n");
		broadcastPropertyChange(new PropertyChangeEvent(this, "AnalysePaths", null, null));
	}

	public void notifyLoadExecutable() {
		if (DEBUG) System.out.printf("XtaDisplayer: notifyLoadExecutable\n");
		broadcastPropertyChange(new PropertyChangeEvent(this, "LoadExecutable", null, null));
	}

	public void notifyUpdateState() {
		if (DEBUG) System.out.printf("XtaDisplayer: notifyUpdateState\n");
		broadcastPropertyChange(new PropertyChangeEvent(this, "UpdateState", null, null));
	}
		
	public void broadcastPropertyChange(PropertyChangeEvent event) {
		for (IPropertyChangeListener listener : m_listeners) {
			listener.propertyChange(event);
		}		
	}	
	
	public XtaRouteInfo getRouteInfo() {
		return m_routeInfo;
	}
	
	private void createHierarchy(Route route, DebugInfo debugInfo,
			MemoryImage memoryImage, SymbolTable table) {		
        RouteNode routeNode = new RouteNode();
        for (Block block : route.completePaths()) {
        	routeNode.addNode(createPath(block, routeNode, true));
        }		
        
        if (DEBUG) {
        	displayHierarchy(routeNode, 0);
        }                
        m_routeInfo = new XtaRouteInfo(route, debugInfo, memoryImage, table, routeNode);
	}

	private PathNode createPath(Block block, Node parent, boolean isRoot) {
        PathNode pathNode = new PathNode(block, parent, isRoot);
                
        for (Block pathBlock : block.getPathBlocks()) {            
            if (pathBlock.numLoopPaths() > 0) {
                LoopNode loopNode = new LoopNode(pathBlock, pathNode);
                pathNode.addNode(loopNode);
                
                for (Block loopPath : pathBlock.getLoopPaths()) {
                	loopNode.addNode(createPath(loopPath, loopNode, false));                    
                }
            }
        	pathNode.addNode(new BlockNode(pathBlock, pathNode));            
        }
        block.doneWithPathBlocks();
        return pathNode;
	}
	
	private void displayHierarchy(Node node, int indent) {
		printIndent(indent);
		System.out.printf("%s\n", node.getTypeName());
		for (Node subNode : node.getNodes()) {
			displayHierarchy(subNode, indent + 4);
		}
	}	

	private void printIndent(int indent) {
		for (int i = 0; i < indent; ++i) {
			System.out.printf(" ");
		}
	}	
}
