package com.xmos.xta.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import com.xmos.dwarfhandler.DebugEngine;
import com.xmos.elfhandler.ElfHandler;
import com.xmos.elfhandler.Symbol;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;
import com.xmos.xeobject.FileFinder;
import com.xmos.xeobject.Function;
import com.xmos.xeobject.Line;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.engine.Route.RouteStatus;

public class Engine { 
	
	public enum ReferenceMode {
		FILTERED,
		PROHIBITED,
		REQUIRED
	}
	
    private XeLoader m_xeLoader;
    private Vector<File> m_sourceFiles;
    private Route m_route;
    private Route m_previousRoute;
    private Config m_config;
    private File m_executableFile;
    private int m_nodeId;
    private int m_coreId;
    private String m_errorString;
    private Vector<Displayer> m_displayers;
    private NodeCoreInstructionCaches m_nodeCoreInstructionCache;
    private boolean m_abort;
    private boolean m_resolveExpression;    
    private Progress m_progress;
    
    private ArrayList<XeObject> m_xeObjects;
    private ArrayList<CodeReferenceList> m_exclusions;
    private ArrayList<CodeReferenceList> m_prohibited;
    private ArrayList<CodeReferenceList> m_required;
    private ArrayList<LabelMap> m_labels;
    private ArrayList<PathQuery> m_pathQueries;    
    
    public Engine(Config config) {    	
        m_xeObjects   = new ArrayList<XeObject>();
    	m_exclusions  = new ArrayList<CodeReferenceList>();
    	m_prohibited  = new ArrayList<CodeReferenceList>();
    	m_required    = new ArrayList<CodeReferenceList>();
    	m_labels      = new ArrayList<LabelMap>();    	
        m_xeLoader    = new XeLoader();        
        m_sourceFiles = new Vector<File>();
        m_route       = null;
        m_previousRoute = null;
        m_config = config;
        m_executableFile = null;
        m_nodeId = 0;
        m_coreId = 0;
        m_errorString = "";
        m_displayers  = new Vector<Displayer>();
        m_pathQueries = new ArrayList<PathQuery>();
        m_abort = false;
        m_resolveExpression = false;        
        m_progress = new Progress(Progress.State.IDLE, 0, 0);
    }
    
    private boolean referenceListContainsReference(CodeReference ref, List<CodeReference> list){
    	for(CodeReference cr : list){
    		if(cr.getRef().equals(ref.getRef())){
    			return true;
    		}
    	}
    	return false;
    }
    
    
    public boolean addReference(ReferenceMode mode, CodeReference ref) {    	    	
        if (!resolveRefernece(ref, this)) {
        	return false;
        }    	
    	if (mode == ReferenceMode.FILTERED){
    		if(referenceListContainsReference(ref, getReferenceList(m_exclusions, m_nodeId, m_coreId))){
    			//give reason to the user
    			return false;
    		} else {
    			getReferenceList(m_exclusions, m_nodeId, m_coreId).add(ref);
    		}
            
    	} else if (mode == ReferenceMode.PROHIBITED){
    		if(referenceListContainsReference(ref, getReferenceList(m_prohibited, m_nodeId, m_coreId))){
    			//give reason to the user
    			return false;
    		} else {
    			getReferenceList(m_prohibited, m_nodeId, m_coreId).add(ref);
    		}
    	} else if (mode == ReferenceMode.REQUIRED){
    		if(referenceListContainsReference(ref, getReferenceList(m_required, m_nodeId, m_coreId))){
    			//give reason to the user
    			return false;
    		} else {
    			getReferenceList(m_required, m_nodeId, m_coreId).add(ref);
    		}
    	} else{
    		assert(false);
    		return false;
    	}
    	if (m_route != null) 
    		m_route.filterPaths();
    	callDisplayers(Displayer.NOTIFY_UPDATE_EXCLUDES, ""); 
    	return true;
    }
    
