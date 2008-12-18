package com.xmos.xta.engine;

import java.util.HashMap;

import com.xmos.xeloader.MemoryImage;

class Memory {	

	public static final boolean DEBUG = false;
	
	private HashMap<Integer, Byte> m_memoryMap;
	
	Memory() {	
	    m_memoryMap = new HashMap<Integer, Byte>();
	}

	Memory(Memory memory) {	
	    m_memoryMap = new HashMap<Integer, Byte>();
	    m_memoryMap.putAll(memory.m_memoryMap);
	}

	void byteWrite(int address, Expression exp) throws Exception{
		boolean removed = needsRemoving(address, exp);
		if(!removed) byteWrite(address, (byte) exp.getValue());
	}
	
	void shortWrite(int address, Expression exp) throws Exception{
		boolean removed = needsRemoving(address, exp);
		if(!removed) shortWrite(address , (short) exp.getValue());
	}
	
	void wordWrite(int address, Expression exp) throws Exception{
		boolean removed = needsRemoving(address, exp);
		if(!removed) wordWrite(address, exp.getValue());
	}
	
	void byteWrite(int address, byte value){
		if(DEBUG) System.out.printf("\t\tWrite 0x%x at \t0x%x\n", value, address);
		m_memoryMap.put(address, value);
	}

	void shortWrite(int address, short half){
		byte lower = (byte) half;
		byte higher = (byte) (half>>8);
		if(DEBUG) System.out.printf("\twriting two bytes : 0x%x 0x%x to 0x%x 0x%x\n", higher, lower, address+1, address);
		byteWrite(address, lower);
		byteWrite(address+1, higher);
	}

	void wordWrite(int address, int value){
		short lower = (short) value;
		short higher =  (short) (value>>16);
		if(DEBUG) System.out.printf("\twriting two shorts: 0x%x 0x%x to 0x%x 0x%x\n", higher, lower, address+2, address);
		shortWrite(address,lower);
		shortWrite(address+2, higher);
	}
	
	void blockWrite(int address, int bytes, MemoryImage mem){
		for(int i=address;i<address+bytes;i++){
			try {
				byteWrite(i, mem.readByte(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	Expression byteRead(int address) {
		if (m_memoryMap.containsKey(address)) {
			return new Expression( m_memoryMap.get(address));
			
		}
	    return new Expression(address, 0, Expression.Type.MEMORY, Expression.Status.UNDEFINED);
	}	
	Expression halfRead(int address) {
		if (m_memoryMap.containsKey(address) && m_memoryMap.containsKey(address + 1)) {
			
			int lower = m_memoryMap.get(address);
			int higher = m_memoryMap.get(address+1);
			higher = (higher<<8)&0xFF00;
			int half = higher | lower;
			return new Expression(half);
		}
		return new Expression(address, 0, Expression.Type.MEMORY, Expression.Status.UNDEFINED);
	}
	
	Expression wordRead(int address) {
		if (m_memoryMap.containsKey(address) 
				&& m_memoryMap.containsKey(address + 1)
				&& m_memoryMap.containsKey(address + 2)
				&& m_memoryMap.containsKey(address + 3)) {
			int byte0= m_memoryMap.get(address+0);
			int byte1= m_memoryMap.get(address+1);
			int byte2= m_memoryMap.get(address+2);
			int byte3= m_memoryMap.get(address+3);
			byte0 = (byte0<<0)&0xFF;
			byte1 = (byte1<<8)&0xFF00;
			byte2 = (byte2<<16)&0xFF0000;
			byte3 = (byte3<<24)&0xFF000000;
			int full = byte3| byte2 | byte1 | byte0;
			return new Expression(full);
		}
		return new Expression(address, 0, Expression.Type.MEMORY, Expression.Status.UNDEFINED);
	}
	
	private boolean needsRemoving(int address, Expression exp) {
		if(!exp.canResolve()){
			if(m_memoryMap.containsKey(address)){
				m_memoryMap.remove(address);
			}
			return true;
		}
		return false;
	}
}
