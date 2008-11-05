package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.MachineState.BranchState;

public class Block implements Comparable<Block> {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_STATUS = false;
    private static final boolean DEBUG_SANITY = false;
    private boolean m_resolveExpressions;

    private static int m_numCompletedPaths = 0;
    private static int m_numUnresolvedPaths = 0;
    private static int m_numLoopPaths = 0;

    enum Mode {
        FUNCTION, ABSOLUTE,
    }

    public enum Status {
        SEARCHING, COMPLETE, UNRESOLVED, LOOPED, RECURSION, ERROR, EXCLUDED, MAX_NUM_PATHS
    }

    private Config m_config;

    private int m_id;
    private int m_instanceId;
    private Mode m_mode;
    private MachineState m_machineState;
    private XtaInstruction m_instruction;

    private Block m_parentBlock;
    private Route m_route;
    private Vector<XtaInstruction> m_instructions;

    private HashMap<Block, PathAttributes> m_loopPathAttributes;
    private Vector<Block> m_loopPaths;

    private InstructionDecodeCache m_instructionDecodeCache;
    private InstructionAttributes m_instructionAttributes;

    private MemoryImage m_memoryImage;
    private SymbolTable m_symbolTable;

    private int m_fromPc;
    private int m_toPc;
    private Status m_status;
    private boolean m_firstStep;
    private int m_functionDepth;

    private int m_seen;
    private Stack<TreeSet<Integer>> m_contexts;
    private Vector<Reference> m_exclusions;

    /*
    private HashMap<String, TreeSet<Integer>> m_sourceLinesCovered;
    private HashMap<String, TreeSet<Integer>> m_asmLinesCovered;
    */

    private static int m_numPaths;
    private static int m_numPathInstances;

    private int m_numFNops;
    private int m_numInstructions;
    private int m_numPausables;

    private ArrayList<Unknown> m_unknowns;
    private HashSet<Integer> m_pcs;
    private boolean m_speculative;

    static int getNumPaths() {
        return m_numPaths;
    }

    static int getNumPathInstances() {
        return m_numPathInstances;
    }

    static void resetNumPaths() {
        m_numPaths = 0;
        m_numPathInstances = 0;
    }

    Block(Mode mode, Config config,
            InstructionDecodeCache instructionDecodeCache,
            InstructionAttributes instructionAttributes,
            MemoryImage memoryImage, SymbolTable symbolTable,
            Vector<Reference> exclusions, int fromPc, int toPc, Route route,
            Block parentBlock, MachineState machineState,
            Stack<TreeSet<Integer>> contexts, boolean speculative, boolean resolveExpressions) {
        m_machineState = new MachineState(machineState);
        m_contexts = new Stack<TreeSet<Integer>>();
        m_contexts.addAll(contexts);
        m_seen = 0;

        m_config = config;
        m_memoryImage = memoryImage;
        m_symbolTable = symbolTable;
        m_instructions = new Vector<XtaInstruction>();
        m_instructionDecodeCache = instructionDecodeCache;
        m_instructionAttributes = instructionAttributes;
        m_id = m_numPaths;
        m_instanceId = m_id;
        ++m_numPathInstances;
        ++m_numPaths;

        m_mode = mode;

        m_fromPc = fromPc;
        m_toPc = toPc;
        m_status = Status.SEARCHING;
        m_firstStep = true;
        m_functionDepth = m_contexts.size() - 1;
        m_exclusions = exclusions;
        m_resolveExpressions = resolveExpressions;
        m_machineState.setPc(m_fromPc);

        m_numInstructions = 0;
        m_numFNops = 0;
        m_numPausables = 0;

        m_route = route;
        m_parentBlock = parentBlock;
        m_loopPaths = new Vector<Block>();
        m_loopPathAttributes = new HashMap<Block, PathAttributes>();

        m_route.addBlock(this);

        m_unknowns = new ArrayList<Unknown>();
        m_pcs = new HashSet<Integer>();

        m_speculative = speculative;

        if (DEBUG) {
            if (m_parentBlock != null) {
                System.out.printf("New block %d with parent %d\n",
                        m_instanceId, m_parentBlock.m_instanceId);
            } else {
                System.out.printf("New root block %d\n", m_instanceId);
            }
        }
    }

