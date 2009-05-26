package com.xmos.xta.rcp.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.icons.Icons;

public class ActionNode extends Action implements IPropertyChangeListener, IMenuCreator {
	
	private static final String DEFAULT_TOOL_TIP = "Node 0";

    private List<String> m_nodeIds;
	private SelectionListener m_listListener;
	private int m_currentNodeId;

	public ActionNode() {
		super("node", Action.AS_DROP_DOWN_MENU);

		XtaNotifier.getInstance().addPropertyChangeListener(this);

		setImageDescriptor(Icons.getIconDescriptor("node"));
		setToolTipText(DEFAULT_TOOL_TIP);
		setEnabled(false);
		setMenuCreator(this);

		m_nodeIds = new ArrayList<String>();

		m_listListener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				assert(false);
			}

			public void widgetSelected(SelectionEvent e) {
				String selected = ((MenuItem)e.getSource()).getText();

				try {
					XtaNotifier.getInstance().notify(Notifier.NOTIFY_SET_GUI_COMMAND, "");					
		        	int nodeId = Integer.parseInt(selected); 
		        	if (nodeId != m_currentNodeId) {
		        		XtaEngine.getEngine().setNodeId(nodeId);
		        		m_currentNodeId = nodeId;
		        	}
		        } catch (NumberFormatException exception) {
		        }

				setToolTipText("Node " + m_currentNodeId);
			}
		};
	}

	public void dispose() {
		// Do nothing
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE) || 
			event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {			
	        updateState();
	        setEnabled(true);
	        
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
		for (Integer nodeId : XtaEngine.getEngine().getXeLoader().getNodeIds()) {
			m_nodeIds.add(String.format("%d", nodeId));
		}
	}
}
