package com.xmos.xta.engine.resources;

public class ClockBlock extends Resource {

	public ClockBlock(int clockNum) {
    	super(Resources.RES_TYPE_CLKBLK, clockNum);
    }

    public ClockBlock(ClockBlock clockBlock) {
        super(clockBlock);
    }
}
