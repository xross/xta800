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

public class ConsoleView extends ViewPart implements IPropertyChangeListener,
                                                     ISelectionListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xtagui.views.Console";

    private ConsoleViewControl m_consoleViewControl;
    
	public ConsoleView() {
		if (DEBUG) System.out.printf("Console()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Console: createPartControl\n");
		m_consoleViewControl = new ConsoleViewControl(parent);
		new Thread() {
	    	public void run() {
	    		m_consoleViewControl.execute();
    		}}.start();    	    	
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Console: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Console: propertyChange: %s\n", event.getProperty());			
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
