package com.xmos.xta.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.handlers.HandlerUtil;

import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.views.ViewControls;

public class ZoomResetHandler extends AbstractHandler implements IPropertyChangeListener {
	private String m_view;
	
    public ZoomResetHandler(String view) {
    	m_view = view;
    	GuiEngine.addBroadcastListener(this);
    }

    public void dispose() {
    	GuiEngine.removeBroadcastListener(this);
    	super.dispose();
    }

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ViewControls control = (ViewControls)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(m_view);
		control.zoomReset();
		return null;
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getOldValue().equals(m_view)) {
			if (event.getProperty().equals(Broadcaster.BROADCAST_ZOOM_RESET_ENABLED)) {
				setBaseEnabled(true);
				
			} else if (event.getProperty().equals(Broadcaster.BROADCAST_ZOOM_RESET_DISABLED)) {
				setBaseEnabled(false);
			}
		}
	}			
}
