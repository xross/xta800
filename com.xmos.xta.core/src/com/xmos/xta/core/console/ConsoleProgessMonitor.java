package com.xmos.xta.core.console;

import com.xmos.libs.core.utils.ProgressMonitor;

public class ConsoleProgessMonitor implements ProgressMonitor {
    private Console m_console;

    ConsoleProgessMonitor(Console console) {
        m_console = console;
    }

    public void beginTask(String name, int totalWork) {
        m_console.display(name+ "\n");
    }

    public void setTaskName(String name) {
        m_console.display(name + "\n");
    }

    public boolean isCanceled() {
        return false;
    }

    public void subTask(String name) {
        m_console.display(name + "\n");
    }

    public void worked(int work) {
    }

    public void done() {
    }
}
