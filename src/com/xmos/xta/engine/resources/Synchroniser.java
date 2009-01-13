package com.xmos.xta.engine.resources;

public class Synchroniser extends Resource {

    
    public Synchroniser(int syncNum) {
    	super(Resources.RES_TYPE_SYNC, syncNum);
    	
    }
    
    public Synchroniser(Synchroniser sync) {
       super(sync);
    }
    
    //need to handle
    //getst
    //msync
    //mjoin
    
    public void attachResource(Resource res){
    	
    }
    
}
