package com.xmos.xta.ui.views.binary.graphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

import com.xmos.xta.ui.views.binary.graphics.objects.GraphicsObject;

public class GraphicsCanvas{
    
	private Display m_display;

	protected Shell m_toolTip;
    private Label m_tipLabel;
	
	private Canvas m_canvas;
	private Image m_canvasImage;
	private int m_offsetX;
	private int m_offsetY;
	
	private ScrollBar m_hScroll;
	private ScrollBar m_vScroll;
	
	private GraphicsObject m_graphicsObject;
	private boolean m_summary;
	private double m_scale;

	public GraphicsCanvas(final Composite parent) {
		m_display = parent.getDisplay();
		m_graphicsObject = null;

		m_scale = 1;
	    m_offsetX = 0;
		m_offsetY = 0;
	    m_canvas = new Canvas(parent, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
	    Rectangle bounds = parent.getBounds();
	    m_canvas.setSize(bounds.x, bounds.y);
	    m_canvas.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	drawCanvas(e.gc);
		    }
	    });
	    m_canvasImage = new Image(m_display, 1, 1);

	    m_canvas.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
			}

			public void controlResized(ControlEvent e) {
				updateScrollBars();
				m_canvas.redraw();
			}
	    });

	    parent.addControlListener(new ControlListener() {
			public void controlResized(ControlEvent e) {
			    Rectangle bounds = parent.getBounds();
			    m_canvas.setSize(bounds.x, bounds.y);
				m_canvas.redraw();
			}
			public void controlMoved(ControlEvent e) {
				m_canvas.redraw();
			}
	    });

	    m_hScroll = parent.getHorizontalBar();
	    m_hScroll.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
				m_offsetX = m_hScroll.getSelection();
				m_canvas.redraw();
	        }
	    });

	    m_vScroll = parent.getVerticalBar();
	    m_vScroll.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
				m_offsetY = m_vScroll.getSelection();
				m_canvas.redraw();
	        }
	    });
	    
		Listener canvasListener = new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
                case SWT.MouseWheel:
                	if (event.count > 0) {
                		zoomIn();
                	} else {
                		zoomOut();
                	}
                	event.doit = false;
                    break;
                    
                case SWT.KeyDown:
                	if (event.character == '+')
                		zoomIn();
                	else if (event.character == '-')
                		zoomOut();
                	break;

                case SWT.MouseMove:
    	        	if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
    	    			m_toolTip.dispose ();
    	        	}

                	break;
				}
			}
		};
		m_canvas.addListener(SWT.MouseMove, canvasListener);
		
	    m_canvas.addMouseTrackListener(new MouseTrackListener() { 
	    	private Listener labelListener = new Listener() {
				public void handleEvent (Event e) {
					// Dispose the tool tip if you click on it
    	        	if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
    	    			m_toolTip.dispose ();
    	        	}
				}
			};

			public void mouseEnter(MouseEvent e) {}
		    public void mouseExit(MouseEvent e) {}
		    
		    public void mouseHover(MouseEvent e) { 
				GraphicsObject object = getObject(e.x, e.y);
				if (object != null) {
    	    		if (m_toolTip != null  && !m_toolTip.isDisposed ())
    	    			m_toolTip.dispose ();

    	    		createToolTip(object.getToolTip(), labelListener);
    			}
		     } 
	    });

	}

	private void createToolTip(String toolTip, Listener labelListener) {
		m_toolTip = new Shell(m_display, SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
		m_toolTip.setBackground (m_display.getSystemColor (SWT.COLOR_INFO_BACKGROUND));
		FillLayout layout = new FillLayout ();
		layout.marginWidth = 2;
		m_toolTip.setLayout(layout);
		m_tipLabel = new Label (m_toolTip, SWT.NONE);
		m_tipLabel.setForeground(m_display.getSystemColor (SWT.COLOR_INFO_FOREGROUND));
		m_tipLabel.setBackground(m_display.getSystemColor (SWT.COLOR_INFO_BACKGROUND));
		m_tipLabel.setText(toolTip);
		m_tipLabel.addListener(SWT.MouseExit, labelListener);
		m_tipLabel.addListener(SWT.MouseDown, labelListener);
		Point size = m_toolTip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point pt = Display.getCurrent().getCursorLocation();

		// Display slightly to the right of the cursor in order to not have it behind cursor
		m_toolTip.setBounds(pt.x + 10, pt.y, size.x, size.y);
		m_toolTip.setVisible(true);
	}

	protected GraphicsObject getObject(int x, int y) {
		if (m_graphicsObject == null) {
			return null;
		}
		x = (int)((double)(x + m_hScroll.getSelection()) / m_scale);
		y = (int)((double)(y + m_vScroll.getSelection()) / m_scale);
		return m_graphicsObject.getObject(x, y, m_summary);
	}

	protected void drawCanvas(GC gc) {
		Rectangle bounds = m_canvas.getBounds();
		Rectangle imageBounds = m_canvasImage.getBounds();
		
		int renderX = scale(imageBounds.width);
		int renderY = scale(imageBounds.height);

		int copyWidth = Math.min(renderX, bounds.width);
		int copyHeight = Math.min(renderY, bounds.height);
		
		int offsetX = (int)((double)m_offsetX / m_scale);
		int offsetY = (int)((double)m_offsetY / m_scale);
		gc.drawImage(m_canvasImage,
				offsetX, offsetY, inverseScale(copyWidth), inverseScale(copyHeight),
				0, 0, copyWidth, copyHeight);
    }

	private int scale(int value) {
		return (int)((double)value * m_scale);
	}

	private int inverseScale(int value) {
		return (int)((double)value / m_scale);
	}

	private void updateScrollBars() {
		Rectangle canvasBounds = m_canvas.getBounds();
		int canvasX = canvasBounds.width;
		int canvasY = canvasBounds.height;
		
		Rectangle imageBounds = m_canvasImage.getBounds();
		int renderX = scale(imageBounds.width);
		if (canvasX > renderX)
			renderX = canvasX;
		
		int renderY = scale(imageBounds.height);
		if (canvasY > renderY)
			renderY = canvasY;

		m_hScroll.setMaximum(renderX);
    	m_hScroll.setThumb(canvasX);
	    m_vScroll.setMaximum(renderY);
    	m_vScroll.setThumb(canvasY);

		m_offsetX = m_hScroll.getSelection();
		m_offsetY = m_vScroll.getSelection();
	}

	public void setGraphicsObject(GraphicsObject object, boolean summary) {
		m_graphicsObject = object;
		m_summary = summary;
		refresh();
	}
	
	private void refresh() {
		if (m_canvasImage != null) {
			m_canvasImage.dispose();
		}

		if (m_graphicsObject != null) {
			int x = m_graphicsObject.getPixelsX(m_summary) + 1;
			int y = m_graphicsObject.getPixelsY(m_summary) + 1;

			m_canvasImage = new Image(m_display, x, y);
			GC gc = new GC(m_canvasImage);
			gc.setAntialias(SWT.ON);
			m_graphicsObject.draw(m_display, gc, 0, 0, 1, m_summary);
			gc.dispose();
		} else {
			m_canvasImage = new Image(m_display, 1, 1);
		}
		updateScrollBars();
		m_canvas.redraw();
	}

	public void exportJpg() {
		GraphicsUtils.exportJpg(m_canvasImage.getImageData());
	}

	public void zoomFit() {
		Rectangle imageBounds = m_canvasImage.getBounds();
		Rectangle canvasBounds = m_canvas.getBounds();
		double xScale = (double)canvasBounds.width / (double)imageBounds.width;
		double yScale = (double)canvasBounds.height / (double)imageBounds.height;
		m_scale = Math.min(xScale, yScale);
		m_scale = Math.min(m_scale, 1);
		refresh();
	}

	protected void zoomOut() {
		m_scale /= 1.2;
		refresh();
	}

	protected void zoomIn() {
		double oldScale = m_scale;
		if (m_scale < 1) {
			m_scale *= 1.2;
		}
		m_scale = Math.min(m_scale, 1);
		
		if (m_scale != oldScale) {
			refresh();
		}
	}

	public void zoomReset() {
		if (m_scale != 1) {
			m_scale = 1;
			refresh();
		}
	}
}
