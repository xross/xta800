package com.xmos.xta.ui.views.info.unknowns;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Unknown;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.views.info.Viewer;

abstract class InfoViewer implements Viewer {
	private Composite m_parent;
	private Display m_display;
	private TableViewer m_viewer;
	private List<Node> m_selectedNodes;

	abstract InfoViewerContentProvider createContentProvider();
	abstract InfoViewerLabelProvider createLabelProvider();
	abstract void createContextMenuActions(MenuManager menuMgr);	
	abstract void enableContextMenuActions(int numInstructionTimeNodes, int numLoopIterationsNodes);
	
	public Control getControl() {
		return m_viewer.getControl();
	}
	
	InfoViewer(TableViewer viewer, Composite parent, IWorkbenchPartSite site) {
		m_parent = parent;
		m_display = parent.getDisplay();
		m_selectedNodes = new ArrayList<Node>();
		m_viewer = viewer;

		final InfoSorter unknownSorter = new InfoSorter();
		m_viewer.setSorter(unknownSorter);

		InfoViewerContentProvider contentProvider = createContentProvider();;
		InfoViewerLabelProvider labelProvider = createLabelProvider();

		for (int i = 0; i < labelProvider.getColumnNames().length; ++i) {
			TableColumn tableColumn = new TableColumn(m_viewer.getTable(), labelProvider
					.getColumnAligmnments()[i]);
			tableColumn.setText(labelProvider.getColumnNames()[i]);
			tableColumn.setWidth(labelProvider.getColumnWidths()[i]);
		}
		m_viewer.setContentProvider(contentProvider);
		m_viewer.setLabelProvider(labelProvider);
		
		for (final TableColumn col : m_viewer.getTable().getColumns()) {
			col.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					int colIndex = 0;
					if (col.getText().equals(InfoViewerLabelProvider.PC)) {
						colIndex = 0;
					} else if (col.getText().equals(InfoViewerLabelProvider.DESC)) {
						colIndex = 1;
					} else if (col.getText().equals(InfoViewerLabelProvider.BACKTRACE)) {
						colIndex = 2;
					} else {
						assert(false);
					}
					
					unknownSorter.setColoumn(colIndex);
					m_viewer.refresh();
				}
			});
		}
		
		m_viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (m_selectedNodes.size() == 1) {					
					Reference ref = ReferenceFactory.createReference("0x" + 
							Long.toHexString(m_selectedNodes.get(0).getStartPc()), ReferenceClass.LABEL_REF);
					
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

				m_selectedNodes.clear();				
				int numInstructionTimeNodes = 0;
				int numLoopIterationsNodes = 0;
				
				for (Object object : ((IStructuredSelection)selection).toList()) {
					Unknown unknown = (Unknown)object;			
					
					switch (unknown.getType()) {
					case INSTRUCTION_TIME:
						++numInstructionTimeNodes;
						break;
						
					case LOOP_ITERATIONS:
						++numLoopIterationsNodes;
						break;
					}
					m_selectedNodes.add(unknown.getNode());
				}
				enableContextMenuActions(numInstructionTimeNodes, numLoopIterationsNodes);
			}			
		});		
		
		// Create the context menu
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(false);
		Menu menu = menuMgr.createContextMenu(getViewer().getControl());
		getViewer().getControl().setMenu(menu);
		site.registerContextMenu(menuMgr, getViewer());		
		createContextMenuActions(menuMgr);	
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

	Composite getParent() {
		return m_parent;
	}

	Display getDisplay() {
		return m_display;
	}

	TableViewer getViewer() {
		return m_viewer;
	}
	
	List<Node> getSelectedNodes() {
		return m_selectedNodes;
	}
		
	int getNumInstructionTimeNodesSelected(ISelection selection) {
		return 0;
	}

	int getNumLoopIterationsNodesSelected(ISelection selection) {
		return 0;
	}
}
