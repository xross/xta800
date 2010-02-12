package com.xmos.xta.ui.handlers;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.xmos.xta.ui.views.ViewControls;

public class PieHandler extends ButtonHandler implements IPropertyChangeListener, IElementUpdater {
	
    public PieHandler(Set<String> enabledViews, String viewId, String commandId, String stateId) {
    	super(enabledViews, viewId, commandId, stateId);
    }

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = service.getCommand(m_commandId);
		State state = command.getState(m_stateId);
		boolean toggled = (Boolean) state.getValue();
		state.setValue(!toggled);
		
		ViewControls control = (ViewControls)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(m_viewId);
		control.setPie(!toggled);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void updateElement(UIElement element, Map parameters) {
		ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = service.getCommand(m_commandId);
		State state = command.getState(m_stateId);
		boolean toggled = (Boolean) state.getValue();
		element.setChecked(toggled);
	}
}
