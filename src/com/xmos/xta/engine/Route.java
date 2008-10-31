package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.Block.Mode;

public class Route {  

    enum Status {
		COMPLETED,
		ABORTED,
		ERROR,
		MAX_NUM_PATHS,
	}

    private String m_id;
    private Config m_config;
    private int m_nodeId;
    private int m_coreId;
    private Reference m_fromRef;
    private Reference m_toRef;
    private String m_functionName;
    
    private HashMap<Integer, Vector<Block>> m_loopPaths;  // Hash of start PC to block at that PC
    private Vector<Block>           m_blocks;  // Hash of start PC to block at that PC

    private Vector<Block> m_completePaths;     // Contains all the complete paths
    private Vector<Block> m_unresolvedPaths;   // Contains all the unresolved paths
    private Engine m_engine;
    Status m_status;        
    private Progress m_progress;
    private InstructionDecodeCache m_instructionDecodeCache;
    private ArrayList<Unknown> m_unknowns;
    
    private static int m_numRoutes;  
    
    Route(Config config, Reference fromRef, Reference toRef, int nodeId, int coreId) {
        m_fromRef = fromRef;
        m_toRef = toRef;
        initialise(config, nodeId, coreId);        
    }

    Route(Config config, String functionName, int nodeId, int coreId) {
        m_functionName = functionName;
        initialise(config, nodeId, coreId);        
    }
    
    public void setCurrentValue(int numPaths){
        m_progress.setCurValue(numPaths);
    }
    
    public ArrayList<Unknown> getUnknowns(){
        return m_unknowns;
    }
    
