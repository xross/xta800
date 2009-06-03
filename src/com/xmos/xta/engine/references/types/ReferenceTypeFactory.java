package com.xmos.xta.engine.references.types;

import java.util.HashMap;
import java.util.Map;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.CodePointTableInfo;
import com.xmos.xta.engine.sections.SrcLabelTableInfo;

public class ReferenceTypeFactory {
	public enum ReferenceClass {
		LABEL_REF, SRCLABEL_REF, ENDPOINT_REF, CALL_REF, CONSTRAINT_REF, ANY_REF
	}
	
	private static Map<ReferenceClass, ReferenceType[]> m_referenceClasses;	
	static {
		m_referenceClasses = new HashMap<ReferenceClass, ReferenceType[]>();
		
		m_referenceClasses.put(ReferenceClass.LABEL_REF, new ReferenceType[] {
				new PcReference(null), 
				new LabelReference(null)}); 
		
		m_referenceClasses.put(ReferenceClass.SRCLABEL_REF, new ReferenceType[] {
				new PcReference(null),
				new LabelReference(null),
        		new CallReference(null),
		        new CallFileLineReference(null),
				new SrcLabelReference(null),
				new SrcLabelFileLineReference(null)});
		
		m_referenceClasses.put(ReferenceClass.ENDPOINT_REF, new ReferenceType[] {
				new PcReference(null), 
				new LabelReference(null), 
				new CallReference(null), 
				new CallFileLineReference(null),
				new EndpointReference(null), 
				new EndpointFileLineReference(null)});

		m_referenceClasses.put(ReferenceClass.CALL_REF, new ReferenceType[] {
				new PcReference(null), 
				new LabelReference(null), 
				new CallReference(null), 
				new CallFileLineReference(null)});
		
		m_referenceClasses.put(ReferenceClass.CONSTRAINT_REF, new ReferenceType[] {
				new PcReference(null), 
				new LabelReference(null), 
				new EndpointReference(null), 
				new EndpointFileLineReference(null),		
		        new SrcLabelReference(null), 
		        new SrcLabelFileLineReference(null),
		        new CallReference(null), 
		        new CallFileLineReference(null)});

		m_referenceClasses.put(ReferenceClass.ANY_REF, new ReferenceType[] {
				new PcReference(null), 
				new LabelReference(null), 
				new EndpointReference(null), 
				new EndpointFileLineReference(null),		
		        new SrcLabelReference(null), 
		        new SrcLabelFileLineReference(null),
		        new CallReference(null), 
		        new CallFileLineReference(null)});
	}
	private static Engine m_engine;
		
	public static ReferenceType createReferenceType(String reference, ReferenceClass referenceClass) {		
		ReferenceType[] referenceTypes = getReferenceClass(referenceClass); 
		for (ReferenceType referenceType : referenceTypes) {					
			if (referenceType.getClass() == PcReference.class) {
				if (isPcString(reference)) {
					return new PcReference(reference);
				}				
			} else if (referenceType.getClass() == LabelReference.class) {
				if (m_engine.getLabels().containsKey(reference)) {
					return new LabelReference(reference);
				}				
			} else if (referenceType.getClass() == EndpointReference.class) {
				if (m_engine.getEndpoints().containsKey(reference)) {
					return new EndpointReference(reference);
				}				
			} else if (referenceType.getClass() == EndpointFileLineReference.class) {
				if (isSourceReference(reference, EndpointFileLineReference.TYPE) && 
					inCodePointTable(reference, m_engine.getEndpointTable())) {
		    		return new EndpointFileLineReference(reference);
				}
			} else if (referenceType.getClass() == SrcLabelReference.class) {
				if (m_engine.getSrcLabels().containsKey(reference)) {
					return new SrcLabelReference(reference);
				}				
			} else if (referenceType.getClass() == SrcLabelFileLineReference.class) {
				if (isSourceReference(reference, SrcLabelFileLineReference.TYPE) && 
					inSrcLabelTable(reference)) {
		    		return new SrcLabelFileLineReference(reference);	
				}				
			} else if (referenceType.getClass() == CallReference.class) {
				if (m_engine.getCalls().containsKey(reference)) {
					return new CallReference(reference);
				}				
			} else if (referenceType.getClass() == CallFileLineReference.class) {
				if (isSourceReference(reference, CallFileLineReference.TYPE) && 
					inCodePointTable(reference, m_engine.getCallTable())) {
		    		return new CallFileLineReference(reference);	
				}				
			}						
		}
		return new ErrorReference(reference);		
	}
		
	private static boolean inSrcLabelTable(String ref) {
		String fileName = getFileName(ref);
        int lineNumber  = getLineNumber(ref);		

		for (SrcLabelTableInfo info : m_engine.getSrcLabelTable().values()) {
            if (info.getFileName().equals(fileName) &&
            	((lineNumber >= info.getLineNumberStart()) &&
            	 (lineNumber <= info.getLineNumberEnd()))) {
            	return true;
            }			
		}		
		return false;
	}
	
	private static boolean inCodePointTable(String ref, 
			Map<Integer, CodePointTableInfo> codePointTable) {
		
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
	
	public static ReferenceType[] getReferenceClass(ReferenceClass referenceClass) {
    	ReferenceType[] referenceTypes = m_referenceClasses.get(referenceClass);
		assert(referenceTypes != null);
		return referenceTypes;
	}
	
    public static void setEngine(Engine engine) {
    	assert(engine != null);
    	m_engine = engine;
    }

	public static boolean isSourceReference(String ref, String refType) {
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
		String[] refArray = fileLineReference.split(":");
		assert((refArray.length == 2) || (refArray.length == 3));		
		return (refArray.length == 2) ? refArray[0] : refArray[1];		
	}

	public static int getLineNumber(String fileLineReference) {
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
