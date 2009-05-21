package com.xmos.xta.engine.sections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.PatternSyntaxException;

import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.Notifier;

public class Sections {	
	private XeLoader m_xeLoader;
	private Notifier m_notifier;
	private ArrayList<BranchMap> m_branches;
	private ArrayList<LabelMap> m_labels;
	private ArrayList<CodePointMap> m_srcLabels;
	private ArrayList<CodePointMap> m_endpoints;
	private ArrayList<SrcLabelTableMap> m_srcLabelTables;
	private ArrayList<EndpointTableMap> m_endpointTables;
	private ArrayList<ThreadDelimiters> m_threadStartPoints;
	private ArrayList<ThreadDelimiters> m_threadStopPoints;
	private ArrayList<SrcLevelCommands> m_srcLevelCommands;	
	
    public Sections() { 
    	m_xeLoader = null;
    	m_notifier = null;
		m_branches  = new ArrayList<BranchMap>();
		m_labels    = new ArrayList<LabelMap>();
		m_srcLabels = new ArrayList<CodePointMap>();
		m_endpoints = new ArrayList<CodePointMap>();
		m_srcLabelTables = new ArrayList<SrcLabelTableMap>();
		m_endpointTables = new ArrayList<EndpointTableMap>();
		m_threadStartPoints = new ArrayList<ThreadDelimiters>();
		m_threadStopPoints = new ArrayList<ThreadDelimiters>();
		m_srcLevelCommands = new ArrayList<SrcLevelCommands>();
    }
		
