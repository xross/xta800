package com.xmos.xta.gui;

import java.util.ArrayList;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;

public class Broadcaster {	
	public static final String BROADCAST_SET_START_POINT    = "SetStartPoint";
	public static final String BROADCAST_SET_END_POINT      = "SetEndPoint";
	public static final String BROADCAST_SET_FOCUS          = "SetFocus";
	public static final String BROADCAST_SHOW_CODE_POINT    = "ShowCodePoint";
	public static final String BROADCAST_SELECT_NODES       = "ShowNodes";
	public static final String BROADCAST_HIGHLIGHTS_UPDATED = "HighlightsUpdated";
	public static final String BROADCAST_UPDATE_FOLDING     = "UpdateFolding";
	
	private ArrayList<IPropertyChangeListener> m_listeners;

    public Broadcaster() {
    	m_listeners = new ArrayList<IPropertyChangeListener>();    	
    }
		
	public void addListener(final IPropertyChangeListener listener) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {        	
				if (!m_listeners.contains(listener))
					m_listeners.add(listener);
			}});
	}

	public void removeListener(final IPropertyChangeListener listener) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {      
				if (m_listeners.contains(listener))
					m_listeners.remove(listener);
			}
		});
	}	
	
	public void broadcast(final String message, final String argument) {		
		GuiEngine.updateGuiState(message, argument);
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				PropertyChangeEvent event = new PropertyChangeEvent(this, message, null, null);
				for (IPropertyChangeListener listener : m_listeners) {
					listener.propertyChange(event);
				}
			}
		});
	}		
}
