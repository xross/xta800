package com.xmos.xta.rcp.views.InfoView;

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
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;
import com.xmos.xta.engine.unknowns.Unknown;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.XtaRouteInfo;
import com.xmos.xta.rcp.dialogs.InstructionTime;
import com.xmos.xta.rcp.nodes.Node;

public class UnknownsViewer {

	private Display m_display;
	private TableViewer m_unknownsViewer;
	
	List<Reference> m_selectedCodeRefs;
	Action m_setInstructionTimeAction;

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
					if (col.getText().equals(UnknownsLabelProvider.BLOCKID)) {
						colIndex = 0;
					} else if (col.getText().equals(UnknownsLabelProvider.PC)) {
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
		XtaNotifier notifier = XtaNotifier.getInstance();
		m_unknownsViewer.setInput(notifier.getSelectedRoute());

		XtaRouteInfo routeInfo = notifier.getRouteInfo();
		if (routeInfo != null) {
			updateHighlighting(routeInfo.getSelectedNode());
		}
	}

	public void updateHighlighting(Node selectedNode) {

		if(selectedNode != null){

			for (TableItem item : m_unknownsViewer.getTable().getItems()) {
				Unknown unknown = (Unknown)item.getData();
				HashSet<Integer> selectedBlocks = new HashSet<Integer>();
				selectedNode.getBlockIds(selectedBlocks);
				if (selectedBlocks.contains(unknown.getBasicBlockId())) {
					item.setBackground(m_display.getSystemColor(SWT.COLOR_YELLOW));
				} else {
					item.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
	
				}
			}
		}
	}

	class UnknownSorter extends ViewerSorter {
		int field = 0;
		int direction = 1;
		
		public int compare(Viewer viewer, Object e1, Object e2) {
			Unknown p1 = (Unknown) e1;
			Unknown p2 = (Unknown) e2;
			switch(field){
			case 0:
				int res0 = p1.getBasicBlockId() > p2.getBasicBlockId() ? 1 : -1;
				return res0 * direction;
			case 1:
				int res2 = p1.getPc() > p2.getPc() ? 1 : -1;
				return res2 * direction;
			case 2:
				int res3 = p1.getPc() > p2.getPc() ? 1 : -1;
				return res3 * direction;
			default:
				int resd = p1.getBasicBlockId() > p2.getBasicBlockId() ? 1 : -1;
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

	private void setupMouseListeners(IWorkbenchPartSite site, final Composite parent) {
		m_unknownsViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (m_selectedCodeRefs.size() == 1) {
					XtaNotifier.getInstance().notifyShow(m_selectedCodeRefs.get(0));
				}
			}
		});
		
		m_unknownsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();

				boolean enabled = ((IStructuredSelection) selection).size() > 0;
				m_setInstructionTimeAction.setEnabled(enabled);

				m_selectedCodeRefs.clear();
				for (Object object : ((IStructuredSelection) selection).toList()) {
					Unknown unknown = (Unknown)object;
					
					if (unknown == null)
						continue;
					
					Reference ref = ReferenceFactory.createReference(
							"0x" + Integer.toHexString(unknown.getPc()), ReferenceClass.ENDPOINT_REF);
					Engine engine = XtaEngine.getEngine();
					ref.resolve(engine, engine.getNodeId(), engine.getCoreId());
					m_selectedCodeRefs.add(ref);
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
				Route route = XtaNotifier.getInstance().getSelectedRoute();
     			new InstructionTime(parent.getShell(), XtaEngine.getEngine(), m_selectedCodeRefs, route);
			}
		}; 
		m_setInstructionTimeAction.setText("Set instruction time");
		menuMgr.add(m_setInstructionTimeAction);
		m_setInstructionTimeAction.setEnabled(false);
		
		// required, for extensions
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
}
