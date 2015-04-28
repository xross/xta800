package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.utils.NonIteratableMap;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Config.ConfigStartMode;
import com.xmos.xta.core.engine.Config.ConfigTaskPriority;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.defines.Defines;
import com.xmos.xta.core.engine.references.Reference;

public class RouteContainer {

    public enum RouteMode {
        ENDPOINTS, FUNCTION, LOOP
    };

    private RouteMode m_mode;
    private String m_description;
    private String m_fromName;
    private String m_toName;
    private CommandSource m_src;
    private ConfigStartMode m_startMode;
    private ConfigTaskPriority m_taskPriority;
    private Collection<RouteCoreContainer> m_routeCoreContainers;
    private Collection<CoreId> m_activeCores;
    private Map<String, Double> m_frequencies;
    private Map<CoreId, Integer> m_numThreads;
    private Map<CoreId, Integer> m_numHighPriorityTasks;
    private Map<Reference, List<Reference>> m_branches;
    private Defines m_defines;

    public RouteContainer(RouteMode mode, Engine engine, CommandSource src, String fromName, String toName) {
        initialise( mode, engine, src, fromName, toName);
    }
    public RouteContainer(RouteMode mode, Engine engine, CommandSource src, String refName) {
        initialise( mode, engine, src, refName, "");
    }
    private void initialise(RouteMode mode, Engine engine, CommandSource src, String fromName, String toName) {
        String description = null;
        switch (mode) {
        case FUNCTION: description = String.format("function: %s", fromName); break;
        case LOOP:     description = String.format("loop: %s", fromName); break;
        case ENDPOINTS: description = String.format("endpoints: %s to %s", fromName, toName);
        }
        m_mode = mode;
        m_description = description;
        m_fromName = fromName;
        m_toName = toName;
        m_src = src;
        m_startMode = engine.getStartMode();
        m_taskPriority = engine.getTaskPriority();
        m_routeCoreContainers = new ArrayList<RouteCoreContainer>();
        m_activeCores = new ArrayList<CoreId>(engine.getActiveCores());
        m_frequencies = new NonIteratableMap<String, Double>(engine.getFrequencyMap());
        m_numThreads = new NonIteratableMap<CoreId, Integer>(engine.getNumThreadMap());
        m_numHighPriorityTasks = new NonIteratableMap<CoreId, Integer>(engine.getNumHighPriorityTasksMap());
        m_branches = engine.getUserBranches();
        m_defines = new Defines(engine.getDefines());
    }

    public RouteMode getMode() {
        return m_mode;
    }

    public String getDescription() {
        return m_description;
    }

    public String getRefName() {
        return m_fromName;
    }
    public String getFromName() {
        return m_fromName;
    }

    public String getToName() {
        return m_toName;
    }

    public CommandSource getCommandSrc() {
        return m_src;
    }

    public ConfigStartMode getStartMode() {
        return m_startMode;
    }

    public ConfigTaskPriority getTaskPriority() {
        return m_taskPriority;
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

    public Map<CoreId, Integer> getNumHighPriorityTasks() {
        return m_numHighPriorityTasks;
    }

    public void addRouteCoreContainer(RouteCoreContainer routeCoreContainer) {
        m_routeCoreContainers.add(routeCoreContainer);
    }

    public Collection<RouteCoreContainer> getRouteCoreContainers() {
        return m_routeCoreContainers;
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
}
