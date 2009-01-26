package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader.Architecture;
import com.xmos.xta.engine.Engine.ReferenceMode;
import com.xmos.xta.engine.Status.ExecutionStatus;
import com.xmos.xta.engine.references.CodeReference;

public class Route {

	enum RouteStatus {
		COMPLETED, ABORTED, ERROR, ANALYSING
	}

	private Config m_config;
	private int m_nodeId;
	private int m_coreId;
	private CodeReference m_fromRef;
	private CodeReference m_toRef;
	private String m_functionName;
	private HashMap<Integer, Vector<Block>> m_loopPaths;
	private ArrayList<ExecutionState> m_executionStates;
	private LinkedList<ExecutionState> m_schedule;
	private ArrayList<Unresolved> m_unresolvedPaths;
	private ArrayList<Block> m_completePaths;
	private ArrayList<Block> m_removedPaths;
	private ArrayList<Block> m_allBlocks;
	private Engine m_engine;
	private RouteStatus m_status;
	private Progress m_progress;
	private NodeCoreInstructionCaches m_nodeCoreInstructionCache;
	private ArrayList<Unknown> m_unknowns;
	private ArrayList<String> m_warnings;
	private static int m_numRoutes;
	private InstructionAttributes m_instructionAttributes;
	private boolean m_resolveExpressions;

	Route(Config config, Progress progress, CodeReference fromRef, CodeReference toRef,
			int nodeId, int coreId, NodeCoreInstructionCaches nodeCoreInstructionCache,
			boolean resolveExpressions) {
		m_progress = progress;
		m_fromRef = fromRef;
		m_toRef = toRef;
		m_resolveExpressions = resolveExpressions;
		m_nodeCoreInstructionCache = nodeCoreInstructionCache;
		initialise(config, nodeId, coreId);

	}

	Route(Config config, Progress progress, String functionName,
			int nodeId, int coreId, NodeCoreInstructionCaches nodeCoreInstructionCache,
			boolean resolveExpressions) {
		m_progress = progress;
		m_functionName = functionName;
		m_nodeCoreInstructionCache = nodeCoreInstructionCache;
		m_resolveExpressions = resolveExpressions;
		initialise(config, nodeId, coreId);
	}

	public ArrayList<Unknown> getUnknowns() {
		return m_unknowns;
	}

	public final String functionName() {
		return m_functionName;
	}

	public final int getNodeId() {
		return m_nodeId;
	}

	public final int getCoreId() {
		return m_coreId;
	}

	public final CodeReference fromRef() {
		return m_fromRef;
	}

	public final CodeReference toRef() {
		return m_toRef;
	}

	public final ArrayList<Block> completePaths() {
		return m_completePaths;
	}

	public final ArrayList<Unresolved> unresolvedPaths() {
		return m_unresolvedPaths;
	}

	RouteStatus analyse(MemoryImage memoryImage, SymbolTable symbolTable, 
			DebugInfo debugInfo, Engine engine) throws Exception {
		m_progress.resetState(Progress.State.PROCESSING_PATHS, m_config.m_minimumRemainingMemory);

		assert (memoryImage != null);
		assert (symbolTable != null);
		assert (debugInfo != null);
		m_engine = engine;
		m_status = RouteStatus.ANALYSING;

		m_instructionAttributes = new InstructionAttributes();

		m_engine.setAbort(false);

		Stack<TreeSet<Integer>> context = new Stack<TreeSet<Integer>>();
		context.push(new TreeSet<Integer>());
		Architecture arch = m_engine.getXeLoader().getArchitecture() ;
		MachineState initialState = new MachineState(symbolTable, memoryImage, arch);
		
		BlockCommonComponents bc = null;

		if (m_functionName != null) {
			bc = new BlockCommonComponents(m_config, m_nodeCoreInstructionCache, m_instructionAttributes, memoryImage, symbolTable, 
					m_engine.getReference(ReferenceMode.PROHIBITED), this, m_resolveExpressions, Block.Mode.FUNCTION, m_engine);
			assert (symbolTable.symbolExists(m_functionName));
			int fromPc = symbolTable.symbolAddress(m_functionName);
			new ExecutionState(this, initialState, bc, fromPc);
		} else {
			bc = new BlockCommonComponents(m_config, m_nodeCoreInstructionCache, m_instructionAttributes, memoryImage, symbolTable, 
					m_engine.getReference(ReferenceMode.PROHIBITED), this, m_resolveExpressions, Block.Mode.ABSOLUTE, m_engine);
			new ExecutionState(this, initialState, bc, m_fromRef.getPc(), m_toRef.getPc());
		}
		stepPath();
		filterPaths();
		sortPaths();
		m_progress.setState(Progress.State.IDLE);
		return m_status;
	}

