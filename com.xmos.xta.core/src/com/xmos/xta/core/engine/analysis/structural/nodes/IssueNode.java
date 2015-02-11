package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.IssueNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;

public class IssueNode extends Node {
    private boolean m_ownsOwnAttrib;
    private IssueNodeAttributes m_attributes;
    private LinkedList<Instruction> m_insts;
    //private boolean m_isFnop;
    private int m_nextBranchNotTakenId;
    private int m_prevBranchNotTakenId;

    public IssueNode(CfgInstructionNode node) {
        super(node);
        m_insts = new LinkedList<Instruction>();
        m_insts.add(node.getInstruction());
        m_attributes = new IssueNodeAttributes(this);
        m_ownsOwnAttrib = true;
        m_nextBranchNotTakenId = (node.getNextBranchNotTakenNode() == null)? 0 : node.getNextBranchNotTakenNode().getId();
        m_prevBranchNotTakenId = (node.getPrevBranchNotTakenNode() == null)? 0 : node.getPrevBranchNotTakenNode().getId();
    }

    private IssueNode(IssueNode node) {
        super(node);
        m_insts = new LinkedList<Instruction>();
        for (Instruction instruction : node.m_insts)
            m_insts.add(instruction);
        m_attributes = node.m_attributes;
        m_ownsOwnAttrib = false;
        m_nextBranchNotTakenId = node.m_nextBranchNotTakenId;
        m_prevBranchNotTakenId = node.m_prevBranchNotTakenId;
    }

    public int getNumBytes() {
        int numBytes = 0;
        for (Instruction instruction : m_insts)
            numBytes += instruction.getNumBytes();
        return numBytes;
    }

    public boolean hasLoadStore() {
        for (Instruction instruction : m_insts)
            if (instruction.isLoadStore()) return true;
        return false;
    }

    public boolean isBranch() {
        for (Instruction instruction : m_insts)
            if (instruction.isBranch()) return true;
        return false;
    }

    public int getNextBranchNotTakenId() {
        return m_nextBranchNotTakenId;
    }
    public int getPrevBranchNotTakenId() {
        return m_prevBranchNotTakenId;
    }

    public long getBranchPc() {
        // We assume there is only one branch instruction per IssueNode!
        for (Instruction instruction : m_insts)
            if (instruction.isBranch())
                return instruction.getBranchPcRelativeTargetPc();
        assert(false); // isBranch() == false.
        return 0;
    }

// First need to refactor FnopAdder.update().
// Need to remove isFNop from NullInstruction and set fnop later here instead!
//    public void setIsFnop(boolean isFnop) {
//        m_isFnop = isFnop;
//    }

    public boolean isFnop() {
        //return m_isFnop;
        for (Instruction instruction : m_insts)
            if (instruction.isFnop())
                return true;
        return false;
    }

    public boolean isIllegal() {
        for (Instruction instruction : m_insts)
            if (instruction.getIsIllegal())
                return true;
        return false;
    }


    public Node copyRouteStructure() {
        IssueNode in = new IssueNode(this);
        in.copyBase(this);
        in.m_attributes = m_attributes;
        return in;
    }

    public String getTypeName() {
        return "instruction";   // This may be an VLIW instruction!
    }

    public NodeAttributes getAttributes(boolean modifier) {
        if(modifier && !m_ownsOwnAttrib){
            m_ownsOwnAttrib = true;
            m_attributes = new IssueNodeAttributes(this);
        }
        return m_attributes;
    }

    public LinkedList<Instruction> getInstructions() {
        return m_insts;
    }

    public boolean containsPc(Set<Long> Pcs) {
        if (!isFnop())
            for (Instruction instruction : m_insts)
                if (Pcs.contains(instruction.getPc()))
                    return true;
        return false;
    }

    public TraverseStatus traverse(NodeTraverser nodeTraverser, Object clientState) {
        TraverseStatus status = nodeTraverser.foundIssueNode(this, clientState);
        if (status != TraverseStatus.ABORT)
            status = nodeTraverser.foundIssueNodeExit(this, clientState);
        return (status == TraverseStatus.ABORT)? TraverseStatus.ABORT : TraverseStatus.CONTINUE;
    }

    public boolean isIssueNode() {
        return true;
    }

    public String toString(){
        return getDescription();
    }

    public String getDescription() {
        return String.format("0x%05x %-22s %s",
                getStartPc(),
                functionOffsetString(),
                getMnemonicOperandString());
    }

    private String functionOffsetString() {
        String functionName = getFunctionName();
        return functionName != null? functionName + " + " + m_insts.getFirst().getFunctionOffset() : "";
    }

    private String getMnemonicOperandString() {
        String str = "";
        boolean multiIssue = m_insts.size() > 1;
        boolean addSeperator = false;
        if (multiIssue) str += "{ ";
        for (Instruction instruction : m_insts) {
            if (addSeperator) str += " ; ";
            addSeperator = true;
            str += String.format("%-12s %-12s",
                    instruction.getMnemonic(),
                    instruction.getOperandString());
        }
        if (multiIssue) str += " }";
        return str;
     }

    public String getMnemonic() {
        String str = "";
        boolean multiIssue = m_insts.size() > 1;
        boolean addSeperator = false;
        if (multiIssue) str += "{ ";
        for (Instruction instruction : m_insts) {
            if (addSeperator) str += " ; ";
            addSeperator = true;
            str += instruction.getMnemonic();
        }
        if (multiIssue) str += " }";
        return str;
    }

    public boolean canPause() {
        for (Instruction instruction : m_insts)
            if (instruction.canPause())
                return true;
        return false;
    }
    
    public TimeDistributionBuckets getTimeSpread() {
        return new TimeDistributionBuckets(m_attributes.getFixedTime().getCycles());
    }

    public List<Node> iChildren() {
        List<Node> children = new LinkedList<Node>();
        return children;
    }

    public void buildListDown(List<Node> nodeList) {
        nodeList.add(this);
    }

    public void buildListUp(List<Node> nodeList, Node start) {
        assert(start == null);
        if(getParent() != null){
            getParent().buildListUp(nodeList, this);
        }
        nodeList.add(this);
    }

    public String getFunctionName() {
        return m_insts.getFirst().getFunctionName();
    }

    public long getStartPc() {
        return m_insts.getFirst().getPc();
    }

}
