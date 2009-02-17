package com.xmos.xta.engine;

import java.util.ArrayList;

public class Node implements Comparable<Node> {

	   int name;
	   ArrayList<Integer> m_pcs;
	   
	   Node(int n){
	       name = n;
	       m_pcs = new ArrayList<Integer>();
	   }

	   public int compareTo(Node n){
	       if(n == this)
	           return 0;
	       return -1;
	   }
	}
