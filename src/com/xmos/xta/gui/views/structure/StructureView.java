package com.xmos.xta.gui.views.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
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

import com.xmos.xta.engine.analysis.routes.Route;
import com.xmos.xta.engine.analysis.structural.nodes.Node;
import com.xmos.xta.engine.analysis.structural.nodes.Node.Case;
import com.xmos.xta.gui.GuiEngine;
import com.xmos.xta.gui.views.Views;
import com.xmos.xta.gui.views.viewnode.StructureBlockNode;
import com.xmos.xta.gui.views.viewnode.StructureFunctionNode;
import com.xmos.xta.gui.views.viewnode.StructureLoopNode;
import com.xmos.xta.gui.views.viewnode.StructurePathNode;
import com.xmos.xta.gui.views.viewnode.StructureSectionNode;
import com.xmos.xta.gui.views.viewnode.ViewNode;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.gui.Broadcaster;

public class StructureView extends ViewPart implements IPropertyChangeListener,
                                                       ISelectionListener,
                                                       ISelectionProvider,
                                                       IMenuListener {
    public static final boolean DEBUG = true;
    public static final String ID = "com.xmos.xta.gui.views.structure.Structure";

    private static final int MAX_IMAGE_SIZE = 1024 * 1024 * 20;

    private int m_borderX = 2;
    private int m_borderY = 2;

    private ViewNode m_viewNode;

    private Node m_rootNode;
    private List<Node> m_selectedNodes;
    private Node m_doubleClickNode;
    
    private boolean m_refreshing;
    
    private Composite m_parent;
    private Display m_display;
    private ScrollBar m_hScroll;
    private ScrollBar m_vScroll;

    private ZoomedCanvasProperties m_canvasProperties;
    private Canvas m_structureCanvas;
    private Image m_structureImage;

    private Canvas m_timesCanvas;
    private Image m_timesImage;

    private int m_hScrollPosition;
    private int m_vScrollPosition;

    private double m_scale;
	
    private Shell m_toolTip;
    private Label m_tipLabel;
    private Vector<ISelectionChangedListener> m_listeners;

	public StructureView() {
		if (DEBUG) System.out.printf("Structure()\n");			
		GuiEngine.addBroadcastListener(this);
	    
		m_listeners = new Vector<ISelectionChangedListener>();
		m_rootNode = null;
		m_doubleClickNode = null;
	    m_selectedNodes = new ArrayList<Node>();

	    m_canvasProperties = new ZoomedCanvasProperties();
   
		m_refreshing = false;
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("ZoomedCanvasView: createPartControl\n");
	    m_parent = parent;
	    m_display = m_parent.getDisplay();

	    // Dimensions must be minimum of 1
		m_structureImage = new Image(m_display, 1, 1);
		m_timesImage = new Image(m_display, 1, 1);

	    // Determine font metrics
        GC gc = new GC(m_structureImage);
        m_canvasProperties.setAverageCharWidth(gc.getFontMetrics().getAverageCharWidth());
        gc.dispose();
	    
	    final Composite composite = new Composite(parent, SWT.BORDER | SWT.H_SCROLL); 	    
	    composite.setLayout(new FillLayout()); 

	    SashForm sashForm = new SashForm(composite, SWT.VERTICAL);
		sashForm.setLayout(new FillLayout());	

	    final Composite structureComposite = new Composite(sashForm, SWT.BORDER | SWT.V_SCROLL); 	    
	    structureComposite.setLayout(new FillLayout()); 
	    m_structureCanvas = new Canvas(structureComposite, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
	    structureComposite.addControlListener(new ControlListener() {
			public void controlResized(ControlEvent e) {
				Rectangle clientArea = structureComposite.getClientArea();
				m_structureCanvas.setSize(clientArea.width, clientArea.height);
		    	resizeImages();
				m_structureCanvas.redraw();
			}
			public void controlMoved(ControlEvent e) {
				Rectangle clientArea = structureComposite.getClientArea();
				m_structureCanvas.setSize(clientArea.width, clientArea.height);
		    	resizeImages();
				m_structureCanvas.redraw();
			}
	    });	    

	    final Composite timesComposite = new Composite(sashForm, SWT.BORDER); 	    
	    timesComposite.setLayout(new FillLayout()); 
	    m_timesCanvas = new Canvas(timesComposite, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
	    timesComposite.addControlListener(new ControlListener() {
			public void controlResized(ControlEvent e) {
				Rectangle clientArea = timesComposite.getClientArea();
				m_timesCanvas.setSize(clientArea.width, clientArea.height);
		    	resizeImages();
				m_timesCanvas.redraw();
			}
			public void controlMoved(ControlEvent e) {
				Rectangle clientArea = timesComposite.getClientArea();
				m_timesCanvas.setSize(clientArea.width, clientArea.height);
		    	resizeImages();
				m_timesCanvas.redraw();
			}	    	
	    });	    

		sashForm.setWeights(new int[]{3, 1});

	    // Sets up the horizontal scroll bar.
	    m_hScroll = composite.getHorizontalBar();
	    m_hScroll.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	    		m_hScrollPosition = m_hScroll.getSelection();
	    		updateCanvasProperties();
	    		refreshView();
	        }
	    });
	    m_hScroll.setMaximum(1);
	    
	    // Sets up the vertical scroll bar.
	    m_vScroll = structureComposite.getVerticalBar();
	    m_vScroll.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	    		m_vScrollPosition = m_vScroll.getSelection();
	    		updateCanvasProperties();
	    		refreshView();
	        }
	    });
	    m_vScroll.setMaximum(1);
	    	    
	    structureComposite.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	m_structureCanvas.redraw();
		    }
	    });

	    m_structureCanvas.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	drawStructureCanvas(e.gc);
		    }
	    });	

	    timesComposite.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	m_timesCanvas.redraw();
		    }
	    });	

	    m_timesCanvas.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	drawTimesCanvas(e.gc);
		    }
	    });	

	    setupMouseListeners();
	    createContextMenus();
	    
	    initialiseTree();
		setScale(1);
		resizeNodes();
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
	    	resizeNodes();
	    	resizeImages();
			refreshView();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE) || 
				   event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			clearView();
			resizeImages();
			refreshView();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE)) {
	    	if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
				m_toolTip.dispose ();
	    	}
	    	updateState();
	        renderImages();
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
	    	resizeNodes();
	    	resizeImages();
	    	refreshView();
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SELECT_NODES)) {
			refreshView();
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_UPDATE_FOLDING)) {
			resizeNodes();
			refreshView();
			
		} 
	}
	
	private void updateState() {
		if (m_viewNode != null) {
	    	Node treeNode = m_viewNode.getTreeNode();
			m_canvasProperties.setTotalBest(treeNode.getAttributes().getTimeTaken(Case.BEST, false));
	    	m_canvasProperties.setTotalWorst(treeNode.getAttributes().getTimeTaken(Case.WORST, false));
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
        	updateSelection(newNodes);
        }
    }	            		         	        	

	private void updateSelection(List<Node> newNodes) {
    	List<Node> wasSelected = m_selectedNodes;
    	m_selectedNodes = newNodes;

    	if (selectionChanged(wasSelected, m_selectedNodes)) {
    		m_canvasProperties.setSelected(m_selectedNodes);
            GC structureGc = new GC(m_structureImage);
            GC timesGc = new GC(m_timesImage);
            if (m_viewNode != null) {
            	if (wasSelected.size() > 0)
            		m_viewNode.redraw(m_display, structureGc, timesGc, wasSelected);
            	if (m_selectedNodes.size() > 0)
            		m_viewNode.redraw(m_display, structureGc, timesGc, m_selectedNodes);
            }
            structureGc.dispose();
            timesGc.dispose();
    		refreshView();
    	}
	}

	private boolean selectionChanged(List<Node> oldList, List<Node> newList) {
		if (oldList.size() != newList.size())
			return true;
		
		for (Node node : oldList) {
			if (!newList.contains(node))
				return true;
		}
		return false;
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
        	List<Node> newNodes = new ArrayList<Node>();
        	for (Object object : ((IStructuredSelection)sel).toArray()) {
        		if (object instanceof Node) {
        			newNodes.add((Node)object);
        		}
            }
        	updateSelection(newNodes);
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
				m_structureCanvas.redraw();
				m_timesCanvas.redraw();
				m_refreshing = false;
			}});
	}

    private void updateCanvasProperties() {
		if (m_viewNode != null) {
			Rectangle canvas = m_structureCanvas.getClientArea();
        	Rectangle visibleArea = new Rectangle(m_hScrollPosition, m_vScrollPosition, canvas.width, canvas.height);
        	m_canvasProperties.setVisibleArea(visibleArea);
        	m_canvasProperties.setTimesHeight(m_timesCanvas.getClientArea().height);
		} else {
        	m_canvasProperties.setVisibleArea(new Rectangle(0, 0, 0, 0));
        	m_canvasProperties.setTimesHeight(0);
		}
    }
    
	private void updateScrollBars() {
    	Rectangle clientArea = m_structureCanvas.getClientArea();
		int canvasX = clientArea.width;
		int canvasY = clientArea.height;
		
		int renderX = (int)((double)getPixelsX() * m_scale);
		int renderY = (int)((double)getPixelsY() * m_scale);

		m_hScroll.setMaximum(renderX);
    	m_hScroll.setThumb(canvasX);
    	if (m_hScrollPosition > (renderX - canvasX))
    		m_hScrollPosition = renderX - canvasX;
    	if (m_hScrollPosition < 0)
    		m_hScrollPosition = 0;
		m_hScroll.setSelection(m_hScrollPosition);
	    
	    m_vScroll.setMaximum(renderY);
    	m_vScroll.setThumb(canvasY);
    	if (m_vScrollPosition > (renderY - canvasY))
    		m_vScrollPosition = renderY - canvasY;
    	if (m_vScrollPosition < 0)
    		m_vScrollPosition = 0;
		m_vScroll.setSelection(m_vScrollPosition);
	}

	private void setScale(double scale) {
		int renderX = (int)((double)getPixelsX() * m_scale);
		int renderY = (int)((double)getPixelsY() * m_scale);
		boolean scaledDown = false;
		while ((renderX * renderY) > MAX_IMAGE_SIZE) {
			scale /= 1.2;
			renderX = (int)((double)getPixelsX() * m_scale);
			renderY = (int)((double)getPixelsY() * m_scale);
			scaledDown = true;
		}
		if (scaledDown) {
			System.out.println("Image too large");
		}
		
		m_scale = scale;
		m_canvasProperties.setScale(scale);
    	resizeImages();
		refreshView();
	}

	public void zoomOut() {
		if (m_viewNode == null)
			return;

		m_hScrollPosition = m_hScroll.getSelection();
		m_vScrollPosition = m_vScroll.getSelection();
		setScale(m_scale / 1.2);
	}

	public void zoomIn() {
		if (m_viewNode == null)
			return;
		
		// Don't zoom any more than the maximum width
		if (m_scale < 5) {
			m_hScrollPosition = m_hScroll.getSelection();
			m_vScrollPosition = m_vScroll.getSelection();
			setScale(m_scale * 1.2);
		}
	}

	public void zoomReset() {
		m_hScrollPosition = 0;
		m_vScrollPosition = 0;
		setScale(1.0);
	}

	public void zoomFit() {
		Rectangle clientArea = m_structureCanvas.getClientArea();
		double xScale = (double)clientArea.width / (double)getPixelsX();
		double yScale = (double)clientArea.height / (double)getPixelsY();
		m_hScrollPosition = 0;
		m_vScrollPosition = 0;
		setScale((xScale < yScale) ? xScale : yScale);
	}

	private void clearView() {
		if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
			m_toolTip.dispose ();
		}
		m_viewNode = null;
		m_rootNode = null;
		m_doubleClickNode = null;
		m_selectedNodes.clear();

		m_hScrollPosition = 0;
		m_vScrollPosition = 0;
	}
	
	private void createContextMenus() {
	    Menu contextMenu;
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(this);
		contextMenu = menuMgr.createContextMenu(m_structureCanvas);
		m_structureCanvas.setMenu(contextMenu);
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

		m_structureCanvas.addListener(SWT.MouseWheel, canvasListener);
		m_structureCanvas.addListener(SWT.KeyDown, canvasListener);
		
		m_structureCanvas.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				if (m_viewNode != null) {
					ViewNode clickNode = m_viewNode.getClickNode(e.x + m_hScrollPosition, e.y + m_vScrollPosition);
					if (clickNode != null) {
						clickNode.setFolded(!clickNode.isFolded(), false);
	    				resizeNodes();

	    	        	List<Node> newNodes = new ArrayList<Node>();
	    	        	newNodes.add(clickNode.getTreeNode());
	    	    		ISelection selection = new StructuredSelection(newNodes);
	    	    		setSelection(selection);
	    				GuiEngine.setFocus(selection);
	    				GuiEngine.broadcast(Broadcaster.BROADCAST_SET_FOCUS, "");
	    			}
	    		}
			}
	    	public void mouseDown(MouseEvent e) {
    			if ((e.button == 1) || (e.button == 3)) {
    				if (m_viewNode != null) {
    					ViewNode clickNode = m_viewNode.getClickNode(e.x + m_hScrollPosition, e.y + m_vScrollPosition);
    					if (clickNode != null) {
    	    	        	List<Node> newNodes = new ArrayList<Node>();
    	    	        	newNodes.add(clickNode.getTreeNode());
	    					setSelection(new StructuredSelection(newNodes));
		    			}
		    		}
    			}
	    	} 
	    });
	    
	    m_structureCanvas.addMouseMoveListener(new MouseMoveListener() {
	        public void mouseMove(MouseEvent e) {
	        	if (m_toolTip != null  && !m_toolTip.isDisposed ())
	    			m_toolTip.dispose ();
	        }
	      });
	    
	    m_structureCanvas.addMouseTrackListener(new MouseTrackListener() { 
		     public void mouseEnter(MouseEvent e) { 
		     }  
		     public void mouseExit(MouseEvent e) {
		     }
		     public void mouseHover(MouseEvent e) { 
 				if (m_viewNode != null) {
					ViewNode clickNode = m_viewNode.getClickNode(e.x + m_hScrollPosition, e.y + m_vScrollPosition);
					if (clickNode != null) {
	    	    		if (m_toolTip != null  && !m_toolTip.isDisposed ())
	    	    			m_toolTip.dispose ();

	    	    		createToolTip(clickNode.getToolTipText(), labelListener);
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

    
    protected void resizeNodes() {
		if (m_viewNode != null) {
			m_viewNode.annotateXy(m_borderX, m_borderY);
			resizeImages();
		}
    }

	private ViewNode buildViewNode(Node node, int depth) {
		if (node == null || node.isNullNode())
			return null;
		
    	ViewNode viewNode = null;
    	
    	if (node.isBlockNode()) {
    		viewNode = new StructureBlockNode(node, m_canvasProperties);
    	} else if (node.isFunctionNode()) {
    		viewNode = new StructureFunctionNode(node, m_canvasProperties);
    		if (depth > 3)
    			viewNode.setFolded(true, false);
    	} else if (node.isLoopNode()) {
    		viewNode = new StructureLoopNode(node, m_canvasProperties);
    	} else if (node.isSectionNode()) {
    		viewNode = new StructureSectionNode(node, m_canvasProperties);
    	} else {
    		viewNode = new StructurePathNode(node, m_canvasProperties);
    	}
    	
    	if (!node.isBlockNode()) {
	        for (Node subNode : node.getChildren()) {
	        	ViewNode child = buildViewNode(subNode, depth + 1);
	        	if (child != null)
	        		viewNode.addChild(child);
	        }
    	}

    	return viewNode;
	}

    protected void initialiseTree() {
		if (DEBUG) System.out.printf("Structure: initialiseTree\n");
		Route route = GuiEngine.getEngine().getSelectedRoute();
		if (route != null) {
			m_rootNode = route.getNode();
			m_doubleClickNode = m_rootNode;
			m_selectedNodes = GuiEngine.getSelectedNodes();
	    	m_canvasProperties.setSelected(m_selectedNodes);

			m_viewNode = buildViewNode(m_doubleClickNode, 0);
			updateState();
		} else {
			m_rootNode = null;
			m_doubleClickNode = null;
			m_viewNode = null;
			m_selectedNodes.clear();
		}
	}

	private int getPixelsX() {
    	int pixelsX = (2 * m_borderX);
    	if (m_viewNode != null) {
    		pixelsX += m_viewNode.getPixelsX();
    	}
		return pixelsX;
	}

	private int getPixelsY() {
    	int pixelsY = (2 * m_borderY);
    	if (m_viewNode != null) {
    		pixelsY += m_viewNode.getPixelsY();
    	}
		return pixelsY;
	}

    protected void resizeImages() {
		int imageX = (int)((double)getPixelsX() * m_scale);
		int imageY = (int)((double)getPixelsY() * m_scale);

    	Rectangle clientArea = m_structureCanvas.getClientArea();
		int structureCanvasX = clientArea.width;
    	int structureCanvasY = clientArea.height;
    	int timesCanvasY = m_timesCanvas.getClientArea().height;

		if (structureCanvasX > imageX)
			imageX = structureCanvasX;
		if (structureCanvasY > imageY)
			imageY = structureCanvasY;
    	
		m_structureImage.dispose();
		if (DEBUG) System.out.println("Resize m_structureImage " + imageX + ", " + imageY);
		m_structureImage = new Image(m_display, imageX, imageY);

		m_timesImage.dispose();
		if (timesCanvasY == 0) timesCanvasY = 1;
		if (DEBUG) System.out.println("Resize m_timesImage " + imageX + ", " + timesCanvasY);
		m_timesImage = new Image(m_display, imageX, timesCanvasY);

    	updateCanvasProperties();
		updateScrollBars();

		if (m_viewNode != null) {
    		m_viewNode.updateScaledXy();
    	}
		renderImages();
    }
    
	private void renderImages() {
    	// Draw the structure
        GC gc = new GC(m_structureImage);
        gc.setAntialias(SWT.ON);
        gc.setTextAntialias(SWT.ON);
        gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        Rectangle imageBounds = m_structureImage.getBounds();
		gc.fillRectangle(0, 0, imageBounds.width, imageBounds.height);

        if (m_viewNode != null) {
			m_viewNode.draw(m_display, gc);
        }
        gc.dispose();

		// Draw the time
        gc = new GC(m_timesImage);
        gc.setAntialias(SWT.ON);
        gc.setTextAntialias(SWT.ON);
        imageBounds = m_timesImage.getBounds();
		gc.fillRectangle(0, 0, imageBounds.width, imageBounds.height);

        if (m_viewNode != null) {
			m_viewNode.drawTimes(m_display, gc);
        }
        gc.dispose();
	}

    protected void drawStructureCanvas(GC gc) {
		Rectangle visibleArea = m_canvasProperties.getVisibleArea();
    	gc.drawImage(m_structureImage, visibleArea.x, visibleArea.y, visibleArea.width, visibleArea.height,
    			0, 0, visibleArea.width, visibleArea.height);
    }

    protected void drawTimesCanvas(GC gc) {
		Rectangle visibleArea = m_canvasProperties.getVisibleArea();
		int height = m_canvasProperties.getTimesHeight();
    	gc.drawImage(m_timesImage, visibleArea.x, 0, visibleArea.width, height,
    			0, 0, visibleArea.width, height);
    }
}
