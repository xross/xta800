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

import com.xmos.libs.dwarfhandler.DebugEngine;
import com.xmos.libs.dwarfhandler.DebugFile;
import com.xmos.libs.xeloader.DebugInfo;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.FunctionContainer;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;

public class SourceMapper {
	private long m_xeLastModified;
    private List<Function> m_functions;
	private List<SourceFile> m_sourceFiles;
	private List<AssemblyLine> m_assemblyLines;
	private InstructionDecoder m_instructionDecoder;
	private Set<FunctionContainer> m_functionContainers;
	private Map<Long, AssemblyLine> m_pcToAsm;
	private Map<Long, Function> m_pcToFunction;
	private Map<String, Set<Long>> m_labels;

    public SourceMapper(CommandSource src, Engine engine, XeLoader xeLoader, int nodeNumber, int coreNumber,
			Set<InstructionNode> starts, InstructionDecoder instructionDecoder,
			Set<FunctionContainer> functionContainers, Map<String, Set<Long>> labels) throws AbortedException {
    	
    	m_labels = labels;
        m_instructionDecoder = instructionDecoder;
        m_functionContainers = functionContainers;
        m_pcToAsm = new TreeMap<Long, AssemblyLine>();
        m_pcToFunction = new TreeMap<Long, Function>();

        m_assemblyLines = createAssemblyLines();
        m_functions = createFunctions(starts);

        // Must record the XE timestamp before loading files
        m_xeLastModified = xeLoader.getFile().lastModified();
        m_sourceFiles = createSourceFiles(src, engine, xeLoader, nodeNumber, coreNumber);
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

		checkSourceFileDate(src, notifier, file);
    }

    public boolean fileNewerThanBinary(File file) {
    	assert(file != null);
    	long sourceLastModified = file.lastModified();
    	return (sourceLastModified > m_xeLastModified);
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

    private void checkSourceFileDate(CommandSource src, Notifier notifier, File file) {
    	if (fileNewerThanBinary(file)) {
    		notifier.notify(src, Notifier.NOTIFY_WARNING,
    				String.format("Source file '%s' newer than binary", file.getName()));
    	}
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
    		sourceFile = new SourceFile(this, debugFile, debugEngine, m_instructionDecoder);
    	}
		sourceFiles.add(sourceFile);
    	setFile(src, notifier, debugEngine, sourceFile, file);
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

    private List<Function> createFunctions(Set<InstructionNode> starts) {
    	List<InstructionNode> instList = m_instructionDecoder.getInstructions();
    	Map<Long, Function> pcToFunction = new HashMap<Long, Function>();
    	
    	for (InstructionNode inst : starts) {    		
    		int startIndex = instList.indexOf(inst);
    		List<AssemblyLine> functionAssembly = new ArrayList<AssemblyLine>();
    		functionAssembly.add(m_assemblyLines.get(startIndex));
    		int index = startIndex +1;
    		while (index < instList.size() && !starts.contains(instList.get(index))) {
    			functionAssembly.add(m_assemblyLines.get(index));
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
    	for(FunctionContainer fc : m_functionContainers){
    		for(Node node : fc.getFunctionContents()){
    			if(node instanceof InstructionNode && node.getStartPc() == inst.getPC()){
    				return fc.getFunctionName();
    			}
    		}
    	}
		assert(false);
		return "";
	}
}
