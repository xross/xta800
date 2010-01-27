package com.xmos.xta.ui.views.binary.graphics.objects;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;


public class GraphicsPins extends GraphicsObject {

    public GraphicsPins() {
    }

	@Override
	public String getIconName() {
		return "block_node";
	}

	@Override
	public String getName() {
		return String.format("User IOs (%d)", numChildren());
	}

	@Override
	public String getToolTip() {
		return getName();
	}

	@Override
	public void draw(Display display, GC gc, int x, int y, double scale, boolean summary) {
	}

	@Override
	public int getPixelsX(boolean summary) {
		return 0;
	}

	@Override
	public int getPixelsY(boolean summary) {
		return 0;
	}
}
