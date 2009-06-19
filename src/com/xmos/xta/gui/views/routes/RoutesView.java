package com.xmos.xta.gui.views.routes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.PathNode;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.gui.Broadcaster;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.dialogs.Required;
import com.xmos.xta.gui.views.console.ConsoleView;
import com.xmos.xta.gui.views.routes.selected.SelectedRouteContentProvider;
import com.xmos.xta.gui.views.routes.selected.SelectedRouteLabelProvider;
import com.xmos.xta.gui.views.routes.selector.RouteNode;
import com.xmos.xta.gui.views.routes.selector.RouteSelectorContentProvider;
import com.xmos.xta.gui.views.routes.selector.RouteSelectorLabelProvider;

public class RoutesView extends ViewPart implements IPropertyChangeListener,
												    ISelectionListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.gui.views.routes.Routes";

    private TreeViewer m_selectedRouteViewer;
    private TreeViewer m_routesViewer;
    private Composite m_parent;

	public RoutesView() {
		if (DEBUG) System.out.printf("RoutesView()\n");			
		GuiEngine.addBroadcastListener(this);
	}

	public void createPartControl(final Composite parent) {
	    if (DEBUG) System.out.printf("RoutesView: createPartControl\n");
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

		getSite().getPage().addSelectionListener(this);		
		initialiseTrees();
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("RoutesView: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("RoutesView: propertyChange: %s\n", event.getProperty());	
		
		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
			initialiseTrees();
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				getActivePage().showView(RoutesView.ID);
			} catch (PartInitException e) {}
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			m_routesViewer.setInput(null);
			m_routesViewer.refresh();
			m_selectedRouteViewer.setInput(null);
			m_selectedRouteViewer.refresh();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			m_selectedRouteViewer.setInput(null);
			m_selectedRouteViewer.refresh();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE) ||
				   event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS)) {
			refreshTree();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE)) {
			initialiseSelectedRouteView();

		} else if (event.getProperty().equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
			initialiseTrees();

		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SELECT_NODES)) {
			selectNodes();
		}
	}
	
	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		if (DEBUG) System.out.printf("RoutesView: selectionChanged\n");			

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
		GuiEngine.setSelectedNodes(selectedNodes);
    	if (part != this) {
    		m_selectedRouteViewer.setSelection(sel, true);
    	}
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

		m_selectedRouteViewer.setContentProvider(new SelectedRouteContentProvider());				
		m_selectedRouteViewer.setLabelProvider(new SelectedRouteLabelProvider());
		
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
	    
	    m_routesViewer.setContentProvider(new RouteSelectorContentProvider());
	    m_routesViewer.setLabelProvider(new RouteSelectorLabelProvider());

	    createRouteContextMenus();
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
		            final Object object = ((IStructuredSelection)selection).getFirstElement();
		            if (object instanceof Node) {
				    	// Adds the 'Trace in console' option.
						manager.add(new Separator());
				    	manager.add(new Action("Trace in console") {
							public void run() {
								ConsoleView console = (ConsoleView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
									getActivePage().findView(ConsoleView.ID);
								
								try {
									PlatformUI.getWorkbench().getActiveWorkbenchWindow().
									    getActivePage().showView(ConsoleView.ID);
								} catch (PartInitException e) {}
								
								int routeId = GuiEngine.getEngine().getSelectedRouteId();
								console.runCommand("print trace " + routeId + " " + ((Node)object).getId());
							}
						});
   	
				    	// Adds the 'Set iterations' option.
					    manager.add(new Separator());
					    manager.add(new Action("Set iterations") {
						    public void run() {
						    	// TODO new Iterations(m_parent.getShell(), (Node)object);
						    }
					    });

				    	// Adds the 'Set scope' option.
					    manager.add(new Action("Set scope") {
						    public void run() {
						    	// TODO new Scope(m_parent.getShell(), (Node)object);
						    }
					    });
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
					GuiEngine.setSelectedNodes(selectedNodes);
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
					            if (object instanceof RouteNode) {
 				            	    GuiEngine.getEngine().removeRoute(((RouteNode)object).getRoute().getRouteId());
				            	}
				            }
						}
					});
				}

				if (GuiEngine.getEngine().getRoutes().size() > 0) {
	            	manager.add(new Action("delete all") {
						public void run() {
							GuiEngine.getEngine().removeAllRoutes();
						}
					});
				}
				
				if (((IStructuredSelection)selection).size() == 1) {
			        if (selection instanceof IStructuredSelection) {	            
			            Object object = ((IStructuredSelection)selection).getFirstElement();
			            if (object instanceof RouteNode) {
			                createContextMenu(m_parent.getShell(), manager, ((RouteNode)object).getRoute());
			            }
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
					int newId = Engine.ROUTE_ID_INVALID;

					if (((IStructuredSelection)selection).size() == 1) {
			            Object object = ((IStructuredSelection)selection).getFirstElement();
			            if (object instanceof RouteNode) {
			            	newId = ((RouteNode)object).getRoute().getRouteId();
			            }
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
		Route selectedRoute = GuiEngine.getEngine().getSelectedRoute();
		if (selectedRoute != null) {
			List<Node> subRouteNodes = selectedRoute.getSubRoutes();
			PathNode hiddenRouteNode = new PathNode(subRouteNodes);
			
			m_selectedRouteViewer.setInput(hiddenRouteNode);
			ColumnViewerToolTipSupport.enableFor(m_selectedRouteViewer, ToolTip.NO_RECREATE);
			m_selectedRouteViewer.expandToLevel(2);		
		}
	}
	
	private void initialiseRoutesView() {	
		RouteNode hiddenRouteNode = new RouteNode(null, null);		
		for (Route route : GuiEngine.getEngine().getRoutes()) {
			hiddenRouteNode.addChild(route);
		}
				
		m_routesViewer.setInput(hiddenRouteNode);
		ColumnViewerToolTipSupport.enableFor(m_routesViewer, ToolTip.NO_RECREATE);
		m_routesViewer.expandToLevel(2);
		
		int selectedRouteId = GuiEngine.getEngine().getSelectedRouteId();
		setRouteTreeSelection(hiddenRouteNode, selectedRouteId);
	}
	
	private boolean setRouteTreeSelection(RouteNode root, int routeId) {
		for (RouteNode node : root.getChildren()) {
			if (node.getRoute().getRouteId() == routeId) {
				ISelection selection = new StructuredSelection(node);
				m_routesViewer.setSelection(selection);
				return true;
			}
		}
		return false;
	}

	private void refreshTree() {
		m_selectedRouteViewer.refresh();
		m_routesViewer.refresh();
		
	}

	private void selectNodes() {
		// This method selects all the nodes in the tree that 
		// contains the given pc in one of their children..		
		Reference codePoint = GuiEngine.getCodePoint();
		Engine engine = GuiEngine.getEngine();
		if (codePoint.resolve(engine, engine.getNodeId(), engine.getCoreId())) {
			final int pc = codePoint.getPc();
			final List<Node> selectedNodes = new ArrayList<Node>();

			// Add the nodes hitting the pc
			Route selectedRoute = GuiEngine.getEngine().getSelectedRoute();
			if (selectedRoute != null) {								
				for (Node subRoute : selectedRoute.getSubRoutes()) {
					subRoute.traverse(0, new UntypedNodeTraverser() {
						public boolean foundNode(Node node, int depth) {
							if (node.getPCs().contains(pc)) {
								selectedNodes.add(node);
							}
							return true;
						}
					});
				}
			}
			m_selectedRouteViewer.setSelection(new StructuredSelection(selectedNodes));			
		}
	}
	
    private void createContextMenu(final Shell parent, IMenuManager manager, final Route route) {
	    manager.add(new Separator());
	    manager.add(new Action("Set timing requirements") {
		    public void run() {
			    new Required(parent, GuiEngine.getEngine(), route);
		    }
	    });
	    manager.add(new Action("Clear timing requirements") {
		    public void run() {
		    	GuiEngine.getEngine().unsetRouteMax(route.getRouteId());
		    }
	    });
    }	
}
