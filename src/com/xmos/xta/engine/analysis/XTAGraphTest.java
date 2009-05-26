package com.xmos.xta.engine.analysis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import com.xmos.xta.engine.BasicBlock;
import com.xmos.xta.engine.analysis.nodes.Block;
import com.xmos.xta.engine.analysis.nodes.Node;
import com.xmos.xta.engine.analysis.patterns.CommonMethods;


public class XTAGraphTest {

	private static final int graphSize = 40; 
	private static final int graphLinks = 8  ;
	
	
	@Test
	public void testRandomGraph() {
		int testCount = 4;
		int successCount = 0;
		for(int j=0;j<testCount;j++){
			Block root = new Block(new BasicBlock(Node.getNodeCount()));
			List<Node> nodes = new ArrayList<Node>();
			for(int i=0;i<graphSize -1 ;i++){
				nodes.add(new Block(new BasicBlock(Node.getNodeCount())));
			}
			
			//make the graph randomly connected
			for(int i =1 ;i< graphSize;i++){
				int startBlock = (int) (Math.random() * (i%(graphSize -2)));
				Node startNode = nodes.get(startBlock);
				Node endNode =  nodes.get((i%(graphSize -1)));
				startNode.addNextNode(endNode);
				endNode.addPrevNode(startNode);
			}
			
			//then add lots of random links
			for(int i =0 ;i< graphLinks;i++){
				int start = (int) (Math.random() * (graphSize -1));
				int end = (int) (Math.random() * (graphSize -1));
				Node startNode = nodes.get(start);
				Node endNode =  nodes.get(end);
				startNode.addNextNode(endNode);
				endNode.addPrevNode(startNode);
			}
			
			Node startNode = nodes.get(0);
			root.addNextNode(startNode);
			startNode.addPrevNode(root);
			nodes.add(root);
			
			
			Collection<Node> startNodes = new HashSet<Node>();
			startNodes.add(root);
			XTAGraph graph = new XTAGraph(nodes, startNodes);
			graph.decomposeAll();
			for(ThreadGraph tg : graph.getThreads()){
				if(tg.getGraphSize() ==1){
					successCount++;
				} else {
					//tg.printGraph();
				}
			}
		}
		double successRate = (100.0*successCount)/testCount;
		assertEquals(100.0, successRate, 0.0);
	}
	
	
	
	@Test
	public void testNestedLoop(){
		
		for(int length =1; length < 10;length++){
			Collection<Node> nodes = new HashSet<Node>();
			Block head = new Block();
			nodes.add(head);
			
			Block body = new Block();
			nodes.add(body);
			
			Block tail = new Block();
			nodes.add(tail);
			
			CommonMethods.connect(body, head);
			CommonMethods.connect(tail, body);
			Node mostRecent = body;
			for(int i=0;i<length;i++){
				Block nest = new Block();
				nodes.add(nest);
				CommonMethods.connect(nest, mostRecent);
				CommonMethods.connect(mostRecent, nest);
				mostRecent = nest;
			}
			Collection<Node> startNodes = new HashSet<Node>();
			startNodes.add(head);
			XTAGraph graph = new XTAGraph(nodes, startNodes);
			graph.decomposeAll();
			testGraph(graph);
			
		}
	}
	
	@Test
	public void testSpecialLoop(){
		
		Collection<Node> nodes = new HashSet<Node>();
		
		Block n0 = new Block();
		nodes.add(n0);
		
		Block n1 = new Block();
		nodes.add(n1);
		
		Block n2 = new Block();
		nodes.add(n2);
		
		Block n3 = new Block();
		nodes.add(n3);
		
		Block n4 = new Block();
		nodes.add(n4);
		
		Block n5 = new Block();
		nodes.add(n5);
		
		Block n6 = new Block();
		nodes.add(n6);
		
		Block n7 = new Block();
		nodes.add(n7);
		
		CommonMethods.connect(n1, n0);
		CommonMethods.connect(n2, n0);
		CommonMethods.connect(n3, n0);
		CommonMethods.connect(n4, n0);
		CommonMethods.connect(n6, n5);
		CommonMethods.connect(n5, n2);
		CommonMethods.connect(n6, n3);
		CommonMethods.connect(n7, n1);
		CommonMethods.connect(n5, n7);
		CommonMethods.connect(n7, n6);
		
		Collection<Node> startNodes = new HashSet<Node>();
		startNodes.add(n0);
		XTAGraph graph = new XTAGraph(nodes, startNodes);
		graph.decomposeAll();
		testGraph(graph);
	}
	
	
	//@Test
	public void testTerminal(){
		
		Collection<Node> nodes = new HashSet<Node>();
		
		Block head = new Block();
		nodes.add(head);
		
		Block t0 = new Block();
		nodes.add(t0);
		Block t1 = new Block();
		nodes.add(t1);
		
		
		Block b0 = new Block();
		nodes.add(b0);
		Block b1 = new Block();
		nodes.add(b1);
		Block b2 = new Block();
		nodes.add(b2);
		
		CommonMethods.connect(b0, head);
		CommonMethods.connect(b1, b0);
		CommonMethods.connect(t0, b0);
		CommonMethods.connect(t1, b1);
		CommonMethods.connect(b2, b1);
		CommonMethods.connect(head, b2);
		
		Collection<Node> startNodes = new HashSet<Node>();
		startNodes.add(head);
		XTAGraph graph = new XTAGraph(nodes, startNodes);
		graph.decomposeAll();
		testGraph(graph);
	}

