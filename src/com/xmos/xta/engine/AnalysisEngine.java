package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import com.xmos.isa.Instruction;
import com.xmos.xeloader.XeLoader;
import com.xmos.xeobject.AssemblyLine;
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
	private ArrayList<FunctionCallLocations> m_functionCallLocations;
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
		m_functionCallLocations = new ArrayList<FunctionCallLocations>();
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

	public TreeSet<Integer> getFunctionRetuns(String functionName, int nodeId, int coreId){
		int functionStartPc = getXeObject(nodeId, coreId).getFunctionStartPc().get(functionName);
		return getFunctionReturns(nodeId, coreId).getFunctionReturns(functionStartPc);
	}

	private FunctionReturns getFunctionReturns(int nodeId, int coreId){
		for(FunctionReturns fr : m_functionReturns){
			if(fr.getNodeId() == nodeId && fr.getCoreId() == coreId){
				return fr;
			}
		}
		FunctionReturns fr = new FunctionReturns(nodeId, coreId);
		m_functionReturns.add(fr);
		return fr;
	}
	
	
	private HashMap<Integer, TreeSet<Integer>> getFunctionCallLocations(int nodeId, int coreId){
		for(FunctionCallLocations fcl : m_functionCallLocations){
			if(fcl.getNodeId() == nodeId && fcl.getCordId() == coreId){
				return fcl.getFunctionCallLocations();
			}
		}
		FunctionCallLocations fcl = new FunctionCallLocations(nodeId, coreId);
		m_functionCallLocations.add(fcl);
		return fcl.getFunctionCallLocations();
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
	/*
	private boolean singlePredessor(int pc){
		return false;
	}
	private boolean singleSuccessor(int pc){
		return false;
	}
	*/
	
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
					addNextInstruction(xeObject, decodeCache);
				}
				addBranchMaps(xeObject, decodeCache, nodeId, coreId);
				
				addBranches(xeObject, decodeCache, nodeId, coreId);
				
				addUserSuccessors(xeObject, decodeCache);
				
				traceFunctionsRetsps(xeObject, decodeCache, nodeId, coreId);
			}
		}
	}
	
	private void addBranchMaps(XeObject xeObject, InstructionDecodeCache decodeCache, int nodeId, int coreId) {
		for(AssemblyLine al : xeObject.getInstructions()){
			for(BranchMap bm : m_branches){
				if(bm.getBranches().containsKey(al.getPc())){
					XtaInstruction inst = decodeCache.getInstruction(al.getPc());
					for(int target : bm.getBranches().get(al.getPc())){
						inst.addSuccessor(target, true);
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
					inst.addSuccessor(us.m_toPc, true);
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
				inst.addSuccessor(inst.getPc() + inst.getNumBytes(), false);
			}
		}
	}

	private void traceFunctionsRetsps(XeObject xeObject,
			InstructionDecodeCache decodeCache, int nodeId, int coreId) {
		for(int branchLinkTarget : getFunctionCallLocations(nodeId, coreId).keySet()){
			followBranchTarget(decodeCache, branchLinkTarget, branchLinkTarget, new HashSet<Integer>(), nodeId, coreId);
		}
		
	}
	
	private void followBranchTarget(InstructionDecodeCache decodeCache, int branchLinkTarget, 
			int currentPc, HashSet<Integer> alreadySeen, int nodeId, int coreId) {
		boolean keepGoing = true;
		HashSet<Integer> nowSeen = new HashSet<Integer>(alreadySeen);
		XtaInstruction previousInstruction = null;
		while(keepGoing){
			XtaInstruction inst = decodeCache.getInstruction(currentPc);
			if(inst == null) return;
			
			if(endOfProgram(inst, previousInstruction)){
				keepGoing = false;
			} else {
			
				if(inst.getBranchLink()){
					currentPc  = inst.getNumBytes() + inst.getPc();
				} else if(isReturn(inst.getNmenonic())){
					keepGoing = false;
					getFunctionReturns(nodeId, coreId).addFunction(branchLinkTarget, inst.getPc());
					for(int returnLocation : getFunctionCallLocations(nodeId, coreId).get(branchLinkTarget)){
						inst.addSuccessor(returnLocation, true);
					}
				} else {
					if(inst.getSuccessors().size() > 1){
						keepGoing = false;
						for(NextPcType nextPc : inst.getSuccessors()){
							if(!nowSeen.contains(nextPc.getPc())){
								nowSeen.add(nextPc.getPc());
								followBranchTarget(decodeCache, branchLinkTarget, nextPc.getPc(), nowSeen, nodeId, coreId);
							}
						}
					} else {
						if(inst.getSuccessors().size() == 1){
							if(!nowSeen.contains(inst.getSuccessors().get(0).getPc())){
								nowSeen.add(inst.getSuccessors().get(0).getPc());
								currentPc = inst.getSuccessors().get(0).getPc();
							} else {
								keepGoing = false;
							}
							currentPc = inst.getSuccessors().get(0).getPc();
						} else {
							keepGoing = false;
						}
					}
					
				}
			}
			previousInstruction = inst;
		}
	}
	
	private boolean endOfProgram(XtaInstruction inst, XtaInstruction previousInstruction) {
		if(inst!= null && previousInstruction!= null){
			if(previousInstruction.getNmenonic().equals(new RevbXtaInstruction_clre_0r(0, 0, null).getNmenonic()) && 
					inst.getNmenonic().equals(new RevbXtaInstruction_waiteu_0r(0,0,null).getNmenonic()))
				return true;
		}
		return false;
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
				inst.addSuccessor(pc , true);
				addToBranchTable(inst, isBranchLink, pc, nodeId, coreId);
			} 
			
			if(inst.getNmenonic().equals(new RevbXtaInstruction_blacp_u10(0, 0, null).getNmenonic())) {
				int imm = XtaInstruction.getBits(16, 10, inst.getOpcode()) << Instruction.BYTES_PER_WORD_LOG2;
				int address = getConstantPoolPointer(nodeId, coreId) + imm;
				int word=0;
				for(int i=0;i<4;i++){
					byte b = m_xeLoader.getMemoryImage(nodeId, coreId).readByte(address+i);
					word = word | (b<<(8 * i));
				}
				inst.addSuccessor(word, true);
				addToBranchTable(inst, isBranchLink, word, nodeId, coreId);
				
			}
			if(inst.getNmenonic().equals(new RevbXtaInstruction_blacp_lu10(0, 0, null).getNmenonic())){
				int imm = XtaInstruction.getBitsHL(16, 10, 0, 10, inst.getOpcode()) << Instruction.BYTES_PER_WORD_LOG2;
				int address = getConstantPoolPointer(nodeId, coreId) + imm;
				int word=0;
				for(int i=0;i<4;i++){
					byte b = m_xeLoader.getMemoryImage(nodeId, coreId).readByte(address+i);
					word = word | (b<<(8 * i));
				}
				inst.addSuccessor(word, true);
				addToBranchTable(inst, isBranchLink, word, nodeId, coreId);
			}
		}
	}

	private void addToBranchTable(XtaInstruction inst, boolean isBranchLink,
			int pc, int nodeId, int coreId) {
		
		if(isBranchLink){
			inst.setBranchLink(true);
			if(getFunctionCallLocations(nodeId, coreId).containsKey(pc)){
				TreeSet<Integer> returnLocations = getFunctionCallLocations(nodeId, coreId).get(pc);
				returnLocations.add(inst.getPc() + inst.getNumBytes());
			} else {
				TreeSet<Integer> returnLocations = new TreeSet<Integer>();
				returnLocations.add(inst.getPc() + inst.getNumBytes());
				getFunctionCallLocations(nodeId, coreId).put(pc, returnLocations);
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
