package com.xmos.xtagui.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xtagui.XtaDisplayer;
import com.xmos.xtagui.nodes.Node;

public class LoopView extends ViewPart implements IPropertyChangeListener,
                                                  ISelectionListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xtagui.views.Loop";

    private ISelectionListener m_selectionListener;
    
	public LoopView() {
		if (DEBUG) System.out.printf("Loop()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
		
		m_selectionListener = new ISelectionListener() {
	         public void selectionChanged(IWorkbenchPart part, ISelection sel) {
	            if (!(sel instanceof IStructuredSelection))
	               return;
	            IStructuredSelection ss = (IStructuredSelection)sel;
	            Object o = ss.getFirstElement();
	            if (o instanceof Node)
	        		if (DEBUG) System.out.printf("Loop: selectionChanged\n");			
	         }};	        
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Loop: createPartControl\n");			
		getSite().getPage().addSelectionListener(m_selectionListener);
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Loop: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Loop: propertyChange: %s\n", event.getProperty());			
	}	
	
	public void dispose() {
		if (DEBUG) System.out.printf("Loop: dispose\n");			
		getSite().getPage().removeSelectionListener(m_selectionListener);
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
