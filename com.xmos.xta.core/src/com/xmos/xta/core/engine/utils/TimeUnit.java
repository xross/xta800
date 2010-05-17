package com.xmos.xta.core.engine.utils;

import com.xmos.libs.util.Conversion;

public class TimeUnit {
	public enum Units {
		NS,
		MHZ,
		CYCLES,
		INVALID,
	}
	
	private CoreConfig m_coreConfig;
	private Units m_units;
	private long m_cycles;
	private double m_ns;
	private double m_mhz;

	public static String getHelp() {
		String helpStr = "";
   		helpStr += "          mode: ns:     nanoseconds\n";
   		helpStr += "          mode: us:     microseconds\n";
   		helpStr += "          mode: ms:     milliseconds\n";
   		helpStr += "          mode: MHz:    megahertz\n";
   		helpStr += "          mode: cycles: processor thread cycles";
   		return helpStr;
	}

	public static String[] getUnits() {
		return new String[] {
				"ns", 
				"us", 
				"ms", 
				"MHz", 
				"cycles"};
	}

	public static String[] getUnitsDesc() {
		return new String[] {
				"nanoseconds", 
				"microseconds", 
				"milliseconds", 
				"megahertz",
				
				"the thread cycle count is the number of scheduled slots " +
				"that the thread required to perform the sequence. The relationship " + 
				"between thread cycles and time is a function of the number of " +
				"threads currently running and the core frequency"};
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

	   	   	} else if (units.equals("MHz") || units.equals("mhz")) {
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
		m_coreConfig = null;
	}

	public TimeUnit(double value, String units) throws Exception {
	   	if (units.equals("ns")) {
	   		m_ns = value;
	   		m_units = Units.NS;

   	   	} else if (units.equals("us")) {
   	   		m_ns = value * 1000.0;
	   		m_units = Units.NS;

   	   	} else if (units.equals("ms")) {
   	   		m_ns = value * 1000000.0;
	   		m_units = Units.NS;

   	   	} else if (units.equals("MHz") || units.equals("mhz")) {
   	   	    m_mhz = value;
	   		m_units = Units.MHZ;

   	   	} else {
			throw new Exception("Invalid units");   	   		
   	   	}
		m_coreConfig = null;
	}
	
	public TimeUnit(CoreConfig coreConfig, long value, Units units) {
		assert(units == Units.CYCLES);
   	    m_cycles = value;
   		m_units = Units.CYCLES;
   		m_coreConfig = coreConfig;
	}

	public TimeUnit(CoreConfig coreConfig, double value, Units units) {
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
		m_coreConfig = coreConfig;
	}

	public TimeUnit(CoreConfig coreConfig, TimeUnit timeUnit) {
		m_coreConfig = coreConfig;
		m_units = timeUnit.m_units;
		m_cycles = timeUnit.m_cycles;
		m_ns = timeUnit.m_ns;
		m_mhz = timeUnit.m_mhz;
	}

	public void setCoreConfig(CoreConfig coreConfig) {
		m_coreConfig = coreConfig;
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
           			Conversion.stripTrailingZeros(String.format("%f", m_mhz)) + " MHz" :
           		    String.format("%.1f MHz", m_mhz);
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
       	case MHZ:    return "MHz";
       	case CYCLES: return "cycles";
       	}
       	assert(false);
       	return "";
	}
 
	public long getCycles() {
		assert(m_coreConfig != null);

       	switch (m_units) {
       	case NS: {
       		int threadFactor = (m_coreConfig.m_numThreads > 4) ? m_coreConfig.m_numThreads : 4;
           	return (long)((m_ns * m_coreConfig.m_frequency) / (threadFactor * 1000));
       	}
       	case MHZ: {
       		int threadFactor = (m_coreConfig.m_numThreads > 4) ? m_coreConfig.m_numThreads : 4;
           	return (long)(m_coreConfig.m_frequency / (threadFactor * m_mhz));
       	}
       	case CYCLES:
           	return m_cycles;
       	}
       	assert(false);
       	return 0;
	}

	public double getNs() {
       	switch (m_units) {
       	case NS:
           	return m_ns;

       	case MHZ:
       		return 1000.0 / m_mhz;

       	case CYCLES:
    		assert(m_coreConfig != null);
       		int threadFactor = (m_coreConfig.m_numThreads > 4) ? m_coreConfig.m_numThreads : 4;
       	    return ((threadFactor * 1000) / m_coreConfig.m_frequency) * m_cycles;
       	}
       	assert(false);
       	return 0;
	}

	public double getMhz() {
       	switch (m_units) {
       	case NS:
       		return 1000.0 / m_ns;

       	case MHZ:
           	return m_mhz;

       	case CYCLES:
    		assert(m_coreConfig != null);
       	  	int threadFactor = (m_coreConfig.m_numThreads > 4) ? m_coreConfig.m_numThreads : 4;
            return m_coreConfig.m_frequency / (threadFactor * m_cycles);
       	}
       	assert(false);
       	return 0;
	}
	
	public double getValue(String units) throws Exception {
	   	if (units.equals("ns")) {
	   		return getNs();

   	   	} else if (units.equals("us")) {
	   		return getNs() / 1000.0;

   	   	} else if (units.equals("ms")) {
	   		return getNs() / 1000000.0;

   	   	} else if (units.equals("MHz") || units.equals("mhz")) {
	   		return getMhz();

   	   	} else {
			throw new Exception("Invalid units");
   	   	}		
	}
}
