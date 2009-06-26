package com.xmos.xta.gui.views.info.endpoints;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.gui.GuiEngine;

public class EndpointsViewer {
	private TableViewer m_viewer;

	public EndpointsViewer(Display display, Composite parent, IWorkbenchPartSite site, TableViewer viewer) {
		m_viewer = viewer;

		EndpointsViewerContentProvider contentProvider = new EndpointsViewerContentProvider();
		EndpointsViewerLabelProvider labelProvider = new EndpointsViewerLabelProvider();

		for (int i = 0; i < labelProvider.getColumnNames().length; ++i) {
			TableColumn tableColumn = new TableColumn(m_viewer.getTable(), labelProvider.getColumnAligmnments()[i]);
			tableColumn.setText(labelProvider.getColumnNames()[i]);
			tableColumn.setWidth(labelProvider.getColumnWidths()[i]);
		}
		m_viewer.setContentProvider(contentProvider);
		m_viewer.setLabelProvider(labelProvider);
	}

	public void update() {
		m_viewer.setInput(GuiEngine.getEngine());
	}
	
	void clear() {
		m_viewer.setInput(null);
	}	
}
