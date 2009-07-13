package com.xmos.xta.gui.views.routes.selector;

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

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.dialogs.Required;

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
						engine.setSelectedRoute(newId);
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
         	    GuiEngine.getEngine().removeRoute(((RouteNode)object).getRoute().getRouteId());
        	}
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
