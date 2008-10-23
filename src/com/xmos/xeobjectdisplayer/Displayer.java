package com.xmos.xeobjectdisplayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import com.xmos.xeobject.Line;
import com.xmos.xeobject.Line.DisplayType;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.gui.XtaXeObjectDisplayer;

public abstract class Displayer extends JPanel {

    private static final long serialVersionUID = 1L;
    protected XTextPane         m_textPane;
	protected LineHeaders       m_lineHeaders;
    protected StyledDocument    m_document;
    protected Style             m_style;
    protected String            m_title = "Displayer";
    protected ArrayList<Line>   m_lines;
    protected SideHighlighter   m_sideHighlighter;
    protected Font              m_font = new Font("Courier", Font.PLAIN, 12);
    protected JScrollPane       m_textPaneScrollPane;
    protected int               m_highlightId=0;
    protected SourceColourer    m_sourceColourer;
    protected AssemblyColourer  m_assemblyColourer;
    protected Engine            m_engine;
    protected XtaXeObjectDisplayer m_parent;
   
    protected HashMap<Integer, Line> m_linesToPcs;
    
	public Displayer(Engine engine, XtaXeObjectDisplayer parent) {
		super(new BorderLayout());
		m_engine = engine;
		m_parent = parent;
		
		init();
	}
	
	private void init(){
	    m_linesToPcs = new HashMap<Integer, Line>();
        m_lines = new ArrayList<Line>();
	    m_textPane = new XTextPane(this);
	    m_textPane.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e) {
                JTextPane editor = (JTextPane) e.getSource();
                Document doc = editor.getDocument();
                DefaultStyledDocument hdoc = (DefaultStyledDocument) doc;
                int pos = editor.viewToModel(e.getPoint());
                System.out.println(hdoc.getParagraphElement(pos));
            }
	    });
        m_document = m_textPane.getStyledDocument();
        
        m_style = m_document.addStyle("NewStyle", null);

        m_lineHeaders = new LineHeaders(m_textPane, m_font, m_lines, this, m_style, m_parent);
        
        StyleConstants.setFontFamily(m_style, m_font.getFamily());
        StyleConstants.setFontSize(m_style, m_font.getSize());
        
        m_textPaneScrollPane = new JScrollPane(m_textPane, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        m_textPaneScrollPane.setRowHeader(new JViewport());
        m_textPaneScrollPane.getRowHeader().setView(m_lineHeaders);
        m_textPaneScrollPane.getViewport().setBackground(Color.white);    

        m_sideHighlighter = new SideHighlighter(this, m_lines);
        
        this.add(m_textPaneScrollPane, BorderLayout.CENTER);
        this.add(m_sideHighlighter, BorderLayout.EAST);
        
        m_sourceColourer = new SourceColourer(m_document, m_font);
        m_assemblyColourer = new AssemblyColourer(m_document, m_font);
	}
	
	public void unfoldLine(Line line){
        if(line.getShowChildern()){
            // nothing to do
            return;
        }
        int location = 0;
        int headerLocation = 0;
        int i=0;
        for(;i<m_lines.indexOf(line)+1;i++ ){
            Line l_line = m_lines.get(i);
            location += l_line.getText().length() +1;
            headerLocation+=9;
        }
        final Point startPosition = m_textPaneScrollPane.getViewport().getViewPosition();
        for(Line childLine : line.getChildern()){
            insertLine(childLine, location);
            m_lineHeaders.insertLine(childLine, headerLocation);
            location+=childLine.getText().length()+1;
            headerLocation+=9;
        }
        m_lines.addAll(m_lines.indexOf(line)+1, line.getChildern());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                m_textPaneScrollPane.getViewport().setViewPosition(startPosition);
            }});
        line.setShowChildern(true);
    }
    
    
    
    public void foldLine(Line line){
        if(line.getShowChildern() == false){
            // nothing to do
            return;
        }
        
        int removeAmount =0;
        
        ArrayList<Line> linesToRemove = line.getChildern();
        for(Line l_line : linesToRemove){
            removeAmount+=l_line.getText().length() +1;
        }
        
        int location = 0;
        int headerLocation = 0;
        int i=0;
        for(;i<m_lines.indexOf(line)+1;i++ ){
            Line l_line = m_lines.get(i);
            location += l_line.getText().length() +1;
            headerLocation+=9;
        }
        
        for(Line l_line : linesToRemove){
            m_lines.remove(l_line);
            m_linesToPcs.remove(l_line);
        }

        final Point startPosition = m_textPaneScrollPane.getViewport().getViewPosition();
        
        m_lineHeaders.remove(headerLocation, linesToRemove.size()*9);
        
        try {
            
            m_document.remove(location, removeAmount);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                m_textPaneScrollPane.getViewport().setViewPosition(startPosition);
            }});
        line.setShowChildern(false);
        
    }

    protected void insertLine(Line line, int index){
        String s = line.getText() + "\n";
        try {
            m_document.insertString(index, s, m_style);
            m_linesToPcs.put(line.getLineNumber(), line);
            if(line.getLineNumberType() == DisplayType.decimal){
                m_sourceColourer.colourLine(index, s.length());
            } else if(line.getLineNumberType() == DisplayType.hex){
                m_assemblyColourer.colourLine(index, s.length());
            } 
            m_textPane.getHighlighter().addHighlight(0, 0, line.getLineHighlighter(m_lines, m_textPane));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        m_lines.add(line);
    }
	
	public void snapToLine(int lineNumber) {
		double value = (double)m_textPane.getHeight() / (double)m_lines.size();
		m_textPaneScrollPane.getVerticalScrollBar().setValue((int)(value * lineNumber));
	}
	
	/*
	public void addHyperlink(URL url, String text) {
	  try {
	    SimpleAttributeSet attrs = new SimpleAttributeSet();
	    StyleConstants.setUnderline(attrs, true);
	    StyleConstants.setForeground(attrs, Color.blue);
	    attrs.addAttribute(m_linkId, new Integer(++m_linkId));
	    attrs.addAttribute(HTML.Attribute.HREF, url.toString());
	    m_document.insertString(m_document.getLength(), text, attrs);
	  }
	  catch (BadLocationException e) {
	    e.printStackTrace(System.err);
	  }        
	}
*/

	public void removeHighlights() {
	    m_textPane.getHighlighter().removeAllHighlights();
	    for(Line line : m_lines){
	        line.setColour(Color.white);
	    }
    }
	
	public void highlightPCs(ArrayList<Integer> pcs, Color colour){
	    for(int pc : pcs){
	        highlightPC(pc, colour);
	    }
	    this.repaint();
	}
	
	public void highlightPC(int pc, Color colour){
	    m_linesToPcs.get(pc).setColour(colour);
	}
	
	public String getTitle() {
		return m_title;
	}

	public JScrollPane getScrollPane() {
		return m_textPaneScrollPane;
	}

	protected abstract void renderFileToDoc();
}
