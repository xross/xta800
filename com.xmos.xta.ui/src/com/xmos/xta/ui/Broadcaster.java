package com.xmos.xta.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;

public class Broadcaster {	
	public static final String BROADCAST_SET_START_POINT    = "SetStartPoint";
	public static final String BROADCAST_SET_END_POINT      = "SetEndPoint";
	public static final String BROADCAST_SHOW_CODE_POINT    = "ShowCodePoint";
	public static final String BROADCAST_SELECT_NODES       = "ShowNodes";
	public static final String BROADCAST_HIGHLIGHTS_UPDATED = "HighlightsUpdated";
	public static final String BROADCAST_UPDATE_FOLDING     = "UpdateFolding";
	public static final String BROADCAST_SELECTION_CHANGED  = "SelectionChanged";
	public static final String BROADCAST_SET_FOCUS          = "SetFocus";
	
	private ArrayList<IPropertyChangeListener> m_listeners;

    public Broadcaster() {
    	m_listeners = new ArrayList<IPropertyChangeListener>();    	
    }
	    
	public void addListener(final IPropertyChangeListener listener) {
		if (!m_listeners.contains(listener))
			m_listeners.add(listener);
	}

	public void removeListener(final IPropertyChangeListener listener) {
		if (m_listeners.contains(listener))
			m_listeners.remove(listener);
	}	

	public void broadcast(final String message, final String argument) {		
		GuiEngine.updateGuiState(message, argument);
		
		final List<IPropertyChangeListener> listeners = getListeners();
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				PropertyChangeEvent event = new PropertyChangeEvent(this, message, null, null);				
				for (IPropertyChangeListener listener : listeners) {
					listener.propertyChange(event);
				}
			}
		});
	}
	
	private List<IPropertyChangeListener> getListeners() {
		// Note: This creates a copy of the listeners list so as to avoid the situation 
		// where the call to propertyChange for one listener needs to remove another
		// listener from the list. Without making the copy, we would get concurrent
		// modification errors..
		List<IPropertyChangeListener> listeners = new ArrayList<IPropertyChangeListener>();
		for (IPropertyChangeListener listener : m_listeners) {
			listeners.add(listener);
		}
		return listeners;
	}
}
