package com.xmos.xta.gui.views.info.knowns;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.gui.Broadcaster;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.views.info.InfoSorter;

public class KnownsViewer {
	private TableViewer m_knownsViewer;
	private List<Node> m_selectedNodes;
	private Action m_clearInstructionTimesAction;

	public KnownsViewer(Composite parent, IWorkbenchPartSite site, TabFolder folders) {
		m_selectedNodes = new ArrayList<Node>();

		TabItem tab = new TabItem(folders, SWT.NONE);
		tab.setText("Set values");
		Composite composite = new Composite(folders, SWT.NONE);
		composite.setLayout(new FillLayout());
		m_knownsViewer = new TableViewer(composite);
		tab.setControl(composite);

		final InfoSorter knownSorter = new InfoSorter();
		m_knownsViewer.setSorter(knownSorter);

		Table table = m_knownsViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		KnownsContentProvider contentProvider = new KnownsContentProvider();
		KnownsLabelProvider labelProvider = new KnownsLabelProvider();

		for (int i = 0; i < labelProvider.getColumnNames().length; ++i) {
			TableColumn tableColumn = new TableColumn(table, labelProvider
					.getColumnAligmnments()[i]);
			tableColumn.setText(labelProvider.getColumnNames()[i]);
			tableColumn.setWidth(labelProvider.getColumnWidths()[i]);
		}
		m_knownsViewer.setContentProvider(contentProvider);
		m_knownsViewer.setLabelProvider(labelProvider);
		for (final TableColumn col : m_knownsViewer.getTable().getColumns()) {
			col.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					int colIndex = 0;
					if (col.getText().equals(KnownsLabelProvider.PC)) {
						colIndex = 0;
					} else if (col.getText().equals(KnownsLabelProvider.DESC)) {
						colIndex = 1;
					}
					
					knownSorter.setColoumn(colIndex);
					m_knownsViewer.refresh();
				}
			});
		}
	    setupMouseListeners(site, parent);
	}

	public void clear() {
		m_knownsViewer.setInput(null);
	}

	public void update() {
		Route route = GuiEngine.getEngine().getSelectedRoute();
		m_knownsViewer.setInput(route);
	}

	private void setupMouseListeners(IWorkbenchPartSite site, final Composite parent) {
		m_knownsViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (m_selectedNodes.size() == 1) {					
					Reference ref = ReferenceFactory.createReference("0x" + 
							Integer.toHexString(m_selectedNodes.get(0).getStartPc()), ReferenceClass.LABEL_REF);
					
					Engine engine = GuiEngine.getEngine();
					ref.resolve(engine, engine.getNodeId(), engine.getCoreId());
					
					GuiEngine.setCodePoint(ref);
					GuiEngine.broadcast(Broadcaster.BROADCAST_SHOW_CODE_POINT, "");
				}
			}
		});
		
		m_knownsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();

				boolean enabled = ((IStructuredSelection) selection).size() > 0;
				m_clearInstructionTimesAction.setEnabled(enabled);
				m_selectedNodes.clear();

				int numInstructionTimeNodes = 0;
				int numLoopIterationsNodes = 0;				
				for (Object object : ((IStructuredSelection) selection).toList()) {
					Node node = (Node)object;
					
					switch (node.getAttributes().getUnknownType()) {
					case INSTRUCTION_TIME:
						++numInstructionTimeNodes;
						break;
						
					case LOOP_ITERATIONS:
						++numLoopIterationsNodes;
						m_clearInstructionTimesAction.setEnabled(false);
						break;
					}
					m_selectedNodes.add(node);
				}
				
				m_clearInstructionTimesAction.setEnabled(false);
				if ((numInstructionTimeNodes > 0) && (numLoopIterationsNodes == 0)) {
					m_clearInstructionTimesAction.setEnabled(true);					
				} 			
			}			
		});
		
		// Create the context menu
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(false);
		Menu menu = menuMgr.createContextMenu(m_knownsViewer.getControl());
		m_knownsViewer.getControl().setMenu(menu);
		site.registerContextMenu(menuMgr, m_knownsViewer);
		
		// Create the action
		m_clearInstructionTimesAction = new Action() { 
			public void run() {
				for (Node node : m_selectedNodes) {
					GuiEngine.getEngine().unsetInstructionTimeTaken(node);
					m_knownsViewer.refresh();
				}
			}
		}; 
		m_clearInstructionTimesAction.setText("Clear instruction time");
		menuMgr.add(m_clearInstructionTimesAction);
		m_clearInstructionTimesAction.setEnabled(false);

		// required, for extensions
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
}
