package com.xmos.xeobjectdisplayer;

import com.xmos.xeobject.AssemblyLine;
import com.xmos.xeobject.LineAttributes;

public class FNOPLine extends AssemblyLine {

    private LineAttributes     m_lineAttributes;
    
    public FNOPLine(LineAttributes attributes) {
        super("FNOP", 0, 0, 0, false, false);

    }

    public FNOPLine() {
        super("FNOP", 0, 0, 0, false, false);
        m_lineAttributes = new LineAttributes();
    }

    public LineAttributes getLineAttributes() {
        return m_lineAttributes;
    }

    public void setLineAttributes(LineAttributes lineAttributes) {
        m_lineAttributes = lineAttributes;
        
    }

	public int getNumUnfoldedLines() {
		return 0;
	}

    public String getOperandString() {
        return "";
    }
}
