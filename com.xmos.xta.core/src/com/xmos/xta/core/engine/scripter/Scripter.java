package com.xmos.xta.core.engine.scripter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.CommandSource.Type;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteDescription;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming;
import com.xmos.xta.core.engine.analysis.routes.RouteDescription.RouteMode;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming.Level;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;
import com.xmos.xta.core.engine.defines.Defines;
import com.xmos.xta.core.engine.defines.InstructionTimeInfo;
import com.xmos.xta.core.engine.defines.LoopInfo;
import com.xmos.xta.core.engine.defines.LoopPathInfo;
import com.xmos.xta.core.engine.defines.LoopScopeInfo;
import com.xmos.xta.core.engine.defines.PathTimeInfo;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.sections.BranchInfo;

public class Scripter {
	private Engine m_engine;
	private int m_currentRouteId;
	private Config m_currentConfig;
	private int m_currentNodeNumber;
	private int m_currentCoreNumber;
	private Collection<Reference> m_currentExclusions;
	private Map<Reference, List<Reference>> m_currentBranches;
	private Defines m_currentDefines;

	// Can't use a Set because the compareTo won't do what we need for matching
	// references
	List<ScripterReference> m_references;
	private Set<String> m_autoLabels;
	private Set<String> m_disables;
	private Map<String, String> m_renames;

	public Scripter(Engine engine) {
		m_engine = engine;
		m_currentConfig = new Config();
		m_currentNodeNumber = 0;
		m_currentCoreNumber = 0;
		m_currentExclusions = new ArrayList<Reference>();
		m_currentDefines = new Defines();
		m_currentBranches = new TreeMap<Reference, List<Reference>>();
		m_autoLabels = new TreeSet<String>();
		m_disables = new TreeSet<String>();
		m_renames = new TreeMap<String, String>();
		m_references = null;
	}

	public void setEngineLoadAndRunDone() {
		m_currentConfig = new Config(m_engine.getConfig());
		m_currentNodeNumber = m_engine.getNodeNumber();
		m_currentCoreNumber = m_engine.getCoreNumber();
		m_currentExclusions = new ArrayList<Reference>(m_engine.getExclusions());
		m_currentDefines = new Defines(m_engine.getDefines());
		m_currentBranches = copyBranches(m_engine.getBranches());
		m_references = null;
	}

	public String dump() {
    	StringBuffer script = new StringBuffer();

    	m_currentRouteId = 0;
    	script.append(String.format("load \"%s\"\n", m_engine.getExecutable().toString()));

    	for (RouteContainer routeContainer : m_engine.getRouteContainers()) {
        	if ((routeContainer.getCommandSrc().getType() == Type.SRC_LEVEL_COMMAND) ||
        	    (routeContainer.numExistingRoutes() <= 0))
        		continue;

       		script.append("\n");

       		RouteDescription description = routeContainer.getDescription();
			int nodeNumber = description.getNodeNumber();
       		int coreNumber = description.getCoreNumber();

       		routeComment(routeContainer, script);
        	globalConfig(routeContainer, script);
        	addBranches(description.getBranches(), script);
        	addDefines(description.getDefines(), nodeNumber, coreNumber, script);
        	addExclusions(description.getExclusions(), script);
        	analyseRoutes(routeContainer, script);
        	removeRoutes(routeContainer, script);

       		for (Route route : routeContainer.getRoutes()) {
       			if (routeContainer.routeExists(route)) {
           			setKnowns(route, script, routeContainer.getRoutes().size() == 1);
           			setRequirements(route, script, routeContainer.getRoutes().size() == 1);
       			}
       			++m_currentRouteId;
    		}
    	}
    	script.append("\n");
    	int nodeNumber = m_engine.getNodeNumber();
		int coreNumber = m_engine.getCoreNumber();
		updateGlobalConfig(script, m_engine.getConfig(), nodeNumber, coreNumber);
    	addBranches(m_engine.getUserBranches(), script);
    	addDefines(m_engine.getDefines(), nodeNumber, coreNumber, script);
		addExclusions(m_engine.getExclusions(), script);

    	return script.toString();
    }

	public String getUniqueLabel() {
		int i = 0;
		String label = "label_" + Integer.toString(i);
		while (m_autoLabels.contains(label)) {
			i++;
			label = "label_" + Integer.toString(i);
		}
		m_autoLabels.add(label);
		return label;
	}

