package com.xmos.xta.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.RouteDescription;
import com.xmos.xta.engine.analysis.structural.StructureGraph;
import com.xmos.xta.engine.constraint.Constraint;
import com.xmos.xta.engine.instructions.InstructionMnemonics;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.unknowns.PausableInstructionUnknown;
import com.xmos.xta.engine.unknowns.LoopIterationsUnknown;
import com.xmos.xta.engine.unknowns.Unknowns;

@SuppressWarnings("serial")
class AbortedException extends Exception {}

@SuppressWarnings("unchecked")
public class Route {
	
    public static final int INVALID_ROUTE_ID = -1;
	public static int WORST_CASE_PATH_ID = -2;

    enum RouteMode {
		ABSLOUTE, FUNCTION
	};
	private static final int SR_IEBLE_SHIFT = 1;
	private static final int SR_IEBLE_SIZE = 1; 

	enum RouteStatus {
		COMPLETED, ABORTED, ERROR
	}

	public static final boolean DEBUG = false;
	
	private static int s_routeCount;
	private static int s_pathCount;
	
	private StructureGraph m_structureGraph;
	private boolean m_firstStep;
	private int m_routeId;
	private int m_startPc;
	private RouteDescription m_description;
	private InstructionMnemonics m_mnenonics;
	private Engine m_engine;	
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
	
	public Route(Config config, Engine engine, int startPc, TreeSet<Integer> endPcs, 
			InstructionMnemonics mnenonics, RouteDescription description){
		m_engine = engine;
		m_constraint = engine.getConstraint().clone();
		m_startPc = startPc;
		m_description = description;
		m_mnenonics = mnenonics;
		m_routeTiming = new RouteTiming(this, config, engine.getNodeId(), engine.getCoreId());
		m_routeId = s_routeCount;
		++s_routeCount;
		s_pathCount = 0;
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
	
	private void checkSupportedInstructions() {
		// TODO
		/*
		//for (XtaInstruction inst : block.getInstructionTrimmed()){
			
			// Checks for threading instructions...
			if (m_mnenonics.isSsync(inst))
				block.addWarning(String.format("Ssync (multiple threads) not supported. Pc: 0x%08x", inst.getPc()));					
			if (m_mnenonics.isMsync(inst))
				block.addWarning(String.format("Msync (multiple threads) not supported. Pc: 0x%08x", inst.getPc()));				
			if (m_mnenonics.isMjoin(inst)) {
				block.addWarning(String.format("Mjoin (multiple threads) not supported. Pc: 0x%08x", inst.getPc()));
			}
			
			// Checks for interrupt enable..
			if (m_mnenonics.isSetSr(inst)) {	
				int op1Field = inst.getOperandField("op1");
				int shift = SR_IEBLE_SHIFT;
				int mask = (1 << SR_IEBLE_SIZE) - 1;
				if (((op1Field >> shift) & mask) == 1) 
    				block.addWarning(String.format("Interrupt enable detected. Pc: 0x%08x", inst.getPc()));					
			}			
		}		
		*/
	}
}
		

