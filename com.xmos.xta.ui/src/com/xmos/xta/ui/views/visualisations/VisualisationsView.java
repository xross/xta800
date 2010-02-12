package com.xmos.xta.ui.views.visualisations;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.views.ControlMonitor;
import com.xmos.xta.ui.views.ViewControls;
import com.xmos.xta.ui.views.visualisations.function.FunctionView;
import com.xmos.xta.ui.views.visualisations.handlers.ExportJpgHandler;
import com.xmos.xta.ui.views.visualisations.handlers.ZoomFitHandler;
import com.xmos.xta.ui.views.visualisations.handlers.ZoomInHandler;
import com.xmos.xta.ui.views.visualisations.handlers.ZoomOutHandler;
import com.xmos.xta.ui.views.visualisations.handlers.ZoomResetHandler;
import com.xmos.xta.ui.views.visualisations.structure.StructureView;
import com.xmos.xta.ui.views.visualisations.timedistribution.TimeDistributionView;

public class VisualisationsView extends ViewPart implements ViewControls {
	public static final String ID = "com.xmos.xta.ui.views.visualisations.Visualisations";
	
	private TabFolder m_folders;
	private Map<TabItem, ControlMonitor> m_tabMap;

	private static Set<String> m_enabledCommands;
	public static Set<String> getEnabledCommands() {
		if (m_enabledCommands == null) {
			m_enabledCommands = new TreeSet<String>();
		}
		return m_enabledCommands;
	}

	public VisualisationsView() {
		m_tabMap = new HashMap<TabItem, ControlMonitor>();
	}

	public void dispose() {
		super.dispose();
		for (ControlMonitor monitor : m_tabMap.values()) {
			monitor.dispose();
		}
		m_tabMap.clear();
	}
	
	public void createPartControl(Composite parent) {
		m_folders = new TabFolder(parent, SWT.BORDER);
		
		createTab("Structure", new controlMonitorBuilder() {
			public ControlMonitor create(Composite composite) {
				return new StructureView(composite);
			}			
		});
		createTab("Function", new controlMonitorBuilder() {
			public ControlMonitor create(Composite composite) {
				return new FunctionView(composite);
			}			
		});
		createTab("Time Distribution", new controlMonitorBuilder() {
			public ControlMonitor create(Composite composite) {
				return new TimeDistributionView(composite);
			}			
		});

		m_folders.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				updateControls();
			}			
		});
		m_folders.setSelection(0);

		// The selection event doesn't fire if the selection doesn't change - force update
		updateControls();
	}

	protected void updateControls() {
	    ControlMonitor controlMonitor = getSelectedTab();
	    controlMonitor.updateButton(ExportJpgHandler.COMMAND_ID);
	    controlMonitor.updateButton(ZoomFitHandler.COMMAND_ID);
	    controlMonitor.updateButton(ZoomInHandler.COMMAND_ID);
	    controlMonitor.updateButton(ZoomOutHandler.COMMAND_ID);
	    controlMonitor.updateButton(ZoomResetHandler.COMMAND_ID);
		GuiEngine.broadcast(Broadcaster.BROADCAST_BUTTON_STATE_CHANGED, "");
	}

	public void setFocus() {
	}
	
	public void zoomIn() {
		getSelectedTab().zoomIn();
	}

	public void zoomOut() {
		getSelectedTab().zoomOut();
	}

	public void zoomReset() {
		getSelectedTab().zoomReset();
	}

	public void zoomFit() {
		getSelectedTab().zoomFit();
	}
	
	public void exportJpg(){
		getSelectedTab().exportJpg();
	}
	
	public void setPie(boolean enable) {
	}

	public void setSummary(boolean enable) {
	}

	private void createTab(String name, controlMonitorBuilder builder) {
		TabItem tab = new TabItem(m_folders, SWT.NONE);
		tab.setText(name);
		Composite composite = new Composite(m_folders, SWT.NONE);
		composite.setLayout(new FillLayout());		
		tab.setControl(composite);	
		ControlMonitor controlMonitor = builder.create(composite);
		m_tabMap.put(tab, controlMonitor);
	}

	private ControlMonitor getSelectedTab() {
		TabItem[] selected = m_folders.getSelection();
		return m_tabMap.get(selected[0]);
	}

	static private interface controlMonitorBuilder {
		ControlMonitor create(Composite composite);
	}
}
