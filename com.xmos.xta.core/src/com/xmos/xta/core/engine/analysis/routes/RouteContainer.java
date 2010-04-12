package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.xmos.libs.util.NonIteratableMap;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.defines.Defines;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.CoreId;

public class RouteContainer {

	public enum RouteMode {
		PATH, FUNCTION
	};

	private RouteMode m_mode;
	private String m_functionName;
	private String m_description;
	private CommandSource m_src;
    private Collection<RouteCoreContainer> m_routeCoreContainers;
    private Collection<CoreId> m_activeCores;
	private Map<String, Double> m_frequencies;
	private Map<CoreId, Integer> m_numThreads;    
	private Collection<Reference> m_exclusions;
	private Map<Reference, List<Reference>> m_branches;
	private Defines m_defines;
	
    public RouteContainer(String fromName, String toName, Engine engine, CommandSource src) {    	
		m_mode = RouteMode.PATH;
		m_description = String.format("path: %s to %s", fromName, toName);
		initialise(engine, src);
    }

    public RouteContainer(String functionName, Engine engine, CommandSource src) {
		m_mode = RouteMode.FUNCTION;
		m_functionName = functionName;
		m_description = String.format("function: %s", functionName);
		initialise(engine, src);
    }

	public RouteMode getMode() {
		return m_mode;
	}

	public String getFunctionName() {
		return m_functionName;
	}
    
	public String getDescription() {
		return m_description;
	}
    
    public CommandSource getCommandSrc() {
    	return m_src;
    }

    public Collection<CoreId> getActiveCores() {
    	return m_activeCores;
    }

    public Map<String, Double> getFrequencies() {
    	return m_frequencies;
    }

    public Map<CoreId, Integer> getNumThreads() {
    	return m_numThreads;
    }
    
    public void addRouteCoreContainer(RouteCoreContainer routeCoreContainer) {
    	m_routeCoreContainers.add(routeCoreContainer);
    }

    public Collection<RouteCoreContainer> getRouteCoreContainers() {
    	return m_routeCoreContainers;
    }
    
	public Collection<Reference> getExclusions() {
		return m_exclusions;
	}

	public Map<Reference, List<Reference>> getBranches() {
		return m_branches;
	}

	public Defines getDefines() {
		return m_defines;
	}
	
    public int getNumExistingRoutes() {
    	int numExistingRoutes = 0;
    	for (RouteCoreContainer coreContainer : m_routeCoreContainers) {
    		numExistingRoutes += coreContainer.getNumExistingRoutes();
    	}
    	return numExistingRoutes;
    }
    
    private void initialise(Engine engine, CommandSource src) {
    	m_src = src;
    	m_activeCores = new ArrayList<CoreId>(engine.getActiveCores());
		m_frequencies = new NonIteratableMap<String, Double>(engine.getFrequencyMap());
		m_numThreads = new NonIteratableMap<CoreId, Integer>(engine.getNumThreadMap());		    	
    	m_routeCoreContainers = new ArrayList<RouteCoreContainer>();
		m_exclusions = new ArrayList<Reference>();
		m_exclusions.addAll(engine.getExclusions());
		m_defines = new Defines(engine.getDefines());
		m_branches = engine.getUserBranches();    	
    }
}
