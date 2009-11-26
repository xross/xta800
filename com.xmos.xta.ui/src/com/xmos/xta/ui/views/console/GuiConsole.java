package com.xmos.xta.ui.views.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import jline.Completor;

import org.eclipse.jface.text.JFaceTextUtil;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
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

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.console.ConsoleCommand;
import com.xmos.xta.core.console.Shell;
import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.completors.XtaCompletor;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.CommandSource.Type;
import com.xmos.xta.ui.Broadcaster;
import com.xmos.xta.ui.GuiEngine;
import com.xmos.xta.ui.XtaPlugin;

public class GuiConsole extends Console implements IPropertyChangeListener {	
    public static final boolean DEBUG = false;

    private Shell m_shell;
	private StyledText m_text;
	private LinkedBlockingQueue<ConsoleCommand> m_commandQueue;
	private LinkedBlockingQueue<Integer> m_characterQueue;	
	private Vector<Command> m_commandHistory;
	private int m_commandHistoryIndex;
	private Display m_display;
	private String m_string;
	private String m_prompt;
	private Completor m_completor;
    private TabCompletionHandler m_completionHandler;
    private boolean m_hasEscaped;

	public GuiConsole(Composite parent) {
		if (DEBUG) System.out.printf("GuiConsole()\n");		

		GuiEngine.addBroadcastListener(this);
		m_shell = new Shell(this, GuiEngine.getEngine(), new GuiConsoleNotifier(this));		        		
		m_commandQueue = new LinkedBlockingQueue<ConsoleCommand>();
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
        			
        		} else if (event.keyCode == SWT.ARROW_UP) {
        			decrementCommandIndex();
        			displayCommand();
        			
        		} else if (event.keyCode == SWT.ARROW_DOWN) {
        			incrementCommandIndex();
        			displayCommand();
        			
        		} else if (event.keyCode == SWT.HOME) {
                    home(event);
                    
        		} else if (event.keyCode == SWT.TAB) {
                    complete();

        		} else if (event.keyCode == SWT.ESC) {
        			m_hasEscaped = true;

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
			    	
				} else if (event.keyCode == SWT.ARROW_DOWN) {
					event.doit = false;
					
				} else if (event.keyCode == SWT.TAB) {
        			event.doit = false;
        			
        		} else if (event.keyCode == SWT.BS) {
					if (checkCursor()) {
    					event.doit = false;
					}
				} else if (event.keyCode == SWT.HOME) {
  					event.doit = false;
  					
				} else if (event.keyCode == SWT.ARROW_LEFT) {
					if (checkCursor()) {
    					event.doit = false;				
	    			}
				} else if ((event.keyCode == SWT.CR) || (event.keyCode == SWT.KEYPAD_CR)) {
					if (DEBUG) System.out.printf("Set Caret to end\n");
					setCaretToEnd();
					m_text.append("\n");
					event.doit = false;
					
        		} else if (((event.stateMask & SWT.CTRL) != 0) &&
        				(event.character == eventChar('u'))) {
        			clearLine();
        			event.doit = false;

				} else if (event.keyCode == SWT.SHIFT) {
					
				} else if (event.keyCode == SWT.END) {

				} else if (event.keyCode == SWT.CONTROL) {
					
        		} else if (event.keyCode == SWT.ESC) {
					
				} else {
					checkRow();
				}
			}

		});
                
        m_completor = new XtaCompletor(GuiEngine.getEngine());
        m_completionHandler = new TabCompletionHandler();        
        m_text.setFocus();
        m_hasEscaped = false;
	}
	
	void dispose() {
		GuiEngine.removeBroadcastListener(this);
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Broadcaster.BROADCAST_SYNC_CONSOLE)) {
			if (!caretAtEnd()) {
		        displayPrompt(m_prompt);
				setCaretToEnd();
			}
		} 	
	}

	public void execute() {
		int returnCode = m_shell.execute();
		System.exit(returnCode);        
    }

	public void display(String str) {
		if (DEBUG) System.out.printf("GuiConsole: Entering: display: %s\n", str);

		if (!redirectToFile(str)) {
			m_string = str;
	        m_display.syncExec(new Runnable() {
	    		public void run() {
		    		m_text.append(m_string);
			    	m_text.setSelection(m_text.getCharCount());
			    }
	    	});
		}			
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
	
	public ConsoleCommand getNextLine(Vector<Command> commandHistory) {
		if (DEBUG) System.out.printf("GuiConsole: Entering: getNextLine\n");			
		
		m_commandHistory = commandHistory;
		resetCommandIndex();

		String str = "";
		CommandSource src = null;
		try {
			ConsoleCommand consoleCommand = m_commandQueue.take();			
			str = consoleCommand.getStr();
			src = consoleCommand.getSrc();
			
			if (src.getType() == Type.CONSOLE_VIA_GUI) {
				display(String.format("%s\n", str));
			}
			
		} catch (InterruptedException e) {}

		m_hasEscaped = false;

		if (DEBUG) System.out.printf("GuiConsole: Leaving: getNextLine: %s\n", str);
		return new ConsoleCommand(str, src);
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

	public void sendCommand(String command, CommandSource src) {
		if (DEBUG) System.out.printf("GuiConsole: Entering: sendCommand\n");
		
		try {
			m_commandQueue.put(new ConsoleCommand(command, src));
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
	
	public boolean hasEscaped() {
		return m_hasEscaped;
	}
	
	public void setFileRedirect(String fileName) {		
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
	
	private int eventChar(char c) {
		return (c - 'a' + 1);
	}
	
	private void handleEnter() {
		setCaretToEnd();
		
		String text = m_text.getText();			
		int idx = text.lastIndexOf(m_prompt) + m_prompt.length();

		try {
			String commandStr = text.substring(idx != -1 ? idx : 1, m_text.getCharCount());
			m_commandQueue.put(
						new ConsoleCommand(commandStr, 
								new CommandSource(Type.CONSOLE)));

		} catch (InterruptedException e) {}
	}

	private void handleCharacter(int character) {
		try {
			m_characterQueue.put(character);
		} catch (InterruptedException e) {}
	}
	
	private void displayCommand() {		
		String text = m_text.getText();		
		int idx = text.lastIndexOf(m_prompt) + m_prompt.length();
		
		int range = m_text.getCharCount() - idx;
		m_text.replaceTextRange(idx, range, "");
		 
        String commandLine = "";
        if (m_commandHistoryIndex < m_commandHistory.size()) {
    		commandLine = m_commandHistory.get(m_commandHistoryIndex).getFullCommandLine();		        	
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
	
	private boolean caretAtEnd() {
		int currentIndex = m_text.getCaretOffset();

		String text = m_text.getText();			
		int idx = text.lastIndexOf(m_prompt) + m_prompt.length();

		return (idx == currentIndex);
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

	private void checkRow() {
		int currentIndex = m_text.getCaretOffset();
        int currentRowIndex = m_text.getLineAtOffset(currentIndex);
        
        if (currentRowIndex != (m_text.getLineCount() - 1)) {
            m_text.setCaretOffset(m_text.getCharCount());
        }
	}
	
	private void home(KeyEvent event) {
		int currentIndex = m_text.getCaretOffset();
        int currentRowIndex = m_text.getLineAtOffset(currentIndex);
		int rowStartIndex = m_text.getOffsetAtLine(currentRowIndex);
				
    	boolean shiftKeyPressed = (event.stateMask & SWT.SHIFT) != 0;
    	
		if (currentRowIndex == (m_text.getLineCount() - 1)) {
			m_text.setCaretOffset(rowStartIndex + m_shell.getPrompt().length());
			if (shiftKeyPressed) {
				m_text.setSelection(rowStartIndex + m_shell.getPrompt().length(), currentIndex);
			}			
		} else {
			m_text.setCaretOffset(rowStartIndex);
			if (shiftKeyPressed) {
				m_text.setSelection(rowStartIndex, currentIndex);
			}			
		}		
	}

	private void clearLine() {
        int lastRowIndex = m_text.getLineAtOffset(m_text.getCharCount() - 1);
		int rowStartIndex = m_text.getOffsetAtLine(lastRowIndex) + m_prompt.length();
		int len = m_text.getCaretOffset() - rowStartIndex;
				
		m_text.setCaretOffset(rowStartIndex);
		if (len > 0) {
			m_text.replaceTextRange(rowStartIndex, len, "");
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

