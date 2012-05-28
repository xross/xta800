package com.xmos.xta.core.engine.sections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.util.IteratableSet;
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
		
		for (CoreId coreId : m_xeLoader.getCoreIds()) {			
			m_branches.add(new Branches(coreId, m_notifier));
			m_labels.add(new Labels(coreId, m_notifier));
			m_srcLabels.add(new CodePoints(coreId, ".xtalabel", m_notifier, true));
			m_endpoints.add(new CodePoints(coreId, ".xtaendpoint", m_notifier, false));
			m_calls.add(new CodePoints(coreId, ".xtacall", m_notifier, false));
			m_srcLabelTables.add(new SrcLabelTable(coreId, ".xtalabeltable", m_notifier));
			m_endpointTables.add(new CodePointTable(coreId, ".xtaendpointtable", m_notifier));
			m_callTables.add(new CodePointTable(coreId, ".xtacalltable", m_notifier));
			m_threadStartPoints.add(new ThreadDelimiters(coreId, ".xtathreadstart", m_notifier));
			m_threadStopPoints.add(new ThreadDelimiters(coreId, ".xtathreadstop", m_notifier));
			m_srcLevelCommands.add(new SrcLevelCommands(coreId, m_notifier));
			m_srcLevelLoops.add(new SrcLevelLoops(coreId, m_notifier));
			m_systems.add(new Systems(coreId, m_notifier));
		}
		
		if (!loadSection(src, m_branches) ||
			!loadSection(src, m_labels) ||
			!loadSection(src, m_srcLabels) ||
			!loadSection(src, m_endpoints) ||
			!loadSection(src, m_calls) ||
			!loadSection(src, m_srcLabelTables) ||
			!loadSection(src, m_endpointTables) ||
			!loadSection(src, m_callTables) ||
			!loadSection(src, m_threadStartPoints) ||
			!loadSection(src, m_threadStopPoints) ||
			!loadSection(src, m_srcLevelCommands) ||
			!loadSection(src, m_srcLevelLoops) ||
			!loadSection(src, m_systems)){
			return false;
		}
		return true;
	}

	public boolean addToBranches(CommandSource src, CoreId coreId, Reference from, Collection<Reference> to) {
		Branches branches = (Branches)getSection(m_branches, coreId);
		return branches.add(src, from, to, true);
	}

	public boolean removeFromBranches(CommandSource src, CoreId coreId, Reference fromRef, Collection<Reference> toRefs) {
		Branches branches = (Branches)getSection(m_branches, coreId);
		return branches.remove(src, fromRef, toRefs);
	}

	public Map<Reference, Set<BranchInfo>> getBranches(CoreId coreId) {
		Map<Reference, Set<BranchInfo>> collections = new IteratableMap<Reference, Set<BranchInfo>>();
		for(Section s : getSections(m_branches, coreId))
			collections.putAll(((Branches)s).get());
		return collections;
	}

	public Map<String, Set<Long>> getLabels(CoreId coreId) {
		Map<String, Set<Long>> collections = new IteratableMap<String, Set<Long>>();
		for(Section s : getSections(m_labels, coreId))
			collections.putAll(((Labels)s).get());
		return collections;
	}

	public List<CodePointInfo> getSrcLabels(CoreId coreId) {
		List<CodePointInfo> collections = new ArrayList<CodePointInfo>();
		for(Section s : getSections(m_srcLabels, coreId))
			collections.addAll(((CodePoints)s).get());
		return collections;
	}

	public IteratableSet<SrcLabelTableInfo> getSrcLabelTable(CoreId coreId) {
		IteratableSet<SrcLabelTableInfo> collections = new IteratableSet<SrcLabelTableInfo>();
		for(Section s : getSections(m_srcLabelTables, coreId))
			collections.addAll(((SrcLabelTable)s).get());
		return collections;
	}

	public List<CodePointInfo> getEndpoints(CoreId coreId) {
		List<CodePointInfo> collections = new ArrayList<CodePointInfo>();
		for(Section s : getSections(m_endpoints, coreId))
			collections.addAll(((CodePoints)s).get());
		return collections;
	}

	public IteratableSet< CodePointTableInfo> getEndpointTable(CoreId coreId) {
		IteratableSet< CodePointTableInfo> collections = new IteratableSet< CodePointTableInfo>();
		for(Section s : getSections(m_endpointTables, coreId))
			collections.addAll(((CodePointTable)s).get());
		return collections;
	}

	public List<CodePointInfo> getCalls(CoreId coreId) {
		List<CodePointInfo> collections = new ArrayList<CodePointInfo>();
		for(Section s : getSections(m_calls, coreId))
			collections.addAll(((CodePoints)s).get());
		return collections;
	}

	public IteratableSet< CodePointTableInfo> getCallTable(CoreId coreId) {
		IteratableSet< CodePointTableInfo> collections = new IteratableSet< CodePointTableInfo>();
		for(Section s : getSections(m_callTables, coreId))
			collections.addAll(((CodePointTable)s).get());
		return collections;
	}

	public Collection<Long> getThreadStartPoints(CoreId coreId) {
		Collection<Long> collections = new ArrayList<Long>();
		for(Section s : getSections(m_threadStartPoints, coreId))
			collections.addAll(((ThreadDelimiters)s).get());
		return collections;
	}

	public Collection<Long> getThreadStopPoints(CoreId coreId) {
		Collection<Long> collections = new ArrayList<Long>();
		for(Section s : getSections(m_threadStopPoints, coreId))
			collections.addAll(((ThreadDelimiters)s).get());
		return collections;
	}

	public Collection<SrcStringInfo> getSrcLevelCommands(CoreId coreId) {
		Collection<SrcStringInfo> collections = new ArrayList<SrcStringInfo>();
		for(Section s : getSections(m_srcLevelCommands, coreId))
			collections.addAll(((SrcLevelCommands)s).get());
		return collections;
	}

	public Collection<SrcLevelLoopInfo> getSrcLevelLoops(CoreId coreId) {
		Collection<SrcLevelLoopInfo> collections = new ArrayList<SrcLevelLoopInfo>();
		for(Section s : getSections(m_srcLevelLoops, coreId))
			collections.addAll(((SrcLevelLoops)s).get());
		return collections;
	}

	public Collection<String> getSystems(CoreId coreId) {
		Collection<String> collections = new ArrayList<String>();
		for(Section s : getSections(m_systems, coreId))
			collections.addAll(((Systems)s).get());
		return collections;
	}
	
	private boolean loadSection(CommandSource src, Collection<Section> sections) {
		for (Section section : sections) {
			if (!section.load(src, m_xeLoader)) {
			    return false;
			}
		}
		return true;
	}
	
	private Section getSection(Collection<Section> sections, CoreId coreId) {
		for (Section section : sections) {
			if (section.getCoreId().equals(coreId)) {
				return section;
			}
		}
		assert(false);
		return null;
	}
	
	private Collection<Section> getSections(Collection<Section> sections, CoreId coreId) {
		Collection<Section> matchingSections =  new ArrayList<Section>();
		for (Section section : sections) {
			if (section.getCoreId().equals(coreId)) {
				matchingSections.add(section);
			}
		}
		return matchingSections;
	}
}
