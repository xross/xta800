package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;
import com.xmos.xta.engine.Engine.ReferenceMode;
import com.xmos.xta.engine.Status.BlockStatus;
import com.xmos.xta.engine.references.CodeReference;

public class Block implements Comparable<Block> {
	private static final boolean DEBUG = false;
	private static final boolean LOG_PROBLEMS = false;

	static enum Mode {
		FUNCTION, ABSOLUTE
	}
	
	private BlockCommonComponents m_bc;

	private Block m_parentBlock;
	private Route m_route;

	private int m_blockId;
	private int m_pathId;

	private MachineState m_machineState;
	private Vector<XtaInstruction> m_instructions;

	private HashMap<Block, PathAttributes> m_loopPathAttributes;
	private ArrayList<Block> m_loopPaths;

	private int m_toPc;
	private BlockStatus m_status;
	private Unresolved m_unresolved;
	private int m_functionDepth;

	private int m_seen;
	private Stack<TreeSet<Integer>> m_contexts;

	private int m_numFNops;
	private int m_numInstructions;
	private int m_numPausables;
	
	private ArrayList<Unknown> m_unknowns;
	private HashSet<Integer> m_pcs;
	private boolean m_speculative;
	private int m_currentThread;
	private boolean m_firstStep;
	private ExecutionState m_executionState;	
	private BlockStats m_blockStats;
	
	private static int m_numBlocks;
	private static int m_numPaths;
	

    public static Block getPath(Route route, int pathId) {
    	assert(route != null);
    	
        for (Block path : route.completePaths()) {
        	Block foundPath = getPath(path, pathId);
            if (foundPath != null) {
                return foundPath;
            }
        }
        for (Unresolved path : route.unresolvedPaths()) {
        	Block foundPath = getPath(path.getBlock(), pathId);
            if (foundPath != null) {
                return foundPath;
            }
        }                
        return null;        
    }
    
    private static Block getPath(Block path, int pathId) {
        if (pathId == path.getId()) {
            return path;
        }
        for (Block block : path.getPathBlocks()) {
            for (Block loopPath : block.getLoopPaths()) {
            	Block foundPath = getPath(loopPath, pathId);
                if (foundPath != null) {
                    path.doneWithPathBlocks();
                    return foundPath;
                }
            }
        }
        path.doneWithPathBlocks();
        return null;
    }    
		
	/*
	 * this is the initial constructor for a path - it only gets called once per
	 * route analysis
	 */
	public Block(BlockCommonComponents bc, int fromPc, int toPc, MachineState machineState, ExecutionState executionState) {
		if(DEBUG) System.out.println("block 0 started at 0x" + Integer.toHexString(fromPc));
		
		m_numBlocks = 0;
		initialise(machineState);
		m_bc = bc;
		m_status = BlockStatus.SEARCHING;
		m_contexts.add(new TreeSet<Integer>());
		m_currentThread = 0;
		m_speculative = false;
		m_firstStep = true;
		m_seen = 0;
		m_toPc = toPc;
		m_route = bc.getRoute();
		m_executionState = executionState;
		m_functionDepth = 0;
		m_numFNops = 0;
		m_numInstructions = 0;
		m_numPausables = 0;
		m_parentBlock = null;
		m_unresolved = null;
		m_blockStats = new BlockStats(this);
		bc.getRoute().addNewBlock(this);
	}

