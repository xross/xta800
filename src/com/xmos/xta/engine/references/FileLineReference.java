package com.xmos.xta.engine.references;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import com.xmos.dwarfhandler.DebugEngine;
import com.xmos.xeloader.DebugInfo;
import com.xmos.xta.engine.Engine;

public class FileLineReference extends CodeReference {
	
	FileLineReference(String ref) {
		super(ref);
		int index = ref.lastIndexOf(File.separatorChar);
		if (index != -1) {
			setShortRef(ref.substring(index + 1));
		}	
	}	
		
    public boolean resolve(Engine engine, int m_nodeId, int m_coreId) {
        // Input: 'source file:line number'
    	String ref = getRef();
    	
        int colonIndex = ref.lastIndexOf(':');
        if (colonIndex == -1) {
        	return false;
        }
                
		String fileName = ref.substring(0, colonIndex);
		DebugInfo debugInfo = engine.getXeLoader().getDebugInfo(m_nodeId, m_coreId);		
		if (!fileExistsInDebugInfo(debugInfo, fileName)) {        	
            return false;            
        }
		        
        String lineNumber = ref.substring(colonIndex + 1);
        if (!isInteger(lineNumber)) {
            return false;            
        }

        DebugEngine debugEngine = debugInfo.getDebugEngine();     
        Collection<Long> pcs = debugEngine.getAddresses(fileName, intStringToInt(lineNumber));
        if (pcs.size() > 0) {
            setPc(findLowestPc(pcs));
        }
        else {
            return false;
        }                    		
		setResolved(true);
        return true;    	
    }    
        
	private boolean fileExistsInDebugInfo(DebugInfo debugInfo, String fileName) {
    	return debugInfo.getDebugEngine().containsFile(fileName);
    }        
	
    private boolean isInteger(String str) {
        try {       
            Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }            
    }
    
    private int intStringToInt(String str) {     
        int i = 0;
        try {       
            i = Integer.parseInt(str);
        }
        catch (NumberFormatException e) {
            assert false;
        }            
        return i;
    }     
    
    private int findLowestPc(Collection<Long> collection) {        
        long lowestPc = 0;
        Iterator<Long> iter = collection.iterator(); 
        while (iter.hasNext()) {
            long pc = iter.next();
            if (pc > lowestPc) {
                lowestPc = pc;
            }
        }
        return (int)lowestPc;
    }    
}
