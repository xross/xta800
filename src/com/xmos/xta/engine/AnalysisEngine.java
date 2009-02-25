package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import com.xmos.isa.Instruction;
import com.xmos.xeloader.XeLoader;
import com.xmos.xeobject.AssemblyLine;
import com.xmos.xeobject.XeObject;
import com.xmos.xta.engine.maps.BranchMap;

public class AnalysisEngine {
	private XeLoader m_xeLoader;
	private NodeCoreInstructionCaches m_mcInstructionCache;
	private ArrayList<XeObject> m_xeObjects;
	private boolean m_unsafeAssumptions;
	private ArrayList<UserSuccessor> m_userSuccessors;
	private HashMap<NodeCore, Integer> m_constantPoolPointers;
	private HashMap<NodeCore, Integer> m_dataPoolPointers;
	private ArrayList<BranchMap> m_branches;
	private ArrayList<FunctionCallLocations> m_functionCallLocations;
	private ArrayList<FunctionReturns> m_functionReturns;
	private ArrayList<BasicBlockStartPcs> m_basicBlockStartPcs;

	private static final boolean DEBUG = false;

	public AnalysisEngine(XeLoader loader, NodeCoreInstructionCaches coreInstructionCache, ArrayList<XeObject> objects,
			ArrayList<BranchMap> branches) {
		m_constantPoolPointers = new HashMap<NodeCore, Integer>();
		m_dataPoolPointers = new HashMap<NodeCore, Integer>();
		m_mcInstructionCache = coreInstructionCache;
		m_xeLoader = loader;
		m_xeObjects = objects;
		m_unsafeAssumptions = true;
		m_userSuccessors = new ArrayList<UserSuccessor>();
		m_branches = branches;
		m_functionCallLocations = new ArrayList<FunctionCallLocations>();
		m_functionReturns = new ArrayList<FunctionReturns>();
		m_basicBlockStartPcs = new ArrayList<BasicBlockStartPcs>();
	}

	public void run() {
		// the order of analysis is important
		locatePoolPointers();
		findAllSuccessors();
		makeAllNodeMaps();
		return;
	}

	public void setUnsafeAssumptions(boolean unsafe) {
		m_unsafeAssumptions = unsafe;
	}

	public void addSuccessor(int fromPc, int toPc) {
		m_userSuccessors.add(new UserSuccessor(fromPc, toPc));
	}

	public void clearSuccessors() {
		m_userSuccessors.clear();
	}

	public void removeSuccessor(int fromPc) {
		Iterator<UserSuccessor> itt = m_userSuccessors.iterator();
		while (itt.hasNext()) {
			UserSuccessor us = itt.next();
			if (us.m_fromPc == fromPc) {
				itt.remove();
			}
		}
	}

	public int getConstantPoolPointer(int node, int core) {
		for (NodeCore nc : m_constantPoolPointers.keySet()) {
			if (nc.m_nodeId == node && nc.m_coreId == core) {
				return m_constantPoolPointers.get(nc);
			}
		}
		return 0;
	}

	public int getDataPoolPointer(int node, int core) {
		for (NodeCore nc : m_dataPoolPointers.keySet()) {
			if (nc.m_nodeId == node && nc.m_coreId == core) {
				return m_dataPoolPointers.get(nc);
			}
		}
		return 0;
	}

	public TreeSet<Integer> getFunctionRetuns(String functionName, int nodeId, int coreId) {
		int functionStartPc = getXeObject(nodeId, coreId).getFunctionStartPc().get(functionName);
		return getFunctionReturns(nodeId, coreId).getFunctionReturns(functionStartPc);
	}

	private FunctionReturns getFunctionReturns(int nodeId, int coreId) {
		for (FunctionReturns fr : m_functionReturns) {
			if (fr.getNodeId() == nodeId && fr.getCoreId() == coreId) {
				return fr;
			}
		}
		FunctionReturns fr = new FunctionReturns(nodeId, coreId);
		m_functionReturns.add(fr);
		return fr;
	}

