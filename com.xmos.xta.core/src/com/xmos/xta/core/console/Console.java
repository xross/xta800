package com.xmos.xta.core.console;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.Command.RedirectMode;

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

	protected void openRedirectFile(String fileName, RedirectMode redirectMode) {
		assert(m_fileWriter == null);
		try {
			switch (redirectMode) {
			case OVERWRITE:
				m_fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName, false)));
				break;

			case APPEND:
				m_fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
				break;

			default:
				assert(false);
			    break;
			}
		} catch (IOException e) {
			System.out.printf("Unable to open file for writing:", fileName);
			m_fileWriter = null;
		}
	}

	protected void closeRedirectFile() {
		assert(m_fileWriter != null);
		m_fileWriter.close();
		m_fileWriter = null;
	}
}
