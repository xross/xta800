package com.xmos.xta.ui.actions;

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

public class ActionCore extends ActionDelegate implements IPropertyChangeListener, 
                                                          IWorkbenchWindowPulldownDelegate {
	private static int s_numInstances = 0;
	
    private Map<String, Integer> m_coreNumbers;
    private Map<Integer, String> m_coreNames;
	private SelectionListener m_listListener;
	private int m_currentCoreNumber;
	private IAction m_action;
	private int m_instanceId;

	public ActionCore() {
		GuiEngine.addBroadcastListener(this);

		m_coreNumbers = new TreeMap<String, Integer>();
		m_coreNames = new TreeMap<Integer, String>();
		
		m_listListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selected = ((MenuItem)e.getSource()).getText();

				try {
					if (!m_coreNumbers.containsKey(selected)) {
						return;
					}
					
		        	int coreNumber = m_coreNumbers.get(selected); 
		        	if (coreNumber != m_currentCoreNumber) {
		        		GuiEngine.getEngine().setCoreNumber(GuiEngine.s_guiSrc, coreNumber, true);
		        		updateCurrentCoreNumber(coreNumber);
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
    		updateCurrentCoreNumber(0);

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
		
        for (final String coreNumber : m_coreNumbers.keySet()) {
        	MenuItem item = new MenuItem(menu, SWT.NONE);
    		item.setText(coreNumber);
    		item.addSelectionListener(m_listListener);
        }
		
		return menu;
	}

	private void updateState() {
		m_coreNumbers.clear();
		m_coreNames.clear();

		int selectedNodeNumber = GuiEngine.getEngine().getNodeNumber();

		XnLoader xnLoader = GuiEngine.getEngine().getXeLoader().getXnLoader();
		List<Integer> coreNumbers = xnLoader.getCoreNumbers(selectedNodeNumber);
		if (coreNumbers.size() < 2) {
			m_action.setText("Core 0");
			m_action.setEnabled(false);

		} else {
			m_action.setEnabled(true);

			for (Integer coreNumber : coreNumbers) {
				String coreName = xnLoader.getCoreName(selectedNodeNumber, coreNumber);
				if (coreName.length() == 0) {
					coreName = String.format("Core %d", coreNumber);
				}
				m_coreNumbers.put(coreName, coreNumber);
				m_coreNames.put(coreNumber, coreName);
			}
			updateCurrentCoreNumber(GuiEngine.getEngine().getCoreNumber());
		}
	}
	
	private void updateCurrentCoreNumber(int coreNumber) {
		m_currentCoreNumber = coreNumber;
		String coreName = m_coreNames.get(coreNumber);
		m_action.setText(coreName);
		m_action.setToolTipText(coreName);		
	}
}
