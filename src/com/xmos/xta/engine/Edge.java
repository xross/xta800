package com.xmos.xta.engine;

public class Edge implements Comparable<Edge> {

	   BasicBlock from, to;
	   int weight;

	   Edge(BasicBlock f, BasicBlock t, int w){
	       from = f;
	       to = t;
	       weight = w;
	   }

	   public int compareTo(Edge e){
	       return weight - e.weight;
	   }
	}
