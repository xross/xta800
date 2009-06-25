package com.xmos.xta.sourcemapper;

import java.util.List;

public class Function implements Line, Comparable<Object> {
	
	private static final boolean debug = false;
	
	private String m_functionName;
    private List<Line> m_assembly;

	public Function(String name, List<Line> functionAssemby) {
	    m_assembly = functionAssemby;
		m_functionName = name;
		
		if(debug){
			System.out.println(name +":");
			for(Line line : m_assembly){
				System.out.println("\t" + line.getText());
			}
		}
		
	}

    public List<Line> getChildern() {
        return m_assembly;
    }

    public String getText() {
        return m_functionName;
    }
    
    public int getLineNumber() {
        return 0;
    }

    public DisplayType getLineNumberType() {
        return DisplayType.none;
    }

    public boolean isExcluded() {
        for (Line childLine : m_assembly){
            if (childLine.isExcluded()) {
            	return true;
            }
        }
        return false;
    }

    public int compareTo(Object o) throws ClassCastException {
        Function f = (Function)o; // If this doesn't work, ClassCastException is thrown
        if (m_functionName.charAt(0) == '_') {
        	if (f.m_functionName.charAt(0) == '_')
        		return m_functionName.compareTo(f.m_functionName);
        	else
        		return 1;
        } else {
        	if (f.m_functionName.charAt(0) == '_')
        		return -1;
        	else
        		return m_functionName.compareTo(f.m_functionName);
        }
      }

    public boolean containsPc(int pc){
    	for(Line line : m_assembly){
    		if(line.getLineNumber() == pc) 
    			return true;
    	}
    	return false;
    }
    
}
