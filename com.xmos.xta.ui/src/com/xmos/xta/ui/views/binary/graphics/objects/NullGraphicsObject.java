package com.xmos.xta.ui.views.binary.graphics.objects;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class NullGraphicsObject extends GraphicsObject {
    
	public NullGraphicsObject(GraphicsObject child) {
		super();
		addChild(child);
    }
    
	public String getName() {
		assert(false);
    	return "null";
    }

	public String getIconName() {
		assert(false);
		return "errors";
	}
	
	public String getToolTip() {
		assert(false);
		return "";
	}

	@Override
	public void draw(Display display, GC gc, int x, int y, double scale, boolean summary) {
		assert(false);
	}

	@Override
	public int getPixelsX(boolean summary) {
		assert(false);
		return 0;
	}

	@Override
	public int getPixelsY(boolean summary) {
		assert(false);
		return 0;
	}
}
