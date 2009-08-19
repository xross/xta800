package com.xmos.xta.engine.analysis.routes;

import com.xmos.xta.engine.CommandSource;
import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.Status;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;

public class RouteTiming {
	public enum Level {
		SIMPLE,
		DETAILED,
		COMPLETE
	}
	
	private Route m_route;
	private Config m_config;
	private double m_maxTime;
	private boolean m_maxTimeSet;
	private int m_nodeId;
	private int m_coreId;
	
	private CommandSource m_requirementsSrc;

	RouteTiming(CommandSource src, Route route, Config config, int nodeId, int coreId) {
		m_requirementsSrc = src;
		m_route = route;
		m_config = config;
		m_nodeId = nodeId;
		m_coreId = coreId;
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

	public void setMaxCycles(CommandSource src, int maxCycles) {
		m_requirementsSrc = src;
		
		int threadFactor = (m_config.m_numThreads > 4) ? m_config.m_numThreads : 4;
		m_maxTime = (double) (threadFactor * 1000 * maxCycles) / (double) m_config.m_frequency;
		m_maxTimeSet = true;
	}

	public int getMaxCycles() {
		int threadFactor = (m_config.m_numThreads > 4) ? m_config.m_numThreads : 4;
		return (int) (m_config.m_frequency * m_maxTime) / (threadFactor * 1000);
	}

	public void setMaxTime(CommandSource src, double maxTime) {
		m_requirementsSrc = src;
		
		m_maxTime = maxTime;
		m_maxTimeSet = true;
	}

	public double getMaxTime() {
		return m_maxTime;
	}

	public void unset() {
		m_maxTime = 0.0;
		m_maxTimeSet = false;
	}

	public CommandSource getCommandSource() {
		return m_requirementsSrc;
	}

	public boolean passedTiming() {
		Status status = new Status();
		double worstTime = getWorstTime(status);
		if (status.isComplete()) {
			if (worstTime <= getMaxTime())
			    return true;
		}
		return false;
	}
	
	public long getViolationCycles(Status status) {
		return getWorstCycles(status) - getMaxCycles();
	}

	public double getViolationTime(Status status) {
		return getWorstTime(status) - getMaxTime();
	}

	public long getSlackCycles(Status status) {
		return getMaxCycles() - getWorstCycles(status);
	}

	public double getSlackTime(Status status) {
		return getMaxTime() - getWorstTime(status);
	}

	public int getMinFrequency(Status status) {
		int threadFactor = (m_config.m_numThreads > 4) ? m_config.m_numThreads : 4;
		return (int)(getWorstCycles(status) * 1000 * threadFactor) / (int)(getMaxTime() + 1.0);
	}
		
	public String getDescription(Level level) {
		String description = "";
		if (isTimingSpecified()) {
			if (level == Level.COMPLETE) {
				description += String.format("Node: %d, Core: %d\n", m_nodeId, m_coreId);

			} else {
				description += "    ";				
			}
			
			if (passedTiming())
				description += "Pass";
			else
				description += "Fail";

			int numWarnings = m_route.getRouteUnknowns().getUnknowns(UnknownState.UNKNOWN).size();
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

				description += String.format("Required: %.1fns, ", getMaxTime());
				
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
		    		    	getMaxTime(), 
		    		    	getMhz(getMaxTime()),
		    			    getMaxCycles());

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
