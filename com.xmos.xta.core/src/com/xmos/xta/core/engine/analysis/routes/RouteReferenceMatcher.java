package com.xmos.xta.core.engine.analysis.routes;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.DefaultNodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.types.ReferenceType;
import com.xmos.xta.core.engine.references.types.ReferenceType.Type;

public class RouteReferenceMatcher extends ReferenceMatcher {

	private static final boolean debug = false;
	
	public Set<Node> matchReference(Node baseOfRouteNode, final Reference ref){
		if (debug) System.out.println("####### begin reference match " + ref + " ###########");
		final Set<Node> matchingNodes = new TreeSet<Node>();
		
		//find all nodes in the route that match the ref base
		
		baseOfRouteNode.traverse(new DefaultNodeTraverser() {
			public TraverseStatus foundInstructionNode(Node node) {
				InstructionNode inst = (InstructionNode)node;
				if(ref.getPcs().contains(inst.getPC()) && !inst.getXtaInstruction().isFnop()){
					matchingNodes.add(node);
				}
				return TraverseStatus.CONTINUE;
			}
		});
		
		
		//refine the matchingNodes down to the ones that match the backtrail
		Set<Node> refinedNodes = refineMatchingNodes(ref, matchingNodes);
		if (debug) {
			System.out.println("matched: " + refinedNodes);
		}
		if (debug) System.out.println("####### end reference match " + ref + " ###########\n\n");
		return refinedNodes;
	}

	public Set<Node> getFunctionNodesMatchingReference(Node baseOfRouteNode,
			Reference reference) {
		Set<Node> matchingNodes = matchReference(baseOfRouteNode, reference);
		Set<Node> functionNodes = new TreeSet<Node>();
		for(Node node : matchingNodes){
			Node containingFunction = getFunction(node);
			if(containingFunction != null && !functionNodes.contains(containingFunction))
			functionNodes.add(containingFunction);
		}
		return functionNodes;
	}
	
	
	private Node getFunction(Node node) {
		Node parent = node.getParent();
		while(parent != null){
			if(parent instanceof FunctionNode){
				return parent;
			} else {
				parent = parent.getParent();
			}
		}
		return null;
	}

	private Set<Node> refineMatchingNodes(Reference ref, Set<Node> matchingNodes) {
		// TODO Auto-generated method stub
		Set<Node> refinedNodes = new TreeSet<Node>();
		
		for(long pc : ref.getPcs()){
			List<ReferenceType> refList = new LinkedList<ReferenceType>();
			
			if (debug) System.out.println("Matching PC: 0x" + Long.toHexString(pc));
			
			matchLabel(ref, pc, refList);
			
			//Collections.reverse(refList);
		
			for(Node node : matchingNodes){
				LinkedList<ReferenceType> matchingList = new LinkedList<ReferenceType>(refList);
				
				
				LinkedList<Node> nodeList = new LinkedList<Node>();
				node.buildListUp(nodeList, null);
				if (debug) System.out.println(nodeList);
				
				//now match the matchingList
				while(!nodeList.isEmpty()){
					Node endOfList = nodeList.getLast();
					nodeList.removeLast();
					while(matchesInstruction(matchingList, endOfList)){
						matchingList.removeLast();
					}
				}
				if(matchingList.isEmpty()){
					refinedNodes.add(node);
				}	
			}
		}
		
		return refinedNodes;
	}
	
	private boolean matchesInstruction(LinkedList<ReferenceType> matchingList, Node endOfList) {
		if(matchingList.isEmpty())
			return false;
		
		ReferenceType ref = matchingList.getLast();
		
		if(ref.getType() == Type.CALL){
			if(endOfList instanceof FunctionNode){
				FunctionNode fn = (FunctionNode) endOfList;
				if(ref.getPcs().contains(fn.getCallingFunctionRef().getFunctionCallPc())){
					return true;
				}
			}
		} else {
			if(endOfList instanceof InstructionNode){
				InstructionNode inst = (InstructionNode)endOfList;
				if(ref.getPcs().contains(inst.getPC())){
					return true;
				}
			}
		}
		return false;
	}
	
}
