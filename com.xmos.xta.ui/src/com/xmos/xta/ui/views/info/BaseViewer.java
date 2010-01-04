package com.xmos.xta.ui.views.info;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableColumn;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;

public abstract class BaseViewer implements Viewer {
	private TableViewer m_viewer;
	private List<Long> m_selectedPcs;

	protected abstract BaseViewerContentProvider createContentProvider();
	protected abstract BaseViewerLabelProvider createLabelProvider();
	protected abstract Set<Long> getPcs(Object object);
	
	public BaseViewer(TableViewer viewer) {
		m_viewer = viewer;
		m_selectedPcs = new ArrayList<Long>();

		BaseViewerContentProvider contentProvider = createContentProvider();
		final BaseViewerLabelProvider labelProvider = createLabelProvider();

		final BaseViewerSorter sectionSorter = new BaseViewerSorter(labelProvider);
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
					ref.resolve(engine);
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
					m_selectedPcs.addAll(getPcs(object));
				}
			}
		});		
		update();
		
		getViewer().getTable().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.DEL) {
					removeSelection();
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});
	}
		
	public void update() {
		Engine engine = GuiEngine.getEngine();
		if (engine.getExecutable() != null) {
		    m_viewer.setInput(GuiEngine.getEngine());
		} else {
			m_viewer.setInput(null);
		}
	}
	
	public void updateHighlighting(List<Node> selectedNodes) {		
	}
	
	public Control getControl() {
		return m_viewer.getControl();
	}

	protected TableViewer getViewer() {
		return m_viewer;
	}	

	protected void removeSelection() {
		// Do nothing
	}	
}
