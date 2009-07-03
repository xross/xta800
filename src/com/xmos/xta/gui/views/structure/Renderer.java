package com.xmos.xta.gui.views.structure;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.views.viewnode.ViewNode;

public class Renderer implements Runnable {
    public static final boolean DEBUG = false;

    private static final int MAX_IMAGE_SIZE = 1024 * 1024 * 50;

    private Display m_display;
    private RendererControl m_control;
    private ZoomedCanvasProperties m_canvasProperties;

    private Image m_image;
    private boolean m_imageTooLarge;
    
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

	public void run() {
		boolean done = false;
		while (!done) {
			done = m_control.waitForChange();
			if (!done) {
				if (m_control.hasPreviousSelection()) {
					selectionChanged();
				}
				
			    if (m_control.renderRequired()) {
			    	updateCanvasProperties();
			    }
		    	
			    if (m_imageTooLarge) {
			    	if (m_control.renderRequired() || m_control.imageMoved()) {
			    		m_display.syncExec(new Runnable() {
			    			public void run() {
			    				render(m_control.getOffsetX(), m_control.getOffsetY());
			    			}});
						m_control.clearRenderRequired();
						m_control.clearImageMoved();
			    	}
					m_control.updateCanvasImages(m_image, 0);
					
			    } else {
			    	if (m_control.renderRequired()) {
						m_display.syncExec(new Runnable() {
							public void run() {
								render(0, 0);
							}});
						m_control.clearRenderRequired();
			    	}
					m_control.updateCanvasImages(m_image, m_control.getOffsetX());
			    	
			    }
			    m_control.signalRenderComplete();
			}
		}
	}

	private void render(int offsetX, int offsetY) {
		if (DEBUG) System.out.printf("StructureRenderer::render(%d, %d)\n", offsetX, offsetY);

	    GC gc = new GC(m_image);
	    gc.setAntialias(SWT.ON);
	    gc.setTextAntialias(SWT.ON);

	    // Draw the structure
        gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        Rectangle structureBounds = m_image.getBounds();
        gc.fillRectangle(0, 0, structureBounds.width, structureBounds.height);

        ViewNode viewNode = m_control.getViewNode();
        if (viewNode != null) {
        	Rectangle visibleArea = new Rectangle(offsetX, offsetY, structureBounds.width, structureBounds.height);
        	m_canvasProperties.setVisibleArea(visibleArea);
    		viewNode.updateScaledXy();
        	viewNode.draw(m_display, gc);
        }
	    gc.dispose();

	    if (DEBUG) System.out.printf("StructureRenderer::render() done\n");
	}

	private void selectionChanged() {
	    ViewNode viewNode = m_control.getViewNode();
		List<Node> currentSelection = m_control.getCurrentSelection();
		m_canvasProperties.setSelected(currentSelection);

    	while (m_control.hasPreviousSelection()) {
    		// Must iterate over the previous selections in order to clear the list
			List<Node> previousSelection = m_control.getPreviousSelection();

			if (m_control.renderRequired() || (viewNode == null) ||
				(previousSelection.size() == 0) || m_imageTooLarge) {
    			continue;
    		}
    		redraw(viewNode, previousSelection);
    	}

    	if ((currentSelection.size() > 0) && (viewNode != null)) {
    		redraw(viewNode, currentSelection);
		}
	}

	private void redraw(final ViewNode viewNode, final List<Node> selection) {
		final GC gc = new GC(m_image);
	    gc.setAntialias(SWT.ON);
	    gc.setTextAntialias(SWT.ON);

		m_display.syncExec(new Runnable() {
			public void run() {
    	    	viewNode.redraw(m_display, gc, selection);
			}});

	    gc.dispose();
	}

	private void updateCanvasProperties() {
    	m_control.updateCanvasProperties(m_canvasProperties);
    	double scale = m_control.getScale();
		int imageX = (int)((double)m_control.getPixelsX() * scale);
		int imageY = (int)((double)m_control.getPixelsY() * scale);
		
		int canvasX = m_control.getCanvasWidth();
    	int canvasY = m_control.getCanvasHeight();

		if (canvasX > imageX)
			imageX = canvasX;
		if (canvasY > imageY)
			imageY = canvasY;

		m_image.dispose();

		if ((imageX * imageY) > MAX_IMAGE_SIZE) {
			assert((canvasX * canvasY) < MAX_IMAGE_SIZE);
			m_imageTooLarge = true;
			imageX = canvasX;
			imageY = canvasY;
		} else {
			m_imageTooLarge = false;
		}

		m_image = new Image(m_display, imageX, imageY);
		if (DEBUG) System.out.println("Resize m_structureImage " + imageX + ", " + imageY);

        ViewNode viewNode = m_control.getViewNode();
		if (viewNode != null) {
    		viewNode.updateScaledXy();
    	}
	}
}
