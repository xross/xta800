package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import com.xmos.dwarfhandler.DebugEngine;
import com.xmos.dwarfhandler.StatementInfo;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;

public class Block implements  Comparable<Block> { 
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_STATUS = false;
    private static int m_numCompletedPaths = 0;    
    private static int m_numUnresolvedPaths = 0;    
    private static int m_numLoopPaths = 0;    

    enum Mode {
        FUNCTION,
        ABSOLUTE,
    }    
    public enum Status {
        SEARCHING,
        COMPLETE,
        UNRESOLVED,
        LOOPED,
        RECURSION,
        ERROR,
        EXCLUDED,
        MAX_NUM_PATHS
    }

    private Config                  m_config;

    private int                     m_id;
    private int                     m_instanceId;
    private Mode                    m_mode; 
    private MachineState            m_machineState;
    private XtaInstruction          m_instruction;

    private Block                   m_parentBlock;
    private Route                   m_route;
    private Vector<XtaInstruction>  m_instructions;

    private HashMap<Block, PathAttributes> m_loopPathAttributes;
    private Vector<Block>            m_loopPaths;

    private InstructionDecodeCache  m_instructionDecodeCache;
    private InstructionAttributes   m_instructionAttributes;

    private MemoryImage             m_memoryImage;
    private SymbolTable             m_symbolTable;

    private int                     m_fromPc;
    private int                     m_toPc;
    private Status                  m_status;
    private boolean                 m_firstStep;
    private int                     m_functionDepth;

    private Stack<TreeSet<Integer>> m_contexts;

    // Map of hit PCs per function
    private HashMap<Integer, TreeSet<Integer>> m_functionContexts;
    
    private Vector<Reference>       m_exclusions;

    private HashMap<String, TreeSet<Integer>>   m_sourceLinesCovered;    
    private HashMap<String, TreeSet<Integer>>   m_asmLinesCovered;    

    boolean                         m_topLevel;

    private static int              m_numPaths;
    private static int              m_numPathInstances;

    private int                     m_numFNops;
    private int                     m_numInstructions;
    private int                     m_numPausables;

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

    Block(Mode mode, Config config, InstructionDecodeCache instructionDecodeCache, 
            InstructionAttributes instructionAttributes, MemoryImage memoryImage, 
            SymbolTable symbolTable, Vector<Reference> exclusions, 
            int fromPc, int toPc, Route route, Block parentBlock, 
            MachineState machineState, Stack<TreeSet<Integer>> contexts,
            HashMap<Integer, TreeSet<Integer>> functionContexts) {
        m_machineState   = new MachineState(machineState);
        m_contexts       = new Stack<TreeSet<Integer>>();
        m_functionContexts = functionContexts;
        m_contexts.addAll(contexts);

        m_config         = config;
        m_memoryImage    = memoryImage;
        m_symbolTable    = symbolTable;
        m_instructions   = new Vector<XtaInstruction>();
        m_instructionDecodeCache = instructionDecodeCache;
        m_instructionAttributes  = instructionAttributes;
        m_id            = m_numPaths;
        m_instanceId    = m_id;
        m_mode          = mode;
        m_fromPc        = fromPc;
        m_toPc          = toPc;
        m_status        = Status.SEARCHING;
        m_firstStep     = true;
        m_functionDepth = m_contexts.size() - 1;
        m_exclusions    = exclusions;

        m_sourceLinesCovered = new HashMap<String, TreeSet<Integer>>();
        m_asmLinesCovered    = new HashMap<String, TreeSet<Integer>>();

        m_topLevel      = true;

        m_machineState.setPc(m_fromPc);

        m_numInstructions =0;
        m_numFNops = 0;
        m_numPausables = 0;

        ++m_numPathInstances;
        ++m_numPaths;

        m_route = route;
        m_parentBlock = parentBlock;
        m_loopPaths = new Vector<Block>();
        m_loopPathAttributes = new HashMap<Block, PathAttributes>();

        m_route.addBlock(this);
        if (DEBUG) {
            if (m_parentBlock != null) {
                System.out.printf("New block %d with parent %d\n",
                        m_instanceId, m_parentBlock.m_instanceId);
            } else {
                System.out.printf("New root block %d\n", m_instanceId);
            }
        }
    }
    
    
    public int getFirstInstructionPc(){
        return m_instructions.firstElement().getPc();
    }
    public int getLastInstructionPc(){
        return m_instructions.lastElement().getPc();
    }

