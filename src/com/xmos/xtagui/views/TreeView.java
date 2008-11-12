package com.xmos.xtagui.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Route;
import com.xmos.xtagui.XtaDisplayer;
import com.xmos.xtagui.nodes.BlockNode;
import com.xmos.xtagui.nodes.LoopNode;
import com.xmos.xtagui.nodes.Node;
import com.xmos.xtagui.nodes.PathNode;
import com.xmos.xtagui.nodes.RouteNode;
import com.xmos.xtagui.properties.DisplayRouteProperty;

public class TreeView extends ViewPart implements IPropertyChangeListener {
    public static final boolean DEBUG = true;
    public static final String ID = "com.xmos.xtagui.views.Tree";

    private TreeViewer m_treeViewer;
    private Display m_display;
    private DisplayRouteProperty m_routeProperty;

	public TreeView() {
		if (DEBUG) System.out.printf("Tree()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
	}

	public void createPartControl(Composite parent) {
	    if (DEBUG) System.out.printf("Tree: createPartControl\n");
	    m_display = parent.getDisplay();	    
		m_treeViewer = new TreeViewer(parent);
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Tree: setFocus\n");			
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Tree: propertyChange: %s\n", event.getProperty());	
		
		if (event.getProperty().equals("DisplayRoute")) {
			m_routeProperty = (DisplayRouteProperty)event.getNewValue();										
			m_display.asyncExec(new Runnable() {
				public void run() {
					m_treeViewer.setContentProvider(new TreeViewContentProvider());				
					m_treeViewer.setLabelProvider(new TreeViewLabelProvider());
					m_treeViewer.setInput(createHierarchy());
					m_treeViewer.expandAll();
					ColumnViewerToolTipSupport.enableFor(m_treeViewer, ToolTip.NO_RECREATE);
				}});					    
		}
	}
	
	private Node createHierarchy() {
		Route route = m_routeProperty.getRoute();
        RouteNode routeNode = new RouteNode(route);
        for (Block block : route.completePaths()) {
        	routeNode.addNode(createPath(block, routeNode));
        }		
        
        if (DEBUG) {
        	displayHierarchy(routeNode, 0);
        }                
		return routeNode;
	}
	
	private PathNode createPath(Block block, Node parent) {
        PathNode pathNode = new PathNode(block, parent);
                
        for (Block pathBlock : block.getPathBlocks()) {            
            if (pathBlock.numLoopPaths() > 0) {
                LoopNode loopNode = new LoopNode(pathBlock, pathNode);
                pathNode.addNode(loopNode);
                
                for (Block loopPath : pathBlock.getLoopPaths()) {
                	loopNode.addNode(createPath(loopPath, loopNode));                    
                }
                pathBlock.doneWithLoopPaths();
            }
        	pathNode.addNode(new BlockNode(pathBlock, pathNode));            
        }        
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
}
