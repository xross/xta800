package com.xmos.xta.ui.views.info.sections;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;

abstract class SectionsViewer {
	private TableViewer m_viewer;
	private List<Long> m_selectedPcs;

	abstract SectionsViewerContentProvider createContentProvider();
	abstract SectionsViewerLabelProvider createLabelProvider();
	
	SectionsViewer(Display display, Composite parent, IWorkbenchPartSite site, TableViewer viewer) {
		m_viewer = viewer;
		m_selectedPcs = new ArrayList<Long>();

		SectionsViewerContentProvider contentProvider = createContentProvider();
		final SectionsViewerLabelProvider labelProvider = createLabelProvider();

		final SectionSorter sectionSorter = new SectionSorter(labelProvider);
		m_viewer.setSorter(sectionSorter);

		for (int i = 0; i < labelProvider.getColumnNames().length; ++i) {
			TableColumn tableColumn = new TableColumn(m_viewer.getTable(), labelProvider.getColumnAligmnments()[i]);
			tableColumn.setText(labelProvider.getColumnNames()[i]);
			tableColumn.setWidth(labelProvider.getColumnWidths()[i]);
		}
		m_viewer.setContentProvider(contentProvider);
		m_viewer.setLabelProvider(labelProvider);

		for (final TableColumn col : m_viewer.getTable().getColumns()) {
			col.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					sectionSorter.setColoumn(col.getText());
					m_viewer.refresh();
				}
			});
		}
		
		m_viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (m_selectedPcs.size() == 1) {					
					Reference ref = ReferenceFactory.createReference("0x" + 
							Long.toHexString(m_selectedPcs.get(0)), ReferenceClass.LABEL_REF);
					
					Engine engine = GuiEngine.getEngine();
					ref.resolve(engine, engine.getNodeId(), engine.getCoreId());
					GuiEngine.setCodePoint(ref);
					GuiEngine.broadcast(Broadcaster.BROADCAST_SHOW_CODE_POINT, "");
				}
			}
		});
		
		m_viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();

				m_selectedPcs.clear();
				for (Object object : ((IStructuredSelection)selection).toList()) {
					m_selectedPcs.add(getPc(object));
				}
			}
		});
	}
	
	protected abstract long getPc(Object object);
	
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
