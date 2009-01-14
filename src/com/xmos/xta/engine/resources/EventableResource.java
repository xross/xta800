package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;
import com.xmos.xta.engine.InstructionExecption;
import com.xmos.xta.engine.InstructionExecption.Error;

public class EventableResource extends Resource {
    private boolean m_eventEnabledKnown;
    private boolean m_eventEnabled;
    
    private Expression m_vector;
    private Expression m_environmentVector;
    
    public EventableResource(int resourceType, int resourceNum) {
    	super(resourceType, resourceNum);
    }
    
    public EventableResource(EventableResource res) {
    	super(res);

    	m_eventEnabledKnown = res.m_eventEnabledKnown;
        m_eventEnabled= res.m_eventEnabled;
        if (res.m_vector!= null) m_vector= new Expression(res.m_vector);
        if (res.m_environmentVector!= null)  m_environmentVector= new Expression(res.m_environmentVector);
    }
    
	public void setEventEnabeled(boolean b) {
		m_eventEnabled = b;
	}
	
	public boolean isEventEnabeled() {
		return m_eventEnabled;
	}
	
	public Expression getEnvironmentVector() {
		if(m_environmentVector == null){
			return new Expression(0, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
		} else {
			return m_environmentVector;
		}
	}
	
	public void setEnvironmentVector(Expression vector) {
		m_environmentVector = vector;
	}

	public Expression getVector() {
			if(m_vector == null){
				return new Expression(0, 0, Expression.Type.REGISTER, Expression.Status.UNDEFINED);
			} else {
				return m_vector;
			}
	}
	
	public void setVector(Expression vector) {
		m_vector = vector;
	}
}
