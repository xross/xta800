package com.xmos.xta.ui.views.routes.selector;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.dialogs.FunctionTime;
import com.xmos.xta.ui.dialogs.PathTime;
import com.xmos.xta.ui.dialogs.Required;

public class RouteSelectorViewer {	
    private TreeViewer m_viewer;	
    private Composite m_parent;
	
    public RouteSelectorViewer(Composite parent) {
    	m_parent = parent;
    }
    
    public TreeViewer getViewer() {
    	return m_viewer;
    }
    
    public void create(Sash sash) {
		m_viewer = new TreeViewer(m_parent);
		FormData data = new FormData();
	    data.top = new FormAttachment(sash, 0);
	    data.bottom = new FormAttachment(100, 0);
	    data.left = new FormAttachment(0, 0);
	    data.right = new FormAttachment(100, 0);
	    m_viewer.getControl().setLayoutData(data);
	    
	    m_viewer.setContentProvider(new RouteSelectorContentProvider());
	    m_viewer.setLabelProvider(new RouteSelectorLabelProvider());
		ColumnViewerToolTipSupport.enableFor(m_viewer, ToolTip.NO_RECREATE);

	    createContextMenus();
	}
	
    public void initialise() {	
		RouteNode hiddenRouteNode = new RouteNode(null, null);		
		for (Route route : GuiEngine.getEngine().getRoutes()) {
			hiddenRouteNode.addChild(route);
		}
				
		m_viewer.setInput(hiddenRouteNode);
		m_viewer.expandToLevel(2);
		
		int selectedRouteId = GuiEngine.getEngine().getSelectedRouteId();
		setSelection(hiddenRouteNode, selectedRouteId);
	}
	
	public void refresh() {
		m_viewer.refresh();
	}

	public void clear() {
		m_viewer.setInput(null);
	}
    
	private void createContextMenus() {
	    Menu contextMenu;
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);

		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {				
				final ISelection selection = m_viewer.getSelection();
				
				if (((IStructuredSelection)selection).size() > 0) {
	            	manager.add(new Action("delete") {
						public void run() {
							deleteSelection();
						}
					});
				}

				if (GuiEngine.getEngine().getRoutes().size() > 0) {
	            	manager.add(new Action("delete all") {
						public void run() {
							GuiEngine.getEngine().removeAllRoutes(GuiEngine.s_guiCommandSrc);
						}
					});
				}
				
		        if (selection instanceof IStructuredSelection) {
		        	IStructuredSelection sSel = (IStructuredSelection)selection;

		        	List<RouteNode> selectedNodes = new ArrayList<RouteNode>();
		        	for (Object object : sSel.toArray()) {
		        		if (object instanceof RouteNode) {
		        			selectedNodes.add((RouteNode)object);
		        		}
		            }
		        	createContextMenu(manager, selectedNodes, m_parent.getShell());
		        }
			}
		});
		
		contextMenu = menuMgr.createContextMenu(m_viewer.getTree());
		m_viewer.getTree().setMenu(contextMenu);		

		m_viewer.addSelectionChangedListener(new ISelectionChangedListener() {
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
					
					if ((newId != currentSelectedId) && (newId != Engine.ROUTE_ID_INVALID)) {
						engine.setSelectedRouteId(GuiEngine.s_guiCommandSrc, newId);
	            	}
		        }
			}
		});
		
		m_viewer.getTree().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.DEL) {
					deleteSelection();
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});
	}

	private void deleteSelection() {
		ISelection selection = m_viewer.getSelection();
        for (Object object : ((IStructuredSelection)selection).toList()) {
            if (object instanceof RouteNode) {
         	    GuiEngine.getEngine().removeRoute(GuiEngine.s_guiCommandSrc, ((RouteNode)object).getRoute().getRouteId());
        	}
        }
	}

	public static void createContextMenu(IMenuManager manager, final List<RouteNode> selectedNodes, final Shell shell) {
		if (selectedNodes.size() < 1)
			return;

		manager.add(new Separator());
		addSetTimingRequirements(manager, selectedNodes, shell);

		manager.add(new Separator());
		addSetPathTiming(manager, selectedNodes, shell);
		addSetFunctionTiming(manager, selectedNodes, shell);
		
		manager.add(new Separator());
		addTraceInConsole(manager, selectedNodes);
		addShowRouteInfoInConsole(manager, selectedNodes);
	}

	private static void addSetTimingRequirements(IMenuManager manager, final List<RouteNode> selectedNodes, final Shell shell) {
		Action action = new Action("Set timing requirements") {
		    public void run() {
			    new Required(shell, GuiEngine.getEngine(), getFirstNode(selectedNodes).getRoute());
		    }
	    };
	    manager.add(action);
	    action.setEnabled(oneNodeSelected(selectedNodes) ? true : false);		
	}

	private static void addSetPathTiming(IMenuManager manager, final List<RouteNode> selectedNodes, final Shell shell) {
		Action action = new Action("Set path timing") {
		    public void run() {
			    new PathTime(shell, GuiEngine.getEngine(), getFirstNode(selectedNodes).getRoute());
		    }
	    };
	    manager.add(action);
	    action.setEnabled(oneNodeSelected(selectedNodes) ? true : false);		
	}

	private static void addSetFunctionTiming(IMenuManager manager, final List<RouteNode> selectedNodes, final Shell shell) {
		Action action = new Action("Set function timing") {
		    public void run() {
			    new FunctionTime(shell, GuiEngine.getEngine(), getFirstNode(selectedNodes).getRoute());
		    }
	    };
	    manager.add(action);
	    action.setEnabled(oneNodeSelected(selectedNodes) ? true : false);		
	}
	
	private static void addTraceInConsole(IMenuManager manager, final List<RouteNode> selectedNodes) {
		Action action = new Action("Trace in console") {
			public void run() {
				GuiEngine.runCommandInConsole("print trace " + getRouteId()); 
			}
		};
		manager.add(action);
		action.setEnabled(oneNodeSelected(selectedNodes) ? true : false);		
	}

	private static void addShowRouteInfoInConsole(IMenuManager manager, final List<RouteNode> selectedNodes) {
		Action action = new Action("Show route info in console") {
			public void run() {
				GuiEngine.runCommandInConsole("print route_info " + getRouteId());
			}
		};
		manager.add(action);
		action.setEnabled(oneNodeSelected(selectedNodes) ? true : false);		
	}
	
	private static int getRouteId() {
		return GuiEngine.getEngine().getSelectedRouteId();
	}
	
	private static RouteNode getFirstNode(List<RouteNode> selectedNodes) {
		return selectedNodes.get(0);
	}
	
	private static boolean oneNodeSelected(List<RouteNode> selectedNodes) {
		return selectedNodes.size() == 1;
	}
	
	private boolean setSelection(RouteNode root, int routeId) {
		for (RouteNode node : root.getChildren()) {
			if (node.getRoute().getRouteId() == routeId) {
				ISelection selection = new StructuredSelection(node);
				m_viewer.setSelection(selection);
				return true;
			}
		}
		return false;
	}    
}
