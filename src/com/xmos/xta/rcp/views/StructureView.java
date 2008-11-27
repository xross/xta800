package com.xmos.xta.rcp.views;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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

import com.xmos.xta.rcp.XtaDisplayer;
import com.xmos.xta.rcp.XtaRouteInfo;
import com.xmos.xta.rcp.nodes.Node;
import com.xmos.xta.rcp.views.Structure.StructureLoopNode;
import com.xmos.xta.rcp.views.Structure.StructureNode;
import com.xmos.xta.rcp.views.Structure.StructurePathNode;
import com.xmos.xta.rcp.views.Structure.StructureRouteNode;

public class StructureView extends ViewPart implements IPropertyChangeListener,
                                                       ISelectionListener,
                                                       ISelectionProvider,
                                                       IMenuListener {
    public static final boolean DEBUG = false;
    public static final int MAX_NUM_RENDER_NODES = 2000;
    public static final String ID = "com.xmos.xta.rcp.views.Structure";

    private boolean m_modeCircle;

    private Node m_rootNode;
    private Node m_startNode;
    private Node m_selectedNode;
    private XtaRouteInfo m_routeInfo;
    
    private Vector<ISelectionChangedListener> m_listeners;

    private double m_scale;
    private int m_lineWidth;
    private int m_pixelsX;
	private int m_pixelsY;
    private int m_borderX;
    private int m_borderY;
    private int m_numTreeNodes;
    private ArrayList<StructureNode> m_clickAreas;
    
    private Composite m_parent;
    private Display m_display;
    private ScrolledComposite m_scrollArea;
    private Canvas m_canvas;
    
    private Shell m_toolTip;
    private Label m_tipLabel;

	public StructureView() {
		if (DEBUG) System.out.printf("Structure()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
		
		m_modeCircle = false;
		m_scale = 1.0;
		m_borderX = 10;
		m_borderY = 10;
		m_lineWidth = 1;
		
		m_listeners = new Vector<ISelectionChangedListener>();
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Structure: createPartControl\n");			
	    m_parent = parent;
	    m_display = m_parent.getDisplay();

	    getSite().setSelectionProvider(this);
	    getSite().getPage().addSelectionListener(this);

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
	    
	    m_clickAreas = new ArrayList<StructureNode>();
	    setupMouseListeners();
	    createContextMenus();
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
						shell.dispose ();
						break;
					case SWT.MouseExit:
						shell.dispose ();
						break;
				}
			}
		};

		MouseAdapter mouseAdapter = new MouseAdapter() { 
	    	public void mouseDown(MouseEvent e) {
    			if ((e.button == 1) || (e.button == 3)) {
		    		for (StructureNode node : m_clickAreas) {
		    			if (node.contains(e.x, e.y)) {
		    	    		m_selectedNode = node.getTreeNode();
		    	    		setSelection(new StructuredSelection(m_selectedNode));
		    				return;
		    			}
		    		}
    			}
	    	} 
	    };  
	    m_canvas.addMouseListener(mouseAdapter);
	    
	    MouseTrackListener mouseTrack = new MouseTrackListener() { 
		     public void mouseEnter(MouseEvent e) { 
		     }  
		     public void mouseExit(MouseEvent e) { 
		     }  
		     public void mouseHover(MouseEvent e) { 
		    		for (StructureNode node : m_clickAreas) {
		    			if (node.contains(e.x, e.y)) {
		    	    		if (m_toolTip != null  && !m_toolTip.isDisposed ())
		    	    			m_toolTip.dispose ();

		    	    		m_toolTip = new Shell(m_display, SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
		    	    		m_toolTip.setBackground (m_display.getSystemColor (SWT.COLOR_INFO_BACKGROUND));
							FillLayout layout = new FillLayout ();
							layout.marginWidth = 2;
							m_toolTip.setLayout (layout);
							m_tipLabel = new Label (m_toolTip, SWT.NONE);
							m_tipLabel.setForeground (m_display.getSystemColor (SWT.COLOR_INFO_FOREGROUND));
							m_tipLabel.setBackground (m_display.getSystemColor (SWT.COLOR_INFO_BACKGROUND));
							m_tipLabel.setText(node.getTreeNode().getToolTipText());
							m_tipLabel.addListener(SWT.MouseExit, labelListener);
							m_tipLabel.addListener(SWT.MouseDown, labelListener);
							Point size = m_toolTip.computeSize (SWT.DEFAULT, SWT.DEFAULT);
							Point pt = Display.getCurrent().getCursorLocation();
							m_toolTip.setBounds(pt.x, pt.y, size.x, size.y);
							m_toolTip.setVisible(true);
		    	    		return;
		    			}
		    		}
		     } 
	    }; 
	    m_canvas.addMouseTrackListener(mouseTrack); 
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Structure: setFocus\n");
		m_scrollArea.redraw();
		m_canvas.redraw();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Structure: propertyChange: %s\n", event.getProperty());			

		if (event.getProperty().equals("DisplayRoute")) {
	    	if (m_toolTip != null  && !m_toolTip.isDisposed ())
				m_toolTip.dispose ();

	    	initialiseTree();
		} else if (event.getProperty().equals("LoadExecutable")) {
			clearTree();
		} else if (event.getProperty().equals("UpdateState")) {
	    	if (m_toolTip != null  && !m_toolTip.isDisposed ())
				m_toolTip.dispose ();
			annotateTree();
		}
		refreshView();
	}
	
	private void refreshView() {
		m_display.asyncExec(new Runnable() {
			public void run() {
				prepareCanvas();
				m_canvas.redraw();
			}});
	}

	public void selectionChanged(IWorkbenchPart part, ISelection sel) {
        if (sel instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)sel).getFirstElement();
            if (object instanceof Node) {
                m_selectedNode = (Node)object;
                annotateTree();
        		m_scrollArea.redraw();
        		m_canvas.redraw();
            }
        }
    }	            		         	        	

	public void menuAboutToShow(IMenuManager manager) {
		if (m_selectedNode != null) {
			m_selectedNode.createContextMenu(m_parent.getShell(), manager);
		}
	}

	public void setCircleMode(boolean modeCircle) {
		m_modeCircle = modeCircle;
		m_scrollArea.redraw();
		m_canvas.redraw();
	}

    private void annotateTree() {
		if (m_startNode != null) {
    		if (m_startNode.isBlockNode()) {
    			m_startNode = m_startNode.getParentNode();
    		}
    	}

		if (m_selectedNode != null) {
    		if (m_selectedNode.isBlockNode()) {
    			m_selectedNode = m_selectedNode.getParentNode();
    		}
    	}

		m_numTreeNodes = 0;
		analyseTree(m_rootNode, 1, 1);
		m_pixelsX = (m_borderX  * 2) + m_rootNode.getStructureNode().getPixelsX();
		m_pixelsY = (m_borderY * 2) + m_rootNode.getStructureNode().getPixelsY();
    }
    
    private void prepareCanvas() {
		Rectangle clientArea = m_scrollArea.getBounds();
		
        if (m_numTreeNodes > MAX_NUM_RENDER_NODES) {
    		m_canvas.setSize(clientArea.width, clientArea.height);
        } else {
			int renderX = (int)((double)m_pixelsX * m_scale);
			int renderY = (int)((double)m_pixelsY * m_scale);
			
			int x = (clientArea.width > renderX) ? clientArea.width : renderX;
			int y = (clientArea.height > renderY) ? clientArea.height : renderY;
			m_canvas.setSize(x, y);
        }
    }
    
    private void drawCanvas(GC gc) {
    	m_clickAreas.clear();

    	gc.setAntialias(SWT.ON);
    	gc.setTextAntialias(SWT.ON);
    	
		Rectangle clientArea = m_canvas.getClientArea();
		m_canvas.setSize(clientArea.width, clientArea.height);
    	gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(0, 0, clientArea.width, clientArea.height);

        if (m_numTreeNodes > MAX_NUM_RENDER_NODES) {
	        gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
	        gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLACK));
	        String error = "Code structure too Complicated: select a smaller region";

	        double functionNameWidth = gc.getFontMetrics().getAverageCharWidth() * error.length();
	        double functionNameHeight = gc.getFontMetrics().getHeight();
	        
	        gc.drawString(error, (int)((clientArea.width - functionNameWidth)/2),
	        		(int)((clientArea.height - functionNameHeight)/2));
	    } else {
	        if (m_startNode == null)
	        	return;
	        
			if (m_modeCircle) {
				drawCircle(gc, m_scale);
			} else {
				gc.setLineWidth(m_lineWidth);
				m_startNode.getStructureNode().draw(m_display, gc, m_clickAreas, m_borderX, m_borderY, m_scale);
			}
	    }
    }
    
    private void drawCircle(GC gc, double scale) {
		System.out.printf("Draw Circle %d x %d\n", m_pixelsX, m_pixelsY);
        gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(0, 0, m_pixelsX, m_pixelsY);

        gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLACK));
        gc.setBackground(m_display.getSystemColor(SWT.COLOR_BLACK));
        gc.fillOval(0, 0, m_pixelsX, m_pixelsY);
    }

	private boolean analyseTree(Node node, int numIterations, double totalTime) {
		if (node == null)
			return false;
		
    	double weight = (numIterations * node.getTimeTakenThis()) / totalTime;
    	boolean selected = (node == m_selectedNode);

    	StructureNode structureNode = null;
    	if (node.isRouteNode())
    		structureNode = new StructureRouteNode(node, weight, m_lineWidth);
    	else if (node.isPathNode())
    		structureNode = new StructurePathNode(node, weight, m_lineWidth);
    	else if (node.isLoopNode())
    		structureNode = new StructureLoopNode(node, weight, m_lineWidth);
    	else
    		assert(false);
    	node.setStructureNode(structureNode);
    	m_numTreeNodes++;
    	
        for (Node subNode : node.getNodes()) {
        	if (node.isRouteNode())
        		totalTime = subNode.getTimeTaken(); // Re-compute total time for sub tree
        	
        	if (subNode.isPathNode())
        		numIterations = node.getNumIterations() * numIterations;

    		if (subNode.isPathNode() || subNode.isLoopNode()) {
    			selected |= analyseTree(subNode, numIterations, totalTime);
    		}
        }

        structureNode.setIsSelected(selected);
   		return selected;
	}

	public void zoomOut() {
		m_scale = m_scale / 1.2;
		refreshView();
	}

	public void zoomIn() {
		m_scale = m_scale * 1.2;
		refreshView();
	}

	public void zoomReset() {
		m_scale = 1.0;
		refreshView();
	}

	private void initialiseTree() {
		m_routeInfo = XtaDisplayer.getInstance().getRouteInfo();										
		m_rootNode = m_routeInfo.getRouteNode();
		m_startNode = m_rootNode;
		m_selectedNode = null;
	}

	private void clearTree() {
		if (m_toolTip != null  && !m_toolTip.isDisposed ())
			m_toolTip.dispose ();
		
		m_rootNode = null;
		m_startNode = null;
		m_selectedNode = null;
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (!m_listeners.contains(listener)) {
			m_listeners.add(listener);
		}
	}

	public ISelection getSelection() {
		return new StructuredSelection(m_selectedNode);
	}

	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {

		if (m_listeners.contains(listener)) {
			m_listeners.remove(listener);
		}
	}

	public void setSelection(ISelection selection) {
		for (ISelectionChangedListener listener : m_listeners) {
			listener.selectionChanged(new SelectionChangedEvent(this, selection));
		}
	}
}
