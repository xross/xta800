package com.xmos.xta.ui.views.structure;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.ui.views.Renderer;
import com.xmos.xta.ui.views.RendererControl;
import com.xmos.xta.ui.views.ZoomedCanvasProperties;
import com.xmos.xta.ui.views.viewnode.ViewNode;

public class StructureRenderer extends Renderer {

	private boolean m_imageTooLarge;
    private static final int MAX_IMAGE_SIZE = 1024 * 1024 * 30;
    
	public StructureRenderer(Display display, RendererControl control, ZoomedCanvasProperties canvasProperties,
			Canvas canvas) {
		super(display, control, canvasProperties, canvas);
	}

	public void run() {
		boolean programExited = false;
		while (!programExited) {
			programExited = m_control.waitForChange();
			if (programExited)
				break;
			
			if (m_control.selectBoxChanged()) {
				Rectangle selectBox = m_control.getSelectBox();
				drawSelectBox(selectBox);
				renderComplete();
				continue;
			}
			
			boolean canvasResized = m_control.canvasResized();
			if (canvasResized) {
				resizeCanvas();
			}
			
			boolean renderRequired = m_control.getAndClearRenderRequired();
			boolean selectionChanged = m_control.hasPreviousSelection();
			if (selectionChanged) {
				selectionChanged(renderRequired);
			}
			
		    if (renderRequired || (m_imageTooLarge && canvasResized)) {
		    	updateCanvasProperties();
		    }

			boolean imageMoved = m_control.getAndClearImageMoved();
		    if (m_imageTooLarge) {
			    if (renderRequired || imageMoved || canvasResized) {
		    		m_display.syncExec(new Runnable() {
		    			public void run() {
		    				render(m_control.getOffsetX(), m_control.getOffsetY());
		    			}});
		    	}
				updateCanvasImages(m_image, 0);
				
		    } else {
		    	if (renderRequired) {
					m_display.syncExec(new Runnable() {
						public void run() {
							render(0, 0);
						}});
		    	}
				updateCanvasImages(m_image, m_control.getOffsetX());
		    	
		    }
		    renderComplete();
		}
	}

	protected void render(int offsetX, int offsetY) {
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

	private void selectionChanged(boolean renderRequired) {
	    ViewNode viewNode = m_control.getViewNode();
		List<Node> currentSelection = m_control.getCurrentSelection();
		m_canvasProperties.setSelected(currentSelection);

    	while (m_control.hasPreviousSelection()) {
    		// Must iterate over the previous selections in order to clear the list
			List<Node> previousSelection = m_control.getPreviousSelection();

			if (renderRequired || (viewNode == null) ||
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
    	double scale = m_control.getScale();
    	m_canvasProperties.setScale(scale);
		int imageX = (int)((double)m_control.getPixelsX() * scale);
		int imageY = (int)((double)m_control.getPixelsY() * scale);
		
		Rectangle bounds = m_canvasImage.getBounds();
		int canvasX = bounds.width;
    	int canvasY = bounds.height;

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
