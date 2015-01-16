package com.xmos.xta.core.console.commands.print;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.analysis.structural.nodes.IssueNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.LoopNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status;

@SuppressWarnings("serial")
class EscapedDetctedException extends Exception {}
@SuppressWarnings("serial")
class UnresolvedDetctedException extends Exception {}
@SuppressWarnings("serial")
class RecursionDetctedException extends Exception {}
@SuppressWarnings("serial")
class IllegalException extends Exception {}

public class TraceDisplayer {
    private Console m_console;
    private double m_currentTime;

    public TraceDisplayer(Console console) {
        m_console = console;
    }

    public void display(Node node, Case c) {
        if (isInfinite(node, c)) {
            m_console.display("Cannot trace - time is infinite\n");
        } else {
            m_currentTime = 0;
            try {
                displayInstructions(node, c, 1, 0);

            } catch (EscapedDetctedException e) {
                m_console.display("Trace aborted...\n");
            } catch (UnresolvedDetctedException e) {

            } catch (RecursionDetctedException e) {
            } catch (IllegalException e) {}
            m_console.display("\n");
        }
    }

    private boolean isInfinite(Node node, Case c) {
        // Allow the user to always trace blocks
        if (node.isBlockNode())
            return false;

        Status status = new Status();
        node.getAttributes(false).getTimeTaken(c, status, null);
        if (status.isInfinite()) {
            return true;
        }
        return false;
    }

    private void displayInstructions(Node node, Case c, long totalIterations, long iteration)
        throws EscapedDetctedException, RecursionDetctedException,
               UnresolvedDetctedException, IllegalException {

        if (node == null || node.isNullNode())
            return;

        if (node.isIssueNode()) {
            displayIssue((IssueNode)node);

        } else if (node.isLoopNode() || node.isSelfLoopNode()) {
            displayLoop(node, c, totalIterations, iteration);

        } else if (node.isCondNode()) {
            Node child = getChild(node, c, totalIterations, iteration);
            displayInstructions(child, c, totalIterations, iteration);

        } else if (node.getAttributes(false).getFixedTime() != null) {
            // Deal with function/sequence nodes which have a fixed time
            double timeBefore = m_currentTime;
            displayChildren(node, c, totalIterations, iteration);
            double timeAfter = m_currentTime;

            double fixedTime = node.getAttributes(false).getFixedTime().getNs();
            double timeDifference = fixedTime - (timeAfter - timeBefore);
             m_console.display(String.format("  %.1f: (%.1fns) fixed %s time\n",
                     m_currentTime,
                     timeDifference,
                     node.isFunctionNode() ? "function" : "path"));

             m_currentTime = m_currentTime + timeDifference;

        } else {
            displayChildren(node, c, totalIterations, iteration);
        }
    }

    private Node getChild(Node cond, Case c, long totalIterations, long iteration) {
        for (Node child : cond.iChildren()) {
            long childIterations = cond.getAttributes(false).getLoopPathIterations(totalIterations, child, c);
            if (iteration < childIterations) {
                return child;
            }
            iteration -= childIterations;
        }
        return null;
    }

    private void displayChildren(Node node, Case c, long totalIterations, long iteration) throws EscapedDetctedException, RecursionDetctedException, UnresolvedDetctedException, IllegalException {
        for (Node child : node.iChildren()) {
            displayInstructions(child, c, totalIterations, iteration);
        }
    }

    private void displayLoop(Node node, Case c, long totalIterations, long iteration) throws EscapedDetctedException, IllegalException, RecursionDetctedException, UnresolvedDetctedException {
        long loopIterations = node.getAttributes(false).getLoopIterations();
        if (node.getAttributes(false).getIsAbsolute()) {
            if (iteration == 0) {
                // Only print body of absolute once
                for (int i = 0; i < loopIterations; i++) {
                    displayChildren(node, c, loopIterations, i);
                }
            }

            if (node.isLoopNode()) {
                displayInstructions(((LoopNode)node).getLoopHeader(), c, totalIterations, iteration);
            }

        } else {
            for (int i = 0; i < loopIterations; i++) {
                displayChildren(node, c, loopIterations, i);
            }
            if (node.isLoopNode()) {
                displayInstructions(((LoopNode)node).getLoopHeader(), c, totalIterations, 0);
            }
        }
    }

    private void displayIssue(IssueNode issueNode) throws EscapedDetctedException,
    IllegalException, UnresolvedDetctedException, RecursionDetctedException {
        m_console.display(String.format("%s %10.1f: (%5.1fns) %s%s%s%s%s\n",
                issueNode.getAttributes(false).getDisplayAsUnknown() ? "*" : " ",
                m_currentTime,
                issueNode.getAttributes(false).getFixedTime().getNs(),
                issueNode.getDescription(),
                issueNode.getUnresolved() ? " [UNRESOLVED]" : "",
                issueNode.getRecursion() ? " [RECURSION]" : "",
                issueNode.isIllegal() ? " [ILLEGAL]" : "",
                issueNode.canPause()? " (P)" : ""));
        m_currentTime += issueNode.getAttributes(false).getFixedTime().getNs();
        if (issueNode.getRecursion()) {
            throw new RecursionDetctedException();
        }
        if (issueNode.getUnresolved()) {
            throw new UnresolvedDetctedException();
        }
        if (m_console.hasEscaped()) {
            throw new EscapedDetctedException();
        }
        if (issueNode.isIllegal()) {
            throw new IllegalException();
        }
    }
}

