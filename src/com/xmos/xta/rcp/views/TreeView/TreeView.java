package com.xmos.xta.rcp.views.TreeView;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.XtaRouteInfo;
import com.xmos.xta.rcp.nodes.Node;
import com.xmos.xta.rcp.nodes.RouteNode;

public class TreeView extends ViewPart implements IPropertyChangeListener,
                                                  ISelectionListener,
                                                  IMenuListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.rcp.views.TreeView.Tree";
    
    private TreeViewer m_treeViewer;
    private Composite m_parent;
    private XtaRouteInfo m_routeInfo;

	public TreeView() {
		if (DEBUG) System.out.printf("Tree()\n");			
		XtaNotifier.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
	    if (DEBUG) System.out.printf("Tree: createPartControl\n");
	    m_parent = parent;
	    
	    m_treeViewer = new TreeViewer(m_parent, SWT.BORDER | SWT.VIRTUAL);

	    
	    getSite().setSelectionProvider(m_treeViewer);
	    getSite().getPage().addSelectionListener(this);

	    TreeViewContentProvider cp = new TreeViewContentProvider(m_treeViewer);
	    
		m_treeViewer.setContentProvider(cp);				
		m_treeViewer.setLabelProvider(new TreeViewLabelProvider());
		
		m_treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				XtaNotifier.getInstance().notifySetFocus(m_treeViewer.getSelection());
			}
		});
	 	
	    createContextMenus();
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Tree: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Tree: propertyChange: %s\n", event.getProperty());	
		
		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
			initialiseTree();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			clearTree();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE) ||
				   event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS)) {
			refreshTree();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_EXCLUDES)) {
			initialiseTree();
		} 
	}
	
    public void selectionChanged(IWorkbenchPart part, ISelection sel) {
    	if (sel.isEmpty())
    		return;
    	
    	if (m_routeInfo == null)
    		return;
    	
		if (sel instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)sel).getFirstElement();
            if (object instanceof Node) {
            	m_routeInfo.setSelectedNode((Node)object);
            	if (part != this) {
            		m_treeViewer.setSelection(sel, true);
            	}
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

	public TreeViewer getViewer() {
		return m_treeViewer;
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
		m_routeInfo = XtaNotifier.getInstance().getRouteInfo();
		
		// Create the magic 'hidden' node to allow the actual node to be seen
		// in the tree

		RouteNode treeRoot = new RouteNode("");
		treeRoot.addNode(m_routeInfo.getRouteNode());
		Node unresolvedNode = m_routeInfo.getUnresolvedNode();
		if (unresolvedNode.getChildren().size() > 0) {
			treeRoot.addNode(unresolvedNode);
		}
		
		
    	m_treeViewer.setInput(treeRoot);
		ColumnViewerToolTipSupport.enableFor(m_treeViewer, ToolTip.NO_RECREATE);

		m_treeViewer.expandToLevel(2);		

	}
	
	private void refreshTree() {
		m_treeViewer.refresh();
		
	}
	
	private void clearTree() {
		m_treeViewer.setInput(new RouteNode(""));
		m_treeViewer.refresh();
	}
}
