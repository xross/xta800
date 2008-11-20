package com.xmos.xtagui.nodes;

import java.util.HashSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;

import com.xmos.xta.engine.Block;
import com.xmos.xtagui.views.TreeView;

public class PathNode extends Node {
    private Block m_block;
    
    public PathNode(Block block, Node parent) {
    	super(parent);
    	m_block = block;
    }

    public String getDescription() {
    	return getTimeTaken() + " ns (path " + getId() + ")";
    }

    public int getId() {
        return m_block.getId();
    }

    public int getInstanceId() {
        return m_block.getInstanceId();
    }

    public int getNumCycles() {
        return m_block.getNumCycles(false);
    }

    public double getTimeTaken() {
        return m_block.getTimeTaken(false);
    }

    public String getToolTipText() {
        return getDescription();
    }

    public String getTypeName() {
        return "path";
    }   
    
    public HashSet<Integer> getPcsCovered() {
    	return m_block.getPcsCovered(false);
    }
    
    public void createContextMenu(IMenuManager manager, final TreeView treeView) {    	
		manager.add(new Action("expand all") {
			public void run() {
				System.out.printf("path expand all\n");
				treeView.getViewer().expandAll();
			}
		});
		manager.add(new Action("collapse all") {
			public void run() {
				System.out.printf("path collapse\n");	
				treeView.getViewer().collapseAll();
			}
		});
		manager.add(new Separator());
    	manager.add(new Action("trace to console") {
			public void run() {
			}
		});
    }    
}
