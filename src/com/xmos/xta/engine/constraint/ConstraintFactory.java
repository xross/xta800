package com.xmos.xta.engine.constraint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.Notifier;
import com.xmos.xta.engine.references.Reference;
import com.xmos.xta.engine.references.ReferenceFactory;
import com.xmos.xta.engine.references.ReferenceFactory.ReferenceClass;

/* Grammar used:
 * 
 * expr           =  logicalOr
 * 
 * logicalOr      =  logicalAnd logicalOrTerm
 * logicalOrTerm  =  || logicalAnd logicalOrTerm
 *                 | NOTHING
 *
 * logicalAnd     =  unary logicalAndTerm
 * logicalAndTerm =  && unary logicalAndTerm
 *                 | NOTHING
 *
 * unary          =  ! primary
 *                 | primary
 *                 
 * primary        =  ( expr )
 *                 | codeRef
 * 
 */

public class ConstraintFactory {
    private static final Exception PARSE_ERROR = null;
	
	private enum Token {codeRef, lparen, rparen, lor, land, not, invalid};

	private static ArrayList<Token> m_tokens = new ArrayList<Token>();
	private static ArrayList<String> m_tokenStrings = new ArrayList<String>();
	private static Token m_token;
	private static String m_tokenString;
	private static String m_stringSoFar;
	private static Engine m_engine; 
	
	public static Constraint createConstraint(Engine engine, String constraint) {
		m_engine = engine;
		
    	tokenise(constraint);

    	Constraint c = null;
    	try {
    		moveToNextToken();
    		c = expr();
    		if ((m_tokens.size() != 0) || (m_token != Token.invalid)) {
				error("constraint parse error: extra tokens after '" + m_stringSoFar + "'");
    		}
    	} catch (Exception e) {
    	}
		return c;
    }

	private static void tokenise(String constraint) {
		m_tokens.clear();
		m_tokenStrings.clear();
		m_stringSoFar = "";
		m_tokenString = "";

        Pattern p = Pattern.compile("([\\s&|]+|[!()])");
        String[] result = split(p, constraint, true);
        for (int i = 0; i < result.length; i++) {
			String tok = result[i];
			tok = tok.trim();
			if (!tok.equals("")) {
				m_tokens.add(strToTok(tok));
				m_tokenStrings.add(tok);
			}
		}
	}

	private static String[] split(Pattern pattern, String text, boolean keepDelimiters) {
        if (text == null) {
            text = "";
        }

        int last_match = 0;
        LinkedList<String> splitted = new LinkedList<String>();

        Matcher m = pattern.matcher(text);

        while (m.find()) {
            splitted.add(text.substring(last_match,m.start()));

            if (keepDelimiters) {
                splitted.add(m.group());
            }
            last_match = m.end();
        }

        splitted.add(text.substring(last_match));
        return splitted.toArray(new String[splitted.size()]);
    }

	
	private static Token strToTok(String tok) {
		if (tok.equals("("))
			return Token.lparen;
		else if (tok.equals(")"))
			return Token.rparen;
		else if (tok.equals("||"))
			return Token.lor;
		else if (tok.equals("&&"))
			return Token.land;
		else if (tok.equals("!"))
			return Token.not;
		else
			return Token.codeRef;
	}

	private static String tokToString(Token tok) {
		if (tok == Token.lparen)
			return "(";
		else if (tok == Token.rparen)
			return ")";
		else if (tok == Token.lor)
			return "||";
		else if (tok == Token.land)
			return "&&";
		else if (tok == Token.not)
			return "!";
		else
			return "code reference";
	}

	private static boolean accept(Token t) {
	    if (m_token == t) {
	        moveToNextToken();
	        return true;
	    }
	    return false;
	}
	 
	private static boolean expect(Token s) throws Exception {
	    if (accept(s))
	        return true;
	    
	    String errorStr = "constraint parse error: ";
	    if (!m_stringSoFar.equals(""))
	    	errorStr += " after '" + m_stringSoFar + "'";
	    
	    if (m_tokenString == "") {
	    	errorStr += ", missing expected '" + tokToString(s) + "'";
	    } else {
	    	errorStr += ", expected '" + tokToString(s) + "', got '" + m_tokenString + "'";
	    }
	    error(errorStr);
	    return false;
	}
	 
	private static Constraint expr() throws Exception {
	    return logicalOr();
	}

	private static Constraint logicalOr() throws Exception {
		Constraint left = logicalAnd();
		return logicalOrTerm(left);
	}
	
	private static Constraint logicalOrTerm(Constraint left) throws Exception {
	    if (accept(Token.lor)) {
	    	Constraint right = logicalAnd();
	    	Constraint or = new ConstraintOr(left, right);
	    	return logicalOrTerm(or);
	    }
	    return left;
	}

	private static Constraint logicalAnd() throws Exception {
		Constraint left = unary();
		return logicalAndTerm(left);
	}

	private static Constraint logicalAndTerm(Constraint left) throws Exception {
	    if (accept(Token.land)) {
	    	Constraint right = unary();
	    	Constraint and = new ConstraintAnd(left, right);
	    	return logicalAndTerm(and);
	    }
	    return left;
	}

	private static Constraint unary() throws Exception {
	    if (accept(Token.not)) {
	    	Constraint child = primary(); 
	    	return new ConstraintNot(child);
	    } else {
	    	return primary();
	    }
	}

	private static Constraint primary() throws Exception {
	    if (accept(Token.lparen)) {
	    	Constraint constraint = expr();
	    	expect(Token.rparen);
	    	return constraint;
	    } else {
	    	return codeRef();
	    }
	}

	private static Constraint codeRef() throws Exception {
		if (m_token == Token.codeRef) {
			Reference codeRef = ReferenceFactory.createReference(
					m_tokenString, ReferenceClass.CONSTRAINT_REF);
			
			if (codeRef.resolve(m_engine, m_engine.getCoreId(), m_engine.getNodeId()) == false) {
				error("Invalid code reference '" + m_tokenString + "'");
			}
			moveToNextToken();
			return new ConstraintCodeRef(codeRef);
		} else {
			String errorStr = "constraint parse error: missing code ref";
			if (!m_stringSoFar.equals(""))
				errorStr += " after '" + m_stringSoFar + "'";
			if (m_token != Token.invalid)
				errorStr += " got '" + tokToString(m_token) + "'";
			error(errorStr);
			return null;
		}
	}

	private static void moveToNextToken() {
		
		// Build up history for context in error messages
		if (!m_tokenString.equals("")) {
			if (!m_stringSoFar.equals("")) {
				m_stringSoFar += " ";
			}
			m_stringSoFar += m_tokenString;
		}

		if (m_tokens.size() > 0) {
			m_token = m_tokens.get(0);
			m_tokenString = m_tokenStrings.get(0);
			m_tokens.remove(0);
			m_tokenStrings.remove(0);
		} else {
			m_token = Token.invalid;
			m_tokenString = "";
		}
	}

	private static void error(String message) throws Exception {
		m_engine.notify(Notifier.NOTIFY_ERROR, message);
		throw(PARSE_ERROR);
	}
}
