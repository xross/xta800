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
import com.xmos.xta.engine.Route.RouteStatus;

public class Engine { 
	
	public enum ReferenceMode {
		EXCLUSION,
		PROHIBITED,
		REQUIRED
	}
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
    
    private Vector<CodeReference> m_exclusions;
    private Vector<CodeReference> m_prohibited;
    private Vector<CodeReference> m_required;
    private HashMap<Integer, String> m_labels;
    
    private boolean m_abort;
    private InstructionDecodeCache m_instructionDecodeCache;
    private boolean m_resolveExpression;
    
    private ArrayList<PathQuery> m_pathQueries;
    
    public Engine(Config config) {
    	
    	m_exclusions = new Vector<CodeReference>();
    	m_prohibited = new Vector<CodeReference>();
    	m_required = new Vector<CodeReference>();
    	m_labels = new HashMap<Integer, String>();
    	
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
        m_pathQueries = new ArrayList<PathQuery>();
        m_abort = false;
        m_resolveExpression = false;
        
    }
    
    public void addReference(ReferenceMode mode, CodeReference ref){
    	//test for duplicates
    	//test for conditions where the user want a reference and not a reference
    	
    	if(mode == ReferenceMode.EXCLUSION){
    		m_exclusions.add(ref);
    	} else if(mode == ReferenceMode.PROHIBITED){
    		m_prohibited.add(ref);
    	} else if(mode == ReferenceMode.REQUIRED){
    		m_required.add(ref);
    	} else{
    		assert(false);
    	}
    }
    
    public boolean removeReference(ReferenceMode mode, CodeReference ref){
    	if(mode == ReferenceMode.EXCLUSION){
    		CodeReference cr = findReference(ref, m_exclusions);
    		if(cr==null) return false;
    		m_exclusions.remove(cr);
    		return true;
    	} else if(mode == ReferenceMode.PROHIBITED){
    		CodeReference cr = findReference(ref, m_prohibited);
    		if(cr==null) return false;
    		m_prohibited.remove(cr);
    		return true;
    	} else if(mode == ReferenceMode.REQUIRED){
    		CodeReference cr = findReference(ref, m_required);
    		if(cr==null) return false;
    		m_required.remove(cr);
    		return true;
    	} else{
    		assert(false);
    		return false;
    	}
    }
    
    private CodeReference findReference(CodeReference ref, Vector<CodeReference> references){
    	for(CodeReference present : references){
    		if(ref.getPc() == present.getPc() && ref.getRef().equals(present.getRef())){
    			return present;
    		}
    	}
    	return null;
    }
    
    
    public Vector<CodeReference> getReference(ReferenceMode mode){
    	if(mode == ReferenceMode.EXCLUSION){
    		return m_exclusions;
    	} else if(mode == ReferenceMode.PROHIBITED){
    		return m_prohibited;
    	} else if(mode == ReferenceMode.REQUIRED){
    		return m_required;
    	} else{
    		assert(false);
    		return null;
    	}
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
    	m_pathQueries.clear();
        boolean success = m_xeLoader.loadExecutable(executableFile);
        if (success) {
            m_exclusions.clear();
            if (m_xeObject == null) {
                m_xeObject = new XeObject(m_xeLoader);
            }
            decodeInstructions();
            //addBranchTargets();
            m_route = null;
            for (Displayer displayer : m_displayers)
                displayer.notifyLoadExecutable();
            
        } else {
            setErrorString("The file '" + executableFile + "'could not be found.");	        
        }
        
        return success;
    }	

    public boolean loadExecutable(XEHandler xeHandler) {
    	m_pathQueries.clear();
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
            boolean t = analyseRouteSafley();
            
            if(t == false){
            	//restore the route
            	m_route = new Route(m_config, functionName, m_nodeId, m_coreId, m_instructionDecodeCache, resolveExpression);
            }
            return t;
        }
    }
    
    
    private boolean analyseRouteSafley(){
    	//catch all errors here
    	
    	boolean t = false;
    	try {
			t = analyseRoute();
		} catch (final java.lang.OutOfMemoryError e) {
			m_route = null;
            System.gc();
            setErrorString("Out of Memory"); 
            return false;
		} catch (Exception e) {
			//unknown Exception
			setErrorString("Unknown error: contact Xmos with stack trace"); 
			e.printStackTrace();
		} 
		return t;
    }
    

    public boolean analyseRoute(String from, String to, boolean resolveExpression) {
        CodeReference fromRef = new CodeReference(from);
        CodeReference toRef = new CodeReference(to);     
        resolveExpression = resolveExpression| m_resolveExpression;
        if (!m_xeLoader.isLoaded(m_nodeId, m_coreId)) {
            setErrorString(String.format("Node: %d, Core: %d image not found in file: %s",
                    m_nodeId, m_coreId, m_executableFile));        	
            return false;
        }
        else if (!resolveRefernece(fromRef, this)) {
            setErrorString(String.format("Unresolved reference: %s", from));
            return false;
        }
        else if (!resolveRefernece(toRef, this)) {
            setErrorString(String.format("Unresolved reference: %s", from));
            return false;
        }
        else {    
            m_previousRoute = m_route;                   
            m_route = new Route(m_config, fromRef, toRef, m_nodeId, m_coreId, m_instructionDecodeCache, resolveExpression);
            boolean t = analyseRouteSafley();
            if(t == false){
            	//restore the route
            	m_route = new Route(m_config, fromRef, toRef, m_nodeId, m_coreId, m_instructionDecodeCache, resolveExpression);
            }
            return t;
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
    
    public void refineRoute(){
    	//this applies all exclusions, requirements and prohibited properties to the route
    	
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

    public boolean setLoopIterations(int pathId, boolean absolute, int iterations) {
        boolean hit = m_route.setLoopIterations(pathId, absolute, iterations);

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

    private boolean analyseRoute() throws Exception {
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
            RouteStatus status = RouteStatus.ABORTED;
            status = m_route.analyse(memoryImage, symbolTable, debugInfo, this);                 
            switch (status) {
            case ABORTED:
                setErrorString("Abort detected");
                m_route = m_previousRoute;
                return false;

            case ERROR:
                setErrorString("Cannot analyse path");
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

    private boolean resolveRefernece(CodeReference ref, Engine engine) {
        return ref.resolve(engine, m_nodeId, m_coreId);
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
	
	public boolean isLabel(String lbl){
		return m_labels.containsValue(lbl);
	}
	
	public int getLabelPc(String label){
		for(int pc : m_labels.keySet()){
			if(label.equals(m_labels.get(pc))){
				return pc;
			}
		}
		assert(false);
		return 0;
	}

	public void addQuery(PathQuery pq) {
		this.m_pathQueries.add(pq);
	}

	public ArrayList<PathQuery> getPathQueries() {
		return m_pathQueries;
	}
}

