package com.xmos.xeobjectdisplayer;

import java.util.List;
import com.xmos.xeobject.Function;

public class DisassemblyDisplayer extends Displayer {

    private static final long serialVersionUID = 1L;
    List<Function> m_functions;

    public DisassemblyDisplayer(List<Function> functions) {
        super();
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
}
