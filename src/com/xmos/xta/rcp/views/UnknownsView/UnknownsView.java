package com.xmos.xta.rcp.views.UnknownsView;

import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.Unknown;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.XtaRouteInfo;
import com.xmos.xta.rcp.dialogs.InstructionTime;
import com.xmos.xta.rcp.nodes.Node;

public class UnknownsView extends ViewPart implements IPropertyChangeListener,
		                                              ISelectionListener {
	public static final boolean DEBUG = false;
	public static final String ID = "com.xmos.xta.rcp.views.UnknownsView.Unknowns";

	protected Display m_display;
	Composite m_parent;
	private TableViewer m_tableViewer;
	private Node m_selectedNode;
	
	Reference m_selectedCodeRef;
	Action m_setInstructionTimeAction;

	public UnknownsView() {
		if (DEBUG)
			System.out.printf("Unknowns()\n");
		XtaNotifier.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG)
			System.out.printf("Unknowns: createPartControl\n");

		m_parent = parent;
		m_display = parent.getDisplay();
		m_tableViewer = new TableViewer(parent);
		final UnknownSorter us = new UnknownSorter();
		m_tableViewer.setSorter(us);

		Table table = m_tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		UnknownsViewContentProvider contentProvider = new UnknownsViewContentProvider();
		UnknownsViewLabelProvider labelProvider = new UnknownsViewLabelProvider();

		for (int i = 0; i < labelProvider.getColumnNames().length; ++i) {
			TableColumn tableColumn = new TableColumn(table, labelProvider
					.getColumnAligmnments()[i]);
			tableColumn.setText(labelProvider.getColumnNames()[i]);
			tableColumn.setWidth(labelProvider.getColumnWidths()[i]);
		}
		m_tableViewer.setContentProvider(contentProvider);
		m_tableViewer.setLabelProvider(labelProvider);
		for (final TableColumn col : m_tableViewer.getTable().getColumns()) {
			
			col.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					int colIndex = 0;
					if(col.getText().equals(UnknownsViewLabelProvider.BLOCKID)){
						colIndex=0;
					} else if(col.getText().equals(UnknownsViewLabelProvider.TYPE)){
						colIndex=1;
					} else if(col.getText().equals(UnknownsViewLabelProvider.PC)){
						colIndex=2;
					} else if(col.getText().equals(UnknownsViewLabelProvider.REASON)){
						colIndex=3;
					}
					
					us.setColoumn(colIndex);
					m_tableViewer.refresh();
				}
			});

		}
		updateDisplay();
		getSite().getPage().addSelectionListener(this);
		
	    setupMouseListeners();
	}

	public void setFocus() {
		if (DEBUG)
			System.out.printf("Unknowns: setFocus\n");
		updateDisplay();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) 
			System.out.printf("Unknowns: propertyChange: %s\n", event.getProperty());

		if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS) ||
			event.getProperty().equals(Notifier.NOTIFY_UPDATE_EXCLUDES)) {
			
			m_tableViewer.setInput(null);
			updateDisplay();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
			updateDisplay();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			m_tableViewer.setInput(null);
		}
	}

	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		if (sel.isEmpty())
			return;

		if (sel instanceof IStructuredSelection) {
			Object object = ((IStructuredSelection) sel).getFirstElement();
			if (object instanceof Node) {
				m_selectedNode = (Node) object;
				updateHighlighting();
			}
		}
	}

	private void updateDisplay() {
		if (DEBUG)
			System.out.printf("Unknowns: updateDisplay\n");

		final XtaRouteInfo routeInfo = XtaNotifier.getInstance()
				.getRouteInfo();
		if (routeInfo != null) {
			m_tableViewer.setInput(routeInfo.getRoute());
			m_selectedNode = routeInfo.getSelectedNode();
			updateHighlighting();
		} else {
			m_tableViewer.setInput(null);
		}
	}

	private void updateHighlighting() {
		if (DEBUG)
			System.out.printf("Unknowns: updateHighlighting\n");

		for (TableItem item : m_tableViewer.getTable().getItems()) {
			Unknown unknown = (Unknown) item.getData();
			Vector<Integer> selectedBlocks = new Vector<Integer>();
			m_selectedNode.getBlockIds(selectedBlocks);
			if (selectedBlocks.contains(unknown.getBlockId())) {
				item.setBackground(m_display.getSystemColor(SWT.COLOR_YELLOW));
			} else {
				item.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
			}
		}
	}

	class UnknownSorter extends ViewerSorter {
		int field=0;
		int direction =1;
		
		public int compare(Viewer viewer, Object e1, Object e2) {
			Unknown p1 = (Unknown) e1;
			Unknown p2 = (Unknown) e2;
			switch(field){
			case 0:
				int res0 = p1.getBlockId() > p2.getBlockId() ? 1 : -1;
				return res0 * direction;
			case 1:
				int res1 = p1.getTypeString().compareTo(p2.getReason());
				return res1 * direction;
			case 2:
				int res2 = p1.getPc() > p2.getPc() ? 1 : -1;
				return res2 * direction;
			case 3:
				int res3 = p1.getReason().compareTo(p2.getReason());
				return res3 * direction;
			default:
				int resd = p1.getBlockId() > p2.getBlockId() ? 1 : -1;
				return resd * direction;
			}
		}

		public void setColoumn(int i) {
			if(field == i) direction = -1*direction;
			else {
				field = i;
				direction = 1;
			}
		}
	}

	private void setupMouseListeners() {
		m_tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				XtaNotifier.getInstance().notifyShow(m_selectedCodeRef);
			}
		});
		
		m_tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				Unknown unknown = (Unknown)((IStructuredSelection) selection).getFirstElement();
				
				if (unknown != null) {
					m_selectedCodeRef = ReferenceFactory.createReference(
							"0x" + Integer.toHexString(unknown.getPc()), ReferenceClass.ENDPOINT_REF);
					Engine engine = XtaEngine.getEngine();
					m_selectedCodeRef.resolve(engine, engine.getNodeId(), engine.getCoreId());
					m_setInstructionTimeAction.setEnabled(true);
				} else {
					m_setInstructionTimeAction.setEnabled(false);
				}
			}
			
		});
		

		// Create the context menu
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(false);
		Menu menu = menuMgr.createContextMenu(m_tableViewer.getControl());
		m_tableViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, m_tableViewer);
		
		// Create the action
		m_setInstructionTimeAction = new Action() { 
			public void run() {
     			new InstructionTime(m_parent.getShell(), XtaEngine.getEngine(), m_selectedCodeRef);
			}
		}; 
		m_setInstructionTimeAction.setText("Set instruction time");
		menuMgr.add(m_setInstructionTimeAction);
		m_setInstructionTimeAction.setEnabled(false);
		
		// required, for extensions
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
}
