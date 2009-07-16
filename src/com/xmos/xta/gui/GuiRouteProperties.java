package com.xmos.xta.gui;

public class GuiRouteProperties {
    private double m_scale;
    private int m_offsetX;
    private int m_offsetY;

    public GuiRouteProperties() {
		m_scale = 0.0;
		m_offsetX = 0;
		m_offsetY = 0;
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
}
