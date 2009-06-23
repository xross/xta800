package com.xmos.xta.engine.analysis.routes;

import com.xmos.xta.engine.Config;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.NodeAttributes.UnknownState;

public class RouteTiming {
	private Route m_route;
	private Config m_config;
	private double m_maxTime;
	private boolean m_maxTimeSet;
	private int m_nodeId;
	private int m_coreId;

	RouteTiming(Route route, Config config, int nodeId, int coreId) {
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

	public void setMaxCycles(int maxCycles) {
		int threadFactor = (m_config.m_numThreads > 4) ? m_config.m_numThreads : 4;
		m_maxTime = (double) (threadFactor * 1000 * maxCycles) / (double) m_config.m_frequency;
		m_maxTimeSet = true;
	}

	public int getMaxCycles() {
		int threadFactor = (m_config.m_numThreads > 4) ? m_config.m_numThreads : 4;
		return (int) (m_config.m_frequency * m_maxTime) / (threadFactor * 1000);
	}

	public void setMaxTime(double maxTime) {
		m_maxTime = maxTime;
		m_maxTimeSet = true;
	}

	public double getMaxTime() {
		return m_maxTime;
	}

	public void resetMaxTime() {
		m_maxTime = 0.0;
		m_maxTimeSet = false;
	}

	public long getWorstCycles() {
		Node worstCaseSubRoute = m_route.getNode();
		return worstCaseSubRoute.getAttributes().getNumCycles(Case.WORST, false);
	}

	public double getWorstTime() {
		Node worstCaseSubRoute = m_route.getNode();		
		return worstCaseSubRoute.getAttributes().getTimeTaken(Case.WORST, false);
	}

	public boolean passedTiming() {
		if (getWorstTime() > getMaxTime()) {
			return false;
		}
		return true;
	}

	public long getViolationCycles() {
		return getWorstCycles() - getMaxCycles();
	}

	public double getViolationTime() {
		return getWorstTime() - getMaxTime();
	}

	public long getSlackCycles() {
		return getMaxCycles() - getWorstCycles();
	}

	public double getSlackTime() {
		return getMaxTime() - getWorstTime();
	}

	public int getMinFrequency() {
		int threadFactor = (m_config.m_numThreads > 4) ? m_config.m_numThreads : 4;
		return (int)(getWorstCycles() * 1000 * threadFactor) / (int)(getMaxTime() + 1.0);
	}

	public long getNumPaths() {
		Node worstCaseSubRoute = m_route.getNode();
		return worstCaseSubRoute.getAttributes().getNumPaths();		
	}
		
	public String getDescription(boolean full) {
		String description = "";
		if (isTimingSpecified()) {
			if (passedTiming())
				description += "    Pass";
			else
				description += "    Fail";

			int numWarnings = m_route.getUnknowns(UnknownState.UNKNOWN).size();
			if (numWarnings > 0)
				description += " with " + numWarnings + " unknown";
			if (numWarnings > 1)
				description += "s";

			if (full) {
				description += ", Num Paths: " + getNumPaths();
				description += ", " + getTimingsDescription();
				description += String.format(", Min Frequency: %d Mhz\n", getMinFrequency());
			}
		} else {
			description += "    No Timing Specified\n";
		}
		return description;
	}

	public String getTimingsDescription() {
		String description = "";
		if (passedTiming())
			description += String.format("Slack: %.1fns, ", getSlackTime());
		else
			description += String.format("Violation: %.1fns, ", getViolationTime());

		description += String.format("Required: %.1fns, Worst: %.1fns", getMaxTime(), getWorstTime());

		return description;
	}
}
