package com.xmos.xta.rcp.views;

import java.util.Vector;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Unknown;
import com.xmos.xta.rcp.XtaDisplayer;
import com.xmos.xta.rcp.XtaRouteInfo;
import com.xmos.xta.rcp.nodes.Node;

public class UnknownsView extends ViewPart implements IPropertyChangeListener,
													  ISelectionListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.rcp.views.Unknowns";

    protected Display m_display;
    private TableViewer m_tableViewer;
    private Node m_selectedNode;
    
	public UnknownsView() {
		if (DEBUG) System.out.printf("Unknowns()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Unknowns: createPartControl\n");	
		
		m_display = parent.getDisplay();
		m_tableViewer = new TableViewer(parent);		

	    Table table = m_tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
			    
	    UnknownsViewContentProvider contentProvider = new UnknownsViewContentProvider();
	    UnknownsViewLabelProvider labelProvider = new UnknownsViewLabelProvider();
	    
	    for (int i = 0; i < labelProvider.getColumnNames().length; ++i) {
	    	TableColumn tableColumn = new TableColumn(table, 
	    			labelProvider.getColumnAligmnments()[i]);
	    	tableColumn.setText(labelProvider.getColumnNames()[i]);
	    	tableColumn.setWidth(labelProvider.getColumnWidths()[i]);
	    }
	    
	    m_tableViewer.setContentProvider(contentProvider);				
	    m_tableViewer.setLabelProvider(labelProvider);
	    
		getSite().getPage().addSelectionListener(this);
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Unknowns: setFocus\n");
		updateDisplay();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Unknowns: propertyChange: %s\n", event.getProperty());		
		
		if (event.getProperty().equals("DisplayRoute")) {
			updateDisplay();
		} else if (event.getProperty().equals("LoadExecutable")) {
			m_tableViewer.setInput(null);
		} 
	}

	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		if (sel.isEmpty())
    		return;
    	
		if (sel instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)sel).getFirstElement();
            if (object instanceof Node) {
            	m_selectedNode = (Node)object;
            	updateHighlighting();
            }
        }
	}

	private void updateDisplay() {
		if (DEBUG) System.out.printf("Unknowns: updateDisplay\n"); 

		final XtaRouteInfo routeInfo = XtaDisplayer.getInstance().getRouteInfo();
		if (routeInfo != null) {
            m_tableViewer.setInput(routeInfo.getRoute());
            m_selectedNode = routeInfo.getSelectedNode();
            updateHighlighting();
		} else {
			m_tableViewer.setInput(null);
		}
    }

	private void updateHighlighting() {
		if (DEBUG) System.out.printf("Unknowns: updateHighlighting\n"); 

		for (TableItem item : m_tableViewer.getTable().getItems()) {
			Unknown unknown = (Unknown)item.getData();
			Vector<Integer> selectedBlocks = new Vector<Integer>();
			m_selectedNode.getBlockIds(selectedBlocks);
			if (selectedBlocks.contains(unknown.getPathId())) {
				item.setBackground(m_display.getSystemColor(SWT.COLOR_YELLOW));
			} else {
				item.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
			}
		}
    }
}