	Block(Block blockToSplit, int pcHit) {
		m_bc = blockToSplit.m_bc;
		m_bc.getRoute().addNewBlock(this);
		m_machineState = null;
		m_contexts = null;
		m_seen = 0;
		m_instructions = new Vector<XtaInstruction>();
		m_unknowns = new ArrayList<Unknown>();
		m_pcs = new HashSet<Integer>();

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

		updateInstructionInfo();
		blockToSplit.updateInstructionInfo();
		
		m_toPc = m_instructions.lastElement().getPc();
		blockToSplit.m_toPc = blockToSplit.m_instructions.lastElement().getPc();
		m_blockId = blockToSplit.m_blockId;
		blockToSplit.m_blockId = m_numBlocks;
		++m_numBlocks;

		m_status = blockToSplit.m_status;
		m_unresolved = blockToSplit.m_unresolved;
		m_firstStep = false;
		m_functionDepth = blockToSplit.m_functionDepth;

		m_route = blockToSplit.m_route;

		m_loopPaths = blockToSplit.m_loopPaths;
		m_loopPathAttributes = blockToSplit.m_loopPathAttributes;
		m_parentBlock = blockToSplit.m_parentBlock;

		blockToSplit.m_loopPaths = new ArrayList<Block>();
		blockToSplit.m_loopPathAttributes = new HashMap<Block, PathAttributes>();
		blockToSplit.m_parentBlock = this;

		m_speculative = blockToSplit.m_speculative;
		if (DEBUG) System.out.println("\tsplit block " + Integer.toHexString(pcHit) + " at to make block " + m_blockId + " and block " + blockToSplit.m_blockId);
		m_blockStats = new BlockStats(this);
	}

	public BlockStats getBlockStats(){
		return m_blockStats;
	}

	public Block(Block block, MachineState machineState, ExecutionState executionState) {

		initialise(machineState);
		m_status = block.m_status;
		m_speculative = block.m_speculative;
		m_parentBlock = block;
		m_bc = block.m_bc;
		m_bc.getRoute().addNewBlock(this);
		m_status = BlockStatus.SEARCHING;

		Iterator<TreeSet<Integer>> iter = m_parentBlock.m_contexts.iterator();
		while (iter.hasNext()) {
			TreeSet<Integer> pcs = iter.next();
			TreeSet<Integer> copy = new TreeSet<Integer>();
			copy.addAll(pcs);
			m_contexts.add(copy);
		}
		m_currentThread = block.m_currentThread;
		m_firstStep = false;
		m_seen = block.m_seen;
		m_toPc = block.m_toPc;
		m_route = block.m_route;
		m_executionState = executionState;
		m_functionDepth = block.m_functionDepth;
		m_numFNops = 0;
		m_numInstructions = 0;
		m_numPausables = 0;
		m_unresolved = block.m_unresolved;
		m_blockStats = new BlockStats(this);
		assert(m_contexts.size()>0);
		if(DEBUG)System.out.println("block " + this.m_blockId +  " started at 0x" + Integer.toHexString(this.m_machineState.getPc(this.m_currentThread)));
	}

	public void build() throws Exception {				
		
		int pc = m_machineState.getPc(m_currentThread);
		
		if (isProhibited(pc)) {
			setStatus(BlockStatus.EXCLUDED);
			releaseMemory();
			return;
		}

		int opcode = 0;
		try {
			opcode = m_bc.getMemoryImage().readInstructionWord(pc);
		} catch (Exception e) {
			setStatus(BlockStatus.ERROR);
			releaseMemory();
			return;
		}

		m_machineState.setOpcode(opcode);
		m_machineState.resetInstructionState();
		XtaInstruction instruction = m_bc.getNodeCoreInstructionCaches()
		 .decode(opcode, pc, m_bc.getEngine().getNodeId(), m_bc.getEngine().getCoreId());
		
		handleIBuffer(instruction.getNumBytes(), pc);
		
		boolean success = executeInstruction(instruction);
		
		if(!success) return;
		
		addInstruction(instruction);
		
		if(checkComplete(instruction)) return;

		if(instruction.canPause()){
			addUnknown(instruction);
		}
		
		if(m_contexts != null){
			m_contexts.peek().add(pc);
		} else {
			if(LOG_PROBLEMS){
				System.out.println("Block " + m_blockId  +" had lost is contexts");
			}
			throw new Exception("During analysis function contexts have become null");
		}
		int nextPc = pc + instruction.getNumBytes();
		
		//the machine can only be in one of the following states
		if (m_machineState.wasEventWait()) {
			handleEvent(instruction);
		} else if (m_machineState.wasJump()) {
			handleJump(pc, instruction);
		} else if (m_machineState.wasBranch()) {
			handleBranch(instruction, nextPc);
		} else {
			handleNormalInstruction(instruction, nextPc);
		}
	}

