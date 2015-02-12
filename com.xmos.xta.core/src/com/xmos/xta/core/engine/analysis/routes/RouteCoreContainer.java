package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.CoreConfig;

public class RouteCoreContainer implements Iterable<Route>{
    private Reference m_from;
    private Set<Reference> m_to;
    private CoreId m_coreId;
    private CoreConfig m_coreConfig;
    // N.B. m_routes & m_exists must be handled together, thus we don't expose them.
    // An iterator has been added to allow Route elements to be retrieved.
    private List<Route> m_routes;
    private List<Boolean> m_exists;
    private RouteContainer m_parentRouteContainer;

    public RouteCoreContainer(RouteContainer routeContainer,  CoreConfig coreConfig, CoreId coreId, Reference reference) {
        initialise(routeContainer, coreConfig, coreId, reference);
    }
    public RouteCoreContainer(RouteContainer routeContainer, CoreConfig coreConfig, CoreId coreId, Reference from, Reference to) {
        initialise(routeContainer, coreConfig, coreId, from);
        m_to.add(to);
    }
    private void initialise(RouteContainer parentRouteContainer, CoreConfig coreConfig, CoreId coreId, Reference reference) {
        m_from = reference;
        m_to = new IteratableSet<Reference>();
        m_coreId = coreId;
        m_coreConfig = coreConfig;
        m_routes = new ArrayList<Route>();
        m_exists = new ArrayList<Boolean>();
        m_parentRouteContainer = parentRouteContainer;
    }

    public Reference getReference() {
        return m_from;
    }

    public Reference getFromReference() {
        return m_from;
    }

    public Set<Reference> getToReferences() {
        return m_to;
    }

    public RouteContainer parentRouteContainer() {
        return m_parentRouteContainer;
    }

    public Iterator<Route> iterator() {
        return m_routes.iterator();
    }

    public boolean hasNoRoutes() {
        return m_routes.isEmpty();
    }

    public void add(Route route) {
        m_routes.add(route);
        m_exists.add(true);
    }

    public void remove(Route route) {
        int index = m_routes.indexOf(route);
        m_exists.set(index, false);
    }

    public boolean routeExists(Route route) {
        int index = m_routes.indexOf(route);
        return m_exists.get(index);
    }

    public int getNumExistingRoutes() {
        int num = 0;
        for (Route route : m_routes)
            if (routeExists(route))
                ++num;
        return num;
    }

    public CoreId getCoreId() {
        return m_coreId;
    }

    public CoreConfig getCoreConfig() {
        return m_coreConfig;
    }
}
