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
		LABEL,
		INVALID
	}
	
	private RefType m_type;
	private String m_ref;
	private String m_shortRef;
	private int m_pc;
	private boolean m_isResolved;
	
	public CodeReference(String ref) {
		m_ref = ref.trim();

		if (m_ref.length() == 0) {
			m_type = RefType.INVALID;
		} else if (isPcString(ref)) {
			m_type = RefType.PC;
		} else if (isSourceReference(ref)) {
			m_type = RefType.LINE;
		} else {
			m_type = RefType.LABEL;
		}
		setShortRef();
	}
	
	public CodeReference(String fileName, int lineNumber) {
		m_ref = String.format("%s:%d", fileName, lineNumber);
		m_type = RefType.LINE;
		m_isResolved = false;
		setShortRef();
	}

	private void setShortRef() {
		if (m_type == RefType.LINE) {
			int index = m_ref.lastIndexOf(File.separatorChar);
			if (index == -1) {
				m_shortRef = m_ref;
			} else {
				m_shortRef = m_ref.substring(index + 1);
			}
		}
	}

	public RefType getType() {
		return m_type;
	}
    
    public String getRef() {
        return m_ref;
    }
    
    public String getShortRef() {
		if (m_type == RefType.LINE)
			return m_shortRef;
		else
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
    		//also need to check for existence within the executable
    		return engine.getXeObject().getPcToFunctionMap().containsKey(m_pc);
            
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
        		file = new File(fileName);
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
    	case INVALID:
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
        int colonIndex = m_ref.lastIndexOf(':');
        if (colonIndex == -1) {
        	return false;
        }

		String fileName = ref.substring(0, colonIndex);
		String lineNum = ref.substring(colonIndex + 1);
		if (lineNum.matches("[0-9]*") && (fileName.length() > 0)) {
			m_isResolved = false;
			return true;
		}
		return false;
	}

	private boolean isPcString(String ref) {
		if (ref.matches("0x[0-9a-fA-F]+")){
			m_pc = Integer.parseInt(ref.substring(2), 16);
		} else if (ref.matches("[0-9]+")){
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
