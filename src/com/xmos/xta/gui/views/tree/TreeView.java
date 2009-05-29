package com.xmos.xta.gui.views.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.Route;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.gui.Broadcaster;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.GuiRouteInfo;
import com.xmos.xta.gui.nodes.HiddenNode;
import com.xmos.xta.gui.nodes.Node;

public class TreeView extends ViewPart implements IPropertyChangeListener,
												  ISelectionListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.gui.views.tree.Tree";

    private TreeViewer m_selectedRouteViewer;
    private TreeViewer m_routesViewer;
    private Composite m_parent;
    private GuiRouteInfo m_routeInfo;

	public TreeView() {
		if (DEBUG) System.out.printf("Tree()\n");			
		GuiEngine.addBroadcastListener(this);
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

		m_routeInfo = GuiEngine.getRouteInfo();
		getSite().getPage().addSelectionListener(this);
		
		initialiseTrees();
	}

	private void createTreeViewer(Sash sash, Composite parent) {
	    m_selectedRouteViewer = new TreeViewer(parent);
	    FormData data = new FormData();
	    data.top = new FormAttachment(0, 0);
	    data.bottom = new FormAttachment(sash, 0);
	    data.left = new FormAttachment(0, 0);
	    data.right = new FormAttachment(100, 0);
	    m_selectedRouteViewer.getControl().setLayoutData(data);
	    
	    getSite().setSelectionProvider(m_selectedRouteViewer);

		m_selectedRouteViewer.setContentProvider(new TreeViewContentProvider());				
		m_selectedRouteViewer.setLabelProvider(new TreeViewLabelProvider());
		
		m_selectedRouteViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				GuiEngine.setFocus(m_selectedRouteViewer.getSelection());
				GuiEngine.broadcast(Broadcaster.BROADCAST_SET_FOCUS, "");
			}
		});
		createTreeContextMenus();
	}

	private void createRouteViewer(Sash sash, Composite parent) {
		m_routesViewer = new TreeViewer(parent);
		FormData data = new FormData();
	    data.top = new FormAttachment(sash, 0);
	    data.bottom = new FormAttachment(100, 0);
	    data.left = new FormAttachment(0, 0);
	    data.right = new FormAttachment(100, 0);
	    m_routesViewer.getControl().setLayoutData(data);
	    
	    m_routesViewer.setContentProvider(new TreeViewContentProvider());
	    m_routesViewer.setLabelProvider(new TreeViewLabelProvider());

	    createRouteContextMenus();
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Tree: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Tree: propertyChange: %s\n", event.getProperty());	
		
		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
			initialiseTrees();
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				getActivePage().showView(TreeView.ID);
			} catch (PartInitException e) {}
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			m_routesViewer.setInput(new HiddenNode());
			m_routesViewer.refresh();
			m_selectedRouteViewer.setInput(new HiddenNode());
			m_selectedRouteViewer.refresh();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			m_selectedRouteViewer.setInput(new HiddenNode());
			m_selectedRouteViewer.refresh();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE) ||
				   event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS)) {
			refreshTree();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE)) {
			initialiseSelectedRouteView();

		} else if (event.getProperty().equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
			initialiseTrees();

		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SELECT_PATHS)) {
			selectPaths();

		}
	}
	
	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
    	if (sel.isEmpty())
    		return;
    	
		List<Node> selectedNodes = new ArrayList<Node>();
		if (sel instanceof IStructuredSelection) {
			for (Object object : ((IStructuredSelection)sel).toArray()) {
				if (object instanceof Node) {
					selectedNodes.add((Node)object);
				}
			}
		}
		m_routeInfo.setSelectedNodes(selectedNodes);
    	if (part != this) {
    		m_selectedRouteViewer.setSelection(sel, true);
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
						m_selectedRouteViewer.expandAll();
					}
				});
				manager.add(new Action("collapse all") {
					public void run() {
						m_selectedRouteViewer.collapseAll();
						m_selectedRouteViewer.expandToLevel(2);
					}
				});

				ISelection selection = m_selectedRouteViewer.getSelection();
		        if (selection instanceof IStructuredSelection) {	            
		            Object object = ((IStructuredSelection)selection).getFirstElement();
		            if (object instanceof Node) {
						((Node)object).createContextMenu(m_parent.getShell(), manager);
		            }
		        }				
			}
		});
		
		contextMenu = menuMgr.createContextMenu(m_selectedRouteViewer.getTree());
		m_selectedRouteViewer.getTree().setMenu(contextMenu);
		
		m_selectedRouteViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection sel = event.getSelection();
				if (sel instanceof IStructuredSelection) {
					List<Node> selectedNodes = new ArrayList<Node>();
					for (Object object : ((IStructuredSelection)sel).toArray()) {
						if (object instanceof Node) {
							selectedNodes.add((Node)object);
						}
					}
					m_routeInfo.setSelectedNodes(selectedNodes);
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
				final ISelection selection = m_routesViewer.getSelection();
				
				if (((IStructuredSelection)selection).size() > 0) {
	            	manager.add(new Action("delete") {
						public void run() {
				            for (Object object : ((IStructuredSelection)selection).toList()) {

// TODO
//				            	GuiEngine.getEngine().removeRoute(((RouteNode)object).getId());
				            }
						}
					});
				}

				if (m_routeInfo.getRoutes().size() > 0) {
	            	manager.add(new Action("delete all") {
						public void run() {
							GuiEngine.getEngine().removeAllRoutes();
						}
					});
				}
				
				if (((IStructuredSelection)selection).size() == 1) {
			        if (selection instanceof IStructuredSelection) {	            
			            Object object = ((IStructuredSelection)selection).getFirstElement();
// TODO
//			            if (object instanceof RouteNode) {
//			            	((Node)object).createContextMenu(m_parent.getShell(), manager);
//			            }
			        }
				}
			}
		});
		
		contextMenu = menuMgr.createContextMenu(m_routesViewer.getTree());
		m_routesViewer.getTree().setMenu(contextMenu);		

		m_routesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					Engine engine = GuiEngine.getEngine();
					int currentSelectedId = engine.getSelectedRouteId();
					int newId = Route.INVALID_ROUTE_ID;

					if (((IStructuredSelection)selection).size() == 1) {
			            Object object = ((IStructuredSelection)selection).getFirstElement();
// TODO
//			            if (object instanceof RouteNode) {
//			            	newId = ((RouteNode)object).getId();
//			            }
					}
					
					if (newId != currentSelectedId) {
						engine.setSelectedRoute(newId);
	            	}
		        }
			}
		});
	}

	private void initialiseTrees() {
		initialiseSelectedRouteView();
		initialiseRoutesView();
	}
	
	private void initialiseSelectedRouteView() {
		// Create the magic 'hidden' node to allow the actual node to be seen
		// in the tree
		Node treeRoot = new HiddenNode();
		Node routeNode = m_routeInfo.getRouteNode();
		if (routeNode != null)
			treeRoot.addNode(routeNode);

		Node unresolvedNode = m_routeInfo.getUnresolvedNode();
		if ((unresolvedNode!= null) && (unresolvedNode.getChildren().size() > 0)) {
			treeRoot.addNode(unresolvedNode);
		}
    	m_selectedRouteViewer.setInput(treeRoot);
		ColumnViewerToolTipSupport.enableFor(m_selectedRouteViewer, ToolTip.NO_RECREATE);
		m_selectedRouteViewer.expandToLevel(2);
	}
	
	private void initialiseRoutesView() {
		// Create hidden node in the route node
		Node routeRoot = new HiddenNode();
		for (Node node : m_routeInfo.getRoutes()) {
			routeRoot.addNode(node);
		}
		// Maintain the same selection
		m_routesViewer.setInput(routeRoot);
		ColumnViewerToolTipSupport.enableFor(m_routesViewer, ToolTip.NO_RECREATE);
		m_routesViewer.expandToLevel(2);
		
		int selectedRouteId = GuiEngine.getEngine().getSelectedRouteId();
		setRouteTreeSelection(routeRoot, selectedRouteId);
	}
	
	private boolean setRouteTreeSelection(Node root, int routeId) {
		for (Node node : root.getChildren()) {
			if (node.getId() == routeId) {
				ISelection selection = new StructuredSelection(node);
				m_routesViewer.setSelection(selection);
				return true;
			}
			if (setRouteTreeSelection(node, routeId))
				return true;
		}
		return false;
	}

	private void refreshTree() {
		m_selectedRouteViewer.refresh();
		m_routesViewer.refresh();
		
	}

	private void selectPaths() {
		Reference codePoint = GuiEngine.getCodePoint();
		Engine engine = GuiEngine.getEngine();
		if (codePoint.resolve(engine, engine.getNodeId(), engine.getCoreId())) {
			int pc = codePoint.getPc();
			List<Node> selectedPaths = new ArrayList<Node>();

			// Add the complete paths hitting the pc
			Node routeNode = m_routeInfo.getRouteNode();
			if (routeNode != null) {
				for (Node child : routeNode.getChildren()) {
					Set<Integer> pcs = child.getPcsCovered(false);
					if (pcs.contains(pc)) {
						selectedPaths.add(child);
					}
				}
			}
			
			// Add the unresolved paths hitting the pc
			Node unresolvedNode = m_routeInfo.getUnresolvedNode();
			if (unresolvedNode != null) {
				for (Node child : unresolvedNode.getChildren()) {
					Set<Integer> pcs = child.getPcsCovered(false);
					if (pcs.contains(pc)) {
						selectedPaths.add(child);
					}
				}
			}
			
			m_selectedRouteViewer.setSelection(new StructuredSelection(selectedPaths));
		}
	}
}
