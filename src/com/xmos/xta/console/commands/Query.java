package com.xmos.xta.console.commands;

import com.xmos.xta.console.Console;
import com.xmos.xta.console.Shell;
import com.xmos.xta.engine.Engine;
import com.xmos.xta.engine.PathQuery;

public class Query  extends Command {

	private String m_pathQuery;
	private String m_expression;
	
	public Query(String query, String expression) {
		m_pathQuery = query;
		m_expression = expression;
	}
	
	public Command clone(String[] args, int index, boolean commandLineMode) {
		if ((index + numArgs()) >= args.length)
			return null;
		String pathQuery = args[index + 1];
		
		//sanitise query name
		if(pathQuery.contains(":") || pathQuery.startsWith("0x")){
			return null;
		} else {
			m_pathQuery = pathQuery;
		}
		m_expression = args[index + 2];
		
		return new Query(m_pathQuery, m_expression);
	}

	public String commandLine() {
		return "list " + m_pathQuery;
	}

	public String description(Engine engine, boolean detailed) {
		return "query <result> <expression> : query the routes with the expression";
	}

	public void execute(Shell shell, Engine engine, Console console) {
		PathQuery pq = new PathQuery(m_pathQuery, m_expression);
		engine.addQuery(pq);
	}

	public String name() {
		return "query";
	}

	public int numArgs() {
		return 2;
	}

}
