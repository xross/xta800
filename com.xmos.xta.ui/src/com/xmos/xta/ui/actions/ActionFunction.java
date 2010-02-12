package com.xmos.xta.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;

import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.dialogs.FilteredFunctions;

public class ActionFunction extends ActionDelegate implements IPropertyChangeListener, 
                                                              IWorkbenchWindowPulldownDelegate {
	private static int s_numInstances = 0;
	
	private List<String> m_functions;
	private SelectionListener m_listListener;
	private IAction m_action;
	private int m_instanceId;

	public ActionFunction() {
		GuiEngine.addBroadcastListener(this);
		
		m_functions = new ArrayList<String>();
		m_listListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectedFunction = ((MenuItem)e.getSource()).getText();
				GuiEngine.runFunction(selectedFunction);
			}
		};
		m_instanceId = s_numInstances;
		++s_numInstances;
	}
	
	public void init(IAction action) {
	    m_action = action;
	    
	    // Note: This is a really nasty hack to get around the fact that the eclipse
	    // platform seems to create another instance of this at exit.
	    // This needs further investigation.
	    if (m_instanceId == 0) {
	        m_action.setEnabled(false);
	    }
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		new FilteredFunctions(PlatformUI.getWorkbench().getDisplay().getActiveShell()); 
	}

	public void dispose() {
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE) || 
			event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {			
	        updateState();
	        m_action.setEnabled(true);
	        
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE)) {
	        updateState();

		}
	}

	public Menu getMenu(Control parent) {
		Menu menu = new Menu(parent);
		
        for (final String function : m_functions) {
    		MenuItem item = new MenuItem(menu, SWT.NONE);
    		item.setText(function);
    		item.addSelectionListener(m_listListener);
        }		
		return menu;
	}

	private void updateState() {
    	m_functions = new LinkedList<String>(GuiEngine.getEngine().getNonSystemFunctionNames());
    	Collections.sort(m_functions);
    	m_action.setToolTipText("Select function to analyse");
	}
}
