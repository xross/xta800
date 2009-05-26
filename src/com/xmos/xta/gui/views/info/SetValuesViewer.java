package com.xmos.xta.gui.views.info;

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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
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
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;
import com.xmos.xta.engine.unknowns.PausableInstructionUnknown;
import com.xmos.xta.engine.unknowns.Unknown;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.dialogs.InstructionTime;

public class SetValuesViewer {

	private TableViewer m_resolvedUnknownsViewer;
	List<Reference> m_selectedCodeRefs;
	Action m_setInstructionTimeAction;
	Action m_clearInstructionTimesAction;

	public SetValuesViewer(Composite parent, IWorkbenchPartSite site, TabFolder folders) {
		m_selectedCodeRefs = new ArrayList<Reference>();

		TabItem tab = new TabItem(folders, SWT.NONE);
		tab.setText("Set values");
		Composite composite = new Composite(folders, SWT.NONE);
		composite.setLayout(new FillLayout());
		m_resolvedUnknownsViewer = new TableViewer(composite);
		tab.setControl(composite);

		final SetTimesSorter setTimesSorter = new SetTimesSorter();
		m_resolvedUnknownsViewer.setSorter(setTimesSorter);

		Table table = m_resolvedUnknownsViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		SetValuesContentProvider contentProvider = new SetValuesContentProvider();
		SetValuesLabelProvider labelProvider = new SetValuesLabelProvider();

		for (int i = 0; i < labelProvider.getColumnNames().length; ++i) {
			TableColumn tableColumn = new TableColumn(table, labelProvider
					.getColumnAligmnments()[i]);
			tableColumn.setText(labelProvider.getColumnNames()[i]);
			tableColumn.setWidth(labelProvider.getColumnWidths()[i]);
		}
		m_resolvedUnknownsViewer.setContentProvider(contentProvider);
		m_resolvedUnknownsViewer.setLabelProvider(labelProvider);
		for (final TableColumn col : m_resolvedUnknownsViewer.getTable().getColumns()) {
			col.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					int colIndex = 0;
					if (col.getText().equals(SetValuesLabelProvider.PC)) {
						colIndex = 0;
					} else if (col.getText().equals(SetValuesLabelProvider.DESC)) {
						colIndex = 1;
					}
					
					setTimesSorter.setColoumn(colIndex);
					m_resolvedUnknownsViewer.refresh();
				}
			});
		}
	    setupMouseListeners(site, parent);
	}

	public void clear() {
		m_resolvedUnknownsViewer.setInput(null);
	}

	public void update() {
		Route route = GuiEngine.getEngine().getSelectedRoute();
		m_resolvedUnknownsViewer.setInput(route);
	}

	private void setupMouseListeners(IWorkbenchPartSite site, final Composite parent) {
		m_resolvedUnknownsViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (m_selectedCodeRefs.size() == 1) {
					GuiEngine.setCodePoint(m_selectedCodeRefs.get(0));
					GuiEngine.broadcast(Notifier.NOTIFY_SHOW_CODE_POINT, "");
				}
			}
		});
		
		m_resolvedUnknownsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				Engine engine = GuiEngine.getEngine();

				boolean enabled = ((IStructuredSelection) selection).size() > 0;
				m_clearInstructionTimesAction.setEnabled(enabled);
				m_setInstructionTimeAction.setEnabled(enabled);

				m_selectedCodeRefs.clear();
				for (Object object : ((IStructuredSelection) selection).toList()) {
					Unknown unknown = (Unknown)object;
					if(unknown.getType().equals(PausableInstructionUnknown.class.getName())){
						Reference ref = ReferenceFactory.createReference(
								"0x" + Integer.toHexString(unknown.getPc()), ReferenceClass.LABEL_REF);
						ref.resolve(engine, engine.getNodeId(), engine.getCoreId());
						m_selectedCodeRefs.add(ref);
						
					} else {
						m_clearInstructionTimesAction.setEnabled(false);
						m_setInstructionTimeAction.setEnabled(false);
					}
				}
			}			
		});
		
		// Create the context menu
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(false);
		Menu menu = menuMgr.createContextMenu(m_resolvedUnknownsViewer.getControl());
		m_resolvedUnknownsViewer.getControl().setMenu(menu);
		site.registerContextMenu(menuMgr, m_resolvedUnknownsViewer);
		
		// Create the action
		m_clearInstructionTimesAction = new Action() { 
			public void run() {
				for (Reference ref : m_selectedCodeRefs) {
					GuiEngine.getEngine().unsetInstructionTime(ref.getPc());
				}
			}
		}; 
		m_clearInstructionTimesAction.setText("Clear instruction time");
		menuMgr.add(m_clearInstructionTimesAction);
		m_clearInstructionTimesAction.setEnabled(false);

		m_setInstructionTimeAction = new Action() { 
			public void run() {
				Route route = GuiEngine.getEngine().getSelectedRoute();
     			new InstructionTime(parent.getShell(), GuiEngine.getEngine(), m_selectedCodeRefs, route);
			}
		}; 
		m_setInstructionTimeAction.setText("Set instruction time");
		menuMgr.add(m_setInstructionTimeAction);
		m_setInstructionTimeAction.setEnabled(false);

		// required, for extensions
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	class SetTimesSorter extends ViewerSorter {
		int field = 0;
		int direction = 1;
		
		public int compare(Viewer viewer, Object e1, Object e2) {
			int pc1 = ((Unknown)e1).getPc();
			int pc2 = ((Unknown)e2).getPc();
			switch(field){
			case 0:
				int res0 = pc1 > pc2 ? 1 : -1;
				return res0 * direction;
			case 1:
				Engine engine = GuiEngine.getEngine();
				int res2 = engine.getInstructionTimeTaken(pc1) > engine.getInstructionTimeTaken(pc2) ? 1 : -1;
				return res2 * direction;
			default:
				int resd = pc1 > pc2 ? 1 : -1;
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
}
