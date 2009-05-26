package com.xmos.xta.engine.sections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Notifier;

public class Sections {	
	private XeLoader m_xeLoader;
	private Notifier m_notifier;
	private Collection<Section> m_branches;
	private Collection<Section> m_labels;
	private Collection<Section> m_srcLabels;
	private Collection<Section> m_endpoints;
	private Collection<Section> m_srcLabelTables;
	private Collection<Section> m_endpointTables;
	private Collection<Section> m_threadStartPoints;
	private Collection<Section> m_threadStopPoints;
	private Collection<Section> m_srcLevelCommands;	
	
    public Sections() { 
    	m_xeLoader = null;
    	m_notifier = null;
		m_branches  = new ArrayList<Section>();
		m_labels    = new ArrayList<Section>();
		m_srcLabels = new ArrayList<Section>();
		m_endpoints = new ArrayList<Section>();
		m_srcLabelTables = new ArrayList<Section>();
		m_endpointTables = new ArrayList<Section>();
		m_threadStartPoints = new ArrayList<Section>();
		m_threadStopPoints = new ArrayList<Section>();
		m_srcLevelCommands = new ArrayList<Section>();
    }

	public boolean load(XeLoader xeLoader, Notifier notifier) {		
		m_xeLoader = xeLoader;
		m_notifier = notifier;
		m_branches.clear();
		m_labels.clear();
		m_srcLabels.clear();
		m_srcLabelTables.clear();
		m_endpoints.clear();
		m_endpointTables.clear();
		m_threadStartPoints.clear();
		m_threadStopPoints.clear();
		m_srcLevelCommands.clear();

		for (int node : m_xeLoader.getNodeIds()) {
			for (int core : m_xeLoader.getCoreIds(node)) {
				m_branches.add(new Branches(node, core, m_notifier));
				m_labels.add(new Labels(node, core, m_notifier));
				m_srcLabels.add(new CodePoints(node, core, ".xtalabel", m_notifier, "label"));
				m_endpoints.add(new CodePoints(node, core, ".xtaendpoint", m_notifier, "endpoint"));
				m_srcLabelTables.add(new SrcLabelTable(node, core, ".xtalabeltable", m_notifier));
				m_endpointTables.add(new EndpointTable(node, core, ".xtaendpointtable", m_notifier));
				m_threadStartPoints.add(new ThreadDelimiters(node, core, ".xtathreadstart", m_notifier));
				m_threadStopPoints.add(new ThreadDelimiters(node, core, ".xtathreadstop", m_notifier));
				m_srcLevelCommands.add(new SrcLevelCommands(node, core, m_notifier));
			}
		}
				
		if (!loadSection(m_branches))
			return false;
		if (!loadSection(m_labels))
			return false;
		if (!loadSection(m_srcLabels))
			return false;
		if (!loadSection(m_endpoints))
			return false;
		if (!loadSection(m_srcLabelTables))
			return false;
		if (!loadSection(m_endpointTables))
		    return false;
		if (!loadSection(m_threadStartPoints))
			return false;
		if (!loadSection(m_threadStopPoints))
			return false;
		if (!loadSection(m_srcLevelCommands))
			return false;
		
		return true;
	}	
	
	public boolean addToBranches(int nodeId, int coreId, int fromPc, int toPc) {
		Branches branches = (Branches)getSection(m_branches, nodeId, coreId);
		return branches.add(fromPc, toPc);
	}

	public boolean removeFromBranches(int nodeId, int coreId, int fromPc) {
		Branches branches = (Branches)getSection(m_branches, nodeId, coreId);
		return branches.remove(fromPc);			
	}	
	
	public Map<Integer, TreeSet<Integer>> getBranches(int nodeId, int coreId) {
		Branches branches = (Branches)getSection(m_branches, nodeId, coreId);
		return branches.get();
	}

	public boolean addToLabels(int nodeId, int coreId, String label, int pc) {
		Labels labels = (Labels)getSection(m_labels, nodeId, coreId);
		return labels.add(label, pc);
	}

	public boolean removeFromLabels(int nodeId, int coreId, String label) {
		Labels labels = (Labels)getSection(m_labels, nodeId, coreId);
		return labels.remove(label);
	}

