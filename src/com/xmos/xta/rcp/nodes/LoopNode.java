package com.xmos.xta.rcp.nodes;

import java.util.HashSet;
import java.util.Vector;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Shell;

import com.xmos.xta.engine.PathBasicBlock;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.rcp.views.ViewNode.FunctionLoopNode;
import com.xmos.xta.rcp.views.ViewNode.StructureLoopNode;

public class LoopNode extends Node {
    private PathBasicBlock m_block;

    public LoopNode(PathBasicBlock block, Node parent){
    	super(parent);
    	m_block = block;

    	m_functionNode = new FunctionLoopNode(this);
    	m_structureNode = new StructureLoopNode(this);
}

    public int getId() {
        return m_block.getBlockId();
    }
    
    public double getTimeTaken() {
        return m_block.getTimeTakenLoopsOnly();
    }
    
    public double getTimeTakenThis() {
        return 0;
    }
    
    public int getNumCycles() {
        return m_block.getNumCyclesLoopsOnly();
    }
    
    public int getNumUnknowns() {
        return m_block.getNumUnknownsLoopsOnly();
    }    
    
    public String getDescription() {
        return getTimeTaken() + " ns - " + getSummary(); 
    }

    public String getToolTipText(){
        String toolTipText = getSummary() + "\n";
        toolTipText += "----------------------------------\n";
        toolTipText += "Loop ID: " + m_block.getPathId() + "\n";
        toolTipText += "----------------------------------\n";
        toolTipText += "Total time taken: " + getTimeTaken() + " ns\n";
        toolTipText += "Total number of cycles: " + getNumCycles() + "\n";
        toolTipText += "Total number of FNOPs: " + m_block.getNumFNopsLoopsOnly() + "\n";
        toolTipText += "Total number of pausable: " + m_block.getNumPausablesLoopsOnly() + "\n";
        toolTipText += "Total number of unknowns: " + m_block.getNumUnknownsLoopsOnly() ;
        return toolTipText;
    }
    
    public String getTypeName() {
        return "loop";
    }
        
    public HashSet<Integer> getPcsCovered() {
        return m_block.getPcsCoveredLoopsOnly();
    }

    public void createContextMenu(final Shell parent, IMenuManager manager) {    	
    }
            
    private String getSummary() {
        String description = "Loop: " + m_block.numLoopPaths(m_block) + " path";
        if (m_block.numLoopPaths(m_block) > 1) {
            description += "s";
        }
        
        description += ", " + getNumIterations() + " iterations";
        return description;
    }        

    public int getNumIterations() {
        int numIterations = 0;
        for (PathBasicBlock loopPath : m_block.getLoopPaths(m_block)) {
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

	public Vector<XtaInstruction> getUnknowns() {
		return new Vector<XtaInstruction>();
	}
	
	public void getFnopPcs(Vector<XtaInstruction> fnops) {
		for (Node node : getNodes()) {
			node.getFnopPcs(fnops);
		}
	}

	public void getBlockIds(Vector<Integer> ids) {
		for (Node node : getNodes()) {
			node.getBlockIds(ids);
		}
	}
}
