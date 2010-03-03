package com.xmos.xta.core.engine.analysis.routes;

import com.xmos.libs.util.Conversion;
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
	private int m_nodeNumber;
	private int m_coreNumber;
	private TimeUnit m_maxTime;
	private boolean m_maxTimeSet;
	private CommandSource m_requirementsSrc;

	RouteTiming(CommandSource src, Route route, CoreConfig coreConfig, int nodeNumber, int coreNumber) {
		m_requirementsSrc = src;
		m_route = route;
		m_coreConfig = coreConfig;
		m_nodeNumber = nodeNumber;
		m_coreNumber = coreNumber;
		m_maxTime = new TimeUnit(m_coreConfig, 0.0, Units.NS);
		m_maxTimeSet = false;
	}

	public int getNodeNumber() {
		return m_nodeNumber;
	}

	public int getCoreNumber() {
		return m_coreNumber;
	}

	public boolean isTimingSpecified() {
		return m_maxTimeSet;
	}

	public void setMaxTime(CommandSource src, TimeUnit maxTime) {
		m_requirementsSrc = src;
		m_maxTime = maxTime;
		m_maxTimeSet = true;
		m_maxTime.setCoreConfig(m_coreConfig);
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
		return (int)((getWorstTime(status) / m_maxTime.getNs()) * m_coreConfig.m_frequency);
	}

	public String getDescription(Level level) {
		String description = "";
		if (isTimingSpecified()) {
			if (level == Level.REQUIRED) {
				return m_maxTime.getDescription(true);
			}

			if (level == Level.COMPLETE) {
				description += String.format("Node: %d, Core: %d\n", m_nodeNumber, m_coreNumber);
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
			    	if (status.isInfinite() || status.isUnresolved()) {
			    		description += String.format("Violation: %s, ", status.getString());

			    	} else {
					    description += String.format("Violation: %s, ",
					    		Conversion.getTimeString(violationTime));
			    	}
				}

				description += String.format("Required: %s, ",
						Conversion.getTimeString(m_maxTime.getNs()));

				Status worstTimeStatus = new Status();
				double worstTime = getWorstTime(worstTimeStatus);
		    	if (worstTimeStatus.isInfinite() || worstTimeStatus.isUnresolved()) {
		    		description += String.format("Worst: %s, ", worstTimeStatus.getString());

		    	} else {
				    description += String.format("Worst: %s, ",
				    		Conversion.getTimeString(worstTime));
		    	}

				Status minFrequencyStatus = new Status();
				int minFrequency = getMinFrequency(minFrequencyStatus);
		    	if (minFrequencyStatus.isInfinite() || minFrequencyStatus.isUnresolved()) {
		    		description += String.format("Min Core Frequency: %s\n", minFrequencyStatus.getString());

		    	} else {
				    description += String.format("Min Core Frequency: %d mhz\n", minFrequency);
				}

			} else if (level == Level.COMPLETE) {
				description += "\n";

		    	Status status = new Status();
		    	double worstTime = getWorstTime(status);

		    	if (status.isInfinite() || status.isUnresolved()) {
		    		description += String.format("Worst Case:  %s\n", status.getString());

		    	} else {
		    		description += String.format("Worst Case Timing:  %8s,%8.2f mhz,%8d thread cycles\n",
		    				Conversion.getTimeString(worstTime),
		    				getMhz(worstTime),
		    				getWorstCycles(new Status()));
		    	}

				if (isTimingSpecified()) {
					description += String.format("Required:    %8s,%8.2f mhz,%8d thread cycles\n",
							Conversion.getTimeString(m_maxTime.getNs()),
							m_maxTime.getMhz(),
							m_maxTime.getCycles());

					if (passedTiming()) {
						assert(status.isComplete());
		                double slackTime = getSlackTime(new Status());
		                description += String.format("Slack:      %8s,%8.2f mhz,%8d thread cycles\n",
		                		Conversion.getTimeString(slackTime),
		                		getMhz(slackTime),
		                		getSlackCycles(new Status()));
		    	    } else {
				    	if (status.isInfinite() || status.isUnresolved()) {
				    		description += String.format("Violation:  %s\n", status.getString());

				    	} else {
			                double violationTime = getViolationTime(new Status());
			                description += String.format("Failed, Violation:  %8s,%8.2f mhz,%8d thread cycles\n",
			                		Conversion.getTimeString(violationTime),
			                		getMhz(violationTime),
			                		getViolationCycles(new Status()));
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

	private boolean passedTiming() {
		Status status = new Status();
		double worstTime = getWorstTime(status);
		if (status.isComplete()) {
			if ((worstTime <= m_maxTime.getNs()) && (worstTime != Double.POSITIVE_INFINITY))
			    return true;
		}
		return false;
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
