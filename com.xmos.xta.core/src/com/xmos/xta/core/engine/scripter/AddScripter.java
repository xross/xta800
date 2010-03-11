package com.xmos.xta.core.engine.scripter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.xmos.xta.core.engine.defines.DefineInfo;
import com.xmos.xta.core.engine.defines.Defines;
import com.xmos.xta.core.engine.defines.ScriptUpdater;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.NodeCorePair;

public class AddScripter {
	private Scripter m_scripter;
	
	AddScripter(Scripter scripter) {
		m_scripter = scripter;
	}
	
    void addDefines(Defines defines, Defines currentDefines, 
    		final StringBuffer script, final Collection<NodeCorePair> activeCores) {
    	    	
    	defines.updateScript(currentDefines, new ScriptUpdater() {
			public void update(
					Collection<? extends DefineInfo> newList,
					Collection<? extends DefineInfo> oldList, 
					String command, String removeStr) {
				
				if (!oldList.isEmpty())
					script.append("remove " + command + " " + removeStr + "\n");

				List<DefineInfo> processed = new ArrayList<DefineInfo>();
				for (DefineInfo info : newList) {
					if (!processed.contains(info)) {
						if (defineSetForAllActiveCores(newList, info, activeCores, processed)) {							
							outputDefineInfo(script, command, info);

						} else {
							outputNodeCore(script, info.getNodeCore());
							outputDefineInfo(script, command, info);
						}						
						processed.add(info);						
					}					
				}		
			}			
		});
    }
    
    void addExclusions(Collection<Reference> exclusions, Collection<Reference> currentExclusions, 
    		StringBuffer script, Collection<NodeCorePair> activeCores) {

    	if (!currentExclusions.equals(exclusions)) {
    		if (!currentExclusions.isEmpty())
        	    script.append("remove exclusion *\n");

			List<Reference> processed = new ArrayList<Reference>();    		
        	for (Reference exclusion : exclusions) {    
				if (!processed.contains(exclusion)) {
	    		    if (exclusionSetOnAllActiveCores(exclusions, exclusion, activeCores, processed)) {
	    		    	outputExclusion(script, exclusion);

	        		} else {
						outputNodeCore(script, exclusion.getNodeCore());
	    		    	outputExclusion(script, exclusion);
	        		}
	    		    processed.add(exclusion);	
				}
        	}  
    	}
    }
    
    void addBranches(Map<Reference, List<Reference>> branches, Map<Reference, List<Reference>> currentBranches, 
    		StringBuffer script, Collection<NodeCorePair> activeCores) {
  
    	if (!currentBranches.equals(branches)) {
		    if (!currentBranches.isEmpty())
    	        script.append("remove branch *\n");
    	
   			List<Entry<Reference, List<Reference>>> processed = new ArrayList<Entry<Reference, List<Reference>>>();   			   			   			
   			Iterator<Entry<Reference, List<Reference>>> iter = branches.entrySet().iterator();
   			while (iter.hasNext()) {
   				Entry<Reference, List<Reference>> branch = iter.next();
   				
				if (!processed.contains(branch)) {
	    		    if (branchSetOnAllActiveCores(branches, branch, activeCores, processed)) {
	    		    	outputBranch(script, branch);

	        		} else {
						outputNodeCore(script, branch.getKey().getNodeCore());
	    		    	outputBranch(script, branch);
	        		}
	    		    processed.add(branch);	
				}
        	}
    	}    	
    }
    	
    private boolean defineSetForAllActiveCores(Collection<? extends DefineInfo> defines, 
    		DefineInfo thisInfo, Collection<NodeCorePair> activeCores,
    		List<DefineInfo> processed) {
    	
		Map<NodeCorePair, DefineInfo> matched = new HashMap<NodeCorePair, DefineInfo>();
		for (NodeCorePair activeCore : activeCores)
			matched.put(activeCore, null);

    	Iterator<? extends DefineInfo> iter = defines.iterator();
   		while (iter.hasNext()) {
   			DefineInfo info = iter.next();
			if (defineInfoMatch(thisInfo, info)) {
				matched.put(info.getNodeCore(), info);
			}
		}    	
				
		if (matched.containsValue(null))
			return false;

		processed.addAll(matched.values());
		return true;
    }

