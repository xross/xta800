package com.xmos.xta.engine;

public class Edge implements Comparable<Edge> {

	   Node from, to;
	   int weight;

	   Edge(Node f, Node t, int w){
	       from = f;
	       to = t;
	       weight = w;
	   }

	   public int compareTo(Edge e){
	       return weight - e.weight;
	   }
	}
