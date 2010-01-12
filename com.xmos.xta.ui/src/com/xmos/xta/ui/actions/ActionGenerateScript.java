package com.xmos.xta.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.actions.ActionDelegate;

import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.ui.GuiEngine;

public class ActionGenerateScript extends ActionDelegate implements IPropertyChangeListener, 
                                                          IWorkbenchWindowPulldownDelegate {
	private static int s_numInstances = 0;
	
	private IAction m_action;
	private int m_instanceId;

	public ActionGenerateScript() {
		GuiEngine.addBroadcastListener(this);

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
		GuiEngine.generateAndOpenScript();
	}
		
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) { 
	        updateState();
		}
	}
 
	private void updateState() {
		if (GuiEngine.getEngine().getXeLoader().isLoaded()) {
			m_action.setEnabled(true);
		} else {
			m_action.setEnabled(false);
		}
	}

	public Menu getMenu(Control parent) {
		return null;
	}
}
