package com.xmos.xta.ui.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.annotations.XtaAnnotations;
import com.xmos.xta.ui.dialogs.InstructionTime;

public class SourceActions  {

    public static final String ShowStartPointID      = "com.xmos.xta.ui.action.ShowStartPoint";
    public static final String ShowEndPointID        = "com.xmos.xta.ui.action.ShowEndPoint";
    public static final String SetFromEndPointID     = "com.xmos.xta.ui.action.SetFromEndPoint";
    public static final String SetToEndPointID       = "com.xmos.xta.ui.action.SetToEndPoint";
    public static final String RemoveFromEndPointID  = "com.xmos.xta.ui.action.RemoveFromEndPoint";
    public static final String RemoveToEndPointID    = "com.xmos.xta.ui.action.RemoveToEndPoint";
    public static final String AddExclusionID        = "com.xmos.xta.ui.action.AddExclusion";
    public static final String RemoveExclusionID     = "com.xmos.xta.ui.action.RemoveExclusion";
    public static final String SelectBlocksID        = "com.xmos.xta.ui.action.SelectBlocks";
    public static final String SetInstructionTimeID  = "com.xmos.xta.ui.action.SetInstructionTime";

    private Map<String, Action> m_actions;
    
    private String m_clickCodeRefString;

	public SourceActions(final Composite parent) {
		m_clickCodeRefString = "";
		m_actions = new TreeMap<String, Action>();
		
     	Action action = new Action("Show start point") {
     		public void run() {
     			GuiEngine.setCodePoint(GuiEngine.getEngine().getFromEndPoint());
				GuiEngine.broadcast(Broadcaster.BROADCAST_SHOW_CODE_POINT, "");
     		}
     	};
     	m_actions.put(ShowStartPointID, action);

     	action = new Action("Show end point") {
     		public void run() {
     			GuiEngine.setCodePoint(GuiEngine.getEngine().getToEndPoint());
				GuiEngine.broadcast(Broadcaster.BROADCAST_SHOW_CODE_POINT, "");
     		}
     	};
     	m_actions.put(ShowEndPointID, action);

     	action =  new Action("Set from endpoint") {
     		public void run() {
     			GuiEngine.getEngine().setFromEndPoint(GuiEngine.s_guiSrc, m_clickCodeRefString);
     		}
     	};
     	m_actions.put(SetFromEndPointID, action);
     	
     	action = new Action("Set to endpoint") {
     		public void run() {
     			GuiEngine.getEngine().setToEndPoint(GuiEngine.s_guiSrc, m_clickCodeRefString);
     		}
     	};
     	m_actions.put(SetToEndPointID, action);
     	
     	action = new Action("Remove from endpoint") {
     		public void run() {
     			GuiEngine.getEngine().removeFromEndPoint(GuiEngine.s_guiSrc);
     		}
     	};
     	m_actions.put(RemoveFromEndPointID, action);
     	
     	action = new Action("Remove to endpoint") {
     		public void run() {
     			GuiEngine.getEngine().removeToEndPoint(GuiEngine.s_guiSrc);
     		}
     	};
     	m_actions.put(RemoveToEndPointID, action);
     	
     	action = new Action("Add to exclusion list") {
     		public void run() {
     	    	GuiEngine.getEngine().addExclusion(GuiEngine.s_guiSrc, m_clickCodeRefString);		
     		}
     	};
     	m_actions.put(AddExclusionID, action);

     	action = new Action("Remove from exclusion list") {
     		public void run() {
     	    	GuiEngine.getEngine().removeExclusion(GuiEngine.s_guiSrc, m_clickCodeRefString);		
     		}
     	};
     	m_actions.put(RemoveExclusionID, action);
     	
     	action = new Action("Select blocks through here") {
     		public void run() {
     			Reference clickCodeRef = ReferenceFactory.createReference(m_clickCodeRefString, ReferenceClass.ANY_REF);
     			GuiEngine.setCodePoint(clickCodeRef);
     			GuiEngine.broadcast(Broadcaster.BROADCAST_SELECT_NODES, "");
     		}
     	};
     	m_actions.put(SelectBlocksID, action);
     	
     	action = new Action("Set instruction time") {
     		public void run() {
     			List<Node> nodes = new ArrayList<Node>();
     			Engine engine = GuiEngine.getEngine();
     	    	Reference reference = ReferenceFactory.createReference(m_clickCodeRefString, ReferenceClass.ANY_REF);
     	    	if (!reference.resolve(engine)) {
     	    		assert(false);
     	    	}
     	    	/*
     			try {
     				//FIXME
					//nodes.addAll(engine.getSelectedRoute().getReferencedNodes(reference));
				} catch (AbortedException e) { }
				*/
     			if(!nodes.isEmpty()){
     				new InstructionTime(parent.getShell(), nodes);
     			}
     			
     		}
     	};
     	m_actions.put(SetInstructionTimeID, action);
    }

	public Action get(String name) {
		return m_actions.get(name);
	}
	
	public void createRulerContextMenu(IMenuManager menu, XtaAnnotations annotations, long clickLineIndex) {
		m_clickCodeRefString = annotations.getLineDescription(clickLineIndex);
		if (m_clickCodeRefString != null) {
			if (annotations.isValidEndPoint(clickLineIndex)) {
				if (annotations.isStartPointSetForIndex(clickLineIndex)) {
				    menu.add(m_actions.get(SourceActions.RemoveFromEndPointID));
				} else {
				    menu.add(m_actions.get(SourceActions.SetFromEndPointID));
				}
				if (annotations.isEndPointSetForIndex(clickLineIndex)) {						
				    menu.add(m_actions.get(SourceActions.RemoveToEndPointID));
				} else {
				    menu.add(m_actions.get(SourceActions.SetToEndPointID));
				}
				menu.add(new Separator(ITextEditorActionConstants.GROUP_REST));
			}
			
			if (annotations.isExcluded(m_clickCodeRefString)) {
			    menu.add(m_actions.get(SourceActions.RemoveExclusionID));
			} else {
			    menu.add(m_actions.get(SourceActions.AddExclusionID));
			}
			
		    Action setInstructionTimeAction = m_actions.get(SourceActions.SetInstructionTimeID);
			menu.add(setInstructionTimeAction);
		    menu.add(m_actions.get(SourceActions.SelectBlocksID));
			
			int selectedRouteId = GuiEngine.getEngine().getSelectedRouteId();
			if ((GuiEngine.getEngine().getRoutes().size() > 0) &&
			     GuiEngine.getEngine().canInstructionPause(selectedRouteId, m_clickCodeRefString)) {
				
				setInstructionTimeAction.setEnabled(true);
			} else {
				setInstructionTimeAction.setEnabled(false);
			}
		}
	}
}
