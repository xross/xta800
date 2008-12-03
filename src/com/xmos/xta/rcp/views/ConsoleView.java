package com.xmos.xta.rcp.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.rcp.XtaDisplayer;
import com.xmos.xta.rcp.nodes.Node;

public class ConsoleView extends ViewPart implements IPropertyChangeListener,
                                                     ISelectionListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.rcp.views.Console";

    private ConsoleViewControl m_consoleViewControl;
    private Boolean m_started = false;
    private List<String> m_pendingCommands = new ArrayList<String>();
    
	public ConsoleView() {
		if (DEBUG) System.out.printf("Console()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Console: createPartControl\n");

		m_consoleViewControl = new ConsoleViewControl(this, parent);

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

    public void runCommand(String command) {
		if (DEBUG) System.out.printf("Console: runCommand: %s\n", command);
		
		synchronized(this) {
			if (m_started) 
				m_consoleViewControl.sendCommand(command);
			else
				m_pendingCommands.add(command);
		}
     }

	public boolean hasPendingCommands() {
		synchronized(this) {
			m_started = true;
			if (m_pendingCommands.size() > 0)
				return true;
			else
				return false;
		}
	}

   public String getCommand() {
		synchronized(this) {
			String command = m_pendingCommands.remove(0);
			return command;
		}
     }
}
