package com.xmos.xta.engine.analysis.structural.patterns;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import com.xmos.xta.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class CommonMethodsTest {

	//TODO -- make the testing of the common methods more thorough
	
	@Test
	public void testWedgeUpNodeCollectionOfNodeBoolean() {
		Collection<Node> nodes = new HashSet<Node>();
		
		int prevCount = 3;
		int nextCount = 2;
		
		BlockNode [] prevs = new BlockNode [prevCount];
		for(int i = 0;i<prevCount;i++){
			prevs[i] = new BlockNode();
			nodes.add(prevs[i]);
		}
		BlockNode [] nexts = new BlockNode [nextCount];
		for(int i = 0;i<nextCount;i++){
			nexts[i] = new BlockNode();
			nodes.add(nexts[i]);
		}

		BlockNode base = new BlockNode();
		nodes.add(base);
		for(Node prev : prevs){
			CommonMethods.connect(base, prev);
		}
		for(Node next : nexts){
			CommonMethods.connect(next, base);
		}
		
		CommonMethods.wedgeUp(base, nodes);
		
		assertTrue(nodes.size() == nextCount + prevCount + nextCount);
		
	}

	@Test
	public void testWedgeDownNodeCollectionOfNodeBoolean() {
		Collection<Node> nodes = new HashSet<Node>();
		
		int prevCount = 2;
		int nextCount = 3;
		
		BlockNode [] prevs = new BlockNode [prevCount];
		for(int i = 0;i<prevCount;i++){
			prevs[i] = new BlockNode();
			nodes.add(prevs[i]);
		}
		BlockNode [] nexts = new BlockNode [nextCount];
		for(int i = 0;i<nextCount;i++){
			nexts[i] = new BlockNode();
			nodes.add(nexts[i]);
		}

		BlockNode base = new BlockNode();
		nodes.add(base);
		for(Node prev : prevs){
			CommonMethods.connect(base, prev);
		}
		for(Node next : nexts){
			CommonMethods.connect(next, base);
		}
		
		CommonMethods.wedgeDown(base, nodes);
		
		assertTrue(nodes.size() == nextCount + prevCount + prevCount);
		
	}

}
