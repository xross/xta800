package com.xmos.xta.engine;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import com.xmos.dwarfhandler.DebugEngine;
import com.xmos.xeloader.DebugInfo;

public class Reference {
    String m_ref;
    int m_pc;
    boolean m_isResolved;
    
    
    public Reference(String ref) {
        m_ref = ref;
        m_pc = 0;
        m_isResolved = false;
    }

    public Reference(String label, int pc) {
        m_ref = label;
        m_pc = pc;
        m_isResolved = true;
    }
    
    public Reference(int pc) {
        m_ref = "";
        m_pc = pc;
        m_isResolved = true;
    }
    
    public String toString(){
		return m_ref + " 0x" + Integer.toHexString(m_pc);
    	
    }
    
    public String getRef() {
        assert m_isResolved == true;
        return m_ref;
    }
    
    public int getPc() {
        assert m_isResolved == true;
        return m_pc;
    }
    
    boolean isResolved() {
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
            if (!fileExists(fileName)) {
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

    private boolean fileExists(String fileName) {
        File f = new File(fileName);
        return f.exists();
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

	public void resolve(Engine engine) {
		if(engine.isLabel(m_ref)){
			m_pc = engine.getLabelPc(m_ref);
	        m_isResolved = true;
		}
	}
}
