package com.xmos.xta.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.PatternSyntaxException;

import com.xmos.xeloader.SymbolTable;
import com.xmos.xeloader.XeLoader;
import com.xmos.xta.engine.references.Reference;

public class InstructionDecodeCache {
	private Engine m_engine;
	private XtaInstructionDecoder m_instructionDecoder;
	private HashMap<Integer, XtaInstruction> m_instructionCache;
	private ArrayList<Integer> m_instructionTimeCache;
	private SymbolTable m_symbolTable;
	private Config m_config;

	// There can be two FNOPs at one PC
	private HashMap<Integer, XtaInstruction> m_fnopCache[];
    
	@SuppressWarnings("unchecked")
	InstructionDecodeCache(Engine engine, SymbolTable symbolTable, XeLoader.Architecture architecture, Config config) {
		m_engine = engine;
    	m_instructionDecoder = XtaInstructionDecoder.createInstrcutionDecoder(architecture);
		m_instructionCache = new HashMap<Integer, XtaInstruction>();
        m_fnopCache = new HashMap[2];
        m_fnopCache[0] = new HashMap<Integer, XtaInstruction>();
        m_fnopCache[1] = new HashMap<Integer, XtaInstruction>();
        m_symbolTable = symbolTable;
        m_config = config;
        
        m_instructionTimeCache = new ArrayList<Integer>();
	}
	
	XtaInstruction getInstruction(int pc){
		if (m_instructionCache.containsKey(pc))
            return m_instructionCache.get(pc);
		 return null;
	}
	
	XtaInstruction decode(int opcode, int pc) {
		if (m_instructionCache.containsKey(pc))
            return m_instructionCache.get(pc);
		        
        XtaInstruction instruction = m_instructionDecoder.decode(opcode, pc, m_config);
        instruction.setFunctionName(m_symbolTable.functionName(pc));
        instruction.setFunctionOffset(m_symbolTable.functionOffset(pc));
        m_instructionCache.put(pc, instruction);
        return instruction;
	}
	
    XtaInstruction getFNop(int pc, int fnopNum) {
        assert(fnopNum < 2);
        if (m_fnopCache[fnopNum].containsKey(pc))
            return m_fnopCache[fnopNum].get(pc);
                
        XtaInstruction fnop = new NullInstruction("--FNOP--", m_config, 0, pc, 0);
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
			    
			    if (!m_instructionTimeCache.contains(pc))
			    	m_instructionTimeCache.add(pc);
			    
			    return true;
			}
			
			engine.notify(Notifier.NOTIFY_ERROR, 
					String.format("Instruction at PC: 0x%08x cannot pause", pc));			
            return false;
		}
		engine.notify(Notifier.NOTIFY_ERROR, 
				String.format("PC: 0x%08x not found", pc));			
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

			    if (!m_instructionTimeCache.contains(pc))
			    	m_instructionTimeCache.add(pc);
			    return true;
			}
			
			engine.notify(Notifier.NOTIFY_ERROR, 
					String.format("Instruction at PC: 0x%05x cannot pause", pc));						
            return true;
		}
		engine.notify(Notifier.NOTIFY_ERROR, 
				String.format("PC: 0x%05x not found", pc));			
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
    
    void resetInstructionTimes() {
    	while (m_instructionTimeCache.size() > 0) {
    		unsetInstructionTime(m_instructionTimeCache.get(0));
    	}
    }

	public ArrayList<Integer> getPcsWithTimeSet() {
		return m_instructionTimeCache;
	}

	public boolean unsetInstructionTime(int pc) {
		if (m_instructionTimeCache.contains(pc)) {
    		assert(m_instructionCache.containsKey(pc));
    		XtaInstruction instruction = m_instructionCache.get(pc);
    		instruction.resetNumCycles();
    		m_instructionTimeCache.remove((Integer)pc);

    		boolean unknownsChanged = m_engine.updateUnknown(pc, false);
    		if (unknownsChanged)
    			m_engine.notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
    		return true;
		} else {
			return false;
		}
	}

	public boolean unsetInstructionTime(Reference ref) {
		if (ref.getResolved()) {
			return unsetInstructionTime(ref.getPc());
		}			
		
		ArrayList<Integer> matches = new ArrayList<Integer>();
		boolean unknownsChanged = false; 

		for (Integer pc : m_instructionTimeCache) {
    		String thisPcString = "0x" + Integer.toHexString(pc); 

    		try {
	    		if (thisPcString.matches(ref.getRef())) {
	        		assert(m_instructionCache.containsKey(pc));
	        		XtaInstruction instruction = m_instructionCache.get(pc);
	        		instruction.resetNumCycles();
	        		matches.add(pc);

	        		unknownsChanged |= m_engine.updateUnknown(pc, false);
	    		}
			} catch (PatternSyntaxException e) {
				// Ignore
			}
		}
    	
		if (matches.size() > 0) {
			for (Integer toRemove : matches)
				m_instructionTimeCache.remove(toRemove);
		}

		if (unknownsChanged)
			m_engine.notify(Notifier.NOTIFY_UPDATE_UNKNOWNS, "");
		
		return (matches.size() > 0);
	}

}
