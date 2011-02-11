package com.xmos.xta.core.engine.references.types;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableSet;
import com.xmos.libs.util.NonIteratableMap;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.sections.CodePointInfo;
import com.xmos.xta.core.engine.sections.CodePointTableInfo;
import com.xmos.xta.core.engine.sections.SrcLabelTableInfo;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;
import com.xmos.xta.core.engine.utils.FileNameMatcher;

public abstract class ReferenceType {
	private String m_ref;
	private boolean m_resolved;
	private Set<Long> m_pcs;
	private Map<Long, List<String>> m_pcBackTrails;
	private String m_fileName;
	private long m_lineNum;
	private Type m_type;
	private CoreId m_coreId;

	public enum Type {
		LABEL,
		FUNCTION,
		ENDPOINT,
		SRCLABEL,
		CALL,
		EVERYTHING,
		ERROR
	}

	ReferenceType(String ref, Type type, CoreId coreId) {
		m_ref = ref;
		m_pcs = new IteratableSet<Long>();
		m_pcBackTrails = new NonIteratableMap<Long, List<String>>();
		m_resolved = false;
		m_fileName = "";
		m_lineNum = 0;
		m_type = type;
		m_coreId = coreId;
	}

	public abstract ReferenceType create(Engine engine, String reference, CoreId coreId);
	public abstract String name();
	public abstract String description();
	public abstract boolean resolve(Engine engine);
	public abstract boolean isPortable();

	public String getRef() {
		return m_ref;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(m_ref + " ");
		for(long pc : m_pcs){
			sb.append("0x" + Long.toHexString(pc) + " ");
		}
		return sb.toString();
	}
	
	public String getScripterRef() {
		if (!isPortable() && (m_lineNum > 0)) {
			return String.format("%s:%d", m_fileName, m_lineNum);
		} else {
			return m_ref;
		}
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

	public Set<Long> getPcs() {
		return m_pcs;
	}

	public void addPc(long pc) {
	    m_pcs.add(pc);
	    m_pcBackTrails.put(pc, new LinkedList<String>());
	}

	public static List<String> convertLabelToList(String tableLabel){
		String[] subStrings = tableLabel.split(",");
	    List<String> backTrail = new LinkedList<String>();
	    for (int i = 0; i < subStrings.length - 1; i++) {
	    	backTrail.add(subStrings[i]);
	    }
		return backTrail;
	}

	public void addPc(long pc, String tableLabel) {
	    m_pcs.add(pc);
	    m_pcBackTrails.put(pc, convertLabelToList(tableLabel));
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

	boolean inSrcLabelTable(String ref, Map<Long, SrcLabelTableInfo> srcLabelTable, 
			String workspacePath, SourceMapper sourceMapper) {
		
		String fileName = getFileName(ref);
        long lineNumber  = getLineNumber(ref);

		for (SrcLabelTableInfo info : srcLabelTable.values()) {
            if ((lineNumber >= info.getLineNumberStart()) &&
            	(lineNumber <= info.getLineNumberEnd())  &&            		
            	FileNameMatcher.isMatch(info.getCompilationDir(), info.getFileName(), 
            			fileName, workspacePath, sourceMapper)) {
            	return true;
            }
		}
		return false;
	}

	boolean inCodePointTable(String ref, Map<Long, CodePointTableInfo> codePointTable, 
			String workspacePath, SourceMapper sourceMapper) {
		
		String fileName = getFileName(ref);
        long lineNumber  = getLineNumber(ref);
        
		for (CodePointTableInfo info : codePointTable.values()) {
            if ((lineNumber == info.getLineNumber()) && 
            	FileNameMatcher.isMatch(info.getCompilationDir(), info.getFileName(), 
            			fileName, workspacePath, sourceMapper)) {
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

	public boolean matches(String s, Engine engine) {
		return m_ref.matches(s);
	}

    public int hashCode() {
        int hash = 1;
        for (long pc : m_pcs) {
        	hash = hash * 31 + (int)pc;
        }
        hash = hash * 31 + m_ref.hashCode();
        return hash;
    }

	public boolean checkPcsMatch() {
		return true;
	}

    public boolean checkBackTrailMatches() {
		return true;
	}

	protected boolean isValid(List<CodePointInfo> list, String label) {
		for (CodePointInfo info : list) {
			if (labelsMatch(info.getLabel(), label)) {
				return true;
			}
		}
		return false;
	}

	protected boolean labelsMatch(String tableLabel, String label) {
    	String[] subRefs = tableLabel.split(",");
    	assert(subRefs.length > 0);
		return subRefs[subRefs.length - 1].equals(label);
	}

	protected boolean fileLineMatches(String s, Engine engine) {
		if (!isSourceReference(s)) {
			return false;
		}

		String fileName = getFileName(s);
        long lineNumber = getLineNumber(s);

        if (lineNumber != getLineNum()) {
        	return false;
        }

        if (FileNameMatcher.isMatch(getFileName(), fileName, 
        		engine.getWorkspacePath(), engine.getSourceMapper())) {
        	return true;
        }

		return false;
	}

	public List<String> getPcBackTrail(long pc) {
		assert(m_pcBackTrails.get(pc) != null);
		return m_pcBackTrails.get(pc);
	}
	
	public CoreId getCoreId() {
		return m_coreId;
	}

	public void sourceLineInserted(String sourceFileName, long lineNumber, 
			String workspacePath, SourceMapper sourceMapper) {
		
		if ((m_lineNum >= lineNumber) && 
			FileNameMatcher.isMatch(sourceFileName, m_fileName, workspacePath, sourceMapper)) {
			m_lineNum += 1;
		}
	}
}
