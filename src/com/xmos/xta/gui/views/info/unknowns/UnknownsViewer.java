package com.xmos.xta.gui.views.info.unknowns;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
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
import com.xmos.xta.gui.dialogs.InstructionTime;
import com.xmos.xta.gui.dialogs.Loop;
import com.xmos.xta.gui.views.info.InfoSorter;

public class UnknownsViewer {
	private Display m_display;
	private TableViewer m_unknownsViewer;
	private List<Node> m_selectedNodes;
	private Action m_setInstructionTimeAction;
	private Action m_setLoopIterationsAction;

	public UnknownsViewer(Display display, Composite parent, IWorkbenchPartSite site, TabFolder folders) {
		m_display = display;
		m_selectedNodes = new ArrayList<Node>();

		TabItem tab = new TabItem(folders, SWT.NONE);
		tab.setText("Unknowns");
		Composite composite = new Composite(folders, SWT.NONE);
		composite.setLayout(new FillLayout());
		m_unknownsViewer = new TableViewer(composite);
		tab.setControl(composite);
		
		final InfoSorter unknownSorter = new InfoSorter();
		m_unknownsViewer.setSorter(unknownSorter);

		Table table = m_unknownsViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		UnknownsContentProvider contentProvider = new UnknownsContentProvider();
		UnknownsLabelProvider labelProvider = new UnknownsLabelProvider();

		for (int i = 0; i < labelProvider.getColumnNames().length; ++i) {
			TableColumn tableColumn = new TableColumn(table, labelProvider
					.getColumnAligmnments()[i]);
			tableColumn.setText(labelProvider.getColumnNames()[i]);
			tableColumn.setWidth(labelProvider.getColumnWidths()[i]);
		}
		m_unknownsViewer.setContentProvider(contentProvider);
		m_unknownsViewer.setLabelProvider(labelProvider);
		for (final TableColumn col : m_unknownsViewer.getTable().getColumns()) {
			col.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					int colIndex = 0;
					if (col.getText().equals(UnknownsLabelProvider.PC)) {
						colIndex = 1;
					} else if (col.getText().equals(UnknownsLabelProvider.REASON)) {
						colIndex = 2;
					}
					
					unknownSorter.setColoumn(colIndex);
					m_unknownsViewer.refresh();
				}
			});
		}

		setupMouseListeners(site, parent);
	}

	public void clear() {
		m_unknownsViewer.setInput(null);
	}

	public void update() {
		Engine engine = GuiEngine.getEngine();
		m_unknownsViewer.setInput(engine.getSelectedRoute());
		updateHighlighting(GuiEngine.getSelectedNodes());
	}

	public void updateHighlighting(List<Node> selectedNodes) {
		for (TableItem item : m_unknownsViewer.getTable().getItems()) {
			Node node = (Node)item.getData();					
			if (selectedNodes.contains(node)) {
				item.setBackground(m_display.getSystemColor(SWT.COLOR_YELLOW));
			} else {
				item.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
			}
		}
	}

 	private void setupMouseListeners(IWorkbenchPartSite site, final Composite parent) {
		m_unknownsViewer.addDoubleClickListener(new IDoubleClickListener() {
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
		
		m_unknownsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();

				m_selectedNodes.clear();
				
				ArrayList<Reference> m_selectedTimeRefs = new ArrayList<Reference>();
				ArrayList<Reference> m_selectedLoopRefs = new ArrayList<Reference>();
				
				for (Object object : ((IStructuredSelection)selection).toList()) {
					Node node = (Node)object;
					
					Reference ref = ReferenceFactory.createReference(
								"0x" + Integer.toHexString(node.getStartPc()), ReferenceClass.ENDPOINT_REF);
					
					Engine engine = GuiEngine.getEngine();
					ref.resolve(engine, engine.getNodeId(), engine.getCoreId());
					
					switch (node.getAttributes().getUnknownType()) {
					case INSTRUCTION_TIME:
						m_selectedTimeRefs.add(ref);
						break;
						
					case LOOP_ITERATIONS:
						m_selectedLoopRefs.add(ref);
						break;
					}
				}
				
				m_setLoopIterationsAction.setEnabled(false);
				m_setInstructionTimeAction.setEnabled(false);
		/* TODO		
				if ((m_selectedTimeRefs.size() == 0) && (m_selectedLoopRefs.size() == 1)) {
					m_selectedCodeRefs.addAll(m_selectedLoopRefs);
					m_setLoopIterationsAction.setEnabled(true);
					
				} else if ((m_selectedTimeRefs.size() > 0) && (m_selectedLoopRefs.size() == 0)) {
					m_selectedCodeRefs.addAll(m_selectedTimeRefs);
					m_setInstructionTimeAction.setEnabled(true);
				} 	*/			
			}			
		});
		
		// Create the context menu
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(false);
		Menu menu = menuMgr.createContextMenu(m_unknownsViewer.getControl());
		m_unknownsViewer.getControl().setMenu(menu);
		site.registerContextMenu(menuMgr, m_unknownsViewer);
		
		// Create the action
		m_setInstructionTimeAction = new Action() { 
			public void run() {
				Route route = GuiEngine.getEngine().getSelectedRoute();
     			new InstructionTime(parent.getShell(), m_selectedNodes);
			}
		}; 
		m_setInstructionTimeAction.setText("Set instruction time");
		menuMgr.add(m_setInstructionTimeAction);
		m_setInstructionTimeAction.setEnabled(false);

		m_setLoopIterationsAction = new Action() { 
			public void run() {
				ISelection selection = m_unknownsViewer.getSelection();
		        if (selection instanceof IStructuredSelection) {	            
		            final Object object = ((IStructuredSelection)selection).getFirstElement();
		            if (object instanceof Node) {				
     			        new Loop(parent.getShell(), (Node)object);
		            }
		        }
			}
		};
		m_setLoopIterationsAction.setText("Set iterations");
		menuMgr.add(m_setLoopIterationsAction);
		m_setLoopIterationsAction.setEnabled(false);

		// required, for extensions
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	} 	
}
