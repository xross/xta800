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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.util.IteratableMap;
import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableMap;
import com.xmos.libs.util.NonIteratableSet;
import com.xmos.libs.xeloader.XeLoader;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.CommandSource.Type;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteCoreContainer;
import com.xmos.xta.core.engine.analysis.routes.RouteContainer.RouteMode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;
import com.xmos.xta.core.engine.defines.DefineInfo;
import com.xmos.xta.core.engine.defines.Defines;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceType;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;
import com.xmos.xta.core.engine.sourcemapper.SourceFile;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;
import com.xmos.xta.core.engine.utils.CoreId;
import com.xmos.xta.core.engine.utils.FileNameMatcher;

public class Scripter {
	private Engine m_engine;
	
	private Collection<CoreId> m_initialActiveCores;
	private Map<Reference, List<Reference>> m_initialBranches;
	private Defines m_initialDefines;
	private Map<String, Double> m_initialFrequencies;
	private Map<CoreId, Integer> m_initialNumThreads;
	
	private Collection<CoreId> m_currentActiveCores;
	private Map<Reference, List<Reference>> m_currentBranches;
	private Defines m_currentDefines;
	private Map<String, Double> m_currentFrequencies;
	private Map<CoreId, Integer> m_currentNumThreads;

	// Can't use a Set because the compareTo won't do what we 
	// need for matching references
	private List<ScripterReference> m_uniqueReferences;
	
	// Need to keep track of all references in order to do the line numbering
	private List<ScripterReference> m_allReferences;
	private Set<String> m_autoLabels;
	private Set<String> m_disables;
	private Map<String, String> m_renames;
	private boolean m_embedIntoSource;
	private String m_embedFileName;

	public Scripter(Engine engine) {
		m_engine = engine;

		m_initialActiveCores = new ArrayList<CoreId>();
		m_initialBranches = new NonIteratableMap<Reference, List<Reference>>();
		m_initialDefines = new Defines();
		m_initialFrequencies = new NonIteratableMap<String, Double>();
		m_initialNumThreads = new NonIteratableMap<CoreId, Integer>();		

		m_currentActiveCores = new ArrayList<CoreId>();
		m_currentBranches = new NonIteratableMap<Reference, List<Reference>>();
		m_currentDefines = new Defines();
		m_currentFrequencies = new NonIteratableMap<String, Double>();
		m_currentNumThreads = new NonIteratableMap<CoreId, Integer>();		
		
		m_autoLabels = new NonIteratableSet<String>();
		m_disables = new IteratableSet<String>();
		m_renames = new IteratableMap<String, String>();
		m_uniqueReferences = null;
		m_allReferences = null;
		
		m_embedIntoSource = false;
		m_embedFileName = null;
	}

	public void setEngineLoadAndRunDone() {
		m_initialActiveCores = new ArrayList<CoreId>(m_engine.getActiveCores());
		m_initialBranches = new NonIteratableMap<Reference, List<Reference>>(m_engine.getUserBranches());
		m_initialDefines = new Defines(m_engine.getDefines());
		m_initialFrequencies = new NonIteratableMap<String, Double>(m_engine.getFrequencyMap());
		m_initialNumThreads = new NonIteratableMap<CoreId, Integer>(m_engine.getNumThreadMap());	
		
		m_disables.clear();
		m_autoLabels.clear();
		m_renames.clear();

		m_uniqueReferences = null;
		m_allReferences = null;
		m_embedIntoSource = false;
		m_embedFileName = null;		
	}

	public String dump() {
		resetState();
		
		if (m_embedIntoSource) {
			// Ensure it is ok to modify the source file
			if (m_embedFileName == null) {
				return "";
			}
			SourceFile sourceFile = getSourceMapper().getSourceFile(m_embedFileName);
			if ((sourceFile == null) || !sourceFile.okToWrite(getSourceMapper())) {
				return "";
			}
		}
		
		createRefs();
		Map<String, List<Long>> fileLineNumbers = getFileLineNumbers();
		addPragmasToFiles(fileLineNumbers);
		StringBuffer script = createScript();
		if (m_embedIntoSource) {
			addCommandsToFile(m_embedFileName, script);
			return "";
		} else {
			return script.toString();
		}
	}
	
	String getUniqueLabel(String prefix) {
		int i = 0;
		String label = prefix + "_" + Integer.toString(i);
		while (labelExists(label)) {
			i++;
			label = prefix + "_" + Integer.toString(i);
		}
		m_autoLabels.add(label);
		return label;
	}

