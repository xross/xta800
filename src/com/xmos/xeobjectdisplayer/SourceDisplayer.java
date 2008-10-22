package com.xmos.xeobjectdisplayer;

import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import com.xmos.xeobject.AssemblyLine;
import com.xmos.xeobject.Line;
import com.xmos.xeobject.SourceFile;
import com.xmos.xeobject.SourceLine;


@SuppressWarnings("serial")
public class SourceDisplayer extends Displayer {

    private SourceFile m_sourceFile;

    public SourceDisplayer(SourceFile sourceFile) {
        super();
        m_title = sourceFile.getFile().getName();
        m_sourceFile = sourceFile;
        try {
            renderFileToDoc();
        } catch (BadLocationException e) {
            System.err.println("BadLocationException");
        }
    }
    
    /*
     * Render everything as-is to the StyledDocument
     */
    public void renderFileToDoc() throws BadLocationException {
        
        final StyledDocument document = m_textPane.getStyledDocument();
        for (SourceLine sl : m_sourceFile.getSourceLines()) {
            String s = sl.getText() + "\n";
            document.insertString(document.getLength(),s , m_style);
            m_textPane.getHighlighter().addHighlight(0, 0, sl.getLineHighlighter());
            if(sl.isFoldable()&& sl.getShowChildern()){
                for(Line assLine : sl.getChildern()){
                    document.insertString(document.getLength(), assLine.getText(), m_style);
                    m_textPane.getHighlighter().addHighlight(0, 0, assLine.getLineHighlighter());
                }
            }
        }
    }

    public void setLineFold(Line line, boolean fold){
        
    }
    
    protected List<AssemblyLine> getAssembly(Line line) {
        return null;
    }

}
