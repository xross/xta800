package com.xmos.xta.core.engine.debugger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.xmos.libs.core.controlflow.ControlFlow;
import com.xmos.libs.core.controlflow.FunctionControlFlow;
import com.xmos.libs.core.controlflow.nodes.CfgInstructionNode;
import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.libs.core.memorymonitor.MemoryMonitorException;
import com.xmos.libs.core.utils.CoreId;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.routes.Route;
import com.xmos.xta.core.engine.analysis.routes.RouteConstructor;
import com.xmos.xta.core.engine.analysis.routes.BackTrails;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.defines.ExclusionInfo;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceType.Result;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class Debugger {
    private Engine m_engine;

    public Debugger(Engine engine) {
        m_engine = engine;
    }

    public Node getCachedFunction(CommandSource src, String functionName, CoreId coreId) {
        ControlFlow controlFlow = m_engine.getControlFlow(coreId);
        for (FunctionControlFlow functionControlFlow : controlFlow) {
            if (functionControlFlow.getFunctionName().equals(functionName)) {
                try {
                    return m_engine.getCachedFunctionMap(coreId).getCachedFunction(functionControlFlow);

                } catch (AbortedException e) {
                    break;

                } catch (MemoryMonitorException e) {
                    break;
                }
            }
        }
        m_engine.notify(src, Notifier.NOTIFY_ERROR, String.format(
                "Cached function: %s not found", functionName));
        return null;
    }

    public boolean validateReference(String refString) {
        boolean hit = false;
        for (CoreId activeCore : m_engine.getActiveCores()) {
            Reference reference = ReferenceFactory.createReference(refString,
                    ReferenceClass.ANY, activeCore);

            if (reference.resolve(m_engine) == Result.RESOLVED) {
                hit = true;
            }
        }
        return hit;
    }

    public Collection<String> getGlobalReferences(CommandSource src, String refString) {
        Collection<String> matchingReferences = new ArrayList<String>();

        for (CoreId activeCore : m_engine.getActiveCores()) {
            Reference reference = ReferenceFactory.createReference(refString,
                    ReferenceClass.ANY, activeCore);

            if (reference.resolve(m_engine) == Result.RESOLVED) {
                try {
                    for (BackTrails backTrails : RouteConstructor.getPathBackTrails(m_engine, activeCore, reference))
                        matchingReferences.addAll(backTrails.getAsStrings());
                } catch (AbortedException e) {
                } catch (MemoryMonitorException e) {
                }
            }
        }
        return matchingReferences;
    }

    public Collection<String> getRouteReferences(CommandSource src, int routeId, String refString) {
        Collection<Route> routes = m_engine.getRoute(routeId);
        if (routes.isEmpty()) {
            return new ArrayList<String>();
        }

        Collection<String> matchingReferences = new ArrayList<String>();
        try {
            for (Route route : routes) {
                Reference reference = ReferenceFactory.createReference(refString,
                        ReferenceClass.ANY,
                        route.parentRouteCoreContainer().getCoreId());

                if (reference.resolve(m_engine) == Result.RESOLVED) {
                    Set<Node> nodes = route.getReferencedNodes(reference);
                    for (Node node : nodes) {
                        matchingReferences.add(node.getReferenceStr());
                    }
                }
            }
        } catch (AbortedException e) {
            return matchingReferences;
        }
        return matchingReferences;
    }

    public void dumpBackTrails(CommandSource src, String refString, Console console) {
        for (CoreId activeCore : m_engine.getActiveCores()) {
            Reference reference = ReferenceFactory.createReference(refString,
                    ReferenceClass.ANY, activeCore);

            if (reference.resolve(m_engine) == Result.RESOLVED) {
                try {
                    for (BackTrails backTrails : RouteConstructor.getPathBackTrails(m_engine, activeCore, reference))
                        console.display(backTrails.toString());
                } catch (AbortedException e) {
                } catch (MemoryMonitorException e) {
                }
            }
        }
    }

    public void dumpUnresolvedInstructions(CommandSource src, Console console) {
        for (CoreId coreId : m_engine.getXeLoader().getCoreIds()) {

            Collection<Instruction> unresolvedInstructions = new ArrayList<Instruction>();
            for (CfgInstructionNode inst : m_engine.getInstructionDecoder(coreId).getInstructions()) {
                if (inst.getUnresolved()) {
                    unresolvedInstructions.add(inst.getInstruction());
                }
            }

            if (!unresolvedInstructions.isEmpty()) {
                System.out.printf("%s:\n", coreId.getCoreReference());
                for (Instruction instruction : unresolvedInstructions) {
                    System.out.println(instruction);
                }
                System.out.println("");
            }
        }
    }

    public void dumpActiveExclusions(CommandSource src, Console console) {
        for (CoreId coreId : m_engine.getActiveCores()){

            List<BackTrails> exclusionBackTrailsList = new LinkedList<BackTrails>();
            for (ExclusionInfo info : m_engine.getExclusions()) {
                Reference ref = info.getReference();
                if (ref.getCoreId().equals(coreId)) {

                    try {
                        List<BackTrails> backTrailsList = RouteConstructor.getPathBackTrails(m_engine, coreId, ref);
                        exclusionBackTrailsList.addAll(backTrailsList);

                    } catch (AbortedException e) {
                    } catch (MemoryMonitorException e) {
                    }
                }
            }

            if (!exclusionBackTrailsList.isEmpty()) {
                System.out.println(coreId + ":");
                for (BackTrails backTrails : exclusionBackTrailsList){
                    System.out.println(backTrails.getInstruction());
                }
                System.out.println("");
            }
        }
    }

    public void dumpControlFlow(CommandSource src, String functionName, Console console) {
        DumpControlFlow dumper = new DumpControlFlow(m_engine);
        dumper.dump(src, functionName, console);
    }

    public void dumpCallGraph(CommandSource src, Console console) {
        DumpCallGraph dumper = new DumpCallGraph(m_engine);
        dumper.dump(src, console);
    }
}
