package com.xmos.xta.core.engine.references.types;

import com.xmos.libs.core.controlflow.functionmapper.FunctionMapper;
import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.instructiondecoder.InstructionDecoder;
import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.engine.Engine;

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

    public Result resolve(Engine engine) {
        FunctionMapper functionMapper = engine.getFunctionMapper(getCoreId());
        if (functionMapper == null)
            return Result.UNRESOLVED;

        InstructionDecoder instructionDecoder = engine.getInstructionDecoder(getCoreId());
        for (Long pc : getPcs()) {

            CfgInstructionNode node = instructionDecoder.getNode(pc);
            if (node == null) {
                return Result.UNRESOLVED;
            }
            if (!node.getInstruction().isLink()) {
                return Result.UNRESOLVED;
            }
        }

        if (functionMapper.areProfilablePcs(getPcs())) {
            setResolved(true);
            return Result.RESOLVED;
        }
        return Result.UNRESOLVED;
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
