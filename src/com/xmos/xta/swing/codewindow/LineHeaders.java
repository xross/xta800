package com.xmos.xta.swing.codewindow;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Style;
import com.xmos.xeobject.Line;
import com.xmos.xeobject.Line.DisplayType;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Reference;
import com.xmos.xta.swing.XtaXeObjectDisplayer;

public class LineHeaders extends JTextPane implements MouseListener{

    private static final long   serialVersionUID = 1L;
    private ArrayList<Line>     m_lines;
    private Style               m_style;
    private Displayer           m_displayer;
    private static final int    LINE_LENGTH = 8;
    private JTextPane           m_this;
    private Document            m_document;
    private XtaXeObjectDisplayer m_parent;
    private Engine              m_engine;

    public LineHeaders(XTextPane textPane, Font font, ArrayList<Line> lines,  
            Displayer displayer, Style style, XtaXeObjectDisplayer parent, Engine engine) {
        super();
        addMouseListener(this);
        this.setPreferredSize(new Dimension(80, 10));
        this.setEnabled(false);
        this.setDisabledTextColor(Color.black);
        m_lines = lines;
        m_engine = engine;
        m_parent = parent;
        m_style = style;
        m_displayer = displayer;
        m_this = this;
        this.setEditable(false);
        m_document = this.getStyledDocument();
        renderFileToDoc();
    }

    public void renderFileToDoc(){
        //also have to add the code folding buttons and mouse over highlighting
        for(Line line : m_lines){
            insertLine(line, m_document.getLength());
        }
    }

