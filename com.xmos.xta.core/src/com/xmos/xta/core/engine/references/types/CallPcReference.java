package com.xmos.xta.core.engine.references.types;

import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.binary.XtaInstruction;
import com.xmos.xta.core.engine.analysis.controlflows.functionmapper.FunctionMapper;
import com.xmos.xta.core.engine.utils.CoreId;

public class CallPcReference extends ReferenceType {
	CallPcReference(String ref, CoreId coreId) {
		super(ref, Type.CALL, coreId);
		if (ref != null) {
		    addPc(getPcString(ref));
		}
	}

	public ReferenceType create(Engine engine, String reference, CoreId coreId) {
		if (checkTypeString(reference, Type.CALL, 1) &&
			isPcString(reference)) {
	    	return new CallPcReference(reference, coreId);
		}
		return null;
	}

	public boolean resolve(Engine engine) {
		FunctionMapper functionMapper = engine.getFunctionMapper(getCoreId());
		if (functionMapper == null)
			return false;

		InstructionDecoder instructionDecoder = engine.getInstructionDecoder(getCoreId());
		for (Long pc : getPcs()) {
			XtaInstruction inst = instructionDecoder.getNode(pc).getXtaInstruction();
			if (!inst.isLink()) {
				return false;
			}
		}

		if (functionMapper.areProfilablePcs(getPcs())) {
	        setResolved(true);
	        return true;
	    }
	    return false;
	}

	public String name() {
		return "CallPc";
	}

	public String description() {
		return "Call specified in the format: 0x*";
	}

	public boolean isPortable() {
		return false;
	}
}
