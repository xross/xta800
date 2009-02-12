package com.xmos.xta.rcp.views.FlowView;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.nodes.Node;

public class FlowView extends ViewPart implements IPropertyChangeListener,
                                                  ISelectionListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.rcp.views.FlowView.Flow";

	public FlowView() {
		if (DEBUG) System.out.printf("Flow()\n");			
		XtaNotifier.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Flow: createPartControl\n");			
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Flow: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Flow: propertyChange: %s\n", event.getProperty());			
	}	
	
    public void selectionChanged(IWorkbenchPart part, ISelection sel) {
        if (sel instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)sel).getFirstElement();
            if (object instanceof Node) {
                // Do selection updates here..
            }
        }
     }	            		         	        	
}
