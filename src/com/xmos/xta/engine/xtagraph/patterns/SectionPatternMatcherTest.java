package com.xmos.xta.engine.xtagraph.patterns;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import com.xmos.xta.engine.xtagraph.nodes.Block;
import com.xmos.xta.engine.xtagraph.nodes.Node;

public class SectionPatternMatcherTest {

	@Test
	public void testIsMatch() {
		Collection<Node> nodes = new HashSet<Node>();

		Block [] node = new Block [5];
		for(int i = 0;i<5;i++){
			node[i] = new Block();
			nodes.add(node[i]);
		}
		CommonMethods.connect(node[2], node[0]);
		CommonMethods.connect(node[3], node[0]);
		CommonMethods.connect(node[1], node[0]);
		CommonMethods.connect(node[3], node[2]);
		CommonMethods.connect(node[4], node[1]);
		CommonMethods.connect(node[4], node[3]);
		
		SectionPatternMatcher spm = new SectionPatternMatcher();
		
		assertTrue(spm.isMatch(node[0], nodes, false, node[0]));
	}

}
