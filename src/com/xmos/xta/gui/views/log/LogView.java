package com.xmos.xta.gui.views.log;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.gui.GuiEngine;

public class LogView extends ViewPart implements IPropertyChangeListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.gui.views.log.Log";

    private TableViewer m_tableViewer;
    
	public LogView() {
		if (DEBUG) System.out.printf("Errors()\n");			
		GuiEngine.addBroadcastListener(this);
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Errors: createPartControl\n");	

		m_tableViewer = new TableViewer(parent);		

	    Table table = m_tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		LogViewContentProvider contentProvider = new LogViewContentProvider();
		LogViewLabelProvider labelProvider = new LogViewLabelProvider();

		for (int i = 0; i < labelProvider.getColumnNames().length; ++i) {
			TableColumn tableColumn = new TableColumn(table, labelProvider
					.getColumnAligmnments()[i]);
			tableColumn.setText(labelProvider.getColumnNames()[i]);
			tableColumn.setWidth(labelProvider.getColumnWidths()[i]);
		}

	    m_tableViewer.setContentProvider(contentProvider);				
	    m_tableViewer.setLabelProvider(labelProvider);	
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Errors: setFocus\n");
		updateDisplay();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Errors: propertyChange: %s\n", event.getProperty());
		
		if (event.getProperty().equals(Notifier.NOTIFY_ERROR)) {
			updateDisplay();
			
    	} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
    		clearDisplay();

		}
	}
	
	private void updateDisplay() {
		if (DEBUG) System.out.printf("Errors: updateDisplay\n"); 
		m_tableViewer.setInput(GuiEngine.getLog());
	}

	private void clearDisplay() {
		if (DEBUG) System.out.printf("Errors: clearDisplay\n"); 
		GuiEngine.getLog().clearAll();
		m_tableViewer.refresh();
	}
}
