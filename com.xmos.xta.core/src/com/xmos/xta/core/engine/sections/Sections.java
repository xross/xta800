package com.xmos.xta.core.engine.sections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.references.Reference;

public class Sections {	
	private XeLoader m_xeLoader;
	private Notifier m_notifier;
	private Collection<Section> m_branches;
	private Collection<Section> m_labels;
	private Collection<Section> m_srcLabels;
	private Collection<Section> m_endpoints;
	private Collection<Section> m_calls;
	private Collection<Section> m_srcLabelTables;
	private Collection<Section> m_endpointTables;
	private Collection<Section> m_callTables;
	private Collection<Section> m_threadStartPoints;
	private Collection<Section> m_threadStopPoints;
	private Collection<Section> m_srcLevelCommands;
	private Collection<Section> m_srcLevelLoops;
	private Collection<Section> m_systems;
	private Collection<Section> m_recursiveFunctions;
	private Collection<Section> m_unreachableFunctions;
	
    public Sections() { 
    	m_xeLoader = null;
    	m_notifier = null;
		m_branches  = new ArrayList<Section>();
		m_labels = new ArrayList<Section>();
		m_srcLabels = new ArrayList<Section>();
		m_endpoints = new ArrayList<Section>();
		m_calls = new ArrayList<Section>();
		m_srcLabelTables = new ArrayList<Section>();
		m_endpointTables = new ArrayList<Section>();
		m_callTables = new ArrayList<Section>();
		m_threadStartPoints = new ArrayList<Section>();
		m_threadStopPoints = new ArrayList<Section>();
		m_srcLevelCommands = new ArrayList<Section>();
		m_srcLevelLoops = new ArrayList<Section>();
		m_systems = new ArrayList<Section>();
		m_recursiveFunctions = new ArrayList<Section>();
		m_unreachableFunctions = new ArrayList<Section>();
    }

	public boolean load(CommandSource src, XeLoader xeLoader, Notifier notifier) {		
		m_xeLoader = xeLoader;
		m_notifier = notifier;
		m_branches.clear();
		m_labels.clear();
		m_srcLabels.clear();
		m_endpoints.clear();
		m_calls.clear();
		m_srcLabelTables.clear();
		m_endpointTables.clear();
		m_callTables.clear();
		m_threadStartPoints.clear();
		m_threadStopPoints.clear();
		m_srcLevelCommands.clear();
		m_srcLevelLoops.clear();
		m_systems.clear();
		m_recursiveFunctions.clear();
		m_unreachableFunctions.clear();

		for (int node : m_xeLoader.getNodeIds()) {
			for (int core : m_xeLoader.getCoreIds(node)) {
				m_branches.add(new Branches(node, core, m_notifier));
				m_labels.add(new Labels(node, core, m_notifier));
				m_srcLabels.add(new CodePoints(node, core, ".xtalabel", m_notifier, "label"));
				m_endpoints.add(new CodePoints(node, core, ".xtaendpoint", m_notifier, "endpoint"));
				m_calls.add(new CodePoints(node, core, ".xtacall", m_notifier, "call"));
				m_srcLabelTables.add(new SrcLabelTable(node, core, ".xtalabeltable", m_notifier));
				m_endpointTables.add(new CodePointTable(node, core, ".xtaendpointtable", m_notifier));
				m_callTables.add(new CodePointTable(node, core, ".xtacalltable", m_notifier));
				m_threadStartPoints.add(new ThreadDelimiters(node, core, ".xtathreadstart", m_notifier));
				m_threadStopPoints.add(new ThreadDelimiters(node, core, ".xtathreadstop", m_notifier));
				m_srcLevelCommands.add(new SrcLevelCommands(node, core, m_notifier));
				m_srcLevelLoops.add(new SrcLevelLoops(node, core, m_notifier));
				m_systems.add(new Systems(node, core, m_notifier));
				m_recursiveFunctions.add(new StringSection(node, core, m_notifier));
				m_unreachableFunctions.add(new StringSection(node, core, m_notifier));
			}
		}
				
		if (!loadSection(src, m_branches))
			return false;
		if (!loadSection(src, m_labels))
			return false;
		if (!loadSection(src, m_srcLabels))
			return false;
		if (!loadSection(src, m_endpoints))
			return false;
		if (!loadSection(src, m_calls))
			return false;
		if (!loadSection(src, m_srcLabelTables))
			return false;
		if (!loadSection(src, m_endpointTables))
		    return false;
		if (!loadSection(src, m_callTables))
		    return false;
		if (!loadSection(src, m_threadStartPoints))
			return false;
		if (!loadSection(src, m_threadStopPoints))
			return false;
		if (!loadSection(src, m_srcLevelCommands))
			return false;
		if (!loadSection(src, m_srcLevelLoops))
			return false;
		if (!loadSection(src, m_systems))
			return false;
		
		return true;
	}	
	
