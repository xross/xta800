package com.xmos.xta.core.engine.sourcemapper;

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

import com.xmos.libs.dwarfhandler.DebugEngine;
import com.xmos.libs.dwarfhandler.DebugFile;
import com.xmos.libs.util.Pair;
import com.xmos.libs.xeloader.DebugInfo;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;

public class SourceMapper {

	private static final boolean DEBUG = false;

	private long m_xeLastModified;
    private List<Function> m_functions;
	private List<SourceFile> m_sourceFiles;
	private List<AssemblyLine> m_instructions;
	private Map<Long, SourceFile> m_pcToFile;

	private InstructionDecoder m_instructionDecoder;
	private Set<InstructionNode> m_starts;
	private Map<String, List<InstructionNode>> m_functionNames;
	private Map<Long, AssemblyLine> m_pcToAsm;
	private Map<Long, Function> m_pcToFunction;
	private Map<String, String> m_functionToFile;

	private Map<String, Set<Long>> m_labels;

    public SourceMapper(CommandSource src, Engine engine, XeLoader xeLoader, int nodeNumber, int coreNumber,
			Set<InstructionNode> starts, InstructionDecoder instructionDecoder,
			Map<String, List<InstructionNode>> treeMap, Map<String, Set<Long>> labels) throws AbortedException {
    	m_labels = labels;
        m_instructionDecoder = instructionDecoder;
        m_starts = starts;
        m_functionNames = treeMap;
        m_pcToAsm = new TreeMap<Long, AssemblyLine>();
        m_pcToFunction = new TreeMap<Long, Function>();
        m_functionToFile = new TreeMap<String, String>();

        m_pcToFile = new TreeMap <Long, SourceFile>();
        m_instructions = createAssemblyLines();
        m_functions = createFunctions();

        // Must record the XE timestamp before loading files
        m_xeLastModified = xeLoader.getFile().lastModified();
        m_sourceFiles = createSourceFiles(src, engine, xeLoader, nodeNumber, coreNumber);
	}

	private SourceFile getSourceFile(DebugFile debugFile, List<SourceFile> sourceFiles){
    	// Every source file added should have been canonicalised as should have the file
        for (SourceFile sourceFile : sourceFiles) {
            if (debugFile == sourceFile.getDebugFile()) {
                return sourceFile;
            }
        }
        return null;
    }

