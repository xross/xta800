package com.xmos.xta.ui;

import com.xmos.xta.ui.views.viewnode.ViewNode;

public class GuiRouteProperties {
    private double m_scale;
    private int m_offsetX;
    private int m_offsetY;
	private ViewNode m_viewNode;

    public GuiRouteProperties() {
		m_scale = 0.0;
		m_offsetX = 0;
		m_offsetY = 0;
		m_viewNode = null;
	}

	public double getScale() {
		return m_scale;
	}

	public void setScale(double scale) {
		m_scale = scale;
	}

	public int getOffsetX() {
		return m_offsetX;
	}

	public void setOffsetX(int offset) {
		m_offsetX = offset;
	}

	public int getOffsetY() {
		return m_offsetY;
	}

	public void setOffsetY(int offset) {
		m_offsetY = offset;
	}

	public ViewNode getViewNode() {
		return m_viewNode;
	}

	public void setViewNode(ViewNode viewNode) {
		m_viewNode = viewNode;
	}
}
