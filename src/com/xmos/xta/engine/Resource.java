package com.xmos.xta.engine;

public class Resource {

    public enum ResourceType {
        Timer,
        Channel,
        Thread,
        Synchroniser,
        Port,
        ClockBlock,
        Lock
        
    }
    
    private ResourceType m_resourceType;
    private boolean m_inUse;
    private boolean m_eventEnabeled;
    private int m_owningThread;
    private int m_vector;
    
    public Resource(){
        m_eventEnabeled = true;
    }
    
    public void setEventVector(int vector){
        if(m_resourceType == ResourceType.Port ||
                m_resourceType == ResourceType.Channel ||
                m_resourceType == ResourceType.Timer){
            m_vector = vector;
        } else {
            System.err.println("Trying to set a resources vector illegaly");
        }
    }
    
    public int getVector(){
        if(m_resourceType == ResourceType.Port ||
                m_resourceType == ResourceType.Channel ||
                m_resourceType == ResourceType.Timer){
            return m_vector;
        } else {
            System.err.println("Trying to ");
            return 0;
        }
        
    }
    
    public void setResourceEnabled(boolean enabeled){
        m_eventEnabeled = enabeled;
    }
    
    public void setResourceInUse(boolean inUse){
        m_inUse = inUse;
    }
    
    public void setOwningThread(int owningThread){
        m_owningThread = owningThread;
    }
    
    public int getOwningThread(){
        return m_owningThread;
    }
    
    public boolean getResourceInUse(){
        return m_inUse;
    }
    
    public boolean getEventEnabeled(){
        return m_eventEnabeled;
    }
    
    public ResourceType getResourceType(){
        return m_resourceType;
    }
    
}
