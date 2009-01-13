package com.xmos.xta.engine.resources;

public class Timer extends EventableResource {

    public Timer(int timerNo) {
    	super(Resources.RES_TYPE_TIMER, timerNo);
    }
    
    public Timer(Timer timer) {
    	super(timer);
    }
}
