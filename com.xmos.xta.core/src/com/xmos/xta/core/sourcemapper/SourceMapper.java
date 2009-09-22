package com.xmos.xta.core.sourcemapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import com.xmos.dwarfhandler.DebugEngine;
import com.xmos.dwarfhandler.DebugFile;
import com.xmos.util.Pair;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.core.engine.analysis.controlflow.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;

public class SourceMapper {

	private static final boolean DEBUG = false;
	
    private List<Function> m_functions;
	private List<SourceFile> m_sourceFiles;
	private List<AssemblyLine> m_instructions;
	private Map<Long, SourceFile> m_pcToFile;
	private FileFinder m_fileFinder;
	
	private InstructionDecoder m_instructionDecoder;
	private Set<InstructionNode> m_starts;
	private TreeMap<String, LinkedList<InstructionNode>> m_functionNames;
	private Map<Long, AssemblyLine> m_pcToAsm;
	private Map<Long, Function> m_pcToFunction;
	private Map<String, String> m_functionToFile;
	
	private Map<String, Long> m_labels;
	
    public SourceMapper(XeLoader xeLoader, int nodeId, int coreId,
			Set<InstructionNode> starts, FileFinder fileFinder, InstructionDecoder instructionDecoder, 
			TreeMap<String, LinkedList<InstructionNode>> treeMap, Map<String, Long> labels) {
    	m_labels = labels;
    	m_fileFinder = fileFinder;
        m_instructionDecoder = instructionDecoder;
        m_starts = starts;
        m_functionNames = treeMap;
        m_pcToAsm = new TreeMap<Long, AssemblyLine>();
        m_pcToFunction = new TreeMap<Long, Function>();
        m_functionToFile = new TreeMap<String, String>();
        initialise(xeLoader, nodeId, coreId);
	}

    public void initialise(XeLoader xeLoader, int nodeId, int coreId) {
        m_pcToFile = new TreeMap <Long, SourceFile>();
        m_instructions = createAssemblyLines();
        m_functions = createFunctions();
        m_sourceFiles = createSourceFiles(xeLoader, nodeId, coreId);
    }   

    private boolean containsSourceFile(File file, List<SourceFile> sourceFiles){    	
    	// Every source file added should have been canonicalised as should have the file
        for (SourceFile sourceFile : sourceFiles){
            File srcFile = sourceFile.getFile();
            
            if (srcFile == null){
                continue;
            }
            
            if (srcFile.equals(file)){
                return true;
            }
        }
        return false;
    }
  
