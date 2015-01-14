package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.structural.nodes.ConditionalNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.IssueNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.LoopNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.UntypedNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.Source;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class RouteUnknowns {
    private Route m_route;

    RouteUnknowns(Route route) {
        m_route = route;
    }

    public Collection<Unknown> getUnknowns(final State unknownState) {
        final Collection<Unknown> unknowns = new ArrayList<Unknown>();
        m_route.getNode().traverse(new UntypedNodeTraverser() {
            public TraverseStatus foundNode(Node node) {
                addToList(node);
                return TraverseStatus.CONTINUE;
            }

            public TraverseStatus foundConditionalNode(Node node) {
                ConditionalNode conditionalNode = (ConditionalNode)node;
                for (Node excludedNode : conditionalNode.getExcludedOptions()) {
                    addToList(excludedNode);
                }
                return foundNode(node);
            }

            public TraverseStatus foundLoopNode(Node node) {
                LoopNode loopNode = (LoopNode)node;
                for (Node excludedNode : loopNode.getExcludedLoops()) {
                    addToList(excludedNode);
                }
                return foundNode(node);
            }

            private void addToList(Node node) {
                for (Unknown unknown : node.getAttributes(false).getUnknowns()) {
                    if (unknown.getState() == unknownState) {
                        unknowns.add(unknown);
                    }
                }
            }
        });
        return unknowns;
    }

    // ===================================================================== //
    // Setting Loop Iterations
    // ===================================================================== //
    public void setLoopIterations(CommandSource src, Engine engine,
            Reference reference, long iterations,
            boolean sourceLevelIterations, Source source) throws AbortedException {

        for (Node node : m_route.getReferencedNodes(reference)) {
            setLoopIterationsNoCheck(src, engine, node, iterations, sourceLevelIterations, reference, source);
        }
    }

    public void checkSetLoopIterations(Reference reference, final long iterations,
            Notifier notifier, CommandSource src) throws Exception {

        if (iterations < 0)
            throw new Exception("Cannot set a negative iteration count");

        checkReferencedNodes(reference, notifier, src, new NodeChecker() {
            public void checkNode(Node node) throws Exception {
                checkSetLoopIterations(node);
                checkLoopIterationsValid(node, iterations);
            }
        });
    }

    public void setLoopIterations(CommandSource src, Engine engine, Node node, long iterations, Source source) throws Exception {
        if (iterations < 0)
            throw new Exception("Cannot set a negative iteration count");

        checkSetLoopIterations(node);
        checkLoopIterationsValid(node, iterations);
        setLoopIterationsNoCheck(src, engine, node, iterations, false, null, source);
    }

    public void checkSetLoopIterations(Node node) throws Exception {
        if (node.getEnclosingInfiniteLoopNode() != null)
            throw new Exception("Cannot set loop iterations on an infinite loop");

        if (node.getEnclosingLoopNode() == null)
            throw new Exception("Must be within a loop to be able to set loop iterations");
    }

    public long getSetLoopIterations(Node node) {
        Node enclosingLoopNode = node.getEnclosingLoopNode();
        if (enclosingLoopNode == null) {
            return 1;
        }
        return enclosingLoopNode.getAttributes(false).getLoopIterations();
    }

    private void checkLoopIterationsValid(Node node, long iterations) throws Exception {
        Node loopNode = node.getEnclosingLoopNode();
        loopNode.getAttributes(true).loopIterationsValid(iterations);
    }

    private void setLoopIterationsNoCheck(CommandSource src, Engine engine,
            Node node, long iterations, boolean sourceLevelIterations,
            Reference reference, Source source) {

        invalidateCachedStats();
        Node enclosingLoopNode = node.getEnclosingLoopNode();
        if (enclosingLoopNode != null) {
            enclosingLoopNode.getAttributes(true).setLoopIterations(src, engine, iterations, sourceLevelIterations);
            enclosingLoopNode.getAttributes(true).addKnown(m_route, Type.LOOP_ITERATIONS, source, new Reference[] {reference});
            clearPathIterations(enclosingLoopNode);
            refreshDisplayedUnknowns();
        }
    }

    // ===================================================================== //
    // Setting Loop Path Iterations
    // ===================================================================== //
    public void setLoopPathIterations(Reference reference,
            long iterations, Source source) throws AbortedException {

        for (Node node : m_route.getReferencedNodes(reference)) {
            setLoopPathIterationsNoCheck(node, iterations, reference, source);
        }
    }

    public void checkSetLoopPathIterations(Reference reference, final long iterations,
            Notifier notifier, CommandSource src) throws Exception {

        if (iterations < 0)
            throw new Exception("Cannot set a negative iteration count");

        checkReferencedNodes(reference, notifier, src, new NodeChecker() {
            public void checkNode(Node node) throws Exception {
                checkSetLoopPathIterations(node);
                checkLoopPathIterationsValid(node, iterations);
            }
        });
    }

    public void setLoopPathIterations(Node node, long iterations, Source source) throws Exception {
        if (iterations < 0)
            throw new Exception("Cannot set a negative iteration count");

        checkSetLoopPathIterations(node);
        checkLoopPathIterationsValid(node, iterations);
        setLoopPathIterationsNoCheck(node, iterations, null, source);
    }

    public void checkSetLoopPathIterations(Node node) throws Exception {
        if (node.getEnclosingInfiniteLoopNode() != null)
            throw new Exception("Cannot set loop iterations on an infinite loop");

        if (node.getEnclosingLoopNode() == null)
            throw new Exception("Must be within a loop to be able to set loop path iterations");

        if (node.getEnclosingCondNode() == null)
            throw new Exception("Must be within a condition to be able to set loop path iterations");

        if (node.getEnclosingOptionNode() == null)
            throw new Exception("Must be within a condition to be able to set loop path iterations");
    }

    public long getSetLoopPathIterations(Node node) {
        Node condNode = node.getEnclosingCondNode();
        Node optionNode = node.getEnclosingOptionNode();
        long iterations = condNode.getAttributes(false).getSetLoopPathIterations(optionNode);
        return iterations;
    }

    private void checkLoopPathIterationsValid(Node node, long iterations) throws Exception {
        Node condNode = node.getEnclosingCondNode();
        Node optionNode = node.getEnclosingOptionNode();
        condNode.getAttributes(true).loopPathIterationsValid(optionNode, iterations);
    }

    private void setLoopPathIterationsNoCheck(Node node, long iterations, Reference reference, Source source) {
        invalidateCachedStats();
        Node condNode = node.getEnclosingCondNode();
        Node optionNode = node.getEnclosingOptionNode();
        if ((condNode != null) && (optionNode != null)) {
            condNode.getAttributes(true).setLoopPathIterations(optionNode, iterations);
            optionNode.getAttributes(true).addKnown(m_route, Type.LOOP_PATH_ITERATIONS, source, new Reference[] {reference});
            refreshDisplayedUnknowns();
        }
    }

    // ===================================================================== //
    // Setting Loop Scope
    // ===================================================================== //
    public void setLoopScope(Reference reference, boolean isAbsolute,
            Source source) throws AbortedException {

        for (Node node : m_route.getReferencedNodes(reference)) {
            setLoopScopeNoCheck(node, isAbsolute, reference, source);
        }
    }

    public void checkSetLoopScope(Reference reference, boolean isAbsolute,
            Notifier notifier, CommandSource src) throws Exception {

        checkReferencedNodes(reference, notifier, src, new NodeChecker() {
            public void checkNode(Node node) throws Exception {
                checkSetLoopScope(node);
            }
        });
    }

    public void setLoopScope(Node node, boolean isAbsolute, Source source) throws Exception {
        checkSetLoopScope(node);
        setLoopScopeNoCheck(node, isAbsolute, null, source);
    }

    public void checkSetLoopScope(Node node) throws Exception {
        if (node.getEnclosingLoopNode() == null)
            throw new Exception("Must be within a loop to be able to set loop scope");
    }

    public boolean getSetLoopScope(Node node) {
        Node enclosingLoopNode = node.getEnclosingLoopNode();
        return enclosingLoopNode.getAttributes(false).getIsAbsolute();
    }

    private void setLoopScopeNoCheck(Node node, boolean isAbsolute, Reference reference, Source source) {
        invalidateCachedStats();
        Node enclosingLoopNode = node.getEnclosingLoopNode();
        if (enclosingLoopNode != null) {
            enclosingLoopNode.getAttributes(true).setIsAbsolute(isAbsolute);
            enclosingLoopNode.getAttributes(true).addKnown(m_route, Type.LOOP_SCOPE, source, new Reference[] {reference});
        }
    }

    // ===================================================================== //
    // Setting Exclusions
    // ===================================================================== //
    public boolean setExclusion(CommandSource src, Engine engine, Reference reference,
            Type type, Source source) throws AbortedException {

        Set<Node> nodes = m_route.getReferencedNodes(reference);
        for (Node node : nodes) {
            setExclusion(src, engine, node, reference, type, source);
        }
        return true;
    }

    public void setExclusion(CommandSource src, Engine engine,
            Node node, Type type, Source source) {

        setExclusion(src, engine, node, null, type, source);
    }

    public boolean checkExclusionWillLeaveAPath(Node node) throws Exception {
        Node currentNode = node;
        while (currentNode.getParent()!= null) {
            Node parent = currentNode.getParent();

            if (parent instanceof ConditionalNode) {
                if (parent.iChildren().size() > 1) {
                    return true;
                }
            } else if (parent instanceof LoopNode) {
                if (!currentNode.getAttributes(false).getIsLoopHeader()) {
                    return true;
                }
            }
            currentNode = parent;
        }
        throw new Exception("No paths will remain if exclusion is applied");
    }

    private void setExclusion(CommandSource src, Engine engine, Node node,
            Reference reference, Type type, Source source) {

        invalidateCachedStats();
        Node currentNode = node;
        while (currentNode.getParent() != null) {
            Node parent = currentNode.getParent();

            if (parent instanceof ConditionalNode) {
                ConditionalNode condNode = (ConditionalNode)parent;
                condNode.setAlive(currentNode, false);
                if (!condNode.iChildren().isEmpty()) {
                    currentNode.getAttributes(true).addKnown(m_route, Type.EXCLUSION, source, new Reference[] {reference});
                    break;
                }
            } else if (parent instanceof LoopNode) {
                LoopNode loopNode = (LoopNode)parent;
                if (!currentNode.getAttributes(false).getIsLoopHeader()) {
                    loopNode.setAlive(currentNode, false);
                    currentNode.getAttributes(true).addKnown(m_route, Type.EXCLUSION, source, new Reference[] {reference});
                    setLoopIterationsNoCheck(src, engine, currentNode, 0, false, reference, source);
                    break;
                }
            }
            currentNode = currentNode.getParent();
        }
        refreshDisplayedUnknowns();
    }

    // ===================================================================== //
    // Fixed Time Methods
    // ===================================================================== //
    public boolean setFixedTime(Reference reference, TimeUnit timeUnit,
            Type type, Source source) throws AbortedException {

        boolean found = false;
        for (Node node : m_route.getReferencedNodes(reference)) {
            setFixedTime(node, timeUnit, reference, type, source);
            found = true;
        }
        return found;
    }

    public void setFixedTime(Node node, TimeUnit timeUnit, Type type, Source source) {
        setFixedTime(node, timeUnit, null, type, source);
    }

    public void setFixedTime(Node node, TimeUnit timeUnit, Reference reference, Type type, Source source) {
        timeUnit.setCoreConfig(m_route.getRouteCoreContainer().getCoreConfig());
        invalidateCachedStats();
        node.getAttributes(true).setFixedTime(timeUnit);
        node.getAttributes(true).addKnown(m_route, type, source, new Reference[] {reference});
        refreshDisplayedUnknowns();
    }

    public TimeUnit getFixedTime(Node node) {
        return node.getAttributes(false).getFixedTime();
    }

    public boolean canInstructionPause(Reference reference) throws AbortedException {
        boolean canPause = false;

        for (Node node : m_route.getReferencedNodes(reference)) {
            if (((IssueNode)node).getInstruction().canPause()) {
                canPause = true;
            }
        }
        return canPause;
    }

    void invalidateCachedStats() {
        m_route.getNode().traverse(new UntypedNodeTraverser() {
            public TraverseStatus foundNode(Node node) {
                node.getAttributes(true).invalidateCachedStats();
                return TraverseStatus.CONTINUE;
            }
        });
    }

    // ===================================================================== //
    // Helper Methods
    // ===================================================================== //
    public void refreshDisplayedUnknowns() {
        refreshDisplayedUnknowns(m_route.getNode());
    }

    private void refreshDisplayedUnknowns(Node node) {
        // Clears the display unknown flags on all nodes..
        node.traverse(new UntypedNodeTraverser() {
            public TraverseStatus foundNode(Node node) {
                node.getAttributes(true).setDisplayAsUnknown(false);
                return TraverseStatus.CONTINUE;
            }
        });

        // On each of the unknown nodes, mark all of their parents to 'display unknown'.
        node.traverse(new UntypedNodeTraverser() {
            public TraverseStatus foundNode(Node node) {
                if (node.getAttributes(false).hasUnknowns()) {
                    setDisplayAsUnknownOnParentNodes(node);
                }
                return TraverseStatus.CONTINUE;
            }
        });
    }

    private void setDisplayAsUnknownOnParentNodes(Node node) {
        Node parent = node;
        while (parent != null) {
            parent.getAttributes(true).setDisplayAsUnknown(true);
            parent = parent.getParent();
        }
    }

    private void clearPathIterations(Node node) {
        node.traverse( new DefaultNodeTraverser() {
            public TraverseStatus foundConditionalNode(Node node) {
                node.getAttributes(true).resetPathIterations();
                return TraverseStatus.CONTINUE;
            }
        });
    }

    private void checkReferencedNodes(Reference reference, Notifier notifier,
            CommandSource src, NodeChecker checker) throws Exception {

        Set<Node> referencedNodes = m_route.getReferencedNodes(reference);
        if (referencedNodes.size() > 0) {

            boolean hit = false;
            boolean missed = false;
            String reason = "";

            for (Node node : referencedNodes) {
                try {
                    checker.checkNode(node);
                    hit = true;

                } catch (Exception e) {
                    missed = true;
                    reason = e.getMessage();
                }
            }
            if (!hit) {
                throw new Exception(reason);
            }
            if (missed) {
                notifier.notify(src, Notifier.NOTIFY_WARNING, reason);
            }

        } else {
            throw new Exception("Reference not found on given route");
        }
    }

    private interface NodeChecker {
        void checkNode(Node node) throws Exception;
    }
}
