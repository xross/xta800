package com.xmos.xta.engine.references;

import java.util.HashMap;
import java.util.Map;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.sections.EndpointTableInfo;
import com.xmos.xta.engine.sections.SrcLabelTableInfo;

public class ReferenceFactory {
	public enum ReferenceClass {
		LABEL_REF, SRCLABEL_REF, ENDPOINT_REF, CONSTRAINT_REF
	}
	
	private static Map<ReferenceClass, Reference[]> m_referenceClasses;	
	static {
		m_referenceClasses = new HashMap<ReferenceClass, Reference[]>();
		
		m_referenceClasses.put(ReferenceClass.LABEL_REF, new Reference[] {
				new PcReference(null), 
				new LabelReference(null)}); 
		
		m_referenceClasses.put(ReferenceClass.SRCLABEL_REF, new Reference[] {
				new PcReference(null),
				new LabelReference(null),
				new SrcLabelReference(null),
				new SrcLabelFileLineReference(null)});
		
		m_referenceClasses.put(ReferenceClass.ENDPOINT_REF, new Reference[] {
				new PcReference(null), 
				new LabelReference(null), 
				new EndpointReference(null), 
				new EndpointFileLineReference(null)});
		
		m_referenceClasses.put(ReferenceClass.CONSTRAINT_REF, new Reference[] {
				new PcReference(null), 
				new LabelReference(null), 
				new EndpointReference(null), 
				new EndpointFileLineReference(null),		
		        new SrcLabelReference(null), 
		        new SrcLabelFileLineReference(null)});
	}
	private static Engine m_engine;
	
	public static Reference createReference(String reference, ReferenceClass referenceClass) {
    	if (reference.length() == 0)
			return new ErrorReference(reference);		
		
    	Reference[] referenceTypes = getReferenceClass(referenceClass); 
		for (Reference referenceType : referenceTypes) {					
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
				if (isSourceReference(reference) && inEndpointTable(reference)) {
		    		return new EndpointFileLineReference(reference);
				}
			} else if (referenceType.getClass() == SrcLabelReference.class) {
				if (m_engine.getSrcLabels().containsKey(reference)) {
					return new SrcLabelReference(reference);
				}				
			} else if (referenceType.getClass() == SrcLabelFileLineReference.class) {
				if (isSourceReference(reference) && inSrcLabelTable(reference)) {
		    		return new SrcLabelFileLineReference(reference);	
				}				
			}
		}
		return new ErrorReference(reference);
	}
	    
	private static boolean inSrcLabelTable(String ref) {
		String fileName = ReferenceFactory.getFileName(ref);
        int lineNumber  = ReferenceFactory.getLineNumber(ref);		

		for (SrcLabelTableInfo info : m_engine.getSrcLabelTable().values()) {
            if (info.getFileName().equals(fileName) &&
            	((lineNumber >= info.getLineNumberStart()) &&
            	 (lineNumber <= info.getLineNumberEnd()))) {
            	return true;
            }			
		}		
		return false;
	}
	
	private static boolean inEndpointTable(String ref) {
		String fileName = ReferenceFactory.getFileName(ref);
        int lineNumber  = ReferenceFactory.getLineNumber(ref);		

		for (EndpointTableInfo info : m_engine.getEndpointTable().values()) {
            if (info.getFileName().equals(fileName) &&
            	(info.getLineNumber() == lineNumber)) {
            	return true;
            }			
		}		
		return false;
	}	

	public static Reference[] getReferenceClass(ReferenceClass referenceClass) {
    	Reference[] referenceTypes = m_referenceClasses.get(referenceClass);
		assert(referenceTypes != null);
		return referenceTypes;
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
