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

	static enum Mode {
		FUNCTION, ABSOLUTE
	}
	
	private ArrayList<String> m_labels;

	private BlockCommonComponents m_bc;

	private Block m_parentBlock;
	private Route m_route;

	private int m_id;
	private int m_instanceId;

	private MachineState m_machineState;
	private Vector<XtaInstruction> m_instructions;

	private HashMap<Block, PathAttributes> m_loopPathAttributes;
	private ArrayList<Block> m_loopPaths;

	private int m_toPc;
	private BlockStatus m_status;
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
	private int m_startTime;
	private int m_endTime;

	private ExecutionState m_executionState;

	private static int m_numBlocks;

	/*
	 * this is the initial constructor for a path - it only gets called once per
	 * route analysis
	 */
	public Block(BlockCommonComponents bc, int fromPc, int toPc, MachineState machineState, ExecutionState executionState) {
		if (DEBUG)
			System.out.println("\tstarting original block 0");

		m_numBlocks = 0;
		initialise(machineState);
		m_labels= new ArrayList<String>(); 
		m_bc = bc;
		m_status = BlockStatus.SEARCHING;
		m_contexts.add(new TreeSet<Integer>());
		m_startTime = 0;
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
	}

	Block(Block blockToSplit, int pcHit) {
		m_bc = blockToSplit.m_bc;
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
		blockToSplit.m_labels.clear();
		m_labels = new ArrayList<String>();
		
		for(XtaInstruction inst : blockToSplit.m_instructions){
			String label = m_bc.getEngine().getLabel(inst.getPc());
			if(label != null){
				blockToSplit.m_labels.add(label);
			}
		}
		for(XtaInstruction inst : m_instructions){
			String label = m_bc.getEngine().getLabel(inst.getPc());
			if(label != null){
				m_labels.add(label);
			}
		}

		updateInstructionInfo();
		blockToSplit.updateInstructionInfo();

		m_id = blockToSplit.m_id;
		m_instanceId = blockToSplit.m_instanceId;
		blockToSplit.m_id = m_numBlocks;
		blockToSplit.m_instanceId = blockToSplit.m_id;
		++m_numBlocks;

		m_status = blockToSplit.m_status;
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
		m_labels= new ArrayList<String>(); 
		m_status = block.m_status;
		m_speculative = block.m_speculative;
		m_parentBlock = block;
		m_bc = block.m_bc;
		m_status = BlockStatus.SEARCHING;

		Iterator<TreeSet<Integer>> iter = m_parentBlock.m_contexts.iterator();
		while (iter.hasNext()) {
			TreeSet<Integer> pcs = iter.next();
			TreeSet<Integer> copy = new TreeSet<Integer>();
			copy.addAll(pcs);
			m_contexts.add(copy);
		}
		m_startTime = block.m_endTime; // plus a bit maybe
		m_currentThread = block.m_currentThread;
		m_firstStep = false;
		m_seen = block.m_seen;
		m_toPc = block.m_toPc;
		m_route = block.m_route;
		m_executionState = executionState;
		m_functionDepth = block.m_functionDepth;
		m_numFNops = 0;
		m_numInstructions = 0;

		if (DEBUG)
			System.out.println("\tstarted new block " + m_id);
		m_numPausables = 0;
	}

	public boolean canUnpause() {
		return false;
	}

	private void initialise(MachineState machineState) {
		m_id = m_numBlocks++;
		m_instanceId = m_id;
		m_contexts = new Stack<TreeSet<Integer>>();
		m_pcs = new HashSet<Integer>();
		m_instructions = new Vector<XtaInstruction>();
		m_loopPathAttributes = new HashMap<Block, PathAttributes>();
		m_loopPaths = new ArrayList<Block>();
		m_unknowns = new ArrayList<Unknown>();
		m_machineState = machineState;
		m_unknowns = new ArrayList<Unknown>();
		m_endTime = 0;
	}

	private void updateInstructionInfo() {
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

	public int compareTo(Block path) {
		return (int) (path.getTimeTaken(true) - getTimeTaken(true));
	}

	static void resetNumPaths() {
		m_numBlocks = 0;
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

	public void build() {

		int pc = m_machineState.getPc(m_currentThread);
		if (isExcluded(pc)) {
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

		XtaInstruction instruction = m_bc.getInstructionDecodeCache().decode(opcode, pc);
		// if(DEBUG) System.out.println("\ntrying: " + instruction);
		int instNumBytes = instruction.getNumBytes();

		handleIBuffer(instNumBytes, pc);
		if (instruction.canPause()) {
			addUnknown(instruction);
		}

		instruction.execute(m_machineState, m_currentThread);
		addInstruction(instruction);

		if (isComplete(instruction, pc)) {
			setStatus(BlockStatus.COMPLETE);
			m_route.addCompletePath(this);
			return;
		}

		if(instruction.canPause()){
			addUnknown(instruction);
		}
		
		m_contexts.peek().add(pc);

		int nextPc = pc + instNumBytes;
		if (m_machineState.wasEventWait()) {
			if (m_machineState.wasEventResolved()) {
				for (int targetVector : m_machineState.getEventTarget()) {
					checkForLoopFormation(targetVector, false, instruction, true, true);
				}
				setStatus(BlockStatus.COMPLETE);
				releaseMemory();
				return;
			}
		} else if (m_machineState.wasJump()) {
			if (m_machineState.wasJumpResolved()) {
				int jumpTarget = m_machineState.getJumpTarget();
				if (instruction.getBranchLink() && isRecursion(jumpTarget)) {
					setStatus(BlockStatus.RECURSION);
					releaseMemory();
					m_route.addUnresolvedPath(this);
					return;
				} else {
					if (instruction.getReturn()) {
						if (DEBUG)
							System.out.println("pop");
						popFunction();
					}
					checkForLoopFormation(jumpTarget, false, instruction, true, false);
					if (instruction.getBranchLink()) {
						if (DEBUG)
							System.out.println("push");
						pushFunction(pc);
					}
					return;
				}
			} else {
				if (instruction.getReturnLocations().size() > 0) {
					for (XtaInstruction callingInst : instruction.getReturnLocations()) {
						checkForLoopFormation(callingInst.getPc() + callingInst.getNumBytes(), false, callingInst, true, true);
					}
					setStatus(BlockStatus.COMPLETE);
					releaseMemory();
					return;
				} else {
					setStatus(BlockStatus.UNRESOLVED);
					m_route.addUnresolvedPath(this);
					releaseMemory();
					return;
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
				return;
			} else {
				setStatus(BlockStatus.UNRESOLVED);
				m_route.addUnresolvedPath(this);
				m_machineState = null;
				return;
			}
		} else {
			checkForLoopFormation(nextPc, false, instruction, false, false);
		}

		m_firstStep = false;
		instruction.setNumIBufferBytes(m_machineState.getNumIBufferBytes(m_currentThread));
	}

	private void releaseMemory(){
		m_machineState.releaseMemory();
		m_machineState = null;
		m_contexts.clear();
		this.m_contexts = null;
	}
	
	
	private void addUnknown(XtaInstruction inst) {
		Unknown unknown;
		if (inst.canPause()) {
			unknown = new Unknown(inst);
		} else {
			unknown = new Unknown(inst, m_machineState.getGuardExpression(), m_machineState.getBranchTargetExpression());
		}
		m_unknowns.add(unknown);
		m_route.addUnknown(unknown);
	}

	private Expression previousGuard;
	private int previousPc;

	private void checkForLoopFormation(int nextPc, boolean speculative, XtaInstruction inst, boolean isJump, boolean isBranch) {
		Block loopBlock = null;
		m_speculative = m_speculative | speculative;
		if (m_speculative) {

			loopBlock = formsLoop(nextPc);
		}
		if (loopBlock != null) {
			if (loopBlock.getInstructionsThis().get(0).getPc() != nextPc) {
				new Block(loopBlock, nextPc);
			}
			loopBlock.addLoopPath(this);
			this.setStatus(BlockStatus.COMPLETE);
		} else {
			if (isBranch) {
				MachineState machineState = new MachineState(m_machineState);
				machineState.branchTo(nextPc, inst.isLoadStore(), m_currentThread);
				new ExecutionState(m_executionState, machineState);
			} else if (isJump) {
				m_machineState.branchTo(nextPc, inst.isLoadStore(), m_currentThread);
			} else {
				m_machineState.setPc(nextPc, m_currentThread);
				m_machineState.issue(inst.getNumBytes(), m_currentThread);
				if (!inst.isLoadStore()) {
					m_machineState.fetch(m_currentThread);
				}
			}
		}
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

	private boolean isExcluded(int pc) {
		for (Reference reference : m_bc.getEngine().getReference(ReferenceMode.PROHIBITED)) {
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

		if (DEBUG)
			System.out.println("adding: " + inst + " to Block " + m_instanceId);
		String label = m_bc.getEngine().getLabel(inst.getPc());
		if(label!= null){
			this.m_labels.add(label);
		}
		
		inst.setBasicNumCycles(m_bc.getM_instructionAttributes().getTiming(inst.getNmenonic()));
		inst.setConfig(m_bc.getConfig());

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
			XtaInstruction fNop = m_bc.getInstructionDecodeCache().getFNop(pc, 0);
			addInstruction(fNop);
			m_machineState.fetch(m_currentThread);
			fNop.setNumIBufferBytes(m_machineState.getNumIBufferBytes(m_currentThread));

			// May require two FNOPs if fetching a mis-aligned long
			if (m_machineState.canIssue(instNumBytes, m_currentThread) == false) {
				fNop = m_bc.getInstructionDecodeCache().getFNop(pc, 1);
				addInstruction(fNop);
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

	public int getInstanceId() {
		return m_instanceId;
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
	
	public void getPathLabels(boolean recurse, ArrayList<String> labels) {
		for (Block block : getPathBlocks()) {
			labels.addAll(block.getLabels());
			if (recurse) {
				for (Block loopPath : block.getLoopPaths()) {
					loopPath.getPathLabels(true, labels);
				}
			}
		}
		doneWithPathBlocks();
		return;
	}
	
	public ArrayList<String> getLabels() {
		return m_labels;
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
	double getTimeTakenRelative(boolean recurse) {
		double time = 0.0;

		for (Block block : getPathBlocks()) {
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
		doneWithPathBlocks();
		return time;
	}

	int getNumInstructionsRelative(boolean recurse) {
		int num = 0;

		for (Block block : getPathBlocks()) {
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
		doneWithPathBlocks();
		return num;
	}

	int getNumFNopsRelative(boolean recurse) {
		int num = 0;

		for (Block block : getPathBlocks()) {
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
		doneWithPathBlocks();
		return num;
	}
	
	int getNumCyclesRelative(boolean recurse) {
		int num = 0;

		for (Block block : getPathBlocks()) {
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
		doneWithPathBlocks();
		return num;
	}

	int getNumPausablesRelative(boolean recurse) {
		int num = 0;

		for (Block block : getPathBlocks()) {
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
		doneWithPathBlocks();
		return num;
	}

	int getNumUnknownsRelative(boolean recurse) {
		int num = 0;

		for (Block block : getPathBlocks()) {
			num += block.getNumUnknownsThis();

			if (recurse) {
				for (Block loopPath : block.getLoopPaths()) {
					PathAttributes attributes = block.getLoopPathAttributes(loopPath);
					if (!attributes.isAbsolute()) {
						int numIterations = attributes.getNumIterations();
						if (numIterations > 0) {
							num += loopPath.getNumUnknownsRelative(true) * numIterations;
						}
					}
				}
			}
		}
		doneWithPathBlocks();
		return num;
	}
	
	// --------------------------------------------------------------------- //
	// Absolute statistics
	// --------------------------------------------------------------------- //	
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
		doneWithPathBlocks();
		return time;
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
		doneWithPathBlocks();
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
		doneWithPathBlocks();
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
		doneWithPathBlocks();
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
		doneWithPathBlocks();
		return num;
	}

	int getNumUnknownsAbsolute(boolean recurse) {
		int num = 0;

		for (Block block : getPathBlocks()) {
			if (recurse) {
				for (Block loopPath : block.getLoopPaths()) {
					num += loopPath.getNumUnknownsAbsolute(true);

					PathAttributes attributes = block.getLoopPathAttributes(loopPath);
					if (attributes.isAbsolute()) {
						int numIterations = attributes.getNumIterations();
						if (numIterations > 0) {
							num += loopPath.getNumUnknownsRelative(false) * numIterations;
						}
					}
				}
			}
		}
		doneWithPathBlocks();
		return num;
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

	int getNumInstructionsThis() {
		return m_numInstructions;
	}

	public int getNumFNopsThis() {
		return m_numFNops;
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
	
	public int getNumUnknownsThis() {
		return m_unknowns.size();
	}
	
	public ArrayList<Integer> getAllReferencePcs() {
		ArrayList<String> labels  = new ArrayList<String>();
       getPathLabels(false, labels);
		ArrayList<Integer> references = new ArrayList<Integer>();
		for(String label : labels){
			Reference ref  = new Reference(label);
			ref.resolve(m_bc.getEngine());
			references.add(ref.getPc());
		}
		return references;
	}

	
	// --------------------------------------------------------------------- //
	// 'Loops only' statistics
	// --------------------------------------------------------------------- //		
	public double getTimeTakenLoopsOnly() {	
        double timeTaken = 0.0;
        for (Block loopPath : getLoopPaths()) {
            int numIterations = getLoopPathAttributes(loopPath).getNumIterations();
            timeTaken += loopPath.getTimeTaken(true) * numIterations;
        }
        return timeTaken;
	}

	public int getNumInstructionsLoopsOnly() {	
        int num = 0;
        for (Block loopPath : getLoopPaths()) {
            int numIterations = getLoopPathAttributes(loopPath).getNumIterations();
            num += loopPath.getNumInstructions(true) * numIterations;
        }
        return num;
	}
	
    public int getNumFNopsLoopsOnly() {
        int num = 0;
        for (Block loopPath : getLoopPaths()) {
            int numIterations = getLoopPathAttributes(loopPath).getNumIterations();
            num += loopPath.getNumFNops(true) * numIterations;
        }
        return num;
    }
	
    public int getNumCyclesLoopsOnly() {
        int num = 0;
        for (Block loopPath : getLoopPaths()) {
            int numIterations = getLoopPathAttributes(loopPath).getNumIterations();
            num += loopPath.getNumCycles(true) * numIterations;
        }
        return num;
    }
    	
    public int getNumPausablesLoopsOnly() {
        int num = 0;
        for (Block loopPath : getLoopPaths()) {
            int numIterations = getLoopPathAttributes(loopPath).getNumIterations();
            num += loopPath.getNumPausables(true) * numIterations;
        }
        return num;
    }
	
    public int getNumUnknownsLoopsOnly() {
        int num = 0;
        for (Block loopPath : getLoopPaths()) {
            int numIterations = getLoopPathAttributes(loopPath).getNumIterations();
            num += loopPath.getNumUnknowns(true) * numIterations;
        }
        return num;
    }
}
