package com.xmos.xta.engine.analysis.structural.patterns;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.TreeSet;

import org.junit.Test;

import com.xmos.xta.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;

public class SectionPatternMatcherTest {

	@Test
	public void testIsMatch() {
		Collection<Node> nodes = new TreeSet<Node>();

		BlockNode [] node = new BlockNode [5];
		for(int i = 0;i<5;i++){
			node[i] = new BlockNode();
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
