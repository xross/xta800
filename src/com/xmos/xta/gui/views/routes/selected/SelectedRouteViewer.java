package com.xmos.xta.gui.views.routes.selected;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
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

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.PathNode;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.views.Views;

public class SelectedRouteViewer {	
    private TreeViewer m_viewer;
    private Composite m_parent;
	
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
		ColumnViewerToolTipSupport.enableFor(m_viewer, ToolTip.NO_RECREATE);
		createContextMenus();
	}

	public void initialise() {
		clear();
		
		Route selectedRoute = GuiEngine.getEngine().getSelectedRoute();
		if (selectedRoute != null) {
			List<Node> nodes = new LinkedList<Node>();
			nodes.add(selectedRoute.getNode());
			PathNode hiddenRouteNode = new PathNode(nodes);
			
			m_viewer.setInput(hiddenRouteNode);
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

	        	Views.createContextMenu(manager, GuiEngine.getSelectedNodes(), m_parent.getShell());
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
