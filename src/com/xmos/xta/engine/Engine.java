package com.xmos.xta.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.xmos.dwarfhandler.DebugEngine;
import com.xmos.elfhandler.ElfHandler;
import com.xmos.elfhandler.Symbol;
import com.xmos.xehandler.XEHandler;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;
import com.xmos.xeobject.Function;
import com.xmos.xeobject.Line;
import com.xmos.xeobject.XeObject;

public class Engine { 

	private static final boolean DEBUG = true;
	
    private XeLoader m_xeLoader;
    private XeObject m_xeObject;
    private Vector<File> m_sourceFiles;
    private Route m_route;
    private Route m_previousRoute;
    private Config m_config;
    private String m_executableFile;
    private int m_nodeId;
    private int m_coreId;
    private String m_errorString;
    private Vector<Displayer> m_displayers;
    private Vector<Reference> m_exclusions;
    private boolean m_abort;
    private InstructionDecodeCache m_instructionDecodeCache;
    private boolean m_resolveExpression;
    private HashMap<Integer, String> m_labels;
    
    public Engine(Config config) {
        m_xeLoader = new XeLoader();
        m_xeObject = null;
        m_route = null;
        m_previousRoute = null;
        m_sourceFiles = new Vector<File>();
        m_config = config;
        m_executableFile = "";
        m_nodeId = 0;
        m_coreId = 0;
        m_errorString = "";
        m_displayers = new Vector<Displayer>();
        m_exclusions = new Vector<Reference>();
        m_abort = false;
        m_resolveExpression = false;
        m_labels = new HashMap<Integer, String>();
    }

    public Config getConfig() {
        return m_config;
    }

    public String getExecutable() {
        return m_xeLoader.getExecutable();
    }	

    public Vector<MemoryImage> getMemoryImages() {
        return m_xeLoader.getMemoryImages();
    }

    public Vector<SymbolTable> getSymbolTables() {
        return m_xeLoader.getSymbolTables();
    }

    public Vector<DebugInfo> getDebugInfos(){
        return m_xeLoader.getDebugInfos();
    }

    public Route getRoute(){
        return m_route;
    }

    public XeLoader getXeLoader() {
        return m_xeLoader;
    }	

    public void setXeLoader(XeLoader xeLoader){
        m_xeLoader = xeLoader;
    }

    public void setXeObject(XeObject xeObject){
        m_xeObject = xeObject;
    }

    public XeObject getXeObject(){
        if (m_xeObject == null) {
            m_xeObject = new XeObject(m_xeLoader);
        }
        return m_xeObject;
    }

    public boolean loadExecutable(String executableFile) {
        boolean success = m_xeLoader.loadExecutable(executableFile);
        if (success) {
            m_exclusions.clear();
            if (m_xeObject == null) {
                m_xeObject = new XeObject(m_xeLoader);
            }
            decodeInstructions();
            for (Displayer displayer : m_displayers)
                displayer.notifyLoadExecutable();
            
        } else {
            setErrorString("The file '" + executableFile + "'could not be found.");	        
        }
        
        return success;
    }	

    public boolean loadExecutable(XEHandler xeHandler) {
        boolean success = m_xeLoader.loadExecutable(xeHandler);
        for (Displayer displayer : m_displayers)
            displayer.notifyLoadExecutable();
        return success;
    }

    public Progress getProgress() {    	
        if (m_route == null) {
            return new Progress(Progress.State.IDLE, 0, 0);
        }    	
        return m_route.getProgress();   
    }

    public boolean analyseRoute(String functionName, boolean resolveExpression) {
    	resolveExpression = resolveExpression| m_resolveExpression;
        if (!m_xeLoader.isLoaded(m_nodeId, m_coreId)) {
            setErrorString(String.format("Node: %d, Core: %d image not found in file: %s",
                    m_nodeId, m_coreId, m_executableFile));        	
            return false;
        }
        else if (!functionExists(functionName)) {
            setErrorString(String.format("Cannot find function: %s in file: %s, node: %d, core: %d",
                    functionName, m_executableFile, m_nodeId, m_coreId));        	
            return false;
        }
        else {     
            m_previousRoute = m_route;
            m_route = new Route(m_config, functionName, m_nodeId, m_coreId, m_instructionDecodeCache, resolveExpression);
            return analyseRoute();
        }
    }
    public Vector<Reference> getExclusions() {
        return m_exclusions;
    }

