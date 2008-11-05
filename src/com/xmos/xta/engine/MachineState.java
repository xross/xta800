package com.xmos.xta.engine;

import com.xmos.xta.engine.resources.Resources;

class MachineState {

    public static enum BranchState {
        TAKEN, NOTTAKEN, UNKNOWN
    }

    public final int CP_REG_NUM = 12;
    public final int DP_REG_NUM = 13;
    public final int SP_REG_NUM = 14;
    public final int LR_REG_NUM = 15;

    int m_opcode;

    int m_op1Fld;
    int m_op2Fld;
    int m_op3Fld;
    int m_op4Fld;
    int m_op5Fld;
    int m_op6Fld;

    Expression m_op1;
    Expression m_op2;
    Expression m_op3;
    Expression m_op4;
    Expression m_op5;
    Expression m_op6;
    
    private Expression m_branchTargetExpression;
    private Expression m_guardExpression;
    
    private boolean m_wasBranch;
    private boolean m_wasBranchResolved;
    private int m_branchTarget;
    private BranchState m_branchTaken;

    private boolean m_wasJump;
    private boolean m_wasJumpResolved;
    private int m_jumpTarget;
    private BranchState m_jumpTaken;

    private Resources m_resources;
    private Registers m_registers;
    private Memory m_memory;

    private InstructionBuffer m_iBuffer;

    MachineState(int memoryBase, int memorySize) {
        m_registers = new Registers();
        m_memory = new Memory();
        m_iBuffer = new InstructionBuffer();
        m_resources = new Resources();
        
        // Initialises the stack pointer to a reasonable
        // default location (i.e. the top of memory..)
        setStackPointerRegister(new Expression(memoryBase + memorySize));
    }
    
    MachineState(MachineState machineState) {
        m_opcode = machineState.m_opcode;
        m_op1Fld = machineState.m_op1Fld;
        m_op2Fld = machineState.m_op2Fld;
        m_op3Fld = machineState.m_op3Fld;
        m_op4Fld = machineState.m_op4Fld;
        m_op5Fld = machineState.m_op5Fld;
        m_op6Fld = machineState.m_op6Fld;

        m_op1 = machineState.m_op1;
        m_op2 = machineState.m_op2;
        m_op3 = machineState.m_op3;
        m_op4 = machineState.m_op4;
        m_op5 = machineState.m_op5;
        m_op6 = machineState.m_op6;

        m_wasBranch = machineState.m_wasBranch;
        m_wasBranchResolved = machineState.m_wasBranchResolved;
        m_branchTarget = machineState.m_branchTarget;

        m_wasJump = machineState.m_wasJump;
        m_wasJumpResolved = machineState.m_wasJumpResolved;
        m_jumpTarget = machineState.m_jumpTarget;

        //m_resources = new Resources(machineState.m_resources);
        m_registers = new Registers(machineState.m_registers);
        m_memory = new Memory(machineState.m_memory);
        m_iBuffer = new InstructionBuffer(machineState.m_iBuffer);
    }

    Resources getResources(){
        return m_resources;
    }
    
    void setPc(int pc) {
        m_iBuffer.setPc(pc);
    }

    int getPc() {
        return m_iBuffer.getPc();
    }

    void setOpcode(int opcode) {
        m_opcode = opcode;
    }

    int getOpcode() {
        return m_opcode;
    }

    boolean wasBranch() {
        return m_wasBranch;
    }

    boolean wasBranchResolved() {
        return m_wasBranchResolved;
    }

    int getBranchTarget() {
        return m_branchTarget;
    }

    boolean wasJump() {
        return m_wasJump;
    }

    boolean wasJumpResolved() {
        return m_wasJumpResolved;
    }

    int getJumpTarget() {
        return m_jumpTarget;
    }

    BranchState getJumpTaken() {
        return m_jumpTaken;
    }

    BranchState getBranchTaken() {
        return m_branchTaken;
    }

