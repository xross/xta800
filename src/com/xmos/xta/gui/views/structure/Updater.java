package com.xmos.xta.gui.views.structure;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class Updater implements Runnable {
    private Display m_display;
    private RendererControl m_control;
    private Canvas m_canvas;
    
	public Updater(Display display, RendererControl control, Canvas canvas) {
		m_display = display;
		m_control = control;
		m_canvas = canvas;
	}

	public void run() {
		boolean done = false;
		while (!done) {
			done = m_control.waitForRender();
			if (!done) {
				m_display.syncExec(new Runnable() {
					public void run() {
						m_canvas.redraw();
					}});
			}
		}
	}
}
