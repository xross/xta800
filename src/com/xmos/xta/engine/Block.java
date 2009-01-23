package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;
import com.xmos.xta.engine.Engine.ReferenceMode;
import com.xmos.xta.engine.MachineState.BranchState;
import com.xmos.xta.engine.Status.BlockStatus;

public class Block implements Comparable<Block> {
	private static final boolean DEBUG = false;
	private static final boolean DUMPSTATE = false;
	private static final boolean IBUFFERSTATE = false;

	static enum Mode {
		FUNCTION, ABSOLUTE
	}
	
	private int m_ibufferBefore;
	private int m_ibufferAfter;
	private int m_ibufferFnops;
	private int m_ibufferFetches;
	private int m_ibufferIssue;
	
	private BlockCommonComponents m_bc;

	private Block m_parentBlock;
	private Route m_route;

	private int m_id;

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
	
	private static int m_numBlocks;

	/*
	 * this is the initial constructor for a path - it only gets called once per
	 * route analysis
	 */
	public Block(BlockCommonComponents bc, int fromPc, int toPc, MachineState machineState, ExecutionState executionState) {
		if(DEBUG)System.out.println("block 0 started at 0x" + Integer.toHexString(fromPc));
		bc.getRoute().addNewBlock(this);
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
		m_id = blockToSplit.m_id;
		blockToSplit.m_id = m_numBlocks;
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
		if (DEBUG)
			System.out.println("\tsplit block " + Integer.toHexString(pcHit) + " at to make block " + m_id + " and block " + blockToSplit.m_id);
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
		assert(m_contexts.size()>0);
		if(DEBUG)System.out.println("block " + this.m_id +  " started at 0x" + Integer.toHexString(this.m_machineState.getPc(this.m_currentThread)));
	}

	public Unresolved getUnresolved(){
		return m_unresolved;
	}

	public boolean canUnpause() {
		return false;
	}

	private void initialise(MachineState machineState) {
		m_id = m_numBlocks++;
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
		if (DEBUG)
			System.out.println("found loopPath: " + pathStartBlock.m_id + " forms a loop with block " + m_id);
		assert (pathStartBlock != null);
		assert (!m_loopPaths.contains(pathStartBlock));
		if (m_loopPaths.contains(pathStartBlock))
			return;
		m_loopPaths.add(pathStartBlock);
		m_loopPathAttributes.put(pathStartBlock, new PathAttributes());
		return;
	}

