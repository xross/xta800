package com.xmos.xta.engine;

import java.util.HashMap;
import java.util.HashSet;

import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.unknowns.Unknown;

public class NodeCoreInstructionCaches {
	
	private HashMap<String, InstructionDecodeCache> m_nodeCore;
	private XeLoader m_xeLoader;
	
	public NodeCoreInstructionCaches(Engine engine, XeLoader xeLoader, Config config) {
		m_xeLoader = xeLoader;
		m_nodeCore = new HashMap<String, InstructionDecodeCache>();
		for (int node : m_xeLoader.getNodeIds()) {
			for (int core : m_xeLoader.getCoreIds(node)) {
				addNodeCore(engine, node, core, config);
			}
		}
	}
	
	public void addNodeCore(Engine engine, int node, int core, Config config) {
		String newNodeCore = nodeCoreString(node, core);
		InstructionDecodeCache idc = new InstructionDecodeCache(
				m_xeLoader.getSymbolTable(node, core), m_xeLoader.getArchitecture(), config);
		m_nodeCore.put(newNodeCore, idc);
	}
	
	public XtaInstruction decode(int opcode, int pc, int node, int core) {
		InstructionDecodeCache idc = m_nodeCore.get(nodeCoreString(node, core));
		return idc.decode(opcode, pc);
	}
	
	public InstructionDecodeCache getInstructionDecodeCache(int node, int core) {
		return m_nodeCore.get(nodeCoreString(node, core));
	}
	
	public XtaInstruction getFNop(int pc, int fnopNum, int node, int core) {
		InstructionDecodeCache idc = m_nodeCore.get(nodeCoreString(node, core));
		return idc.getFNop(pc, fnopNum);
	}
	
	public boolean setInstructionTimeTaken(Engine engine, int pc, double timeTaken, int node, int core) {
		InstructionDecodeCache idc = m_nodeCore.get(nodeCoreString(node, core));
		return idc.setInstructionTimeTaken(engine, pc, timeTaken);
	}
	
	public double getInstructionTimeTaken(int pc, int node, int core) {
		InstructionDecodeCache idc = m_nodeCore.get(nodeCoreString(node, core));
		return idc.getInstructionTimeTaken(pc);
	}
	
	public boolean setInstructionNumCycles(Engine engine, int pc, long numCycles, int node, int core) {
		InstructionDecodeCache idc = m_nodeCore.get(nodeCoreString(node, core));
		return idc.setInstructionNumCycles(engine, pc, numCycles);
	}

	public long getInstructionNumCycles(int pc, int node, int core) {
		InstructionDecodeCache idc = m_nodeCore.get(nodeCoreString(node, core));
		return idc.getInstructionNumCycles(pc);
	}
	
	public boolean unsetInstructionTime(int pc, int node, int core) {
		InstructionDecodeCache idc = m_nodeCore.get(nodeCoreString(node, core));
		return idc.unsetInstructionTime(pc);
	}

	public boolean unsetInstructionTime(Reference ref, int node, int core, HashSet<Unknown> unknowns) {
		InstructionDecodeCache idc = m_nodeCore.get(nodeCoreString(node, core));
		return idc.unsetInstructionTime(ref, unknowns);
	}

	private String nodeCoreString(int node, int core) {
		return "node:" + node + " core:" + core;
	}
}
