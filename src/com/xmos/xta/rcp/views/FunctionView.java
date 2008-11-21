package com.xmos.xta.rcp.views;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

import com.xmos.elfhandler.Symbol;
import com.xmos.xeloader.SymbolTable;
import com.xmos.xta.engine.XtaInstruction;
import com.xmos.xta.rcp.XtaDisplayer;
import com.xmos.xta.rcp.nodes.Node;

public class FunctionView extends ViewPart implements IPropertyChangeListener, 
                                                      ISelectionListener {
    public static final boolean DEBUG = false;
    public static final String ID = "com.xmos.xta.rcp.views.Function";

    private Display m_display;
    private Canvas m_canvas;

    private SymbolTable m_table;
    private Node m_selectedNode;
    
    private Vector<XtaCodeBlock> m_blockList;
    private int m_pathDepth;
    private int m_pathLength;
    private int m_blockLength;
    private int m_blockDepth;
    private int m_blockPausable;
    private int m_blockFNops;
    private String m_currentFunction;
    private String m_previousFunction;
    private int m_currentDepth;

    private int m_colorIndex;
    private Vector<Color> m_colors;
    private HashMap<String, Color> m_functionColors;

	public FunctionView() {
		if (DEBUG) System.out.printf("Function()\n");			
		XtaDisplayer.getInstance().addPropertyChangeListener(this);
		
		m_blockList = new Vector<XtaCodeBlock>();
        m_functionColors = new HashMap<String, Color>();
	}

	public void createPartControl(Composite parent) {
		if (DEBUG) System.out.printf("Function: createPartControl\n");
	    m_display = parent.getDisplay();

	    getSite().getPage().addSelectionListener(this);
        initaliseColors(m_display);

	    m_canvas = new Canvas(parent, SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND);
	    m_canvas.addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	        	drawFunctionPanel(e.gc);
	        }
	    });
	}

	public void setFocus() {
		if (DEBUG) System.out.printf("Function: setFocus\n");			
	    m_canvas.redraw();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (DEBUG) System.out.printf("Function: propertyChange: %s\n", event.getProperty());
	}	
	
    public void selectionChanged(IWorkbenchPart part, ISelection sel) {
		if (DEBUG) System.out.printf("Function: selectionChanged\n");			
	    if (sel instanceof IStructuredSelection) {	            
            Object object = ((IStructuredSelection)sel).getFirstElement();
            if (object instanceof Node) {
                getDebugInfo();
                m_selectedNode = (Node)object;
                clearAll();
                Node pathRoot = getPathRoot(XtaDisplayer.getInstance().getRouteInfo().getRouteNode(), m_selectedNode);
                if (pathRoot != null)
                	addPath(pathRoot, 1, false, false);
                m_canvas.redraw();
            }
        }
    }

    private Node getPathRoot(Node root, Node start) {
    	if (start == root) 
    		return null;
    	
    	Node node = start;
    	while (node.getParentNode() != root) {
    		node = node.getParentNode();
    	}
		return node;
	}

	private boolean addPath(Node node, int numIterations,
            boolean previousBlockSelected, boolean currentBlockSelected) {
        if (numIterations == 0)
            return previousBlockSelected;
        
        // Set the current selected 
        if (node == m_selectedNode) {
            currentBlockSelected = true;
        }

        for (XtaInstruction inst : node.getInstructions()) {
            String functionName = m_table.functionName(inst.getPc());

            if (m_blockLength == 0) {
                previousBlockSelected = currentBlockSelected;
            }
                
            if (m_currentFunction.length() == 0) {
                m_currentFunction = functionName;
                m_previousFunction = functionName;
            }

            if ((m_currentFunction.equals(functionName) == false) || 
                (m_currentDepth != m_blockDepth) ||
                (currentBlockSelected != previousBlockSelected)) {
                
                if (m_blockLength > 0) {
                    addBlock(previousBlockSelected);
                    m_currentFunction = functionName;
                    previousBlockSelected = currentBlockSelected;
                } else {
                    m_currentFunction = m_previousFunction;
                }
            }
            m_blockLength += numIterations;
            m_blockDepth = m_currentDepth;
            
            if (inst.canPause())
                m_blockPausable++;

            if (inst.isFnop())
                m_blockFNops++;
        }

        boolean subBlockSelected = currentBlockSelected;
        for (Node subNode : node.getNodes()) {
        	if (node.isPathNode())
        		numIterations = node.getNumIterations();
        	
            if (node.isLoopNode()) m_currentDepth++;
            subBlockSelected = addPath(subNode, numIterations, subBlockSelected, currentBlockSelected);
            if (node.isLoopNode()) m_currentDepth--;
        }
        
        if (m_blockLength > 0) {
            addBlock(currentBlockSelected);
        }
        return currentBlockSelected;
    }

    private void addBlock(boolean blockSelected) {
        m_blockList.add(new XtaCodeBlock(m_currentFunction, m_blockLength,
                m_blockDepth, m_blockPausable,
                m_blockFNops, blockSelected));
        m_pathLength += m_blockLength;
        m_blockLength = 0;
        m_blockPausable = 0;
        m_blockFNops = 0;
        if (m_currentDepth > m_pathDepth)
            m_pathDepth = m_currentDepth;
        m_previousFunction = m_currentFunction;
    }

    private void drawFunctionPanel(GC gc){
        Rectangle clientArea = m_canvas.getClientArea();
        int totalWidth = clientArea.width;
        int totalHeight = clientArea.height;

        gc.setBackground(m_display.getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(0, 0, totalWidth, totalHeight);
        
        if (m_blockList.isEmpty())
            return;

        int borderY = totalHeight / 6;
        int y = borderY;
        int blockHeight = totalHeight - (2 * borderY);
        double instWidth = (double)(totalWidth - 2) / (double)m_pathLength;

        int x = 0;
        boolean wasSelected = true;
        for (int i = 0; i < m_blockList.size(); i++) {
            XtaCodeBlock block = m_blockList.get(i);
            boolean selected = block.getSelected();
            
            boolean drawLeft = (!selected && wasSelected);
            boolean drawRight = (!selected &&
                    ((i == (m_blockList.size() - 1)) || m_blockList.get(i+1).getSelected()));
            wasSelected = selected;
            
            x += drawFunctionBlock(gc, block, x, y, blockHeight, totalHeight,
                    instWidth, true, drawLeft, drawRight);
        }
        
    }
    
    private int drawFunctionBlock(GC gc, XtaCodeBlock block,
            int x, int y, int height, int totalHeight,
            double instWidth, boolean drawDetails,
            boolean drawLeft, boolean drawRight) {
        
        Color color = m_functionColors.get(block.getFunction());
        gc.setBackground(color);
        
        Color pausableColor = lighter(color);
        Color fnopColor = lighter(pausableColor);
        Color borderColor = darker(color);

        int width = (int)(Math.ceil(instWidth * block.getLength()));
        gc.fillRectangle(x, y, width, height);
        gc.setForeground(borderColor);
        gc.setBackground(borderColor);
        gc.drawRectangle(x, y, width, height);
        
        if (drawDetails && (block.getPausable() > 0)) {
            // Show the pausable
            int pausableWidth = (int)Math.ceil(instWidth * block.getPausable()) - 1;
            gc.setBackground(pausableColor);
            gc.fillRectangle(x, y + (height / 3), pausableWidth, (height / 3) - 1);
            gc.setBackground(m_display.getSystemColor(SWT.COLOR_BLACK));
            gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLACK));
            gc.drawRectangle(x, y + (height / 3), pausableWidth, (height / 3) - 1);
        }

        if (drawDetails && (block.getFNops() > 0)) {
            // Show the FNops
            int fnopWidth = (int)Math.ceil(instWidth * block.getFNops()) - 1;
            gc.setBackground(fnopColor);
            gc.fillRectangle(x, y + ((2 * height) / 3), fnopWidth, (height / 3) - 1);
            gc.setBackground(m_display.getSystemColor(SWT.COLOR_BLACK));
            gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLACK));
            gc.drawRectangle(x, y + ((2 * height) / 3), fnopWidth, (height / 3) - 1);
        }
        
        gc.setForeground(m_display.getSystemColor(SWT.COLOR_WHITE));
        gc.setBackground(color);
        String functionName = block.getFunction();
        double functionNameWidth = gc.getFontMetrics().getAverageCharWidth() * functionName.length();
        double functionNameHeight = gc.getFontMetrics().getHeight();

        if (width > (int)functionNameWidth){
            gc.drawString(functionName,
                    (int)(x + width/2.0 - functionNameWidth/2.0),
                    (int)(y + height/2.0 + functionNameHeight/2.0));
        }

        if (!block.getSelected()) {
            gc.setAlpha(100);
            gc.setForeground(m_display.getSystemColor(SWT.COLOR_GRAY));
            gc.setBackground(m_display.getSystemColor(SWT.COLOR_GRAY));
            gc.fillRectangle(x, 0, width, totalHeight);
            gc.setAlpha(255);
        }
        if (drawLeft) {
            gc.setBackground(m_display.getSystemColor(SWT.COLOR_BLACK));
            gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLACK));
            gc.drawLine(x, 0, x, totalHeight);
        }
        if (drawRight) {
            gc.setBackground(m_display.getSystemColor(SWT.COLOR_BLACK));
            gc.setForeground(m_display.getSystemColor(SWT.COLOR_BLACK));
            gc.drawLine(x + width, 0, x + width, totalHeight);
        }

        pausableColor.dispose();
        fnopColor.dispose();
        borderColor.dispose();
        return width;
    }

    private Color darker(Color color) {
    	RGB rgb = color.getRGB();
    	float[] hsb = rgb.getHSB();
    	hsb[2] = (float) (hsb[2] / 1.5); // Drop brightness
    	RGB newRgb = new RGB(hsb[0], hsb[1], hsb[2]);
    	return new Color(color.getDevice(), newRgb);
	}

	private Color lighter(Color color) {
    	RGB rgb = color.getRGB();
    	float[] hsb = rgb.getHSB();
    	hsb[2] = (float) (1.0 - ((1.0 - hsb[2]) / 2.0)); // Increase brightness
    	RGB newRgb = new RGB(hsb[0], hsb[1], hsb[2]);
    	return new Color(color.getDevice(), newRgb);
	}

	private void getDebugInfo(){
    	m_table = XtaDisplayer.getInstance().getRouteInfo().getSymbolTable();
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
    
    private void clearAll() {
        m_blockList.clear();
        m_pathDepth = 0;
        m_pathLength = 0;
        m_blockLength = 0;
        m_blockDepth = 0;
        m_blockPausable = 0;
        m_blockFNops = 0;
        
        m_currentFunction = "";
        m_previousFunction = "";
        m_currentDepth = 0;
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

    class XtaCodeBlock {
        private String m_function;
        private int m_length;
        private int m_pausable;
        private int m_fNops;
        private int m_depth;
        private boolean m_selected;
        public XtaCodeBlock(String function, int length,
                int depth, int pausable, int fNops, boolean selected) {
            m_function = function;
            m_length = length;
            m_depth = depth;
            m_pausable = pausable;
            m_fNops = fNops;
            m_selected = selected;
        }
        public String getFunction() {
            return m_function;
        }
        public int getLength() {
            return m_length;
        }
        public int getDepth() {
            return m_depth;
        }
        public int getPausable() {
            return m_pausable;
        }
        public int getFNops() {
            return m_fNops;
        }
        public boolean getSelected() {
            return m_selected;
        }
    }
}
