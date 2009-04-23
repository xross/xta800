package com.xmos.xta.engine.xtagraph.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.xmos.xta.engine.XtaInstruction;

public class Loop extends Node {
	
	private List<AttachedLoop> m_loops;
	private Node m_base;
	
	public Loop(Loop loop){
		super();
		m_loops = loop.m_loops;
		m_base = loop.m_base;
	}
	
	
	public Loop(Node loop, Node base) {
		super();
		m_loops = new ArrayList<AttachedLoop>();
		m_loops.add(new AttachedLoop(loop));
		m_base = base;
	}
	
	public Loop(Set<Node> loops, Node base) {
		super();
		m_loops = new ArrayList<AttachedLoop>();
		for(Node loop : loops){
			m_loops.add(new AttachedLoop(loop));
		}
		m_base = base;
	}
	
	public void addLoop(Node loop){
		m_loops.add(new AttachedLoop(loop));
	}
	
	public boolean setIterations(Node loop, long count){
		for(AttachedLoop al : m_loops){
			if(al.getLoop() == loop){
				al.setIterations(count);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * order of loops unspecified
	 */
	public List<XtaInstruction> getInstructionStream() {
		List<XtaInstruction> insts = new ArrayList<XtaInstruction>();
		for(AttachedLoop al : m_loops){
			if(al.iterationsKnown()){
				insts.addAll(al.getLoop().getInstructionStream());
			}
		}
		return insts;
	}

	public List<XtaInstruction> getInstructions() {
		List<XtaInstruction> insts = new ArrayList<XtaInstruction>();
		for(AttachedLoop al : m_loops){
			if(al.iterationsKnown()){
				insts.addAll(al.getLoop().getInstructions());
			}
		}
		return insts;
	}

	public long getNumberCycles() {
		long count = 0;
		for(AttachedLoop al : m_loops){
			if(al.iterationsKnown()){
				count += (al.getLoop().getNumberCycles()*al.numIterations());
			}
		}
		return count;
	}

	public long getNumberFNOPS() {
		long count = 0;
		for(AttachedLoop al : m_loops){
			if(al.iterationsKnown()){
				count += (al.getLoop().getNumberFNOPS()*al.numIterations());
			}
		}
		return count;
	}

	public Set<Integer> getPCsCovered() {
		Set<Integer> pcs = new HashSet<Integer>();
		for(AttachedLoop al : m_loops){
			if(al.iterationsKnown()){
				pcs.addAll(al.getLoop().getPCsCovered());
			}
		}
		return pcs;
	}

	class AttachedLoop{
		private Node m_loop;
		private boolean m_iterationsKnown;
		private long m_iterations;
		
		public AttachedLoop(Node loop){
			m_loop = loop;
			m_iterationsKnown = false;
		}
		
		public Node getLoop(){
			return m_loop;
		}
		
		public boolean iterationsKnown(){
			return m_iterationsKnown;
		}
		
		public long numIterations(){
			if(m_iterationsKnown){
				return m_iterations;
			} else {
				return 0;
			}
		}
		
		public void setIterations(long count){
			m_iterationsKnown = true;
			m_iterations = count;
		}
	}

	public Loop clone() {
		//FIXME
		return new Loop(this);
	}



	public Collection<Node> getContaintedNodes() {
		Collection<Node> containedNodes = new HashSet<Node> ();
		containedNodes.addAll(m_base.getContaintedNodes());
		for(AttachedLoop loop: m_loops){
			containedNodes.addAll(loop.getLoop().getContaintedNodes());
		}
		return containedNodes;
	}


	public String getGraphvizName() {
		return "cluster_Loop" + m_id;
	}


	public void appendToGraph(StringBuffer body, StringBuffer connections,
			Collection<Node> last, Collection<Node> next) {
		body.append("subgraph " + getGraphvizName()  + " {\n");
		body.append("style=filled;\n");
		body.append("color=yellow;\n");

		Collection<Node> nextThis = new HashSet<Node>();
		m_base.appendToGraph(body, connections, last, nextThis);
		next.addAll(nextThis);
		if(!m_base.getGraphvizName().startsWith("B")){
			System.out.println(" ############################################## " +m_base.getGraphvizName());
		}
		
		Collection<Node> lastThis = nextThis;
		nextThis = new HashSet<Node>();
		
		for (AttachedLoop loopNode : m_loops) {
			loopNode.getLoop().appendToGraph(body, connections, lastThis, nextThis);
		}
		body.append("}\n");

		for (Node node : nextThis) {
			connections.append(  node.getGraphvizName() + " -> " + m_base.getGraphvizName() + ";\n");
		}
		
		return;
		
	}


}
