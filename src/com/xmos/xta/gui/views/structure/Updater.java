package com.xmos.xta.gui.views.structure;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class Updater implements Runnable {
    public static final boolean DEBUG = false;

    private Display m_display;
    private RendererControl m_control;
    private Canvas m_structureCanvas;
    private Canvas m_timesCanvas;
    
	public Updater(Display display, RendererControl control, Canvas structureCanvas, Canvas timesCanvas) {
		if (DEBUG) System.out.printf("Updater()\n");
		m_display = display;
		m_control = control;
		m_structureCanvas = structureCanvas;
		m_timesCanvas = timesCanvas;
	}

	public void run() {
		boolean done = false;
		while (!done) {
			done = m_control.waitForRender();
			if (!done) {
				if (m_display.isDisposed()) {
					System.out.println("display disposed");
				} else {
					m_display.syncExec(new Runnable() {
						public void run() {
							if (m_structureCanvas.isDisposed()) {
								System.out.println("m_structureCanvas disposed");
							} else {
								m_structureCanvas.redraw();
							}
							if (m_timesCanvas.isDisposed()) {
								System.out.println("m_timesCanvas disposed");
							} else {
								m_timesCanvas.redraw();
							}
						}});
				}
			}
		}
	}
}
