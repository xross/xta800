package com.xmos.xeobjectdisplayer;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
public class AssemblyColourer {

	private List<Expression>     m_expressions;
	private StyledDocument       m_document;
    private Font                 m_font;
    private Pattern              m_lexer;
    private Style                m_style;
    private SimpleAttributeSet   m_defaultAttributeSet;
	
	public AssemblyColourer(StyledDocument document, Font font) {
		m_document = document;
        m_font = font;
		m_style = StyleContext.getDefaultStyleContext()
			.getStyle(StyleContext.DEFAULT_STYLE);

        m_defaultAttributeSet = createStyle(Color.blue);
		
		// Add expressions to match with a style
		m_expressions = new ArrayList<Expression>();
		// Register
		m_expressions.add(
				new Expression("([r][0-9][0-9]?)",
				createStyle((Color.red).darker())));
		// Register
		m_expressions.add(
				new Expression("(res|ps|sp|cp|dp)",
				createStyle((Color.red).darker())));
		// Hex number
		m_expressions.add(
				new Expression("(0x[0-9a-fA-F]+)",
				createStyle((Color.green).darker())));
		// Decimal number
		m_expressions.add(
				new Expression("([0-9]+)",
				createStyle(Color.black)));
		// Nmenmonic
		m_expressions.add(
				new Expression("([A-Z][a-zA-Z0-9_]+)",
				createStyle((Color.blue))));
        
		// Create a regex string
		String regex = "";
		int i=1;
		for(Expression e : m_expressions) {
			regex += e.getRegex();
			if (i < m_expressions.size())
				regex += "|";
			i++;
		}
		
		// Compile it
		m_lexer = Pattern.compile(regex);
	}
	
	public void colourLine(int lineStart, int lineLength) {
		try {
			Matcher m = m_lexer.matcher(m_document.getText(lineStart, lineLength));
            styleText(lineStart, lineStart + lineLength, m_defaultAttributeSet);

			while (m.find()) {
				for (int g = m_expressions.size(); g >= 1; g--) {
                    if (m.start(g) != -1) {
                         styleText(m.start(g) + lineStart,
                                 m.end(g) + lineStart, 
                        		 m_expressions.get(g - 1).getAttributes());
                    }
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
		
	private void styleText(int from, int to, SimpleAttributeSet a) {
	    m_document.setCharacterAttributes(from, to-from, a, true);
    }
	
    private SimpleAttributeSet createStyle(Color c) {
        SimpleAttributeSet s = new SimpleAttributeSet(m_style);
    	StyleConstants.setFontFamily(s, m_font.getFamily());
		StyleConstants.setFontSize(s, m_font.getSize());
        StyleConstants.setForeground(s, c);
        return s;
    }
}

class Expression {
	private String m_regex;
	private SimpleAttributeSet m_attributes;
	
	public Expression(String regex, SimpleAttributeSet attributes) {
		m_regex = regex;
		m_attributes = attributes;
	}
	
	public String getRegex() {
		return m_regex;
	}
	
	public SimpleAttributeSet getAttributes() {
		return m_attributes;
	}
}