    public Block getParentPath(){
        return m_parentBlock;
    }

    public String getDescription(){
        String desc = "Block (" + this.getId() + "): " + this.getTimeTakenThis() + "ns";
        return desc;
    }

    public String getPathDescription(Block caller){
        String desc = "Block (" + this.getId() + "): " + this.getTimeTakenThis() + "ns";
        return desc;
    }


    public int compareTo(Block Block) throws ClassCastException {       	    
        return Block.getNumCyclesThis() - getNumCyclesThis();
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

    public Vector<Block> getLoopPaths() {
        return m_loopPaths;
    }

    public PathAttributes getLoopPathAttributes(Block block) {
        PathAttributes attributes = m_loopPathAttributes.get(block);
        assert(attributes != null);
        return attributes;
    }

    public Vector<XtaInstruction> getInstructionsThis() {
        return m_instructions;
    }

    public Vector<XtaInstruction> getInstructions(boolean recurse) {
        Vector<XtaInstruction> insts = new Vector<XtaInstruction>();
        for(Block block : getPathBlocks()) {
            insts.addAll(block.getInstructionsThis());
            if (recurse) {
                for(Block loopPath : block.getLoopPaths()){
                    insts.addAll(loopPath.getInstructions(true));
                }
            }
        }        
        return insts;
    }

    /*
     * Trace back from a starting block to the point the path ends.
     */
    public ArrayList<Block> getPathBlocks() {
        ArrayList<Block> paths = new ArrayList<Block>();

        Block parent = m_parentBlock;
        paths.add(this);

        while (parent != null) {
            if (parent.getLoopPaths().contains(this))
                break;

            paths.add(0, parent);
            parent = parent.m_parentBlock;
        } 
        return paths;
    }

    public void getAllBlocks(TreeSet<Block> allBlocks) {
        for (Block block : getPathBlocks()) {
            allBlocks.add(block);
            for (Block loopPath : block.getLoopPaths()) {
                loopPath.getAllBlocks(allBlocks);
            }
        }
    }

    public int getNumIterations() {
        Block parent = m_parentBlock;
        while (parent != null) {
            if (parent.getLoopPaths().contains(this)) {
                PathAttributes attributes = parent.getLoopPathAttributes(this);
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
        return getNumInstructionsRelative(recurse) + getNumInstructionsAbsolute(recurse);            
    }

    public int getNumFNops(boolean recurse) {
        return getNumFNopsRelative(recurse) + getNumFNopsAbsolute(recurse);
    }

    public int getNumCycles(boolean recurse) {
        return getNumCyclesRelative(recurse) + getNumCyclesAbsolute(recurse);
    }

    public int getNumPausables(boolean recurse) {
        return getNumPausablesRelative(recurse) +  getNumPausablesAbsolute(recurse);            
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

    public HashMap<String, TreeSet<Integer>> getSourceLinesCovered() {
        HashMap<String, TreeSet<Integer>> sourceLinesCovered = new HashMap<String, TreeSet<Integer>>();

        for (Block block : getPathBlocks()) {
            addCoveredLineToHash(block.m_sourceLinesCovered, sourceLinesCovered); 
        }
        return sourceLinesCovered;
    }

    public HashMap<String, TreeSet<Integer>> getAsmLinesCovered() {
        HashMap<String, TreeSet<Integer>> asmLinesCovered = new HashMap<String, TreeSet<Integer>>();

        for (Block block : getPathBlocks()) {
            addCoveredLineToHash(block.m_asmLinesCovered, asmLinesCovered); 
        }
        return asmLinesCovered;
    }

    private void addCoveredLineToHash(HashMap<String, TreeSet<Integer>> blockLines, 
            HashMap<String, TreeSet<Integer>> allLines) {

        Iterator<Map.Entry<String, TreeSet<Integer>>> iter = 
            blockLines.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, TreeSet<Integer>> pair = iter.next();
            String fileName = pair.getKey();

            TreeSet<Integer> lineNumbers;


            if (!allLines.containsKey(fileName)) {
                lineNumbers = new TreeSet<Integer>();
                allLines.put(fileName, lineNumbers);    
            } else {
                lineNumbers = allLines.get(fileName);
            }

            for (Integer lineNumber : pair.getValue()) {
                if (!lineNumbers.contains(lineNumber)) {
                    lineNumbers.add(lineNumber);
                }
            }
        }
    }

    public int getDepth() {
        int maxDepth = 0;
        //figure this out later
        return maxDepth;

    }

    public boolean addLoopPath(Block pathStartBlock) {
        assert(pathStartBlock != null);
        assert(!m_loopPaths.contains(pathStartBlock));
        if (m_loopPaths.contains(pathStartBlock)) return false;
        m_loopPaths.add(pathStartBlock);
        m_loopPathAttributes.put(pathStartBlock, new PathAttributes());
        return true;
    }

    // Returns true if recursion is detected
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

        if (DEBUG) traceInstruction(this);
        /*
         * after an instruction is added then one of options can occur:
         * 1. Block is completed - add to the routes paths and stop stepping
         * 2. the PC is unknown - stop stepping and add unresolved path
         * 3. recursion is detected - stop stepping and add unresolved path
         * 4. a branch has been hit - follow the branch then don't
         */

        if (isComplete(m_instruction, pc)) {
            setStatus(Status.COMPLETE);
            m_route.addCompletePath(this);
            return;
        } 
        
        if (isExcluded(pc)) {
            setStatus(Status.EXCLUDED);
            return;
        }
        
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

                    m_machineState.branchTo(jumpTarget, m_instruction.isLoadStore());
                }
                
            } else {
                setStatus(Status.UNRESOLVED);
                m_route.addUnresolvedPath(this);
            }

        } else if (m_machineState.wasBranch()) {
            if (m_machineState.wasBranchResolved()){
                if (resolveLoop(m_machineState.getPc())){
                    setStatus(Status.LOOPED);
                } else {
                    m_contexts.peek().add(m_instruction.getPc());
                    takeBranch(m_machineState.getBranchTarget());
                    takeDontBranch(m_instruction);
                    setStatus(Status.COMPLETE);
                }
            } else {
                setStatus(Status.UNRESOLVED);
                m_route.addUnresolvedPath(this);
            }
            
        } else {
            m_contexts.peek().add(m_instruction.getPc());
            
            if (!m_instruction.isLoadStore()) {
                m_machineState.fetch();
            }

            updatePc(m_instruction);
            m_machineState.issue(instNumBytes);
        }

