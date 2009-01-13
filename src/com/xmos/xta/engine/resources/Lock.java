package com.xmos.xta.engine.resources;

public class Lock extends Resource {
	
    public Lock(int lockNum) {
        super(Resources.RES_TYPE_LOCK, lockNum);
    }

    public Lock(Lock lock) {
        super(lock);
    }
}
