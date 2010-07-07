package com.xmos.xta.core.engine.references;

import java.util.Set;

import com.xmos.libs.util.CoreId;
import com.xmos.xta.core.engine.references.types.EndpointPcReference;
import com.xmos.xta.core.engine.references.types.ReferenceType;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class ReferenceFactory {
	public static final String CORE_ID_SEPARATOR = "::";
	
	public static Reference createReference(String refString, ReferenceClass referenceClass, CoreId coreId) {		
		String referenceString = refString;
		String coreIdString = null;
		
		String[] coreRef = refString.split(CORE_ID_SEPARATOR);
		if (coreRef.length == 2) {
			if (!coreId.getCoreReference().equals(coreRef[0])) {
			    return new Reference(null, ReferenceTypeFactory.createErrorReferenceType(refString, coreId));
			}			
			coreIdString = coreRef[0];
			referenceString = coreRef[1];
		}
		
    	String[] subRefs = referenceString.split(",");
        ReferenceType baseType = ReferenceTypeFactory.createReferenceType(subRefs[subRefs.length - 1], referenceClass, coreId);

        Reference reference = new Reference(coreIdString, baseType);
		for (int index = 0; index < (subRefs.length - 1); ++index) {			
			ReferenceType bt = ReferenceTypeFactory.createReferenceType( subRefs[index], ReferenceClass.ANY, coreId);			
			reference.addToBackTrail(bt);
		}
		return reference;
	}
	
	public static Reference createReference(Reference reference, Set<Long> pcs) {
		ReferenceType baseType = new EndpointPcReference("INTERNAL", pcs, reference.getCoreId());
		
        Reference newReference = new Reference(null, baseType);        
		for (ReferenceType referenceType : reference.getBackTrail()) {
			newReference.addToBackTrail(referenceType);
		}
		return newReference;		
	}
}
