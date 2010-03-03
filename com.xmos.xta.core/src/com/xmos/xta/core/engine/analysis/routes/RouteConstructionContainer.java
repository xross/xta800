package com.xmos.xta.core.engine.analysis.routes;

import java.util.Set;
import java.util.Stack;

import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionReferenceNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class RouteConstructionContainer {
	private Stack<FunctionReferenceNode> 	m_currentStack;
	private Node 							m_currentNode;
	private Set<StackNode> 					m_endStackNodes;
	private Set<StackNode> 					m_exclusionStackNodes;
	private Set<Node> 						m_workingSet;
	private Node 							m_workingSetStartNode;
	private Node 							m_prevCFGNode;

	public RouteConstructionContainer(
			Stack<FunctionReferenceNode> currentStack, Node currentNode,
			Set<StackNode> endStackNodes, Set<StackNode> exclusionStackNodes,
			Set<Node> workingSet, Node workingSetStartNode, Node prevCFGNode) {
		super();
		m_currentStack = currentStack;
		m_currentNode = currentNode;
		m_endStackNodes = endStackNodes;
		m_exclusionStackNodes = exclusionStackNodes;
		m_workingSet = workingSet;
		m_workingSetStartNode = workingSetStartNode;
		m_prevCFGNode = prevCFGNode;
	}

	public Stack<FunctionReferenceNode> getCurrentStack() {
		return m_currentStack;
	}

	public void setCurrentStack(Stack<FunctionReferenceNode> currentStack) {
		m_currentStack = currentStack;
	}

	public Node getCurrentNode() {
		return m_currentNode;
	}

	public void setCurrentNode(Node currentNode) {
		m_currentNode = currentNode;
	}

	public Set<StackNode> getEndStackNodes() {
		return m_endStackNodes;
	}

	public void setEndStackNodes(Set<StackNode> endStackNodes) {
		m_endStackNodes = endStackNodes;
	}

	public Set<StackNode> getExclusionStackNodes() {
		return m_exclusionStackNodes;
	}

	public void setExclusionStackNodes(Set<StackNode> exclusionStackNodes) {
		m_exclusionStackNodes = exclusionStackNodes;
	}

	public Set<Node> getWorkingSet() {
		return m_workingSet;
	}

	public void setWorkingSet(Set<Node> workingSet) {
		m_workingSet = workingSet;
	}

	public Node getWorkingSetStartNode() {
		return m_workingSetStartNode;
	}

	public void setWorkingSetStartNode(Node workingSetStartNode) {
		m_workingSetStartNode = workingSetStartNode;
	}

	public Node getPrevCFGNode() {
		return m_prevCFGNode;
	}

	public void setPrevCFGNode(Node prevCFGNode) {
		m_prevCFGNode = prevCFGNode;
	}
}
