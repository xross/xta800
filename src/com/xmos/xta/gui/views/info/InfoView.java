package com.xmos.xta.gui.views.info;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.views.info.knowns.KnownsViewer;
import com.xmos.xta.gui.views.info.unknowns.UnknownsViewer;

public class InfoView extends ViewPart implements IPropertyChangeListener,
		                                              ISelectionListener {
	public static final boolean DEBUG = false;
	public static final String ID = "com.xmos.xta.gui.views.info.Info";

	private UnknownsViewer m_unknowns;
	private KnownsViewer m_knowns;
	
	public InfoView() {
		if (DEBUG) System.out.printf("Info()\n");
		GuiEngine.addBroadcastListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Info: createPartControl\n");

		TabFolder folders = new TabFolder(parent, SWT.BORDER);
		m_unknowns = new UnknownsViewer(parent.getDisplay(), parent, getSite(), folders);
		m_knowns = new KnownsViewer(parent, getSite(), folders);

		m_unknowns.update();
		m_knowns.update();

		getSite().getPage().addSelectionListener(this);
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Info: setFocus\n");
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Info: propertyChange: %s\n", event.getProperty());

		if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS)) {
			m_unknowns.update();
			m_knowns.update();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE) ||
				   event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE) ||
				   event.getProperty().equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
			m_unknowns.update();
			m_knowns.update();
			
		}  else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE)) {
			m_unknowns.update();
			m_knowns.update();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE) || 
				   event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			m_unknowns.clear();
			m_knowns.clear();
		}
	}

	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		if (DEBUG) System.out.printf("Info: selectionChanged: %s\n");

		List<Node> selectedNodes = new ArrayList<Node>();
		
		if (!sel.isEmpty()) {
			if (sel instanceof IStructuredSelection) {
				for (Object object : ((IStructuredSelection)sel).toArray()) {
					if (object instanceof Node) {
						selectedNodes.add((Node)object);
					}
				}
			}
		}
		//TODO
		//m_unknowns.updateHighlighting(selectedNodes);
	}
}
