package com.xmos.xta.gui.views.info.sections;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.gui.GuiEngine;

abstract class SectionsViewer {
	private TableViewer m_viewer;

	abstract SectionsViewerContentProvider createContentProvider();
	abstract SectionsViewerLabelProvider createLabelProvider();
	
	SectionsViewer(Display display, Composite parent, IWorkbenchPartSite site, TableViewer viewer) {
		m_viewer = viewer;

		SectionsViewerContentProvider contentProvider = createContentProvider();
		SectionsViewerLabelProvider labelProvider = createLabelProvider();

		for (int i = 0; i < labelProvider.getColumnNames().length; ++i) {
			TableColumn tableColumn = new TableColumn(m_viewer.getTable(), labelProvider.getColumnAligmnments()[i]);
			tableColumn.setText(labelProvider.getColumnNames()[i]);
			tableColumn.setWidth(labelProvider.getColumnWidths()[i]);
		}
		m_viewer.setContentProvider(contentProvider);
		m_viewer.setLabelProvider(labelProvider);
	}

	public void update() {
		Engine engine = GuiEngine.getEngine();
		if (engine.getExecutable() != null) {
		    m_viewer.setInput(GuiEngine.getEngine());
		} else {
			m_viewer.setInput(null);
		}
	}
	
	void clear() {
		m_viewer.setInput(null);
	}
	
	TableViewer getViewer() {
		return m_viewer;
	}
}
