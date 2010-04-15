package com.xmos.xta.core.engine.analysis;

import java.util.List;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.PathPoints;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteConstructor;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteCoreContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteConstructor.RouteError;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.utils.CoreId;

public class Analyzer {
	private Engine m_engine;
	private List<Route> m_routes;
	private List<RouteContainer> m_routeContainers;
	
	public static final boolean development_mode = false;
		
	public Analyzer(Engine engine, List<Route> routes, List<RouteContainer> routeContainers) {
		m_engine = engine;
		m_routes = routes;
		m_routeContainers = routeContainers;
	}

	public boolean analyzeFunction(CommandSource src, String functionName) {
        RouteContainer routeContainer = new RouteContainer(functionName, m_engine, src);		

		boolean routeFound = false;
		for (CoreId activeCore : m_engine.getActiveCores()) {			
			Reference functionRef = ReferenceFactory.createReference(functionName, 
					ReferenceClass.FUNCTION_REF, activeCore);

			if (functionRef.resolve(m_engine)) {
				RouteCoreContainer routeCoreContainer = new RouteCoreContainer(functionRef, 
						routeContainer, m_engine, activeCore);

				analyzeRoute(src, routeContainer, routeCoreContainer, activeCore);
				routeFound = true;
			}		
		}
		if (routeContainer.getNumExistingRoutes() > 0)   			
			m_routeContainers.add(routeContainer);

	    if (!routeFound) {
	    	m_engine.notify(src, Notifier.NOTIFY_ERROR, String.format("Reference: %s not resolved on any active core", functionName));
		    return false;
	    }
	    return true;
	}

	public boolean analyzePath(CommandSource src, PathPoints pathPoints) {
        RouteContainer routeContainer = new RouteContainer(pathPoints.getFromName(), pathPoints.getToName(), m_engine, src);

		boolean routeFound = false;
		for (CoreId activeCore : m_engine.getActiveCores()) {

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
				
				analyzeRoute(src, routeContainer, routeCoreContainer, activeCore);
				routeFound = true;
    		}			
		}
		if (routeContainer.getNumExistingRoutes() > 0)   			
			m_routeContainers.add(routeContainer);

	    if (!routeFound) {
	    	m_engine.notify(src, Notifier.NOTIFY_ERROR, "References not resolved on any active core");
	    	return false;
	    }
	    return true;
	}

	private boolean analyzeRoute(CommandSource src, RouteContainer routeContainer,
			RouteCoreContainer routeCoreContainer, CoreId core) {
		
		try {
			doRouteAnalysis(src, routeContainer, routeCoreContainer, core);
			return true;
			
		} catch (final java.lang.OutOfMemoryError e) {
			if (development_mode) e.printStackTrace();
			m_engine.notify(src, Notifier.NOTIFY_ERROR, "out of memory (heap)");
			return false;
			
		} catch (final java.lang.StackOverflowError e) {
			if (development_mode) e.printStackTrace();
			m_engine.notify(src, Notifier.NOTIFY_ERROR, "out of memory (stack)");
			return false;
			
		} catch (AbortedException e) {
			return false;
			
		} catch (Exception e) {
			if (development_mode) e.printStackTrace();
			m_engine.notify(src, Notifier.NOTIFY_ERROR, String.format("internal error detected (%s)", e.getMessage()));
			return false;
		}
	}

	private boolean doRouteAnalysis(CommandSource src, RouteContainer routeContainer,
			RouteCoreContainer routeCoreContainer, CoreId core) throws Exception {

		if (!m_engine.updateControlFlowGraphs(src, core))
			return false;
		

		RouteConstructor routeConstructor = new RouteConstructor(m_engine, m_engine.getCallGraph(core), routeCoreContainer);
		RouteError error = routeConstructor.analyze(src);

		if (error == RouteError.NO_ENDPOINTS_FOUND ) {
			m_engine.notify(src, Notifier.NOTIFY_ERROR, "no paths of execution could reach the endpoint(s)");
			return false;

		} else if (error == RouteError.COULD_NOT_FIND_ENDPOINT ) {
			m_engine.notify(src, Notifier.NOTIFY_ERROR, "could not find any end points");
			return false;

		} else if (error == RouteError.COULD_NOT_FIND_STARTPOINT ) {
			m_engine.notify(src, Notifier.NOTIFY_ERROR,"could not find any start points");
			return false;

		} else if (error == RouteError.ENDPOINTS_MISSING ) {
			m_engine.notify(src, Notifier.NOTIFY_ERROR, "could not determine the endpoint(s)");
			return false;

		} else if (error == RouteError.STARTPOINT_MISSING ) {
			m_engine.notify(src, Notifier.NOTIFY_ERROR, "could not determine the startpoint");
			return false;

		} else if (error == RouteError.ABORTED ) {
			return false;

		} else {
			int currentSize = m_routes.size();
			for (Route route : routeConstructor.getRoutes()) {
				m_routes.add(route);
			}
			if (!m_routes.isEmpty() && !routeConstructor.getRoutes().isEmpty()) {				
				routeCoreContainer.add(routeConstructor.getRoutes());
				routeContainer.addRouteCoreContainer(routeCoreContainer);				

				m_engine.setSelectedRoute(src, m_routes.get(currentSize));
				m_engine.notify(src, Notifier.NOTIFY_ANALYSIS_COMPLETE, Integer.toString(m_routes.get(currentSize).getRouteId()));

			} else {
				m_engine.notify(src, Notifier.NOTIFY_WARNING, "no valid paths of execution");
			}
			return true;
		}
	}	
}
