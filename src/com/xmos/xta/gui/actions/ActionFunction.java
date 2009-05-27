package com.xmos.xta.gui.actions;

import java.util.ArrayList;
import java.util.Collections;
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

import com.xmos.xeobject.Function;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.GuiNotifier;
import com.xmos.xta.gui.icons.Icons;

public class ActionFunction extends Action implements IPropertyChangeListener, IMenuCreator {

	private static final String DEFAULT_TOOL_TIP = "Select function to analyse";

	private List<Function> m_functions;
	private SelectionListener m_listListener;
	private String m_functionName;

	public ActionFunction() {
		GuiEngine.addBroadcastListener(this);
		
		setImageDescriptor(Icons.getIconDescriptor("function"));
		setToolTipText(DEFAULT_TOOL_TIP);
		setEnabled(false);
		setMenuCreator(this);

		m_functions = new ArrayList<Function>();

		m_listListener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				assert(false);
			}

			public void widgetSelected(SelectionEvent e) {
				String selectedFunction = ((MenuItem)e.getSource()).getText();
				GuiEngine.runFunction(selectedFunction);
			}
		};
	}

	public void dispose() {
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE) || 
			event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {			
	        updateState();
	        setEnabled(true);
	        
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE)) {
	        updateState();
	        
		} else if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
			if (GuiEngine.getRunMode() == GuiNotifier.RUN_MODE_FUNCTION) {
				setToolTipText("Currently analysed: " + m_functionName);
			}
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_FUNCTION_NAME)) {
			m_functionName = GuiEngine.getFunctionName();
		}
	}

	public Menu getMenu(Menu parent) {
		assert(false);
		return null;
	}

	public Menu getMenu(Control parent) {
		Menu menu = new Menu(parent);
		
        for (final Function function : m_functions) {
    		MenuItem item = new MenuItem(menu, SWT.NONE);
    		item.setText(function.getText());
    		item.addSelectionListener(m_listListener);
        }
		
		return menu;
	}

	private void updateState() {
		m_functions.clear();
		m_functionName = "";
		
    	XeObject xeObject = GuiEngine.getEngine().getXeObject();
    	List<Function> functions = xeObject.getFunctions();
    	Collections.sort(functions);
    	m_functions.addAll(functions);
    	
    	setToolTipText(DEFAULT_TOOL_TIP);
	}
}
