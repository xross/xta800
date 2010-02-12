package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.Stack;

import com.xmos.libs.xeloader.XeLoader.Architecture;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.ReferenceMatcher;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.InstructionNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.core.engine.references.Reference;

public class Route {
	private static int s_routeCount;

	enum RouteStatus {
		COMPLETED, ABORTED, ERROR
	}

	private int m_routeId;
	private RouteDescription m_description;
	private RouteContainer m_container;
	private RouteTiming m_timing;
	private RouteUnknowns m_unknowns;
	private Node m_node;
	private Node m_searchNode;
	private Engine m_engine;

	public static void resetRouteCount() {
		s_routeCount = 0;
	}

	public Route(CommandSource src, Engine engine, Node node, RouteDescription description, RouteContainer container) {
		m_description = description;
		m_container = container;
		m_routeId = s_routeCount++;
		m_node = node;
		m_engine = engine;

		Architecture architecture = engine.getXeLoader().getArchitecture();
		m_timing = new RouteTiming(src, this, description.getConfig(), engine.getNodeNumber(), engine.getCoreNumber());
		m_unknowns = new RouteUnknowns(this);		
		setRouteSpecificConfigs(description.getConfig(), architecture);
	}
	
	public int getRouteId() {
		return m_routeId;
	}

	public Node getNode() {
		return m_node;
	}
	
	public void setNode(Node node) {
		m_node = node;
	}
	
	public RouteDescription getRouteDescription() {
		return m_description;
	}

    public RouteContainer getRouteContainer() {
		return m_container;
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

    public Node getNode(final int nodeId) {
    	if (nodeId == Engine.NODE_ID_TOP_LEVEL) {
    		return m_node;
    		
    	} else {
        	m_searchNode = null;			
        	m_node.traverse(new Stack<Node>(), new UntypedNodeTraverser() {
    			public TraverseStatus foundNode(Node node, Stack<Node> ancestors) {
    				if (node.getId() == nodeId) {
    					m_searchNode = node;
    					return TraverseStatus.NO_CHILDREN;
    				}
    				return TraverseStatus.CONTINUE;
    			}
    		});
            return m_searchNode;
    	}    	
    }
    
	public Node getNode(String nodeString) {
		
		// Note: the nodeString takes the format ~x~x~x~x~...
		// where x = the index of the child node at that level
		// of the structure. It provides a (reasonably) robust way
		// to describe a particular node and is required
		// purely for testing purposes.
		if (nodeString.charAt(0) != '~')
			return null;
		
		if (nodeString.charAt(nodeString.length() - 1) != '~')
			return null;
		
		Node node = m_node;
		
		String[] nodeIndecies = nodeString.substring(1, nodeString.length() - 1).split("~");
		for (String nodeIndex : nodeIndecies) {
			int index;
	    	try { index = Integer.parseInt(nodeIndex);
		    } catch (NumberFormatException e) {
		        return null;
		    }
		    
		    if (index >= node.iChildren().size())
		    	return null;
			
			node = node.iChildren().get(index);
			if(node instanceof FunctionReferenceNode){
				node = node.iChildren().get(0);
			}
		}		
		return node;
	}

	public Set<Node> getReferencedNodes(Reference reference) throws AbortedException {
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();		
		return referenceMatcher.getNodesMatchingReference(m_node, reference);		
	} 

	public Collection<Node> getReferencedFunctionNodes(Reference reference) throws AbortedException {
		ReferenceMatcher referenceMatcher = new ReferenceMatcher();		
		Collection<Node> referencedNodes = new ArrayList<Node>();
		
		for (Node function : referenceMatcher.getFunctionNodesMatchingReference(m_node, reference)) {
			referencedNodes.add(function);
		}
		return referencedNodes;		
	} 
	
	private void setRouteSpecificConfigs(final Config config, final Architecture architecture) {
		m_node.traverse(new Stack<Node>(), new DefaultNodeTraverser() {
			public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {
				((InstructionNodeAttributes)node.getAttributes()).setConfig(config, architecture);				
				return TraverseStatus.CONTINUE;
			}
		});
	}
}

