package com.xmos.xta.rcp;

import java.util.ArrayList;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.PlatformUI;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.CodeReference;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Engine;
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

	private ISelection m_focusSelection;
	private CodeReference m_startPoint;
	private CodeReference m_endPoint;
	private CodeReference m_codePoint;
	private String m_notifyArgument;

	private XtaDisplayer() {
		m_listeners = new ArrayList<IPropertyChangeListener>();
		
		m_startPoint = null;
		m_endPoint = null;
		m_codePoint = null;
		m_notifyArgument = null;
	}
	
	public static XtaDisplayer getInstance() {
		if (m_displayer == null) {
			m_displayer = new XtaDisplayer();					
		}
		return m_displayer;		
	}

	public void addPropertyChangeListener(final IPropertyChangeListener listener) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {        	
				if (!m_listeners.contains(listener))
					m_listeners.add(listener);
			}});
	}

	public void removePropertyChangeListener(final IPropertyChangeListener listener) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {        	
				m_listeners.remove(listener);
			}});
	}
		
	public void displayRoute(Route route, DebugInfo debugInfo,
			MemoryImage memoryImage, SymbolTable table) {		
		if (DEBUG) System.out.printf("XtaDisplayer: displayRoute\n");		
		createHierarchy(route, debugInfo, memoryImage, table);
		broadcastPropertyChange(new PropertyChangeEvent(this, NOTIFY_DISPLAY_ROUTE, null, null));
	}

	public void notify(String message, String argument) {
		if (DEBUG) System.out.printf("XtaDisplayer: notify %s (%s)\n", message, argument);

		if (message.equals(NOTIFY_SET_START_POINT)) {
			notifySetStartPoint(new CodeReference(argument));
		} else if (message.equals(NOTIFY_SET_END_POINT)) {
			notifySetEndPoint(new CodeReference(argument));
		} else if (message.equals(NOTIFY_UPDATE_EXCLUDES)) {
			m_notifyArgument = argument;
			m_routeInfo.buildRouteNode();
			broadcastPropertyChange(new PropertyChangeEvent(this, message, null, null));
		} else if (message.equals(NOTIFY_LOAD_EXECUTABLE)) {
			//set the displayer state to the uninitialised state
			m_startPoint = null;
			m_endPoint = null;
			m_codePoint = null;
			m_notifyArgument = null;
			m_routeInfo = null;
			m_notifyArgument = argument;
			broadcastPropertyChange(new PropertyChangeEvent(this, message, null, null));
		} else {
			m_notifyArgument = argument;
			broadcastPropertyChange(new PropertyChangeEvent(this, message, null, null));
		}
	}

	public void notifySetFocus(ISelection selection) {
		if (DEBUG) System.out.println("XtaDisplayer: notifySetFocus " + selection);
		m_focusSelection = selection;
		broadcastPropertyChange(new PropertyChangeEvent(this, NOTIFY_SET_FOCUS, null, null));
	}	

	public void notifySetStartPoint(CodeReference startPoint) {
		if (DEBUG) System.out.printf("XtaDisplayer: notifySetStartPoint\n");
		Engine engine = XtaEngine.getEngine();
		startPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());
    	m_startPoint = startPoint;
		broadcastPropertyChange(new PropertyChangeEvent(this, NOTIFY_SET_START_POINT, null, null));
	}
		
	public void notifySetEndPoint(CodeReference endPoint) {
		if (DEBUG) System.out.printf("XtaDisplayer: notifySetStartPoint\n");
		Engine engine = XtaEngine.getEngine();
		endPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());
		m_endPoint = endPoint;
		broadcastPropertyChange(new PropertyChangeEvent(this, NOTIFY_SET_END_POINT, null, null));
	}

	public void notifyShow(CodeReference codePoint) {
		m_codePoint = codePoint;
		broadcastPropertyChange(new PropertyChangeEvent(this, NOTIFY_SHOW_CODE_POINT, null, null));
	}

	public void broadcastPropertyChange(final PropertyChangeEvent event) {			
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {        	
				for (IPropertyChangeListener listener : m_listeners) {
					listener.propertyChange(event);
				}
			}});
	}
	
	public XtaRouteInfo getRouteInfo() {
		return m_routeInfo;
	}
	
	public ISelection getFocusSelection() {
		return m_focusSelection;
	}
	
	public CodeReference getStartPoint() {
		return m_startPoint;
	}
	
	public CodeReference getEndPoint() {
		return m_endPoint;
	}

	public CodeReference getCodePoint() {
		return m_codePoint;
	}
	
	public String getNotifyArgument() {
		return m_notifyArgument;
	}

	private void createHierarchy(Route route, DebugInfo debugInfo,
			MemoryImage memoryImage, SymbolTable table) {		
        /*
		RouteNode routeNode = new RouteNode();
		
        for (Block block : route.completePaths()) {
        	routeNode.addNode(createPath(block, routeNode, true));
        }		
        
        if (DEBUG) {
        	displayHierarchy(routeNode, 0);
        }   
        */     
		RouteNode routeNode = null;
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
