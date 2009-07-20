package com.xmos.xta.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Set;

import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.routes.RouteDescription;
import com.xmos.xta.engine.analysis.structural.ReferenceMatcher;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.InstructionNodeAttributes;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.engine.references.Reference;

class AbortedException extends Exception {
	private static final long serialVersionUID = 1L;
}

public class Route {
	private static int s_routeCount;

	enum RouteStatus {
		COMPLETED, ABORTED, ERROR
	}

	private int m_routeId;
	private RouteDescription m_description;
	private RouteTiming m_timing;
	private RouteUnknowns m_unknowns;
	private Node m_node;
	private Node m_searchNode;

	public static void resetRouteCount() {
		s_routeCount = 0;
	}

	public Route(Engine engine, Node node, RouteDescription description) {
		m_description = description;
		m_routeId = s_routeCount++;
		m_node = node;
		
		Config config = new Config(engine.getConfig());
		m_timing = new RouteTiming(this, config, engine.getNodeId(), engine.getCoreId());
		m_unknowns = new RouteUnknowns(this);		
		setRouteSpecificConfigs(config);
	}
	
	public int getRouteId() {
		return m_routeId;
	}

	public Node getNode() {
		return m_node;
	}
	
	public RouteDescription getRouteDescription(){
		return m_description;
	}
	
	public RouteTiming getRouteTiming() {
		return m_timing;
	}
	
	public RouteUnknowns getRouteUnknowns() {
		return m_unknowns;
	}
	
	public String getEndpointsPcsString() {
		return "";
	}

	public Set<Integer> getPcsHit() {
		final Set<Integer> pcs = new TreeSet<Integer>();
		m_node.traverse(0, new DefaultNodeTraverser() {
			public boolean foundInstructionNode(Node node, int depth) {
				pcs.add(node.getStartPc());
				return true;
			}
		});
		return pcs;
	}
	
    public Node getNode(final int nodeId) {
    	if (nodeId == Engine.NODE_ID_TOP_LEVEL) {
    		return m_node;
    		
    	} else {
        	m_searchNode = null;			
        	m_node.traverse(0, new UntypedNodeTraverser() {
    			public boolean foundNode(Node node, int depth) {
    				if (node.getId() == nodeId) {
    					m_searchNode = node;
    					return false;
    				}
    				return true;
    			}
    		});
            return m_searchNode;   	    		
    	}    	
    }
    
	public Collection<Node> getReferencedNodes(Reference reference) {
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();		
		Collection<Node> referencedNodes = new ArrayList<Node>();
		referencedNodes.addAll(referenceMatcher.getNodes(m_node, reference));
		return referencedNodes;		
	} 

	private void setRouteSpecificConfigs(final Config config) {
		m_node.traverse(0, new DefaultNodeTraverser() {
			public boolean foundInstructionNode(Node node, int depth) {
				((InstructionNodeAttributes)node.getAttributes()).setConfig(config);				
				return true;
			}
		});
	}
}

