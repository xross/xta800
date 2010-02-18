package com.xmos.xta.core.engine.sections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

		for (int nodeNumber : m_xeLoader.getNodeNumbers()) {
			for (int coreNumber : m_xeLoader.getCoreNumbers(nodeNumber)) {
				m_branches.add(new Branches(nodeNumber, coreNumber, m_notifier));
				m_labels.add(new Labels(nodeNumber, coreNumber, m_notifier));
				m_srcLabels.add(new CodePoints(nodeNumber, coreNumber, ".xtalabel", m_notifier));
				m_endpoints.add(new CodePoints(nodeNumber, coreNumber, ".xtaendpoint", m_notifier));
				m_calls.add(new CodePoints(nodeNumber, coreNumber, ".xtacall", m_notifier));
				m_srcLabelTables.add(new SrcLabelTable(nodeNumber, coreNumber, ".xtalabeltable", m_notifier));
				m_endpointTables.add(new CodePointTable(nodeNumber, coreNumber, ".xtaendpointtable", m_notifier));
				m_callTables.add(new CodePointTable(nodeNumber, coreNumber, ".xtacalltable", m_notifier));
				m_threadStartPoints.add(new ThreadDelimiters(nodeNumber, coreNumber, ".xtathreadstart", m_notifier));
				m_threadStopPoints.add(new ThreadDelimiters(nodeNumber, coreNumber, ".xtathreadstop", m_notifier));
				m_srcLevelCommands.add(new SrcLevelCommands(nodeNumber, coreNumber, m_notifier));
				m_srcLevelLoops.add(new SrcLevelLoops(nodeNumber, coreNumber, m_notifier));
				m_systems.add(new Systems(nodeNumber, coreNumber, m_notifier));
				m_recursiveFunctions.add(new StringSection(nodeNumber, coreNumber, m_notifier));
				m_unreachableFunctions.add(new StringSection(nodeNumber, coreNumber, m_notifier));
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

	public boolean addToBranches(CommandSource src, int nodeNumber, int coreNumber,
			Reference from, Collection<Reference> to) {

		Branches branches = (Branches)getSection(m_branches, nodeNumber, coreNumber);
		return branches.add(src, from, to, true);
	}

	public boolean removeFromBranches(CommandSource src, int nodeNumber, int coreNumber,
			Reference fromRef, Collection<Reference> toRefs) {

		Branches branches = (Branches)getSection(m_branches, nodeNumber, coreNumber);
		return branches.remove(src, fromRef, toRefs);
	}

	public Map<Reference, Set<BranchInfo>> getBranches(int nodeNumber, int coreNumber) {
		Branches branches = (Branches)getSection(m_branches, nodeNumber, coreNumber);
		return branches.get();
	}

	public Map<String, Set<Long>> getLabels(int nodeNumber, int coreNumber) {
	    Labels labels = (Labels)getSection(m_labels, nodeNumber, coreNumber);
	    return labels.get();
	}

	public List<CodePointInfo> getSrcLabels(int nodeNumber, int coreNumber) {
		CodePoints codePoints = (CodePoints)getSection(m_srcLabels, nodeNumber, coreNumber);
	    return codePoints.get();
	}

	public Map<Long, SrcLabelTableInfo> getSrcLabelTable(int nodeNumber, int coreNumber) {
		SrcLabelTable srcLabelTable = (SrcLabelTable)getSection(m_srcLabelTables, nodeNumber, coreNumber);
		return srcLabelTable.get();
	}

	public List<CodePointInfo> getEndpoints(int nodeNumber, int coreNumber) {
		CodePoints codePoints = (CodePoints)getSection(m_endpoints, nodeNumber, coreNumber);
	    return codePoints.get();
	}

	public Map<Long, CodePointTableInfo> getEndpointTable(int nodeNumber, int coreNumber) {
		CodePointTable endpointTable = (CodePointTable)getSection(m_endpointTables, nodeNumber, coreNumber);
		return endpointTable.get();
	}

	public List<CodePointInfo> getCalls(int nodeNumber, int coreNumber) {
		CodePoints codePoints = (CodePoints)getSection(m_calls, nodeNumber, coreNumber);
	    return codePoints.get();
	}

	public Map<Long, CodePointTableInfo> getCallTable(int nodeNumber, int coreNumber) {
		CodePointTable endpointTable = (CodePointTable)getSection(m_callTables, nodeNumber, coreNumber);
		return endpointTable.get();
	}

	public Collection<Long> getThreadStartPoints(int nodeNumber, int coreNumber) {
		ThreadDelimiters threadDelimiter = (ThreadDelimiters)getSection(m_threadStartPoints, nodeNumber, coreNumber);
		return threadDelimiter.get();
	}

	public Collection<Long> getThreadStopPoints(int nodeNumber, int coreNumber) {
		ThreadDelimiters threadDelimiter = (ThreadDelimiters)getSection(m_threadStopPoints, nodeNumber, coreNumber);
		return threadDelimiter.get();
	}

	public Collection<SrcStringInfo> getSrcLevelCommands(int nodeNumber, int coreNumber) {
		SrcLevelCommands srcLevelCommands = (SrcLevelCommands)getSection(m_srcLevelCommands, nodeNumber, coreNumber);
		return srcLevelCommands.get();
	}

	public Collection<SrcLevelLoopInfo> getSrcLevelLoops(int nodeNumber, int coreNumber) {
		SrcLevelLoops loops = (SrcLevelLoops)getSection(m_srcLevelLoops, nodeNumber, coreNumber);
		return loops.get();
	}

	public Collection<String> getSystems(int nodeNumber, int coreNumber) {
		Systems systems = (Systems)getSection(m_systems, nodeNumber, coreNumber);
		return systems.get();
	}

	public Collection<String> getRecursiveFunctions(int nodeNumber, int coreNumber) {
		StringSection strings = (StringSection)getSection(m_recursiveFunctions, nodeNumber, coreNumber);
		return strings.get();
	}

	public Collection<String> getUnreachableFunctions(int nodeNumber, int coreNumber) {
		StringSection strings = (StringSection)getSection(m_unreachableFunctions, nodeNumber, coreNumber);
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

	private Section getSection(Collection<Section> sections, int nodeNumber, int coreNumber) {
		for (Section section : sections) {
			if ((section.getNodeNumber() == nodeNumber) &&
				(section.getCoreNumber() == coreNumber)) {
				return section;
			}
		}
		assert(false);
		return null;
	}
}
