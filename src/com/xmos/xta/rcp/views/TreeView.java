package com.xmos.xta.rcp.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xeobject.Function;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.rcp.XtaDisplayer;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.XtaErrorLog;
import com.xmos.xta.rcp.XtaFileFinder;
import com.xmos.xta.rcp.XtaRouteInfo;
import com.xmos.xta.rcp.editors.SourcesEditor;
import com.xmos.xta.rcp.editors.SourcesEditorInput;
import com.xmos.xta.rcp.nodes.Node;
import com.xmos.xta.rcp.nodes.RouteNode;

public class TreeView extends ViewPart implements IPropertyChangeListener,
                                                  ISelectionListener,
                                                  IMenuListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.rcp.views.Tree";

    private TreeViewer m_treeViewer;
    private Composite m_parent;
    private XtaRouteInfo m_routeInfo;
    private TreeViewFunctionCombo m_functions;

    private boolean m_exhustive;
    private boolean m_fnops;
    
	public TreeView() {
		if (DEBUG) System.out.printf("Tree()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
		
		m_exhustive = false;
		m_fnops = true;
	}

	public void createPartControl(Composite parent) {
	    if (DEBUG) System.out.printf("Tree: createPartControl\n");
	    m_parent = parent;
		
	    m_treeViewer = new TreeViewer(m_parent);		
	    getSite().setSelectionProvider(m_treeViewer);
	    getSite().getPage().addSelectionListener(this);

		m_treeViewer.setContentProvider(new TreeViewContentProvider());				
		m_treeViewer.setLabelProvider(new TreeViewLabelProvider());
	    
	    m_functions = new TreeViewFunctionCombo("com.xmos.xta.rcp.functions.combo");
	    getViewSite().getActionBars().getToolBarManager().add(m_functions);

	    createContextMenus();
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Tree: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Tree: propertyChange: %s\n", event.getProperty());	
		
		if (event.getProperty().equals("DisplayRoute")) {
			initialiseTree();
		} else if (event.getProperty().equals("LoadExecutable")) {
			createXeObject();
            populateFunctionList();			
	        openEditors();
		} else if (event.getProperty().equals("UpdateState")) {
			refreshTree();
		}
	}
	
    public void selectionChanged(IWorkbenchPart part, ISelection sel) {
    	if ((part == this) || sel.isEmpty())
    		return;
    	
		if (sel instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)sel).getFirstElement();
            if (object instanceof Node) {
            	m_routeInfo.setSelectedNode((Node)object);
        		m_treeViewer.setSelection(sel, true);
            }
        }
     }
	
	public void menuAboutToShow(IMenuManager manager) {				
		manager.add(new Action("expand all") {
			public void run() {
				getViewer().expandAll();
			}
		});
		manager.add(new Action("collapse all") {
			public void run() {
				getViewer().collapseAll();
			}
		});

		ISelection selection = m_treeViewer.getSelection();
        if (selection instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)selection).getFirstElement();
            if (object instanceof Node) {
				((Node)object).createContextMenu(m_parent.getShell(), manager);
            }
        }				
	}

    public void executeLoad() {				
		if (DEBUG) System.out.printf("Tree: executeLoad\n");	

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

	public void executeRun() {
		if (DEBUG) System.out.printf("Tree: executeRun\n");	

		Views.showView(TreeView.ID);
		
		String functionName = m_functions.getCurrentEntry();
		if (!functionName.equals("")) {
			RunEngineMethodFunc run = new RunEngineMethodFunc(functionName, m_exhustive);
			run.start();
		} else {
			XtaErrorLog.getInstance().addError("No function/path specified");
		}
	}
	
	public boolean getExhustive() {
		return m_exhustive;
	}

	public void setExhustive(boolean exhustive) {
		m_exhustive = exhustive;
	}

	public boolean getFnops() {
		return m_fnops;
	}

	public void setFnops(boolean fnops) {
		m_fnops = fnops;
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
	
	public TreeViewer getViewer() {
		return m_treeViewer;
	}
			
    private void createXeObject() {
		Engine engine = XtaEngine.getEngine();
		XeObject xeObject = new XeObject(engine.getXeLoader(), new XtaFileFinder());
		XtaEngine.getEngine().setXeObject(xeObject);                    
    }
    
    private void populateFunctionList() {
    	m_functions.clearEntries();
    	XeObject xeObject = XtaEngine.getEngine().getXeObject();                    
        for (final Function function : xeObject.getFunctions()) {
           	m_functions.addEntry(function.getText());
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
		clearTree();
    }
			
	private void createContextMenus() {
	    Menu contextMenu;
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(this);
		contextMenu = menuMgr.createContextMenu(m_treeViewer.getTree());
		m_treeViewer.getTree().setMenu(contextMenu);		
	}
	
	private void initialiseTree() {
		m_routeInfo = XtaDisplayer.getInstance().getRouteInfo();										
    	m_treeViewer.setInput(m_routeInfo.getRouteNode());
		ColumnViewerToolTipSupport.enableFor(m_treeViewer, ToolTip.NO_RECREATE);
	}
	
	private void refreshTree() {
		m_treeViewer.refresh();
	}
	
	private void clearTree() {
		m_treeViewer.setInput(new RouteNode());
		m_treeViewer.refresh();
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
}
