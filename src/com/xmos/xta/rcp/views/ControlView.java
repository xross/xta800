package com.xmos.xta.rcp.views;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xeobject.Function;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.engine.CodeReference;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Progress;
import com.xmos.xta.rcp.XtaDisplayer;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.XtaErrorLog;
import com.xmos.xta.rcp.XtaFileFinder;
import com.xmos.xta.rcp.editors.SourcesEditor;
import com.xmos.xta.rcp.editors.SourcesEditorInput;
import com.xmos.xta.rcp.icons.Icons;

public class ControlView extends ViewPart implements IPropertyChangeListener,
                                                     ISelectionListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.rcp.views.Control";

    public static final String STATUS_EMPTY  = "Empty";
    public static final String STATUS_LOADED = "Loaded";
    public static final String STATUS_BUSY   = "Busy";
    public static final String STATUS_DONE   = "Done";
    
    private Display m_display;
    private Composite m_parent;
    private Combo m_functions;

    private boolean m_isExhaustive;

    private Text m_startPoint;
    private Text m_endPoint;
    private Label m_engineStatus;
    private Label m_progress;
    
    private volatile ProgressUpdater m_progressUpdater;

	public ControlView() {
		if (DEBUG) System.out.printf("Tree()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
		
		m_isExhaustive = false;
		m_progressUpdater = null;
	}

	public void createPartControl(Composite parent) {
	    if (DEBUG) System.out.printf("Control: createPartControl\n");
	    m_parent = parent;
	    m_display = m_parent.getDisplay();

	    ExpandBar bar = new ExpandBar(m_parent, SWT.V_SCROLL | SWT.H_SCROLL);

	    createSettingsBar(bar);
	    createStatusBar(bar);
	    createControlBar(bar);
	    bar.setSpacing(8);
	}
	
	public void setFocus() {
		if (DEBUG) System.out.printf("Control: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Control: propertyChange: %s\n", event.getProperty());	
		
		if (event.getProperty().equals("DisplayRoute")) {
	        if (m_progressUpdater != null) {
	        	m_progressUpdater.end();
	        }
        	m_progress.setText("" + XtaEngine.getEngine().getProgress().getCurValue());
			m_engineStatus.setText(STATUS_DONE);

		} else if (event.getProperty().equals("LoadExecutable")) {
			createXeObject();
            populateFunctionList();			
	        openEditors();
	        if (m_progressUpdater != null) {
	        	m_progressUpdater.end();
	        }
        	m_progress.setText("0");
        	m_engineStatus.setText(STATUS_LOADED);

		} else if (event.getProperty().equals("UpdateState")) {
			
		} else if (event.getProperty().equals("SetStartPoint")) {
			CodeReference startPoint = XtaDisplayer.getInstance().getStartPoint();
			m_startPoint.setText(startPoint.getRef());

		} else if (event.getProperty().equals("SetEndPoint")) {
			CodeReference endPoint = XtaDisplayer.getInstance().getEndPoint();
			m_endPoint.setText(endPoint.getRef());
		}
	}
	
    public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		if (DEBUG) System.out.printf("Control: selectionChanged\n");	
     }
	
    public void executeLoad() {				
		if (DEBUG) System.out.printf("Control: executeLoad\n");	

		FileDialog fileDialog = new FileDialog(getSite().getWorkbenchWindow().getShell(), SWT.OPEN);
        fileDialog.setText("Open Xe File");
        String[] filterExt = { "*.xe" };
        fileDialog.setFilterExtensions(filterExt);
        String xeFileName = fileDialog.open();
        
        if (xeFileName != null) {
        	if (DEBUG) {
        		System.out.printf("Control: File open: %s\n", xeFileName);
        	}
        	Engine engine = XtaEngine.getEngine();
        	boolean success = engine.loadExecutable(xeFileName);
        	if (DEBUG) {
        		if (success) {
                    System.out.printf("Control: Successfully loaded executable: %s\n", xeFileName);
        		} else {
                    System.out.printf("Control: Failed to open file: %s\n", xeFileName);
        		}
        	}
        } else {
        	if (DEBUG) {
        		System.out.printf("Control: File open: null\n");
        	}
        }	                
	}

	public void executeRunFunction() {
		if (DEBUG) System.out.printf("Control: executeRunFunction\n");	

		if (m_progressUpdater != null) {
			return;
		}

		String functionName = m_functions.getText();
		if (!functionName.equals("")) {
			RunEngineMethodFunc run = new RunEngineMethodFunc(functionName, m_isExhaustive);
			run.start();
			m_progressUpdater = new ProgressUpdater(XtaEngine.getEngine(), m_progress);
			m_progressUpdater.start();
		} else {
			XtaErrorLog.getInstance().addError("No function/path specified");
		}
	}
	
	public void executeRunPath() {
		if (DEBUG) System.out.printf("Control: executeRunPath\n");	

		if (m_progressUpdater != null) {
			return;
		}
		
		String startRef = m_startPoint.getText();
		String endRef = m_endPoint.getText();
		if (startRef.equals("")) {
			XtaErrorLog.getInstance().addError("Please specify a start reference");
		} else if (endRef.equals("")) {
			XtaErrorLog.getInstance().addError("Please specify an end reference");
		} else {
			m_engineStatus.setText(STATUS_BUSY);
			RunEngineMethodPath run = new RunEngineMethodPath(startRef, endRef, m_isExhaustive);
			run.start();
			m_progressUpdater = new ProgressUpdater(XtaEngine.getEngine(), m_progress);
			m_progressUpdater.start();
		}
	}
	
	public void setExhaustive(boolean isExhaustive) {
		m_isExhaustive = isExhaustive;
	}
	
	public boolean isExhaustive() {
		return m_isExhaustive;
	}

	public int getFrequency() {
		return XtaEngine.getEngine().getFrequency();
	}

	public void setFrequency(int frequency) {
		XtaEngine.getEngine().setFrequency(frequency);
	}

	public int getNumThreads() {
		return XtaEngine.getEngine().getNumThreads();
	}

	public void setNumThreads(int numThreads) {
		XtaEngine.getEngine().setNumThreads(numThreads);
	}
	
    private void createXeObject() {
		Engine engine = XtaEngine.getEngine();
		XeObject xeObject = new XeObject(engine.getXeLoader(), new XtaFileFinder());
		XtaEngine.getEngine().setXeObject(xeObject);                    
    }
    
    private void populateFunctionList() {
    	m_functions.removeAll();
    	XeObject xeObject = XtaEngine.getEngine().getXeObject();                    
        for (final Function function : xeObject.getFunctions()) {
           	m_functions.add(function.getText());
        }
    }
    
    private void openEditors() {
		final IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
		page.closeAllEditors(false);
		IEditorInput sourcesInput = new SourcesEditorInput();
		try {
		    page.openEditor(sourcesInput, SourcesEditor.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}    	
    }
			
    class RunEngineMethodFunc extends Thread {
        private String m_functionName;
        private boolean m_full;

        RunEngineMethodFunc(String functionName, boolean full) {
            m_functionName = functionName;
            m_full = full;
        }

        public void run() {
            if (!XtaEngine.getEngine().analyseRoute(m_functionName, m_full))
            	XtaErrorLog.getInstance().addError(XtaEngine.getEngine().getErrorString());
        }
    }

    class RunEngineMethodPath extends Thread {
        private String m_startRef;
        private String m_endRef;
        private boolean m_full;

        RunEngineMethodPath(String startRef, String endRef, boolean full) {
        	m_startRef = startRef;
        	m_endRef = endRef;
            m_full = full;
        }

        public void run() {
            if (!XtaEngine.getEngine().analyseRoute(m_startRef, m_endRef, m_full))
            	XtaErrorLog.getInstance().addError(XtaEngine.getEngine().getErrorString());
        }
    }

    class RunEngineMethodRef extends Thread {
        private String m_startRef;
        private String m_endRef;
        private boolean m_full;

        RunEngineMethodRef(String startRef, String endRef, boolean full) {
            m_startRef = startRef;
            m_endRef = endRef;
            m_full = full;
        }

        public void run() {        	
            if (!XtaEngine.getEngine().analyseRoute(m_startRef, m_endRef, m_full))
            	XtaErrorLog.getInstance().addError(XtaEngine.getEngine().getErrorString());
        }
    }	

    class ProgressUpdater extends Thread {
        private Engine m_engine;
        private Label m_progress;

        public ProgressUpdater(Engine engine, final Label progress) {
            m_engine = engine;
            m_progress = progress;
        }

        public void end() {
        	m_progressUpdater = null;
        }

        public void run() {
        	Thread thisThread = Thread.currentThread();
        	final Progress p = m_engine.getProgress();  
            while (m_progressUpdater == thisThread) {
            	m_display.asyncExec(new Runnable() {
        			public void run() {
        				m_progress.setText("" + p.getCurValue());
        			}});            	

            	try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
	private void createControlBar(ExpandBar bar) {
		Composite composite = getBarComposite(bar, 1);

	    Button loadButton = new Button(composite, SWT.PUSH);
	    loadButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	    loadButton.setToolTipText("Open file");
	    loadButton.setImage(Icons.getIcon("load"));
	    loadButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				executeLoad();
			}			
		});

	    createFunctionControl(composite);
	    createPathControl(composite);
	    
	    ExpandItem barItem = new ExpandItem(bar, SWT.NONE, 0);
	    barItem.setText("Xta Control");
	    barItem.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	    barItem.setControl(composite);
	    barItem.setExpanded(true);
	}

	private void createFunctionControl(Composite composite) {
	    Group group = new Group(composite, SWT.SHADOW_ETCHED_IN);
	    GridLayout layout = new GridLayout();
	    group.setText("Function timing");
	    group.setLayout(layout);
	    group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	    m_functions = new Combo(group, SWT.NONE | SWT.DROP_DOWN | SWT.READ_ONLY);
	    m_functions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	    Button runButton = new Button(group, SWT.PUSH);
	    runButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	    runButton.setToolTipText("Time specified function");
	    runButton.setImage(Icons.getIcon("run"));
	    runButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				executeRunFunction();
			}			
		});
	}
	
	private void createPathControl(Composite composite) {
		int numColumns = 5;
	    Group group = new Group(composite, SWT.SHADOW_ETCHED_IN);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = numColumns;
	    group.setLayout(layout);
	    group.setText("Path timing");
	    group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    
	    Group startGroup = new Group(group, SWT.SHADOW_ETCHED_IN);
	    startGroup.setLayout(new FillLayout());
	    startGroup.setText("From");
	    startGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
	    m_startPoint = new Text(startGroup, SWT.SINGLE | SWT.CENTER);
	
	    Group endGroup = new Group(group, SWT.SHADOW_ETCHED_IN);
	    endGroup.setLayout(new FillLayout());
	    endGroup.setText("To");
	    endGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
	    m_endPoint = new Text(endGroup, SWT.SINGLE | SWT.CENTER);
	
	    Button findStartButton = new Button(group, SWT.PUSH);
	    findStartButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
	    findStartButton.setToolTipText("Show start point");
	    findStartButton.setImage(Icons.getIcon("start16x16"));
	    findStartButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CodeReference startPoint = XtaDisplayer.getInstance().getStartPoint();
				XtaDisplayer.getInstance().notifyShow(startPoint);
			}
		});

	    Button findStopButton = new Button(group, SWT.PUSH);
	    findStopButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
	    findStopButton.setToolTipText("Show stop point");
	    findStopButton.setImage(Icons.getIcon("stop16x16"));
	    findStopButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CodeReference startPoint = XtaDisplayer.getInstance().getEndPoint();
				XtaDisplayer.getInstance().notifyShow(startPoint);
			}
		});

	    Button timePathButton = new Button(group, SWT.PUSH);
	    timePathButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	    timePathButton.setToolTipText("Time specified path");
	    timePathButton.setImage(Icons.getIcon("run"));
	    timePathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				executeRunPath();
			}
		});
	}

	private void createControlLabel(Composite parent, String str) {
		Label label = new Label(parent, SWT.NONE);		        
	    label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        label.setText(str);
	}

	private void createStatusBar(ExpandBar bar) {
		Composite composite = getBarComposite(bar, 1);

	    Group group = new Group(composite, SWT.SHADOW_ETCHED_IN);
	    GridLayout layout = new GridLayout();
	    group.setLayout(layout);
	    group.setText("Engine status");
	    group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		m_engineStatus = new Label(group, SWT.SHADOW_NONE | SWT.CENTER);
		m_engineStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		m_engineStatus.setText(STATUS_EMPTY);

	    Group progressGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
	    GridLayout progressLayout = new GridLayout();
	    progressGroup.setLayout(progressLayout);
	    progressGroup.setText("Blocks analysed");
	    progressGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	    m_progress = new Label(progressGroup, SWT.SHADOW_NONE | SWT.CENTER);
	    m_progress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    m_progress.setText("" + 0);

	    ExpandItem barItem = new ExpandItem(bar, SWT.NONE, 0);
	    barItem.setText("Status");
	    barItem.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	    barItem.setControl(composite);
	    barItem.setExpanded(true);
	}

	private void createSettingsBar(ExpandBar bar) {
		Composite composite = getBarComposite(bar, 2);

		createControlLabel(composite, "Exhustive:");
		final Button button = new Button(composite, SWT.CHECK);
		button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		button.setSelection(m_isExhaustive);		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				m_isExhaustive = button.getSelection();
			}			
		});
	 	
		createControlLabel(composite, "Frequency:");
		final Spinner freqSpinner = new Spinner(composite, SWT.BORDER);
		freqSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		freqSpinner.setMinimum(1);
		freqSpinner.setMaximum(10000);
		freqSpinner.setSelection(getFrequency());
		freqSpinner.setIncrement(1);
		freqSpinner.setPageIncrement(10);
		freqSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setFrequency(freqSpinner.getSelection());
			}			
		});

		createControlLabel(composite, "Num Threads:");
		final Spinner threadSpinner = new Spinner(composite, SWT.BORDER);
		threadSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		threadSpinner.setMinimum(1);
		threadSpinner.setMaximum(8);
		threadSpinner.setSelection(getNumThreads());
		threadSpinner.setIncrement(1);
		threadSpinner.setPageIncrement(4);
		threadSpinner.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setNumThreads(threadSpinner.getSelection());
			}			
		});
		
	    ExpandItem barItem = new ExpandItem(bar, SWT.NONE, 0);
	    barItem.setText("Settings");
	    barItem.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	    barItem.setControl(composite);
	    barItem.setExpanded(true);
	}

	private Composite getBarComposite(ExpandBar bar, int numColumns) { 
	    Composite composite = new Composite(bar, SWT.NONE);
	    GridLayout layout = new GridLayout();
	    layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 10;
	    layout.verticalSpacing = 10;
	    layout.numColumns = numColumns;
	    composite.setLayout(layout);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    return composite;
	}
}
