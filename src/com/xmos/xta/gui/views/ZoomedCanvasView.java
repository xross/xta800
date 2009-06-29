package com.xmos.xta.gui.views;

import java.util.ArrayList;
import java.util.List;
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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.gui.Broadcaster;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.views.viewnode.ViewNode;

public abstract class ZoomedCanvasView extends ViewPart implements IPropertyChangeListener,
                                                       ISelectionListener,
                                                       ISelectionProvider,
                                                       IMenuListener {
    public static final boolean DEBUG = false;

    protected Node m_rootNode;
    protected List<Node> m_selectedNodes;
    protected Node m_doubleClickNode;
    
    private boolean m_refreshing;
    protected ArrayList<ViewNode> m_clickAreas;
    
    private Composite m_parent;
    protected Display m_display;
    protected Canvas m_canvas;
    protected ZoomedCanvasProperties m_canvasProperties;
    protected ScrollBar m_hScroll;
    protected ScrollBar m_vScroll;
	protected int m_hScrollPosition;
	protected int m_vScrollPosition;

	protected double m_scale;
	protected boolean m_scaleY;
    
    private Shell m_toolTip;
    private Label m_tipLabel;
    private Vector<ISelectionChangedListener> m_listeners;

	public ZoomedCanvasView() {
		if (DEBUG) System.out.printf("Structure()\n");			
		GuiEngine.addBroadcastListener(this);
	    
		m_listeners = new Vector<ISelectionChangedListener>();
		m_rootNode = null;
		m_doubleClickNode = null;
	    m_selectedNodes = new ArrayList<Node>();

	    m_canvasProperties = new ZoomedCanvasProperties();
		m_refreshing = false;
		m_scale = 1;
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("ZoomedCanvasView: createPartControl\n");
	    m_parent = parent;
	    m_display = m_parent.getDisplay();

	    // Determine average font metrics
        Image image = new Image(m_display, 1, 1);
        GC gc = new GC(image);
        m_canvasProperties.setAverageCharWidth(gc.getFontMetrics().getAverageCharWidth());
        gc.dispose();
        image.dispose();

	    
	    final Composite composite = new Composite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL); 	    
	    FillLayout fillLayout = new FillLayout();
	    composite.setLayout(fillLayout); 

	    m_canvas = new Canvas(composite, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
	    
	    // Sets up the horizontal waves scroll bar.
	    m_hScroll = composite.getHorizontalBar();
	    m_hScroll.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	    		m_hScrollPosition = (int)((double)m_hScroll.getSelection() / m_scale);
	        	refreshView();
	        }
	    });
	    m_hScroll.setMaximum(1);
	    
	    // Sets up the vertical waves scroll bar.
	    m_vScroll = composite.getVerticalBar();
	    m_vScroll.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	    		m_vScrollPosition = (int)((double)m_vScroll.getSelection() / m_scale);
	        	refreshView();
	        }
	    });
	    m_vScroll.setMaximum(1);
	    	    
	    m_canvas.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	drawCanvas(e.gc);
		    }
	    });	

	    m_clickAreas = new ArrayList<ViewNode>();
	    setupMouseListeners();
	    createContextMenus();
	    
	    initialiseTree();
	    nodeCoreChanged();
		annotateTree();
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
			annotateTree();
			refreshView();
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SET_FOCUS)) {
			ISelection sel = GuiEngine.getFocus();
	        if (sel instanceof IStructuredSelection) {	            
	            Object object = ((IStructuredSelection)sel).getFirstElement();
	            if (object instanceof Node) {
	                m_doubleClickNode = (Node)object;
	            }
	        }
			refreshView();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE) || 
				   event.getProperty().equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
			initialiseTree();
			refreshView();
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SELECT_NODES)) {
			refreshView();
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_UPDATE_FOLDING)) {
			annotateTree();
			refreshView();
			
		} 
	}
	
	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		if (part == this)
			return;
		
        if (sel instanceof IStructuredSelection) {
        	IStructuredSelection sSel = (IStructuredSelection)sel;

        	List<Node> newNodes = new ArrayList<Node>();
        	for (Object object : sSel.toArray()) {
        		if (object instanceof Node) {
        			newNodes.add((Node)object);
        		}
            }
        	m_selectedNodes = newNodes;
            refreshView();
        }
    }	            		         	        	

	public void menuAboutToShow(IMenuManager manager) {
		if (m_selectedNodes.size() == 1) {
	    	if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
				m_toolTip.dispose ();
	    	}
	    	Views.createContextMenu(manager, m_selectedNodes, m_parent.getShell());
		}
	}

	//
	// Methods for implementing the ISelectionProvider
	//
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (!m_listeners.contains(listener)) {
			m_listeners.add(listener);
		}
	}

	public ISelection getSelection() {
		return new StructuredSelection(m_selectedNodes);
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
        	m_selectedNodes.clear();
        	for (Object object : ((IStructuredSelection)sel).toArray()) {
        		if (object instanceof Node) {
        			m_selectedNodes.add((Node)object);
        		}
            }
	        refreshView();
        }
        
		for (ISelectionChangedListener listener : m_listeners) {
			listener.selectionChanged(new SelectionChangedEvent(this, sel));
		}
	}

	//
	// Structure view methods 
	//
	protected void refreshView() {
		if (m_refreshing)
			return;
		
		m_display.syncExec(new Runnable() {
			public void run() {
				m_refreshing = true;
				prepareCanvas();
				m_canvas.redraw();
				m_refreshing = false;
			}});
	}

    private void prepareCanvas() {
    	if (m_scale == 0) {
    		zoomFit();
    	}
		Rectangle clientArea = m_canvas.getClientArea();
		int canvasX = clientArea.width;
		int canvasY = clientArea.height;
		
		
		int renderX = (int)((double)m_canvasProperties.getPixelsX() * m_scale);
		int renderY = m_canvasProperties.getPixelsY();
		if (m_scaleY)
			renderY = (int)((double)m_canvasProperties.getPixelsY() * m_scale);

	    m_hScroll.setMaximum(renderX);
    	m_hScroll.setThumb(canvasX);
		m_hScroll.setSelection((int)((double)m_hScrollPosition * m_scale));
	    
	    m_vScroll.setMaximum(renderY);
    	m_vScroll.setThumb(canvasY);
		m_vScroll.setSelection((int)((double)m_vScrollPosition * m_scale));
    	
    	if (DEBUG) System.out.println("Prepared render " + renderX + ", " + renderY + " in canvas " + canvasX + ", " + canvasY);
    }
    
	public void zoomOut() {
		if (m_doubleClickNode == null)
			return;

		m_hScrollPosition = (int)((double)m_hScroll.getSelection() / m_scale);
		m_vScrollPosition = (int)((double)m_vScroll.getSelection() / m_scale);
		m_scale = m_scale / 1.2;
		if (DEBUG) System.out.printf("ZoomedCanvasView: zoomOut %f\n", m_scale);
		refreshView();
	}

	public void zoomIn() {
		if (m_doubleClickNode == null)
			return;
		
		// Don't zoom any more than the maximum width
		if (m_scale < 5) {
			m_hScrollPosition = (int)((double)m_hScroll.getSelection() / m_scale);
			m_vScrollPosition = (int)((double)m_vScroll.getSelection() / m_scale);
			m_scale = m_scale * 1.2;
			if (DEBUG) System.out.printf("ZoomedCanvasView: zoomIn %f\n", m_scale);
			refreshView();
		}
	}

	public void zoomReset() {
		m_hScrollPosition = (int)((double)m_hScroll.getSelection() / m_scale);
		m_vScrollPosition = (int)((double)m_vScroll.getSelection() / m_scale);
		m_scale = 1.0;

		if (DEBUG) System.out.printf("ZoomedCanvasView: zoomReset %f\n", m_scale);
		refreshView();
	}

	public void zoomFit() {
		double xScale = (double)m_canvas.getClientArea().width / (double)m_canvasProperties.getPixelsX();
		double yScale = (double)m_canvas.getClientArea().height / (double)m_canvasProperties.getPixelsY();

		if (m_scaleY) {
			m_scale = (xScale < yScale) ? xScale : yScale;
		} else {
			m_scale = xScale;
		}

		if (DEBUG) System.out.printf("ZoomedCanvasView: zoomFit %f\n", m_scale);
		refreshView();
	}

	private void clearTree() {
		if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
			m_toolTip.dispose ();
		}
		m_rootNode = null;
		m_doubleClickNode = null;
		m_selectedNodes.clear();
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
	    				node.setFolded(!node.isFolded(), false);
	    				annotateTree();

	    	    		m_selectedNodes.clear();
	    	    		m_selectedNodes.add(node.getTreeNode());
	    	    		ISelection selection = new StructuredSelection(m_selectedNodes);
	    	    		setSelection(selection);
	    				GuiEngine.setFocus(selection);
	    				GuiEngine.broadcast(Broadcaster.BROADCAST_SET_FOCUS, "");
	    				return;
	    			}
	    		}
			}
	    	public void mouseDown(MouseEvent e) {
    			if ((e.button == 1) || (e.button == 3)) {
		    		for (ViewNode node : m_clickAreas) {
		    			if (node.contains(e.x, e.y)) {
		    				if (node.getTreeNode() != null) {
			    	    		m_selectedNodes.clear();
			    	    		m_selectedNodes.add(node.getTreeNode());
		    					setSelection(new StructuredSelection(m_selectedNodes));
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
    protected abstract void annotateTree();
    protected abstract void updatePixelsXy();
}

