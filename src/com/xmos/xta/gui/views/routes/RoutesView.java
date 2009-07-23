package com.xmos.xta.gui.views.routes;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.gui.Broadcaster;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.views.routes.selected.SelectedRouteViewer;
import com.xmos.xta.gui.views.routes.selector.RouteSelectorViewer;

public class RoutesView extends ViewPart implements IPropertyChangeListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.gui.views.routes.Routes";
    
    private SelectedRouteViewer m_selectedRouteViewer;
    private RouteSelectorViewer m_routeSelectorViewer;
    
	public RoutesView() {
		if (DEBUG) System.out.printf("RoutesView()\n");			
		GuiEngine.addBroadcastListener(this);
	}

	public void createPartControl(final Composite parent) {
	    if (DEBUG) System.out.printf("RoutesView: createPartControl\n");
		
	    parent.setLayout(new FormLayout());

	    final Sash sash = new Sash(parent, SWT.HORIZONTAL);
	    final FormData sashData = new FormData();
	    sashData.top = new FormAttachment(70, 0); 
	    sashData.left = new FormAttachment(0, 0); 
	    sashData.right = new FormAttachment(100, 0); 
	    sash.setLayoutData(sashData);
	    
	    m_selectedRouteViewer = new SelectedRouteViewer(parent);
	    m_selectedRouteViewer.create(sash);

	    m_routeSelectorViewer = new RouteSelectorViewer(parent);
	    m_routeSelectorViewer.create(sash);	    

	    // Control the sash re-sizing
	    final int limit = 40;
	    sash.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	          Rectangle sashRect = sash.getBounds();
	          Rectangle shellRect = parent.getClientArea();
	          int bottom = shellRect.height - sashRect.height - limit;
	          e.y = Math.max(Math.min(e.y, bottom), limit);
	          if (e.y != sashRect.y) {
	            sashData.top = new FormAttachment(0, e.y);
	            parent.layout();
	          }
	        }
	    });

		initialiseTrees();
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("RoutesView: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("RoutesView: propertyChange: %s\n", event.getProperty());	
		
		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
		    m_routeSelectorViewer.initialise();
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				getActivePage().showView(RoutesView.ID);
			} catch (PartInitException e) {}
			
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
