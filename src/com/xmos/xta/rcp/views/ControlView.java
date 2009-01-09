package com.xmos.xta.rcp.views;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import com.xmos.xta.engine.Displayer;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Progress;
import com.xmos.xta.engine.Route;
import com.xmos.xta.rcp.XtaDisplayer;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.XtaErrorLog;
import com.xmos.xta.rcp.XtaFileFinder;
import com.xmos.xta.rcp.XtaRouteInfo;
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

    private Text m_fileName;
    private Text m_startPoint;
    private Text m_endPoint;
    private Text m_currentRoute;
    private Label m_engineStatus;
    private Label m_progress;

    private MemoryUpdater m_memoryUpdater;
    private Group m_memoryGroup;
    private ProgressBar m_memoryUsage;
    
    private Button m_resolveExpressions;
    private Spinner m_freqSpinner;
    private Spinner m_threadSpinner;
    private Combo m_nodeIds;
    private Combo m_coreIds;
        
    private volatile ProgressUpdater m_progressUpdater;

	public ControlView() {
		if (DEBUG) System.out.printf("Tree()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
		
		m_progressUpdater = null;
	}

	public void createPartControl(Composite parent) {
	    if (DEBUG) System.out.printf("Control: createPartControl\n");
	    m_parent = parent;
	    m_display = m_parent.getDisplay();

	    ExpandBar bar = new ExpandBar(m_parent, SWT.V_SCROLL);

	    createControlBar(bar);
	    createStatusBar(bar);
	    createSettingsBar(bar);
	    bar.setSpacing(8);
	    
	    m_memoryUpdater = new MemoryUpdater(m_memoryGroup, m_memoryUsage);
	    m_memoryUpdater.start();
	}
	
	public void setFocus() {
		if (DEBUG) System.out.printf("Control: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Control: propertyChange: %s\n", event.getProperty());	
		
		if (event.getProperty().equals(Displayer.NOTIFY_DISPLAY_ROUTE)) {
			setDone("Done");
			updateCurrentRoute();
			checkUnresolvedPaths();
		} else if (event.getProperty().equals(Displayer.NOTIFY_LOAD_EXECUTABLE)) {
			
			createXeObject();
            populateFunctionList();			
	        openEditors();
	        setDone(STATUS_LOADED);
	        m_progress.setText("0");
	        updateState();
	        updateCurrentRoute();
		} else if (event.getProperty().equals(Displayer.NOTIFY_ANALYSE_PATHS)) {
			m_engineStatus.setText(STATUS_BUSY);
			m_progressUpdater = new ProgressUpdater(XtaEngine.getEngine(), m_progress);
			m_progressUpdater.start();
	        m_currentRoute.setText("");
		} else if (event.getProperty().equals(Displayer.NOTIFY_UPDATE_STATE)) {
	        updateState();
		} else if (event.getProperty().equals(Displayer.NOTIFY_SET_START_POINT)) {
			CodeReference startPoint = XtaDisplayer.getInstance().getStartPoint();
			m_startPoint.setText(startPoint.getRef());
		} else if (event.getProperty().equals(Displayer.NOTIFY_SET_END_POINT)) {
			CodeReference endPoint = XtaDisplayer.getInstance().getEndPoint();
			m_endPoint.setText(endPoint.getRef());
		} else if (event.getProperty().equals(Displayer.NOTIFY_SELECT_FUNCTION)) {
			String functionName = XtaDisplayer.getInstance().getNotifyArgument();
			setSelectedFunction(functionName);
		} else if (event.getProperty().equals(Displayer.NOTIFY_ERROR)) {
			setDone("Aborted");
			updateCurrentRoute();
			displayMessageBox("Error", "Analysis Unsuccessful\n(See error log for details)");
		}
	}
	
	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		if (DEBUG) System.out.printf("Control: selectionChanged\n");	
     }
	
	public void dispose() {
		if (m_memoryUpdater != null) {
			m_memoryUpdater.end();
			m_memoryUpdater = null;
		}
		if (m_progressUpdater != null) {
			m_progressUpdater.end();
			m_progressUpdater = null;
		}
	}
	
    public void executeLoad() {				
		if (DEBUG) System.out.printf("Control: executeLoad\n");	

		if (!m_engineStatus.getText().equals(STATUS_BUSY)) {
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
		} else {
			displayMessageBox("Error", "Cannot open file, engine currently busy");
		}
	}

	public void executeRunFunction() {
		if (DEBUG) System.out.printf("Control: executeRunFunction\n");	

		if (m_progressUpdater != null) {
			return;
		}

		String functionName = m_functions.getText();
		if (!functionName.equals("")) {
			RunEngineMethodFunc run = new RunEngineMethodFunc(functionName);
			run.start();
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
			RunEngineMethodPath run = new RunEngineMethodPath(startRef, endRef);
			run.start();
		}
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

	public void setNodeId(String string) {
        try {
        	int nodeId = Integer.parseInt(string); 
        	XtaEngine.getEngine().setNodeId(nodeId);
        } catch (NumberFormatException e) {
        }    	        
	}

	public void setCoreId(String string) {
        try {
        	int coreId = Integer.parseInt(string); 
        	XtaEngine.getEngine().setCoreId(coreId);
        } catch (NumberFormatException e) {
        }    	        
	}

	public void setResolve(boolean resolve) {
		XtaEngine.getEngine().setResolve(resolve);
	}
	
	private void updateState() {
		m_resolveExpressions.setSelection(XtaEngine.getEngine().getResolve());

		m_freqSpinner.setSelection(XtaEngine.getEngine().getFrequency());

		m_threadSpinner.setSelection(XtaEngine.getEngine().getNumThreads());

		updateNodeIds();
		updateCoreIds();
	}
	
	private void updateNodeIds() {
		m_nodeIds.removeAll();

		int selectedNodeId = XtaEngine.getEngine().getNodeId();
		int index = 0;
		for (Integer nodeId : XtaEngine.getEngine().getXeLoader().getNodeIds()) {
			m_nodeIds.add(String.format("%d", nodeId));
			
			if (nodeId == selectedNodeId)
				m_nodeIds.select(index);
			index++;
		}
	}

	private void updateCoreIds() {
		m_coreIds.removeAll();

		int selectedNodeId = XtaEngine.getEngine().getNodeId();
		int selectedCoreId = XtaEngine.getEngine().getCoreId();
		int index = 0;
		for (Integer coreId : XtaEngine.getEngine().getXeLoader().getCoreIds(selectedNodeId)) {
			m_coreIds.add(String.format("%d", coreId));
			
			if (coreId == selectedCoreId)
				m_coreIds.select(index);
			index++;
		}
	}

    private void createXeObject() {
		Engine engine = XtaEngine.getEngine();
		File xe = new File(engine.getXeLoader().getExecutable());
		if (xe.isFile()) {
			m_fileName.setText(xe.getName());
		} else {
			//TODO - catch this - shouldn't fail
		}
    }
    
    private void populateFunctionList() {
    	m_functions.removeAll();
    	XeObject xeObject = XtaEngine.getEngine().getXeObject();                    
        for (final Function function : xeObject.getFunctions()) {
           	m_functions.add(function.getText());
        }
    }

    private void setSelectedFunction(String functionName) {
    	for (int index = 0; index < m_functions.getItemCount(); index++) {
    		if (m_functions.getItem(index).equals(functionName)) {
    			m_functions.select(index);
    			break;
    		}
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
			
	private void cancelRun() {
		XtaEngine.getEngine().setAbort(true);
	}

	private void setDone(String status) {
        if (m_progressUpdater != null) {
        	m_progressUpdater.end();
        	m_progressUpdater = null;
        }
    	m_engineStatus.setText(status);
	}

	private void updateCurrentRoute() {
		XtaRouteInfo routeInfo = XtaDisplayer.getInstance().getRouteInfo();
		if (routeInfo != null) {
			Route route = routeInfo.getRoute();
			m_currentRoute.setText(route.getStatusString());
		} else {
		m_currentRoute.setText("No routes analysed.");
	}
	}
	
	private void checkUnresolvedPaths() {
		XtaRouteInfo routeInfo = XtaDisplayer.getInstance().getRouteInfo();
		if (routeInfo != null) {
			Route route = routeInfo.getRoute();
			int numUnresolvedPaths = route.unresolvedPaths().size();
			if (numUnresolvedPaths > 0) {
				Status status = new Status(IStatus.ERROR, "My Plug-in ID", 0,
						"Found " + numUnresolvedPaths + " unresolved path(s)", null);
		        ErrorDialog.openError(Display.getCurrent().getActiveShell(),
		            "Warning", null, status);
			}
		}		
	}
	
	private void displayMessageBox(String title, String message) {
		Status status = new Status(IStatus.ERROR, "My Plug-in ID", 0, message, null);
        ErrorDialog.openError(Display.getCurrent().getActiveShell(), title, null, status);	      				
	}		
	
	class RunEngineMethodFunc extends Thread {
        private String m_functionName;

        RunEngineMethodFunc(String functionName) {
            m_functionName = functionName;
        }

        public void run() {
            if (!XtaEngine.getEngine().analyseRoute(m_functionName))
            	XtaErrorLog.getInstance().addError(XtaEngine.getEngine().getErrorString());
        }
    }

    class RunEngineMethodPath extends Thread {
        private String m_startRef;
        private String m_endRef;

        RunEngineMethodPath(String startRef, String endRef) {
        	m_startRef = startRef;
        	m_endRef = endRef;
        }

        public void run() {
            if (!XtaEngine.getEngine().analyseRoute(m_startRef, m_endRef))
            	XtaErrorLog.getInstance().addError(XtaEngine.getEngine().getErrorString());
        }
    }

    class RunEngineMethodRef extends Thread {
        private String m_startRef;
        private String m_endRef;

        RunEngineMethodRef(String startRef, String endRef) {
            m_startRef = startRef;
            m_endRef = endRef;
        }

        public void run() {        	
            if (!XtaEngine.getEngine().analyseRoute(m_startRef, m_endRef))
            	XtaErrorLog.getInstance().addError(XtaEngine.getEngine().getErrorString());
        }
    }	

    class ProgressUpdater extends Thread {
        private Engine m_engine;
        private Label m_progress;
        private boolean m_active;

        public ProgressUpdater(Engine engine, final Label progress) {
            m_engine = engine;
            m_progress = progress;
        }

        public void end() {
        	m_active = false;
        }

        public void run() {
            m_active = true;
        	final Progress p = m_engine.getProgress();  
            while (m_active) {
            	m_display.asyncExec(new Runnable() {
        			public void run() {
        				if (m_active) {
        					m_progress.setText("" + p.getCurValue());
        				}
        			}});            	

            	try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        	m_display.asyncExec(new Runnable() {
    			public void run() {
    				if (m_active) {
    					m_progress.setText("" + p.getCurValue());
    				}
    			}});            	
        }
    }
    
    class MemoryUpdater extends Thread {
        private Group m_memoryGroup;
        private ProgressBar m_memoryUsage;
        private boolean m_active;

        public MemoryUpdater(final Group memoryGroup, final ProgressBar memoryUsage) {
        	m_memoryGroup = memoryGroup;
        	m_memoryUsage = memoryUsage;
        }

        public void end() {
        	m_active = false;
        }

        public void run() {
            m_active = true;
        	int refreshRate = 50;
        	int i = 0;
            while (m_active) {
            	if (i == 0) {
	            	m_display.asyncExec(new Runnable() {
	        			public void run() {
	        				System.gc();
	        				double max = Runtime.getRuntime().maxMemory();
	        				double total = Runtime.getRuntime().totalMemory();
	        				double free = Runtime.getRuntime().freeMemory();
	        				int usage = (int)(((total - free) / max) * 100);
	        				
	        				if (m_active) {
	        					m_memoryGroup.setText(String.format("Memory used %d%%", usage));
	        					m_memoryUsage.setSelection(usage);
	        				}
	        			}});
            	}

            	try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                i = (i + 1) % refreshRate;
            }
        }
    }
    
	private void createControlBar(ExpandBar bar) {
		Composite composite = getBarComposite(bar, 3);

	    createFileControl(composite);
	    createFunctionControl(composite);
	    createPathControl(composite);
	    
	    ExpandItem barItem = new ExpandItem(bar, SWT.NONE, 0);
	    barItem.setText("Xta Control");
	    barItem.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	    barItem.setControl(composite);
	    barItem.setExpanded(true);
	}

	private void createFileControl(Composite composite) {
		Group group = createGroup(composite, "File", 1, 2);
	    m_fileName = new Text(group, SWT.SINGLE);
	    m_fileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    m_fileName.setText("");
	    m_fileName.setEditable(false);

	    Button loadButton = new Button(composite, SWT.PUSH);
	    loadButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	    loadButton.setToolTipText("Open file");
	    loadButton.setImage(Icons.getIcon("load"));
	    loadButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				executeLoad();
			}			
		});
	}
	
	private void createFunctionControl(Composite composite) {
		Group group = createGroup(composite, "Function", 1, 2);
	    m_functions = new Combo(group, SWT.NONE | SWT.DROP_DOWN | SWT.READ_ONLY);
	    m_functions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

	    Button runButton = new Button(composite, SWT.PUSH);
	    runButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	    runButton.setToolTipText("Time selected function");
	    runButton.setImage(Icons.getIcon("run"));
	    runButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				executeRunFunction();
			}			
		});
	}
	
	private void createPathControl(Composite composite) {
		Group group = createGroup(composite, "Path", 2, 2);

		Group startGroup = createGroup(group, "From", 1, 1);
	    m_startPoint = new Text(startGroup, SWT.SINGLE | SWT.CENTER);
	    m_startPoint.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
	    Group endGroup = createGroup(group, "To", 1, 1);
	    m_endPoint = new Text(endGroup, SWT.SINGLE | SWT.CENTER);
	    m_endPoint.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	
	    Button findStartButton = new Button(group, SWT.PUSH);
	    findStartButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	    findStartButton.setToolTipText("Show start point");
	    findStartButton.setImage(Icons.getIcon("start16x16"));
	    findStartButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CodeReference startPoint = XtaDisplayer.getInstance().getStartPoint();
				XtaDisplayer.getInstance().notifyShow(startPoint);
			}
		});

	    Button findStopButton = new Button(group, SWT.PUSH);
	    findStopButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	    findStopButton.setToolTipText("Show stop point");
	    findStopButton.setImage(Icons.getIcon("stop16x16"));
	    findStopButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CodeReference startPoint = XtaDisplayer.getInstance().getEndPoint();
				XtaDisplayer.getInstance().notifyShow(startPoint);
			}
		});

	    Button timePathButton = new Button(composite, SWT.PUSH);
	    timePathButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	    timePathButton.setToolTipText("Time specified path");
	    timePathButton.setImage(Icons.getIcon("run"));
	    timePathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				executeRunPath();
			}
		});
	}

	private void createStatusBar(ExpandBar bar) {
		Composite composite = getBarComposite(bar, 3);

		Group engineGroup = createGroup(composite, "Engine status", 1, 2);
		m_engineStatus = new Label(engineGroup, SWT.SHADOW_NONE | SWT.CENTER);
		m_engineStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		m_engineStatus.setText(STATUS_EMPTY);

	    Button cancelButton = new Button(composite, SWT.PUSH);
	    cancelButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	    cancelButton.setToolTipText("Cancel");
	    cancelButton.setImage(Icons.getIcon("cancel"));
	    cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cancelRun();
			}
		});

		Group currentGroup = createGroup(composite, "Currently analysed", 1, 3);
	    m_currentRoute = new Text(currentGroup, SWT.SINGLE);
	    m_currentRoute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    m_currentRoute.setText("");
	    m_currentRoute.setEditable(false);

		Group progressGroup = createGroup(composite, "Blocks analysed", 1, 3);
	    m_progress = new Label(progressGroup, SWT.SHADOW_NONE | SWT.CENTER);
	    m_progress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    m_progress.setText("0");

		m_memoryGroup = createGroup(composite, "Memory used", 1, 3);
	    m_memoryUsage = new ProgressBar(m_memoryGroup, SWT.SMOOTH | SWT.HORIZONTAL);
	    m_memoryUsage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    m_memoryUsage.setMaximum(0);
	    m_memoryUsage.setMaximum(100);
	    m_memoryUsage.setSelection(0);

	    ExpandItem barItem = new ExpandItem(bar, SWT.NONE, 0);
	    barItem.setText("Status");
	    barItem.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	    barItem.setControl(composite);
	    barItem.setExpanded(true);
	}

	private void createSettingsBar(ExpandBar bar) {
		Composite composite = getBarComposite(bar, 2);

		Group resolveGroup = createGroup(composite, "Resolve expressions", 1, 2);
		m_resolveExpressions = new Button(resolveGroup, SWT.CHECK);
		m_resolveExpressions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		m_resolveExpressions.setSelection(XtaEngine.getEngine().getResolve());		
		m_resolveExpressions.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setResolve(m_resolveExpressions.getSelection());
			}			
		});
	 	
		Group nodeGroup = createGroup(composite, "Node", 1, 1);
	    m_nodeIds = new Combo(nodeGroup, SWT.NONE | SWT.DROP_DOWN | SWT.READ_ONLY);
	    m_nodeIds.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    m_nodeIds.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setNodeId(m_nodeIds.getText());
			}			
		});

		Group coreGroup = createGroup(composite, "Core", 1, 1);
	    m_coreIds = new Combo(coreGroup, SWT.NONE | SWT.DROP_DOWN | SWT.READ_ONLY);
	    m_coreIds.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    m_coreIds.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setCoreId(m_coreIds.getText());
			}			
		});

		Group freqGroup = createGroup(composite, "Frequency", 1, 1);
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

		Group threadsGroup = createGroup(composite, "Num threads", 1, 1);
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
		
		ExpandItem barItem = new ExpandItem(bar, SWT.NONE, 0);
	    barItem.setText("Settings");
	    barItem.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	    barItem.setControl(composite);
	    barItem.setExpanded(false);
	}

	private Group createGroup(Composite parent, String description, int numColumns, int span) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = numColumns;
	    group.setLayout(layout);
	    group.setText(description);
	    group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, span, 1));
	    return group;
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