	public void enable(CommandSource src, Notifier notifier, String ref) {
		ScripterReference scripterReference = getReference(ref);
		if (scripterReference != null) {
			scripterReference.setEnable(true);
			m_disables.remove(ref);
		} else {
			notifier.notify(src, Notifier.NOTIFY_ERROR,
					String.format("Reference '%s' not used in scripter", ref));
		}
	}

	public void disable(CommandSource src, Notifier notifier, String ref) {
		ScripterReference scripterReference = getReference(ref);
		if (scripterReference != null) {
			scripterReference.setEnable(false);
			m_disables.add(ref);
		} else {
			notifier.notify(src, Notifier.NOTIFY_ERROR,
					String.format("Reference '%s' not used in scripter", ref));
		}
	}

	public void rename(CommandSource src, Notifier notifier, String from, String to) {
		ScripterReference scripterReference = getReference(from);
		if (scripterReference == null) {
			notifier.notify(src, Notifier.NOTIFY_ERROR,
					String.format("Reference '%s' not used in scripter", from));

		} else if (!scripterReference.isModifiable()) {
			notifier.notify(src, Notifier.NOTIFY_ERROR,
					String.format("Reference '%s' cannot be renamed", from));

		} else {
			scripterReference.rename(to);
			m_renames.put(from, to);
		}
	}

	public Collection<ScripterReference> getRefs() {
		assert(m_references != null);
		return m_references;
	}

	public void createRefs() {
		m_autoLabels.clear();
		m_references = new LinkedList<ScripterReference>();

		// Create references for the existing routes
		for (RouteContainer routeContainer : m_engine.getRouteContainers()) {
        	if ((routeContainer.getCommandSrc().getType() == Type.SRC_LEVEL_COMMAND) ||
        	    (routeContainer.numExistingRoutes() <= 0))
        		continue;

       		RouteDescription description = routeContainer.getDescription();
       		if (description.getMode() == RouteMode.PATH) {
       			addReference(description.getStartPoint());
       			addReferences(description.getEndPoints());
       		} else {
       			addReference(description.getFunctionReference());
       		}

       		for (Reference ref : description.getBranches().keySet()) {
       			addReference(ref);
       			addReferences(description.getBranches().get(ref));
       		}

       		addDefineReferences(description.getDefines(),
       				description.getNodeNumber(), description.getCoreNumber());

        	for (Reference ref : description.getExclusions()) {
        		addReference(ref);
        	}

       		for (Route route : routeContainer.getRoutes()) {
       			for (Unknown unknown : route.getRouteUnknowns().getUnknowns(State.KNOWN)) {
       				for (Reference ref : unknown.getReferences()) {
       					addReference(ref);
       				}
       			}
    		}
    	}

		// Create references for the current state
   		for (Reference ref : m_engine.getUserBranches().keySet()) {
   			addReference(ref);
   			addReferences(m_engine.getUserBranches().get(ref));
   		}

   		addDefineReferences(m_engine.getDefines(), m_engine.getNodeNumber(), m_engine.getCoreNumber());

    	for (Reference ref : m_engine.getExclusions()) {
    		addReference(ref);
    	}

    	disableReferences();
    	renameReferences();
	}

    public void outputPortableWarnings(StringBuffer script, Reference[] references) {
    	boolean portable = true;
    	for (Reference reference : references) {
    		if (reference == null) {
    			portable = false;

    		} else if (!reference.isPortable()) {
    			if (reference.getRef().equals(getReferenceString(reference))) {
    				// Only emit the warning if the reference is not being mapped to a new value
    				script.append(String.format("# Warning: '%s' is a non-portable reference.\n",
    					reference.getRef()));
    			}
    		}
    	}
    	if (!portable) {
			script.append("# Warning: The following line contains a non-portable reference.\n");
		}
    }

	private void routeComment(RouteContainer routeContainer, StringBuffer script) {
    	RouteDescription routeDescription = routeContainer.getDescription();

    	script.append("#\n");
    	switch (routeDescription.getMode()) {
    	case FUNCTION:
    		script.append(String.format("# Route: %s\n", routeDescription.getFunctionName()));
    		break;

    	case PATH:
    		script.append(String.format("# Route: %s => %s\n", routeDescription.getFromName(), routeDescription.getToName()));
    		break;
    	}
    	script.append("#\n");
	}

    private void globalConfig(RouteContainer routeContainer, StringBuffer script) {
    	updateGlobalConfig(script,
    			routeContainer.getDescription().getConfig(),
    			routeContainer.getDescription().getNodeNumber(),
    			routeContainer.getDescription().getCoreNumber());
    }

