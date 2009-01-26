package com.xmos.xta.rcp;

import java.util.ArrayList;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.PlatformUI;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.references.CodeReference;
import com.xmos.xta.engine.references.CodeReferenceFactory;
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
		resetState();
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
			notifySetStartPoint(CodeReferenceFactory.createCodeReference(argument));
		} else if (message.equals(NOTIFY_SET_END_POINT)) {
			notifySetEndPoint(CodeReferenceFactory.createCodeReference(argument));
		} else if (message.equals(NOTIFY_UPDATE_EXCLUDES)) {
			m_notifyArgument = argument;
			if (m_routeInfo != null) {
				m_routeInfo.buildRouteNode();
				broadcastPropertyChange(new PropertyChangeEvent(this, message, null, null));
			}
		} else if (message.equals(NOTIFY_LOAD_EXECUTABLE)) {
			resetState();
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
		if (startPoint != null) {
			Engine engine = XtaEngine.getEngine();
		    startPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());
		}
    	m_startPoint = startPoint;
		broadcastPropertyChange(new PropertyChangeEvent(this, NOTIFY_SET_START_POINT, null, null));
	}
		
	public void notifySetEndPoint(CodeReference endPoint) {
		if (DEBUG) System.out.printf("XtaDisplayer: notifySetStartPoint\n");
		if (endPoint != null) {
			Engine engine = XtaEngine.getEngine();
			endPoint.resolve(engine, engine.getNodeId(), engine.getCoreId());			
		}
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
	
	private void resetState() {
		m_routeInfo = null;
		m_startPoint = null;
		m_endPoint = null;
		m_codePoint = null;
		m_notifyArgument = null;		
	}

	private void createHierarchy(Route route, DebugInfo debugInfo,
			MemoryImage memoryImage, SymbolTable table) {		   
		RouteNode routeNode = null;
        m_routeInfo = new XtaRouteInfo(route, debugInfo, memoryImage, table, routeNode);
	}

}
