package com.xmos.xta.gui.views.info.log;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.gui.GuiEngine;

public class LogViewer {
	private TableViewer m_tableViewer;

	public LogViewer(Display display, Composite parent, IWorkbenchPartSite site, TableViewer tableViewer) {
		m_tableViewer = tableViewer;

		LogViewerContentProvider contentProvider = new LogViewerContentProvider();
		LogViewerLabelProvider labelProvider = new LogViewerLabelProvider();

		for (int i = 0; i < labelProvider.getColumnNames().length; ++i) {
			TableColumn tableColumn = new TableColumn(m_tableViewer.getTable(), labelProvider.getColumnAligmnments()[i]);
			tableColumn.setText(labelProvider.getColumnNames()[i]);
			tableColumn.setWidth(labelProvider.getColumnWidths()[i]);
		}
	    m_tableViewer.setContentProvider(contentProvider);				
	    m_tableViewer.setLabelProvider(labelProvider);	
	}
	
	public void update() {
		m_tableViewer.setInput(GuiEngine.getLog());
	}

	public void clear() {
		GuiEngine.getLog().clear();
		m_tableViewer.refresh();
	}
}
