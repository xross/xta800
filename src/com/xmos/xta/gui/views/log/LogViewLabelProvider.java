package com.xmos.xta.gui.views.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import com.xmos.xta.gui.XtaLog;
import com.xmos.xta.gui.XtaLogEntry;

public class LogViewLabelProvider extends LabelProvider implements ITableLabelProvider {
    public static final boolean DEBUG = false;

    private String[] m_columnNames;
    private int[] m_columnWidths;
    private int[] m_columnAligmnments;
    
    static final String DATE        = "Date";
    static final String LEVEL       = "Type";
    static final String DESCRIPTION = "Description";
    
    DateFormat m_dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    
    public LogViewLabelProvider() {
    	m_columnNames = new String[] { DATE, LEVEL, DESCRIPTION };
    	m_columnWidths = new int[] { 150, 50, 500 };
    	m_columnAligmnments = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT };
    }
    
	public Image getColumnImage(Object element, int columnIndex) {
		if (DEBUG) System.out.printf("LogViewLabelProvider: getColumnImage\n");	
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (DEBUG) System.out.printf("LogViewLabelProvider: getColumnText: index: %d\n", columnIndex);	
		
		assert((columnIndex >= 0) && (columnIndex < m_columnNames.length));
		
		XtaLogEntry entry = (XtaLogEntry)element;	
		if (m_columnNames[columnIndex].equals(DATE)) {
			return m_dateFormat.format(entry.getDate());
		} else if (m_columnNames[columnIndex].equals(LEVEL)) {
			return XtaLog.getInstance().getLevelString(entry.getLevel());			
		} else if (m_columnNames[columnIndex].equals(DESCRIPTION)) {
			return entry.getString();
		}
		assert(false);
		return "";
	}
		
	public String[] getColumnNames() {
		return m_columnNames;
	}

	public int[] getColumnWidths() {
		return m_columnWidths;
	}

	public int[] getColumnAligmnments() {
		return m_columnAligmnments;
	}
}
