package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.xmos.isa.Instruction;
import com.xmos.xeloader.XeLoader;
import com.xmos.xeobject.AssemblyLine;
import com.xmos.xeobject.Function;
import com.xmos.xeobject.Line;
import com.xmos.xeobject.XeObject;

public class AnalysisEngine {
	private XeLoader m_xeLoader;
	private NodeCoreInstructionCaches m_mcInstructionCache;
	private ArrayList<XeObject> m_xeObjects;
	private boolean m_unsafeAssumptions;
	private ArrayList<UserSuccessor> m_userSuccessors;
	private HashMap<NodeCore, Integer> m_constantPoolPointers;
	private HashMap<NodeCore, Integer> m_dataPoolPointers;
	private ArrayList<BranchMap> m_branches;
	private HashMap<NodeCore, Node[]> m_nodes;
	private HashMap<NodeCore, Edge[]> m_edges;
	private ArrayList<FunctionReturns> m_functionReturns;
	
	
	private static final boolean MAKEDOT = false;
	private static final boolean DEBUG = false;
	
	
	public AnalysisEngine(XeLoader loader, NodeCoreInstructionCaches coreInstructionCache, ArrayList<XeObject> objects, ArrayList<BranchMap> branches){
		m_constantPoolPointers = new HashMap<NodeCore, Integer>();
		m_dataPoolPointers = new HashMap<NodeCore, Integer>();
		m_mcInstructionCache = coreInstructionCache;
		m_xeLoader = loader; 
		m_xeObjects = objects;
		m_unsafeAssumptions = true;
		m_userSuccessors = new ArrayList<UserSuccessor>();
		m_branches = branches;
		m_edges = new HashMap<NodeCore, Edge[]>();
		m_nodes= new HashMap<NodeCore, Node[]>();
		m_functionReturns = new ArrayList<FunctionReturns>();
	}
	
	public void run(){
		//the order of analysis is important
		locatePoolPointers();
		findAllSuccessors();
		makeAllNodeMaps();

		return;
	}
	
	public void setUnsafeAssumptions(boolean unsafe){
		m_unsafeAssumptions = unsafe;
	}
	
	public void addSuccessor(int fromPc, int toPc){
		m_userSuccessors.add(new UserSuccessor(fromPc, toPc));
	}
	
	public void clearSuccessors(){
		m_userSuccessors.clear();
	}

	public void removeSuccessor(int fromPc){
		Iterator<UserSuccessor> itt = m_userSuccessors.iterator();
		while(itt.hasNext()){
			UserSuccessor us = itt.next();
			if(us.m_fromPc == fromPc){
				itt.remove();
			}
		}
	}
	
	public int getConstantPoolPointer(int node, int core){
		for(NodeCore nc : m_constantPoolPointers.keySet()){
			if(nc.m_nodeId == node && nc.m_coreId == core){
				return m_constantPoolPointers.get(nc);
			}
		}
		return 0;
	}
	
	public int getDataPoolPointer(int node, int core){
		for(NodeCore nc : m_dataPoolPointers.keySet()){
			if(nc.m_nodeId == node && nc.m_coreId == core){
				return m_dataPoolPointers.get(nc);
			}
		}
		return 0;
	}


	
	public HashSet<Integer> getFunctionRetuns(String functionName, int nodeId, int coreId){
		for(FunctionReturns f : m_functionReturns){
			if(f.getNode() == nodeId && f.getCore() == coreId){
				return f.getFunctionReturns(functionName);
			}
		}
		return new HashSet<Integer>();
	}
	
	
	public int getFunctionStartPc(String functionName, int nodeId, int coreId) {
		return getXeObject(nodeId, coreId).getFunctionStartPc().get(functionName);
	}
	
	
	
	private void makeAllNodeMaps(){
		for(int nodeId : m_xeLoader.getNodeIds()){
			for(int coreId: m_xeLoader.getCoreIds(nodeId)){
				XeObject xeObject = getXeObject(nodeId, coreId);
				InstructionDecodeCache decodeCache = m_mcInstructionCache.getInstructionDecodeCache(nodeId, coreId);
				makeNodeMap(xeObject, decodeCache, nodeId, coreId);
			}
		}
	}

	private HashMap<Integer, Node> m_pcToNode = new HashMap<Integer, Node>();
	
