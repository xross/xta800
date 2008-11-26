package com.xmos.xta.rcp.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;

import com.xmos.xta.rcp.views.StructureView;

public class StructureCircleHandler extends AbstractHandler implements IHandler {
    public static final boolean DEBUG = false;
    private boolean m_isCircle = false;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		m_isCircle = !m_isCircle;
		StructureView control = (StructureView)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(StructureView.ID);
		control.setCircleMode(m_isCircle);
		
		if (DEBUG) System.out.printf("StructureCircleHandler: execute - circle (%d)\n", m_isCircle ? 1 : 0);			
		return null;
	}

}
