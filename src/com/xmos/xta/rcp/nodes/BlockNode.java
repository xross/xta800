package com.xmos.xta.rcp.nodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Shell;
import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.rcp.views.ViewNode.FunctionBlockNode;

public class BlockNode extends Node {
	Block m_block;
	
    public BlockNode(Block block, Node parent) {
    	super(parent);
    	m_block = block; 

    	m_functionNode = new FunctionBlockNode(this);
    }
    
    public int getId() {
        return m_block.getId();
    }

    public double getTimeTaken() {
        return m_block.getTimeTakenThis();
    }
    
    public double getTimeTakenThis() {
        return m_block.getTimeTakenThis();
    }
    
    public int getNumCycles() {
        return m_block.getNumCyclesThis();
    }

    public int getNumUnknowns() {
        return m_block.getNumUnknownsThis();
    }
    
    public String getDescription() {
        return getTimeTaken() + " ns";
    }
    
    public String getToolTipText() {
        String toolTipText = "";
        toolTipText += "Block ID: " + m_block.getId() + "\n";
        toolTipText += "----------------------------------\n";
        toolTipText += "Time taken: " + getTimeTaken() + " ns\n";
        toolTipText += "Number of cycles: " + getNumCycles() + "\n";
        toolTipText += "Number of FNOPs: " + m_block.getNumFNopsThis() + "\n";
        toolTipText += "Number of pausables: " + m_block.getNumPausablesThis() + "\n";
        ArrayList<String> labels  = m_block.getLabels();
        boolean first= true;
        for(String label : labels){
        	if(first) {
        		toolTipText += "Labels (this): " + label + "\n"; 	
        		first = false;
        	}
        	else toolTipText += "                   : " + label + "\n"; 
        }
        
        toolTipText += "Number of unknowns: " + m_block.getNumUnknownsThis();
        
        
        return toolTipText;
    }

    public String getTypeName() {
        return "block";
    }
    
    public HashSet<Integer> getPcsCovered() {
    	return m_block.getPcsCoveredThis();
    }
    
    public void createContextMenu(final Shell parent, IMenuManager manager) {    	
    }

	public int getNumIterations() {
		return 1;
	}

	public boolean isBlockNode() {
		return true;
	}

	public boolean isLoopNode() {
		return false;
	}

	public boolean isPathNode() {
		return false;
	}

	public boolean isRouteNode() {
		return false;
	}

	public Vector<XtaInstruction> getInstructions() {
		return m_block.getInstructionsThis();
	}
}
