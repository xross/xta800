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
					new PcReference(null)}));

		m_referenceClasses.put(ReferenceClass.FUNCTION_REF,
				new ReferenceClassType(true, new ReferenceType[] {
					new FunctionReference(null)}));

		m_referenceClasses.put(ReferenceClass.LABEL_REF,
				new ReferenceClassType(true, new ReferenceType[] {
					new PcReference(null),
					new LabelReference(null)}));

		m_referenceClasses.put(ReferenceClass.ENDPOINT_REF,
				new ReferenceClassType(true, new ReferenceType[] {
					new PcReference(null),
					new EndpointReference(null),
					new EndpointFileLineReference(null),
					new CallReference(null),
					new CallFileLineReference(null),
					new CallPcReference(null),
					new LabelReference(null)}));

		m_referenceClasses.put(ReferenceClass.ANY_REF,
				new ReferenceClassType(true, new ReferenceType[] {
					new PcReference(null),
					new EndpointReference(null),
					new EndpointFileLineReference(null),
					new CallReference(null),
					new CallFileLineReference(null),
					new CallPcReference(null),
					new SrcLabelReference(null),
					new SrcLabelFileLineReference(null),
					new LabelReference(null)}));
		
		m_referenceClasses.put(ReferenceClass.PC_REF_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null),
					new PcReference(null)}));

		m_referenceClasses.put(ReferenceClass.FUNCTION_REF_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null),
					new FunctionReference(null)}));

		m_referenceClasses.put(ReferenceClass.LABEL_REF_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null),
					new PcReference(null),
					new LabelReference(null)}));

		m_referenceClasses.put(ReferenceClass.ENDPOINT_REF_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null),
					new PcReference(null),
					new EndpointReference(null),
					new EndpointFileLineReference(null),
					new CallReference(null),
					new CallFileLineReference(null),
					new CallPcReference(null),
					new LabelReference(null)}));

		m_referenceClasses.put(ReferenceClass.ANY_REF_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null),
					new PcReference(null),
					new EndpointReference(null),
					new EndpointFileLineReference(null),
					new CallReference(null),
					new CallFileLineReference(null),
					new CallPcReference(null),
					new SrcLabelReference(null),
					new SrcLabelFileLineReference(null),
					new LabelReference(null)}));
	}
	private static Engine m_engine;

	public static ReferenceType createReferenceType(String reference, ReferenceClass classType) {
		ReferenceClassType referenceClass = getReferenceClass(classType);
		ReferenceType[] referenceTypes = referenceClass.getReferencTypes();
		for (ReferenceType referenceType : referenceTypes) {
			ReferenceType refType = referenceType.create(m_engine, reference);
			if (refType != null) {
				return refType;
			}
		}
		return new ErrorReference(reference);
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