	public void build() throws Exception {
		
		
		if (IBUFFERSTATE) {
			m_ibufferBefore = 0;
			m_ibufferAfter = 0;
			m_ibufferFnops = 0;
			m_ibufferFetches = 0;
			m_ibufferIssue = 0;
			m_ibufferBefore = m_machineState.getResources().getCurrentIBuffer(m_currentThread).getNumBytes();
		}
		
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
		int instNumBytes = instruction.getNumBytes();


		handleIBuffer(instNumBytes, pc);
		if (DUMPSTATE) dumpState(m_machineState);
		
		boolean success = executeInstruction(instruction);
		
		if(!success) {
			return;
		}
		
		addInstruction(instruction);
		
		if (DUMPSTATE) {
			dumpState(m_machineState);
			System.out.println();
			System.out.println();
		}
		
		if (isComplete(instruction, pc)) {
			setStatus(BlockStatus.COMPLETE);
			if(!m_route.completePaths().contains(this)) 
				m_route.addCompletePath(this);
			return;
		}

		if(instruction.canPause()){
			addUnknown(instruction);
		}
		
		if(m_contexts != null){
			m_contexts.peek().add(pc);
		} else {
			System.err.println("contexts disappered!");
		}
		int nextPc = pc + instNumBytes;
		
		if (m_machineState.wasEventWait()) {
			if (m_machineState.wasEventResolved()) {
				ArrayList<Integer> targets = new ArrayList<Integer>(m_machineState.getEventTarget());
				m_machineState.clearEventTargets();
				for (int targetVector : targets) {
					checkForLoopFormation(targetVector, false, instruction, true, true);
				}
				
				setStatus(BlockStatus.COMPLETE);
			} else {
				setStatus(BlockStatus.UNRESOLVED);
				String reason  = "Unable to resolve the event vectors (possibly event vector set up out of scope?)";
				Unresolved unresolved = new Unresolved(this, instruction, reason);
				m_unresolved = unresolved;
				m_route.addUnresolvedPath(unresolved);
			}
			releaseMemory();
		} else if (m_machineState.wasJump()) {
			if (m_machineState.wasJumpResolved()) {
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
							m_contexts.add(new TreeSet<Integer>());
						}
					}
					checkForLoopFormation(jumpTarget, false, instruction, true, false);
					if (instruction.getBranchLink()) {
						pushFunction(pc);
					}
				}
			} else {
				if (instruction.getReturnLocations().size() > 0) {
					for (XtaInstruction callingInst : instruction.getReturnLocations()) {
						checkForLoopFormation(callingInst.getPc() + callingInst.getNumBytes(), false, callingInst, true, true);
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
		} else if (m_machineState.wasBranch()) {
			if (m_machineState.wasBranchResolved()) {
				if (m_bc.isResolveExpressions()) {
					if (m_machineState.getBranchTaken() == BranchState.TAKEN) {
						checkForLoopFormation(m_machineState.getBranchTarget(), false, instruction, true, false);
					} else if (m_machineState.getBranchTaken() == BranchState.NOTTAKEN) {
						checkForLoopFormation(nextPc, false, instruction, false, false);
					} else if (m_machineState.getBranchTaken() == BranchState.UNKNOWN) {
						addUnknown(instruction);
						checkForLoopFormation(m_machineState.getBranchTarget(), true, instruction, false, true);
						checkForLoopFormation(nextPc, true, instruction, false, true);
						setStatus(BlockStatus.COMPLETE);
						releaseMemory();
					}
				} else {
					checkForLoopFormation(m_machineState.getBranchTarget(), true, instruction, false, true);
					checkForLoopFormation(nextPc, true, instruction, false, true);
					setStatus(BlockStatus.COMPLETE);
					releaseMemory();
				}
			} else {
				setStatus(BlockStatus.UNRESOLVED);
				String reason  = "Unknown branch target";
				Unresolved unresolved = new Unresolved(this, instruction, reason);
				m_unresolved = unresolved;
				m_route.addUnresolvedPath(unresolved);
				m_machineState = null;
			}
		} else {
			checkForLoopFormation(nextPc, false, instruction, false, false);
			if(m_route.completePaths().contains(this)){
				setStatus(BlockStatus.COMPLETE);
				releaseMemory();
			} else{
				m_firstStep = false;
				instruction.setNumIBufferBytes(m_machineState.getNumIBufferBytes(m_currentThread));
			}
		}
		
		if(IBUFFERSTATE) {
			m_ibufferAfter = m_machineState.getResources().getCurrentIBuffer(m_currentThread).getNumBytes();
		}
		
		if(IBUFFERSTATE) {
			System.out.print("start: " + m_ibufferBefore + 
					" fnop fetches: " + m_ibufferFnops + 
					" fetches: " + m_ibufferFetches+ 
					" issues:" + m_ibufferIssue + 
					" After: " + m_ibufferAfter);
			int properState = m_ibufferBefore + m_ibufferFnops + m_ibufferFetches - m_ibufferIssue ;
			
			if(properState != m_ibufferAfter){
				System.out.print("  eek");
				// m_machineState.getResources().getCurrentIBuffer(m_currentThread).m_numBytes = properState;
			}
			System.out.println("\n");
		}
	}

	private boolean executeInstruction(XtaInstruction instruction) 
			throws Exception {
		try {
			instruction.execute(m_machineState, m_currentThread);
			return true;
		} catch (InstructionExecption e) {
			
			if(m_bc.isResolveExpressions()){
				//die when we wont be able to be sure of everything anymore
				setStatus(BlockStatus.UNRESOLVED);
				String reason  = e.getMessage();
				
				Unresolved unresolved = new Unresolved(this, instruction, reason);
				m_unresolved = unresolved;
				m_route.addUnresolvedPath(unresolved);
			} else {
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

	private void checkForLoopFormation(int nextPc, boolean speculative, 
			XtaInstruction inst, boolean isJump, boolean isBranch) {	
		Block loopBlock = null;
		m_speculative = m_speculative | speculative;
		if (m_speculative) {
			loopBlock = formsLoop(nextPc);
		}
		if (loopBlock != null) {
			XtaInstruction instruction = null;
			try {
				instruction = m_bc.getNodeCoreInstructionCaches()
				 .decode(m_bc.getMemoryImage().readInstructionWord(nextPc), 
						 nextPc, m_bc.getEngine().getNodeId(), m_bc.getEngine().getCoreId());
			} catch (Exception e) {
				assert(false);
			}
			
			if(isComplete(instruction, nextPc)){
				//addInstruction(instruction);
				if(!m_route.completePaths().contains(this)) 
					m_route.addCompletePath(this);
				return;
			}
			
			if (loopBlock.getInstructionsThis().get(0).getPc() != nextPc) {
				new Block(loopBlock, nextPc);
			}
			loopBlock.addLoopPath(this);
			this.setStatus(BlockStatus.COMPLETE);
		} else {
			if (isBranch) {
				MachineState machineState = new MachineState(m_machineState);
				

				m_ibufferBefore = machineState.branchTo(nextPc, inst.isLoadStore(), m_currentThread);
				new ExecutionState(m_executionState, machineState);
			} else if (isJump) {
				m_ibufferBefore = m_machineState.branchTo(nextPc, inst.isLoadStore(), m_currentThread);
			} else {
				if (!inst.isLoadStore()) {
					
					int fetched =m_machineState.fetch(m_currentThread);
					if(IBUFFERSTATE){
						m_ibufferFetches+=fetched;
					} 
				}
				m_machineState.setPc(nextPc, m_currentThread);
				if(IBUFFERSTATE){
					m_ibufferIssue+=inst.getNumBytes();
				}
				m_machineState.issue(inst.getNumBytes(), m_currentThread);
			}
		}
	}
	
	private void releaseMemory(){
		m_machineState.releaseMemory();
		m_machineState = null;
		m_contexts.clear();
		this.m_contexts = null;
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
		if(DEBUG) {
			System.out.println(this.m_id + " " +m_functionDepth + " "+inst);
		}
		inst.setBasicNumCycles(m_bc.getM_instructionAttributes().getTiming(inst.getNmenonic()));
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
			
			int fetched1 = m_machineState.fetch(m_currentThread);
			if(IBUFFERSTATE){
				m_ibufferFnops+=fetched1;
			}
			fNop.setNumIBufferBytes(m_machineState.getNumIBufferBytes(m_currentThread));

			// May require two FNOPs if fetching a mis-aligned long
			if (m_machineState.canIssue(instNumBytes, m_currentThread) == false) {
				fNop = m_bc.getNodeCoreInstructionCaches().getFNop(pc, 1, m_bc.getEngine().getNodeId(), m_bc.getEngine().getCoreId());
				if(!(m_bc.getMode() == Mode.FUNCTION && m_firstStep)){
					addInstruction(fNop);
				}
				int fetched2 = m_machineState.fetch(m_currentThread);
				if(IBUFFERSTATE){
					m_ibufferFnops+=fetched2;
				}
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
			System.out.println("block " + m_id + " status is " + blockStatus);
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

	public int getId() {
		return m_id;
	}

	public void setId(int id) {
		m_id = id;
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

	public ArrayList<Integer> getAllPcs() {
		ArrayList<Integer> allPcs = new ArrayList<Integer>();
		allPcs.addAll(m_pcs);
		for (Block block : getPathBlocks()) {
			allPcs.addAll(block.m_pcs);
			for (Block loopPath : block.getLoopPaths()) {
				allPcs.addAll(loopPath.getAllPcs());
			}
		}
		doneWithPathBlocks();
		return allPcs;
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

	public Block getParentBlock() {
		return m_parentBlock;
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
		return getNumPausablesRelative(recurse) + getNumPausablesAbsolute(recurse);
	}

	public int getNumUnknowns(boolean recurse) {
		return getNumUnknownsRelative(recurse) + getNumUnknownsAbsolute(recurse);
	}
	
	// --------------------------------------------------------------------- //
	// Relative statistics
	// --------------------------------------------------------------------- //	
	interface IntegerRelative { 
		int getValueThis(Block block); 
		int getValueRelative(Block block); 
	}
	interface DoubleRelative { 
		double getValueThis(Block block); 
		double getValueRelative(Block block); 
	}
	
	private double getTimeTakenRelative(boolean recurse) {
		return getRelativeDouble(recurse, new DoubleRelative() {
			public double getValueThis(Block block)     { return block.getTimeTakenThis(); }			
			public double getValueRelative(Block block) { return block.getTimeTakenRelative(true); }});
	}

	private int getNumCyclesRelative(boolean recurse) {
		return getRelativeInteger(recurse, new IntegerRelative() {
			public int getValueThis(Block block)     { return block.getNumCyclesThis(); }			
			public int getValueRelative(Block block) { return block.getNumCyclesRelative(true); }});
	}	
	
	private int getNumInstructionsRelative(boolean recurse) {
		return getRelativeInteger(recurse, new IntegerRelative() {
			public int getValueThis(Block block)     { return block.getNumInstructionsThis(); }			
			public int getValueRelative(Block block) { return block.getNumInstructionsRelative(true); }});
	}	

	private int getNumFNopsRelative(boolean recurse) {
		return getRelativeInteger(recurse, new IntegerRelative() {
			public int getValueThis(Block block)     { return block.getNumFNopsThis(); }			
			public int getValueRelative(Block block) { return block.getNumFNopsRelative(true); }});
	}	
	
	private int getNumPausablesRelative(boolean recurse) {
		return getRelativeInteger(recurse, new IntegerRelative() {
			public int getValueThis(Block block)     { return block.getNumPausablesThis(); }			
			public int getValueRelative(Block block) { return block.getNumPausablesRelative(true); }});
	}	

	private int getNumUnknownsRelative(boolean recurse) {
		return getRelativeInteger(recurse, new IntegerRelative() {
			public int getValueThis(Block block)     { return block.getNumUnknownsThis(); }			
			public int getValueRelative(Block block) { return block.getNumUnknownsRelative(true); }});
	}				
	
	private double getRelativeDouble(boolean recurse, DoubleRelative val) {
		double result = 0.0;
		for (Block block : getPathBlocks()) {
			result += val.getValueThis(block);
			if (recurse) {
				for (Block loopPath : block.getLoopPaths()) {

					PathAttributes attributes = block.getLoopPathAttributes(loopPath);
					if (!attributes.isAbsolute()) {
						int numIterations = attributes.getNumIterations();
						if (numIterations > 0) {
							result += val.getValueRelative(loopPath) * numIterations;
						}
					}
				}
			}
		}
		doneWithPathBlocks();
		return result;
	}

	private int getRelativeInteger(boolean recurse, IntegerRelative val) {
		int result = 0;
		for (Block block : getPathBlocks()) {
			result += val.getValueThis(block);
			if (recurse) {
				for (Block loopPath : block.getLoopPaths()) {

					PathAttributes attributes = block.getLoopPathAttributes(loopPath);
					if (!attributes.isAbsolute()) {
						int numIterations = attributes.getNumIterations();
						if (numIterations > 0) {
							result += val.getValueRelative(loopPath) * numIterations;
						}
					}
				}
			}
		}
		doneWithPathBlocks();
		return result;
	}
	
	// --------------------------------------------------------------------- //
	// Absolute statistics
	// --------------------------------------------------------------------- //	
	interface IntegerAbsolute { 
		int getValueAbsolute(Block block); 
		int getValueRelative(Block block); 
	}
	interface DoubleAbsolute { 
		double getValueAbsolute(Block block); 
		double getValueRelative(Block block); 
	}

	private double getTimeTakenAbsolute(boolean recurse) {
		return getAbsoluteDouble(recurse, new DoubleAbsolute() {
			public double getValueAbsolute(Block block) { return block.getTimeTakenAbsolute(true); }
			public double getValueRelative(Block block) { return block.getTimeTakenRelative(false); }});
	}

	private int getNumCyclesAbsolute(boolean recurse) {
		return getAbsoluteInteger(recurse, new IntegerAbsolute() {
			public int getValueAbsolute(Block block) { return block.getNumCyclesAbsolute(true); }
			public int getValueRelative(Block block) { return block.getNumCyclesRelative(false); }});
	}
	
	private int getNumInstructionsAbsolute(boolean recurse) {
		return getAbsoluteInteger(recurse, new IntegerAbsolute() {
			public int getValueAbsolute(Block block) { return block.getNumInstructionsAbsolute(true); }
			public int getValueRelative(Block block) { return block.getNumInstructionsRelative(false); }});
	}

	private int getNumFNopsAbsolute(boolean recurse) {
		return getAbsoluteInteger(recurse, new IntegerAbsolute() {
			public int getValueAbsolute(Block block) { return block.getNumFNopsAbsolute(true); }
			public int getValueRelative(Block block) { return block.getNumFNopsRelative(false); }});
	}

	private int getNumPausablesAbsolute(boolean recurse) {
		return getAbsoluteInteger(recurse, new IntegerAbsolute() {
			public int getValueAbsolute(Block block) { return block.getNumPausablesAbsolute(true); }
			public int getValueRelative(Block block) { return block.getNumPausablesRelative(false); }});
	}

	private int getNumUnknownsAbsolute(boolean recurse) {
		return getAbsoluteInteger(recurse, new IntegerAbsolute() {
			public int getValueAbsolute(Block block) { return block.getNumUnknownsAbsolute(true); }
			public int getValueRelative(Block block) { return block.getNumUnknownsRelative(false); }});
	}
	
	private double getAbsoluteDouble(boolean recurse, DoubleAbsolute val) {
		double result = 0.0;
		for (Block block : getPathBlocks()) {
			if (recurse) {
				for (Block loopPath : block.getLoopPaths()) {
					result += val.getValueAbsolute(loopPath);
					PathAttributes attributes = block.getLoopPathAttributes(loopPath);
					if (attributes.isAbsolute()) {
						int numIterations = attributes.getNumIterations();
						if (numIterations > 0) {
							result += val.getValueRelative(loopPath) * numIterations;
						}
					}
				}
			}
		}
		doneWithPathBlocks();
		return result;
	}

	private int getAbsoluteInteger(boolean recurse, IntegerAbsolute val) {
		int result = 0;
		for (Block block : getPathBlocks()) {
			if (recurse) {
				for (Block loopPath : block.getLoopPaths()) {
					result += val.getValueAbsolute(loopPath);
					PathAttributes attributes = block.getLoopPathAttributes(loopPath);
					if (attributes.isAbsolute()) {
						int numIterations = attributes.getNumIterations();
						if (numIterations > 0) {
							result += val.getValueRelative(loopPath) * numIterations;
						}
					}
				}
			}
		}
		doneWithPathBlocks();
		return result;
	}
	
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
		return m_unknowns.size();
	}

	
	// --------------------------------------------------------------------- //
	// 'Loops only' statistics
	// --------------------------------------------------------------------- //		
	interface IntegerLoops { 
		int getValue(Block block); 
	}
	interface DoubleLoops { 
		double getValue(Block block); 
	}
	
	public double getTimeTakenLoopsOnly() {	
		return getLoopsOnlyDouble(new DoubleLoops() {
			public double getValue(Block block) { return block.getTimeTaken(true); }});
	}
	
	public int getNumCyclesLoopsOnly() {	
		return getLoopsOnlyInteger(new IntegerLoops() {
			public int getValue(Block block) { return block.getNumCycles(true); }});
	}

	public int getNumInstructionsLoopsOnly() {	
		return getLoopsOnlyInteger(new IntegerLoops() {
			public int getValue(Block block) { return block.getNumInstructions(true); }});
	}

	public int getNumFNopsLoopsOnly() {	
		return getLoopsOnlyInteger(new IntegerLoops() {
			public int getValue(Block block) { return block.getNumFNops(true); }});
	}

	public int getNumPausablesLoopsOnly() {	
		return getLoopsOnlyInteger(new IntegerLoops() {
			public int getValue(Block block) { return block.getNumPausables(true); }});
	}
	
	public int getNumUnknownsLoopsOnly() {	
		return getLoopsOnlyInteger(new IntegerLoops() {
			public int getValue(Block block) { return block.getNumUnknowns(true); }});
	}
	    
	private double getLoopsOnlyDouble(DoubleLoops val) {	
        double result = 0.0;
        for (Block loopPath : getLoopPaths()) {
            int numIterations = getLoopPathAttributes(loopPath).getNumIterations();
            result += val.getValue(loopPath) * numIterations;
        }
        return result;
	}

    private int getLoopsOnlyInteger(IntegerLoops val) {
        int result = 0;
        for (Block loopPath : getLoopPaths()) {
            int numIterations = getLoopPathAttributes(loopPath).getNumIterations();
            result += val.getValue(loopPath) * numIterations;
        }
        return result;
    }
    
    private void dumpState(MachineState state) throws Exception {
		Registers reg = state.getResources().getThread(m_currentThread).getRegisters();
		
		for(int i=0;i<4;i++){
			String s = "";
			for(int j=0;j<4;j++){
				
				s+= "reg " + (i*4 +j) + " ";
				if(reg.getRegisters()[i*4 +j].canResolve()){
					
					s+= "0x" + String.format("%8x", reg.getRegisters()[i*4 +j].getValue()) +" ";
				} else {
					s+= "-----------";
				}
				s+="\t";
				
			}
			System.out.println(s);
		}
		return;
		
	}

}
