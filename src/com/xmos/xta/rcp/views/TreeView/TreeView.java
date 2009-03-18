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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.Route;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.XtaRouteInfo;
import com.xmos.xta.rcp.nodes.HiddenNode;
import com.xmos.xta.rcp.nodes.Node;
import com.xmos.xta.rcp.nodes.RouteNode;

public class TreeView extends ViewPart implements IPropertyChangeListener,
												  ISelectionListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.rcp.views.TreeView.Tree";

    private TreeViewer m_treeViewer;
    private TreeViewer m_routeViewer;
    private Composite m_parent;
    private XtaRouteInfo m_routeInfo;
    private int m_selectedRouteId;

	public TreeView() {
		if (DEBUG) System.out.printf("Tree()\n");			
		XtaNotifier.getInstance().addPropertyChangeListener(this);
		m_selectedRouteId = Route.INVALID_ROUTE_ID;
	}

	public void createPartControl(final Composite parent) {
	    if (DEBUG) System.out.printf("Tree: createPartControl\n");
	    m_parent = parent;
		
	    parent.setLayout(new FormLayout());

	    final Sash sash = new Sash(parent, SWT.HORIZONTAL);
	    final FormData sashData = new FormData();
	    sashData.top = new FormAttachment(70, 0); 
	    sashData.left = new FormAttachment(0, 0); 
	    sashData.right = new FormAttachment(100, 0); 
	    sash.setLayoutData(sashData);
	    
	    createTreeViewer(sash, parent);
	    createRouteViewer(sash, parent);

	    // Control the sash re-sizing
	    final int limit = 40;
	    sash.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	          Rectangle sashRect = sash.getBounds();
	          Rectangle shellRect = parent.getClientArea();
	          int bottom = shellRect.height - sashRect.height - limit;
	          e.y = Math.max(Math.min(e.y, bottom), limit);
	          if (e.y != sashRect.y) {
	            sashData.top = new FormAttachment(0, e.y);
	            parent.layout();
	          }
	        }
	      });

		m_routeInfo = XtaNotifier.getInstance().getRouteInfo();
		getSite().getPage().addSelectionListener(this);
		
		m_selectedRouteId = XtaNotifier.getInstance().getSelectedRouteId();
		initialiseTrees();
	}

	private void createTreeViewer(Sash sash, Composite parent) {
	    m_treeViewer = new TreeViewer(parent, SWT.VIRTUAL | SWT.BORDER);
	    FormData data = new FormData();
	    data.top = new FormAttachment(0, 0);
	    data.bottom = new FormAttachment(sash, 0);
	    data.left = new FormAttachment(0, 0);
	    data.right = new FormAttachment(100, 0);
	    m_treeViewer.getControl().setLayoutData(data);
	    
	    getSite().setSelectionProvider(m_treeViewer);

		m_treeViewer.setContentProvider(new TreeViewContentProvider(m_treeViewer));				
		m_treeViewer.setLabelProvider(new TreeViewLabelProvider());
		
		m_treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				XtaNotifier.getInstance().notifySetFocus(m_treeViewer.getSelection());
			}
		});
		createTreeContextMenus();
	}

	private void createRouteViewer(Sash sash, Composite parent) {
		m_routeViewer = new TreeViewer(parent, SWT.VIRTUAL | SWT.BORDER);
		FormData data = new FormData();
	    data.top = new FormAttachment(sash, 0);
	    data.bottom = new FormAttachment(100, 0);
	    data.left = new FormAttachment(0, 0);
	    data.right = new FormAttachment(100, 0);
	    m_routeViewer.getControl().setLayoutData(data);
	    
	    m_routeViewer.setContentProvider(new TreeViewContentProvider(m_routeViewer));				
	    m_routeViewer.setLabelProvider(new TreeViewLabelProvider());

	    createRouteContextMenus();
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Tree: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Tree: propertyChange: %s\n", event.getProperty());	
		
		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
			m_selectedRouteId = XtaNotifier.getInstance().getSelectedRouteId();
			initialiseTrees();
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				getActivePage().showView(TreeView.ID);
			} catch (PartInitException e) {}
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			clearTree();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE) ||
				   event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS)) {
			refreshTree();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_EXCLUDES) || 
				   event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE) || 
				   event.getProperty().equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
			initialiseTrees();

		} 
	}
	
    public void selectionChanged(IWorkbenchPart part, ISelection sel) {
    	if (sel.isEmpty())
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
	
	private void createTreeContextMenus() {
	    Menu contextMenu;
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);

		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {				
				manager.add(new Action("expand all") {
					public void run() {
						m_treeViewer.expandAll();
					}
				});
				manager.add(new Action("collapse all") {
					public void run() {
						m_treeViewer.collapseAll();
						m_treeViewer.expandToLevel(2);
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
		});
		
		contextMenu = menuMgr.createContextMenu(m_treeViewer.getTree());
		m_treeViewer.getTree().setMenu(contextMenu);
		
		m_treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection sel = event.getSelection();
		    	if (sel.isEmpty())
		    		return;
		    	
				if (sel instanceof IStructuredSelection) {	            
		            Object object = ((IStructuredSelection)sel).getFirstElement();
		            
	            	if (object instanceof Node) {
		            	m_routeInfo.setSelectedNode((Node)object);
	            	}
		        }
			}
		});
	}
	
	private void createRouteContextMenus() {
	    Menu contextMenu;
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);

		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {				
				if (m_selectedRouteId != Route.INVALID_ROUTE_ID) {
	            	manager.add(new Action("delete") {
						public void run() {
							if (m_selectedRouteId != Route.INVALID_ROUTE_ID)
								XtaEngine.getEngine().removeRoute(m_selectedRouteId);
						}
					});
	            	manager.add(new Action("delete all") {
						public void run() {
							XtaEngine.getEngine().removeAllRoutes();
						}
					});
				}
				
				ISelection selection = m_routeViewer.getSelection();
		        if (selection instanceof IStructuredSelection) {	            
		            Object object = ((IStructuredSelection)selection).getFirstElement();
		            if (object instanceof RouteNode) {
		            	((Node)object).createContextMenu(m_parent.getShell(), manager);
		            }
		        }				
			}
		});
		
		contextMenu = menuMgr.createContextMenu(m_routeViewer.getTree());
		m_routeViewer.getTree().setMenu(contextMenu);		

		m_routeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection sel = event.getSelection();
		    	if (sel.isEmpty()) {
		    		return;
		    	
				} else if (sel instanceof IStructuredSelection) {	            
		            Object object = ((IStructuredSelection)sel).getFirstElement();
		            if (object instanceof RouteNode) {
		            	int newId = ((RouteNode)object).getId();
						if (newId != m_selectedRouteId) {
		            		m_selectedRouteId = newId;

							XtaNotifier.getInstance().notify(
		            				Notifier.NOTIFY_SELECT_ROUTE,
		            				Integer.toString(m_selectedRouteId));
		            	}
		            }

		        }
			}
		});
	}

	private void initialiseTrees() {
		// Create the magic 'hidden' node to allow the actual node to be seen
		// in the tree
		Node treeRoot = new HiddenNode();
		treeRoot.addNode(m_routeInfo.getRouteNode());

		Node unresolvedNode = m_routeInfo.getUnresolvedNode();
		if ((unresolvedNode!= null) && (unresolvedNode.getChildren().size() > 0)) {
			treeRoot.addNode(unresolvedNode);
		}
    	m_treeViewer.setInput(treeRoot);
		ColumnViewerToolTipSupport.enableFor(m_treeViewer, ToolTip.NO_RECREATE);
		m_treeViewer.expandToLevel(2);

		// Create hidden node in the route node
		Node routeRoot = new HiddenNode();
		for (Node routeNode : m_routeInfo.getRoutes()) {
			routeRoot.addNode(routeNode);
		}
		// Maintain the same selection
		m_routeViewer.setInput(routeRoot);
		ColumnViewerToolTipSupport.enableFor(m_routeViewer, ToolTip.NO_RECREATE);
		m_routeViewer.expandToLevel(2);
		setRouteTreeSelection(routeRoot, m_selectedRouteId);
	}
	
	private boolean setRouteTreeSelection(Node root, int routeId) {
		for (Node node : root.getChildren()) {
			if (node.getId() == routeId) {
				ISelection selection = new StructuredSelection(node);
				m_routeViewer.setSelection(selection);
				return true;
			}
			if (setRouteTreeSelection(node, routeId))
				return true;
		}
		return false;
	}

	private void refreshTree() {
		m_treeViewer.refresh();
		m_routeViewer.refresh();
		
	}
	
	private void clearTree() {
		m_treeViewer.setInput(new HiddenNode());
		m_treeViewer.refresh();

		m_routeViewer.setInput(new HiddenNode());
		m_routeViewer.refresh();
	}
}
