package com.xmos.xta.core.engine.analysis;

import java.util.List;

import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteConstructor;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteCoreContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer.RouteMode;
import com.xmos.xta.core.engine.points.LoopPoints;
import com.xmos.xta.core.engine.points.PathPoints;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceType.Result;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class Analyzer {
	private Engine m_engine;
	private List<Route> m_routes;
	private List<RouteContainer> m_routeContainers;

	private static final boolean development_mode = false;

	public Analyzer(Engine engine, List<Route> routes, List<RouteContainer> routeContainers) {
		m_engine = engine;
		m_routes = routes;
		m_routeContainers = routeContainers;
		engine.getMemoryMonitor().reset();
	}

	public void analyzeFunction(CommandSource src, String functionName) {
        RouteContainer routeContainer = new RouteContainer(
        		RouteMode.FUNCTION, functionName, m_engine, src);		

        boolean isResolved = false;
        boolean routeFound = false;
		for (CoreId activeCore : m_engine.getActiveCores()) {			
			Reference functionRef = ReferenceFactory.createReference(functionName, 
					ReferenceClass.FUNCTION, activeCore);

			if (functionRef.resolve(m_engine) == Result.RESOLVED) {
				isResolved = true;
				
				RouteCoreContainer routeCoreContainer = new RouteCoreContainer(functionRef, 
						routeContainer, m_engine, activeCore);
				
				if (!analyzeRoute(src, routeContainer, routeCoreContainer, activeCore)) {
					return;
				}
				routeFound = true;
			}
		}
		update(src, routeContainer, isResolved);
		if (!routeFound) {
	    	m_engine.notify(src, Notifier.NOTIFY_ERROR, 
	    			String.format("Reference: %s not resolved on any active tile", functionName));
		}
	}

	public void analyzeEndpoints(CommandSource src, PathPoints pathPoints) {
        RouteContainer routeContainer = new RouteContainer(pathPoints.getFromName(), pathPoints.getToName(), m_engine, src);

        boolean isResolved = false;
        boolean routeFound = false;
		for (CoreId activeCore : m_engine.getActiveCores()) {
			isResolved = true;
			
			boolean fromFound = true;
			Reference fromEndpoint = pathPoints.getFrom(activeCore);
		    if ((fromEndpoint == null) || fromEndpoint.getRef().equals("")) {
		    	fromFound = false;
		    }

			boolean toFound = true;
			Reference toEndpoint = pathPoints.getTo(activeCore);
		    if ((toEndpoint == null) || toEndpoint.getRef().equals("")) {
		    	toFound = false;
		    }

		    if (fromFound && toFound) {
				RouteCoreContainer routeCoreContainer = new RouteCoreContainer(fromEndpoint, toEndpoint,
						routeContainer, m_engine, activeCore);
				
				if (!analyzeRoute(src, routeContainer, routeCoreContainer, activeCore)) {
					return;
				}
				routeFound = true;
    		}			
		}
		update(src, routeContainer, isResolved);
		if (!routeFound) {
	    	m_engine.notify(src, Notifier.NOTIFY_ERROR, 
	    			"References not resolved on any active tile");
	    }
	}

	public void analyzeLoop(CommandSource src, LoopPoints loopPoints) {
        RouteContainer routeContainer = new RouteContainer(
        		RouteMode.LOOP, loopPoints.getRefString(), m_engine, src);		

        boolean isResolved = false;
        boolean routeFound = false;
		for (CoreId activeCore : m_engine.getActiveCores()) {
			isResolved = true;
			
			Reference loopRef = loopPoints.getLoop(activeCore);
		    if ((loopRef != null) && !loopRef.getRef().equals("")) {
				RouteCoreContainer routeCoreContainer = new RouteCoreContainer(loopRef, 
						routeContainer, m_engine, activeCore);
				
				if (!analyzeRoute(src, routeContainer, routeCoreContainer, activeCore)) {
					return;
				}
				routeFound = true;
			}		    
		}
		update(src, routeContainer, isResolved);
		if (!routeFound) {
	    	m_engine.notify(src, Notifier.NOTIFY_ERROR, String.format(
	    			"Reference: %s not resolved on any active tile", loopPoints.getRefString()));
		}
	}

	private boolean analyzeRoute(CommandSource src, RouteContainer routeContainer,
			RouteCoreContainer routeCoreContainer, CoreId core) {
		
		try {
			RouteConstructor routeConstructor = new RouteConstructor(m_engine, 					
					m_engine.getControlFlow(core), m_engine.getCachedFunctionMap(core),
					routeCoreContainer, core);			
			routeConstructor.analyze(src);

			if (!routeConstructor.getRoutes().isEmpty()) {
				routeCoreContainer.add(routeConstructor.getRoutes());
				routeContainer.addRouteCoreContainer(routeCoreContainer);
			}			
			
		} catch (final java.lang.OutOfMemoryError e) {
			if (development_mode) e.printStackTrace();
			m_engine.notify(src, Notifier.NOTIFY_ERROR, "out of memory (heap)");
			return false;
			
		} catch (final java.lang.StackOverflowError e) {
			if (development_mode) e.printStackTrace();
			m_engine.notify(src, Notifier.NOTIFY_ERROR, "out of memory (stack)");
			return false;

		} catch (MemoryMonitorException e) {
			if (development_mode) e.printStackTrace();
			m_engine.notify(src, Notifier.NOTIFY_ERROR, "heap memory running low, aborting");
			return false;
			
		} catch (AbortedException e) {
			return false;
			
		} catch (Exception e) {
			if (development_mode) e.printStackTrace();
			m_engine.notify(src, Notifier.NOTIFY_ERROR, String.format("internal error detected (%s)", e.getMessage()));
			return false;
		}
		return true;
	}
	
	private void update(CommandSource src, RouteContainer routeContainer, boolean isResolved) {
		if (routeContainer.getNumExistingRoutes() > 0) {
			m_routeContainers.add(routeContainer);

			int currentSize = m_routes.size();			
			for (RouteCoreContainer routeCoreContainer : routeContainer.getRouteCoreContainers()) {
				for (Route route : routeCoreContainer.getRoutes()) {
					m_routes.add(route);	
				}
			}
			m_engine.setSelectedRoute(src, m_routes.get(currentSize));
			m_engine.notify(src, Notifier.NOTIFY_ANALYSIS_COMPLETE, 
				Integer.toString(m_routes.get(currentSize).getRouteId()));
		} else {
			if (isResolved) {
				// All the references for this analysis were resolved, 
				// but no routes were created..
		        m_engine.notify(src, Notifier.NOTIFY_WARNING, "no valid paths of execution");
			}
		}
	}
}
