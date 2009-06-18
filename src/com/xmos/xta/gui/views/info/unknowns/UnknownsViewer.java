package com.xmos.xta.gui.views.info.unknowns;

import java.util.ArrayList;
import java.util.HashSet;
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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
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
import com.xmos.xta.gui.dialogs.Iterations;

public class UnknownsViewer {
	private Display m_display;
	private TableViewer m_unknownsViewer;
	private List<Reference> m_selectedCodeRefs;
	private Action m_setInstructionTimeAction;
	private Action m_setLoopIterationsAction;

	public UnknownsViewer(Display display, Composite parent, IWorkbenchPartSite site, TabFolder folders) {
		m_display = display;
		m_selectedCodeRefs = new ArrayList<Reference>();

		TabItem tab = new TabItem(folders, SWT.NONE);
		tab.setText("Unknowns");
		Composite composite = new Composite(folders, SWT.NONE);
		composite.setLayout(new FillLayout());
		m_unknownsViewer = new TableViewer(composite);
		tab.setControl(composite);
		
		final UnknownSorter unknownSorter = new UnknownSorter();
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
		// Get blocks for all selected nodes
		HashSet<Integer> selectedBlocks = new HashSet<Integer>();
/* TODO
		for (Node node : selectedNodes) {
			node.getBlockIds(selectedBlocks);
		}
		for (TableItem item : m_unknownsViewer.getTable().getItems()) {
			Unknown unknown = (Unknown)item.getData();
			if (selectedBlocks.contains(unknown.getNodeId())) {
				item.setBackground(m_display.getSystemColor(SWT.COLOR_YELLOW));
			} else {
				item.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
			}
		}
*/		
	}

	class UnknownSorter extends ViewerSorter {
		int field = 0;
		int direction = 1;
		
		public int compare(Viewer viewer, Object e1, Object e2) {
/* TODO			
			Unknown p1 = (Unknown) e1;
			Unknown p2 = (Unknown) e2;
			switch(field){
			case 0:
				int res2 = p1.getPc() > p2.getPc() ? 1 : -1;
				return res2 * direction;
			case 1:
				int res3 = p1.getPc() > p2.getPc() ? 1 : -1;
				return res3 * direction;
			default:
				int resd = p1.getNodeId() > p2.getNodeId() ? 1 : -1;
				return resd * direction;
			}
*/
return 0;
		}

		public void setColoumn(int i) {
			if(field == i) direction = -1*direction;
			else {
				field = i;
				direction = 1;
			}
		}
	}
	
 	private void setupMouseListeners(IWorkbenchPartSite site, final Composite parent) {
		m_unknownsViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (m_selectedCodeRefs.size() == 1) {
					GuiEngine.setCodePoint(m_selectedCodeRefs.get(0));
					GuiEngine.broadcast(Broadcaster.BROADCAST_SHOW_CODE_POINT, "");
				}
			}
		});
		
		m_unknownsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();

				m_selectedCodeRefs.clear();
				
				ArrayList<Reference> m_selectedTimeRefs = new ArrayList<Reference>();
				ArrayList<Reference> m_selectedLoopRefs = new ArrayList<Reference>();
				
				for (Object object : ((IStructuredSelection)selection).toList()) {
/* TODO					
					Unknown unknown = (Unknown)object;
					
					if (unknown == null)
						continue;
					
					Reference ref = ReferenceFactory.createReference(
								"0x" + Integer.toHexString(unknown.getPc()), ReferenceClass.ENDPOINT_REF);
						Engine engine = GuiEngine.getEngine();
						ref.resolve(engine, engine.getNodeId(), engine.getCoreId());
					if(unknown.getType().equals(InstructionTimeUnknown.class.getName())){
						m_selectedTimeRefs.add(ref);
					} else if(unknown.getType().equals(LoopIterationsUnknown.class.getName())){
						m_selectedLoopRefs.add(ref);
						LoopIterationsUnknown loop = (LoopIterationsUnknown)unknown;
						// TODO
						//selectedLoopBlock = loop.getTail();
					} else {
						assert(false);
					}
*/					
				}
				
				m_setLoopIterationsAction.setEnabled(false);
				m_setInstructionTimeAction.setEnabled(false);
				if(m_selectedTimeRefs.size() == 0 && m_selectedLoopRefs.size() == 1){
					m_selectedCodeRefs.addAll(m_selectedLoopRefs);
					m_setLoopIterationsAction.setEnabled(true);
				} else if(m_selectedTimeRefs.size() > 0 && m_selectedLoopRefs.size() == 0){
					m_selectedCodeRefs.addAll(m_selectedTimeRefs);
					m_setInstructionTimeAction.setEnabled(true);
				} 
				
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
     			new InstructionTime(parent.getShell(), GuiEngine.getEngine(), m_selectedCodeRefs, route);
			}
		}; 
		m_setInstructionTimeAction.setText("Set instruction time");
		menuMgr.add(m_setInstructionTimeAction);
		m_setInstructionTimeAction.setEnabled(false);

		m_setLoopIterationsAction = new Action() { 
			public void run() {
				Route route = GuiEngine.getEngine().getSelectedRoute();
     			new Iterations(parent.getShell(), route);
			}
		};
		 
		m_setLoopIterationsAction.setText("Set iterations");
		menuMgr.add(m_setLoopIterationsAction);
		m_setLoopIterationsAction.setEnabled(false);
		
		// required, for extensions
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
}
