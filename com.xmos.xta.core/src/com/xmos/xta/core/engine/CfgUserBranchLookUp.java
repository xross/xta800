package com.xmos.xta.core.engine;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.core.controlflow.nodes.CfgNode;
import com.xmos.libs.core.utils.BackTrail;
import com.xmos.libs.core.utils.IteratableSet;
import com.xmos.libs.core.utils.Pair;
import com.xmos.xta.core.engine.analysis.routes.BackTrails;

public class CfgUserBranchLookUp {


    private Map<CfgNode, List<Pair<BackTrails, CfgNode>>> m_cfgNodeBranchMap;


    public CfgUserBranchLookUp(Map<CfgNode, List<Pair<BackTrails, CfgNode>>> cfgNodeBranchMap){
        m_cfgNodeBranchMap = cfgNodeBranchMap;
    }

    public Set<CfgNode> getNextNodes(BackTrail currentTrail, CfgNode node){

        if(!m_cfgNodeBranchMap.containsKey(node)){
            return node.getNextNodes();
        }

        Set<CfgNode> allNexts = node.getNextNodes();

        Set<CfgNode> add = new IteratableSet<CfgNode>();

        for(CfgNode mapNode : m_cfgNodeBranchMap.keySet()){
            if(mapNode == node){
                List<Pair<BackTrails, CfgNode>> pairs = m_cfgNodeBranchMap.get(mapNode);

                for(Pair<BackTrails, CfgNode> pair : pairs){

                    BackTrails backTrails = pair.first;
                    CfgNode nextNode = pair.second;

                    if(backTrails.contains(currentTrail))
                        add.add(nextNode);
                    else
                        allNexts.remove(nextNode);
                }
            }
        }
        allNexts.addAll(add);

        return allNexts;
    }

}