	private void makeNodeMap(XeObject xeObject, InstructionDecodeCache decodeCache, int nodeId, int coreId){
		
		ArrayList<Edge> edges = new ArrayList<Edge>();
		ArrayList<Node> nodes = new ArrayList<Node>();
		if(MAKEDOT){
			System.out.println("digraph g {");
		}
		for(AssemblyLine al : xeObject.getInstructions()){
			XtaInstruction inst = decodeCache.getInstruction(al.getPc());
			Node from = m_pcToNode.get(inst.getPc());
			if(from == null){
				from = new Node(nodes.size());
				nodes.add(from);
				m_pcToNode.put(inst.getPc(), from);
				if(MAKEDOT){
					System.out.println("node" + from.name+ " [label = \"0x" + Integer.toHexString(inst.getPc())+ "\"];");
				}
			}
			for(NextPcType target : inst.getSuccessors()){
				Node to = m_pcToNode.get(target.getPc());
				if(to == null){
					to = new Node(nodes.size());
					nodes.add(to);
					m_pcToNode.put(target.getPc(), to);
					if(MAKEDOT){
						System.out.println("node" + to.name + " [label = \"0x" + Integer.toHexString(target.getPc())+ "\"];");
					}
				}
				Edge edge = new Edge( to,from ,1);
				edges.add(edge);
				if(MAKEDOT){
					System.out.println("node" + from.name + " -> node" + to.name + ";");
				}
			}
		}
		if(MAKEDOT){
			System.out.println("}");
		}
		Edge[] edgeArray = edges.toArray(new Edge[] {} );
		m_edges.put(new NodeCore(nodeId, coreId), edgeArray);
		Node[] nodeArray = nodes.toArray(new Node[] {});
		m_nodes.put(new NodeCore(nodeId, coreId), nodeArray);
	}
	
	
	public int determineReachability(int startPc, int endPc, int nodeId, int coreId){
		Node to = getNode(startPc, nodeId, coreId);
		Node from = getNode(endPc, nodeId, coreId);
		if(from!= null && to!= null){
			FloydWarshall fw = new FloydWarshall();
			fw.calcShortestPaths(getNodes(nodeId, coreId), getEdges(nodeId, coreId));
			if(DEBUG){
				for(Node node : fw.getShortestPath(from, to)){
					System.out.println("node: 0x" + Integer.toHexString(getPc(node, nodeId, coreId)));
				}
			}
			return fw.getShortestDistance(from, to);
		} else {
			return Integer.MAX_VALUE;
		}
	}
	
	private int getPc(Node node, int nodeId, int coreId){
		for(int pc : m_pcToNode.keySet()){
			if(m_pcToNode.get(pc).name == node.name){
				return pc;
			}
		}
		return 0;
	}
	
	//warning this may/will break for multicore
	private Node getNode(int pc,int node, int core){
		for(NodeCore nc : m_nodes.keySet()){
			if(nc.m_nodeId == node && nc.m_coreId == core){
				for(Node n : m_nodes.get(nc)){
					if(n == m_pcToNode.get(pc)) return n;
				}
			}
		}
		return null;
	}
	public Edge[] getEdges(int node, int core){
		for(NodeCore nc : m_edges.keySet()){
			if(nc.m_nodeId == node && nc.m_coreId == core){
				return m_edges.get(nc);
			}
		}
		return null;
	}
	public Node[] getNodes(int node, int core){
		for(NodeCore nc : m_nodes.keySet()){
			if(nc.m_nodeId == node && nc.m_coreId == core){
				return m_nodes.get(nc);
			}
		}
		return null;
	}
	
	/*
	 * this tries its best to tag all the resources with their type
	 * and like ones as the same
	 */
	/*
	private void resourceAnalysis(){
		for(int nodeId : m_xeLoader.getNodeIds()){
			for(int coreId: m_xeLoader.getCoreIds(nodeId)){
				InstructionDecodeCache decodeCache = m_mcInstructionCache.getInstructionDecodeCache(nodeId, coreId);
				XeObject xeObject = getXeObject(nodeId, coreId);
				for(AssemblyLine al : xeObject.getInstructions()){
					XtaInstruction inst = decodeCache.getInstruction(al.getPc());
					
				}
			}
		}
	}
	*/
	
