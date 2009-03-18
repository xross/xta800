package com.xmos.xta.rcp.views.StatusView;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Progress;
import com.xmos.xta.engine.Route;
import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.XtaRouteInfo;

public class StatusView extends ViewPart implements IPropertyChangeListener,
                                                     ISelectionListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.rcp.views.StatusView.Status";

    public static final String STATUS_EMPTY  = "Empty";
    public static final String STATUS_LOADED = "Loaded";
    public static final String STATUS_BUSY   = "Busy";
    public static final String STATUS_DONE   = "Done";
    
    private Display m_display;
    private Composite m_parent;

    private Text m_currentRoute;
    private Label m_engineStatus;
    private Label m_progress;

    private MemoryUpdater m_memoryUpdater;
    private Group m_memoryGroup;
    private ProgressBar m_memoryUsage;
    
    private volatile ProgressUpdater m_progressUpdater;

	public StatusView() {
		if (DEBUG) System.out.println("Status(" + this.hashCode() + "): new");			
		XtaNotifier.getInstance().addPropertyChangeListener(this);
		
		m_progressUpdater = null;
	}

	private void setBackground(Composite composite) {
	    composite.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
	}

	public void createPartControl(Composite parent) {
	    if (DEBUG) System.out.println("Control (" + this.hashCode() + "): createPartControl");
	    m_parent = parent;
	    m_display = m_parent.getDisplay();

		Composite composite = new Composite(parent, SWT.NONE);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = 3;
	    composite.setLayout(layout);
	    composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
	    setBackground(composite);

	    createStatusBar(composite);
	    
	    m_memoryUpdater = new MemoryUpdater(m_memoryGroup, m_memoryUsage);
	    m_memoryUpdater.start();
	    
	    if (XtaEngine.getEngine().getProgress().getState() != Progress.State.IDLE) {
	    	startAnalysis();
	    }
	}
	
	public void setFocus() {
		if (DEBUG) System.out.println("Status(" + this.hashCode() + "): setFocus");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.println("Status(" + this.hashCode() + ") propertyChange: " + event.getProperty());	
		
		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
	        m_memoryUpdater.setFastRefresh(false);
			setDone(STATUS_DONE);
			updateCurrentRoute();
			checkTotalPaths();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {			
	        setDone(STATUS_LOADED);
	        m_progress.setText("0");
	        updateCurrentRoute();
	        
		} else if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_BEGIN)) {
			startAnalysis();
	        
		} else if (event.getProperty().equals(Notifier.NOTIFY_ERROR)) {
	        m_memoryUpdater.setFastRefresh(false);
			updateCurrentRoute();
			setDone(STATUS_DONE);

		}
	}
	
	private void startAnalysis() {
        m_memoryUpdater.setFastRefresh(true);
		m_engineStatus.setText(STATUS_BUSY);
		m_progressUpdater = new ProgressUpdater(XtaEngine.getEngine(), m_progress);
		m_progressUpdater.start();
        m_currentRoute.setText("");
        m_currentRoute.setToolTipText("");
	}

	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		if (DEBUG) System.out.println("Status(" + this.hashCode() + "): selectionChanged");	
     }
	
	public void dispose() {
		if (m_memoryUpdater != null) {
			m_memoryUpdater.end();
			m_memoryUpdater = null;
		}
		if (m_progressUpdater != null) {
			m_progressUpdater.end(true);
			m_progressUpdater = null;
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

	private void setDone(String status) {
        if (m_progressUpdater != null) {
        	m_progressUpdater.end(false);
        	m_progressUpdater = null;
        }
    	m_engineStatus.setText(status);
	}

	private void updateCurrentRoute() {
		XtaRouteInfo routeInfo = XtaNotifier.getInstance().getRouteInfo();
		if ((routeInfo != null) && (routeInfo.getRoute() != null)) {
			Route route = routeInfo.getRoute();
			String routeStatus = route.getDescription();
			m_currentRoute.setText(routeStatus);
			m_currentRoute.setToolTipText(routeStatus);
		} else {
			m_currentRoute.setText("No routes analysed.");
			m_currentRoute.setToolTipText("");
		}
	}
	
	private void checkTotalPaths() {
		XtaRouteInfo routeInfo = XtaNotifier.getInstance().getRouteInfo();
		if (routeInfo != null) {
			Route route = routeInfo.getRoute();
			int numUnresolvedPaths = route.getUnresolvedPaths().size();
			if (numUnresolvedPaths == 0 && route.getCompletedPaths().size() == 0) {
				Status status = new Status(IStatus.ERROR, "My Plug-in ID", 0,
						"No complete paths found, may have found an infinite loop", null);
		        ErrorDialog.openError(Display.getCurrent().getActiveShell(),
		            "Warning", null, status);
			}
		}		
	}

	class ProgressUpdater extends Thread {
        private Engine m_engine;
        private Label m_progress;
        private boolean m_active;
        private boolean m_exited;

        public ProgressUpdater(Engine engine, final Label progress) {
            m_engine = engine;
            m_progress = progress;
        }

        public void end(boolean exited) {
        	m_exited = exited;
        	m_active = false;
        }

        public void run() {
            m_active = true;
        	final Progress p = m_engine.getProgress();  
            while (m_active) {
            	m_display.asyncExec(new Runnable() {
        			public void run() {
        				if (!m_exited) {
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
    				if (!m_exited) {
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
	        					m_memoryGroup.setText(String.format("Host memory used %d%%", usage));
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
    
	private void createStatusBar(Composite composite) {
		
		Group engineGroup = createGroup(composite, "Engine status", 1, 3);
		m_engineStatus = new Label(engineGroup, SWT.SHADOW_NONE | SWT.CENTER);
		m_engineStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		m_engineStatus.setText(STATUS_EMPTY);
		m_engineStatus.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));

		Group currentGroup = createGroup(composite, "Currently analysed", 1, 3);
	    m_currentRoute = new Text(currentGroup, SWT.SINGLE);
	    m_currentRoute.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    m_currentRoute.setText("");
	    m_currentRoute.setEditable(false);
	    m_currentRoute.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));

		Group progressGroup = createGroup(composite, "Blocks analysed", 1, 3);
	    m_progress = new Label(progressGroup, SWT.SHADOW_NONE | SWT.CENTER);
	    m_progress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    int status = XtaEngine.getEngine().getProgress().getCurValue();
	    m_progress.setText(Integer.toString(status));
	    m_progress.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));

		m_memoryGroup = createGroup(composite, "Memory used", 1, 3);
	    m_memoryUsage = new ProgressBar(m_memoryGroup, SWT.SMOOTH | SWT.HORIZONTAL);
	    m_memoryUsage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    m_memoryUsage.setMaximum(0);
	    m_memoryUsage.setMaximum(100);
	    m_memoryUsage.setSelection(0);
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
