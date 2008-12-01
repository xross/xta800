package com.xmos.xta.rcp.nodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Shell;

import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.dialogs.Iterations;

public class PathNode extends Node {
    private Block m_block;
    private boolean m_isRoot;
    
    private boolean m_allInstances;
    private boolean m_absoluteCount;
    
    public PathNode(Block block, Node parent, boolean isRoot) {
    	super(parent);
    	m_block = block;
    	m_isRoot = isRoot;
    
    	m_allInstances = false;
    	m_absoluteCount = false;
    }

    public String getDescription() {
    	String str = getTimeTaken() + " ns (path " + getId() + ")";
    	if (!m_isRoot) {
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

    public int getNumUnknowns() {
        return m_block.getNumUnknowns(true);
    }    
    
    public double getTimeTaken() {
        return m_block.getTimeTaken(true);
    }

    public double getTimeTakenThis() {
        return m_block.getTimeTaken(false);
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
        toolTipText += "Number of unknowns: "  + m_block.getNumUnknowns(true) + "\n";
        toolTipText += "----------------------------------\n";
        toolTipText += "Time taken (this): "          + m_block.getTimeTaken(false) + " ns\n";
        toolTipText += "Number of cycles (this): "    + m_block.getNumCycles(false) + "\n";
        toolTipText += "Number of FNOPs (this): "     + m_block.getNumFNops(false) + "\n";
        
        toolTipText += "Number of pausables (this): " + m_block.getNumPausables(false) + "\n";  
        ArrayList<String> labels  = new ArrayList<String>();
        m_block.getPathLabels(false, labels);
        boolean first= true;
        for(String label : labels){
        	if(first) {
        		toolTipText += "Labels (this): " + label + "\n"; 	
        		first = false;
        	}
        	else toolTipText += "                   : " + label + "\n"; 
        }
        toolTipText += "Number of unknowns (this): "  + m_block.getNumUnknowns(false);     
        
        if (!m_isRoot) {
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
    
    PathNode m_this = this;
    public void createContextMenu(final Shell parent, IMenuManager manager) {    	
		manager.add(new Separator());
    	manager.add(new Action("Trace in console") {
			public void run() {
				// TODO - implement
			}
		});    	
    	
    	if (!m_isRoot) {
		    manager.add(new Separator());
    	    manager.add(new Action("Set iterations") {
			    public void run() {
				    new Iterations(parent, m_this);
			    }
		    });
    	}
    }

	public int getNumIterations() {
		return m_block.getNumIterations();
	}

	public boolean isBlockNode() {
		return false;
	}

	public boolean isLoopNode() {
		return false;
	}

	public boolean isPathNode() {
		return true;
	}

	public boolean isRouteNode() {
		return false;
	}

    public Vector<XtaInstruction> getInstructions() {
		return new Vector<XtaInstruction>();
	}

	public void setIterations(int numIterations) {
		if (m_allInstances) {
			XtaEngine.getEngine().setLoopIterations(m_block.getId(), true,
					m_absoluteCount, numIterations);
		} else {
			XtaEngine.getEngine().setLoopIterations(m_block.getInstanceId(), false,
					m_absoluteCount, numIterations);		
		}
	}

	public void setAllInstances(boolean allInstances) {
		m_allInstances = allInstances;
	}

	public boolean getAllInstances() {
		return m_allInstances;
	}

	public boolean getAbsoluteCount() {
		return m_absoluteCount;
	}

	public void setAbsoluteCount(boolean absoluteCount) {
		m_absoluteCount = absoluteCount;
	}
}
