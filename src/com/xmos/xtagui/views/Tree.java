package com.xmos.xtagui.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Route;
import com.xmos.xtagui.XtaEngine;

public class Tree extends ViewPart implements Displayer {
    public static final boolean DEBUG = true;
    public static final String ID = "com.xmos.xtagui.views.Tree";

	public Tree() {
		if (DEBUG) System.out.printf("Tree()\n");			
		XtaEngine.getEngine().registerDisplayer(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Tree: createPartControl\n");			
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Tree: setFocus\n");			
	}

	public void displayRoute(Route route, DebugInfo debugInfo,
			MemoryImage memoryImage, SymbolTable table) {
		if (DEBUG) System.out.printf("Tree: displayRoute\n");			
	}

	public void notifyAnalysePaths() {
		if (DEBUG) System.out.printf("Tree: notifyAnalysePaths\n");			
	}

	public void notifyLoadExecutable() {
		if (DEBUG) System.out.printf("Tree: notifyLoadExecutable\n");			
	}

	public void notifyUpdateState() {
		if (DEBUG) System.out.printf("Tree: notifyUpdateState\n");			
	}
}
