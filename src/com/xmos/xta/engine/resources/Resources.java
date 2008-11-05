package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.XtaInstruction;

public class Resources {

    private static final int RES_TYPE_PORT = 0;
    private static final int RES_TYPE_TIMER  = 1;
    private static final int RES_TYPE_CHANEND  = 2;
    private static final int RES_TYPE_SYNC = 3;
    private static final int RES_TYPE_THREAD  = 4;
    private static final int RES_TYPE_LOCK = 5;
    private static final int RES_TYPE_CLKBLK = 6;
    private static final int RES_TYPE_COPROC = 7;

    private static final int NUM_TIMERS = 10;
    private static final int NUM_CHANNELS = 32;
    private static final int NUM_LOCKS = 10;
    private static final int NUM_PORTS = 32;
    private static final int NUM_SYNCHRONISERS = 10;
    private static final int NUM_CLOCKBLOCKS = 10;
    private static final int NUM_THREADS = 10;
    private static final int NUM_COPROC = 10;
    
    private Coprocessor[] m_coprocs;
    private Timer[] m_timers;
    private Channel[] m_channels;
    private Lock[] m_locks;
    private Port[] m_ports;
    private Synchroniser[] m_synchronisers;
    private Thread[] m_threads;
    private ClockBlock[] m_clockBlocks;

    public Resources(){
        //set up all the resources
        m_timers = new Timer[NUM_TIMERS];
        m_channels = new Channel[NUM_CHANNELS];
        m_locks = new Lock[NUM_LOCKS];
        m_ports = new Port[NUM_PORTS];
        m_synchronisers = new Synchroniser[NUM_SYNCHRONISERS];
        m_clockBlocks = new ClockBlock[NUM_CLOCKBLOCKS];
        m_threads = new Thread[NUM_THREADS];
        m_coprocs = new Coprocessor[NUM_COPROC];
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
    
    private int makeResID(int resType, int regId, int resNumber, int portWidth){
        int id = resType;
        id |= (regId<<4);
        id|= (resNumber<<8);
        id|=(portWidth<<16);
        return id;
    }
    
    private int makeChannelID(int resType, int regId, int chanNumber, int processor, int node){
        int id = resType;
        id |= (regId<<4);
        id|= (chanNumber<<8);
        id|=(processor<<16);
        id|=(node<<24);
        return id;
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


}
