package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.List;

import com.xmos.libs.util.Conversion;
import com.xmos.libs.util.CoreId;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;
import com.xmos.xta.core.engine.utils.CoreConfig;
import com.xmos.xta.core.engine.utils.TimeUnit;
import com.xmos.xta.core.engine.utils.TimeUnit.Units;

public class RouteTiming {
	public enum Level {
		REQUIRED,
		SIMPLE,
		DETAILED,
		COMPLETE
	}

	private Route m_route;
	private CoreConfig m_coreConfig;
	private CoreId m_coreId;
	private TimeUnit m_maxTime;
	private boolean m_maxTimeSet;
	private CommandSource m_requirementsSrc;

	RouteTiming(CommandSource src, Route route, CoreConfig coreConfig, CoreId coreId) {
		m_requirementsSrc = src;
		m_route = route;
		m_coreConfig = coreConfig;
		m_coreId = coreId;
		m_maxTime = new TimeUnit(m_coreConfig, 0.0, Units.NS);
		m_maxTimeSet = false;
	}

	public boolean isTimingSpecified() {
		return m_maxTimeSet;
	}

	public void setMaxTime(CommandSource src, TimeUnit maxTime) {
		m_requirementsSrc = src;
		m_maxTime = new TimeUnit(m_coreConfig, maxTime);
		m_maxTimeSet = true;
	}

	public TimeUnit getMaxTime() {
		return m_maxTime;
	}

	public CommandSource getCommandSource() {
		return m_requirementsSrc;
	}

	public boolean passed() {
		for (Unknown unknown : m_route.getRouteUnknowns().getUnknowns(State.UNKNOWN)) {
			if (unknown.getLevel() == Unknown.Level.ERROR) {
				return false;
			}
		}
		return passedTiming();
	}

	public List<String> getWarnings() {
		return getStrings(Unknown.Level.WARNING);
	}

	public List<String> getErrors() {
		return getStrings(Unknown.Level.ERROR);
	}
	
	public long getViolationCycles(Status status) {
		return getCycles(status, Case.WORST) - m_maxTime.getCycles();
	}

	public double getViolationTime(Status status) {
		return getTime(status, Case.WORST) - m_maxTime.getNs();
	}

	public long getSlackCycles(Status status) {
		return m_maxTime.getCycles() - getCycles(status, Case.WORST);
	}

	public double getSlackTime(Status status) {
		return m_maxTime.getNs() - getTime(status, Case.WORST);
	}

	public int getMinFrequency(Status status) {
		return (int)((getTime(status, Case.WORST) / m_maxTime.getNs()) * m_coreConfig.m_frequency);
	}

	public Status getWorstCaseTimeStatus() {
		Status status = new Status();
		getTime(status, Case.WORST);
		return status;
	}

