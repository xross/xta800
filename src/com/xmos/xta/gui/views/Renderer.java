package com.xmos.xta.gui.views;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public abstract class Renderer implements Runnable {
    public static final boolean DEBUG = false;

    protected Display m_display;
    protected RendererControl m_control;
    protected ZoomedCanvasProperties m_canvasProperties;

    protected Image m_image;
    
	public Renderer(Display display, RendererControl control, ZoomedCanvasProperties canvasProperties) {
		if (DEBUG) System.out.printf("Renderer()\n");
		m_display = display;
		m_control = control;
		m_canvasProperties = canvasProperties;
		
	    // Dimensions must be minimum of 1
		m_image = new Image(m_display, 1, 1);

	    // Determine font metrics
        GC gc = new GC(m_image);
        m_canvasProperties.setAverageCharWidth(gc.getFontMetrics().getAverageCharWidth());
        gc.dispose();
	}
}
