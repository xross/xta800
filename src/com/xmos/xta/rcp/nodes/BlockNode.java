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
import com.xmos.xta.rcp.views.ConsoleView.ConsoleView;
import com.xmos.xta.rcp.views.ViewNode.FunctionBlockNode;
import com.xmos.xta.rcp.views.ViewNode.StructureBlockNode;

public class BlockNode extends Node {
	PathBasicBlock m_block;
	
    public BlockNode(PathBasicBlock block, Node parent) {
    	super(parent);
    	m_block = block; 

    	m_functionNode = new FunctionBlockNode(this);
    	m_structureNode = new StructureBlockNode(this);
    }
    
    public int getId() {
        return m_block.getBlockId();
    }

    public double getTimeTaken() {
        return m_block.getTimeTakenThis();
    }
    
    public double getTimeTakenThis() {
        return m_block.getTimeTakenThis();
    }
    
    public long getNumCycles() {
        return m_block.getNumCyclesThis();
    }

    public long getNumUnknowns() {
        return m_block.getNumUnknownsThis();
    }
    
    public String getDescription() {
        return getTimeTaken() + " ns";
    }
    
    public String getToolTipText() {
        String toolTipText = "";
        toolTipText += "Block ID: " + m_block.getBlockId() + "\n";
        toolTipText += "----------------------------------\n";
        toolTipText += "Time taken: " + getTimeTaken() + " ns\n";
        toolTipText += "Number of cycles: " + getNumCycles() + "\n";
        toolTipText += "Number of FNOPs: " + m_block.getNumFNopsThis() + "\n";
        toolTipText += "Number of pausables: " + m_block.getNumPausablesThis() + "\n";
        toolTipText += "Number of unknowns: " + m_block.getNumUnknownsThis();
        
        
        return toolTipText;
    }

    public String getTypeName() {
        return "block";
    }
    
    public HashSet<Integer> getPcsCovered() {
    	return m_block.getPcsCoveredThis();
    }
    
    BlockNode m_this = this;
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
				console.runCommand("print trace " + routeId + " b" + m_this.getId());
			}
		});    	
    }

	public long getNumIterations() {
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
		return new Vector<XtaInstruction>(m_block.getInstructionStream());
	}

	public Vector<XtaInstruction> getUnknowns() {
		return new Vector<XtaInstruction>(m_block.getUnknowns());
	}

	public void getFnopPcs(Vector<XtaInstruction> fnops) {
		for (XtaInstruction inst : m_block.getInstructionStream()) {
			if (inst.isFnop() && !fnops.contains(inst)) {
				fnops.add(inst);
			}
		}
	}

	public void getBlockIds(Vector<Integer> ids) {
		ids.add(m_block.getBasicBlock().getId());
	}
}
