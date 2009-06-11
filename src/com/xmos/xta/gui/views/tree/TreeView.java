package com.xmos.xta.gui.views.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.dialogs.Iterations;
import com.xmos.xta.gui.dialogs.Scope;

public class TreeView extends ViewPart implements IPropertyChangeListener,
												  ISelectionListener {
    public static final boolean DEBUG = true;
    public static final String ID = "com.xmos.xta.gui.views.tree.Tree";

    private TreeViewer m_treeViewer;
    private Composite m_parent;

	public TreeView() {
		if (DEBUG) System.out.printf("Tree()\n");			
		GuiEngine.addBroadcastListener(this);
	}

	public void createPartControl(final Composite parent) {
	    if (DEBUG) System.out.printf("Tree: createPartControl\n");
	    m_parent = parent;
		
	    createTreeViewer(parent);
		getSite().getPage().addSelectionListener(this);
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Tree: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Tree: propertyChange: %s\n", event.getProperty());	
		
		if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE)) {
			initialiseTree();
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				getActivePage().showView(TreeView.ID);
			} catch (PartInitException e) {}
						
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			initialiseTree();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE) ||
				   event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS)) {
			refreshTree();			
		}
	}
	
	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
/*		
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
*/    	
     }
	
	private void createTreeViewer(Composite parent) {
		m_treeViewer = new TreeViewer(parent);
			    
	    getSite().setSelectionProvider(m_treeViewer);

	    m_treeViewer.setContentProvider(new TreeViewContentProvider());				
	    m_treeViewer.setLabelProvider(new TreeViewLabelProvider());
		
		createTreeContextMenus();
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
		            	TreeViewNodeContextMenu.createNodeContextMenu(m_parent.getShell(), manager, (Node)object);
		            }
		        }				
			}
		});
		
		contextMenu = menuMgr.createContextMenu(m_treeViewer.getTree());
		m_treeViewer.getTree().setMenu(contextMenu);
		
		m_treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
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
	
	private void initialiseTree() {
		Collection<Node> threadNodes = GuiEngine.getEngine().getStructureGraph().getThreads();
		Node threadNode = threadNodes.iterator().next();
		if (threadNode != null) {
			m_treeViewer.setInput(threadNode);
			ColumnViewerToolTipSupport.enableFor(m_treeViewer, ToolTip.NO_RECREATE);
			m_treeViewer.expandToLevel(2);		
			
		}		
	}

	private void refreshTree() {
		m_treeViewer.refresh();		
	}	
}
