package com.xmos.xta.core.engine;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import com.xmos.libs.controlflow.nodes.CfgFunctionReferenceNode;
import com.xmos.libs.controlflow.nodes.CfgNode;
import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.Pair;
import com.xmos.xta.core.engine.analysis.routes.StackNode;

public class CfgUserBranchLookUp {

	
	private Map<CfgNode, List<Pair<StackNode, CfgNode>>> m_cfgNodeBranchMap;
	
	
	public CfgUserBranchLookUp(Map<CfgNode, List<Pair<StackNode, CfgNode>>> cfgNodeBranchMap){
		m_cfgNodeBranchMap = cfgNodeBranchMap;
	}
	
	public Set<CfgNode> getNextNodes(Stack<CfgFunctionReferenceNode> currentStack, CfgNode node){
		
		if(!m_cfgNodeBranchMap.containsKey(node)){
			return node.getNextNodes();
		}
		
		Set<CfgNode> allNexts = node.getNextNodes();
		
		Set<CfgNode> add = new IteratableSet<CfgNode>();
		
		for(CfgNode mapNode : m_cfgNodeBranchMap.keySet()){
			if(mapNode == node){
				List<Pair<StackNode, CfgNode>> pairs = m_cfgNodeBranchMap.get(mapNode);
				
				for(Pair<StackNode, CfgNode> pair : pairs){
					
					StackNode stack = pair.first;
					CfgNode nextNode = pair.second;
					
					if(stack.getNodeStacks().contains(currentStack)){
						add.add(nextNode);
					} else {
						allNexts.remove(nextNode);
					}
				}
			}
		}
		allNexts.addAll(add);
		
		return allNexts;
	}
	
}
