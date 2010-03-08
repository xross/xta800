package com.xmos.xta.core.engine.references.types;

import java.util.HashMap;
import java.util.Map;

import com.xmos.xta.core.engine.Engine;

public class ReferenceTypeFactory {
	public enum ReferenceClass {
		PC_REF,
		FUNCTION_REF,
		LABEL_REF,
		ENDPOINT_REF,
		ANY_REF,
		PC_REF_WITH_EVERYTHING,
		FUNCTION_REF_WITH_EVERYTHING,
		LABEL_REF_WITH_EVERYTHING,
		ENDPOINT_REF_WITH_EVERYTHING,
		ANY_REF_WITH_EVERYTHING
	}

	private static Map<ReferenceClass, ReferenceClassType> m_referenceClasses;
	static {
		m_referenceClasses = new HashMap<ReferenceClass, ReferenceClassType>();
		
		m_referenceClasses.put(ReferenceClass.PC_REF,
				new ReferenceClassType(true, new ReferenceType[] {
					new PcReference(null, 0, 0)}));

		m_referenceClasses.put(ReferenceClass.FUNCTION_REF,
				new ReferenceClassType(true, new ReferenceType[] {
					new FunctionReference(null, 0, 0)}));

		m_referenceClasses.put(ReferenceClass.LABEL_REF,
				new ReferenceClassType(true, new ReferenceType[] {
					new PcReference(null, 0, 0),
					new LabelReference(null, 0, 0)}));

		m_referenceClasses.put(ReferenceClass.ENDPOINT_REF,
				new ReferenceClassType(true, new ReferenceType[] {
					new PcReference(null, 0, 0),
					new EndpointReference(null, 0, 0),
					new EndpointFileLineReference(null, 0, 0),
					new CallReference(null, 0, 0),
					new CallFileLineReference(null, 0, 0),
					new CallPcReference(null, 0, 0),
					new LabelReference(null, 0, 0)}));

		m_referenceClasses.put(ReferenceClass.ANY_REF,
				new ReferenceClassType(true, new ReferenceType[] {
					new PcReference(null, 0, 0),
					new EndpointReference(null, 0, 0),
					new EndpointFileLineReference(null, 0, 0),
					new CallReference(null, 0, 0),
					new CallFileLineReference(null, 0, 0),
					new CallPcReference(null, 0, 0),
					new SrcLabelReference(null, 0, 0),
					new SrcLabelFileLineReference(null, 0, 0),
					new LabelReference(null, 0, 0)}));
		
		m_referenceClasses.put(ReferenceClass.PC_REF_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null, 0, 0),
					new PcReference(null, 0, 0)}));

		m_referenceClasses.put(ReferenceClass.FUNCTION_REF_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null, 0, 0),
					new FunctionReference(null, 0, 0)}));

		m_referenceClasses.put(ReferenceClass.LABEL_REF_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null, 0, 0),
					new PcReference(null, 0, 0),
					new LabelReference(null, 0, 0)}));

		m_referenceClasses.put(ReferenceClass.ENDPOINT_REF_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null, 0, 0),
					new PcReference(null, 0, 0),
					new EndpointReference(null, 0, 0),
					new EndpointFileLineReference(null, 0, 0),
					new CallReference(null, 0, 0),
					new CallFileLineReference(null, 0, 0),
					new CallPcReference(null, 0, 0),
					new LabelReference(null, 0, 0)}));

		m_referenceClasses.put(ReferenceClass.ANY_REF_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null, 0, 0),
					new PcReference(null, 0, 0),
					new EndpointReference(null, 0, 0),
					new EndpointFileLineReference(null, 0, 0),
					new CallReference(null, 0, 0),
					new CallFileLineReference(null, 0, 0),
					new CallPcReference(null, 0, 0),
					new SrcLabelReference(null, 0, 0),
					new SrcLabelFileLineReference(null, 0, 0),
					new LabelReference(null, 0, 0)}));
	}
	private static Engine m_engine;

	public static ReferenceType createReferenceType(String reference, 
			ReferenceClass classType, int nodeNumber, int coreNumber) {
		
		ReferenceClassType referenceClass = getReferenceClass(classType);
		ReferenceType[] referenceTypes = referenceClass.getReferenceTypes();
		for (ReferenceType referenceType : referenceTypes) {
			ReferenceType refType = referenceType.create(m_engine, reference, nodeNumber, coreNumber);
			if (refType != null) {
				return refType;
			}
		}
		return new ErrorReference(reference, 0, 0);
	}
	
	public static ReferenceClassType getReferenceClass(ReferenceClass classType) {
		ReferenceClassType referenceClass = m_referenceClasses.get(classType);
		assert(referenceClass != null);
		return referenceClass;
	}

    public static void setEngine(Engine engine) {
    	assert(engine != null);
    	m_engine = engine;
    }
}