	private boolean labelExists(String label) {
		if (m_autoLabels.contains(label)) {
			return true;
		}
		
		XeLoader xeLoader = m_engine.getXeLoader();
		for (int nodeChainIndex : xeLoader.getNodeChainIndexes()) {
			for (int coreNumber : xeLoader.getCoreNumbers(nodeChainIndex)) {
				CoreId coreId = new CoreId(xeLoader.getXnLoader(), nodeChainIndex, coreNumber);
				Reference ref = ReferenceFactory.createReference(label, ReferenceClass.ANY, coreId);
				if (ref.resolve(m_engine)) {
					return true;
				}
			}
		}
		return false;
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

	public boolean anyModifiableReferences() {
		for (ScripterReference scripterReference : m_uniqueReferences) {
			if (scripterReference.isModifiable()) {
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
	
	       		for (Route route : routeCoreContainer.getRoutes()) {
	       			for (Unknown unknown : route.getRouteUnknowns().getUnknowns(State.KNOWN)) {
	       				if (unknown.getSource() == Unknown.Source.SET) {
		       				for (Reference ref : unknown.getReferences()) {
		       					addReference(ref, unknown, route);
		       				}
	       				}	       				
	       			}
	    		}
       		}
       		
       		for (Reference ref : routeContainer.getBranches().keySet()) {
       			addReference(ref);
       			addReferences(routeContainer.getBranches().get(ref));
       		}

       		addDefineReferences(routeContainer.getDefines());
    	}

    	// Apply all current disables and renames
    	disableReferences();
    	renameReferences();

    	// Disable those which can't be done due to source file issues
    	Map<String, List<Long>> fileLineNumbers = getFileLineNumbers();
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
			script.append(String.format(commentString() + " Warning: '%s' is a non-portable reference.\n",
					getReferenceString(baseRef)));
		}
    }

	public boolean getEmbedIntoSource() {
		return m_embedIntoSource;
	}

	public void setEmbedIntoSource(boolean embedIntoSource) {
		m_embedIntoSource = embedIntoSource;
	}

	public void setEmbedFileName(String embedFileName) {
		m_embedFileName = embedFileName;
	}

    public void addScriptCommand(StringBuffer script, String command) {
    	// All commands should be single line
    	assert(command.lastIndexOf("\n") == -1);
    	
		if (m_embedIntoSource) {
			script.append("#pragma xta command \"" + command + "\"\n"); 		
		} else {
			script.append(command + "\n");
		}
	}
	
	Reference createReferenceForUnknown(Unknown unknown) {
		String refString = unknown.getNode().getReferenceStr();
		CoreId coreId = unknown.getRoute().getRouteCoreContainer().getCoreId();
		Reference ref = ReferenceFactory.createReference(refString, ReferenceClass.ANY, coreId);
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
 	
		if (!m_embedIntoSource) {
			script.append(String.format("# load \"%s\"\n", m_engine.getExecutable().toString()));
		}

    	for (RouteContainer routeContainer : m_engine.getRouteContainers()) {
        	if ((routeContainer.getCommandSrc().getType() == Type.SRC_LEVEL_COMMAND) ||
        	    (routeContainer.getNumExistingRoutes() <= 0))
        		continue;
    		
       		script.append("\n");
       		routeComment(routeContainer, script);
       		updateFrequencies(routeContainer, script);
       		updateNumThreads(routeContainer, script);       		
        	addBranches(routeContainer, script);
        	addDefines(routeContainer, script);
       		updateActiveCores(routeContainer, script);
        	analyzeRoutes(routeContainer, script);
        	removeRoutes(routeContainer, script);
        	setKnowns(routeContainer, script);
        	setRequired(routeContainer, script);  
    	}    	
    	script.append("\n");
		return script;
    }
 	
	private void routeComment(RouteContainer routeContainer, StringBuffer script) {
    	script.append(commentString() + "\n");
    	switch (routeContainer.getMode()) {
    	case FUNCTION:
    		script.append(String.format(commentString() + " Route: %s\n", routeContainer.getFunctionName()));
    		break;

    	case PATH:
        	RouteCoreContainer routeCoreContainer = routeContainer.getRouteCoreContainers().iterator().next();
    		script.append(String.format(commentString() + " Route: %s => %s\n",
		    		getReferenceString(routeCoreContainer.getFromReference()),
		    		getReferenceString(routeCoreContainer.getToReferences().iterator().next())));
    		break;
    	}
    	script.append(commentString() + "\n");
	}
	
	private void updateActiveCores(RouteContainer routeContainer, StringBuffer script) {
    	Collection<CoreId> activeCores = routeContainer.getActiveCores();

    	if (!m_currentActiveCores.equals(activeCores)) {
    		addScriptCommand(script, "remove core *");
    		for (CoreId activeCore : activeCores) {
    			addScriptCommand(script, String.format("add core %s", activeCore.getCoreReference()));
    		}
    		m_currentActiveCores.clear();
    		m_currentActiveCores.addAll(activeCores);
    	}
    }

