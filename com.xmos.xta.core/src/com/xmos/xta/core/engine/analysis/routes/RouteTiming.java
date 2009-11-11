package com.xmos.xta.core.engine.analysis.routes;

import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.TimeUnit.Units;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.core.engine.analysis.structural.nodes.attributes.Status;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.State;

public class RouteTiming {
	public enum Level {
		REQUIRED,
		SIMPLE,
		DETAILED,
		COMPLETE
	}
	
	private Route m_route;
	private Config m_config;
	private int m_nodeId;
	private int m_coreId;
	private TimeUnit m_maxTime;
	private boolean m_maxTimeSet;
	
	private CommandSource m_requirementsSrc;

	RouteTiming(CommandSource src, Route route, Config config, int nodeId, int coreId) {
		m_requirementsSrc = src;
		m_route = route;
		m_config = config;
		m_nodeId = nodeId;
		m_coreId = coreId;
		m_maxTime = new TimeUnit(m_config, 0.0, Units.NS);
		m_maxTimeSet = false;
	}

	public int getNodeId() {
		return m_nodeId;
	}

	public int getCoreId() {
		return m_coreId;
	}

	public boolean isTimingSpecified() {
		return m_maxTimeSet;
	}

	public void setMaxTime(CommandSource src, TimeUnit maxTime) {
		m_requirementsSrc = src;
		m_maxTime = maxTime;
		m_maxTimeSet = true;
		m_maxTime.setConfig(m_config);
	}

	public TimeUnit getMaxTime() {
		return m_maxTime;
	}
	
	public CommandSource getCommandSource() {
		return m_requirementsSrc;
	}

	public boolean passedTiming() {
		Status status = new Status();
		double worstTime = getWorstTime(status);
		if (status.isComplete()) {
			if (worstTime <= m_maxTime.getNs())
			    return true;
		}
		return false;
	}
	
	public long getViolationCycles(Status status) {
		return getWorstCycles(status) - m_maxTime.getCycles();
	}

	public double getViolationTime(Status status) {
		return getWorstTime(status) - m_maxTime.getNs();
	}

	public long getSlackCycles(Status status) {
		return m_maxTime.getCycles() - getWorstCycles(status);
	}

	public double getSlackTime(Status status) {
		return m_maxTime.getNs() - getWorstTime(status);
	}

	public int getMinFrequency(Status status) {
		return (int)((getWorstTime(status) / m_maxTime.getNs()) * (double)m_config.m_frequencyHz / 1000000);
	}
		
	public String getDescription(Level level) {
		String description = "";
		if (isTimingSpecified()) {			
			if (level == Level.REQUIRED) {
				return m_maxTime.getDescription(true);
			}
			
			if (level == Level.COMPLETE) {
				description += String.format("Node: %d, Core: %d\n", m_nodeId, m_coreId);

			} else {
				description += "    ";				
			}
			
			if (passedTiming())
				description += "Pass";
			else
				description += "Fail";

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
				    description += String.format("Slack: %.1fns, ", slackTime);						
					
				} else {
					Status status = new Status();
					double violationTime = getViolationTime(status);
			    	if (status.isInfinite()) {
			    		description += "Violation: Infinity, ";
			    		
			    	} else if (status.isUnresolved()) {
			    		description += "Violation: Unresolved, ";
			    		
			    	} else {
					    description += String.format("Violation: %.1fns, ", violationTime);
			    	}
				}

				description += String.format("Required: %.1fns, ", m_maxTime.getNs());
				
				Status worstTimeStatus = new Status();
				double worstTime = getWorstTime(worstTimeStatus);
		    	if (worstTimeStatus.isInfinite()) {
		    		description += "Worst: Infinity, ";
		    		
		    	} else if (worstTimeStatus.isUnresolved()) {
		    		description += "Worst: Unresolved, ";
		    		
		    	} else {
				    description += String.format("Worst: %.1fns, ", worstTime);
		    	}

				Status minFrequencyStatus = new Status();
				int minFrequency = getMinFrequency(minFrequencyStatus);
		    	if (minFrequencyStatus.isInfinite()) {
		    		description += "Min Frequency: Infinity\n";
		    		
		    	} else if (minFrequencyStatus.isUnresolved()) {
		    		description += "Min Frequency: Unresolved\n";

		    	} else {
				    description += String.format("Min Frequency: %d Mhz\n", minFrequency);
				}
				
			} else if (level == Level.COMPLETE) {
				description += "\n";
				
		    	Status status = new Status();
		    	double worstTime = getWorstTime(status);
		    	
		    	if (status.isInfinite()) {
		    		description += "Worst Case:  Infinity\n";
		    		
		    	} else if (status.isUnresolved()) {
		    		description += "Worst Case:  Unresolved\n";
		    		
		    	} else {
		    		description += String.format("Worst Case Timing:  %8.1f ns,%8.2f mhz,%8d thread cycles\n", 
		        			worstTime, getMhz(worstTime), getWorstCycles(new Status()));    	    		
		    	}
		    	
				if (isTimingSpecified()) {  			
					description += String.format("Required:    %8.1f ns,%8.2f mhz,%8d thread cycles\n", 
							m_maxTime.getNs(), 
							m_maxTime.getMhz(),
							m_maxTime.getCycles());

					if (passedTiming()) {
						assert(status.isComplete());						
		                double slackTime = getSlackTime(new Status());
		                description += String.format("Slack:      %8.1f ns,%8.2f mhz,%8d thread cycles\n", 
		                		slackTime, getMhz(slackTime), getSlackCycles(new Status()));
		    	    } else {
				    	if (status.isInfinite()) {
				    		description += "Violation:  Infinity\n";
				    		
				    	} else if (status.isUnresolved()) {
				    		description += "Violation:  Unresolved\n";
				    		
				    	} else {
			                double violationTime = getViolationTime(new Status());
			                description += String.format("Failed, Violation:  %8.1f ns,%8.2f mhz,%8d thread cycles\n", 
			                		violationTime, getMhz(violationTime), getViolationCycles(new Status()));    						    		
				    	}
		    	    }
					
					if (status.isComplete())
					    description += String.format("Min Core Frequency: %8d mhz\n", getMinFrequency(new Status()));
					
				} else {
					description += "No Timing Specified\n";			
				}				
			}			
		} else {
			description += "    No Timing Specified\n";
		}
		return description;
	}
	    
	private long getWorstCycles(Status status) {
		Node worstCaseSubRoute = m_route.getNode();
		return worstCaseSubRoute.getAttributes().getNumCycles(Case.WORST, status, null);
	}

	private double getWorstTime(Status status) {
		Node worstCaseSubRoute = m_route.getNode();
		return worstCaseSubRoute.getAttributes().getTimeTaken(Case.WORST, status, null);
	}
	
    private double getMhz(double ns) {
    	return (1.0 * 1000) / ns; 
    }	
}
