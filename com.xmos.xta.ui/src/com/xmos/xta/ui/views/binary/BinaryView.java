package com.xmos.xta.ui.views.binary;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
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
import com.xmos.xta.ui.views.binary.disassembly.DisassemblyView;
import com.xmos.xta.ui.views.binary.graphics.GraphicsView;
import com.xmos.xta.ui.views.binary.handlers.ExportJpgHandler;
import com.xmos.xta.ui.views.binary.handlers.PieHandler;
import com.xmos.xta.ui.views.binary.handlers.SummaryHandler;
import com.xmos.xta.ui.views.binary.handlers.ZoomFitHandler;
import com.xmos.xta.ui.views.binary.handlers.ZoomInHandler;
import com.xmos.xta.ui.views.binary.handlers.ZoomOutHandler;
import com.xmos.xta.ui.views.binary.handlers.ZoomResetHandler;
import com.xmos.xta.ui.views.binary.tables.DataContentProvider;
import com.xmos.xta.ui.views.binary.tables.FunctionContentProvider;
import com.xmos.xta.ui.views.binary.tables.TableView;

public class BinaryView extends ViewPart implements ViewControls, IPropertyChangeListener {
	public static final String ID = "com.xmos.xta.ui.views.binary.Binary";
	
	private TabFolder m_folders;
	private Map<TabItem, ControlMonitor> m_tabMap;
	private int m_numTabs;
	private int m_disassemblyIndex;

	private static Set<String> m_enabledCommands;
	public static Set<String> getEnabledCommands() {
		if (m_enabledCommands == null) {
			m_enabledCommands = new TreeSet<String>();
		}
		return m_enabledCommands;
	}
	
	public BinaryView() {
		GuiEngine.addBroadcastListener(this);
		m_numTabs = 0;
	}
	
	public void createPartControl(Composite parent) {
		m_folders = new TabFolder(parent, SWT.BORDER);
		m_tabMap = new HashMap<TabItem, ControlMonitor>();

		m_disassemblyIndex = createTab("Disassembly", new controlBuilder() {
			public ControlMonitor create(Composite composite) {
				return new DisassemblyView(composite, getSite());
			}
		});
		createTab("Resources", new controlBuilder() {
			public ControlMonitor create(Composite composite) {
				return new GraphicsView(composite);
			}
		});

		createTab("Function Table", new controlBuilder() {
			public ControlMonitor create(Composite composite) {
				return new TableView(composite, new FunctionContentProvider(), true);
			}
		});

		createTab("Data Table", new controlBuilder() {
			public ControlMonitor create(Composite composite) {
				return new TableView(composite, new DataContentProvider(), false);
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
	    controlMonitor.updateButton(PieHandler.COMMAND_ID);
	    controlMonitor.updateButton(SummaryHandler.COMMAND_ID);
		GuiEngine.broadcast(Broadcaster.BROADCAST_BUTTON_STATE_CHANGED, "");
	}

	public void dispose() {
		super.dispose();
		for (ControlMonitor monitor : m_tabMap.values()) {
			monitor.dispose();
		}
		m_tabMap.clear();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Broadcaster.BROADCAST_SHOW_CODE_POINT)) {
			m_folders.setSelection(m_disassemblyIndex);
		} 
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

	public void setPie(boolean enable){
		getSelectedTab().setPie(enable);
	}

	public void setSummary(boolean enable){
		getSelectedTab().setSummary(enable);
	}

	private int createTab(String name, controlBuilder builder) {
		TabItem tab = new TabItem(m_folders, SWT.NONE);
		tab.setText(name);
		Composite composite = new Composite(m_folders, SWT.NONE);
		composite.setLayout(new FillLayout());		
		tab.setControl(composite);	
		ControlMonitor controlMonitor = builder.create(composite);
		m_tabMap.put(tab, controlMonitor);		
		int tabIndex = m_numTabs;
		++m_numTabs;
		return tabIndex;
	}

	static private interface controlBuilder {
		ControlMonitor create(Composite composite);
	}

	private ControlMonitor getSelectedTab() {
		TabItem[] selected = m_folders.getSelection();
		return m_tabMap.get(selected[0]);
	}
}
