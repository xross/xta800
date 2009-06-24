package com.xmos.xta.gui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.actions.ActionDelegate;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.gui.GuiEngine;

public class ActionNode extends ActionDelegate implements IPropertyChangeListener, 
                                                  IWorkbenchWindowPulldownDelegate {
    private List<String> m_nodeIds;
	private SelectionListener m_listListener;
	private int m_currentNodeId;
	private IAction m_action;

	public ActionNode() {
		GuiEngine.addBroadcastListener(this);

		m_nodeIds = new ArrayList<String>();
		m_listListener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				assert(false);
			}

			public void widgetSelected(SelectionEvent e) {
				String selected = ((MenuItem)e.getSource()).getText();

				try {
		        	int nodeId = Integer.parseInt(selected); 
		        	if (nodeId != m_currentNodeId) {
		        		GuiEngine.getEngine().setNodeId(nodeId);
		        		m_currentNodeId = nodeId;
		        	}
		        } catch (NumberFormatException exception) {
		        }

		        m_action.setToolTipText("Node " + m_currentNodeId);
			}
		};
	}

	public void init(IAction action) {
		m_action = action;
		m_action.setEnabled(false);
	}
	
	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
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

	public Menu getMenu(Menu parent) {
		assert(false);
		return null;
	}

	public Menu getMenu(Control parent) {
		Menu menu = new Menu(parent);
		
        for (final String nodeId : m_nodeIds) {
    		MenuItem item = new MenuItem(menu, SWT.NONE);
    		item.setText(nodeId);
    		item.addSelectionListener(m_listListener);
        }
		
		return menu;
	}

	private void updateState() {
		m_nodeIds.clear();
		for (Integer nodeId : GuiEngine.getEngine().getXeLoader().getNodeIds()) {
			m_nodeIds.add(String.format("%d", nodeId));
		}
	}
}