    private boolean exclusionSetOnAllActiveCores(Collection<Reference> exclusions, 
    		Reference thisExclusion, Collection<NodeCorePair> activeCores, 
    		List<Reference> processed) {

		Map<NodeCorePair, Reference> matched = new HashMap<NodeCorePair, Reference>();
		for (NodeCorePair activeCore : activeCores)
			matched.put(activeCore, null);

    	Iterator<Reference> iter = exclusions.iterator();
   		while (iter.hasNext()) {
   			Reference exclusion = iter.next();
   			if (exclusionMatch(thisExclusion, exclusion)) {
				matched.put(exclusion.getNodeCore(), exclusion);
			}
		}    	

		if (matched.containsValue(null))
			return false;

		processed.addAll(matched.values());    	
    	return true;
    }
    
    private boolean branchSetOnAllActiveCores(Map<Reference, List<Reference>> branches, 
    		Entry<Reference, List<Reference>> thisBranch, Collection<NodeCorePair> activeCores, 
    		List<Entry<Reference, List<Reference>>> processed) {
    	
		Map<NodeCorePair, Entry<Reference, List<Reference>>> matched = new HashMap<NodeCorePair, Entry<Reference, List<Reference>>>();
		for (NodeCorePair activeCore : activeCores)
			matched.put(activeCore, null);

    	Iterator<Entry<Reference, List<Reference>>> iter = branches.entrySet().iterator();
   		while (iter.hasNext()) {
   			Entry<Reference, List<Reference>> branch = iter.next();
   			if (branchMatch(thisBranch, branch)) {
				matched.put(branch.getKey().getNodeCore(), branch); 				
     		}
		}    	

		if (matched.containsValue(null))
			return false;

		processed.addAll(matched.values());    	
    	return true;
    }
    
    private boolean defineInfoMatch(DefineInfo thisInfo, DefineInfo thatInfo) {
    	if (!thisInfo.getDescription().equals(thatInfo.getDescription())) 
    		return false;
    	
    	if (!thisInfo.getValue().equals(thatInfo.getValue())) 
    		return false;
    	
    	return true;
    }
        
    private boolean exclusionMatch(Reference thisExclusion, Reference thatExclusion) {
    	if (!thisExclusion.equals(thatExclusion))
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

    	script.append("add " + command);
		for (Reference reference : info.getReferences()) {
			script.append(" " + m_scripter.getReferenceString(reference));
		}
		script.append(" " + info.getValue() + "\n");							    	
    }
    
    private void outputExclusion(StringBuffer script, Reference exclusion) {
		m_scripter.outputPortableWarnings(script, exclusion);
		script.append("add exclusion " + m_scripter.getReferenceString(exclusion) + "\n");						        			    	
    }
    
    private void outputBranch(StringBuffer script, Entry<Reference, List<Reference>> branch) {
    	m_scripter.outputPortableWarnings(script, branch.getKey());
		for (Reference toRef : branch.getValue()) {
			m_scripter.outputPortableWarnings(script, toRef);
		}

    	script.append(String.format("add branch %s", m_scripter.getReferenceString(branch.getKey())));
		for (Reference toRef : branch.getValue()) {
			script.append(String.format(" %s", m_scripter.getReferenceString(toRef)));
		}
    	script.append(String.format("\n"));    	
    }
    
    private void outputNodeCore(StringBuffer script, NodeCorePair activeCore) {
		script.append("remove core *\n");
		script.append(String.format("add core %d %d\n", activeCore.getNode(), activeCore.getCore()));							    	
    }    
}
