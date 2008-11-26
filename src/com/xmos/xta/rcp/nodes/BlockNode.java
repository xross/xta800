package com.xmos.xta.rcp.nodes;

import java.util.HashSet;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;

import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.rcp.views.TreeView;

public class BlockNode extends Node {
	Block m_block;
	
    public BlockNode(Block block, Node parent) {
    	super(parent);
    	m_block = block; 
    }
    
    public int getId() {
        return m_block.getId();
    }
    
    public int getInstanceId() {
        return m_block.getInstanceId();
    }

    public double getTimeTaken() {
        return m_block.getTimeTakenThis();
    }
    
    public int getNumCycles() {
        return m_block.getNumCyclesThis();
    }

    public int getNumUnknowns() {
        return m_block.getUnknowns();
    }
    
    public String getDescription() {
        return getTimeTaken() + " ns";
    }
    
    public String getToolTipText() {
        String toolTipText = "";
        toolTipText += "Block ID: " + m_block.getId() + "\n";
        toolTipText += "Block Instance ID: " + m_block.getInstanceId() + "\n";
        toolTipText += "----------------------------------\n";
        toolTipText += "Time taken: " + getTimeTaken() + " ns\n";
        toolTipText += "Number of cycles: " + getNumCycles() + "\n";
        toolTipText += "Number of FNOPs: " + m_block.getNumFNopsThis() + "\n";
        toolTipText += "Number of pausable: " + m_block.getNumPausablesThis();
        return toolTipText;
    }

    public String getTypeName() {
        return "block";
    }
    
    public HashSet<Integer> getPcsCovered() {
    	return m_block.getPcsCoveredThis();
    }
    
    public void createContextMenu(IMenuManager manager, final TreeView treeView) {    	
		manager.add(new Action("expand all") {
			public void run() {
				treeView.getViewer().expandAll();
			}
		});
		manager.add(new Action("collapse all") {
			public void run() {
				treeView.getViewer().collapseAll();
			}
		});
    }

	public int getNumIterations() {
		return 1;
	}

	public boolean isLoopNode() {
		return false;
	}

	public boolean isPathNode() {
		return false;
	}

	public Vector<XtaInstruction> getInstructions() {
		return m_block.getInstructionsThis();
	}
}