	private void addDefines(Defines defines, int nodeNumber, int coreNumber, StringBuffer script) {
		defines.updateScript(m_currentDefines, script, nodeNumber, coreNumber);
		m_currentDefines = defines;
	}

    private void addBranches(Map<Reference, List<Reference>> branches, StringBuffer script) {
    	if (!m_currentBranches.equals(branches)) {
    		if (!m_currentBranches.isEmpty()) {
        	    script.append("remove branch *\n");
        		m_currentBranches.clear();
    		}
    		for (Reference fromRef : branches.keySet()) {
    			outputPortableWarnings(script, new Reference[]{fromRef});
    			for (Reference toRef : branches.get(fromRef)) {
        			outputPortableWarnings(script, new Reference[]{toRef});
    			}

    			List<Reference> toRefs = new ArrayList<Reference>();
            	script.append(String.format("add branch %s", getReferenceString(fromRef)));

    			for (Reference toRef : branches.get(fromRef)) {
    				script.append(String.format(" %s", getReferenceString(toRef)));
                	toRefs.add(toRef);
    			}
            	script.append(String.format("\n"));
        		m_currentBranches.put(fromRef, toRefs);
    		}
    	}
    }

    private void addExclusions(Collection<Reference> exclusions, StringBuffer script) {
    	if (!m_currentExclusions.equals(exclusions)) {
    		if (!m_currentExclusions.isEmpty()) {
        	    script.append("remove exclusion *\n");
    		}
        	for (Reference exclusion : exclusions) {
    			outputPortableWarnings(script, new Reference[]{exclusion});
            	script.append(String.format("add exclusion %s\n",
            			getReferenceString(exclusion)));
        	}
    		m_currentExclusions.clear();
    		m_currentExclusions.addAll(exclusions);
    	}
    }

    private void analyseRoutes(RouteContainer routeContainer, StringBuffer script) {
    	RouteDescription routeDescription = routeContainer.getDescription();
    	switch (routeDescription.getMode()) {
    	case FUNCTION:
		    outputPortableWarnings(script, new Reference[]{routeDescription.getFunctionReference()});

		    script.append(String.format("analyse function %s\n",
		    		getReferenceString(routeDescription.getFunctionReference())));
    		break;

    	case PATH:
		    outputPortableWarnings(script, new Reference[]{routeDescription.getStartPoint()});
		    outputPortableWarnings(script, new Reference[]{routeDescription.getEndPoint()});

		    script.append(String.format("analyse path %s %s\n",
		    		getReferenceString(routeDescription.getStartPoint()),
		    		getReferenceString(routeDescription.getEndPoint())));
    		break;
    	}
    }

	private void removeRoutes(RouteContainer routeContainer, StringBuffer script) {
    	int index = 0;
   		for (Route route : routeContainer.getRoutes()) {
   			if (!routeContainer.routeExists(route)) {
   				script.append(String.format("remove route %d\n", m_currentRouteId + index));
   			}
   			++index;
   		}
    }

    private void setKnowns(Route route, StringBuffer script, boolean singleRoute) {
		for (Unknown unknown : route.getRouteUnknowns().getUnknowns(State.KNOWN)) {
			String routeIdStr = getRouteIdString(singleRoute);
		    String[] refStrs = getReferenceStrings(unknown);

		    try {
		    	String scriptStr = unknown.getScriptStr(routeIdStr, refStrs);
			    outputPortableWarnings(script, unknown.getReferences());
		    	script.append(scriptStr);
		    } catch (Exception e) {}
		}
    }

    private void setRequirements(Route route, StringBuffer script, boolean singleRoute) {
		String routeIdStr = getRouteIdString(singleRoute);

		RouteTiming routeTiming = route.getRouteTiming();
		if (routeTiming.isTimingSpecified()) {
	    	script.append(String.format("set required %s %s\n", routeIdStr,
	    			routeTiming.getDescription(Level.REQUIRED)));
		}
    }

