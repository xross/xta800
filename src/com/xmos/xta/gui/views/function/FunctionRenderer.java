package com.xmos.xta.gui.views.function;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.engine.analysis.structural.nodes.attributes.Status;
import com.xmos.xta.gui.views.Renderer;
import com.xmos.xta.gui.views.RendererControl;
import com.xmos.xta.gui.views.ZoomedCanvasProperties;
import com.xmos.xta.gui.views.viewnode.FunctionNode;
import com.xmos.xta.gui.views.viewnode.ViewNode;

public class FunctionRenderer extends Renderer {

	private static final double TIMELINE_MIN_PIXELS_GAP = 50.0;
    private static final double TIMELINE_MAX_PIXELS_GAP = 100.0;
    private static final int BEST_WORST_SEPARATION = FunctionNode.TEXT_AREA_Y;

    private int m_timeLineY;

    private String m_timeUnits;
    private double m_pixelsPerTimeUnit;
	private Status m_status;

	public FunctionRenderer(Display display, RendererControl control, ZoomedCanvasProperties canvasProperties,
			int timeLineY, int borderY) {
		super(display, control, canvasProperties);
		m_timeLineY = timeLineY;
		m_status = new Status();
	}

	public void run() {
		boolean done = false;
		while (!done) {
			done = m_control.waitForChange();
			if (!done) {
				boolean renderRequired = m_control.getAndClearRenderRequired();
				boolean canvasResized = m_control.canvasResized();
				boolean imageMoved = m_control.getAndClearImageMoved();
				boolean selectionChanged = m_control.hasPreviousSelection();

				if (selectionChanged) {
					selectionChanged(renderRequired);
				}
				
			    if (renderRequired || canvasResized) {
			    	updateCanvasProperties();
			    }

			    if (renderRequired || imageMoved || canvasResized || selectionChanged) {
		    		m_display.syncExec(new Runnable() {
		    			public void run() {
		    				render(m_control.getOffsetX(), m_control.getOffsetY());
		    			}});
		    	}
				m_control.updateCanvasImages(m_image, 0);
			    m_control.signalRenderComplete();
			}
		}
	}

	private void selectionChanged(boolean renderRequired) {
		// Update current selection
		List<Node> currentSelection = m_control.getCurrentSelection();
		m_canvasProperties.setSelected(currentSelection);

    	while (m_control.hasPreviousSelection()) {
    		// Clear list of previous selections
			m_control.getPreviousSelection();
    	}
	}

	private void updateCanvasProperties() {
    	m_control.updateCanvasProperties(m_canvasProperties);
		
		Image canvasImage = m_control.getResizedCanvas();
		Rectangle bounds = canvasImage.getBounds();
		int imageX = bounds.width;
    	int imageY = bounds.height;

		m_image.dispose();
		m_image = new Image(m_display, imageX, imageY);
		if (DEBUG) System.out.println("Resize m_structureImage " + imageX + ", " + imageY);

        ViewNode viewNode = m_control.getViewNode();
		if (viewNode != null) {
    		viewNode.updateScaledXy();
    	}
	}

	protected void render(int offsetX, int offsetY) {
		if (DEBUG) System.out.printf("StructureRenderer::render(%d, %d)\n", offsetX, offsetY);

	    GC gc = new GC(m_image);
	    gc.setAntialias(SWT.ON);
	    gc.setTextAntialias(SWT.ON);

        gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        Rectangle structureBounds = m_image.getBounds();
        gc.fillRectangle(0, 0, structureBounds.width, structureBounds.height);

        ViewNode viewNode = m_control.getViewNode();
        if (viewNode != null) {
        	Rectangle visibleArea = new Rectangle(offsetX, offsetY, structureBounds.width, structureBounds.height);
        	m_canvasProperties.setVisibleArea(visibleArea);
        	viewNode.updateScaledXy();
        	if (updateUnits()) {
	        	drawVerticalLines(gc, offsetX);
	        	drawHorizontalLines(gc, m_timeLineY + ViewNode.TEXT_AREA_Y - offsetY, visibleArea);
	        	
	        	int startY = m_timeLineY;
	        	viewNode.draw(m_display, gc, Case.WORST, 0, startY);
	        	startY += viewNode.getPixelsY(Case.WORST) + BEST_WORST_SEPARATION;
	        	viewNode.draw(m_display, gc, Case.BEST, 0, startY);
	        	drawTimeLine(gc, offsetX);
        	} else {
        		drawInfiniteTime(gc);
        	}
        }
	    gc.dispose();

	    if (DEBUG) System.out.printf("StructureRenderer::render() done\n");
	}

