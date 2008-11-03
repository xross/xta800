package com.xmos.xta.codewindow;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import com.xmos.xeobject.Line;
import com.xmos.xeobject.SourceFile;
import com.xmos.xeobject.SourceLine;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.gui.XtaXeObjectDisplayer;

public class SourceDisplayer extends Displayer {

    private static final long   serialVersionUID = 1L;
    private SourceFile          m_sourceFile;
    private HashMap<Integer, Line> m_pcsToLines;

    public SourceDisplayer(SourceFile sourceFile, Engine engine, XtaXeObjectDisplayer parent) {
        super(engine, parent);
        m_title = sourceFile.getFile().getName();
        m_sourceFile = sourceFile;
        m_pcsToLines = new HashMap<Integer, Line>();
        renderFileToDoc();
        m_lineHeaders.renderFileToDoc();
        for(SourceLine sourceLine : m_sourceFile.getSourceLines()){
            for(Line line : sourceLine.getChildern()){
                m_pcsToLines.put(line.getLineNumber(), line);
            }
        }
    }

    public void renderFileToDoc()  {
        assert(m_sourceFile!= null);
        assert(m_sourceFile.getSourceLines()!=null);
        StringBuffer st = new StringBuffer("");
        try {
            for (SourceLine sl : m_sourceFile.getSourceLines()) {
                st.append(sl.getText());
                st.append("\n");
                LineHighlighter lh = new LineHighlighter(sl);
                lh.setLines(m_lines, m_textPane, 0.0);
                m_lineHighlighters.add(lh);
                m_textPane.getHighlighter().addHighlight(0, 0, lh);
            }
            m_lines.addAll(m_sourceFile.getSourceLines());
            m_document.insertString(0, st.toString(), m_style);
            m_sourceColourer.colourLine(0, st.length());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                m_textPaneScrollPane.getViewport().setViewPosition(new Point(0,0));
            }});
    }

    public void highlightPC(int pc, Color colour){
        for(SourceLine sourceLine : m_sourceFile.getSourceLines()){
            for(Line line : sourceLine.getChildern()){
                if(line.getLineNumber() == pc){
                    sourceLine.setColour(colour);
                    return;
                }
            }
        }
        
    }

    public void clearFnops() {
        for(SourceLine sourceLine : m_sourceFile.getSourceLines()){
            for(Line line : sourceLine.getChildern()){
                line.setFnopCount(0);
            }
        }
    }

    public void setFnops(ArrayList<Integer> fnopPcs) {
        for(int pc : fnopPcs){
            Line line = m_pcsToLines.get(pc);
            if(line!= null)
                line.setFnopCount(line.getFnopCount()+1);
        }

    }   

}
