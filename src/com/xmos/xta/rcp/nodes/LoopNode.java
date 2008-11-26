package com.xmos.xta.rcp.nodes;

import java.util.HashSet;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;

import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.rcp.views.TreeView;

public class LoopNode extends Node {
    private Block m_block;

    public LoopNode(Block block, Node parent){
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
        return m_block.getTimeTaken(true);
    }
    
    public double getTimeTakenThis() {
        return 0;
    }
    
    public int getNumCycles() {
        return m_block.getNumCycles(true);
    }
    
    public int getNumUnknowns() {
        return m_block.getNumUnknowns(true);
    }    
    
    public String getDescription() {
        return getTimeTaken() + " ns - " + getSummary(); 
    }

    public String getToolTipText(){
        String toolTipText = getSummary() + "\n";
        toolTipText += "----------------------------------\n";
        toolTipText += "Loop ID: " + m_block.getId() + "\n";
        toolTipText += "Loop Instance ID: " + m_block.getInstanceId() + "\n";
        toolTipText += "----------------------------------\n";
        toolTipText += "Total time taken: " + getTimeTaken() + " ns\n";
        toolTipText += "Total number of cycles: " + getNumCycles() + "\n";
        toolTipText += "Total number of FNOPs: " + m_block.getNumFNops(true) + "\n";
        toolTipText += "Total number of pausable: " + m_block.getNumPausables(true);
        return toolTipText;
    }
    
    public String getTypeName() {
        return "loop";
    }
        
    public HashSet<Integer> getPcsCovered() {
        return m_block.getPcsCovered(true);
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
            
    private String getSummary() {
        String description = "Loop: " + m_block.numLoopPaths() + " path";
        if (m_block.numLoopPaths() > 1) {
            description += "s";
        }
        
        description += ", " + getNumIterations() + " iterations";
        return description;
    }        

    public int getNumIterations() {
        int numIterations = 0;
        for (Block loopPath : m_block.getLoopPaths()) {
            numIterations += m_block.getLoopPathAttributes(loopPath).getNumIterations();
        }
        return numIterations;
    }

	public boolean isBlockNode() {
		return false;
	}

	public boolean isLoopNode() {
		return true;
	}

	public boolean isPathNode() {
		return false;
	}

	public boolean isRouteNode() {
		return false;
	}

	public Vector<XtaInstruction> getInstructions() {
		return new Vector<XtaInstruction>();
	}
}
