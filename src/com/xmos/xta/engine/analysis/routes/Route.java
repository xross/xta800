package com.xmos.xta.engine.analysis.routes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.RouteDescription;
import com.xmos.xta.engine.analysis.structural.StructuralAnalysisEngine;
import com.xmos.xta.engine.analysis.structural.StructureGraph;
import com.xmos.xta.engine.analysis.structural.nodes.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.patterns.CommonMethods;
import com.xmos.xta.engine.constraint.Constraint;
import com.xmos.xta.engine.unknowns.Unknowns;

class AbortedException extends Exception {
	private static final long serialVersionUID = 1L;
}

public class Route {

	public static final int INVALID_ROUTE_ID = -1;
	public static int WORST_CASE_PATH_ID = -2;
	private static final boolean DEBUG = false;

	enum RouteMode {
		ABSLOUTE, FUNCTION
	};

	enum RouteStatus {
		COMPLETED, ABORTED, ERROR
	}

	private static int s_routeCount;

	private int m_routeId;
	private RouteDescription m_description;
	private Set<Integer> m_pcsHit;
	private Set<Integer> m_startPcs;
	private Set<Integer> m_endPcs;
	private StructureGraph m_structureGraph;
	private Collection<Node> m_routes;
	private Engine m_engine;

	public static void resetRouteCount() {
		s_routeCount = 0;
	}

	public Route(Config config, Engine engine, StructureGraph structureGraph, Set<Integer> startPcs, Set<Integer> endPcs, RouteDescription description) {
		m_startPcs = startPcs;
		m_endPcs = endPcs;
		m_structureGraph = structureGraph;
		m_pcsHit = new HashSet<Integer>();
		m_description = description;
		m_routeId = s_routeCount++;
		m_routes = new HashSet<Node>();
		m_engine = engine;

		m_constraint = engine.getConstraint().clone();

		m_routeTiming = new RouteTiming(this, config, engine.getNodeId(), engine.getCoreId());
		m_unknowns = new Unknowns();
	}

	public void analyse() {
		Collection<Node> startNodes = new HashSet<Node>();
		Collection<Node> endNodes = new HashSet<Node>();
		Map<Integer, HashSet<Node>> pcToStruct = buildPcMap(m_structureGraph);

		for (int from : m_startPcs) {
			if (pcToStruct.get(from) != null) {
				startNodes.addAll(pcToStruct.get(from));
			}
		}
		for (int to : m_endPcs) {
			if (pcToStruct.get(to) != null) {
				endNodes.addAll(pcToStruct.get(to));
			}
		}
		m_structureGraph.removeLinks();
		for (Node start : startNodes) {
			if (DEBUG)
				System.out.println("digraph g { ");

			Collection<Node> setExplored = new HashSet<Node>();
			explore(start, setExplored, m_endPcs, endNodes, null);

			StructuralAnalysisEngine sae = new StructuralAnalysisEngine();
			sae.createStructure(setExplored, start, m_engine);

			m_structureGraph.removeLinks();

			m_routes.add(setExplored.iterator().next());

			if (DEBUG)
				System.out.println("}\n");
		}

	}

	private boolean explore(Node node, Collection<Node> set, Collection<Integer> endPcs, Collection<Node> endNodes, Node parent) {

		Set<Integer> pcs = new HashSet<Integer>(endPcs);
		pcs.retainAll(node.getPCs());
		// if the node does not contain the pc then just add the node
		if (pcs.isEmpty()) {
			makeConnection(node, parent);
			set.add(node);
			if (node.getParent().whatsNext(node) != null) {
				for (Node next : node.getParent().whatsNext(node)) {
					explore(next, set, endPcs, endNodes, node);
				}
			}
		} else {
			// if this is an instruction then add the node and we are done
			if (endNodes.contains(node)) {
				makeConnection(node, parent);
				set.add(node);
				return true;
			} else {
				// dont add the node and start to drop down until it is found.
				for (Node children : node.getChildern()) {
					if (explore(children, set, endPcs, endNodes, parent))
						break;
				}
			}
		}
		return false;
	}

	private void makeConnection(Node node, Node parent) {
		if (parent != null) {
			if (DEBUG)
				System.out.println(parent + " -> " + node + ";");
			CommonMethods.connect(node, parent);
		}
	}

	private Map<Integer, HashSet<Node>> buildPcMap(StructureGraph structureGraph) {
		final Map<Integer, HashSet<Node>> pcToNodes = new HashMap<Integer, HashSet<Node>>();
		for (Node node : structureGraph.getThreads()) {
			node.traverse(0, new DefaultNodeTraverser() {
				public boolean foundInstructionNode(Node node, int depth) {
					HashSet<Node> map = pcToNodes.get(node.getStartPc());
					if (map == null) {
						map = new HashSet<Node>();
					}
					map.add(node);
					pcToNodes.put(node.getStartPc(), map);
					return true;
				}
			});
		}
		return pcToNodes;
	}

	// below here need sorting / removing

	private RouteTiming m_routeTiming;
	private boolean m_constrained;
	private Unknowns m_unknowns;
	private Constraint m_constraint;

	public StructureGraph getStructureGraph() {
		return m_structureGraph;
	}

	public boolean setLoopIterations(int pc, Long count) {
		// TODO
		return false;
	}

	public boolean setLoopMaxIterations(int pc, Long count) {
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

	public String getDescription() {
		return m_description.getDescription();
	}

	public RouteTiming getRouteTiming() {
		return m_routeTiming;
	}

	public Unknowns getUnknowns() {
		return m_unknowns;
	}

	public String getEndpointsPcsString() {
		String pcString = "(0x" + Integer.toHexString(0) + " => ";
		for (int end : m_endPcs) {
			pcString += "0x" + Integer.toHexString(end) + ", ";
		}
		pcString = pcString.substring(0, pcString.length() - 2);
		pcString += ")";
		return pcString;
	}

	public Set<Integer> getPcsHit() {
		return m_pcsHit;
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

}
