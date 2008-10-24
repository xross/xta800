package com.xmos.xeobjectdisplayer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.text.BadLocationException;

import com.xmos.xeobject.Function;
import com.xmos.xeobject.Line;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.gui.XtaXeObjectDisplayer;

public class DisassemblyDisplayer extends Displayer {

    private static final long serialVersionUID = 1L;
    List<Function> m_functions;
    private HashMap<Integer, Line> m_pcsToLines;

    public DisassemblyDisplayer(List<Function> functions, Engine engine, XtaXeObjectDisplayer parent) {
        super(engine, parent);
        m_functions = functions;
        m_title = "Disassembly";
        renderFileToDoc();
        m_lineHeaders.renderFileToDoc();
        m_pcsToLines = new HashMap<Integer, Line>();
        for(Function function : m_functions){
            for(Line line : function.getChildern()){
                m_pcsToLines.put(line.getLineNumber(), line);
            }
        }
    }
    
    public void renderFileToDoc() {
        assert(m_functions!= null);
        StringBuffer st= new StringBuffer("");
        try {
            for (Function func : m_functions) {
                st.append(func.getText());
                st.append("\n");
                m_textPane.getHighlighter().addHighlight(0, 0, func.getLineHighlighter(m_lines, m_textPane, 0.0));
            }
            m_lines.addAll(m_functions);
            m_document.insertString(0, st.toString(), m_style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    protected void highlightPC(int pc, Color colour) {
        for(Function function : m_functions){
            for(Line line : function.getChildern()){
                if(line.getLineNumber() == pc){
                    function.setColour(colour);
                    line.setColour(colour);
                    return;
                }
            }
        }
    }

    public void clearFnops() {
        for(Function function : m_functions){
            for(Line line : function.getChildern()){
                line.setFnopCount(0);
            }
        }
        
    }
    public void setFnops(ArrayList<Integer> fnopPcs) {
        for(int pc : fnopPcs){
            Line line = m_pcsToLines.get(pc);
            line.setFnopCount(line.getFnopCount()+1);
        }
        
    }

}