	public int getId() {
		return 0;
	}

	public int getInstanceId() {
		return 0;
	}

	public int getNumCycles() {
		int maxCycles = 0;
		for (Block path : m_completePaths) {
			maxCycles = Math.max(path.getNumCycles(true), maxCycles);
		}
		return maxCycles;
	}

	public double getTimeTaken() {
		return 0;
	}

	void notifyUpdate() {
		Collections.sort(m_completePaths);
		m_engine.callDisplayers(Displayer.NOTIFY_UPDATE_STATE, "");
	}

	Progress getProgress() {
		return m_progress;
	}

	Config getConfig() {
		return m_config;
	}

	boolean setInstructionTimeTaken(int pc, double timeTaken) {
		return m_nodeCoreInstructionCache.setInstructionTimeTaken(m_engine, pc, timeTaken, m_nodeId, m_coreId);
	}

	double getInstructionTimeTaken(int pc) {
		return m_nodeCoreInstructionCache.getInstructionTimeTaken(pc, m_nodeId, m_coreId);
	}

	boolean setInstructionNumCycles(int pc, int numCycles) {
		for (Unknown unknown : m_unknowns) {
			if (unknown.getPc() == pc) {
				m_unknowns.remove(unknown);
				m_engine.callDisplayers(Displayer.NOTIFY_UPDATE_STATE, "");
				break;
			}
		}
		return m_nodeCoreInstructionCache.setInstructionNumCycles(m_engine, pc, numCycles, m_nodeId, m_coreId);
	}

	int getInstructionNumCycles(int pc) {
		return m_nodeCoreInstructionCache.getInstructionNumCycles(pc, m_nodeId, m_coreId);
	}

	private void initialise(Config config, int nodeId, int coreId) {
		m_unknowns = new ArrayList<Unknown>();
		m_warnings = new ArrayList<String>();
		m_config = config;
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_completePaths = new ArrayList<Block>();
		m_unresolvedPaths = new ArrayList<Unresolved>();
		m_removedPaths = new ArrayList<Block>();
		m_allBlocks = new ArrayList<Block>();
		m_loopPaths = new HashMap<Integer, Vector<Block>>();
		m_executionStates = new ArrayList<ExecutionState>();
		m_schedule = new LinkedList<ExecutionState>();
		m_status = RouteStatus.COMPLETED;
		++m_numRoutes;
	}

	public void addUnresolvedPath(Unresolved unresolved) {
		m_unresolvedPaths.add(unresolved);
	}

	public RouteStatus getStatus(){
		return m_status;
	}
	
	private void stepPath() throws Exception {
		ExecutionState searching = nextBlock();
		while (searching != null && (m_status != RouteStatus.ABORTED )) {
			searching.build();
			searching = nextBlock();
			cleanSchedule();
			checkAbort();
		}
	}


	private void cleanSchedule() {
		
		Iterator<ExecutionState> shedIter = m_schedule.iterator();
	    while ( shedIter.hasNext() ){
	    	ExecutionState e = shedIter.next();
	    	if (e.getStatus() == ExecutionStatus.UNRESOLVED 
	    			|| e.getStatus() == ExecutionStatus.COMPLETE) {
	    		shedIter.remove();
			}
	    }
	}

	private ExecutionState nextBlock() {
		for (ExecutionState executionState : m_schedule) {
			if (executionState.getStatus() == ExecutionStatus.SEARCHING) {
				return executionState;
			}
		}
		return null;
	}

