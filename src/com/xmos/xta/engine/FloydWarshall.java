package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Arrays;

public class FloydWarshall {

	   int[][] D;
	   BasicBlock[][] P;

	   public void calcShortestPaths(BasicBlock[] nodes, Edge[] edges) {
	       D = initializeWeight(nodes, edges);
	       P = new BasicBlock[nodes.length][nodes.length];

	       for(int k=0; k<nodes.length; k++){
	           for(int i=0; i<nodes.length; i++){
	               for(int j=0; j<nodes.length; j++){
	                   if(D[i][k] != Integer.MAX_VALUE && D[k][j] != Integer.MAX_VALUE && D[i][k]+D[k][j] < D[i][j]){
	                       D[i][j] = D[i][k]+D[k][j];
	                       P[i][j] = nodes[k];
	                   }
	               }
	           }
	       }
	   }

	   public int getShortestDistance(BasicBlock source, BasicBlock target){
	       return D[source.m_nodeId][target.m_nodeId];
	   }

	   public ArrayList<BasicBlock> getShortestPath(BasicBlock source, BasicBlock target){
	       if(D[source.m_nodeId][target.m_nodeId] == Integer.MAX_VALUE){
	           return new ArrayList<BasicBlock>();
	       }
	       ArrayList<BasicBlock> path = getIntermediatePath(source, target);
	       path.add(0, source);
	       path.add(target);
	       return path;
	   }

	   private ArrayList<BasicBlock> getIntermediatePath(BasicBlock source, BasicBlock target){
	       if(D == null){
	           throw new IllegalArgumentException("Must call calcShortestPaths(...) before attempting to obtain a path.");
	       }
	       if(P[source.m_nodeId][target.m_nodeId] == null){
	           return new ArrayList<BasicBlock>();
	       }
	       ArrayList<BasicBlock> path = new ArrayList<BasicBlock>();
	       path.addAll(getIntermediatePath(source, P[source.m_nodeId][target.m_nodeId]));
	       path.add(P[source.m_nodeId][target.m_nodeId]);
	       path.addAll(getIntermediatePath(P[source.m_nodeId][target.m_nodeId], target));
	       return path;
	   }
	   
	   private int[][] initializeWeight(BasicBlock[] nodes, Edge[] edges){
	       int[][] Weight = new int[nodes.length][nodes.length];
	       for(int i=0; i<nodes.length; i++){
	           Arrays.fill(Weight[i], Integer.MAX_VALUE);
	       }
	       for(Edge e : edges){
	           Weight[e.from.m_nodeId][e.to.m_nodeId] = e.weight;
	       }
	       return Weight;
	   }
	}
