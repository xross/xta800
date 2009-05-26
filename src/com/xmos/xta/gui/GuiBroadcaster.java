package com.xmos.xta.gui;

import java.util.ArrayList;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;

public class GuiBroadcaster {
	private ArrayList<IPropertyChangeListener> m_listeners;

    public GuiBroadcaster() {
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
