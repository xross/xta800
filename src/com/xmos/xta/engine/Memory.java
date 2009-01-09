package com.xmos.xta.engine;

import java.util.HashMap;
import java.util.HashSet;

import com.xmos.xeloader.MemoryImage;

class Memory {	

	public static final boolean DEBUG = false;
	
	private HashMap<Integer, Byte> m_memoryMap;
	private HashSet<Integer> m_unknownMemoryMap;
	private Memory m_parentMemory;
	
	Memory() {	
		m_parentMemory = null;
	    m_memoryMap = new HashMap<Integer, Byte>();
	    m_unknownMemoryMap = new HashSet<Integer>();
	}

	Memory(Memory memory) {	
		m_parentMemory = memory;
	    m_memoryMap = new HashMap<Integer, Byte>();
	    m_unknownMemoryMap = new HashSet<Integer>();
	}

	void byteWrite(int address, Expression exp) throws Exception{
		
		if(exp.canResolve()){
			//remove this byte from the unknowns if it is in there
			//if(m_unknownMemoryMap.contains(address))
			//	m_unknownMemoryMap.remove(address);
			//add this byte to the memoryMap
			byteWrite(address, (byte) exp.getValue());
		} else {
			//remove this byte from the memoryMap
			if(m_memoryMap.containsKey(address))
				m_memoryMap.remove(address);
			//add this byte to the unknowns
			m_unknownMemoryMap.add(address);
		}
	}
	
	void shortWrite(int address, Expression exp) throws Exception{
		
		if(exp.canResolve()){
			//remove this byte from the unknowns if it is in there
			if(m_unknownMemoryMap.contains(address)){
				m_unknownMemoryMap.remove(address);
				m_unknownMemoryMap.remove(address+1);
			}
				
			//add this byte to the memoryMap
			shortWrite(address , (short) exp.getValue());
		} else {
			//remove this byte from the memoryMap
			if(m_memoryMap.containsKey(address)){
				m_memoryMap.remove(address);
				m_memoryMap.remove(address+1);
			}
				
			//add this byte to the unknowns
			m_unknownMemoryMap.add(address);
			m_unknownMemoryMap.add(address+1);
		}
		
	}
	
	void wordWrite(int address, Expression exp) throws Exception{
		if(exp.canResolve()){
			//remove this byte from the unknowns if it is in there
			if(m_unknownMemoryMap.contains(address)){
				m_unknownMemoryMap.remove(address);
				m_unknownMemoryMap.remove(address+1);
				m_unknownMemoryMap.remove(address+2);
				m_unknownMemoryMap.remove(address+3);
			}
				
			//add this byte to the memoryMap
			wordWrite(address, exp.getValue());
		} else {
			//remove this byte from the memoryMap
			if(m_memoryMap.containsKey(address)){
				m_memoryMap.remove(address);
				m_memoryMap.remove(address+1);
				m_memoryMap.remove(address+2);
				m_memoryMap.remove(address+3);
			}
				
			//add this byte to the unknowns
			m_unknownMemoryMap.add(address);
			m_unknownMemoryMap.add(address+1);
			m_unknownMemoryMap.add(address+2);
			m_unknownMemoryMap.add(address+3);
		}
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
		if(m_unknownMemoryMap.contains(address)){
			 return new Expression(address, 0, Expression.Type.MEMORY, Expression.Status.UNDEFINED);
		} else if(m_memoryMap.containsKey(address)){
			return new Expression( m_memoryMap.get(address));
		} else {
			//try the parent
			if(m_parentMemory!=null){
				return m_parentMemory.byteRead(address);
			} else {
				return new Expression(address, 0, Expression.Type.MEMORY, Expression.Status.UNDEFINED);
			}
		}
	}	
	Expression halfRead(int address) throws Exception {
		Expression lower = byteRead(address);
		Expression higher = byteRead(address);
		
		if(lower.canResolve() && higher.canResolve()){
			int lowerbyte = byteRead(address).getValue();
			int higherbyte = byteRead(address+1).getValue();
			higherbyte = (higherbyte<<8)&0xFF00;
			int half = higherbyte | lowerbyte;
			return new Expression(half);
		} else {
			return new Expression(address, 0, Expression.Type.MEMORY, Expression.Status.UNDEFINED);
		}
	}
	
	Expression wordRead(int address) throws Exception {
		Expression byte0 = byteRead(address);
		Expression byte1 = byteRead(address+1);
		Expression byte2 = byteRead(address+2);
		Expression byte3 = byteRead(address+3);
		
		if(byte0.canResolve() && byte1.canResolve()
				&& byte2.canResolve() && byte3.canResolve()){
			int byte0b= byte0.getValue();
			int byte1b= byte1.getValue();
			int byte2b= byte2.getValue();
			int byte3b= byte3.getValue();
			byte0b = (byte0b<<0)&0xFF;
			byte1b = (byte1b<<8)&0xFF00;
			byte2b = (byte2b<<16)&0xFF0000;
			byte3b = (byte3b<<24)&0xFF000000;
			int full = byte3b| byte2b | byte1b | byte0b;
			return new Expression(full);
		} else
			return new Expression(address, 0, Expression.Type.MEMORY, Expression.Status.UNDEFINED);
	}
	
}
