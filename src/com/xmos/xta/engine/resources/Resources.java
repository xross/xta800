package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.InstructionBuffer;
import com.xmos.xta.engine.Registers;

public class Resources {
	
	private static final boolean VERBOSE_WARNINGS = false;

    public static final int RES_TYPE_PORT = 0;
    public static final int RES_TYPE_TIMER  = 1;
    public static final int RES_TYPE_CHANEND  = 2;
    public static final int RES_TYPE_SYNC = 3;
    public static final int RES_TYPE_THREAD  = 4;
    public static final int RES_TYPE_LOCK = 5;
    public static final int RES_TYPE_CLKBLK = 6;
    public static final int RES_TYPE_COPROC = 7;

    public static final int NUM_TIMERS = 10;
    public static final int NUM_CHANNELS = 32;
    public static final int NUM_LOCKS = 4;
    public static final int NUM_PORTS = 32;
    public static final int NUM_SYNCHRONISERS = 7;
    public static final int NUM_CLOCKBLOCKS = 6;
    public static final int NUM_THREADS = 8;
    public static final int NUM_COPROC = 1;
    
    private Coprocessor[] m_coprocs;
    private Timer[] m_timers;
    private Channel[] m_channels;
    private Lock[] m_locks;
    private Port[] m_ports;
    private Synchroniser[] m_synchronisers;
    private Thread[] m_threads;
    private ClockBlock[] m_clockBlocks;

    
    public Resources(int node, int core ){
        //set up all the resources
        m_timers = new Timer[NUM_TIMERS];
        m_channels = new Channel[NUM_CHANNELS];
        m_locks = new Lock[NUM_LOCKS];
        m_ports = new Port[NUM_PORTS];
        m_synchronisers = new Synchroniser[NUM_SYNCHRONISERS];
        m_clockBlocks = new ClockBlock[NUM_CLOCKBLOCKS];
        m_threads = new Thread[NUM_THREADS];
        m_coprocs = new Coprocessor[NUM_COPROC];
        
        for(int i=0;i<NUM_TIMERS;i++){
            m_timers[i] = new Timer(i);
        }
        
        for(int i=0;i<NUM_CHANNELS;i++){
            m_channels[i] = new Channel(i, node, core);
        }
        for(int i=0;i<NUM_LOCKS;i++){
            m_locks[i] = new Lock(i);
        }
        
        for(int i=0;i<NUM_PORTS;i++){
            m_ports[i] = new Port(i);
        }
        for(int i=0;i<NUM_SYNCHRONISERS;i++){
            m_synchronisers[i] = new Synchroniser(i);
        }
        for(int i=0;i<NUM_CLOCKBLOCKS;i++){
            m_clockBlocks[i] = new ClockBlock(i);
        }
        for(int i=0;i<NUM_THREADS;i++){
            m_threads[i] = new Thread(i);
        }
        for(int i=0;i<NUM_COPROC;i++){
            m_coprocs[i] = new Coprocessor();
        }
        
    }
    
    public Coprocessor[] getCoprocs() {
		return m_coprocs;
	}

	public Timer[] getTimers() {
		return m_timers;
	}

	public Lock[] getLocks() {
		return m_locks;
	}

	public Port[] getPorts() {
		return m_ports;
	}

	public Synchroniser[] getSynchronisers() {
		return m_synchronisers;
	}

	public Thread[] getThreads() {
		return m_threads;
	}

	public ClockBlock[] getClockBlocks() {
		return m_clockBlocks;
	}

	public Resources(Resources original){
        //copy all the resources
        m_timers = new Timer[NUM_TIMERS];
        m_channels = new Channel[NUM_CHANNELS];
        m_locks = new Lock[NUM_LOCKS];
        m_ports = new Port[NUM_PORTS];
        m_synchronisers = new Synchroniser[NUM_SYNCHRONISERS];
        m_clockBlocks = new ClockBlock[NUM_CLOCKBLOCKS];
        m_threads = new Thread[NUM_THREADS];
        m_coprocs = new Coprocessor[NUM_COPROC];
        
        for(int i=0;i<NUM_TIMERS;i++){
            m_timers[i] = new Timer(original.m_timers[i]);
        }
        for(int i=0;i<NUM_CHANNELS;i++){
            m_channels[i] = new Channel(original.m_channels[i]);
        }
        for(int i=0;i<NUM_LOCKS;i++){
            m_locks[i] = new Lock(original.m_locks[i]);
        }
        for(int i=0;i<NUM_PORTS;i++){
            m_ports[i] = new Port(original.m_ports[i]);
        }
        for(int i=0;i<NUM_SYNCHRONISERS;i++){
            m_synchronisers[i] = new Synchroniser(original.m_synchronisers[i]);
        }
        for(int i=0;i<NUM_CLOCKBLOCKS;i++){
            m_clockBlocks[i] = new ClockBlock(original.m_clockBlocks[i]);
        }
        for(int i=0;i<NUM_THREADS;i++){
            m_threads[i] = new Thread(original.m_threads[i]);
        }
        for(int i=0;i<NUM_COPROC;i++){
            m_coprocs[i] = new Coprocessor(original.m_coprocs[i]);
        }
        
    }
    
