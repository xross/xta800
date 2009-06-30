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
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.gui.views.structure.Structure";

    private ViewNode m_viewNode;

    protected Node m_rootNode;
    protected List<Node> m_selectedNodes;
    protected Node m_doubleClickNode;
    
    private boolean m_refreshing;
    protected ArrayList<ViewNode> m_clickAreas;
    
    private Composite m_parent;
    protected Display m_display;
    protected ScrollBar m_hScroll;
    protected ScrollBar m_vScroll;

    protected ZoomedCanvasProperties m_canvasProperties;
    protected Canvas m_structureCanvas;

    protected Canvas m_timesCanvas;

    protected int m_hScrollPosition;
	protected int m_vScrollPosition;

	protected double m_scale;
	
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

	    
	    final Composite composite = new Composite(parent, SWT.BORDER | SWT.H_SCROLL); 	    
	    composite.setLayout(new FillLayout()); 

	    SashForm sashForm = new SashForm(composite, SWT.VERTICAL);
		sashForm.setLayout(new FillLayout());	

	    final Composite structureComposite = new Composite(sashForm, SWT.BORDER | SWT.V_SCROLL); 	    
	    structureComposite.setLayout(new FillLayout()); 
	    m_structureCanvas = new Canvas(structureComposite, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
	    structureComposite.addControlListener(new ControlListener() {
			public void controlResized(ControlEvent e) {
			}
			public void controlMoved(ControlEvent e) {
				Rectangle clientArea = structureComposite.getClientArea();
				m_structureCanvas.setSize(clientArea.width, clientArea.height);
	        	refreshView();
			}	    	
	    });	    

	    final Composite timesComposite = new Composite(sashForm, SWT.BORDER); 	    
	    timesComposite.setLayout(new FillLayout()); 
	    m_timesCanvas = new Canvas(timesComposite, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
	    timesComposite.addControlListener(new ControlListener() {
			public void controlResized(ControlEvent e) {
			}
			public void controlMoved(ControlEvent e) {
				Rectangle clientArea = timesComposite.getClientArea();
				m_timesCanvas.setSize(clientArea.width, clientArea.height);
	        	refreshView();
			}	    	
	    });	    

		sashForm.setWeights(new int[]{3, 1});

	    // Sets up the horizontal scroll bar.
	    m_hScroll = composite.getHorizontalBar();
	    m_hScroll.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	    		m_hScrollPosition = (int)((double)m_hScroll.getSelection() / m_scale);
	        	refreshView();
	        }
	    });
	    m_hScroll.setMaximum(1);
	    
	    // Sets up the vertical scroll bar.
	    m_vScroll = structureComposite.getVerticalBar();
	    m_vScroll.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	    		m_vScrollPosition = (int)((double)m_vScroll.getSelection() / m_scale);
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
				m_structureCanvas.redraw();
				m_timesCanvas.redraw();
				m_refreshing = false;
			}});
	}

    private void prepareCanvas() {
    	if (m_scale == 0) {
    		zoomFit();
    	}
		Rectangle clientArea = m_structureCanvas.getClientArea();
		int canvasX = clientArea.width;
		int canvasY = clientArea.height;
		
		int renderX = (int)((double)m_canvasProperties.getPixelsX() * m_scale);
		int renderY = (int)((double)m_canvasProperties.getPixelsY() * m_scale);

	    m_hScroll.setMaximum(renderX);
    	m_hScroll.setThumb(canvasX);
		m_hScroll.setSelection((int)((double)m_hScrollPosition * m_scale));
	    
	    m_vScroll.setMaximum(renderY);
    	m_vScroll.setThumb(canvasY);
		m_vScroll.setSelection((int)((double)m_vScrollPosition * m_scale));
    	
		if (m_viewNode != null) {
	    	m_canvasProperties.setScale(m_scale);
	    	m_canvasProperties.setSelected(m_selectedNodes);

			int offsetX = m_hScroll.getSelection();
        	int offsetY = m_vScroll.getSelection();
        	Rectangle visibleArea = new Rectangle(offsetX, offsetY, canvasX, canvasY);
        	m_canvasProperties.setVisibleArea(visibleArea);
        	m_canvasProperties.setTimesHeight(m_timesCanvas.getClientArea().height);

	    	Node treeNode = m_viewNode.getTreeNode();
			m_canvasProperties.setTotalBest(treeNode.getAttributes().getTimeTaken(Case.BEST, false));
	    	m_canvasProperties.setTotalWorst(treeNode.getAttributes().getTimeTaken(Case.WORST, false));
			m_viewNode.updateXy();
		}
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
		double xScale = (double)m_structureCanvas.getClientArea().width / (double)m_canvasProperties.getPixelsX();
		double yScale = (double)m_structureCanvas.getClientArea().height / (double)m_canvasProperties.getPixelsY();

		m_scale = (xScale < yScale) ? xScale : yScale;

		if (DEBUG) System.out.printf("ZoomedCanvasView: zoomFit %f\n", m_scale);
		refreshView();
	}

	private void clearTree() {
		if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
			m_toolTip.dispose ();
		}
		m_viewNode = null;
		m_rootNode = null;
		m_doubleClickNode = null;
		m_selectedNodes.clear();
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

    
    protected void buildClickAreas() {
    	m_clickAreas.clear();
    	if (m_viewNode != null) {
    		m_viewNode.buildClickAreas(m_clickAreas);
    	}
    }

    protected void annotateTree() {
		if (m_viewNode != null) {
			m_viewNode.annotateXy(0, 0);
			updatePixelsXy();
		}
    }

    protected void updatePixelsXy() {
    	if (m_doubleClickNode == null) {
    		m_canvasProperties.setPixels(0, 0);
		} else {
    		m_canvasProperties.setPixels(m_viewNode.getPixelsX(), m_viewNode.getPixelsY());
		}
    }
    
    protected void drawStructureCanvas(GC gc) {
		Rectangle clientArea = m_structureCanvas.getClientArea();
		int renderX = clientArea.width;
		int renderY = clientArea.height;
        Image bufferedImage = new Image(m_display, renderX, renderY);
        GC bufferedGc = new GC(bufferedImage);
        
        bufferedGc.setAntialias(SWT.ON);
        bufferedGc.setTextAntialias(SWT.ON);

        bufferedGc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        bufferedGc.fillRectangle(0, 0, renderX, renderY);

        if (m_viewNode != null) {
			m_viewNode.draw(m_display, bufferedGc, true, true);
        }
        gc.drawImage(bufferedImage, 0, 0);
        bufferedGc.dispose();
        bufferedImage.dispose();
		buildClickAreas();
    }

    protected void drawTimesCanvas(GC gc) {
		Rectangle clientArea = m_timesCanvas.getClientArea();
		int renderX = clientArea.width;
		int renderY = clientArea.height;
        Image bufferedImage = new Image(m_display, renderX, renderY);
        GC bufferedGc = new GC(bufferedImage);
        
        bufferedGc.setAntialias(SWT.ON);
        bufferedGc.setTextAntialias(SWT.ON);

        bufferedGc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        bufferedGc.fillRectangle(0, 0, renderX, renderY);

        if (m_viewNode != null) {
			m_viewNode.drawTimes(m_display, bufferedGc, true, true);
        }

        gc.drawImage(bufferedImage, 0, 0);
        bufferedGc.dispose();
        bufferedImage.dispose();
		buildClickAreas();
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

    protected void nodeCoreChanged() {
		if (DEBUG) System.out.printf("Structure: nodeCoreChanged\n");
	}

    protected void initialiseTree() {
		if (DEBUG) System.out.printf("Structure: initialiseTree\n");
		Route route = GuiEngine.getEngine().getSelectedRoute();
		if (route != null) {
			m_rootNode = route.getNode();
			m_doubleClickNode = m_rootNode;
			m_selectedNodes = GuiEngine.getSelectedNodes();
			m_viewNode = buildViewNode(m_doubleClickNode, 0);
			annotateTree();
		} else {
			m_rootNode = null;
			m_doubleClickNode = null;
			m_selectedNodes.clear();
		}
	}
}