	private void locatePoolPointers(){
		for(int nodeId : m_xeLoader.getNodeIds()){
			for(int coreId: m_xeLoader.getCoreIds(nodeId)){
				int cpStart = m_xeLoader.getSymbolTable(nodeId, coreId).symbolAddress("_cp");
				int dpStart = m_xeLoader.getSymbolTable(nodeId, coreId).symbolAddress("_dp");
				m_constantPoolPointers.put(new NodeCore(nodeId, coreId), cpStart);
				m_dataPoolPointers.put(new NodeCore(nodeId, coreId), dpStart);
			}
		}

	}

	private void findAllSuccessors(){
	
		for(int nodeId : m_xeLoader.getNodeIds()){
			for(int coreId: m_xeLoader.getCoreIds(nodeId)){
				XeObject xeObject = getXeObject(nodeId, coreId);
				InstructionDecodeCache decodeCache = m_mcInstructionCache.getInstructionDecodeCache(nodeId, coreId);
				if(m_unsafeAssumptions){
					traceFunctionsRetsps(xeObject, decodeCache, nodeId, coreId);
					addNextInstruction(xeObject, decodeCache);
				}
				addBranchMaps(xeObject, decodeCache, nodeId, coreId);
				
				addBranches(xeObject, decodeCache, nodeId, coreId);
				
				addUserSuccessors(xeObject, decodeCache);
				
				findFunctionRetsps(xeObject, decodeCache, nodeId, coreId);
			}
		}
	}
	
	private void addBranchMaps(XeObject xeObject, InstructionDecodeCache decodeCache, int nodeId, int coreId) {
		for(AssemblyLine al : xeObject.getInstructions()){
			for(BranchMap bm : m_branches){
				if(bm.getBranches().containsKey(al.getPc())){
					XtaInstruction inst = decodeCache.getInstruction(al.getPc());
					for(int target : bm.getBranches().get(al.getPc())){
						inst.addSuccessor(target, true, decodeCache.getInstruction(target).getBranchLink());
					}
				}
			}
		}
	}

	private void addUserSuccessors(XeObject xeObject, InstructionDecodeCache decodeCache) {
		for(AssemblyLine al : xeObject.getInstructions()){
			for(UserSuccessor us : m_userSuccessors){
				if(us.m_fromPc == al.getPc()){
					XtaInstruction inst = decodeCache.getInstruction(al.getPc());
					//inst.addSuccessor(us.m_toPc, true, decodeCache.getInstruction(us.m_toPc).getBranchLink());
					inst.addSuccessor(us.m_toPc, true, false);
				}
			}
		}
	}

