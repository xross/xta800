package com.xmos.xta.rcp.views.ConsoleView;

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

import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.nodes.Node;

public class ConsoleView extends ViewPart implements IPropertyChangeListener,
                                                     ISelectionListener {
    public static final String ID = "com.xmos.xta.rcp.views.ConsoleView.Console";

    private GuiConsole m_consoleViewControl;
    private Boolean m_started = false;
    private List<String> m_pendingCommands = new ArrayList<String>();
    
	public ConsoleView() {
		XtaNotifier.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		m_consoleViewControl = new GuiConsole(this, parent);

		new Thread() {
	    	public void run() {
	    		m_consoleViewControl.execute();
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

    public void runCommand(String command) {
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
