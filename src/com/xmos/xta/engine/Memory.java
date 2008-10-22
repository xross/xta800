package com.xmos.xta.engine;

import java.util.HashMap;

class Memory {	
    private static final boolean DEBUG = false;

	private HashMap<Integer, Expression> m_memoryMap;
	
	Memory() {	
	    m_memoryMap = new HashMap<Integer, Expression>();
	}

	Memory(Memory memory) {	
	    m_memoryMap = new HashMap<Integer, Expression>(memory.m_memoryMap);
	}
	
	void write(int address, Expression value) {
	    if (DEBUG) {
	        System.out.println("Memory write " + value + " -> [" + Integer.toHexString(address) + "]");
	    }
        m_memoryMap.put(address, value);
	}

	Expression read(int address) {
	    if (m_memoryMap.containsKey(address)) {
	        Expression value = m_memoryMap.get(address); 
	        if (DEBUG) {
	            System.out.println("Memory read [" + Integer.toHexString(address) + "] ->" + value);
	        }
	        
            return value;
	    }

	    if (DEBUG) {
            System.out.println("Memory read [" + Integer.toHexString(address) + "] -> NEW UNDEFINED");
        }
	    return new Expression(address, 0, Expression.Type.MEMORY, Expression.Status.UNDEFINED);
	}	
}
