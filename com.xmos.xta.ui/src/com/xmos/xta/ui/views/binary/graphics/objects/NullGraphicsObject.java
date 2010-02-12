package com.xmos.xta.ui.views.binary.graphics.objects;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class NullGraphicsObject extends GraphicsObject {
    
	public NullGraphicsObject(GraphicsObject child) {
		super();
		addChild(child);
    }
    
	@Override
	public String getName() {
		assert(false);
    	return "null";
    }

	@Override
	public String getShortName() {
		return getName();
	}

	@Override
	public String getIconName() {
		assert(false);
		return "errors";
	}
	
	@Override
	public String getToolTip() {
		assert(false);
		return "";
	}

	@Override
	public void draw(Display display, GC gc, int x, int y, boolean summary) {
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
