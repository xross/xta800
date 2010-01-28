package com.xmos.xta.ui.handlers;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.views.ViewControls;

public class PieHandler extends AbstractHandler implements IPropertyChangeListener, IElementUpdater {
	public static final String COMMAND_ID = "com.xmos.xta.ui.binary.pie.command";
	public static final String STATE_ID = "com.xmos.xta.ui.binary.pie.command.toggleState";

	private String m_view;
	private boolean m_toggled;
	
    public PieHandler(String view) {
    	m_view = view;
    	m_toggled = true;		
    	GuiEngine.addBroadcastListener(this);
    }

    public void dispose() {
    	GuiEngine.removeBroadcastListener(this);
    	super.dispose();
    }

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = service.getCommand(COMMAND_ID);
		State state = command.getState(STATE_ID);
		m_toggled = (Boolean) state.getValue();
		state.setValue(!m_toggled);
		
		ViewControls control = (ViewControls)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(m_view);
		control.setPie(m_toggled);
		return null;
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getOldValue().equals(m_view)) {
			if (event.getProperty().equals(Broadcaster.BROADCAST_PIE_ENABLED)) {
				setBaseEnabled(true);
				
			} else if (event.getProperty().equals(Broadcaster.BROADCAST_PIE_DISABLED)) {
				setBaseEnabled(false);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void updateElement(UIElement element, Map parameters) {
		element.setChecked(m_toggled);
	}		
}
