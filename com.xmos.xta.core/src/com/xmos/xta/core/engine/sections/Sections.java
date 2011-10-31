package com.xmos.xta.core.engine.sections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.util.CoreId;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.references.Reference;

public class Sections {
	private XeLoader m_xeLoader;
	private Notifier m_notifier;
	
	private Collection<Section> m_llvmSrcLabel;
	private Collection<Section> m_fileTables;
	
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
		
		m_llvmSrcLabel = new ArrayList<Section>(); 
		m_fileTables = new ArrayList<Section>();
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
		
		m_llvmSrcLabel.clear();
		m_fileTables.clear();
		
		for (CoreId coreId : m_xeLoader.getCoreIds()) {
			
			//All filenames are stored in this table and their index is used by the 
			FileTable ft = new FileTable(coreId, ".file_table", m_notifier);
			
			m_fileTables.add(ft);
			
			m_endpoints.add(new CodePoints(coreId, ".xtaendpoint", m_notifier, ft));
			m_calls.add(new CodePoints(coreId, ".xtacall", m_notifier, ft));
			m_llvmSrcLabel.add(new LLVMSrcLabelTable(coreId, ".xtasrclabletabel", m_notifier, ft));
			
			m_endpointTables.add(new CodePointTable(coreId, ".xtaendpoint", m_notifier, ft));
			m_callTables.add(new CodePointTable(coreId, ".xtacall", m_notifier, ft));
			
			m_endpointTables.add(new CodePointTable(coreId, ".xtaendpointtable", m_notifier, ft));
			m_callTables.add(new CodePointTable(coreId, ".xtacalltable", m_notifier, ft));
			
			
			m_branches.add(new Branches(coreId, m_notifier));
			m_labels.add(new Labels(coreId, m_notifier));
			m_srcLabels.add(new CodePoints(coreId, ".xtalabel", m_notifier));
			m_srcLabelTables.add(new SrcLabelTable(coreId, ".xtalabeltable", m_notifier));
			
			
			

			m_threadStartPoints.add(new ThreadDelimiters(coreId, ".xtathreadstart", m_notifier));
			m_threadStopPoints.add(new ThreadDelimiters(coreId, ".xtathreadstop", m_notifier));
			m_srcLevelCommands.add(new SrcLevelCommands(coreId, m_notifier));
			m_srcLevelLoops.add(new SrcLevelLoops(coreId, m_notifier));
			m_systems.add(new Systems(coreId, m_notifier));
			m_recursiveFunctions.add(new StringSection(coreId, m_notifier));
			m_unreachableFunctions.add(new StringSection(coreId, m_notifier));
		}
		
		if (!loadSection(src, m_fileTables) ||
			!loadSection(src, m_llvmSrcLabel) ||
			!loadSection(src, m_branches) ||
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
		
		//now sort the llvm src labels into the correct tables
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
		Branches branches = (Branches)getSection(m_branches, coreId);
		return branches.get();
	}

	public Map<String, Set<Long>> getLabels(CoreId coreId) {
	    Labels labels = (Labels)getSection(m_labels, coreId);
	    return labels.get();
	}

	public List<CodePointInfo> getSrcLabels(CoreId coreId) {
		CodePoints codePoints = (CodePoints)getSection(m_srcLabels, coreId);
	    return codePoints.get();
	}

	public Map<Long, SrcLabelTableInfo> getSrcLabelTable(CoreId coreId) {
		SrcLabelTable srcLabelTable = (SrcLabelTable)getSection(m_srcLabelTables, coreId);
		return srcLabelTable.get();
	}

	public List<CodePointInfo> getEndpoints(CoreId coreId) {
		CodePoints codePoints = (CodePoints)getSection(m_endpoints, coreId);
	    return codePoints.get();
	}

	public Map<Long, CodePointTableInfo> getEndpointTable(CoreId coreId) {
		CodePointTable endpointTable = (CodePointTable)getSection(m_endpointTables, coreId);
		return endpointTable.get();
	}

	public List<CodePointInfo> getCalls(CoreId coreId) {
		CodePoints codePoints = (CodePoints)getSection(m_calls, coreId);
	    return codePoints.get();
	}

	public Map<Long, CodePointTableInfo> getCallTable(CoreId coreId) {
		CodePointTable endpointTable = (CodePointTable)getSection(m_callTables, coreId);
		return endpointTable.get();
	}

	public Collection<Long> getThreadStartPoints(CoreId coreId) {
		ThreadDelimiters threadDelimiter = (ThreadDelimiters)getSection(m_threadStartPoints, coreId);
		return threadDelimiter.get();
	}

	public Collection<Long> getThreadStopPoints(CoreId coreId) {
		ThreadDelimiters threadDelimiter = (ThreadDelimiters)getSection(m_threadStopPoints, coreId);
		return threadDelimiter.get();
	}

	public Collection<SrcStringInfo> getSrcLevelCommands(CoreId coreId) {
		SrcLevelCommands srcLevelCommands = (SrcLevelCommands)getSection(m_srcLevelCommands, coreId);
		return srcLevelCommands.get();
	}

	public Collection<SrcLevelLoopInfo> getSrcLevelLoops(CoreId coreId) {
		SrcLevelLoops loops = (SrcLevelLoops)getSection(m_srcLevelLoops, coreId);
		return loops.get();
	}

	public Collection<String> getSystems(CoreId coreId) {
		Systems systems = (Systems)getSection(m_systems, coreId);
		return systems.get();
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
}
