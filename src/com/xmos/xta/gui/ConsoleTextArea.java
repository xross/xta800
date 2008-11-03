package com.xmos.xta.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.Reader;
import java.util.Vector;

import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.Utilities;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.commands.Command;

public class ConsoleTextArea implements Console {
	private JTextArea m_textArea;
	private TextAreaReader m_textAreaReader;
	
	public ConsoleTextArea(JTextArea textArea) {
		m_textArea = textArea;
		m_textArea.addCaretListener(new CaretListener(){

            public void caretUpdate(CaretEvent e) {
                PlainDocument pd = (PlainDocument) m_textArea.getDocument();
                int lineStart = pd.getParagraphElement(pd.getLength()).getStartOffset();
                int lineEnd = pd.getParagraphElement(pd.getLength()).getEndOffset();
                
                if(e.getDot() > lineStart && e.getDot() < lineEnd){
                    //just to be safe
                    m_textArea.setEditable(true);
                    if(m_textArea.getSelectionStart() < lineStart+1){
                        m_textArea.setEditable(false);
                    }
                } else {
                    if(m_textArea.getSelectedText()== null){
                        m_textArea.getCaret().setDot(lineEnd - 1);
                        m_textArea.setEditable(true);
                    } else {
                        m_textArea.setEditable(false);   
                    }
                    
                }
            }
		    
		    
		});
		m_textAreaReader = new TextAreaReader(textArea);
	}
	
	public void display(String str) {
		m_textArea.append(str);
		m_textArea.setCaretPosition(m_textArea.getDocument().getLength());
	}
	
	public String getNextLine(Vector<Command> commandHistory) {
        String command = m_textAreaReader.readLine(commandHistory);
        return command;
	}	
	
	public boolean wasInternal() {
		return m_textAreaReader.wasInternal();
	}
	
	public void sendCommand(String command) {
		m_textAreaReader.sendCommand(command);
	}	
}

class TextAreaReader extends Reader implements KeyListener {	 
	private JTextArea m_textArea;
	private Object m_keyLock = new Object();
	private Object m_lineLock = new Object();
	private String m_lastLine;
	private Vector<Command> m_commandHistory;
	private int m_commandHistoryIndex;
	private boolean m_internal;
	
	public TextAreaReader(JTextArea area) {
		super();
		m_textArea = area;
		m_textArea.addKeyListener(this);
	}
		
	public void keyPressed(KeyEvent keyEvent) {
		synchronized(m_keyLock) {
			m_keyLock.notifyAll();
		}		
		if ((keyEvent.getKeyCode() == KeyEvent.VK_ENTER) && keyEvent.isShiftDown()) {
			m_textArea.append("\n");
			handleEnter();
		}
		else if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
			handleEnter();
		}
		else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
			decrementCommandIndex();
			displayCommand();
			keyEvent.consume();
		}
		else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
			incrementCommandIndex();
			displayCommand();
			keyEvent.consume();
		}
		else if (keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			if (checkCursor())
				keyEvent.consume();
		}
		else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
			if (checkCursor())
			    keyEvent.consume();
		}
	}
	
	public void keyReleased(KeyEvent keyEvent) {
	}
	
	public void keyTyped(KeyEvent keyEvent) {		
	}
	
	public int read(char[] arg0, int arg1, int arg2) throws IOException {
		throw new IOException("Not supported");
	}
	
	public String readLine(Vector<Command> commandHistory) {
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
	 
	public void close() throws IOException {
	}	
	
	public void sendCommand(String command) {
		m_textArea.append(String.format("%s\n", command));
		m_internal = true;
		m_lastLine = command;
		synchronized(m_lineLock) {
			m_lineLock.notifyAll();
		}
	}
	
	public boolean wasInternal() {
		return m_internal;
	}
	
	private void handleEnter() {
		m_textArea.setCaretPosition(m_textArea.getDocument().getLength());			
		String txt = m_textArea.getText();
		int idx = txt.lastIndexOf('>', m_textArea.getCaretPosition() - 1);
		m_internal = false;
		m_lastLine = txt.substring(idx != -1 ? idx + 1 : 1, m_textArea.getCaretPosition());		
		synchronized(m_lineLock) {
			m_lineLock.notifyAll();
		}		
	}
	
	private void displayCommand() {
		int length = m_textArea.getDocument().getLength();
		String text = m_textArea.getText();
		int index = text.lastIndexOf('>', length);
		m_textArea.replaceRange("", index + 1, length);
		 
        String commandLine = "";
        if (m_commandHistoryIndex < m_commandHistory.size()) {
    		commandLine = m_commandHistory.get(m_commandHistoryIndex).commandLine();		        	
        }
   		m_textArea.append(String.format("%s", commandLine));		        	        	
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
		
	private boolean checkCursor() {
		try { 
			int currentIndex = m_textArea.getCaretPosition();
			int rowStartIndex = Utilities.getRowStart(m_textArea, currentIndex);
			if (currentIndex < (rowStartIndex + 2))
				return true;
		} 
		catch (BadLocationException e) {
			return false;
		}
		return false;
	}
}