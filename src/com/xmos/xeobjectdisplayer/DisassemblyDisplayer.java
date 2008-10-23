package com.xmos.xeobjectdisplayer;

import java.awt.Color;
import java.util.List;
import com.xmos.xeobject.Function;
import com.xmos.xeobject.Line;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.gui.XtaXeObjectDisplayer;

public class DisassemblyDisplayer extends Displayer {

    private static final long serialVersionUID = 1L;
    List<Function> m_functions;

    public DisassemblyDisplayer(List<Function> functions, Engine engine, XtaXeObjectDisplayer parent) {
        super(engine, parent);
        m_functions = functions;
        m_title = "Disassembly";
        renderFileToDoc();
        m_lineHeaders.renderFileToDoc();
    }

    public void renderFileToDoc() {
        for(Function function : m_functions){
            insertLine(function, m_document.getLength());
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
}
