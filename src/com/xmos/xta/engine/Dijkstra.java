package com.xmos.xta.engine;

import java.util.*;

public class Dijkstra {

	// assumes Nodes are numbered 0, 1, ... n and that the source Node is 0
	ArrayList<Node> findShortestPath(Node[] nodes, Edge[] edges, Node target) {
		int[][] Weight = initializeWeight(nodes, edges);
		int[] D = new int[nodes.length];
		Node[] P = new Node[nodes.length];
		ArrayList<Node> C = new ArrayList<Node>();

		// initialize:
		// (C)andidate set,
		// (D)yjkstra special path length, and
		// (P)revious Node along shortest path
		for (int i = 0; i < nodes.length; i++) {
			C.add(nodes[i]);
			D[i] = Weight[0][i];
			if (D[i] != Integer.MAX_VALUE) {
				P[i] = nodes[0];
			}
		}

		// crawl the graph
		for (int i = 1; i < nodes.length - 1; i++) {
			// find the lightest Edge among the candidates
			int l = Integer.MAX_VALUE;
			Node n = nodes[0];
			for (Node j : C) {
				if (D[j.m_nodeId] < l) {
					n = j;
				}
			}
			C.remove(n);

			// see if any Edges from this Node yield a shorter path than from
			// source->that Node
			for (int j = 0; j < nodes.length - 1; j++) {
				if (D[n.m_nodeId] != Integer.MAX_VALUE
						&& Weight[n.m_nodeId][j] != Integer.MAX_VALUE
						&& D[n.m_nodeId] + Weight[n.m_nodeId][j] < D[j]) {
					// found one, update the path
					D[j] = D[n.m_nodeId] + Weight[n.m_nodeId][j];
					P[j] = n;
				}
			}
		}
		// we have our path. reuse C as the result list
		C.clear();
		int loc = target.m_nodeId;
		C.add(target);
		// backtrack from the target by P(revious), adding to the result list
		while (P[loc] != nodes[0]) {
			if (P[loc] == null) {
				// looks like there's no path from source to target
				return null;
			}
			C.add(0, P[loc]);
			loc = P[loc].m_nodeId;
		}
		C.add(0, nodes[0]);
		return C;
	}

	private int[][] initializeWeight(Node[] nodes, Edge[] edges) {
		int[][] Weight = new int[nodes.length][nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			Arrays.fill(Weight[i], Integer.MAX_VALUE);
		}
		for (Edge e : edges) {
			Weight[e.from.m_nodeId][e.to.m_nodeId] = e.weight;
		}
		return Weight;
	}
}
