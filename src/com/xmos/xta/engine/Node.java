package com.xmos.xta.engine;

public class Node implements Comparable<Node> {

	   int name;

	   Node(int n){
	       name = n;
	   }

	   public int compareTo(Node n){
	       if(n == this)
	           return 0;
	       return -1;
	   }
	}
