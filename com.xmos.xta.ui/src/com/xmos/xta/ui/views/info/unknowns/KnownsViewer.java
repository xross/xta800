package com.xmos.xta.ui.views.info.unknowns;

import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;
import com.xmos.xta.ui.GuiEngine;

public class KnownsViewer extends InfoViewer {
	public KnownsViewer(TableViewer tableViewer, Composite parent, IWorkbenchPartSite site) {
		super(tableViewer, parent, site);
		update();
	}

	public void update() {
		getViewer().setInput(GuiEngine.getEngine().getSelectedRoute());
	}	
	
	public void updateHighlighting(List<Node> selectedNodes) {
	}
	
	InfoViewerContentProvider createContentProvider() {
		return new InfoViewerContentProvider(State.KNOWN);
	}
	
	InfoViewerLabelProvider createLabelProvider() {
		return new InfoViewerLabelProvider();
	}

	void enableContextMenuActions(int numInstructionTimeNodes, int numLoopIterationsNodes) {
	}
	
	void createContextMenuActions(MenuManager menuMgr) {		
	}	
}
