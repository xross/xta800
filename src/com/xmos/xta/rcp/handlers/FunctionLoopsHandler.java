package com.xmos.xta.rcp.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;

import com.xmos.xta.rcp.views.FunctionView;

public class FunctionLoopsHandler extends AbstractHandler implements IHandler {
    public static final boolean DEBUG = false;
    private boolean m_isActive = false;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		m_isActive = !m_isActive;
		FunctionView control = (FunctionView)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(FunctionView.ID);
		control.setShowLoops(m_isActive);
		
		if (DEBUG) System.out.printf("FunctionLoopsHandler: execute - enabled(%d)\n", m_isActive ? 1 : 0);			
		return null;
	}
}