    public boolean setExclusion(Reference ref) {
        m_exclusions.add(ref);
        return true;
    }

    public boolean setExclusion(String refStr) {
        Reference ref = new Reference(refStr);

        if (!m_xeLoader.isLoaded()) {
            setErrorString("No executable file loaded");
            return false;
        }
        else if (!resolveRefernece(ref)) {
            setErrorString(String.format("Unresolved reference: %s", refStr));
            return false;
        }

        m_exclusions.add(ref);
        return true;
    }

    public boolean removeExclusion(Reference ref) {
        for(Reference present : m_exclusions){
            if(present.getPc() == ref.getPc()){
                m_exclusions.remove(present);
                return true;
            }
        }
        return false;
    }

    public boolean removeExclusion(String refStr){
        for(Reference present : m_exclusions){
            if(present.getRef().equals(refStr)){
                m_exclusions.remove(present);
                return true;
            }
        }
        return false;
    }
    public boolean removeAllExclusions(){
        m_exclusions.clear();
        return true;
    }

    public boolean analyseRoute(String from, String to, boolean resolveExpression) {
        Reference fromRef = new Reference(from);
        Reference toRef = new Reference(to);     
        resolveExpression = resolveExpression| m_resolveExpression;
        if (!m_xeLoader.isLoaded(m_nodeId, m_coreId)) {
            setErrorString(String.format("Node: %d, Core: %d image not found in file: %s",
                    m_nodeId, m_coreId, m_executableFile));        	
            return false;
        }
        else if (!resolveRefernece(fromRef)) {
            setErrorString(String.format("Unresolved reference: %s", from));
            return false;
        }
        else if (!resolveRefernece(toRef)) {
            setErrorString(String.format("Unresolved reference: %s", from));
            return false;
        }
        else {    
            m_previousRoute = m_route;                   
            m_route = new Route(m_config, fromRef, toRef, m_nodeId, m_coreId, m_instructionDecodeCache, resolveExpression);
            return analyseRoute();
        }
    }

    public void displayRoute() {
        if (m_route != null) { 
            int nodeId = m_route.getNodeId();
            int coreId = m_route.getCoreId();

            MemoryImage memoryImage = m_xeLoader.getMemoryImage(nodeId, coreId);
            SymbolTable symbolTable = m_xeLoader.getSymbolTable(nodeId, coreId);
            DebugInfo debugInfo = m_xeLoader.getDebugInfo(nodeId, coreId);                               

            assert(memoryImage != null);
            assert(symbolTable != null);
            assert(debugInfo != null);

            for (Displayer displayer : m_displayers) {
                displayer.displayRoute(m_route, debugInfo, memoryImage, symbolTable);    	
            }
        }
    }    

    public void displayRoute(Displayer displayer) {
        if (m_route != null) { 
            int nodeId = m_route.getNodeId();
            int coreId = m_route.getCoreId();

            MemoryImage memoryImage = m_xeLoader.getMemoryImage(nodeId, coreId);
            SymbolTable symbolTable = m_xeLoader.getSymbolTable(nodeId, coreId);
            DebugInfo debugInfo = m_xeLoader.getDebugInfo(nodeId, coreId);                               

            assert(memoryImage != null);
            assert(symbolTable != null);
            assert(debugInfo != null);

            displayer.displayRoute(m_route, debugInfo, memoryImage, symbolTable);    	
        }
    }        

    public void registerDisplayer(Displayer displayer) {    	
        m_displayers.add(displayer);
    }

    public void clearDisplayer(Displayer displayer){
        m_displayers.remove(displayer);    			
    }

    public void clearDisplayers(){
        m_displayers.clear();
    }

    public void setAbort(boolean value){
        m_abort = value;
    }

    public boolean getAbort(){
        return m_abort;
    }

    public void setNodeId(int nodeId) {
        m_nodeId = nodeId;
    }

