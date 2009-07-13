package com.xmos.xta.gui.views.control;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.gui.GuiEngine;

public class ControlView extends ViewPart implements IPropertyChangeListener,
                                                     ISelectionListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.gui.views.control.Control";

    private Display m_display;

    private Spinner m_freqSpinner;
    private Spinner m_threadSpinner;
    
	public ControlView() {
		if (DEBUG) System.out.println("Control(" + this.hashCode() + "): new");			
		GuiEngine.addBroadcastListener(this);
	}
	
	public void createPartControl(Composite parent) {
	    if (DEBUG) System.out.println("Control (" + this.hashCode() + "): createPartControl");

	    m_display = parent.getDisplay();

		Composite controls = new Composite(parent, SWT.NONE);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = 3;
	    controls.setLayout(layout);
	    controls.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
	    setBackground(controls);

	    createSettingsBar(controls);
	}
	
	public void setFocus() {
		if (DEBUG) System.out.println("Control(" + this.hashCode() + "): setFocus");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.println("Control(" + this.hashCode() + ") propertyChange: " + event.getProperty());	
		
		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {			
	        updateState();
	        
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE)) {
	        updateState();
	        
		}	        
	}

	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		if (DEBUG) System.out.println("Control(" + this.hashCode() + "): selectionChanged");	
     }
	
	public int getFrequency() {
		return GuiEngine.getEngine().getFrequency();
	}

	public void setFrequency(int frequency) {
		GuiEngine.getEngine().setFrequency(frequency);
	}

	public int getNumThreads() {
		return GuiEngine.getEngine().getNumThreads();
	}

	public void setNumThreads(int numThreads) {
		GuiEngine.getEngine().setNumThreads(numThreads);
	}

	public void setNodeId(String string) {
        try {
        	int nodeId = Integer.parseInt(string); 
        	GuiEngine.getEngine().setNodeId(nodeId);
        } catch (NumberFormatException e) {
        }    	        
	}

	public void setCoreId(String string) {
        try {
        	int coreId = Integer.parseInt(string); 
        	GuiEngine.getEngine().setCoreId(coreId);
        } catch (NumberFormatException e) {
        }    	        
	}

	private void setBackground(Composite composite) {
	    composite.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
	}
		
	private void updateState() {
		m_freqSpinner.setSelection(GuiEngine.getEngine().getFrequency());
		m_threadSpinner.setSelection(GuiEngine.getEngine().getNumThreads());		
	}
	
	private void createSettingsBar(Composite controls) {
		Group freqGroup = createGroup(controls, "Frequency", 1, 1);
	    m_freqSpinner = new Spinner(freqGroup, SWT.BORDER);
		m_freqSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		m_freqSpinner.setMinimum(1);
		m_freqSpinner.setMaximum(10000);
		m_freqSpinner.setSelection(getFrequency());
		m_freqSpinner.setIncrement(1);
		m_freqSpinner.setPageIncrement(10);
		m_freqSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setFrequency(m_freqSpinner.getSelection());
			}			
		});

		Group threadsGroup = createGroup(controls, "Num threads", 1, 1);
		m_threadSpinner = new Spinner(threadsGroup, SWT.BORDER);
		m_threadSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		m_threadSpinner.setMinimum(1);
		m_threadSpinner.setMaximum(8);
		m_threadSpinner.setSelection(getNumThreads());
		m_threadSpinner.setIncrement(1);
		m_threadSpinner.setPageIncrement(4);
		m_threadSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setNumThreads(m_threadSpinner.getSelection());
			}			
		});
	}

	private Group createGroup(Composite parent, String description, int numColumns, int span) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = numColumns;
	    group.setLayout(layout);
	    group.setText(description);
	    group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, span, 1));
	    setBackground(group);
	    return group;
	}
}
