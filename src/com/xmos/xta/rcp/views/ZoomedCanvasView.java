package com.xmos.xta.rcp.views;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.XtaRouteInfo;
import com.xmos.xta.rcp.nodes.Node;
import com.xmos.xta.rcp.views.ViewNode.ViewNode;

public abstract class ZoomedCanvasView extends ViewPart implements IPropertyChangeListener,
                                                       ISelectionListener,
                                                       ISelectionProvider,
                                                       IMenuListener {
    public static final boolean DEBUG = false;
    public static final int MIN_CANVAS_X = 100;
    public static final int MAX_CANVAS_X = 3000;
    public static final int MAX_CANVAS_Y = 500;

    protected Node m_rootNode;
    protected Node m_selectedNode;
    protected Node m_doubleClickNode;
    protected XtaRouteInfo m_routeInfo;
    
    private boolean m_refreshing;
    protected double m_scale;
    protected boolean m_scaleY;
    protected int m_lineWidth;
    protected int m_pixelsX;
    protected int m_pixelsY;
    protected int m_borderX;
    protected int m_borderY;
    protected ArrayList<ViewNode> m_clickAreas;
    
    private Composite m_parent;
    protected Display m_display;
    protected ScrolledComposite m_scrollArea;
    protected Canvas m_canvas;
    
    private Shell m_toolTip;
    private Label m_tipLabel;
    private Vector<ISelectionChangedListener> m_listeners;

	public ZoomedCanvasView() {
		if (DEBUG) System.out.printf("Structure()\n");			
		XtaNotifier.getInstance().addPropertyChangeListener(this);
	    
		m_listeners = new Vector<ISelectionChangedListener>();
		m_rootNode = null;
		m_doubleClickNode = null;
	    m_selectedNode = null;

		m_scale = 1.0;
		m_refreshing = false;
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("ZoomedCanvasView: createPartControl\n");
	    m_parent = parent;
	    m_display = m_parent.getDisplay();

	    m_scrollArea = new ScrolledComposite(m_parent,
	    		SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL); 
	    m_canvas = new Canvas(m_scrollArea, SWT.NO_BACKGROUND); 

	    m_scrollArea.setAlwaysShowScrollBars(true); 
	    m_scrollArea.setContent(m_canvas); 
	    
	    m_scrollArea.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	prepareCanvas();
	        }
	    });

	    m_canvas.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	drawCanvas(e.gc);
 	        }
	    });
	    
	    m_clickAreas = new ArrayList<ViewNode>();
	    setupMouseListeners();
	    createContextMenus();
	    
	    initialiseTree();
		annotateTree(false);
		refreshView();

		getSite().getPage().addSelectionListener(this);
		getSite().setSelectionProvider(this);
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("ZoomedCanvasView: setFocus\n");
		refreshView();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("ZoomedCanvasView: propertyChange: %s\n", event.getProperty());			

		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
	    	if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
				m_toolTip.dispose ();
	    	}
	    	initialiseTree();
			annotateTree(false);
			refreshView();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE) || 
				   event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			clearTree();
			nodeCoreChanged();
			refreshView();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE)) {
	    	if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
				m_toolTip.dispose ();
	    	}
			annotateTree(false);
			refreshView();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_SET_FOCUS)) {
			ISelection sel = XtaNotifier.getInstance().getFocusSelection();
	        if (sel instanceof IStructuredSelection) {	            
	            Object object = ((IStructuredSelection)sel).getFirstElement();
	            if (object instanceof Node) {
	                m_doubleClickNode = (Node)object;
	            }
	        }
			annotateTree(false);
			refreshView();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_EXCLUDES) || 
				   event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE) || 
				   event.getProperty().equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
			initialiseTree();
			annotateTree(false);
			refreshView();
			
		}
	}
	
	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		if (part == this)
			return;
		
        if (sel instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)sel).getFirstElement();
            if (object instanceof Node) {
            	
            	boolean selectionChanged = true;
            	if (m_selectedNode == null ||
            			(m_selectedNode.getPathRoot() != ((Node)object).getPathRoot())) {
            		selectionChanged = false; // Cause zoom to fit
            	}
            		
                m_selectedNode = (Node)object;
                annotateTree(selectionChanged);
                refreshView();
            }
        }
    }	            		         	        	

	public void menuAboutToShow(IMenuManager manager) {
		if (m_selectedNode != null) {
	    	if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
				m_toolTip.dispose ();
	    	}
			m_selectedNode.createContextMenu(m_parent.getShell(), manager);
		}
	}

	/*
	 * Methods for implementing the ISelectionProvider
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (!m_listeners.contains(listener)) {
			m_listeners.add(listener);
		}
	}

	public ISelection getSelection() {
		if (m_selectedNode == null) {
			return null;
		} else {
			return new StructuredSelection(m_selectedNode);
		}
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		if (m_listeners.contains(listener)) {
			m_listeners.remove(listener);
		}
	}

	public void setSelection(ISelection sel) {
		if (sel == null)
			return;

        if (sel instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)sel).getFirstElement();
            if (object instanceof Node) {
		        m_selectedNode = (Node)object;
		        annotateTree(true);
		        refreshView();
            }
        }
        
		for (ISelectionChangedListener listener : m_listeners) {
			listener.selectionChanged(new SelectionChangedEvent(this, sel));
		}
	}

	/*
	 * Structure view methods 
	 */
	protected void refreshView() {
		if (m_refreshing)
			return;
		
		m_display.syncExec(new Runnable() {
			public void run() {
				m_refreshing = true;
				prepareCanvas();
				m_scrollArea.redraw();
				m_canvas.redraw();
				m_refreshing = false;
			}});
	}

    private void prepareCanvas() {
    	if (m_scale == 0) {
    		zoomFit();
    	}

    	Rectangle clientArea = m_scrollArea.getClientArea();
		int renderX = (int)((double)m_pixelsX * m_scale);
		int renderY = m_pixelsY;
		if (m_scaleY)
			renderY = (int)((double)m_pixelsY * m_scale);
		
		int x = (clientArea.width > renderX) ? clientArea.width : renderX;
		int y = (clientArea.height > renderY) ? clientArea.height : renderY;
		m_canvas.setSize(x, y);

		if (DEBUG) {
			System.out.printf("ZoomedCanvasView: prepareCanvas %d,%d (%d, %d) %f\n",
				x, y, clientArea.width, clientArea.height, m_scale);
		}
    }
    
	public void zoomOut() {
		if (m_selectedNode == null)
			return;

		// Don't zoom too far out
		if ((int)((double)m_pixelsX * m_scale) > MIN_CANVAS_X) {
			m_scale = m_scale / 1.2;
			refreshView();
		}
	}

	public void zoomIn() {
		if (m_selectedNode == null)
			return;
		
		// Don't zoom any more than the maximum width
		if ((int)((double)m_pixelsX * m_scale) < MAX_CANVAS_X) {
			if (!m_scaleY || ((int)((double)m_pixelsY * m_scale) < MAX_CANVAS_Y)) {
				m_scale = m_scale * 1.2;
				refreshView();
			}
		}
	}

	public void zoomReset() {
		// Don't zoom any more than the maximum width
		if ((m_pixelsX < MAX_CANVAS_X) &&
			(m_pixelsY < MAX_CANVAS_Y)) {
			m_scale = 1.0;
		} else {
			// Ensure both dimensions are within the desired range
			double xScale = (double)MAX_CANVAS_X / (double)m_pixelsX;
			double yScale = (double)MAX_CANVAS_Y / (double)m_pixelsY;
			m_scale = (xScale < yScale) ? xScale : yScale;
		}

		refreshView();
	}

	public void zoomFit() {
		double xScale = (double)m_scrollArea.getClientArea().width / (double)m_pixelsX;
		double yScale = (double)m_scrollArea.getClientArea().height / (double)m_pixelsY;

		if (m_scaleY) {
			m_scale = (xScale < yScale) ? xScale : yScale;
		} else {
			m_scale = xScale;
		}

		if (DEBUG) {
			System.out.printf("ZoomedCanvasView: zoomFit %f (%d, %d)\n",
				m_scale, m_parent.getBounds().width, m_parent.getBounds().height);
		}

		refreshView();
	}

	private void clearTree() {
		if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
			m_toolTip.dispose ();
		}
		m_rootNode = null;
		m_doubleClickNode = null;
		m_selectedNode = null;
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

		final Listener labelListener = new Listener() {
			public void handleEvent (Event e) {
				Label label = (Label)e.widget;
				Shell shell = label.getShell();
				switch (e.type) {
					case SWT.MouseDown:
						shell.dispose();
						break;
					case SWT.MouseExit:
						shell.dispose();
						break;
				}
			}
		};
		
		Listener canvasListener = new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
                case SWT.MouseWheel:
                	if (event.count > 0)
                		zoomIn();
                	else
                		zoomOut();
                    break;
                case SWT.KeyDown:
                	if (event.character == '+')
                		zoomIn();
                	else if (event.character == '-')
                		zoomOut();
                	break;
				}
			}
		};

		m_canvas.addListener(SWT.MouseWheel, canvasListener);
		m_canvas.addListener(SWT.KeyDown, canvasListener);
		
		m_canvas.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
	    		for (ViewNode node : m_clickAreas) {
	    			if (node.contains(e.x, e.y)) {
	    				if (node.getTreeNode() != null) {
		    	    		m_selectedNode = node.getTreeNode();
		    	    		ISelection selection = new StructuredSelection(m_selectedNode);
		    	    		setSelection(selection);
		    				XtaNotifier.getInstance().notifySetFocus(selection);
	    				}
	    				return;
	    			}
	    		}
			}
	    	public void mouseDown(MouseEvent e) {
    			if ((e.button == 1) || (e.button == 3)) {
		    		for (ViewNode node : m_clickAreas) {
		    			if (node.contains(e.x, e.y)) {
		    				if (node.getTreeNode() != null) {
		    					m_selectedNode = node.getTreeNode();
		    					setSelection(new StructuredSelection(m_selectedNode));
		    				}
		    				return;
		    			}
		    		}
    			}
	    	} 
	    });
	    
	    m_canvas.addMouseMoveListener(new MouseMoveListener() {
	        public void mouseMove(MouseEvent e) {
	        	if (m_toolTip != null  && !m_toolTip.isDisposed ())
	    			m_toolTip.dispose ();
	        }
	      });
	    
	    m_canvas.addMouseTrackListener(new MouseTrackListener() { 
		     public void mouseEnter(MouseEvent e) { 
		     }  
		     public void mouseExit(MouseEvent e) {
		     }
		     public void mouseHover(MouseEvent e) { 
		    		for (ViewNode node : m_clickAreas) {
		    			if (node.contains(e.x, e.y)) {
		    	    		if (m_toolTip != null  && !m_toolTip.isDisposed ())
		    	    			m_toolTip.dispose ();

		    	    		createToolTip(node.getToolTipText(), labelListener);
		    	    		return;
		    			}
		    		}
		     } 
	    });
	}

	private void createToolTip(String toolTip, Listener labelListener) {
		m_toolTip = new Shell(m_display, SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
		m_toolTip.setBackground (m_display.getSystemColor (SWT.COLOR_INFO_BACKGROUND));
		FillLayout layout = new FillLayout ();
		layout.marginWidth = 2;
		m_toolTip.setLayout (layout);
		m_tipLabel = new Label (m_toolTip, SWT.NONE);
		m_tipLabel.setForeground (m_display.getSystemColor (SWT.COLOR_INFO_FOREGROUND));
		m_tipLabel.setBackground (m_display.getSystemColor (SWT.COLOR_INFO_BACKGROUND));
		m_tipLabel.setText(toolTip);
		m_tipLabel.addListener(SWT.MouseExit, labelListener);
		m_tipLabel.addListener(SWT.MouseDown, labelListener);
		Point size = m_toolTip.computeSize (SWT.DEFAULT, SWT.DEFAULT);
		Point pt = Display.getCurrent().getCursorLocation();

		// Display slightly to the right of the cursor in order to not have it behind cursor
		m_toolTip.setBounds(pt.x + 10, pt.y, size.x, size.y);
		m_toolTip.setVisible(true);
	}

	protected abstract void nodeCoreChanged();
	protected abstract void initialiseTree();
    protected abstract void drawCanvas(GC gc);    
    protected abstract void buildClickAreas();
    protected abstract void annotateTree(boolean isSelctionChange);
    protected abstract void updatePixelsXy();
}