    private void updateFrequencies(RouteContainer routeContainer, StringBuffer script) {
    	Map<String, Double> frequencies = routeContainer.getFrequencies();
    	
    	Collection<CoreId> activeCores = routeContainer.getActiveCores();
    	for (CoreId activeCore : activeCores) {
    		String nodeId = activeCore.getNodeId();
    		
    		Double frequency = frequencies.get(nodeId);
    		Double currentFrequency = m_currentFrequencies.get(nodeId);

    		if (!currentFrequency.equals(frequency)) {
    			addScriptCommand(script, String.format("config freq %s %f", nodeId, frequency));    			
    			m_currentFrequencies.put(nodeId, frequency);
    		}
    	}
    }
        
	private void updateNumThreads(RouteContainer routeContainer, StringBuffer script) {
    	Map<CoreId, Integer> numThreadsMap = routeContainer.getNumThreads();

    	Collection<CoreId> activeCores = routeContainer.getActiveCores();    	
    	for (CoreId activeCore : activeCores) {
            int numThreads = numThreadsMap.get(activeCore);
            int currentNumThreads = m_currentNumThreads.get(activeCore);
    		
    		if (currentNumThreads != numThreads) {
    			addScriptCommand(script, String.format("config threads %s %d", activeCore.getCoreReference(), numThreads));    			
    			m_currentNumThreads.put(activeCore, numThreads);
    		}
    	}		
	}
    
	private void addBranches(RouteContainer routeContainer, StringBuffer script) {
    	AddScripter scripter = new AddScripter(this);
    	scripter.addBranches(routeContainer.getBranches(), 
    			m_currentBranches, script, routeContainer.getActiveCores());		
    	
    	m_currentBranches = routeContainer.getBranches();    
    }

	private void addDefines(RouteContainer routeContainer, StringBuffer script) {		
    	AddScripter scripter = new AddScripter(this);
    	scripter.addDefines(routeContainer.getDefines(), 
    			m_currentDefines, script, routeContainer.getActiveCores());
    	
		m_currentDefines = routeContainer.getDefines();
	}

    private void analyzeRoutes(RouteContainer routeContainer, StringBuffer script) {
    	
    	// Uses the first core container in the list, as from the string point of view,
    	// all the references should be the same (i.e. they only differ with respect
    	// to the node/core that they refer to).
    	RouteCoreContainer routeCoreContainer = routeContainer.getRouteCoreContainers().iterator().next();
    	assert(routeCoreContainer != null);
    	
    	switch (routeContainer.getMode()) {
    	case FUNCTION:
		    outputPortableWarnings(script, routeCoreContainer.getFunctionReference());

		    addScriptCommand(script, String.format("analyze function %s",
		    		getReferenceString(routeCoreContainer.getFunctionReference())));
    		break;

    	case PATH:
		    outputPortableWarnings(script, routeCoreContainer.getFromReference());
		    outputPortableWarnings(script, routeCoreContainer.getToReferences().iterator().next());
		    addScriptCommand(script, String.format("analyze endpoints %s %s",
		    		getReferenceString(routeCoreContainer.getFromReference()),
		    		getReferenceString(routeCoreContainer.getToReferences().iterator().next())));
    		break;
    	}
    }

