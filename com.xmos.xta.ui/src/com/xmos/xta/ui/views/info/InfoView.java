package com.xmos.xta.ui.views.info;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;

public class InfoView extends ViewPart implements IPropertyChangeListener {
	public static final String ID = "com.xmos.xta.ui.views.info.Info";
	
	private Viewer m_currentViewer;
	
	public InfoView() {
		GuiEngine.addBroadcastListener(this);
	}

	public void dispose() {
		GuiEngine.removeBroadcastListener(this);
		super.dispose();
	}

	public void createPartControl(final Composite parent) {
        final ViewerFactory viewerFactory = new ViewerFactory();
		
		final Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new FormLayout());		

		final Sash sash = new Sash(composite, SWT.VERTICAL);
	    FormData data = new FormData();	    
	    data.top    = new FormAttachment(0, 0);
	    data.bottom = new FormAttachment(100, 0);
	    data.left   = new FormAttachment(20, 0);
	    sash.setLayoutData(data);

	    // Create the available viewers tree and attach its right edge to the sash.
		final Tree tree = new Tree(composite, SWT.BORDER | SWT.V_SCROLL);
		TreeItem first = viewerFactory.initialiseTree(tree);
		tree.setSelection(first);

		final FormData treeData = new FormData();
		treeData.top    = new FormAttachment(0, 0);
		treeData.bottom = new FormAttachment(100, 0);
		treeData.left   = new FormAttachment(0, 0);
		treeData.right  = new FormAttachment(sash, 0);
		tree.setLayoutData(treeData);

	    // Create the default info viewer and attach its left edge to the sash.
		m_currentViewer = viewerFactory.createDefaultViewer(composite, getSite());				

		final FormData controlData = new FormData();
		controlData.top    = new FormAttachment(0, 0);
		controlData.bottom = new FormAttachment(100, 0);
		controlData.left   = new FormAttachment(sash, 0);
		controlData.right  = new FormAttachment(100, 0);
		m_currentViewer.getControl().setLayoutData(controlData);		

        // Control the sash re-sizing.
	    sash.addSelectionListener(new SelectionAdapter() {
	        public void widgetSelected(SelectionEvent event) {
	          ((FormData)sash.getLayoutData()).left = new FormAttachment(0, event.x);
	          sash.getParent().layout();
	        }
	    });	    
	    
	    // Swaps viewers on tree selection events.
	    tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			    String selection = tree.getSelection()[0].getText();
		    	m_currentViewer.getControl().dispose();			    
			    m_currentViewer = viewerFactory.createViewer(selection, composite, getSite());			    
		    	m_currentViewer.getControl().setLayoutData(controlData);
			    composite.layout(true);
		    }
		});
	}

	public void setFocus() {
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE) ||
		    event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE) ||
			event.getProperty().equals(Notifier.NOTIFY_REMOVE_ROUTE) ||
			event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS) ||
			event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE) ||
		    event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE) || 
			event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE) ||
		    event.getProperty().equals(Notifier.NOTIFY_UPDATE_EXCLUSIONS) ||
		    event.getProperty().equals(Notifier.NOTIFY_ERROR) || 
			event.getProperty().equals(Notifier.NOTIFY_WARNING)) {
			m_currentViewer.update();

		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SELECTION_CHANGED)) {
			m_currentViewer.updateHighlighting(GuiEngine.getSelectedNodes());
		}
	}
}
