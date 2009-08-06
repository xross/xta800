package com.xmos.xta.gui.views;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.GuiRouteProperties;
import com.xmos.xta.gui.views.Renderer;
import com.xmos.xta.gui.views.RendererControl;
import com.xmos.xta.gui.views.Views;
import com.xmos.xta.gui.views.viewnode.ViewNode;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.gui.Broadcaster;

public abstract class ZoomedCanvasView extends ViewPart implements IPropertyChangeListener,
                                                       IMenuListener {
    public static final boolean DEBUG = false;

	private static final int LEFT_MOUSE_BUTTON = 1;
	private static final int RIGHT_MOUSE_BUTTON = 3;

    private ViewNode m_viewNode;
    protected ZoomedCanvasProperties m_canvasProperties;
    private Canvas m_canvas;

    private List<Node> m_selectedNodes;
    
    private Composite m_parent;
    private Display m_display;
    private ScrollBar m_hScroll;
    private ScrollBar m_vScroll;
    private int m_hScrollPosition;
    private int m_vScrollPosition;

    private Shell m_toolTip;
    private Label m_tipLabel;
    
    private RendererControl m_renderControl;
    private Renderer m_renderer;
    private Rectangle m_compositeArea;
    
    private String m_name;

	public ZoomedCanvasView(String name) {
		m_name = name;
		if (DEBUG) System.out.println(m_name + "()");			
		GuiEngine.addBroadcastListener(this);
	    
	    m_selectedNodes = new ArrayList<Node>();

		m_canvasProperties = createCanvasProperties();
		m_compositeArea = new Rectangle(0, 0, 0, 0);
	}

	protected abstract ViewNode buildViewNode(Node node, int depth);
	protected abstract ZoomedCanvasProperties createCanvasProperties();
	protected abstract RendererControl createRenderControl(Display display);
	protected abstract Renderer createRenderer(Display display, RendererControl renderControl,
			ZoomedCanvasProperties canvasProperties, Canvas canvas);
	
	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.println(m_name + ": createPartControl");
	    m_parent = parent;
	    m_display = m_parent.getDisplay();
		m_renderControl = createRenderControl(m_display);

	    final Composite composite = new Composite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL); 	    
	    composite.setLayout(new FillLayout()); 

	    m_canvas = new Canvas(composite, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
	    composite.addControlListener(new ControlListener() {
			public void controlResized(ControlEvent e) {
				compositeResized(composite);
			}
			public void controlMoved(ControlEvent e) {
				compositeResized(composite);
			}
			private void compositeResized(final Composite composite) {
				Rectangle clientArea = composite.getClientArea();
				m_renderControl.setCanvasSize(clientArea.width, clientArea.height);

				if (m_compositeArea.width == 0 || m_compositeArea.height == 0) {
					zoomFit();
				}
				m_compositeArea = clientArea;
				updateScrollBars();
			}
	    });	    

	    // Sets up the horizontal scroll bar.
	    m_hScroll = composite.getHorizontalBar();
	    m_hScrollPosition = 0;
	    m_hScroll.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	    		m_renderControl.setOffsetX(m_hScroll.getSelection());
	    		m_hScrollPosition = (int)((double)m_hScroll.getSelection() / m_renderControl.getScale());
	        }
	    });
	    m_hScroll.setMaximum(1);
	    
	    // Sets up the vertical scroll bar.
	    m_vScroll = composite.getVerticalBar();
	    m_vScrollPosition = 0;
	    m_vScroll.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	        	m_renderControl.setOffsetY(m_vScroll.getSelection());
	    		m_vScrollPosition = (int)((double)m_vScroll.getSelection() / m_renderControl.getScale());
	        }
	    });
	    m_vScroll.setMaximum(1);

	    composite.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	m_renderControl.refresh();
		    }
	    });

	    m_canvas.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	drawCanvas(e.gc);
		    }
	    });	

	    setupMouseListeners();
	    createContextMenus();

	    m_renderer = createRenderer(m_display, m_renderControl, m_canvasProperties, m_canvas);
		(new Thread(m_renderer)).start();
		
		initialiseTree();
	    m_renderControl.refresh();
	}
	
	public void dumpJpg(){
		
		BufferedOutputStream bufferedOutput = null;
        try {
        	FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
            String[] filterExt = { "*.jpg" };
            fileDialog.setFilterExtensions(filterExt);
        	fileDialog.setText("Save as...");
            String fileName = fileDialog.open();
        	
            if (fileName != null) {
	            bufferedOutput = new BufferedOutputStream(new FileOutputStream(fileName));
	            
	            ImageLoader imgLoader = new ImageLoader();
	    		ImageData[] imgData = new ImageData[1];
	    		imgData[0] = m_renderer.m_image.getImageData();
	
	    		imgLoader.data = imgData;
	    		imgLoader.save(bufferedOutput, 4);
            }
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (bufferedOutput != null) {
                    bufferedOutput.flush();
                    bufferedOutput.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
	}
	
	public void dispose() {
		if (DEBUG) System.out.println(m_name + ": dispose()");			
		m_renderControl.signalDone();
	}

	public void setFocus() {
		if (DEBUG) System.out.println(m_name + ": setFocus");
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.println(m_name + ": propertyChange: " + event.getProperty());			

		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
	    	if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
				m_toolTip.dispose ();
	    	}
	    	initialiseTree();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE) || 
				   event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			clearView();
			m_renderControl.refresh();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE) ||
				   event.getProperty().equals(Notifier.NOTIFY_UPDATE_UNKNOWNS)) {
	    	if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
				m_toolTip.dispose ();
	    	}
	    	m_renderControl.refresh();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE) || 
				   event.getProperty().equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
			initialiseTree();
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_UPDATE_FOLDING)) {
			m_renderControl.refresh();
			updateScrollBars();
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SELECTION_CHANGED)) {
			updateSelection(GuiEngine.getSelectedNodes());
			
		}
	}
	
	private void updateSelection(List<Node> newNodes) {
    	m_selectedNodes = newNodes;
    	m_renderControl.setSelection(newNodes);
	}

	public void menuAboutToShow(IMenuManager manager) {
    	if (m_toolTip != null  && !m_toolTip.isDisposed()) {
			m_toolTip.dispose();
    	}
    	Views.createContextMenu(manager, m_selectedNodes, m_parent.getShell());
	}

	public String getName() {
		return m_name;
	}
	
	private void updateScrollBars() {
		if (m_renderControl.getScale() == 0) {
			zoomFit();
			return;
		}
		int canvasX = m_renderControl.getCanvasWidth();
		int canvasY = m_renderControl.getCanvasHeight();
		
		int renderX = scale(m_renderControl.getPixelsX());
		if (canvasX > renderX)
			renderX = canvasX;
		
		int renderY = m_renderControl.getPixelsY();
		if (m_canvasProperties.scaleY())
			renderY = scale(renderY);
		if (canvasY > renderY)
			renderY = canvasY;

		m_hScroll.setMaximum(renderX);
    	m_hScroll.setThumb(canvasX);
    	int position = setScrollPosition(m_hScroll, scale(m_hScrollPosition));
    	m_renderControl.setOffsetX(position);
	    
	    m_vScroll.setMaximum(renderY);
    	m_vScroll.setThumb(canvasY);
    	position = m_vScrollPosition;
    	if (m_canvasProperties.scaleY()) {
    		position = setScrollPosition(m_vScroll, scale(m_vScrollPosition));
    	}
    	m_renderControl.setOffsetY(position);
    	
		if (DEBUG) System.out.printf("%s: updateScrollBars (%d, %d, %d) (%d, %d, %d)\n", m_name,
			m_hScroll.getMaximum(), m_hScroll.getThumb(), m_hScroll.getSelection(),
			m_vScroll.getMaximum(), m_vScroll.getThumb(), m_vScroll.getSelection());
	}

	private int setScrollPosition(ScrollBar scrollBar, int position) {
		// Need to ensure the scroll bar is in the valid range
		int scrollRange = (scrollBar.getMaximum() - scrollBar.getThumb());
    	if (position > scrollRange)
    		position = scrollRange;
    	if (position < 0)
    		position = 0;
    	scrollBar.setSelection(position);
    	return position;
	}

	public void zoomOut() {
		if (m_viewNode == null)
			return;

		updateScrollPositions();
		m_renderControl.setScale(m_renderControl.getScale() / 1.2);
		updateScrollBars();
	}

	public void zoomIn() {
		if (m_viewNode == null)
			return;
		
		// Don't zoom any more than the maximum width
		double scale = m_renderControl.getScale();
		if (scale < 5) {
			updateScrollPositions();
			m_renderControl.setScale(scale * 1.2);
			updateScrollBars();
		}
	}

	public void zoomReset() {
		updateScrollPositions();
		m_renderControl.setScale(1.0);
		updateScrollBars();
	}

	public void zoomFit() {
		double xScale = (double)m_renderControl.getCanvasWidth() / (double)m_renderControl.getPixelsX();
		double yScale = (double)m_renderControl.getCanvasHeight() / (double)m_renderControl.getPixelsY();
		m_hScrollPosition = 0;
		m_vScrollPosition = 0;
		if (m_canvasProperties.scaleY()) {
			m_renderControl.setScale((xScale < yScale) ? xScale : yScale);
		} else {
			m_renderControl.setScale(xScale);
		}
		updateScrollBars();
	}

	protected void zoomDragBox(Rectangle box) {
		double scale = m_renderControl.getScale();
		
		m_hScrollPosition = (int)((double)m_hScrollPosition + ((double)box.x / scale));

		if (m_canvasProperties.scaleY()) {
			m_vScrollPosition = (int)((double)m_vScrollPosition + ((double)box.y / scale));
		}

		double xScale = (double)m_renderControl.getCanvasWidth() / (double)box.width * scale;
		double yScale = (double)m_renderControl.getCanvasHeight() / (double)box.height * scale;
		if (m_canvasProperties.scaleY()) {
			m_renderControl.setScale((xScale < yScale) ? xScale : yScale);
		} else {
			m_renderControl.setScale(xScale);
		}

		updateScrollBars();
	}

	private void updateScrollPositions() {
		double scale = m_renderControl.getScale();
		m_hScrollPosition = (int)((double)m_hScroll.getSelection() / scale);
		if (m_canvasProperties.scaleY()) {
			m_vScrollPosition = (int)((double)m_vScroll.getSelection() / scale);
		} else {
			m_vScrollPosition = m_vScroll.getSelection();
		}
	}

	private void clearView() {
		if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
			m_toolTip.dispose ();
		}
		m_viewNode = null;
		updateSelection(new ArrayList<Node>());
		m_renderControl.reset();
	}
	
	private void createContextMenus() {
	    Menu contextMenu;
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(this);
		contextMenu = menuMgr.createContextMenu(m_canvas);
		m_canvas.setMenu(contextMenu);
	}
	
	private void setupMouseListeners() {
		m_toolTip = null;
		m_tipLabel = null;

		Listener canvasListener = new Listener() {
			private Rectangle m_selectBox;
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

                case SWT.MouseDown:
                	if (event.button == LEFT_MOUSE_BUTTON) {
                		m_selectBox = new Rectangle(event.x, event.y, 0, 0);
                	}
                	break;
                	
                case SWT.MouseMove:
    	        	if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
    	    			m_toolTip.dispose ();
    	        	}

    	        	if (m_selectBox != null) {
	                	m_selectBox.width = event.x - m_selectBox.x;
	                	m_selectBox.height = event.y - m_selectBox.y;
	                	m_renderControl.setSelectBox(normaliseBox(m_selectBox));
                	}
                	break;
                	
                case SWT.MouseUp:
                	boolean ctrlKeyPressed = (event.stateMask & SWT.CTRL) != 0;
                	
                	if (m_viewNode == null) {
                		// Ensure the selection box gets removed
                		m_renderControl.refresh();
                	} else {
                		if (event.button == LEFT_MOUSE_BUTTON) {
                    		Rectangle box = normaliseBox(m_selectBox);
                			if ((box.width > 10) && (box.height > 10)) {
                				// Only zoom if the user has selected enough
                				zoomDragBox(box);
                			} else {
                				click(event.x, event.y, ctrlKeyPressed);
                			}
                		} else if (event.button == RIGHT_MOUSE_BUTTON) {
            				click(event.x, event.y, ctrlKeyPressed);
                		}
                	}
        			m_selectBox = null;
                	break;
				}
			}
			
			private void click(int x, int y, boolean ctrlKeyPressed) {
				ViewNode clickNode = getClickNode(x, y);
				if (clickNode != null) {
		        	Node newNode = clickNode.getTreeNode();
		        	List<Node> newNodes = new ArrayList<Node>();
		        	
		        	if (ctrlKeyPressed) {
		        		newNodes.addAll(m_selectedNodes);
		        		if (newNodes.contains(newNode)) {
		        			newNodes.remove(newNode);
		        		} else {
		        			newNodes.add(newNode);
		        		}
		        	} else {
		        		newNodes.add(newNode);
		        	}
	        		updateSelection(newNodes);
	            	GuiEngine.setSelectedNodes(newNodes);
				}
			}

			private Rectangle normaliseBox(Rectangle box) {
				if (box == null) {
					return new Rectangle(0, 0, 0, 0);
				}
				
				int x = box.x;
				int width = box.width;
				if (box.width < 0) {
					x = box.x + box.width;
					width = -box.width;
				}

				int y = box.y;
				int height = box.height;
				if (box.height < 0) {
					y = box.y + box.height;
					height = -box.height;
				}
				return new Rectangle(x, y, width, height);
			}
		};

		m_canvas.addListener(SWT.MouseWheel, canvasListener);
		m_canvas.addListener(SWT.KeyDown, canvasListener);
		m_canvas.addListener(SWT.MouseDown, canvasListener);
		m_canvas.addListener(SWT.MouseMove, canvasListener);
		m_canvas.addListener(SWT.MouseUp, canvasListener);

		m_canvas.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				ViewNode clickNode = getClickNode(e.x, e.y);
				if (clickNode != null) {
					clickNode.setFolded(!clickNode.isFolded(), false);
    				m_renderControl.refresh();
    				updateScrollBars();
    			}
			}
	    });
	    
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
				ViewNode clickNode = getClickNode(e.x, e.y);
				if (clickNode != null) {
    	    		if (m_toolTip != null  && !m_toolTip.isDisposed ())
    	    			m_toolTip.dispose ();

    	    		createToolTip(clickNode.getToolTipText(), labelListener);
    			}
		     } 
	    });
	}

	private ViewNode getClickNode(int x, int y) {
    	if (m_viewNode != null) {
			int offsetX = m_renderControl.getOffsetX();
			int offsetY = m_renderControl.getOffsetY();
			return m_viewNode.getClickNode(x + offsetX, y + offsetY);
    	} else {
    		return null;
    	}
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

    protected void initialiseTree() {
		if (DEBUG) System.out.println(m_name + ": initialiseTree");
		Route route = GuiEngine.getEngine().getSelectedRoute();
		if (route != null) {
			updateSelection(GuiEngine.getSelectedNodes());

			GuiRouteProperties routeProperties = GuiEngine.getRouteProperties(route.getRouteId(), getName());
			m_viewNode = routeProperties.getViewNode();
			if (m_viewNode == null) {
				m_viewNode = buildViewNode(route.getNode(), 0);
				routeProperties.setViewNode(m_viewNode);
			}
		} else {
    		m_viewNode = null;
			updateSelection(new ArrayList<Node>());
		}
		m_renderControl.setRoute(route);
		m_renderControl.setViewNode(m_viewNode);

		double scale = m_renderControl.getScale();
		if (scale != 0) {
			m_hScrollPosition = (int)((double)m_renderControl.getOffsetX() / scale);
			m_vScrollPosition = (int)((double)m_renderControl.getOffsetY() / scale);
		}
		updateScrollBars();
	}

	protected void drawCanvas(GC gc) {
		if (DEBUG) System.out.println(m_name + ": drawCanvas");
		m_renderControl.drawCanvasImage(gc);
    }

	private int scale(int value) {
		return m_renderControl.scale(value);
	}
}
