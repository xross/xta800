package com.xmos.xta.ui.views.info.log;

import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableColumn;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.views.info.Viewer;

public class LogViewer implements Viewer {
	private TableViewer m_tableViewer;

	public LogViewer(TableViewer tableViewer) {
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
	    update();
	}

	public void update() {
		m_tableViewer.setInput(GuiEngine.getLog());
	}
	
	public void updateHighlighting(List<Node> selectedNodes) {		
	}
	
	public Control getControl() {
		return m_tableViewer.getControl();
	}
}
