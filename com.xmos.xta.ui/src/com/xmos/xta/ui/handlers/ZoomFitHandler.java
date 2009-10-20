package com.xmos.xta.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.handlers.HandlerUtil;

import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.views.visualisations.VisualisationsView;

public class ZoomFitHandler extends AbstractHandler implements IPropertyChangeListener {
    public ZoomFitHandler() {
    	GuiEngine.addBroadcastListener(this);
    }

	public Object execute(ExecutionEvent event) throws ExecutionException {
		VisualisationsView control = (VisualisationsView)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(VisualisationsView.ID);
		control.zoomFit();
		return null;
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Broadcaster.BROADCAST_ZOOM_FIT_ENABLED)) {
			setBaseEnabled(true);
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_ZOOM_FIT_DISABLED)) {
			setBaseEnabled(false);
		}
	}		
}
