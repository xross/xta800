package com.xmos.xta.ui.editors.script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.xmos.xta.core.console.commands.Command;
import com.xmos.xta.core.console.commands.CommandFactory;

public class ScannerScript extends RuleBasedScanner {
    
    public ScannerScript() {
    	Display display = PlatformUI.getWorkbench().getDisplay();
    	List<IRule> rules = new ArrayList<IRule>();
      
    	processWhitespace(display, rules);
    	processStrings(display, rules);
    	processKeywords(display, rules);
    	processNumbers(display, rules);
    	processComments(display, rules);
    	
    	IRule[] rulesArray = new IRule[rules.size()];
    	rulesArray = rules.toArray(rulesArray);    	
        setRules(rulesArray);
    }

    private void processStrings(Display display, List<IRule> rules) {
    	Color color = display.getSystemColor(SWT.COLOR_BLUE);
    	
    	IToken token = new Token(new TextAttribute(color));
    	
    	rules.add(new SingleLineRule("\"", "\"", token, '\\'));
        rules.add(new SingleLineRule("'", "'", token, '\\'));    	
    }

    private void processWhitespace(Display display, List<IRule> rules) {
    	rules.add(new WhitespaceRule(new IWhitespaceDetector() {
            public boolean isWhitespace(char c) {
                return Character.isWhitespace(c);    
            }
    	}));
    }
        
    private void processKeywords(Display display, List<IRule> rules) {
    	Color color = display.getSystemColor(SWT.COLOR_BLUE);
    	
    	Collection<String> keywords = new ArrayList<String>();
    	for (Command command : CommandFactory.getCommands()) {
    		keywords.add(command.name());
    		for (Command subCommand : command.getSubCommands()) {
    			keywords.add(subCommand.name());
    		}
    	}
    	
        WordRule rule = new WordRule(new IWordDetector() {
            public boolean isWordStart(char c) { 
         	    return Character.isJavaIdentifierStart(c); 
            }
            public boolean isWordPart(char c) {   
            	return Character.isJavaIdentifierPart(c); 
            }
        }, Token.WHITESPACE);
        
        IToken keywordToken = new Token(new TextAttribute(color, null, SWT.BOLD));        
        for (String keyword : keywords) {
           rule.addWord(keyword, keywordToken);
        }
        rules.add(rule);
    }
    
    private void processNumbers(Display display, List<IRule> rules) {
    	Color color = display.getSystemColor(SWT.COLOR_DARK_GREEN);
    	IToken token = new Token(new TextAttribute(color));
    	rules.add(new NumberRule(token));
    }

    private void processComments(Display display, List<IRule> rules) {
    	Color color = display.getSystemColor(SWT.COLOR_RED);
    	IToken token = new Token(new TextAttribute(color));
    	rules.add(new PatternRule("#", null, token, '\0', true));    	
    }
}
