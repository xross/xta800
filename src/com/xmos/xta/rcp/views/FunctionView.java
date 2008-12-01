package com.xmos.xta.rcp.views;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
import com.xmos.xta.rcp.XtaDisplayer;
import com.xmos.xta.rcp.nodes.Node;
import com.xmos.xta.rcp.views.ViewNode.FunctionBlockNode;
import com.xmos.xta.rcp.views.ViewNode.FunctionLoopNode;
import com.xmos.xta.rcp.views.ViewNode.ViewNode;
import com.xmos.xta.rcp.views.ViewNode.FunctionPathNode;

public class FunctionView extends ZoomedCanvasView {
	
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.rcp.views.Function";

    private SymbolTable m_table;
    private Vector<XtaFunctionBlock> m_functionList;
    private int m_nodeOffset;
    private int m_blockLength;
    private Node m_currentNode;
    private String m_currentFunction;
    private String m_previousFunction;

    private int m_colorIndex;
    private Vector<Color> m_colors;
    private HashMap<String, Color> m_functionColors;

    private int m_functionNameY = 30;
    
	public FunctionView() {
		super();
		if (DEBUG) System.out.printf("Function()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
		
		m_functionList = new Vector<XtaFunctionBlock>();
        m_functionColors = new HashMap<String, Color>();
        initaliseColors(m_display);

		m_scale = 1.0;
		m_scaleY = false;
		m_pixelsX = 0;
		m_pixelsY = 0;
		m_borderX = 0;
		m_borderY = 10;
		m_lineWidth = 1;
	}

    private Node getPathRoot(Node start) {
    	if ((start == null) || start.isRouteNode()) 
    		return null;
    	
    	Node node = start;
    	while (!node.getParentNode().isRouteNode()) {
    		node = node.getParentNode();
    	}
		return node;
	}

    protected void initialiseTree() {
    	clearAll();
		m_routeInfo = XtaDisplayer.getInstance().getRouteInfo();
		if (m_routeInfo != null) {
			m_rootNode = m_routeInfo.getRouteNode();
			m_selectedNode = m_routeInfo.getSelectedNode();
			getDebugInfo();
		} else {
			m_rootNode = null;
			m_selectedNode = null;
		}
	}

    protected void annotateTree(boolean isSelctionChange) {
    	clearAll();
        Node pathRoot = getPathRoot(m_selectedNode);
		if (pathRoot == null) {
			m_pixelsX = 0;
			m_pixelsY = 0;
		} else {
			annotateTree(pathRoot, 1, false);

			m_clickAreas.clear();
			pathRoot.getFunctionNode().annotateXy(m_borderX, m_borderY, m_clickAreas);
			if (m_blockLength > 0)
				addBlock();
		}
		updatePixelsXy();
		if (!isSelctionChange)
			zoomFit();
    }

    protected void updatePixelsXy() {
        Node pathRoot = getPathRoot(m_selectedNode);
		if (pathRoot == null) {
			m_pixelsX = 0;
			m_pixelsY = 0;
		} else {
			m_pixelsX = (m_borderX  * 2) + pathRoot.getFunctionNode().getPixelsX();
			m_pixelsY = (m_borderY * 2) + pathRoot.getFunctionNode().getPixelsY() + m_functionNameY;
		}
    }
    
    private void clearAll() {
        m_functionList.clear();
        m_nodeOffset = 0;
        m_currentFunction = "";
        m_previousFunction = "";
    }

    private void annotateTree(Node node, int numIterations, boolean selected) {
		if ((node == null) || node.isRouteNode())
			return;

		m_currentNode = node;
    	selected |= (node == m_selectedNode);

		ViewNode functionNode = null;
    	if (node.isBlockNode()) {
            for (XtaInstruction inst : node.getInstructions()) {
                String functionName = m_table.functionName(inst.getPc());

                if (m_currentFunction.length() == 0) {
                    m_currentFunction = functionName;
                    m_previousFunction = functionName;
                }

                if (m_currentFunction.equals(functionName) == false) {
                    if (m_nodeOffset > 0) {
                        addBlock();
                        m_currentFunction = functionName;
                    } else {
                        m_currentFunction = m_previousFunction;
                    }
                }
            	m_nodeOffset += numIterations;
            	m_blockLength += numIterations;
            }
    		functionNode = new FunctionBlockNode(node, numIterations, m_lineWidth);
    	} else if (node.isPathNode()) {
    		functionNode = new FunctionPathNode(node, numIterations, m_lineWidth);
    	} else if (node.isLoopNode()) {
    		functionNode = new FunctionLoopNode(node, numIterations, m_lineWidth);
    	}

        functionNode.setIsSelected(selected);
    	node.setFunctionNode(functionNode);
        for (Node subNode : node.getNodes()) {
        	m_nodeOffset = 0;
        	
        	int subNumIterations = numIterations;
			if (node.isLoopNode())
				subNumIterations = subNode.getNumIterations() * numIterations;
			
        	annotateTree(subNode, subNumIterations, selected);
        }
	}

    private void addBlock() {
    	Color color = m_functionColors.get(m_currentFunction);
    	XtaFunctionBlock block = new XtaFunctionBlock(m_currentFunction, color,
    			m_currentNode, m_nodeOffset, m_blockLength);
        m_functionList.add(block);
        m_blockLength = 0;
        m_previousFunction = m_currentFunction;
    }

    protected void drawCanvas(GC gc) {
    	gc.setAntialias(SWT.ON);
    	gc.setTextAntialias(SWT.ON);
    	
		Rectangle clientArea = m_canvas.getClientArea();
		m_canvas.setSize(clientArea.width, clientArea.height);
    	gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(0, 0, clientArea.width, clientArea.height);

        Node pathRoot = getPathRoot(m_selectedNode);
        if (pathRoot == null)
        	return;
        
		gc.setLineWidth(m_lineWidth);
		int blockX = m_borderX;
		for (XtaFunctionBlock block : m_functionList) {
			blockX += block.draw(m_display, gc, blockX, m_borderY, m_pixelsY - m_functionNameY, m_pixelsY, m_scale);
		}
		pathRoot.getFunctionNode().draw(m_display, gc, m_scale);
    }
    
	private void getDebugInfo(){
    	m_table = m_routeInfo.getSymbolTable();
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
        RGB rgb = new RGB((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
        return new Color(m_display, rgb);
    }

    private void initaliseColors(Display display) {
        m_colorIndex = 0;
        m_colors = new Vector<Color>();

        int increment = 32;
        for (int r = increment * 2; r < 255; r += increment) {
            for (int g = increment * 2; g < 255; g += increment) {
                for (int b = increment * 2; b < 255; b += increment) {
                	RGB rgb = new RGB(r, g, b);
                    m_colors.add(new Color(display, rgb));
                }
            }
        }
    }
    
    class XtaFunctionBlock {
        private String m_function;
        private Color m_color;
        private Node m_node;
        private int m_offset;
        public XtaFunctionBlock(String function, Color color, Node node, int offset, 
        		int length) {
            m_function = function;
            m_color = color;
            m_node = node;
            m_offset = offset;
        }
        public String getFunction() {
            return m_function;
        }
    	
        public int draw(Display display, GC gc, int x, int y, int textBase, int height, double scale) {

            int length = (m_node.getFunctionNode().getPixelOffsetX() + m_offset) - x;

            gc.setBackground(m_color);
        	gc.fillRectangle((int)((double)x * scale), y, (int)((double)length * scale) + 1, height);

            gc.setForeground(m_display.getSystemColor(SWT.COLOR_WHITE));
            gc.setBackground(m_color);
            double functionNameWidth = gc.getFontMetrics().getAverageCharWidth() * m_function.length();

            if (((double)length * scale) > functionNameWidth){
                gc.drawString(m_function,
                        (int)(((((double)x + ((double)length / 2.0)) * scale) - (functionNameWidth)/2.0)),
                        textBase);
            }
            
            return length;
        }
    }
}