	private void sortPaths() {
		Collections.sort(m_completePaths);
		for(Block block : m_allBlocks){
			Collections.sort(block.getLoopPaths());
		}
	}

	public void addUnknown(Unknown unknown) {
		m_unknowns.add(unknown);
	}

	public void addWarning(String warning) {
		m_warnings.add(warning);
	}

	public boolean checkAbort() {
		if (m_engine.getAbort()) {
			m_status = RouteStatus.ABORTED;
			return true;
		}
		return false;
	}

	
	public void addCompletePath(Block endBlock) {
		m_completePaths.add(endBlock);
	}
	
	public void filterPaths() {
		
		//TODO
		ArrayList<Block> toRemove = new ArrayList<Block>();
		
		ArrayList<Integer> exclusions = new ArrayList<Integer>();
		for(CodeReference ref : m_engine.getReference(ReferenceMode.FILTERED)){
			exclusions.add(ref.getPc());
		}
		ArrayList<Integer> required = new ArrayList<Integer>();
		for(CodeReference ref : m_engine.getReference(ReferenceMode.REQUIRED)){
			required.add(ref.getPc());
		}
		m_completePaths.addAll(m_removedPaths);
		m_removedPaths.clear();
		
		for(Block completePath : m_completePaths){
			
			ArrayList<Integer> allPathPcs = completePath.getAllPcs();

			for(int req : required){
				if(!allPathPcs.contains(req)){
					toRemove.add(completePath);
					break;
				}
			}

			for(int exc : exclusions){
				if(allPathPcs.contains(exc)){
					toRemove.add(completePath);
					break;
				}
			}

		}
		
		for(Unknown unknown : m_unknowns){
			if(exclusions.contains(unknown.getPc())){
				unknown.setShow(false);
			} else {
				unknown.setShow(true);
			}
		}
		
		m_completePaths.removeAll(toRemove);
		m_removedPaths.addAll(toRemove);
	}
	
	
	public void addLoopPath(Block startBlock) {
		assert (startBlock != null);
		int fromPc = startBlock.getInstructions(false).firstElement().getPc();

		Vector<Block> paths = m_loopPaths.get(fromPc);
		if (paths == null) {
			paths = new Vector<Block>();
			m_loopPaths.put(fromPc, paths);
		} else {
			Iterator<Block> iter = paths.iterator();
			while (iter.hasNext()) {
				Block block = iter.next();
				if (block.equivalent(startBlock)) {
					startBlock.setId(block.getId());
					break;
				}
			}
		}
		paths.add(startBlock);
	}

	public void addExecutionState(ExecutionState ex) {
		m_executionStates.add(ex);
		m_schedule.addFirst(ex);
	}

	public void removeExecutionState(ExecutionState ex) {
		assert (m_schedule.contains(ex));
		m_schedule.remove(ex);
	}

	public boolean setLoopIterations(int pathId, boolean absolute, int iterations) {
		boolean hit = false;

		Iterator<ExecutionState> iter = m_executionStates.iterator();
		while (iter.hasNext()) {
			ExecutionState executionState = iter.next();
			for (Block block : executionState.getThreadBlocks()) {
				if (block != null) {
					for (Block loopPath : block.getLoopPaths()) {
						int blockId =  loopPath.getId();
						if (blockId == pathId) {
							PathAttributes attributes = block.getLoopPathAttributes(loopPath);
							attributes.setNumIterations(iterations, absolute);
							hit = true;
							return hit;
						}
					}
				}
			}
		}

		return hit;
	}

	public ArrayList<ExecutionState> getExecutionStates() {
		return m_executionStates;
	}
	
	public void addNewBlock(Block block){
		m_allBlocks.add(block);
		m_progress.setCurValue(m_allBlocks.size());
	}

	public String getStatusString() {
	    if (functionName() != null) {
	    	return String.format("function: %s", functionName());
	    } else {
	    	return String.format("path: %s -> %s",  fromRef().getRef(), toRef().getRef());
	    }
	}
}
