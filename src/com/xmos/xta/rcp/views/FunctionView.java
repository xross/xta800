package com.xmos.xta.rcp.views;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import org.eclipse.jface.viewers.StructuredSelection;
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
import com.xmos.xta.rcp.views.ViewNode.TextBlockNode;
import com.xmos.xta.rcp.views.ViewNode.ViewNode;

public class FunctionView extends ZoomedCanvasView {
	
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.rcp.views.Function";

    private SymbolTable m_table;
    private Vector<XtaFunctionBlock> m_functionList;
    private boolean m_addedFunctionsToClickAreas;
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
		m_addedFunctionsToClickAreas = false;
        m_functionColors = new HashMap<String, Color>();
        initaliseColors(m_display);

		m_scaleY = false;
		m_pixelsX = 0;
		m_pixelsY = 0;
		m_borderX = 0;
		m_borderY = 10;
		m_lineWidth = 1;
	}

    protected void initialiseTree() {
		if (DEBUG) System.out.printf("Function: initialiseTree\n");			
    	m_routeInfo = XtaDisplayer.getInstance().getRouteInfo();
		if (m_routeInfo != null) {
    		getDebugInfo();
			m_rootNode = m_routeInfo.getRouteNode();
			m_selectedNode = m_routeInfo.getSelectedNode();
			if (m_selectedNode != null) {
				setSelection(new StructuredSelection(m_selectedNode));
			}
		} else {
			m_rootNode = null;
			m_selectedNode = null;
		}
	}

    protected void buildClickAreas() {
		if (DEBUG) System.out.printf("Function: buildClickAreas\n");			
    	m_clickAreas.clear();
    	if (m_rootNode != null)
    		m_rootNode.getFunctionNode().buildClickAreas(m_clickAreas);

    	m_addedFunctionsToClickAreas = false;
    }

    protected void annotateTree(boolean isSelctionChange) {
		if (DEBUG) System.out.printf("Function: annotateTree\n");			
    	clearAll();
    	Node pathRoot = null;
    	if (m_selectedNode != null) {
    		pathRoot = m_selectedNode.getPathRoot();
    	}
    	
		if (pathRoot == null) {
			m_pixelsX = 0;
			m_pixelsY = 0;
		} else {
			annotateTree(pathRoot, 1, false);

			pathRoot.getFunctionNode().annotateXy(m_borderX, m_borderY);
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
    	if (m_selectedNode != null) {
    		pathRoot = m_selectedNode.getPathRoot();
    	}

    	if (pathRoot == null) {
			m_pixelsX = 0;
			m_pixelsY = 0;
		} else {
			m_pixelsX = (m_borderX * 2) + pathRoot.getFunctionNode().getPixelsX();
			m_pixelsY = (m_borderY * 2) + pathRoot.getFunctionNode().getPixelsY() + m_functionNameY;
		}

		if (DEBUG) System.out.printf("%d,%d\n", m_pixelsX, m_pixelsY);			
    }
    
    private void clearAll() {
        m_functionList.clear();
        m_nodeOffset = 0;
        m_currentFunction = "";
        m_previousFunction = "";
        m_addedFunctionsToClickAreas = false;
    }

    private void annotateTree(Node node, int numIterations, boolean selected) {
		if ((node == null) || node.isRouteNode())
			return;

		m_currentNode = node;
    	selected |= (node == m_selectedNode);

		ViewNode functionNode = node.getFunctionNode();
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
    	}
    	
    	functionNode.setNumIterations(numIterations);
    	functionNode.setLineWidth(m_lineWidth);
        functionNode.setIsSelected(selected);
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
    			m_currentNode, m_nodeOffset);
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

    	Node pathRoot = null;
    	if (m_selectedNode != null) {
    		pathRoot = m_selectedNode.getPathRoot();
    	}
        if (pathRoot == null)
        	return;
        
		gc.setLineWidth(m_lineWidth);
		int blockX = m_borderX;
		for (XtaFunctionBlock block : m_functionList) {
			blockX += block.draw(m_display, gc, blockX, m_borderY, m_pixelsY, m_scale);
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
        Random r = new Random(); 
        RGB rgb = new RGB(r.nextInt(256), r.nextInt(256), r.nextInt(256));
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
        private Node m_node;
        private int m_offset;
        private TextBlockNode m_textBlockNode;
        
        public XtaFunctionBlock(String function, Color color, Node node, int offset) {
            m_node = node;
            m_offset = offset;
            
            m_textBlockNode = new TextBlockNode(function, color);
        }
        
        TextBlockNode getTextBlockNode() {
        	return m_textBlockNode;
        }

        public int draw(Display display, GC gc, int x, int y, int height, double scale) {

            int length = (m_node.getFunctionNode().getPixelOffsetX() + m_offset) - x;
            
            m_textBlockNode.setBounds((int)((double)x * scale), y, (int)((double)length * scale) + 1, height, m_pixelsY - m_functionNameY);
            m_textBlockNode.draw(display, gc, scale);

            return length;
        }
    }
}
