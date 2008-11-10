package com.xmos.xtagui;

import java.util.ArrayList;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Route;
import com.xmos.xtagui.properties.DisplayRouteProperty;

public class XtaDisplayer implements Displayer {
    public static final boolean DEBUG = true;	
	
	private static XtaDisplayer m_displayer;
	private ArrayList<IPropertyChangeListener> m_listeners;
	
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
	
	public void broadcastPropertyChange(PropertyChangeEvent event) {
		for (IPropertyChangeListener listener : m_listeners) {
			listener.propertyChange(event);
		}		
	}
	
	public void displayRoute(Route route, DebugInfo debugInfo,
			MemoryImage memoryImage, SymbolTable table) {		
		if (DEBUG) System.out.printf("XtaDisplayer: displayRoute\n");		
		DisplayRouteProperty property = new DisplayRouteProperty(
			route, debugInfo, memoryImage, table);
		broadcastPropertyChange(new PropertyChangeEvent(this, "DisplayRoute", null, property));
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
}
