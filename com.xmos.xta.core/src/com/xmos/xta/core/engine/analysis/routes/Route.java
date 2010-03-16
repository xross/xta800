package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.Stack;

import com.xmos.libs.xeloader.XeLoader.Architecture;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.InstructionNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverserWithAncestors;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.CoreConfig;

public class Route {
	private static int s_routeCount;

	enum RouteStatus {
		COMPLETED, ABORTED, ERROR
	}

	private int m_routeId;
	private RouteCoreContainer m_coreContainer;
	private RouteTiming m_timing;
	private RouteUnknowns m_unknowns;
	
	//private Stack<FunctionReferenceNode> m_stack;
	private Node m_baseOfRouteNode;
	
	private Node m_searchNode;
	public static void resetRouteCount() {
		s_routeCount = 0;
	}

	public Route(CommandSource src, Engine engine, Stack<FunctionReferenceNode> stack, Node node, RouteCoreContainer routeCoreContainer) {
		m_coreContainer = routeCoreContainer;
		m_routeId = s_routeCount++;
		//m_stack = stack;
		m_baseOfRouteNode = node;
		Architecture architecture = engine.getXeLoader().getArchitecture();
		
		m_timing = new RouteTiming(src, this, 
				m_coreContainer.getCoreConfig(), 
				m_coreContainer.getNodeNumber(), 
				m_coreContainer.getCoreNumber());
		
		m_unknowns = new RouteUnknowns(this);
		
		setRouteSpecificCoreConfigs(m_coreContainer.getCoreConfig(), architecture);
	}

	public int getRouteId() {
		return m_routeId;
	}

	public Node getNode() {
		return m_baseOfRouteNode;
	}

	public void setNode(Node node) {
		m_baseOfRouteNode = node;
	}

    public RouteCoreContainer getRouteCoreContainer() {
		return m_coreContainer;
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
    		return m_baseOfRouteNode;

    	} else {
        	m_searchNode = null;
        	m_baseOfRouteNode.traverse(new UntypedNodeTraverser() {
    			public TraverseStatus foundNode(Node node) {
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

		Node node = m_baseOfRouteNode;

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

	public Set<Node> getReferencedNodes(Reference ref) throws AbortedException {
		RouteReferenceMatcher rrm = new RouteReferenceMatcher();
		Set<Node> r = rrm.matchReference(m_baseOfRouteNode, ref);
		assert(r != null);
		return r;
	}

	public Collection<Node> getReferencedFunctionNodes(Reference reference) throws AbortedException {
		RouteReferenceMatcher rrm = new RouteReferenceMatcher();
		
		Collection<Node> referencedNodes = new ArrayList<Node>();

		for (Node function : rrm.getFunctionNodesMatchingReference(m_baseOfRouteNode, reference)) {
			referencedNodes.add(function);
		}
		
		return referencedNodes;
	}

	private void setRouteSpecificCoreConfigs(final CoreConfig coreConfig, final Architecture architecture) {
		m_baseOfRouteNode.traverse(new Stack<Node>(), new DefaultNodeTraverserWithAncestors() {
			public TraverseStatus foundInstructionNode(Node node, Stack<Node> ancestors) {
				((InstructionNodeAttributes)node.getAttributes()).setCoreConfig(coreConfig, architecture);
				return TraverseStatus.CONTINUE;
			}
		});
	}
}

