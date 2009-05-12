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
		
		//graph.printGraph(nodes);
		
		graph.decompose();
		testGraph(graph);
		//graph.renderGraph(graph.m_nodes.iterator().next());

	}
	
	@Test
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
		
		//graph.printGraph(nodes);
		
		graph.decompose();
		testGraph(graph);
		//graph.renderGraph(graph.m_nodes.iterator().next());

	}
	private void testGraph(XTAGraph graph) {
		assertTrue(graph.getGraphSize() == 1);
	}
}