        m_firstStep = false;
        m_instruction.setNumIBufferBytes(m_machineState.getNumIBufferBytes());
    }

    private void takeDontBranch(XtaInstruction instruction) {
        if (m_instruction.isLoadStore() == false) {
            m_machineState.fetch();
        }

        int startPc = m_machineState.getPc() + instruction.getNumBytes(); 
        m_machineState.issue(instruction.getNumBytes());

        Block nextPath = new Block(m_mode, m_config, m_instructionDecodeCache, m_instructionAttributes, 
                m_memoryImage, m_symbolTable, m_exclusions, startPc, m_toPc, m_route,
                this, m_machineState, m_contexts, m_functionContexts);
        nextPath.build();
    }

    private void takeBranch(int branchTarget) {
        Block nextPath = new Block(m_mode, m_config, m_instructionDecodeCache, m_instructionAttributes, 
                m_memoryImage, m_symbolTable, m_exclusions, branchTarget, m_toPc, m_route,
                this, m_machineState, m_contexts, m_functionContexts);
        nextPath.m_machineState.branchTo(branchTarget, m_instruction.isLoadStore());
        nextPath.build();
    }

    private boolean resolveLoop(int pc) {
        if (!isLoop(pc)) {
            return false;
        }
        
        Block parent = m_parentBlock;
        while (parent != null){
            if (parent.getBranchingPC() == pc){

                if (DEBUG) {
                    System.out.printf("Block %d adding %d as loop\n",
                            parent.m_instanceId, m_instanceId);
                }

                parent.addLoopPath(this);
                m_route.addLoopPath(this);
                return true;
            }
            parent = parent.getParentPath();
        }
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
            if (getStatus() != Block.Status.SEARCHING){
                m_status = getStatus();
                break;
            }
            m_route.setCurrentValue(Block.getNumPaths());
        }
    }

    void updatePc(XtaInstruction instruction) {
        int nextPc = 0;
        if (m_machineState.wasJump()){
            if (m_machineState.wasJumpResolved()){
                nextPc = m_machineState.getJumpTarget(); 
            } else {
                setStatus(Status.UNRESOLVED);
                if (DEBUG) System.out.println("found an unresolved Block");
                m_route.addUnresolvedPath(this);
                return;
            }

        } else {
            nextPc = m_machineState.getPc() + instruction.getNumBytes(); 
        }
        assert(nextPc != 0);
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

            System.out.printf("%d - %d - %d\n", 
                    m_numCompletedPaths, m_numUnresolvedPaths, m_numLoopPaths);
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

    double getTimeTakenRelative(boolean recurse) {
        double time = 0.0;

        for (Block block : getPathBlocks()){
            time += block.getTimeTakenThis();

            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {
                    PathAttributes attributes = block.getLoopPathAttributes(loopPath);
                    if (!attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            time += loopPath.getTimeTakenRelative(true) * numIterations;
                        }
                    }
                }
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

                    PathAttributes attributes = block.getLoopPathAttributes(loopPath);
                    if (attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            time += loopPath.getTimeTakenRelative(false) * numIterations;
                        }
                    }
                }
            }
        }
        return time;
    }

    public double getTimeTakenThis() {
        double time = 0.0;
        for (XtaInstruction instruction : m_instructions) {
            time += instruction.getTimeTaken();
        }
        return time;
    }

    int getNumInstructionsRelative(boolean recurse) {
        int num = 0;

        for (Block block : getPathBlocks()){
            num += block.getNumInstructionsThis();

            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {
                    PathAttributes attributes = block.getLoopPathAttributes(loopPath);
                    if (!attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            num += loopPath.getNumInstructionsRelative(true) * numIterations;
                        }
                    }
                }
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

                    PathAttributes attributes = block.getLoopPathAttributes(loopPath);
                    if (attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            num += loopPath.getNumInstructionsRelative(false) * numIterations;
                        }
                    }
                }
            }
        }
        return num;
    }

    int getNumInstructionsThis() {	
        assert(m_numInstructions + m_numFNops == m_instructions.size());
        return m_numInstructions;
    }

    int getNumFNopsRelative(boolean recurse) {
        int num = 0;

        for (Block block : getPathBlocks()){
            num += block.getNumFNopsThis();

            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {
                    PathAttributes attributes = block.getLoopPathAttributes(loopPath);
                    if (!attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            num += loopPath.getNumFNopsRelative(true) * numIterations;
                        }
                    }
                }
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

                    PathAttributes attributes = block.getLoopPathAttributes(loopPath);
                    if (attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            num += loopPath.getNumFNopsRelative(false) * numIterations;
                        }
                    }
                }
            }
        }
        return num;
    }

    public int getNumFNopsThis() {
        return m_numFNops;
    }

    int getNumCyclesRelative(boolean recurse) {
        int num = 0;

        for (Block block : getPathBlocks()){
            num += block.getNumCyclesThis();

            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {
                    PathAttributes attributes = block.getLoopPathAttributes(loopPath);
                    if (!attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            num += loopPath.getNumCyclesRelative(true) * numIterations;
                        }
                    }
                }
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

                    PathAttributes attributes = block.getLoopPathAttributes(loopPath);
                    if (attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            num += loopPath.getNumCyclesRelative(false) * numIterations;
                        }
                    }
                }
            }
        }
        return num;
    }

    public int getNumCyclesThis() {
        int numCycles = 0;    	
        for (XtaInstruction instruction : m_instructions) {
            numCycles += instruction.getNumCycles();
        }	
        return numCycles;
    }

    int getNumPausablesRelative(boolean recurse) {
        int num = 0;

        for (Block block : getPathBlocks()){
            num += block.getNumPausablesThis();

            if (recurse) {
                for (Block loopPath : block.getLoopPaths()) {
                    PathAttributes attributes = block.getLoopPathAttributes(loopPath);
                    if (!attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            num += loopPath.getNumPausablesRelative(true) * numIterations;
                        }
                    }
                }
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

                    PathAttributes attributes = block.getLoopPathAttributes(loopPath);
                    if (attributes.isAbsolute()) {
                        int numIterations = attributes.getNumIterations();
                        if (numIterations > 0) {
                            num += loopPath.getNumPausablesRelative(false) * numIterations;
                        }
                    }
                }
            }
        }
        return num;        
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
            assert(reference.isResolved());
            if (reference.getPc() == pc) {
                return true;
            }
        }
        return false;
    }

    /*
     * Check this and all higher contexts for recursion
     */
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

    private boolean isLoop(int pc) {  
        if (m_contexts.peek().contains(pc)) {
            return true;
        }
        return false;
    }

    private void addInstruction(XtaInstruction inst){
        assert(m_instructions!=null);
        assert(inst!=null);

        if (DEBUG && false) {
            System.out.println("adding inst :" + inst.getNmenonic() + " " +
                    inst.getOperandString()  +" to Block " + m_instanceId);
        }

        inst.setBasicNumCycles(m_instructionAttributes.getTiming(inst.getNmenonic()));
        inst.setConfig(m_config);

        m_instructions.add(inst);
        if (inst.canPause()){
            m_numPausables++;
        }
        if (inst.isFnop()){
            m_numFNops++;
        } else {
            m_numInstructions++;
        }
    }

    void buildCoveredLinesHashes(DebugInfo debugInfo, SymbolTable symbolTable) {

        DebugEngine debugEngine = debugInfo.getDebugEngine();

        for (Block block : getPathBlocks()) {
            block.buildSourceLinesFileHash(debugEngine, m_sourceLinesCovered);                
            block.buildAsmLinesFunctionHash(symbolTable, m_asmLinesCovered);

            if (DEBUG) {
                System.out.printf("---------- Block: %d ----------\n", m_id);   
                System.out.printf("File -> Source Line Map:\n");   
                dumpHash(m_sourceLinesCovered, false);
                System.out.printf("Function -> Asm Line Map:\n");   
                dumpHash(m_asmLinesCovered, true);
            }

            for (Block loopPath : block.getLoopPaths()) {
                loopPath.buildCoveredLinesHashes(debugInfo, symbolTable);
            }
        }
    }

    private void buildSourceLinesFileHash(DebugEngine debugEngine, 
            HashMap<String, TreeSet<Integer>> sourceLinesCovered) { 

        for(XtaInstruction inst : m_instructions){
            Collection<StatementInfo> statements = 
                debugEngine.getStatement(inst.getPc());
            for (StatementInfo statementInfo : statements) {
                String fileName = statementInfo.getFile().getFileName();
                Long ln = statementInfo.getLine();
                int lineNumber = ln.intValue();                  

                addCoveredLineToHash(m_sourceLinesCovered, fileName, lineNumber);
                addCoveredLineToHash(sourceLinesCovered, fileName, lineNumber);
            }
        }
    }  

    private void addCoveredLineToHash(HashMap<String, TreeSet<Integer>> linesCovered, 
            String fileName, int lineNumber) {

        if (linesCovered.containsKey(fileName)) {
            linesCovered.get(fileName).add(lineNumber);
        }
        else {
            TreeSet<Integer> lineNumbers = new TreeSet<Integer>();
            lineNumbers.add(lineNumber);
            linesCovered.put(fileName, lineNumbers);    
        }                                                       
    }


    private void buildAsmLinesFunctionHash(SymbolTable symbolTable, 
            HashMap<String, TreeSet<Integer>> asmLinesCovered) { 

        for (XtaInstruction instruction : m_instructions) {
            String functionName = symbolTable.functionName(instruction.getPc());
            int pc = instruction.getPc();                    

            addCoveredLineToHash(m_asmLinesCovered, functionName, pc);
            addCoveredLineToHash(asmLinesCovered, functionName, pc);                        
        }    	
    }  

    private void dumpHash(HashMap<String, TreeSet<Integer>> linesHash, boolean hex) {
        for (Iterator<Map.Entry<String, TreeSet<Integer>>> iter = 
            linesHash.entrySet().iterator(); iter.hasNext();) {
            Map.Entry<String, TreeSet<Integer>> entry = 
                (Map.Entry<String, TreeSet<Integer>>)iter.next();    

            String fileName = (String)entry.getKey();                    
            TreeSet<Integer> fileLines = (TreeSet<Integer>)entry.getValue();

            System.out.printf("%s: ", fileName);
            for (long line : fileLines) {
                if (hex)
                    System.out.printf(" 0x%05x", line);
                else
                    System.out.printf(" %d", line);
            }
            System.out.printf("\n");                                                   
        }    

    }

    private void traceInstruction(Block Block) {
        XtaInstruction instruction = Block.getCurrentInstruction();

        String functionOffsetString = 
            instruction.getFunctionName() + " + " + instruction.getFunctionOffset();
        System.out.print(Block.hashCode() + " " );
        System.out.printf("%3d:[%8d] (%-20s) ", Block.getId(), 
                instruction.hashCode(), functionOffsetString);

        for (int i = 0; i < Block.getId(); ++i)
            System.out.printf(".");             

        System.out.printf("  0x%05x %s %s", 
                instruction.getPc(), 
                instruction.getNmenonic(), 
                instruction.getOperandString());

        if (Block.getStatus() == Status.LOOPED) {
            System.out.printf(" [LOOPED -> 0x%05x]\n", Block.getPc());
        }
        else if (Block.getStatus() == Status.UNRESOLVED) {
            System.out.printf(" [UNRESOLVED]\n");
        }
        else if (Block.getStatus() == Status.COMPLETE) {
            System.out.printf(" [COMPLETE]\n");         
        }
        else {
            System.out.printf("\n");
        }                       
    }

    private void pushFunction(int startPc) {
        if (DEBUG) {
            System.out.printf("Block: %d: pushFunction: m_functionDepth: %d\n", m_id, m_functionDepth);
            dumpContexts();
        }
        
        TreeSet<Integer> context;
        if (m_functionContexts.containsKey(startPc)) {
            context = m_functionContexts.get(startPc);
        } else {
            context = new TreeSet<Integer>();
            m_functionContexts.put(startPc, context);
        }
        
        m_contexts.push(context);
        ++m_functionDepth;        
    }

    private void popFunction() {
        if (DEBUG) {
            System.out.printf("Block: %d: popFunction: m_functionDepth: %d\n", m_id, m_functionDepth);           
            dumpContexts();
        }		

        m_contexts.pop();
        --m_functionDepth;
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

    private int getBranchingPC(){
        return m_instructions.lastElement().getPc();
    }

    public int getPathNumCycles(Block caller) {
        // TODO Auto-generated method stub
        return 0;
    }
}
