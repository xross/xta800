package com.xmos.xta.core.engine.references.types;

import java.util.Map;

import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.IteratableMap;
import com.xmos.xta.core.engine.Engine;

public class ReferenceTypeFactory {
	public enum ReferenceClass {
		FUNCTION,
		BRANCH,
		INSTRUCTION,
		ENDPOINT,
		ANY,

		FUNCTION_WITH_EVERYTHING,
		BRANCH_WITH_EVERYTHING,
		INSTRUCTION_WITH_EVERYTHING,
		ENDPOINT_WITH_EVERYTHING,
		ANY_WITH_EVERYTHING
	}

	private static CoreId m_coreId = new CoreId(null, 0, 0);
	
	private static Map<ReferenceClass, ReferenceClassType> m_referenceClasses;
	static {
		m_referenceClasses = new IteratableMap<ReferenceClass, ReferenceClassType>();
		
		m_referenceClasses.put(ReferenceClass.FUNCTION,
				new ReferenceClassType(true, new ReferenceType[] {
					new FunctionPcReference(null, m_coreId),
					new FunctionReference(null, m_coreId)}));

		m_referenceClasses.put(ReferenceClass.BRANCH,
				new ReferenceClassType(true, new ReferenceType[] {
					new EndpointPcReference(null, m_coreId),
					new CallPcReference(null, m_coreId),
					new CallFileLineReference(null, m_coreId),
					new CallReference(null, m_coreId),
					new LabelReference(null, m_coreId),
					new CallLabelReference(null, m_coreId)}));

		m_referenceClasses.put(ReferenceClass.INSTRUCTION,
				new ReferenceClassType(true, new ReferenceType[] {
					new EndpointPcReference(null, m_coreId),
					new FunctionPcReference(null, m_coreId),
					new FunctionReference(null, m_coreId),
					new LabelReference(null, m_coreId)}));

		m_referenceClasses.put(ReferenceClass.ENDPOINT,
				new ReferenceClassType(true, new ReferenceType[] {
					new EndpointPcReference(null, m_coreId),
					new EndpointFileLineReference(null, m_coreId),
					new EndpointReference(null, m_coreId),
					new LabelReference(null, m_coreId)}));					

		m_referenceClasses.put(ReferenceClass.ANY,
				new ReferenceClassType(true, new ReferenceType[] {
					new SrcLabelPcReference(null, m_coreId),
					new EndpointPcReference(null, m_coreId),
					new EndpointFileLineReference(null, m_coreId),
					new EndpointReference(null, m_coreId),
					new CallPcReference(null, m_coreId),
					new CallFileLineReference(null, m_coreId),
					new CallReference(null, m_coreId),
					new SrcLabelFileLineReference(null, m_coreId),
					new SrcLabelReference(null, m_coreId),
					new LabelReference(null, m_coreId),
					new CallLabelReference(null, m_coreId)}));		

		m_referenceClasses.put(ReferenceClass.FUNCTION_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null, m_coreId),
					new FunctionPcReference(null, m_coreId),
					new FunctionReference(null, m_coreId)}));

		m_referenceClasses.put(ReferenceClass.BRANCH_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null, m_coreId),
					new EndpointPcReference(null, m_coreId),
					new CallPcReference(null, m_coreId),
					new CallFileLineReference(null, m_coreId),
					new CallReference(null, m_coreId),
					new LabelReference(null, m_coreId),
              		new CallLabelReference(null, m_coreId)}));

		m_referenceClasses.put(ReferenceClass.INSTRUCTION_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null, m_coreId),
					new EndpointPcReference(null, m_coreId),
					new FunctionPcReference(null, m_coreId),
					new FunctionReference(null, m_coreId),
					new LabelReference(null, m_coreId)}));

		m_referenceClasses.put(ReferenceClass.ENDPOINT_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null, m_coreId),
					new EndpointPcReference(null, m_coreId),
					new EndpointFileLineReference(null, m_coreId),
					new EndpointReference(null, m_coreId),
					new LabelReference(null, m_coreId)}));

		m_referenceClasses.put(ReferenceClass.ANY_WITH_EVERYTHING,
				new ReferenceClassType(false, new ReferenceType[] {
					new EverythingReference(null, m_coreId),
					new SrcLabelPcReference(null, m_coreId),
					new EndpointPcReference(null, m_coreId),
					new EndpointFileLineReference(null, m_coreId),
					new EndpointReference(null, m_coreId),
					new CallPcReference(null, m_coreId),
					new CallFileLineReference(null, m_coreId),
					new CallReference(null, m_coreId),
					new SrcLabelFileLineReference(null, m_coreId),
					new SrcLabelReference(null, m_coreId),
					new LabelReference(null, m_coreId),
					new CallLabelReference(null, m_coreId)}));
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

	public static ReferenceType createErrorReferenceType(String reference, CoreId coreId) {
		return new ErrorReference(reference, coreId);
	}

	public static Map<ReferenceClass, ReferenceClassType> getReferenceClasses() {
		return m_referenceClasses;
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
