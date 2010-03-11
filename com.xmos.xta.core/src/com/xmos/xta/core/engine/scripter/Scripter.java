package com.xmos.xta.core.engine.scripter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.CommandSource.Type;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteCoreContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer.RouteMode;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;
import com.xmos.xta.core.engine.defines.Defines;
import com.xmos.xta.core.engine.defines.InstructionTimeInfo;
import com.xmos.xta.core.engine.defines.LoopInfo;
import com.xmos.xta.core.engine.defines.LoopPathInfo;
import com.xmos.xta.core.engine.defines.LoopScopeInfo;
import com.xmos.xta.core.engine.defines.PathTimeInfo;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceType;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.sourcemapper.SourceFile;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;
import com.xmos.xta.core.engine.utils.FileNameMatcher;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class Scripter {
	private Engine m_engine;
	private Collection<NodeCorePair> m_currentActiveCores;
	private Collection<Reference> m_currentExclusions;
	private Map<Reference, List<Reference>> m_currentBranches;
	private Defines m_currentDefines;
	private Map<Integer, Double> m_currentFrequencies;
	private Map<NodeCorePair, Integer> m_currentNumThreads;

	// Can't use a Set because the compareTo won't do what we 
	// need for matching references
	List<ScripterReference> m_uniqueReferences;
	
	// Need to keep track of all references in order to do the line numbering
	List<ScripterReference> m_allReferences;
	private Set<String> m_autoLabels;
	private Set<String> m_disables;
	private Map<String, String> m_renames;

	public Scripter(Engine engine) {
		m_engine = engine;
		m_currentActiveCores = new ArrayList<NodeCorePair>();
		m_currentExclusions = new ArrayList<Reference>();
		m_currentDefines = new Defines();
		m_currentBranches = new TreeMap<Reference, List<Reference>>();
		m_autoLabels = new TreeSet<String>();
		m_disables = new TreeSet<String>();
		m_renames = new TreeMap<String, String>();
		m_currentFrequencies = new HashMap<Integer, Double>();
		m_currentNumThreads = new HashMap<NodeCorePair, Integer>();
		m_uniqueReferences = null;
		m_allReferences = null;
	}

	public void setEngineLoadAndRunDone() {
		m_currentActiveCores = new ArrayList<NodeCorePair>(m_engine.getActiveCores());
		m_currentExclusions = new ArrayList<Reference>(m_engine.getExclusions());
		m_currentDefines = new Defines(m_engine.getDefines());
		m_currentFrequencies = new HashMap<Integer, Double>(m_engine.getFrequencyMap());
		m_currentNumThreads = new HashMap<NodeCorePair, Integer>(m_engine.getNumThreadMap());		
		m_uniqueReferences = null;
		m_allReferences = null;
	}

	public String dump() {
		createRefs();
		Map<SourceFile, List<Long>> fileLineNumbers = getFileLineNumbers();
		addPragmasToFiles(fileLineNumbers);
		StringBuffer script = createScript();
    	return script.toString();
	}
	
	public String getUniqueLabel() {
		int i = 0;
		String label = "label_" + Integer.toString(i);
		while (m_autoLabels.contains(label)) {
			i++;
			label = "label_" + Integer.toString(i);
		}
		m_autoLabels.add(label);
		return label;
	}

	public void enable(CommandSource src, Notifier notifier, String ref) {
		if (ref.equals("*")) {
			for (ScripterReference scripterReference : m_uniqueReferences) {
				setEnable(scripterReference, true);
			}			
		} else {
			ScripterReference scripterReference = getReference(ref);
			if (scripterReference != null) {
				setEnable(scripterReference, true);
			} else {
				notifier.notify(src, Notifier.NOTIFY_ERROR,
						String.format("Reference '%s' not used in scripter", ref));
			}			
		}		
	}

	public void disable(CommandSource src, Notifier notifier, String ref) {
		if (ref.equals("*")) {
			for (ScripterReference scripterReference : m_uniqueReferences) {
				setEnable(scripterReference, false);
			}			
		} else {
			ScripterReference scripterReference = getReference(ref);
			if (scripterReference != null) {
				setEnable(scripterReference, false);
			} else {
				notifier.notify(src, Notifier.NOTIFY_ERROR,
						String.format("Reference '%s' not used in scripter", ref));
			}			
		}		
	}

	public void setEnable(ScripterReference scripterReference, boolean enable) {
		scripterReference.setEnable(enable);
		if (enable) {
			m_disables.remove(scripterReference.getRef());
		} else {
			m_disables.add(scripterReference.getRef());
		}	
	}

	public void rename(CommandSource src, Notifier notifier, String from, String to) {
		ScripterReference scripterReference = getReference(from);
		if (scripterReference == null) {
			notifier.notify(src, Notifier.NOTIFY_ERROR,
					String.format("Reference '%s' not used in scripter", from));

		} else if (!scripterReference.isModifiable()) {
			notifier.notify(src, Notifier.NOTIFY_ERROR,
					String.format("Reference '%s' cannot be renamed", from));

		} else {
			rename(scripterReference, from, to);
		}
	}

	public void rename(ScripterReference scripterReference, String from, String to) {
		scripterReference.rename(to);
		m_renames.put(from, to);
	}

	public Collection<ScripterReference> getRefs() {
		assert(m_uniqueReferences != null);
		return m_uniqueReferences;
	}

	public boolean promptUser() {
		for (ScripterReference scripterReference : m_uniqueReferences) {
			if (scripterReference.isModifiable() && scripterReference.isEnabled()) {
				return true;
			}
		}
		return false;
	}

	public void createRefs() {
		m_autoLabels.clear();
		m_uniqueReferences = new LinkedList<ScripterReference>();
		m_allReferences = new LinkedList<ScripterReference>();

		// Create references for the existing routes
		for (RouteContainer routeContainer : m_engine.getRouteContainers()) {
        	if ((routeContainer.getCommandSrc().getType() == Type.SRC_LEVEL_COMMAND) ||
        	    (routeContainer.getNumExistingRoutes() <= 0))
        		continue;

       		for (RouteCoreContainer routeCoreContainer : routeContainer.getRouteCoreContainers()) {
       			
	       		if (routeContainer.getMode() == RouteMode.PATH) {
	       			addReference(routeCoreContainer.getFromReference());
	       			addReferences(routeCoreContainer.getToReferences());
	       		} else {
	       			addReference(routeCoreContainer.getFunctionReference());
	       		}
	
	       		for (Reference ref : routeContainer.getBranches().keySet()) {
	       			addReference(ref);
	       			addReferences(routeContainer.getBranches().get(ref));
	       		}
	
	       		addDefineReferences(routeContainer.getDefines());
	
	        	for (Reference ref : routeContainer.getExclusions()) {
	        		addReference(ref);
	        	}
	
	       		for (Route route : routeCoreContainer.getRoutes()) {
	       			for (Unknown unknown : route.getRouteUnknowns().getUnknowns(State.KNOWN)) {
	       				if (unknown.getSource() == Unknown.Source.SET) {
		       				for (Reference ref : unknown.getReferences()) {
		       					addReference(ref, unknown);
		       				}
	       				}	       				
	       			}
	    		}
       		}
    	}

    	// Apply all current disables and renames
    	disableReferences();
    	renameReferences();

    	// Disable those which can't be done due to source file issues
    	Map<SourceFile, List<Long>> fileLineNumbers = getFileLineNumbers();
    	disableRefsForInvalidFiles(fileLineNumbers);
	}

    public void outputPortableWarnings(StringBuffer script, Reference ref) {
		for (ReferenceType baseRef : ref.getBackTrail()) {
			outputPortableWarnings(script, baseRef);
		}
		outputPortableWarnings(script, ref.getBase());
    }

    public void outputPortableWarnings(StringBuffer script, ReferenceType baseRef) {
    	ScripterReference scripterReference = getReference(baseRef.getScripterRef());
		if (scripterReference.isPortable()) {
			return;
		}
		
		if (!(scripterReference.isModifiable() && scripterReference.isEnabled())) {
			// Only emit the warning if the reference is not being mapped to a new value
			script.append(String.format("# Warning: '%s' is a non-portable reference.\n",
				getReferenceString(baseRef)));
		}
    }

    Reference createReferenceForUnknown(Unknown unknown) {
		String refString = unknown.getNode().getReferenceStr();
		NodeCorePair nodeCore = unknown.getRoute().getRouteCoreContainer().getNodeCore();
		Reference ref = ReferenceFactory.createReference(refString, ReferenceClass.ANY_REF,
				nodeCore.getNode(), nodeCore.getCore());
		return ref;
	}
    
	String getReferenceString(Reference ref) {
    	StringBuffer str = new StringBuffer();
    	for (ReferenceType backTrail : ref.getBackTrail()) {
    		str.append(getReferenceString(backTrail));
    		str.append(",");
    	}
    	str.append(getReferenceString(ref.getBase()));
    	return str.toString();
	}
    
	private StringBuffer createScript() {
		StringBuffer script = new StringBuffer();
 	
    	script.append(String.format("load \"%s\"\n", m_engine.getExecutable().toString()));
    	    	    	
    	for (RouteContainer routeContainer : m_engine.getRouteContainers()) {
        	if ((routeContainer.getCommandSrc().getType() == Type.SRC_LEVEL_COMMAND) ||
        	    (routeContainer.getNumExistingRoutes() <= 0))
        		continue;
    		
       		script.append("\n");
       		routeComment(routeContainer, script);
       		updateFrequencies(routeContainer, script);
       		updateNumThreads(routeContainer, script);       		
        	addBranches(routeContainer, script);
        	addExclusions(routeContainer, script);
        	addDefines(routeContainer, script);
       		updateActiveCores(routeContainer, script);
        	analyseRoutes(routeContainer, script);
        	removeRoutes(routeContainer, script);
        	setKnowns(routeContainer, script);
        	setRequired(routeContainer, script);  
    	}    	
    	script.append("\n");
		return script;
    }
 	
	private void routeComment(RouteContainer routeContainer, StringBuffer script) {
    	script.append("#\n");
    	switch (routeContainer.getMode()) {
    	case FUNCTION:
    		script.append(String.format("# Route: %s\n", routeContainer.getFunctionName()));
    		break;

    	case PATH:
        	RouteCoreContainer routeCoreContainer = routeContainer.getRouteCoreContainers().iterator().next();
    		script.append(String.format("# Route: %s => %s\n",
		    		getReferenceString(routeCoreContainer.getFromReference()),
		    		getReferenceString(routeCoreContainer.getToReference())));
    		break;
    	}
    	script.append("#\n");
	}
	
    private void updateActiveCores(RouteContainer routeContainer, StringBuffer script) {
    	Collection<NodeCorePair> activeCores = routeContainer.getActiveCores();

    	if (!m_currentActiveCores.equals(activeCores)) {
    		script.append("remove core *\n");
    		for (NodeCorePair activeCore : activeCores) {
    			script.append(String.format("add core %d %d\n", activeCore.getNode(), activeCore.getCore()));
    		}
    		m_currentActiveCores.clear();
    		m_currentActiveCores.addAll(activeCores);
    	}
    }

    private void updateFrequencies(RouteContainer routeContainer, StringBuffer script) {
    	Map<Integer, Double> frequencies = routeContainer.getFrequencies();
    	
    	Collection<NodeCorePair> activeCores = routeContainer.getActiveCores();
    	for (NodeCorePair activeCore : activeCores) {
    		int nodeNumber = activeCore.getNode();
    		
    		Double frequency = frequencies.get(nodeNumber);
    		Double currentFrequency = m_currentFrequencies.get(nodeNumber);

    		if (!currentFrequency.equals(frequency)) {
    			script.append(String.format("config freq %d %f\n", nodeNumber, frequency));    			
    			m_currentFrequencies.put(nodeNumber, frequency);
    		}
    	}
    }
        
	private void updateNumThreads(RouteContainer routeContainer, StringBuffer script) {
    	Map<NodeCorePair, Integer> numThreadsMap = routeContainer.getNumThreads();

    	Collection<NodeCorePair> activeCores = routeContainer.getActiveCores();    	
    	for (NodeCorePair activeCore : activeCores) {
    		int nodeNumber = activeCore.getNode();    		
    		int coreNumber = activeCore.getCore();    		
    		
            int numThreads = numThreadsMap.get(activeCore);
            int currentNumThreads = m_currentNumThreads.get(activeCore);
    		
    		if (currentNumThreads != numThreads) {
    			script.append(String.format("config threads %d %d %d\n", nodeNumber, coreNumber, numThreads));    			
    			m_currentNumThreads.put(activeCore, numThreads);
    		}
    	}		
	}
    
    private void addBranches(RouteContainer routeContainer, StringBuffer script) {
    	AddScripter scripter = new AddScripter(this);
    	scripter.addBranches(routeContainer, m_currentBranches, script);		
    	m_currentBranches = routeContainer.getBranches();    
    }

    private void addExclusions(RouteContainer routeContainer, StringBuffer script) {
    	AddScripter scripter = new AddScripter(this);
    	scripter.addExclusions(routeContainer, m_currentExclusions, script);		
    	m_currentExclusions = routeContainer.getExclusions();
    }

	private void addDefines(RouteContainer routeContainer, StringBuffer script) {		
    	AddScripter scripter = new AddScripter(this);
    	scripter.addDefines(routeContainer, m_currentDefines, script);		
		m_currentDefines = routeContainer.getDefines();
	}

    private void analyseRoutes(RouteContainer routeContainer, StringBuffer script) {
    	
    	// Uses the first core container in the list, as from the string point of view,
    	// all the references should be the same (i.e. they only differ with respect
    	// to the node/core that they refer to).
    	RouteCoreContainer routeCoreContainer = routeContainer.getRouteCoreContainers().iterator().next();
    	assert(routeCoreContainer != null);
    	
    	switch (routeContainer.getMode()) {
    	case FUNCTION:
		    outputPortableWarnings(script, routeCoreContainer.getFunctionReference());

		    script.append(String.format("analyse function %s\n",
		    		getReferenceString(routeCoreContainer.getFunctionReference())));
    		break;

    	case PATH:
		    outputPortableWarnings(script, routeCoreContainer.getFromReference());
		    outputPortableWarnings(script, routeCoreContainer.getToReference());

		    script.append(String.format("analyse path %s %s\n",
		    		getReferenceString(routeCoreContainer.getFromReference()),
		    		getReferenceString(routeCoreContainer.getToReference())));
    		break;
    	}
    }

	private void removeRoutes(RouteContainer routeContainer, StringBuffer script) {
    	for (RouteCoreContainer routeCoreContainer : routeContainer.getRouteCoreContainers()) {
       		for (Route route : routeCoreContainer.getRoutes()) {
      			if (!routeCoreContainer.routeExists(route)) {
      				script.append(String.format("remove route %d\n", route.getRouteId()));
   		    	}
       		}
   		}
    }

    private void setKnowns(RouteContainer routeContainer, StringBuffer script) { 
    	SetScripter scripter = new SetScripter(this);
    	scripter.setKnowns(routeContainer, script);
    }

    private void setRequired(RouteContainer routeContainer, StringBuffer script) {
    	SetScripter scripter = new SetScripter(this);
    	scripter.setRequired(routeContainer, script);    	
    }

	private void disableReferences() {
		Set<String> toRemove = new TreeSet<String>();
		for (String disable : m_disables) {
			ScripterReference ref = getReference(disable);
			if (ref == null) {
				toRemove.add(disable);
			} else {
				ref.setEnable(false);
			}
		}
		m_disables.removeAll(toRemove);
	}

	private void renameReferences() {
		Set<String> toRemove = new TreeSet<String>();
		for (String rename : m_renames.keySet()) {
			ScripterReference ref = getReference(rename);
			if (ref == null) {
				toRemove.add(rename);
			} else {
				ref.rename(m_renames.get(rename));
			}
		}

		for (String remove : toRemove) {
			m_renames.remove(remove);
		}
	}

	private void addDefineReferences(Defines defines) {

		for (InstructionTimeInfo info : defines.getInstructionTimes()) {
			addReference(info.getReference());
		}
		for (LoopPathInfo info : defines.getLoopPaths()) {
			addReference(info.getReference());
		}
		for (LoopInfo info : defines.getLoops()) {
			addReference(info.getReference());
		}
		for (LoopScopeInfo info : defines.getLoopScopes()) {
			addReference(info.getReference());
		}
		for (PathTimeInfo info : defines.getPathTimes()) {
			addReference(info.getFrom());
			addReference(info.getTo());
		}
	}

	private void addReferences(Collection<Reference> fromRefs) {
		for (Reference ref : fromRefs) {
			addReference(ref);
		}
	}

	private void addReference(Reference ref, Unknown unknown) {
		if (ref == null) {
			ref = createReferenceForUnknown(unknown);
		} 
		addReference(ref);
	}

	private void addReference(Reference ref) {
		assert(ref != null);

		for (ReferenceType baseRef : ref.getBackTrail()) {
			addReference(baseRef);
		}
		addReference(ref.getBase());
	}

	private void addReference(ReferenceType baseRef) {
		for (ScripterReference ref : m_allReferences) {
			if (ref.getBaseReference() == baseRef) {
				return;
			}
		}
		
		ScripterReference newRef = new ScripterReference(this, m_engine, baseRef);
		m_allReferences.add(newRef);
		
		ScripterReference scripterReference = getReference(baseRef.getScripterRef());
		if (scripterReference == null) {
			m_uniqueReferences.add(newRef);
		}
	}

	private ScripterReference getReference(String ref) {
		for (ScripterReference scripterReference : m_uniqueReferences) {
			if (scripterReference.getBaseReference().matches(ref, m_engine)) {
				return scripterReference;
			}
		}
		return null;
	}

    private String getReferenceString(ReferenceType baseRef) {
    	ScripterReference scripterReference = getReference(baseRef.getScripterRef());
    	if (scripterReference == null) {
    		return baseRef.getRef();
    	}

    	return scripterReference.getReferenceString();
	}

	private void disableRefsForInvalidFiles(Map<SourceFile, List<Long>> fileLineNumbers) {
		SourceMapper sourceMapper = getSourceMapper();
		for (SourceFile sourceFile : fileLineNumbers.keySet()) {
			File file = sourceFile.getFile();
			if ((file == null) || !file.canWrite() || sourceMapper.fileNewerThanBinary(file)) {
				disableRefsForFile(sourceFile.getFileName());
			}
		}
	}

	private void disableRefsForFile(String fileName) {
		for (ScripterReference scripterReference : m_uniqueReferences) {
			String refFileName = scripterReference.getFileName();
			if (FileNameMatcher.isMatch(fileName, refFileName, getSourceMapper())) {
				scripterReference.setEnable(false);
				scripterReference.setModifiable(false);
			}
		}
	}

	private void addPragmasToFiles(Map<SourceFile, List<Long>> fileLineNumbers) {
		for (SourceFile sourceFile : fileLineNumbers.keySet()) {
			List<Long> lineNumbers = fileLineNumbers.get(sourceFile);

			if (!fileContainsActivePragmas(sourceFile, lineNumbers)) {
				continue;
			}
			
			boolean ok = backupFile(sourceFile);
			if (!ok) {
				disableRefsForFile(sourceFile.getFullFileName());
				continue;
			}
			
			Collections.sort(lineNumbers);
			addPragmasToFile(sourceFile, lineNumbers);
		}
	}

	private boolean fileContainsActivePragmas(SourceFile sourceFile, List<Long> lineNumbers) {
		for (long lineNumber : lineNumbers) {
			ScripterReference scripterReference = getReference(sourceFile, lineNumber);
			if (scripterReference.isModifiable() && scripterReference.isEnabled()) {
				return true;
			}
		}
		return false;
	}

	private void addPragmasToFile(SourceFile sourceFile, List<Long> lineNumbers) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(sourceFile.getFile()));
			String fileContents = "";
			String line = "";
			long lineNumber = 1;
			long offset = 0;
			while ((line = in.readLine()) != null) {
				if (lineNumbers.contains(lineNumber)) {
					ScripterReference scripterReference = getReference(sourceFile, lineNumber + offset);
					if (scripterReference.isModifiable() && scripterReference.isEnabled()) {
						String pragma = getPragmaString(scripterReference);
						String indent = getIndent(line);
						fileContents += indent + pragma + "\n";
						sourceLineInserted(sourceFile, lineNumber);
						offset += 1;
					}
				}
				fileContents += line + "\n";
				lineNumber++;
			}
			in.close();
			
			BufferedWriter out = new BufferedWriter(new FileWriter(sourceFile.getFile()));
			out.write(fileContents);
            out.close();
		} catch(IOException e) {
		}
	}

	private void sourceLineInserted(SourceFile sourceFile, long lineNumber) {
		SourceMapper sourceMapper = getSourceMapper();
		for (ScripterReference scripterReference : m_allReferences) {
			scripterReference.sourceLineInserted(sourceFile, lineNumber, sourceMapper);
		}
	}

	private String getIndent(String line) {
		String indent = line.replaceAll("[^\\s].*$", "");
		return indent;
	}

	private ScripterReference getReference(SourceFile sourceFile, long lineNumber) {
		SourceMapper sourceMapper = getSourceMapper();
		String sourceFileName = sourceFile.getFullFileName();

		for (ScripterReference scripterReference : m_uniqueReferences) {
			if (FileNameMatcher.isMatch(sourceFileName, scripterReference.getFileName(), sourceMapper) && 
				(scripterReference.getLineNum() == lineNumber)) {
				
				return scripterReference;
			}
		}
		assert(false);
		return null;
	}
	
	private String getPragmaString(ScripterReference scripterReference) {
		String type = "";
		switch (scripterReference.getType()) {
		case ENDPOINT:
			type = "endpoint";
			break;
		case CALL:
			type = "call";
			break;
		case LABEL:
		case SRCLABEL:
			type = "label";
			break;
		default:
			assert(false);
			return "";
		}
		return "#pragma xta " + type + " \"" + scripterReference.getMapsToLabel() + "\"";
	}		

	private boolean backupFile(SourceFile sourceFile) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(sourceFile.getFile()));
			File f2 = new File(sourceFile.getFullFileName() + ".bak");
			BufferedWriter out = new BufferedWriter(new FileWriter(f2));
			String s = "";
			int lineCount = 1;
			while ((s = in.readLine()) != null) {
				out.write(s);
				out.newLine();
				lineCount++;
			}
			in.close();
            out.close();
		} catch(IOException e) {
			return false;
		}
		return true;
	}

	private Map<SourceFile, List<Long>> getFileLineNumbers() {
		TreeMap<SourceFile, List<Long>> fileLineNumbers = new TreeMap<SourceFile, List<Long>>();

		for (int nodeNumber : m_engine.getXeLoader().getNodeNumbers()) {
			for (int coreNumber : m_engine.getXeLoader().getCoreNumbers(nodeNumber)) {
				
				SourceMapper sourceMapper = m_engine.getSourceMapper(nodeNumber, coreNumber);				
				for (ScripterReference scripterReference : m_uniqueReferences) {
					String fileName = scripterReference.getFileName();
					if (!fileName.equals("")) {
						SourceFile sourceFile = sourceMapper.getSourceFile(fileName);
						if (sourceFile != null){
							List<Long> lineNumbers = fileLineNumbers.get(sourceFile);
							if (lineNumbers == null) {
								lineNumbers = new LinkedList<Long>();
								fileLineNumbers.put(sourceFile, lineNumbers);
							}
							lineNumbers.add(scripterReference.getLineNum());
						}
					}
				}	
			}
		};		
		return fileLineNumbers;
	}
	
	private SourceMapper getSourceMapper() {
		int nodeNumber = m_engine.getXeLoader().getNodeNumbers().get(0);
		int coreNumber = m_engine.getXeLoader().getCoreNumbers(0).get(0);
		return m_engine.getSourceMapper(nodeNumber, coreNumber);
	}
}