    Block(Block blockToSplit, int pcHit) {
        if (DEBUG) {
            System.out.printf("Splitting block %d at 0x%x\n",
                    blockToSplit.m_instanceId, pcHit);
        }

        m_machineState = null;
        m_contexts = null;
        m_seen = 0;

        m_config = blockToSplit.m_config;
        m_memoryImage = blockToSplit.m_memoryImage;
        m_symbolTable = blockToSplit.m_symbolTable;

        m_instructions = new Vector<XtaInstruction>();
        m_unknowns = new ArrayList<Unknown>();
        m_pcs = new HashSet<Integer>();

        int instructionCount;
        if (DEBUG_SANITY) {
            instructionCount = blockToSplit.m_instructions.size();
        }
        for (XtaInstruction inst : blockToSplit.m_instructions) {
            if (inst.getPc() == pcHit)
                break;

            for (Unknown unknown : blockToSplit.m_unknowns) {
                if (unknown.getPc() == inst.getPc()) {
                    m_unknowns.add(unknown);
                    break;
                }
            }
            m_pcs.add(inst.getPc());
            m_instructions.add(inst);
        }

        for (Unknown unknown : m_unknowns) {
            blockToSplit.m_unknowns.remove(unknown);
        }
        for (XtaInstruction inst : m_instructions) {
            blockToSplit.m_pcs.remove(new Integer(inst.getPc()));
            blockToSplit.m_instructions.remove(inst);
        }
        if (DEBUG_SANITY) {
            assert (m_instructions.size() + blockToSplit.m_instructions.size() == instructionCount);
            assert (m_instructions.size() > 0);
            assert (blockToSplit.m_instructions.size() > 0);
        }

        updateInstructionInfo();
        blockToSplit.updateInstructionInfo();

        m_instructionDecodeCache = blockToSplit.m_instructionDecodeCache;
        m_instructionAttributes = blockToSplit.m_instructionAttributes;

        m_resolveExpressions = blockToSplit.m_resolveExpressions;
        
        m_id = blockToSplit.m_id;
        m_instanceId = blockToSplit.m_instanceId;
        blockToSplit.m_id = m_numPaths;
        blockToSplit.m_instanceId = m_numPathInstances;
        ++m_numPathInstances;
        ++m_numPaths;

        m_mode = blockToSplit.m_mode;

        m_status = blockToSplit.m_status;
        m_firstStep = false;
        m_functionDepth = blockToSplit.m_functionDepth;
        m_exclusions = blockToSplit.m_exclusions;

        m_route = blockToSplit.m_route;

        m_loopPaths = blockToSplit.m_loopPaths;
        m_loopPathAttributes = blockToSplit.m_loopPathAttributes;
        m_parentBlock = blockToSplit.m_parentBlock;

        blockToSplit.m_loopPaths = new Vector<Block>();
        blockToSplit.m_loopPathAttributes = new HashMap<Block, PathAttributes>();
        blockToSplit.m_parentBlock = this;

        m_route.addBlock(this);

        m_speculative = blockToSplit.m_speculative;

        if (DEBUG) {
            if (m_parentBlock != null) {
                System.out.printf("New block %d with parent %d\n",
                        m_instanceId, m_parentBlock.m_instanceId);
            } else {
                System.out.printf("New root block %d\n", m_instanceId);
            }
            System.out.println("block " + blockToSplit.m_id + " is now "
                    + Integer.toHexString(blockToSplit.m_fromPc) + " "
                    + Integer.toHexString(blockToSplit.m_toPc));
            System.out.println("block " + this.m_id + " is now "
                    + Integer.toHexString(this.m_fromPc) + " "
                    + Integer.toHexString(this.m_toPc));
        }
    }

    private void updateInstructionInfo() {
        m_fromPc = m_instructions.firstElement().getPc();
        m_toPc = m_instructions.lastElement().getPc();
        m_numInstructions = m_instructions.size();
        m_numFNops = 0;
        m_numPausables = 0;
        for (XtaInstruction inst : m_instructions) {
            if (inst.isFnop())
                m_numFNops++;
            if (inst.canPause())
                m_numPausables++;
        }
    }

