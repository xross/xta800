package com.xmos.xta.ui.handlers;

import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.handlers.HandlerUtil;

import com.xmos.xta.ui.views.ViewControls;

public class ZoomResetHandler extends ButtonHandler implements IPropertyChangeListener {

	public ZoomResetHandler(Set<String> enabledViews, String viewId, String commandId) {
		super(enabledViews, viewId, commandId, "");
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ViewControls control = (ViewControls)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(m_viewId);
		control.zoomReset();
		return null;
	}
}
