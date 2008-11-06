package com.xmos.xtagui.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Route;
import com.xmos.xtagui.XtaEngine;

public class Loop extends ViewPart implements Displayer {
    public static final boolean DEBUG = true;
    public static final String ID = "com.xmos.xtagui.views.Loop";

	public Loop() {
		if (DEBUG) System.out.printf("Loop()\n");			
		XtaEngine.getEngine().registerDisplayer(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Loop: createPartControl\n");			
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Loop: setFocus\n");			
	}

	public void displayRoute(Route route, DebugInfo debugInfo,
			MemoryImage memoryImage, SymbolTable table) {
		if (DEBUG) System.out.printf("Loop: displayRoute\n");			
	}

	public void notifyAnalysePaths() {
		if (DEBUG) System.out.printf("Loop: notifyAnalysePaths\n");			
	}

	public void notifyLoadExecutable() {
		if (DEBUG) System.out.printf("Loop: notifyLoadExecutable\n");			
	}

	public void notifyUpdateState() {
		if (DEBUG) System.out.printf("Loop: notifyUpdateState\n");			
	}
}
