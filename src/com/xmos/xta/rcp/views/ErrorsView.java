package com.xmos.xta.rcp.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Displayer;
import com.xmos.xta.rcp.XtaDisplayer;
import com.xmos.xta.rcp.XtaLog;

public class ErrorsView extends ViewPart implements IPropertyChangeListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.rcp.views.Errors";

    private TableViewer m_tableViewer;
    
	public ErrorsView() {
		if (DEBUG) System.out.printf("Errors()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Errors: createPartControl\n");	
		 
		m_tableViewer = new TableViewer(parent);		

	    Table table = m_tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
			    	    
    	TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
    	tableColumn.setText("Description");
    	tableColumn.setWidth(500);
	    
	    m_tableViewer.setContentProvider(new ErrorsViewContentProvider());				
	    m_tableViewer.setLabelProvider(new ErrorsViewLabelProvider());	
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Errors: setFocus\n");
		updateDisplay();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Errors: propertyChange: %s\n", event.getProperty());
		
		if (event.getProperty().equals(Displayer.NOTIFY_ERROR)) {
			updateDisplay();
    	} else if (event.getProperty().equals(Displayer.NOTIFY_ANALYSIS_BEGIN)) {
    		clearDisplay();
		}
	}
	
	private void updateDisplay() {
		if (DEBUG) System.out.printf("Errors: updateDisplay\n"); 
		m_tableViewer.setInput(XtaLog.getInstance());
	}

	private void clearDisplay() {
		if (DEBUG) System.out.printf("Errors: clearDisplay\n"); 
		XtaLog.getInstance().clearStrings(XtaLog.Level.ERROR);
		m_tableViewer.refresh();
	}
}
