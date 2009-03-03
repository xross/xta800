package com.xmos.xta.engine.references;

import java.util.HashMap;
import java.util.Map;

import com.xmos.xta.engine.Engine;

public class CodeReferenceFactory {
	public enum ReferenceClass {
		REF, CODE_REF, SRCLABEL_REF, ENDPOINT_REF,
	}	
	public enum ReferenceType {
		PC, LABEL, SRCLABEL, SRCLABEL_FILELINE, ENDPOINT, ENDPOINT_FILELINE,
	}
	
	private static Map<ReferenceClass, ReferenceType[]> m_referenceClasses;	
	static {
		m_referenceClasses = new HashMap<ReferenceClass, ReferenceType[]>();
		m_referenceClasses.put(ReferenceClass.REF, new ReferenceType[] {
				ReferenceType.PC, 
				ReferenceType.LABEL, 
				ReferenceType.SRCLABEL, 
				ReferenceType.SRCLABEL_FILELINE, 
				ReferenceType.ENDPOINT, 
				ReferenceType.ENDPOINT_FILELINE});
		m_referenceClasses.put(ReferenceClass.CODE_REF, new ReferenceType[] {
				ReferenceType.PC, 
				ReferenceType.LABEL}); 
		m_referenceClasses.put(ReferenceClass.SRCLABEL_REF, new ReferenceType[] {
				ReferenceType.PC, 
				ReferenceType.LABEL, 
				ReferenceType.SRCLABEL, 
				ReferenceType.SRCLABEL_FILELINE});
		m_referenceClasses.put(ReferenceClass.ENDPOINT_REF, new ReferenceType[] {
				ReferenceType.PC, 
				ReferenceType.LABEL, 
				ReferenceType.ENDPOINT, 
				ReferenceType.ENDPOINT_FILELINE});		
	}
	private static Engine m_engine;
	
	public static CodeReference createReference(String reference, ReferenceClass referenceClass) {
    	if (reference.length() == 0)
			return new ErrorReference(reference);		
		
		ReferenceType[] referenceTypes = m_referenceClasses.get(referenceClass);
		assert(referenceTypes != null);
		
		for (ReferenceType referenceType : referenceTypes) {
			switch (referenceType) {
			case PC:
				if (isPcString(reference)) {
					return new PcReference(reference);
				}
				break;
				
			case LABEL:
				if (m_engine.getLabels().containsKey(reference)) {
					return new LabelReference(reference);
				}
				break;
				
			case SRCLABEL:
				if (m_engine.getSrcLabels().containsKey(reference)) {
					return new SrcLabelReference(reference);
				}
				break;
				
			case SRCLABEL_FILELINE:
				if (isSourceReference(reference)) {
		    		return new SrcLabelFileLineReference(reference);	
				}
				break;
				
			case ENDPOINT:
				if (m_engine.getEndpoints().containsKey(reference)) {
					return new EndpointReference(reference);
				}
				break;
				
			case ENDPOINT_FILELINE:
				if (isSourceReference(reference)) {
		    		return new EndpointFileLineReference(reference);
				}
				break;
			}
		}
		return new ErrorReference(reference);
	}
	    
    public static void setEngine(Engine engine) {
    	assert(engine != null);
    	m_engine = engine;
    }

	public static boolean isSourceReference(String ref) {
        int colonIndex = ref.lastIndexOf(':');
        if (colonIndex == -1) {
        	return false;
        }

		String fileName = ref.substring(0, colonIndex);
		String lineNum = ref.substring(colonIndex + 1);
		if (lineNum.matches("[0-9]*") && (fileName.length() > 0)) {
			return true;
		}
		return false;
	}

	public static boolean isPcString(String ref) {
		if (ref.matches("0x[0-9a-fA-F]+")){
			return true;
		} else if (ref.matches("[0-9]+")){
			return true;
		} else {
			return false;
		}
	}
	
	public static int getPcString(String ref) {
		if (ref.matches("0x[0-9a-fA-F]+")){
			return Integer.parseInt(ref.substring(2), 16);
		} else if (ref.matches("[0-9]+")){
			return Integer.parseInt(ref, 10);
		} else {
			assert(false);
			return 0;
		}
	}	
	
	public static String getFileName(String fileLineReference) {
        int colonIndex = fileLineReference.lastIndexOf(':');
        assert(colonIndex != -1);
		return fileLineReference.substring(0, colonIndex);		
	}

	public static int getLineNumber(String fileLineReference) {
        int colonIndex = fileLineReference.lastIndexOf(':');
        assert(colonIndex != -1);
		
		String lineNumberStr = fileLineReference.substring(colonIndex + 1);		
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
