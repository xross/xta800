package com.xmos.xta.ui.actions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import org.eclipse.ui.actions.ActionDelegate;

import com.xmos.libs.xnloader.XnLoader;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.ui.GuiEngine;

public class ActionNode extends ActionDelegate implements IPropertyChangeListener, 
                                                  IWorkbenchWindowPulldownDelegate {
	private static int s_numInstances = 0;

    private Map<String, Integer> m_nodeNumbers;
    private Map<Integer, String> m_nodeNames;
	private SelectionListener m_listListener;
	private int m_currentNodeNumber;
	private IAction m_action;
	private int m_instanceId;

	public ActionNode() {
		GuiEngine.addBroadcastListener(this);

		m_nodeNumbers = new TreeMap<String, Integer>();
		m_nodeNames = new TreeMap<Integer, String>();
		m_listListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selected = ((MenuItem)e.getSource()).getText();

				try {
					if (!m_nodeNumbers.containsKey(selected)) {
						return;
					}

					int nodeNumber = m_nodeNumbers.get(selected); 
		        	if (nodeNumber != m_currentNodeNumber) {
		        		GuiEngine.getEngine().setNodeNumber(GuiEngine.s_guiSrc, nodeNumber, true);
		        		updateCurrentNodeNumber(nodeNumber);
		        	}
		        } catch (NumberFormatException exception) { }
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
		
	}
			
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) { 
	        updateState();
    		updateCurrentNodeNumber(0);
    		
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
	        updateState();

		} else if (event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE) ||
				   event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE)) {
			updateState();	       
		}
	}

	public Menu getMenu(Menu parent) {
		assert(false);
		return null;
	}
	
	public Menu getMenu(Control parent) {
		Menu menu = new Menu(parent);
		
		List<Integer> nodeNumbers = new LinkedList<Integer>(m_nodeNames.keySet());
		Collections.sort(nodeNumbers);
        for (final Integer nodeNumber : nodeNumbers) {
        	MenuItem item = new MenuItem(menu, SWT.NONE);
    		item.setText(m_nodeNames.get(nodeNumber));
    		item.addSelectionListener(m_listListener);
        }
		
		return menu;
	}

	private void updateState() {
		m_nodeNumbers.clear();
		m_nodeNames.clear();
		
		XnLoader xnLoader = GuiEngine.getEngine().getXeLoader().getXnLoader();
		for (Integer nodeNumber : GuiEngine.getEngine().getXeLoader().getNodeNumbers()) {
			String nodeName = xnLoader.getNodeName(nodeNumber);
			if (nodeName.length() == 0) {
				nodeName = String.format("%d", nodeNumber);
			}

			m_nodeNumbers.put(nodeName, nodeNumber);
			m_nodeNames.put(nodeNumber, nodeName);
		}
		updateCurrentNodeNumber(GuiEngine.getEngine().getNodeNumber());		
		
		if (GuiEngine.getEngine().getXeLoader().getNodeNumbers().size() < 2) {
			m_action.setEnabled(false);
		} else {
			m_action.setEnabled(true);
		}
	}

	private void updateCurrentNodeNumber(int nodeNumber) {
		m_currentNodeNumber = nodeNumber;
		String nodeName = m_nodeNames.get(nodeNumber);
		m_action.setText("Node " + nodeName);		
		m_action.setToolTipText("Node " + nodeName);		
	}	
}
