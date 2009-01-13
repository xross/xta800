package com.xmos.xta.engine;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import com.xmos.dwarfhandler.DebugEngine;
import com.xmos.xeloader.DebugInfo;

public class CodeReference {	
	public enum RefType{
		PC,
		LINE,
		LABEL
	}
	
	private RefType m_type;
	private String m_ref;
	private int m_pc;
	private boolean m_isResolved;
	
	public CodeReference(String ref) {
		m_ref = ref;
		if (isPcString(ref)) {
			m_type = RefType.PC;
			return;
		} else if (isSourceReference(ref)) {
			m_type = RefType.LINE;
			return;
		} else {
			m_type = RefType.LABEL;
			return;
		}
	}
	
	public CodeReference(String fileName, int lineNumber) {
		m_ref = String.format("%s:%d", fileName, lineNumber);
		m_type = RefType.LINE;
		m_isResolved = false;
	}

	public RefType getType() {
		return 	m_type;
	}
    
    public String getRef() {
        assert(m_isResolved == true);
        return m_ref;
    }
    
    public int getPc() {
        assert(m_isResolved == true);
        return m_pc;
    }
    
    public boolean isResolved() {
        return m_isResolved;
    }
    
	public boolean resolve(Engine engine, int m_nodeId, int m_coreId) {
    	switch (m_type) {
    	case PC:
    		m_isResolved = true;
            return true;
            
    	case LINE:
            // Input: 'source file:line number'
            int colonIndex = m_ref.lastIndexOf(':');
            if (colonIndex == -1) {
            	return false;
            }

    		DebugInfo debugInfo = engine.getXeLoader().getDebugInfo(m_nodeId, m_coreId);		
            
    		String fileName = m_ref.substring(0, colonIndex);

    		File file = new File(fileName);
    		if (!file.isAbsolute()) {
    			fileName = engine.getXeLoader().getFile().getParent() + File.separatorChar + fileName;
            }

            try {
            	file = file.getCanonicalFile();
			} catch (IOException e) {
				return false;
			}
            
            if (!fileExists(debugInfo, file)) {
                return false;            
            }
            
            String lineNumber = m_ref.substring(colonIndex + 1);
            if (!isInteger(lineNumber)) {
                return false;            
            }
                        
            DebugEngine debugEngine = debugInfo.getDebugEngine();            
            Collection<Long> pcs = debugEngine.getXtaAddresses(file, intStringToInt(lineNumber));
            if (pcs.size() > 0) {
                m_pc = findLowestPc(pcs);
            }
            else {
                return false;                
            }                    		
    		m_isResolved = true;
            return true;
            
    	case LABEL:
    	    if (engine.isLabel(m_ref)) {
    		    m_pc = engine.getLabelPc(m_ref);
                m_isResolved = true;
                return true;
    	    }
    	    return false;
    	}
    	assert(false);
    	return false;
	}
  
	private boolean fileExists(DebugInfo debugInfo, File fileName) {
    	return debugInfo.getDebugEngine().containsXtaFile(fileName);
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
	
	private boolean isSourceReference(String ref) {
		if (ref.contains(":")) {
			String [] parts = ref.split(":");
			if (parts.length == 2 && 
					parts[1].matches("[0-9]*") && 
					parts[0].length()>0){
				m_isResolved = false;
				return true;
			}
		}
		return false;
	}

	private boolean isPcString(String ref) {
		if (ref.matches("0x[0-9a-fA-F]*")){
			m_pc = Integer.parseInt(ref.substring(2), 16);
		} else if (ref.matches("[0-9]*")){
			m_pc = Integer.parseInt(ref, 10);
		} else {
			m_isResolved = false;
			return false;
		}
		//TODO make these bounds refer to the memory size
		if (m_pc >= 0x10000 && m_pc < 0x20000){
			m_isResolved = true;
			return true;
		} else {
			m_isResolved = false;
			return false;
		}
	}	
}
