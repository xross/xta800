package com.xmos.xeobjectdisplayer;

import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import com.xmos.xeobject.AssemblyLine;
import com.xmos.xeobject.Function;
import com.xmos.xeobject.Line;

public class DisassemblyDisplayer extends Displayer {

    private static final long serialVersionUID = 1L;
    List<Function> m_functions;

    public DisassemblyDisplayer(List<Function> functions) {
        super();
        m_functions = functions;
        m_title = "Disassembly";
        try {
            renderFileToDoc();
        } catch (BadLocationException e) {
            System.err.println("BadLocationException");
        }
    }


    public void renderFileToDoc() throws BadLocationException {

        final StyledDocument document = m_textPane.getStyledDocument();
        for(Function function : m_functions){
            String fnName = function.getText() + "\n";
            document.insertString(document.getLength(),fnName , m_style);
            m_textPane.getHighlighter().addHighlight(0, 0, function.getLineHighlighter());
            if(function.getShowChildern()){
                for (Line line : function.getChildern()) {
                    String s = line.getText() + "\n";
                    document.insertString(document.getLength(),s , m_style);
                    m_textPane.getHighlighter().addHighlight(0, 0, line.getLineHighlighter());
                }
            }
        }
    }

    protected List<AssemblyLine> getAssembly(Line line) {
        return null;
    }

    public List<Function> getFunctions() {
        return m_functions;
    }

    public void setLineFold(Line line, boolean fold) {
        //line.getLineAttributes().setAssemblyFolded(fold); 
    }

}