    public int getNodeId(){
        return m_nodeId;
    }

    public void setCoreId(int coreId) {
        m_coreId = coreId;
    }

    public int getCoreId(){
        return m_coreId;
    }

    public void setFrequency(int frequency) {
        m_config.m_frequency = frequency;
        notifyUpdateState();
    }

    public int getFrequency(){
        return m_config.m_frequency;
    }

    public void setNumThreads(int numThreads) {
        m_config.m_numThreads = numThreads;
        notifyUpdateState();
    }

    public int getNumThreads(){
        return m_config.m_numThreads;
    }

    public Vector<File> getFileNames(){
        for(DebugInfo bd : m_xeLoader.getDebugInfos()){
            DebugEngine de = bd.getDebugEngine();
            for (File  fileName : de.findFiles(m_executableFile)){
                if(!m_sourceFiles.contains(fileName)) 
                    m_sourceFiles.add(fileName);
            }
        }
        return m_sourceFiles;
    }

    public String getErrorString() {
        return m_errorString;
    }

    public void setErrorString(String errorString) {
        m_errorString = errorString;
    }

    public boolean setLoopIterations(int pathId, boolean allInstances, boolean absolute, int iterations) {
        boolean hit = m_route.setLoopIterations(pathId, allInstances, absolute, iterations);

        if (hit) {
            m_route.notifyUpdate();
        }

        return hit;
    }    

    public boolean setInstructionTimeTaken(int pc, double timeTaken) {
        boolean hit = false;
        if (m_route != null) {
            hit = m_route.setInstructionTimeTaken(pc, timeTaken);
            if(hit) notifyUpdateState(); 
        }
        else {
            setErrorString("No route currently anaylsed");    		
        }
        return hit;
    }

    public double getInstructionTimeTaken(int pc) {
        if (m_route != null) {
            return m_route.getInstructionTimeTaken(pc);
        }
        return 0;
    }

    public boolean setInstructionNumCycles(int pc, int numCycles) {
        boolean hit = false;
        if (m_route != null) {         		    		
            hit = m_route.setInstructionNumCycles(pc, numCycles);
            if(hit) notifyUpdateState();    
        }
        else {
            setErrorString("No route currently anaylsed");    		
        }
        return hit;
    }

    public int getInstructionNumCycles(int pc) {
        if (m_route != null) {         		    		
            return m_route.getInstructionNumCycles(pc);
        }
        return 0;
    }

    public Double getTimeTaken(int lineNumber) {
        return new Double (0.0);
    }

    private void decodeInstructions(){
        int nodeId = 0;
        int coreId = 0;
        m_instructionDecodeCache = new InstructionDecodeCache(
                m_xeLoader.getSymbolTable(nodeId, coreId), getXeLoader().getArchitecture());
       
        int lastPc = 0;
        HashMap <Integer, ArrayList<Integer>> instructionEntryPc = new HashMap<Integer, ArrayList<Integer>>();
        for(Function function : m_xeObject.getFunctions()){
        	
        	if(function.getChildern() != null && function.getChildern().size()>0){
	            int functionEntry = function.getChildern().get(0).getLineNumber();
	            for(Line assembly : function.getChildern()){
	                int pc = assembly.getLineNumber();
	                int opcode = getOpcode(pc, nodeId,coreId);
	                if(m_instructionDecodeCache.decode(opcode, pc).getNmenonic().startsWith("RETSP")){
	                    ArrayList<Integer> retsps = instructionEntryPc.get(functionEntry);
	                    if(retsps==null){
	                        retsps = new ArrayList<Integer>();
	                    } 
	                    retsps.add(pc);
	                    instructionEntryPc.put(functionEntry, retsps);  
	                }
	            }
	            lastPc = function.getChildern().get(function.getChildern().size()-1).getLineNumber();
        	}
        }
        int pc =m_xeLoader.getMemoryImage(nodeId, coreId).getMemoryBase();
        while(pc <= lastPc){
            int opcode = getOpcode(pc, nodeId,coreId);
            int currentTarget = m_instructionDecodeCache.decode(opcode, pc).getBranchPcRelativeTargetPc();
            if(currentTarget>0 && m_instructionDecodeCache.decode(opcode, pc).getNmenonic().startsWith("BL")){
                if(instructionEntryPc.get(currentTarget)!=null){
                    for(int targetPc : instructionEntryPc.get(currentTarget)){
                        int opcodeRetsp = getOpcode(targetPc, nodeId,coreId);
                        int pcOpcode = getOpcode(pc, nodeId,coreId);
                        XtaInstruction inst = m_instructionDecodeCache.decode(pcOpcode, pc);
                        m_instructionDecodeCache.decode(opcodeRetsp, targetPc).addReturnLocation(inst);
                    }
                } else {
                    //this is the case where dosyscall has been hit
                }
            }
            pc += m_instructionDecodeCache.decode(opcode, pc).getNumBytes();
        }
    }

