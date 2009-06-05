package com.xmos.xta.engine.references.types;

import java.util.Map;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.CodePointTableInfo;
import com.xmos.xta.engine.sections.SrcLabelTableInfo;

public abstract class ReferenceType {
	private String m_ref;
	private boolean m_resolved;
	private int m_pc;
	private String m_fileName;
	private int m_lineNum;	
	private Type m_type;

	public enum Type {
		PC,
		LABEL,
		ENDPOINT,
		SRCLABEL,
		CALL,
		ERROR,
	}
	
	ReferenceType(String ref, Type type) {
		m_ref = ref;
		m_pc = 0;
		m_resolved = false;
		m_fileName = "";
		m_lineNum = 0;
		m_type = type;
	}
	
	public abstract ReferenceType create(Engine engine, String reference);
	public abstract String name();
	public abstract String description();
	public abstract boolean resolve(Engine engine, int nodeId, int coreId);
	
	public String getRef() {
	    return m_ref;	    
	}
	
	public void setRef(String ref) {
		m_ref = ref;
	}
	    
	public boolean getResolved() {
    	return m_resolved;
    }

    public void setResolved(boolean resolved) {
    	m_resolved = resolved;
    }
    
	public int getPc() {
		return m_pc;
	}	

	public void setPc(int pc) {
	    m_pc = pc;
	}

	public String getFileName() {
		return m_fileName;
	}	

	public void setFileName(String fileName) {
		m_fileName = fileName;
	}

	public int getLineNum() {
		return m_lineNum;
	}	

	public void setLineNum(int lineNum) {
		m_lineNum = lineNum;
	}
	
	public Type getType() {
		return m_type;
	}
	
	boolean inSrcLabelTable(String ref, Map<Integer, SrcLabelTableInfo> srcLabelTable) {
		String fileName = getFileName(ref);
        int lineNumber  = getLineNumber(ref);		

		for (SrcLabelTableInfo info : srcLabelTable.values()) {
            if (info.getFileName().equals(fileName) &&
            	((lineNumber >= info.getLineNumberStart()) &&
            	 (lineNumber <= info.getLineNumberEnd()))) {
            	return true;
            }			
		}		
		return false;
	}
	
	boolean inCodePointTable(String ref, Map<Integer, CodePointTableInfo> codePointTable) {		
		String fileName = getFileName(ref);
        int lineNumber  = getLineNumber(ref);		

		for (CodePointTableInfo info : codePointTable.values()) {
            if (info.getFileName().equals(fileName) &&
            	(info.getLineNumber() == lineNumber)) {
            	return true;
            }			
		}		
		return false;
	}	

	boolean isSourceReference(String ref, String refType) {
		String[] refArray = ref.split(":"); 

		if ((refArray.length < 2) || (refArray.length > 3))
			return false;
				
		String fileName = (refArray.length == 2) ? refArray[0] : refArray[1];
		if (fileName.length() == 0)
			return false;

		String lineNum = (refArray.length == 2) ? refArray[1] : refArray[2];			
		if (!lineNum.matches("[0-9]*")) 
			return false;

		if ((refArray.length == 3) && !refArray[0].equals(refType)) {
			return false;			
		}		
		return true;
	}

	boolean isPcString(String ref) {
		if (ref.matches("0x[0-9a-fA-F]+")){
			return true;
		} else if (ref.matches("[0-9]+")){
			return true;
		} else {
			return false;
		}
	}
	
	int getPcString(String ref) {
		if (ref.matches("0x[0-9a-fA-F]+")){
			return Integer.parseInt(ref.substring(2), 16);
		} else if (ref.matches("[0-9]+")){
			return Integer.parseInt(ref, 10);
		} else {
			assert(false);
			return 0;
		}
	}	
		
	String getFileName(String fileLineReference) {
		String[] refArray = fileLineReference.split(":");
		assert((refArray.length == 2) || (refArray.length == 3));		
		return (refArray.length == 2) ? refArray[0] : refArray[1];		
	}

	int getLineNumber(String fileLineReference) {
		String[] refArray = fileLineReference.split(":");
		assert((refArray.length == 2) || (refArray.length == 3));		
		
		String lineNumberStr = (refArray.length == 2) ? refArray[1] : refArray[2];		        
        int lineNumber = 0;
        try {       
        	lineNumber = Integer.parseInt(lineNumberStr);
        }
        catch (NumberFormatException e) {
            assert false;
        }            
        return lineNumber;
	}	
}
