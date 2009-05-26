package com.xmos.xta.gui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;

import com.xmos.xta.gui.views.structure.StructureView;

public class StructureCumulativeHandler extends AbstractHandler implements IHandler {
    public static final boolean DEBUG = false;
	private boolean m_isCumulative = false;
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (DEBUG) System.out.printf("StructureCumulativeHandler: execute %s\n",
				!m_isCumulative ? "false" : "true");			

		StructureView control = (StructureView)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(StructureView.ID);
		m_isCumulative = !m_isCumulative;
		control.setWeightCumulative(m_isCumulative);
		return null;
	}

}
