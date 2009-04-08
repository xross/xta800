package com.xmos.xta.rcp.views.FunctionView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.xmos.elfhandler.Symbol;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.rcp.XtaNotifier;
import com.xmos.xta.rcp.nodes.Node;
import com.xmos.xta.rcp.views.ZoomedCanvasView;
import com.xmos.xta.rcp.views.ViewNode.TextBlockNode;
import com.xmos.xta.rcp.views.ViewNode.ViewNode;

public class FunctionView extends ZoomedCanvasView {
	
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.rcp.views.FunctionView.Function";

    private static final double MIN_PIXELS_GAP = 50;
    private static final double MAX_PIXELS_GAP = 100;

    private SymbolTable m_table;
    private Vector<XtaFunctionBlock> m_functionList;
    private boolean m_addedFunctionsToClickAreas;
    private int m_blockLength;
    private Node m_currentNode;
    private String m_currentFunction;

    private int m_colorIndex;
    private Vector<Color> m_colors;
    private HashMap<String, Color> m_functionColors;

    private int m_functionNameY = 30;

    private int m_timelineY = 15;
    private String m_timeUnits;
    private double m_pixelsPerTimeUnit;
    
	public FunctionView() {
		super();
		if (DEBUG) System.out.printf("Function()\n");
		XtaNotifier.getInstance().addPropertyChangeListener(this);
		
		m_functionList = new Vector<XtaFunctionBlock>();
		m_addedFunctionsToClickAreas = false;
        m_functionColors = new HashMap<String, Color>();
        initaliseColors(m_display);

		m_scaleY = false;
		m_pixelsX = 0;
		m_pixelsY = 0;
		m_borderX = 0;
		m_borderY = 10;
		m_lineWidth = 1;

		m_pixelsPerTimeUnit = 1;
		m_timeUnits = "ns";
	}

    protected void nodeCoreChanged() {
		if (DEBUG) System.out.printf("Function: nodeCoreChanged\n");
		getDebugInfo();
	}

    protected void initialiseTree() {
		if (DEBUG) System.out.printf("Function: initialiseTree\n");
    	m_routeInfo = XtaNotifier.getInstance().getRouteInfo();
		if (m_routeInfo != null) {
			m_rootNode = m_routeInfo.getRouteNode();
			m_selectedNodes = m_routeInfo.getSelectedNodes();
		} else {
			m_rootNode = null;
			m_selectedNodes.clear();
		}
	}

    protected void buildClickAreas() {
		if (DEBUG) System.out.printf("Function: buildClickAreas\n");
    	m_clickAreas.clear();
    	if (m_selectedNodes.size() == 1) {
    		Node pathRoot = m_selectedNodes.get(0).getPathRoot();
    		if (pathRoot != null)
    			pathRoot.getFunctionNode().buildClickAreas(m_clickAreas);
    	}

    	m_addedFunctionsToClickAreas = false;
    }

    protected void annotateTree(boolean isSelctionChange) {
		if (DEBUG) System.out.printf("Function: annotateTree\n");
    	clearAll();
    	buildClickAreas();
    	Node pathRoot = null;
    	if (m_selectedNodes.size() == 1) {
    		pathRoot = m_selectedNodes.get(0).getPathRoot();
    	}
    	
		if (pathRoot == null) {
			m_pixelsX = 0;
			m_pixelsY = 0;
		} else {
			annotateTree(pathRoot, 1, false);

			pathRoot.getFunctionNode().annotateXy(m_borderX, m_borderY + m_timelineY);
			if (m_blockLength > 0)
				addBlock();

			updatePixelsXy();
			if (!isSelctionChange)
				zoomFit();

			if (!m_addedFunctionsToClickAreas) {
				for (XtaFunctionBlock block : m_functionList) {
					m_clickAreas.add(block.getTextBlockNode());
				}
				m_addedFunctionsToClickAreas = true;
			}
		}
    }

    protected void updatePixelsXy() {
		if (DEBUG) System.out.printf("Function: updatePixelsXy ");
    	Node pathRoot = null;
    	if (m_selectedNodes.size() == 1) {
    		pathRoot = m_selectedNodes.get(0).getPathRoot();
    	}

    	if (pathRoot == null) {
			m_pixelsX = 0;
			m_pixelsY = 0;
		} else {
			m_pixelsX = (m_borderX * 2) + pathRoot.getFunctionNode().getPixelsX();
			m_pixelsY = ((m_borderY + m_timelineY) * 2) + pathRoot.getFunctionNode().getPixelsY() + m_functionNameY;
		}
    }
    