	private void handleNormalInstruction(XtaInstruction instruction, int nextPc) {
		checkForLoopFormation(nextPc, false, instruction, false, false);
		if(m_route.completePaths().contains(this)){
			setStatus(BlockStatus.COMPLETE);
			releaseMemory();
		} else{
			m_firstStep = false;
			instruction.setNumIBufferBytes(m_machineState.getNumIBufferBytes(m_currentThread));
		}
	}

	private void handleBranch(XtaInstruction instruction, int nextPc) {
		if (m_machineState.wasBranchTargetResolved() && m_machineState.wasGuardResolved()) {
			checkForLoopFormation(
					m_machineState.getBranchTarget(), 
					false, 
					instruction, 
					false, 
					false);
			
			
		} else {
			//add the branch target to the sucessors if need be
			if (m_machineState.wasBranchTargetResolved()){
				//System.out.println("adding 0x" + m_machineState.getBranchTarget());
				//instruction.addSuccessor(m_machineState.getBranchTarget());
			}
			
			
			//take all the successors
			if(instruction.getSuccessors().size()>0){
				
				for(int target : instruction.getSuccessors()){
					checkForLoopFormation(target, true, instruction, false, true);
				}
				if(instruction.getSuccessors().size()!=1){
					setStatus(BlockStatus.COMPLETE);
					releaseMemory();
				}
			} else {
				setStatus(BlockStatus.UNRESOLVED);
				String reason  = "Unknown branch target";
				Unresolved unresolved = new Unresolved(this, instruction, reason);
				m_unresolved = unresolved;
				m_route.addUnresolvedPath(unresolved);
				releaseMemory();
			}
		}
		//setStatus(BlockStatus.COMPLETE);
		//releaseMemory();
	}

	private void handleJump(int pc, XtaInstruction instruction) {
		
		if (m_machineState.wasJumpResolved()) {
			//take the jump
			
			int jumpTarget = m_machineState.getJumpTarget();
			if (instruction.getBranchLink() && isRecursion(jumpTarget)) {
				setStatus(BlockStatus.UNRESOLVED);
				String reason  = "Recursion with 0x" + Integer.toHexString(jumpTarget);
				Unresolved unresolved = new Unresolved(this, instruction, reason);
				m_unresolved = unresolved;
				m_route.addUnresolvedPath(unresolved);
				releaseMemory();
			} else {
				if (instruction.getReturn()) {
					popFunction();
					if(m_contexts.size() == 0){
						if(LOG_PROBLEMS){
							System.out.println("Block " + m_blockId  +" had lost is contexts");
						}
						m_contexts.add(new TreeSet<Integer>());
					}
				} 
				checkForLoopFormation(jumpTarget, false, instruction, true, false);
				if (instruction.getBranchLink()) {
					pushFunction(pc);
				}
			}
		} else {
			if (instruction.getReturn() ) {
				popFunction();
				if(m_contexts.size() == 0){
					if(LOG_PROBLEMS){
						System.out.println("Block " + m_blockId  +" had lost is contexts");
					}
					m_contexts.add(new TreeSet<Integer>());
				}
			}
			if(instruction.getSuccessors().size()>0){
				for(int returnLocation : instruction.getSuccessors()){
					XtaInstruction callingInst = m_bc.getNodeCoreInstructionCaches().
					getInstructionDecodeCache(m_bc.getEngine().getNodeId(), m_bc.getEngine().getCoreId()).getInstruction(returnLocation);
					checkForLoopFormation(returnLocation, true, callingInst, false, true);
				}
				setStatus(BlockStatus.COMPLETE);
				releaseMemory();
			} else {
				setStatus(BlockStatus.UNRESOLVED);
				String reason  = "Unknown jump target";
				Unresolved unresolved = new Unresolved(this, instruction, reason);
				m_unresolved = unresolved;
				m_route.addUnresolvedPath(unresolved);
				releaseMemory();
			}
		}
	}

