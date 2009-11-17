package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.StructureGraph;
import com.xmos.xta.core.engine.analysis.structural.nodes.FunctionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.assertions.Assertions;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceType;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class RouteDescription {
	private String m_description;
	private String m_functionName;
	private String m_fromName;
	private String m_toName;
	private RouteMode m_mode;
	private Reference m_function;
	private Reference m_from;
	private Collection<Reference> m_to;
	private int m_nodeId;
	private int m_coreId;
	private Collection<Reference> m_exclusions;
	private Map<Reference, List<Reference>> m_branches;
	private Assertions m_assertions;
	private Config m_config;

	public enum RouteMode {
		PATH, FUNCTION
	};

	public RouteDescription(Reference from, Reference to, Engine engine, int nodeId, int coreId, 
			Collection<Reference> exclusions, Assertions assertions, Config config) {

		m_mode = RouteMode.PATH;
		m_description = String.format("path: %s to %s", from.getRef(), to.getRef());
		m_from = from;
		m_to = new LinkedList<Reference>();
		m_to.add(to);
		m_fromName = from.getRef();
		m_toName = to.getRef();
		m_nodeId = nodeId;
		m_coreId = coreId;		
		m_exclusions = new ArrayList<Reference>();
		m_exclusions.addAll(exclusions);
		m_assertions = new Assertions(assertions);
		m_config = new Config(config);
		m_branches = engine.getUserBranches();
	}

	public RouteDescription(Reference function, Engine engine, int nodeId, int coreId, 
			Collection<Reference> exclusions, Assertions assertions, Config config) {

		m_mode = RouteMode.FUNCTION;
		m_description = String.format("function: %s", function.getRef());
		m_function = function;
		m_functionName = function.getBase().getRef();
		m_from = function;
		m_to = new LinkedList<Reference>();
		m_nodeId = nodeId;
		m_coreId = coreId;				
		m_exclusions = new ArrayList<Reference>();
		m_exclusions.addAll(exclusions);
		m_assertions = new Assertions(assertions);		
		m_config = new Config(config);
		m_branches = engine.getUserBranches();
		
		StructureGraph sg = engine.getStructureGraph();
        findEndpoints(sg, sg.getThreads());
        findEndpoints(sg, sg.getFunctions());
        
        for (Reference to : m_to) {
        	to.resolve(engine);
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

	public Reference getFunctionReference() {
		return m_function;
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

	public Map<Reference, List<Reference>> getBranches() {
		return m_branches;
	}

	public Assertions getAssertions() {
		return m_assertions;
	}

	public Config getConfig() {
		return m_config;
	}
	
	private void findEndpoints(StructureGraph sg, Collection<FunctionNode> functionNodes) {
		for (FunctionNode fn : functionNodes) {
			if (fn.getFunctionName().equals(m_functionName)) {
				long pc = fn.getStartPc();

				TreeSet<Node> endInstructions = sg.getPcToEndPoints().get(pc);
				
				//TreeSet<Node> endInstructions = fn.getFunctionEndNodes();
				
				for (Node endPoint : endInstructions){
					
					String to = "";
			    	for (ReferenceType backTrail : m_function.getBackTrail()) {
			    		to += backTrail.getRef() + ",";
			    	}					
					to += "0x" + Long.toHexString(endPoint.getStartPc());				
					m_to.add(ReferenceFactory.createReference(to, ReferenceClass.ENDPOINT_REF));
				}
				break;
			}
		}		
	}	
}
