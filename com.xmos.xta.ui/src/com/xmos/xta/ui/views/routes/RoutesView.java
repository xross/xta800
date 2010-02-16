package com.xmos.xta.ui.views.routes;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.views.routes.selected.SelectedRouteViewer;
import com.xmos.xta.ui.views.routes.selector.RouteSelectorViewer;

public class RoutesView extends ViewPart implements IPropertyChangeListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.ui.views.routes.Routes";
    
    private SelectedRouteViewer m_selectedRouteViewer;
    private RouteSelectorViewer m_routeSelectorViewer;
    
	public RoutesView() {
		if (DEBUG) System.out.printf("RoutesView()\n");			
		GuiEngine.addBroadcastListener(this);
	}
	
	public void dispose() {
		GuiEngine.removeBroadcastListener(this);
		super.dispose();
	}

	public void createPartControl(final Composite parent) {
	    if (DEBUG) System.out.printf("RoutesView: createPartControl\n");
	    parent.setLayout(new FillLayout());

	    SashForm sash = new SashForm(parent, SWT.VERTICAL);
	    
	    m_routeSelectorViewer = new RouteSelectorViewer(sash);
	    m_selectedRouteViewer = new SelectedRouteViewer(sash);
	    sash.setWeights(new int[] {1, 4});

		initialiseTrees();
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("RoutesView: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("RoutesView: propertyChange: %s\n", event.getProperty());	
		
		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
		    m_routeSelectorViewer.initialise();

		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			m_routeSelectorViewer.clear();
			m_routeSelectorViewer.refresh();
			m_selectedRouteViewer.clear();
			m_selectedRouteViewer.refresh();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			m_selectedRouteViewer.clear();
			m_selectedRouteViewer.refresh();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE) ||
				   event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS)) {
			refreshTrees();
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SRC_COMMANDS_DONE)) {
			initialiseTrees();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE)) {
			m_selectedRouteViewer.initialise();

		} else if (event.getProperty().equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
		    m_routeSelectorViewer.initialise();
		    m_selectedRouteViewer.initialise();

		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SELECT_NODES)) {
			m_selectedRouteViewer.selectNodes();
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SELECTION_CHANGED)) {
			m_selectedRouteViewer.updateSelection();			
		}
	}
	
	private void initialiseTrees() {		
	    m_selectedRouteViewer.initialise();
	    m_routeSelectorViewer.initialise();
	}
		
	private void refreshTrees() {
		m_selectedRouteViewer.refresh();
		m_routeSelectorViewer.refresh();
	}
}
