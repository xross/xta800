package com.xmos.xta.gui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;

import com.xmos.xta.gui.editors.XtaEditor;

public class XtaEditorClearGreyHandler extends AbstractHandler implements IHandler {
    public static final boolean DEBUG = false;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (DEBUG) System.out.printf("XtaEditorClearGreyHandler: execute\n");			
		XtaEditor control = (XtaEditor)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(XtaEditor.ID);
		control.clearGrey();
		return null;
	}

}
