package com.xmos.xta.core.console;

import java.io.IOException;
import java.util.Vector;

import jline.ConsoleReader;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.completors.XtaCompletor;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.CommandSource.Type;

public class JLineConsole extends Console {
    private ConsoleReader m_reader;
    private String m_prompt;
    private Engine m_engine;
    private boolean m_hasEscaped;

    JLineConsole(Engine engine) throws IOException {
          m_engine = engine;
          m_hasEscaped = false;
          initialise();
    }

    public void display(String str) {
        if (!redirectToFile(str)) {
            System.out.printf("%s", str);
        }
    }

    public void displayPrompt(String str) {
        m_prompt = str;
    }

    public ConsoleCommand getNextLine(Vector<Command> commandHistory) {
        String line = null;
        while (line == null) {
            try {
                line = m_reader.readLine(m_prompt);
                if (line == null) {
                       reportError();
                    return null;
                }

            } catch (IOException e) {
                line = null;
            }
        }
        m_hasEscaped = false;

        return new ConsoleCommand(line, new CommandSource(Type.CONSOLE));
    }

    public void clearScreen() {
    }

    public boolean hasEscaped() {
        return m_hasEscaped;
    }

    private void reportError() {
        System.out.printf("internal error: Problem reading console input (aborting)..\n");
        System.out.printf("try using -DConsole=Basic\n");
        System.exit(1);
    }

    private void initialise() throws IOException {
        m_reader = new ConsoleReader();
        m_reader.setBellEnabled(false);
          m_reader.addCompletor(new XtaCompletor(m_engine));
    }
}
