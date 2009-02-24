package com.xmos.xta.engine.references;

import com.xmos.xta.engine.Engine;

public class CodeReferenceFactory {
	
	private static Engine m_engine;

    public static CodeReference createReference(String reference) {
    	String ref = reference.trim();
    	
    	if (ref.length() == 0) {
			return new ErrorReference(ref);
    	} else if (isPcString(ref)) {
			return new PcReference(ref, getPcString(ref));
		} else if (m_engine.getLabels().containsKey(ref)) {
			return new LabelReference(ref);
		} else {
			return new ErrorReference(ref);
		}
    }

    public static CodeReference createEndpointReference(String reference) {
    	String ref = reference.trim();
    	
    	if (ref.length() == 0) {
			return new ErrorReference(ref);
    	} else if (isPcString(ref)) {
			return new PcReference(ref, getPcString(ref));
    	} else if (isSourceReference(ref)) {
    		return new EndpointFileLineReference(ref);
		} else if (m_engine.getEndpoints().containsKey(ref)) {
			return new EndpointReference(ref);
		} else if (m_engine.getLabels().containsKey(ref)) {
			return new LabelReference(ref);
		} else {
			return new ErrorReference(ref);
		}
    }

    public static CodeReference createSrcLabelReference(String reference) {
    	String ref = reference.trim();

    	if (ref.length() == 0) {
			return new ErrorReference(ref);			
    	} else if (isPcString(ref)) {
			return new PcReference(ref, getPcString(ref));
    	} else if (isSourceReference(ref)) {
    		return new SrcLabelFileLineReference(ref);			
		} else if (m_engine.getSrcLabels().containsKey(ref)) {
			return new SrcLabelReference(ref);
		} else if (m_engine.getLabels().containsKey(reference)) {
			return new LabelReference(ref);
		} else {
			return new ErrorReference(ref);
		}
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
