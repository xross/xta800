package com.xmos.xta.rcp.views;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Progress;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.references.CodeReference;
import com.xmos.xta.engine.references.CodeReferenceFactory;
import com.xmos.xta.rcp.XtaCorePlugin;
import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.XtaLog;
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

    private Text m_fileNameTextBox;
    private Text m_startPoint;
    private Text m_endPoint;
    private ModifyListener m_startPointListener;
    private ModifyListener m_endPointListener;
    
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
    
    private Button m_cancelButton;
        
    private volatile ProgressUpdater m_progressUpdater;

	public ControlView() {
		if (DEBUG) System.out.printf("Tree()\n");			
		XtaNotifier.getInstance().addPropertyChangeListener(this);
		
		m_progressUpdater = null;
		
		m_startPointListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
    		    CodeReference startPoint = CodeReferenceFactory.createCodeReference(m_startPoint.getText());
			    XtaNotifier.getInstance().notifySetStartPoint(startPoint);
			}			
		};
		m_endPointListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
			    CodeReference endPoint = CodeReferenceFactory.createCodeReference(m_endPoint.getText());
			    XtaNotifier.getInstance().notifySetEndPoint(endPoint);
			}			
		};
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
		
		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
	        m_memoryUpdater.setFastRefresh(false);
			setDone("Done");
			updateCurrentRoute();
			checkUnresolvedPaths();
			checkTotalPaths();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {			
	        openEditors();
	        setDone(STATUS_LOADED);
	        m_progress.setText("0");
	        m_functions.removeAll();	       	       
	        setText(m_startPoint, m_startPointListener, "", "");
	        setText(m_endPoint, m_endPointListener, "", "");
	        updateState();
	        updateCurrentRoute();
	        
		} else if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_BEGIN)) {
	        m_memoryUpdater.setFastRefresh(true);
			m_engineStatus.setText(STATUS_BUSY);
			m_progressUpdater = new ProgressUpdater(XtaEngine.getEngine(), m_progress);
			m_progressUpdater.start();
	        m_currentRoute.setText("");
	        m_currentRoute.setToolTipText("");
	        
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE)) {
	        updateState();
	        
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_START_POINT)) {
			CodeReference startPoint = XtaNotifier.getInstance().getStartPoint();					
			updateText(startPoint, m_startPoint, m_startPointListener);
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_END_POINT)) {
			CodeReference endPoint = XtaNotifier.getInstance().getEndPoint();
			updateText(endPoint, m_endPoint, m_endPointListener);
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_SELECT_FUNCTION)) {
			String functionName = XtaNotifier.getInstance().getNotifyArgument();
			setSelectedFunction(functionName);
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_EXCLUDES)) {
			updateState();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_ERROR)) {
	        m_memoryUpdater.setFastRefresh(false);
			setDone("Aborted");
			updateCurrentRoute();
			displayMessageBox("Error", "Error detected\n(See error tab for details)");
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
	        
	        //this sets the initial path
	        XtaCorePlugin plugin = XtaCorePlugin.getDefault();
	        
	        String lastDir = plugin.getPreferenceStore().getString("LastDir");
	        
	        fileDialog.setFilterPath(lastDir);
	        
	        String xeFileName = fileDialog.open();
	        
	        if (xeFileName == null)
	        	return;
	        
	        String path = new File(xeFileName).getParent();
	        plugin.getPreferenceStore().setValue("LastDir", path);

	        if (xeFileName != null) {
	        	if (DEBUG)
	        		System.out.printf("Control: File open: %s\n", xeFileName);
	        
	        	File xeFile = new File(xeFileName);
	        	
	        	try {
					xeFile = xeFile.getCanonicalFile();
				} catch (IOException e) {
					displayMessageBox("Error", "Cannot resolve file: " + xeFileName);
					return;
				}
        	
	        	Engine engine = XtaEngine.getEngine();
	        	boolean success = engine.loadExecutable(xeFile, new XtaFileFinder());
	        	if (success) {
	    			m_fileNameTextBox.setText(xeFile.getName());
	    			m_fileNameTextBox.setToolTipText(xeFileName);

	    			if (DEBUG)
	                    System.out.printf("Control: executeLoad, success: %d\n", success);
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
        	m_cancelButton.setEnabled(true);
			run.start();
		} else {
			XtaLog.getInstance().add(XtaLog.Level.ERROR, "No function/path specified");
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
			XtaLog.getInstance().add(XtaLog.Level.ERROR, "Please specify a start reference");
		} else if (endRef.equals("")) {
			XtaLog.getInstance().add(XtaLog.Level.ERROR, "Please specify an end reference");
		} else {
			RunEngineMethodPath run = new RunEngineMethodPath(startRef, endRef);
        	m_cancelButton.setEnabled(true);
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
        populateFunctionList();
		
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
    
	private void populateFunctionList() {
    	// Remember current function name to select it again if it exists
    	String currentFunction = m_functions.getText();
    	
    	m_functions.removeAll();
    	XeObject xeObject = XtaEngine.getEngine().getXeObject();
    	List<Function> functions = xeObject.getFunctions();
    	Collections.sort(functions);
        for (final Function function : functions) {
           	m_functions.add(function.getText());
        }
        setSelectedFunction(currentFunction);
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
    	m_cancelButton.setEnabled(false);
	}

	private void updateCurrentRoute() {
		XtaRouteInfo routeInfo = XtaNotifier.getInstance().getRouteInfo();
		if (routeInfo != null) {
			Route route = routeInfo.getRoute();
			String routeStatus = route.getStatusString();
			m_currentRoute.setText(routeStatus);
			m_currentRoute.setToolTipText(routeStatus);
		} else {
			m_currentRoute.setText("No routes analysed.");
			m_currentRoute.setToolTipText("");
		}
	}
	
	private void checkUnresolvedPaths() {
		XtaRouteInfo routeInfo = XtaNotifier.getInstance().getRouteInfo();
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
	private void checkTotalPaths() {
		XtaRouteInfo routeInfo = XtaNotifier.getInstance().getRouteInfo();
		if (routeInfo != null) {
			Route route = routeInfo.getRoute();
			int numUnresolvedPaths = route.unresolvedPaths().size();
			if (numUnresolvedPaths == 0 && route.completePaths().size() == 0) {
				Status status = new Status(IStatus.ERROR, "My Plug-in ID", 0,
						"No complete paths found, may have found an infinite loop", null);
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
            XtaEngine.getEngine().analyseRoute(m_functionName);
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
        	XtaEngine.getEngine().analyseRoute(m_startRef, m_endRef);
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
            XtaEngine.getEngine().analyseRoute(m_startRef, m_endRef);
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
    	private static final int SLOW_REFRESH_RATE = 1000;	
    	private static final int FAST_REFRESH_RATE = 50;

    	private Group m_memoryGroup;
        private ProgressBar m_memoryUsage;
        private boolean m_active;
        private int m_refreshRate;

        public MemoryUpdater(final Group memoryGroup, final ProgressBar memoryUsage) {
        	m_memoryGroup = memoryGroup;
        	m_memoryUsage = memoryUsage;
        	m_refreshRate = SLOW_REFRESH_RATE;
        }

        public void end() {
        	m_active = false;
        }

        public void run() {
            m_active = true;
        	int i = 0;
            while (m_active) {
            	if (i == 0) {
	            	m_display.asyncExec(new Runnable() {
	        			public void run() {
	        				//System.gc();
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

                i = (i + 1) % m_refreshRate;
            }
        }
        
        public void setFastRefresh(boolean fast) {
        	m_refreshRate = (fast) ? FAST_REFRESH_RATE : SLOW_REFRESH_RATE;
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
	    m_fileNameTextBox = new Text(group, SWT.SINGLE);
	    m_fileNameTextBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    m_fileNameTextBox.setText("");
	    m_fileNameTextBox.setEditable(false);

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
	    m_startPoint.addModifyListener(m_startPointListener);
		
	    Group endGroup = createGroup(group, "To", 1, 1);
	    m_endPoint = new Text(endGroup, SWT.SINGLE | SWT.CENTER);
	    m_endPoint.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    m_endPoint.addModifyListener(m_endPointListener);
	
	    Button findStartButton = new Button(group, SWT.PUSH);
	    findStartButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	    findStartButton.setToolTipText("Show start point in disassembly");
	    findStartButton.setImage(Icons.getIcon("start16x16"));
	    findStartButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CodeReference startPoint = XtaNotifier.getInstance().getStartPoint();
				XtaNotifier.getInstance().notifyShow(startPoint);
			}
		});

	    Button findStopButton = new Button(group, SWT.PUSH);
	    findStopButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	    findStopButton.setToolTipText("Show end point in disassembly");
	    findStopButton.setImage(Icons.getIcon("stop16x16"));
	    findStopButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CodeReference startPoint = XtaNotifier.getInstance().getEndPoint();
				XtaNotifier.getInstance().notifyShow(startPoint);
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

		m_cancelButton = new Button(composite, SWT.PUSH);
		m_cancelButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		m_cancelButton.setToolTipText("Cancel");
		m_cancelButton.setImage(Icons.getIcon("cancel"));
		m_cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cancelRun();
			}
		});
		m_cancelButton.setEnabled(false);

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

		Group resolveGroup = createGroup(composite, "Resolve", 1, 2);
		m_resolveExpressions = new Button(resolveGroup, SWT.CHECK);
		m_resolveExpressions.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
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
	
	private void updateText(CodeReference point, Text text, ModifyListener listener) {
		if (point != null) {
			if (point.getResolved() || point.getRef().equals("")) {		
		     	setText(text, listener, point.getRef(), point.getRef());
		    }
		} else {
			setText(text, listener, "", "");
		}		
	}	
	
	private void setText(Text text, ModifyListener listener, String str, String toolTip) {
		text.removeModifyListener(listener);
		text.setText(str);
		text.addModifyListener(listener);	
		text.setSelection(str.length());
		text.setToolTipText(toolTip);
	}
}
