package com.xmos.xtagui.nodes;

import java.util.HashSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;

import com.xmos.xta.engine.Block;
import com.xmos.xtagui.dialogs.Iterations;
import com.xmos.xtagui.views.TreeView;

public class PathNode extends Node {
    private Block m_block;
    private boolean m_complete;
    
    public PathNode(Block block, Node parent, boolean complete) {
    	super(parent);
    	m_block = block;
    	m_complete = complete;
    }

    public String getDescription() {
    	String str = getTimeTaken() + " ns (path " + getId() + ")";
    	if (!m_complete) {
    		str += " (iterations: " + m_block.getNumIterations() + ")";
    	}
    	return str;
    }

    public int getId() {
        return m_block.getId();
    }

    public int getInstanceId() {
        return m_block.getInstanceId();
    }

    public int getNumCycles() {
        return m_block.getNumCycles(true);
    }

    public double getTimeTaken() {
        return m_block.getTimeTaken(true);
    }

    public String getToolTipText() {
        String toolTipText = "";
        toolTipText += "Path ID: "          + m_block.getId() + "\n";
        toolTipText += "Path Instance ID: " + m_block.getInstanceId() + "\n";
        toolTipText += "----------------------------------\n";
        toolTipText += "Time taken: "          + m_block.getTimeTaken(true) + " ns\n";
        toolTipText += "Number of cycles: "    + m_block.getNumCycles(true) + "\n";
        toolTipText += "Number of FNOPs: "     + m_block.getNumFNops(true) + "\n";
        toolTipText += "Number of pausables: " + m_block.getNumPausables(true) + "\n";
        toolTipText += "----------------------------------\n";
        toolTipText += "Time taken (this): "          + m_block.getTimeTaken(false) + " ns\n";
        toolTipText += "Number of cycles (this): "    + m_block.getNumCycles(false) + "\n";
        toolTipText += "Number of FNOPs (this): "     + m_block.getNumFNops(false) + "\n";
        toolTipText += "Number of pausables (this): " + m_block.getNumPausables(false);        
        if (!m_complete) {
            toolTipText += "\n----------------------------------\n";
            toolTipText += "Number of iterations: " + m_block.getNumIterations();
        }
        return toolTipText;
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
    	
    	if (!m_complete) {
		    manager.add(new Separator());
    	    manager.add(new Action("Set iterations") {
			    public void run() {
				    new Iterations(treeView);
			    }
		    });
    	}
    }    
}
