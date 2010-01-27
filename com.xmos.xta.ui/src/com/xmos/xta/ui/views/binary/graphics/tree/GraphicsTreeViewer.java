package com.xmos.xta.ui.views.binary.graphics.tree;

import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.widgets.Composite;

import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsObject;
import com.xmos.xta.ui.views.binary.graphics.objects.NullGraphicsObject;

public class GraphicsTreeViewer {	
    private TreeViewer m_viewer;
    private Composite m_parent;
	
    public GraphicsTreeViewer(Composite parent) {    	
    	m_parent = parent;

    	m_viewer = new TreeViewer(m_parent);
	    m_viewer.setContentProvider(new GraphicsTreeContentProvider());				
	    m_viewer.setLabelProvider(new GraphicsTreeLabelProvider());
		ColumnViewerToolTipSupport.enableFor(m_viewer, ToolTip.NO_RECREATE);
    }
    
    public TreeViewer getViewer() {
    	return m_viewer;
    }
    
	public void setGraphicsObject(GraphicsObject root) {
		clear();
		if (root != null) {
			GraphicsObject hiddenRouteNode = new NullGraphicsObject(root);
			
			m_viewer.setInput(hiddenRouteNode);
			m_viewer.expandToLevel(2);
			
			m_viewer.setSelection(new StructuredSelection(root), true);
		}
	}
    
	public void refresh() {
		m_viewer.refresh();
	}

	public void clear() {
		m_viewer.setInput(null);
	}
}
