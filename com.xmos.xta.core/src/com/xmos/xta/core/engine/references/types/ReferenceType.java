package com.xmos.xta.core.engine.references.types;

import java.util.Map;

import com.xmos.libs.util.FileNameCompare;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;
import com.xmos.xta.core.engine.sections.SrcLabelTableInfo;

public abstract class ReferenceType {
	private String m_ref;
	private boolean m_resolved;
	private long m_pc;
	private String m_fileName;
	private long m_lineNum;	
	private Type m_type;

	public enum Type {
		PC,
		LABEL,
		ENDPOINT,
		SRCLABEL,
		CALL,
		ERROR
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
	public abstract boolean resolve(Engine engine);
	public abstract boolean isPortable();
	
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
    
	public long getPc() {
		return m_pc;
	}	

	public void setPc(long pc) {
	    m_pc = pc;
	}

	public String getFileName() {
		return m_fileName;
	}	

	public void setFileName(String fileName) {
		m_fileName = fileName;
	}

	public long getLineNum() {
		return m_lineNum;
	}	

	public void setLineNum(long lineNum) {
		m_lineNum = lineNum;
	}
	
	public Type getType() {
		return m_type;
	}
	
	boolean inSrcLabelTable(String ref, Map<Long, SrcLabelTableInfo> srcLabelTable) {
		String fileName = getFileName(ref);
        long lineNumber  = getLineNumber(ref);		

		for (SrcLabelTableInfo info : srcLabelTable.values()) {
            if (FileNameCompare.fileNamesMatch(info.getCompilationDir(), info.getFileName(), fileName) &&
            	((lineNumber >= info.getLineNumberStart()) &&
            	 (lineNumber <= info.getLineNumberEnd()))) {
            	return true;
            }			
		}		
		return false;
	}
	
	boolean inCodePointTable(String ref, Map<Long, CodePointTableInfo> codePointTable) {		
		String fileName = getFileName(ref);
        long lineNumber  = getLineNumber(ref);		

		for (CodePointTableInfo info : codePointTable.values()) {
            if (FileNameCompare.fileNamesMatch(info.getCompilationDir(), info.getFileName(), fileName) &&
            	(info.getLineNumber() == lineNumber)) {
            	return true;
            }			
		}		
		return false;
	}	

	boolean isSourceReference(String ref) {
		String[] refArray = ref.split(":"); 

		if ((refArray.length < 2) || (refArray.length > 4))
			return false;
				
		String fileName = getFileName(ref);
		if (fileName.length() == 0)
			return false;

		String lineNum = refArray[refArray.length - 1];			
		if (!lineNum.matches("[0-9]*")) 
			return false;
		
		return true;
	}

	boolean isPcString(String ref) {
		String[] refArray = ref.split(":"); 
        String pcString = (refArray.length == 2) ? refArray[1] : refArray[0];	 
		
		if (pcString.matches("0x[0-9a-fA-F]+")){
			return true;
		} else if (pcString.matches("[0-9]+")){
			return true;
		} else {
			return false;
		}
	}
	
	long getPcString(String ref) {
		String[] refArray = ref.split(":"); 
        String pcString = (refArray.length == 2) ? refArray[1] : refArray[0];	 		
		
		if (pcString.matches("0x[0-9a-fA-F]+")){
			return Long.parseLong(pcString.substring(2), 16);
		} else if (pcString.matches("[0-9]+")){
			return Long.parseLong(pcString, 10);
		} else {
			assert(false);
			return 0;
		}
	}	
		
	String getFileName(String fileLineReference) {
		String[] refArray = fileLineReference.split(":");
		assert((refArray.length == 2) || (refArray.length == 3) || (refArray.length == 4));
		
		if ((refArray.length == 4) || (refArray.length == 3) &&
				(refArray[refArray.length - 3].length() == 1)) {
			return refArray[refArray.length - 3] + ":" + refArray[refArray.length - 2];
		} else {
			return refArray[refArray.length - 2];
		}
	}

	long getLineNumber(String fileLineReference) {
		String[] refArray = fileLineReference.split(":");
		assert((refArray.length == 2) || (refArray.length == 3) || (refArray.length == 4));		
		
		String lineNumberStr = refArray[refArray.length - 1];
        long lineNumber = 0;
        try {       
        	lineNumber = Long.parseLong(lineNumberStr);
        }
        catch (NumberFormatException e) {
            assert false;
        }            
        return lineNumber;
	}
	
	String getLabel(String reference) {
		String[] refArray = reference.split(":");
		assert((refArray.length == 1) || (refArray.length == 2));		
		return (refArray.length == 1) ? refArray[0] : refArray[1];		
	}
	
	boolean checkTypeString(String reference, Type refType, int numElements) {
		String[] refArray = reference.split(":"); 

		if ((refArray.length == numElements))
			return true;

		if (numElements == 1) {
			// No file names supported
			 if ((refArray.length == 2) && (refArray[0].equals(refType.toString()))) {
				return true;
			}

		} else {
			// File names supported - deal with possible windows path
			assert(numElements == 2);
			if (refArray.length > 4)
				return false;

			if ((refArray.length == 3) && (refArray[0].length() == 1)) {
				return true; // Case of a windows path and no type
				
			} else if (refArray[0].equals(refType.toString())) {
				return true;
			}
		}
		return false;
	}
}