    private int getOpcode(int pc, int nodeId, int coreId){
        int opcode=0;
        try {
            opcode = m_xeLoader.getMemoryImage(nodeId, coreId).readInstructionWord(pc);
        } catch (Exception e) {
            System.err.println("Problem with XeLoader in engine");
        }
        return opcode;
    }


    void notifyUpdateState() {
        for (Displayer displayer : m_displayers)
            displayer.notifyUpdateState();
    }

    private boolean analyseRoute() {
        assert(m_route != null);

        if (m_route.getProgress().getState() != Progress.State.IDLE) {
            setErrorString("Engine busy");
            return false;
        }

        for (Displayer displayer : m_displayers)
            displayer.notifyAnalysePaths();    	

        int nodeId = m_route.getNodeId();
        int coreId = m_route.getCoreId();

        if (m_xeLoader.isLoaded(nodeId, coreId)) {
            MemoryImage memoryImage = m_xeLoader.getMemoryImage(nodeId, coreId);
            SymbolTable symbolTable = m_xeLoader.getSymbolTable(nodeId, coreId);
            DebugInfo debugInfo = m_xeLoader.getDebugInfo(nodeId, coreId);                               

            assert(memoryImage != null);
            assert(symbolTable != null);
            assert(debugInfo != null);

            Route.RouteStatus status = m_route.analyse(memoryImage, symbolTable, debugInfo, this);                 
            switch (status) {
            case ABORTED:
                setErrorString("Abort detected");
                m_route = m_previousRoute;
                return false;

            case ERROR:
                setErrorString("Cannot analyse path");
                m_route = m_previousRoute;
                return false;

            case MAX_NUM_PATHS:
                setErrorString(String.format("Maximum number of paths reached (%d)", 
                        m_config.m_maxNumPaths));
                m_route = m_previousRoute;
                return false; 
            case COMPLETED:
                setErrorString(null);
            }

            for (Displayer displayer : m_displayers) {
                displayer.displayRoute(m_route, debugInfo, memoryImage, symbolTable);            	
            }                
        }
        else {
            setErrorString(String.format("Node: %d, Core: %d image not found in file: %s",
                    m_route.getNodeId(), m_route.getCoreId(), m_executableFile));
            return false;
        }

        return true;
    }

    private boolean functionExists(String functionName) {
        SymbolTable symbolTable = m_xeLoader.getSymbolTable(m_nodeId, m_coreId);
        if (symbolTable != null) {
            ElfHandler elfHandler = symbolTable.getElfHandler();

            Symbol symbol = elfHandler.getSymbolsNameKeyed().get(functionName);
            if (symbol != null) {
                if (symbol.isFunction()) {
                    return true;
                }		    	  
            }
            return false;
        }
        return false;	
    }    

    private boolean resolveRefernece(Reference ref) {
        DebugInfo debugInfo = m_xeLoader.getDebugInfo(m_nodeId, m_coreId);
        return ref.resolve(debugInfo);
    }

	public void setDynamicMode(boolean b) {
		m_resolveExpression = b;
	}

	public void addLabel(int parseInt, String string) {
		this.m_labels.put(parseInt, string);
	}

	public String getLabel(int pc) {
		if(this.m_labels.containsKey(pc)){
			return m_labels.get(pc);
		}
		return null;
	}


}

