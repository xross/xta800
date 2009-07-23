package com.xmos.xta.gui.views.info;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.gui.Broadcaster;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.views.info.log.LogViewer;
import com.xmos.xta.gui.views.info.sections.AllCallsViewer;
import com.xmos.xta.gui.views.info.sections.AllEndpointsViewer;
import com.xmos.xta.gui.views.info.sections.AllSrcLabelsViewer;
import com.xmos.xta.gui.views.info.sections.BranchesViewer;
import com.xmos.xta.gui.views.info.sections.CallsViewer;
import com.xmos.xta.gui.views.info.sections.EndpointsViewer;
import com.xmos.xta.gui.views.info.sections.ExclusionsViewer;
import com.xmos.xta.gui.views.info.sections.LabelsViewer;
import com.xmos.xta.gui.views.info.sections.SrcCommandsViewer;
import com.xmos.xta.gui.views.info.sections.SrcLabelsViewer;
import com.xmos.xta.gui.views.info.sections.SrcLoopsViewer;
import com.xmos.xta.gui.views.info.sections.ThreadStartPointsViewer;
import com.xmos.xta.gui.views.info.sections.ThreadStopPointsViewer;
import com.xmos.xta.gui.views.info.unknowns.KnownsViewer;
import com.xmos.xta.gui.views.info.unknowns.UnknownsViewer;

public class InfoView extends ViewPart implements IPropertyChangeListener {
	public static final String ID = "com.xmos.xta.gui.views.info.Info";

	private LogViewer m_log;
	private UnknownsViewer m_unknowns;
	private KnownsViewer m_knowns;
	private ExclusionsViewer m_exclusions;
	private EndpointsViewer m_endpoints;
	private AllEndpointsViewer m_allEndpoints;
	private SrcLabelsViewer m_srcLabels;
	private AllSrcLabelsViewer m_allSrcLabels;	
	private CallsViewer m_calls;
	private AllCallsViewer m_allCalls;	
	private BranchesViewer m_branches;	
	private LabelsViewer m_labels;	
	private ThreadStartPointsViewer m_threadStartPoints;	
	private ThreadStopPointsViewer m_threadStopPoints;	
	private SrcCommandsViewer m_srcCommands;	
	private SrcLoopsViewer m_srcLoops;	
	
	public InfoView() {
		GuiEngine.addBroadcastListener(this);
	}

	public void createPartControl(Composite parent) {
		TabFolder folders = new TabFolder(parent, SWT.BORDER);		
		m_log = new LogViewer(parent.getDisplay(), parent, getSite(), 
				createNewViewer("Log", folders));		
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
		m_calls = new CallsViewer(parent.getDisplay(), parent, getSite(),
				createNewViewer("Calls", folders));		
		m_allCalls = new AllCallsViewer(parent.getDisplay(), parent, getSite(),
				createNewViewer("All Calls", folders));		
		m_branches = new BranchesViewer(parent.getDisplay(), parent, getSite(),
				createNewViewer("Branches", folders));	
		m_labels = new LabelsViewer(parent.getDisplay(), parent, getSite(),
				createNewViewer("Labels", folders));	
		m_threadStartPoints = new ThreadStartPointsViewer(parent.getDisplay(), parent, getSite(),
				createNewViewer("Thread Start Points", folders));	
		m_threadStopPoints = new ThreadStopPointsViewer(parent.getDisplay(), parent, getSite(),
				createNewViewer("Thread Stop Points", folders));
		m_srcCommands = new SrcCommandsViewer(parent.getDisplay(), parent, getSite(),
				createNewViewer("Src Level Commands", folders));
		m_srcLoops = new SrcLoopsViewer(parent.getDisplay(), parent, getSite(),
				createNewViewer("Src Level Loops", folders));
		
		m_log.update();
		m_unknowns.update();
		m_knowns.update();
		m_exclusions.update();
		m_endpoints.update();
		m_allEndpoints.update();
		m_srcLabels.update();
		m_allSrcLabels.update();
		m_calls.update();
		m_allCalls.update();
		m_branches.update();
		m_labels.update();
		m_threadStartPoints.update();
		m_threadStopPoints.update();
		m_srcCommands.update();
		m_srcLoops.update();
	}

	public void setFocus() {
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE) ||
				   event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE) ||
				   event.getProperty().equals(Notifier.NOTIFY_REMOVE_ROUTE) ||
				   event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS) ||
				   event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE)) {
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
			m_calls.update();
			m_allCalls.update();			
			m_branches.update();
			m_labels.update();
			m_threadStartPoints.update();
			m_threadStopPoints.update();
			m_srcCommands.update();
			m_srcLoops.update();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_EXCLUSIONS)) {
			m_exclusions.update();
		
		} else if (event.getProperty().equals(Notifier.NOTIFY_ERROR) || 
				   event.getProperty().equals(Notifier.NOTIFY_WARNING)) {
			m_log.update();

		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SELECTION_CHANGED)) {
			m_unknowns.updateHighlighting(GuiEngine.getSelectedNodes());
			
		}
		
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
