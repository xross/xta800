package com.xmos.xta.rcp.views;

import java.io.IOException;
import java.io.Reader;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.rcp.XtaCorePlugin;
import com.xmos.xta.rcp.XtaEngine;

public class ConsoleViewControl implements Console {
    public static final boolean DEBUG = false;

    private Shell m_shell;
	private TextReader m_textReader;
	private ConsoleView m_consoleView; 
	
	public ConsoleViewControl(ConsoleView console, Composite parent) {
		if (DEBUG) System.out.printf("ConsoleViewControl()\n");			

		m_consoleView = console;
		m_shell = new Shell(this, XtaEngine.getEngine(), false);		        
		m_textReader = new TextReader(parent);
	}
	
    public void execute() {
		if (DEBUG) System.out.printf("ConsoleViewControl: execute\n");
        m_shell.execute();
    }
	
	public void display(String str) {		
		if (DEBUG) System.out.printf("ConsoleViewControl: display: %s\n", str);			
		m_textReader.display(str);
	}
	
	public String getNextLine(Vector<Command> commandHistory) {
		if (DEBUG) System.out.printf("ConsoleViewControl: getNextLine\n");
		if (m_consoleView.hasPendingCommands()) {
			String command = m_consoleView.getCommand();
			display(command);
	        return command;
		} else {
			String command = m_textReader.readLine(commandHistory);
	        return command;
		}
	}	
	
	public boolean wasInternal() {
		if (DEBUG) System.out.printf("ConsoleViewControl: wasInternal\n");			
		return m_textReader.wasInternal();
	}
	
	public void clearScreen() {
		if (DEBUG) System.out.printf("ConsoleViewControl: clearScreen\n");			
		m_textReader.clearScreen();		
	}
	
	public void sendCommand(String command) {
		if (DEBUG) System.out.printf("ConsoleViewControl: sendCommand: %s\n", command);
		m_textReader.sendCommand(command);
	}	
}

class TextReader extends Reader {	 
    public static final boolean DEBUG = false;

	private StyledText m_text;
	private Object m_keyLock = new Object();
	private Object m_lineLock = new Object();
	private String m_lastLine;
	private Vector<Command> m_commandHistory;
	private int m_commandHistoryIndex;
	private boolean m_internal;
	private Display m_display;
	private String m_string;
	
	public TextReader(Composite parent) {
		if (DEBUG) System.out.printf("TextReader()\n");		
		
		m_display = PlatformUI.getWorkbench().getDisplay();						
        m_text = new StyledText(parent, SWT.V_SCROLL);
             
        Font font = new Font(PlatformUI.getWorkbench().getDisplay(), "Courier New", 10, SWT.NONE);
        m_text.setFont(font);
        m_text.addKeyListener(new KeyListener() {
        	public void keyPressed(KeyEvent event) {
        		if (DEBUG) System.out.printf("TextReader: keyPressed: %d\n", event.keyCode);			

        		synchronized(m_keyLock) {
        			m_keyLock.notifyAll();
        		}		
        				
        		if ((event.keyCode == SWT.CR) || (event.keyCode == SWT.KEYPAD_CR)) {
        			handleEnter();
        		}
        		else if (event.keyCode == SWT.ARROW_UP) {
        			decrementCommandIndex();
        			displayCommand();
        		}
        		else if (event.keyCode == SWT.ARROW_LEFT) {

        		}
        		else if (event.keyCode == SWT.ARROW_RIGHT) {

        		}
        		else if (event.keyCode == SWT.ARROW_DOWN) {
        			incrementCommandIndex();
        			displayCommand();
        		}
        		else {
        			//if the carat is not on the last line then don't draw it
        			//i'm not sure if it is too late by now
        		}
        	}
        	
        	public void keyReleased(KeyEvent keyEvent) {        		
        	}        	
        });
        
        m_text.addVerifyKeyListener(new VerifyKeyListener() {
			public void verifyKey(VerifyEvent event) {
				if (DEBUG) System.out.printf("TextReader: verifyKey: %d\n", event.keyCode);

			    if (event.keyCode == SWT.ARROW_UP) {
			    	event.doit = false;				
				}
				else if (event.keyCode == SWT.ARROW_DOWN) {
					event.doit = false;				
				}
				else if (event.keyCode == SWT.BS) {
					if (checkCursor()) {
    					event.doit = false;				
					}
				}
				else if (event.keyCode == SWT.ARROW_LEFT) {
					if (checkCursor()) {
    					event.doit = false;				
	    			}
				} else if (event.keyCode == SWT.CR) {
					if (DEBUG) System.out.printf("Set Caret to end\n");
					setCaretToEnd();
					m_text.append("\n");
					event.doit = false;	
				}
				
			}			
		});       
	}
	
