package com.xmos.xta.core.engine.references;

import com.xmos.xta.core.engine.references.types.ReferenceType;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class ReferenceFactory {
	
	public static Reference createReference(String refString, 
			ReferenceClass referenceClass, int nodeNumber, int coreNumber) {
		
    	String[] subRefs = refString.split(",");

        ReferenceType baseType = ReferenceTypeFactory.createReferenceType(
        		subRefs[subRefs.length - 1], referenceClass, nodeNumber, coreNumber);

        Reference reference = new Reference(baseType);
		for (int index = 0; index < (subRefs.length - 1); ++index) {
			
			ReferenceType bt = ReferenceTypeFactory.createReferenceType( subRefs[index], ReferenceClass.ANY_REF, nodeNumber, coreNumber);
			
			reference.addToBackTrail(bt);
		}
		return reference;
	}
}
