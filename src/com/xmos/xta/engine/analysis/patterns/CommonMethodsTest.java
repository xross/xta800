package com.xmos.xta.engine.analysis.patterns;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import com.xmos.xta.engine.analysis.nodes.Block;
import com.xmos.xta.engine.analysis.nodes.Node;

public class CommonMethodsTest {

	//TODO -- make the testing of the common methods more thorough
	
	@Test
	public void testWedgeUpNodeCollectionOfNodeBoolean() {
		Collection<Node> nodes = new HashSet<Node>();
		
		int prevCount = 3;
		int nextCount = 2;
		
		Block [] prevs = new Block [prevCount];
		for(int i = 0;i<prevCount;i++){
			prevs[i] = new Block();
			nodes.add(prevs[i]);
		}
		Block [] nexts = new Block [nextCount];
		for(int i = 0;i<nextCount;i++){
			nexts[i] = new Block();
			nodes.add(nexts[i]);
		}

		Block base = new Block();
		nodes.add(base);
		for(Node prev : prevs){
			CommonMethods.connect(base, prev);
		}
		for(Node next : nexts){
			CommonMethods.connect(next, base);
		}
		
		CommonMethods.wedgeUp(base, nodes, false);
		
		assertTrue(nodes.size() == nextCount + prevCount + nextCount);
		
	}

	@Test
	public void testWedgeDownNodeCollectionOfNodeBoolean() {
		Collection<Node> nodes = new HashSet<Node>();
		
		int prevCount = 2;
		int nextCount = 3;
		
		Block [] prevs = new Block [prevCount];
		for(int i = 0;i<prevCount;i++){
			prevs[i] = new Block();
			nodes.add(prevs[i]);
		}
		Block [] nexts = new Block [nextCount];
		for(int i = 0;i<nextCount;i++){
			nexts[i] = new Block();
			nodes.add(nexts[i]);
		}

		Block base = new Block();
		nodes.add(base);
		for(Node prev : prevs){
			CommonMethods.connect(base, prev);
		}
		for(Node next : nexts){
			CommonMethods.connect(next, base);
		}
		
		CommonMethods.wedgeDown(base, nodes, false);
		
		assertTrue(nodes.size() == nextCount + prevCount + prevCount);
		
	}

}