    private List<SourceFile> createSourceFiles(XeLoader xeLoader, int nodeId, int coreId) {    
    	List<SourceFile> sourceFiles = new LinkedList<SourceFile>();
    	DebugInfo di = xeLoader.getDebugInfo(nodeId, coreId);     	
        DebugEngine de = di.getDebugEngine();
            
        if (DEBUG) System.out.printf("createSourceFiles: node: %d, core: %d\n", nodeId, coreId);            
            
      	for (DebugFile debugFile : de.getDebugFiles()) {
      		
			File source = new File(debugFile.getFullFileName());
			boolean done = false;
			while (!done) {
				if (source.exists()) {

					// convert it to its canonical form
					try {
						source = source.getCanonicalFile();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// sourceFiles doesn't already contain this file
					if (!containsSourceFile(source, sourceFiles)) {
						// add it to the sourceFiles
						debugFile.setFile(source);
						SourceFile sf = new SourceFile(debugFile);
						sourceFiles.add(sf);
						done = true;
					} else {
						done = true;
					}
				} else {
					if (m_fileFinder != null) {
						source = m_fileFinder.openFileFinder(debugFile.getBaseDir(),
								debugFile.getFileName());

						if (source == null)
							done = true;

					} else {
						done = true;
					}
				}
			}
		}

        SymbolTable symbolTable = xeLoader.getSymbolTable(nodeId, coreId);
        DebugInfo debugInfo = xeLoader.getDebugInfo(nodeId, coreId);
        DebugEngine debugEngine = debugInfo.getDebugEngine();

        assert debugEngine != null;
        assert symbolTable != null;

        for (SourceFile sourceFile : sourceFiles){    
        	for (SourceLine sourceLine : sourceFile.getSourceLines()) {        		
        		long lineNumber = sourceLine.getLineNumber();
				Collection<Pair<Long,Long>> addressRanges = debugEngine.getAddressRangesMatchingPath(
            			sourceFile.getFullFileName(), lineNumber);
            	Collection<Long> addresses = rangesToAddresses(addressRanges);
            	
                for (long pc : addresses) {
                	if (m_pcToAsm.containsKey(pc)) {
                        AssemblyLine assemblyLine = m_pcToAsm.get(pc);
                        Function fn = m_pcToFunction.get(pc);
                        m_functionToFile.put(fn.getText(), sourceFile.getFileName());
                        sourceLine.addAssembly(assemblyLine);
                        
                	} else {
                		System.err.println("Could not find assembly line correspinding to 0x" + 
                				Long.toHexString(pc) + " in debug information. ");
                		assert(false);
                	}

                	m_pcToFile.put(pc, sourceFile);
                }
                
            	Collection<Long> statementPcs = debugEngine.getStatementPcsMatchingPath(
            			sourceFile.getFullFileName(), lineNumber);
            	sourceLine.addStatementPcs(statementPcs);

            	for (long statementPc : statementPcs) {
            		sourceFile.addPcToLine(statementPc, sourceLine);
            	}
            }            
        }
        assert(sourceFiles != null);
        return sourceFiles;
    }

    private Collection<Long> rangesToAddresses(Collection<Pair<Long, Long>> addressRanges) {
    	Set<Long> addresses = new TreeSet<Long>();
		for (Pair<Long, Long> range : addressRanges) {
			int asmLineNum = 0;
			boolean active = false;
			
			while (!active && asmLineNum < m_instructions.size()) {
				AssemblyLine line = m_instructions.get(asmLineNum);
				if (line.getPc() == range.first) {
					active = true;
				} else {
					asmLineNum++;
				}
			}

			assert(active);
			while (active && asmLineNum < m_instructions.size()) {
				AssemblyLine line = m_instructions.get(asmLineNum);
				if (line.getPc() == range.second) {
					active = false;
				} else {
					addresses.add((long)line.getPc());
					asmLineNum++;
				}
			}
		}
		return addresses;
	}
    
	private List<AssemblyLine> createAssemblyLines() {
		List<AssemblyLine> asmLines = new ArrayList<AssemblyLine>();
		for (InstructionNode instNode : m_instructionDecoder.getInstructions()){
			AssemblyLine assLine = new AssemblyLine(instNode, m_labels);
			asmLines.add(assLine);
			m_pcToAsm.put(instNode.getStartPc(), assLine);
		}
        return asmLines;
    }

    private List<Function> createFunctions() {
    	List<InstructionNode> instList = m_instructionDecoder.getInstructions();
    	
    	Map<Long, Function> pcToFunction = new HashMap<Long, Function>();
    	
    	for(InstructionNode inst : m_starts){
    		int startIndex = instList.indexOf(inst);
    		List<AssemblyLine> functionAssemby = new ArrayList<AssemblyLine>();
    		functionAssemby.add(m_instructions.get(startIndex));
    		int index = startIndex +1;
    		while(index < instList.size() && !m_starts.contains(instList.get(index))){
    			functionAssemby.add(m_instructions.get(index));
    			index++;
    		}
    		String fnName = getFunctionName(inst);
    		Function function = new Function(fnName, functionAssemby);
    		
    		for (Line assembly : functionAssemby){
    			m_pcToFunction.put(assembly.getLineNumber(), function);
    		}
    		
    		pcToFunction.put(inst.getStartPc(), function);
    	}
    	
    	//sort the functions into ascending pc order
    	List<Long> pcs = new LinkedList<Long>();
    	pcs.addAll(pcToFunction.keySet());
    	Collections.sort(pcs);
    	
    	List<Function> sortedFunctions = new LinkedList<Function>();
    	for (long pc : pcs){
    		sortedFunctions.add(pcToFunction.get(pc));
    	}
    	return sortedFunctions;
    }
    
    private String getFunctionName(InstructionNode inst) {
    	
    	for(String functionName : m_functionNames.keySet()){
    		for(InstructionNode fnInst : m_functionNames.get(functionName)){
				if(fnInst.getStartPc() == inst.getStartPc()){
					return functionName;
				}
    		}
		}
		//FIXME
		System.err.println("cound not find the function corresponding to " + inst.getXtaInstruction());
		
		assert(false);
		return "";
	}

	public List<SourceFile> getSourceFiles() {
        return m_sourceFiles;
    }

	public SourceFile getSourceFile(String fileName) {
        for (SourceFile sourceFile : m_sourceFiles){
        	if (sourceFile.getFileName().equals(fileName))
        		return sourceFile;
        }
        assert(false);
        return null;
    }

	public List<Function> getFunctions() {
        return m_functions;
    }
	public boolean isProfilablePc(long pc) {
		for (Function function : m_functions){
			if (function.containsPc(pc)){
				return true;
			}
		}
		return false;
	}
	
	public Function getFunction(long pc) {
		return m_pcToFunction.get(pc);
	}
	
	public String getFunctionFileName(String string) {
		return m_functionToFile.get(string);
	}
}