    public boolean removeReference(ReferenceMode mode, CodeReference ref){
    	boolean found = true;
    	
    	if (mode == ReferenceMode.FILTERED){
    		List<CodeReference> references = getReferenceList(m_exclusions, m_nodeId, m_coreId);
    		found = removeReferences(ref, references);
    	} else if (mode == ReferenceMode.PROHIBITED){
    		List<CodeReference> references = getReferenceList(m_prohibited, m_nodeId, m_coreId);
    		found = removeReferences(ref, references);
    	} else if (mode == ReferenceMode.REQUIRED){
    		List<CodeReference> references = getReferenceList(m_required, m_nodeId, m_coreId);
    		found = removeReferences(ref, references);
    	} else {
    		assert(false);
    		return false;
    	}
    	if (!found)
    		return false;

    	if (m_route != null) 
    		m_route.filterPaths();
    	callDisplayers(Displayer.NOTIFY_UPDATE_EXCLUDES, ""); 
    	return true;
    }
        
    public void callDisplayers(String message, String argument){
    	for (Displayer displayer : m_displayers)
            displayer.notify(message, argument); 
    }
    
    public List<CodeReference> getReference(ReferenceMode mode){
    	if(mode == ReferenceMode.FILTERED){
    		return getReferenceList(m_exclusions, m_nodeId, m_coreId);
    	} else if (mode == ReferenceMode.PROHIBITED){
    		return getReferenceList(m_prohibited, m_nodeId, m_coreId);
    	} else if (mode == ReferenceMode.REQUIRED){
    		return getReferenceList(m_required, m_nodeId, m_coreId);
    	} else {
    		assert(false);
    		return null;
    	}
    }
    
    public Config getConfig() {
        return m_config;
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

    public XeObject getXeObject(){
        return getXeObject(m_nodeId, m_coreId);
    }

    public boolean loadExecutable(File executableFile) {
    	return loadExecutable(executableFile, null);
    }

    public boolean loadExecutable(File executableFile, FileFinder fileFinder) {
    	m_pathQueries.clear();
    	
        boolean success = m_xeLoader.loadExecutable(executableFile);
        if (success) {
        	m_executableFile = executableFile;
        	initialiseLists(fileFinder);
            m_nodeCoreInstructionCache = new NodeCoreInstructionCaches(m_xeLoader);
            decodeInstructions(); 
            
            //TODO
            loadLabels();
            
            if (success) {
            	m_route = null;
            	callDisplayers(Displayer.NOTIFY_LOAD_EXECUTABLE, executableFile.getAbsolutePath()); 
            }
        } else {
            setErrorString("The file '" + executableFile + "'could not be found.");	        
        }        
        return success;
    }	

    public Progress getProgress() {
    	return m_progress;
    }

    public boolean analyseRoute(String functionName) {
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
            m_route = new Route(m_config, m_progress, functionName,
            		m_nodeId, m_coreId, m_nodeCoreInstructionCache,
            		m_resolveExpression);

            boolean t = analyseRouteSafley();
            if (t) {
            	callDisplayers(Displayer.NOTIFY_SELECT_FUNCTION, functionName); 
            } else {
            	//restore the route
            	m_route = m_previousRoute;
            }
            return t;
        }
    }    