	public void initialise(XeLoader xeLoader, Notifier notifier) {
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
				m_branches.add(new BranchMap(node, core, m_notifier));
				m_labels.add(new LabelMap(node, core));
				m_srcLabels.add(new CodePointMap(node, core, ".xtalabel", m_notifier, "label"));
				m_endpoints.add(new CodePointMap(node, core, ".xtaendpoint", m_notifier, "endpoint"));
				m_srcLabelTables.add(new SrcLabelTableMap(node, core, ".xtalabeltable", m_notifier));
				m_endpointTables.add(new EndpointTableMap(node, core, ".xtaendpointtable", m_notifier));
				m_threadStartPoints.add(new ThreadDelimiters(node, core, ".xtathreadstart", m_notifier));
				m_threadStopPoints.add(new ThreadDelimiters(node, core, ".xtathreadstop", m_notifier));
				m_srcLevelCommands.add(new SrcLevelCommands(node, core, m_notifier));
			}
		}
	}

	public boolean load() {
		assert(m_xeLoader != null);
		
		if (!loadBranchMaps()) 
			return false;		
		if (!loadLabelMaps())
		    return false;		
		if (!loadCodePointMaps(m_srcLabels))
		    return false;		
		if (!loadCodePointMaps(m_endpoints))
		    return false;		
		if (!loadSrcLabelTables())
		    return false;		
		if (!loadEndpointTables())
		    return false;
		if (!loadThreadDelimiters(m_threadStartPoints))
			return false;
		if (!loadThreadDelimiters(m_threadStopPoints))
			return false;
		if (!loadSrcLevelCommands())
			return false;
		
		return true;
	}	
	
	public boolean addToBranchMap(int nodeId, int coreId, int fromPc, int toPc) {
		BranchMap branchMap = getBranchMapNull(nodeId, coreId);
		if (branchMap != null) {
			branchMap.add(fromPc, toPc);
			return true;
		}
		return false;
	}

	public boolean removeFromBranchMap(int nodeId, int coreId, int fromPc) {
		BranchMap branchMap = getBranchMapNull(nodeId, coreId);
		if (branchMap != null) {
			if (branchMap.getMap().containsKey(fromPc)) {
				branchMap.remove(fromPc);
				return true;
			} else {
				m_notifier.notify(Notifier.NOTIFY_ERROR, String.format(
						"Unable to remove branch: 0x%x", fromPc));
				return false;
			}
		}
		return false;
	}	
	
	public boolean addToLabelMap(int nodeId, int coreId, String label, int pc) {
		LabelMap labelMap = getLabelMapNull(nodeId, coreId);
		if (labelMap != null) {
			if (!labelMap.getMap().containsKey(label)) {										
				labelMap.getMap().put(label, pc);
			} else {
				m_notifier.notify(Notifier.NOTIFY_ERROR, String.format(
						"Label already exists: %s", label));
			}
			return true;
		}
		return false;
	}

	public void removeFromLabelMap(int nodeId, int coreId, String label) {
		LabelMap labelMap = getLabelMapNull(nodeId, coreId);
		if (labelMap != null) {
			List<String> matches = new ArrayList<String>();

			for (String mapEntry : labelMap.getMap().keySet()) {
				try {
					if (mapEntry.matches(label)) {
						matches.add(mapEntry);
					}
				} catch (PatternSyntaxException e) {
				}
			}

			if (matches.size() > 0) {
				for (String toRemove : matches)
					labelMap.getMap().remove(toRemove);
			} else {
				m_notifier.notify(Notifier.NOTIFY_ERROR, String.format(
						"Unable to remove label: %s", label));
			}
		}
	}

	public boolean addToSrcLabelMap(int nodeId, int coreId, String label, int pc) {
		return addToCodePointMap(nodeId, coreId, m_srcLabels, label, pc);
	}
		
	public void removeFromSrcLabelMap(int nodeId, int coreId, String label) {
	    removeFromCodePointMap(nodeId, coreId, m_srcLabels, label);
	}
	
	public boolean addToEndpointMap(int nodeId, int coreId, String endpoint, int pc) {
		return addToCodePointMap(nodeId, coreId, m_endpoints, endpoint, pc);
	}

	public void removeFromEndpointMap(int nodeId, int coreId, String label) {
	    removeFromCodePointMap(nodeId, coreId, m_endpoints, label);
	}	
	
	public boolean addToThreadStartPoints(int nodeId, int coreId, int pc) {
		return addToThreadDelimiters(nodeId, coreId, m_threadStartPoints, pc);
	}

	public void removeFromThreadStartPoints(int nodeId, int coreId, int pc) {
		removeFromThreadDelimiters(nodeId, coreId, m_threadStartPoints, pc);
	}
	
	public boolean addToThreadStopPoints(int nodeId, int coreId, int pc) {
		return addToThreadDelimiters(nodeId, coreId, m_threadStopPoints, pc);
	}

	public void removeFromThreadStopPoints(int nodeId, int coreId, int pc) {
		removeFromThreadDelimiters(nodeId, coreId, m_threadStopPoints, pc);
	}
	
	public Map<Integer, TreeSet<Integer>> getBranchMap(int nodeId, int coreId) {
		BranchMap branchMap = getBranchMapNull(nodeId, coreId);
		if (branchMap != null) {
			return branchMap.getMap();
		}
		return new HashMap<Integer, TreeSet<Integer>>();
	}

	public Map<String, Integer> getLabelMap(int nodeId, int coreId) {
	    LabelMap labelMap = getLabelMapNull(nodeId, coreId);
	    if (labelMap != null) {
		    return labelMap.getMap();
	    }
	    return new HashMap<String, Integer>();		
	}

	public Map<String, CodePointInfo> getSrcLabelMap(int nodeId, int coreId) {
		return getCodePointMap(nodeId, coreId, m_srcLabels);
	}
	
	public Map<String, CodePointInfo> getEndpointMap(int nodeId, int coreId) {
		return getCodePointMap(nodeId, coreId, m_endpoints);
	}
		
	public Map<Integer, SrcLabelTableInfo> getSrcLabelTableMap(int nodeId, int coreId) {
		SrcLabelTableMap tableMap = getSrcLabelTableMapNull(nodeId, coreId);
		if (tableMap != null) {
			return tableMap.getMap();
		}
		return new HashMap<Integer, SrcLabelTableInfo>();
	}	
	
	public Map<Integer, EndpointTableInfo> getEndpointTableMap(int nodeId, int coreId) {
		EndpointTableMap tableMap = getEndpointTableMapNull(nodeId, coreId);
		if (tableMap != null) {
			return tableMap.getMap();
		}
		return new HashMap<Integer, EndpointTableInfo>();
	}
	
	public Collection<Integer> getThreadStartPoints(int nodeId, int coreId) {
		return getThreadDelimiters(nodeId, coreId, m_threadStartPoints);
	}

	public Collection<Integer> getThreadStopPoints(int nodeId, int coreId) {
		return getThreadDelimiters(nodeId, coreId, m_threadStopPoints);
	}
	
	public Collection<String> getSrcLevelCommands(int nodeId, int coreId) {
		SrcLevelCommands srcLevelCommands = getSrcLevelCommandsNull(nodeId, coreId);
		if (srcLevelCommands != null) {
			return srcLevelCommands.getCommands();
		}
		return new ArrayList<String>();
	}
	
	private boolean addToCodePointMap(int nodeId, int coreId, List<CodePointMap> codePointMaps, String label, int pc) {
		CodePointMap labelMap = getCodePointMapNull(codePointMaps, nodeId, coreId);
		if (labelMap != null) {
			if (!labelMap.getMap().containsKey(label)) {										
				labelMap.getMap().put(label, new CodePointInfo(pc, label, "", "", 0));
			} else {
				m_notifier.notify(Notifier.NOTIFY_ERROR, String.format(
						"Label already exists: %s", label));
			}
			return true;
		}
		return false;
	}

	private void removeFromCodePointMap(int nodeId, int coreId, List<CodePointMap> codePointMaps, String label) {
		CodePointMap labelMap = getCodePointMapNull(codePointMaps, nodeId, coreId);
		if (labelMap != null) {
			List<String> matches = new ArrayList<String>();

			for (String mapEntry : labelMap.getMap().keySet()) {
				try {
					if (mapEntry.matches(label)) {
						matches.add(mapEntry);
					}
				} catch (PatternSyntaxException e) {
				}
			}

			if (matches.size() > 0) {
				for (String toRemove : matches)
					labelMap.getMap().remove(toRemove);
			} else {
				m_notifier.notify(Notifier.NOTIFY_ERROR, String.format(
						"Unable to remove label: %s", label));
			}
		}
	}
	
	private Map<String, CodePointInfo> getCodePointMap(int nodeId, int coreId, List<CodePointMap> codePointMaps) {
	    CodePointMap labelMap = getCodePointMapNull(codePointMaps, nodeId, coreId);
	    if (labelMap != null) {
		    return labelMap.getMap();
	    }
	    return new HashMap<String, CodePointInfo>();
	}	
	
	private boolean addToThreadDelimiters(
			int nodeId, int coreId, List<ThreadDelimiters> threadDelimiters, int pc) {
		
		ThreadDelimiters threadDelimiter = getThreadDelimitersNull(
				threadDelimiters, nodeId, coreId);
		
		if (threadDelimiter != null) {
			threadDelimiter.getPcs().add(pc);
			return true;
		}
		return false;
	}

	private void removeFromThreadDelimiters(
			int nodeId, int coreId, List<ThreadDelimiters> threadDelimiters, int pc) {

		ThreadDelimiters threadDelimiter = getThreadDelimitersNull(
				threadDelimiters, nodeId, coreId);
		
		if (threadDelimiter != null) {
			if (!threadDelimiter.getPcs().remove(pc)) {
				m_notifier.notify(Notifier.NOTIFY_ERROR, String.format(
						"Unable to remove pc: %s", pc));
			}
		}
	}
	
	public Collection<Integer> getThreadDelimiters(
			int nodeId, int coreId, List<ThreadDelimiters> threadDelimiters) {
		
		ThreadDelimiters threadDelimiter = getThreadDelimitersNull(threadDelimiters, nodeId, coreId);
		if (threadDelimiter != null) {
			return threadDelimiter.getPcs();
		}
		return new TreeSet<Integer>();		
	}
	
	private BranchMap getBranchMapNull(int nodeId, int coreId) {
		for (BranchMap branches : m_branches) {
			if ((branches.getNodeId() == nodeId) &&
				(branches.getCoreId() == coreId)) {
				return branches;
			}
		}
		return null;
	}

	private LabelMap getLabelMapNull(int nodeId, int coreId) {
		for (LabelMap labels : m_labels) {
			if ((labels.getNodeId() == nodeId) &&
				(labels.getCoreId() == coreId)) {
				return labels;
			}
		}
		return null;
	}
	
	private CodePointMap getCodePointMapNull(List<CodePointMap> codePoints, int nodeId, int coreId) {
		for (CodePointMap codePointMap : codePoints) {
			if ((codePointMap.getNodeId() == nodeId) &&
				(codePointMap.getCoreId() == coreId)) {
				return codePointMap;
			}
		}
		return null;
	}

	private SrcLabelTableMap getSrcLabelTableMapNull(int nodeId, int coreId) {
		for (SrcLabelTableMap tableMap : m_srcLabelTables) {
			if ((tableMap.getNodeId() == nodeId) &&
				(tableMap.getCoreId() == coreId)) {
				return tableMap;
			}
		}
		return null;
	}

	private EndpointTableMap getEndpointTableMapNull(int nodeId, int coreId) {
		for (EndpointTableMap tableMap : m_endpointTables) {
			if ((tableMap.getNodeId() == nodeId) &&
				(tableMap.getCoreId() == coreId)) {
				return tableMap;
			}
		}
		return null;
	}

	private ThreadDelimiters getThreadDelimitersNull(
			List<ThreadDelimiters> threadDelimiters, int nodeId, int coreId) {
		for (ThreadDelimiters threadDelimiter : threadDelimiters) {
			if ((threadDelimiter.getNodeId() == nodeId) &&
				(threadDelimiter.getCoreId() == coreId)) {
				return threadDelimiter;
			}
		}
		return null;		
	}	
		
	private SrcLevelCommands getSrcLevelCommandsNull(int nodeId, int coreId) {
		for (SrcLevelCommands srcLevelCommands : m_srcLevelCommands) {
			if ((srcLevelCommands.getNodeId() == nodeId) &&
				(srcLevelCommands.getCoreId() == coreId)) {
				return srcLevelCommands;
			}
		}
		return null;		
	}		
	
	private boolean loadBranchMaps() {
		for (BranchMap branchMap : m_branches) {
			if (!branchMap.load(m_xeLoader)) {
			    return false;
			}
		}
		return true;
	}
	
	private boolean loadLabelMaps() {
		for (LabelMap labelMap : m_labels) {
			if (!labelMap.load(m_xeLoader)) {
				return false;
			}			
		}	
		return true;
	}

	private boolean loadCodePointMaps(List<CodePointMap> codePointMaps) {
		for (CodePointMap codePointMap : codePointMaps) {
			if (!codePointMap.load(m_xeLoader)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean loadSrcLabelTables() {
		for (SrcLabelTableMap tableMap : m_srcLabelTables) {
			if (!tableMap.load(m_xeLoader)) {
				return false;
			}
		}
		return true;
	}	
		
	private boolean loadEndpointTables() {
		for (EndpointTableMap tableMap : m_endpointTables) {
			if (!tableMap.load(m_xeLoader)) {
				return false;
			}
		}
		return true;
	}	
	
	private boolean loadThreadDelimiters(List<ThreadDelimiters> threadDelimiters) {
		for (ThreadDelimiters threadDelimiter : threadDelimiters) {
			if (!threadDelimiter.load(m_xeLoader)) {
				return false;
			}
		}
		return true;
	}

	private boolean loadSrcLevelCommands() {
		for (SrcLevelCommands srcLevelCommand : m_srcLevelCommands) {
			if (!srcLevelCommand.load(m_xeLoader)) {
				return false;
			}
		}
		return true;
	}	
}
