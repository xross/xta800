package com.xmos.xeobjectdisplayer;

import com.xmos.xeobject.SourceFile;
import com.xmos.xeobject.SourceLine;

public class SourceDisplayer extends Displayer {

    private static final long   serialVersionUID = 1L;
    private SourceFile          m_sourceFile;
    
    public SourceDisplayer(SourceFile sourceFile) {
        super();
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
