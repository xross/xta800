package com.xmos.xta.engine;

import java.util.HashMap;

import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;

public class InstructionDecodeCache {
	XtaInstructionDecoder m_instructionDecoder;
	HashMap<Integer, XtaInstruction> m_instructionCache;
	SymbolTable m_symbolTable;

	// There can be two FNOPs at one PC
	HashMap<Integer, XtaInstruction> m_fnopCache[];
    
	@SuppressWarnings("unchecked")
	InstructionDecodeCache(SymbolTable symbolTable, XeLoader.Architecture architecture) {		
    	m_instructionDecoder = XtaInstructionDecoder.createInstrcutionDecoder(architecture);
		m_instructionCache = new HashMap<Integer, XtaInstruction>();
        m_fnopCache = new HashMap[2];
        m_fnopCache[0] = new HashMap<Integer, XtaInstruction>();
        m_fnopCache[1] = new HashMap<Integer, XtaInstruction>();
        m_symbolTable = symbolTable;
	}
	
	XtaInstruction decode(int opcode, int pc) {
		if (m_instructionCache.containsKey(pc))
            return m_instructionCache.get(pc);
		        
        XtaInstruction instruction = m_instructionDecoder.decode(opcode, pc);
        instruction.setFunctionName(m_symbolTable.functionName(pc));
        instruction.setFunctionOffset(m_symbolTable.functionOffset(pc));
        m_instructionCache.put(pc, instruction);
        return instruction;
	}
	
    XtaInstruction getFNop(int pc, int fnopNum) {
        assert(fnopNum < 2);
        if (m_fnopCache[fnopNum].containsKey(pc))
            return m_fnopCache[fnopNum].get(pc);
                
        XtaInstruction fnop = new NullInstruction("--FNOP--", 0, pc, 0);
        fnop.setFunctionName(m_symbolTable.functionName(pc));
        fnop.setFunctionOffset(m_symbolTable.functionOffset(pc));
        m_fnopCache[fnopNum].put(pc, fnop);
        return fnop;
    }

    boolean setInstructionTimeTaken(Engine engine, int pc, double timeTaken) {
		if (m_instructionCache.containsKey(pc)) {
			XtaInstruction instruction = m_instructionCache.get(pc);
			if (instruction.canPause()) {
			    instruction.setTimeTaken(timeTaken);
			    return true;
			}
			
			engine.callDisplayers(Displayer.NOTIFY_ERROR, 
					String.format("Instruction at pc: 0x%08x cannot pause", pc));			
            return false;
		}
		engine.callDisplayers(Displayer.NOTIFY_ERROR, 
				String.format("Pc: 0x%08x not found", pc));			
		return false;
    }
    
    double getInstructionTimeTaken(int pc) {
		if (m_instructionCache.containsKey(pc)) {
			XtaInstruction instruction = m_instructionCache.get(pc);
			if (instruction.canPause()) {
			    return instruction.getTimeTaken();
			}
			return 0;
		}
		return 0;    	
    }

    boolean setInstructionNumCycles(Engine engine, int pc, int numCycles) {
		if (m_instructionCache.containsKey(pc)) {
			XtaInstruction instruction = m_instructionCache.get(pc);
			if (instruction.canPause()) {
				instruction.setNumCycles(numCycles);
				return true;
			}
			
			engine.callDisplayers(Displayer.NOTIFY_ERROR, 
					String.format("Instruction at pc: 0x%05x cannot pause", pc));						
            return true;
		}
		engine.callDisplayers(Displayer.NOTIFY_ERROR, 
				String.format("Pc: 0x%05x not found", pc));			
		return false;
    }    
    
    int getInstructionNumCycles(int pc) {
		if (m_instructionCache.containsKey(pc)) {
			XtaInstruction instruction = m_instructionCache.get(pc);
			if (instruction.canPause()) {
				return instruction.getNumCycles();
			}
			return 0;
		}
		return 0;
    }
}