	private void handleEvent(XtaInstruction instruction) {
		if (m_machineState.wasEventResolved()) { //ie we know where we are going
			//follow the event vector
			int eventTarget = m_machineState.getEventTarget();
			
			//FIXME - probably tell the user that this has happened
			instruction.addSuccessor(eventTarget);
			checkForLoopFormation(eventTarget, false, instruction, true, true);
			setStatus(BlockStatus.COMPLETE);
			releaseMemory();
		} else {
			//follow the successors
			if(instruction.getSuccessors().size()>0){
				for(int eventTarget : instruction.getSuccessors()){
					checkForLoopFormation(eventTarget, true, instruction, true, true);
				}
				setStatus(BlockStatus.COMPLETE);
				releaseMemory();
			} else {
				setStatus(BlockStatus.UNRESOLVED);
				String reason  = "Unable to resolve the event vectors (possibly event vector set up out of scope?)";
				Unresolved unresolved = new Unresolved(this, instruction, reason);
				m_unresolved = unresolved;
				m_route.addUnresolvedPath(unresolved);
			}
		}
	}

	public Unresolved getUnresolved(){
		return m_unresolved;
	}

	public boolean canUnpause() {
		return false;
	}

	private void initialise(MachineState machineState) {
		m_blockId = m_numBlocks++;
		m_contexts = new Stack<TreeSet<Integer>>();
		m_pcs = new HashSet<Integer>();
		m_instructions = new Vector<XtaInstruction>();
		m_loopPathAttributes = new HashMap<Block, PathAttributes>();
		m_loopPaths = new ArrayList<Block>();
		m_unknowns = new ArrayList<Unknown>();
		m_machineState = machineState;
		m_unknowns = new ArrayList<Unknown>();
	}

