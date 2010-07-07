package com.xmos.xta.core.engine.scripter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableMap;
import com.xmos.xta.core.engine.defines.DefineInfo;
import com.xmos.xta.core.engine.defines.Defines;
import com.xmos.xta.core.engine.defines.ScriptUpdater;
import com.xmos.xta.core.engine.references.Reference;

public class AddScripter {
	private Scripter m_scripter;
	
	AddScripter(Scripter scripter) {
		m_scripter = scripter;
	}
	
    void addDefines(Defines defines, Defines currentDefines, 
    		final StringBuffer script, final Collection<CoreId> activeCores) {
    	    	
    	defines.updateScript(currentDefines, new ScriptUpdater() {
			public void update(
					Collection<? extends DefineInfo> newList,
					Collection<? extends DefineInfo> oldList, 
					String command, String removeStr) {
				
				if (!oldList.isEmpty())
					m_scripter.addScriptCommand(script, "remove " + command + " " + removeStr);

				List<DefineInfo> processed = new ArrayList<DefineInfo>();
				for (DefineInfo info : newList) {
					if (!processed.contains(info)) {
						if (defineSetForAllActiveCores(newList, info, activeCores, processed)) {							
							outputDefineInfo(script, command, info);

						} else {
							outputCoreId(script, info.getCoreId());
							outputDefineInfo(script, command, info);
						}						
						processed.add(info);						
					}					
				}		
			}			
		});
    }
        
    void addBranches(Map<Reference, List<Reference>> branches, Map<Reference, List<Reference>> currentBranches, 
    		StringBuffer script, Collection<CoreId> activeCores) {
  
    	if (!currentBranches.equals(branches)) {
		    if (!currentBranches.isEmpty()) {
    	        m_scripter.addScriptCommand(script, "remove branch *");
		    }
    	
   			List<Entry<Reference, List<Reference>>> processed = new ArrayList<Entry<Reference, List<Reference>>>();   			   			   			
   			Iterator<Entry<Reference, List<Reference>>> iter = branches.entrySet().iterator();
   			while (iter.hasNext()) {
   				Entry<Reference, List<Reference>> branch = iter.next();
   				
				if (!processed.contains(branch)) {
	    		    if (branchSetOnAllActiveCores(branches, branch, activeCores, processed)) {
	    		    	outputBranch(script, branch);

	        		} else {
						outputCoreId(script, branch.getKey().getCoreId());
	    		    	outputBranch(script, branch);
	        		}
	    		    processed.add(branch);	
				}
        	}
    	}    	
    }
    	
    private boolean defineSetForAllActiveCores(Collection<? extends DefineInfo> defines, 
    		DefineInfo thisInfo, Collection<CoreId> activeCores,
    		List<DefineInfo> processed) {
    	
		Map<CoreId, DefineInfo> matched = new IteratableMap<CoreId, DefineInfo>();
		for (CoreId activeCore : activeCores)
			matched.put(activeCore, null);

    	Iterator<? extends DefineInfo> iter = defines.iterator();
   		while (iter.hasNext()) {
   			DefineInfo info = iter.next();
			if (defineInfoMatch(thisInfo, info)) {
				matched.put(info.getCoreId(), info);
			}
		}    	
				
		if (matched.containsValue(null))
			return false;

		processed.addAll(matched.values());
		return true;
    }
    
    private boolean branchSetOnAllActiveCores(Map<Reference, List<Reference>> branches, 
    		Entry<Reference, List<Reference>> thisBranch, Collection<CoreId> activeCores, 
    		List<Entry<Reference, List<Reference>>> processed) {
    	
		Map<CoreId, Entry<Reference, List<Reference>>> matched = new IteratableMap<CoreId, Entry<Reference, List<Reference>>>();
		for (CoreId activeCore : activeCores)
			matched.put(activeCore, null);

    	Iterator<Entry<Reference, List<Reference>>> iter = branches.entrySet().iterator();
   		while (iter.hasNext()) {
   			Entry<Reference, List<Reference>> branch = iter.next();
   			if (branchMatch(thisBranch, branch)) {
				matched.put(branch.getKey().getCoreId(), branch); 				
     		}
		}    	

		if (matched.containsValue(null))
			return false;

		processed.addAll(matched.values());    	
    	return true;
    }
    
    private boolean defineInfoMatch(DefineInfo thisInfo, DefineInfo thatInfo) {
    	Reference[] thisReferences = thisInfo.getReferences();
    	Reference[] thatReferences = thatInfo.getReferences();
    	
    	if (thisReferences.length != thatReferences.length)
    		return false;
    	
    	for (int i = 0; i < thisReferences.length; ++i) {
    		if (!thisReferences[i].equals(thatReferences[i])) {
    			return false;
    		}
    	}    	
    	if (!thisInfo.getValue().equals(thatInfo.getValue())) 
    		return false;
    	
    	return true;
    }
        
    private boolean branchMatch(Entry<Reference, List<Reference>> thisBranch, Entry<Reference, List<Reference>> thatBranch) {
    	Reference thisFromRef = thisBranch.getKey();
    	Reference thatFromRef = thatBranch.getKey();
    	if (!thisFromRef.equals(thatFromRef))
    		return false;
    	
    	List<Reference> thisToRefs = thisBranch.getValue();
    	List<Reference> thatToRefs = thatBranch.getValue();
    	
    	if (thisToRefs.size() != thatToRefs.size())
    		return false;
    	
	    for (int i = 0; i < thisToRefs.size(); ++i) {
	    	if (!thisToRefs.get(i).equals(thatToRefs.get(i))) {	    		
	    		return false;
	    	}
	    }    	
    	return true;
    }    
        
    private void outputDefineInfo(StringBuffer script, String command, DefineInfo info) {
		for (Reference reference : info.getReferences()) {
			m_scripter.outputPortableWarnings(script, reference);
		}

		StringBuffer commandString = new StringBuffer();
		commandString.append("add ");
		commandString.append(command);
		for (Reference reference : info.getReferences()) {
			commandString.append(" ");
			commandString.append(m_scripter.getReferenceString(reference));
		}
		if (!info.getValue().equals("")) {
		    commandString.append(" ");
		    commandString.append(info.getValue());
		}
		m_scripter.addScriptCommand(script, commandString.toString());
    }
    
    private void outputBranch(StringBuffer script, Entry<Reference, List<Reference>> branch) {
    	m_scripter.outputPortableWarnings(script, branch.getKey());
		for (Reference toRef : branch.getValue()) {
			m_scripter.outputPortableWarnings(script, toRef);
		}
		StringBuffer commandString = new StringBuffer();
		commandString.append(String.format("add branch %s", m_scripter.getReferenceString(branch.getKey())));
		for (Reference toRef : branch.getValue()) {
			commandString.append(String.format(" %s", m_scripter.getReferenceString(toRef)));
		}
    	
    	m_scripter.addScriptCommand(script, commandString.toString());
    }
    
    private void outputCoreId(StringBuffer script, CoreId activeCore) {
    	m_scripter.addScriptCommand(script, "remove core *");
    	m_scripter.addScriptCommand(script, String.format("add core %s", activeCore.getCoreReference()));							    	
    }    
}
