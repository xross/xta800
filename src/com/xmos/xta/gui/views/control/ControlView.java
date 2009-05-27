package com.xmos.xta.gui.views.control;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.constraint.Constraint;
import com.xmos.xta.engine.constraint.ConstraintFactory;
import com.xmos.xta.engine.constraint.ConstraintNull;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.gui.Broadcaster;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.icons.Icons;

public class ControlView extends ViewPart implements IPropertyChangeListener,
                                                     ISelectionListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.gui.views.control.Control";

    private Display m_display;
    private Text m_startPoint;
    private Text m_endPoint;
    private Text m_constraint;
    private ModifyListener m_startPointListener;
    private ModifyListener m_endPointListener;
    private KeyListener m_constraintListener;
    private Button m_setConstraintButton; 
    private Button m_timePathButton;
    
    private Spinner m_freqSpinner;
    private Spinner m_threadSpinner;
    
	public ControlView() {
		if (DEBUG) System.out.println("Control(" + this.hashCode() + "): new");			
		GuiEngine.addBroadcastListener(this);
		
		m_startPointListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
			    GuiEngine.broadcast(Broadcaster.BROADCAST_SET_START_POINT, m_startPoint.getText());
			}			
		};
		m_endPointListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
			    GuiEngine.broadcast(Broadcaster.BROADCAST_SET_END_POINT, m_endPoint.getText());
			}			
		};
		m_constraintListener = new KeyListener() {
			public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
				if (e.character == SWT.CR) {
					setConstraint();
					e.doit = false;
				} else if ((e.character >= 0x20 && e.character <= 0xf7) ||
						e.character == SWT.BS) {
					// Any typing characters
				    m_setConstraintButton.setEnabled(true);
				}
			}

			public void keyReleased(org.eclipse.swt.events.KeyEvent e) {
				// Do nothing
			}			
		};

	}

	protected void setConstraint() {
		Engine engine = GuiEngine.getEngine();
		Constraint constraint = ConstraintFactory.createConstraint(engine, m_constraint.getText());
		if (constraint != null) {
			engine.setConstraint(constraint);
		    m_setConstraintButton.setEnabled(false);
		}
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

	    createControlBar(controls);
	    createSettingsBar(controls);
	    
	    updateConstraint();
	    updateStartPoint();
		updateEndPoint();
	}
	
	private void setBackground(Composite composite) {
	    composite.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
	}

	public void setFocus() {
		if (DEBUG) System.out.println("Control(" + this.hashCode() + "): setFocus");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.println("Control(" + this.hashCode() + ") propertyChange: " + event.getProperty());	
		
		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {			
	        setText(m_startPoint, m_startPointListener, "", "");
	        setText(m_endPoint, m_endPointListener, "", "");
	        updateState();
	        
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE)) {
	        updateState();
	        
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_START_POINT) || 
				   event.getProperty().equals(Broadcaster.BROADCAST_SET_START_POINT)) {
			updateStartPoint();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_END_POINT) ||
    			   event.getProperty().equals(Broadcaster.BROADCAST_SET_END_POINT)) {
			updateEndPoint();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_CONSTRAINT) || 
				   event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE)) {
			updateConstraint();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			updateStartPoint();
			updateEndPoint();			
		}
	}

	private void updateEndPoint() {
		Reference endPoint = GuiEngine.getEndPoint();
		updateText(endPoint, m_endPoint, m_endPointListener);
	}

	private void updateStartPoint() {
		Reference startPoint = GuiEngine.getStartPoint();
		updateText(startPoint, m_startPoint, m_startPointListener);
	}
	
	private void updateConstraint() {
		Constraint constraint = GuiEngine.getEngine().getConstraint();
		String str = constraint.getString();
		if (constraint.getClass() == ConstraintNull.class) {
			m_constraint.setText("");
		} else { 
			m_constraint.setText(str);
		}
		m_constraint.setToolTipText(str);
		m_constraint.setSelection(str.length());
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

	private void updateState() {
		m_freqSpinner.setSelection(GuiEngine.getEngine().getFrequency());
		m_threadSpinner.setSelection(GuiEngine.getEngine().getNumThreads());
	}
	
	private void createControlBar(Composite controls) {
	    createConstraintControl(controls);
	    createPathControl(controls);
	}

	private void createPathControl(Composite composite) {
		Group group = createGroup(composite, "Path", 1, 2);

		Group startGroup = createGroup(group, "From", 1, 1);
	    m_startPoint = new Text(startGroup, SWT.SINGLE | SWT.CENTER);
	    m_startPoint.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    m_startPoint.addModifyListener(m_startPointListener);
		
	    Group endGroup = createGroup(group, "To", 1, 1);
	    m_endPoint = new Text(endGroup, SWT.SINGLE | SWT.CENTER);
	    m_endPoint.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    m_endPoint.addModifyListener(m_endPointListener);

	    m_timePathButton = new Button(composite, SWT.PUSH);
	    m_timePathButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	    m_timePathButton.setToolTipText("Time specified path");
	    m_timePathButton.setImage(Icons.getIcon("run"));
	    m_timePathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				GuiEngine.runPath();
			}
		});
	}

	private void createConstraintControl(Composite composite) {
		Group group = createGroup(composite, "Constraint", 1, 2);

	    m_constraint = new Text(group, SWT.SINGLE | SWT.CENTER);
	    m_constraint.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    m_constraint.addKeyListener(m_constraintListener);

	    m_setConstraintButton = new Button(composite, SWT.PUSH);
	    m_setConstraintButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	    m_setConstraintButton.setToolTipText("Set the constraint");
	    m_setConstraintButton.setImage(Icons.getIcon("setconstraint"));
	    m_setConstraintButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setConstraint();
			}
		});
	    m_setConstraintButton.setEnabled(false);
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

	private void updateText(Reference point, Text text, ModifyListener listener) {
		if (point != null) {
			Engine engine = GuiEngine.getEngine();
			point.resolve(engine, engine.getNodeId(), engine.getCoreId());
			if (point.getResolved() || point.getRef().equals("")) {		
		     	setText(text, listener, point.getRef(), point.getRef());
		    }
		} else {
			setText(text, listener, "", "");
		}
		updateTimePathButton();
	}	
	
	private void updateTimePathButton() {
		if (!m_startPoint.getText().equals("") && !m_endPoint.getText().equals(""))
			m_timePathButton.setEnabled(true);
		else
			m_timePathButton.setEnabled(false);
	}

	private void setText(Text text, ModifyListener listener, String str, String toolTip) {
		text.removeModifyListener(listener);
		text.setText(str);
		text.addModifyListener(listener);	
		text.setSelection(str.length());
		text.setToolTipText(toolTip);
	}
}
