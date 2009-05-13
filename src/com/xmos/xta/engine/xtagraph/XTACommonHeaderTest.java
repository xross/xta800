package com.xmos.xta.engine.xtagraph;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import com.xmos.xta.engine.xtagraph.nodes.Block;
import com.xmos.xta.engine.xtagraph.nodes.Node;
import com.xmos.xta.engine.xtagraph.patterns.CommonMethods;


public class XTACommonHeaderTest {
	@Test
	public void testCommonHeaderGraph(){
		Collection<Node> nodes = new HashSet<Node>();
		
		Block n0 = new Block();
		Block n1 = new Block();
		Block n2 = new Block();
		Block n3 = new Block();
		Block n4 = new Block();
		Block n5 = new Block();
		Block n6 = new Block();
		Block n7 = new Block();
		Block n8 = new Block();
		
		nodes.add(n0);
		nodes.add(n1);
		nodes.add(n2);
		nodes.add(n3);
		nodes.add(n4);
		nodes.add(n5);
		nodes.add(n6);
		nodes.add(n7);
		nodes.add(n8);
		
		CommonMethods.connect(n1, n0);
		CommonMethods.connect(n2, n1);
		CommonMethods.connect(n8, n2);
		CommonMethods.connect(n3, n2);
		CommonMethods.connect(n7, n3);
		CommonMethods.connect(n4, n3);
		CommonMethods.connect(n5, n4);
		CommonMethods.connect(n6, n4);
		CommonMethods.connect(n7, n6);
		CommonMethods.connect(n8, n7);
		CommonMethods.connect(n1, n8);
		
		
		XTAGraph graph = new XTAGraph(nodes);
		
		graph.printGraph(nodes);
		
		for(Node node : nodes){
			for(Node next : node.getNextNodes()){
				if(node.getDominators().contains(next)){
					System.out.println("#" +node + " has " + next + " as a loop header");
				}
			}
		}
		
		
		graph.decompose();
		testGraph(graph);
		//
		graph.displayStructureTree();
		//graph.renderGraph(graph.m_cfgNodes.iterator().next());

	}
	
	//@Test
	public void testDominatorTreeTest(){
		Collection<Node> nodes = new HashSet<Node>();
		
		Block n1 = new Block();
		Block n2 = new Block();
		Block n3 = new Block();
		Block n4 = new Block();
		Block n5 = new Block();
		Block n6 = new Block();
		Block n7 = new Block();
		Block n8 = new Block();
		Block n9 = new Block();
		Block n10 = new Block();
		Block n11 = new Block();
		Block n12 = new Block();
		
		nodes.add(n1);
		nodes.add(n2);
		nodes.add(n3);
		nodes.add(n4);
		nodes.add(n5);
		nodes.add(n6);
		nodes.add(n7);
		nodes.add(n8);
		nodes.add(n9);
		nodes.add(n10);
		nodes.add(n11);
		nodes.add(n12);
		
		
		CommonMethods.connect(n2, n1);
		CommonMethods.connect(n3, n2);
		CommonMethods.connect(n4, n2);
		CommonMethods.connect(n2, n3);
		CommonMethods.connect(n2, n4);
		CommonMethods.connect(n5, n4);
		CommonMethods.connect(n6, n4);
		CommonMethods.connect(n7, n5);
		CommonMethods.connect(n8, n5);
		CommonMethods.connect(n7, n6);
		CommonMethods.connect(n11, n7);
		CommonMethods.connect(n9, n8);
		CommonMethods.connect(n8, n9);
		CommonMethods.connect(n10, n9);
		CommonMethods.connect(n5, n10);
		CommonMethods.connect(n12, n10);
		CommonMethods.connect(n12, n11);
		
		XTAGraph graph = new XTAGraph(nodes);
		graph.printDominatorTree();
		
		graph.decompose();
		testGraph(graph);
		//graph.renderGraph(graph.m_nodes.iterator().next());
		
	}
	
	//@Test
	public void testSectionOnly(){
		Collection<Node> nodes = new HashSet<Node>();
		
		Block n0 = new Block();
		Block n1 = new Block();
		Block n2 = new Block();
		Block n3 = new Block();
		
		
		nodes.add(n0);
		nodes.add(n1);
		nodes.add(n2);
		nodes.add(n3);

		
		CommonMethods.connect(n1, n0);
		CommonMethods.connect(n2, n0);
		CommonMethods.connect(n3, n1);
		CommonMethods.connect(n3, n2);

		
		
		XTAGraph graph = new XTAGraph(nodes);
		
		graph.printGraph(nodes);
		
		graph.decompose();
		testGraph(graph);
		//graph.renderGraph(graph.m_nodes.iterator().next());

	}
	//@Test
	public void testMultipleLoopEntry(){
		Collection<Node> nodes = new HashSet<Node>();
		
		Block n0 = new Block();
		Block n1 = new Block();
		Block n2 = new Block();
		Block n3 = new Block();
		Block n4 = new Block();
		Block n5 = new Block();
		Block n6 = new Block();
		
		nodes.add(n0);
		nodes.add(n1);
		nodes.add(n2);
		nodes.add(n3);
		nodes.add(n4);
		nodes.add(n5);
		nodes.add(n6);
		
		CommonMethods.connect(n1, n0);
		CommonMethods.connect(n2, n1);
		CommonMethods.connect(n3, n1);
		CommonMethods.connect(n4, n2);
		CommonMethods.connect(n4, n3);
		CommonMethods.connect(n5, n4);
		CommonMethods.connect(n6, n4);
		CommonMethods.connect(n3, n6);

		
		
		XTAGraph graph = new XTAGraph(nodes);
		
		graph.printGraph(nodes);
		
		graph.decompose();
		testGraph(graph);
		//graph.renderGraph(graph.m_nodes.iterator().next());

	}
	private void testGraph(XTAGraph graph) {
		assertTrue(graph.getGraphSize() == 1);
	}
}
