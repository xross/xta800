package com.xmos.xta.gui.views.info;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.views.info.sections.AllEndpointsViewer;
import com.xmos.xta.gui.views.info.sections.AllSrcLabelsViewer;
import com.xmos.xta.gui.views.info.sections.BranchesViewer;
import com.xmos.xta.gui.views.info.sections.EndpointsViewer;
import com.xmos.xta.gui.views.info.sections.ExclusionsViewer;
import com.xmos.xta.gui.views.info.sections.LabelsViewer;
import com.xmos.xta.gui.views.info.sections.SrcLabelsViewer;
import com.xmos.xta.gui.views.info.unknowns.KnownsViewer;
import com.xmos.xta.gui.views.info.unknowns.UnknownsViewer;

public class InfoView extends ViewPart implements IPropertyChangeListener,
		                                              ISelectionListener {
	public static final boolean DEBUG = false;
	public static final String ID = "com.xmos.xta.gui.views.info.Info";

	private UnknownsViewer m_unknowns;
	private KnownsViewer m_knowns;
	private ExclusionsViewer m_exclusions;
	private EndpointsViewer m_endpoints;
	private AllEndpointsViewer m_allEndpoints;
	private SrcLabelsViewer m_srcLabels;
	private AllSrcLabelsViewer m_allSrcLabels;	
	private BranchesViewer m_branches;	
	private LabelsViewer m_labels;	
	
	public InfoView() {
		if (DEBUG) System.out.printf("Info()\n");
		GuiEngine.addBroadcastListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Info: createPartControl\n");

		TabFolder folders = new TabFolder(parent, SWT.BORDER);
		m_unknowns = new UnknownsViewer(parent.getDisplay(), parent, getSite(), 
				createNewViewer("Unknowns", folders));		
		m_knowns = new KnownsViewer(parent.getDisplay(), parent, getSite(),
				createNewViewer("Knowns", folders));		
		m_exclusions = new ExclusionsViewer(parent.getDisplay(), parent, getSite(),
				createNewViewer("Exclusions", folders));		
		m_endpoints = new EndpointsViewer(parent.getDisplay(), parent, getSite(),
				createNewViewer("Endpoints", folders));				
		m_allEndpoints = new AllEndpointsViewer(parent.getDisplay(), parent, getSite(),
				createNewViewer("All Endpoints", folders));		
		m_srcLabels = new SrcLabelsViewer(parent.getDisplay(), parent, getSite(),
				createNewViewer("SrcLabels", folders));		
		m_allSrcLabels = new AllSrcLabelsViewer(parent.getDisplay(), parent, getSite(),
				createNewViewer("All SrcLabels", folders));		
		m_branches = new BranchesViewer(parent.getDisplay(), parent, getSite(),
				createNewViewer("Branches", folders));	
		m_labels = new LabelsViewer(parent.getDisplay(), parent, getSite(),
				createNewViewer("Labels", folders));	

		m_unknowns.update();
		m_knowns.update();
		m_exclusions.update();
		m_endpoints.update();
		m_allEndpoints.update();
		m_srcLabels.update();
		m_allSrcLabels.update();
		m_branches.update();
		m_labels.update();

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
			m_exclusions.update();			
			m_endpoints.update();
			m_allEndpoints.update();
			m_srcLabels.update();
			m_allSrcLabels.update();
			m_branches.update();
			m_labels.update();
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_EXCLUSIONS)) {
			m_exclusions.update();
	        
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
		m_unknowns.updateHighlighting(selectedNodes);
	}
	
	private TableViewer createNewViewer(String displayText, TabFolder folders) {
		TabItem tab = new TabItem(folders, SWT.NONE);
		tab.setText(displayText);
		Composite composite = new Composite(folders, SWT.NONE);
		composite.setLayout(new FillLayout());
		TableViewer viewer = new TableViewer(composite);
		tab.setControl(composite);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);		
		return viewer;
	}
	
}
