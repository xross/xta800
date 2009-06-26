package com.xmos.xta.gui.views.info.allsrclabels;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.gui.GuiEngine;

public class AllSrcLabelsViewer {
	private TableViewer m_viewer;

	public AllSrcLabelsViewer(Display display, Composite parent, IWorkbenchPartSite site, TableViewer viewer) {
		m_viewer = viewer;

		AllSrcLabelsViewerContentProvider contentProvider = new AllSrcLabelsViewerContentProvider();
		AllSrcLabelsViewerLabelProvider labelProvider = new AllSrcLabelsViewerLabelProvider();

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
