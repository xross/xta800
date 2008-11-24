package com.xmos.xta.rcp.views;

import java.util.Vector;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.xmos.xta.rcp.XtaDisplayer;
import com.xmos.xta.rcp.nodes.Node;

public class StructureView extends ViewPart implements IPropertyChangeListener,
                                                       ISelectionListener {
    public static final boolean DEBUG = false;
    public static final int MAX_NUM_RENDER_NODES = 2000;
    public static final String ID = "com.xmos.xta.rcp.views.Structure";

    private boolean m_modeCircle;

    private TreeStructureNode m_stuctureTree;
    private Node m_selectedNode;
    private int m_treeDepth;
    private int m_totalSize;
    
    private Display m_display;
    private Canvas m_canvas;
    
	public StructureView() {
		if (DEBUG) System.out.printf("Structure()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
		
		m_modeCircle = false;
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Structure: createPartControl\n");			
	    m_display = parent.getDisplay();

	    getSite().getPage().addSelectionListener(this);

	    m_canvas = new Canvas(parent, SWT.NO_BACKGROUND);
	    m_canvas.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	draw(e.gc);
	        }
	    });
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Structure: setFocus\n");			
		m_canvas.redraw();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Structure: propertyChange: %s\n", event.getProperty());			

		if (event.getProperty().equals("DisplayRoute")) {
			m_selectedNode = null;
		} else if (event.getProperty().equals("LoadExecutable")) {
			m_selectedNode = null;
		} else if (event.getProperty().equals("UpdateState")) {
		}

		m_display.asyncExec(new Runnable() {
			public void run() {
				m_canvas.redraw();
			}});
	}
	
    public void selectionChanged(IWorkbenchPart part, ISelection sel) {
        if (sel instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)sel).getFirstElement();
            if (object instanceof Node) {
                m_selectedNode = (Node)object;
                m_canvas.redraw();
            }
        }
     }	            		         	        	

	public void setCircleMode(boolean modeCircle) {
		m_modeCircle = modeCircle;
		m_canvas.redraw();
	}

    private void draw(GC gc) {
    	if (m_selectedNode == null) {
    		m_selectedNode = XtaDisplayer.getInstance().getRouteInfo().getRouteNode();
    	} else if (m_selectedNode.isBlockNode()) {
    		m_selectedNode = m_selectedNode.getParentNode();
    	}

		Rectangle clientArea = m_canvas.getClientArea();
        int totalWidth = clientArea.width;
        int totalHeight = clientArea.height;
    	gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(0, 0, totalWidth, totalHeight);

        analyseTree(m_selectedNode);

		if (m_totalSize > MAX_NUM_RENDER_NODES){
	        gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
	        gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLACK));
	        String error = "Code structure too Complicated: select a smaller region";

	        double functionNameWidth = gc.getFontMetrics().getAverageCharWidth() * error.length();
	        double functionNameHeight = gc.getFontMetrics().getHeight();
	        
	        gc.drawString(error, (int)((totalWidth - functionNameWidth)/2),
	        		(int)((totalHeight - functionNameHeight)/2));
	        return;
	    }
	
		if (m_modeCircle) {
			drawCircle(gc, totalWidth, totalHeight);
		} else {
			drawTree(gc, totalWidth, totalHeight);
		}
    }
    
    private void drawTree(GC gc, int totalWidth, int totalHeight) {
        gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(0, 0, totalWidth, totalHeight);

        double borderY = 10.0;
        double yFactor = (((double)totalHeight - (2.0 * borderY)) / (double)m_treeDepth);

        double borderX = 10.0;
        drawTree(gc, m_stuctureTree, borderX, (double)totalWidth - (2.0 * borderX),
        		borderY, yFactor);
    }

    private void drawTree(GC gc, TreeStructureNode node, double xOffset, double xWidth,
    		double yOffset, double yFactor) {
    	node.draw(gc, xOffset, xWidth, yOffset, yFactor);
    	yOffset += node.getHeight() * yFactor;
        
        int numNodes = node.numChildren();
        int subNodeNum = 0;
        for (TreeStructureNode subNode : node.getChildren()) {
        	double subWidth = xWidth / numNodes;
        	drawTree(gc, subNode, xOffset + (subWidth * subNodeNum), subWidth, 
        			yOffset, yFactor);
        	subNodeNum++;
        }
    }
    
    private void drawCircle(GC gc, int totalWidth, int totalHeight) {
        gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(0, 0, totalWidth, totalHeight);

        gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLACK));
        gc.setBackground(m_display.getSystemColor(SWT.COLOR_BLACK));
        gc.fillOval(0, 0, totalWidth, totalHeight);
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
		m_treeDepth = 0;
		m_totalSize = 0;

		double totalTime = 0;
		if (!node.isRouteNode()) {
			totalTime = getPathRoot(node).getTimeTaken();
		}
 
		// FIXME - ensure correct start iterations
		m_stuctureTree = analyseTree(node, 0, node.getNumIterations(), totalTime);
	}

	private TreeStructureNode analyseTree(Node node, int depth, int numIterations, double totalTime) {
		TreeStructureNode treeNode = new TreeStructureNode(node, numIterations, totalTime);
		m_totalSize++;
		
		depth += treeNode.getHeight();
		if (depth > m_treeDepth) {
        	m_treeDepth = depth;
		}
		
        for (Node subNode : node.getNodes()) {
        	if (node.isRouteNode())
        		totalTime = subNode.getTimeTaken(); // Re-compute total time for sub tree
        	
        	if (subNode.isPathNode())
        		numIterations = node.getNumIterations() * numIterations;

    		if (subNode.isPathNode() || subNode.isLoopNode()) {
    			TreeStructureNode newNode = analyseTree(subNode, depth, numIterations, totalTime);
    			treeNode.addChild(newNode);
    		}
        }
        return treeNode;
	}
	
    class TreeStructureNode {
        private Node m_treeNode;
        private Vector<TreeStructureNode> m_children;
        private double m_weight;
    	
        public TreeStructureNode(Node treeNode, int numIterations, double totalTime) {
        	m_treeNode = treeNode;
        	m_children = new Vector<TreeStructureNode>();
        	m_weight = (numIterations * treeNode.getTimeTakenThis()) / totalTime;
        }    
        
        public int numChildren() {
        	return m_children.size();
		}

		public void addChild(TreeStructureNode node) {
        	m_children.add(node);
        }

        public Vector<TreeStructureNode> getChildren() {
        	return m_children;
        }    
        
        public Node getTreeNode() {
        	return m_treeNode;
        }
        
        public int getHeight() {
        	if (m_treeNode.isRouteNode()) {
        		return 10;
        	} else if (m_treeNode.isPathNode()) {
        		return 20;
        	} else if (m_treeNode.isLoopNode()) {
        		return 20;
        	} else {
        		assert(false);
        		return 0;
        	}
        }

        public void draw(GC gc, double xOffset, double xWidth, double yOffset, double yFactor) {
            double yHeight = (double)getHeight() * yFactor;
    	    if (m_treeNode.isRouteNode()) {
    	        int height = getHeight();
    	    	gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLUE));
    	        gc.setBackground(m_display.getSystemColor(SWT.COLOR_BLUE));
    	    	gc.fillOval((int)(xOffset + ((xWidth - 10) / 2.0)),
    	    			(int)(yOffset + ((yHeight - height) / 2.0)), height, height);
    	        gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLACK));
    	        gc.setBackground(m_display.getSystemColor(SWT.COLOR_BLACK));
    	    	gc.drawOval((int)(xOffset + ((xWidth - 10) / 2.0)),
    	    			(int)(yOffset + ((yHeight - height) / 2.0)), height, height);
    	    } else if (m_treeNode.isLoopNode()) {
                gc.setForeground(m_display.getSystemColor(SWT.COLOR_YELLOW));
                gc.setBackground(m_display.getSystemColor(SWT.COLOR_YELLOW));
    	    	gc.fillOval((int)xOffset + 1, (int)yOffset + 1, (int)xWidth - 1, (int)yHeight - 1);
                gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLACK));
                gc.setBackground(m_display.getSystemColor(SWT.COLOR_BLACK));
    	    	gc.drawOval((int)xOffset + 1, (int)yOffset + 1, (int)xWidth - 1, (int)yHeight - 1);
    	    } else {
    	    	gc.setAlpha((int)(255.0 * m_weight));
                gc.setForeground(m_display.getSystemColor(SWT.COLOR_RED));
                gc.setBackground(m_display.getSystemColor(SWT.COLOR_RED));
    	    	gc.fillRectangle((int)xOffset + 1, (int)yOffset + 1, (int)xWidth - 1, (int)yHeight - 1);

    	    	gc.setAlpha(255);
    	    	gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLACK));
                gc.setBackground(m_display.getSystemColor(SWT.COLOR_BLACK));
    	    	gc.drawRectangle((int)xOffset + 1, (int)yOffset + 1, (int)xWidth - 1, (int)yHeight - 1);
    	    }
        }
    }
}
