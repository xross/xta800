package com.xmos.xta.ui.views.binary.graphics.objects;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;


public class GraphicsPorts extends GraphicsObject {

    public GraphicsPorts() {
    }

	@Override
	public String getIconName() {
		return "ports";
	}

	@Override
	public String getName() {
		return String.format("Ports (%d)", numChildren());
	}

	@Override
	public String getShortName() {
		return getName();
	}

	@Override
	public String getToolTip() {
		return getName();
	}

	@Override
	public void draw(Display display, GC gc, int x, int y, boolean summary) {
	}

	@Override
	public int getPixelsX(boolean summary) {
		return 0;
	}

	@Override
	public int getPixelsY(boolean summary) {
		return 0;
	}

	public GraphicsObject getObject(int x, int y, boolean summary) {
		return null;
	}
}
