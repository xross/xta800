package com.xmos.xtagui.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Route;
import com.xmos.xtagui.XtaEngine;

public class Errors extends ViewPart implements Displayer {
    public static final boolean DEBUG = true;
    public static final String ID = "com.xmos.xtagui.views.Errors";

	public Errors() {
		if (DEBUG) System.out.printf("Errors()\n");			
		XtaEngine.getEngine().registerDisplayer(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Errors: createPartControl\n");			
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Errors: setFocus\n");			
	}

	public void displayRoute(Route route, DebugInfo debugInfo,
			MemoryImage memoryImage, SymbolTable table) {
		if (DEBUG) System.out.printf("Errors: displayRoute\n");			
	}

	public void notifyAnalysePaths() {
		if (DEBUG) System.out.printf("Errors: notifyAnalysePaths\n");			
	}

	public void notifyLoadExecutable() {
		if (DEBUG) System.out.printf("Errors: notifyLoadExecutable\n");			
	}

	public void notifyUpdateState() {
		if (DEBUG) System.out.printf("Errors: notifyUpdateState\n");			
	}
}
