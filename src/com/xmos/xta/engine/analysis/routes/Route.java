package com.xmos.xta.engine.analysis.routes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.RouteDescription;
import com.xmos.xta.engine.analysis.structural.StructureGraph;
import com.xmos.xta.engine.constraint.Constraint;
import com.xmos.xta.engine.instructions.InstructionMnemonics;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.unknowns.Unknowns;

@SuppressWarnings("serial")
class AbortedException extends Exception {}

public class Route {
	
    public static final int INVALID_ROUTE_ID = -1;
	public static int WORST_CASE_PATH_ID = -2;

    enum RouteMode {
		ABSLOUTE, FUNCTION
	};


	enum RouteStatus {
		COMPLETED, ABORTED, ERROR
	}
	
	private static int s_routeCount;

	private StructureGraph m_structureGraph;
	private int m_routeId;
	private int m_startPc;
	private RouteDescription m_description;
	private RouteTiming m_routeTiming;
	private boolean m_constrained;
	private Unknowns m_unknowns;
	private Constraint m_constraint;
	private Set<Integer> m_endPcs;
	private Set<String> m_functionsVisited;	
	private HashSet<Integer> m_pcsHit;
	
	public static void resetRouteCount() {
		s_routeCount = 0;
	}
	
	public Route(Config config, Engine engine, int startPc, Set<Integer> endPcs, 
			InstructionMnemonics mnenonics, RouteDescription description){
		
		m_constraint = engine.getConstraint().clone();
		m_startPc = startPc;
		m_description = description;
		m_routeTiming = new RouteTiming(this, config, engine.getNodeId(), engine.getCoreId());
		m_routeId = s_routeCount;
		++s_routeCount;
		m_endPcs = new HashSet<Integer>();
		m_pcsHit = new HashSet<Integer>();
		m_unknowns = new Unknowns();
		m_functionsVisited = new HashSet<String>();
	}

	public StructureGraph getStructureGraph() {
		return m_structureGraph;
	}
	
	public boolean setLoopIterations(int pc, Long count){
		// TODO
		return false;
	}
	
	public boolean setLoopMaxIterations(int pc, Long count){
		// TODO
		return false;
	}
	
	public boolean setLoopScope(int pc, boolean absolute) {
		// TODO
		return false;
	}	
	
	public int getRouteId() {
		return m_routeId;
	}
	
	public int getStartPc() {
		return m_startPc;
	}
	
	public String getDescription() {
		return m_description.getDescription();
	}
	
	public RouteTiming getRouteTiming() {
		return m_routeTiming;
	}
	
	public RouteStatus analyse(RouteMode mode) {		
		return RouteStatus.COMPLETED;
	}

	public Collection<String> getFunctionsVisited() {
		return m_functionsVisited;
	}
	
	public Unknowns getUnknowns() {
		return m_unknowns;
	}

	public String getEndpointsPcsString() {
		String pcString =  "(0x" + Integer.toHexString(m_startPc) + " => ";
		for (int end : m_endPcs) {
			pcString += "0x" + Integer.toHexString(end) + ", ";
		}
		pcString = pcString.substring(0, pcString.length() - 2);
		pcString += ")";
		return pcString;
	}
	
	public void setConstrained(boolean constrained) {
		m_constrained = constrained;
	}

	public HashSet<Integer> getPcsHit(){
		return m_pcsHit;
	}
	
	public boolean wasConstrained() {
		return m_constrained;
	}

	public Constraint getConstraint() {
		return m_constraint;
	}
	
	public String getFullDescription() {
		String fullDescription = String.format("Route(%d) %s\n", getRouteId(), getDescription());
		fullDescription += m_routeTiming.getDescription(true);
		fullDescription += String.format("    Complete paths: %d, Unresolved paths: %d\n\n", 0, 0);		
		return fullDescription;
	}

	public Reference getStartPoint() {
		return m_description.getStartPoint();
	}

	public Reference getEndPoint() {
		return m_description.getEndPoint();
	}
	
}
		

