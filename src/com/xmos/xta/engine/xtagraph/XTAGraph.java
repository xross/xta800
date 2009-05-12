package com.xmos.xta.engine.xtagraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.xta.engine.BasicBlock;
import com.xmos.xta.engine.xtagraph.nodes.Block;
import com.xmos.xta.engine.xtagraph.nodes.Node;
import com.xmos.xta.engine.xtagraph.patterns.LoopPatternMatcher;
import com.xmos.xta.engine.xtagraph.patterns.LoopPeelingTransformer;
import com.xmos.xta.engine.xtagraph.patterns.MultiSectionPatternMatcher;
import com.xmos.xta.engine.xtagraph.patterns.MultiSectionTransformer;
import com.xmos.xta.engine.xtagraph.patterns.PathPatternMatcher;
import com.xmos.xta.engine.xtagraph.patterns.PatternMatcher;
import com.xmos.xta.engine.xtagraph.patterns.SelfLoopTransformer;
import com.xmos.xta.engine.xtagraph.patterns.TerminalPatternMatcher;
import com.xmos.xta.engine.xtagraph.patterns.TerminalTransformer;


public class XTAGraph {	
	static int g;
	private static int graphcount;
	private Set<BasicBlock> m_unreachable;
	public Collection<Node> m_nodes;
	private static final boolean DEBUG = true;
	private int m_graphSize;
	
	public XTAGraph(HashSet<BasicBlock> values) {
		m_unreachable = new HashSet<BasicBlock>();
		basicBlocksToNodes(values);
	}
	
	public XTAGraph(Collection<Node> nodes) {
		m_unreachable = new HashSet<BasicBlock>();
		m_nodes = new HashSet<Node>(nodes);
	}
	

	
	public void decompose(){

		//now we start applying the rules
		Collection<Node> workingSet = m_nodes;
		boolean working = true;
		
		List<PatternMatcher> patternMatchers = new ArrayList<PatternMatcher>();
		
		patternMatchers.add(new SelfLoopTransformer());
		patternMatchers.add(new PathPatternMatcher());
		patternMatchers.add(new TerminalPatternMatcher());
		patternMatchers.add(new MultiSectionPatternMatcher());
		patternMatchers.add(new LoopPatternMatcher());
		
		
		
		patternMatchers.add(new MultiSectionTransformer());
		patternMatchers.add(new LoopPeelingTransformer());
		patternMatchers.add(new TerminalTransformer());
		while(working){
			
			//check graph integrity
			
			PatternMatcher recent = null;
			
			working = false;
			
			for(PatternMatcher pm : patternMatchers){
				for(Node node : workingSet){
					if(pm.isMatch(node, workingSet, DEBUG)){
						working = true;
						recent = pm;
						break;
					}
				}
				if(working) break;
			}
			
			int ins=0;
			int outs=0;
			for(Node node : workingSet){
				ins +=node.getPrevNodes().size();
				outs+=node.getNextNodes().size();
			}
			if(ins != outs){
				System.out.println("graph integrity lost after " + recent.getClass().getSimpleName());
				if(ins > outs){
					System.out.println("too many ins");
				} else {
					System.out.println("too many outs");
				}
				
				return;
			}
		}
		
		m_graphSize = workingSet.size();
	}

	public static void printGraph(Collection<Node> workingSet) {
		System.out.println("digraph g" + graphcount++ +" {");
		for(Node node : workingSet){
			for(Node next : node.getNextNodes()){
				System.out.println(node.graphVizLabel() + " -> " + next.graphVizLabel() +";");
			}
		}
		System.out.println("};");
	}
	
	private void basicBlocksToNodes(HashSet<BasicBlock> nodes){
		Map<BasicBlock, Node> nodeSet = new HashMap<BasicBlock, Node>();
		//remove all unreachable basic blocks
		for(BasicBlock b : nodes){
			if(b.getPrevoiusNodes().size() == 0 && b.getInstructions().getFirst().getPc() != 0x10000){
				m_unreachable.add(b);
			} else {
				nodeSet.put(b, new Block(b));
			}
		}
		// now add all the edges into the graph
		for(BasicBlock b : nodes){
			if(!m_unreachable.contains(b)){
				for(BasicBlock next : b.getNextNodes()){
					nodeSet.get(b).addNextNode(nodeSet.get(next));
					nodeSet.get(next).addPrevNode(nodeSet.get(b));
				}	
			}
		}
		m_nodes = new HashSet<Node>(nodeSet.values());
	}
	
	public int getGraphSize(){
		return m_graphSize;
	}

	/**
	 * this taken a single node that represents the whole program and unpeels it to
	 * render the whole graph again with the structure
	 * @param node
	 */
	public void renderGraph(Node node){
		System.out.println("digraph f" + g++ + " {");
		StringBuffer body = new StringBuffer();
		StringBuffer connections = new StringBuffer();
		
		node.appendToGraph(body, connections, new HashSet<Node>(), new HashSet<Node>(), 0, node);
		
		System.out.println(body);
		System.out.println(connections);
		System.out.println("}");
	}
	
	
}
