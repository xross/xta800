package com.xmos.xta.core.engine.scripter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteCoreContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming;
import com.xmos.xta.core.engine.analysis.routes.RouteTiming.Level;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.Source;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;
import com.xmos.xta.core.engine.references.Reference;

class SetScripter {
	private Scripter m_scripter;
	private String m_description;
	
	SetScripter(Scripter scripter) {
		m_scripter = scripter;
		m_description = null;
	}

    void setKnowns(RouteContainer routeContainer, StringBuffer script) {
   	    List<Unknown> processed = new ArrayList<Unknown>();

   	    for (Unknown known : getSetKnownsList(routeContainer)) {
       	    if (!processed.contains(known)) {
                String routeIdStr = String.format("%d", known.getRoute().getRouteId());       				    
       		    if (isKnownOnAllOtherRoutes(routeContainer, known, processed)) {
       				routeIdStr = "-";
       			}

           	    String[] refStrs = getReferenceStrings(known);
              	String scriptStr = known.getScriptStr(routeIdStr, refStrs);
               	outputPortableWarnings(script, known);
               	m_scripter.addScriptCommand(script, scriptStr);
           		
           		processed.add(known);
       	    }   	    	
        }       				    	
    }
    
    void setRequired(RouteContainer routeContainer, StringBuffer script) {
    	if (areRequirementsSameOnAllRoutes(routeContainer)) {
    		m_scripter.addScriptCommand(script, String.format("set required - %s", m_description));
	    	
    	} else {
        	for (RouteCoreContainer routeCoreContainer : routeContainer.getRouteCoreContainers()) {
           		for (Route route : routeCoreContainer.getRoutes()) {
           			if (routeCoreContainer.routeExists(route)) {
           				RouteTiming routeTiming = route.getRouteTiming();
           				if (routeTiming.isTimingSpecified()) {
           					m_scripter.addScriptCommand(script, String.format("set required %d %s", route.getRouteId(),
           			    			routeTiming.getDescription(Level.REQUIRED)));
           				}
           			}
        		}        		
        	}    	    	    
    	}
    }
    
    private void outputPortableWarnings(StringBuffer script, Unknown unknown) {
    	for (Reference ref : unknown.getReferences()) {
    		if (ref == null) {
    			ref = m_scripter.createReferenceForUnknown(unknown);
    		}
    		m_scripter.outputPortableWarnings(script, ref);
    	}
    }
        
    private String[] getReferenceStrings(Unknown unknown) {
    	Collection<String> refStrings = new ArrayList<String>();
    	for (Reference ref : unknown.getReferences()) {
    		if (ref == null) {
    			ref = m_scripter.createReferenceForUnknown(unknown);
    		}
    		refStrings.add(m_scripter.getReferenceString(ref));
    	}
    	return refStrings.toArray(new String[0]);
    }
    
	private List<Unknown> getSetKnownsList(RouteContainer routeContainer) {
        List<Unknown> setKnownsList = new ArrayList<Unknown>();
   	    for (RouteCoreContainer routeCoreContainer : routeContainer.getRouteCoreContainers()) {
       		for (Route route : routeCoreContainer.getRoutes()) {
       			if (routeCoreContainer.routeExists(route)) {
       				for (Unknown known : route.getRouteUnknowns().getUnknowns(State.KNOWN)) {
       					if (known.getSource() == Source.SET) {
       						setKnownsList.add(known);	
       					}
       				}       				
       			}
       		}
   	    }
   	    return setKnownsList;
	}

	private boolean isKnownOnAllOtherRoutes(RouteContainer routeContainer, Unknown thisUnknown, List<Unknown> processed) {
		Route thisRoute = thisUnknown.getRoute();
		
		List<Unknown> matched = new ArrayList<Unknown>();
   	    for (RouteCoreContainer routeCoreContainer : routeContainer.getRouteCoreContainers()) {       		
   	    	for (Route route : routeCoreContainer.getRoutes()) {
       			if (routeCoreContainer.routeExists(route) && (route != thisRoute)) {  
       				
       				boolean found = false;
       				for (Unknown unknown : route.getRouteUnknowns().getUnknowns(State.KNOWN)) {       					
       					if (knownsMatch(thisUnknown, unknown)) {
       						matched.add(unknown);
       						found = true;
       						break;
       					}
       				}
       				if (!found)
           			    return false;
       			}
    		}
    	}   	    
   	    processed.addAll(matched);
   	    return true;
	}
	
	private boolean knownsMatch(Unknown thisUnknown, Unknown thatUnknown) {
		if (thisUnknown.getType() != thatUnknown.getType())
			return false;
		
		if (thisUnknown.getSource() != thatUnknown.getSource())
			return false;
		    
		if (!thisUnknown.getValueStr().equals(thatUnknown.getValueStr()))
			return false;
		
	    String[] thisRefStrs = getReferenceStrings(thisUnknown);
	    String[] thatRefStrs = getReferenceStrings(thatUnknown);
	    if (thisRefStrs.length != thatRefStrs.length)
	    	return false;
	    		 
	    for (int i = 0; i < thisRefStrs.length; ++i) {
	    	if (!thisRefStrs[i].equals(thatRefStrs[i])) {	    		
	    		return false;
	    	}
	    }	    
		return true;
	}    
	
    private boolean areRequirementsSameOnAllRoutes(RouteContainer routeContainer) {
    	for (RouteCoreContainer routeCoreContainer : routeContainer.getRouteCoreContainers()) {
       		for (Route route : routeCoreContainer.getRoutes()) {
       			if (routeCoreContainer.routeExists(route)) {
       				RouteTiming routeTiming = route.getRouteTiming();
       				
       				if (!routeTiming.isTimingSpecified())
       					return false;

       				String description = routeTiming.getDescription(Level.REQUIRED);
       				if ((m_description != null) && (!m_description.equals(description)))
       					return false;
       					
   					m_description = description;
       			}
    		}
        }
        return true;
    }	
}
