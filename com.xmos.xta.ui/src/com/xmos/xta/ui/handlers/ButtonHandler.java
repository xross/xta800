package com.xmos.xta.ui.handlers;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;

public abstract class ButtonHandler extends AbstractHandler implements IPropertyChangeListener, IElementUpdater {
	protected Set<String> m_enabledViews;
	protected String m_viewId;
	protected String m_commandId;
	protected String m_stateId;
	
    public ButtonHandler(Set<String> enabledViews, String viewId, String commandId, String stateId) {
    	m_enabledViews = enabledViews;
    	m_viewId = viewId;
    	m_commandId = commandId;
    	m_stateId = stateId;
    	updateEnable();
    	GuiEngine.addBroadcastListener(this);
    }

    public void dispose() {
    	GuiEngine.removeBroadcastListener(this);
    	super.dispose();
    }

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Broadcaster.BROADCAST_BUTTON_STATE_CHANGED)) {
			updateEnable();
		}
	}
	
	private void updateEnable() {
		if (m_enabledViews.contains(m_commandId)) {
			setBaseEnabled(true);
		} else {
			setBaseEnabled(false);
		}
	}

	@SuppressWarnings("unchecked")
	public void updateElement(UIElement element, Map parameters) {
		// No button state to keep track of
	}
}