    private void updateGlobalConfig(StringBuffer script, Config config, int nodeNumber, int coreNumber) {
    	// Need to change node/core first since the other config items are changed when node/core changes
    	if ((m_currentNodeNumber != nodeNumber) || (m_currentCoreNumber != coreNumber)) {
        	script.append(String.format("config core %d %d\n", nodeNumber, coreNumber));
        	m_currentNodeNumber = nodeNumber;
        	m_currentCoreNumber = coreNumber;
    	}
    	if (m_currentConfig.m_numThreads != config.m_numThreads) {
        	script.append(String.format("config threads %s\n", config.m_numThreads));
        	m_currentConfig.m_numThreads = config.m_numThreads;
    	}
    	if (m_currentConfig.m_frequency != config.m_frequency) {
        	script.append(String.format("config freq %s\n", config.m_frequency));
        	m_currentConfig.m_frequency = config.m_frequency;
    	}
    	if (m_currentConfig.m_case != config.m_case) {
        	script.append(String.format("config case %s\n", config.m_case));
        	m_currentConfig.m_case = config.m_case;
    	}
    }

    private String getRouteIdString(boolean singleRoute) {
		String routeIdStr = "-";
		if (!singleRoute) {
			routeIdStr = String.format("%d", m_currentRouteId);
		}
		return routeIdStr;
    }

    private String[] getReferenceStrings(Unknown unknown) {
    	Collection<String> refStrings = new ArrayList<String>();
    	for (Reference reference : unknown.getReferences()) {
    		refStrings.add(getReferenceString(reference, unknown.getNode()));
    	}
    	return refStrings.toArray(new String[0]);
    }

    private String getReferenceString(Reference reference, Node node) {
	    String refStr = "";
		if (reference == null) {
			// This unknown was set directly onto a node (and not via a reference),
			// so we need to generate a suitable reference to use in the script.
			refStr = node.getReferenceStr();

		} else {
			// This unknown was set via a reference, so can use this directly in the script.
			refStr = getReferenceString(reference);
		}
		return refStr;
    }

    private Map<Reference, List<Reference>> copyBranches(Map<Reference, Set<BranchInfo>> map) {
    	Map<Reference, List<Reference>> copy = new TreeMap<Reference, List<Reference>>();

		for (Reference fromRef : map.keySet()) {
			List<Reference> toRefs = new ArrayList<Reference>();
			for (BranchInfo to : map.get(fromRef)) {
            	toRefs.add(to.getRef());
			}
			copy.put(fromRef, toRefs);
		}
		return copy;
    }

	private void disableReferences() {
		Set<String> toRemove = new TreeSet<String>();
		for (String disable : m_disables) {
			ScripterReference ref = getReference(disable);
			if (ref == null) {
				toRemove.add(disable);
			} else {
				ref.setEnable(false);
			}
		}
		m_disables.removeAll(toRemove);
	}

	private void renameReferences() {
		Set<String> toRemove = new TreeSet<String>();
		for (String rename : m_renames.keySet()) {
			ScripterReference ref = getReference(rename);
			if (ref == null) {
				toRemove.add(rename);
			} else {
				ref.rename(m_renames.get(rename));
			}
		}

		for (String remove : toRemove) {
			m_renames.remove(remove);
		}
	}

	private void addDefineReferences(Defines defines, int nodeNumber, int coreNumber) {

		for (InstructionTimeInfo info : defines.getInstructionTimes(nodeNumber, coreNumber)) {
			addReference(info.getReference());
		}
		for (LoopPathInfo info : defines.getLoopPaths(nodeNumber, coreNumber)) {
			addReference(info.getReference());
		}
		for (LoopInfo info : defines.getLoops(nodeNumber, coreNumber)) {
			addReference(info.getReference());
		}
		for (LoopScopeInfo info : defines.getLoopScopes(nodeNumber, coreNumber)) {
			addReference(info.getReference());
		}
		for (PathTimeInfo info : defines.getPathTimes(nodeNumber, coreNumber)) {
			addReference(info.getFrom());
			addReference(info.getTo());
		}
	}

	private void addReferences(Collection<Reference> fromRefs) {
		for (Reference ref : fromRefs) {
			addReference(ref);
		}
	}

	private void addReference(Reference ref) {
		if (ref == null) {
			return;
		}

		ScripterReference scripterReference = getReference(ref.getRef());
		if (scripterReference == null) {
			m_references.add(new ScripterReference(this, m_engine, ref));
		}
	}

	private ScripterReference getReference(String ref) {
		for (ScripterReference scripterReference : m_references) {
			if (scripterReference.getReference().matches(ref, m_engine)) {
				return scripterReference;
			}
		}
		return null;
	}

    private String getReferenceString(Reference ref) {
    	ScripterReference scripterReference = getReference(ref.getRef());
    	assert(scripterReference != null);
    	if (scripterReference.isEnabled()) {
    		return scripterReference.getMapsToLabel();
    	} else {
    		return ref.getRef();
    	}
	}
}