    public final String id() {
        return m_id;
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
    
    public final Reference fromRef() {
        return m_fromRef;
    }

    public final Reference toRef() {
        return m_toRef;
    }
    
    public final Vector<Block> completePaths() {
        return m_completePaths;
    }

    public final Vector<Block> unresolvedPaths() {
        return m_unresolvedPaths;
    }
             
    Status analyse(MemoryImage memoryImage, SymbolTable symbolTable, DebugInfo debugInfo, Engine engine) {    	
    	m_progress.resetState(Progress.State.PROCESSING_PATHS, m_config.m_maxNumPaths);
    	
        assert (memoryImage != null);
        assert (symbolTable != null);
        assert (debugInfo != null);
        m_engine = engine;
        m_status = Status.COMPLETED;
        
        m_instructionDecodeCache = new InstructionDecodeCache(
        		symbolTable, engine.getXeLoader().getArchitecture());        
        InstructionAttributes instructionAttributes = new InstructionAttributes();
        
        Block.resetNumPaths();
        m_engine.setAbort(false);

        Block rootPath;
        Stack<TreeSet<Integer>> context = new Stack<TreeSet<Integer>>();
        context.push(new TreeSet<Integer>());
        MachineState initialState = new MachineState(memoryImage.getMemoryBase(), memoryImage.getMemorySize());
        
        if (m_functionName != null) {
            assert(symbolTable.symbolExists(m_functionName));
            int fromPc = symbolTable.symbolAddress(m_functionName);                

            rootPath = new Block(Mode.FUNCTION, m_config, m_instructionDecodeCache,
                instructionAttributes, memoryImage, symbolTable, m_engine.getExclusions(),
                fromPc, 0, this, null, initialState, context, false);
        }
        else {
            rootPath = new Block(Mode.ABSOLUTE, m_config, m_instructionDecodeCache,
                instructionAttributes, memoryImage, symbolTable, m_engine.getExclusions(),
                m_fromRef.getPc(), m_toRef.getPc(), this, null, initialState, context, false);            
        }
                         
        stepPath(rootPath);
    
        if (m_status == Status.COMPLETED) {
            sortPaths();
            if (!buildCoveredLinesHashes(debugInfo, symbolTable)) {	        	
        	    return m_status;
            }	
        }
        else if (rootPath.getStatus() == Block.Status.ERROR) {
            m_status = Status.ERROR;
        }

        m_progress.resetState(Progress.State.IDLE, 0);   
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
        for(Block path : m_completePaths){
            maxCycles = Math.max(path.getNumCycles(true), maxCycles);
        }
        return maxCycles;
    }

    public double getTimeTaken() {
        return 0;
    }    
    
    void notifyUpdate() {
    	Collections.sort(m_completePaths);
    	m_engine.notifyUpdateState();
    }
        
    Progress getProgress() {
    	return m_progress;
    }
       
    Config getConfig() {
    	return m_config;
    }
    
    boolean setInstructionTimeTaken(int pc, double timeTaken) {
    	return m_instructionDecodeCache.setInstructionTimeTaken(m_engine, pc, timeTaken);
    }

    double getInstructionTimeTaken(int pc) {
    	return m_instructionDecodeCache.getInstructionTimeTaken(pc);
    }
    
    boolean setInstructionNumCycles(int pc, int numCycles) {
    	return m_instructionDecodeCache.setInstructionNumCycles(m_engine, pc, numCycles);
    }

    int getInstructionNumCycles(int pc) {
    	return m_instructionDecodeCache.getInstructionNumCycles(pc);
    }
    
    private void initialise(Config config, int nodeId, int coreId) {
        m_unknowns = new ArrayList<Unknown>();
        m_id = new String("route" + m_numRoutes);   
        m_config = config;
        m_nodeId = nodeId;
        m_coreId = coreId;      
        m_completePaths = new Vector<Block>();
        m_unresolvedPaths = new Vector<Block>();
        m_loopPaths = new HashMap<Integer, Vector<Block>>();
        m_blocks = new Vector<Block>();
        m_status = Status.COMPLETED;
        m_progress = new Progress(Progress.State.IDLE, 0, 0);
        ++m_numRoutes;        
    }    
    
    private void stepPath(Block path) {      
        while (path.getStatus() == Block.Status.SEARCHING) {
            if (checkAbort()) {
            	break;
            }
            path.step();
            if (Block.getNumPaths() >= m_config.m_maxNumPaths) {
                m_status = Status.MAX_NUM_PATHS;                
                break;
            }
            if (path.getStatus() == Block.Status.ERROR) {
            	m_status = Status.ERROR;
            	break;
            }
            m_progress.setCurValue(Block.getNumPaths());
        }
    }    
  
    private void sortPaths() { 
        Collections.sort(m_completePaths);
        Collections.sort(m_unresolvedPaths);
    }

    private boolean buildCoveredLinesHashes(DebugInfo debugInfo, SymbolTable symbolTable) {    
        for (Block path : m_completePaths) {
            if (checkAbort()) 
            	return false;

            path.buildCoveredLinesHashes(debugInfo, symbolTable);
            m_progress.incr();
        }
        return true;
    }
    
    public void addUnknown(Unknown unknown){
        m_unknowns.add(unknown);
    }
    
    public boolean checkAbort() {
    	if (m_engine.getAbort()) {    		
    		m_status = Status.ABORTED;
    		return true;
    	}
    	return false;
    }

    public void addCompletePath(Block endBlock) {
        assert(endBlock != null);
        /*
         * can only add the null parent in the case when there is only one 
         * straight line from beginning to end of the route
         */
        assert(m_completePaths != null);
        if (m_completePaths.size() > 0) assert(endBlock.getParentPath() != null);
        assert(endBlock.getInstructionsThis()!= null);
        assert(endBlock.getInstructionsThis().size()>0);
        m_completePaths.add(endBlock);
    }

    public void addLoopPath(Block startBlock) {
        assert(startBlock != null);
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

    public void addBlock(Block block) {
        m_blocks.add(block);
    }

    public void addUnresolvedPath(Block path){
        assert(path != null);
        /*
         * can only add the null parent in the case when there is only one 
         * straight line from beginning to end of the route
         */
        assert(m_unresolvedPaths != null);
        if (m_unresolvedPaths.size() > 0) assert(path.getParentPath() != null);
        assert(path.getInstructionsThis()!= null);
        assert(path.getInstructionsThis().size()>0);
        m_unresolvedPaths.add(path);
    }
    
    public boolean setLoopIterations(int pathId, boolean allInstances, boolean absolute, int iterations) {
        boolean hit = false;

        Iterator<Block> iter = m_blocks.iterator();
        while (iter.hasNext()) {
            Block block = iter.next();
            for (Block loopPath : block.getLoopPaths()) {
                int blockId = allInstances ? loopPath.getId() : loopPath.getInstanceId();              
                if (blockId == pathId) {
                    PathAttributes attributes = block.getLoopPathAttributes(loopPath);
                    attributes.setNumIterations(iterations, absolute);
                    hit = true;
                    if (!allInstances) {
                        return hit;
                    }
                }
            }
        }
        
        return hit;
    }
    
    public Vector<Block> getBlocks(){
        return m_blocks;
    }
    
}
