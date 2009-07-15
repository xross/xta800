package com.xmos.xta.engine.references.types;

import java.util.HashMap;
import java.util.Map;

import com.xmos.xta.engine.Engine;

public class ReferenceTypeFactory {
	public enum ReferenceClass {
		PC_REF, LABEL_REF, SRCLABEL_REF, ENDPOINT_REF, CALL_REF, ANY_REF,
	}
	
	private static Map<ReferenceClass, ReferenceType[]> m_referenceClasses;	
	static {
		m_referenceClasses = new HashMap<ReferenceClass, ReferenceType[]>();

		m_referenceClasses.put(ReferenceClass.PC_REF, new ReferenceType[] {
				new PcReference(null)});
		
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
				new EndpointReference(null), 
				new EndpointFileLineReference(null),
				new CallReference(null), 
				new CallFileLineReference(null)});

		m_referenceClasses.put(ReferenceClass.CALL_REF, new ReferenceType[] {
				new PcReference(null), 
				new LabelReference(null), 
				new CallReference(null), 
				new CallFileLineReference(null)});
		
		m_referenceClasses.put(ReferenceClass.ANY_REF, new ReferenceType[] {
				new PcReference(null), 
				new LabelReference(null), 
				new EndpointReference(null), 
				new EndpointFileLineReference(null),		
		        new CallReference(null), 
		        new CallFileLineReference(null),
		        new SrcLabelReference(null), 
		        new SrcLabelFileLineReference(null)});
	}
	private static Engine m_engine;

	public static ReferenceType createReferenceType(String reference, ReferenceClass referenceClass) {		
		ReferenceType[] referenceTypes = getReferenceClass(referenceClass); 
		for (ReferenceType referenceType : referenceTypes) {
			ReferenceType refType = referenceType.create(m_engine, reference);
			if (refType != null) {
				return refType;
			}			
		}
		return new ErrorReference(reference);		
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
}
