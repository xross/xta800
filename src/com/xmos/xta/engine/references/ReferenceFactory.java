package com.xmos.xta.engine.references;

import com.xmos.xta.engine.references.types.ReferenceType;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class ReferenceFactory {
	public static Reference createReference(String refString, ReferenceClass referenceClass) {
    	String[] subRefs = refString.split(",");

        ReferenceType baseType = ReferenceTypeFactory.createReferenceType(
        		subRefs[subRefs.length - 1], referenceClass);
        
        Reference reference = new Reference(baseType);
		for (int index = 0; index < (subRefs.length - 1); ++index) {			
			reference.addToBackTrail(ReferenceTypeFactory.createReferenceType(
					subRefs[index], ReferenceClass.ANY_REF));
		}
		return reference;    	
	}
}
