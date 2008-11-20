package com.xmos.xtagui.views;

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
import org.eclipse.swt.widgets.Display;
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
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Route;
import com.xmos.xtagui.XtaEngine;
import com.xmos.xtagui.XtaErrorLog;
import com.xmos.xtagui.XtaFileFinder;
import com.xmos.xtagui.XtaRouteInfo;
import com.xmos.xtagui.XtaDisplayer;
import com.xmos.xtagui.editors.Sources;
import com.xmos.xtagui.editors.SourcesInput;
import com.xmos.xtagui.nodes.BlockNode;
import com.xmos.xtagui.nodes.LoopNode;
import com.xmos.xtagui.nodes.Node;
import com.xmos.xtagui.nodes.PathNode;
import com.xmos.xtagui.nodes.RouteNode;

public class TreeView extends ViewPart implements IPropertyChangeListener,
                                                  ISelectionListener,
                                                  IMenuListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xtagui.views.Tree";

    private TreeViewer m_treeViewer;
    private Display m_display;
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
	    m_display = parent.getDisplay();	    
		
	    m_treeViewer = new TreeViewer(parent);		
	    getSite().setSelectionProvider(m_treeViewer);

	    m_functions = new TreeViewFunctionCombo("com.xmos.xtagui.functions.combo");
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
        if (sel instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)sel).getFirstElement();
            if (object instanceof Node) {
                // Do selection updates here..
            }
        }
     }	            		         	        
	
	public void menuAboutToShow(IMenuManager manager) {				
		ISelection selection = m_treeViewer.getSelection();
        if (selection instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)selection).getFirstElement();
            if (object instanceof Node) {
				((Node)object).createContextMenu(manager, this);
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
	
	public void setAllInstances(boolean allInstances) {		
	}

	public void setAbsoluteCount(boolean allInstances) {		
	}
	
	public int setNumIterations(int numIterations) {
		return 0;
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
    	XeObject xeObject = XtaEngine.getEngine().getXeObject();                    
        for (Function function : xeObject.getFunctions()) {
        	m_functions.addEntry(function.getText());
        }
    }
    
    private void openEditors() {
		IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
		try {
			IEditorInput sourcesInput = new SourcesInput();
			page.openEditor(sourcesInput, Sources.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}    	
    }
	
	private Node createHierarchy() {
		Route route = m_routeInfo.getRoute();
        RouteNode routeNode = new RouteNode(route);
        for (Block block : route.completePaths()) {
        	routeNode.addNode(createPath(block, routeNode, true));
        }		
        
        if (DEBUG) {
        	displayHierarchy(routeNode, 0);
        }                
		return routeNode;
	}
	
	private PathNode createPath(Block block, Node parent, boolean complete) {
        PathNode pathNode = new PathNode(block, parent, complete);
                
        for (Block pathBlock : block.getPathBlocks()) {            
            if (pathBlock.numLoopPaths() > 0) {
                LoopNode loopNode = new LoopNode(pathBlock, pathNode);
                pathNode.addNode(loopNode);
                
                for (Block loopPath : pathBlock.getLoopPaths()) {
                	loopNode.addNode(createPath(loopPath, loopNode, false));                    
                }
            }
        	pathNode.addNode(new BlockNode(pathBlock, pathNode));            
        }
        block.doneWithPathBlocks();
        return pathNode;
	}
	
	private void displayHierarchy(Node node, int indent) {
		printIndent(indent);
		System.out.printf("%s\n", node.getTypeName());
		for (Node subNode : node.getNodes()) {
			displayHierarchy(subNode, indent + 4);
		}
	}
	
	private void printIndent(int indent) {
		for (int i = 0; i < indent; ++i) {
			System.out.printf(" ");
		}
	}
	
	private void createContextMenus() {
	    Menu m_contextMenu;
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(this);
		m_contextMenu = menuMgr.createContextMenu(m_treeViewer.getTree());
		m_treeViewer.getTree().setMenu(m_contextMenu);		
	}
	
	private void initialiseTree() {
		m_routeInfo = XtaDisplayer.getInstance().getDisplayRouteInfo();										
		m_display.asyncExec(new Runnable() {
			public void run() {
				m_treeViewer.setContentProvider(new TreeViewContentProvider());				
				m_treeViewer.setLabelProvider(new TreeViewLabelProvider());
				m_treeViewer.setInput(createHierarchy());
				m_treeViewer.expandAll();
				ColumnViewerToolTipSupport.enableFor(m_treeViewer, ToolTip.NO_RECREATE);
			}});					    		
	}
	
	private void refreshTree() {
		m_display.asyncExec(new Runnable() {
			public void run() {
    			m_treeViewer.refresh();
			}});					            	   		
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
