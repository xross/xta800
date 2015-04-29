package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.IssueNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.CoreConfig;

public class Route {
    private static int s_routeCount;

    private int m_routeId;
    private RouteTiming m_timing;
    private RouteUnknowns m_unknowns;
    private Node m_baseOfRouteNode;
    private RouteCoreContainer m_parentRouteCoreContainer;

    private Node m_searchNode;  // Used to return result from traverser.

    public static void resetRouteCount() {
        s_routeCount = 0;
    }

    public Route(Node node, RouteCoreContainer parentRouteCoreContainer) {
        m_routeId = s_routeCount++;
        m_timing = new RouteTiming(parentRouteCoreContainer.parentRouteContainer().getCommandSrc(), this,
                parentRouteCoreContainer.getCoreConfig(),
                parentRouteCoreContainer.getCoreId());
        m_unknowns = new RouteUnknowns(this);
        m_baseOfRouteNode = node;
        m_parentRouteCoreContainer = parentRouteCoreContainer;

        // N.B. Creating a new Route has side effects!
        setCoreConfig();
    }

    public void setNode(Node node) {
        m_baseOfRouteNode = node;
        //setCoreConfig();  // RL-TODO do we need to traverse? Or should this be done by RouteConstructor.setParent()?
    }

    public Node getNode() {
        return m_baseOfRouteNode;
    }

    public int getRouteId() {
        return m_routeId;
    }

    public RouteContainer parentRouteContainer() {
        return m_parentRouteCoreContainer.parentRouteContainer();
    }

    public RouteCoreContainer parentRouteCoreContainer() {
        return m_parentRouteCoreContainer;
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
                public TraverseStatus foundNode(Node node, Object clientState) {
                    if (node.getId() == nodeId) {
                        m_searchNode = node;
                        return TraverseStatus.NO_CHILDREN;
                    }
                    return TraverseStatus.CONTINUE;
                }
            },
            m_searchNode
            );
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
        for (Node function : rrm.getFunctionNodesMatchingReference(m_baseOfRouteNode, reference))
            referencedNodes.add(function);
        return referencedNodes;
    }

    public boolean sameDescription(Node newNode) {
        // N.B. We compare a potential Route before creating it as creation has side effects!
        // RL-TODO Is this inefficient?
        // If it is we could:
        //      1. Traverse in parallel, checking at each step.
        //      2. Cache the result of the traversal, as an SHA.
        List<String> routeDesc = createRouteDescription(m_baseOfRouteNode);
        List<String> newRouteDesc = createRouteDescription(newNode);
        return routeDesc.equals(newRouteDesc);
    }

    private List<String> createRouteDescription(Node node) {
        final List<String> nodeList = new LinkedList<String>();
        node.traverse(new UntypedNodeTraverser() {
            public TraverseStatus foundNode(Node node, Object clientState) {
                if(node.isIssueNode())
                    nodeList.add(node.getTypeName() + node.getStartPc());
                else
                    nodeList.add(node.getTypeName());
                return TraverseStatus.CONTINUE;
            }
        },
        null  // No clientState
        );
        return nodeList;
    }

    private void setCoreConfig() {
        // N.B. Creating a new Route has side effects!
        final CoreConfig coreConfig = m_parentRouteCoreContainer.getCoreConfig();
        m_baseOfRouteNode.traverse( new DefaultNodeTraverser() {
            public TraverseStatus foundIssueNode(Node node, Object clientState) {
                ((IssueNodeAttributes)node.getAttributes(true)).setCoreConfig(coreConfig);
                return TraverseStatus.CONTINUE;
            }
        },
        null    // No clientState
        );
    }
}

