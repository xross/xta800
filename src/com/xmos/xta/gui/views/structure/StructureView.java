package com.xmos.xta.gui.views.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
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
    private ZoomedCanvasProperties m_canvasProperties;
    private Canvas m_structureCanvas;
    private Canvas m_timesCanvas;

    private Node m_rootNode;
    private List<Node> m_selectedNodes;
    private Node m_doubleClickNode;
    
    private Composite m_parent;
    private Display m_display;
    private ScrollBar m_hScroll;
    private ScrollBar m_vScroll;

    private Shell m_toolTip;
    private Label m_tipLabel;
    private Vector<ISelectionChangedListener> m_listeners;
    
    private RendererControl m_renderControl;
    private Renderer m_renderer;

	public StructureView() {
		if (DEBUG) System.out.printf("Structure()\n");			
		GuiEngine.addBroadcastListener(this);
	    
		m_listeners = new Vector<ISelectionChangedListener>();
		m_rootNode = null;
		m_doubleClickNode = null;
	    m_selectedNodes = new ArrayList<Node>();

		m_canvasProperties = new ZoomedCanvasProperties();
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("ZoomedCanvasView: createPartControl\n");
	    m_parent = parent;
	    m_display = m_parent.getDisplay();
		m_renderControl = new RendererControl(m_display);

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
				m_renderControl.setStructureCanvasSize(clientArea.width, clientArea.height);
				updateScrollBars();
			}
			public void controlMoved(ControlEvent e) {
				Rectangle clientArea = structureComposite.getClientArea();
				m_renderControl.setStructureCanvasSize(clientArea.width, clientArea.height);
				updateScrollBars();
			}
	    });	    

	    final Composite timesComposite = new Composite(sashForm, SWT.BORDER); 	    
	    timesComposite.setLayout(new FillLayout()); 
	    m_timesCanvas = new Canvas(timesComposite, SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
	    timesComposite.addControlListener(new ControlListener() {
			public void controlResized(ControlEvent e) {
				Rectangle clientArea = timesComposite.getClientArea();
				m_renderControl.setTimesCanvasHeight(clientArea.height);
				updateScrollBars();
			}
			public void controlMoved(ControlEvent e) {
				Rectangle clientArea = timesComposite.getClientArea();
				m_renderControl.setTimesCanvasHeight(clientArea.height);
				updateScrollBars();
			}	    	
	    });	    

		sashForm.setWeights(new int[]{3, 1});

	    // Sets up the horizontal scroll bar.
	    m_hScroll = composite.getHorizontalBar();
	    m_hScroll.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	    		m_renderControl.setOffsetX(m_hScroll.getSelection());
	        }
	    });
	    m_hScroll.setMaximum(1);
	    
	    // Sets up the vertical scroll bar.
	    m_vScroll = structureComposite.getVerticalBar();
	    m_vScroll.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event e) {
	        	m_renderControl.setOffsetY(m_vScroll.getSelection());
	        }
	    });
	    m_vScroll.setMaximum(1);

	    structureComposite.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	m_renderControl.refresh();
		    }
	    });

	    m_structureCanvas.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	drawStructureCanvas(e.gc);
		    }
	    });	

	    timesComposite.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	m_renderControl.refresh();
		    }
	    });

	    m_timesCanvas.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	drawTimesCanvas(e.gc);
		    }
	    });
	    
	    setupMouseListeners();
	    createContextMenus();
	    
		getSite().getPage().addSelectionListener(this);
		getSite().setSelectionProvider(this);

	    m_renderer = new Renderer(m_display, m_renderControl, m_canvasProperties);
		(new Thread(m_renderer)).start();
		(new Thread(new Updater(m_display, m_renderControl, m_structureCanvas, m_timesCanvas))).start();
	
	    initialiseTree();
	    m_renderControl.refresh();
		updateScrollBars();
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("ZoomedCanvasView: setFocus\n");
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("ZoomedCanvasView: propertyChange: %s\n", event.getProperty());			

		if (event.getProperty().equals(Notifier.NOTIFY_ANALYSIS_COMPLETE)) {
	    	if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
				m_toolTip.dispose ();
	    	}
	    	initialiseTree();
			m_renderControl.refresh();
			updateScrollBars();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_LOAD_EXECUTABLE) || 
				   event.getProperty().equals(Notifier.NOTIFY_SET_NODE_CORE)) {
			clearView();
			m_renderControl.refresh();
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_UPDATE_STATE)) {
	    	if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
				m_toolTip.dispose ();
	    	}
	    	updateState();
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_SET_FOCUS)) {
			ISelection sel = GuiEngine.getFocus();
	        if (sel instanceof IStructuredSelection) {	            
	            Object object = ((IStructuredSelection)sel).getFirstElement();
	            if (object instanceof Node) {
	                m_doubleClickNode = (Node)object;
	            }
	        }
			
		} else if (event.getProperty().equals(Notifier.NOTIFY_SELECT_ROUTE) || 
				   event.getProperty().equals(Notifier.NOTIFY_REMOVE_ROUTE)) {
			initialiseTree();
			m_renderControl.refresh();
			
		} else if (event.getProperty().equals(Broadcaster.BROADCAST_UPDATE_FOLDING)) {
			m_renderControl.refresh();
			updateScrollBars();
			
		} 
	}
	
	private void updateState() {
		if (m_viewNode != null) {
	    	Node treeNode = m_viewNode.getTreeNode();
	    	m_renderControl.setBestWorstTimes(
	    			treeNode.getAttributes().getTimeTaken(Case.BEST, false),
	    			treeNode.getAttributes().getTimeTaken(Case.WORST, false));
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
    	m_selectedNodes = newNodes;
    	m_renderControl.setSelection(newNodes);
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

	private void updateScrollBars() {
		int canvasX = m_renderControl.getStructureCanvasWidth();
		int canvasY = m_renderControl.getStructureCanvasHeight();
		
		double scale = m_renderControl.getScale();
		int renderX = (int)((double)m_renderControl.getPixelsX() * scale);
		int renderY = (int)((double)m_renderControl.getPixelsY() * scale);

		m_hScroll.setMaximum(renderX);
    	m_hScroll.setThumb(canvasX);
    	int position = setScrollPosition(m_hScroll, m_renderControl.getOffsetX());
    	m_renderControl.setOffsetX(position);
	    
	    m_vScroll.setMaximum(renderY);
    	m_vScroll.setThumb(canvasY);
    	position = setScrollPosition(m_vScroll, m_renderControl.getOffsetY());
    	m_renderControl.setOffsetY(position);
    	
		if (DEBUG) System.out.printf("updateScrollBars (%d, %d, %d) (%d, %d, %d)\n",
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

		m_renderControl.setOffsetX(m_hScroll.getSelection());
		m_renderControl.setOffsetY(m_vScroll.getSelection());
		m_renderControl.setScale(m_renderControl.getScale() / 1.2);
		updateScrollBars();
	}

	public void zoomIn() {
		if (m_viewNode == null)
			return;
		
		// Don't zoom any more than the maximum width
		double scale = m_renderControl.getScale();
		if (scale < 5) {
			m_renderControl.setOffsetX(m_hScroll.getSelection());
			m_renderControl.setOffsetY(m_vScroll.getSelection());
			m_renderControl.setScale(scale * 1.2);
			updateScrollBars();
		}
	}

	public void zoomReset() {
		m_renderControl.setOffsetX(0);
		m_renderControl.setOffsetY(0);
		m_renderControl.setScale(1.0);
		updateScrollBars();
	}

	public void zoomFit() {
		double xScale = (double)m_renderControl.getStructureCanvasWidth() / (double)m_renderControl.getPixelsX();
		double yScale = (double)m_renderControl.getStructureCanvasHeight() / (double)m_renderControl.getPixelsY();
		m_renderControl.setOffsetX(0);
		m_renderControl.setOffsetY(0);
		m_renderControl.setScale((xScale < yScale) ? xScale : yScale);
		updateScrollBars();
	}

	private void clearView() {
		if (m_toolTip != null  && !m_toolTip.isDisposed ()) {
			m_toolTip.dispose ();
		}
		m_viewNode = null;
		m_rootNode = null;
		m_doubleClickNode = null;
		updateSelection(new ArrayList<Node>());
		m_renderControl.reset();
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
					int offsetX = m_renderControl.getOffsetX();
					int offsetY = m_renderControl.getOffsetY();
					ViewNode clickNode = m_viewNode.getClickNode(e.x + offsetX, e.y + offsetY);
					if (clickNode != null) {
						clickNode.setFolded(!clickNode.isFolded(), false);
	    				m_renderControl.refresh();
	    				updateScrollBars();

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
    					int offsetX = m_renderControl.getOffsetX();
    					int offsetY = m_renderControl.getOffsetY();
    					ViewNode clickNode = m_viewNode.getClickNode(e.x + offsetX, e.y + offsetY);
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
					int offsetX = m_renderControl.getOffsetX();
					int offsetY = m_renderControl.getOffsetY();
					ViewNode clickNode = m_viewNode.getClickNode(e.x + offsetX, e.y + offsetY);
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
    	} else if (node.isLoopNode() || node.isSelfLoopNode()) {
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
			updateSelection(GuiEngine.getSelectedNodes());
			m_viewNode = buildViewNode(m_doubleClickNode, 0);
		} else {
			m_rootNode = null;
			m_doubleClickNode = null;
			m_viewNode = null;
			updateSelection(new ArrayList<Node>());
		}
		m_renderControl.setViewNode(m_viewNode);
		updateState();
	}

    protected void drawStructureCanvas(GC gc) {
		gc.drawImage(m_renderControl.getStructureCanvasImage(), 0, 0);
    }

    protected void drawTimesCanvas(GC gc) {
    	gc.drawImage(m_renderControl.getTimesCanvasImage(), 0, 0);
    }
}