    public void insertLine(Line line, int location){
        String lineNumber ="";
        if(line.getLineNumberType()==DisplayType.decimal){
            lineNumber = Integer.toString(line.getLineNumber());
        } else if(line.getLineNumberType()==DisplayType.hex){
            lineNumber = "0x" + Integer.toHexString(line.getLineNumber());
        } else if(line.getLineNumberType()==DisplayType.none){
            lineNumber = "";
        } else {
            assert(false);
        }
        while(lineNumber.length()<LINE_LENGTH){
            lineNumber+=" ";
        } 
        assert(lineNumber.length() == 8);
        try {
            m_document.insertString(location, lineNumber+"\n", m_style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        LineNumberImage lni = new LineNumberImage(line, m_lines, this, m_parent, m_engine);
        try {
            this.getHighlighter().addHighlight(0, 0, lni);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

    }

    public void insertLines(ArrayList<Line> lines, int location){
        StringBuffer sb = new StringBuffer("");

        try {
            for(Line line : lines){
                String lineNumber = "0x" + Integer.toHexString(line.getLineNumber());
                while(lineNumber.length()<LINE_LENGTH){
                    lineNumber+=" ";
                }
                sb.append(lineNumber);
                sb.append("\n");
                LineNumberImage lni = new LineNumberImage(line, m_lines, this, m_parent, m_engine);
                this.getHighlighter().addHighlight(0, 0, lni);
            }
            m_document.insertString(location, sb.toString(), m_style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

    }


    public void remove(int start, int ammount){
        try {
            m_document.remove(start, ammount);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


    public void mousePressed(MouseEvent e) {
        Document doc = m_this.getDocument();
        DefaultStyledDocument hdoc = (DefaultStyledDocument) doc;
        int pos = m_this.viewToModel(e.getPoint());
        int lineNo =hdoc.getParagraphElement(pos).getEndOffset()/(LINE_LENGTH+1);
        final Line line = m_lines.get(lineNo-1);
        if(e.getButton()==1){
            if(e.getX()> this.getWidth() - LineNumberImage.circleWidth-5){
                if(line.isFoldable()){
                    foldChange(line); 
                }
            } 
        } else if(e.getButton()==3){
            final JPopupMenu popup = new JPopupMenu();
            if(line.getLineNumberType()==DisplayType.decimal && line.isFoldable()){
                
                JMenuItem startLine = new JMenuItem("Set as start line");
                startLine.addMouseListener(new MouseAdapter(){
                    public void mousePressed(MouseEvent e) {
                        m_parent.setM_startLine(String.valueOf(line.getLineNumber()));
                    }
                });
                popup.add(startLine);
                JMenuItem endLine = new JMenuItem("Set as end line");
                endLine.addMouseListener(new MouseAdapter(){
                    public void mousePressed(MouseEvent e) {
                        m_parent.setM_endLine(String.valueOf(line.getLineNumber()));
                    }
                });
                popup.add(endLine);

            } else if(line.getLineNumberType()==DisplayType.hex){
                JMenuItem startPc = new JMenuItem("Set as start PC");
                startPc.addMouseListener(new MouseAdapter(){
                    public void mousePressed(MouseEvent e) {
                        m_parent.setM_startPc( Integer.toHexString(line.getLineNumber()));

                    }
                });
                popup.add(startPc);
                JMenuItem endPc = new JMenuItem("Set as end PC");
                endPc.addMouseListener(new MouseAdapter(){
                    public void mousePressed(MouseEvent e) {
                        m_parent.setM_endPc( Integer.toHexString(line.getLineNumber()));

                    }
                });
                popup.add(endPc);
            } 
            if(line.isPausable()){
                JMenuItem setPausable = new JMenuItem("Set pause time");
                setPausable.addMouseListener(new MouseAdapter(){
                    public void mousePressed(MouseEvent e) {
                        m_parent.setPausable(line);

                    }
                });
                popup.add(setPausable);
            }

            //exclusions
            JMenu exclusionsMenu = new JMenu("Exclusions");
            JMenuItem removeAllExclusions = new JMenuItem("Remove all exclusions");
            removeAllExclusions.addMouseListener(new MouseAdapter(){
                public void mousePressed(MouseEvent e) {
                    m_engine.removeAllExclusions();
                }
            });
            exclusionsMenu.add(removeAllExclusions);
            if(line.getLineNumberType() == DisplayType.hex){
                JMenuItem setExclusion = new JMenuItem("Set exclusion");
                setExclusion.addMouseListener(new MouseAdapter(){
                    public void mousePressed(MouseEvent e) {
                        m_engine.setExclusion(new Reference(line.getLineNumber()));

                    }
                });
                exclusionsMenu.add(setExclusion);
                JMenuItem removeExclusion = new JMenuItem("Remove exclusion");
                removeExclusion.addMouseListener(new MouseAdapter(){
                    public void mousePressed(MouseEvent e) {
                        m_engine.removeExclusion(new Reference(line.getLineNumber()));
                    }
                });
                exclusionsMenu.add(removeExclusion);
            }

            popup.add(exclusionsMenu);
            JMenuItem unfoldAll = new JMenuItem("Unfold all");
            unfoldAll.addMouseListener(new MouseAdapter(){
                public void mousePressed(MouseEvent e) {
                    int i=m_lines.size()-1;
                    while(i>=0){
                        m_displayer.unfoldLine(m_lines.get(i));
                        i--;
                    }
                }
            });
            popup.add(unfoldAll);
            JMenuItem foldAll = new JMenuItem("Fold all");
            foldAll.addMouseListener(new MouseAdapter(){
                public void mousePressed(MouseEvent e) {
                    int i=0;
                    while(i<m_lines.size()){
                        m_displayer.foldLine(m_lines.get(i));
                        i++;
                    }
                }
            });
            popup.add(foldAll);
            popup.setOpaque(true);
            popup.setLightWeightPopupEnabled(true);
            popup.show( (JComponent)e.getSource(), e.getX(), e.getY() );
            popup.addPropertyChangeListener(new PropertyChangeListener(){
                public void propertyChange(PropertyChangeEvent evt) {
                   if(!popup.isVisible()) m_this.repaint();
                }
            });

        }
    }

    private void foldChange(Line line){

        if(!line.getShowChildern()){
            m_displayer.unfoldLine(line);
        } else {
            m_displayer.foldLine(line);
        }
    }

    public void mouseEntered(MouseEvent arg0) {}
    public void mouseExited(MouseEvent arg0) {}
    public void mouseClicked(MouseEvent arg0) {	}
    public void mouseReleased(MouseEvent arg0) {}


}

