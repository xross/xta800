package com.xmos.xta.engine.analysis.routes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.analysis.structural.StructureGraph;
import com.xmos.xta.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class RouteDescription {
	private String m_description;
	private String m_functionName;
	private RouteMode m_mode;
	private Reference m_from;
	private Collection<Reference> m_to;

	enum RouteMode {
		PATH, FUNCTION
	};
	//TODO --node and core id arent used
	public RouteDescription(Reference from, Reference to, int nodeId, int coreId) {
		m_description = String.format("path: %s to %s", from.getRef(), to.getRef());
		m_mode = RouteMode.PATH;
		m_from = from;
		m_to = new LinkedList<Reference>();
		m_to.add(to);

	}

	public RouteDescription(String functionName, Engine engine, int nodeId, int coreId) {
		m_description = String.format("function: %s", functionName);
		m_functionName = functionName;
		m_mode = RouteMode.FUNCTION;
		m_to = new LinkedList<Reference>();
		StructureGraph sg = engine.getStructureGraph();
		
		for (FunctionNode fn : sg.getThreads()){
			if (fn.getFunctionName().equals(functionName)){
				long pc = fn.getStartPc();
				String from = "0x" + Long.toHexString(pc);
				m_from = ReferenceFactory.createReference(
						from, ReferenceClass.ENDPOINT_REF);
				TreeSet<Node> endInstructions = sg.getPcToEndPoints().get(pc);
				for(Node endPoint : endInstructions){
					String to = "0x" + Long.toHexString(endPoint.getStartPc());
					m_to.add(ReferenceFactory.createReference(to, ReferenceClass.ENDPOINT_REF));
				}
				break;
			}
		}
		
		for (FunctionNode fn : sg.getFunctions()){
			if (fn.getFunctionName().equals(functionName)){
				long pc = fn.getStartPc();
				String from = "0x" + Long.toHexString(pc);
				m_from = ReferenceFactory.createReference(
						from, ReferenceClass.ENDPOINT_REF);
				TreeSet<Node> endInstructions = sg.getPcToEndPoints().get(pc);
				for(Node endPoint : endInstructions){
					String to = "0x" + Long.toHexString(endPoint.getStartPc());
					m_to.add(ReferenceFactory.createReference(to, ReferenceClass.ENDPOINT_REF));
				}
				break;
			}
		}
	}
	
	public String getDescription() {
		return m_description;
	}

	public RouteMode getMode() {
		return m_mode;
	}

	public Reference getStartPoint() {
		return m_from;
	}

	public Collection<Reference> getEndPoint() {
		return m_to;
	}

	public String getFunctionName() {
		return m_functionName;
	}
}
