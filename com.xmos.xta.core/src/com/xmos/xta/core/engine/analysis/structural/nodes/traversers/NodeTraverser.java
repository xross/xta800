package com.xmos.xta.core.engine.analysis.structural.nodes.traversers;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public interface NodeTraverser {
	TraverseStatus foundBlockNode(Node node);
	TraverseStatus foundFunctionNode(Node node);
	TraverseStatus foundInstructionNode(Node node);
	TraverseStatus foundSequenceNode(Node node);
	TraverseStatus foundLoopNode(Node node);
	TraverseStatus foundInfiniteLoopNode(Node node);
	TraverseStatus foundSelfLoopNode(Node node);
	TraverseStatus foundConditionalNode(Node node);
	TraverseStatus foundInfiniteSelfLoopNode(Node node);
	TraverseStatus foundFunctionReferenceNode(Node node);
}