package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.xmos.xta.core.engine.references.types.ReferenceType;

public class NodeDescriptor implements Comparable<NodeDescriptor>{
	private Node m_node;
	private Stack<Node> m_ancestors;
	private Set<NodeDescriptor> m_previous;	
	private Set<NodeDescriptor> m_next;
	
    public NodeDescriptor(Node node, Stack<Node> ancestors) {    	
    	m_node = node;
    	
    	m_ancestors = new Stack<Node>();
    	m_ancestors.addAll(ancestors);    	
    	
    	m_previous = new HashSet<NodeDescriptor>();
    	m_next = new HashSet<NodeDescriptor>();
    }
    
    public Node getNode() {
    	return m_node;
    }

    public void setNode(Node node) {
    	m_node = node;
    }
    
    public Stack<Node> getAncestors() {
    	return m_ancestors;
    }

    public void setAncestors(Stack<Node> ancestors) {
    	m_ancestors = ancestors;
    }
    
	public boolean checkBackTrail(Stack<ReferenceType> referenceTypes) {
		Stack<Node> ancestors = new Stack<Node>();
		for (Node ancestor : m_ancestors) {
			ancestors.push(ancestor);
		}
		return m_node.checkBackTrail(referenceTypes, ancestors, null, false);
	}
	
	public int compareTo(NodeDescriptor nodeDescriptor) {
		// First compare the node id's...
		if (m_node.getId() != nodeDescriptor.getNode().getId()) {
			return Integer.signum(m_node.getId() - nodeDescriptor.getNode().getId());			
		}
		
		// Next compare the number of ancestors..
		if (m_ancestors.size() != nodeDescriptor.getAncestors().size()) {
			return Integer.signum(m_ancestors.size() - nodeDescriptor.getAncestors().size());						
		}
		
		// Next compare the node id's of each of their ancestors in turn..
		for (int i = 0; i < m_ancestors.size(); ++i) {
			if (m_ancestors.get(i).getId() != nodeDescriptor.getAncestors().get(i).getId()) {
				return Integer.signum(m_ancestors.get(i).getId() - nodeDescriptor.getAncestors().get(i).getId());						
			}
		}
		
		// They must be the same!
		return 0;
	}
	
    public boolean equals(Object obj) {
    	if (obj == null)
    	    return false;
               
        if(!(obj instanceof NodeDescriptor))
            return false;

    	// A node descriptor is equal to another if its node id, and
    	// all of its ancestors node id's, match.
        NodeDescriptor nodeDescriptor = (NodeDescriptor)obj;
               
        if (m_node.getId() != nodeDescriptor.getNode().getId())
    	    return false;

        if (m_ancestors.size() != nodeDescriptor.getAncestors().size()) 
    	    return false;

        for (int i = 0; i < m_ancestors.size(); ++i) {
    	    if (m_ancestors.get(i).getId() != nodeDescriptor.getAncestors().get(i).getId())
    	        return false;
        }
    	return true;
    }	
    
    public int hashCode() { 
        int hash = 1;
        hash = hash * 31 + m_node.getId();
        if (m_ancestors == null) {
        	hash = hash * 31;
        } else {
        	for (Node ancestor : m_ancestors) {
                hash = hash * 31 + ancestor.getId();        		
        	}        	
        }
        return hash;
    }

    public void addPreviousNode(NodeDescriptor previous) {
    	m_previous.add(previous);
    }

    public void removePreviousNode(NodeDescriptor previous) {
    	m_previous.remove(previous);
    }
    
    public Set<NodeDescriptor> getPreviousNodes() {
    	return m_previous;
    }

	public NodeDescriptor getPreviousNode(){
		assert(m_previous != null);
		assert(m_previous.size() == 1);
		return m_previous.iterator().next();
	}
    
    public void addNextNode(NodeDescriptor next) {
    	m_next.add(next);
    }

    public void removeNextNode(NodeDescriptor next) {
    	m_next.remove(next);
    }

    public Set<NodeDescriptor> getNextNodes() {
    	return m_next;
    }
    
	public NodeDescriptor getNextNode() {
		assert(m_next != null);
		assert(m_next.size() == 1);
		return m_next.iterator().next();
	}
	    
    public String toString() {
    	String str = String.format("[ %d -> ", m_node.getId());
    	for (Node ancestor : m_ancestors) {
    		str += String.format("%d ", ancestor.getId());
    	}
    	
    	str += "Next: ";
    	for (NodeDescriptor next : m_next) {
    		str += String.format("%d ", next.getNode().getId());
    	}

    	str += "Prev: ";
    	for (NodeDescriptor prev : m_previous) {
    		str += String.format("%d ", prev.getNode().getId());
    	}
    	
    	if (m_node.getInstructions().size() > 0) {
        	str += String.format(" (%s, 0x%x)]", m_node, m_node.getStartInstruction().getPc());    		
    	} else {
        	str += String.format(" (%s)]", m_node);    		    		
    	}
    	return str;
    }
    
    public Set<NodeDescriptor> getEndInstructions() {
    	return m_node.getEndInstructions(m_ancestors);
    }

    public List<NodeDescriptor> getImmediateChildren() {
    	return m_node.getDirectChildren(m_ancestors);
    }
}