	public boolean addToBranches(CommandSource src, int nodeId, int coreId, Reference from, Collection<Reference> to) {
		Branches branches = (Branches)getSection(m_branches, nodeId, coreId);
		return branches.add(src, from, to, true);
	}

	public boolean removeFromBranches(CommandSource src, int nodeId, int coreId,
			Reference fromRef, Collection<Reference> toRefs) {
		
		Branches branches = (Branches)getSection(m_branches, nodeId, coreId);
		return branches.remove(src, fromRef, toRefs);			
	}	

	public Map<Reference, Set<BranchInfo>> getBranches(int nodeId, int coreId) {
		Branches branches = (Branches)getSection(m_branches, nodeId, coreId);
		return branches.get();
	}

	public Map<String, Long> getLabels(int nodeId, int coreId) {
	    Labels labels = (Labels)getSection(m_labels, nodeId, coreId);
	    return labels.get();
	}
	
	public Map<String, CodePointInfo> getSrcLabels(int nodeId, int coreId) {
		CodePoints codePoints = (CodePoints)getSection(m_srcLabels, nodeId, coreId);
	    return codePoints.get();
	}
	
	public Map<Long, SrcLabelTableInfo> getSrcLabelTable(int nodeId, int coreId) {
		SrcLabelTable srcLabelTable = (SrcLabelTable)getSection(m_srcLabelTables, nodeId, coreId);
		return srcLabelTable.get();
	}	

	public Map<String, CodePointInfo> getEndpoints(int nodeId, int coreId) {
		CodePoints codePoints = (CodePoints)getSection(m_endpoints, nodeId, coreId);
	    return codePoints.get();
	}

	public Map<Long, CodePointTableInfo> getEndpointTable(int nodeId, int coreId) {
		CodePointTable endpointTable = (CodePointTable)getSection(m_endpointTables, nodeId, coreId);
		return endpointTable.get();		
	}	

	public Map<String, CodePointInfo> getCalls(int nodeId, int coreId) {
		CodePoints codePoints = (CodePoints)getSection(m_calls, nodeId, coreId);
	    return codePoints.get();
	}
	
	public Map<Long, CodePointTableInfo> getCallTable(int nodeId, int coreId) {
		CodePointTable endpointTable = (CodePointTable)getSection(m_callTables, nodeId, coreId);
		return endpointTable.get();		
	}
		
	public Collection<Long> getThreadStartPoints(int nodeId, int coreId) {
		ThreadDelimiters threadDelimiter = (ThreadDelimiters)getSection(m_threadStartPoints, nodeId, coreId);				
		return threadDelimiter.get();	
	}
	
	public Collection<Long> getThreadStopPoints(int nodeId, int coreId) {
		ThreadDelimiters threadDelimiter = (ThreadDelimiters)getSection(m_threadStopPoints, nodeId, coreId);				
		return threadDelimiter.get();	
	}
	
	public Collection<SrcStringInfo> getSrcLevelCommands(int nodeId, int coreId) {
		SrcLevelCommands srcLevelCommands = (SrcLevelCommands)getSection(m_srcLevelCommands, nodeId, coreId);
		return srcLevelCommands.get();		
	}

	public Collection<SrcLevelLoopInfo> getSrcLevelLoops(int nodeId, int coreId) {
		SrcLevelLoops loops = (SrcLevelLoops)getSection(m_srcLevelLoops, nodeId, coreId);
		return loops.get();		
	}
	
	public Collection<String> getSystems(int nodeId, int coreId) {
		Systems systems = (Systems)getSection(m_systems, nodeId, coreId);
		return systems.get();
	}

	public Collection<String> getRecursiveFunctions(int nodeId, int coreId) {
		StringSection strings = (StringSection)getSection(m_recursiveFunctions, nodeId, coreId);
		return strings.get();		
	}

	public Collection<String> getUnreachableFunctions(int nodeId, int coreId) {
		StringSection strings = (StringSection)getSection(m_unreachableFunctions, nodeId, coreId);
		return strings.get();		
	}

	private boolean loadSection(CommandSource src, Collection<Section> sections) {
		for (Section section : sections) {
			if (!section.load(src, m_xeLoader)) {
			    return false;
			}
		}
		return true;
	}	
 	
	private Section getSection(Collection<Section> sections, int nodeId, int coreId) {
		for (Section section : sections) {
			if ((section.getNodeId() == nodeId) &&
				(section.getCoreId() == coreId)) {
				return section;
			}
		}
		assert(false);
		return null;
	}
}
