package com.xmos.xta.ui.views.binary.graphics.tree;

import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.widgets.Composite;

import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsCore;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsDevice;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsNode;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsObject;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsPin;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsPins;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsPort;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsPortBit;
import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsPorts;
import com.xmos.xta.ui.views.binary.graphics.objects.NullGraphicsObject;

public class GraphicsTreeViewer {	
    private TreeViewer m_viewer;
    private Composite m_parent;
    private GraphicsDevice m_root;
	
    public GraphicsTreeViewer(Composite parent) {    	
    	m_parent = parent;

    	m_viewer = new TreeViewer(m_parent);
	    m_viewer.setContentProvider(new GraphicsTreeContentProvider());				
	    m_viewer.setLabelProvider(new GraphicsTreeLabelProvider());
		ColumnViewerToolTipSupport.enableFor(m_viewer, ToolTip.NO_RECREATE);
		
		m_viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				GraphicsObject object = getSelectedObject();
				if (object != null) {
					if (object instanceof GraphicsPortBit) {
						GraphicsPortBit bit = (GraphicsPortBit)object;
						selectPort(bit.getNode(), bit.getCore(), bit.getPortName());
					} else if (object instanceof GraphicsPin) {
						GraphicsPin pin = (GraphicsPin)object;
						selectPin(pin.getPinName());
					}
				}
			}
		});
    }
    
    protected void selectPin(String name) {
    	if (m_root == null) {
    		return;
    	}
		
    	GraphicsPin pin = m_root.getPin(name);
    	if (pin != null) {
    		GraphicsPins pins = m_root.getPins();
			m_viewer.setExpandedState(pins, true);

    		m_viewer.setSelection(new StructuredSelection(pin), true);
			m_viewer.setExpandedState(pin, true);
    	}
	}

	protected void selectPort(int nodeNumber, int coreNumber, String name) {
    	if (m_root == null) {
    		return;
    	}

    	GraphicsPort port = m_root.getPort(nodeNumber, coreNumber, name);
    	if (port != null) {
    		GraphicsNode node = m_root.getNode(nodeNumber);
			m_viewer.setExpandedState(node, true);

			GraphicsCore core = node.getCore(coreNumber);
			m_viewer.setExpandedState(core, true);

			GraphicsPorts ports = core.getPorts();
			m_viewer.setExpandedState(ports, true);
    		
    		m_viewer.setSelection(new StructuredSelection(port), true);
    	}
	}

	public GraphicsObject getSelectedObject() {
		ISelection sel = m_viewer.getSelection();
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection selection = ((IStructuredSelection)sel);
			if (selection.size() == 1) {
				return (GraphicsObject) selection.getFirstElement();
			} 
        }
		return null;
	}

	public TreeViewer getViewer() {
    	return m_viewer;
    }
    
	public void setRoot(GraphicsDevice root) {
		clear();
		m_root = root;
		
		if (m_root != null) {
			GraphicsObject hiddenRouteNode = new NullGraphicsObject(m_root);
			m_viewer.setInput(hiddenRouteNode);
			m_viewer.expandToLevel(2);
			m_viewer.setSelection(new StructuredSelection(m_root), true);
		}
	}
    
	public void refresh() {
		m_viewer.refresh();
	}

	public void clear() {
		m_viewer.setInput(null);
	}
}