    private List<SourceFile> createSourceFiles(CommandSource src, Engine engine, XeLoader xeLoader,
    		int nodeNumber, int coreNumber) throws AbortedException {

    	List<SourceFile> sourceFiles = new LinkedList<SourceFile>();

    	DebugInfo di = xeLoader.getDebugInfo(nodeNumber, coreNumber);
        DebugEngine de = di.getDebugEngine();

        if (DEBUG) System.out.printf("createSourceFiles: node: %d, core: %d\n", nodeNumber, coreNumber);

		Collection<DebugFile> debugFiles = de.getDebugFiles();
		engine.startAnalysis("Processing sources", debugFiles.size());
      	for (DebugFile debugFile : debugFiles) {
			File file = new File(debugFile.getFullFileName());
    		engine.provideFeedback(file.getName());

			boolean done = false;
			while (!done) {
				Notifier notifier = engine.getNotifier();
				if (file.exists()) {
					try {
						file = file.getCanonicalFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					addSourceFile(src, notifier, de, debugFile, file, sourceFiles);
					done = true;

				} else {
					file = findFileInSrcPaths(engine.getSrcPaths(), debugFile.getFileName());
					addSourceFile(src, notifier, de, debugFile, file, sourceFiles);
					done = true;
				}
			}
			engine.provideWorkDone(1);
			engine.getAbort();
		}

        return sourceFiles;
    }

	private File findFileInSrcPaths(String srcPathsString, String fileName) {
		String[] srcPaths = srcPathsString.split(";");
		for (String srcPath : srcPaths) {
		    File file = new File(srcPath + File.separator + fileName);
	        if (file.exists()) {
	    	    return file;
	        }
		}
		return null;
	}

	private void addSourceFile(CommandSource src, Notifier notifier,
			DebugEngine debugEngine, DebugFile debugFile,
			File file, List<SourceFile> sourceFiles) {

    	SourceFile sourceFile = getSourceFile(debugFile, sourceFiles);
    	if (sourceFile == null) {
    		sourceFile = new SourceFile(debugFile);
    	}
		sourceFiles.add(sourceFile);
    	setFile(src, notifier, debugEngine, sourceFile, file);
	}

    public File findFileForSourceFile(SourceFile sourceFile, FileFinder fileFinder) {
    	if (fileFinder == null) {
    		return null;
    	}

    	File file = null;
    	DebugFile debugFile = sourceFile.getDebugFile();
    	boolean done = false;
		while (!done) {
			file = fileFinder.openFileFinder(debugFile.getBaseDir(), debugFile.getFileName());

			if (file == null) {
				done = true;

			} else {
				if (file.exists()) {
					try {
						file = file.getCanonicalFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					done = true;
				}
			}
		}
		return file;
    }

    public void setFile(CommandSource src, Notifier notifier, DebugEngine debugEngine,
    		SourceFile sourceFile, File file) {

    	if (file == null)
    		return;

    	DebugFile debugFile = sourceFile.getDebugFile();
		debugFile.setFile(file);
		sourceFile.readFile();
		mapSourceFileLines(debugEngine, sourceFile);

		checkSourceFileDate(src, notifier, file);
    }

    private void checkSourceFileDate(CommandSource src, Notifier notifier, File file) {
    	long sourceLastModified = file.lastModified();
    	if (sourceLastModified > m_xeLastModified) {
    		notifier.notify(src, Notifier.NOTIFY_WARNING,
    				String.format("Source file '%s' newer than binary",
    						file.getName()));
    	}
	}

	public String getDebugFileName(String diskFileName) {
        for (SourceFile sourceFile : m_sourceFiles) {
        	try {
        		File fileOnDisk = sourceFile.getFile();
        		if (fileOnDisk != null) {
    				if (fileOnDisk.getCanonicalPath().equals(diskFileName)) {
    					return sourceFile.getFullFileName();
    				}
        		}
			} catch (IOException e) {}
        }
        return diskFileName;
    }

	public List<SourceFile> getSourceFiles() {
        return m_sourceFiles;
    }

	public SourceFile getSourceFile(String fileName) {
        for (SourceFile sourceFile : m_sourceFiles){
        	if (sourceFile.fileNameMatches(fileName))
        		return sourceFile;
        }
        return null;
    }

	public List<Function> getFunctions() {
        return m_functions;
    }

	public boolean areProfilablePcs(Set<Long> pcs) {
		for (long pc : pcs) {
			boolean found = false;
			for (Function function : m_functions){
				if (function.containsPc(pc)){
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	public Function getFunction(long pc) {
		return m_pcToFunction.get(pc);
	}

	public String getFunctionFileName(String string) {
		return m_functionToFile.get(string);
	}

	public InstructionNode getInstructionNode(long pc) {
		return m_instructionDecoder.getNode(pc);
	}

    private void mapSourceFileLines(DebugEngine debugEngine, SourceFile sourceFile) {
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

	private Collection<Long> rangesToAddresses(Collection<Pair<Long, Long>> addressRanges) {
    	Set<Long> addresses = new TreeSet<Long>();
		for (Pair<Long, Long> range : addressRanges) {
			long endPc = m_instructionDecoder.getEndPc();
			for (long pc = range.first; (pc < range.second) && (pc <= endPc); pc += 2) {
				InstructionNode node = m_instructionDecoder.getNode(pc);
				if (node != null) {
					addresses.add(pc);
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

    	for (InstructionNode inst : m_starts) {
    		int startIndex = instList.indexOf(inst);
    		List<AssemblyLine> functionAssembly = new ArrayList<AssemblyLine>();
    		functionAssembly.add(m_instructions.get(startIndex));
    		int index = startIndex +1;
    		while (index < instList.size() && !m_starts.contains(instList.get(index))) {
    			functionAssembly.add(m_instructions.get(index));
    			index++;
    		}

    		String fnName = getFunctionName(inst);
    		Function function = new Function(fnName, functionAssembly);
    		for (Line assembly : functionAssembly) {
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

    	for (String functionName : m_functionNames.keySet()) {
    		for (InstructionNode fnInst : m_functionNames.get(functionName)) {
				if (fnInst.getStartPc() == inst.getStartPc()) {
					return functionName;
				}
    		}
		}
		//FIXME
		System.err.println("cound not find the function corresponding to " + inst.getXtaInstruction());

		assert(false);
		return "";
	}
}
