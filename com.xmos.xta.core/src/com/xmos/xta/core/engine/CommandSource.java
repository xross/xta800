package com.xmos.xta.core.engine;

public class CommandSource {
    public enum Type {
        CONSOLE,           // Commands typed in via the console.
        GUI,               // Actions run via the gui.
        FILE,              // Commands sourced from an xta script.
        COMMAND_LINE,      // Commands passed on the command line.
        COMPILER,          // Actions prompted via the compiler.
        CONSOLE_VIA_GUI,   // Actions passed to the gui console via the gui.
        SRC_LEVEL_COMMAND, // Embedded command in the executable
        JYTHON,            // Commands run from a jython script.
    }

    private String m_fileName;
    private long m_lineNum;
    private Type m_type;

    public CommandSource(Type type, String fileName, long lineNum) {
        assert(type == Type.FILE || type == Type.SRC_LEVEL_COMMAND);
        m_fileName = fileName;
        m_lineNum = lineNum;
        m_type = type;
    }

    public CommandSource(Type type) {
        m_type = type;
        switch (type) {
        case CONSOLE:
            m_fileName = "console";
            break;

        case GUI:
            m_fileName = "gui";
            break;

        case COMMAND_LINE:
            m_fileName = "cmd";
            break;

        case COMPILER:
            m_fileName = "internal";
            break;

        case CONSOLE_VIA_GUI:
            m_fileName = "gui";
            break;

        case FILE:
        case SRC_LEVEL_COMMAND:
        default:
            assert(false);
        }
        m_lineNum = 0;
    }

    public Type getType() {
        return m_type;
    }

    public String getFileName() {
        return m_fileName;
    }

    public long getLineNum() {
        return m_lineNum;
    }
}
