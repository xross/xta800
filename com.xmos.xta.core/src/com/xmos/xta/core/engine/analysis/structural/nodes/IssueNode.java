package com.xmos.xta.core.engine.analysis.structural.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.libs.core.instructiondecoder.Mnemonics;
import com.xmos.libs.core.instructiondecoder.NullInstruction;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.IssueNodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.NodeAttributes;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.NodeTraverser;
import com.xmos.xta.core.engine.analysis.structural.nodes.traversers.TraverseStatus;

public class IssueNode extends Node {
    private LinkedList<Instruction> m_insts;
    private IssueNodeAttributes m_attributes;
    private boolean m_ownsOwnAttrib;
    // BranchNotTakenId fields are used by InstructionBuffer to add FNOPs.
    private int m_nextBranchNotTakenId;
    private int m_branchNotTakenId;

    public IssueNode(CfgInstructionNode node) {
        super(node);
        m_insts = new LinkedList<Instruction>();
        m_insts.add(node.getInstruction());
        m_attributes = new IssueNodeAttributes(this);
        m_ownsOwnAttrib = true;
        m_nextBranchNotTakenId = (node.getNextBranchNotTakenNode() == null)? 0 : node.getNextBranchNotTakenNode().getId();
        m_branchNotTakenId = (node.getPrevBranchNotTakenNode() == null)? 0 : node.getId();  // Set to zero if we don't have a previous.
    }

    private IssueNode(IssueNode node) {
        super(node);
        m_insts = new LinkedList<Instruction>();
        for (Instruction instruction : node.m_insts)
            m_insts.add(instruction);
        m_attributes = node.m_attributes;
        m_ownsOwnAttrib = false;
        m_nextBranchNotTakenId = node.m_nextBranchNotTakenId;
        m_branchNotTakenId = node.m_branchNotTakenId;
    }

    public IssueNode createFnopNode() {
        NullInstruction instr = new NullInstruction(m_insts.getFirst().getArch(), "--FNOP--", 0, getStartPc(), 0, true);
        IssueNode fnop = new IssueNode(new CfgInstructionNode(instr));
        fnop.m_attributes.setCoreConfig(m_attributes);   // Copy the core config.
        return fnop;
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

    public boolean isWait() {
        for (Instruction instruction : m_insts)
            if (instruction.isWait())
                return true;
        return false;
    }

    public int getNextBranchNotTakenId() {
        return m_nextBranchNotTakenId;
    }
    public int getBranchNotTakenId() {
        return m_branchNotTakenId;
    }

    public long getBranchPc() {
        // We assume there is only one branch instruction per IssueNode!
        for (Instruction instruction : m_insts)
            if (instruction.isBranch())
                return instruction.getPcRelativeAddr();
        assert(false); // isBranch() == false, should not have called this function.
        return 0;
    }

    public boolean isFnop() {
        if (m_insts.getFirst().isA(Mnemonics.FNOP)) {
            assert(m_insts.size() == 1);
            return true;
        }
        return false;
    }

    public boolean isIllegal() {
        for (Instruction instruction : m_insts)
            if (instruction.isA(Mnemonics.UNKNOWN))
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
        return !isFnop() && Pcs.contains(getStartPc());
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
                    instruction.mnemonicString(),
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
            str += instruction.mnemonicString();
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
