package com.xmos.xeobjectdisplayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import com.xmos.xeobject.AssemblyLine;
import com.xmos.xeobject.Line;

@SuppressWarnings("serial")
public abstract class Displayer extends JPanel {

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
    
    
	public Displayer() {
		super(new BorderLayout());
		init();
	}
	
	private void init(){
	    
        m_lines = new ArrayList<Line>();
	    
	    m_textPane = new XTextPane(this);
	    
        m_document = m_textPane.getStyledDocument();
        m_style = m_document.addStyle("NewStyle", null);
        
        
        m_lineHeaders = new LineHeaders(m_textPane, m_font, m_lines, this, m_style);
        
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
	    
    }
	
	
	public String getTitle() {
		return m_title;
	}
	
	public Font getFont() {
		return m_font;
	}

	public JScrollPane getScrollPane() {
		return m_textPaneScrollPane;
	}
	
	public Style getStyle() {
		return m_style;
	}

	protected abstract void renderFileToDoc() throws BadLocationException;
	protected abstract List<AssemblyLine> getAssembly(Line line);
	protected abstract void setLineFold(Line line, boolean fold);
}
