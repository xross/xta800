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
	}

	private static Map<ReferenceClass, ReferenceType[]> m_referenceClasses;
	static {
		m_referenceClasses = new HashMap<ReferenceClass, ReferenceType[]>();

		m_referenceClasses.put(ReferenceClass.PC_REF, new ReferenceType[] {
				new EverythingReference(null),
				new PcReference(null)});

		m_referenceClasses.put(ReferenceClass.FUNCTION_REF, new ReferenceType[] {
				new EverythingReference(null),
				new FunctionReference(null)});

		m_referenceClasses.put(ReferenceClass.LABEL_REF, new ReferenceType[] {
				new EverythingReference(null),
				new PcReference(null),
				new LabelReference(null)});

		m_referenceClasses.put(ReferenceClass.ENDPOINT_REF, new ReferenceType[] {
				new EverythingReference(null),
				new PcReference(null),
				new EndpointReference(null),
				new EndpointFileLineReference(null),
				new CallReference(null),
				new CallFileLineReference(null),
				new CallPcReference(null),
				new LabelReference(null)});

		m_referenceClasses.put(ReferenceClass.ANY_REF, new ReferenceType[] {
				new EverythingReference(null),
				new PcReference(null),
				new EndpointReference(null),
				new EndpointFileLineReference(null),
		        new CallReference(null),
		        new CallFileLineReference(null),
		        new CallPcReference(null),
		        new SrcLabelReference(null),
		        new SrcLabelFileLineReference(null),
				new LabelReference(null)});
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