	private void testGraph(XTAGraph graph) {
		for(ThreadGraph tg : graph.getThreads()){
			assertTrue(tg.getGraphSize() == 1);
		}
	}
	
	@Test
	public void testGraphviz(){
		int maxLength = 5;
		for(int length = 0; length < maxLength;length++){
			Collection<Node> nodes = new HashSet<Node>();
			
			Block head = new Block();
			Node current = head;
			for(int i=0;i<length;i++){
				Block block = new Block();
				nodes.add(block);
				block.addPrevNode(current);
				current.addNextNode(block);
				current = block;
			}
			
			Block tail = new Block();
			Block leaf0 = new Block();
			Block leaf1 = new Block();
			tail.addNextNode(leaf0);
			leaf0.addPrevNode(tail);
			tail.addNextNode(leaf1);
			leaf1.addPrevNode(tail);
			
			current.addNextNode(tail);
			tail.addPrevNode(current);
			
			nodes.add(head);
			nodes.add(tail);
			nodes.add(leaf1);
			nodes.add(leaf0);
			Collection<Node> startNodes = new HashSet<Node>();
			startNodes.add(head);
			XTAGraph graph = new XTAGraph(nodes, startNodes);
			graph.decomposeAll();
			testGraph(graph);
		}
	}
	
	@Test
	public void testPath(){
		int maxLength = 20;
		for(int length = 0; length < maxLength;length++){
			Collection<Node> nodes = new HashSet<Node>();
			
			Block head = new Block();
			Node current = head;
			for(int i=0;i<length;i++){
				Block block = new Block();
				nodes.add(block);
				block.addPrevNode(current);
				current.addNextNode(block);
				current = block;
			}
			
			Block tail = new Block();
			Block leaf0 = new Block();
			Block leaf1 = new Block();
			tail.addNextNode(leaf0);
			leaf0.addPrevNode(tail);
			tail.addNextNode(leaf1);
			leaf1.addPrevNode(tail);
			
			current.addNextNode(tail);
			tail.addPrevNode(current);
			
			nodes.add(head);
			nodes.add(tail);
			nodes.add(leaf1);
			nodes.add(leaf0);
			Collection<Node> startNodes = new HashSet<Node>();
			startNodes.add(head);
			XTAGraph graph = new XTAGraph(nodes, startNodes);
			graph.decomposeAll();
			testGraph(graph);
		}
	}
	
	
	/**
	 * this makes a straight line path with a loop of various sizes attached to it
	 */
	@Test
	public void testSimpleLoop(){
		for(int length =1;length < 5; length ++){
			Collection<Node> nodes = new HashSet<Node>();
			
			Block head = new Block();
			nodes.add(head);
			Block body = new Block();
			nodes.add(body);
			Block tail = new Block();
			nodes.add(tail);
			
			CommonMethods.connect(body, head);
			CommonMethods.connect(tail, body);
				
			Node current = body;
			for(int i=0;i<length;i++){
				Block block = new Block();
				nodes.add(block);
				CommonMethods.connect(block, current);
				current = block;
			}
			
			CommonMethods.connect(body, current);
			
			
			Collection<Node> startNodes = new HashSet<Node>();
			startNodes.add(head);
			XTAGraph graph = new XTAGraph(nodes, startNodes);
			graph.decomposeAll();
			testGraph(graph);
		}
	}
	
