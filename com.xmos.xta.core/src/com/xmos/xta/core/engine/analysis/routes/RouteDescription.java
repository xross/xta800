package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;

import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.StructureGraph;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class RouteDescription {
	private String m_description;
	private String m_functionName;
	private String m_fromName;
	private String m_toName;
	private RouteMode m_mode;
	private Reference m_from;
	private Collection<Reference> m_to;
	private int m_nodeId;
	private int m_coreId;
	private Collection<Reference> m_exclusions;
	private Config m_config;

	public enum RouteMode {
		PATH, FUNCTION
	};

	public RouteDescription(Reference from, Reference to, 
			int nodeId, int coreId, Collection<Reference> exclusions, Config config) {
		
		m_description = String.format("path: %s to %s", from.getRef(), to.getRef());
		m_mode = RouteMode.PATH;
		m_from = from;
		m_to = new LinkedList<Reference>();
		m_to.add(to);
		m_fromName = from.getRef();
		m_toName = to.getRef();
		m_nodeId = nodeId;
		m_coreId = coreId;		
		m_exclusions = new ArrayList<Reference>();
		m_exclusions.addAll(exclusions);		
		m_config = new Config(config);
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

	public String getFromName() {
		return m_fromName;
	}

	public String getToName() {
		return m_toName;
	}
	
	public int getNodeId() {
		return m_nodeId;
	}
	
	public int getCoreId() {
		return m_coreId;
	}
	
	public Collection<Reference> getExclusions() {
		return m_exclusions;
	}
	
	public Config getConfig() {
		return m_config;
	}
}
