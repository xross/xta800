package com.xmos.xta.ui.views;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public abstract class Renderer implements Runnable {
    public static final boolean DEBUG = false;

    protected Display m_display;
    protected RendererControl m_control;
    protected ZoomedCanvasProperties m_canvasProperties;

    protected Image m_image;
    Canvas m_canvas;

    protected Image m_canvasImage;
    private Image m_canvasImageClean;
    
	public Renderer(Display display, RendererControl control, ZoomedCanvasProperties canvasProperties,
			Canvas canvas) {
		if (DEBUG) System.out.printf("Renderer()\n");
		m_display = display;
		m_control = control;
		m_canvasProperties = canvasProperties;
		m_canvas = canvas;
		
	    // Dimensions must be minimum of 1
	    m_canvasImageClean = new Image(m_display, 1, 1);
		m_canvasImage = new Image(m_display, 1, 1);
		m_control.setCanvasImage(m_canvasImage);
		m_image = new Image(m_display, 1, 1);

	    // Determine font metrics
        GC gc = new GC(m_image);
        m_canvasProperties.setAverageCharWidth(gc.getFontMetrics().getAverageCharWidth());
        gc.dispose();
	}

	public void updateCanvasImages(Image image, int offsetX) {
		if (DEBUG) System.out.println("Renderer::updateCanvasImages()");
		copyImage(image, m_canvasImageClean, offsetX, m_control.getOffsetY());
		copyImage(m_canvasImageClean, m_canvasImage, 0, 0);
	}

	// Copy images, ensuring bounds are valid
	private void copyImage(Image fromImage, Image toImage, int offsetX, int offsetY) {
		Rectangle toBounds = toImage.getBounds();
		int canvasX = toBounds.width;
		int canvasY = toBounds.height;
		
		Rectangle fromBounds = fromImage.getBounds();
		if (canvasX > fromBounds.width)
			canvasX = fromBounds.width;
		if (canvasY > fromBounds.height)
			canvasY = fromBounds.height;
		
		if ((offsetX + canvasX) > fromBounds.width)
			offsetX = fromBounds.width - canvasX;
		if ((offsetY + canvasY) > fromBounds.height)
			offsetY = fromBounds.height - canvasY;
		
		GC gc = new GC(toImage);
		gc.drawImage(fromImage,
				offsetX, offsetY, canvasX, canvasY,
				0, 0, canvasX, canvasY);
		gc.dispose();
	}

	public void resizeCanvas() {
		Rectangle bounds = m_control.getCanvasBounds();
		m_canvasImage.dispose();
		m_canvasImage = new Image(m_display, bounds.width, bounds.height);

		m_canvasImageClean.dispose();
		m_canvasImageClean =  new Image(m_display, bounds.width, bounds.height);
	}

	public void drawSelectBox(Rectangle selectBox) {
		copyImage(m_canvasImageClean, m_canvasImage, 0, 0);
		if (selectBox == null)
			return;
		
		GC gc = new GC(m_canvasImage);
		gc.drawFocus(selectBox.x, selectBox.y, selectBox.width, selectBox.height);
		gc.dispose();
	}
	
	protected void renderComplete() {
		m_display.syncExec(new Runnable() {
			public void run() {
				m_control.setCanvasImage(m_canvasImage);
				m_canvas.redraw();
			}});
	}
}
