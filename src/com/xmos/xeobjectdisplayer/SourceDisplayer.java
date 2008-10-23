package com.xmos.xeobjectdisplayer;

import com.xmos.xeobject.SourceFile;
import com.xmos.xeobject.SourceLine;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.gui.XtaXeObjectDisplayer;

public class SourceDisplayer extends Displayer {

    private static final long   serialVersionUID = 1L;
    private SourceFile          m_sourceFile;
    
    public SourceDisplayer(SourceFile sourceFile, Engine engine, XtaXeObjectDisplayer parent) {
        super(engine, parent);
        m_title = sourceFile.getFile().getName();
        m_sourceFile = sourceFile;
        renderFileToDoc();
        m_lineHeaders.renderFileToDoc();
    }
    
    public void renderFileToDoc()  {
        assert(m_sourceFile!= null);
        assert(m_sourceFile.getSourceLines()!=null);
        for (SourceLine sl : m_sourceFile.getSourceLines()) {
            insertLine(sl, m_document.getLength());
        }
    }
    
}