	/**
	 * this makes a straight line path with equally weighted loops attached to it
	 */
	@Test
	public void testEquallyWeightedLoop(){
		for(int count =0; count < 20; count ++){
			Collection<Node> nodes = new HashSet<Node>();
			
			Block head = new Block();
			nodes.add(head);
			Block loopConnection = new Block();
			nodes.add(loopConnection);
			Block tail = new Block();
			nodes.add(tail);
			
			CommonMethods.connect(loopConnection, head);
			CommonMethods.connect(tail, loopConnection);
		
			
			for(int j=0;j< count ;j++){
				Node current = loopConnection;
				for(int i=0;i<5;i++){
					Block block = new Block();
					nodes.add(block);
					CommonMethods.connect(block, current);
					current = block;
				}
				CommonMethods.connect(loopConnection, current);
			}
			
			Collection<Node> startNodes = new HashSet<Node>();
			startNodes.add(head);
			XTAGraph graph = new XTAGraph(nodes, startNodes);
			graph.decomposeAll();
			testGraph(graph);
		}
	}
	
	/**
	 * this makes a straight line path with nested loops attached to it
	 */
	@Test
	public void testCommonHeader(){
		for(int length =2; length < 80; length ++){
			Collection<Node> nodes = new HashSet<Node>();
			Block head = new Block();
			Node current = head;

			for(int i=0;i<length;i++){
				Block block = new Block();
				nodes.add(block);
				block.addPrevNode(current);
				current.addNextNode(block);
				
				block.addNextNode(head);
				head.addPrevNode(block);
				
				current = block;
			}
			
			Block tail = new Block();
			Block leaf0 = new Block();
			Block leaf1 = new Block();
			tail.addNextNode(leaf0);
			leaf0.addPrevNode(tail);
			tail.addNextNode(leaf1);
			leaf1.addPrevNode(tail);
			
			current.addNextNode(tail);
			tail.addPrevNode(current);
			
			nodes.add(head);
			nodes.add(tail);
			nodes.add(leaf1);
			nodes.add(leaf0);
			Collection<Node> startNodes = new HashSet<Node>();
			startNodes.add(head);
			XTAGraph graph = new XTAGraph(nodes, startNodes);
			
			graph.decomposeAll();
			//graph.displayStructureTreeAll();
			//testGraph(graph);
		}
	}
	
	
	@Test
	public void testSingleSection(){
		
		for(int count = 2;count < 30;count++){
			Collection<Node> nodes = new HashSet<Node>();
			
			Block head = new Block();
			nodes.add(head);
			Block tail = new Block();
			nodes.add(tail);
			for(int i=0;i<count;i++){
				Block section = new Block();
				nodes.add(section);
				
				head.addNextNode(section);
				section.addPrevNode(head);
				
				section.addNextNode(tail);
				tail.addPrevNode(section);
				
			}
			
			
			Collection<Node> startNodes = new HashSet<Node>();
			startNodes.add(head);
			XTAGraph graph = new XTAGraph(nodes, startNodes);
			graph.decomposeAll();
			testGraph(graph);
		}
	}
	
	@Test
	public void testSingleSectionWithJump(){
		
		for(int count = 1;count < 10;count++){
			Collection<Node> nodes = new HashSet<Node>();
			
			Block head = new Block();
			nodes.add(head);
			Block tail = new Block();
			nodes.add(tail);
			head.addNextNode(tail);
			tail.addPrevNode(head);
			for(int i=0;i<count;i++){
				Block section = new Block();
				nodes.add(section);
				
				head.addNextNode(section);
				section.addPrevNode(head);
				
				section.addNextNode(tail);
				tail.addPrevNode(section);
				
			}
			
			Collection<Node> startNodes = new HashSet<Node>();
			startNodes.add(head);
			XTAGraph graph = new XTAGraph(nodes, startNodes);
			graph.decomposeAll();
			testGraph(graph);
		}
	}
	
	@Test
	public void testMultipleSectionWithJumps(){
		for(int sectionCount = 1;sectionCount < 40;sectionCount++){
			Collection<Node> nodes = new HashSet<Node>();
			Block [] tails  = new Block[sectionCount];
			for(int section = 0; section < sectionCount ;section++){
				tails[section] = new Block();
				nodes.add(tails[section]);
			}
			Block head = new Block();
			nodes.add(head);
			for(int section = 0; section < sectionCount ;section++){
				
				if(section%2 == 0){	
					head.addNextNode(tails[section]);
					tails[section].addPrevNode(head);
				}
				for(int i=0;i<3;i++){
					Block b = new Block();
					nodes.add(b);
					
					head.addNextNode(b);
					b.addPrevNode(head);
					
					b.addNextNode(tails[section]);
					tails[section].addPrevNode(b);
				}
			}
			Collection<Node> startNodes = new HashSet<Node>();
			startNodes.add(head);
			XTAGraph graph = new XTAGraph(nodes, startNodes);
			graph.decomposeAll();
			testGraph(graph);
		}
	}
	
	
	

}