    public int getFirstInstructionPc() {
        return m_instructions.firstElement().getPc();
    }

    public int getLastInstructionPc() {
        return m_instructions.lastElement().getPc();
    }

    public Block getParentBlock() {
        return m_parentBlock;
    }

    public String getDescription() {
        String desc = "Block (" + this.getId() + "): "
                + this.getTimeTakenThis() + "ns";
        return desc;
    }

    public String getPathDescription(Block caller) {
        String desc = "Block (" + this.getId() + "): "
                + this.getTimeTakenThis() + "ns";
        return desc;
    }

    public int compareTo(Block block) throws ClassCastException {
        return (int) (block.getTimeTaken(true) - getTimeTaken(true));
    }

    public boolean equivalent(Block block) {
        Vector<XtaInstruction> thisI = getInstructions(false);
        Vector<XtaInstruction> thatI = block.getInstructions(false);

        if (thisI.size() != thatI.size())
            return false;

        for (int i = 0; i < thisI.size(); i++) {
            if (thisI.get(i) != thatI.get(i))
                return false;
        }

        return true;
    }

    public int getId() {
        return m_id;
    }

    public void setId(int id) {
        m_id = id;
    }

    public int getInstanceId() {
        return m_instanceId;
    }

    public int numLoopPaths() {
        if (m_seen == 0) {
            return m_loopPaths.size();
        } else {
            return 0;
        }
    }

    public Vector<Block> getLoopPaths() {
        m_seen++;
        if (m_seen == 1) {
            return m_loopPaths;
        } else {
            return new Vector<Block>();
        }
    }

    public void doneWithLoopPaths() {
        m_seen--;
        assert (m_seen >= 0);
    }

    public void incrementSeen() {
        m_seen++;
    }

    public void decrementSeen() {
        m_seen--;
        assert (m_seen >= 0);
    }

    public PathAttributes getLoopPathAttributes(Block block) {
        PathAttributes attributes = m_loopPathAttributes.get(block);
        assert (attributes != null);
        return attributes;
    }

    public Vector<XtaInstruction> getInstructionsThis() {
        return m_instructions;
    }

