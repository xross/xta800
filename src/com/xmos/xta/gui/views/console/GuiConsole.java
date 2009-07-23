package com.xmos.xta.gui.views.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import jline.Completor;

import org.eclipse.jface.text.JFaceTextUtil;
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
import com.xmos.xta.console.completors.XtaCompletor;
import com.xmos.xta.gui.XtaPlugin;
import com.xmos.xta.gui.GuiEngine;

public class GuiConsole implements Console {	
    public static final boolean DEBUG = false;

    private Shell m_shell;
	private StyledText m_text;
	private LinkedBlockingQueue<CommandString> m_commandQueue;
	private LinkedBlockingQueue<Integer> m_characterQueue;	
	private Vector<Command> m_commandHistory;
	private int m_commandHistoryIndex;
	private Display m_display;
	private String m_string;
	private String m_prompt;
	private Completor m_completor;
    private TabCompletionHandler m_completionHandler;

	public GuiConsole(Composite parent) {
		if (DEBUG) System.out.printf("GuiConsole()\n");		
		
		m_shell = new Shell(this, GuiEngine.getEngine(), new GuiConsoleNotifier(this));		        		
		m_commandQueue = new LinkedBlockingQueue<CommandString>();
		m_characterQueue = new LinkedBlockingQueue<Integer>();		
        m_text = new StyledText(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		m_display = PlatformUI.getWorkbench().getDisplay();						
             
        Font font = new Font(PlatformUI.getWorkbench().getDisplay(), "Courier New", 10, SWT.NONE);
        m_text.setFont(font);
        m_text.addKeyListener(new KeyListener() {
        	public void keyPressed(KeyEvent event) {
        		if (DEBUG) System.out.printf("GuiConsole: keyPressed: %d\n", event.keyCode);			
        				
        		if ((event.keyCode == SWT.CR) || (event.keyCode == SWT.KEYPAD_CR)) {
        			handleEnter();
        		}
        		else if (event.keyCode == SWT.ARROW_UP) {
        			decrementCommandIndex();
        			displayCommand();
        		}
        		else if (event.keyCode == SWT.ARROW_DOWN) {
        			incrementCommandIndex();
        			displayCommand();
        		}
        		else if (event.keyCode == SWT.HOME) {
                    home();
        		}
        		else if (event.keyCode == SWT.TAB) {
                    complete();
        		} else {
        			handleCharacter(event.character);
        		}
        	}
        	
        	public void keyReleased(KeyEvent keyEvent) {        		
        	}        	
        });
        
        m_text.addVerifyKeyListener(new VerifyKeyListener() {
			public void verifyKey(VerifyEvent event) {
				if (DEBUG) System.out.printf("GuiConsole: verifyKey: %d\n", event.keyCode);

			    if (event.keyCode == SWT.ARROW_UP) {
			    	event.doit = false;				
				}
				else if (event.keyCode == SWT.ARROW_DOWN) {
					event.doit = false;				
				}
        		else if (event.keyCode == SWT.TAB) {
        			event.doit = false;
        		}
				else if (event.keyCode == SWT.BS) {
					if (checkCursor()) {
    					event.doit = false;				
					}
				}
				else if (event.keyCode == SWT.HOME) {
  					event.doit = false;
				}
				else if (event.keyCode == SWT.ARROW_LEFT) {
					if (checkCursor()) {
    					event.doit = false;				
	    			}
				} else if ((event.keyCode == SWT.CR) || (event.keyCode == SWT.KEYPAD_CR)) {
					if (DEBUG) System.out.printf("Set Caret to end\n");
					setCaretToEnd();
					m_text.append("\n");
					event.doit = false;	
				}
			}
		});
                
        m_completor = new XtaCompletor(GuiEngine.getEngine());
        m_completionHandler = new TabCompletionHandler();
	}
	
    public void execute() {
        m_shell.execute();
    }
	
	public void display(String str) {
		if (DEBUG) System.out.printf("GuiConsole: Entering: display: %s\n", str);
		
		m_string = str;
        m_display.syncExec(new Runnable() {
    		public void run() {
	    		m_text.append(m_string);
		    	m_text.setSelection(m_text.getCharCount());
		    }
    	});
		if (DEBUG) System.out.printf("GuiConsole: Leaving: display\n");
	}

	public void displayPrompt(String str) {
		if (DEBUG) System.out.printf("GuiConsole: Enterning: displayPrompt: %s\n", str);			
		
		m_prompt = str;
        m_display.syncExec(new Runnable() {
    		public void run() {
	    		m_text.append(m_prompt);
		    	m_text.setSelection(m_text.getCharCount());
		    }
    	});
		if (DEBUG) System.out.printf("GuiConsole: Leaving: displayPrompt\n");			
	}
	
	public String getNextLine(Vector<Command> commandHistory) {
		if (DEBUG) System.out.printf("GuiConsole: Entering: getNextLine\n");			

		m_commandHistory = commandHistory;
		resetCommandIndex();

		String command = "";
		CommandString commandString;
		try {
			commandString = m_commandQueue.take();
			if (commandString.getEcho()) {
				display(String.format("%s\n", commandString.getStr()));
			}
			command = commandString.getStr();
		} catch (InterruptedException e) {}

		if (DEBUG) System.out.printf("GuiConsole: Leaving: getNextLine: %s\n", command);			
		return command;
	}
	
	public int readCharacter() {
		if (DEBUG) System.out.printf("GuiConsole: Entering: readCharacter\n");
		
		int character = 0;
		try {
			character = m_characterQueue.take();
		} catch (InterruptedException e) {}
		
		if (DEBUG) System.out.printf("GuiConsole: Leaving: readCharacter: %d\n", character);					
		return character;
	}	
	
	public void sendCommand(String command) {
		if (DEBUG) System.out.printf("GuiConsole: Entering: sendCommand\n");
		
		try {
			m_commandQueue.put(new CommandString(command, true));
		} catch (InterruptedException e) {}

		if (DEBUG) System.out.printf("GuiConsole: Leaving: sendCommand\n");
	}
		
	public void clearScreen() {
		if (DEBUG) System.out.printf("GuiConsole: Entering: clearScreen\n");			
        m_display.syncExec(new Runnable() {
    		public void run() {
        		m_text.setText("");
    		}
        });    		
		if (DEBUG) System.out.printf("GuiConsole: Leaving: clearScreen\n");			
	}
	
	public void close() throws IOException {
		if (DEBUG) System.out.printf("GuiConsole: close\n");			

		XtaPlugin plugin = XtaPlugin.getDefault();
		StringBuffer sb = new StringBuffer();
		for (Command c : m_commandHistory){
			sb.append(c.name() + " || ");
		}
		plugin.getPreferenceStore().setValue("History", sb.toString());
	}

	public int read(char[] cbuf, int off, int len) throws IOException {
		if (DEBUG) System.out.printf("GuiConsole: read\n");			
		return 0;
	}
	
	String getCurrentBuffer() {				
		int currentIndex = m_text.getCaretOffset();
        int currentRowIndex = m_text.getLineAtOffset(currentIndex);
		int rowStartIndex = m_text.getOffsetAtLine(currentRowIndex);
		int startIndex = rowStartIndex + m_shell.getPrompt().length();
		
		int numCharsInBuffer = currentIndex - startIndex;		
		if (numCharsInBuffer > 0) {
			if (currentIndex == m_text.getCharCount()) {
				--currentIndex;
			}
	        return m_text.getText(startIndex, currentIndex);				
		}
		return "";
	}

	int getCurrentCursorIndex() {
		int currentIndex = m_text.getCaretOffset();
        int currentRowIndex = m_text.getLineAtOffset(currentIndex);
		int rowStartIndex = m_text.getOffsetAtLine(currentRowIndex);		
        return currentIndex - (rowStartIndex + m_shell.getPrompt().length());	
	}
	
	void insertText(String text, int cursorIndex) {
		int currentIndex = m_text.getCaretOffset();
        int currentRowIndex = m_text.getLineAtOffset(currentIndex);
		int rowStartIndex = m_text.getOffsetAtLine(currentRowIndex);
		int startIndex = rowStartIndex + m_shell.getPrompt().length() + cursorIndex;		
		int numCharsTotal = m_text.getCharCount();
		int numCharsToEnd = numCharsTotal - startIndex;
		m_text.replaceTextRange(startIndex, numCharsToEnd, text);		
	}

	void insertCompletions(Collection<String> completions, String buffer) {
		m_text.append("\n");
		
	    int numPixels = m_text.getSize().x;
	    int averageCharPixelWidth = JFaceTextUtil.getAverageCharWidth(m_text);
	    int averageNumChars = numPixels / averageCharPixelWidth;
		
	    // Finds the longest candidate..
	    int longestCompletionLength = 0;
        for (String completion : completions) {
        	if (completion.length() > longestCompletionLength) 
        		longestCompletionLength = completion.length();
        }

        int fieldWidth = longestCompletionLength + 5;
	    int numFields = averageNumChars / fieldWidth;
        	
	    int fieldIndex = 0;
        for (String completion : completions) {
        	
        	// Builds a string with the correct amount of space inserted at the end...
        	String completionField = completion;
        	for (int i = completion.length(); i < fieldWidth; ++i)
        		completionField += " ";
        	        	
        	m_text.append(completionField);   
        	++fieldIndex;
        	if (fieldIndex == numFields) {
            	m_text.append("\n");        	
            	fieldIndex = 0;        		
        	}
        }
    	m_text.append("\n");        	
        displayPrompt(m_prompt);
        display(buffer);
        setCaretToEnd();
	}
	
	private void handleEnter() {
		setCaretToEnd();
		String txt = m_text.getText();
		int idx = txt.lastIndexOf('>', m_text.getCharCount() - 1);
		
		try {
			m_commandQueue.put(new CommandString(txt.substring(idx != -1 ? idx + 1 : 1, m_text.getCharCount()), false));
		} catch (InterruptedException e) {}					
	}

	private void handleCharacter(int character) {
		try {
			m_characterQueue.put(character);
		} catch (InterruptedException e) {}
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
		
		if (currentIndex <= (rowStartIndex + m_shell.getPrompt().length())) {
			return true;
		} 
		return false;
	}	

	private void home() {
		int currentIndex = m_text.getCaretOffset();
        int currentRowIndex = m_text.getLineAtOffset(currentIndex);
		int rowStartIndex = m_text.getOffsetAtLine(currentRowIndex);
		
		if (currentRowIndex == (m_text.getLineCount() - 1)) {
			m_text.setCaretOffset(rowStartIndex + m_shell.getPrompt().length());
		} else {
			m_text.setCaretOffset(rowStartIndex);
		}
	}

	private void complete() {
        List<String> candidates = new ArrayList<String>();
        String buffer = getCurrentBuffer();
        int cursor = getCurrentCursorIndex();

        int position = m_completor.complete(buffer, cursor, candidates);
        if (candidates.size() != 0) {
            m_completionHandler.complete(this, candidates, position);		
        }
	}

	static private class CommandString {
		private String m_str;
		private boolean m_echo;
		
		CommandString(String str, boolean echo) {
			m_str = str;
			m_echo = echo;
		}		
		String getStr()   { return m_str; }
		boolean getEcho() { return m_echo; }
	}	
}