    private void clearAll() {
        m_functionList.clear();
        m_currentFunction = "";
        m_addedFunctionsToClickAreas = false;
    }

    private void annotateTree(Node node, long numIterations, boolean selected) {
		if ((node == null) || node.isRouteNode() || (m_table == null))
			return;

		m_currentNode = node;
    	selected |= m_selectedNodes.contains(node);

		ViewNode functionNode = node.getFunctionNode();
    	if (node.isBlockNode()) {
            for (XtaInstruction inst : node.getInstructions()) {
                String functionName = m_table.functionName(inst.getPc());

                if (m_currentFunction.length() == 0) {
                    m_currentFunction = functionName;
                }

                if (!m_currentFunction.equals(functionName)) {
                    addBlock();
                    m_currentFunction = functionName;
                }
            	m_blockLength += numIterations;
            }
    	}
    	
    	functionNode.setNumIterations(numIterations);
    	functionNode.setLineWidth(m_lineWidth);
        functionNode.setIsSelected(selected);
        for (Node subNode : node.getChildren()) {
        	long subNumIterations = numIterations;
			if (node.isLoopNode())
				subNumIterations = subNode.getNumIterations() * numIterations;
			
        	annotateTree(subNode, subNumIterations, selected);
        }
	}

    private void addBlock() {
		if (DEBUG) System.out.printf("Function: addBlock " + m_currentFunction + "\n");
		
    	Color color = m_functionColors.get(m_currentFunction);
    	XtaFunctionBlock block = new XtaFunctionBlock(m_currentFunction, color, m_currentNode);
        m_functionList.add(block);
        m_blockLength = 0;
    }

    protected void drawCanvas(GC gc) {
    	gc.setAntialias(SWT.ON);
    	gc.setTextAntialias(SWT.ON);
    	
		Rectangle clientArea = m_canvas.getClientArea();
		m_canvas.setSize(clientArea.width, clientArea.height);
    	gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(0, 0, clientArea.width, clientArea.height);

    	Node pathRoot = null;
    	if (m_selectedNodes.size() == 1) {
    		pathRoot = m_selectedNodes.get(0).getPathRoot();
    	}
        if (pathRoot == null)
        	return;

        drawTimeLine(gc);

		gc.setLineWidth(m_lineWidth);
		int blockX = m_borderX;
		for (XtaFunctionBlock block : m_functionList) {
            int length = block.getOffsetX() - blockX;
			block.draw(m_display, gc, blockX, m_borderY + m_timelineY, length, m_pixelsY, m_scale);
			blockX += length;
		}
		if (m_functionList.size() > 0) {
			XtaFunctionBlock lastBlock = m_functionList.lastElement();
	        int length = lastBlock.getEndX() - blockX;
			lastBlock.draw(m_display, gc, blockX, m_borderY + m_timelineY, length, m_pixelsY, m_scale);
		}
		
		pathRoot.getFunctionNode().draw(m_display, gc, m_scale);
    }

	private void getDebugInfo(){
		m_functionColors.clear();
		m_colorIndex = 0;
		
    	m_table = m_routeInfo.getSymbolTable();
    	if (m_table == null)
    		return;
    	
        Iterator<Map.Entry<Integer, Symbol>> iter =
            m_table.getElfHandler().getFunctionsPcKeyed().entrySet().iterator();
        
        while (iter.hasNext()) {
            Map.Entry<Integer, Symbol> pair = iter.next();
            Symbol symbol = pair.getValue();  
            if (!m_functionColors.containsKey(symbol.getName())){
                m_functionColors.put(symbol.getName(), getColor());
            }
        }
    }
    
    private Color getColor() {
        if (m_colorIndex < m_colors.size()) {
            Color color = m_colors.get(m_colorIndex);
            m_colorIndex++;
            return color;
        }
        Random r = new Random(); 
        RGB rgb = new RGB(r.nextInt(256), r.nextInt(256), r.nextInt(256));
        return new Color(m_display, rgb);
    }

