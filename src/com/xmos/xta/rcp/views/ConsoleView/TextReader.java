package com.xmos.xta.rcp.views.ConsoleView;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

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

import com.xmos.xta.console.Shell;
import com.xmos.xta.console.commands.Command;
import com.xmos.xta.console.completors.XtaCompletor;
import com.xmos.xta.rcp.XtaCorePlugin;
import com.xmos.xta.rcp.XtaEngine;

public class TextReader extends Reader {	 
    public static final boolean DEBUG = false;

	private StyledText m_text;
	private Object m_keyLock = new Object();
	private Object m_lineLock = new Object();
	private Object m_charLock = new Object();
	private String m_lastLine;
	private int m_lastChar;
	private Vector<Command> m_commandHistory;
	private int m_commandHistoryIndex;
	private boolean m_internal;
	private Display m_display;
	private String m_string;
	private String m_prompt;
	private Shell m_shell;
	private Completor m_completor;
    private TabCompletionHandler m_completionHandler;

	public TextReader(Composite parent, Shell shell) {
		if (DEBUG) System.out.printf("TextReader()\n");		
		
		m_display = PlatformUI.getWorkbench().getDisplay();						
        m_text = new StyledText(parent, SWT.V_SCROLL);
        m_shell = shell;
             
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
				if (DEBUG) System.out.printf("TextReader: verifyKey: %d\n", event.keyCode);

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
                
        m_completor = new XtaCompletor(XtaEngine.getEngine());
        m_completionHandler = new TabCompletionHandler();
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

	public void displayPrompt(String str) {
		if (DEBUG) System.out.printf("TextReader: displayPrompt: %s\n", str);			
		
		m_prompt = str;
        m_display.syncExec(new Runnable() {
    		public void run() {
	    		m_text.append(m_prompt);
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
	
	public int readCharacter() {
		if (DEBUG) System.out.printf("TextReader: readCharacter\n");			
		
		synchronized(m_charLock) {
			try {
				m_charLock.wait();
			}
			catch (InterruptedException ex) {
			}
		}	
		return m_lastChar;
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
		m_internal = false;
		m_lastLine = txt.substring(idx != -1 ? idx + 1 : 1, m_text.getCharCount());	
		synchronized(m_lineLock) {
			m_lineLock.notifyAll();
		}		
	}

	private void handleCharacter(int character) {
		m_lastChar = character;
		synchronized(m_charLock) {
			m_charLock.notifyAll();
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
}
