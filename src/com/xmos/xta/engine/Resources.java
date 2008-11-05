package com.xmos.xta.engine;

import java.util.ArrayList;

public class Resources {
    final static int NUM_RESOURCES = 16;    
    private Resource[] m_resource;
    
    public Resources(){
        m_resource = new Resource[NUM_RESOURCES];
        for (int resIndex = 0; resIndex < NUM_RESOURCES; ++resIndex) {
            m_resource[resIndex] = new Resource();
        }
    }
    
    public int readResource(int i){
        return 0;
    }
    
    public ArrayList<Integer> getResourceVectors(){
        ArrayList<Integer> vector = new ArrayList<Integer>();
        return vector;
    }
    
    public void setResourceInUse(int resource){
        
    }
    
    public void setEventEnabeled(int resource){
        
    }
    
    public void setResourceOwner(int threadId){
        
    }
    
    
    
}
