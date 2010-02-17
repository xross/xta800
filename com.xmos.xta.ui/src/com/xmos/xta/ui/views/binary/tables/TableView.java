package com.xmos.xta.ui.views.binary.tables;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.views.ControlMonitor;
import com.xmos.xta.ui.views.binary.BinaryView;

public class TableView extends ControlMonitor implements IPropertyChangeListener {
	private TableViewer m_viewer;

	static final int NAME = 0;
	static final int ADDRESS = 1;
	static final int SIZE = 2;
	
	public TableView(final Composite parent, IStructuredContentProvider contentProvider, boolean supportDoubleClicks) {
		super(BinaryView.getEnabledCommands(), true);
		GuiEngine.addBroadcastListener(this);
		
		parent.setLayout(new FillLayout());
		
		m_viewer = new TableViewer(parent);
		Table table = m_viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);	
				
		createTableColumn("Name",    150, SWT.LEFT);
		createTableColumn("Address", 100, SWT.LEFT);
		createTableColumn("Size",    100, SWT.LEFT);
				
		m_viewer.setContentProvider(contentProvider);
		m_viewer.setLabelProvider(new TableViewLabelProvider());			
		
		final TableViewSorter sorter = new TableViewSorter();
		m_viewer.setSorter(sorter);
		
		registerSorterWithColumn(sorter, NAME);
		registerSorterWithColumn(sorter, ADDRESS);
		registerSorterWithColumn(sorter, SIZE);		
		
		if (supportDoubleClicks) {
		    addDoubleClickListener();
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE) ||
			event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			initialise();			
		}		
	}	

	private void initialise() {
		XeLoader xeLoader = GuiEngine.getEngine().getXeLoader();
		if (!xeLoader.isLoaded()) {
			return;
		}
		m_viewer.setInput(xeLoader);
	}
	
	private void createTableColumn(String text, int width, int alignment) {
		TableColumn tableColumn = new TableColumn(m_viewer.getTable(), alignment);
		tableColumn.setText(text);
		tableColumn.setWidth(width);	
	}
	
	private void registerSorterWithColumn(final TableViewSorter sorter, final int columnIndex) {
		m_viewer.getTable().getColumn(columnIndex).addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				sorter.setColoumn(columnIndex);
				m_viewer.refresh();			
			}
		});
	}
	
	private void addDoubleClickListener() {
		m_viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
    		    ISelection selection = event.getSelection(); 
	    		if (selection instanceof IStructuredSelection) {
	    			Object[] elements = ((IStructuredSelection)selection).toArray();
	    			if (elements.length > 0) {
	    				if (elements[0] instanceof TableElement) {
	    					TableElement tableElement = (TableElement)elements[0];
	    					
	    					Reference ref = ReferenceFactory.createReference("0x" + 
	    							Long.toHexString(tableElement.getAddress()), ReferenceClass.LABEL_REF);
	
	    					Engine engine = GuiEngine.getEngine();
	    					ref.resolve(engine);
	    					
	    					GuiEngine.setCodePoint(ref);
	    					GuiEngine.broadcast(Broadcaster.BROADCAST_SHOW_CODE_POINT, "");    					
	    				}
	    			}
	    		}
			}
		});		
	}
}