    public Vector<XtaInstruction> getInstructions(boolean recurse) {
        Vector<XtaInstruction> insts = new Vector<XtaInstruction>();

        for (Block block : getPathBlocks()) {
            insts.addAll(block.getInstructionsThis());
            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {
                    insts.addAll(loopPath.getInstructions(true));
                }
                block.doneWithLoopPaths();
            }
        }
        return insts;
    }

    /*
     * Trace back from a starting block to the point the path ends.
     */
    public ArrayList<Block> getPathBlocks() {
        ArrayList<Block> paths = new ArrayList<Block>();
        Block block = this;

        do {
            paths.add(0, block);
            if (block.m_loopPaths.contains(this)) {
                break;
            }
            block = block.m_parentBlock;
        } while (block != null);

        return paths;
    }

    public void getAllBlocks(TreeSet<Block> allBlocks) {
        for (Block block : getPathBlocks()) {
            allBlocks.add(block);
            for (Block loopPath : block.getLoopPaths()) {
                loopPath.getAllBlocks(allBlocks);
            }
            block.doneWithLoopPaths();
        }
    }

    public int getNumIterations() {
        Block parent = this;
        while (parent != null) {
            if (parent.m_loopPaths.contains(this)) {
                Attributes attributes = parent.getLoopPathAttributes(this);
                return attributes.getNumIterations();
            }
            parent = parent.m_parentBlock;
        }
        return 1;
    }

    public double getTimeTaken(boolean recurse) {
        return getTimeTakenRelative(recurse) + getTimeTakenAbsolute(recurse);
    }

    public int getNumInstructions(boolean recurse) {
        return getNumInstructionsRelative(recurse)
                + getNumInstructionsAbsolute(recurse);
    }

    public int getNumFNops(boolean recurse) {
        return getNumFNopsRelative(recurse) + getNumFNopsAbsolute(recurse);
    }

    public int getNumCycles(boolean recurse) {
        return getNumCyclesRelative(recurse) + getNumCyclesAbsolute(recurse);
    }

    public int getNumPausables(boolean recurse) {
        return getNumPausablesRelative(recurse)
                + getNumPausablesAbsolute(recurse);
    }

    public int getLength() {
        int pathLength = 0;
        for (Block block : getPathBlocks()) {
            pathLength += block.m_instructions.size();
        }
        return pathLength;
    }

    public int fromPc() {
        return m_fromPc;
    }

    public int toPc() {
        return m_toPc;
    }

    public boolean addLoopPath(Block pathStartBlock) {
        assert (pathStartBlock != null);
        assert (!m_loopPaths.contains(pathStartBlock));
        if (m_loopPaths.contains(pathStartBlock))
            return false;

        if (DEBUG) {
            System.out.printf("Block %d adding %d as loop\n", m_instanceId,
                    pathStartBlock.m_instanceId);
        }

        m_loopPaths.add(pathStartBlock);
        m_loopPathAttributes.put(pathStartBlock, new PathAttributes());
        return true;
    }

    void step() {
        int pc = m_machineState.getPc();

        int opcode = 0;
        try {
            opcode = m_memoryImage.readInstructionWord(pc);
        } catch (Exception e) {
            setStatus(Status.ERROR);
            return;
        }

        m_machineState.setOpcode(opcode);
        m_machineState.resetInstructionState();

        m_instruction = m_instructionDecodeCache.decode(opcode, pc);
        int instNumBytes = m_instruction.getNumBytes();

        // Add an FNOP if required
        if (m_machineState.canIssue(instNumBytes) == false) {
            XtaInstruction fNop = m_instructionDecodeCache.getFNop(pc, 0);
            addInstruction(fNop);
            m_machineState.fetch();
            fNop.setNumIBufferBytes(m_machineState.getNumIBufferBytes());

            // May require two FNOPs if fetching a mis-aligned long
            if (m_machineState.canIssue(instNumBytes) == false) {
                fNop = m_instructionDecodeCache.getFNop(pc, 1);
                addInstruction(fNop);
                m_machineState.fetch();
                fNop.setNumIBufferBytes(m_machineState.getNumIBufferBytes());
            }
        }

        m_instruction.execute(m_machineState);
        addInstruction(m_instruction);

        if (m_instruction.canPause()) {
            addUnknown(m_instruction);
        }

        if (DEBUG) traceInstruction(this);
        /*
         * after an instruction is added then one of options can occur: 1. Block
         * is completed - add to the routes paths and stop stepping 2. the PC is
         * unknown - stop stepping and add unresolved path 3. recursion is
         * detected - stop stepping and add unresolved path 4. a branch has been
         * hit - follow the branch then don't
         */

        if (isComplete(m_instruction, pc)) {
            setStatus(Status.COMPLETE);
            m_route.addCompletePath(this);
            releaseMemory();
            return;
        }

        if (isExcluded(pc)) {
            setStatus(Status.EXCLUDED);
            releaseMemory();
            return;
        }
        
        m_contexts.peek().add(m_instruction.getPc());
        
        if (m_machineState.wasJump()) {
            int jumpTarget = m_machineState.getJumpTarget();

            if (m_machineState.wasJumpResolved()) {
                if (m_instruction.getBranchLink() && isRecursion(jumpTarget)) {
                    setStatus(Status.RECURSION);
                    m_route.addUnresolvedPath(this);

                } else {
                    m_contexts.peek().add(m_instruction.getPc());

                    if (m_instruction.getBranchLink()) {
                        pushFunction(m_instruction.getPc());
                    } else if (m_instruction.getReturn()) {
                        popFunction();
                    }

                    m_machineState.branchTo(jumpTarget, m_instruction
                            .isLoadStore());
                }

            } else {
                if(m_instruction.getNmenonic().startsWith("RETSP")){
                    for(int callingpc : m_instruction.getReturnLocations()){
                        boolean singleCallingTarget  = m_instruction.getReturnLocations().size() == 1;
                        int op=0;
                        try {
                             op = m_memoryImage.readInstructionWord(callingpc);
                        } catch (Exception e) {
                            assert(false);
                        }
                        int callingPcBytes = m_instructionDecodeCache.decode(op, callingpc).getNumBytes();
                        takeBranch(callingpc+callingPcBytes, !singleCallingTarget);
                    }
                    setStatus(Status.COMPLETE);
                    releaseMemory();
                    return;
                } else {
                    setStatus(Status.UNRESOLVED);
                    m_route.addUnresolvedPath(this);
                    releaseMemory();
                    return;
                }
            }

        } else if (m_machineState.wasBranch()) {

            if (m_machineState.wasBranchResolved()) {
                if (m_resolveExpressions) {
                    if (m_machineState.getBranchTaken() == BranchState.TAKEN) {
                        takeBranch(m_machineState.getBranchTarget(), false);
                        setStatus(Status.COMPLETE);
                    } else if (m_machineState.getBranchTaken() == BranchState.NOTTAKEN) {
                        takeDontBranch(m_instruction, false);
                        setStatus(Status.COMPLETE);
                    } else if (m_machineState.getBranchTaken() == BranchState.UNKNOWN) {
                        
                        addUnknown(m_instruction);
                        takeBranch(m_machineState.getBranchTarget(), true);
                        takeDontBranch(m_instruction, true);
                    }
                    releaseMemory();
                    return;
                } else {
                    addUnknown(m_instruction);
                    takeBranch(m_machineState.getBranchTarget(), true);
                    takeDontBranch(m_instruction, true);
                    releaseMemory();
                    return;
                }

            } else {
                setStatus(Status.UNRESOLVED);
                m_route.addUnresolvedPath(this);
                releaseMemory();
                return;
            }
        } else {
            int nextPc = m_machineState.getPc() + m_instruction.getNumBytes();
            Block loopBlock = formsLoop(nextPc);
            // if it does then split the parent
            if (loopBlock != null) {
                if (loopBlock.getInstructionsThis().firstElement().getPc() != nextPc) {
                    new Block(loopBlock, nextPc);
                }
                loopBlock.addLoopPath(this);
                setStatus(Status.LOOPED);
                releaseMemory();
                return;
            }
            if (!m_instruction.isLoadStore()) {
                m_machineState.fetch();
            }
            updatePc(m_instruction);
            m_machineState.issue(instNumBytes);
        }

        m_firstStep = false;
        m_instruction.setNumIBufferBytes(m_machineState.getNumIBufferBytes());
    }

    private void releaseMemory() {
        m_machineState = null;
    }

    private void pushFunction(int startPc) {
        if (DEBUG) {
            System.out.printf("Block: %d: pushFunction: m_functionDepth: %d\n",
                    m_id, m_functionDepth);
            dumpContexts();
        }

        m_contexts.push(new TreeSet<Integer>());
        ++m_functionDepth;
    }

    private void popFunction() {
        if (DEBUG) {
            System.out.printf("Block: %d: popFunction: m_functionDepth: %d\n",
                    m_id, m_functionDepth);
            dumpContexts();
        }

        m_contexts.pop();
        --m_functionDepth;
    }
    
    private void addUnknown(XtaInstruction inst) {
        Unknown unknown;
        if(inst.canPause() ){
            unknown = new Unknown(inst);
        } else {
            unknown = new Unknown(inst, 
                m_machineState.getGuardExpression(),
                m_machineState.getBranchTargetExpression());
        }
        m_unknowns.add(unknown);
        m_route.addUnknown(unknown);
    }

    private void takeDontBranch(XtaInstruction instruction, boolean speculative) {
        if (m_instruction.isLoadStore() == false) {
            m_machineState.fetch();
        }
        int startPc = m_machineState.getPc() + instruction.getNumBytes();
        m_machineState.issue(instruction.getNumBytes());
        Block nextPath = new Block(m_mode, m_config, m_instructionDecodeCache,
                m_instructionAttributes, m_memoryImage, m_symbolTable,
                m_exclusions, startPc, m_toPc, m_route, this, m_machineState,
                m_contexts, speculative,m_resolveExpressions);
        nextPath.build();
    }

    private void takeBranch(int branchTarget, boolean speculative) {
        Block loopBlock = formsLoop(branchTarget);
        if (loopBlock != null) {
            if (loopBlock.getInstructionsThis().firstElement().getPc() != branchTarget) {
                new Block(loopBlock, branchTarget);
            }
            loopBlock.addLoopPath(this);
            setStatus(Status.LOOPED);
        } else {
            Block nextPath = new Block(m_mode, m_config,
                    m_instructionDecodeCache, m_instructionAttributes,
                    m_memoryImage, m_symbolTable, m_exclusions, branchTarget,
                    m_toPc, m_route, this, m_machineState, m_contexts,
                    speculative, m_resolveExpressions);
            nextPath.m_machineState.branchTo(branchTarget, m_instruction
                    .isLoadStore());
            nextPath.build();
            setStatus(Status.COMPLETE);
        }
    }

    private Block formsLoop(int pc) {
        if (m_resolveExpressions) {
            Block parent = this;
            while (parent != null) {
                if (parent.containsPc(pc) && isLoop(pc) && parent.m_speculative) {
                    return parent;
                }
                parent = parent.getParentBlock();
            }
            return null;
        } else {
            Block parent = this;
            boolean isLoop = isLoop(pc);
            if(!isLoop) return null;
            while (parent != null) {
                if (parent.containsPc(pc)) return parent;
                parent = parent.getParentBlock();
            }
            return null;
        }
    }

    private boolean isLoop(int pc) {
        if (m_contexts.peek().contains(pc)) {
            return true;
        }
        return false;
    }

    public boolean containsPc(int pc) {
        if (m_pcs.contains(pc))
            return true;
        return false;
    }

    private void build() {
        while (getStatus() == Block.Status.SEARCHING) {
            if (m_route.checkAbort()) {
                break;
            }
            if (Block.getNumPaths() >= m_config.m_maxNumPaths) {
                m_status = Status.MAX_NUM_PATHS;
                break;
            }
            step();
            if (getStatus() != Block.Status.SEARCHING) {
                m_status = getStatus();
                break;
            }
            m_route.setCurrentValue(Block.getNumPaths());
        }
    }

    void updatePc(XtaInstruction instruction) {
        int nextPc = 0;
        if (m_machineState.wasJump()) {
            if (m_machineState.wasJumpResolved()) {
                nextPc = m_machineState.getJumpTarget();
            } else {
                setStatus(Status.UNRESOLVED);
                m_route.addUnresolvedPath(this);
                return;
            }

        } else {
            nextPc = m_machineState.getPc() + instruction.getNumBytes();
        }
        assert (nextPc != 0);
        m_machineState.setPc(nextPc);
    }

    int getPc() {
        return m_machineState.getPc();
    }

    void setPc(int pc) {
        m_machineState.setPc(pc);
    }

    XtaInstruction getCurrentInstruction() {
        return m_instruction;
    }

    public Status getStatus() {
        return m_status;
    }

    void setStatus(Status status) {
        m_status = status;

        if (DEBUG_STATUS) {
            if (status == Status.COMPLETE)
                ++m_numCompletedPaths;
            else if (status == Status.UNRESOLVED)
                ++m_numUnresolvedPaths;
            else if (status == Status.LOOPED)
                ++m_numLoopPaths;

            System.out.printf("%d - %d - %d\n", m_numCompletedPaths,
                    m_numUnresolvedPaths, m_numLoopPaths);
        }
    }

    boolean wasFunctionCall() {
        return m_instruction.getBranchLink();
    }

    boolean wasBranch() {
        return m_machineState.wasBranch();
    }

    boolean wasBranchResolved() {
        return m_machineState.wasBranchResolved();
    }

    int getBranchTarget() {
        return m_machineState.getBranchTarget();
    }

    boolean wasJump() {
        return m_machineState.wasJump();
    }

    boolean wasJumpResolved() {
        return m_machineState.wasJumpResolved();
    }

    int getJumpTarget() {
        return m_machineState.getJumpTarget();
    }

    public int getUnknowns() {
        return m_unknowns.size();
    }

    public int getNumCyclesThis() {
        int numCycles = 0;
        for (XtaInstruction instruction : m_instructions) {
            numCycles += instruction.getNumCycles();
        }
        return numCycles;
    }

    public int getNumPausablesThis() {
        return m_numPausables;
    }

    private boolean isComplete(XtaInstruction instruction, int pc) {
        switch (m_mode) {
        case FUNCTION:
            return ((m_functionDepth == 0) && instruction.getReturn());
        case ABSOLUTE:
            boolean foundEnd = ((pc == m_toPc) && !m_firstStep);
            return foundEnd;
        default:
            assert false;
            return false;
        }
    }

    private boolean isExcluded(int pc) {
        for (Reference reference : m_exclusions) {
            assert (reference.isResolved());
            if (reference.getPc() == pc) {
                return true;
            }
        }
        return false;
    }

    private boolean isRecursion(int pc) {
        int lastIndex = m_contexts.size() - 1;
        for (int index = lastIndex; index >= 0; --index) {
            TreeSet<Integer> pcs = m_contexts.get(index);
            if (pcs.contains(pc)) {
                return true;
            }
        }
        return false;
    }

    private void addInstruction(XtaInstruction inst) {
        assert (m_instructions != null);
        assert (inst != null);

        if (DEBUG && false) {
            System.out.println("adding inst :" + inst.getNmenonic() + " "
                    + inst.getOperandString() + " to Block " + m_instanceId);
        }

        inst.setBasicNumCycles(m_instructionAttributes.getTiming(inst
                .getNmenonic()));
        inst.setConfig(m_config);

        m_pcs.add(inst.getPc());
        m_instructions.add(inst);
        if (inst.canPause()) {
            m_numPausables++;
        }
        if (inst.isFnop()) {
            m_numFNops++;
        } else {
            m_numInstructions++;
        }
    }
    
    public int getNumFNopsThis() {
        return m_numFNops;
    }
    

    public double getTimeTakenThis() {
        double time = 0.0;
        for (XtaInstruction instruction : m_instructions) {
            time += instruction.getTimeTaken();
        }
        return time;
    }
    

    int getNumInstructionsThis() {
        return m_numInstructions;
    }

    
    /*
     *  recursive information gathering below here
     */
    
    double getTimeTakenRelative(boolean recurse) {
        double time = 0.0;

        for (Block block : getPathBlocks()) {
            time += block.getTimeTakenThis();

            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {

                    PathAttributes attributes = block
                            .getLoopPathAttributes(loopPath);
                    if (!attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            time += loopPath.getTimeTakenRelative(true)
                                    * numIterations;
                        }
                    }
                }
                block.doneWithLoopPaths();
            }
        }
        return time;
    }

    double getTimeTakenAbsolute(boolean recurse) {
        double time = 0.0;
        for (Block block : getPathBlocks()) {
            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {
                    time += loopPath.getTimeTakenAbsolute(true);
                    PathAttributes attributes = block
                            .getLoopPathAttributes(loopPath);
                    if (attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            time += loopPath.getTimeTakenRelative(false)
                                    * numIterations;
                        }
                    }
                }
                block.doneWithLoopPaths();
            }
        }

        return time;
    }


    int getNumInstructionsRelative(boolean recurse) {
        int num = 0;

        for (Block block : getPathBlocks()) {
            num += block.getNumInstructionsThis();

            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {
                    PathAttributes attributes = block
                            .getLoopPathAttributes(loopPath);
                    if (!attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            num += loopPath.getNumInstructionsRelative(true)
                                    * numIterations;
                        }
                    }
                }
                block.doneWithLoopPaths();
            }
        }
        return num;
    }

    int getNumInstructionsAbsolute(boolean recurse) {
        int num = 0;

        for (Block block : getPathBlocks()) {
            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {
                    num += loopPath.getNumInstructionsAbsolute(true);

                    PathAttributes attributes = block
                            .getLoopPathAttributes(loopPath);
                    if (attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            num += loopPath.getNumInstructionsRelative(false)
                                    * numIterations;
                        }
                    }
                }
                block.doneWithLoopPaths();
            }
        }
        return num;
    }

    int getNumFNopsRelative(boolean recurse) {
        int num = 0;

        for (Block block : getPathBlocks()) {
            num += block.getNumFNopsThis();

            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {
                    PathAttributes attributes = block
                            .getLoopPathAttributes(loopPath);
                    if (!attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            num += loopPath.getNumFNopsRelative(true)
                                    * numIterations;
                        }
                    }
                }
                block.doneWithLoopPaths();
            }
        }
        return num;
    }

    int getNumFNopsAbsolute(boolean recurse) {
        int num = 0;

        for (Block block : getPathBlocks()) {
            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {
                    num += loopPath.getNumFNopsAbsolute(true);

                    PathAttributes attributes = block
                            .getLoopPathAttributes(loopPath);
                    if (attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            num += loopPath.getNumFNopsRelative(false)
                                    * numIterations;
                        }
                    }
                }
                block.doneWithLoopPaths();
            }
        }
        return num;
    }

    int getNumCyclesRelative(boolean recurse) {
        int num = 0;

        for (Block block : getPathBlocks()) {
            num += block.getNumCyclesThis();

            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {
                    PathAttributes attributes = block
                            .getLoopPathAttributes(loopPath);
                    if (!attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            num += loopPath.getNumCyclesRelative(true)
                                    * numIterations;
                        }
                    }
                }
                block.doneWithLoopPaths();
            }
        }
        return num;
    }
    int getNumPausablesRelative(boolean recurse) {
        int num = 0;

        for (Block block : getPathBlocks()) {
            num += block.getNumPausablesThis();

            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {
                    PathAttributes attributes = block
                            .getLoopPathAttributes(loopPath);
                    if (!attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            num += loopPath.getNumPausablesRelative(true)
                                    * numIterations;
                        }
                    }
                }
                block.doneWithLoopPaths();
            }
        }
        return num;
    }

    int getNumPausablesAbsolute(boolean recurse) {
        int num = 0;

        for (Block block : getPathBlocks()) {
            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {
                    num += loopPath.getNumPausablesAbsolute(true);

                    PathAttributes attributes = block
                            .getLoopPathAttributes(loopPath);
                    if (attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            num += loopPath.getNumPausablesRelative(false)
                                    * numIterations;
                        }
                    }
                }
                block.doneWithLoopPaths();
            }
        }
        return num;
    }   
    public int getNumUnknowns(boolean recurse) {
        int num = 0;
        for (Block block : getPathBlocks()) {
            num += block.getUnknowns();
            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {
                    num += loopPath.getNumUnknowns(true);
                }
                block.doneWithLoopPaths();
            }
        }
        return num;
    }

    int getNumCyclesAbsolute(boolean recurse) {
        int num = 0;

        for (Block block : getPathBlocks()) {
            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {
                    num += loopPath.getNumCyclesAbsolute(true);

                    PathAttributes attributes = block
                            .getLoopPathAttributes(loopPath);
                    if (attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            num += loopPath.getNumCyclesRelative(false)
                                    * numIterations;
                        }
                    }
                }
                block.doneWithLoopPaths();
            }
        }
        return num;
    }
    
    private void traceInstruction(Block Block) {
        XtaInstruction instruction = Block.getCurrentInstruction();

        String functionOffsetString = instruction.getFunctionName() + " + "
                + instruction.getFunctionOffset();
        System.out.print(Block.hashCode() + " ");
        System.out.printf("%3d:[%8d] (%-20s) ", Block.getId(), instruction
                .hashCode(), functionOffsetString);

        System.out.printf("  0x%05x %s %s", instruction.getPc(), instruction
                .getNmenonic(), instruction.getOperandString());

        if (Block.getStatus() == Status.LOOPED) {
            System.out.printf(" [LOOPED -> 0x%05x]\n", Block.getPc());
        } else if (Block.getStatus() == Status.UNRESOLVED) {
            System.out.printf(" [UNRESOLVED]\n");
        } else if (Block.getStatus() == Status.COMPLETE) {
            System.out.printf(" [COMPLETE]\n");
        } else {
            System.out.printf("\n");
        }
    }

    

    private void dumpContexts() {
        for (TreeSet<Integer> context : m_contexts) {
            System.out.printf("Context: ");
            for (Integer pc : context) {
                System.out.printf("0x%05x ", pc);
            }
            System.out.printf("\n");
        }
    }
}
