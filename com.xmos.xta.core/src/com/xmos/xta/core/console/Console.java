package com.xmos.xta.core.console;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.Command.RedirectMode;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;

public abstract class Console {
    private PrintWriter m_fileWriter;

    public abstract void display(String str);
    public abstract void displayPrompt(String str);
    public abstract ConsoleCommand getNextLine(Vector<Command> commandHistory);
    public abstract void clearScreen();
    public abstract boolean hasEscaped();

    protected boolean redirectToFile(String str) {
        if (m_fileWriter != null) {
            m_fileWriter.printf("%s", str);
            return true;
        }
        return false;
    }

    protected boolean openRedirectFile(String fileName, RedirectMode redirectMode, CommandSource src, Notifier notifier) {
        assert(m_fileWriter == null);
        try {
            switch (redirectMode) {
            case OVERWRITE:
                m_fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName, false)));
                return true;

            case APPEND:
                m_fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
                return true;

            default:
                assert(false);
                return false;
            }

        } catch (IOException e) {
            notifier.notify(src, Notifier.NOTIFY_ERROR, String.format("Unable to open file for writing: %s", fileName));
            m_fileWriter = null;
            return false;
        }
    }

    protected void closeRedirectFile() {
        if (m_fileWriter != null) {
            m_fileWriter.close();
            m_fileWriter = null;
        }
    }
}
