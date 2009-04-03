package com.xmos.xta.rcp.actions.control;

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

public class ActionCore extends Action implements IPropertyChangeListener, IMenuCreator {

	private static final String DEFAULT_TOOL_TIP = "Core 0";

    private List<String> m_coreIds;
	private SelectionListener m_listListener;
	private int m_currentCoreId;

	public ActionCore() {
		super("core", Action.AS_DROP_DOWN_MENU);

		XtaNotifier.getInstance().addPropertyChangeListener(this);

		setImageDescriptor(Icons.getIconDescriptor("core"));
		setToolTipText(DEFAULT_TOOL_TIP);
		setEnabled(false);
		setMenuCreator(this);

		m_coreIds = new ArrayList<String>();

		m_listListener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				assert(false);
			}

			public void widgetSelected(SelectionEvent e) {
				String selected = ((MenuItem)e.getSource()).getText();

				try {
		        	int coreId = Integer.parseInt(selected); 
		        	if (coreId != m_currentCoreId) {
		        		XtaEngine.getEngine().setCoreId(coreId);
		        		m_currentCoreId = coreId;
						setToolTipText("Core " + m_currentCoreId);
		        	}
		        } catch (NumberFormatException exception) {
		        }
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
	        this.setEnabled(true);
	        
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
		
        for (final String coreId : m_coreIds) {
    		MenuItem item = new MenuItem(menu, SWT.NONE);
    		item.setText(coreId);
    		item.addSelectionListener(m_listListener);
        }
		
		return menu;
	}

	private void updateState() {
		m_coreIds.clear();

		int selectedNodeId = XtaEngine.getEngine().getNodeId();
		for (Integer coreId : XtaEngine.getEngine().getXeLoader().getCoreIds(selectedNodeId)) {
			m_coreIds.add(String.format("%d", coreId));
		}
	}
}
