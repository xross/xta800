package com.xmos.xta.core.engine.references.types;

import java.util.Map;

import com.xmos.libs.util.NonIteratableMap;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.utils.CoreId;

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

	private static CoreId m_coreId = new CoreId(null, 0, 0);
	
	private static Map<ReferenceClass, ReferenceClassType> m_referenceClasses;
	static {
		m_referenceClasses = new NonIteratableMap<ReferenceClass, ReferenceClassType>();
		
		m_referenceClasses.put(ReferenceClass.PC_REF,
				new ReferenceClassType(true, new ReferenceType[] {
					new PcReference(null, m_coreId)}));

		m_referenceClasses.put(ReferenceClass.FUNCTION_REF,
				new ReferenceClassType(true, new ReferenceType[] {
					new FunctionReference(null, m_coreId)}));

		m_referenceClasses.put(ReferenceClass.LABEL_REF,
				new ReferenceClassType(true, new ReferenceType[] {
					new PcReference(null, m_coreId),
					new LabelReference(null, m_coreId)}));

		m_referenceClasses.put(ReferenceClass.ENDPOINT_REF,
				new ReferenceClassType(true, new ReferenceType[] {
					new PcReference(null, m_coreId),
					new EndpointReference(null, m_coreId),
					new EndpointFileLineReference(null, m_coreId),
					new CallReference(null, m_coreId),
					new CallFileLineReference(null, m_coreId),
					new CallPcReference(null, m_coreId),
					new LabelReference(null, m_coreId)}));

		m_referenceClasses.put(ReferenceClass.ANY_REF,
				new ReferenceClassType(true, new ReferenceType[] {
					new PcReference(null, m_coreId),
					new EndpointReference(null, m_coreId),
					new EndpointFileLineReference(null, m_coreId),
					new CallReference(null, m_coreId),
					new CallFileLineReference(null, m_coreId),
					new CallPcReference(null, m_coreId),
					new SrcLabelReference(null, m_coreId),
					new SrcLabelFileLineReference(null, m_coreId),
					new LabelReference(null, m_coreId)}));
		
		m_referenceClasses.put(ReferenceClass.PC_REF_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null, m_coreId),
					new PcReference(null, m_coreId)}));

		m_referenceClasses.put(ReferenceClass.FUNCTION_REF_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null, m_coreId),
					new FunctionReference(null, m_coreId)}));

		m_referenceClasses.put(ReferenceClass.LABEL_REF_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null, m_coreId),
					new PcReference(null, m_coreId),
					new LabelReference(null, m_coreId)}));

		m_referenceClasses.put(ReferenceClass.ENDPOINT_REF_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null, m_coreId),
					new PcReference(null, m_coreId),
					new EndpointReference(null, m_coreId),
					new EndpointFileLineReference(null, m_coreId),
					new CallReference(null, m_coreId),
					new CallFileLineReference(null, m_coreId),
					new CallPcReference(null, m_coreId),
					new LabelReference(null, m_coreId)}));

		m_referenceClasses.put(ReferenceClass.ANY_REF_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null, m_coreId),
					new PcReference(null, m_coreId),
					new EndpointReference(null, m_coreId),
					new EndpointFileLineReference(null, m_coreId),
					new CallReference(null, m_coreId),
					new CallFileLineReference(null, m_coreId),
					new CallPcReference(null, m_coreId),
					new SrcLabelReference(null, m_coreId),
					new SrcLabelFileLineReference(null, m_coreId),
					new LabelReference(null, m_coreId)}));
	}
	private static Engine m_engine;

	public static ReferenceType createReferenceType(String reference,  ReferenceClass classType, CoreId coreId) {
		
		ReferenceClassType referenceClass = getReferenceClass(classType);
		ReferenceType[] referenceTypes = referenceClass.getReferenceTypes();
		for (ReferenceType referenceType : referenceTypes) {
			ReferenceType refType = referenceType.create(m_engine, reference, coreId);
			if (refType != null) {
				return refType;
			}
		}
		return new ErrorReference(reference, m_coreId);
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