    public Channel [] getChannels(){
    	return m_channels;
    }

    
    public int decodeChanNum(int register){
        return ((register>>8)&0xf);
    }
    public int decodeResNum(int register){
        return ((register>>8)&0xf);
    }
    public int decodeResType(int register){
        return (register & 0xf);
    }
    
    public void addThread(){

    }
    
    public int getResID(int resType, int regId, int resNumber, int portWidth){
        int id = resType;
        id |= (regId<<4);
        id |= (resNumber<<8);
        id |=(portWidth<<16);
        return id;
    }
    
    public int makeChannelID(int resType, int regId, int chanNumber, int processor, int node){
        int id = resType;
        id |= (regId<<4);
        id |= (chanNumber<<8);
        id |=(processor<<16);
        id |=(node<<24);
        return id;
    }
    
    public InstructionBuffer getCurrentIBuffer(int thread){
    	return m_threads[thread].getIBuffer();
    }
    
    public Registers getCurrentThreadsRegisters(int thread){
        return m_threads[thread].getRegisters();
    }
    
    public Resource getResource(int resType, int resNum) {
        switch(resType){
        case RES_TYPE_PORT:
            assert(resNum<NUM_PORTS);
            return m_ports[resNum];
        case RES_TYPE_TIMER:
            assert(resNum<NUM_TIMERS);
            return m_timers[resNum];
        case RES_TYPE_CHANEND:
            assert(resNum<NUM_CHANNELS);
            return m_channels[resNum];
        case RES_TYPE_SYNC:
            assert(resNum<NUM_SYNCHRONISERS);
            return m_synchronisers[resNum];
        case RES_TYPE_THREAD:
            assert(resNum<NUM_THREADS);
            return m_threads[resNum];
        case RES_TYPE_LOCK:
            assert(resNum<NUM_LOCKS);
            return m_locks[resNum];
        case RES_TYPE_CLKBLK:
            assert(resNum<NUM_CLOCKBLOCKS);
            return m_clockBlocks[resNum];
        case RES_TYPE_COPROC:
            assert(resNum<NUM_COPROC);
            return m_coprocs[resNum];
        }
        return null;
    }
    public Thread getThread(int resType, int resNum) {
        if(resType != RES_TYPE_THREAD){
        	if(VERBOSE_WARNINGS) System.err.println("Trying to get a new thread but failing");
            return null;
        } else {
        	return (Thread)getResource(resType, resNum);
        }
    }

	public Resource getNewResource(int resType) {
		switch(resType){
        case RES_TYPE_PORT:
            return getNewResource(m_ports);
        case RES_TYPE_TIMER:
            return getNewResource(m_timers);
        case RES_TYPE_CHANEND:
            return getNewResource(m_channels);
        case RES_TYPE_SYNC:
            return getNewResource(m_synchronisers);
        case RES_TYPE_THREAD:
            return getNewResource(m_threads);
        case RES_TYPE_LOCK:
            return getNewResource(m_locks);
        case RES_TYPE_CLKBLK:
            return getNewResource(m_clockBlocks);
        case RES_TYPE_COPROC:
            return getNewResource(m_coprocs);
        }
        return null;
	}
	
	Resource getNewResource(Resource [] resources){

		for(Resource res : resources){
			if(!res.isInUse()){
				res.setInUse(true);
				return res;
			}
		}
		if(VERBOSE_WARNINGS) System.err.println("Trying to get a new resource but failing");
		return null;
		
	}

	public Synchroniser getSynchroniser(int resType, int resNum) {
		if(resType != RES_TYPE_SYNC){
			if(VERBOSE_WARNINGS) System.err.println("Trying to get a new synchroniser but failing");
            return null;
        } else {
        	return (Synchroniser)getResource(resType, resNum);
        }
        
	}

	public Thread getThread(int thread) {
		assert(thread<NUM_THREADS);
		return m_threads[thread];
	}

}
