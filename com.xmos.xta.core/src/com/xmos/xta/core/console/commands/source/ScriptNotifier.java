package com.xmos.xta.core.console.commands.source;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Config;
import com.xmos.xta.core.engine.Notifier;

public class ScriptNotifier extends Notifier {
    private Console m_console;
    private boolean m_errorDetected;
    private String m_errorMessage;

    public ScriptNotifier(Config config, Console console) {
        super(config);
        m_console = console;
    }

    public void notify(CommandSource src, String message, String argument) {
        String level = getLevel(message);

        if (level.equals(NOTIFY_ERROR)) {
            m_errorMessage = argument;
            m_errorDetected = true;

        } else if (level.equals(NOTIFY_WARNING)) {
            m_console.display("xta: ");
            if (src.getLineNum() > 0) {
                m_console.display(src.getFileName() + ":" + src.getLineNum() + ": ");
            }
            m_console.display("warning: " + argument + "\n");

        } else if (level.equals(NOTIFY_INFO)) {
            m_console.display("xta: ");
            if (src.getLineNum() > 0) {
                m_console.display(src.getFileName() + ":" + src.getLineNum() + ": ");
            }
            m_console.display("info: " + argument + "\n");
        }
    }

    public boolean errorDetected() {
        if (m_errorDetected) {
            m_errorDetected = false;
            return true;
        }
        return false;
    }

    public String getErrorMessage() {
        return m_errorMessage;
    }
}