	public String getDescription(Level level) {
		String description = "";
		if (isTimingSpecified()) {
			if (level == Level.REQUIRED) {
				return m_maxTime.getDescription(true);
			}

			if (level == Level.COMPLETE) {
				description += String.format("%s (node: %d, core: %d)\n",
						m_coreId.getCoreReference(), 
						m_coreId.getNodeChainIndex(), 
						m_coreId.getCoreNumber());				
			} else {
				description += "    ";
			}

			if (passed()) {
				description += "Pass";
			} else {
				description += "Fail";
			}
			
			int numWarnings = m_route.getRouteUnknowns().getUnknowns(State.UNKNOWN).size();
			if (numWarnings > 0)
				description += " with " + numWarnings + " unknown";
			if (numWarnings > 1)
				description += "s";

			description += ", Num Paths: " + m_route.getNode().getAttributes().getNumPathsStr();

			if (level == Level.DETAILED) {
				description += ", ";

				if (passedTiming()) {
					Status status = new Status();
					double slackTime = getSlackTime(status);
					assert(status.isComplete());
				    description += String.format("Slack: %s, ",
				    		Conversion.getTimeString(slackTime));

				} else {
					Status status = new Status();
					double violationTime = getViolationTime(status);
			    	if (status.isExceptionalCase()) {
			    		description += String.format("Violation: %s, ", status.getString());

			    	} else {
					    description += String.format("Violation: %s, ",
					    		Conversion.getTimeString(violationTime));
			    	}
				}

				description += String.format("Required: %s, ",
						Conversion.getTimeString(m_maxTime.getNs()));

				Status worstTimeStatus = new Status();
				double worstTime = getTime(worstTimeStatus, Case.WORST);
		    	if (worstTimeStatus.isExceptionalCase()) {
		    		description += String.format("Worst: %s, ", worstTimeStatus.getString());

		    	} else {
				    description += String.format("Worst: %s, ",
				    		Conversion.getTimeString(worstTime));
		    	}

				Status minFrequencyStatus = new Status();
				int minFrequency = getMinFrequency(minFrequencyStatus);
		    	if (minFrequencyStatus.isExceptionalCase()) {
		    		description += String.format("Min Core Frequency: %s\n", minFrequencyStatus.getString());

		    	} else {
				    description += String.format("Min Core Frequency: %d MHz\n", minFrequency);
				}

			} else if (level == Level.COMPLETE) {
				description += "\n";

		    	Status status = new Status();
		    	double worstTime = getTime(status, Case.WORST);

		    	if (status.isExceptionalCase()) {
		    		description += String.format("Worst Case:  %s\n", status.getString());

		    	} else {
		    		description += String.format("Worst Case Timing:  %8s,%8.2f MHz,%8d thread cycles\n",
		    				Conversion.getTimeString(worstTime),
		    				getMhz(worstTime),
		    				getCycles(new Status(), Case.WORST));
		    	}

				if (isTimingSpecified()) {
					description += String.format("Required:    %8s,%8.2f MHz,%8d thread cycles\n",
							Conversion.getTimeString(m_maxTime.getNs()),
							m_maxTime.getMhz(),
							m_maxTime.getCycles());

					if (passedTiming()) {
						assert(status.isComplete());
		                double slackTime = getSlackTime(new Status());
		                description += String.format("Slack:      %8s,%8.2f MHz,%8d thread cycles\n",
		                		Conversion.getTimeString(slackTime),
		                		getMhz(slackTime),
		                		getSlackCycles(new Status()));
		    	    } else {
				    	if (status.isInfinite() || status.isUnresolved()) {
				    		description += String.format("Violation:  %s\n", status.getString());

				    	} else {
			                double violationTime = getViolationTime(new Status());
			                description += String.format("Failed, Violation:  %8s,%8.2f MHz,%8d thread cycles\n",
			                		Conversion.getTimeString(violationTime),
			                		getMhz(violationTime),
			                		getViolationCycles(new Status()));
				    	}
		    	    }

					if (status.isComplete())
					    description += String.format("Min Core Frequency: %8d MHz\n", getMinFrequency(new Status()));

				} else {
					description += "No Timing Specified\n";
				}
			}
		} else {
			if (level == Level.COMPLETE) {
				description += String.format("%s (node: %d, core: %d)\n",
						m_coreId.getCoreReference(), 
						m_coreId.getNodeChainIndex(), 
						m_coreId.getCoreNumber());				
			} else {
				description += "    ";
			}
			description += "No Timing Specified\n";
		}
		return description;
	}

	public long getCycles(Status status, Case c) {
		Node worstCaseSubRoute = m_route.getNode();
		return worstCaseSubRoute.getAttributes().getNumCycles(c, status, null);
	}

	public double getTime(Status status, Case c) {
		Node worstCaseSubRoute = m_route.getNode();
		return worstCaseSubRoute.getAttributes().getTimeTaken(c, status, null);
	}
	
	private boolean passedTiming() {
		Status status = new Status();
		double worstTime = getTime(status, Case.WORST);
		if (status.isComplete()) {
			if ((worstTime <= m_maxTime.getNs()) && (worstTime != Double.POSITIVE_INFINITY))
			    return true;
		}
		return false;
	}

    private double getMhz(double ns) {
    	return (1.0 * 1000) / ns;
    }
    
    private List<String> getStrings(Unknown.Level level) {
    	List<String> strings = new ArrayList<String>();		
    	for (Unknown unknown : m_route.getRouteUnknowns().getUnknowns(State.UNKNOWN)) {
    		if (unknown.getLevel() == level) {
    			strings.add(unknown.getDescription());
    		}
    	}
    	return strings;        	
    }
}
