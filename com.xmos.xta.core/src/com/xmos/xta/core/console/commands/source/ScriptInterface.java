package com.xmos.xta.core.console.commands.source;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.ConsoleCommandRunner;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.CommandSource.Type;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming.Level;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class ScriptInterface {
	public static String XTA_OBJECT_REFERENCE = "xta";
	
	private Shell m_shell;
	private Engine m_engine;
	private Console m_console;
	private CommandSource m_src;
	private ScriptNotifier m_notifier;
	
    public ScriptInterface(Shell shell, Engine engine, Console console, String scriptFileName) {
		m_shell = shell;
		m_engine = engine;
		m_console = console;
    	m_src = new CommandSource(Type.FILE, scriptFileName, 0);    	
    	m_notifier = new ScriptNotifier(engine.getConfig(), console);
    }

	// ===================================================================== //
    // Load Methods
	// ===================================================================== //
    public void load(String fileName) throws Exception {
    	m_engine.setNotifier(m_notifier);
    	
    	m_engine.loadExecutable(m_src, new File(fileName), 
    			new ConsoleCommandRunner(m_shell, m_engine, m_console));    	
    	checkForErrors("load");
    }

	// ===================================================================== //
    // Analyze Methods
	// ===================================================================== //
    public List<Integer> analyzeFunction(String functionName) throws Exception {
    	m_engine.analyzeFunction(m_src, functionName);
        checkForErrors("analyzeFunction");
    	return getLastRouteIds();
    }

    public List<Integer> analyzeEndpoints(String fromRef, String toRef) throws Exception {
		m_engine.getPathPoints().setFrom(m_src, fromRef);
        checkForErrors("analyzeEndpoints");
		
		m_engine.getPathPoints().setTo(m_src, toRef);
        checkForErrors("analyzeEndpoints");
				    	
    	m_engine.analyzeEndpoints(m_src);
        checkForErrors("analyzeEndpoints");
        
    	return getLastRouteIds();
    }

	// ===================================================================== //
    // Add/Remove Methods
	// ===================================================================== //
    public void addExclusion(String ref) throws Exception {
    	m_engine.addExclusion(m_src, ref);
        checkForErrors("addExclusion");
    }    

    public void removeExclusion(String ref) throws Exception {
    	m_engine.removeExclusion(m_src, ref);
        checkForErrors("removeExclusion");
    }

	// ===================================================================== //
    // Set Methods
	// ===================================================================== //
    public void setRequired(int routeId, double value, String units) throws Exception {
    	TimeUnit timeUnit = new TimeUnit(value, units);    	    
    	m_engine.setRequiredTime(m_src, routeId, timeUnit);
        checkForErrors("setRequired");
    }

	// ===================================================================== //
    // Get Methods
	// ===================================================================== //
    public double getRequired(int routeId, String units) throws Exception {
    	try {
       	    return getRoute(routeId).getRouteTiming().getMaxTime().getValue(units);
    	} catch (Exception e) {
    		throw new Exception("xta.getRequired: " + e.getMessage());
    	}
    }

    public double getWorstCase(int routeId, String units) throws Exception {
    	return getTime(routeId, units, Case.WORST);
    }

    public double getBestCase(int routeId, String units) throws Exception {
    	return getTime(routeId, units, Case.BEST);
    }

    public String getRouteSummary(int routeId) throws Exception {
    	try {
          	return getRoute(routeId).getRouteTiming().getDescription(Level.COMPLETE);
    	} catch (Exception e) {
    		throw new Exception("getRouteSummary: " + e.getMessage());
    	}
    }

	// ===================================================================== //
    // Private Helper Methods
	// ===================================================================== //    
    private Route getRoute(int routeId) throws Exception {
    	Collection<Route> routes = m_engine.getRoute(routeId);
    	if (!routes.isEmpty()) {
        	assert(routes.size() == 1);
        	return routes.iterator().next();
    	}
		throw new Exception("Invalid route id");    	
    }
    
    private List<Integer> getLastRouteIds() {
    	List<Integer> routeIds = new ArrayList<Integer>(); 
    	for (Route route : m_engine.getRoute(Engine.ROUTE_ID_LAST)) {
    		routeIds.add(route.getRouteId());
    	}
    	return routeIds;    	
    }
    
    private double getTime(int routeId, String units, Case c) throws Exception {
		Status status = new Status();
		double time = getRoute(routeId).getRouteTiming().getTime(status, c);
    	if (status.isExceptionalCase()) {
    		throw new Exception("xta.getActual: timing on routeId " + routeId + " is " + status.getString());
    	} 
    	TimeUnit timeUnit = new TimeUnit(time, "ns");    	
   	    return timeUnit.getValue(units);
    }

    private void checkForErrors(String functionName) throws Exception {
    	if (m_notifier.errorDetected()) {
    		String xtaObjectReference = XTA_OBJECT_REFERENCE + "." + functionName;
    		throw new Exception(xtaObjectReference + ": " + m_notifier.getErrorMessage());    		    
    	}    	
    }
}
