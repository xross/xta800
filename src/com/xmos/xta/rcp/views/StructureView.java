package com.xmos.xta.rcp.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
                                                       ISelectionListener {
    public static final boolean DEBUG = false;
    public static final int MAX_NUM_RENDER_NODES = 2000;
    public static final String ID = "com.xmos.xta.rcp.views.Structure";

    private boolean m_modeCircle;

    private Node m_selectedNode;
    private StructureNode m_stuctureTree;
    private double m_scale;
    private int m_pixelsX;
	private int m_pixelsY;
    private int m_borderX;
    private int m_borderY;
    private int m_fontX;
    private int m_fontY;
    private int m_numTreeNodes;
    
    private Display m_display;
    private Composite m_parent;
    private ScrolledComposite m_scrollArea;
    private Canvas m_canvas;
    
	public StructureView() {
		if (DEBUG) System.out.printf("Structure()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
		
		m_modeCircle = false;
		m_scale = 1.0;
		m_borderX = 10;
		m_borderY = 10;
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Structure: createPartControl\n");			
	    m_parent = parent;
	    m_display = m_parent.getDisplay();
	    GC gc = new GC(m_display);
	    m_fontX = gc.getFontMetrics().getAverageCharWidth();
	    m_fontY = gc.getFontMetrics().getHeight();
	    gc.dispose();

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
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Structure: setFocus\n");
		m_scrollArea.redraw();
		m_canvas.redraw();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Structure: propertyChange: %s\n", event.getProperty());			

		if (event.getProperty().equals("DisplayRoute")) {
			m_selectedNode = null;
			refreshTree();
		} else if (event.getProperty().equals("LoadExecutable")) {
			m_selectedNode = null;
		} else if (event.getProperty().equals("UpdateState")) {
			m_selectedNode = null;
			refreshTree();
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
                analyseTree(m_selectedNode);
        		m_scrollArea.redraw();
        		m_canvas.redraw();
            }
        }
    }	            		         	        	

	public void setCircleMode(boolean modeCircle) {
		m_modeCircle = modeCircle;
		m_scrollArea.redraw();
		m_canvas.redraw();
	}

    private void refreshTree() {
    	if (m_selectedNode == null) {
    		XtaRouteInfo info = XtaDisplayer.getInstance().getRouteInfo();
    		if (info != null)
    			m_selectedNode = info.getRouteNode();
    	} else if (m_selectedNode.isBlockNode()) {
    		m_selectedNode = m_selectedNode.getParentNode();
    	}
        analyseTree(m_selectedNode);
    }
    
    private void prepareCanvas() {
		Rectangle clientArea = m_scrollArea.getBounds();
		int renderX = (int)((double)m_pixelsX * m_scale);
		int renderY = (int)((double)m_pixelsY * m_scale);
		
		int x = (clientArea.width > renderX) ? clientArea.width : renderX;
		int y = (clientArea.height > renderY) ? clientArea.height : renderY;
		m_canvas.setSize(x, y);
    }
    
    private void drawCanvas(GC gc) {
    	gc.setAntialias(SWT.ON);
    	gc.setTextAntialias(SWT.ON);
    	
		Rectangle clientArea = m_canvas.getClientArea();
		m_canvas.setSize(clientArea.width, clientArea.height);
    	gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(0, 0, clientArea.width, clientArea.height);

        if (m_numTreeNodes > MAX_NUM_RENDER_NODES){
	        gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
	        gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLACK));
	        String error = "Code structure too Complicated: select a smaller region";

	        double functionNameWidth = gc.getFontMetrics().getAverageCharWidth() * error.length();
	        double functionNameHeight = gc.getFontMetrics().getHeight();
	        
	        gc.drawString(error, (int)((m_borderY - functionNameWidth)/2),
	        		(int)((m_borderY - functionNameHeight)/2));
	    } else {
	        if (m_stuctureTree == null)
	        	return;
	        
			if (m_modeCircle) {
				drawCircle(gc, m_scale);
			} else {
				m_stuctureTree.draw(m_display, gc, m_borderX, m_borderY, m_scale);
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

    private Node getPathRoot(Node start) {
    	if (start.isRouteNode()) 
    		return null;
    	
    	Node node = start;
    	while (!node.getParentNode().isRouteNode()) {
    		node = node.getParentNode();
    	}
		return node;
	}

	private void analyseTree(Node node) {
		double totalTime = 0;
		if (!node.isRouteNode()) {
			totalTime = getPathRoot(node).getTimeTaken();
		}

		m_numTreeNodes = 0;
		// FIXME - ensure correct start iterations
		m_stuctureTree = analyseTree(node, node.getNumIterations(), totalTime);

		m_pixelsX = (m_borderX  * 2) + m_stuctureTree.getPixelsX();
		m_pixelsY = (m_borderY * 2) + m_stuctureTree.getPixelsY();
	}

	private StructureNode analyseTree(Node node, int numIterations, double totalTime) {
		if (node == null)
			return null;
		
    	StructureNode treeNode = null;
		m_numTreeNodes++;
    	double weight = (numIterations * node.getTimeTakenThis()) / totalTime;

    	if (node.isRouteNode())
    		treeNode = new StructureRouteNode(m_fontX, m_fontY, node, weight);
    	else if (node.isPathNode())
    		treeNode = new StructurePathNode(node, weight);
    	else if (node.isLoopNode())
    		treeNode = new StructureLoopNode(node, weight);
    	else
    		assert(false);
		
        for (Node subNode : node.getNodes()) {
        	if (node.isRouteNode())
        		totalTime = subNode.getTimeTaken(); // Re-compute total time for sub tree
        	
        	if (subNode.isPathNode())
        		numIterations = node.getNumIterations() * numIterations;

    		if (subNode.isPathNode() || subNode.isLoopNode()) {
    			StructureNode newNode = analyseTree(subNode, numIterations, totalTime);
    			treeNode.addChild(newNode);
    		}
        }
        return treeNode;
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
}
