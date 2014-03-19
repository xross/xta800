package com.xmos.xta.core.engine.analysis.structural.nodes.attributes;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status.S;

public class BlockNodeAttributes extends NodeAttributes {
    public BlockNodeAttributes(Node node) {
        super(node);
    }

    public double getTimeTaken(Case c, Status status, Node ignoreIterationsAboveCond) {
        long totalIterations = getTotalIterations(c, ignoreIterationsAboveCond);
        if (totalIterations == INFINITE_ITERATIONS) {
            status.mergeStatus(S.INFINITE);
            return Double.MAX_VALUE;

        } else if (totalIterations == 0) {
            status.mergeStatus(S.ZERO);
            return 0;
        }

        if (getFixedTime() != null) {
            return getFixedTime().getNs() * totalIterations;
        }
        return super.getTimeTaken(c, status, ignoreIterationsAboveCond) * totalIterations;
    }

    public long getNumCycles(Case c, Status status, Node ignoreIterationsAboveCond) {
        long totalIterations = getTotalIterations(c, ignoreIterationsAboveCond);
        if (totalIterations == INFINITE_ITERATIONS) {
            status.mergeStatus(S.INFINITE);
            return Long.MAX_VALUE;

        } else if (totalIterations == 0) {
            status.mergeStatus(S.ZERO);
            return 0;
        }
        return super.getNumCycles(c, status, ignoreIterationsAboveCond) * totalIterations;
    }

    public long getNumInstructions(Case c, Status status, Node ignoreIterationsAboveCond) {
        long totalIterations = getTotalIterations(c, ignoreIterationsAboveCond);
        if (totalIterations == INFINITE_ITERATIONS) {
            status.mergeStatus(S.INFINITE);
            return Long.MAX_VALUE;

        } else if (totalIterations == 0) {
            status.mergeStatus(S.ZERO);
            return 0;
        }
        return super.getNumInstructions(c, status, ignoreIterationsAboveCond) * totalIterations;
    }

    public long getNumFNops(Case c, Status status, Node ignoreIterationsAboveCond) {
        long totalIterations = getTotalIterations(c, ignoreIterationsAboveCond);
        if (totalIterations == INFINITE_ITERATIONS) {
            status.mergeStatus(S.INFINITE);
            return Long.MAX_VALUE;

        } else if (totalIterations == 0) {
            status.mergeStatus(S.ZERO);
            return 0;
        }
        return super.getNumFNops(c, status, ignoreIterationsAboveCond) * totalIterations;
    }
}
