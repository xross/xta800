package com.xmos.xta.test.pseudorandom;

import com.xmos.xta.test.pseudorandom.Generator.PragmaType;

public class XtaPragma {

    private PragmaType m_type;
    private String m_label;
    private String m_file;
    private int m_line;

    public XtaPragma(PragmaType type, String label, String file) {
        super();
        m_type = type;
        m_label = label;
        m_file = file;
    }

    public XtaPragma(String label) {
        super();
        m_type = PragmaType.Root;
        m_label = label;
        m_file = "";
        m_line = 0;
    }

    public PragmaType getType() {
        return m_type;
    }

    public String getLabel() {
        return m_label;
    }

    public void setFileLine(int line){
        m_line = line;
    }

    public String getFileLine(){
        return m_file + ":" + m_line;
    }

    public String getTypeString() {
        switch(m_type){
        case Endpoint:
            return "ENDPOINT:";
        case Call:
            return "CALL:";
        case Label:
            return "SRCLABEL:";
        case Function:
            return "";
        case Root:
            break;
        }
        return "";
    }

}