    private void initaliseColors(Display display) {
        m_colorIndex = 0;
        m_colors = new Vector<Color>();

        int increment = 32;
        for (int i = 32; i < 255; i += increment) {
        	RGB r = new RGB(i, 0, 0);
            m_colors.add(new Color(display, r));
        	RGB g = new RGB(0, i, 0);
            m_colors.add(new Color(display, g));
        	RGB b = new RGB(0, 0, i);
            m_colors.add(new Color(display, b));
        }

        for (int r = 32; r < 255; r += increment) {
            for (int g = 32; g < 255; g += increment) {
                for (int b = 32; b < 255; b += increment) {
                	RGB rgb = new RGB(r, g, b);
                    m_colors.add(new Color(display, rgb));
                }
            }
        }
    }
    
    class XtaFunctionBlock {
        private Node m_node;
        private TextBlockNode m_textBlockNode;
        
        public XtaFunctionBlock(String function, Color color, Node node) {
            m_node = node;
            m_textBlockNode = new TextBlockNode(function, color);
        }
        
        public TextBlockNode getTextBlockNode() {
        	return m_textBlockNode;
        }
        
        public int getOffsetX() {
        	return m_node.getFunctionNode().getPixelOffsetX();
        }

        public int getEndX() {
        	return m_node.getFunctionNode().getPixelOffsetX() + m_node.getFunctionNode().getPixelsX();
        }

        public void draw(Display display, GC gc, int x, int y, int length, int height, double scale) {
            m_textBlockNode.setBounds((int)((double)x * scale), y, (int)((double)length * scale) + 1, height, m_pixelsY - m_functionNameY);
            m_textBlockNode.draw(display, gc, scale);
        }
    }

    /*
     * Timeline functions
     */
    private void drawTimeLine(GC gc) {
		Rectangle clientArea = m_canvas.getClientArea();

		gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLUE));
    	gc.setLineStyle(SWT.LINE_DOT);
    	
    	gc.drawLine(0, m_timelineY, clientArea.width, m_timelineY);
    	
    	updateUnits();

    	double timeUnit = 0; 	
    	double timeUnitIncrement = getTimeUnitIncrement();
		for (double x = 0; x < clientArea.width; x += m_pixelsPerTimeUnit * m_scale * timeUnitIncrement) {
        	double time = timeUnit;
        	
        	gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLACK));
        	if (timeUnitIncrement < 0.1)
        		gc.drawText(String.format("%.02f %s", time, m_timeUnits), (int)x, 0);
        	else
        		gc.drawText(String.format("%.01f %s", time, m_timeUnits), (int)x, 0);
       	    
       	    gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLUE));
            gc.drawLine((int)x, m_timelineY, (int)x, m_borderY + m_timelineY);
         	timeUnit += timeUnitIncrement;
        }    	

    	gc.setLineStyle(SWT.LINE_SOLID);
    }
 
	private double getTimeUnitIncrement() {
    	double increment = 1.0;
    	double numPixels = m_pixelsPerTimeUnit * m_scale;
    	while (numPixels > MAX_PIXELS_GAP) {
    		numPixels /= 2.0;
    		increment /= 2.0;
    	}
    	while (numPixels < MIN_PIXELS_GAP) {
    		numPixels *= 2.0;
    		increment *= 2.0;
    	}
    	return increment;
    }

	private void updateUnits() {
    	Node pathRoot = null;
    	if (m_selectedNodes.size() == 1) {
    		pathRoot = m_selectedNodes.get(0).getPathRoot();
    	}

    	if (pathRoot == null) {
			m_timeUnits = "ns";
			m_pixelsPerTimeUnit = 1;
			return;
    	}

    	double timeTaken = pathRoot.getTimeTaken();
		m_pixelsPerTimeUnit = m_pixelsX / timeTaken;

		
		double renderPixels = ((double)m_pixelsX * m_scale);
		double endTime = timeTaken * ((double)m_canvas.getClientArea().width / renderPixels);
		if (endTime > 2000000) {
			m_timeUnits = "ms";
			m_pixelsPerTimeUnit *= 1000 * 1000;
		} else if (endTime > 2000) {
			m_timeUnits = "us";
			m_pixelsPerTimeUnit *= 1000;
		} else {
			m_timeUnits = "ns";
		}
	}
}
