package com.xmos.xta.core.engine;

import com.xmos.libs.util.Conversion;

public class TimeUnit {
	public enum Units {
		NS,
		MHZ,
		CYCLES,
		INVALID,
	}

	private Config m_config;
	private Units m_units;
	private long m_cycles;
	private double m_ns;
	private double m_mhz;

	public static String getHelp() {
		String helpStr = "";
   		helpStr += "          mode: ns:     nanoseconds\n";
   		helpStr += "          mode: mhz:    megahertz\n";
   		helpStr += "          mode: cycles: processor thread cycles";
   		return helpStr;
	}
	
	public static String[] getUnits() {
		return new String[] {"ns", "us", "ms", "mhz", "cycles"};
	}
	
	public TimeUnit(String value, String units) {
		try {
		   	if (units.equals("ns")) {
		   		m_ns = Double.parseDouble(value);
		   		m_units = Units.NS;
	   	   		    
	   	   	} else if (units.equals("us")) {
	   	   		m_ns = Double.parseDouble(value) * 1000.0;
		   		m_units = Units.NS;
	   	    		    
	   	   	} else if (units.equals("ms")) {
	   	   		m_ns = Double.parseDouble(value) * 1000000.0;
		   		m_units = Units.NS;
	   	    		    
	   	   	} else if (units.equals("mhz")) {
	   	   	    m_mhz = Double.parseDouble(value);
		   		m_units = Units.MHZ;
	   	    		    
	   	   	} else if (units.equals("cycles")) {
	   	   	    m_cycles = Long.parseLong(value);
		   		m_units = Units.CYCLES;
		   		
	   	   	} else {
				m_units = Units.INVALID;	   	   		
	   	   	}
			
		} catch (NumberFormatException e) {
			m_units = Units.INVALID;
		}    					
		m_config = null;
	}
	
	public TimeUnit(Config config, long value, Units units) {
		assert(units == Units.CYCLES);
   	    m_cycles = value;
   		m_units = Units.CYCLES;		
		m_config = config;
	}

	public TimeUnit(Config config, double value, Units units) {		
		assert((units == Units.NS) || (units == Units.MHZ));
		m_units = units;
		switch (m_units) {
		case NS:
			m_ns = value;
			break;
		case MHZ:
			m_mhz = value;
			break;
		}
		m_config = config;
	}
		
	public TimeUnit(Config config, TimeUnit timeUnit) {
		m_config = config;
		m_units = timeUnit.m_units;
		m_cycles = timeUnit.m_cycles;
		m_ns = timeUnit.m_ns;
		m_mhz = timeUnit.m_mhz;
	}
	
	public void setConfig(Config config) {
		m_config = config;
	}
	
	public boolean isValid() {
		return m_units != Units.INVALID;
	}

	public String getDescription(boolean precise) {
       	switch (m_units) {
       	case NS:
       		return Conversion.getTimeString(precise, m_ns);
       	case MHZ:
           	return precise ? 
           		    String.format("%f mhz", m_mhz) : 
           		    String.format("%.1f mhz", m_mhz);
       	case CYCLES:
           	return String.format("%d cycles", m_cycles);
       	}
       	assert(false);
       	return "";
	}

	public String getValueStr() {
       	switch (m_units) {
       	case NS:     return Conversion.getValueString(false, m_ns); 
       	case MHZ:    return String.format("%.1f", m_mhz); 
       	case CYCLES: return String.format("%d", m_cycles); 
       	}
       	assert(false);
       	return "";	
	}

	public String getUnitsStr() {
       	switch (m_units) {
       	case NS:     return Conversion.getUnitsString(m_ns); 
       	case MHZ:    return "mhz"; 
       	case CYCLES: return "cycles"; 
       	}
       	assert(false);
       	return "";		
	}
	
	public long getCycles() {
		assert(m_config != null);
		
       	switch (m_units) {
       	case NS: {
       		int threadFactor = (m_config.m_numThreads > 4) ? m_config.m_numThreads : 4;
           	return (long)((m_ns * m_config.m_frequency) / (threadFactor * 1000));
       	}
       	case MHZ: {
       		int threadFactor = (m_config.m_numThreads > 4) ? m_config.m_numThreads : 4;
           	return (long)(m_config.m_frequency / (threadFactor * m_mhz));
       	}
       	case CYCLES:
           	return m_cycles;           	    	
       	}
       	assert(false);
       	return 0;				
	}

	public double getNs() {
		assert(m_config != null);
		
       	switch (m_units) {
       	case NS:
           	return m_ns;
           	
       	case MHZ:
       		return (double)1000.0 / m_mhz;
       		
       	case CYCLES:
       		int threadFactor = (m_config.m_numThreads > 4) ? m_config.m_numThreads : 4;
       	    return ((double)(threadFactor * 1000) / m_config.m_frequency) * m_cycles;           	    		
       	}
       	assert(false);
       	return 0;				
	}

	public double getMhz() {
		assert(m_config != null);
		
       	switch (m_units) {
       	case NS:
       		return (double)1000.0 / m_ns;
    
       	case MHZ:
           	return m_mhz;
           	
       	case CYCLES:       		
       	  	int threadFactor = (m_config.m_numThreads > 4) ? m_config.m_numThreads : 4;
            return m_config.m_frequency / (double)(threadFactor * m_cycles);
       	}
       	assert(false);
       	return 0;				
	}
}
