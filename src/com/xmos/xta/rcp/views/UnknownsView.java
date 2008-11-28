package com.xmos.xta.rcp.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.rcp.XtaDisplayer;
import com.xmos.xta.rcp.XtaRouteInfo;

public class UnknownsView extends ViewPart implements IPropertyChangeListener {
    public static final boolean DEBUG = true;
    public static final String ID = "com.xmos.xta.rcp.views.Unknowns";

    private TableViewer m_tableViewer;
    
	public UnknownsView() {
		if (DEBUG) System.out.printf("Unknowns()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Unknowns: createPartControl\n");	
		
		m_tableViewer = new TableViewer(parent);		
	    getSite().setSelectionProvider(m_tableViewer);

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
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Unknowns: setFocus\n");
		updateDisplay();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Unknowns: propertyChange: %s\n", event.getProperty());		
		
		if (event.getProperty().equals("DisplayRoute")) {
			updateDisplay();
		}
	}

    private void updateDisplay() {
		if (DEBUG) System.out.printf("Unknowns: updateDisplay\n"); 

		final XtaRouteInfo routeInfo = XtaDisplayer.getInstance().getRouteInfo();
		if (routeInfo != null) {
            m_tableViewer.setInput(routeInfo.getRoute());
		}		
    }
}
