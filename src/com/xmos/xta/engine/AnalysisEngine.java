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
	
	public AnalysisEngine(XeLoader loader, NodeCoreInstructionCaches coreInstructionCache, ArrayList<XeObject> objects){
		m_constantPoolPointers = new HashMap<NodeCore, Integer>();
		m_dataPoolPointers = new HashMap<NodeCore, Integer>();
		m_mcInstructionCache = coreInstructionCache;
		m_xeLoader = loader; 
		m_xeObjects = objects;
		m_unsafeAssumptions = true;
		m_userSuccessors = new ArrayList<UserSuccessor>();
	}
	
	public void run(){
		locatePoolPointers();
		findAllSuccessors();
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
	
	/*
	 * this tries its best to tag all the resources with their type
	 * and like ones as the same
	 */
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
	
	private void allocatePortsTransferWidth(){
		//TODO
	}
	
	private void findAllSuccessors(){
	
		for(int nodeId : m_xeLoader.getNodeIds()){
			for(int coreId: m_xeLoader.getCoreIds(nodeId)){
				XeObject xeObject = getXeObject(nodeId, coreId);
				InstructionDecodeCache decodeCache = m_mcInstructionCache.getInstructionDecodeCache(nodeId, coreId);
				if(m_unsafeAssumptions){
					traceRetsps(xeObject, decodeCache);
					addNextInstruction(xeObject, decodeCache);
				}
				addBranches(xeObject, decodeCache, nodeId, coreId);
				
				addUserSuccessors(xeObject, decodeCache);
			}
		}
	}
	
	private void addUserSuccessors(XeObject xeObject, InstructionDecodeCache decodeCache) {
		for(AssemblyLine al : xeObject.getInstructions()){
			for(UserSuccessor us : m_userSuccessors){
				if(us.m_fromPc == al.getPc()){
					XtaInstruction inst = decodeCache.getInstruction(al.getPc());
					inst.addSuccessor(us.m_toPc);
				}
			}
		}
	}

	//TODO
	//get rid of this and do it automatically from arch dbs
	private static final HashSet<String> m_noNextPc = new HashSet<String>(
    		Arrays.asList(new RevbXtaInstruction_bau_1r(0, 0).getNmenonic(),
			new RevbXtaInstruction_bla_1r(0, 0).getNmenonic(),
			new RevbXtaInstruction_blacp_u10(0, 0).getNmenonic(),
			new RevbXtaInstruction_blacp_lu10(0, 0).getNmenonic(),
			new RevbXtaInstruction_blat_u6(0, 0).getNmenonic(),
			new RevbXtaInstruction_blat_lu6(0, 0).getNmenonic(),
			new RevbXtaInstruction_blrb_u10(0, 0).getNmenonic(),
			new RevbXtaInstruction_blrb_lu10(0, 0).getNmenonic(),
			new RevbXtaInstruction_blrf_u10(0, 0).getNmenonic(),
			new RevbXtaInstruction_blrf_lu10(0, 0).getNmenonic(),
			new RevbXtaInstruction_brbu_u6(0, 0).getNmenonic(),
			new RevbXtaInstruction_brbu_lu6(0, 0).getNmenonic(),
			new RevbXtaInstruction_brfu_u6(0, 0).getNmenonic(),
			new RevbXtaInstruction_brfu_lu6(0, 0).getNmenonic(),
			new RevbXtaInstruction_bru_1r(0, 0).getNmenonic(),
			new RevbXtaInstruction_dret_0r(0, 0).getNmenonic(),
			new RevbXtaInstruction_kret_0r(0, 0).getNmenonic(),
			new RevbXtaInstruction_retsp_u6(0, 0).getNmenonic(),
			new RevbXtaInstruction_retsp_lu6(0, 0).getNmenonic(),
			new RevbXtaInstruction_waitef_1r(0, 0).getNmenonic(),
			new RevbXtaInstruction_waitet_1r(0, 0).getNmenonic(),
			new RevbXtaInstruction_waiteu_0r(0, 0).getNmenonic()));
	
	/*
	 * if the instruction doesn't unconditionally branch then add the next pc to the successor list
	 */
	private void addNextInstruction(XeObject xeObject, InstructionDecodeCache decodeCache) {
		for(AssemblyLine al : xeObject.getInstructions()){
			XtaInstruction inst = decodeCache.getInstruction(al.getPc());
			if(!m_noNextPc.contains(inst.getNmenonic())){
				inst.addSuccessor(inst.getPc() + inst.getNumBytes());
			}
		}
	}
	
	/*
	 * this adds to the list of successors and retsp's the possible next pc of a retsp
	 */
	public void traceRetsps(XeObject xeObject, InstructionDecodeCache decodeCache){

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
						inst.addSuccessor(blPc);
						inst.addReturnLocation(decodeCache.getInstruction(blPc));
					}
				}
			}
		}
	}
	
	private boolean isReturn(String nmenonic){
		String retsp_u6 = new RevbXtaInstruction_retsp_u6(0, 0).getNmenonic();
		String retsp_lu6 = new RevbXtaInstruction_retsp_lu6(0, 0).getNmenonic();
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
			if(inst.getBranchPcRelative()){
				inst.addSuccessor(inst.getBranchPcRelativeTargetPc());
			}
			
			if(inst.getNmenonic().equals(new RevbXtaInstruction_blacp_u10(0, 0).getNmenonic())) {
				int imm = XtaInstruction.getBits(16, 10, inst.getOpcode()) << Instruction.BYTES_PER_WORD_LOG2;
				int address = getConstantPoolPointer(nodeId, coreId) + imm;
				int word=0;
				for(int i=0;i<4;i++){
					byte b = m_xeLoader.getMemoryImage(nodeId, coreId).readByte(address+i);
					word = word | (b<<(8 * i));
				}
				inst.addSuccessor(word);
				
			}
			if(inst.getNmenonic().equals(new RevbXtaInstruction_blacp_lu10(0, 0).getNmenonic())){
				int imm = XtaInstruction.getBitsHL(16, 10, 0, 10, inst.getOpcode()) << Instruction.BYTES_PER_WORD_LOG2;
				int address = getConstantPoolPointer(nodeId, coreId) + imm;
				int word=0;
				for(int i=0;i<4;i++){
					byte b = m_xeLoader.getMemoryImage(nodeId, coreId).readByte(address+i);
					word = word | (b<<(8 * i));
				}
				inst.addSuccessor(word);
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
