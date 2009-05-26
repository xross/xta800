package com.xmos.xta.engine.analysis;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import com.xmos.xta.engine.BasicBlock;
import com.xmos.xta.engine.analysis.nodes.Block;
import com.xmos.xta.engine.analysis.nodes.Node;
import com.xmos.xta.engine.analysis.patterns.CommonMethods;


public class XTACommonHeaderTest {
	
	@Test
	public void testEmbeddedLoop(){
		Collection<Node> nodes = new HashSet<Node>();
		
		Block n0 = new Block(new BasicBlock(0));
		Block n1 = new Block(new BasicBlock(1));
		Block n2 = new Block(new BasicBlock(2));
		Block n3 = new Block(new BasicBlock(3));

		
		nodes.add(n0);
		nodes.add(n1);
		nodes.add(n2);
		nodes.add(n3);

		
		CommonMethods.connect(n1, n0);
		CommonMethods.connect(n3, n0);
		CommonMethods.connect(n2, n1);
		CommonMethods.connect(n1, n2);
		CommonMethods.connect(n0, n2);
		
		runTest(nodes, n0);

	}

	private void runTest(Collection<Node> nodes, Block n0) {
		Collection<Node> startNodes = new HashSet<Node>();
		startNodes.add(n0);
		XTAGraph graph = new XTAGraph(nodes, startNodes);
		graph.decomposeAll();
		testGraph(graph);
		//graph.displayStructureTreeAll();
	}
	
	@Test
	public void testCommonHeaderGraph(){
		Collection<Node> nodes = new HashSet<Node>();
		
		Block n0 = new Block(new BasicBlock(0));
		Block n1 = new Block(new BasicBlock(1));
		Block n2 = new Block(new BasicBlock(2));
		Block n3 = new Block(new BasicBlock(3));
		Block n4 = new Block(new BasicBlock(4));
		Block n5 = new Block(new BasicBlock(5));
		Block n6 = new Block(new BasicBlock(6));
		Block n7 = new Block(new BasicBlock(7));
		Block n8 = new Block(new BasicBlock(8));
		
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
		
		runTest(nodes, n0);

	}
	
	@Test
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
		
		runTest(nodes, n1);
		
	}
	
	@Test
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

		
		
		runTest(nodes, n0);

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

		runTest(nodes, n0);

	}
	private void testGraph(XTAGraph graph) {
		for(ThreadGraph tg : graph.getThreads()){
			assertTrue(tg.getGraphSize() == 1);
		}
	}
}
