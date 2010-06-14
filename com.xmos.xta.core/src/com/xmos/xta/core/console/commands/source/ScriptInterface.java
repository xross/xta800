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
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status;
import com.xmos.xta.core.engine.defines.DefineInfo;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.CoreId;
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

    public Collection<String> getExclusions() throws Exception {
    	Collection<String> exclusions = new ArrayList<String>();
        for (Reference exclusion : m_engine.getExclusions()) {
        	exclusions.add(exclusion.getRef());
        }
        return exclusions;
    }

    public void addBranch(String fromRefString, Collection<String> toRefStrings) throws Exception {
    	m_engine.addBranch(m_src, fromRefString, toRefStrings);
        checkForErrors("addBranch");
    }    

    public void removeBranch(String fromRefString, Collection<String> toRefStrings) throws Exception {
    	m_engine.removeBranch(m_src, fromRefString, toRefStrings);
        checkForErrors("removeBranch");
    }    

    public Collection<String> getBranches() throws Exception {
    	Collection<String> branches = new ArrayList<String>();
        for (Reference branch : m_engine.getUserBranches().keySet()) {
        	branches.add(branch.getRef());
        }
        return branches;
    }
    
    public Collection<String> getBranchTargets(String branch) throws Exception {
    	Collection<String> branchTargets = new ArrayList<String>();
        for (Reference ref : m_engine.getUserBranches().keySet()) {
        	if (ref.getRef().equals(branch)) {
                for (Reference targetRef : m_engine.getUserBranches().get(ref)) {
                	branchTargets.add(targetRef.getRef());
                }
        	}
        }    	
        return branchTargets;
    }    
    
    public void addLoop(String ref, long iterations) throws Exception {
    	m_engine.addLoop(m_src, ref, iterations);
        checkForErrors("addLoop");
    }    

    public void removeLoop(String ref) throws Exception {
    	m_engine.removeLoop(m_src, ref);
        checkForErrors("removeLoop");
    }
    
    public Collection<String> getLoops() throws Exception {
    	return getDefines(m_engine.getLoopDefines());
    }

    public void addLoopPath(String ref, long iterations) throws Exception {
    	m_engine.addLoopPath(m_src, ref, iterations);
        checkForErrors("addLoopPath");
    }    

    public void removeLoopPath(String ref) throws Exception {
    	m_engine.removeLoopPath(m_src, ref);
        checkForErrors("removeLoopPath");
    }
    
    public Collection<String> getLoopPaths() throws Exception {
    	return getDefines(m_engine.getLoopPathDefines());
    }

    public void addLoopScope(String ref, boolean absolute) throws Exception {
    	m_engine.addLoopScope(m_src, ref, absolute);
        checkForErrors("addLoopScope");
    }    

    public void removeLoopScope(String ref) throws Exception {
    	m_engine.removeLoopScope(m_src, ref);
        checkForErrors("removeLoopScope");
    }
    
    public Collection<String> getLoopScopes() throws Exception {
    	return getDefines(m_engine.getLoopScopeDefines());
    }

    public void addInstructionTime(String ref, double value, String units) throws Exception {
    	m_engine.addInstructionTime(m_src, ref, new TimeUnit(value, units));
        checkForErrors("addInstructionTime");
    }    

    public void removeInstructionTime(String ref) throws Exception {
    	m_engine.removeInstructionTime(m_src, ref);
        checkForErrors("removeInstructionTime");
    }
    
    public Collection<String> getInstructionTimes() throws Exception {
    	return getDefines(m_engine.getInstructionTimeDefines());
    }

    public void addFunctionTime(String ref, double value, String units) throws Exception {
    	m_engine.addFunctionTime(m_src, ref, new TimeUnit(value, units));
        checkForErrors("addFunctionTime");
    }

    public void removeFunctionTime(String ref) throws Exception {
    	m_engine.removeFunctionTime(m_src, ref);
        checkForErrors("removeFunctionTime");
    }
    
    public Collection<String> getFunctionTimes() throws Exception {
    	return getDefines(m_engine.getFunctionTimeDefines());
    }

    public void addPathTime(String fromRef, String toRef, double value, String units) throws Exception {
    	m_engine.addPathTime(m_src, fromRef, toRef, new TimeUnit(value, units));
        checkForErrors("addPathTime");
    }

    public void removePathTime(String fromRef, String toRef) throws Exception {
    	m_engine.removePathTime(m_src, fromRef, toRef);
        checkForErrors("removePathTime");
    }

    public Collection<String> getPathTimes() throws Exception {
    	return getDefines(m_engine.getPathTimeDefines());
    }
    
	// ===================================================================== //
    // Set Methods
	// ===================================================================== //
    public void setRequired(int routeId, double value, String units) throws Exception {
    	TimeUnit timeUnit = new TimeUnit(value, units);    	    
    	m_engine.setRequiredTime(m_src, routeId, timeUnit);
        checkForErrors("setRequired");
    }

    public void setFunctionTime(int routeId, String refString, double value, String units) throws Exception {
    	TimeUnit timeUnit = new TimeUnit(value, units);    	    
    	m_engine.setFunctionTime(m_src, routeId, refString, timeUnit);
        checkForErrors("setFunctionTime");    	
    }

    public void setPathTime(int routeId, String fromRef, String toRef, double value, String units) throws Exception {
    	TimeUnit timeUnit = new TimeUnit(value, units);    	    
    	m_engine.setPathTime(m_src, routeId, fromRef, toRef, timeUnit);
        checkForErrors("setPathTime");    	
    }
    
    public void setInstructionTime(int routeId, String refString, double value, String units) throws Exception {
    	TimeUnit timeUnit = new TimeUnit(value, units);    	    
    	m_engine.setInstructionTime(m_src, routeId, refString, timeUnit);
        checkForErrors("setInstructionTime");
    }
    
    public void setLoop(int routeId, String refString, long iterations) throws Exception {
    	m_engine.setLoop(m_src, routeId, refString, iterations);
        checkForErrors("setLoop");
    }

    public void setLoopPath(int routeId, String refString, long iterations) throws Exception {
    	m_engine.setLoopPath(m_src, routeId, refString, iterations);
        checkForErrors("setLoopPath");
    }

    public void setLoopScope(int routeId, String refString, boolean absolute) throws Exception {
    	m_engine.setLoopScope(m_src, routeId, refString, absolute);
        checkForErrors("setLoopScope");
    }
    
    public void setExclusion(int routeId, String refString) throws Exception {
    	m_engine.setExclusion(m_src, routeId, refString);
        checkForErrors("setExclusion");
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
    	return getTime(routeId, units, Case.WORST, "getWorstCase");
    }

    public double getBestCase(int routeId, String units) throws Exception {
    	return getTime(routeId, units, Case.BEST, "getBestCase");
    }
    
    public List<String> getWarnings(int routeId) throws Exception {
    	try {
       	    return getRoute(routeId).getRouteTiming().getWarnings();
    	} catch (Exception e) {
    		throw new Exception("getWarnings: " + e.getMessage());
    	}
    }

    public List<String> getErrors(int routeId) throws Exception {
    	try {
       	    return getRoute(routeId).getRouteTiming().getErrors();
    	} catch (Exception e) {
    		throw new Exception("getErrors: " + e.getMessage());
    	}
    }
    
    public String getRouteDescription(int routeId) throws Exception {
    	try {
          	return getRoute(routeId).getRouteCoreContainer().getRouteContainer().getDescription();
    	} catch (Exception e) {
    		throw new Exception("xta.getRouteDescription: " + e.getMessage());
    	}
    }

    // ===================================================================== //
    // Config Methods
	// ===================================================================== //
    public void configThreads(String coreReference, int numThreads) throws Exception {
    	CoreId coreId = m_engine.getCoreId(coreReference);
		if (coreId == null) {
			throw new Exception("configThreads: invalid core reference");	
		}
		m_engine.setNumThreads(m_src, coreId, numThreads);
		checkForErrors("configThreads");
    }

    public void configFreq(String nodeId, double coreFrequency) throws Exception {
    	m_engine.setFrequency(m_src, nodeId, coreFrequency);
    	checkForErrors("configFreq");
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
    
    private double getTime(int routeId, String units, Case c, String exStr) throws Exception {
		Status status = new Status();
		double time = getRoute(routeId).getRouteTiming().getTime(status, c);
    	if (status.isExceptionalCase()) {
    		throw new Exception("xta." + exStr + ": timing on routeId " + routeId + " is " + status.getString());
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
    
    private Collection<String> getDefines(Collection<? extends DefineInfo> infos) throws Exception {
    	Collection<String> collection = new ArrayList<String>();
        for (DefineInfo info : infos) {
        	collection.add(info.getDescription() + " " + info.getValue());
        }
        return collection;
    }    
}
