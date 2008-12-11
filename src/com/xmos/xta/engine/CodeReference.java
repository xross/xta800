package com.xmos.xta.engine;

import java.io.File;
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

	
	public CodeReference(String ref){
		m_ref = ref;
		if(isPcString(ref)){
			m_type = RefType.PC;
			return;
		} else if(isSourceReference(ref)) {
			m_type = RefType.LINE;
			return;
		} else {
			//must be a label
			m_type = RefType.LABEL;
			return;
		}
	}

	private boolean isSourceReference(String ref) {
		if(ref.contains(":")){
			String [] parts = ref.split(":");
			if(parts.length == 2 && 
					parts[1].matches("[0-9]*") && 
					parts[0].length()>0){
				m_type = RefType.LINE;
				m_isResolved = false;
				return true;
			}
		}
		return false;
	}

	private boolean isPcString(String ref) {
		if(ref.matches("0x[0-9a-fA-F]*")){
			m_pc = Integer.parseInt(ref.substring(2), 16);
		} else if(ref.matches("[0-9]*")){
			m_pc = Integer.parseInt(ref, 10);
		} else {
			m_isResolved = false;
			return false;
		}
		//TODO make these bounds refer to the memory size
		if(m_pc >= 0x10000 && m_pc < 0x20000){
			m_isResolved = true;
			return true;
		} else {
			m_isResolved = false;
			return false;
		}
	}

	public String toString(){
		if(m_type == RefType.LABEL){
			if(m_isResolved){
				return "label " + m_ref + " : 0x" + Integer.toHexString(m_pc);
			} else {
				return "label " + m_ref + " : unresolved";
			}
			
		} else if(m_type == RefType.LINE){
			if(m_isResolved){
				return "line " + m_ref + " -> 0x" + Integer.toHexString(m_pc);
			} else {
				return "line " + m_ref + " -> : unresolved";
			}
		} else if(m_type == RefType.PC){
			if(m_isResolved){
				return "PC : 0x" + Integer.toHexString(m_pc);
			} else {
				return "PC : unresolved";
			}
		} else {
			assert(false);
			return "";
		}
    }
    
    public String getRef() {
        assert m_isResolved == true;
        return m_ref;
    }
    
    public int getPc() {
        assert m_isResolved == true;
        return m_pc;
    }
    
    public boolean isResolved() {
        return m_isResolved;
    }
    
    boolean resolve(DebugInfo debugInfo) {
        if (isHexInteger(m_ref)) {
            // Input: 'program counter'
            m_pc = hexStringToInt(m_ref);
        }
        else {
            // Input: 'source file:line number'
            int colonIndex = m_ref.lastIndexOf(':');
            if (colonIndex == -1) {
            	return false;
            }
            
            String fileName = m_ref.substring(0, colonIndex);
            if (!fileExists(debugInfo, fileName)) {
                return false;            
            }
            
            String lineNumber = m_ref.substring(colonIndex + 1);
            if (!isInteger(lineNumber)) {
                return false;            
            }
                        
            DebugEngine debugEngine = debugInfo.getDebugEngine();            
            Collection<Long> pcs = debugEngine.getAddresses(fileName, intStringToInt(lineNumber));
            if (pcs.size() > 0) {
                m_pc = findLowestPc(pcs);
            }
            else {
                return false;                
            }                
        }
        m_isResolved = true;
        return true;
    }

    private boolean fileExists(DebugInfo debugInfo, String fileName) {
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
    
    private boolean isHexInteger(String str) {
        // Strips the 0x off the beginning of the string if present.
        String hexString = str;
        if (str.matches("0x[0-9a-fA-F]*"))
            hexString = str.substring(2);
        
        try {       
            Integer.parseInt(hexString, 16);
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
    
    private int hexStringToInt(String str) {     
        // Strips the 0x off the beginning of the string if present.
        String hexString = str;
        if (str.matches("0x[0-9a-fA-F]*"))
            hexString = str.substring(2);
        
        int i = 0;
        try {       
            i = Integer.parseInt(hexString, 16);
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

	public boolean resolve(Engine engine) {
		if(engine.isLabel(m_ref)){
			m_pc = engine.getLabelPc(m_ref);
	        m_isResolved = true;
	        return true;
		}
		return false;
	}

	public boolean resolve(Engine engine, int m_nodeId, int m_coreId) {
		 DebugInfo debugInfo = engine.getXeLoader().getDebugInfo(m_nodeId, m_coreId);
		if (this.resolve(engine)){
			return true;
		} else {
			return this.resolve(debugInfo);
		}
	}

	public RefType getType() {
		return 	m_type;
	}
}
