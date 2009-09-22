package com.xmos.xta.gui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

//import com.xmos.xta.gui.views.timedistribution.TimeDistributionView;

public class TimeDistributionExportJpgHandler extends AbstractHandler implements IHandler {
    public static final boolean DEBUG = false;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (DEBUG) System.out.printf("TimeDistributionExportJpgHandler: execute\n");	
//		TimeDistributionView control = (TimeDistributionView)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(TimeDistributionView.ID);
//		control.dumpJpg();
		return null;
	}
}