	private void removeRoutes(RouteContainer routeContainer, StringBuffer script) {
    	for (RouteCoreContainer routeCoreContainer : routeContainer.getRouteCoreContainers()) {
       		for (Route route : routeCoreContainer.getRoutes()) {
      			if (!routeCoreContainer.routeExists(route)) {
      				addScriptCommand(script, String.format("remove route %d", route.getRouteId()));
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
		Set<String> toRemove = new IteratableSet<String>();
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
		Set<String> toRemove = new IteratableSet<String>();
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
		for (DefineInfo info : defines.getDefines()) {
			for (Reference reference : info.getReferences()) {
				addReference(reference);
			}
		}
	}

	private void addReferences(Collection<Reference> fromRefs) {
		for (Reference ref : fromRefs) {
			addReference(ref);
		}
	}

	private void addReference(Reference ref, Unknown unknown, Route route) {
		if (ref == null) {
			ref = createReferenceForUnknown(unknown);
		} 
		addReference(ref, unknown.getType(), unknown.getNode());
	}

	private void addReference(Reference ref) {
		addReference(ref, UnknownFactory.Type.OTHER, null);
	}

	private void addReference(Reference ref, UnknownFactory.Type type, Node node) {
		assert(ref != null);

		for (ReferenceType baseRef : ref.getBackTrail()) {
			addReference(baseRef, UnknownFactory.Type.OTHER, null);
		}
		addReference(ref.getBase(), type, node);
	}

	private void addReference(ReferenceType baseRef, UnknownFactory.Type type, Node node) {
		for (ScripterReference ref : m_allReferences) {
			if (ref.getBaseReference() == baseRef) {
				return;
			}
		}
		
		ScripterReference newRef = new ScripterReference(this, m_engine, baseRef, type, node);
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

	private void disableRefsForInvalidFiles(Map<String, List<Long>> fileLineNumbers) {
		for (String fileName : fileLineNumbers.keySet()) {
			SourceFile sourceFile = getSourceFile(fileName);

			if ((sourceFile == null) || !sourceFile.okToWrite(getSourceMapper())) {
				disableRefsForFile(fileName);
			}
		}
	}

	public void disableRefsForFile(String fileName) {
		for (ScripterReference scripterReference : m_uniqueReferences) {
			String refFileName = scripterReference.getFileName();
			if (FileNameMatcher.isMatch(fileName, refFileName, getSourceMapper())) {
				setEnable(scripterReference, false);
				scripterReference.setModifiable(false);
			}
		}
	}

	private void addPragmasToFiles(Map<String, List<Long>> fileLineNumbers) {
		for (String fileName : fileLineNumbers.keySet()) {
			SourceFile sourceFile = getSourceFile(fileName);
			if (sourceFile == null) {
				continue;
			}
			
			List<Long> lineNumbers = fileLineNumbers.get(fileName);

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
			StringBuffer fileContents = new StringBuffer();
			String line = "";
			long lineNumber = 1;
			long offset = 0;
			while ((line = in.readLine()) != null) {
				if (lineNumbers.contains(lineNumber)) {
					ScripterReference scripterReference = getReference(sourceFile, lineNumber + offset);
					if (scripterReference.isModifiable() && scripterReference.isEnabled()) {
						String pragma = getPragmaString(scripterReference);
						String indent = getIndent(line);
						fileContents.append(indent);
						fileContents.append(pragma);
						fileContents.append("\n");
						sourceLineInserted(sourceFile, lineNumber);
						offset += 1;
					}
				}
				fileContents.append(line);
				fileContents.append("\n");
				lineNumber++;
			}
			in.close();
			
			BufferedWriter out = new BufferedWriter(new FileWriter(sourceFile.getFile()));
			out.write(fileContents.toString());
            out.close();
		} catch(IOException e) {
		}
	}
	
	private void addCommandsToFile(String fileName, StringBuffer script) {
		try {
			SourceFile sourceFile = getSourceMapper().getSourceFile(fileName);
			assert(sourceFile != null);
			assert(sourceFile.getFile() != null);
			
			BufferedReader in = new BufferedReader(new FileReader(sourceFile.getFile()));
			StringBuffer fileContents = new StringBuffer();
			String line = "";
			while ((line = in.readLine()) != null) {
				fileContents.append(line + "\n");
			}
			in.close();
			
			fileContents.append("\n" + script);
			BufferedWriter out = new BufferedWriter(new FileWriter(sourceFile.getFile()));
			out.write(fileContents.toString());
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

	private Map<String, List<Long>> getFileLineNumbers() {
		Map<String, List<Long>> fileLineNumbers = new IteratableMap<String, List<Long>>();

		for (ScripterReference scripterReference : m_uniqueReferences) {
			String fileName = getFileName(scripterReference);
			if (!fileName.equals("")) {
				List<Long> lineNumbers = fileLineNumbers.get(fileName);
				if (lineNumbers == null) {
					lineNumbers = new LinkedList<Long>();
					fileLineNumbers.put(fileName, lineNumbers);
				}
				lineNumbers.add(scripterReference.getLineNum());
			}
		}	
		return fileLineNumbers;
	}
	
	private SourceMapper getSourceMapper() {
		return m_engine.getSourceMapper();
	}

    private String commentString() {
		if (m_embedIntoSource) {
			return "//";
		} else {
			return "#";
		}
	}

    private SourceFile getSourceFile(String fileName) {
		SourceFile sourceFile = m_engine.getSourceMapper().getSourceFile(fileName);
		if (sourceFile != null) {
			return sourceFile;
		}
		return null;
	}

	// Ensure that all valid representations of a file name are mapped to the same string
	private String getFileName(ScripterReference scripterReference) {
		String fileName = scripterReference.getFileName();
		SourceFile sourceFile = getSourceFile(fileName);
		if (sourceFile == null) {
			return fileName;
		}
		return sourceFile.getFullFileName();
	}
    
    private void resetState() {
		m_currentActiveCores = new ArrayList<CoreId>(m_initialActiveCores);
		m_currentBranches = new NonIteratableMap<Reference, List<Reference>>(m_initialBranches);
		m_currentDefines = new Defines(m_initialDefines);
		m_currentFrequencies = new NonIteratableMap<String, Double>(m_initialFrequencies);
		m_currentNumThreads = new NonIteratableMap<CoreId, Integer>(m_initialNumThreads);			
    }
}
