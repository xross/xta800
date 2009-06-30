package com.xmos.xta.gui.views.structure;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.views.viewnode.ViewNode;

public class ZoomedCanvasProperties {
    public static final boolean DEBUG = false;

    private double m_scale;

    private int m_lineWidth;
    
    private double m_totalBest;
    private double m_totalWorst;
    
    private int m_averageCharWidth;

    private int m_timesHeight;
    private Rectangle m_visibleArea;
    private List<Node> m_selectedNodes;

    public ZoomedCanvasProperties() {
		if (DEBUG) System.out.printf("ZoomedCanvasProperties()\n");			
		m_lineWidth = 1;

		m_scale = 1.0;
		m_totalBest = 0.0;
		m_totalWorst = 0.0;

		m_selectedNodes = new ArrayList<Node>();
		m_visibleArea = new Rectangle(0, 0, 0, 0);
		m_timesHeight = 0;
	}

	public double getScale() {
		return m_scale;
	}

	public void setScale(double scale) {
		m_scale = scale;
	}

	public double getTotalBest() {
		return m_totalBest;
	}

	public void setTotalBest(double totalBest) {
		m_totalBest = totalBest;
	}

	public double getTotalWorst() {
		return m_totalWorst;
	}

	public void setTotalWorst(double totalWorst) {
		m_totalWorst = totalWorst;
	}

	public void setSelected(List<Node> nodes) {
		m_selectedNodes = nodes;		
	}

	public boolean isSelected(ViewNode viewNode) {
		Node node = viewNode.getTreeNode();
		if (m_selectedNodes.contains(node)) {
			return true;
		}
		return false;
	}

	public void setVisibleArea(Rectangle visibleArea) {
		m_visibleArea = visibleArea;
	}

	public Rectangle getVisibleArea() {
		return m_visibleArea;
	}

	public int getLineWidth() {
		return m_lineWidth;
	}

	public void setAverageCharWidth(int averageCharWidth) {
		m_averageCharWidth = averageCharWidth;
	}

	public int getAverageCharWidth() {
		return m_averageCharWidth;
	}

	public int getTimesHeight() {
		return m_timesHeight;
	}

	public void setTimesHeight(int timesHeight) {
		m_timesHeight = timesHeight;
	}
}

