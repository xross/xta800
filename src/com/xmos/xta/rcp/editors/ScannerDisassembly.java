package com.xmos.xta.rcp.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.xmos.xeobject.AssemblyLine;

public class ScannerDisassembly extends RuleBasedScanner {
    public static final boolean DEBUG = false;
    
    public ScannerDisassembly() {  	
    	Display display = PlatformUI.getWorkbench().getDisplay();
    	List<IRule> rules = new ArrayList<IRule>();

    	processRegisters(display, rules);
    	//processSpecialRegisters(display, rules);
    	IRule[] rulesArray = new IRule[rules.size()];
    	rulesArray = rules.toArray(rulesArray);    	
        setRules(rulesArray);
    }

    private void processRegisters(Display display, List<IRule> rules) {
    	Color color = display.getSystemColor(SWT.COLOR_BLUE);
    	
        String[] keywords = {
        	"r0", "r1", "r2", "r3", 
        	"r4", "r5", "r6", "r7",
        	"r8", "r9", "r10", "r11",
        	"r12", "r13", "r14", "r15", "FNOP"
        };
        
        String[] specialKeywords = {
        	"sp", "dp", "ps", "pc", 
        	"cp", "et", "ed", "sed",
        	"spc", "ssr", "kep", "et", 
        };
            
        ArrayList<String> resource = new ArrayList<String>();
        for(String s : AssemblyLine.getResourceNames()){
        	resource.add(s);
        }
        resource.add("res");
        
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
        color = display.getSystemColor(SWT.COLOR_DARK_RED);
        keywordToken = new Token(new TextAttribute(color, null, SWT.BOLD));        
        for (String keyword : specialKeywords) {
           rule.addWord(keyword, keywordToken);
        }
        color = display.getSystemColor(SWT.COLOR_RED);
        keywordToken = new Token(new TextAttribute(color, null, SWT.BOLD));        
        for (String keyword : resource) {
           rule.addWord(keyword, keywordToken);
        }
        rules.add(rule);
    }
}