    public boolean analyseRoute(String from, String to) {
        CodeReference fromRef = new CodeReference(from);
        CodeReference toRef = new CodeReference(to);     
        
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
            setErrorString(String.format("Unresolved reference: %s", to));
            return false;
        }
        else {    
            m_previousRoute = m_route;                   
            m_route = new Route(m_config, m_progress, fromRef, toRef,
            		m_nodeId, m_coreId, m_nodeCoreInstructionCache,
            		m_resolveExpression);
            boolean t = analyseRouteSafley();
            if (t) {
            	callDisplayers(Displayer.NOTIFY_SET_START_POINT, from); 
            	callDisplayers(Displayer.NOTIFY_SET_END_POINT, to); 
                //for (Displayer displayer : m_displayers) {
                //	displayer.notify(Displayer.NOTIFY_SET_START_POINT, from);
                //	displayer.notify(Displayer.NOTIFY_SET_END_POINT, to);
                //}
            } else {
            	// restore the route
            	m_route = m_previousRoute;
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

    public void clearDisplayer(Displayer displayer) {
        m_displayers.remove(displayer);    			
    }

    public void clearDisplayers() {
        m_displayers.clear();
    }

    public void setAbort(boolean value) {
        m_abort = value;
    }

    public boolean getAbort() {
        return m_abort;
    }

    public void setNodeId(int nodeId) {
        m_nodeId = nodeId;
        switchNodeCode();
        callDisplayers(Displayer.NOTIFY_UPDATE_STATE, "");
    }

    public int getNodeId(){
        return m_nodeId;
    }

    public void setCoreId(int coreId) {
        m_coreId = coreId;
        switchNodeCode();
        callDisplayers(Displayer.NOTIFY_UPDATE_STATE, "");
    }

    public int getCoreId() {
        return m_coreId;
    }

    public void setFrequency(int frequency) {
        m_config.m_frequency = frequency;
        callDisplayers(Displayer.NOTIFY_UPDATE_STATE, "");
    }

    public int getFrequency() {
        return m_config.m_frequency;
    }

    public void setNumThreads(int numThreads) {
        m_config.m_numThreads = numThreads;
        callDisplayers(Displayer.NOTIFY_UPDATE_STATE, "");
    }

    public int getNumThreads() {
        return m_config.m_numThreads;
    }

	public void setResolve(boolean resolve) {
		m_resolveExpression = resolve;
		callDisplayers(Displayer.NOTIFY_UPDATE_STATE, "");
	}

	public boolean getResolve() {
		return m_resolveExpression;
	}

    public Vector<File> getFileNames() {
        for(DebugInfo bd : m_xeLoader.getDebugInfos()){
            DebugEngine de = bd.getDebugEngine();
            for (File  fileName : de.getFileNames()){
                if (!m_sourceFiles.contains(fileName)) 
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
            if (hit) callDisplayers(Displayer.NOTIFY_UPDATE_STATE, "");
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
            if (hit) callDisplayers(Displayer.NOTIFY_UPDATE_STATE, ""); 
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

	public boolean addLabel(String label , CodeReference ref) {
        if (resolveRefernece(ref, this)) {
    		Map<String, Integer> labels = getLabelsMap(m_nodeId, m_coreId);
    		if (labels != null) {
    			labels.put(label, ref.getPc()); 
    		    return true;
    		}
        }    			
        return false;
	}

	public void removeLabel(String label) {
		Map<String, Integer> labels = getLabelsMap(m_nodeId, m_coreId);
		if (labels != null) {
			labels.remove(label);
		}
	}
	
	public Map<String, Integer> getLabels() {
		return getLabelsMap(m_nodeId, m_coreId);
	}
	
	public boolean isLabel(String label) {
		Map<String, Integer> labels = getLabelsMap(m_nodeId, m_coreId);
		if (labels != null) {
			return labels.containsKey(label);
		}
		return false;
	}
	
	public int getLabelPc(String label){
		Map<String, Integer> labels = getLabelsMap(m_nodeId, m_coreId);		
		if (labels != null) {
    		if (labels.containsKey(label)) {
	    		return labels.get(label);
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
	
    private boolean analyseRouteSafley() {
    	//catch all errors here
    	boolean t = false;
    	try {
			t = analyseRoute();
		} catch (final java.lang.OutOfMemoryError e) {
			m_route = null;
			m_progress.setState(Progress.State.IDLE);
            System.gc();
            setErrorString("Out of Memory"); 
            return false;
		} catch (final java.lang.StackOverflowError e) {
			m_route = null;
			m_progress.setState(Progress.State.IDLE);
            System.gc();
            setErrorString("Out of Memory"); 
            return false;
		} catch (Exception e) {
			//unknown Exception
			m_progress.setState(Progress.State.IDLE);
			setErrorString("Unknown error: contact Xmos with stack trace"); 
			e.printStackTrace();
			return false;
		} 
		return t;
    }
    
    private void switchNodeCode(){
    	/*
    	m_exclusions.clear();
    	m_prohibited.clear();
    	m_required.clear();
    	getLabelsMap(m_nodeId, m_coreId);
    	*/
    }
    
    private void decodeInstructions(){
        for(int nodeId : m_xeLoader.getNodeIds()){
        	for(int coreId : m_xeLoader.getCoreIds(nodeId)){
		        int lastPc = 0;
		        HashMap <Integer, ArrayList<Integer>> instructionEntryPc = new HashMap<Integer, ArrayList<Integer>>();
		        for (Function function : getXeObject(nodeId, coreId).getFunctions()){
		        	
		        	if (function.getChildern() != null && function.getChildern().size()>0){
			            int functionEntry = function.getChildern().get(0).getLineNumber();
			            for (Line assembly : function.getChildern()){
			                int pc = assembly.getLineNumber();
			                int opcode = getOpcode(pc, nodeId,coreId);			                
			                
			                if (m_nodeCoreInstructionCache.decode(opcode, pc, nodeId, coreId).getNmenonic().toLowerCase().startsWith("retsp")){
			                    ArrayList<Integer> retsps = instructionEntryPc.get(functionEntry);
			                    if (retsps==null){
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
		        while (pc <= lastPc){
		            int opcode = getOpcode(pc, nodeId,coreId);
		            int currentTarget = m_nodeCoreInstructionCache.decode(opcode, pc, nodeId, coreId).getBranchPcRelativeTargetPc();
		            if (currentTarget>0 && m_nodeCoreInstructionCache.decode(opcode, pc, nodeId, coreId).getNmenonic().toLowerCase().startsWith("bl")){
		                if (instructionEntryPc.get(currentTarget)!=null){
		                    for (int targetPc : instructionEntryPc.get(currentTarget)){
		                        int opcodeRetsp = getOpcode(targetPc, nodeId,coreId);
		                        int pcOpcode = getOpcode(pc, nodeId,coreId);
		                        XtaInstruction inst = m_nodeCoreInstructionCache.decode(pcOpcode, pc, nodeId, coreId);
		                        m_nodeCoreInstructionCache.decode(opcodeRetsp, targetPc, nodeId, coreId).addReturnLocation(inst);
		                    }
		                } else {
		                    //this is the case where dosyscall has been hit
		                }
		            }
		            pc += m_nodeCoreInstructionCache.decode(opcode, pc, nodeId, coreId).getNumBytes();
		        }
  		
        	}
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

    private boolean analyseRoute() throws Exception {
        assert(m_route != null);

        if (m_progress.getState() != Progress.State.IDLE) {
            setErrorString("Engine busy");
            return false;
        }
        callDisplayers(Displayer.NOTIFY_ANALYSE_PATHS, "");

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
    
    private List<CodeReference> getReferenceList(List<CodeReferenceList> referenceList, 
    		int nodeId, int coreId) {
    	for (CodeReferenceList references : referenceList) {
    		if ((references.getNodeId() == nodeId) &&
    			(references.getCoreId() == coreId)) {
    			return references.getReferences();
    		}
    	}
    	assert(false);
    	return null;
    }
    
    private boolean removeReferences(CodeReference ref, List<CodeReference> references) {
    	List<CodeReference> matches = new ArrayList<CodeReference>();
    	
    	for (CodeReference present : references){
    		if (present.getRef().matches(ref.getRef())){
    			matches.add(present);
    		}
    	}

    	if (matches.size() > 0) {
    		references.removeAll(matches);
    		return true;
    	}
    	return false;
	}

    private Map<String, Integer> getLabelsMap(int nodeId, int coreId) {
    	for (LabelMap labels : m_labels) {
    		if ((labels.getNodeId() == nodeId) &&
    			(labels.getCoreId() == coreId)) {
    			return labels.getLabels();
    		}
    	}
    	return null;
    }

    private XeObject getXeObject(int nodeId, int coreId) {
    	for (XeObject xeObject : m_xeObjects) {
    		if ((xeObject.getNodeId() == nodeId) &&
        		(xeObject.getCoreId() == coreId)) {
    			return xeObject;
    		}    		
    	}
    	assert(false);
    	return null;
    }
    
    private void initialiseLists(FileFinder fileFinder) {
    	m_exclusions.clear();
    	m_prohibited.clear();
    	m_required.clear();
    	m_labels.clear();
    	m_xeObjects.clear();
    	
		for (int node : m_xeLoader.getNodeIds()){
			for (int core : m_xeLoader.getCoreIds(node)) {
				
			    m_exclusions.add(new CodeReferenceList(node, core));
			    m_prohibited.add(new CodeReferenceList(node, core));
			    m_required.add(new CodeReferenceList(node, core));
			    m_labels.add(new LabelMap(node, core));
				m_xeObjects.add(new XeObject(m_xeLoader, node, core, fileFinder));
			}
		}
    }
    
    private void loadLabels() {
    	for (LabelMap labelMap : m_labels) {
    		labelMap.loadLabels(this, m_xeLoader);
		}
    	return;
    }

	public File getExecutable() {
		return m_executableFile;
	}
}