	//TODO
	//get rid of this and do it automatically from arch dbs
	private static final HashSet<String> m_noNextPc = new HashSet<String>(
    		Arrays.asList(new RevbXtaInstruction_bau_1r(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_bla_1r(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_blacp_u10(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_blacp_lu10(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_blat_u6(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_blat_lu6(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_blrb_u10(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_blrb_lu10(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_blrf_u10(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_blrf_lu10(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_brbu_u6(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_brbu_lu6(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_brfu_u6(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_brfu_lu6(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_bru_1r(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_dret_0r(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_kret_0r(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_retsp_u6(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_retsp_lu6(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_waitef_1r(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_waitet_1r(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_waiteu_0r(0, 0, null).getNmenonic()));
	
	/*
	 * if the instruction doesn't unconditionally branch then add the next pc to the successor list
	 */
	private void addNextInstruction(XeObject xeObject, InstructionDecodeCache decodeCache) {
		for(AssemblyLine al : xeObject.getInstructions()){
			XtaInstruction inst = decodeCache.getInstruction(al.getPc());
			if(!m_noNextPc.contains(inst.getNmenonic())){
				inst.addSuccessor(inst.getPc() + inst.getNumBytes(), false, false);
			}
		}
	}
	
	private void findFunctionRetsps(XeObject xeObject, 
			InstructionDecodeCache decodeCache, int nodeId, int coreId){
		HashMap<String, HashSet<Integer>> m_nodeCoreMap = new HashMap<String, HashSet<Integer>>();
		for(Function function: xeObject.getFunctions()){
			HashSet<Integer> functionReturns = new HashSet<Integer>();
			for(Line line : function.getChildern()){
				XtaInstruction inst = decodeCache.getInstruction(((AssemblyLine)line).getPc());
				if(isReturn(inst.getNmenonic()))
					functionReturns.add(inst.getPc());
			}
			m_nodeCoreMap.put( function.getText(), functionReturns);
		}
		
		boolean added = false;
		for(FunctionReturns f : m_functionReturns){
			if(f.getNode() == nodeId && f.getCore() == coreId){
				added = true;
				m_functionReturns.set(m_functionReturns.indexOf(f), new FunctionReturns(nodeId, coreId, m_nodeCoreMap));
			}
		}
		if(!added)m_functionReturns.add(new FunctionReturns(nodeId, coreId, m_nodeCoreMap));
	}
	
	
	
	private void traceFunctionsRetsps(XeObject xeObject,
			InstructionDecodeCache decodeCache, int nodeId, int coreId) {
		for(Function function: xeObject.getFunctions()){
			if(function.getChildern().size()<1) continue;
			int functionEntry = ((AssemblyLine)function.getChildern().get(0)).getPc();
			ArrayList<Integer> branchLinkPcs = new ArrayList<Integer>();
			
			for(AssemblyLine al : xeObject.getInstructions()){
				
				XtaInstruction inst = decodeCache.getInstruction(al.getPc());
				if(inst.getBranchLink()){
					int targetPc = inst.getBranchPcRelativeTargetPc();
					if(targetPc == functionEntry){	
						branchLinkPcs.add(inst.getPc() + inst.getNumBytes());
					}
				}
			}
			for(Line line : function.getChildern()){
				XtaInstruction inst = decodeCache.getInstruction(((AssemblyLine)line).getPc());
				if(isReturn(inst.getNmenonic())){
					for(int blPc : branchLinkPcs){
						inst.addSuccessor(blPc, true, false);
					}
				}
			}
			
		}
		
	}
	
	private boolean isReturn(String nmenonic){
		String retsp_u6 = new RevbXtaInstruction_retsp_u6(0, 0, null).getNmenonic();
		String retsp_lu6 = new RevbXtaInstruction_retsp_lu6(0, 0, null).getNmenonic();
		if(retsp_u6.equals(nmenonic)|| retsp_lu6.equals(nmenonic)){
			return true;
		}
		return false;
	}
	
	/*
	 * this adds the known branches to the the list of successors
	 */
	public void addBranches(XeObject xeObject, InstructionDecodeCache decodeCache, int nodeId, int coreId){
		for(AssemblyLine al : xeObject.getInstructions()){
			XtaInstruction inst = decodeCache.getInstruction(al.getPc());
			boolean isBranchLink = inst.getNmenonic().startsWith("bl");
			if(inst.getBranchPcRelative()){
				int pc = inst.getBranchPcRelativeTargetPc();
				inst.addSuccessor(pc , true, isBranchLink);
			}
			if(inst.getNmenonic().equals(new RevbXtaInstruction_blacp_u10(0, 0, null).getNmenonic())) {
				int imm = XtaInstruction.getBits(16, 10, inst.getOpcode()) << Instruction.BYTES_PER_WORD_LOG2;
				int address = getConstantPoolPointer(nodeId, coreId) + imm;
				int word=0;
				for(int i=0;i<4;i++){
					byte b = m_xeLoader.getMemoryImage(nodeId, coreId).readByte(address+i);
					word = word | (b<<(8 * i));
				}
				inst.addSuccessor(word, true,isBranchLink );
				
			}
			if(inst.getNmenonic().equals(new RevbXtaInstruction_blacp_lu10(0, 0, null).getNmenonic())){
				int imm = XtaInstruction.getBitsHL(16, 10, 0, 10, inst.getOpcode()) << Instruction.BYTES_PER_WORD_LOG2;
				int address = getConstantPoolPointer(nodeId, coreId) + imm;
				int word=0;
				for(int i=0;i<4;i++){
					byte b = m_xeLoader.getMemoryImage(nodeId, coreId).readByte(address+i);
					word = word | (b<<(8 * i));
				}
				inst.addSuccessor(word, true, isBranchLink);
			}
		}
	}
	
	
	private XeObject getXeObject(int nodeId, int coreId) {
    	for (XeObject xeObject : m_xeObjects) {
    		if ((xeObject.getNodeId() == nodeId) &&
        		(xeObject.getCoreId() == coreId)) {
    			return xeObject;
    		}    		
    	}
    	assert(false);
    	return null;
    }
	
	class UserSuccessor{
		int m_fromPc;
		int m_toPc;
		UserSuccessor(int fromPc, int toPc){
			m_fromPc = fromPc;
			m_toPc = toPc;
		}
	}
	
	class NodeCore {
		int m_nodeId;
		int m_coreId;
		NodeCore(int node, int core){
			m_nodeId = node;
			m_coreId = core;
		}
	}
}
