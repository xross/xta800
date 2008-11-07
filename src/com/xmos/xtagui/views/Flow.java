package com.xmos.xtagui.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Route;
import com.xmos.xtagui.XtaEngine;

public class Flow extends ViewPart implements Displayer {
    public static final boolean DEBUG = true;
    public static final String ID = "com.xmos.xtagui.views.Flow";

	public Flow() {
		if (DEBUG) System.out.printf("Flow()\n");			
		XtaEngine.getEngine().registerDisplayer(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Flow: createPartControl\n");			
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Flow: setFocus\n");			
	}

	public void displayRoute(Route route, DebugInfo debugInfo,
			MemoryImage memoryImage, SymbolTable table) {
		if (DEBUG) System.out.printf("Flow: displayRoute\n");			
	}

	public void notifyAnalysePaths() {
		if (DEBUG) System.out.printf("Flow: notifyAnalysePaths\n");			
	}

	public void notifyLoadExecutable() {
		if (DEBUG) System.out.printf("Flow: notifyLoadExecutable\n");			
	}

	public void notifyUpdateState() {
		if (DEBUG) System.out.printf("Flow: notifyUpdateState\n");			
	}
}