	private HashMap<Integer, TreeSet<Integer>> getFunctionCallLocations(int nodeId, int coreId) {
		for (FunctionCallLocations fcl : m_functionCallLocations) {
			if (fcl.getNodeId() == nodeId && fcl.getCordId() == coreId) {
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

	private void makeAllNodeMaps() {
		for (int nodeId : m_xeLoader.getNodeIds()) {
			for (int coreId : m_xeLoader.getCoreIds(nodeId)) {
				XeObject xeObject = getXeObject(nodeId, coreId);
				InstructionDecodeCache decodeCache = m_mcInstructionCache.getInstructionDecodeCache(nodeId, coreId);
				makeNodeMap(xeObject, decodeCache, nodeId, coreId);

				fillGraphFnops(nodeId, coreId);

				// TODO
				// printDot(nodeId, coreId);
			}
		}
	}

	private void fillGraphFnops(int nodeId, int coreId) {
		HashMap<XtaInstruction, BasicBlock> pcToNode = getBasicBlockStartPcs(nodeId, coreId);
		for (BasicBlock node : pcToNode.values()) {
			node.getPrevoiusNodes().clear();
		}
		for (BasicBlock node : pcToNode.values()) {
			for (BasicBlock nextNode : node.getNextNodes()) {

				nextNode.getPrevoiusNodes().add(node);
			}
		}
		itterateOverGraph(pcToNode);
	}

	private void itterateOverGraph(HashMap<XtaInstruction, BasicBlock> pcToNode) {
		
		for(int i=0;i<pcToNode.values().size();i++){
			for (BasicBlock node : pcToNode.values()) {
				if (node.getPrevoiusNodes().size() == 0 && node.getIBufferStates().size() == 0) {
					 node.setIBuferStart(0);
				} else {
					for (BasicBlock previouseNode : node.getPrevoiusNodes()) {
						ArrayList<IBufferState> bob = new  ArrayList<IBufferState>(previouseNode.getIBufferStates());
						for (IBufferState ibuffer : bob) {
							node.setIBuferStart(ibuffer.finalIBuffer);
						}
					}
				}
			}
		}
		return;
	}

	private void makeNodeMap(XeObject xeObject, InstructionDecodeCache decodeCache, int nodeId, int coreId) {

		HashMap<XtaInstruction, BasicBlock> pcToNode = getBasicBlockStartPcs(nodeId, coreId);

		for (AssemblyLine al : xeObject.getInstructions()) {
			XtaInstruction inst = decodeCache.getInstruction(al.getPc());
			inst.getPredessors().clear();
		}

		for (AssemblyLine al : xeObject.getInstructions()) {
			XtaInstruction inst = decodeCache.getInstruction(al.getPc());
			for (NextPcType successor : inst.getSuccessors()) {
				XtaInstruction succ = decodeCache.getInstruction(successor.getPc());
				if (succ != null) {
					succ.addPredessor(inst);
				}
			}
		}

		for (AssemblyLine al : xeObject.getInstructions()) {
			XtaInstruction inst = decodeCache.getInstruction(al.getPc());

			if (inst.getPredessors().size() == 1) {
				if(inst.getPredessors().get(0).getReturn() || inst.getPredessors().get(0).getBranchLink()){
					pcToNode.put(inst, new BasicBlock(pcToNode.size()));
				} else if (inst.getPredessors().get(0).getSuccessors().size() != 1) {
					pcToNode.put(inst, new BasicBlock(pcToNode.size()));
				}
			} else {
				pcToNode.put(inst, new BasicBlock(pcToNode.size()));
			}
		}
		for (XtaInstruction basicBlockStart : pcToNode.keySet()) {
			XtaInstruction inst = basicBlockStart;
			BasicBlock node = pcToNode.get(basicBlockStart);
			node.getInstructions().add(basicBlockStart);
			while (inst != null && inst.getSuccessors().size() == 1) {
				inst = inst.getSuccessors().get(0).getInstruction();
				if (inst != null && !pcToNode.containsKey(inst)) {
					node.getInstructions().add(inst);
				} else {
					break;
				}
			}
		}
		for (XtaInstruction basicBlockStart : pcToNode.keySet()) {
			BasicBlock node = pcToNode.get(basicBlockStart);
			XtaInstruction inst = node.getInstructions().getLast();
			for (NextPcType nextPc : inst.getSuccessors()) {
				BasicBlock next = pcToNode.get(decodeCache.getInstruction(nextPc.getPc()));
				if (next != null) {
					node.getNextNodes().add(next);
				}
			}
		}
	}

	public void printDot(int nodeId, int coreId) {
		HashMap<XtaInstruction, BasicBlock> pcToNode = getBasicBlockStartPcs(nodeId, coreId);

		System.out.println("digraph g {");
		for (BasicBlock node : pcToNode.values()) {
			int maxFnops =0 ;
			int minFnops =0x10000 ;
			boolean singular = false;
			System.out.print("node" + node.m_nodeId + " [label = \"");
			if(node.getIBufferStates().size() ==1){
				singular = true;
			} else {
				for(IBufferState ibs :node.getIBufferStates()){
					maxFnops = Math.max(maxFnops, ibs.m_fnops.size());
					minFnops = Math.min(minFnops, ibs.m_fnops.size());
				}
				System.out.print("max: " +maxFnops + " min: " + minFnops + "\\n");
			}
			for (XtaInstruction inst : node.getInstructions()) {
				if(singular ){
					for(int pc : node.getIBufferStates().get(0).m_fnops){
						if(pc == inst.getPc()) System.out.print( "FNOP\\n");
					}
				} 
				System.out.print(inst + "\\n");
			}
			System.out.print("\", style=filled, shape=box,");
			if(singular){
				System.out.println("fillcolor=green];");
			} else {
				if(maxFnops == minFnops){
					if(maxFnops == 0){
						System.out.println("fillcolor=green];");
					} else {
						System.out.println("fillcolor=blue];");
					}
					
				} else {
					System.out.println("fillcolor=red];");
				}
				
			}
		}
		for (BasicBlock node : pcToNode.values()) {
			for (BasicBlock nextNode : node.getNextNodes()) {
				System.out.println("node" + node.m_nodeId + " -> node" + nextNode.m_nodeId + ";");
			}
		}
		System.out.println("}");
	}

	private HashMap<XtaInstruction, BasicBlock> getBasicBlockStartPcs(int nodeId, int coreId) {
		for (BasicBlockStartPcs bbsp : m_basicBlockStartPcs) {
			if (bbsp.getCoreId() == coreId && bbsp.getNodeId() == nodeId) {
				return bbsp.getBasicBlockMap();
			}
		}
		BasicBlockStartPcs bb = new BasicBlockStartPcs(nodeId, coreId);
		m_basicBlockStartPcs.add(bb);
		return bb.getBasicBlockMap();
	}

	public int determineReachability(int startPc, int endPc, int nodeId, int coreId) {
		BasicBlock to = getNode(startPc, nodeId, coreId);
		BasicBlock from = getNode(endPc, nodeId, coreId);
		if (from != null && to != null) {
			if (from == to) {
				return 0;
			}
			FloydWarshall fw = new FloydWarshall();
			fw.calcShortestPaths(getNodes(nodeId, coreId), getEdges(nodeId, coreId));
			if (DEBUG) {
				for (BasicBlock node : fw.getShortestPath(from, to)) {
					System.out.println("node: 0x" + Integer.toHexString(getPc(node, nodeId, coreId).getPc()));
				}
			}
			return fw.getShortestDistance(from, to);
		} else {
			return Integer.MAX_VALUE;
		}
	}

	private XtaInstruction getPc(BasicBlock node, int nodeId, int coreId) {
		for (XtaInstruction pc : getBasicBlockStartPcs(nodeId, coreId).keySet()) {
			if (getBasicBlockStartPcs(nodeId, coreId).get(pc).m_nodeId == node.m_nodeId) {
				return pc;
			}
		}
		// FIXME
		return null;
	}

	private BasicBlock getNode(int pc, int nodeId, int coreId) {
		for (BasicBlock n : getBasicBlockStartPcs(nodeId, coreId).values()) {
			LinkedList<XtaInstruction> insts = n.getInstructions();
			for (XtaInstruction inst : insts) {
				if (inst.getPc() == pc) {
					return n;
				}
			}
		}
		return null;
	}

	public Edge[] getEdges(int nodeId, int coreId) {
		ArrayList<Edge> edges = new ArrayList<Edge>();
		for (BasicBlock node : getBasicBlockStartPcs(nodeId, coreId).values()) {
			for (BasicBlock nextNode : node.getNextNodes()) {
				edges.add(new Edge(nextNode, node, 1));
			}
		}
		return edges.toArray(new Edge[] {});
	}

	public BasicBlock[] getNodes(int nodeId, int coreId) {
		return getBasicBlockStartPcs(nodeId, coreId).values().toArray(new BasicBlock[] {});
	}

	private void locatePoolPointers() {
		for (int nodeId : m_xeLoader.getNodeIds()) {
			for (int coreId : m_xeLoader.getCoreIds(nodeId)) {
				int cpStart = m_xeLoader.getSymbolTable(nodeId, coreId).symbolAddress("_cp");
				int dpStart = m_xeLoader.getSymbolTable(nodeId, coreId).symbolAddress("_dp");
				m_constantPoolPointers.put(new NodeCore(nodeId, coreId), cpStart);
				m_dataPoolPointers.put(new NodeCore(nodeId, coreId), dpStart);
			}
		}

	}

	private void findAllSuccessors() {

		for (int nodeId : m_xeLoader.getNodeIds()) {
			for (int coreId : m_xeLoader.getCoreIds(nodeId)) {
				XeObject xeObject = getXeObject(nodeId, coreId);
				InstructionDecodeCache decodeCache = m_mcInstructionCache.getInstructionDecodeCache(nodeId, coreId);
				if (m_unsafeAssumptions) {
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
		for (AssemblyLine al : xeObject.getInstructions()) {
			for (BranchMap bm : m_branches) {
				if (bm.getMap().containsKey(al.getPc())) {
					XtaInstruction inst = decodeCache.getInstruction(al.getPc());
					for (int target : bm.getMap().get(al.getPc())) {
						inst.addSuccessor(decodeCache.getInstruction(target), true);
					}
				}
			}
		}
	}

	private void addUserSuccessors(XeObject xeObject, InstructionDecodeCache decodeCache) {
		for (AssemblyLine al : xeObject.getInstructions()) {
			for (UserSuccessor us : m_userSuccessors) {
				if (us.m_fromPc == al.getPc()) {
					XtaInstruction inst = decodeCache.getInstruction(al.getPc());
					inst.addSuccessor(decodeCache.getInstruction(us.m_toPc), true);
				}
			}
		}
	}

	// TODO
	// get rid of this and do it automatically from arch dbs
	private static final HashSet<String> m_noNextPc = new HashSet<String>(Arrays.asList(new RevbXtaInstruction_bau_1r(
			0, 0, null).getNmenonic(), new RevbXtaInstruction_bla_1r(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_blacp_u10(0, 0, null).getNmenonic(), new RevbXtaInstruction_blacp_lu10(0, 0, null)
					.getNmenonic(), new RevbXtaInstruction_blat_u6(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_blat_lu6(0, 0, null).getNmenonic(), new RevbXtaInstruction_blrb_u10(0, 0, null)
					.getNmenonic(), new RevbXtaInstruction_blrb_lu10(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_blrf_u10(0, 0, null).getNmenonic(), new RevbXtaInstruction_blrf_lu10(0, 0, null)
					.getNmenonic(), new RevbXtaInstruction_brbu_u6(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_brbu_lu6(0, 0, null).getNmenonic(), new RevbXtaInstruction_brfu_u6(0, 0, null)
					.getNmenonic(), new RevbXtaInstruction_brfu_lu6(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_bru_1r(0, 0, null).getNmenonic(), new RevbXtaInstruction_dret_0r(0, 0, null)
					.getNmenonic(), new RevbXtaInstruction_kret_0r(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_retsp_u6(0, 0, null).getNmenonic(), new RevbXtaInstruction_retsp_lu6(0, 0, null)
					.getNmenonic(), new RevbXtaInstruction_waitef_1r(0, 0, null).getNmenonic(),
			new RevbXtaInstruction_waitet_1r(0, 0, null).getNmenonic(), new RevbXtaInstruction_waiteu_0r(0, 0, null)
					.getNmenonic()));

	/*
	 * if the instruction doesn't unconditionally branch then add the next pc to
	 * the successor list
	 */
	private void addNextInstruction(XeObject xeObject, InstructionDecodeCache decodeCache) {
		for (AssemblyLine al : xeObject.getInstructions()) {
			XtaInstruction inst = decodeCache.getInstruction(al.getPc());
			if (!m_noNextPc.contains(inst.getNmenonic())) {
				inst.addSuccessor(decodeCache.getInstruction(inst.getPc() + inst.getNumBytes()), false);
			}
		}
	}

	private void traceFunctionsRetsps(XeObject xeObject, InstructionDecodeCache decodeCache, int nodeId, int coreId) {
		for (int branchLinkTarget : getFunctionCallLocations(nodeId, coreId).keySet()) {
			followBranchTarget(decodeCache, branchLinkTarget, branchLinkTarget, new HashSet<Integer>(), nodeId, coreId);
		}

	}

	private void followBranchTarget(InstructionDecodeCache decodeCache, int branchLinkTarget, int currentPc,
			HashSet<Integer> alreadySeen, int nodeId, int coreId) {
		boolean keepGoing = true;
		HashSet<Integer> nowSeen = new HashSet<Integer>(alreadySeen);
		XtaInstruction previousInstruction = null;
		while (keepGoing) {
			XtaInstruction inst = decodeCache.getInstruction(currentPc);
			if (inst == null)
				return;

			if (endOfProgram(inst, previousInstruction)) {
				keepGoing = false;
			} else {

				if (inst.getBranchLink()) {
					currentPc = inst.getNumBytes() + inst.getPc();
				} else if (isReturn(inst.getNmenonic())) {
					keepGoing = false;
					getFunctionReturns(nodeId, coreId).addFunction(branchLinkTarget, inst.getPc());
					for (int returnLocation : getFunctionCallLocations(nodeId, coreId).get(branchLinkTarget)) {
						inst.addSuccessor(decodeCache.getInstruction(returnLocation), true);
					}
				} else {
					if (inst.getSuccessors().size() > 1) {
						keepGoing = false;
						for (NextPcType nextPc : inst.getSuccessors()) {
							if (!nowSeen.contains(nextPc.getPc())) {
								nowSeen.add(nextPc.getPc());
								followBranchTarget(decodeCache, branchLinkTarget, nextPc.getPc(), nowSeen, nodeId,
										coreId);
							}
						}
					} else {
						if (inst.getSuccessors().size() == 1) {
							if (!nowSeen.contains(inst.getSuccessors().get(0).getPc())) {
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
		if (inst != null && previousInstruction != null) {
			if (previousInstruction.getNmenonic().equals(new RevbXtaInstruction_clre_0r(0, 0, null).getNmenonic())
					&& inst.getNmenonic().equals(new RevbXtaInstruction_waiteu_0r(0, 0, null).getNmenonic()))
				return true;
		}
		return false;
	}

	private boolean isReturn(String nmenonic) {
		String retsp_u6 = new RevbXtaInstruction_retsp_u6(0, 0, null).getNmenonic();
		String retsp_lu6 = new RevbXtaInstruction_retsp_lu6(0, 0, null).getNmenonic();
		if (retsp_u6.equals(nmenonic) || retsp_lu6.equals(nmenonic)) {
			return true;
		}
		return false;
	}

	/*
	 * this adds the known branches to the the list of successors
	 */
	public void addBranches(XeObject xeObject, InstructionDecodeCache decodeCache, int nodeId, int coreId) {
		for (AssemblyLine al : xeObject.getInstructions()) {
			XtaInstruction inst = decodeCache.getInstruction(al.getPc());
			boolean isBranchLink = inst.getNmenonic().startsWith("bl");
			if (inst.getBranchPcRelative()) {
				int pc = inst.getBranchPcRelativeTargetPc();
				inst.addSuccessor(decodeCache.getInstruction(pc), true);
				addToBranchTable(inst, isBranchLink, pc, nodeId, coreId);
			}

			if (inst.getNmenonic().equals(new RevbXtaInstruction_blacp_u10(0, 0, null).getNmenonic())) {
				int imm = XtaInstruction.getBits(16, 10, inst.getOpcode()) << Instruction.NUM_BYTES_PER_WORD_LOG2;
				int address = getConstantPoolPointer(nodeId, coreId) + imm;
				int word = 0;
				for (int i = 0; i < 4; i++) {
					byte b = m_xeLoader.getMemoryImage(nodeId, coreId).readByte(address + i);
					word = word | (b << (8 * i));
				}
				inst.addSuccessor(decodeCache.getInstruction(word), true);
				addToBranchTable(inst, isBranchLink, word, nodeId, coreId);

			}
			if (inst.getNmenonic().equals(new RevbXtaInstruction_blacp_lu10(0, 0, null).getNmenonic())) {
				int imm = XtaInstruction.getBitsHL(16, 10, 0, 10, inst.getOpcode()) << Instruction.NUM_BYTES_PER_WORD_LOG2;
				int address = getConstantPoolPointer(nodeId, coreId) + imm;
				int word = 0;
				for (int i = 0; i < 4; i++) {
					byte b = m_xeLoader.getMemoryImage(nodeId, coreId).readByte(address + i);
					word = word | (b << (8 * i));
				}
				inst.addSuccessor(decodeCache.getInstruction(word), true);
				addToBranchTable(inst, isBranchLink, word, nodeId, coreId);
			}
		}
	}

	private void addToBranchTable(XtaInstruction inst, boolean isBranchLink, int pc, int nodeId, int coreId) {

		if (isBranchLink) {
			inst.setBranchLink(true);
			if (getFunctionCallLocations(nodeId, coreId).containsKey(pc)) {
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
			if ((xeObject.getNodeId() == nodeId) && (xeObject.getCoreId() == coreId)) {
				return xeObject;
			}
		}
		assert (false);
		return null;
	}

	class UserSuccessor {
		int m_fromPc;
		int m_toPc;

		UserSuccessor(int fromPc, int toPc) {
			m_fromPc = fromPc;
			m_toPc = toPc;
		}
	}

	class NodeCore {
		int m_nodeId;
		int m_coreId;

		NodeCore(int node, int core) {
			m_nodeId = node;
			m_coreId = core;
		}
	}
}