	public void display(String str) {
		if (DEBUG) System.out.printf("TextReader: display: %s\n", str);			
		
		m_string = str;
        m_display.syncExec(new Runnable() {
    		public void run() {
	    		m_text.append(m_string);
		    	m_text.setSelection(m_text.getCharCount());
		    }
    	});
	}
	
	public String readLine(Vector<Command> commandHistory) {
		if (DEBUG) System.out.printf("TextReader: readLine\n");			

		m_commandHistory = commandHistory;
		resetCommandIndex();
		
		synchronized(m_lineLock) {
			try {
				m_lineLock.wait();
			}
			catch (InterruptedException ex) {
			}
		}	
		return m_lastLine;
	}
	 	
	public void sendCommand(String command) {
		if (DEBUG) System.out.printf("TextReader: sendCommand\n");			
		m_text.append(String.format("%s\n", command));
		m_internal = true;
		m_lastLine = command;
		synchronized(m_lineLock) {
			m_lineLock.notifyAll();
		}
	}
	
	public boolean wasInternal() {
		if (DEBUG) System.out.printf("TextReader: wasInternal\n");			
		return m_internal;
	}
	
	public void clearScreen() {
		if (DEBUG) System.out.printf("TextReader: clearScreen\n");			
        m_display.syncExec(new Runnable() {
    		public void run() {
        		m_text.setText("");
    		}
        });    		
	}
	
	public void close() throws IOException {
		XtaCorePlugin plugin = XtaCorePlugin.getDefault();
		StringBuffer sb = new StringBuffer();
		for(Command c : m_commandHistory){
			sb.append(c.name() + " || ");
		}
		plugin.getPreferenceStore().setValue("History", sb.toString());
		if (DEBUG) System.out.printf("TextReader: close\n");			
	}

	public int read(char[] cbuf, int off, int len) throws IOException {
		if (DEBUG) System.out.printf("TextReader: read\n");			
		return 0;
	}
	
	private void handleEnter() {
		setCaretToEnd();
		String txt = m_text.getText();
		int idx = txt.lastIndexOf('>', m_text.getCharCount() - 1);
		m_internal = false;
		m_lastLine = txt.substring(idx != -1 ? idx + 1 : 1, m_text.getCharCount());	
		//m_lastLine = m_lastLine.replaceAll("\n", "");
		synchronized(m_lineLock) {
			m_lineLock.notifyAll();
		}		
	}
	
	private void displayCommand() {		
		int length = m_text.getCharCount();
		String text = m_text.getText();
		int index = text.lastIndexOf('>', length);
		int range = length - (index + 1);
		m_text.replaceTextRange(index + 1, range, "");
		 
        String commandLine = "";
        if (m_commandHistoryIndex < m_commandHistory.size()) {
    		commandLine = m_commandHistory.get(m_commandHistoryIndex).commandLine();		        	
        }
   		display(String.format("%s", commandLine));
	}
	
	private void resetCommandIndex() {
		m_commandHistoryIndex = m_commandHistory.size();
	}
	
	private void decrementCommandIndex() {
	    if (m_commandHistoryIndex > 0)
	        --m_commandHistoryIndex;
	}
	
	private void incrementCommandIndex() {
		if (m_commandHistoryIndex < m_commandHistory.size())
		    ++m_commandHistoryIndex;			
	}
			
	private void setCaretToEnd() {
        m_display.asyncExec(new Runnable() {
    		public void run() {
    			m_text.setSelection(m_text.getCharCount());
		    }});					    				
	}
	
	private boolean checkCursor() {
		int currentIndex = m_text.getCaretOffset();
        int currentRowIndex = m_text.getLineAtOffset(currentIndex);
		int rowStartIndex = m_text.getOffsetAtLine(currentRowIndex);
		
		if (currentIndex < (rowStartIndex + 2)) {
			return true;
		} 
		return false;
	}	
}