package com.xmos.xta.rcp.nodes;

import java.util.HashSet;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Shell;
import com.xmos.xta.engine.PathBasicBlock;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.dialogs.MaxIterations;
import com.xmos.xta.rcp.dialogs.Scope;
import com.xmos.xta.rcp.views.ViewNode.FunctionLoopNode;
import com.xmos.xta.rcp.views.ViewNode.StructureLoopNode;

public class LoopNode extends Node {
    private PathBasicBlock m_block;
    private LoopNode m_this;
    
    public LoopNode(PathBasicBlock block, Node parent){
    	super(parent);
    	m_block = block;
    	m_this = this;
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
    
    public long getNumCycles() {
        return m_block.getNumCyclesLoopsOnly();
    }
    
    public long getNumUnknowns() {
        return m_block.getNumUnknownsLoopsOnly();
    }    
    
    public String getDescription() {
        return getTimeTaken() + " ns - " + getSummary(); 
    }

    public String getToolTipText(){
        String toolTipText = getSummary() + "\n";
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
        
    public HashSet<Integer> getPcsCovered(boolean isSelected) {
		HashSet<Integer> pcsCovered = new HashSet<Integer>();
		for (Node child : getChildren()) {
			pcsCovered.addAll(child.getPcsCovered(isSelected));
        }
        return pcsCovered;
    }

    public void createContextMenu(final Shell parent, IMenuManager manager) {    	
	    manager.add(new Separator());
	    manager.add(new Action("Set scope") {
		    public void run() {
		    	new Scope(parent, m_this);
		    }
	    });
	    
	    manager.add(new Action("Set max iterations") {
		    public void run() {
		    	new MaxIterations(parent, m_this);
		    }
	    });
    }
            
    private String getSummary() {
        int numPaths = getChildren().size();
		String description = "Loop: " + numPaths + " path";
        if (numPaths > 1) {
            description += "s";
        }
        
        description += ", " + getNumIterations() + " iterations";
        return description;
    }        

    public long getNumIterations() {
        int numIterations = 0;
        for (Node child : getChildren()) {
            numIterations += child.getNumIterations();
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

	public void getFnopPcs(Vector<XtaInstruction> fnops) {
		for (Node node : getChildren()) {
			node.getFnopPcs(fnops);
		}
	}

	public void getBlockIds(HashSet<Integer> ids) {
		for (Node node : getChildren()) {
			node.getBlockIds(ids);
		}
	}
	
	public long getLoopMax(){
		Vector<Node> children = getChildren(); 		
		PathNode loopEndNode = (PathNode)children.get(children.size() - 1);		
		PathBasicBlock loopBlock = loopEndNode.getPathBasicBlock();		 		 
		try {
			return m_block.getloops().getLoopMax(loopBlock);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public void setLoopMax(long count){
		int pc = m_block.getInstructionTrimmed().getFirst().getPc();	
		XtaEngine.getEngine().setLoopMaxIterations(getRouteId(), pc, count);
	}

	public void setScope(boolean absolute) {
		int pc = m_block.getInstructionTrimmed().getFirst().getPc();	
		XtaEngine.getEngine().setLoopScope(getRouteId(), pc, absolute);
	}

	public boolean getScope() {
		return m_block.getloops().getLoopScope(m_block.getloops().getSubLoops().get(0).getLoop());
	}
	
}