	private void updateInstructionInfo() {
		
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

	public int compareTo(Block path) {
		return (int) (path.getTimeTaken(true) - getTimeTaken(true));
	}

	public void addLoopPath(Block pathStartBlock) {
		if (DEBUG) System.out.println("found loopPath: " + pathStartBlock.m_blockId + " forms a loop with block " + m_blockId);
		pathStartBlock.addPathId();
		assert (pathStartBlock != null);
		assert (!m_loopPaths.contains(pathStartBlock));
		if (m_loopPaths.contains(pathStartBlock))
			return;
		m_loopPaths.add(pathStartBlock);
		m_loopPathAttributes.put(pathStartBlock, new PathAttributes());
		return;
	}
	
	void addPathId(){
		m_pathId = m_numPaths;
		m_numPaths++;
	}
	
	private boolean executeInstruction(XtaInstruction instruction) 
			throws Exception {
		try {
			instruction.execute(m_machineState, m_currentThread);
			return true;
		} catch (InstructionExecption e) {
			//continue on if not fatal
			if(e.isFatal()){
				setStatus(BlockStatus.UNRESOLVED);
				String reason  = e.getMessage();
				Unresolved unresolved = new Unresolved(this, instruction, reason);
				m_unresolved = unresolved;
				m_route.addUnresolvedPath(unresolved);
			} else {
				return true;
				//addUnknown(instruction);
			}
		}
		releaseMemory();
		return false;
	}

	private void addUnknown(XtaInstruction inst) {
		Unknown unknown;
		if (inst.canPause()) {
			unknown = new Unknown(this, inst);
		} else {
			unknown = new Unknown(this, inst, m_machineState.getGuardExpression(),
					m_machineState.getBranchTargetExpression());
		}
		m_unknowns.add(unknown);
		m_route.addUnknown(unknown);
	}
	
	private void checkForLoopFormation(int targetPc, boolean speculative, 
			XtaInstruction currentInst, boolean hasJumped, boolean hasBranched) {	
		
		m_speculative = m_speculative | speculative;
		Block loopBlock = formsLoop(targetPc);
		/*
		 * this instruction forms a loop if
		 * 	- the next instruction is already in the blocks path
		 * 	- the current instruction is speculative
		 */
		if (m_speculative && loopBlock != null) {
			
			//this is the case when a loop should be formed
			XtaInstruction nextInstruction = null;
			
			try {
				nextInstruction = m_bc.getNodeCoreInstructionCaches()
				 .decode(m_bc.getMemoryImage().readInstructionWord(targetPc), 
						 targetPc, m_bc.getEngine().getNodeId(), m_bc.getEngine().getCoreId());
			} catch (Exception e) {
				assert(false);
			}
			
			//if the path completes on the next instruction then 
			if(checkComplete(nextInstruction)) return;
			
			if (loopBlock.getInstructionsThis().get(0).getPc() != targetPc) {
				new Block(loopBlock, targetPc);
			}
			loopBlock.addLoopPath(this);
			this.setStatus(BlockStatus.COMPLETE);
		} else {
			if (hasBranched && speculative) {
				MachineState machineState = new MachineState(m_machineState); 
				machineState.branchTo(targetPc, currentInst.isLoadStore(), m_currentThread);
				new ExecutionState(m_executionState, machineState);
			} else if (hasJumped || (hasBranched && !speculative)) {
				m_machineState.branchTo(targetPc, currentInst.isLoadStore(), m_currentThread);
			} else {
				if (!currentInst.isLoadStore()) {
					m_machineState.fetch(m_currentThread);
				}
				m_machineState.setPc(targetPc, m_currentThread);
				m_machineState.issue(currentInst.getNumBytes(), m_currentThread);
			}
		}
	}
	
	private void releaseMemory(){
		m_machineState.releaseMemory();
		m_machineState = null;
		m_contexts.clear();
		this.m_contexts = null;
	}
	
	private boolean checkComplete(XtaInstruction instruction) {
		if (isComplete(instruction, instruction.getPc())) {
			setStatus(BlockStatus.COMPLETE);
			if(m_route.completePaths().contains(this)) {
				if(LOG_PROBLEMS){
					System.out.println("Block " + m_blockId  +" completed and was "
							+" already added to m_completePaths");
				}
			} else {
				m_route.addCompletePath(this);
			}
			return true;
		}
		return false;
	}
	
	private boolean isComplete(XtaInstruction instruction, int pc) {
		switch (m_bc.getMode()) {
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

	private boolean isProhibited(int pc) {
		Engine engine = m_bc.getEngine();
		if(engine == null) return false;
		for (CodeReference reference : engine.getReference(ReferenceMode.PROHIBITED)) {
			assert (reference.getResolved());
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
		if(DEBUG) {
			System.out.println(this.m_blockId + " "+inst);
		}
		//inst.setBasicNumCycles(m_bc.getM_instructionAttributes().getTiming(inst.getNmenonic()));
		inst.setConfig(m_bc.getConfig());

		boolean keepInstructionTrace =true;
		
		if (m_instructions != null && keepInstructionTrace){
			m_instructions.add(inst);
		} else {
			if(m_instructions.size()>0){
				m_instructions = null;
			}
		}
		m_pcs.add(inst.getPc());
		if (inst.isFnop()) {
			m_numFNops++;
		} else {
			if (inst.canPause()) {
				m_numPausables++;
			}
			m_numInstructions++;
		}

	}
	
	private Block formsLoop(int pc) {
		boolean contextsContainPc = m_contexts.peek().contains(pc);
		Block parent = this;
		while (parent != null) {
			if (parent.m_pcs.contains(pc) && contextsContainPc) {
				return parent;
			}
			parent = parent.m_parentBlock;
		}
		return null;
	}

	private void pushFunction(int startPc) {
		m_contexts.push(new TreeSet<Integer>());
		++m_functionDepth;
	}

	private void popFunction() {
		m_contexts.pop();
		--m_functionDepth;
	}

	private void handleIBuffer(int instNumBytes, int pc) {
		
		if (m_machineState.canIssue(instNumBytes, m_currentThread) == false) {
			XtaInstruction fNop = m_bc.getNodeCoreInstructionCaches().getFNop(pc, 0, m_bc.getEngine().getNodeId(), m_bc.getEngine().getCoreId());
			if(!(m_bc.getMode() == Mode.FUNCTION && m_firstStep)){
				addInstruction(fNop);
			}
			
			m_machineState.fetch(m_currentThread);
			fNop.setNumIBufferBytes(m_machineState.getNumIBufferBytes(m_currentThread));

			// May require two FNOPs if fetching a mis-aligned long
			if (m_machineState.canIssue(instNumBytes, m_currentThread) == false) {
				fNop = m_bc.getNodeCoreInstructionCaches().getFNop(pc, 1, m_bc.getEngine().getNodeId(), m_bc.getEngine().getCoreId());
				if(!(m_bc.getMode() == Mode.FUNCTION && m_firstStep)){
					addInstruction(fNop);
				}
				m_machineState.fetch(m_currentThread);
				fNop.setNumIBufferBytes(m_machineState.getNumIBufferBytes(m_currentThread));
			}
		}
	}

	public BlockStatus getStatus() {
		return m_status;
	}

	public void setStatus(BlockStatus blockStatus) {
		m_status = blockStatus;
		if (DEBUG) {
			System.out.println("block " + m_blockId + " status is " + blockStatus);
		}
	}

	public int numLoopPaths() {
		if (m_seen == 0) {
			return m_loopPaths.size();
		} else {
			return 0;
		}
	}

	public ArrayList<Block> getLoopPaths() {
		if (m_seen == 0) {
			return m_loopPaths;
		} else {
			return new ArrayList<Block>();
		}
	}
	
	public int getPathId(){
		return m_pathId;
	}
	

	public int getId() {
		return m_blockId;
	}

	public void setId(int id) {
		m_blockId = id;
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

	public PathAttributes getLoopPathAttributes(Block block) {
		PathAttributes attributes = m_loopPathAttributes.get(block);
		assert (attributes != null);
		return attributes;
	}

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

		if (block != null)
			block.m_seen++;

		return paths;
	}

	public void doneWithPathBlocks() {
		Block block = this;

		do {
			if (block.m_loopPaths.contains(this)) {
				break;
			}
			block = block.m_parentBlock;
		} while (block != null);

		if (block != null) {
			assert (block.m_seen > 0);
			block.m_seen--;
		}
	}

	public int getNumPathBlocks() {
		int numPathBlocks = 0;
		Block block = this;

		do {
			++numPathBlocks;
			if (block.m_loopPaths.contains(this)) {
				break;
			}
			block = block.m_parentBlock;
		} while (block != null);

		return numPathBlocks;
	}
	
	public int getNumIterations() {
		Block parent = this;
		while (parent != null) {
			if (parent.m_loopPaths.contains(this)) {
				PathAttributes attributes = parent.getLoopPathAttributes(this);
				return attributes.getNumIterations();
			}
			parent = parent.m_parentBlock;
		}
		return 1;
	}

	public int getLength() {
		int pathLength = 0;
		for (Block block : getPathBlocks()) {
			pathLength += block.m_instructions.size();
		}
		return pathLength;
	}

	public void getAllBlocks(TreeSet<Block> allBlocks) {
		for (Block block : getPathBlocks()) {
			allBlocks.add(block);
			for (Block loopPath : block.getLoopPaths()) {
				loopPath.getAllBlocks(allBlocks);
			}
		}
		doneWithPathBlocks();
	}

	public Vector<XtaInstruction> getInstructions(boolean recurse) {
		Vector<XtaInstruction> insts = new Vector<XtaInstruction>();

		for (Block block : getPathBlocks()) {
			insts.addAll(block.getInstructionsThis());
			if (recurse) {
				for (Block loopPath : block.getLoopPaths()) {
					insts.addAll(loopPath.getInstructions(true));
				}
			}
		}
		doneWithPathBlocks();
		return insts;
	}
	
	public Vector<XtaInstruction> getInstructionsThis() {
		return m_instructions;
	}
	
	public HashSet<Integer> getPcsCovered(boolean recurse) {
		HashSet<Integer> pcsCovered = new HashSet<Integer>();
		for (Block block : getPathBlocks()) {
			pcsCovered.addAll(block.getPcsCoveredThis());		
			if (recurse) {
				for (Block loopPath : block.getLoopPaths()) {
					pcsCovered.addAll(loopPath.getPcsCovered(true));
				}
			}
		}
		doneWithPathBlocks();		
		return pcsCovered;
	}
	
	public HashSet<Integer> getPcsCoveredThis() {
		return m_pcs;
	}
	
	public HashSet<Integer> getPcsCoveredLoopsOnly() {
		HashSet<Integer> pcsCovered = new HashSet<Integer>();

		for (Block loopPath : getLoopPaths()) {
			pcsCovered.addAll(loopPath.getPcsCovered(true));
		}
		return pcsCovered;
	}
	
	// --------------------------------------------------------------------- //
	// Statistics
	// --------------------------------------------------------------------- //	
	
	public double getTimeTaken(boolean recurse) { return m_blockStats.getTimeTaken(recurse);}
	public int getNumInstructions(boolean recurse) { return m_blockStats.getNumInstructions(recurse);}
	public int getNumFNops(boolean recurse) { return m_blockStats.getNumFNops(recurse);}
	public int getNumCycles(boolean recurse) { return m_blockStats.getNumCycles(recurse);}
	public int getNumPausables(boolean recurse) { return m_blockStats.getNumPausables(recurse);}
	public int getNumUnknowns(boolean recurse) { return m_blockStats.getNumUnknowns(recurse);}
	

	// --------------------------------------------------------------------- //
	// 'This' statistics
	// --------------------------------------------------------------------- //		
	public double getTimeTakenThis() {
		double time = 0.0;
		for (XtaInstruction instruction : m_instructions) {
			time += instruction.getTimeTaken();
		}
		return time;
	}

	public int getNumCyclesThis() {
		int numCycles = 0;
		for (XtaInstruction instruction : m_instructions) {
			numCycles += instruction.getNumCycles();
		}
		return numCycles;
	}
	
	int getNumInstructionsThis() {
		return m_numInstructions;
	}

	public int getNumFNopsThis() {
		return m_numFNops;
	}
			
	public int getNumPausablesThis() {
		return m_numPausables;
	}	
	
	public int getNumUnknownsThis() {
		int num = 0;
		for (Unknown unknown : m_unknowns) {
			if (unknown.isShown() && !unknown.isSet())
				num++;
		}
		return num;
	}

	
	// --------------------------------------------------------------------- //
	// 'Loops only' statistics
	// --------------------------------------------------------------------- //		
	
	public double getTimeTakenLoopsOnly() {	return m_blockStats.getTimeTakenLoopsOnly();}
	public int getNumCyclesLoopsOnly() { return m_blockStats.getNumCyclesLoopsOnly();}
	public int getNumInstructionsLoopsOnly() { return m_blockStats.getNumInstructionsLoopsOnly();}
	public int getNumFNopsLoopsOnly() {	return m_blockStats.getNumFNopsLoopsOnly();}
	public int getNumPausablesLoopsOnly() {	return m_blockStats.getNumPausablesLoopsOnly();}
	public int getNumUnknownsLoopsOnly() { return m_blockStats.getNumUnknownsLoopsOnly();}
	
}
