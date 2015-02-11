package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.xmos.libs.core.controlflow.nodes.CfgFunctionReferenceNode;
import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.utils.BackTrail;

public class BackTrails implements Comparable<BackTrails> {
    private List<BackTrail> m_backTrails;
    private CfgInstructionNode m_instruction;

    public BackTrails(CfgInstructionNode instruction, List<BackTrail> backTrails) {
        m_backTrails = backTrails;
        m_instruction = instruction;
    }

    public boolean contains(BackTrail backTrail) {
        return m_backTrails.contains(backTrail);
    }

    public List<BackTrail> getBackTrails() { // only need for iterating over. Move here?
        return m_backTrails;
    }

    public CfgInstructionNode getInstruction() {
        return m_instruction;
    }

    public String toString(){
        StringBuffer str = new StringBuffer();
        for(BackTrail backTrail : m_backTrails) {
            str.append("\t");
            str.append(backTrail);
            str.append("\n");
        }
        return m_instruction + " from " + m_backTrails.size() + " stacks\n" + str.toString();
    }

    public int compareTo(BackTrails o) {
        return this.hashCode() - o.hashCode();
    }

    public Collection<String> getAsStrings() {
        Collection<String> collection = new ArrayList<String>();
        for (BackTrail backTrail : m_backTrails) {
            String str = "";
            for (CfgFunctionReferenceNode node : backTrail) {
                long callPc = node.getFunctionCallPc();
                if (callPc != 0)
                    str += String.format("CALL:0x%x,", callPc);
            }
            str += String.format("0x%x", m_instruction.getPc());
            collection.add(str);
        }
        return collection;
    }
}