    private void drawInfiniteTime(GC gc) {
		gc.setForeground(m_display.getSystemColor(SWT.COLOR_RED));
		gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
		gc.drawText(" Worst-case time is infinite - cannot render", 0, 0);
	}

	private void drawHorizontalLines(GC gc, int startY, Rectangle visibleArea) {
		gc.setForeground(m_display.getSystemColor(SWT.COLOR_GRAY));
		gc.setBackground(m_display.getSystemColor(SWT.COLOR_GRAY));
    	gc.setLineStyle(SWT.LINE_DASH);
    	for (int y = startY; y < visibleArea.height; y += ViewNode.TEXT_AREA_Y) {
    		gc.drawLine(0, y, visibleArea.width, y);
    	}
    	gc.setLineStyle(SWT.LINE_SOLID);
		
	}

	private void drawVerticalLines(GC gc, int offsetX) {
    	double scale = m_control.getScale();
    	Rectangle visibleArea = m_canvasProperties.getVisibleArea();

    	gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLUE));
    	gc.setLineStyle(SWT.LINE_DOT);
    	double timeUnitIncrement = getTimeUnitIncrement();
		for (double x = 0; x < (offsetX + visibleArea.width); x += m_pixelsPerTimeUnit * scale * timeUnitIncrement) {
        	if ((int)x > offsetX) {
	            gc.drawLine((int)x - offsetX, m_timeLineY, (int)x - offsetX, visibleArea.height);
        	}
        }    	

    	gc.setLineStyle(SWT.LINE_SOLID);
    }
 
	private void drawTimeLine(GC gc, int offsetX) {
    	double scale = m_control.getScale();
    	Rectangle visibleArea = m_canvasProperties.getVisibleArea();
		int visibleWidth = visibleArea.width;
    	
    	gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
    	gc.fillRectangle(0, 0, visibleWidth, m_timeLineY);

    	gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLUE));
    	gc.setLineStyle(SWT.LINE_DOT);
    	gc.drawLine(0, m_timeLineY, visibleWidth, m_timeLineY);
    	gc.setLineStyle(SWT.LINE_SOLID);
    	
    	gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLACK));
    	double timeUnit = 0; 	
    	double timeUnitIncrement = getTimeUnitIncrement();
		for (double x = 0; x < (offsetX + visibleWidth); x += m_pixelsPerTimeUnit * scale * timeUnitIncrement) {
        	if ((int)x > offsetX) {
	        	if (timeUnitIncrement < 0.1) {
	        		gc.drawText(String.format("%.02f %s", timeUnit, m_timeUnits), (int)x - offsetX, 0);
	        	} else {
	        		gc.drawText(String.format("%.01f %s", timeUnit, m_timeUnits), (int)x - offsetX, 0);
	        	}
        	}
         	timeUnit += timeUnitIncrement;
        }    	
    }
 
	private double getTimeUnitIncrement() {
    	double increment = 1.0;
    	double numPixels = m_pixelsPerTimeUnit * m_control.getScale();
    	while (numPixels > TIMELINE_MAX_PIXELS_GAP) {
    		numPixels /= 2.0;
    		increment /= 2.0;
    	}
    	while (numPixels < TIMELINE_MIN_PIXELS_GAP) {
    		numPixels *= 2.0;
    		increment *= 2.0;
    	}
    	return increment;
    }

	private boolean updateUnits() {
    	Node pathRoot = null;
		int pixelsX = 0;

		ViewNode viewNode = m_control.getViewNode();
		if (viewNode != null) {
    		pathRoot = viewNode.getTreeNode();
    		pixelsX = viewNode.getPixelsX();
    	}

    	if (pathRoot == null) {
			m_timeUnits = "ns";
			m_pixelsPerTimeUnit = 1;
			return true;
    	}

    	double timeTaken = pathRoot.getAttributes().getTimeTaken(Case.WORST, m_status, false);
    	if (m_status.isInfinite() || m_status.isUnresolved()) {
    		return false;
		}
		m_pixelsPerTimeUnit = pixelsX / timeTaken;
		
		double scale = m_control.getScale();
		double renderPixels = ((double)pixelsX * scale);
		double endTime = timeTaken * ((double)m_control.getCanvasWidth() / renderPixels);
		if (endTime > 2000000) {
			m_timeUnits = "ms";
			m_pixelsPerTimeUnit *= 1000 * 1000;
		} else if (endTime > 2000) {
			m_timeUnits = "us";
			m_pixelsPerTimeUnit *= 1000;
		} else {
			m_timeUnits = "ns";
		}
		return true;
	}
}
