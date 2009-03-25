package com.xmos.xta.rcp.nodes;

import java.util.HashSet;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import com.xmos.xta.engine.PathBasicBlock;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.rcp.XtaEngine;
import com.xmos.xta.rcp.dialogs.Iterations;
import com.xmos.xta.rcp.views.ConsoleView.ConsoleView;
import com.xmos.xta.rcp.views.ViewNode.FunctionPathNode;
import com.xmos.xta.rcp.views.ViewNode.StructurePathNode;

public class PathNode extends Node {
    private PathBasicBlock m_block;
    private boolean m_isRoot;
    
    private boolean m_absoluteCount;
    
    public PathNode(PathBasicBlock block, Node parent, boolean isRoot) {
    	super(parent);
    	m_block = block;
    	m_isRoot = isRoot;
    
    	m_absoluteCount = false;
    	
    	m_functionNode = new FunctionPathNode(this);
    	m_structureNode = new StructurePathNode(this);
    }

    public String getDescription() {
    	String str = getTimeTaken() + " ns (path " + getId() + ")";
    	if (!m_isRoot) {
    		str += " (iterations: " + m_block.getHardNumIterations() + " + " + m_block.getSoftNumIterations() + ")";
    	}
    	return str;
    }

    public int getId() {
        return m_block.getPathId();
    }

    public long getNumCycles() {
        return m_block.getNumCycles(true);
    }

    public long getNumUnknowns() {
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
        toolTipText += "Path ID: "          + m_block.getPathId() + "\n";
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
        toolTipText += "Number of unknowns (this): "  + m_block.getNumUnknowns(false);     
        
        if (!m_isRoot) {
            toolTipText += "\n----------------------------------\n";
            toolTipText += "Number of iterations: " +  + m_block.getHardNumIterations() + " + " + m_block.getSoftNumIterations();
        }
        return toolTipText;
    }

    public String getTypeName() {
        return "path";
    }   
    
    public HashSet<Integer> getPcsCovered() {
		HashSet<Integer> pcsCovered = new HashSet<Integer>();
		if (getNumIterations() > 0) {
		    for (Node child : getChildren()) {
			    pcsCovered.addAll(child.getPcsCovered());
            }
		}
        return pcsCovered;
    }
    
    PathNode m_this = this;
    public void createContextMenu(final Shell parent, IMenuManager manager) {    	
		manager.add(new Separator());
    	manager.add(new Action("Trace in console") {
			public void run() {
				ConsoleView console = (ConsoleView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().findView(ConsoleView.ID);
				
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView(ConsoleView.ID);
				} catch (PartInitException e) {}
				
				int routeId = getRouteId();
				console.runCommand("print trace " + routeId + " " + m_this.getId());
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

    public long getHardNumIterations() {
		if (m_isRoot) {
			return 1;
		} else {
			return m_block.getHardNumIterations();
		}
	}
    
	public long getNumIterations() {
		if (m_isRoot) {
			return 1;
		} else {
			return m_block.getNumIterations();
		}
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

	public void setIterations(long numIterations) {
		XtaEngine.getEngine().setLoopIterations(getRouteId(), m_block.getPathId(), numIterations);
	}

	public boolean getAbsoluteCount() {
		return m_absoluteCount;
	}

	public void setAbsoluteCount(boolean absoluteCount) {
		m_absoluteCount = absoluteCount;
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
}
