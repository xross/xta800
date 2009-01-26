package com.xmos.xta.engine.references;

public class CodeReferenceFactory {
    public static CodeReference createCodeReference(String reference) {
    	String ref = reference.trim();

    	if (ref.length() == 0) {
			return null;
    	} else if (isPcString(ref)) {
			return new PcReference(ref, getPcString(ref));
		} else if (isSourceReference(ref)) {
			return new FileLineReference(ref);
		} else {
			return new LabelReference(ref);
		}
    }
        
	private static boolean isSourceReference(String ref) {
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

	private static boolean isPcString(String ref) {
		if (ref.matches("0x[0-9a-fA-F]+")){
			return true;
		} else if (ref.matches("[0-9]+")){
			return true;
		} else {
			return false;
		}
	}
	
	private static int getPcString(String ref) {
		if (ref.matches("0x[0-9a-fA-F]+")){
			return Integer.parseInt(ref.substring(2), 16);
		} else if (ref.matches("[0-9]+")){
			return Integer.parseInt(ref, 10);
		} else {
			assert(false);
			return 0;
		}		
	}		
}
