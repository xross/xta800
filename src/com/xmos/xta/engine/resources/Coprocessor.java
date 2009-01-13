package com.xmos.xta.engine.resources;

public class Coprocessor extends Resource {
	
    public Coprocessor() {
        super(Resources.RES_TYPE_COPROC, 0);
    }
    
    public Coprocessor(Coprocessor coprocessor) {
    	super(coprocessor);
    }
}
