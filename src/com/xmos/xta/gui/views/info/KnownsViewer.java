package com.xmos.xta.gui.views.info;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;
import com.xmos.xta.gui.GuiEngine;

class KnownsViewer extends InfoViewer {
	private Action m_clearInstructionTimesAction;

	KnownsViewer(Display display, Composite parent, IWorkbenchPartSite site, TabFolder folders) {
		super(display, parent, site, folders, "Knowns");
	}

	InfoViewerContentProvider createContentProvider() {
		return new InfoViewerContentProvider(UnknownState.KNOWN);
	}
	
	InfoViewerLabelProvider createLabelProvider() {
		return new InfoViewerLabelProvider();
	}

	void enableContextMenuActions(int numInstructionTimeNodes, int numLoopIterationsNodes) {
		m_clearInstructionTimesAction.setEnabled(false);
		
		if ((numInstructionTimeNodes > 0) && (numLoopIterationsNodes == 0)) {
			m_clearInstructionTimesAction.setEnabled(true);					
		}		
	}
	
	void createContextMenuActions(MenuManager menuMgr) {		
		// Create the 'Unset Instruction Time' action
		m_clearInstructionTimesAction = new Action() { 
			public void run() {
				for (Node node : getSelectedNodes()) {
					GuiEngine.getEngine().unsetInstructionTimeTaken(node);
				}
			}
		};
		m_clearInstructionTimesAction.setText("Clear instruction time");
		menuMgr.add(m_clearInstructionTimesAction);
		m_clearInstructionTimesAction.setEnabled(false);
	}
	
	void update() {
		getViewer().setInput(GuiEngine.getEngine().getSelectedRoute());
	}	
	
	void updateHighlighting(List<Node> selectedNodes) {
	}
	
}
