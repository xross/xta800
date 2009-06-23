package com.xmos.xta.gui.views.routes.selected;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.PathNode;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.gui.Broadcaster;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.dialogs.Loop;
import com.xmos.xta.gui.dialogs.LoopPath;
import com.xmos.xta.gui.dialogs.LoopScope;
import com.xmos.xta.gui.views.console.ConsoleView;

public class SelectedRouteViewer {	
    private TreeViewer m_viewer;
    private Composite m_parent;
    private Action m_setLoopIterationsAction;    
    private Action m_setLoopPathIterationsAction;    
    private Action m_setLoopScopeAction;    
	
    public SelectedRouteViewer(Composite parent) {    	
    	m_parent = parent;
    }
    
    public TreeViewer getViewer() {
    	return m_viewer;
    }
    
    public void create(Sash sash) {
    	m_viewer = new TreeViewer(m_parent);
	    FormData data = new FormData();
	    data.top = new FormAttachment(0, 0);
	    data.bottom = new FormAttachment(sash, 0);
	    data.left = new FormAttachment(0, 0);
	    data.right = new FormAttachment(100, 0);
	    m_viewer.getControl().setLayoutData(data);
	    
	    m_viewer.setContentProvider(new SelectedRouteContentProvider());				
	    m_viewer.setLabelProvider(new SelectedRouteLabelProvider());
		
	    m_viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				GuiEngine.setFocus(m_viewer.getSelection());
				GuiEngine.broadcast(Broadcaster.BROADCAST_SET_FOCUS, "");
			}
		});
		createContextMenus();
	}

	public void initialise() {
		Route selectedRoute = GuiEngine.getEngine().getSelectedRoute();
		if (selectedRoute != null) {
			List<Node> nodes = new LinkedList<Node>();
			nodes.add(selectedRoute.getNode());
			PathNode hiddenRouteNode = new PathNode(nodes);
			
			m_viewer.setInput(hiddenRouteNode);
			ColumnViewerToolTipSupport.enableFor(m_viewer, ToolTip.NO_RECREATE);
			m_viewer.expandToLevel(2);		
		}
	}
    
	public void refresh() {
		m_viewer.refresh();
	}

	public void clear() {
		m_viewer.setInput(null);
	}
	
	public void selectNodes() {
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
				selectedRoute.getNode().traverse(0, new UntypedNodeTraverser() {
					public boolean foundNode(Node node, int depth) {
						if (node.getPCs().contains(pc)) {
							selectedNodes.add(node);
						}
						return true;
					}
				});
			}
			m_viewer.setSelection(new StructuredSelection(selectedNodes));			
		}
	}	
	
	private void createContextMenus() {
	    Menu contextMenu;
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);

		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {				
				manager.add(new Action("expand all") {
					public void run() {
						m_viewer.expandAll();
					}
				});
				manager.add(new Action("collapse all") {
					public void run() {
						m_viewer.collapseAll();
						m_viewer.expandToLevel(2);
					}
				});

				ISelection selection = m_viewer.getSelection();
		        if (selection instanceof IStructuredSelection) {	            
		            final Object object = ((IStructuredSelection)selection).getFirstElement();
		            if (object instanceof Node) {
						manager.add(new Separator());

		            	// Adds the 'Trace in console' option.
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

					    manager.add(new Separator());
				    	
				    	// Adds the 'Set iterations' option.
					    m_setLoopIterationsAction = new Action("Set loop iterations") {
						    public void run() {
						    	new Loop(m_parent.getShell(), (Node)object);
						    }
					    };
					    manager.add(m_setLoopIterationsAction);
					    if (GuiEngine.getEngine().canSetLoopIterations((Node)object))
					        m_setLoopIterationsAction.setEnabled(true);
					    else
					    	m_setLoopIterationsAction.setEnabled(false);

				    	// Adds the 'Set iterations' option.
					    m_setLoopPathIterationsAction = new Action("Set loop path iterations") {
						    public void run() {
						    	new LoopPath(m_parent.getShell(), (Node)object);
						    }
					    };
					    manager.add(m_setLoopPathIterationsAction);
					    if (GuiEngine.getEngine().canSetLoopPathIterations((Node)object))
					        m_setLoopPathIterationsAction.setEnabled(true);
					    else
					        m_setLoopPathIterationsAction.setEnabled(false);
					    
				    	// Adds the 'Set scope' option.
					    m_setLoopScopeAction = new Action("Set loop scope") {
						    public void run() {
						    	new LoopScope(m_parent.getShell(), (Node)object);
						    }
					    };
					    manager.add(m_setLoopScopeAction);
					    if (GuiEngine.getEngine().canSetLoopScope((Node)object))
    					    m_setLoopScopeAction.setEnabled(true);
					    else
    					    m_setLoopScopeAction.setEnabled(false);
		            }
		        }				
			}
		});
		
		contextMenu = menuMgr.createContextMenu(m_viewer.getTree());
		m_viewer.getTree().setMenu(contextMenu);
		
		m_viewer.addSelectionChangedListener(new ISelectionChangedListener() {
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
    
}