    void branch(boolean b, Expression guard, Expression branchTarget) {
        m_branchTargetExpression = branchTarget;
        branchTarget.resolve();

        m_wasBranch = true;
        m_wasBranchResolved = branchTarget.isResolved();
        if (m_wasBranchResolved) {
            guard.resolve();
            m_guardExpression = guard;
            m_branchTarget = branchTarget.getValue();
            if (guard.isResolved()) {
                if (guard.getValue() == 0) {
                    m_branchTaken = b ? BranchState.NOTTAKEN : BranchState.TAKEN;
                } else {
                    m_branchTaken = b ? BranchState.TAKEN : BranchState.NOTTAKEN;
                }
            } else {
                m_branchTaken = BranchState.UNKNOWN;
            }
        } else {
            m_branchTaken = BranchState.UNKNOWN;
        }

    }

    void jump(Expression jumpTarget) {
        jumpTarget.resolve();
        m_wasJump = true;
        m_wasJumpResolved = jumpTarget.isResolved();
        if (m_wasJumpResolved) {
            m_jumpTarget = jumpTarget.getValue();
            m_jumpTaken = BranchState.TAKEN;
        } else {
            m_jumpTaken = BranchState.UNKNOWN;
        }

    }

    boolean canIssue(int numBytes) {
        return m_iBuffer.canIssue(numBytes);
    }

    void fetch() {
        m_iBuffer.fetch();
    }

    void issue(int numBytes) {
        m_iBuffer.issue(numBytes);
    }

    void branchTo(int branchTarget, boolean isLoadStore) {
        m_iBuffer.branchTo(branchTarget, isLoadStore);
    }

    int getNumIBufferBytes() {
        return m_iBuffer.getNumBytes();
    }

    Expression getConstPoolRegister() {
        return m_registers.readRegister(CP_REG_NUM);
    }

    void setConstPoolRegister(Expression value) {
        writeRegister(CP_REG_NUM, value);
    }

    Expression getDataPoolRegister() {
        return m_registers.readRegister(DP_REG_NUM);
    }

    void setDataPoolRegister(Expression value) {
        writeRegister(DP_REG_NUM, value);
    }

    Expression getStackPointerRegister() {
        return m_registers.readRegister(SP_REG_NUM);
    }

    void setStackPointerRegister(Expression value) {
        writeRegister(SP_REG_NUM, value);
    }

    Expression getLinkRegister() {
        return m_registers.readRegister(LR_REG_NUM);
    }

    void setLinkRegister(Expression value) {
        writeRegister(LR_REG_NUM, value);
    }

    Expression readRegister(int regNum) {
        return m_registers.readRegister(regNum);
    }

    void writeRegister(int regNum, Expression regValue) {
        m_registers.writeRegister(regNum, regValue);
    }

    Expression readMemory(int address) {
        return m_memory.read(address);
    }

    void writeMemory(int address, Expression value) {
        m_memory.write(address, value);
    }

    void resetInstructionState() {
        m_wasBranch = false;
        m_wasBranchResolved = false;
        m_branchTarget = 0;
        m_wasJump = false;
        m_wasJumpResolved = false;
        m_jumpTarget = 0;
    }

    void dumpState() {
        for (int regIndex = 0; regIndex < Registers.NUM_REGISTERS; regIndex += 2) {
            String exprString;

            exprString = m_registers.readRegister(regIndex + 0)
                    .expressionString();
            System.out.printf("R[%-2d] = %-40s", regIndex + 0, exprString);

            exprString = m_registers.readRegister(regIndex + 1)
                    .expressionString();
            System.out.printf("R[%-2d] = %-40s", regIndex + 1, exprString);

            System.out.printf("\n");
        }
    }

    public Expression getBranchTargetExpression() {
        return m_branchTargetExpression;
    }
    
    public Expression getGuardExpression() {
        return m_guardExpression;
    }
}