	public Map<String, Integer> getLabels(int nodeId, int coreId) {
	    Labels labels = (Labels)getSection(m_labels, nodeId, coreId);
	    return labels.get();
	}
	
	public boolean addToSrcLabels(int nodeId, int coreId, String label, int pc) {
		CodePoints codePoints = (CodePoints)getSection(m_srcLabels, nodeId, coreId);
		return codePoints.add(label, pc);
	}
		
	public boolean removeFromSrcLabels(int nodeId, int coreId, String label) {
		CodePoints codePoints = (CodePoints)getSection(m_srcLabels, nodeId, coreId);
		return codePoints.remove(label);
	}
	
	public Map<String, CodePointInfo> getSrcLabels(int nodeId, int coreId) {
		CodePoints codePoints = (CodePoints)getSection(m_srcLabels, nodeId, coreId);
	    return codePoints.get();
	}
	
	public Map<Integer, SrcLabelTableInfo> getSrcLabelTable(int nodeId, int coreId) {
		SrcLabelTable srcLabelTable = (SrcLabelTable)getSection(m_srcLabelTables, nodeId, coreId);
		return srcLabelTable.get();
	}	

	public boolean addToEndpoints(int nodeId, int coreId, String endpoint, int pc) {
		CodePoints codePoints = (CodePoints)getSection(m_endpoints, nodeId, coreId);
		return codePoints.add(endpoint, pc);
	}

	public boolean removeFromEndpoints(int nodeId, int coreId, String label) {
		CodePoints codePoints = (CodePoints)getSection(m_endpoints, nodeId, coreId);
		return codePoints.remove(label);
	}	
	
	public Map<String, CodePointInfo> getEndpoints(int nodeId, int coreId) {
		CodePoints codePoints = (CodePoints)getSection(m_endpoints, nodeId, coreId);
	    return codePoints.get();
	}
	
	public Map<Integer, EndpointTableInfo> getEndpointTable(int nodeId, int coreId) {
		EndpointTable endpointTable = (EndpointTable)getSection(m_endpointTables, nodeId, coreId);
		return endpointTable.get();		
	}
		
	public boolean addToThreadStartPoints(int nodeId, int coreId, int pc) {
		ThreadDelimiters threadDelimiter = (ThreadDelimiters)getSection(m_threadStartPoints, nodeId, coreId);
		return threadDelimiter.add(pc);
	}

	public boolean removeFromThreadStartPoints(int nodeId, int coreId, int pc) {
		ThreadDelimiters threadDelimiter = (ThreadDelimiters)getSection(m_threadStartPoints, nodeId, coreId);
		return threadDelimiter.remove(pc);
	}
	
	public Collection<Integer> getThreadStartPoints(int nodeId, int coreId) {
		ThreadDelimiters threadDelimiter = (ThreadDelimiters)getSection(m_threadStartPoints, nodeId, coreId);				
		return threadDelimiter.get();	
	}
	
	public boolean addToThreadStopPoints(int nodeId, int coreId, int pc) {
		ThreadDelimiters threadDelimiter = (ThreadDelimiters)getSection(m_threadStopPoints, nodeId, coreId);
		return threadDelimiter.add(pc);
	}

	public boolean removeFromThreadStopPoints(int nodeId, int coreId, int pc) {
		ThreadDelimiters threadDelimiter = (ThreadDelimiters)getSection(m_threadStopPoints, nodeId, coreId);
		return threadDelimiter.remove(pc);
	}
					
	public Collection<Integer> getThreadStopPoints(int nodeId, int coreId) {
		ThreadDelimiters threadDelimiter = (ThreadDelimiters)getSection(m_threadStopPoints, nodeId, coreId);				
		return threadDelimiter.get();	
	}
	
	public Collection<String> getSrcLevelCommands(int nodeId, int coreId) {
		SrcLevelCommands srcLevelCommands = (SrcLevelCommands)getSection(m_srcLevelCommands, nodeId, coreId);
		return srcLevelCommands.get();		
	}
			
	private boolean loadSection(Collection<Section> sections) {
		for (Section section : sections) {
			if (!section.load(m_xeLoader)) {
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
