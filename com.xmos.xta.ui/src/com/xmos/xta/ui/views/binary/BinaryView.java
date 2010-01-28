package com.xmos.xta.ui.views.binary;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.ui.views.ControlMonitor;
import com.xmos.xta.ui.views.ViewControls;
import com.xmos.xta.ui.views.binary.disassembly.DisassemblyView;
import com.xmos.xta.ui.views.binary.graphics.GraphicsView;

public class BinaryView extends ViewPart implements ViewControls {
	public static final String ID = "com.xmos.xta.ui.views.binary.Binary";
	
	private TabFolder m_folders;
	private Map<TabItem, ControlMonitor> m_tabMap;

	public void createPartControl(Composite parent) {
		m_folders = new TabFolder(parent, SWT.BORDER);
		m_tabMap = new HashMap<TabItem, ControlMonitor>();

		createTab("Disassembly", new controlBuilder() {
			public ControlMonitor create(Composite composite) {
				return new DisassemblyView(composite, getSite());
			}
		});
		createTab("Resources", new controlBuilder() {
			public ControlMonitor create(Composite composite) {
				return new GraphicsView(composite);
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
	    controlMonitor.updateExportJpgEnabled();
	    controlMonitor.updateZoomFitEnabled();
	    controlMonitor.updateZoomInEnabled();
	    controlMonitor.updateZoomOutEnabled();
	    controlMonitor.updateZoomResetEnabled();			    
	    controlMonitor.updatePieEnabled();
	    controlMonitor.updateSummaryEnabled();
	}

	public void dispose() {
		super.dispose();
		for (ControlMonitor monitor : m_tabMap.values()) {
			monitor.dispose();
		}
		m_tabMap.clear();
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

	private void createTab(String name, controlBuilder builder) {
		TabItem tab = new TabItem(m_folders, SWT.NONE);
		tab.setText(name);
		Composite composite = new Composite(m_folders, SWT.NONE);
		composite.setLayout(new FillLayout());		
		tab.setControl(composite);	
		ControlMonitor controlMonitor = builder.create(composite);
		m_tabMap.put(tab, controlMonitor);
	}

	static private interface controlBuilder {
		ControlMonitor create(Composite composite);
	}

	private ControlMonitor getSelectedTab() {
		TabItem[] selected = m_folders.getSelection();
		return m_tabMap.get(selected[0]);
	}
}
