package com.xmos.xta.ui.views.info.unknowns;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Unknown.State;
import com.xmos.xta.ui.GuiEngine;

public class KnownsViewer extends InfoViewer {
	private Action m_clearInstructionTimesAction;

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
					GuiEngine.getEngine().unsetInstructionTime(GuiEngine.s_guiCommandSrc, node);
				}
			}
		};
		m_clearInstructionTimesAction.setText("Clear instruction time");
		menuMgr.add(m_clearInstructionTimesAction);
		m_clearInstructionTimesAction.setEnabled(false);
	}	
}
