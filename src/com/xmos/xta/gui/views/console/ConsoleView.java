package com.xmos.xta.gui.views.console;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.gui.XtaNotifier;
import com.xmos.xta.gui.nodes.Node;

public class ConsoleView extends ViewPart implements IPropertyChangeListener,
                                                     ISelectionListener {
    public static final String ID = "com.xmos.xta.gui.views.console.Console";

    private GuiConsole m_guiConsole;
    
	public ConsoleView() {
		XtaNotifier.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		m_guiConsole = new GuiConsole(parent);

		new Thread() {
	    	public void run() {
	    		m_guiConsole.execute();
    		}}.start();
    }

	public void setFocus() {
	}

	public void propertyChange(PropertyChangeEvent event) {
	}
	
    public void selectionChanged(IWorkbenchPart part, ISelection sel) {
        if (sel instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)sel).getFirstElement();
            if (object instanceof Node) {
                // Do selection updates here..
            }
        }
     }

    public synchronized void runCommand(String command) {
		m_guiConsole.sendCommand(command);
    }
}
