package com.xmos.xta.rcp.editors;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class ScannerShownRoute extends RuleBasedScanner {

    public ScannerShownRoute() {  	
    	
    	Display display = PlatformUI.getWorkbench().getDisplay();
    	List<IRule> rules = new ArrayList<IRule>();
    	processComments(display, rules);
    	
    	IRule[] rulesArray = new IRule[rules.size()];
    	rulesArray = rules.toArray(rulesArray);    	
        setRules(rulesArray);
    }

    private void processComments(Display display, List<IRule> rules) {
    	Color color = display.getSystemColor(SWT.COLOR_GRAY);
    	IToken token = new Token(new TextAttribute(color));
    	
    	//Don't look at this
    	
    	//TODO make this better
    	for(char i = 0; i < 256;i++){
    		rules.add(new PatternRule(i+"", null, token, '\0', true));
    	}  	
    }
}
